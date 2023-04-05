package com.facilio.multiImport.importFileWriter;

@FunctionalInterface
public interface RowFunction {
    void apply(Integer rowNumber) throws Exception;
}
