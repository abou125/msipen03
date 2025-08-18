package com.cl.msipen03.entities;

import java.io.ByteArrayInputStream;

public record RemitanceExcelFile(ByteArrayInputStream contentOfExcelFile, Integer idReper) {
}