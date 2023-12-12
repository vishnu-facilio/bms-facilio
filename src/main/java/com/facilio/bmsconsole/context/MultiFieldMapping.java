package com.facilio.bmsconsole.context;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiFieldMapping {

    Map<String, List<String>> sourceAndTargetMap = new HashMap<>();

    public void add(String sourceValue, String... targetValues) {
        sourceAndTargetMap.put(sourceValue, Arrays.asList(targetValues));
    }

    public Map<String, List<String>> getSourceAndTargetMap() {
        return sourceAndTargetMap;
    }
}
