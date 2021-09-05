package com.facilio.bundle.enums;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.bundle.context.*;
import com.facilio.bundle.interfaces.BundleComponentInterface;

import lombok.Getter;

@Getter
public enum BundleComponentsEnum {

	MODULE(1,"Module",ModuleBundleComponent.class,null,true),
	FIELD(2,"Field",FieldBundleComponent.class,BundleComponentsEnum.MODULE,false),
	FUNCTION_NAME_SPACE(3,"Function_NameSpace",FunctionBundleComponent.class,null,true),
	FUNCTION(4,"Function",FunctionBundleComponent.class,BundleComponentsEnum.FUNCTION_NAME_SPACE,true),
//	WORKFLOW_RULE(5,"Workflow_Rule",WorkflowRuleBundleComponent.class,BundleComponentsEnum.MODULE,true),
//	NOTIFICATION_RULE(6,"Notification_Rule",NotificationRuleBundleComponent.class,BundleComponentsEnum.MODULE,true),
	;
	
	int value;
	String name;
	BundleComponentsEnum parent;
	Class<? extends BundleComponentInterface> componentClass;
	Boolean asSeperateFolder;
	
	BundleComponentsEnum(int value, String name,Class<? extends BundleComponentInterface> componentClass,BundleComponentsEnum parent,Boolean asSeperateFolder) {
		this.value = value;
		this.name = name;
		this.parent = parent;
		this.componentClass = componentClass;
		this.asSeperateFolder = asSeperateFolder;
	}
	
	public BundleComponentInterface getBundleComponentClassInstance() throws Exception {
		
		BundleComponentInterface bundleComponentObject = componentClass.getDeclaredConstructor().newInstance();
		
		return bundleComponentObject;
	}
	
	private static final Map<Integer, BundleComponentsEnum> ALL_BUNDLE_COMPONENTS = Collections.unmodifiableMap(initAllBundleMap());
	
	private static final ArrayList<BundleComponentsEnum> PARENT_COMPONENT_LIST = initParentComponentList();
	
	private static final Map<BundleComponentsEnum, ArrayList<BundleComponentsEnum>> PARENT_CHILD_MAP = Collections.unmodifiableMap(initParentChildMap());
	
	private static Map<Integer, BundleComponentsEnum> initAllBundleMap() {
		Map<Integer, BundleComponentsEnum> typeMap = new HashMap<>();
		for(BundleComponentsEnum type : values()) {
			typeMap.put(type.getValue(), type);
		}
		return typeMap;
	}
	
	private static ArrayList<BundleComponentsEnum> initParentComponentList() {
		ArrayList<BundleComponentsEnum> parentComponents = new ArrayList<BundleComponentsEnum>();
		for(BundleComponentsEnum type : values()) {
			if(type.getParent() == null) {
				parentComponents.add(type);
			}
		}
		return parentComponents;
	}
	
	private static Map<BundleComponentsEnum, ArrayList<BundleComponentsEnum>> initParentChildMap() {
		
		Map<BundleComponentsEnum, ArrayList<BundleComponentsEnum>> returnMap = new HashMap<BundleComponentsEnum, ArrayList<BundleComponentsEnum>>();
		
		for(BundleComponentsEnum type : values()) {
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
}
