package com.facilio.multiImport.importFileReader;

import com.facilio.multiImport.multiImportExceptions.ImportParseException;

import java.util.HashMap;
import java.util.Map;

public interface AbstractImportSheetReader {
    int getLastRowNumber();
    int getTotalRowCount();
    String getSheetName();
    ImportIterator iterator();
    boolean isEmptyRow() throws Exception;
    Map<Integer, String> getHeaderIndex();
    String getColumnHeadings() throws Exception;
    public HashMap<String, Object> getRowVal() throws ImportParseException;
}
