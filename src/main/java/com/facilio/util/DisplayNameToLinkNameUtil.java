package com.facilio.util;


import java.util.regex.PatternSyntaxException;

import com.facilio.beans.ModuleBean;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

import org.apache.commons.collections.CollectionUtils;
import java.util.*;
import java.util.stream.Collectors;

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
	public static String getGenericLinkName(FacilioModule module, FacilioField nameField, Criteria criteria, String nameToChange, Boolean isSystem, List<String> existingNames ) throws Exception {
		String linkName;
		String name;
		if(nameToChange != null) {
			if(isSystem) {
				name = nameToChange.toLowerCase().replaceAll("[^a-zA-Z0-9_]+", "");
				linkName = name;
			}
			else{
				name = truncateAndNormalize(nameToChange);
				linkName = getLinkNameIfAlreadyExist(name, module, nameField, criteria, false , existingNames);
			}
			return linkName;
		}
		return null;
	}
	public static String truncateAndNormalize(String input) {
		String normalized = input.toLowerCase().replaceAll("[^a-zA-Z0-9]+", "");
		if (normalized.length() <= 45) {
			return normalized;
		} else {
			return normalized.substring(0, 45);
		}
	}
	public static String getLinkNameIfAlreadyExist(String name, FacilioModule module, FacilioField nameField, Criteria criteria, Boolean isSystem, List<String> existingNames) throws Exception {
		Set<String> linkNames;
		if(existingNames ==null || existingNames.isEmpty()) {
			GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
					.table(module.getTableName())
					.select(Collections.singletonList(nameField));
			Criteria nameCriteria = new Criteria();
			nameCriteria.addAndCondition(CriteriaAPI.getCondition(nameField, name, StringOperators.IS));
			nameCriteria.addOrCondition(CriteriaAPI.getCondition(nameField, name, StringOperators.STARTS_WITH));
			select.andCriteria(nameCriteria);
			if (criteria != null && !criteria.isEmpty()) {
				select.andCriteria(criteria);
			}
			List<Map<String, Object>> props = select.get();
			String fieldName = nameField.getName();
			linkNames = props.stream().map(prop -> ((String) prop.get(fieldName)).replaceAll("__C$", "")).collect(Collectors.toSet());
		}else {
			linkNames = existingNames.stream().map(existingName -> existingName.replaceAll("__C$", "")).collect(Collectors.toSet());
		}
		if (CollectionUtils.isNotEmpty(linkNames)) {
			name = computeUnique(name, linkNames);
		}
		if (!isSystem) {
			name += "__C";
		}

		return name;
	}
	private static String computeUnique(String name, Set<String> linkNames) {
		String baseName;
		int count = 1;
		while (linkNames.contains(name)) {
			if (name.endsWith("_" + count)) {
				int lastIndex = name.lastIndexOf('_');
				baseName = name.substring(0, lastIndex);
				count = Integer.parseInt(name.substring(lastIndex + 1)) + 1;
			} else {
				baseName = name;
				count = 1;
			}
			name = baseName + "_" + count;
		}
		return name;
	}

}
