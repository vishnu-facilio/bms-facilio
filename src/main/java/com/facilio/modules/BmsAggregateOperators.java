package com.facilio.modules;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.collections.UniqueMap;
import com.facilio.db.util.DBConf;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.time.DateTimeUtil;
import com.facilio.workflows.util.ExpressionAggregateInterface;

public class BmsAggregateOperators {
    // Max Operator Code : 29 - Kindly change here if you add new aggregation operator

    public static AggregateOperator getAggregateOperator(int value) {
        return AGGREGATE_OPERATOR_MAP.get(value);
    }
    public static AggregateOperator getAggregateOperator(String value) {
        return AGGREGATE_OPERATOR_STRING_MAP.get(value);
    }
    public static AggregateOperator getRightInclusiveAggr(Integer aggr)
    {
        return AGGREGATE_VS_RIGHT_INCLUSIVE_MAP.get(aggr);
    }
    static final Map<Integer, AggregateOperator> AGGREGATE_VS_RIGHT_INCLUSIVE_MAP = Collections.unmodifiableMap(new UniqueMap<Integer, DateAggregateOperator>(){{
        put(DateAggregateOperator.HOURSOFDAYONLY.getValue(), DateAggregateOperator.HOURLY_RIGHT_INCLUSIVE);
        put(DateAggregateOperator.FULLDATE.getValue(), DateAggregateOperator.DAILY_RIGHT_INCLUSIVE);
        put(DateAggregateOperator.WEEKANDYEAR.getValue(), DateAggregateOperator.WEEKLY_RIGHT_INCLUSIVE);
        put(DateAggregateOperator.MONTHANDYEAR.getValue(), DateAggregateOperator.MONTHLY_RIGHT_INCLUSIVE);
        put(DateAggregateOperator.QUARTERLY.getValue(), DateAggregateOperator.QUARTERLY_RIGHT_INCLUSIVE);
        put(DateAggregateOperator.YEAR.getValue(), DateAggregateOperator.YEARLY_RIGHT_INCLUSIVE);
        put(DateAggregateOperator.HOURSOFDAY.getValue(), DateAggregateOperator.HOURSOFDAY_RIGHT_INCLUSIVE);
        put(DateAggregateOperator.WEEKDAY.getValue(), DateAggregateOperator.WEEKDAY_RIGHT_INCLUSIVE);
        put(DateAggregateOperator.DAYSOFMONTH.getValue(), DateAggregateOperator.DAYSOFMONTH_RIGHT_INCLUSIVE);
    }});
    static final Map<Integer, AggregateOperator> AGGREGATE_OPERATOR_MAP = Collections.unmodifiableMap(initTypeMap());
    static final Map<String, AggregateOperator> AGGREGATE_OPERATOR_STRING_MAP = Collections.unmodifiableMap(initTypeStringMap());
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
        for(AggregateOperator type : SpecialAggregateOperator.values()) {
            typeMap.put(type.getValue(), type);
        }
        return typeMap;
    }
    static Map<String, AggregateOperator> initTypeStringMap() {
        Map<String, AggregateOperator> stringTypeMap = new UniqueMap<>();
        for(AggregateOperator type : CommonAggregateOperator.values()) {
            stringTypeMap.put(type.getStringValue(), type);
        }
        for(AggregateOperator type : NumberAggregateOperator.values()) {
            stringTypeMap.put(type.getStringValue(), type);
        }
        for(AggregateOperator type : StringAggregateOperator.values()) {
            stringTypeMap.put(type.getStringValue(), type);
        }
        for(AggregateOperator type : DateAggregateOperator.values()) {
            stringTypeMap.put(type.getStringValue(), type);
        }
        for(AggregateOperator type : SpaceAggregateOperator.values()) {
            stringTypeMap.put(type.getStringValue(), type);
        }
        for(AggregateOperator type : EnergyPurposeAggregateOperator.values()) {
            stringTypeMap.put(type.getStringValue(), type);
        }
        for(AggregateOperator type : SpecialAggregateOperator.values()) {
            stringTypeMap.put(type.getStringValue(), type);
        }
        return stringTypeMap;
    }

    public enum CommonAggregateOperator implements AggregateOperator {
        ACTUAL(0,"Actual","{$place_holder$}"),
        COUNT(1,"count","count({$place_holder$})") {
            public Object getAggregateResult(List<Map<String, Object>> props,String fieldName) {
                return props.size();
            }

            @Override
            protected FieldType getFieldType() {
                // TODO Auto-generated method stub
                return FieldType.NUMBER;
            }
        },
        DISTINCT(29, "distinct", "distinct({$place_holder$})") {
            @Override
            public Object getAggregateResult(List<Map<String, Object>> props, String fieldName) {
                return props.size();
            }

            @Override
            protected FieldType getFieldType() {
                return FieldType.NUMBER;
            }
        }
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
                case DISTINCT:
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
        @Override
        public Object getAggregateResult(List<Map<String, Object>> props, String fieldName) {
            // TODO Auto-generated method stub
            return null;
        }
        protected FieldType getFieldType() {
            return null;
        }
    }

    public enum NumberAggregateOperator implements AggregateOperator {

        AVERAGE(2,"avg","avg({$place_holder$})") {
            public Object getAggregateResult(List<Map<String, Object>> props,String fieldName) {
                double sum = 0;
                for(Map<String, Object> prop:props) {
                    if(prop.get(fieldName) != null) {
                        sum = sum + (double) prop.get(fieldName);
                    }
                }

                return sum/props.size();
            }
            @Override
            protected FieldType getFieldType() {
                // TODO Auto-generated method stub
                return FieldType.NUMBER;
            }
        },
        SUM(3,"sum","sum({$place_holder$})"){
            public Object getAggregateResult(List<Map<String, Object>> props,String fieldName) {
                double sum = 0;
                for(Map<String, Object> prop:props) {
                    if(prop.get(fieldName) != null) {
                        sum = sum + (double) prop.get(fieldName);
                    }
                }
                return sum;
            }

            @Override
            protected FieldType getFieldType() {
                // TODO Auto-generated method stub
                return FieldType.NUMBER;
            }
        },
        MIN(4,"min","min({$place_holder$})"){
            public Object getAggregateResult(List<Map<String, Object>> props,String fieldName) {
                double min = 0;
                boolean isFirst = true;
                for(Map<String, Object> prop:props) {
                    if(prop.get(fieldName) != null) {
                        double value = (double) prop.get(fieldName);
                        if(isFirst) {
                            isFirst = false;
                            min = value;
                        }
                        min = value < min ? value:min;
                    }
                }
                return min;
            }

            @Override
            protected FieldType getFieldType() {
                // TODO Auto-generated method stub
                return FieldType.NUMBER;
            }
        },
        MAX(5,"max","max({$place_holder$})"){
            public Object getAggregateResult(List<Map<String, Object>> props,String fieldName) {
                double max = 0;
                boolean isFirst = true;
                for(Map<String, Object> prop:props) {
                    if(prop.get(fieldName) != null) {
                        double value = (double) prop.get(fieldName);
                        if(isFirst) {
                            isFirst = false;
                            max = value;
                        }
                        max = value > max ? value:max;
                    }
                }
                return max;
            }

            @Override
            protected FieldType getFieldType() {
                // TODO Auto-generated method stub
                return FieldType.NUMBER;
            }
        },

        RANGE(6,"Range","CONCAT(CAST(MIN({$place_holder$}) as char),',',CAST(AVG({$place_holder$}) as char),',',CAST(MAX({$place_holder$}) as char))")
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
        protected FieldType getFieldType() {
            return null;
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
            selectField.setDataType(FieldType.DECIMAL);
            return selectField;
        }
        @Override
        public Object getAggregateResult(List<Map<String, Object>> props, String fieldName) {
            // TODO Auto-generated method stub
            return null;
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
            selectField.setDataType(FieldType.STRING);
            return selectField;
        }
        @Override
        public Object getAggregateResult(List<Map<String, Object>> props, String fieldName) {
            // TODO Auto-generated method stub
            return null;
        }
    }

    public enum DateAggregateOperator implements AggregateOperator {

        YEAR(8,"Yearly","year.expr",true, 20), //Yearly aggregation
        MONTHANDYEAR(10,"monthAndYear","monthandyear.expr", "MMMM yyyy",false, 15), //Monthly aggregation
        WEEKANDYEAR(11,"weekAndYear","weekandyear.expr",false, 12), //Weekly aggregation
        FULLDATE(12,"daily","fulldate.expr", "EEEE, MMMM dd, yyyy",true, 5), //Daily Aggregation
        DATEANDTIME(13,"dateAndTime","dateandtime.expr",false, 2), //Hourly aggregation
        MONTH(15,"Monthly","month.expr",true, 14), //Monthly aggregation
        WEEK(16,"Week","week.expr",false, 12), //Weekly aggregation
        WEEKDAY(17,"Weekly","weekday.expr",true, 10), //Daily aggregation
        DAYSOFMONTH(18,"Daily","daysofmonth.expr", "EEEE, MMMM dd, yyyy",false, 5), //Daily
        HOURSOFDAY(19,"hoursOfDay","hoursofday.expr", "EEE, MMM dd, yyyy hh a",false, 2), //Hourly
        HOURSOFDAYONLY(20,"Hourly","hoursofdayonly.expr", "EEE, MMM dd, yyyy hh a",true, 1), //Hourly
        QUARTERLY(25,"Quarterly","quaterly.expr", "MMMM yyyy",false, 17), //Quarterly
        HOURLY_RIGHT_INCLUSIVE(30,"Hourly Right Inclusive","hoursofdayonly.expr_right_exclusive", "EEE, MMM dd, yyyy hh a",false, 30), //Hourly
        DAILY_RIGHT_INCLUSIVE(31,"Daily Right Inclusive","fulldate.expr_right_exclusive", "EEEE, MMMM dd, yyyy",false, 31),
        WEEKLY_RIGHT_INCLUSIVE(32,"weekly Right Inclusive","weekandyear.expr_right_exclusive",false, 32),
        MONTHLY_RIGHT_INCLUSIVE(33,"Monthly Right Inclusive","monthandyear.expr_right_exclusive", "MMMM yyyy",false, 33),
        QUARTERLY_RIGHT_INCLUSIVE(34,"Quarterly Right Inclusive","quaterly.expr_right_exclusive", "MMMM yyyy",false, 34),
        YEARLY_RIGHT_INCLUSIVE(35,"Yearly Right Inclusive","year.expr_right_exclusive", false, 35),
        HOURSOFDAY_RIGHT_INCLUSIVE(36,"Hourofday Right Inclusive","hoursofday.expr_right_exclusive", "EEE, MMM dd, yyyy hh a",false, 36),
        WEEKDAY_RIGHT_INCLUSIVE(37,"Weekday Right Inclusive","weekday.expr_right_exclusive", false, 37),
        DAYSOFMONTH_RIGHT_INCLUSIVE(38,"Dayofmonth Right Inclusive","daysofmonth.expr_right_exclusive", "EEEE, MMMM dd, yyyy",false, 38)
        ;

        private int value;
        private String stringValue;
        private String expr;
        private String format;
        private boolean isPublic;
        private int order;
        public int getOrder() {
            return order;
        }
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
        DateAggregateOperator(Integer value,String stringValue,String expr,boolean isPublic, int order) {
            this(value, stringValue, expr, null, isPublic, order);
        }
        DateAggregateOperator(Integer value,String stringValue,String expr, String format,boolean isPublic, int order) {
            this.value = value;
            this.stringValue = stringValue;
            this.expr = DBConf.getInstance().getQuery(expr);
            this.format = format;
            this.isPublic = isPublic;
            this.order = order;
        }
        public FacilioField getSelectField(FacilioField field) throws Exception {
            String selectFieldString = expr.replace("{$place_holder$}", field.getCompleteColumnName());
            String timeZone = getTimeZoneString();
            // temp fix for aster account [There is an issue with the budget module transactions not rolling up correctly due to the site timezone being applied in the rollup to fix this manually overriding org timezone]
            if(AccountUtil.getCurrentOrg().getOrgId() == 583 && field.getName().equals("transactionDate")) {
                timeZone = AccountUtil.getCurrentOrg().getTimezone();
            }
            selectFieldString = selectFieldString.replace("{$place_holder1$}",timeZone);

            FacilioField selectField =  new FacilioField();
            selectField.setName(field.getName());
            selectField.setDisplayName(field.getDisplayName());
            selectField.setColumnName(selectFieldString);
            selectField.setFieldId(field.getFieldId());
            selectField.setDataType(FieldType.STRING);
            return selectField;
        }

        public FacilioField getTimestampField(FacilioField field) {
            FacilioField selectField = new FacilioField();
            selectField.setName(field.getName());
            selectField.setDisplayName(field.getDisplayName());
            selectField.setColumnName("MIN("+field.getCompleteColumnName()+")");
            selectField.setFieldId(field.getFieldId());
            selectField.setDataType(FieldType.DATE_TIME);
            return selectField;
        }

        private String getTimeZoneString() {
            String db = DBConf.getInstance().getDBName();
            if (db.equalsIgnoreCase("mysql")) {
                String timeZone = AccountUtil.getCurrentAccount().getTimeZone();
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
                case YEARLY_RIGHT_INCLUSIVE:
                    return DateTimeUtil.getYearStartTimeOf(time);
                case MONTH:
                case MONTHANDYEAR:
                case MONTHLY_RIGHT_INCLUSIVE:
                    return DateTimeUtil.getMonthStartTimeOf(time);
                case WEEK:
                case WEEKANDYEAR:
                case WEEKLY_RIGHT_INCLUSIVE:
                    return DateTimeUtil.getWeekStartTimeOf(time);
                case FULLDATE:
                case WEEKDAY:
                case DAYSOFMONTH:
                case WEEKDAY_RIGHT_INCLUSIVE:
                case DAYSOFMONTH_RIGHT_INCLUSIVE:
                case DAILY_RIGHT_INCLUSIVE:
                    return DateTimeUtil.getDayStartTimeOf(time);
                case HOURSOFDAY:
                case DATEANDTIME:
                case HOURSOFDAYONLY:
                case HOURLY_RIGHT_INCLUSIVE:
                case HOURSOFDAY_RIGHT_INCLUSIVE:
                    return DateTimeUtil.getHourStartTimeOf(time);
                case QUARTERLY:
                case QUARTERLY_RIGHT_INCLUSIVE:
                    return DateTimeUtil.getQuarterStartTimeOf(time);

            }
            return -1;
        }
        @Override
        public Object getAggregateResult(List<Map<String, Object>> props, String fieldName) {
            // TODO Auto-generated method stub
            return null;
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
        @Override
        public Object getAggregateResult(List<Map<String, Object>> props, String fieldName) {
            // TODO Auto-generated method stub
            return null;
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
        @Override
        public Object getAggregateResult(List<Map<String, Object>> props, String fieldName) {
            // TODO Auto-generated method stub
            return null;
        }
    }
    public enum SpecialAggregateOperator implements AggregateOperator, ExpressionAggregateInterface {
        FIRST_VALUE(27,"[0]","{$place_holder$}") {
            public Object getAggregateResult(List<Map<String, Object>> props,String fieldName) {
                return props.get(0).get(fieldName);
            }
        },
        LAST_VALUE(28,"lastValue","{$place_holder$}") {
            public Object getAggregateResult(List<Map<String, Object>> props,String fieldName) {
                if(props != null && props.isEmpty()) {
                    return props.get(props.size()-1).get(fieldName);
                }
                return null;
            }
        },
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
        SpecialAggregateOperator(Integer value,String stringValue,String expr) {
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
            selectField.setDataType(field.getDataType());
            return selectField;
        }
        @Override
        public Object getAggregateResult(List<Map<String, Object>> props, String fieldName) {
            return null;
        }
    }
}
