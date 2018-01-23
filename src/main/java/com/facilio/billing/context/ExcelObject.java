package com.facilio.billing.context;

public class ExcelObject {
	
	String fileName;
	String sheetName;
	int sheetId = -1;
	int rowId;
	int cellId;
	int cellType;
	Object cellValue;
	String cellStr;
	
	public String getDirectCellStr()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("S");
		if(sheetId != -1)
		{
			sb.append(getSheetId());
		}
		else
		{
			sb.append(getSheetName());
		}
		sb.append("R");
		sb.append(getRowId());
		sb.append("C");
		sb.append(getCellId());
		String cellStr = sb.toString();
		System.out.println("Excel Path :"+cellStr);
		return cellStr;	
	}
	public String getCellStr() {
		return cellStr;
	}
	public void setCellStr(String cellStr) {
		this.cellStr = cellStr;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getSheetName() {
		return sheetName;
	}
	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}
	public int getSheetId() {
		return sheetId;
	}
	public void setSheetId(int sheetId) {
		this.sheetId = sheetId;
	}
	public int getRowId() {
		return rowId;
	}
	public void setRowId(int rowId) {
		this.rowId = rowId;
	}
	public int getCellId() {
		return cellId;
	}
	public void setCellId(int cellId) {
		this.cellId = cellId;
	}
	public int getCellType() {
		return cellType;
	}
	public void setCellType(int cellType) {
		this.cellType = cellType;
	}
	public Object getCellValue() {
		return cellValue;
	}
	public void setCellValue(Object cellValue) {
		this.cellValue = cellValue;
	}

}