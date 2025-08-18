package com.cl.msipen03.entities;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Date;

import static org.assertj.core.api.Assertions.assertThat;

class RemittanceOperationTest {


    private RemittanceOperation createOp(String refBout, long count) {
        return new RemittanceOperation(
                "LCL CREDIT LYONNAIS",
                "30002003870000017010T61",
                "CRLYFRPPXXX",
                new BigDecimal("500.00"),
                "EUR",
                "0157390000153517262 REMONTEE COTISATIONS NEGATIVES PACIFICA",
                "PACIFICA2000157390",
                refBout,
                "IPF000000127611",
                "REJECTED",
                "AC03",
                "Specific transaction/message amount is greater than allowed maximum",
                count
        );
    }

    @Test
    void testShouldCreateRemittanceOperations() {
        RemittanceOperation op = createOp("0157390153517262", 2L);

        assertThat(op.nom_beneficiaire()).isEqualTo("LCL CREDIT LYONNAIS");
        assertThat(op.iban_beneficiaire()).isEqualTo("30002003870000017010T61");
        assertThat(op.bic_beneficiare()).isEqualTo("CRLYFRPPXXX");
        assertThat(op.montant()).isEqualByComparingTo("500.00");
        assertThat(op.devise()).isEqualTo("EUR");
        assertThat(op.motif_paiement()).startsWith("0157390000153517262");
        assertThat(op.reference_paiement()).isEqualTo("PACIFICA2000157390");
        assertThat(op.reference_Bout_en_bout()).isEqualTo("0157390153517262");
        assertThat(op.reference_Banque()).isEqualTo("IPF000000127611");
        assertThat(op.statut()).isEqualTo("REJECTED");
        assertThat(op.motif_rejet()).contains("greater than allowed");
        assertThat(op.totalCount()).isEqualTo(2L);
    }

    @Test
    void testShouldSerializeToJson() throws Exception {
        RemittanceOperation op = createOp("IPF0000001276694", 2L);

        ObjectMapper mapper = new ObjectMapper();

        String json = mapper.writeValueAsString(op);

        assertThat(json).contains("\"reference_paiement\":\"PACIFICA2000157390\"");
        assertThat(json).doesNotContain("totalCount");
    }
}
