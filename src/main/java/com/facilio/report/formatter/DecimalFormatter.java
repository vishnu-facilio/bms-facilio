package com.facilio.report.formatter;

import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.time.DateTimeUtil;
import com.facilio.unitconversion.Unit;
import com.facilio.unitconversion.UnitsUtil;
import org.json.simple.JSONObject;

import javax.validation.constraints.NotNull;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

public class DecimalFormatter extends Formatter {

    private Integer displayUnit;
    private String customUnit;
    private Boolean appendUnit = true;
    private Boolean prefix = false;
    private Integer decimalPoints = 2;
    private Boolean applyLocaleSeparator = true;
    private Character thousandSeparator;
    private Character decimalSeparator;

    public DecimalFormatter(FacilioField field) {
        super(field);
    }

    public Integer getDisplayUnit() {
        return displayUnit;
    }

    public DecimalFormatter displayUnit(Integer displayUnit) {
        this.displayUnit = displayUnit;
        return this;
    }

    public String getCustomUnit() {
        return customUnit;
    }

    public DecimalFormatter customUnit(String customUnit) {
        this.customUnit = customUnit;
        return this;
    }

    public boolean isAppendUnit() {
        return appendUnit;
    }

    public DecimalFormatter appendUnit(boolean appendUnit) {
        this.appendUnit = appendUnit;
        return this;
    }

    public Boolean isPrefix() {
        return prefix;
    }

    public DecimalFormatter prefix(Boolean prefix) {
        this.prefix = prefix;
        return this;
    }

    public Integer getDecimalPoints() {
        return decimalPoints;
    }

    public DecimalFormatter decimalPoints(Integer decimalPoints) {
        this.decimalPoints = decimalPoints;
        return this;
    }

    public Boolean getApplyLocaleSeparator() {
        return applyLocaleSeparator;
    }

    public DecimalFormatter applyLocaleSeparator(Boolean applyLocaleSeparator) {
        this.applyLocaleSeparator = applyLocaleSeparator;
        return this;
    }

    public Character getThousandSeparator() {
        return thousandSeparator;
    }

    public DecimalFormatter thousandSeparator(Character thousandSeparator) {
        this.thousandSeparator = thousandSeparator;
        return this;
    }

    public Character getDecimalSeparator() {
        return decimalSeparator;
    }

    public DecimalFormatter decimalSeparator(Character decimalSeparator) {
        this.decimalSeparator = decimalSeparator;
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
        if (!isNullOrEmpty(numberField.getUnitId(), getDisplayUnit())) {
            result = UnitsUtil.convert(value, numberField.getUnitId(), getDisplayUnit());
            unit = Unit.valueOf(getDisplayUnit()).getSymbol();
        }
        else if (!isNullOrEmpty(getCustomUnit())) {
            unit = getCustomUnit();
        }

        String pattern = "###,###.###";
        if (!isNullOrEmpty(unit) && isAppendUnit()) {
            if (isPrefix()) {
                pattern = unit + pattern;
            }
            else {
                pattern = pattern + " " + unit;
            }
        }

        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(DateTimeUtil.getLocale());
        formatter.setMaximumFractionDigits(getDecimalPoints());
        if (!getApplyLocaleSeparator()) {
            DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
            if (!isNullOrEmpty(getThousandSeparator())) {
                symbols.setGroupingSeparator(getThousandSeparator());
            }
            if (!isNullOrEmpty(getDecimalSeparator())) {
                symbols.setDecimalSeparator(getDecimalSeparator());
            }
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
        JSONObject formatJSON = new JSONObject();
        formatJSON.put("displayUnit", getDisplayUnit());
        formatJSON.put("customUnit", getCustomUnit());
        formatJSON.put("appendUnit", isAppendUnit());
        formatJSON.put("prefix", isPrefix());
        formatJSON.put("decimalPoints", getDecimalPoints());
        formatJSON.put("applyLocaleSeparator", getApplyLocaleSeparator());
        formatJSON.put("thousandSeparator", getThousandSeparator());
        formatJSON.put("decimalSeparator", getDecimalSeparator());
        return formatJSON;
    }

    @Override
    public void deserialize(JSONObject formatJSON) {
        if (formatJSON.containsKey("displayUnit") && formatJSON.get("displayUnit") != null ) {
            this.displayUnit = Integer.parseInt(formatJSON.getOrDefault("displayUnit", this.displayUnit).toString());
        }
        if (formatJSON.containsKey("decimalPoints") && formatJSON.get("decimalPoints") != null ) {
            this.displayUnit = Integer.parseInt(formatJSON.getOrDefault("decimalPoints", this.displayUnit).toString());
        }
        if (formatJSON.containsKey("thousandSeparator") && formatJSON.get("thousandSeparator") != null ) {
            this.thousandSeparator = (Character) formatJSON.getOrDefault("thousandSeparator", this.thousandSeparator).toString().charAt(0);
        }
        if (formatJSON.containsKey("decimalSeparator") && formatJSON.get("decimalSeparator") != null ) {
            this.thousandSeparator = (Character) formatJSON.getOrDefault("decimalSeparator", this.thousandSeparator).toString().charAt(0);
        }
        this.customUnit = (String) formatJSON.getOrDefault("customUnit", this.customUnit);
        this.appendUnit = (Boolean) formatJSON.getOrDefault("appendUnit", this.appendUnit);
        this.prefix = (Boolean) formatJSON.getOrDefault("prefix", this.prefix);
        this.applyLocaleSeparator = (Boolean) formatJSON.getOrDefault("applyLocaleSeparator", this.applyLocaleSeparator);
    }
}