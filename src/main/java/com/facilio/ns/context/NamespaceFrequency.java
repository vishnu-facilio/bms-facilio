package com.facilio.ns.context;

import com.facilio.modules.FacilioIntEnum;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public enum NamespaceFrequency implements FacilioIntEnum {
    ONE_MIN(60000L, 1, "1 Min"),  //1
    TWO_MINS(120000L, 2, "2 Mins"),  //2
    THREE_MINS(180000L,3, "3 Mins"),  //3
    FOUR_MINS(240000L,4, "4 Mins"),  //4
    FIVE_MINS(300000L,5, "5 Mins"),  //5
    TEN_MINS(600000L,10, "10 Mins"),  //6
    FIFTEEN_MINS(900000L,15, "15 Mins"),  //7
    TWENTY_MINS(1200000L,20, "20 Mins"),  //8
    THIRTY_MINS(1800000L,30, "30 Mins"),  //9
    ONE_HOUR(3600000L,1, "1 Hour"),  //10
    TWO_HOUR(7200000L,2, "2 Hours"),  //11
    THREE_HOUR(10800000L,3, "3 Hours"),  //12
    FOUR_HOUR(14400000L,4, "4 Hours"),  //13
    EIGHT_HOUR(28800000L,8, "8 Hours"),  //14
    TWELVE_HOUR(43200000L,12, "12 Hours"),  //15
    ONE_DAY(86400000L,0, "1 Day"),  //16
    WEEKLY(604800000L,0, "1 Week"),  //17
    MONTHLY(2630000000L,0, "1 Month"),  //18
    QUARTERLY(7889400000L,0, "Quarterly"), // 19
    HALF_YEARLY(15778800000L,0, "Half Yearly"), //20
    ANNUALLY(31560000000L,0, "Annually");  //21

    private final Long ms;
    private final Integer divisor;
    private final String name;
    private static final Map<Long, NamespaceFrequency> msVsEnumMap = Arrays.stream(NamespaceFrequency.values()).collect(Collectors.toMap(NamespaceFrequency::getMs, Function.identity()));

    NamespaceFrequency(Long ms, Integer divisor, String name) {
        this.ms = ms;
        this.divisor = divisor;
        this.name = name;
    }

    public static NamespaceFrequency getEnumFromMs(long ms) {
        return msVsEnumMap.get(ms);
    }

    public static NamespaceFrequency valueOf(int idx) {
        if (idx > 0 && idx <= values().length) {
            return values()[idx - 1];
        }
        return null;
    }

    @Override
    public String getValue() {
        return this.name;
    }

}
