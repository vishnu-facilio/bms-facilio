package com.facilio.bmsconsole.actions;

import java.util.HashMap;
import java.util.Map;

import com.facilio.modules.FieldType;
import com.opensymphony.xwork2.ActionSupport;

public class NewCFAction extends ActionSupport {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Override
	public String execute() throws Exception {
		// TODO Auto-generated method stub
		
		dataTypeList = new HashMap<>();
		for(FieldType type : FieldType.values()) {
			dataTypeList.put(type.getTypeAsInt(), type.getTypeAsString());
		}
		
		return SUCCESS;
	}
	
	private Map<Integer, String> dataTypeList;
	public Map<Integer, String> getDataTypeList() {
		return dataTypeList;
	}
	public void setDataTypeList(Map<Integer, String> dataTypeList) {
		this.dataTypeList = dataTypeList;
	}
}
