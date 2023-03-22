package com.facilio.multiImport.importFileWriter;

import java.io.IOException;

public interface AbstractFileWriter {
    AbstractSheetWriter createSheet(String sheetName);
    int getNumberOfSheets();
    String getSupportedFileType();
    void write(String filePath) throws Exception;
    void close() throws IOException;
}
