package com.facilio.bmsconsole.context;

import java.util.Collections;
import java.util.Map;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.NumberField;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.collections.UniqueMap;
import com.facilio.fw.BeanFactory;

public class FormulaContext {

	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	private long orgId = -1;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private long moduleId = -1;
	public long getModuleId() throws Exception {
		if(moduleId == -1 && getModule() != null) {
			return getModule().getModuleId();
		}
		return moduleId;
	}
	
	private Long selectFieldId;
	private int aggregateOperationValue;
	private Long criteriaId;
	
	public AggregateOperator getAggregateOperator() {
		return AggregateOperator.getAggregateOperator(getAggregateOperationValue());
	}
	public Long getSelectFieldId() {
		return selectFieldId;
	}
	public void setSelectFieldId(Long selectFieldId) {
		this.selectFieldId = selectFieldId;
	}
	public int getAggregateOperationValue() {
		return aggregateOperationValue;
	}
	public void setAggregateOperationValue(int aggregateOperationValue) {
		this.aggregateOperationValue = aggregateOperationValue;
	}
	public Long getCriteriaId() {
		return criteriaId;
	}
	public void setCriteriaId(Long criteriaId) {
		this.criteriaId = criteriaId;
	}
	
	Criteria criteria;
	public Criteria getCriteria() throws Exception {
		if(criteria != null) {
			return criteria;
		}
		else {
			if(criteriaId == null) {
				return null;
			}
			criteria = CriteriaAPI.getCriteria(AccountUtil.getCurrentOrg().getId(), criteriaId);
		}
		return criteria;
	}
	
	public void setCriteria(Criteria criteria) {
		this.criteria = criteria;
	}

	private String moduleName;
	
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	public FacilioModule getModule() throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		return modBean.getModule(moduleName);
	}
	
	public FacilioField getSelectField() throws Exception {
		if(this.getSelectFieldId() != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioField facilioField = getAggregateOperator().getSelectField(modBean.getField(this.getSelectFieldId()));
			facilioField.setName("formulaValue");
			return facilioField;
		}
		return null;
	}
	
	public interface AggregateOperator {
		
		// Max Operator Code : 25 - Kindly change here if you add new operator
		
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
	}
	
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
			String selectFieldString = expr.replace("{$place_holder$}", field.getColumnName());
			FacilioField selectField = field.clone();
			selectField.setColumnName(selectFieldString);
			
			if (this == CommonAggregateOperator.COUNT) { //Temp Fix. Have to look into this
				selectField.setModule(null);
				selectField.setExtendedModule(null);
			}
			
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
			return selectField;
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
			String selectFieldString = expr.replace("{$place_holder$}", field.getColumnName());
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
			String selectFieldString =expr.replace("{$place_holder$}", field.getColumnName());
			
			FacilioField selectField =  new FacilioField();
			selectField.setName(field.getName());
			selectField.setDisplayName(field.getDisplayName());
			selectField.setColumnName(selectFieldString);
			selectField.setFieldId(field.getFieldId());
			return selectField;
		}
	}
	
	public enum DateAggregateOperator implements AggregateOperator {
		
		YEAR(8,"Yearly","DATE_FORMAT(CONVERT_TZ(from_unixtime(floor({$place_holder$}/1000)),@@session.time_zone,'{$place_holder1$}'),'%Y')",true), //Yearly aggregation
		MONTHANDYEAR(10,"monthAndYear","DATE_FORMAT(CONVERT_TZ(from_unixtime(floor({$place_holder$}/1000)),@@session.time_zone,'{$place_holder1$}'),'%Y %m')", "MMMM yyyy",false), //Monthly aggregation
		WEEKANDYEAR(11,"weekAndYear","DATE_FORMAT(CONVERT_TZ(from_unixtime(floor({$place_holder$}/1000)),@@session.time_zone,'{$place_holder1$}'),'%Y %V')",false), //Weekly aggregation 
		FULLDATE(12,"daily","DATE_FORMAT(CONVERT_TZ(from_unixtime(floor({$place_holder$}/1000)),@@session.time_zone,'{$place_holder1$}'),'%Y %m %d')", "EEEE, MMMM dd, yyyy",true), //Daily Aggregation
		DATEANDTIME(13,"dateAndTime","DATE_FORMAT(CONVERT_TZ(from_unixtime(floor({$place_holder$}/1000)),@@session.time_zone,'{$place_holder1$}'),'%Y %m %d %H:%i')",false), //Hourly aggregation
		MONTH(15,"Monthly","DATE_FORMAT(CONVERT_TZ(from_unixtime(floor({$place_holder$}/1000)),@@session.time_zone,'{$place_holder1$}'),'%m')",true), //Monthly aggregation
		WEEK(16,"Week","DATE_FORMAT(CONVERT_TZ(from_unixtime(floor({$place_holder$}/1000)),@@session.time_zone,'{$place_holder1$}'),'%V')",false), //Weekly aggregation
		WEEKDAY(17,"Weekly","DATE_FORMAT(CONVERT_TZ(from_unixtime(floor({$place_holder$}/1000)),@@session.time_zone,'{$place_holder1$}'),'%w')",true), //Daily aggregation
		DAYSOFMONTH(18,"Daily","DATE_FORMAT(CONVERT_TZ(from_unixtime(floor({$place_holder$}/1000)),@@session.time_zone,'{$place_holder1$}'),'%d')", "EEEE, MMMM dd, yyyy",false), //Daily
		HOURSOFDAY(19,"hoursOfDay","DATE_FORMAT(CONVERT_TZ(from_unixtime(floor({$place_holder$}/1000)),@@session.time_zone,'{$place_holder1$}'),'%H')", "EEE, MMM dd, yyyy hh a",false), //Hourly
		HOURSOFDAYONLY(20,"Hourly","DATE_FORMAT(CONVERT_TZ(from_unixtime(floor({$place_holder$}/1000)),@@session.time_zone,'{$place_holder1$}'),'%Y %m %d %H')", "EEE, MMM dd, yyyy hh a",true), //Hourly
		QUARTERLY(25,"Quarterly","YEAR(CONVERT_TZ(from_unixtime(floor({$place_holder$}/1000)),@@session.time_zone,'{$place_holder1$}')), QUARTER(CONVERT_TZ(from_unixtime(floor({$place_holder$}/1000)),@@session.time_zone,'{$place_holder1$}'))", "MMMM yyyy",false), //Quarterly
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
			this.value = value;
			this.stringValue = stringValue;
			this.expr = expr;
			this.isPublic = isPublic;
		}
		DateAggregateOperator(Integer value,String stringValue,String expr, String format,boolean isPublic) {
			this.value = value;
			this.stringValue = stringValue;
			this.expr = expr;
			this.format = format;
			this.isPublic = isPublic;
		}
		public FacilioField getSelectField(FacilioField field) throws Exception {
			String selectFieldString =expr.replace("{$place_holder$}", field.getColumnName());
			String timeZone = DateTimeUtil.getDateTime().getOffset().toString().equalsIgnoreCase("Z") ? "+00:00":DateTimeUtil.getDateTime().getOffset().toString(); 
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
		
		SITE(21,BaseSpaceContext.SpaceType.SITE.getStringVal(),"SITE_ID"),
		BUILDING(22,BaseSpaceContext.SpaceType.BUILDING.getStringVal(),"BUILDING_ID"),
		FLOOR(23,BaseSpaceContext.SpaceType.FLOOR.getStringVal(),"FLOOR_ID");
		
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
			selectField.setExtendedModule(null);
			
			return selectField;
		}
	}
}

