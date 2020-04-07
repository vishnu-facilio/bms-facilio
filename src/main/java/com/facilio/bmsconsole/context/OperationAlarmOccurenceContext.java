package com.facilio.bmsconsole.context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import com.fasterxml.jackson.annotation.JsonInclude;

public class OperationAlarmOccurenceContext extends AlarmOccurrenceContext {
	private static final long serialVersionUID = 1L;

	@Override
    public Type getTypeEnum() {
        return Type.OPERATION_OCCURRENCE;
    }
    private OperationAlarmContext.CoverageType coverageType;
    @JsonInclude
    public final int getCoverageType() {
        coverageType = getCoverageTypeEnum();
        if (coverageType != null) {
            return coverageType.getIndex();
        }
        return -1;
    }

    private OperationAlarmContext.CoverageType getCoverageTypeEnum() {
        return coverageType;
    }

    public final void setCoverageType(int eventCoverageType) {
        coverageType = OperationAlarmContext.CoverageType.valueOf(eventCoverageType);
    }

    private FacilioField readingField;
    public FacilioField getReadingField(){
        try {
            if(readingField == null && readingFieldId > 0) {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                readingField = modBean.getField(readingFieldId);
            }
        }
        catch(Exception e) {
        }
        return readingField;
    }
    public void setReadingField(FacilioField readingField) {
        this.readingField = readingField;
    }

    private long readingFieldId = -1;
    public long getReadingFieldId() {
        return readingFieldId;
    }
    public void setReadingFieldId(long readingFieldId) {
        this.readingFieldId = readingFieldId;
    }


}
