package com.cl.msipen03.repository;

import com.cl.msipen03.entities.RemittanceOperation;
import com.cl.msipen03.entities.RemittanceOrder;
import com.cl.msipen03.mapper.RemittanceOperationsMapper;
import com.cl.msipen03.mapper.RemittanceOrderMapper;
import com.cl.msipen03.repository.impl.RemittanceRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
class RemittanceRepositoryImplTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private JdbcTemplate jdbcTemplate;

    private RemittanceRepositoryImpl remittanceRepository;

    @BeforeEach
    void setUp() {
        remittanceRepository = new RemittanceRepositoryImpl(dataSource);
        ReflectionTestUtils.setField(remittanceRepository, "jdbcTemplate", jdbcTemplate);
    }

    @Test
    void testShouldReturnRemittanceRequest() {
        Date executionDate = Date.valueOf("2025-07-28");
        LocalDateTime creationDate = LocalDateTime.of(2024, 6, 19, 14, 3, 41);

        RemittanceOrder mockRequest = new RemittanceOrder(
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

        when(jdbcTemplate.queryForObject(
                anyString(),
                any(RemittanceOrderMapper.class),
                anyString()
        )).thenReturn(mockRequest);

        Optional<RemittanceOrder> result = remittanceRepository.findByRemittanceOrderId("REM000000101000");

        assertThat(result).isPresent();
        assertThat(result.get().reference_banque_de_la_remise()).isEqualTo("REM000000101000");
        assertThat(result.get().statut_de_la_remise()).isEqualTo("PART");
        assertThat(result.get().libelle_du_compte_DO()).isEqualTo("CONDAT SAS");
    }

    @Test
    void testShouldReturnPagedOperationsWhenDataExists() {
        List<RemittanceOperation> mockOperations = Arrays.asList(
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
                        "30002095050000079135P57",
                        "CRLYFRPPXXX",
                        new BigDecimal("500.00"),
                        "EUR",
                        "0157390000153517262 REMONTEE COTISATIONS NEGATIVES PACIFICA",
                        "PACIFICA2000157390",
                        "IPF0000001276694",
                        "BNK124",
                        "REJECTED",
                        "AC03",
                        "Account number is invalid or missing.",
                        2L
                )
        );

        when(jdbcTemplate.query(
                anyString(),
                any(RemittanceOperationsMapper.class),
                anyString(),
                anyInt(),
                anyInt()
        )).thenReturn(mockOperations);

        Page<RemittanceOperation> result = remittanceRepository.findOperationsByRemittanceOrderId(
                "REM000000101000",
                0,
                10
        );

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2L);
        assertThat(result.getNumber()).isZero();
        assertThat(result.getSize()).isEqualTo(10);
    }

    @Test
    void testShouldReturnEmptyPageWhenNoDataExists() {
        when(jdbcTemplate.query(
                anyString(),
                any(RemittanceOperationsMapper.class),
                anyString(),
                anyInt(),
                anyInt()
        )).thenReturn(Collections.emptyList());

        Page<RemittanceOperation> result = remittanceRepository.findOperationsByRemittanceOrderId(
                "REM000000101000",
                0,
                10
        );

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
        assertThat(result.getNumber()).isZero();
        assertThat(result.getSize()).isEqualTo(10);
    }

    @Test
    void testShouldHandlePagination() {
        List<RemittanceOperation> mockOperations = Arrays.asList(
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
                        "30002095050000079135P57",
                        "CRLYFRPPXXX",
                        new BigDecimal("500.00"),
                        "EUR",
                        "0157390000153517262 REMONTEE COTISATIONS NEGATIVES PACIFICA",
                        "PACIFICA2000157390",
                        "IPF0000001276694",
                        "BNK124",
                        "REJECTED",
                        "AC03",
                        "Account number is invalid or missing.",
                        2L
                )
        );

        when(jdbcTemplate.query(
                anyString(),
                any(RemittanceOperationsMapper.class),
                anyString(),
                anyInt(),
                anyInt()
        )).thenReturn(mockOperations);

        Page<RemittanceOperation> result = remittanceRepository.findOperationsByRemittanceOrderId(
                "REM000000101000",
                1,
                2
        );

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(4L);
        assertThat(result.getNumber()).isEqualTo(1);
        assertThat(result.getSize()).isEqualTo(2);
    }
}