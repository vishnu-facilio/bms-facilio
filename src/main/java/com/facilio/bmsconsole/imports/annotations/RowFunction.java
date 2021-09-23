package com.facilio.bmsconsole.imports.annotations;

import org.apache.commons.chain.Context;

import java.util.Map;

@FunctionalInterface
public interface RowFunction {
    String apply(Integer rowNumber, Map rowValue, Context context);
}
