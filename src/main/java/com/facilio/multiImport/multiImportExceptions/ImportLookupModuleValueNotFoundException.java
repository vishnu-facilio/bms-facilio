package com.facilio.multiImport.multiImportExceptions;

import com.facilio.fw.FacilioException;

public class ImportLookupModuleValueNotFoundException extends FacilioException {

    private int row_no;
    private String columnName;
    private String moduleName;

    public ImportLookupModuleValueNotFoundException(String moduleName, int row_no, String columnName, Exception e){
        super(e);
        this.row_no = row_no;
        this.columnName = columnName;
        this.moduleName = moduleName;
    }

    public String getClientMessage() {
        return moduleName + " under column " + columnName + " in row " + row_no + " not found. Kindly Add Record before Importing.";
    }

}
