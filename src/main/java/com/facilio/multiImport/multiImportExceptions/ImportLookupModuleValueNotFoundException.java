package com.facilio.multiImport.multiImportExceptions;

import com.facilio.fw.FacilioException;

public class ImportLookupModuleValueNotFoundException extends FacilioException {

    private String columnName;
    private String moduleName;

    public ImportLookupModuleValueNotFoundException(String moduleName,String columnName, Exception e){
        super(e);
        this.columnName = columnName;
        this.moduleName = moduleName;
    }

    public String getClientMessage() {
        return "The value in the lookup field "+columnName+" does not exist in "+moduleName+" module. Please provide a valid lookup value";
    }

}
