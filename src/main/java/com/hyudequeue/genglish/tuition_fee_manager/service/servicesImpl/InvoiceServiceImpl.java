package com.hyudequeue.genglish.tuition_fee_manager.service.servicesImpl;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.Invoice.request.InvoiceItemRequestDTO;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.Invoice.response.InvoiceResponseDto;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Classes;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.InvoiceStatusEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Invoice;
import com.hyudequeue.genglish.tuition_fee_manager.entities.InvoiceItem;
import com.hyudequeue.genglish.tuition_fee_manager.entities.User;
import com.hyudequeue.genglish.tuition_fee_manager.repository.ClassRepository;
import com.hyudequeue.genglish.tuition_fee_manager.repository.InvoiceRepository;
import com.hyudequeue.genglish.tuition_fee_manager.repository.UserRepository;
import com.hyudequeue.genglish.tuition_fee_manager.service.services.InvoiceService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceServiceImpl implements InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final UserRepository userRepository;
    private final ClassRepository classRepository;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository, UserRepository userRepository, ClassRepository classRepository) {
        this.invoiceRepository = invoiceRepository;
        this.userRepository = userRepository;
        this.classRepository = classRepository;
    }

    @Override
    public InvoiceResponseDto createInvoiceForStudent(Long userId, Long classId, Integer month, LocalDate dueDate, List<InvoiceItemRequestDTO> items) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Classes classes = classRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found"));

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
        return InvoiceResponseDto.toDto(saved);
    }

    @Override
    public Page<InvoiceResponseDto> createInvoicesForClass(Long classId, Integer month, LocalDate dueDate, List<InvoiceItemRequestDTO> items) {
        Classes classes = classRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found"));

        List<User> students = userRepository.findAllByEnrolledClass(classes);

        List<Invoice> invoices = students.stream().map(user -> {
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
            return invoice;
        }).toList();

        List<Invoice> saved = invoiceRepository.saveAll(invoices);
        List<InvoiceResponseDto> responseDtos = saved.stream()
                .map(InvoiceResponseDto::toDto)
                .toList();
        return new PageImpl<>(responseDtos);
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
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        invoice.getItems().clear();

        List<InvoiceItem> newItems = updatedItems.stream()
                .map(dto -> dto.toEntity(invoice))
                .toList();

        invoice.setItems(newItems);
        invoice.setTotalAmount(calculateTotalAmount(updatedItems));
        invoice.setUpdatedAt(LocalDateTime.now());

        Invoice saved = invoiceRepository.save(invoice);
        return InvoiceResponseDto.toDto(saved);
    }

    @Override
    public Page<InvoiceResponseDto> getAllInvoices(Pageable pageable) {
        return invoiceRepository.findAllByStatusNot(InvoiceStatusEnum.CANCELLED, pageable)
                .map(InvoiceResponseDto::toDto);
    }

    @Override
    public Page<InvoiceResponseDto> getInvoicesByStatus(Pageable pageable, InvoiceStatusEnum invoiceStatus) {
        return invoiceRepository.findByStatus(invoiceStatus, pageable)
                .map(InvoiceResponseDto::toDto);
    }

    @Override
    public void deleteInvoice(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
        invoice.setStatus(InvoiceStatusEnum.CANCELLED);
    }

    @Override
    public void processInvoiceStatus(Long invoiceId, InvoiceStatusEnum invoiceStatus) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        invoice.setStatus(invoiceStatus);
        invoice.setUpdatedAt(LocalDateTime.now());

        invoiceRepository.save(invoice);
    }

    private int calculateTotalAmount(List<InvoiceItemRequestDTO> items) {
        return items.stream()
                .mapToInt(i -> i.getAmount() * i.getQuantity())
                .sum();
    }
}
