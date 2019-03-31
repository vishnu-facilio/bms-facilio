package com.facilio.bmsconsole.modules;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.collections.UniqueMap;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.DBUtil;

import java.util.Collections;
import java.util.Map;

public interface AggregateOperator {
    // Max Operator Code : 26 - Kindly change here if you add new aggregation operator

    public static AggregateOperator getAggregateOperator(int value) {
        return AGGREGATE_OPERATOR_MAP.get(value);
    }
    public FacilioField getSelectField(FacilioField field) throws Exception;
    static final Map<Integer, AggregateOperator> AGGREGATE_OPERATOR_MAP = Collections.unmodifiableMap(initTypeMap());
    static Map<Integer, AggregateOperator> initTypeMap() {
        Map<Integer, AggregateOperator> typeMap = new UniqueMap<>();
        for(AggregateOperator type : CommonAggregateOperator.values()) {
            typeMap.put(type.getValue(), type);
        }
        for(AggregateOperator type : NumberAggregateOperator.values()) {
            typeMap.put(type.getValue(), type);
        }
        for(AggregateOperator type : StringAggregateOperator.values()) {
            typeMap.put(type.getValue(), type);
        }
        for(AggregateOperator type : DateAggregateOperator.values()) {
            typeMap.put(type.getValue(), type);
        }
        for(AggregateOperator type : SpaceAggregateOperator.values()) {
            typeMap.put(type.getValue(), type);
        }
        for(AggregateOperator type : EnergyPurposeAggregateOperator.values()) {
            typeMap.put(type.getValue(), type);
        }
        return typeMap;
    }
    public int getValue();
    public String getStringValue();

    public enum CommonAggregateOperator implements AggregateOperator {
        ACTUAL(0,"Actual","{$place_holder$}"),
        COUNT(1,"Count","count({$place_holder$})"),
        ;

        private int value;
        private String stringValue;
        private String expr;
        public int getValue() {
            return value;
        }
        public String getStringValue() {
            return stringValue;
        }
        CommonAggregateOperator(int value,String stringValue,String expr) {
            this.value = value;
            this.stringValue = stringValue;
            this.expr = expr;
        }

        public FacilioField getSelectField(FacilioField field) throws Exception {
            switch (this) {
                case ACTUAL:
                    return field.clone();
                case COUNT:
                    String selectFieldString = expr.replace("{$place_holder$}", field.getCompleteColumnName());
                    NumberField field1 = new NumberField();
                    field1.setDataType(FieldType.NUMBER);
                    field1.setName(field.getName());
                    field1.setDisplayName(field.getDisplayName());
                    field1.setColumnName(selectFieldString);
                    return field1;
            }
            return field;
//			if(field instanceof NumberField) {
//				NumberField numberField =  (NumberField)field;
//				NumberField selectFieldNumber = new NumberField();
//				selectFieldNumber.setMetric(numberField.getMetric());
//				selectFieldNumber.setUnitId(numberField.getUnitId());
//				selectField = selectFieldNumber;
//			}
//			else {
//				selectField = new FacilioField();
//			}
//			selectField.setName(field.getName());
//			selectField.setDisplayName(field.getDisplayName());
//			selectField.setColumnName(selectFieldString);
//			selectField.setFieldId(field.getFieldId());
//			return selectField;
        }
    }

    public enum NumberAggregateOperator implements AggregateOperator {
        AVERAGE(2,"Average","avg({$place_holder$})"),
        SUM(3,"Sum","sum({$place_holder$})"),
        MIN(4,"Min","min({$place_holder$})"),
        MAX(5,"Max","max({$place_holder$})"),
        RANGE(6,"Range","CONCAT(MIN({$place_holder$}),',',AVG({$place_holder$}),',',MAX({$place_holder$}))")
        ;

        private int value;
        private String stringValue;
        private String expr;
        public int getValue() {
            return value;
        }
        public String getStringValue() {
            return stringValue;
        }
        NumberAggregateOperator(Integer value,String stringValue,String expr) {
            this.value = value;
            this.stringValue = stringValue;
            this.expr = expr;
        }

