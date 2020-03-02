package com.facilio.controlaction.context;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import com.facilio.controlaction.context.ControllableAssetCategoryContext.ControllableCategory;
import com.fasterxml.jackson.annotation.JsonFormat;

public class ControllablePointContext {

	long id;
	long orgId;
	long fieldId;
	
	ControllablePoints controllablePoint;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getOrgId() {
		return orgId;
	}

	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}

	public long getFieldId() {
		return fieldId;
	}

	public void setFieldId(long fieldId) {
		this.fieldId = fieldId;
	}

	public int getControllablePoint() {
		if(controllablePoint != null) {
			return controllablePoint.getPointId();
		}
		return -1;
	}
	
	public ControllablePoints getControllableEnum() {
		return controllablePoint;
	}

	public void setControllablePoint(int controllablePoint) {
		this.controllablePoint = ControllablePoints.getControllableCategoryMap().get(controllablePoint);
	}
	
	@JsonFormat(shape = JsonFormat.Shape.OBJECT)
	public enum ControllablePoints {
		
		LIGHT_CONTROL(1,"Light Control",ControllableCategory.LIGHT),
		LIGHT_INTENSITY(2,"Light Intensity",ControllableCategory.LIGHT),
		
		AC_SETPOINT(3,"Setpoint",ControllableCategory.AC),
		
		CURTAIN_CONTROL(4,"Curtain Control",ControllableCategory.CURTAIN),
		
		PROJECTOR_CONTROL(5,"Projector Control",ControllableCategory.PROJECTOR),
		
		FAN_CONTROL(6,"Fan Control",ControllableCategory.FAN),
		FAN_SPEED(7,"Fan Speed",ControllableCategory.FAN),
		;
		
		public static Map<Integer, ControllablePoints> getControllableCategoryMap() {
			return POINT_MAP;
		}

		int pointId;
		String name;
		ControllableCategory parentCategory;
		
		public int getPointId() {
			return pointId;
		}
		
		public String getName() {
			return name;
		}
		public ControllableCategory getParentControllableCategory() {
			return parentCategory;
		}
		
		
		ControllablePoints(int pointId, String name,ControllableCategory parentCategory) {
			this.pointId = pointId;
			this.name = name;
			this.parentCategory = parentCategory;
		}
		
		private static final Map<Integer, ControllablePoints> POINT_MAP = Collections.unmodifiableMap(initTypeMap());
		private static Map<Integer, ControllablePoints> initTypeMap() {
			Map<Integer, ControllablePoints> typeMap = new HashMap<>();
			for(ControllablePoints type : values()) {
				typeMap.put(type.getPointId(), type);
			}
			return typeMap;
		}
		
		public static Collection<ControllablePoints> getAllPoints() {
			return POINT_MAP.values();
		}
		
		public static ControllablePoints valueOf(int categoryId) {
			return POINT_MAP.get(categoryId);
		}

	}
}
