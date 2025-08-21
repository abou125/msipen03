package com.cl.msipen03.entities;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

import static org.assertj.core.api.Assertions.assertThat;

class RemittanceOrderTest {

    private final Timestamp saisieTimestamp = Timestamp.valueOf("2025-07-28 15:30:00");
    private final Date executionDate = Date.valueOf("2025-07-29");

    private RemittanceOrder createOrder(String id, int virements, String remiseNum, BigDecimal totalMontant) {
        RemittanceOrder order = new RemittanceOrder();
        order.setReferenceBanqueDeLaRemise(id);
        order.setDateEtHeureDeValidation(saisieTimestamp);
        order.setReferenceBanqueDeLaRemise(remiseNum);
        order.setStatutDeLaRemise("ACCEPTED");
        order.setLibelleDuCompteDO("COMPTE COURANT");
        order.setCompteDO("FR7630006000011234567890189");
        order.setNombreVirements(virements);
        order.setDateExecution(executionDate);
        order.setMontantTotalDeLaRemise(totalMontant);
        return order;
    }

    @Test
    void testShouldCreateRemittanceOrder() {
        RemittanceOrder order = createOrder("IPF000000127641", 10, "REM000000101065", new BigDecimal("1500"));

        assertThat(order.getDateEtHeureDeValidation()).isEqualTo(saisieTimestamp);
        assertThat(order.getReferenceBanqueDeLaRemise()).isEqualTo("REM000000101065");
        assertThat(order.getStatutDeLaRemise()).isEqualTo("ACCEPTED");
        assertThat(order.getLibelleDuCompteDO()).isEqualTo("COMPTE COURANT");
        assertThat(order.getCompteDO()).isEqualTo("FR7630006000011234567890189");
        assertThat(order.getNombreVirements()).isEqualTo(10);
        assertThat(order.getDateExecution()).isEqualTo(executionDate);
        assertThat(order.getMontantTotalDeLaRemise())
                .isEqualByComparingTo("1500.00");
    }

    @Test
    void testShouldSerializeToJson() throws Exception {
        RemittanceOrder order = createOrder("IPF000000127611", 3, "REM000000101065", new BigDecimal("42.50"));

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(order);

        assertThat(json).contains("\"referenceBanqueDeLaRemise\":\"REM000000101065\"");
        assertThat(json).contains("\"statutDeLaRemise\":\"ACCEPTED\"");
        assertThat(json).contains("\"libelleDuCompteDO\":\"COMPTE COURANT\"");
        assertThat(json).contains("\"compteDO\":\"FR7630006000011234567890189\"");
        assertThat(json).contains("\"nombreVirements\":3");
        assertThat(json).contains("\"dateEtHeureDeValidation\":");
    }
}