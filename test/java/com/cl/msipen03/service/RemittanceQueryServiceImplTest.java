package com.cl.msipen03.service;

import com.cl.msipen03.entities.RemittanceOperation;
import com.cl.msipen03.entities.RemittanceOrder;
import com.cl.msipen03.repository.RemittanceOperationRepository;
import com.cl.msipen03.repository.RemittanceOrderRepository;
import com.cl.msipen03.service.impl.RemittanceQueryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RemittanceQueryServiceImplTest {

    @Mock
    private RemittanceOrderRepository orderRepository;

    @Mock
    private RemittanceOperationRepository operationRepository;

    @InjectMocks
    private RemittanceQueryServiceImpl service;

    private RemittanceOrder mockOrder;
    private RemittanceOperation mockOperation;
    private String orderId;

    @BeforeEach
    void setUp() {
        orderId = "REM000000101000";
        mockOrder = createMockOrder();
        mockOperation = createMockOperation();
    }

    private RemittanceOrder createMockOrder() {
        RemittanceOrder order = new RemittanceOrder();
        order.setReferenceBanqueDeLaRemise("IPF000000127611");
        order.setDateEtHeureDeValidation(Timestamp.from(
                LocalDateTime.of(2024, 6, 19, 14, 3, 41)
                        .toInstant(ZoneOffset.UTC)));
        order.setReferenceBanqueDeLaRemise(orderId);
        order.setStatutDeLaRemise("PART");
        order.setLibelleDuCompteDO("CONDAT SAS");
        order.setCompteDO("30002002600000000013S10");
        order.setNombreVirements(11);
        order.setDateExecution(Date.valueOf("2025-07-28"));
        order.setMontantTotalDeLaRemise(new BigDecimal("100057.00"));
        return order;
    }

    private RemittanceOperation createMockOperation() {
        RemittanceOperation operation = new RemittanceOperation();
        operation.setNomBeneficiaire("LCL CREDIT LYONNAIS");
        operation.setIbanBeneficiaire("30002003870000017010T61");
        operation.setBicBeneficare("CRLYFRPPXXX");
        operation.setMontant(new BigDecimal("500.00"));
        operation.setDevise("EUR");
        operation.setMotifPaiement("0157390000153517262 REMONTEE COTISATIONS");
        operation.setReferencePaiement("PACIFICA2000157390");
        operation.setReferenceBoutEnBout("0157390153517262");
        operation.setReferenceBanque("IPF000000127611");
        operation.setStatut("REJECTED");
        operation.setCodeRejetIso("AC03");
        operation.setMotifRejet("Invalid account number");
        return operation;
    }

    @Test
    void shouldReturnRemittanceOrder() {
        when(orderRepository.findByRemittanceOrderId(orderId))
                .thenReturn(Optional.of(mockOrder));

        Optional<RemittanceOrder> result = service.getRemittanceOrder(orderId);

        assertThat(result).isPresent();
        assertThat(result.get().getReferenceBanqueDeLaRemise()).isEqualTo(orderId);
        assertThat(result.get().getStatutDeLaRemise()).isEqualTo("PART");
        assertThat(result.get().getLibelleDuCompteDO()).isEqualTo("CONDAT SAS");
        assertThat(result.get().getMontantTotalDeLaRemise())
                .isEqualByComparingTo("100057.00");
    }

    @Test
    void shouldReturnEmptyWhenOrderNotFound() {
        when(orderRepository.findByRemittanceOrderId(anyString()))
                .thenReturn(Optional.empty());

        Optional<RemittanceOrder> result = service.getRemittanceOrder("REM000000101000");

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnRemittanceOperations() {
        List<RemittanceOperation> operations = List.of(mockOperation);
        Page<RemittanceOperation> page = new PageImpl<>(operations);

        when(orderRepository.findByRemittanceOrderId(orderId))
                .thenReturn(Optional.of(mockOrder));
        when(operationRepository.findOperationsByRemittanceOrderId(
                eq(orderId), anyInt(), anyInt(), any(Pageable.class)))
                .thenReturn(page);

        Page<RemittanceOperation> result = service.getRemittanceOperations(orderId, 0, 10);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getNomBeneficiaire())
                .isEqualTo("LCL CREDIT LYONNAIS");
        assertThat(result.getContent().get(0).getMontant())
                .isEqualByComparingTo("500.00");
        assertThat(result.getContent().get(0).getReferenceBanque())
                .isEqualTo("IPF000000127611");
    }

    @Test
    void shouldReturnEmptyPageWhenOrderNotFound() {
        when(orderRepository.findByRemittanceOrderId(anyString()))
                .thenReturn(Optional.empty());

        Page<RemittanceOperation> result = service.getRemittanceOperations("REM000000101000", 0, 10);

        assertThat(result).isEmpty();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }
}