package com.facilio.bmsconsole.context;

import java.io.Serializable;
import java.util.Map;

import com.facilio.modules.FacilioIntEnum;
import com.facilio.modules.fields.FacilioField;
import com.facilio.unitconversion.Unit;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

@Log4j
@Getter
@Setter
public class ReadingDataMeta implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long orgId = -1;
	int unit = -1;
	public Unit getUnitEnum() {
		if(unit != -1) {
			return Unit.valueOf(unit);
		}
		return null;
	}

	private long id;

	private long resourceId = -1;

	private ResourceContext resourceContext;

	private long fieldId = -1;

	private FacilioField field;

	private long ttime = -1;

	private Object value;

	private String actualValue;

	private long readingDataId = -1;

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

	private Boolean custom;
	public boolean isCustom() {
		if(custom != null) {
			return custom.booleanValue();
		}
		return false;
	}

	public enum ReadingInputType {
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
	
	public enum ReadingType {
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
		StringBuilder builder = new StringBuilder()
				.append("Org Id : ").append(orgId)
				.append(", Resource ID : ").append(resourceId)
				.append(", Field ID : ").append(fieldId)
				.append(", Ttime : ").append(ttime)
				.append(", Value : ").append(value)
				.append(", Reading Data ID : ").append(readingDataId)
				.append(", Unit : ").append(unit)
				.append(", Input Type : ").append(inputType);
		return builder.toString();
	}
}
