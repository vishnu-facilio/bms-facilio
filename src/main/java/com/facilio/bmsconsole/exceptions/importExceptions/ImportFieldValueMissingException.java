package com.facilio.bmsconsole.exceptions.importExceptions;

public class ImportFieldValueMissingException extends Exception{
	private static final long serialVersionUID = 1L;
	
	private int row_no;
	private String columnName;
	
	public ImportFieldValueMissingException(int row_no, String columnName, Exception e){
		super(e);
		this.row_no = row_no;
		this.columnName = columnName;
	}
	
	public String getClientMessage() {
		return "Value missing at row " + this.row_no + " under column " + this.columnName + ".";
	}

}
