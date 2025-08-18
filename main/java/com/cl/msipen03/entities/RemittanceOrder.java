package com.cl.msipen03.entities;

import com.cl.msipen03.utils.FormatAmountTwoDecimals;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

public record RemittanceOrder(

        Integer id_reper,
        Timestamp date_et_heure_de_validation,
        String reference_banque_de_la_remise,
        String statut_de_la_remise,
        String libelle_du_compte_DO,
        String compte_DO,
        Integer nombre_virements,
        Date date_execution,
        @JsonSerialize(using = FormatAmountTwoDecimals.class)
        BigDecimal montant_total_de_la_remise

) {
}