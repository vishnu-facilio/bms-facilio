package com.facilio.multiImport.importFileReader;

import com.facilio.multiImport.context.ImportRowContext;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class CSVFileReader implements AbstractImportFileReader<ImportRowContext>{
    private InputStream inputStream;
    private File file;

    public CSVFileReader(InputStream inputStream){
        this.inputStream = inputStream;
    }
    public CSVFileReader(File file){
        this.file = file;
    }

    @Override
    public AbstractImportSheetReader getSheetReaderAt(int sheetIndex) {
        return null;
    }

    @Override
    public int getNumberOfSheets() {
        return 0;
    }

    @Override
    public String getSupportedFileType() {
        return null;
    }

    @Override
    public void close() throws IOException {

    }
}
