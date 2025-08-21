package com.cl.msipen03.facades;

import com.cl.logs.commun.CommonLoggerFactory;
import com.cl.msipen03.entities.RemittanceOrderPaginationResponse;
import com.cl.msipen03.service.RemittanceExcelFileService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class RemittanceExcelFileFacade {


    private static final Logger LOGGER = CommonLoggerFactory.getLogger(RemittanceExcelFileFacade.class).logger();
    private final RemittanceOperationsFacade remittanceOperationsFacade;
    private final RemittanceExcelFileService remittanceBuildExcelService;

    public ResponseEntity<Resource> generateExcelFile(String remittanceOrderId) {
        LOGGER.info("Preparing Excel export for remittanceOrderId: {}", remittanceOrderId);

        RemittanceOrderPaginationResponse response = remittanceOperationsFacade
                .getOperationListResponse(remittanceOrderId, 0, Integer.MAX_VALUE);

        Integer idReper = response.request().getIdReper();
        LOGGER.info("Processing export for id_reper: {}", idReper);

        ByteArrayInputStream excelContent = remittanceBuildExcelService.generateExcelContent(response);
        String filename = generateFilename(remittanceOrderId);

        return createExcelResponse(excelContent, filename);
    }



    public String generateFilename(String remittanceOrderId) {
        return String.format("Remise_%s_%s.xlsx", remittanceOrderId, LocalDate.now());
    }

    private ResponseEntity<Resource> createExcelResponse(ByteArrayInputStream content, String filename) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(content));
    }
}


