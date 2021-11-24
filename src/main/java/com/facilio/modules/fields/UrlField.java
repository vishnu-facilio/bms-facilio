package com.facilio.modules.fields;

import com.facilio.constants.FacilioConstants;
import com.facilio.modules.UrlRecord;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class UrlField extends BaseSystemLookupField<UrlRecord> {
    public UrlField() {
        super(FacilioConstants.SystemLookup.URL_RECORD, UrlRecord.class);
    }

    @Override
    public void addDefaultPropsToLookupRecordDuringFetch(Map<String, Object> lookupRecord) {
        lookupRecord.put("target", target);
    }

    @Override
    public void addDefaultPropsToLookupRecordDuringFetch(UrlRecord lookupRecord) {
        lookupRecord.setTarget(target);
    }

    private UrlTarget target;
    private Boolean showAlt;

    public static enum UrlTarget {
        _blank,
        _self
    }
}
