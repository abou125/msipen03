package com.cl.msipen03.service;

import com.cl.msipen03.entities.RemittanceOperation;
import com.cl.msipen03.entities.RemittanceOrder;
import com.cl.msipen03.repository.impl.RemittanceRepositoryImpl;
import com.cl.msipen03.service.impl.RemittanceQueryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
class RemittanceQueryServiceImplTest {

    @Mock
    private RemittanceRepositoryImpl remittanceRepository;

    @InjectMocks
    private RemittanceQueryServiceImpl service;

    private List<RemittanceOperation> mockOperations;
    private RemittanceOrder mockRequest;
    private Page<RemittanceOperation> mockPage;

    @BeforeEach
    void setUp() {
        Date executionDate = Date.valueOf("2025-07-28");
        LocalDateTime creationDate = LocalDateTime.of(2024, 6, 19, 14, 3, 41);

        mockRequest = new RemittanceOrder(
                1000001342,
                Timestamp.from(creationDate.toInstant(ZoneOffset.UTC)),
                "REM000000101065",
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
                        "PACIFICA2000157390",
                        "IPF000000127611",
                        "REJECTED",
                        "AC03",
                        "Specific transaction/message amount is greater than allowed maximum",
                        2L
                ),
                new RemittanceOperation(
                        "LCL CREDIT LYONNAIS",
                        "30002095050000079135P57",
                        "CRLYFRPPXXX",
                        new BigDecimal("500.00"),
                        "EUR",
                        "0157390000153517262 REMONTEE COTISATIONS NEGATIVES PACIFICA",
                        "PACIFICA2000157390",
                        "PACIFICA2000157390",
                        "BNK124",
                        "REJECTED",
                        "AC03",
                        "Account number is invalid or missing.",
                        2L
                )
        );

        mockPage = new PageImpl<>(mockOperations, PageRequest.of(0, 10), 2);
    }

    @Test
    void testShouldReturnRemittanceOperations() {
        when(remittanceRepository.findOperationsByRemittanceOrderId(anyString(), anyInt(), anyInt()))
                .thenReturn(mockPage);

        Page<RemittanceOperation> result = service.getRemittanceOperations("REM000000101065", 0, 10);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2L);
        assertThat(result.getContent().get(0).nom_beneficiaire()).isEqualTo("LCL CREDIT LYONNAIS");
        assertThat(result.getContent().get(0).bic_beneficiare()).isEqualTo("CRLYFRPPXXX");
        assertThat(result.getContent().get(0).montant()).isEqualTo(new BigDecimal("500.00"));
        assertThat(result.getContent().get(0).reference_Bout_en_bout()).isEqualTo("PACIFICA2000157390");
    }

    @Test
    void testShouldReturnRemittanceRequest() {
        when(remittanceRepository.findByRemittanceOrderId(anyString()))
                .thenReturn(Optional.of(mockRequest));

        Optional<RemittanceOrder> result = service.getRemittanceOrder("REM000000101065");

        assertThat(result).isPresent();
        assertThat(result.get().reference_banque_de_la_remise()).isEqualTo("REM000000101065");
        assertThat(result.get().statut_de_la_remise()).isEqualTo("PART");
        assertThat(result.get().libelle_du_compte_DO()).isEqualTo("CONDAT SAS");
        assertThat(result.get().montant_total_de_la_remise()).isEqualTo(new BigDecimal("100057.00"));
    }

    @Test
    void testShouldReturnEmptyPage() {
        Page<RemittanceOperation> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
        when(remittanceRepository.findOperationsByRemittanceOrderId(anyString(), anyInt(), anyInt()))
                .thenReturn(emptyPage);

        Page<RemittanceOperation> result = service.getRemittanceOperations("REM000000101065", 0, 10);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }
}