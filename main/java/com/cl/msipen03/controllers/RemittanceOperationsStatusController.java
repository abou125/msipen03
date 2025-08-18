package com.cl.msipen03.controllers;

import com.cl.msipen03.entities.RemittanceOrderPaginationResponse;
import com.cl.msipen03.facades.RemittanceOperationsFacade;
import io.swagger.v3.oas.annotations.Parameter;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.cl.logs.commun.CommonLoggerFactory;
import com.cl.msipen03.exceptions.InternalMSIPEN03Exception;
import lombok.AllArgsConstructor;


@RestController
@AllArgsConstructor
@RequestMapping(value = "/msipen03")
public class RemittanceOperationsStatusController {

    private static final Logger LOGGER = CommonLoggerFactory.getLogger(RemittanceOperationsStatusController.class).logger();

    private final RemittanceOperationsFacade remittanceOperationsFacade;


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
}