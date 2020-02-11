package com.facilio.modules.fields;

import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.modules.FacilioEnum;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;

import java.util.Map;

public class MultiLookupField extends BaseLookupField implements SupplementRecord {
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

    public MultiLookupField() {
        // TODO Auto-generated constructor stub
        super();
    }

    protected MultiLookupField(MultiLookupField field) { // Do not forget to Handle here if new property is added
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

    public String parentFieldName() {
        if (parentFieldPosition != null) {
            switch (parentFieldPosition) {
                case LEFT:
                    return "left";
                case RIGHT:
                    return "right";
            }
        }
        return null;
    }

    public String childFieldName() {
        if (parentFieldPosition != null) {
            switch (parentFieldPosition) {
                case LEFT:
                    return "right";
                case RIGHT:
                    return "left";
            }
        }
        return null;
    }


    @Override
    public MultiLookupField clone() {
        // TODO Auto-generated method stub
        return new MultiLookupField(this);
    }

    @Override
    public FacilioField selectField() {
        return null;
    }

    @Override
    public FetchSupplementHandler newFetchHandler() {
        return new MultiLookupFetchHandler(this);
    }

    @Override
    public InsertSupplementHandler newInsertHandler() {
        return new MultiLookupWriteHandler(this);
    }

    @Override
    public UpdateSupplementHandler newUpdateHandler() {
        return new MultiLookupWriteHandler(this);
    }

    @Override
    public DeleteSupplementHandler newDeleteHandler() {
        return new MultiLookupWriteHandler(this);
    }

    public static enum ParentFieldPosition implements FacilioEnum {
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

    public long parseLookupRecordId(Object lookup, String duringAction) throws Exception {
        if (lookup instanceof Map) {
            Object recordId = ((Map<String, Object>) lookup).get("id");
            if (recordId == null || !(recordId instanceof Long)) {
                throw new IllegalArgumentException("Invalid ID in lookup object for multi lookup "+duringAction);
            }
            return (long) recordId;
        } else if (lookup instanceof ModuleBaseWithCustomFields) {
            long recordId = ((ModuleBaseWithCustomFields) lookup).getId();
            if (recordId < 1) {
                throw new IllegalArgumentException("Invalid ID in lookup object for multi lookup "+duringAction);
            }
            return recordId;
        } else if (LookupSpecialTypeUtil.isSpecialType(this.getSpecialType())) {
            long recordId = LookupSpecialTypeUtil.getLookupObjectId(this.getSpecialType(), lookup);
            if (recordId < 1) {
                throw new IllegalArgumentException("Invalid ID in lookup object for multi lookup "+duringAction);
            }
            return recordId;
        } else {
            throw new IllegalArgumentException("Invalid lookup object in record for multi lookup "+duringAction);
        }
    }
}
