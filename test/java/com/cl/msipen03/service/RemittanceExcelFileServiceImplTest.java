package com.cl.msipen03.service;

import com.cl.msipen03.entities.RemittanceOperation;
import com.cl.msipen03.entities.RemittanceOrder;
import com.cl.msipen03.entities.RemittanceOrderPaginationResponse;
import com.cl.msipen03.service.impl.RemittanceExcelFileServiceImpl;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RemittanceExcelFileServiceImplTest {

    private final RemittanceExcelFileServiceImpl service = new RemittanceExcelFileServiceImpl();
    private final Timestamp executionDateHour = Timestamp.valueOf("2025-07-28 15:30:00");
    private final Date executionDate = Date.valueOf("2025-07-29");

    private RemittanceOrder buildOrder() {
        RemittanceOrder order = new RemittanceOrder();
        order.setReferenceBanqueDeLaRemise("IPF000000127611");
        order.setDateEtHeureDeValidation(executionDateHour);
        order.setReferenceBanqueDeLaRemise("REM000000101000");
        order.setStatutDeLaRemise("PARTIAL");
        order.setLibelleDuCompteDO("CONDAT SAS");
        order.setCompteDO("FR7630006000011234567890189");
        order.setNombreVirements(2);
        order.setDateExecution(executionDate);
        order.setMontantTotalDeLaRemise(new BigDecimal("1234.50"));
        return order;
    }

    private RemittanceOperation buildOperation(String suffix) {
        RemittanceOperation operation = new RemittanceOperation();
        operation.setNomBeneficiaire("LCL CREDIT LYONNAIS" + suffix);
        operation.setIbanBeneficiaire("30002003870000017010T61" + suffix);
        operation.setBicBeneficare("CRLYFRPPXXX" + suffix);
        operation.setMontant(new BigDecimal("500.00"));
        operation.setDevise("EUR");
        operation.setMotifPaiement("REMONTEE " + suffix);
        operation.setReferencePaiement("PACIFICA2000157390" + suffix);
        operation.setReferenceBoutEnBout("0157390153517262" + suffix);
        operation.setReferenceBanque("IPF000000127669" + suffix);
        operation.setStatut("ACCEPTED");
        operation.setCodeRejetIso("AC03");
        operation.setMotifRejet("Creditor account number invalid or missing");
        return operation;
    }

    @Test
    void shouldGenerateExcel_withSyntheseAndOperations() throws Exception {
        var mockResponse = mock(RemittanceOrderPaginationResponse.class);
        RemittanceOrder order = buildOrder();
        List<RemittanceOperation> ops = List.of(buildOperation("A"), buildOperation("B"));

        when(mockResponse.request()).thenReturn(order);
        when(mockResponse.list()).thenReturn(ops);

        ByteArrayInputStream in = service.generateExcelContent(mockResponse);
        assertThat(in).isNotNull();

        try (var wb = new XSSFWorkbook(in)) {
            Sheet sheet = wb.getSheet("Liste des virements");
            assertThat(sheet).isNotNull();

            Row titleRow = sheet.getRow(0);
            assertThat(titleRow.getCell(0).getStringCellValue())
                    .isEqualTo("COMPTE RENDU DE TRAITEMENT DE LA REMISE");

            Row virementsTitle = sheet.getRow(10);
            assertThat(virementsTitle.getCell(0).getStringCellValue())
                    .isEqualTo("DÉTAIL DES OPERATIONS DE LA REMISE ");

            Row headerRow = sheet.getRow(11);
            String[] expectedHeaders = {
                    "Nom bénéficiaire", "Iban bénéficiaire", "Bic bénéficiaire ", "Montant", "Devise", "Motif du paiement",
                    "Référence du paiement", "Référence de Bout en bout", "Référence Banque", "Statut",
                    "Code rejet ISO", "Motif de rejet"
            };
            for (int i = 0; i < expectedHeaders.length; i++) {
                assertThat(headerRow.getCell(i).getStringCellValue())
                        .isEqualTo(expectedHeaders[i]);
            }

            // First operation row at 13
            Row opRow = sheet.getRow(12);
            RemittanceOperation firstOp = ops.get(0);

            assertThat(opRow.getCell(0).getStringCellValue())
                    .isEqualTo(firstOp.getNomBeneficiaire());
            assertThat(opRow.getCell(1).getStringCellValue())
                    .isEqualTo(firstOp.getIbanBeneficiaire());
            assertThat(opRow.getCell(2).getStringCellValue())
                    .isEqualTo(firstOp.getBicBeneficare());
        }
    }
}