package com.cl.msipen03.facades;

import com.cl.msipen03.entities.RemittanceOrder;
import com.cl.msipen03.entities.RemittanceOrderPaginationResponse;
import com.cl.msipen03.exceptions.RemittanceNotFoundException;
import org.slf4j.MDC;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.UUID;

import com.cl.msipen03.entities.RemittanceOperation;
import com.cl.msipen03.service.RemittanceQueryService;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class RemittanceOperationsFacade {

    private final RemittanceQueryService remittanceQueryService;

    public RemittanceOrderPaginationResponse getOperationListResponse(String remittanceOrderId, int page, int size) {
        MDC.put("CORRELATION_ID", UUID.randomUUID().toString());

        Page<RemittanceOperation> remittanceOperations = remittanceQueryService
                .getRemittanceOperations(remittanceOrderId, page, size);

        RemittanceOrder remittanceOrder = remittanceQueryService.getRemittanceOrder(remittanceOrderId)
                .orElseThrow(() -> new RemittanceNotFoundException(" order not found : " + remittanceOrderId));

        return RemittanceOrderPaginationResponse.builder()
                .size(remittanceOperations.getSize())
                .page(remittanceOperations.getNumber())
                .total(remittanceOperations.getTotalElements())
                .request(remittanceOrder)
                .list(remittanceOperations.getContent())
                .build();
    }
}