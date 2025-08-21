package com.cl.msipen03.controllers;

import com.cl.msipen03.entities.*;
import com.cl.msipen03.exceptions.InternalMSIPEN03Exception;
import com.cl.msipen03.facades.RemittanceOperationsFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RemittanceOperationStatusControllerTest {

    @Mock
    private RemittanceOperationsFacade remittanceOperationsFacade;

    @InjectMocks
    private RemittanceOperationsStatusController controller;

    private RemittanceOrderPaginationResponse mockResponse;
    private RemittanceOrder mockRemittanceOrder;
    private List<RemittanceOperation> operations;

    @BeforeEach
    void setUp() {
        mockRemittanceOrder = createMockRemittanceOrder();

        operations = createMockOperations();

        mockResponse = createMockResponse();
    }

    private RemittanceOrder createMockRemittanceOrder() {
        RemittanceOrder order = new RemittanceOrder();
        order.setReferenceBanqueDeLaRemise("1000001342");
        order.setDateEtHeureDeValidation(Timestamp.from(Instant.now()));
        order.setReferenceBanqueDeLaRemise("REM000000101000");
        order.setStatutDeLaRemise("REJECTED");
        order.setLibelleDuCompteDO("CONDAT SAS");
        order.setCompteDO("30002002600000000013S10");
        order.setNombreVirements(2);
        order.setDateExecution(Date.valueOf("2025-07-28"));
        order.setMontantTotalDeLaRemise(new BigDecimal("1000.00"));
        return order;
    }

    private List<RemittanceOperation> createMockOperations() {
        RemittanceOperation operation1 = new RemittanceOperation();
        operation1.setNomBeneficiaire("LCL CREDIT LYONNAIS");
        operation1.setIbanBeneficiaire("30002003870000017010T61");
        operation1.setBicBeneficare("CRLYFRPPXXX");
        operation1.setMontant(new BigDecimal("500.00"));
        operation1.setDevise("EUR");
        operation1.setMotifPaiement("0157390000153517262 REMONTEE COTISATIONS NEGATIVES PACIFICA");
        operation1.setReferencePaiement("PACIFICA2000157390");
        operation1.setReferenceBoutEnBout("0157390153517262");
        operation1.setReferenceBanque("IPF000000127611");
        operation1.setStatut("REJECTED");
        operation1.setCodeRejetIso("AC03");
        operation1.setMotifRejet("Specific transaction/message amount is greater than allowed maximum");

        RemittanceOperation operation2 = new RemittanceOperation();
        operation2.setNomBeneficiaire("LCL CREDIT LYONNAIS");
        operation2.setIbanBeneficiaire("30002095050000079135P57");
        operation2.setBicBeneficare("CRLYFRPPXXX");
        operation2.setMontant(new BigDecimal("500.00"));
        operation2.setDevise("EUR");
        operation2.setMotifPaiement("0157390000153517262 REMONTEE COTISATIONS NEGATIVES PACIFICA");
        operation2.setReferencePaiement("PACIFICA2000157390");
        operation2.setReferenceBoutEnBout("0157390153517262");
        operation2.setReferenceBanque("IPF0000001276694");
        operation2.setStatut("REJECTED");
        operation2.setCodeRejetIso("AC03");
        operation2.setMotifRejet("Account number is invalid or missing.");

        return Arrays.asList(operation1, operation2);
    }

    private RemittanceOrderPaginationResponse createMockResponse() {
        return RemittanceOrderPaginationResponse.builder()
                .size(10)
                .page(0)
                .total(2L)
                .request(mockRemittanceOrder)
                .list(operations)
                .build();
    }

    @Test
    void testShouldReturnOperationsList() throws InternalMSIPEN03Exception {
        when(remittanceOperationsFacade.getOperationListResponse(anyString(), anyInt(), anyInt()))
                .thenReturn(mockResponse);

        ResponseEntity<RemittanceOrderPaginationResponse> response =
                controller.getOperationList("IPF000000127670", 0, 10);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().list()).hasSize(2);
        assertThat(response.getBody().total()).isEqualTo(2L);
        assertThat(response.getBody().request()).isNotNull();
    }

    @Test
    void testShouldHandleInternalException() {
        when(remittanceOperationsFacade.getOperationListResponse(anyString(), anyInt(), anyInt()))
                .thenThrow(new InternalMSIPEN03Exception("Database error"));

        assertThatThrownBy(() ->
                controller.getOperationList("IPF000000127670", 0, 10))
                .isInstanceOf(InternalMSIPEN03Exception.class)
                .hasMessage("Database error");
    }

    @Test
    void testShouldReturnEmptyList() throws InternalMSIPEN03Exception {
        RemittanceOrderPaginationResponse emptyResponse = RemittanceOrderPaginationResponse.builder()
                .size(10)
                .page(0)
                .total(0L)
                .request(mockRemittanceOrder)
                .list(List.of())
                .build();

        when(remittanceOperationsFacade.getOperationListResponse(anyString(), anyInt(), anyInt()))
                .thenReturn(emptyResponse);

        ResponseEntity<RemittanceOrderPaginationResponse> response =
                controller.getOperationList("REM000000101000", 0, 10);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().list()).isEmpty();
        assertThat(response.getBody().total()).isZero();
        assertThat(response.getBody().request()).isNotNull();
    }
}