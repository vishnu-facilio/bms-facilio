package com.facilio.multiImport.multiImportExceptions;

import com.facilio.fw.FacilioException;
import org.apache.commons.lang3.StringUtils;

public class ImportParseException extends FacilioException {
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
		StringBuilder exceptionString=new StringBuilder();
		if(this.e instanceof NumberFormatException) {
			exceptionString.append("The value in row " + this.row_no +" under the column " + this.columnName
			+" could not be parsed as a number because it contains non-numeric characters.");
		}
		else if(e instanceof ImportFieldValueMissingException){
			exceptionString.append(((ImportFieldValueMissingException)e).getClientMessage());
		}
		else if (e instanceof ImportLookupModuleValueNotFoundException) {
			exceptionString.append(((ImportLookupModuleValueNotFoundException)e).getClientMessage());
		}
		else if (e instanceof IllegalArgumentException){
			exceptionString.append(e.getMessage());
		}
		else if (e instanceof ImportTimeColumnParseException){
			exceptionString.append(((ImportTimeColumnParseException)e).getClientMessage());
		}
		else {
			exceptionString.append("Exception at row: " + this.row_no + " under column: " + this.columnName);
			if(this.e!=null && StringUtils.isNotEmpty(this.e.getMessage())){
				String reason=this.e.getMessage();
				exceptionString.append(" because of ");
				exceptionString.append(reason);
			}
		}

		return exceptionString.toString();
	}
}
