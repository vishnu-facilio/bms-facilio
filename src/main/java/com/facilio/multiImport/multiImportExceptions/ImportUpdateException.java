package com.facilio.multiImport.multiImportExceptions;

import com.facilio.fw.FacilioException;
import org.apache.commons.lang3.StringUtils;

public class ImportUpdateException extends FacilioException {
    private int row_no;
    private Exception e;
    public ImportUpdateException(int row_number, Exception e){
        super(e);
        this.e = e;
        this.row_no = row_number;

    }
    public String getClientMessage() {
        StringBuilder exceptionString=new StringBuilder();
        exceptionString.append("Update exception at row: " + this.row_no);
        if(this.e!=null && StringUtils.isNotEmpty(this.e.getMessage())){
            String reason=this.e.getMessage();
            exceptionString.append(" because of ");
            exceptionString.append(reason);
        }
        return exceptionString.toString();
    }
}
