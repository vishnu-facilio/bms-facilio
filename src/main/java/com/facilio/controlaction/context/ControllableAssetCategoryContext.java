package com.facilio.controlaction.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.controlaction.context.ControllablePointContext.ControllablePoints;
import com.fasterxml.jackson.annotation.JsonFormat;

public class ControllableAssetCategoryContext extends AssetCategoryContext {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	ControllableCategory controlType;
	
	public int getControlType() {
		if(controlType != null) {
			return controlType.getCategoryId();
		}
		return -1;
	}

	public void setControlType(int controlType) {
		this.controlType = ControllableCategory.getControllableCategoryMap().get(controlType);
	}
	
	LinkedHashSet<ControllablePoints> availablePoints;

	public LinkedHashSet<ControllablePoints> getAvailablePoints() {
		return availablePoints;
	}

	public void setAvailablePoints(LinkedHashSet<ControllablePoints> availablePoints) {
		this.availablePoints = availablePoints;
	}
	
	public void addAvailablePoints(ControllablePoints availablePoint) {
		this.availablePoints = this.availablePoints == null ? new LinkedHashSet<>() : availablePoints;
		this.availablePoints.add(availablePoint);
	}

	@JsonFormat(shape = JsonFormat.Shape.OBJECT)
	public enum ControllableCategory {
		
		LIGHT(1,"Light"),
		AC(2,"Air Conditioner"),
		FAN(3,"Fan"),
		CURTAIN(4,"Curtain"),
		PROJECTOR(5,"Projector"),
		;
		
		int categoryId;
		String name;
		List<ControllablePoints> points;
		
		public String getName() {
			return name;
		}
		
		public int getCategoryId() {
			return categoryId;
		}
		
		public List<ControllablePoints> getPoints() {
			return points;
		}
		
		ControllableCategory(int categoryId, String name) {
			this.categoryId = categoryId;
			this.name = name;
		}
		
		private static Map<Integer, ControllableCategory> CATEGORY_MAP = null;
		
		private static Map<Integer, ControllableCategory> initTypeMap() {
			Map<Integer, ControllableCategory> typeMap = new HashMap<>();
			for(ControllableCategory type : values()) {
				type.points = new ArrayList<ControllablePoints>();
				typeMap.put(type.getCategoryId(), type);
			}
			Collection<ControllablePoints> points = ControllablePoints.getAllPoints();
			
			for(ControllablePoints point :points) {
				ControllableCategory category = point.getParentControllableCategory();
				category.points.add(point);
			}
			
			return typeMap;
		}
		
		public static Map<Integer, ControllableCategory> getControllableCategoryMap() {
			CATEGORY_MAP = CATEGORY_MAP == null ? initTypeMap() : CATEGORY_MAP; 
			return CATEGORY_MAP;
		}

		public static Collection<ControllableCategory> getAllCategories() {
			CATEGORY_MAP = CATEGORY_MAP == null ? initTypeMap() : CATEGORY_MAP;
			return CATEGORY_MAP.values();
		}
		
		public static ControllableCategory valueOf(int categoryId) {
			CATEGORY_MAP = CATEGORY_MAP == null ? initTypeMap() : CATEGORY_MAP;
			return CATEGORY_MAP.get(categoryId);
		}

	}
	
	List<ControllableResourceContext> controllableResourceContexts;

	public List<ControllableResourceContext> getControllableResourceContexts() {
		return controllableResourceContexts;
	}

	public void setControllableResourceContexts(List<ControllableResourceContext> controllableResourceContexts) {
		this.controllableResourceContexts = controllableResourceContexts;
	}
	
	public void addControllableResourceContexts(ControllableResourceContext controllableResourceContext) {
		this.controllableResourceContexts = controllableResourceContexts == null ? new ArrayList<ControllableResourceContext>() : controllableResourceContexts;
		this.controllableResourceContexts.add(controllableResourceContext);
	}
}
