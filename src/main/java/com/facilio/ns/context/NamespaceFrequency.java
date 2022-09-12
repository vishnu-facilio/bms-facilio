package com.facilio.ns.context;

import com.facilio.modules.FacilioIntEnum;
import lombok.Getter;
import org.apache.commons.collections.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public enum NamespaceFrequency implements FacilioIntEnum {
    ONE_MIN(60000l),  //1
    TWO_MINS(120000l),  //2
    THREE_MINS(180000l),  //3
    FOUR_MINS(240000l),  //4
    FIVE_MINS(300000l),  //5
    TEN_MINS(600000l),  //6
    FIFTEEN_MINS(900000l),  //7
    TWENTY_MINS(1200000l),  //8
    THIRTY_MINS(1800000l),  //9
    ONE_HOUR(3600000l),  //10
    TWO_HOUR(7200000l),  //11
    THREE_HOUR(10800000l),  //12
    FOUR_HOUR(14400000l),  //13
    EIGHT_HOUR(28800000l),  //14
    TWELVE_HOUR(43200000l),  //15
    ONE_DAY(86400000l),  //16
    WEEKLY(604800000l),  //17
    MONTHLY(2630000000l),  //18
    ANNUALLY(31560000000l);  //19

    private long ms;

    NamespaceFrequency(Long ms) {
        this.ms = ms;
    }

    public static NamespaceFrequency getEnumFromMs(long ms) {
        List<NamespaceFrequency> enumOfMs = Arrays.stream(values())
                .filter(c -> c.getMs() == ms)
                .collect(Collectors.toList());
        return CollectionUtils.isNotEmpty(enumOfMs) ? enumOfMs.get(0) : null;
    }

    public static NamespaceFrequency valueOf(int idx) {
        if (idx > 0 && idx <= values().length) {
            return values()[idx - 1];
        }
        return null;
    }
}
