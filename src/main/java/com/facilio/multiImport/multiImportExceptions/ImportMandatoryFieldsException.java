package com.facilio.multiImport.multiImportExceptions;

import com.facilio.fw.FacilioException;

import java.util.ArrayList;

public class ImportMandatoryFieldsException extends FacilioException {
    private static final long serialVersionUID = 1L;
    ArrayList<String> columns = new ArrayList<String>();

    public ImportMandatoryFieldsException(ArrayList<String> columns, Exception e) {
        super(e);
        this.columns = columns;
    }

    public String getClientMessage() {
        StringBuilder builder = new StringBuilder();

        if (columns.size() == 1) {
            builder.append("Mandatory field value is missing under column ");
        } else {
            builder.append("Mandatory field values are missing under columns ");
        }

        for (String column : this.columns) {
            if (this.columns.indexOf(column) == this.columns.size() - 1) {
                builder.append(" " + column + ".");
            } else {
                builder.append(" " + column + ",");
            }

        }

        builder.append("Please provide a value for the mandatory field");

        return builder.toString();
    }
}
