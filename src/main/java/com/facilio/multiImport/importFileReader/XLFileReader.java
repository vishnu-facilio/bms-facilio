package com.facilio.multiImport.importFileReader;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class XLFileReader implements  AbstractImportFileReader{

    protected InputStream inputStream;
    protected File file;
    protected Workbook workbook;
    protected FormulaEvaluator evaluator;
    protected List<AbstractImportSheetReader>  sheetReaders;

    public XLFileReader(InputStream inputStream) throws Exception {
        Objects.requireNonNull(inputStream);
        this.inputStream = inputStream;
        this.workbook = WorkbookFactory.create(inputStream);
        this.evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        this.sheetReaders = initSheetReaders();
    }
    public XLFileReader(File file) throws Exception {
        Objects.requireNonNull(file);
        this.file = file;
        this.workbook = WorkbookFactory.create(file);
        this.evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        this.sheetReaders = initSheetReaders();

    }
    private List<AbstractImportSheetReader> initSheetReaders() throws Exception {
        List<AbstractImportSheetReader>  sheetReaders = new ArrayList<>();

        for(int i=0;i<getNumberOfSheets();i++){
            AbstractImportSheetReader sheetReader = new XLSheetReader(this, workbook.getSheetAt(i));
            sheetReaders.add(sheetReader);
        }
        return sheetReaders;
    }

    @Override
    public AbstractImportSheetReader getSheetReaderAt(int sheetIndex) {
        return sheetReaders.get(sheetIndex);
    }

    @Override
    public int getNumberOfSheets() {
        return workbook.getNumberOfSheets();
    }

    @Override
    public String getSupportedFileType() {
        return "XLS,XLSX";
    }

    @Override
    public void close() throws IOException {
        if(inputStream!=null){
            inputStream.close();
        }
        if(workbook!=null){
            workbook.close();
        }
    }
}
