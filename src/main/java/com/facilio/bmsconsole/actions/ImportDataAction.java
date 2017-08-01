package com.facilio.bmsconsole.actions;

import com.opensymphony.xwork2.ActionSupport;

public class ImportDataAction extends ActionSupport {
	
	// Upload xls , store the file and meta data(modulename/column heading/), generate importid
	public String upload()
	{
		return SUCCESS;
	}
	// importid is argument, display the excel column heading vs module field names,
	public String displayColumnFieldMapping()
	{
		return SUCCESS;
	}
	// column heading vs field name mapping + importid
	
	public String processImport()
	{
		// based on the option selected in displayColumnFieldMapping, load the data from excel file and get it imported
		return SUCCESS;
	}
	public String showformupload()
	{
		
		System.out.println("Displaying formupload");
		return SUCCESS;
	}

}
