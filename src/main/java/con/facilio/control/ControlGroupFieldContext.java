package con.facilio.control;

import com.facilio.modules.FacilioEnum;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.V3Context;

public class ControlGroupFieldContext extends V3Context {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String name;
	Type type;
	ControlGroupContext controlGroup;
	ControlGroupAssetContext controlGroupAsset;
	ControlGroupRoutineContext routine;
	Long fieldId;
	FacilioField field;
	String trueVal;
	String falseVal;
	
	public static enum Type implements FacilioEnum {
        CONTROL_GROUP, ROUTINE;

        @Override
        public int getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return name();
        }

        public static Type valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }




	public String getName() {
		return name;
	}




	public void setName(String name) {
		this.name = name;
	}




	public int getType() {
		if(type != null) {
			return type.getIndex();
		}
		return -1;
	}
	
	public Type getTypeEnum() {
		return type;
	}

	public void setType(int type) {
		this.type = Type.valueOf(type);
	}




	public ControlGroupContext getControlGroup() {
		return controlGroup;
	}




	public void setControlGroup(ControlGroupContext controlGroup) {
		this.controlGroup = controlGroup;
	}




	public ControlGroupAssetContext getControlGroupAsset() {
		return controlGroupAsset;
	}




	public void setControlGroupAsset(ControlGroupAssetContext controlGroupAsset) {
		this.controlGroupAsset = controlGroupAsset;
	}




	public ControlGroupRoutineContext getRoutine() {
		return routine;
	}




	public void setRoutine(ControlGroupRoutineContext routine) {
		this.routine = routine;
	}




	public Long getFieldId() {
		return fieldId;
	}




	public void setFieldId(Long fieldId) {
		this.fieldId = fieldId;
	}




	public FacilioField getField() {
		return field;
	}




	public void setField(FacilioField field) {
		this.field = field;
	}




	public String getTrueVal() {
		return trueVal;
	}




	public void setTrueVal(String trueVal) {
		this.trueVal = trueVal;
	}




	public String getFalseVal() {
		return falseVal;
	}




	public void setFalseVal(String falseVal) {
		this.falseVal = falseVal;
	}
} 
