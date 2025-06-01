package com.care4elders.patientbill.repository;

import com.care4elders.patientbill.model.Bill;
import com.care4elders.patientbill.model.BillItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Repository
public interface BillRepository extends MongoRepository<Bill, String> {
    
    // ========== Patient ID Queries ==========
    
    // Primary method for finding by patientId (String - updated to match model)
    List<Bill> findByPatientId(String patientId);
    
    // Legacy method for backward compatibility (Integer)
    @Query("{ 'patientId': ?0 }")
    List<Bill> findByPatientIdInteger(Integer patientId);
    
    // Legacy method for backward compatibility (Long)
    @Query("{ 'patientId': ?0 }")
    List<Bill> findByPatientIdLong(Long patientId);
    
    // Custom query for string-based patient ID search
    @Query("{ 'patientId': ?0 }")
    List<Bill> findByPatientIdAsString(String patientIdStr);
    
    // ========== Service Type Queries ==========
    
    // Find bills by service type (direct field match)
    List<Bill> findByServiceType(BillItem.ServiceType serviceType);
    
    // Find bills containing items of specific service type
    @Query("{ 'items.serviceType': ?0 }")
    List<Bill> findByItemsServiceType(BillItem.ServiceType serviceType);
    
    // Find bills by service type with pagination
    Page<Bill> findByServiceType(BillItem.ServiceType serviceType, Pageable pageable);
    
    // ========== Status Queries ==========
    
    // Find bills by status
    List<Bill> findByStatus(String status);
    
    // Find bills by status with pagination
    Page<Bill> findByStatus(String status, Pageable pageable);
    
    // Find bills by multiple statuses
    @Query("{ 'status': { $in: ?0 } }")
    List<Bill> findByStatusIn(List<String> statuses);
    
    // ========== Combined Queries ==========
    
    // Find bills by patient ID and status
    List<Bill> findByPatientIdAndStatus(String patientId, String status);
    
    // Find bills by patient ID and service type
    List<Bill> findByPatientIdAndServiceType(String patientId, BillItem.ServiceType serviceType);
    
    // Find bills by service type and status
    List<Bill> findByServiceTypeAndStatus(BillItem.ServiceType serviceType, String status);
    
    // ========== Patient Name Queries ==========
    
    // Find bills by patient name (case-insensitive)
    @Query("{ 'patientName': { $regex: ?0, $options: 'i' } }")
    List<Bill> findByPatientNameContainingIgnoreCase(String patientName);
    
    // Find bills by exact patient name
    List<Bill> findByPatientName(String patientName);
    
    // ========== Pagination Support ==========
    
    // Find bills by patient ID with pagination
    Page<Bill> findByPatientId(String patientId, Pageable pageable);
    
    // Find all bills with pagination (inherited from MongoRepository)
    Page<Bill> findAll(Pageable pageable);
    
    // ========== Bill Number Queries ==========
    
    // Find bills by bill number
    Optional<Bill> findByBillNumber(String billNumber);
    
    // Check if bill number exists
    boolean existsByBillNumber(String billNumber);
    
    // ========== Date Range Queries ==========
    
    // Find bills by bill date range
    List<Bill> findByBillDateBetween(Date startDate, Date endDate);
    
    // Find bills by due date range
    List<Bill> findByDueDateBetween(Date startDate, Date endDate);
    
    // Find bills by patient ID within bill date range
    List<Bill> findByPatientIdAndBillDateBetween(String patientId, Date startDate, Date endDate);
    
    // Find bills created within date range (if you add createdAt field to Bill model)
    @Query("{ 'createdAt': { $gte: ?0, $lte: ?1 } }")
    List<Bill> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // ========== Payment and Balance Queries ==========
    
    // Find unpaid bills (status is PENDING or PARTIALLY_PAID)
    @Query("{ 'status': { $in: ['PENDING', 'PARTIALLY_PAID'] } }")
    List<Bill> findUnpaidBills();
    
    // Find unpaid bills for a specific patient
    @Query("{ 'patientId': ?0, 'status': { $in: ['PENDING', 'PARTIALLY_PAID'] } }")
    List<Bill> findUnpaidBillsByPatientId(String patientId);
    

    
    // Find bills with balance amount greater than zero
    @Query("{ 'balanceAmount': { $gt: 0 } }")
    List<Bill> findBillsWithOutstandingBalance();
    
    // Find bills with balance amount greater than zero for specific patient
    @Query("{ 'patientId': ?0, 'balanceAmount': { $gt: 0 } }")
    List<Bill> findBillsWithOutstandingBalanceByPatientId(String patientId);
    
    // ========== Amount Range Queries ==========
    
