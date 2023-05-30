package com.facilio.multiImport.multiImportExceptions;

import com.facilio.fw.FacilioException;

public class ImportFieldValueMissingException extends FacilioException {
	private static final long serialVersionUID = 1L;

	private String columnName;
	
	public ImportFieldValueMissingException(String columnName, Exception e){
		super(e);
		this.columnName = columnName;
	}
	
	public String getClientMessage() {
		return "Value is missing under column " + this.columnName;
	}

}
