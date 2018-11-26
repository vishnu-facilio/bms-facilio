package com.facilio.bmsconsole.exceptions.importExceptions;

public class ImportParseException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int row_no;
	private String columnName;
	private Exception e;
	public ImportParseException(int row_number, String columnName,Exception e){
		super(e);
		this.e = e;
		this.row_no = row_number;
		this.columnName = columnName;
	}
	
	public String getClientMessage() {
		if(this.e instanceof NumberFormatException) {
			String exceptionString = "Value cannot be parsed at row: " + this.row_no + "under column: " + this.columnName;
		}
		
		String exceptionString = "Exception at row: " + this.row_no + " under column: " + this.columnName;
		return exceptionString;
	}
}
