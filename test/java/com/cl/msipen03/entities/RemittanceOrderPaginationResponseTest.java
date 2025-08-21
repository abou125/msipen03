package com.cl.msipen03.entities;

import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RemittanceOrderPaginationResponseTest {

    private final Date executionDate = Date.valueOf("2025-07-28");

    private RemittanceOrder createRemittanceOrder(int nombreVirements, BigDecimal montant) {
        RemittanceOrder request = new RemittanceOrder();
        request.setReferenceBanqueDeLaRemise("1000001342");
        request.setDateEtHeureDeValidation(Timestamp.from(LocalDateTime.now().toInstant(ZoneOffset.UTC)));
        request.setReferenceBanqueDeLaRemise("REM000000101000");
        request.setStatutDeLaRemise("PART");
        request.setLibelleDuCompteDO("CONDAT SAS");
        request.setCompteDO("30002002600000000013S10");
        request.setNombreVirements(nombreVirements);
        request.setDateExecution(executionDate);
        request.setMontantTotalDeLaRemise(montant);
        return request;
    }

    private RemittanceOperation createRemittanceOperation(String referenceBoutEnBout, String referenceBanque, String motifRejet) {
        RemittanceOperation operation = new RemittanceOperation();
        operation.setNomBeneficiaire("LCL CREDIT LYONNAIS");
        operation.setIbanBeneficiaire("30002003870000017010T61");
        operation.setBicBeneficare("CRLYFRPPXXX");
        operation.setMontant(new BigDecimal("500.00"));
        operation.setDevise("EUR");
        operation.setMotifPaiement("0157390000153517262 REMONTEE COTISATIONS NEGATIVES PACIFICA");
        operation.setReferencePaiement("PACIFICA2000157390");
        operation.setReferenceBoutEnBout(referenceBoutEnBout);
        operation.setReferenceBanque(referenceBanque);
        operation.setStatut("REJECTED");
        operation.setCodeRejetIso("AC03");
        operation.setMotifRejet(motifRejet);
        return operation;
    }

    @Test
    void testShouldCreatePaginationResponse() {
        RemittanceOrder request = createRemittanceOrder(2, new BigDecimal("1000.00"));

        List<RemittanceOperation> operations = Arrays.asList(
                createRemittanceOperation(
                        "0157390153517262",
                        "IPF000000127611",
                        "Specific transaction/message amount is greater than allowed maximum"
                ),
                createRemittanceOperation(
                        "IPF0000001276694",
                        "BNK124",
                        "Account number is invalid or missing."
                )
        );

        RemittanceOrderPaginationResponse response = RemittanceOrderPaginationResponse.builder()
                .size(10)
                .page(0)
                .total(2L)
                .request(request)
                .list(operations)
                .build();

        assertThat(response.size()).isEqualTo(10);
        assertThat(response.page()).isZero();
        assertThat(response.total()).isEqualTo(2L);
        assertThat(response.list()).hasSize(2);
        assertThat(response.request()).isNotNull();
    }

    @Test
    void testShouldSerializeToJson() throws Exception {
        RemittanceOrder request = createRemittanceOrder(1, new BigDecimal("500.00"));

        List<RemittanceOperation> operations = List.of(
                createRemittanceOperation(
                        "0157390153517262",
                        "IPF000000127611",
                        "Specific transaction/message amount is greater than allowed maximum"
                )
        );

        RemittanceOrderPaginationResponse response = RemittanceOrderPaginationResponse.builder()
                .size(10)
                .page(0)
                .total(1L)
                .request(request)
                .list(operations)
                .build();

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(response);

        assertThat(json).contains("\"size\":10");
        assertThat(json).contains("\"page\":0");
        assertThat(json).contains("\"total\":1");
        assertThat(json).contains("\"COMPTE_RENDU_DE_TRAITEMENT_DE_LA_REMISE\":");
        assertThat(json).contains("\"DETAIL_DES_OPERATIONS_DE_LA_REMISE \":");
    }
}