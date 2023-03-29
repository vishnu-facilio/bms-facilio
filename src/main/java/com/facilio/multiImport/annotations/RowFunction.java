package com.facilio.multiImport.annotations;

import org.apache.commons.chain.Context;

import java.util.Map;

@FunctionalInterface
public interface RowFunction {
    Object apply(Long rowNumber, Map<String, Object> rowValue, Map<String, Object> prop, Context context) throws Exception;
}