package com.smartbank.repository;
import com.smartbank.entity.BankAccount;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
    List<BankAccount> findAllByCustomerIdOrderByCreatedAtDesc(Long customerId);
    Optional<BankAccount> findByIdAndCustomerId(Long id, Long customerId);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from BankAccount a where a.id = :id")
    Optional<BankAccount> findByIdForUpdate(@Param("id") Long id);
}
