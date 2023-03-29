package com.facilio.multiImport.importFileReader;

import java.io.IOException;

public interface AbstractImportFileReader<T> {
    AbstractImportSheetReader getSheetReaderAt(int sheetIndex);
    int getNumberOfSheets();
    String getSupportedFileType();
    void close() throws IOException;
}
