package com.facilio.modules.fields;

import com.facilio.modules.FacilioEnum;

public class ScoreField extends FacilioField {

    private Type type;
    public int getType() {
        if (type != null) {
            return type.getIndex();
        }
        return -1;
    }
    public void setType(int type) {
        this.type = Type.valueOf(type);
    }
    public Type getTypeEnum() {
        return type;
    }
    public void setType(Type type) {
        this.type = type;
    }

    private float scale;
    public float getScale() {
        return scale;
    }
    public void setScale(float scale) {
        this.scale = scale;
    }

    public enum Type implements FacilioEnum {
        PERCENTAGE("Percentage"),
        RANGE("Range")
        ;

        private String name;

        Type(String name) {
            this.name = name;
        }

        @Override
        public int getIndex() {
            return ordinal() + 1;
        }

        public static Type valueOf(int type) {
            if (type > 0 && type <= values().length) {
                return values()[type - 1];
            }
            return null;
        }

        @Override
        public String getValue() {
            return name;
        }
    }
}
