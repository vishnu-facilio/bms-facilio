package com.facilio.bmsconsole.exceptions.importExceptions;

public class ImportTimeColumnParseException extends Exception{
	
	private static final long serialVersionUID = 1L;
	private int row_no;
	private String columnName;
	private Exception e;
	public ImportTimeColumnParseException(int row_number, String columnName,Exception e){
		super(e);
		this.e = e;
		this.row_no = row_number;
		this.columnName = columnName;
	}
	
	public String getClientMessage() {
		String exceptionMessage="Time column " + "(" + columnName + ")" + " date format mismatch at row: " + row_no;
		return exceptionMessage;
	}
}
