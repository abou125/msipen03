package com.cl.msipen03.entities;

import com.cl.msipen03.utils.FormatAmountTwoDecimals;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.math.BigDecimal;

public record RemittanceOperation(

        String nom_beneficiaire,
        String iban_beneficiaire,
        String bic_beneficiare,
        @JsonSerialize(using = FormatAmountTwoDecimals.class)
        BigDecimal montant,
        String devise,
        String motif_paiement,
        String reference_paiement,
        String reference_Bout_en_bout,
        String reference_Banque,
        String statut,
        String code_rejet_iso,
        String motif_rejet,

        @JsonIgnore
        long totalCount

) {
}