        public FacilioField getSelectField(FacilioField field) throws Exception {
            String selectFieldString = expr.replace("{$place_holder$}", field.getCompleteColumnName());
            FacilioField selectField = null;
            if(field instanceof NumberField) {
                NumberField numberField =  (NumberField)field;
                NumberField selectFieldNumber = new NumberField();
                selectFieldNumber.setMetric(numberField.getMetric());
                selectFieldNumber.setUnitId(numberField.getUnitId());
                selectField = selectFieldNumber;
            }
            else {
                selectField = new FacilioField();
            }
            selectField.setName(field.getName());
            selectField.setDisplayName(field.getDisplayName());
            selectField.setColumnName(selectFieldString);
            selectField.setFieldId(field.getFieldId());
            return selectField;
        }
    }

    public enum StringAggregateOperator implements AggregateOperator {
        ;

        private int value;
        private String stringValue;
        private String expr;

        public int getValue() {
            return value;
        }
        public String getStringValue() {
            return stringValue;
        }
        StringAggregateOperator(Integer value,String stringValue,String expr) {
            this.value = value;
            this.stringValue = stringValue;
            this.expr = expr;
        }
        public FacilioField getSelectField(FacilioField field) throws Exception {
            String selectFieldString =expr.replace("{$place_holder$}", field.getCompleteColumnName());

            FacilioField selectField =  new FacilioField();
            selectField.setName(field.getName());
            selectField.setDisplayName(field.getDisplayName());
            selectField.setColumnName(selectFieldString);
            selectField.setFieldId(field.getFieldId());
            return selectField;
        }
    }

    public enum DateAggregateOperator implements AggregateOperator {

        YEAR(8,"Yearly","year.expr",true), //Yearly aggregation
        MONTHANDYEAR(10,"monthAndYear","monthandyear.expr", "MMMM yyyy",false), //Monthly aggregation
        WEEKANDYEAR(11,"weekAndYear","weekandyear.expr",false), //Weekly aggregation
        FULLDATE(12,"daily","fulldate.expr", "EEEE, MMMM dd, yyyy",true), //Daily Aggregation
        DATEANDTIME(13,"dateAndTime","dateandtime.expr",false), //Hourly aggregation
        MONTH(15,"Monthly","month.expr",true), //Monthly aggregation
        WEEK(16,"Week","week.expr",false), //Weekly aggregation
        WEEKDAY(17,"Weekly","weekday.expr",true), //Daily aggregation
        DAYSOFMONTH(18,"Daily","daysofmonth.expr", "EEEE, MMMM dd, yyyy",false), //Daily
        HOURSOFDAY(19,"hoursOfDay","hoursofday.expr", "EEE, MMM dd, yyyy hh a",false), //Hourly
        HOURSOFDAYONLY(20,"Hourly","hoursofdayonly.expr", "EEE, MMM dd, yyyy hh a",true), //Hourly
        QUARTERLY(25,"Quarterly","quaterly.expr", "MMMM yyyy",false), //Quarterly
        ;

        private int value;
        private String stringValue;
        private String expr;
        private String format;
        private boolean isPublic;
        public boolean isPublic() {
            return isPublic;
        }
        public int getValue() {
            return value;
        }
        public String getStringValue() {
            return stringValue;
        }
        public String getFormat() {
            return format;
        }
        DateAggregateOperator(Integer value,String stringValue,String expr,boolean isPublic) {
            this(value, stringValue, expr, null, isPublic);
        }
        DateAggregateOperator(Integer value,String stringValue,String expr, String format,boolean isPublic) {
            this.value = value;
            this.stringValue = stringValue;
            this.expr = DBUtil.getQuery(expr);
            this.format = format;
            this.isPublic = isPublic;
        }
        public FacilioField getSelectField(FacilioField field) throws Exception {
            String selectFieldString = expr.replace("{$place_holder$}", field.getCompleteColumnName());
            String timeZone = getTimeZoneString();
            selectFieldString = selectFieldString.replace("{$place_holder1$}",timeZone);

            FacilioField selectField =  new FacilioField();
            selectField.setName(field.getName());
            selectField.setDisplayName(field.getDisplayName());
            selectField.setColumnName(selectFieldString);
            selectField.setFieldId(field.getFieldId());
            return selectField;
        }

