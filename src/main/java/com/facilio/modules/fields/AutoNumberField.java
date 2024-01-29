package com.facilio.modules.fields;

import com.facilio.modules.FacilioIntEnum;

public class AutoNumberField extends FacilioField{

    private static final long serialVersionUID = 1L;

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public int getIdStartsFrom() {
        return idStartsFrom;
    }

    public void setIdStartsFrom(int idStartsFrom) {
        this.idStartsFrom = idStartsFrom;
    }

    public boolean isChangeExistingIds() {
        return changeExistingIds;
    }

    public void setChangeExistingIds(boolean changeExistingIds) {
        this.changeExistingIds = changeExistingIds;
    }

    private String prefix;
    private String suffix;
    private int idStartsFrom;
    private boolean changeExistingIds;

    public int getLastAutoNumberId() {
        return lastAutoNumberId;
    }

    public void setLastAutoNumberId(int lastAutoNumberId) {
        this.lastAutoNumberId = lastAutoNumberId;
    }

    private int lastAutoNumberId;

    public static enum AutoNumberFieldStatus implements FacilioIntEnum {

        NOT_INITIATED,
        INITIATED,
        COMPLETED;

        @Override
        public String getValue() {
            return name();
        }

        public static AutoNumberField.AutoNumberFieldStatus valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }

    private AutoNumberFieldStatus status;
    public AutoNumberFieldStatus getStatusEnum() {
        return status;
    }
    public void setStatus(AutoNumberFieldStatus status) {
        this.status = status;
    }
    public int getStatus() {
        if (status != null) {
            return status.getIndex();
        }
        return -1;
    }
    public void setStatus(int status) {
        this.status = AutoNumberFieldStatus.valueOf(status);
    }


    public AutoNumberField() {
        super();
    }

    protected AutoNumberField(AutoNumberField field) { // Do not forget to Handle here if new property is added
        super(field);
        setAutoNumberProps(field);
    }

    public void setAutoNumberProps(AutoNumberField field){
        this.prefix = field.prefix;
        this.suffix = field.suffix;
        this.idStartsFrom = field.idStartsFrom;
        this.changeExistingIds = field.changeExistingIds;
    }

}
