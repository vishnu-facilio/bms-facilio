package com.facilio.bmsconsole.context;

import java.io.Serializable;
import java.util.Map;

import com.facilio.modules.FacilioIntEnum;
import com.facilio.modules.fields.FacilioField;
import com.facilio.unitconversion.Unit;

public class ReadingDataMeta implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long orgId = -1;
	int unit = -1;
	public int getUnit() {
		return unit;
	}
	public void setUnit(int unit) {
		this.unit = unit;
	}
	public Unit getUnitEnum() {
		if(unit != -1) {
			return Unit.valueOf(unit);
		}
		return null;
	}
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private long id;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	private long resourceId = -1;
	public long getResourceId() {
		return resourceId;
	}
	public void setResourceId(long resourceId) {
		this.resourceId = resourceId;
	}
	
	private ResourceContext resourceContext;
	
	public ResourceContext getResourceContext() {
		return resourceContext;
	}
	public void setResourceContext(ResourceContext resourceContext) {
		this.resourceContext = resourceContext;
	}

	private long fieldId = -1;
	public long getFieldId() {
		return fieldId;
	}
	public void setFieldId(long fieldId) {
		this.fieldId = fieldId;
	}
	
	private FacilioField field;
	public FacilioField getField() {
		return field;
	}
	public void setField(FacilioField field) {
		this.field = field;
	}

	private long ttime = -1;
	public long getTtime() {
		return ttime;
	}
	public void setTtime(long ttime) {
		this.ttime = ttime;
	}
	
	private Object value;
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	
	private String actualValue;
	public String getActualValue() {
		return actualValue;
	}
	public void setActualValue(String actualValue) {
		this.actualValue = actualValue;
	}

	private long readingDataId = -1;
	public long getReadingDataId() {
		return readingDataId;
	}
	public void setReadingDataId(long readingDataId) {
		this.readingDataId = readingDataId;
	}
	
	private ReadingInputType inputType;
	public ReadingInputType getInputTypeEnum() {
		return inputType;
	}
	public void setInputType(ReadingInputType inputType) {
		this.inputType = inputType;
	}
	public int getInputType() {
		if (inputType != null) {
			return inputType.getValue();
		}
		return -1;
	}
	public void setInputType(int inputType) {
		this.inputType = ReadingInputType.valueOf(inputType);
	}
	
	private ReadingType readingType;
	public ReadingType getReadingTypeEnum() {
		return readingType;
	}
	public void setReadingType(ReadingType readingType) {
		this.readingType = readingType;
	}
	public int getReadingType() {
		if (readingType != null) {
			return readingType.getValue();
		}
		return -1;
	}
	public void setReadingType(int readingType) {
		this.readingType = ReadingType.valueOf(readingType);
	}
	
	Map<Integer, String> inputValues;
	public Map<Integer, String> getInputValues() {
		return inputValues;
	}
	public void setInputValues(Map<Integer, String> inputValues) {
		this.inputValues = inputValues;
	}
	
	ControlActionMode controlActionMode;
	public int getControlActionMode() {
		if(controlActionMode != null) {
			return controlActionMode.getIntVal();
		}
		return -1;
	}
	
	public ControlActionMode getControlActionModeEnum() {
		return controlActionMode;
	}

	public void setControlActionMode(int controlActionMode) {
		if(controlActionMode > 0) {
			this.controlActionMode = ControlActionMode.valueOf(controlActionMode);
		}
	}
	private Boolean isControllable;
	
	public boolean isControllable() {
		if(isControllable != null) {
			return isControllable.booleanValue();
		}
		return false;
	}
	
	public Boolean getIsControllable() {
		return isControllable;
	}
	public void setIsControllable(Boolean isControllable) {
		this.isControllable = isControllable;
	}

	private Boolean custom;
	public boolean isCustom() {
		if(custom != null) {
			return custom.booleanValue();
		}
		return false;
	}
	public Boolean getCustom() {
		return custom;
	}
	public void setCustom(boolean custom) {
		this.custom = custom;
	}

	public static enum ReadingInputType {
		WEB,
		TASK,
		CONTROLLER_MAPPED,
		FORMULA_FIELD,
		HIDDEN_FORMULA_FIELD,
		ALARM_POINT_FIELD,
		;
		
		public int getValue() {
			return ordinal()+1;
		}
		public static ReadingInputType valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}
	
	public static enum ReadingType {
		READ,
		WRITE
		;
		
		public int getValue() {
			return ordinal() + 1;
		}
		public static ReadingType valueOf (int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
 	}
	
	public enum ControlActionMode implements FacilioIntEnum {
		SANDBOX(1,"Sandbox"),
		LIVE(2,"Live")
		;
		int intVal;
		String name;

		public int getIntVal() {
			return intVal;
		}

		public String getName() {
			return name;
		}

		ControlActionMode(int intVal, String name) {
			this.intVal = intVal;
			this.name = name;
		}
		@Override
		public Integer getIndex() {
			return ordinal() + 1;
		}

		public static ControlActionMode valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
		@Override
		public String getValue() {
			return getName();
		}

 	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		StringBuilder builder =  new StringBuilder()
										.append("Org Id : ").append(orgId)
										.append(", Resource ID : ").append(resourceId)
										.append(", Field ID : ").append(fieldId)
										.append(", Ttime : ").append(ttime)
										.append(", Value : ").append(value)
										.append(", Reading Data ID : ").append(readingDataId)
										.append(", Unit : ").append(unit)
										.append(", Input Type : "+inputType);
		return builder.toString();
	}
}
