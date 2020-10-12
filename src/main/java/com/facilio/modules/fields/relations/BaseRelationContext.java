package com.facilio.modules.fields.relations;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioEnum;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import org.apache.http.MethodNotSupportedException;

public class BaseRelationContext {
    private long id = -1;
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    private long baseFieldModuleId = -1;
    public long getBaseFieldModuleId() {
        return baseFieldModuleId;
    }
    public void setBaseFieldModuleId(long baseFieldModuleId) {
        this.baseFieldModuleId = baseFieldModuleId;
    }

    private long baseFieldId = -1;
    public long getBaseFieldId() {
        return baseFieldId;
    }
    public void setBaseFieldId(long baseFieldId) {
        this.baseFieldId = baseFieldId;
    }

    private FacilioField baseField;
    public FacilioField getBaseField() throws Exception {
        if (baseField == null) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            baseField = modBean.getField(baseFieldId, baseFieldModuleId);
        }
        return baseField;
    }
    public void setBaseField(FacilioField baseField) {
        this.baseField = baseField;
    }

    private long derivedFieldModuleId = -1;
    public long getDerivedFieldModuleId() {
        return derivedFieldModuleId;
    }
    public void setDerivedFieldModuleId(long derivedFieldModuleId) {
        this.derivedFieldModuleId = derivedFieldModuleId;
    }

    private long derivedFieldId = -1;
    public long getDerivedFieldId() {
        return derivedFieldId;
    }
    public void setDerivedFieldId(long derivedFieldId) {
        this.derivedFieldId = derivedFieldId;
    }

    private FacilioField derivedField;
    public FacilioField getDerivedField() throws Exception {
        if (derivedField == null) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            derivedField = modBean.getField(derivedFieldId, derivedFieldModuleId);
        }
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
    public Type getTypeEnum() {
        return type;
    }
    public void setType(Type type) {
        this.type = type;
    }

    private boolean history;
    public boolean isHistory() {
        return history;
    }
    public void setHistory(boolean history) {
        this.history = history;
    }

    protected boolean olderReading(ReadingContext reading, ReadingDataMeta previousRDM) {
        // when running for history, don't check for latest reading..
        if (history) {
            return false;
        }

        if (reading.getId()!=-1 || (reading.getTtime() != -1 && reading.getTtime() < previousRDM.getTtime())) {
            return true;
        }
        return false;
    }

    protected final void validateType(FacilioField field, FieldType... typesSupported) {
        for (FieldType fieldType : typesSupported) {
            if (field.getDataTypeEnum() == fieldType) {
                return;
            }
        }
        throw new IllegalArgumentException("Fields of type " + field.getDataTypeEnum().getTypeAsString() + " is not supported");
    }

    public void validate() throws Exception {
        throw new MethodNotSupportedException("Should override this method");
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
        DELTA("Delta"),
        TIME_DELTA("Time Delta"),
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
