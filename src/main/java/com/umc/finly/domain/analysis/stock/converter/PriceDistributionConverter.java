package com.umc.finly.domain.analysis.stock.converter;

import com.umc.finly.domain.analysis.stock.dto.response.PriceDistributionResDTO;
import com.umc.finly.domain.analysis.stock.enums.PriceRangeType;

import java.util.ArrayList;
import java.util.List;

public class PriceDistributionConverter {
    private PriceDistributionConverter() {}

    public static PriceDistributionResDTO toDto(
            int averageBuyPrice,
            int rangePercent,
            int lowerBound,
            int upperBound,
            int low,
            int mid,
            int high,
            int lowRatio,
            int midRatio,
            int highRatio
    ) {
        int finalMaxRatio = Math.max(lowRatio, Math.max(midRatio, highRatio));

        List<PriceDistributionResDTO.PriceDistributionItem> items = new ArrayList<>();

        items.add(new PriceDistributionResDTO.PriceDistributionItem(
                PriceRangeType.LOW,
                String.format("%,d원 미만", lowerBound),
                low,
                lowRatio,
                lowRatio == finalMaxRatio ? true : null
        ));

        items.add(new PriceDistributionResDTO.PriceDistributionItem(
                PriceRangeType.MID,
                String.format("%,d원 ~ %,d원", lowerBound, upperBound),
                mid,
                midRatio,
                midRatio == finalMaxRatio ? true : null
        ));

        items.add(new PriceDistributionResDTO.PriceDistributionItem(
                PriceRangeType.HIGH,
                String.format("%,d원 이상", upperBound),
                high,
                highRatio,
                highRatio == finalMaxRatio ? true : null
        ));

        return new PriceDistributionResDTO(
                averageBuyPrice,
                new PriceDistributionResDTO.RangePolicy("AVERAGE_BUY_PRICE", rangePercent),
                items
        );
    }
}
