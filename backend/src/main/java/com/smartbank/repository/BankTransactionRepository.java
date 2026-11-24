package com.smartbank.repository;
import com.smartbank.entity.BankTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface BankTransactionRepository extends JpaRepository<BankTransaction, Long> {
    List<BankTransaction> findAllByAccountCustomerIdOrderByCreatedAtDesc(Long customerId);
}
