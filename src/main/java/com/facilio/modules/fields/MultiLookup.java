package com.facilio.modules.fields;

import com.facilio.modules.FacilioEnum;
import com.facilio.modules.FacilioModule;

public class MultiLookup extends BaseLookupField implements SupplementRecord {
    private long relModuleId = -1;
    public long getRelModuleId() {
        return relModuleId;
    }
    public void setRelModuleId(long relModuleId) {
        this.relModuleId = relModuleId;
    }

    private FacilioModule relModule;
    public FacilioModule getRelModule() {
        return relModule;
    }
    public void setRelModule(FacilioModule relModule) {
        this.relModule = relModule;
    }

    protected MultiLookup(MultiLookup field) { // Do not forget to Handle here if new property is added
        super(field);
        this.relModuleId = field.relModuleId;
        this.relModule = field.relModule;
    }

    private ParentFieldPosition parentFieldPosition;
    public ParentFieldPosition getParentFieldPositionEnum() {
        return parentFieldPosition;
    }
    public void setParentFieldPositionEnum(ParentFieldPosition parentFieldPosition) {
        this.parentFieldPosition = parentFieldPosition;
    }
    public int getParentFieldPosition() {
        if (parentFieldPosition != null) {
            return parentFieldPosition.getIndex();
        }
        return -1;
    }
    public void setParentFieldPosition(int parentFieldPosition) {
        this.parentFieldPosition = ParentFieldPosition.valueOf(parentFieldPosition);
    }

    @Override
    public MultiLookup clone() {
        // TODO Auto-generated method stub
        return new MultiLookup(this);
    }

    @Override
    public FacilioField selectField() {
        return null;
    }

    @Override
    public FetchSupplementHandler newFetchHandler() {
        return new MultiLookupFetchHandler(this);
    }

    static enum ParentFieldPosition implements FacilioEnum {
        LEFT,
        RIGHT
        ;

        @Override
        public int getIndex() {
            return ordinal()+1;
        }

        @Override
        public String getValue() {
            return name();
        }

        private static ParentFieldPosition valueOf (int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }
}
