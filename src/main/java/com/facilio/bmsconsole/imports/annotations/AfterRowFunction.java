package com.facilio.bmsconsole.imports.annotations;

import org.apache.commons.chain.Context;

import java.util.Map;

@FunctionalInterface
public interface AfterRowFunction {
    Object apply(Integer rowNumber, Map<String, Object> rowValue, Map<String, Object> prop, Context context);
}
