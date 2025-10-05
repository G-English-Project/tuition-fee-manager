package com.hyudequeue.genglish.tuition_fee_manager.service.servicesImpl;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.Invoice.request.InvoiceItemRequestDTO;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.Invoice.request.InvoiceUpdateRequestDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.Invoice.request.StudentInvoiceRequest;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.Invoice.response.InvoiceResponseDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.Invoice.response.InvoiceStatResponseDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.Invoice.response.RevenueSummaryDto;
import com.hyudequeue.genglish.tuition_fee_manager.entities.*;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.InvoiceStatusEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.PaymentMethodEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.RoleEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.UserStatusEnum;
import com.hyudequeue.genglish.tuition_fee_manager.repository.ClassEnrollmentRepository;
import com.hyudequeue.genglish.tuition_fee_manager.repository.ClassRepository;
import com.hyudequeue.genglish.tuition_fee_manager.repository.InvoiceCategoryRepository;
import com.hyudequeue.genglish.tuition_fee_manager.repository.InvoiceRepository;
import com.hyudequeue.genglish.tuition_fee_manager.repository.UserRepository;
import com.hyudequeue.genglish.tuition_fee_manager.service.services.InvoiceService;
import com.hyudequeue.genglish.tuition_fee_manager.service.services.NotificationService;
import com.hyudequeue.genglish.tuition_fee_manager.utility.constants.NotificationTemplateEnum;
import com.hyudequeue.genglish.tuition_fee_manager.utility.helper.NotificationTemplateBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
    private final InvoiceCategoryRepository invoiceCategoryRepository;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository,
                              UserRepository userRepository,
                              ClassRepository classRepository,
                              NotificationService notificationService,
                              ClassEnrollmentRepository classEnrollmentRepository,
                              EmailServiceImpl emailService,
                              InvoiceCategoryRepository categoryRepository,
                              InvoiceNotificationServiceImpl invoiceNotificationService, InvoiceCategoryRepository invoiceCategoryRepository) {
        this.invoiceRepository = invoiceRepository;
        this.userRepository = userRepository;
        this.classRepository = classRepository;
        this.notificationService = notificationService;
        this.classEnrollmentRepository = classEnrollmentRepository;
        this.emailService = emailService;
        this.categoryRepository = categoryRepository;
        this.invoiceNotificationService = invoiceNotificationService;
        this.invoiceCategoryRepository = invoiceCategoryRepository;
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

        // Use mutable list for categories
        List<InvoiceCategory> categories = (categoryIds == null || categoryIds.isEmpty())
                ? new ArrayList<>()
                : new ArrayList<>(categoryRepository.findAllById(categoryIds));

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

        // Use mutable list for invoice items
        List<InvoiceItem> invoiceItems = (items == null
                ? new ArrayList<>()
                : items.stream().map(dto -> dto.toEntity(invoice)).collect(Collectors.toList()));
        invoice.setItems(invoiceItems);

        Invoice saved = invoiceRepository.save(invoice);

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
                .collect(Collectors.toList());

        if (!invalidUserIds.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Users not enrolled (or unenrolled) in class " + classId + ": " + invalidUserIds
            );
        }

        List<User> users = userRepository.findAllById(activeUserIds);

        List<Invoice> invoices = new ArrayList<>();
        for (User user : users) {
            StudentInvoiceRequest sreq = reqByUser.get(user.getUserId());
            List<InvoiceItemRequestDTO> itemsReq = (sreq == null ? new ArrayList<>() : sreq.getItems());
            if (itemsReq == null) itemsReq = new ArrayList<>();

            List<Long> categoryIds = (sreq == null ? new ArrayList<>() : sreq.getCategoryIds());
            if (categoryIds == null) categoryIds = new ArrayList<>();
            List<InvoiceCategory> categories = new ArrayList<>();
            if (!categoryIds.isEmpty()) {
                categories.addAll(categoryRepository.findAllById(categoryIds));
            }

            PaymentMethodEnum paymentType = (sreq == null ? PaymentMethodEnum.BANKING : sreq.getPaymentType());
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

            List<InvoiceItem> invoiceItems = new ArrayList<>();
            if (itemsReq != null && !itemsReq.isEmpty()) {
                for (InvoiceItemRequestDTO dto : itemsReq) {
                    invoiceItems.add(dto.toEntity(invoice));
                }
            }
            invoice.setItems(invoiceItems);

            invoices.add(invoice);
        }

        List<Invoice> saved = invoiceRepository.saveAll(invoices);

        invoiceRepository.saveAll(saved);

        List<InvoiceResponseDto> responseDtos = new ArrayList<>();
        for (Invoice inv : saved) {
            responseDtos.add(InvoiceResponseDto.toDto(inv));
        }

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

    @Override
    public Page<InvoiceResponseDto> getAllInvoices(
            Pageable pageable,
            InvoiceStatusEnum status,
            Integer month,
            Integer year,
            Long classId,
            List<Long> categoryIds,
            String username
    ) {
        Specification<Invoice> spec = Specification.where(null);

        // ✅ Filter by status
        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }

        // ✅ Use the "month" field directly (instead of MONTH(createdAt))
        if (month != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("month"), month));
        }

        // ✅ Use createdAt for year filter
        if (year != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(cb.function("YEAR", Integer.class, root.get("createdAt")), year)
            );
        }

        // ✅ Filter by classId
        if (classId != null) {
            spec = spec.and((root, query, cb) -> {
                Join<Invoice, Classes> classJoin = root.join("classes", JoinType.INNER);
                return cb.equal(classJoin.get("classId"), classId);
            });
        }

        // ✅ Filter by categories
        if (categoryIds != null && !categoryIds.isEmpty()) {
            spec = spec.and((root, query, cb) -> {
                Join<Object, Object> categoryJoin = root.join("categories", JoinType.INNER);
                return categoryJoin.get("categoryId").in(categoryIds);
            });
        }

        // ✅ Filter by user fullName
        if (username != null && !username.isBlank()) {
            spec = spec.and((root, query, cb) -> {
                Join<Invoice, User> userJoin = root.join("user", JoinType.INNER);
                return cb.like(cb.lower(userJoin.get("fullName")), "%" + username.toLowerCase() + "%");
            });
        }

        Page<Invoice> invoices = invoiceRepository.findAll(spec, pageable);
        return invoices.map(InvoiceResponseDto::toDto);
    }




    @Override
    public Page<InvoiceResponseDto> getInvoicesByStatus(Pageable pageable, InvoiceStatusEnum invoiceStatus) {
        return invoiceRepository.findByStatus(invoiceStatus, pageable)
                .map(InvoiceResponseDto::toDto);
    }

    @Override
    public Page<InvoiceResponseDto> getInvoicesByStatusAndClass(Pageable pageable, InvoiceStatusEnum invoiceStatus, Long classId) {
        return invoiceRepository.findByStatusAndClasses_ClassId(invoiceStatus, classId, pageable)
                .map(InvoiceResponseDto::toDto);
    }

    @Override
    public InvoiceResponseDto getInvoiceById(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice not found"));
        return InvoiceResponseDto.toDto(invoice);
    }

    @Override
    @Transactional
    public InvoiceResponseDto updateInvoice(Long invoiceId, InvoiceUpdateRequestDto requestDto) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice not found"));

        // ✅ Fetch Classes entity if classId is provided
        Classes classes = null;
        if (requestDto.getClassId() != null) {
            classes = classRepository.findById(requestDto.getClassId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Class not found"));
        }

        // ✅ Apply changes
        requestDto.applyTo(invoice, classes);

        // ✅ Update categories
        if (requestDto.getCategoryIds() != null) {
            List<InvoiceCategory> categories = invoiceCategoryRepository.findAllById(requestDto.getCategoryIds());
            invoice.setCategories(categories);
        }

        // ✅ Replace items
        invoice.getItems().clear();
        invoiceRepository.saveAndFlush(invoice);

        if (requestDto.getUpdatedItems() != null) {
            for (InvoiceItemRequestDTO itemDto : requestDto.getUpdatedItems()) {
                InvoiceItem newItem = itemDto.toEntity(invoice);
                invoice.getItems().add(newItem);
            }
        }

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
    public Page<RevenueSummaryDto> getRevenueSummaryByDateRange(LocalDate fromDate, LocalDate toDate, Pageable pageable) {
        if (fromDate == null) {
            fromDate = LocalDate.of(1970, 1, 1);
        }
        if (toDate == null) {
            toDate = LocalDate.now();
        }

        // Repository actually returns Integer
        Integer totalRevenue = invoiceRepository.sumRevenueByDateRange(
                fromDate.atStartOfDay(),
                toDate.plusDays(1).atStartOfDay()
        );

        RevenueSummaryDto dto = new RevenueSummaryDto(
                fromDate + " ~ " + toDate,
                totalRevenue
        );

        return new PageImpl<>(List.of(dto), pageable, 1);
    }




    @Override
    public Page<RevenueSummaryDto> getRevenueSummaryByYear(PageRequest pageable) {
        return invoiceRepository.sumRevenueGroupByYear(pageable);
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

    @Override
    @Transactional
    public void bulkSoftDeleteInvoices(List<Long> invoiceIds) {
        List<Invoice> invoices = invoiceRepository.findAllById(invoiceIds);
        if (invoices.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No invoices found to delete");
        }

        invoices.forEach(invoice -> {
            invoice.setStatus(InvoiceStatusEnum.CANCELLED);
            invoice.setUpdatedAt(LocalDateTime.now());
        });

        invoiceRepository.saveAll(invoices);
    }

    @Override
    @Transactional
    public void bulkHardDeleteInvoices(List<Long> invoiceIds) {
        List<Invoice> invoices = invoiceRepository.findAllById(invoiceIds);
        if (invoices.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No invoices found to delete");
        }

        // Clear relationships first to avoid foreign key constraint violations
        for (Invoice invoice : invoices) {
            // Clear invoice-category relationships
            if (invoice.getCategories() != null) {
                invoice.getCategories().clear();
            }
            // Save to remove the relationships
            invoiceRepository.save(invoice);
        }

        // Now delete the invoices
        invoiceRepository.deleteAll(invoices);
    }

    @Override
    public InvoiceStatResponseDto getInvoiceStats() {
        // 1. Hóa đơn chưa thanh toán
        List<Invoice> unpaid = invoiceRepository.findByStatus(InvoiceStatusEnum.UNPAID);
        long unpaidCount = unpaid.size();
        int unpaidTotal = unpaid.stream()
                .mapToInt(Invoice::getTotalAmount)
                .sum();

        // 2. Hóa đơn quá hạn (status = OVERDUE)
        List<Invoice> overdue = invoiceRepository.findByStatus(InvoiceStatusEnum.OVERDUE);
        long overdueCount = overdue.size();
        int overdueTotal = overdue.stream()
                .mapToInt(Invoice::getTotalAmount)
                .sum();

        // 3. Học sinh Inactive
        int inactiveStudentCount = userRepository.countByRoleAndStatus(
                RoleEnum.STUDENT, UserStatusEnum.DISABLED
        );

        // 4. Tổng tiền của tháng hiện tại (PAID, UNPAID, OVERDUE)
        int currentMonth = LocalDate.now().getMonthValue();
        List<Invoice> currentMonthInvoices = invoiceRepository.findByMonthAndStatusIn(
                currentMonth,
                List.of(
                        InvoiceStatusEnum.PAID,
                        InvoiceStatusEnum.UNPAID,
                        InvoiceStatusEnum.OVERDUE
                )
        );

        int currentMonthTotal = currentMonthInvoices.stream()
                .mapToInt(Invoice::getTotalAmount)
                .sum();

        // ✅ Trả về thêm thống kê học sinh Inactive & tổng tiền tháng hiện tại
        return new InvoiceStatResponseDto(
                unpaidCount,
                unpaidTotal,
                overdueCount,
                overdueTotal,
                inactiveStudentCount,
                currentMonthTotal
        );
    }



}
