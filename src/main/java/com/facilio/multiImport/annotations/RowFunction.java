package com.facilio.multiImport.annotations;

import com.facilio.multiImport.context.ImportRowContext;
import org.apache.commons.chain.Context;

import java.util.Map;

@FunctionalInterface
public interface RowFunction {
    Object apply(ImportRowContext rowContext, Map<String, Object> rowValue, Map<String, Object> prop, Context context) throws Exception;
}