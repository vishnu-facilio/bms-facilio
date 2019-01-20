package com.facilio.bmsconsole.context;

import java.util.HashMap;

import com.facilio.bmsconsole.modules.FieldUtil;

public class ImportRowContext {
	
	long parentId = -1;
	long ttime = -1;
	boolean isDuplicate = false;
	
	HashMap<String, Object> colVal = new HashMap<String, Object>();
	Integer error_code;

	public Integer getError_code() {
		return error_code;
	}
	public void setError_code(Integer error_code) {
		this.error_code = error_code;
	}

	int sheetNumber = -1;
	int rowNumber = -1;
	
	public boolean isDuplicate() {
		return isDuplicate;
	}
	public HashMap<String, Object> getColVal() {
		return colVal;
	}
	public void setColVal(HashMap<String, Object> colVal) {
		this.colVal = colVal;
	}
	public void setDuplicate(boolean isDuplicate) {
		this.isDuplicate = isDuplicate;
	}

	public long getParentId() {
		return parentId;
	}
	public int getSheetNumber() {
		return sheetNumber;
	}
	public void setSheetNumber(int sheetNumber) {
		this.sheetNumber = sheetNumber;
	}
	public int getRowNumber() {
		return rowNumber;
	}
	public void setRowNumber(int rowNumber) {
		this.rowNumber = rowNumber;
	}
	public void setParentId(long parentId) {
		this.parentId = parentId;
	}
	
	public long getTtime() {
		return ttime;
	}
	public void setTtime(long ttime) {
		this.ttime = ttime;
	}
	
	public boolean isDuplicate(ImportRowContext rowContext) {
		if(parentId == rowContext.getParentId()) {
			if(ttime == rowContext.getTtime()) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			// different parent id
			return false;
		}
	}
}
