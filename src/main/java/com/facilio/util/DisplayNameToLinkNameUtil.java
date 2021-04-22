package com.facilio.util;

import java.util.Collections;
import java.util.List;

import com.facilio.beans.ModuleBean;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

public class DisplayNameToLinkNameUtil {
	

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

	private static boolean checkIfLinkNameAlreadyExist(String linkName, String moduleName, String nameFieldName) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule module = modBean.getModule(moduleName);
		
		FacilioField nameField = modBean.getField(nameFieldName, moduleName);
		
		SelectRecordsBuilder<ModuleBaseWithCustomFields> select = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
				.module(module)
				.select(Collections.singletonList(nameField))
				.beanClass(ModuleBaseWithCustomFields.class)
				.andCondition(CriteriaAPI.getCondition(nameField, linkName, StringOperators.IS));
		
		
		List<ModuleBaseWithCustomFields> res = select.get();
		 
		if(res == null || res.isEmpty()) {
			return false;
		}
		
		return true;
		
	}
}
