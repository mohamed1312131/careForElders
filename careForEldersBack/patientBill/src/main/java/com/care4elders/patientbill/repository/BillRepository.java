// src/main/java/com/care4elders/patientbill/repository/BillRepository.java
package com.care4elders.patientbill.repository;

import com.care4elders.patientbill.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {
}