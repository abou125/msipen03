package com.cl.msipen03.service.impl;


import com.cl.logs.commun.CommonLoggerFactory;
import com.cl.msipen03.entities.RemittanceOperation;
import com.cl.msipen03.entities.RemittanceOrder;
import com.cl.msipen03.repository.RemittanceRepository;
import com.cl.msipen03.service.RemittanceQueryService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RemittanceQueryServiceImpl implements RemittanceQueryService {

    private final RemittanceRepository remittanceRepository;

    private static final Logger LOGGER = CommonLoggerFactory.getLogger(RemittanceExcelFileServiceImpl.class).logger();


    @Override
    public Optional<RemittanceOrder> getRemittanceOrder(String orderId) {
        LOGGER.info("Retrieving remittance order {}", orderId);
        return remittanceRepository.findByRemittanceOrderId(orderId);
    }

    @Override
    public Page<RemittanceOperation> getRemittanceOperations(String orderId, int page, int size) {
        LOGGER.info("Retrieving operations for order: {} with paginations: {} ", orderId, page);
        return remittanceRepository.findOperationsByRemittanceOrderId(orderId, page, size);
    }
}
