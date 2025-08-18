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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class RemittanceExcelFileServiceImplTest {

    private final RemittanceExcelFileServiceImpl service = new RemittanceExcelFileServiceImpl();

    private final Timestamp executionDateHour = Timestamp.valueOf("2025-07-28 15:30:00");
    private final Date executionDate = Date.valueOf("2025-07-29");

    private RemittanceOrder buildOrder() {
        return new RemittanceOrder(
                42,
                executionDateHour,
                "REM000000101000",
                "PARTIAL",
                "CONDAT SAS",
                "FR7630006000011234567890189",
                2,
                executionDate,
                new BigDecimal("1234.50")
        );
    }

    private RemittanceOperation buildOperation(String suffix) {
        return new RemittanceOperation(
                "LCL CREDIT LYONNAIS" + suffix,
                "30002003870000017010T61" + suffix,
                "CRLYFRPPXXX" + suffix,
                new BigDecimal("500.00"),
                "EUR",
                "REMONTEE " + suffix,
                "PACIFICA2000157390" + suffix,
                "0157390153517262" + suffix,
                "IPF000000127669" + suffix,
                "ACCEPTED",
                "AC03",
                "Creditor account number invalid or missing",
                0
        );
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

            // --- Table headers at row 12
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
                    .isEqualTo(firstOp.nom_beneficiaire());
            assertThat(opRow.getCell(1).getStringCellValue())
                    .isEqualTo(firstOp.iban_beneficiaire());
            assertThat(opRow.getCell(2).getStringCellValue())
                    .isEqualTo(firstOp.bic_beneficiare());

        }
    }
}