        public FacilioField getTimestampField(FacilioField field) {
            FacilioField selectField = new FacilioField();
            selectField.setName(field.getName());
            selectField.setDisplayName(field.getDisplayName());
            selectField.setColumnName("MIN("+field.getCompleteColumnName()+")");
            selectField.setFieldId(field.getFieldId());
            return selectField;
        }

        private String getTimeZoneString() {
            String db = AwsUtil.getDB();
            if (db.equalsIgnoreCase("mysql")) {
                String timeZone = AccountUtil.getCurrentOrg().getTimezone();
                if(timeZone == null) {
                    timeZone = "UTC";
                }
                return timeZone;
            } else if (db.equalsIgnoreCase("mssql")) {
                return DateTimeUtil.getDateTime().getOffset().toString().equalsIgnoreCase("Z") ? "0": String.valueOf(DateTimeUtil.getDateTime().getOffset().getTotalSeconds());
            }
            throw new IllegalArgumentException("Invalid db");
        }

        public long getAdjustedTimestamp(long time) {
            switch (this) {
                case YEAR:
                    return DateTimeUtil.getYearStartTimeOf(time);
                case MONTH:
                case MONTHANDYEAR:
                    return DateTimeUtil.getMonthStartTimeOf(time);
                case WEEK:
                case WEEKANDYEAR:
                    return DateTimeUtil.getWeekStartTimeOf(time);
                case FULLDATE:
                case WEEKDAY:
                case DAYSOFMONTH:
                    return DateTimeUtil.getDayStartTimeOf(time);
                case HOURSOFDAY:
                case DATEANDTIME:
                case HOURSOFDAYONLY:
                    return DateTimeUtil.getHourStartTimeOf(time);
                case QUARTERLY:
                    return DateTimeUtil.getQuarterStartTimeOf(time);
            }
            return -1;
        }
    }

    public enum SpaceAggregateOperator implements AggregateOperator {

        SITE(21, BaseSpaceContext.SpaceType.SITE.getStringVal(),"SITE_ID"),
        BUILDING(22,BaseSpaceContext.SpaceType.BUILDING.getStringVal(),"BUILDING_ID"),
        FLOOR(23,BaseSpaceContext.SpaceType.FLOOR.getStringVal(),"FLOOR_ID"),
        SPACE(26,BaseSpaceContext.SpaceType.SPACE.getStringVal(),"ID")
        ;

        private int value;
        private String stringValue;
        private String columnName;

        public int getValue() {
            return value;
        }
        public String getStringValue() {
            return stringValue;
        }
        public String getcolumnName() {
            return columnName;
        }
        SpaceAggregateOperator(Integer value,String stringValue,String columnName) {
            this.value = value;
            this.stringValue = stringValue;
            this.columnName = columnName;
        }
        public FacilioField getSelectField(FacilioField field) throws Exception {
//			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
//			FacilioModule baseSpaceModule = modBean.getModule("basespace");
//
//			field.setColumnName(getcolumnName());
//			field.setModule(baseSpaceModule);
//
//			field.setExtendedModule(null);

            return field;
        }
    }

    public enum EnergyPurposeAggregateOperator implements AggregateOperator {

        PURPOSE(24,"Purpose");

        private int value;
        private String stringValue;
        private String columnName;

        public int getValue() {
            return value;
        }
        public String getStringValue() {
            return stringValue;
        }
        public String getcolumnName() {
            return columnName;
        }
        EnergyPurposeAggregateOperator(Integer value,String stringValue) {
            this.value = value;
            this.stringValue = stringValue;
//			this.columnName = columnName;
        }

        public FacilioField getSelectField(FacilioField field) throws Exception {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule baseSpaceModule = modBean.getModule("basespace");

            FacilioField selectField = new FacilioField();
            selectField.setName(field.getName());
            selectField.setDisplayName(field.getDisplayName());
            selectField.setColumnName(getcolumnName());
            selectField.setModule(baseSpaceModule);
            selectField.setFieldId(field.getFieldId());
//			selectField.setExtendedModule(null);

            return selectField;
        }
    }
}
