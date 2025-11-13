package com.invoiceme.infrastructure.persistence.payments;

import com.invoiceme.domain.payments.PaymentSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for PaymentSchedule entity.
 */
@Repository
interface PaymentScheduleJpaRepository extends JpaRepository<PaymentScheduleEntity, UUID> {
    
    List<PaymentScheduleEntity> findByInvoiceIdOrderByInstallmentNumberAsc(UUID invoiceId);
    
    @Query("SELECT ps FROM PaymentScheduleEntity ps WHERE ps.status IN ('PENDING', 'OVERDUE') AND ps.dueDate <= :upToDate ORDER BY ps.dueDate ASC")
    List<PaymentScheduleEntity> findUpcomingInstallments(@Param("upToDate") LocalDate upToDate);
    
    void deleteByInvoiceId(UUID invoiceId);
}









