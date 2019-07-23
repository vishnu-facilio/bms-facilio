package com.facilio.billing.context;

import java.io.File;

import org.apache.poi.ss.usermodel.Workbook;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.templates.Template;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class ExcelTemplate extends Template{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Override
	public JSONObject getOriginalTemplate() {
		
		return null;
	}
	
	
	private long excelFileId = -1;
	public long getExcelFileId() {
		return excelFileId;
	}
	public void setExcelFileId(long excelFileId) {
		this.excelFileId = excelFileId;
	}

	private File excelFile;
	public File getExcelFile() {
		return excelFile;
	}
	public void setExcelFile(File excelFile) {
		this.excelFile = excelFile;
	}
	private Workbook workbook;
	public Workbook getWorkbook() {
		return workbook;
	}
	public void setWorkbook(Workbook workbook) {
		this.workbook = workbook;
	}
	
	@Override
	@JsonInclude(Include.ALWAYS)
	public int getType() {
		return Type.EXCEL.getIntVal();
	}
	@Override
	@JsonInclude(Include.ALWAYS)
	public Type getTypeEnum() {
		return Type.EXCEL;
	}

}
