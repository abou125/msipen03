package com.cl.msipen03.repository;

import com.cl.msipen03.entities.RemittanceOperation;
import com.cl.msipen03.entities.RemittanceOrder;
import org.springframework.data.domain.Page;

import java.util.Optional;


public interface RemittanceRepository {
    Optional<RemittanceOrder> findByRemittanceOrderId(String remittanceOrderId);

    Page<RemittanceOperation> findOperationsByRemittanceOrderId(String remittanceOrderId, int page, int size);
}
