package com.cl.msipen03.entities;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

import static org.assertj.core.api.Assertions.assertThat;

class RemittanceOrderTest {

    private final Timestamp saisieTimestamp = Timestamp.valueOf("2025-07-28 15:30:00");
    private final Date executionDate        = Date.valueOf("2025-07-29");

    private RemittanceOrder createOrder(int id, int virements, String remiseNum, BigDecimal totalMontant) {
        return new RemittanceOrder(
                id,
                saisieTimestamp,
                remiseNum,
                "ACCEPTED",
                "COMPTE COURANT",
                "FR7630006000011234567890189",
                virements,
                executionDate,
                totalMontant
        );
    }

    @Test
    void testShouldCreateRemittanceOrder() {
        RemittanceOrder order = createOrder(5, 10, "REM000000101065", new BigDecimal("1500"));

      //  assertThat(order.id_reper()).isEqualTo(5);
        assertThat(order.date_et_heure_de_validation()).isEqualTo(saisieTimestamp);
        assertThat(order.reference_banque_de_la_remise()).isEqualTo("REM000000101065");
        assertThat(order.statut_de_la_remise()).isEqualTo("ACCEPTED");
        assertThat(order.libelle_du_compte_DO()).isEqualTo("COMPTE COURANT");
        assertThat(order.compte_DO()).isEqualTo("FR7630006000011234567890189");
        assertThat(order.nombre_virements()).isEqualTo(10);
        assertThat(order.date_execution()).isEqualTo(executionDate);
        assertThat(order.montant_total_de_la_remise())
                .isEqualByComparingTo("1500.00");
    }

    @Test
    void testShouldSerializeToJson() throws Exception {
        RemittanceOrder order = createOrder(7, 3, "REM000000101065", new BigDecimal("42.50"));

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(order);

        // All fields should be present
        assertThat(json).contains("\"id_reper\":7");
        assertThat(json).contains("\"reference_banque_de_la_remise\":\"REM000000101065\"");
        assertThat(json).contains("\"statut_de_la_remise\":\"ACCEPTED\"");
        assertThat(json).contains("\"libelle_du_compte_DO\":\"COMPTE COURANT\"");
        assertThat(json).contains("\"compte_DO\":\"FR7630006000011234567890189\"");
        assertThat(json).contains("\"nombre_virements\":3");

        assertThat(json).contains("\"date_et_heure_de_validation\":");

    }
}
