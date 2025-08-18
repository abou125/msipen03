package com.cl.msipen03.mapper;

import com.cl.msipen03.entities.RemittanceOperation;
import com.cl.msipen03.utils.Utils;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RemittanceOperationsMapper implements RowMapper<RemittanceOperation> {

    @Override
    public RemittanceOperation mapRow(ResultSet rs, int rowNum) throws SQLException {

        return new RemittanceOperation(
                rs.getString("libnombnf"),
                rs.getString("iban_bnf"),
                rs.getString("codbicisobnf"),
                rs.getBigDecimal("mntvireur"),
                rs.getString("coddevitn"),
                rs.getString("libmotpmt"),
                rs.getString("librefopr"),
                rs.getString("id_refalttrn"),
                rs.getString("id_oprvir_cl"),
                Utils.statusMapping(rs, "codetagrptrn_psr2"),
                rs.getString("rej_code"),
                rs.getString("rej_long_desc"),
                rs.getLong("total_count")
        );
    }
}