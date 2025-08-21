package com.cl.msipen03.controllers;

import com.cl.msipen03.entities.RemittanceOrderPaginationResponse;
import com.cl.msipen03.facades.RemittanceExcelFileFacade;
import com.cl.msipen03.facades.RemittanceOperationsFacade;
import io.swagger.v3.oas.annotations.Parameter;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.cl.logs.commun.CommonLoggerFactory;
import com.cl.msipen03.exceptions.InternalMSIPEN03Exception;
import lombok.AllArgsConstructor;


import org.springframework.core.io.Resource;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/msipen03")
public class RemittanceOperationsStatusController {

    private static final Logger LOGGER = CommonLoggerFactory.getLogger(RemittanceOperationsStatusController.class).logger();

    private final RemittanceOperationsFacade remittanceOperationsFacade;


    private RemittanceExcelFileFacade remittanceBuildExcelFacade;


    @GetMapping(value = "remittance/operations/{remittanceOrderId}")
    public ResponseEntity<RemittanceOrderPaginationResponse> getOperationList(
            @Parameter(description = "Remittance order identifier)", example = "REM000000101065")
            @PathVariable String remittanceOrderId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) throws InternalMSIPEN03Exception {

        LOGGER.info("Remittance Operations Status Controller:{}", remittanceOrderId);

        RemittanceOrderPaginationResponse response = remittanceOperationsFacade
                .getOperationListResponse(remittanceOrderId, page, size);


        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping(value = "remittance/operations/{remittanceOrderId}/export")
    public ResponseEntity<Resource> exportOperationsAsExcel(
            @Parameter(description = "Remittance order identifier", example = "REM000000101065")
            @PathVariable String remittanceOrderId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10000") int size) {

        LOGGER.info("Export request received for remittanceOrderId: {}", remittanceOrderId);
        return remittanceBuildExcelFacade.generateExcelFile(remittanceOrderId);
    }
}


