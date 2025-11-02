package com.hyudequeue.genglish.tuition_fee_manager.repository;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.Invoice.response.RevenueSummaryDto;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.InvoiceStatusEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long>, JpaSpecificationExecutor<Invoice> {
    @Query("SELECT i FROM Invoice i LEFT JOIN FETCH i.items WHERE i.id IN :ids")
    List<Invoice> findAllWithItems(@Param("ids") List<Long> ids);

    Page<Invoice> findByClasses_ClassIdAndStatusNot(Long classId, InvoiceStatusEnum status, Pageable pageable);
    List<Invoice> findByStatus(InvoiceStatusEnum status);
    List<Invoice> findByMonthAndStatusIn(Integer month, List<InvoiceStatusEnum> statuses);
    @Query("SELECT i FROM Invoice i " +
            "WHERE i.status = :status " +
            "AND FUNCTION('MONTH', i.paidAt) = :month " +
            "AND FUNCTION('YEAR', i.paidAt) = :year")
    List<Invoice> findPaidInvoicesInCurrentMonth(
            @Param("status") InvoiceStatusEnum status,
            @Param("month") int month,
            @Param("year") int year
    );



    Page<Invoice> findByUser_UserIdAndStatusNot(Long userId, InvoiceStatusEnum status, Pageable pageable);
    Page<Invoice> findAllByStatusNot(InvoiceStatusEnum status, Pageable pageable);
    Page<Invoice> findByStatus(InvoiceStatusEnum status, Pageable pageable);
    Page<Invoice> findByStatusAndClasses_ClassId(InvoiceStatusEnum status, Long classId, Pageable pageable);
    Page<Invoice> findByMonth(Integer month, Pageable pageable);
    Page<Invoice> findByStatusAndMonth(InvoiceStatusEnum status, Integer month, Pageable pageable);
    @Modifying
    @Query("""
        UPDATE Invoice i
           SET i.status = 'OVERDUE'
         WHERE i.dueDate < :today
           AND i.status <> 'OVERDUE'
           AND i.status <> 'CANCELLED'
    """)
    int markOverdue(@Param("today") LocalDate today);

    @Query("""
    select i.invoiceId
      from Invoice i
     where i.dueDate < :today
       and i.status = 'UNPAID'
""")
    List<Long> findIdsDueBeforeAndStatusUnpaid(@Param("today") LocalDate today);


    @Modifying
    @Query("""
    update Invoice i
       set i.status = 'OVERDUE'
     where i.invoiceId in :ids
       and i.status = 'UNPAID'
       and i.status <> 'CANCELLED'
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
  cast(i.month as string),
  sum(cast(i.totalAmount as big_decimal))
)
from Invoice i
where i.status = com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.InvoiceStatusEnum.PAID
group by cast(i.month as string)
order by cast(i.month as string) desc
""")
    Page<RevenueSummaryDto> sumRevenueGroupByMonth(Pageable pageable);

    @Query("""
select new com.hyudequeue.genglish.tuition_fee_manager.controller.model.Invoice.response.RevenueSummaryDto(
  cast(i.month as string),
  sum(cast(i.totalAmount as big_decimal))
)
from Invoice i
where i.status = com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.InvoiceStatusEnum.PAID
  and (:categoryId is null or exists (select 1 from i.categories c where c.categoryId = :categoryId))
group by cast(i.month as string)
order by cast(i.month as string) desc
""")
    Page<RevenueSummaryDto> sumRevenueGroupByMonthWithCategory(Pageable pageable, @Param("categoryId") Long categoryId);


    @Query("""
select new com.hyudequeue.genglish.tuition_fee_manager.controller.model.Invoice.response.RevenueSummaryDto(
  i.classes.className,
  sum(cast(i.totalAmount as big_decimal))
)
from Invoice i
where i.status = com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.InvoiceStatusEnum.PAID
group by i.classes.className
order by sum(cast(i.totalAmount as big_decimal)) desc
""")
    Page<RevenueSummaryDto> sumRevenueGroupByClass(Pageable pageable);

    @Query("""
select new com.hyudequeue.genglish.tuition_fee_manager.controller.model.Invoice.response.RevenueSummaryDto(
  i.classes.className,
  sum(cast(i.totalAmount as big_decimal))
)
from Invoice i
where i.status = com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.InvoiceStatusEnum.PAID
  and (:categoryId is null or exists (select 1 from i.categories c where c.categoryId = :categoryId))
group by i.classes.className
order by sum(cast(i.totalAmount as big_decimal)) desc
""")
    Page<RevenueSummaryDto> sumRevenueGroupByClassWithCategory(Pageable pageable, @Param("categoryId") Long categoryId);

    @Query("""
select new com.hyudequeue.genglish.tuition_fee_manager.controller.model.Invoice.response.RevenueSummaryDto(
  cast(
    function('date_format', coalesce(i.paidAt, cast(i.dueDate as timestamp)), '%x-W%v')
    as string
  ),
  sum(cast(i.totalAmount as big_decimal))
)
from Invoice i
where i.status = com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.InvoiceStatusEnum.PAID
group by cast(function('date_format', coalesce(i.paidAt, cast(i.dueDate as timestamp)), '%x-W%v') as string)
order by cast(function('date_format', coalesce(i.paidAt, cast(i.dueDate as timestamp)), '%x-W%v') as string) desc
""")
    Page<RevenueSummaryDto> sumRevenueGroupByWeek(Pageable pageable);

    @Query("""
select new com.hyudequeue.genglish.tuition_fee_manager.controller.model.Invoice.response.RevenueSummaryDto(
  cast(
    function('date_format', coalesce(i.paidAt, cast(i.dueDate as timestamp)), '%x-W%v')
    as string
  ),
  sum(cast(i.totalAmount as big_decimal))
)
from Invoice i
where i.status = com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.InvoiceStatusEnum.PAID
  and (:categoryId is null or exists (select 1 from i.categories c where c.categoryId = :categoryId))
group by cast(function('date_format', coalesce(i.paidAt, cast(i.dueDate as timestamp)), '%x-W%v') as string)
order by cast(function('date_format', coalesce(i.paidAt, cast(i.dueDate as timestamp)), '%x-W%v') as string) desc
""")
    Page<RevenueSummaryDto> sumRevenueGroupByWeekWithCategory(Pageable pageable, @Param("categoryId") Long categoryId);

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
    @Query("""
select new com.hyudequeue.genglish.tuition_fee_manager.controller.model.Invoice.response.RevenueSummaryDto(
  cast(function('year', coalesce(i.paidAt, i.dueDate)) as string),
  sum(cast(i.totalAmount as big_decimal))
)
from Invoice i
where i.status = com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.InvoiceStatusEnum.PAID
group by cast(function('year', coalesce(i.paidAt, i.dueDate)) as string)
order by cast(function('year', coalesce(i.paidAt, i.dueDate)) as string)
""")
    Page<RevenueSummaryDto> sumRevenueGroupByYear(Pageable pageable);

    @Query("""
select new com.hyudequeue.genglish.tuition_fee_manager.controller.model.Invoice.response.RevenueSummaryDto(
  cast(function('year', coalesce(i.paidAt, i.dueDate)) as string),
  sum(cast(i.totalAmount as big_decimal))
)
from Invoice i
where i.status = com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.InvoiceStatusEnum.PAID
  and (:categoryId is null or exists (select 1 from i.categories c where c.categoryId = :categoryId))
group by cast(function('year', coalesce(i.paidAt, i.dueDate)) as string)
order by cast(function('year', coalesce(i.paidAt, i.dueDate)) as string)
""")
    Page<RevenueSummaryDto> sumRevenueGroupByYearWithCategory(Pageable pageable, @Param("categoryId") Long categoryId);


    @Query("""
    SELECT COALESCE(SUM(i.totalAmount),0)
    FROM Invoice i
    WHERE i.status = 'PAID'
      AND (COALESCE(i.paidAt, i.dueDate) BETWEEN :fromDate AND :toDate)
""")
    Integer sumRevenueByDateRange(LocalDateTime fromDate, LocalDateTime toDate);

    @Query("""
    SELECT COALESCE(SUM(i.totalAmount),0)
    FROM Invoice i
    WHERE i.status = 'PAID'
      AND (COALESCE(i.paidAt, i.dueDate) BETWEEN :fromDate AND :toDate)
      AND (:categoryId is null or exists (select 1 from i.categories c where c.categoryId = :categoryId))
""")
    Integer sumRevenueByDateRangeWithCategory(LocalDateTime fromDate, LocalDateTime toDate, @Param("categoryId") Long categoryId);

    // ======= REVENUE SUMMARY GROUP BY MONTH =======

    @Query("""
    select new com.hyudequeue.genglish.tuition_fee_manager.controller.model.Invoice.response.RevenueSummaryDto(
      cast(i.month as string),
      sum(cast(i.totalAmount as big_decimal))
    )
    from Invoice i
    where i.status = com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.InvoiceStatusEnum.PAID
      and (:categoryId is null or exists (select 1 from i.categories c where c.categoryId = :categoryId))
      and (:classId is null or i.classes.classId = :classId)
    group by cast(i.month as string)
    order by cast(i.month as string) desc
    """)
    Page<RevenueSummaryDto> sumRevenueGroupByMonthWithFilter(Pageable pageable,
                                                             @Param("categoryId") Long categoryId,
                                                             @Param("classId") Long classId);


    // ======= REVENUE SUMMARY GROUP BY CLASS =======

    @Query("""
    select new com.hyudequeue.genglish.tuition_fee_manager.controller.model.Invoice.response.RevenueSummaryDto(
      i.classes.className,
      sum(cast(i.totalAmount as big_decimal))
    )
    from Invoice i
    where i.status = com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.InvoiceStatusEnum.PAID
      and (:categoryId is null or exists (select 1 from i.categories c where c.categoryId = :categoryId))
      and (:classId is null or i.classes.classId = :classId)
    group by i.classes.className
    order by sum(cast(i.totalAmount as big_decimal)) desc
    """)
    Page<RevenueSummaryDto> sumRevenueGroupByClassWithFilter(Pageable pageable,
                                                             @Param("categoryId") Long categoryId,
                                                             @Param("classId") Long classId);


    // ======= REVENUE SUMMARY GROUP BY WEEK =======

    @Query("""
    select new com.hyudequeue.genglish.tuition_fee_manager.controller.model.Invoice.response.RevenueSummaryDto(
      cast(function('date_format', coalesce(i.paidAt, cast(i.dueDate as timestamp)), '%x-W%v') as string),
      sum(cast(i.totalAmount as big_decimal))
    )
    from Invoice i
    where i.status = com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.InvoiceStatusEnum.PAID
      and (:categoryId is null or exists (select 1 from i.categories c where c.categoryId = :categoryId))
      and (:classId is null or i.classes.classId = :classId)
    group by cast(function('date_format', coalesce(i.paidAt, cast(i.dueDate as timestamp)), '%x-W%v') as string)
    order by cast(function('date_format', coalesce(i.paidAt, cast(i.dueDate as timestamp)), '%x-W%v') as string) desc
    """)
    Page<RevenueSummaryDto> sumRevenueGroupByWeekWithFilter(Pageable pageable,
                                                            @Param("categoryId") Long categoryId,
                                                            @Param("classId") Long classId);


    // ======= REVENUE SUMMARY GROUP BY YEAR =======

    @Query("""
    select new com.hyudequeue.genglish.tuition_fee_manager.controller.model.Invoice.response.RevenueSummaryDto(
      cast(function('year', coalesce(i.paidAt, i.dueDate)) as string),
      sum(cast(i.totalAmount as big_decimal))
    )
    from Invoice i
    where i.status = com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.InvoiceStatusEnum.PAID
      and (:categoryId is null or exists (select 1 from i.categories c where c.categoryId = :categoryId))
      and (:classId is null or i.classes.classId = :classId)
    group by cast(function('year', coalesce(i.paidAt, i.dueDate)) as string)
    order by cast(function('year', coalesce(i.paidAt, i.dueDate)) as string)
    """)
    Page<RevenueSummaryDto> sumRevenueGroupByYearWithFilter(Pageable pageable,
                                                            @Param("categoryId") Long categoryId,
                                                            @Param("classId") Long classId);


    // ======= REVENUE SUMMARY BY DATE RANGE =======

    @Query("""
    SELECT COALESCE(SUM(i.totalAmount), 0)
    FROM Invoice i
    WHERE i.status = 'PAID'
      AND (COALESCE(i.paidAt, i.dueDate) BETWEEN :fromDate AND :toDate)
      AND (:categoryId is null or exists (select 1 from i.categories c where c.categoryId = :categoryId))
      AND (:classId is null or i.classes.classId = :classId)
    """)
    Integer sumRevenueByDateRangeWithFilter(@Param("fromDate") LocalDateTime fromDate,
                                            @Param("toDate") LocalDateTime toDate,
                                            @Param("categoryId") Long categoryId,
                                            @Param("classId") Long classId);

    // ==========================================
    // ✅ NATIVE QUERY - GROUP BY MONTH WITH CLASS
    // ==========================================

    @Query(value = """
        SELECT 
            CONCAT(YEAR(created_at), '-', LPAD(month, 2, '0')) as period,
            SUM(total_amount) as revenue
        FROM invoices
        WHERE status = 'PAID' AND class_id = :classId
        GROUP BY YEAR(created_at), month
        ORDER BY YEAR(created_at) DESC, month DESC
        """,
            countQuery = """
        SELECT COUNT(DISTINCT CONCAT(YEAR(created_at), '-', month))
        FROM invoices
        WHERE status = 'PAID' AND class_id = :classId
        """,
            nativeQuery = true)
    Page<Object[]> sumRevenueGroupByMonthWithClassNative(Pageable pageable, @Param("classId") Long classId);

    default Page<RevenueSummaryDto> sumRevenueGroupByMonthWithClass(Pageable pageable, Long classId) {
        Page<Object[]> results = sumRevenueGroupByMonthWithClassNative(pageable, classId);
        List<RevenueSummaryDto> dtos = results.getContent().stream()
                .map(row -> new RevenueSummaryDto(
                        (String) row[0],
                        ((BigDecimal) row[1]).intValue()
                ))
                .collect(Collectors.toList());
        return new PageImpl<>(dtos, pageable, results.getTotalElements());
    }

    @Query(value = """
        SELECT 
            CONCAT(YEAR(i.created_at), '-', LPAD(i.month, 2, '0')) as period,
            SUM(i.total_amount) as revenue
        FROM invoices i
        INNER JOIN invoice_category_map icm ON i.invoice_id = icm.invoice_id
        WHERE i.status = 'PAID' 
          AND icm.category_id = :categoryId
          AND i.class_id = :classId
        GROUP BY YEAR(i.created_at), i.month
        ORDER BY YEAR(i.created_at) DESC, i.month DESC
        """,
            countQuery = """
        SELECT COUNT(DISTINCT CONCAT(YEAR(i.created_at), '-', i.month))
        FROM invoices i
        INNER JOIN invoice_category_map icm ON i.invoice_id = icm.invoice_id
        WHERE i.status = 'PAID' 
          AND icm.category_id = :categoryId
          AND i.class_id = :classId
        """,
            nativeQuery = true)
    Page<Object[]> sumRevenueGroupByMonthWithCategoryAndClassNative(
            Pageable pageable,
            @Param("categoryId") Long categoryId,
            @Param("classId") Long classId
    );

    default Page<RevenueSummaryDto> sumRevenueGroupByMonthWithCategoryAndClass(
            Pageable pageable, Long categoryId, Long classId) {
        Page<Object[]> results = sumRevenueGroupByMonthWithCategoryAndClassNative(pageable, categoryId, classId);
        List<RevenueSummaryDto> dtos = results.getContent().stream()
                .map(row -> new RevenueSummaryDto(
                        (String) row[0],
                        ((BigDecimal) row[1]).intValue()
                ))
                .collect(Collectors.toList());
        return new PageImpl<>(dtos, pageable, results.getTotalElements());
    }

    // ==========================================
    // ✅ NATIVE QUERY - GROUP BY WEEK WITH CLASS
    // ==========================================

    @Query(value = """
        SELECT 
            CONCAT(YEAR(created_at), '-W', LPAD(WEEK(created_at, 3), 2, '0')) as period,
            SUM(total_amount) as revenue
        FROM invoices
        WHERE status = 'PAID' AND class_id = :classId
        GROUP BY YEAR(created_at), WEEK(created_at, 3)
        ORDER BY YEAR(created_at) DESC, WEEK(created_at, 3) DESC
        """,
            countQuery = """
        SELECT COUNT(DISTINCT CONCAT(YEAR(created_at), '-', WEEK(created_at, 3)))
        FROM invoices
        WHERE status = 'PAID' AND class_id = :classId
        """,
            nativeQuery = true)
    Page<Object[]> sumRevenueGroupByWeekWithClassNative(Pageable pageable, @Param("classId") Long classId);

    default Page<RevenueSummaryDto> sumRevenueGroupByWeekWithClass(Pageable pageable, Long classId) {
        Page<Object[]> results = sumRevenueGroupByWeekWithClassNative(pageable, classId);
        List<RevenueSummaryDto> dtos = results.getContent().stream()
                .map(row -> new RevenueSummaryDto(
                        (String) row[0],
                        ((BigDecimal) row[1]).intValue()
                ))
                .collect(Collectors.toList());
        return new PageImpl<>(dtos, pageable, results.getTotalElements());
    }

    @Query(value = """
        SELECT 
            CONCAT(YEAR(i.created_at), '-W', LPAD(WEEK(i.created_at, 3), 2, '0')) as period,
            SUM(i.total_amount) as revenue
        FROM invoices i
        INNER JOIN invoice_category_map icm ON i.invoice_id = icm.invoice_id
        WHERE i.status = 'PAID' 
          AND icm.category_id = :categoryId
          AND i.class_id = :classId
        GROUP BY YEAR(i.created_at), WEEK(i.created_at, 3)
        ORDER BY YEAR(i.created_at) DESC, WEEK(i.created_at, 3) DESC
        """,
            countQuery = """
        SELECT COUNT(DISTINCT CONCAT(YEAR(i.created_at), '-', WEEK(i.created_at, 3)))
        FROM invoices i
        INNER JOIN invoice_category_map icm ON i.invoice_id = icm.invoice_id
        WHERE i.status = 'PAID' 
          AND icm.category_id = :categoryId
          AND i.class_id = :classId
        """,
            nativeQuery = true)
    Page<Object[]> sumRevenueGroupByWeekWithCategoryAndClassNative(
            Pageable pageable,
            @Param("categoryId") Long categoryId,
            @Param("classId") Long classId
    );

    default Page<RevenueSummaryDto> sumRevenueGroupByWeekWithCategoryAndClass(
            Pageable pageable, Long categoryId, Long classId) {
        Page<Object[]> results = sumRevenueGroupByWeekWithCategoryAndClassNative(pageable, categoryId, classId);
        List<RevenueSummaryDto> dtos = results.getContent().stream()
                .map(row -> new RevenueSummaryDto(
                        (String) row[0],
                        ((BigDecimal) row[1]).intValue()
                ))
                .collect(Collectors.toList());
        return new PageImpl<>(dtos, pageable, results.getTotalElements());
    }

    // ==========================================
    // ✅ NATIVE QUERY - GROUP BY YEAR WITH CLASS
    // ==========================================

    @Query(value = """
        SELECT 
            CAST(YEAR(created_at) AS CHAR) as period,
            SUM(total_amount) as revenue
        FROM invoices
        WHERE status = 'PAID' AND class_id = :classId
        GROUP BY YEAR(created_at)
        ORDER BY YEAR(created_at) DESC
        """,
            countQuery = """
        SELECT COUNT(DISTINCT YEAR(created_at))
        FROM invoices
        WHERE status = 'PAID' AND class_id = :classId
        """,
            nativeQuery = true)
    Page<Object[]> sumRevenueGroupByYearWithClassNative(Pageable pageable, @Param("classId") Long classId);

    default Page<RevenueSummaryDto> sumRevenueGroupByYearWithClass(Pageable pageable, Long classId) {
        Page<Object[]> results = sumRevenueGroupByYearWithClassNative(pageable, classId);
        List<RevenueSummaryDto> dtos = results.getContent().stream()
                .map(row -> new RevenueSummaryDto(
                        (String) row[0],
                        ((BigDecimal) row[1]).intValue()
                ))
                .collect(Collectors.toList());
        return new PageImpl<>(dtos, pageable, results.getTotalElements());
    }

    @Query(value = """
        SELECT 
            CAST(YEAR(i.created_at) AS CHAR) as period,
            SUM(i.total_amount) as revenue
        FROM invoices i
        INNER JOIN invoice_category_map icm ON i.invoice_id = icm.invoice_id
        WHERE i.status = 'PAID' 
          AND icm.category_id = :categoryId
          AND i.class_id = :classId
        GROUP BY YEAR(i.created_at)
        ORDER BY YEAR(i.created_at) DESC
        """,
            countQuery = """
        SELECT COUNT(DISTINCT YEAR(i.created_at))
        FROM invoices i
        INNER JOIN invoice_category_map icm ON i.invoice_id = icm.invoice_id
        WHERE i.status = 'PAID' 
          AND icm.category_id = :categoryId
          AND i.class_id = :classId
        """,
            nativeQuery = true)
    Page<Object[]> sumRevenueGroupByYearWithCategoryAndClassNative(
            Pageable pageable,
            @Param("categoryId") Long categoryId,
            @Param("classId") Long classId
    );

    default Page<RevenueSummaryDto> sumRevenueGroupByYearWithCategoryAndClass(
            Pageable pageable, Long categoryId, Long classId) {
        Page<Object[]> results = sumRevenueGroupByYearWithCategoryAndClassNative(pageable, categoryId, classId);
        List<RevenueSummaryDto> dtos = results.getContent().stream()
                .map(row -> new RevenueSummaryDto(
                        (String) row[0],
                        ((BigDecimal) row[1]).intValue()
                ))
                .collect(Collectors.toList());
        return new PageImpl<>(dtos, pageable, results.getTotalElements());
    }

    // ==========================================
    // DATE RANGE WITH CLASS FILTER
    // ==========================================

    @Query(value = """
        SELECT COALESCE(SUM(total_amount), 0)
        FROM invoices
        WHERE status = 'PAID'
          AND paid_at >= :fromDate
          AND paid_at < :toDate
          AND class_id = :classId
        """,
            nativeQuery = true)
    Integer sumRevenueByDateRangeWithClass(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("classId") Long classId
    );

    @Query(value = """
        SELECT COALESCE(SUM(i.total_amount), 0)
        FROM invoices i
        INNER JOIN invoice_category_map icm ON i.invoice_id = icm.invoice_id
        WHERE i.status = 'PAID'
          AND i.paid_at >= :fromDate
          AND i.paid_at < :toDate
          AND icm.category_id = :categoryId
          AND i.class_id = :classId
        """,
            nativeQuery = true)
    Integer sumRevenueByDateRangeWithCategoryAndClass(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("categoryId") Long categoryId,
            @Param("classId") Long classId
    );
}