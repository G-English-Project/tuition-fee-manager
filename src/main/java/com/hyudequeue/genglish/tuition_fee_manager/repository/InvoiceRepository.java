package com.hyudequeue.genglish.tuition_fee_manager.repository;

import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.InvoiceStatusEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Page<Invoice> findByClasses_ClassIdAndStatusNot(Long classId, InvoiceStatusEnum status, Pageable pageable);

    Page<Invoice> findByUser_UserIdAndStatusNot(Long userId, InvoiceStatusEnum status, Pageable pageable);
    Page<Invoice> findAllByStatusNot(InvoiceStatusEnum status, Pageable pageable);
    Page<Invoice> findByStatus(InvoiceStatusEnum status, Pageable pageable);
    Page<Invoice> findByMonth(Integer month, Pageable pageable);
    Page<Invoice> findByStatusAndMonth(InvoiceStatusEnum status, Integer month, Pageable pageable);
    @Modifying
    @Query("""
        UPDATE Invoice i
           SET i.status = 'OVERDUE'
         WHERE i.dueDate < :today
           AND i.status <> 'OVERDUE'
    """)
    int markOverdue(@Param("today") LocalDate today);

    @Query("""
    select i.invoiceId
      from Invoice i
     where i.dueDate < :today
       and i.status <> :status
""")
    List<Long> findIdsDueBeforeAndStatusNot(@Param("today") LocalDate today,
                                            @Param("status") InvoiceStatusEnum status);

    @Modifying
    @Query("""
    update Invoice i
       set i.status = 'OVERDUE'
     where i.invoiceId in :ids
       and i.status <> 'OVERDUE'
""")
    int markOverdueByIds(@Param("ids") List<Long> ids);


    @Query("""
    SELECT i.user.userId, i.invoiceId
      FROM Invoice i
     WHERE i.invoiceId IN :ids
""")
    List<Object[]> findUserIdAndInvoiceIds(@Param("ids") List<Long> ids);

    default Map<Long, List<String>> mapUserToOverdueInvoiceContents(List<Long> ids) {
        List<Object[]> rows = findUserIdAndInvoiceIds(ids);
        Map<Long, List<String>> result = new HashMap<>();
        for (Object[] row : rows) {
            Long userId = (Long) row[0];
            Long invoiceId = (Long) row[1];
            // Format thành "Mã hóa đơn #id"
            String formatted = "Mã hóa đơn #" + invoiceId;
            result.computeIfAbsent(userId, k -> new ArrayList<>()).add(formatted);
        }
        return result;
    }
}
