package com.facilio.bmsconsoleV3.util;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class HashMapValueComparator implements Comparator<LinkedHashMap<String, HashMap>> {
    String alias;
    boolean isLookupField;
    Long sortOrder;


    public HashMapValueComparator(String alias, boolean isLookupField, Long sortOrder)
    {
        this.alias = alias;
        this.isLookupField = isLookupField;
        this.sortOrder = sortOrder;
    }

    @Override
    public int compare(LinkedHashMap<String, HashMap> map1, LinkedHashMap<String, HashMap> map2) {

        HashMap  alias_object1 = map1.get(alias);
        HashMap  alias_object2 = map2.get(alias);

        Object value1, value2;
        if(isLookupField )
        {
            value1 = alias_object1.get("formattedValue");
            value2 = alias_object2.get("formattedValue");
        }else{
            value1 = alias_object1.get("value");
            value2 = alias_object2.get("value");
        }
        Comparator<String> ascComparator = Comparator.naturalOrder();
        Comparator<String> descComparator = Comparator.naturalOrder();
        if (value1 == null && value2 == null) {
            return 0;
        } else if (value1 == null) {
            return -1; // obj1 is considered less than obj2
        } else if (value2 == null) {
            return 1; // obj1 is considered greater than obj2
        } else {
            try {
                Double number1 = Double.parseDouble(value1.toString());
                Double number2 = Double.parseDouble(value2.toString());
                Comparator<Double> doubleAscComparator = Comparator.naturalOrder();
                Comparator<Double> doubleDescComparator = Comparator.naturalOrder();
                return sortOrder == 3 ? doubleAscComparator.compare(number1, number2) : doubleDescComparator.compare(number2, number1);
            } catch (Exception e) {
                String str1 = value1.toString();
                String str2 = value2.toString();
                return sortOrder == 3 ? ascComparator.compare(str1, str2) : descComparator.compare(str2, str1);
            }
        }
    }
}
