package com.cl.msipen03.utils;

import com.cl.msipen03.entities.RemittanceStatusEnum;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.apache.poi.ss.usermodel.*;

import java.time.format.DateTimeFormatter;
import java.sql.Timestamp;
import java.util.Optional;
import java.sql.Date;

public class Utils {

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    @Converter
    public static class StatusAttributeConverter implements AttributeConverter<String, String> {
        @Override
        public String convertToDatabaseColumn(String attribute) {
            return attribute;
        }

        @Override
        public String convertToEntityAttribute(String dbData) {
            return RemittanceStatusEnum.getStatusLabel(dbData);
        }
    }

    public static String checkTimeStampToAvoidNPE(Timestamp timestamp, DateTimeFormatter formatter) {
        return Optional.ofNullable(timestamp)
                .map(ts -> ts.toLocalDateTime().format(formatter))
                .orElse("");
    }

    public static String checkDateToAvoidNPE(Date date, DateTimeFormatter formatter) {
        return Optional.ofNullable(date)
                .map(d -> d.toLocalDate().format(formatter))
                .orElse("");
    }

    public static String checkStringValueToAvoidNPE(Object value) {
        return Optional.ofNullable(value)
                .map(Object::toString)
                .orElse("");
    }

}