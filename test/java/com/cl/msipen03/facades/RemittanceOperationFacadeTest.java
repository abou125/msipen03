package com.cl.msipen03.facades;

import com.cl.msipen03.entities.RemittanceOperation;
import com.cl.msipen03.entities.RemittanceOrder;
import com.cl.msipen03.entities.RemittanceOrderPaginationResponse;
import com.cl.msipen03.service.RemittanceQueryService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RemittanceOperationFacadeTest {

    @Mock
    private RemittanceQueryService remittanceQueryService;

    @InjectMocks
    private RemittanceOperationsFacade facade;

    private RemittanceOrder mockRequest;
    private List<RemittanceOperation> mockOperations;

    @BeforeEach
    void setUp() {

        MDC.clear();
        Date executionDate = Date.valueOf("2025-07-28");
        LocalDateTime creationDate = LocalDateTime.of(2024, 6, 19, 14, 3, 41);

        mockRequest = new RemittanceOrder(
                1000001342,
                Timestamp.from(creationDate.toInstant(ZoneOffset.UTC)),
                "REM000000101000",
                "PART",
                "CONDAT SAS",
                "30002002600000000013S10",
                11,
                executionDate,
                new BigDecimal("100057.00")
        );

        mockOperations = Arrays.asList(
                new RemittanceOperation(
                        "LCL CREDIT LYONNAIS",
                        "30002003870000017010T61",
                        "CRLYFRPPXXX",
                        new BigDecimal("500.00"),
                        "EUR",
                        "0157390000153517262 REMONTEE COTISATIONS NEGATIVES PACIFICA",
                        "PACIFICA2000157390",
                        "0157390153517262",
                        "IPF000000127611",
                        "REJECTED",
                        "AC03",
                        "Specific transaction/message amount is greater than allowed maximum",
                        2L
                ),
                new RemittanceOperation(
                        "LCL CREDIT LYONNAIS",
                        "30002003870000017010T61",
                        "CRLYFRPPXXX",
                        new BigDecimal("500.00"),
                        "EUR",
                        "0157390000153517262 REMONTEE COTISATIONS NEGATIVES PACIFICA",
                        "PACIFICA2000157390",
                        "0157390153517262",
                        "IPF000000127611",
                        "REJECTED",
                        "AC03",
                        "Specific transaction/message amount is greater than allowed maximum",
                        2L
                )
        );
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void testShouldReturnPaginatedResponse() {
        Page<RemittanceOperation> page = new PageImpl<>(mockOperations, PageRequest.of(0, 10), 11);
        when(remittanceQueryService.getRemittanceOperations(anyString(), anyInt(), anyInt()))
                .thenReturn(page);
        when(remittanceQueryService.getRemittanceOrder(anyString()))
                .thenReturn(Optional.of(mockRequest));

        RemittanceOrderPaginationResponse response = facade.getOperationListResponse(
                "REM000000101000", 0, 10);

        assertThat(response.size()).isEqualTo(10);
        assertThat(response.page()).isZero();
        assertThat(response.total()).isEqualTo(11);
        assertThat(response.list()).hasSize(2);
        assertThat(response.request()).isNotNull();
        assertThat(response.request().reference_banque_de_la_remise()).isEqualTo("REM000000101000");
        assertThat(MDC.get("CORRELATION_ID")).isNotNull();
    }

    @Test
    void testShouldHandleEmptyResponse() {
        Page<RemittanceOperation> emptyPage = new PageImpl<>(
                List.of(),
                PageRequest.of(0, 10),
                0
        );

        when(remittanceQueryService.getRemittanceOperations(anyString(), anyInt(), anyInt()))
                .thenReturn(emptyPage);
        when(remittanceQueryService.getRemittanceOrder(anyString()))
                .thenReturn(Optional.of(mockRequest));

        RemittanceOrderPaginationResponse response = facade.getOperationListResponse(
                "REM000000101000", 0, 10);

        assertThat(response.size()).isEqualTo(10);
        assertThat(response.page()).isZero();
        assertThat(response.total()).isZero();
        assertThat(response.list()).isEmpty();
        assertThat(response.request()).isNotNull();
        assertThat(MDC.get("CORRELATION_ID")).isNotNull();
    }

    @Test
    void testShouldHandlePaginationParameters() {
        Page<RemittanceOperation> page = new PageImpl<>(mockOperations, PageRequest.of(1, 2), 11);

        when(remittanceQueryService.getRemittanceOperations(anyString(), anyInt(), anyInt()))
                .thenReturn(page);
        when(remittanceQueryService.getRemittanceOrder(anyString()))
                .thenReturn(Optional.of(mockRequest));

        RemittanceOrderPaginationResponse response = facade.getOperationListResponse(
                "REM000000101000", 1, 2);

        assertThat(response.size()).isEqualTo(2);
        assertThat(response.page()).isEqualTo(1);
        assertThat(response.total()).isEqualTo(11);
        assertThat(response.list()).hasSize(2);
        assertThat(response.request()).isNotNull();
        assertThat(MDC.get("CORRELATION_ID")).isNotNull();
    }
}