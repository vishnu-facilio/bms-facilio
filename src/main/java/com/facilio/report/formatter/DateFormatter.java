package com.facilio.report.formatter;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateTimeUtil;
import com.facilio.time.TimeFormat;
import org.json.simple.JSONObject;

import javax.validation.constraints.NotNull;

public class DateFormatter extends Formatter {

    private String customFormat;

    public DateFormatter(FacilioField field) {
        super(field);
    }

    public String getCustomFormat() {
        return customFormat;
    }

    public DateFormatter customFormat(String customFormat) {
        this.customFormat = customFormat;
        return this;
    }

    @Override
    public Object format(Object value) {
        if (value == null) {
            return null;
        }
        Long result = Long.valueOf(value.toString());

        if (!isNullOrEmpty(getCustomFormat())) {
            try {
                return DateTimeUtil.getFormattedTime(result, getCustomFormat());
            } catch (Exception e) {}
        }

        String dateFormat = (String) getOrDefault(AccountUtil.getCurrentAccount().getOrg().getDateFormat(), "DD-MMM-YYYY");

        if (getField().getDataTypeEnum() == FieldType.DATE_TIME) {
            TimeFormat timeFormat = AccountUtil.getCurrentAccount().getOrg().getTimeFormatEnum();
            dateFormat = dateFormat + " " + ((timeFormat == TimeFormat.HOUR_12) ? "hh:mm a" : "HH:mm");
        }

        if(getCustomFormat() != null) {
            return DateTimeUtil.getFormattedTime(result, getCustomFormat());
        } else {
            return DateTimeUtil.getFormattedTime(result);
        }
    }

    @Override
    public JSONObject serialize() {
        JSONObject format = new JSONObject();
        format.put("customFormat", getCustomFormat());
        return format;
    }

    @Override
    public void deserialize(JSONObject format) {
        this.customFormat = (String) format.getOrDefault("customFormat", this.customFormat);
    }
}