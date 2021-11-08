package com.facilio.util;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.PatternSyntaxException;

import com.facilio.beans.ModuleBean;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

public class DisplayNameToLinkNameUtil {
	
	/**
     * For modules with entry in Modules table.
     */
	public static String getLinkName(String displayName,String moduleName,String nameFieldName) throws Exception {
		
		if(displayName != null) {
			
			String linkName = displayName.toLowerCase().replaceAll("[^a-zA-Z0-9]+","");
			
			int noToAdd = 1;
			while(true) {
				boolean res = checkIfLinkNameAlreadyExist(linkName,moduleName,nameFieldName);
				if(!res) {
					break;
				}
				if(noToAdd > 1) {
					linkName = linkName.replaceAll(noToAdd-1+"$", "");
				}
				linkName = linkName + noToAdd;
				noToAdd++;
			}
			return linkName;
		}
		return null;
		
	}
	
	/**
     * For modules with entry in Default Modules file.
     */
	public static String getLinkName(String displayName,FacilioModule module,FacilioField nameField) throws Exception {
		
		if(displayName != null) {
			
			String linkName = displayName.toLowerCase().replaceAll("[^a-zA-Z0-9]+","");
			
			int noToAdd = 1;
			while(true) {
				boolean res = checkIfLinkNameAlreadyExist(linkName,module,nameField);
				if(!res) {
					break;
				}
				if(noToAdd > 1) {
					linkName = linkName.replaceAll(noToAdd-1+"$", "");
				}
				linkName = linkName + noToAdd;
				noToAdd++;
			}
			return linkName;
		}
		return null;
		
	}
	
	private static boolean checkIfLinkNameAlreadyExist(String linkName, FacilioModule module, FacilioField nameField) throws Exception {
		// TODO Auto-generated method stub
		
		GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
				.table(module.getTableName())
				.select(Collections.singletonList(nameField))
				.andCondition(CriteriaAPI.getCondition(nameField, linkName, StringOperators.IS))
				;
		
		List<Map<String, Object>> props = select.get();
			
		if(props == null || props.isEmpty()) {
			return false;
		}
					
		return true;
	}

	private static boolean checkIfLinkNameAlreadyExist(String linkName, String moduleName, String nameFieldName) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule module = modBean.getModule(moduleName);
		
		FacilioField nameField = modBean.getField(nameFieldName, moduleName);
		
		SelectRecordsBuilder<ModuleBaseWithCustomFields> select = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
				.module(module)
				.select(Collections.singletonList(nameField))
				.beanClass(ModuleBaseWithCustomFields.class)
				.andCondition(CriteriaAPI.getCondition(nameField, linkName, StringOperators.IS))
				.fetchDeleted();
		
		
		List<ModuleBaseWithCustomFields> res = select.get();
		 
		if(res == null || res.isEmpty()) {
			return false;
		}
		
		return true;
		
	}
}
