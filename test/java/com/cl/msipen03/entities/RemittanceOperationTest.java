package com.cl.msipen03.entities;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class RemittanceOperationTest {

    private RemittanceOperation createOp(String refBoutEnBout) {
        RemittanceOperation operation = new RemittanceOperation();
        operation.setNomBeneficiaire("LCL CREDIT LYONNAIS");
        operation.setIbanBeneficiaire("30002003870000017010T61");
        operation.setBicBeneficare("CRLYFRPPXXX");
        operation.setMontant(new BigDecimal("500.00"));
        operation.setDevise("EUR");
        operation.setMotifPaiement("0157390000153517262 REMONTEE COTISATIONS NEGATIVES PACIFICA");
        operation.setReferencePaiement("PACIFICA2000157390");
        operation.setReferenceBoutEnBout(refBoutEnBout);
        operation.setReferenceBanque("IPF000000127611");
        operation.setStatut("REJECTED");
        operation.setCodeRejetIso("AC03");
        operation.setMotifRejet("Specific transaction/message amount is greater than allowed maximum");
        return operation;
    }

    @Test
    void testShouldCreateRemittanceOperations() {
        RemittanceOperation op = createOp("0157390153517262");

        assertThat(op.getNomBeneficiaire()).isEqualTo("LCL CREDIT LYONNAIS");
        assertThat(op.getIbanBeneficiaire()).isEqualTo("30002003870000017010T61");
        assertThat(op.getBicBeneficare()).isEqualTo("CRLYFRPPXXX");
        assertThat(op.getMontant()).isEqualByComparingTo("500.00");
        assertThat(op.getDevise()).isEqualTo("EUR");
        assertThat(op.getMotifPaiement()).startsWith("0157390000153517262");
        assertThat(op.getReferencePaiement()).isEqualTo("PACIFICA2000157390");
        assertThat(op.getReferenceBoutEnBout()).isEqualTo("0157390153517262");
        assertThat(op.getReferenceBanque()).isEqualTo("IPF000000127611");
        assertThat(op.getStatut()).isEqualTo("REJECTED");
        assertThat(op.getMotifRejet()).contains("greater than allowed");
        assertThat(op.getCodeRejetIso()).isEqualTo("AC03");
    }

    @Test
    void testShouldSerializeToJson() throws Exception {
        RemittanceOperation op = createOp("IPF0000001276694");

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(op);

        assertThat(json).contains("\"referencePaiement\":\"PACIFICA2000157390\"");
        assertThat(json).contains("\"montant\":\"500.00\"");
        assertThat(json).contains("\"statut\":\"REJECTED\"");
    }
}