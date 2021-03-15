package com.facilio.bundle.enums;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

@Getter
public enum BundleComponentsEnum {

	MODULE(1,"Module"),
	FIELD(2,"Field",BundleComponentsEnum.MODULE),
	FUNCTION(3,"Function"),
	;
	
	int value;
	String name;
	BundleComponentsEnum parent;
	
	BundleComponentsEnum(int value, String name,BundleComponentsEnum parent) {
		this.value = value;
		this.name = name;
		this.parent = parent;
	}
	
	BundleComponentsEnum(int value, String name) {
		this.value = value;
		this.name = name;
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
}
