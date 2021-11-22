package com.facilio.bundle.enums;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bundle.context.FieldBundleComponent;
import com.facilio.bundle.context.FunctionBundleComponent;
import com.facilio.bundle.context.FunctionNameSpaceBundleComponent;
import com.facilio.bundle.context.ModuleBundleComponent;
import com.facilio.bundle.context.ScheduledActionBundleComponent;
import com.facilio.bundle.interfaces.BundleComponentInterface;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.workflowv2.util.UserFunctionAPI;

import lombok.Getter;

@Getter
public enum BundleComponentsEnum {

	MODULE(1,"Module",ModuleBundleComponent.class,null,
			ModuleFactory.getModuleModule(),FieldFactory.getModuleFields(),"moduleId",null,null,null,null,null),
	
	FIELD(2,"Field",FieldBundleComponent.class,BundleComponentsEnum.MODULE,
			ModuleFactory.getFieldsModule(),FieldFactory.getSelectFieldFields(),"fieldId",null,null,null,null,"deleted"),
	
	FUNCTION_NAME_SPACE(3,"Function_NameSpace",FunctionNameSpaceBundleComponent.class,null,
			ModuleFactory.getWorkflowNamespaceModule(),FieldFactory.getWorkflowNamespaceFields(),null,"sysModifiedTime","sysCreatedTime","linkName","name","deleted"),
	
	FUNCTION(4,"Function",FunctionBundleComponent.class,BundleComponentsEnum.FUNCTION_NAME_SPACE,
	ModuleFactory.getWorkflowUserFunctionModule(),UserFunctionAPI.getUserFunctionFields(),null,"sysModifiedTime","sysCreatedTime","linkName","name","deleted"),
	
	SCHEDULED_ACTIONS(5,"Scheduled_Actions",ScheduledActionBundleComponent.class,null,
			ModuleFactory.getScheduledWorkflowModule(),FieldFactory.getScheduledWorkflowFields(),null,null,null,"linkName","name","deleted"),
	
	;
	
	int value;
	String name;
	Class<? extends BundleComponentInterface> componentClass;
	BundleComponentsEnum parent;
	
	int level = -1;
	
	private void setLevel(int level) {
		this.level = level;
	}
	
	FacilioModule module;
	List<FacilioField> fields;
	
	String idFieldName="id";												
	String modifiedTimeFieldName="modifiedTime";
	String createdTimeFieldName="createdTime";
	String nameFieldName = "name";
	String displayNameFieldName = "displayName";
	String deletedFieldName;
	
	BundleComponentsEnum(int value, String name,Class<? extends BundleComponentInterface> componentClass,BundleComponentsEnum parent,FacilioModule module,List<FacilioField> fields,String idFieldName,String modifiedTimeFieldName,String createdTime,String nameFieldName,String displayNameFieldName,String deletedFieldName) {
		this.value = value;
		this.name = name;
		this.parent = parent;
		this.componentClass = componentClass;
		
		this.module = module;
		this.fields = fields;
		
		this.idFieldName = idFieldName == null ? this.idFieldName : idFieldName;
		this.modifiedTimeFieldName = modifiedTimeFieldName == null ? this.modifiedTimeFieldName : modifiedTimeFieldName;
		this.createdTimeFieldName = createdTime == null ? this.createdTimeFieldName : createdTime;
		this.nameFieldName = nameFieldName == null ? this.nameFieldName : nameFieldName;
		this.displayNameFieldName = displayNameFieldName == null ? this.displayNameFieldName : displayNameFieldName;
		
		this.deletedFieldName = deletedFieldName;
	}
	
	public BundleComponentInterface getBundleComponentClassInstance() throws Exception {
		
		BundleComponentInterface bundleComponentObject = componentClass.getDeclaredConstructor().newInstance();
		
		return bundleComponentObject;
	}
	
	private static final Map<Integer, BundleComponentsEnum> ALL_BUNDLE_COMPONENTS = Collections.unmodifiableMap(initAllBundleMap());
	
	private static final Map<String, BundleComponentsEnum> ALL_BUNDLE_COMPONENTS_BY_NAME = Collections.unmodifiableMap(initAllBundleMapByName());
	
	private static final ArrayList<BundleComponentsEnum> PARENT_COMPONENT_LIST = initParentComponentList();
	
	private static final Map<BundleComponentsEnum, ArrayList<BundleComponentsEnum>> PARENT_CHILD_MAP = Collections.unmodifiableMap(initParentChildMap());
	
	private static List<BundleComponentsEnum> getAllValuesWithLevelFilled() {
		
		List<BundleComponentsEnum> values = new ArrayList<BundleComponentsEnum>();
		for(BundleComponentsEnum type : values()) {
			
			BundleComponentsEnum temp = type;
			if(type.getLevel() < 0) {
				int level = 0;
				while(temp.getParent() != null) {
					temp = temp.getParent();
					level++;
				}
				type.setLevel(level);
			}
			values.add(type);
		}
		return values;
	}
	
	private static Map<Integer, BundleComponentsEnum> initAllBundleMap() {
		Map<Integer, BundleComponentsEnum> typeMap = new HashMap<>();
		for(BundleComponentsEnum type : getAllValuesWithLevelFilled()) {
			typeMap.put(type.getValue(), type);
		}
		return typeMap;
	}
	
	private static Map<String, BundleComponentsEnum> initAllBundleMapByName() {
		Map<String, BundleComponentsEnum> typeMap = new HashMap<>();
		for(BundleComponentsEnum type : getAllValuesWithLevelFilled()) {
			typeMap.put(type.getName(), type);
		}
		return typeMap;
	}
	
	private static ArrayList<BundleComponentsEnum> initParentComponentList() {
		ArrayList<BundleComponentsEnum> parentComponents = new ArrayList<BundleComponentsEnum>();
		for(BundleComponentsEnum type : getAllValuesWithLevelFilled()) {
			if(type.getParent() == null) {
				parentComponents.add(type);
			}
		}
		return parentComponents;
	}
	
	private static Map<BundleComponentsEnum, ArrayList<BundleComponentsEnum>> initParentChildMap() {
		
		Map<BundleComponentsEnum, ArrayList<BundleComponentsEnum>> returnMap = new HashMap<BundleComponentsEnum, ArrayList<BundleComponentsEnum>>();
		
		for(BundleComponentsEnum type : getAllValuesWithLevelFilled()) {
			if(type.getParent() != null) {
				ArrayList<BundleComponentsEnum> childList = returnMap.getOrDefault(type.getParent(), new ArrayList<BundleComponentsEnum>());
				childList.add(type);
				returnMap.put(type.getParent(), childList);
			}
		}
		
		return returnMap;
	}

	public static ArrayList<BundleComponentsEnum> getParentComponentList() {
		return PARENT_COMPONENT_LIST;
	}

	public static Map<BundleComponentsEnum, ArrayList<BundleComponentsEnum>> getParentChildMap() {
		return PARENT_CHILD_MAP;
	}

	public static Map<Integer, BundleComponentsEnum> getAllBundleComponents() {
		return ALL_BUNDLE_COMPONENTS;
	}
	
	public static Map<String, BundleComponentsEnum> getAllBundleComponentsByName() {
		return ALL_BUNDLE_COMPONENTS_BY_NAME;
	}
}
