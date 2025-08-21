package com.hyudequeue.genglish.tuition_fee_manager.service.servicesImpl;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.Invoice.request.InvoiceItemRequestDTO;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.Invoice.request.StudentInvoiceRequest;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.Invoice.response.InvoiceResponseDto;
import com.hyudequeue.genglish.tuition_fee_manager.entities.*;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.InvoiceStatusEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.RoleEnum;
import com.hyudequeue.genglish.tuition_fee_manager.repository.ClassEnrollmentRepository;
import com.hyudequeue.genglish.tuition_fee_manager.repository.ClassRepository;
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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class InvoiceServiceImpl implements InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final UserRepository userRepository;
    private final ClassRepository classRepository;
    private final NotificationService notificationService;
    private final EmailServiceImpl emailService;
    private final ClassEnrollmentRepository classEnrollmentRepository;
    private final InvoiceNotificationServiceImpl invoiceNotificationService;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository, UserRepository userRepository, ClassRepository classRepository, NotificationService notificationService, ClassEnrollmentRepository classEnrollmentRepository, EmailServiceImpl emailService, InvoiceNotificationServiceImpl invoiceNotificationService) {
        this.invoiceRepository = invoiceRepository;
        this.userRepository = userRepository;
        this.classRepository = classRepository;
        this.notificationService = notificationService;
        this.classEnrollmentRepository = classEnrollmentRepository;
        this.emailService = emailService;
        this.invoiceNotificationService = invoiceNotificationService;
    }

    @Override
    @Transactional
    public InvoiceResponseDto createInvoiceForStudent(Long userId, Long classId, Integer month, LocalDate dueDate, List<InvoiceItemRequestDTO> items) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Classes classes = classRepository.findById(classId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Class not found"));

        Invoice invoice = Invoice.builder()
                .user(user)
                .classes(classes)
                .month(month)
                .dueDate(dueDate)
                .status(InvoiceStatusEnum.UNPAID)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .totalAmount(calculateTotalAmount(items))
                .build();

        List<InvoiceItem> invoiceItems = items.stream()
                .map(itemDto -> itemDto.toEntity(invoice))
                .collect(Collectors.toList());

        invoice.setItems(invoiceItems);
        Invoice saved = invoiceRepository.save(invoice);
        // Gọi hàm async để gửi mail và notif
        invoiceNotificationService.notifyInvoiceCreated(user, month);
        return InvoiceResponseDto.toDto(saved);
    }

    @Override
    @Transactional
    public Page<InvoiceResponseDto> createInvoicesForClass(
            Long classId,
            Integer month,
            LocalDate dueDate,
            List<StudentInvoiceRequest> studentRequests
    ) {
        Classes classes = classRepository.findById(classId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Class not found"));

        if (studentRequests == null || studentRequests.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student fee list is empty");
        }

        // month hợp lệ 1..12 (nếu có rule)
        if (month != null && (month < 1 || month > 12)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Month must be 1..12");
        }

        // Map userId -> request
        Map<Long, StudentInvoiceRequest> reqByUser = studentRequests.stream()
                .collect(Collectors.toMap(StudentInvoiceRequest::getUserId, r -> r, (a, b) -> a));

        List<Long> userIds = new ArrayList<>(reqByUser.keySet());

        // Lấy các enrollment active cho lớp này & tập user gửi lên
        List<ClassEnrollment> enrollments =
                classEnrollmentRepository.findActiveByClassAndUserIds(classId, userIds);

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

        // Load user entities cho các id hợp lệ
        List<User> users = userRepository.findAllById(activeUserIds);

        // Tạo invoices theo payload từng user
        List<Invoice> invoices = users.stream().map(user -> {
            List<InvoiceItemRequestDTO> itemsReq = reqByUser.get(user.getUserId()).getItems();

            Invoice invoice = Invoice.builder()
                    .user(user)
                    .classes(classes)
                    .month(month)
                    .dueDate(dueDate)
                    .status(InvoiceStatusEnum.UNPAID)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .totalAmount(calculateTotalAmount(itemsReq))
                    .build();

            List<InvoiceItem> invoiceItems = itemsReq.stream()
                    .map(itemDto -> itemDto.toEntity(invoice))
                    .collect(Collectors.toList());

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

    private int calculateTotalAmount(List<InvoiceItemRequestDTO> items) {
        return items.stream()
                .mapToInt(i -> i.getAmount() * i.getQuantity())
                .sum();
    }




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
    @Transactional
    public InvoiceResponseDto updateInvoice(Long invoiceId, List<InvoiceItemRequestDTO> updatedItems) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice not found"));

        invoice.getItems().clear();
        
        invoiceRepository.saveAndFlush(invoice);

        for (InvoiceItemRequestDTO itemDto : updatedItems) {
            InvoiceItem newItem = itemDto.toEntity(invoice);
            invoice.getItems().add(newItem);
        }

        invoice.setTotalAmount(calculateTotalAmount(updatedItems));
        invoice.setUpdatedAt(LocalDateTime.now());

        Invoice saved = invoiceRepository.save(invoice);
        return InvoiceResponseDto.toDto(saved);
    }

    @Override
    public Page<InvoiceResponseDto> getAllInvoices(Pageable pageable, InvoiceStatusEnum status, Integer month) {
        Page<Invoice> invoices;

        if (status != null && month != null) {
            invoices = invoiceRepository.findByStatusAndMonth(status, month, pageable);
        } else if (status != null) {
            invoices = invoiceRepository.findByStatus(status, pageable);
        } else if (month != null) {
            invoices = invoiceRepository.findByMonth(month, pageable);
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
    public void deleteInvoice(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice not found"));
        invoice.setStatus(InvoiceStatusEnum.CANCELLED);
        invoice.setUpdatedAt(LocalDateTime.now());
        Map<String, String> teacherValues = Map.of(
                "invoiceId", invoice.getInvoiceId().toString(),
                "className", invoice.getClasses().getClassName(),
                "invoiceContent", "Hóa đơn #" + invoice.getInvoiceId() + " (" + invoice.getClasses().getClassName() + ")"
        );


        String adminSubject = NotificationTemplateBuilder.buildSubject(
                NotificationTemplateEnum.INVOICE_CANCELLED_ALERT, teacherValues
        );
        String adminrBody = NotificationTemplateBuilder.buildBody(
                NotificationTemplateEnum.INVOICE_CANCELLED_ALERT, teacherValues
        );

        List<User> admins = userRepository.findByRole(RoleEnum.ADMIN);

        admins.forEach(admin -> {
            notificationService.createNotification(
                    admin.getUserId(),
                    adminSubject,
                    adminrBody
            );
            emailService.sendNotificationEmail(
                    admin.getEmail(),
                    NotificationTemplateEnum.CLASS_INVOICE_CREATED,
                    teacherValues
            );
        });

        invoiceRepository.save(invoice);
    }

    @Override
    public void processInvoiceStatus(Long invoiceId, InvoiceStatusEnum invoiceStatus) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice not found"));

        invoice.setStatus(invoiceStatus);
        invoice.setUpdatedAt(LocalDateTime.now());

        invoiceRepository.save(invoice);
    }

    @Override
    public InvoiceResponseDto getInvoiceById(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice not found"));
        return InvoiceResponseDto.toDto(invoice);
    }
}
