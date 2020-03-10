package com.facilio.bmsconsole.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.facilio.bmsconsole.util.BimAPI;
import com.facilio.constants.FacilioConstants;

public class ValidateBimFileSheetsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub		
		File fileUpload = (File) context.get(FacilioConstants.ContextNames.FILE);
		Workbook wb = WorkbookFactory.create(fileUpload);

		List<String> sheetNames = new ArrayList<String>();
		for (int i=0; i<wb.getNumberOfSheets(); i++) {
			if(BimAPI.getModuleNameBySheetName( wb.getSheetName(i) ) !=null){
			    sheetNames.add( wb.getSheetName(i) );
			}
		}
		context.put(FacilioConstants.ContextNames.SHEET_NAMES, sheetNames);
		
		return false;
	}

}
