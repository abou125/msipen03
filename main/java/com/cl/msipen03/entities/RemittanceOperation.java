package com.cl.msipen03.entities;

import com.cl.msipen03.utils.Utils;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import com.cl.msipen03.utils.FormatAmountTwoDecimals;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Entity
@Table(name = "operation")
@Getter
@Setter
public class RemittanceOperation {
    @Id // Fake ID - Just so Hibernate doesn't throw an error
    @Column(name = "id_oprvir_cl")
    private String referenceBanque;

    @Column(name = "libnombnf")
    private String nomBeneficiaire;

    @Column(name = "iban_bnf")
    private String ibanBeneficiaire;

    @Column(name = "codbicisobnf")
    private String bicBeneficare;

    @Column(name = "mntvireur")
    @JsonSerialize(using = FormatAmountTwoDecimals.class)
    private BigDecimal montant;

    @Column(name = "coddevitn")
    private String devise;

    @Column(name = "libmotpmt")
    private String motifPaiement;

    @Column(name = "librefopr")
    private String referencePaiement;

    @Column(name = "id_refalttrn")
    private String referenceBoutEnBout;

    @Convert(converter = Utils.StatusAttributeConverter.class)
    @Column(name = "codetagrptrn_psr2")
    private String statut;

    @Column(name = "rej_code")
    private String codeRejetIso;

    @Column(name = "rej_long_desc")
    private String motifRejet;
}



