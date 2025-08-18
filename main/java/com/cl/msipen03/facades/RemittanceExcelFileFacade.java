package com.cl.msipen03.facades;

import com.cl.logs.commun.CommonLoggerFactory;
import com.cl.msipen03.entities.RemitanceExcelFile;
import com.cl.msipen03.entities.RemittanceOrderPaginationResponse;
import com.cl.msipen03.service.RemittanceExcelFileService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class RemittanceExcelFileFacade {

    private static final Logger LOGGER = CommonLoggerFactory.getLogger(RemittanceExcelFileFacade.class).logger();
    private final RemittanceOperationsFacade remittanceOperationsFacade;
    private final RemittanceExcelFileService remittanceExcelFileService;

    public RemitanceExcelFile generateExcelFile(String remittanceOrderId) {
        LOGGER.info("Preparing Excel file for remittanceOrderId: {}", remittanceOrderId);

        RemittanceOrderPaginationResponse response = remittanceOperationsFacade
                .getOperationListResponse(remittanceOrderId, 0, Integer.MAX_VALUE);

        Integer idReper = response.request().id_reper();
        LOGGER.info("Processing export for id_reper: {}", idReper);

        ByteArrayInputStream contentOfExcelFile = remittanceExcelFileService.generateExcelContent(response);

        generateFilename(remittanceOrderId);

        return new RemitanceExcelFile(contentOfExcelFile, idReper);
    }

    public String generateFilename(String remittanceOrderId) {
        return String.format("Remise_%s_%s.xlsx", remittanceOrderId, LocalDate.now());
    }

}