package com.facilio.bmsconsole.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.util.DateTimeUtil;
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
		
		public static AggregateOperator getAggregateOperator(int value) {
			return AGGREGATE_OPERATOR_MAP.get(value);
		}
		public FacilioField getSelectField(FacilioField field) throws Exception;
		static final Map<Integer, AggregateOperator> AGGREGATE_OPERATOR_MAP = Collections.unmodifiableMap(initTypeMap());
		static Map<Integer, AggregateOperator> initTypeMap() {
			Map<Integer, AggregateOperator> typeMap = new HashMap<>();
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
		public Integer getValue();
		public String getStringValue();
	}
	
	public enum NumberAggregateOperator implements AggregateOperator {
		
		COUNT(1,"Count","count({$place_holder$})"),
		AVERAGE(2,"Average","avg({$place_holder$})"),
		SUM(3,"Sum","sum({$place_holder$})"),
		MIN(4,"Min","min({$place_holder$})"),
		MAX(5,"Max","max({$place_holder$})");
		
		private Integer value;
		private String stringValue;
		private String expr;
		public Integer getValue() {
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
			
			FacilioField selectField = new FacilioField();
			selectField.setDisplayName(this.name());
			selectField.setColumnName(selectFieldString);
			
			return selectField;
		}
	}
	
	public enum StringAggregateOperator implements AggregateOperator {
		
		ACTUAL(0,"Actual","{$place_holder$}"),
		COUNT(1,"Count","count({$place_holder$})");
		
		private Integer value;
		private String stringValue;
		private String expr;
		
		public Integer getValue() {
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
			
			FacilioField selectField = new FacilioField();
			selectField.setColumnName(selectFieldString);
			
			return selectField;
		}
	}
	
	public enum DateAggregateOperator implements AggregateOperator {
		
		ACTUAL(0,"Actual", "{$place_holder$}",false),
		COUNT(1,"Count","count({$place_holder$})",false),
		YEAR(8,"Yearly","DATE_FORMAT(CONVERT_TZ(from_unixtime(floor({$place_holder$}/1000)),@@session.time_zone,'{$place_holder1$}'),'%Y')",true),
		MONTHANDYEAR(10,"monthAndYear","DATE_FORMAT(CONVERT_TZ(from_unixtime(floor({$place_holder$}/1000)),@@session.time_zone,'{$place_holder1$}'),'%Y %m')", "MMMM yyyy",false),
		WEEKANDYEAR(11,"weekAndYear","DATE_FORMAT(CONVERT_TZ(from_unixtime(floor({$place_holder$}/1000)),@@session.time_zone,'{$place_holder1$}'),'%Y %V')",false),
		FULLDATE(12,"daily","DATE_FORMAT(CONVERT_TZ(from_unixtime(floor({$place_holder$}/1000)),@@session.time_zone,'{$place_holder1$}'),'%Y %m %d')", "EEEE, MMMM dd, yyyy",true),
		DATEANDTIME(13,"dateAndTime","DATE_FORMAT(CONVERT_TZ(from_unixtime(floor({$place_holder$}/1000)),@@session.time_zone,'{$place_holder1$}'),'%Y %m %d %H:%i')",false),
		MONTH(15,"Monthly","DATE_FORMAT(CONVERT_TZ(from_unixtime(floor({$place_holder$}/1000)),@@session.time_zone,'{$place_holder1$}'),'%m')",true),
		WEEK(16,"Week","DATE_FORMAT(CONVERT_TZ(from_unixtime(floor({$place_holder$}/1000)),@@session.time_zone,'{$place_holder1$}'),'%V')",false),
		WEEKDAY(17,"Weekly","DATE_FORMAT(CONVERT_TZ(from_unixtime(floor({$place_holder$}/1000)),@@session.time_zone,'{$place_holder1$}'),'%w')",true),
		DAYSOFMONTH(18,"Daily","DATE_FORMAT(CONVERT_TZ(from_unixtime(floor({$place_holder$}/1000)),@@session.time_zone,'{$place_holder1$}'),'%d')", "EEEE, MMMM dd, yyyy",false),
		HOURSOFDAY(19,"hoursOfDay","DATE_FORMAT(CONVERT_TZ(from_unixtime(floor({$place_holder$}/1000)),@@session.time_zone,'{$place_holder1$}'),'%H')", "EEE, MMM dd, yyyy hh a",false),
		HOURSOFDAYONLY(20,"Hourly","DATE_FORMAT(CONVERT_TZ(from_unixtime(floor({$place_holder$}/1000)),@@session.time_zone,'{$place_holder1$}'),'%Y %m %d %H')", "EEE, MMM dd, yyyy hh a",true)
		;
		
		private Integer value;
		private String stringValue;
		private String expr;
		private String format;
		private boolean isPublic;
		public boolean isPublic() {
			return isPublic;
		}
		public Integer getValue() {
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
			System.out.println("org timeZone -- "+DateTimeUtil.getDateTime().getOffset().toString());
			String selectFieldString =expr.replace("{$place_holder$}", field.getColumnName());
			String timeZone = DateTimeUtil.getDateTime().getOffset().toString().equalsIgnoreCase("Z") ? "+00:00":DateTimeUtil.getDateTime().getOffset().toString(); 
			selectFieldString = selectFieldString.replace("{$place_holder1$}",timeZone);
			
			FacilioField selectField = new FacilioField();
			selectField.setColumnName(selectFieldString);
			
			return selectField;
		}
	}
	
	public enum SpaceAggregateOperator implements AggregateOperator {
		
		SITE(21,BaseSpaceContext.SpaceType.SITE.getStringVal(),"SITE_ID"),
		BUILDING(22,BaseSpaceContext.SpaceType.BUILDING.getStringVal(),"BUILDING_ID"),
		FLOOR(23,BaseSpaceContext.SpaceType.FLOOR.getStringVal(),"FLOOR_ID");
		
		private Integer value;
		private String stringValue;
		private String columnName;
		
		public Integer getValue() {
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
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule baseSpaceModule = modBean.getModule("basespace");
			
			field.setColumnName(getcolumnName());
			field.setModule(baseSpaceModule);
			
			field.setExtendedModule(null);
			
			return field;
		}
	}
	
	public enum EnergyPurposeAggregateOperator implements AggregateOperator {
		
		PURPOSE(24,"Purpose");
		
		private Integer value;
		private String stringValue;
		private String columnName;
		
		public Integer getValue() {
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
			
			field.setColumnName(getcolumnName());
			field.setModule(baseSpaceModule);
			
			field.setExtendedModule(null);
			
			return field;
		}
	}
}

