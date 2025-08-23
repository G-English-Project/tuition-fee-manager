package com.hyudequeue.genglish.tuition_fee_manager.repository;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.Invoice.response.RevenueSummaryDto;
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
            String formatted = "Mã hóa đơn #" + invoiceId;
            result.computeIfAbsent(userId, k -> new ArrayList<>()).add(formatted);
        }
        return result;
    }

    @Query("""
        select new com.hyudequeue.genglish.tuition_fee_manager.controller.model.Invoice.response.RevenueSummaryDto(
          function('date_format', coalesce(function('date', i.paidAt), i.dueDate), '%Y-%m'),
          sum(i.totalAmount)
        )
        from Invoice i
        where i.status = com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.InvoiceStatusEnum.PAID
        group by function('date_format', coalesce(function('date', i.paidAt), i.dueDate), '%Y-%m')
        order by 1 desc
        """)
    Page<RevenueSummaryDto> sumRevenueGroupByMonth(Pageable pageable);

    @Query("""
        select new com.hyudequeue.genglish.tuition_fee_manager.controller.model.Invoice.response.RevenueSummaryDto(
          i.classes.className,
          sum(i.totalAmount)
        )
        from Invoice i
        where i.status = com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.InvoiceStatusEnum.PAID
        group by i.classes.className
        order by 2 desc
        """)
    Page<RevenueSummaryDto> sumRevenueGroupByClass(Pageable pageable);

    @Query("""
        select new com.hyudequeue.genglish.tuition_fee_manager.controller.model.Invoice.response.RevenueSummaryDto(
          function('concat',
            function('date_format', coalesce(function('date', i.paidAt), i.dueDate), '%x'),
            '-W',
            function('date_format', coalesce(function('date', i.paidAt), i.dueDate), '%v')
          ),
          sum(i.totalAmount)
        )
        from Invoice i
        where i.status = com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.InvoiceStatusEnum.PAID
        group by
          function('date_format', coalesce(function('date', i.paidAt), i.dueDate), '%x'),
          function('date_format', coalesce(function('date', i.paidAt), i.dueDate), '%v')
        order by 1 desc
        """)
    Page<RevenueSummaryDto> sumRevenueGroupByWeek(Pageable pageable);


    @Query("SELECT DISTINCT i FROM Invoice i JOIN i.categories c WHERE c.categoryId = :categoryId")
    Page<Invoice> findByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);

    @Query("SELECT DISTINCT i FROM Invoice i JOIN i.categories c " +
            "WHERE i.status = :status AND c.categoryId = :categoryId")
    Page<Invoice> findByStatusAndCategoryId(@Param("status") InvoiceStatusEnum status,
                                            @Param("categoryId") Long categoryId,
                                            Pageable pageable);

    @Query("SELECT DISTINCT i FROM Invoice i JOIN i.categories c " +
            "WHERE i.month = :month AND c.categoryId = :categoryId")
    Page<Invoice> findByMonthAndCategoryId(@Param("month") Integer month,
                                           @Param("categoryId") Long categoryId,
                                           Pageable pageable);

    @Query("SELECT DISTINCT i FROM Invoice i JOIN i.categories c " +
            "WHERE i.status = :status AND i.month = :month AND c.categoryId = :categoryId")
    Page<Invoice> findByStatusAndMonthAndCategoryId(@Param("status") InvoiceStatusEnum status,
                                                    @Param("month") Integer month,
                                                    @Param("categoryId") Long categoryId,
                                                    Pageable pageable);

}
