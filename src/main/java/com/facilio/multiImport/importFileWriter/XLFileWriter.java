package com.facilio.multiImport.importFileWriter;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XLFileWriter implements AbstractFileWriter{
    protected Workbook workbook;
    private List<AbstractSheetWriter> sheetWriters;
    public XLFileWriter() throws Exception {
        this.workbook = new XSSFWorkbook();
        this.sheetWriters = new ArrayList<>();
    }

    @Override
    public AbstractSheetWriter createSheet(String sheetName) {
        Sheet sheet = workbook.createSheet(sheetName);
        AbstractSheetWriter sheetWriter = new XLSheetWriter(sheet,this);
        sheetWriters.add(sheetWriter);
        return sheetWriter;
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
    public void write(String filePath) throws Exception {
        try(FileOutputStream fileOut = new FileOutputStream(filePath);) {
            workbook.write(fileOut);
        }
    }

    @Override
    public void close() throws IOException {
        if(workbook!=null){
            workbook.close();
        }
    }

}


