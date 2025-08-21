package com.cl.msipen03.entities;

import com.cl.msipen03.utils.FormatAmountTwoDecimals;
import com.cl.msipen03.utils.Utils;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "ordre")
@Getter
@Setter
public class RemittanceOrder {
    @Id // Fake ID - Just so Hibernate doesn't throw an error
    @Column(name = "id_ordpmt_cl")
    private String referenceBanqueDeLaRemise;

    @Column(name = "numtecprs")
    private Integer idReper;

    @Column(name = "timcre_psr2")
    private Timestamp dateEtHeureDeValidation;

    @Convert(converter = Utils.StatusAttributeConverter.class)
    @Column(name = "codetagrpord_psr2")
    private String statutDeLaRemise;

    @Column(name = "libnomdbt")
    private String libelleDuCompteDO;

    @Column(name = "iban_dbt")
    private String compteDO;

    @Column(name = "nbrtrnlot")
    private Integer nombreVirements;

    @Column(name = "timreaordvir")
    private Date dateExecution;

    @Column(name = "mntlot")
    @JsonSerialize(using = FormatAmountTwoDecimals.class)
    private BigDecimal montantTotalDeLaRemise;
}