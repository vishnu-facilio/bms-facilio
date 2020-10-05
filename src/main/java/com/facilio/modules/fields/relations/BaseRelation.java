package com.facilio.modules.fields.relations;

import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.modules.FacilioEnum;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import org.apache.http.MethodNotSupportedException;

public class BaseRelation {
    private long id = -1;
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    private long baseFieldId = -1;
    public long getBaseFieldId() {
        return baseFieldId;
    }
    public void setBaseFieldId(long baseFieldId) {
        this.baseFieldId = baseFieldId;
    }

    private FacilioField baseField;
    public FacilioField getBaseField() {
        return baseField;
    }
    public void setBaseField(FacilioField baseField) {
        this.baseField = baseField;
    }

    private long derivedFieldId = -1;
    public long getDerivedFieldId() {
        return derivedFieldId;
    }
    public void setDerivedFieldId(long derivedFieldId) {
        this.derivedFieldId = derivedFieldId;
    }

    private FacilioField derivedField;
    public FacilioField getDerivedField() {
        return derivedField;
    }
    public void setDerivedField(FacilioField derivedField) {
        this.derivedField = derivedField;
    }

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

    protected boolean olderReading(ReadingContext reading, ReadingDataMeta previousRDM) {
        if (reading.getId()!=-1 || (reading.getTtime() != -1 && reading.getTtime() < previousRDM.getTtime())) {
            return true;
        }
        return false;
    }

    protected Object getReadingValue(FacilioField baseField, ReadingDataMeta previousRDM) {
        switch (baseField.getDataTypeEnum()) {
            case DECIMAL:
            case NUMBER:
                return FacilioUtil.castOrParseValueAsPerType(baseField, previousRDM.getValue());

            default:
                throw new IllegalArgumentException("Invalid data type for reading calculation");

        }
    }

    public Object calculateValue(ReadingContext reading, ReadingDataMeta previousRDM) throws Exception {
        throw new MethodNotSupportedException("Base class should override this method");
    }

    public enum Type implements FacilioEnum {
        DELTA("Delta")
        ;

        private String name;

        Type(String name) {
            this.name = name;
        }

        @Override
        public int getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return name;
        }

        public static Type valueOf(int type) {
            if (type > 0 && type <= values().length) {
                return values()[type - 1];
            }
            return null;
        }
    }
}
