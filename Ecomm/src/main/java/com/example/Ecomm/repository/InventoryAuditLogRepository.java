package com.example.Ecomm.repository;

import com.example.Ecomm.entity.InventoryAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryAuditLogRepository extends JpaRepository<InventoryAuditLog, Long> {

    List<InventoryAuditLog> findTop50ByOrderByTimestampDesc();

    List<InventoryAuditLog> findByProductIdOrderByTimestampDesc(Long productId);
}
