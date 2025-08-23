package com.hyudequeue.genglish.tuition_fee_manager.service.servicesImpl;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.Invoice.request.InvoiceItemRequestDTO;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.Invoice.request.StudentInvoiceRequest;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.Invoice.response.InvoiceResponseDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.Invoice.response.RevenueSummaryDto;
import com.hyudequeue.genglish.tuition_fee_manager.entities.*;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.InvoiceStatusEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.PaymentMethodEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.RoleEnum;
import com.hyudequeue.genglish.tuition_fee_manager.repository.ClassEnrollmentRepository;
import com.hyudequeue.genglish.tuition_fee_manager.repository.ClassRepository;
import com.hyudequeue.genglish.tuition_fee_manager.repository.InvoiceCategoryRepository;
import com.hyudequeue.genglish.tuition_fee_manager.repository.InvoiceRepository;
import com.hyudequeue.genglish.tuition_fee_manager.repository.UserRepository;
import com.hyudequeue.genglish.tuition_fee_manager.service.services.InvoiceService;
import com.hyudequeue.genglish.tuition_fee_manager.service.services.NotificationService;
import com.hyudequeue.genglish.tuition_fee_manager.utility.constants.NotificationTemplateEnum;
import com.hyudequeue.genglish.tuition_fee_manager.utility.helper.NotificationTemplateBuilder;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final UserRepository userRepository;
    private final ClassRepository classRepository;
    private final NotificationService notificationService;
    private final EmailServiceImpl emailService;
    private final ClassEnrollmentRepository classEnrollmentRepository;
    private final InvoiceCategoryRepository categoryRepository;
    private final InvoiceNotificationServiceImpl invoiceNotificationService;
    public InvoiceServiceImpl(InvoiceRepository invoiceRepository,
                              UserRepository userRepository,
                              ClassRepository classRepository,
                              NotificationService notificationService,
                              ClassEnrollmentRepository classEnrollmentRepository,
                              EmailServiceImpl emailService,
                              InvoiceCategoryRepository categoryRepository,
                              InvoiceNotificationServiceImpl invoiceNotificationService) {
        this.invoiceRepository = invoiceRepository;
        this.userRepository = userRepository;
        this.classRepository = classRepository;
        this.notificationService = notificationService;
        this.classEnrollmentRepository = classEnrollmentRepository;
        this.emailService = emailService;
        this.categoryRepository = categoryRepository;
        this.invoiceNotificationService = invoiceNotificationService;
    }

    // =========================
    // CREATE (single) - giữ hàm cũ để không breaking
    // =========================
    @Override
    @Transactional
    public InvoiceResponseDto createInvoiceForStudent(Long userId,
                                                      Long classId,
                                                      Integer month,
                                                      LocalDate dueDate,
                                                      List<InvoiceItemRequestDTO> items) {
        return createInvoiceForStudent(userId, classId, month, dueDate, items, Collections.emptyList(), PaymentMethodEnum.BANKING);
    }

    @Transactional
    public InvoiceResponseDto createInvoiceForStudent(Long userId,
                                                      Long classId,
                                                      Integer month,
                                                      LocalDate dueDate,
                                                      List<InvoiceItemRequestDTO> items,
                                                      List<Long> categoryIds,
                                                      PaymentMethodEnum paymentType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Classes classes = classRepository.findById(classId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Class not found"));

        List<InvoiceCategory> categories = (categoryIds == null || categoryIds.isEmpty())
                ? List.of()
                : categoryRepository.findAllById(categoryIds);

        Invoice invoice = Invoice.builder()
                .user(user)
                .userName(user.getFullName())
                .classes(classes)
                .month(month)
                .dueDate(dueDate)
                .status(InvoiceStatusEnum.UNPAID)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .paymentType(paymentType == null ? PaymentMethodEnum.BANKING : paymentType)
                .totalAmount(calculateTotalAmount(items))
                .build();

        invoice.setCategories(categories);

        List<InvoiceItem> invoiceItems = (items == null ? List.<InvoiceItem>of()
                : items.stream().map(dto -> dto.toEntity(invoice)).toList());
        invoice.setItems(invoiceItems);

        Invoice saved = invoiceRepository.save(invoice);

        // Gọi hàm async để gửi mail và notif
        invoiceNotificationService.notifyInvoiceCreated(user, month);

        return InvoiceResponseDto.toDto(saved);
    }

    // =========================
    // CREATE (batch for class)
    // =========================
    @Override
    @Transactional
    public Page<InvoiceResponseDto> createInvoicesForClass(Long classId,
                                                           Integer month,
                                                           LocalDate dueDate,
                                                           List<StudentInvoiceRequest> studentRequests) {
        Classes classes = classRepository.findById(classId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Class not found"));

        if (studentRequests == null || studentRequests.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student fee list is empty");
        }

        // Valid month
        if (month != null && (month < 1 || month > 12)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Month must be 1..12");
        }

        // Map by userId
        Map<Long, StudentInvoiceRequest> reqByUser = studentRequests.stream()
                .collect(Collectors.toMap(StudentInvoiceRequest::getUserId, r -> r, (a, b) -> a));

        List<Long> userIds = new ArrayList<>(reqByUser.keySet());

        // Validate enrollments active in this class
        List<ClassEnrollment> enrollments = classEnrollmentRepository.findActiveByClassAndUserIds(classId, userIds);
        Set<Long> activeUserIds = enrollments.stream()
                .map(e -> e.getUser().getUserId())
                .collect(Collectors.toSet());

        List<Long> invalidUserIds = userIds.stream()
                .filter(uid -> !activeUserIds.contains(uid))
                .toList();

        if (!invalidUserIds.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Users not enrolled (or unenrolled) in class " + classId + ": " + invalidUserIds
            );
        }

        List<User> users = userRepository.findAllById(activeUserIds);

        List<Invoice> invoices = users.stream().map(user -> {
            StudentInvoiceRequest sreq = reqByUser.get(user.getUserId());
            List<InvoiceItemRequestDTO> itemsReq = (sreq == null ? null : sreq.getItems());

            List<Long> categoryIds = (sreq == null ? null : sreq.getCategoryIds());
            List<InvoiceCategory> categories = (categoryIds == null || categoryIds.isEmpty())
                    ? List.of()
                    : categoryRepository.findAllById(categoryIds);

            PaymentMethodEnum paymentType = (sreq == null ? null : sreq.getPaymentType());
            if (paymentType == null) paymentType = PaymentMethodEnum.BANKING;

            Invoice invoice = Invoice.builder()
                    .user(user)
                    .userName(user.getFullName())
                    .classes(classes)
                    .month(month)
                    .dueDate(dueDate)
                    .status(InvoiceStatusEnum.UNPAID)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .paymentType(paymentType)
                    .totalAmount(calculateTotalAmount(itemsReq))
                    .build();

            invoice.setCategories(categories);

            List<InvoiceItem> invoiceItems = (itemsReq == null ? List.<InvoiceItem>of()
                    : itemsReq.stream().map(dto -> dto.toEntity(invoice)).toList());
            invoice.setItems(invoiceItems);

            return invoice;
        }).toList();

        List<Invoice> saved = invoiceRepository.saveAll(invoices);

        invoiceNotificationService.sendClassInvoiceNotificationsAsync(saved, classes);

        List<InvoiceResponseDto> responseDtos = saved.stream()
                .map(InvoiceResponseDto::toDto)
                .toList();

        return new PageImpl<>(responseDtos);
    }

    // =========================
    // READ
    // =========================
    @Override
    public Page<InvoiceResponseDto> getInvoicesByClass(Long classId, Pageable pageable) {
        return invoiceRepository.findByClasses_ClassIdAndStatusNot(classId, InvoiceStatusEnum.CANCELLED, pageable)
                .map(InvoiceResponseDto::toDto);
    }

    @Override
    public Page<InvoiceResponseDto> getInvoicesByStudent(Long userId, Pageable pageable) {
        return invoiceRepository.findByUser_UserIdAndStatusNot(userId, InvoiceStatusEnum.CANCELLED, pageable)
                .map(InvoiceResponseDto::toDto);
    }

    public Page<InvoiceResponseDto> getAllInvoices(Pageable pageable,
                                                   InvoiceStatusEnum status,
                                                   Integer month,
                                                   Long categoryId) {
        Page<Invoice> invoices;

        if (status != null && month != null && categoryId != null) {
            invoices = invoiceRepository.findByStatusAndMonthAndCategoryId(status, month, categoryId, pageable);
        } else if (status != null && month != null) {
            invoices = invoiceRepository.findByStatusAndMonth(status, month, pageable);
        } else if (status != null && categoryId != null) {
            invoices = invoiceRepository.findByStatusAndCategoryId(status, categoryId, pageable);
        } else if (month != null && categoryId != null) {
            invoices = invoiceRepository.findByMonthAndCategoryId(month, categoryId, pageable);
        } else if (status != null) {
            invoices = invoiceRepository.findByStatus(status, pageable);
        } else if (month != null) {
            invoices = invoiceRepository.findByMonth(month, pageable);
        } else if (categoryId != null) {
            invoices = invoiceRepository.findByCategoryId(categoryId, pageable);
        } else {
            invoices = invoiceRepository.findAll(pageable);
        }

        return invoices.map(InvoiceResponseDto::toDto);
    }


    @Override
    public Page<InvoiceResponseDto> getInvoicesByStatus(Pageable pageable, InvoiceStatusEnum invoiceStatus) {
        return invoiceRepository.findByStatus(invoiceStatus, pageable)
                .map(InvoiceResponseDto::toDto);
    }

    @Override
    public InvoiceResponseDto getInvoiceById(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice not found"));
        return InvoiceResponseDto.toDto(invoice);
    }

    // =========================
    // UPDATE
    // =========================
    @Override
    @Transactional
    public InvoiceResponseDto updateInvoice(Long invoiceId, List<InvoiceItemRequestDTO> updatedItems) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice not found"));

        // Replace items (orphanRemoval)
        invoice.getItems().clear();
        invoiceRepository.saveAndFlush(invoice);

        if (updatedItems != null) {
            for (InvoiceItemRequestDTO itemDto : updatedItems) {
                InvoiceItem newItem = itemDto.toEntity(invoice);
                invoice.getItems().add(newItem);
            }
        }

        invoice.setTotalAmount(calculateTotalAmount(updatedItems));
        invoice.setUpdatedAt(LocalDateTime.now());

        Invoice saved = invoiceRepository.save(invoice);
        return InvoiceResponseDto.toDto(saved);
    }

    // =========================
    // DELETE (soft cancel)
    // =========================
    @Override
    @Transactional
    public void deleteInvoice(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice not found"));

        invoice.setStatus(InvoiceStatusEnum.CANCELLED);
        invoice.setUpdatedAt(LocalDateTime.now());

        invoiceNotificationService.notifyInvoiceCancelled(invoice);

        invoiceRepository.save(invoice);
    }

    // =========================
    // PROCESS STATUS
    // =========================
    @Override
    @Transactional
    public void processInvoiceStatus(Long invoiceId, InvoiceStatusEnum invoiceStatus) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice not found"));

        invoice.setStatus(invoiceStatus);
        if (invoiceStatus == InvoiceStatusEnum.PAID) {
            invoice.setPaidAt(LocalDateTime.now());
        }
        invoice.setUpdatedAt(LocalDateTime.now());

        invoiceRepository.save(invoice);
    }

    // =========================
    // HELPERS
    // =========================
    private int calculateTotalAmount(List<InvoiceItemRequestDTO> items) {
        if (items == null || items.isEmpty()) return 0;
        return items.stream()
                .mapToInt(i -> i.getAmount() * i.getQuantity())
                .sum();
    }

    @Override
    public Page<RevenueSummaryDto> getRevenueSummaryByMonth(Pageable pageable) {
        return invoiceRepository.sumRevenueGroupByMonth(pageable);
    }

    @Override
    public Page<RevenueSummaryDto> getRevenueSummaryByClass(Pageable pageable) {
        return invoiceRepository.sumRevenueGroupByClass(pageable);
    }

    @Override
    public Page<RevenueSummaryDto> getRevenueSummaryByWeek(Pageable pageable) {
        return invoiceRepository.sumRevenueGroupByWeek(pageable);
    }

    @Override
    public void manualConfirmInvoice(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Invoice not found"));

        // Cập nhật trạng thái và phương thức
        invoice.setStatus(InvoiceStatusEnum.PAID);
        invoice.setPaymentType(PaymentMethodEnum.MANUAL);
        invoice.setPaidAt(LocalDateTime.now()); // nếu bạn có field paidAt
        invoiceRepository.save(invoice);
        invoiceNotificationService.notifyManualConfirm(invoice);
    }

}
