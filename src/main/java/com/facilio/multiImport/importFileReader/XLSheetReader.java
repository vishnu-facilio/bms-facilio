package com.facilio.multiImport.importFileReader;

import com.facilio.multiImport.multiImportExceptions.ImportParseException;
import com.facilio.multiImport.util.MultiImportApi;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.json.simple.JSONArray;

import java.util.*;

public class XLSheetReader implements AbstractImportSheetReader {
    private int rowNumber;
    private final Sheet sheet;
    private final Headers headers;
    private final Map<Integer, String> headerIndex;
    private final XLRecordIterator xlRecordIterator;
    private Row currentRow;
    private final int lastRowNum;
    private XLFileReader parent;

    public XLSheetReader(XLFileReader parent, Sheet sheet) throws Exception {
        this.parent = parent;
        this.sheet = sheet;
        this.headers = this.createHeaders();
        this.headerIndex = headers.headerMap;
        this.xlRecordIterator = new XLRecordIterator();
        this.lastRowNum = sheet.getLastRowNum();

    }
    @Override
    public int getLastRowNumber() {
        return this.sheet.getLastRowNum();
    }

    @Override
    public int getTotalRowCount() {
        int rowCount = 0;
        boolean heading=true;
        for (Row row : sheet) {
            if(heading){
                heading=false;
                continue;
            }
            boolean isRowEmpty = true;
            for (Cell cell : row) {
                if (cell.getCellTypeEnum() != CellType.BLANK) {
                    isRowEmpty = false;
                    break;
                }
            }

            if (!isRowEmpty) {
                rowCount++;
            }
        }
        return rowCount;
    }

    @Override
    public String getSheetName() {
        return sheet.getSheetName();
    }

    @Override
    public String getColumnHeadings() {
        JSONArray jsonArray = new JSONArray();
        for(Map.Entry<Integer,String> entry: headerIndex.entrySet()){
           String columnName = entry.getValue();
           if(columnName.equals("")){
               jsonArray.add(null);
           }
           else{
               jsonArray.add(columnName);
           }
        }
        return jsonArray.toJSONString();
    }

    @Override
    public Map<Integer, String> getHeaderIndex() {
        return headerIndex;
    }

    @Override
    public boolean isEmptyRow() {
        return currentRow == null || currentRow.getPhysicalNumberOfCells() <= 0 ? true : false;
    }

    @Override
    public HashMap<String, Object> getRowVal() throws ImportParseException {
        HashMap<String, Object> rowVal = new HashMap<>();

        if(isEmptyRow()){
            return rowVal;
        }

        Iterator<Cell> cellItr = currentRow.cellIterator();
        while (cellItr.hasNext()) {
            Cell cell = cellItr.next();

            String cellName = headerIndex.get(cell.getColumnIndex());
            if (StringUtils.isEmpty(cellName)) {
                continue;
            }

            Object val;
            try {
                CellValue cellValue = parent.evaluator.evaluate(cell);
                val = MultiImportApi.getValueFromCell(cell, cellValue);
            } catch (Exception e) {
                ImportParseException parseException = new ImportParseException(cellName, e);
                throw parseException;
            }
            rowVal.put(cellName, val);
        }
        return rowVal;
    }

    @Override
    public ImportIterator<Map<String, Object>> iterator() {
        return xlRecordIterator;
    }


    private static final class Headers {
        final Map<Integer, String> headerMap;
        final List<String> headerNames;

        Headers(Map<Integer, String> headerMap, List<String> headerNames) {
            this.headerMap = headerMap;
            this.headerNames = headerNames;
        }
    }

    private Headers createHeaders() throws Exception{
        Map<Integer, String> hdrMap = new HashMap<>();
        List<String> headerNames = null;

        Row columnHeadingRow = sheet.getRow(0);
        if(columnHeadingRow == null || columnHeadingRow.getPhysicalNumberOfCells()<=0){
            throw new IllegalArgumentException("First row of the "+sheet.getSheetName()+" sheet should contain column names");
        }
        int lastCellNum = columnHeadingRow.getLastCellNum();
        for (int i = 0; i < lastCellNum; i++) {
            Cell cell = columnHeadingRow.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (cell != null) {
                String cellValue = cell.getStringCellValue();
                hdrMap.put(i, cellValue);
            } else {
                hdrMap.put(i, "");
            }
        }

        if (headerNames == null) {
            headerNames = Collections.emptyList();
        } else {
            headerNames = Collections.unmodifiableList(headerNames);
        }
        return new Headers(hdrMap, headerNames);
    }

    class XLRecordIterator implements ImportIterator<Map<String,Object>>{
        private Map<String,Object> current;

        private Map<String,Object> getNextRecord() throws ImportParseException{
            return XLSheetReader.this.nextRecord();
        }

        public boolean hasNext() {
            return XLSheetReader.this.rowNumber <= lastRowNum;
        }

        public Map<String,Object> next() throws ImportParseException{
            this.current = this.getNextRecord();
            return current;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private Map<String,Object> nextRecord() throws ImportParseException {
        Row next = null;
        if (rowNumber >= 0 && rowNumber <= lastRowNum) {
            next = sheet.getRow(rowNumber);
            currentRow = next;
        }
        ++rowNumber;

        return this.getRowVal();
    }

}
