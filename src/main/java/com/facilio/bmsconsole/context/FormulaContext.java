package com.facilio.bmsconsole.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.fw.BeanFactory;

public class FormulaContext extends ModuleBaseWithCustomFields {

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
			return typeMap;
		}
		public Integer getValue();
		public String getStringValue();
	}
	
	public enum NumberAggregateOperator implements AggregateOperator {
		
		COUNT(1,"count({$place_holder$})"),
		AVERAGE(2,"avg({$place_holder$})"),
		SUM(3,"sum({$place_holder$})"),
		MIN(4,"min({$place_holder$})"),
		MAX(5,"max({$place_holder$})");
		
		private Integer value;
		private String stringValue;
		public Integer getValue() {
			return value;
		}
		public String getStringValue() {
			return stringValue;
		}
		NumberAggregateOperator(Integer value,String stringValue) {
			this.value = value;
			this.stringValue = stringValue;
		}
		public FacilioField getSelectField(FacilioField field) throws Exception {
			String selectFieldString =stringValue.replace("{$place_holder$}", field.getColumnName());
			
			FacilioField selectField = new FacilioField();
			selectField.setColumnName(selectFieldString);
			
			return selectField;
		}
	}
	
	public enum StringAggregateOperator implements AggregateOperator {
		
		COUNT(1,"count({$place_holder$})");
		
		private Integer value;
		private String stringValue;
		public Integer getValue() {
			return value;
		}
		public String getStringValue() {
			return stringValue;
		}
		StringAggregateOperator(Integer value,String stringValue) {
			this.value = value;
			this.stringValue = stringValue;
		}
		public FacilioField getSelectField(FacilioField field) throws Exception {
			String selectFieldString =stringValue.replace("{$place_holder$}", field.getColumnName());
			
			FacilioField selectField = new FacilioField();
			selectField.setColumnName(selectFieldString);
			
			return selectField;
		}
	}
	
	public enum DateAggregateOperator implements AggregateOperator {
		
		COUNT(1,"count","count({$place_holder$})"),
		YEAR(8,"year","DATE_FORMAT(CONVERT_TZ(from_unixtime(floor({$place_holder$}/1000)),@@session.time_zone,'+04:00'),'%Y')"),
		//QUARTERANDYEAR(9,"quarterAndYear",7889229000l),
		MONTHANDYEAR(10,"monthAndYear","DATE_FORMAT(CONVERT_TZ(from_unixtime(floor({$place_holder$}/1000)),@@session.time_zone,'{$place_holder1$}'),'%Y %m')"),
		WEEKANDYEAR(11,"weekAndYear","DATE_FORMAT(CONVERT_TZ(from_unixtime(floor({$place_holder$}/1000)),@@session.time_zone,'{$place_holder1$}'),'%Y %V')"),
		FULLDATE(12,"fullDate","DATE_FORMAT(CONVERT_TZ(from_unixtime(floor({$place_holder$}/1000)),@@session.time_zone,'{$place_holder1$}'),'%Y %m %d'"),
		DATEANDTIME(13,"dateAndTime","DATE_FORMAT(CONVERT_TZ(from_unixtime(floor({$place_holder$}/1000)),@@session.time_zone,'{$place_holder1$}'),,'%Y %m %d %H:%i'"),
		//QUARTER(14,"quarter"),
		MONTH(15,"month","DATE_FORMAT(CONVERT_TZ(from_unixtime(floor({$place_holder$}/1000)),@@session.time_zone,'{$place_holder1$}'),'%m')"),
		WEEK(16,"week","DATE_FORMAT(CONVERT_TZ(from_unixtime(floor({$place_holder$}/1000)),@@session.time_zone,'{$place_holder1$}'),'%V')"),
		WEEKDAY(17,"weekDay","DATE_FORMAT(CONVERT_TZ(from_unixtime(floor({$place_holder$}/1000)),@@session.time_zone,'{$place_holder1$}'),'%w')"),
		DAYSOFMONTH(18,"daysOfMonth","DATE_FORMAT(CONVERT_TZ(from_unixtime(floor({$place_holder$}/1000)),@@session.time_zone,'{$place_holder1$}'),'%d'"),
		HOURSOFDAY(19,"hoursOfDay","DATE_FORMAT(CONVERT_TZ(from_unixtime(floor({$place_holder$}/1000)),@@session.time_zone,'{$place_holder1$}'),'%H'")
		;
		
		private Integer value;
		private String stringValue;
		private String expr;
		public Integer getValue() {
			return value;
		}
		public String getStringValue() {
			return stringValue;
		}
		DateAggregateOperator(Integer value,String stringValue) {
			this.value = value;
			this.stringValue = stringValue;
		}
		DateAggregateOperator(Integer value,String stringValue,String expr) {
			this.value = value;
			this.stringValue = stringValue;
			this.expr = expr;
		}
		public FacilioField getSelectField(FacilioField field) throws Exception {
			System.out.println("SSSsss -- "+DateTimeUtil.getDateTime().getOffset().toString());
			String selectFieldString =expr.replace("{$place_holder$}", field.getColumnName());
			selectFieldString = selectFieldString.replace("{$place_holder1$}", DateTimeUtil.getDateTime().getOffset().toString());
			
			FacilioField selectField = new FacilioField();
			selectField.setColumnName(selectFieldString);
			
			return selectField;
		}
	}
}

