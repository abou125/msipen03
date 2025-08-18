package com.cl.msipen03.mapper;

import com.cl.msipen03.entities.RemittanceOrder;
import com.cl.msipen03.utils.Utils;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RemittanceOrderMapper implements RowMapper<RemittanceOrder> {

    @Override
    public RemittanceOrder mapRow(ResultSet rs, int rowNum) throws SQLException {


        return new RemittanceOrder(
                rs.getInt("numtecprs"),
                rs.getTimestamp("timcre_psr2"),
                rs.getString("id_ordpmt_cl"),
                Utils.statusMapping(rs, "codetagrpord_psr2"),
                rs.getString("libnomdbt"),
                rs.getString("iban_dbt"),
                rs.getInt("nbrtrnlot"),
                rs.getDate("timreaordvir"),
                rs.getBigDecimal("mntlot")
        );
    }
}