    // Find bills by total amount range
    @Query("{ 'totalAmount': { $gte: ?0, $lte: ?1 } }")
    List<Bill> findByTotalAmountBetween(BigDecimal minAmount, BigDecimal maxAmount);
    
    // Find bills by balance amount range
    @Query("{ 'balanceAmount': { $gte: ?0, $lte: ?1 } }")
    List<Bill> findByBalanceAmountBetween(BigDecimal minAmount, BigDecimal maxAmount);
    
    // Find bills by paid amount range
    @Query("{ 'paidAmount': { $gte: ?0, $lte: ?1 } }")
    List<Bill> findByPaidAmountBetween(BigDecimal minAmount, BigDecimal maxAmount);
    
    // ========== Overdue and Follow-up Queries ==========
    
    // Find overdue bills (due date has passed and not fully paid)
    @Query("{ 'dueDate': { $lt: ?0 }, 'status': { $in: ['PENDING', 'PARTIALLY_PAID'] } }")
    List<Bill> findOverdueBills(Date currentDate);
    
    // Find overdue bills for specific patient
    @Query("{ 'patientId': ?0, 'dueDate': { $lt: ?1 }, 'status': { $in: ['PENDING', 'PARTIALLY_PAID'] } }")
    List<Bill> findOverdueBillsByPatientId(String patientId, Date currentDate);
    
    // Find bills due within next N days
    @Query("{ 'dueDate': { $gte: ?0, $lte: ?1 }, 'status': { $in: ['PENDING', 'PARTIALLY_PAID'] } }")
    List<Bill> findBillsDueSoon(Date startDate, Date endDate);
    
    // ========== Counting Queries ==========
    
    // Count bills by status
    long countByStatus(String status);
    
    // Count bills by patient ID
    long countByPatientId(String patientId);
    
    // Count bills by service type
    long countByServiceType(BillItem.ServiceType serviceType);
    
    // Count unpaid bills
    @Query(value = "{ 'status': { $in: ['PENDING', 'PARTIALLY_PAID'] } }", count = true)
    long countUnpaidBills();
    
    // Count unpaid bills for specific patient
    @Query(value = "{ 'patientId': ?0, 'status': { $in: ['PENDING', 'PARTIALLY_PAID'] } }", count = true)
    long countUnpaidBillsByPatientId(String patientId);
    
    // ========== Aggregation Queries ==========
    
    // Get total outstanding amount for a patient
    @Query(value = "{ 'patientId': ?0, 'status': { $in: ['PENDING', 'PARTIALLY_PAID'] } }", 
           fields = "{ 'balanceAmount': 1 }")
    List<Bill> findOutstandingBillsByPatientId(String patientId);
    
    // Find recent bills (last N days)
    @Query("{ 'billDate': { $gte: ?0 } }")
    List<Bill> findRecentBills(Date sinceDate);
    
    // Find recent bills for specific patient
    @Query("{ 'patientId': ?0, 'billDate': { $gte: ?1 } }")
    List<Bill> findRecentBillsByPatientId(String patientId, Date sinceDate);
    
    // ========== Advanced Search Queries ==========
    
    // Search bills by multiple criteria
    @Query("{ $and: [ " +
           "{ $or: [ { 'patientName': { $regex: ?0, $options: 'i' } }, { 'billNumber': { $regex: ?0, $options: 'i' } } ] }, " +
           "{ 'status': { $in: ?1 } } " +
           "] }")
    List<Bill> searchBillsByKeywordAndStatuses(String keyword, List<String> statuses);
    
    // Find bills by patient email
    List<Bill> findByPatientEmail(String patientEmail);
    
    // Find bills by patient phone
    List<Bill> findByPatientPhone(String patientPhone);
    
    // ========== Bill Item Queries ==========
    
    // Find bills containing specific service codes
    @Query("{ 'items.serviceCode': ?0 }")
    List<Bill> findByItemsServiceCode(String serviceCode);
    
    // Find bills containing specific categories
    @Query("{ 'items.category': ?0 }")
    List<Bill> findByItemsCategory(String category);
    
    // Find bills with items in specific date range
    @Query("{ 'items.serviceDate': { $gte: ?0, $lte: ?1 } }")
    List<Bill> findByItemsServiceDateBetween(java.time.LocalDate startDate, java.time.LocalDate endDate);
    
    // ========== Utility Queries ==========
    
    // Check if patient has any bills
    boolean existsByPatientId(String patientId);
    
    // Find latest bill for patient
    @Query(value = "{ 'patientId': ?0 }", sort = "{ 'billDate': -1 }")
    Optional<Bill> findLatestBillByPatientId(String patientId);
    
    // Find bills with notes containing specific text
    @Query("{ 'notes': { $regex: ?0, $options: 'i' } }")
    List<Bill> findByNotesContainingIgnoreCase(String noteText);
}