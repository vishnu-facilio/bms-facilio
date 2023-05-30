package com.facilio.multiImport.multiImportExceptions;

import com.facilio.fw.FacilioException;

public class ImportTimeColumnParseException extends FacilioException {
	
	private static final long serialVersionUID = 1L;
	private String columnName;
	private Exception e;
	public ImportTimeColumnParseException(String columnName,Exception e){
		super(e);
		this.e = e;
		this.columnName = columnName;
	}
	
	public String getClientMessage() {
		String exceptionMessage="Date format mismatch under column "+columnName+". Please ensure the date value is in the correct format";
		return exceptionMessage;
	}
}
