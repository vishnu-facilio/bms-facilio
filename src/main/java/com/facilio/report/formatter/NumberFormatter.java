package com.facilio.report.formatter;

import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.time.DateTimeUtil;
import com.facilio.unitconversion.Unit;
import com.facilio.unitconversion.UnitsUtil;
import org.json.simple.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NumberFormatter extends Formatter {

    private Integer displayUnit;
    private String customUnit;
    private Boolean appendUnit = true;
    private Boolean prefix = false;
    private Boolean applyLocaleSeparator = true;
    private Character thousandSeparator;
    private static List<Long> timesList = Arrays.asList(TimeUnit.DAYS.toMillis(365L), TimeUnit.DAYS.toMillis(30L), TimeUnit.DAYS.toMillis(1L), TimeUnit.HOURS.toMillis(1L), TimeUnit.MINUTES.toMillis(1L), TimeUnit.SECONDS.toMillis(1L));
    private static List<String> timesString = Arrays.asList("year", "month", "day", "hour", "minute", "second");
    public NumberFormatter(FacilioField field) {
        super(field);
    }

    public Integer getDisplayUnit() {
        return displayUnit;
    }

    public NumberFormatter displayUnit(Integer displayUnit) {
        this.displayUnit = displayUnit;
        return this;
    }

    public String getCustomUnit() {
        return customUnit;
    }

    public NumberFormatter customUnit(String customUnit) {
        this.customUnit = customUnit;
        return this;
    }

    public boolean isAppendUnit() {
        return appendUnit;
    }

    public NumberFormatter appendUnit(boolean appendUnit) {
        this.appendUnit = appendUnit;
        return this;
    }

    public Boolean isPrefix() {
        return prefix;
    }

    public NumberFormatter prefix(Boolean prefix) {
        this.prefix = prefix;
        return this;
    }

    public Boolean getApplyLocaleSeparator() {
        return applyLocaleSeparator;
    }

    public NumberFormatter applyLocaleSeparator(Boolean applyLocaleSeparator) {
        this.applyLocaleSeparator = applyLocaleSeparator;
        return this;
    }

    public Character getThousandSeparator() {
        return thousandSeparator;
    }

    public NumberFormatter thousandSeparator(Character thousandSeparator) {
        this.thousandSeparator = thousandSeparator;
        return this;
    }

    @Override
    public Object format(Object value) {
        if (value == null) {
            return null;
        }
        Double result = Double.valueOf(value.toString());
        String unit = null;
        NumberField numberField = (NumberField) getField();
        if(!isNullOrEmpty(numberField) && numberField.getDisplayType() == FacilioField.FieldDisplayType.DURATION)
        {
            if(value != null && value instanceof BigDecimal)
            {
                BigDecimal bigDecimal_value = (BigDecimal) value;
                value = bigDecimal_value.longValue();
            }
            return relativeTime(Long.parseLong((((Long)value)*1000)+""));
        }
        else if (!isNullOrEmpty(numberField.getUnitId(), getDisplayUnit())) {
            result = UnitsUtil.convert(value, numberField.getUnitId(), getDisplayUnit());
            unit = Unit.valueOf(getDisplayUnit()).getSymbol();
        }
        else if (!isNullOrEmpty(getCustomUnit())) {
            unit = getCustomUnit();
        }

        String pattern = "###,###";
        if (!isNullOrEmpty(unit) && isAppendUnit()) {
            if (isPrefix() && isAppendUnit()) {
                pattern = unit + pattern;
            }
            else if (isAppendUnit()){
                pattern = pattern + " " + unit;
            }
        }

        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(DateTimeUtil.getLocale());
        if (!getApplyLocaleSeparator() && !isNullOrEmpty(getThousandSeparator())) {
            DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
            symbols.setGroupingSeparator(getThousandSeparator());
            formatter.setDecimalFormatSymbols(symbols);
        }
        formatter.applyPattern(pattern);
        return formatter.format(result);
    }

    public String getUnit(){
        if(getDisplayUnit() != null){
            return Unit.valueOf(getDisplayUnit()).getSymbol();
        } else {
            return getCustomUnit();
        }
    }

    @Override
    public JSONObject serialize() {
        JSONObject format = new JSONObject();
        format.put("displayUnit", getDisplayUnit());
        format.put("customUnit", getDisplayUnit());
        format.put("appendUnit", isAppendUnit());
        format.put("prefix", isPrefix());
        format.put("applyLocaleSeparator", getApplyLocaleSeparator());
        format.put("thousandSeparator", getThousandSeparator());
        return format;
    }

    @Override
    public void deserialize(JSONObject format) {
        if (format.containsKey("displayUnit") && format.get("displayUnit") != null ) {
            this.displayUnit = Integer.parseInt(format.getOrDefault("displayUnit", this.displayUnit).toString());
        }
        if (format.containsKey("thousandSeparator") && format.get("thousandSeparator") != null ) {
            this.thousandSeparator = (Character) format.getOrDefault("thousandSeparator", this.thousandSeparator).toString().charAt(0);
        }
        this.customUnit = (String) format.getOrDefault("customUnit", this.customUnit);
        this.appendUnit = (Boolean) format.getOrDefault("appendUnit", this.appendUnit);
        this.prefix = (Boolean) format.getOrDefault("prefix", this.prefix);
        this.applyLocaleSeparator = (Boolean) format.getOrDefault("applyLocaleSeparator", this.applyLocaleSeparator);
    }
    public static String relativeTime(long duration) {
        List<Long> timesList = Arrays.asList(TimeUnit.DAYS.toMillis(365L), TimeUnit.DAYS.toMillis(30L), TimeUnit.DAYS.toMillis(1L), TimeUnit.HOURS.toMillis(1L), TimeUnit.MINUTES.toMillis(1L), TimeUnit.SECONDS.toMillis(1L));
        StringBuffer relativeTime = new StringBuffer();
        for(int i = 0; i < timesList.size(); ++i) {
            Long current = (Long)timesList.get(i);
            long temp = duration / current;
            if (temp > 0L) {
                relativeTime.append(" ").append(temp).append(" ").append((String)timesString.get(i)).append(temp != 1L ? "s" : "");
                duration = duration % current;
            }
        }

        return relativeTime.length() == 0 ? relativeTime.append("0 seconds").toString() : relativeTime.toString();
    }
}