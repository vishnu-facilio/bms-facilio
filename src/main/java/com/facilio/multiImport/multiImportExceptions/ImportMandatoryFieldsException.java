package com.facilio.multiImport.multiImportExceptions;

import com.facilio.fw.FacilioException;

import java.util.ArrayList;

public class ImportMandatoryFieldsException extends FacilioException {
	private static final long serialVersionUID = 1L;
		
	Integer row_no = null;
	ArrayList<String> columns = new ArrayList<String>();
	
	public ImportMandatoryFieldsException(Integer row_no, ArrayList<String> columns, Exception e) {
		super(e);
		this.row_no = row_no;
		this.columns = columns;
	}
	
	public String getClientMessage() {
		
		if(this.row_no == null) {
			StringBuilder builder = new StringBuilder();
			builder.append("Import cannot be performed as mandatory ");
			if(columns.size() == 1) {
				builder.append(" field ");
			}
			else {
				builder.append(" fields ");
			}
			for(String column : this.columns) {
				
				if(this.columns.indexOf(column) == this.columns.size() - 1) {
					builder.append(" " + column);
				}
				else {
					builder.append(" " + column + ",");
				}
			}
			if(columns.size() == 1) {
				builder.append(" is not mapped.");
			}
			else {
				builder.append(" are not mapped.");
			}
			
			return builder.toString();
		}
		else {
			StringBuilder builder = new StringBuilder();
			builder.append("Mandatory value missing at row number " + this.row_no + " under column");
			for(String column : this.columns) {
				if(this.columns.indexOf(column) == this.columns.size() - 1) {
					builder.append(" " + column + ".");
				}
				else {
					builder.append(" " + column + ",");
				}
				
			}
			
			return builder.toString();
		}
		
	}
	
	
}
