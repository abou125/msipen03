package com.cl.msipen03.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
public record RemittanceOrderPaginationResponse(
        int size,
        int page,
        long total,

        @JsonProperty("COMPTE_RENDU_DE_TRAITEMENT_DE_LA_REMISE")
        RemittanceOrder request,

        @JsonProperty("DETAIL_DES_OPERATIONS_DE_LA_REMISE ")
        List<RemittanceOperation> list

) {
}