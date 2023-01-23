package com.facilio.util;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.ValueGenerator;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.reflections.Reflections;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@Log4j
public class ValueGeneratorUtil {
    private static Map<String,Class<? extends ValueGenerator>> valueGeneratorsMap = new HashMap<>();
    public static void initialize() throws IOException {
        try {
            Reflections reflections = new Reflections("com.facilio.modules");
            Set<Class<? extends ValueGenerator>> classes = reflections.getSubTypesOf(ValueGenerator.class);
            if(CollectionUtils.isNotEmpty(classes)){
                for(Class<? extends ValueGenerator> vg : classes){
                    ValueGenerator obj = vg.newInstance();
                    valueGeneratorsMap.put(obj.getLinkName(),vg);
                }
            }
        }
        catch (Exception e) {
            LOGGER.info(e);
        }
    }
    public static Class<? extends ValueGenerator> getValueGeneratorClassForLinkName(String linkName) {
        Class<? extends ValueGenerator> valGen = valueGeneratorsMap.get(linkName);
        return valGen;
    }
    public static ValueGenerator getValueGeneratorObjectForLinkName(String linkName) throws InstantiationException, IllegalAccessException {
        Class<? extends ValueGenerator> valGen = valueGeneratorsMap.get(linkName);
        if(valGen != null) {
            ValueGenerator obj = valGen.newInstance();
            return obj;
        }
        return null;
    }
}