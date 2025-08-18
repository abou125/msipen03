package com.cl.msipen03.service.impl;

import com.cl.logs.commun.CommonLoggerFactory;
import com.cl.msipen03.entities.RemittanceOperation;
import com.cl.msipen03.entities.RemittanceOrder;
import com.cl.msipen03.entities.RemittanceOrderPaginationResponse;
import com.cl.msipen03.exceptions.InternalMSIPEN03Exception;
import com.cl.msipen03.service.RemittanceExcelFileService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.cl.msipen03.utils.Utils.*;

@Service
public class RemittanceExcelFileServiceImpl implements RemittanceExcelFileService {

    private static final Logger LOGGER = CommonLoggerFactory.getLogger(RemittanceExcelFileServiceImpl.class).logger();

    private  CellStyle headerStyle;

    private static final String[] TABLE_HEADERS = {
            "Nom bénéficiaire", "Iban bénéficiaire", "Bic bénéficiaire ", "Montant", "Devise", "Motif du paiement",
            "Référence du paiement", "Référence de Bout en bout", "Référence Banque", "Statut",
            "Code rejet ISO", "Motif de rejet"
    };

    @Override
    public ByteArrayInputStream generateExcelContent(RemittanceOrderPaginationResponse response) {
        try (var workbook = new XSSFWorkbook();
             var out = new ByteArrayOutputStream()) {

            headerStyle = createHeaderStyle(workbook);

            LOGGER.info("Starting Generation of excel file");
            Sheet sheet = workbook.createSheet("Liste des virements");

            int currentRow = createSyntheseSection(sheet, response.request());
            createVirementsSection(sheet, currentRow, response);

            autoSizeColumns(sheet);
            workbook.write(out);

            LOGGER.info("Generation Excel file done");
            return new ByteArrayInputStream(out.toByteArray());

        } catch (Exception e) {
            LOGGER.error("Failed to generate Excel file", e);
            throw new InternalMSIPEN03Exception("Failed to generate Excel content", e);
        }
    }

    private int createSyntheseSection(Sheet sheet, RemittanceOrder req) {

        int rowNum = 0;
        rowNum = addSectionTitle(sheet, rowNum, "COMPTE RENDU DE TRAITEMENT DE LA REMISE");

        Map<String, Object> synthese = new LinkedHashMap<>();
        synthese.put("Date et heure de validation de la remise", req.date_et_heure_de_validation().toLocalDateTime().format(TIMESTAMP_FORMATTER)); //TODO: gére le null Pointer Exception
        synthese.put("Référence Banque de la remise", req.reference_banque_de_la_remise());
        synthese.put("Statut de la remise", req.statut_de_la_remise());
        synthese.put("Libellé du compte donneur d'ordre", req.libelle_du_compte_DO());
        synthese.put("Compte donneur d'ordre", req.compte_DO());
        synthese.put("Nombre de virements", req.nombre_virements());
        synthese.put("Date d'exécution", req.date_execution().toLocalDate().format(DATE_FORMATTER));
        synthese.put("Montant total de la remise", req.montant_total_de_la_remise());

        return addKeyValuePairs(sheet, rowNum, synthese) + 1;
    }

    private int createVirementsSection(Sheet sheet, int startRow, RemittanceOrderPaginationResponse response) {

        int rowNum = startRow;
        rowNum = addSectionTitle(sheet, rowNum, "DÉTAIL DES OPERATIONS DE LA REMISE ");
        rowNum = addTableHeaders(sheet, rowNum);
        return addOperationsData(sheet, rowNum, response.list()
        );
    }


    private int addSectionTitle(Sheet sheet, int rowNum, String title) {
        Row titleRow = sheet.createRow(rowNum);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue(title);
        titleCell.setCellStyle(headerStyle);

        return rowNum + 1;
    }

    private int addKeyValuePairs(Sheet sheet, int startRow, Map<String, Object> data) {
        int rowNum = startRow;
        for (var entry : data.entrySet()) {
            Row row = sheet.createRow(rowNum++);
            Cell keyCell = row.createCell(0);
            keyCell.setCellValue(entry.getKey());

            Cell valCell = row.createCell(1);
            Object val = entry.getValue();

            if (val instanceof Timestamp) {
                valCell.setCellValue(((Timestamp) val).toLocalDateTime());

            } else {
                valCell.setCellValue(val.toString());
            }
        }
        return rowNum;
    }

    private int addTableHeaders(Sheet sheet, int rowNum) {

        Row headerRow = sheet.createRow(rowNum);
        for (int i = 0; i < TABLE_HEADERS.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(TABLE_HEADERS[i]);
            cell.setCellStyle(headerStyle);
        }
        return rowNum + 1;
    }


    private int addOperationsData(Sheet sheet, int startRow, Iterable<RemittanceOperation> operations) {

        int rowNum = startRow;
        for (RemittanceOperation op : operations) {
            Row row = sheet.createRow(rowNum++);
            addOperationRow(row, op);
        }
        return rowNum;
    }


    private void addOperationRow(Row row, RemittanceOperation op) {

        int col = 0;
        row.createCell(col++).setCellValue(op.nom_beneficiaire());
        row.createCell(col++).setCellValue(op.iban_beneficiaire());
        row.createCell(col++).setCellValue(op.bic_beneficiare());
        row.createCell(col++).setCellValue(op.montant().toString()); //TODO: fix nullPointerException
        row.createCell(col++).setCellValue(op.devise());
        row.createCell(col++).setCellValue(op.motif_paiement());
        row.createCell(col++).setCellValue(op.reference_paiement());
        row.createCell(col++).setCellValue(op.reference_Bout_en_bout());
        row.createCell(col++).setCellValue(op.reference_Banque());
        row.createCell(col++).setCellValue(op.statut());
        row.createCell(col++).setCellValue(op.code_rejet_iso());
        row.createCell(col).setCellValue(op.motif_rejet());
    }

    private void autoSizeColumns(Sheet sheet) {
        for (int i = 0; i < TABLE_HEADERS.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }


}