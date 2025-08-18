package com.cl.msipen03.service;

import com.cl.msipen03.entities.RemittanceOrderPaginationResponse;

import java.io.ByteArrayInputStream;

public interface RemittanceExcelFileService {
    ByteArrayInputStream generateExcelContent(RemittanceOrderPaginationResponse response);
}