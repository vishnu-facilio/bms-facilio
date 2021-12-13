package com.facilio.modules.fields;

import com.facilio.constants.FacilioConstants;
import com.facilio.modules.UrlRecord;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

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

    @Override
    public void validateRecord(Map<String, Object> lookupRecord) throws Exception {
        String href = (String) lookupRecord.get("href");
        V3Util.throwRestException(StringUtils.isEmpty(href), ErrorCode.VALIDATION_ERROR, "HREF cannot be empty for Url Field");
    }

    private UrlTarget target;
    private Boolean showAlt;

    public static enum UrlTarget {
        _blank,
        _self
    }
}
