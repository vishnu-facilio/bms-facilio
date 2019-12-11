package com.facilio.bmsconsole.exceptions.importExceptions;

public class ImportModuleMissingException extends Exception{

    private int row_no;
    private String columnName;
    private String moduleName;

    public ImportModuleMissingException(String moduleName,int row_no, String columnName, Exception e){
        super(e);
        this.row_no = row_no;
        this.columnName = columnName;
        this.moduleName = moduleName;
    }

    public String getClientMessage() {
        return moduleName + " under column " + columnName + " in row " + row_no + " not found.";
    }

}
