package com.facilio.cb.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import com.facilio.cb.context.ChatBotIntentAction.ResponseType;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.FieldType;

public class ChatBotIntentParam {

	long id = -1;
	long orgId = -1;
	long intentId = -1;
	String name;
	String displayName;
	String askAs;
	FieldType dataType;
	ML_Type mlType;
	int localId;
	Type_Config typeConfig;
	boolean editable;
	boolean fillableByParent;
	String addParamTriggerText;
	String updateParamTriggerText;
	long criteriaId = -1;
	boolean multipleAllowed;
	Date_Period datePeriod;
	
	public boolean isMultipleAllowed() {
		return multipleAllowed;
	}

	public void setMultipleAllowed(boolean multipleAllowed) {
		this.multipleAllowed = multipleAllowed;
	}

	public long getCriteriaId() {
		return criteriaId;
	}

	public void setCriteriaId(long criteriaId) {
		this.criteriaId = criteriaId;
	}
	
	public String getAddParamTriggerText() {
		return addParamTriggerText;
	}

	public void setAddParamTriggerText(String addParamTriggerText) {
		this.addParamTriggerText = addParamTriggerText;
	}

	public String getUpdateParamTriggerText() {
		return updateParamTriggerText;
	}

	public void setUpdateParamTriggerText(String updateParamTriggerText) {
		this.updateParamTriggerText = updateParamTriggerText;
	}

	public boolean isFillableByParent() {
		return fillableByParent;
	}

	public void setFillableByParent(boolean fillableByParent) {
		this.fillableByParent = fillableByParent;
	}

	List<JSONObject> options;
	
	String moduleName;		// for criteria Type
	Criteria criteria;
	
	public int getTypeConfig() {
		if(typeConfig != null) {
			return typeConfig.getIntVal();
		}
		return -1;
	}

	public void setTypeConfig(int typeConfig) {
		this.typeConfig = Type_Config.getAllTypeConfig().get(typeConfig);
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}
	
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public List<JSONObject> getOptions() {
		return options;
	}

	public void setOptions(List<JSONObject> options) {
		this.options = options;
	}
	
	public Criteria getCriteria() {
		return criteria;
	}
	public void setCriteria(Criteria criteria) {
		this.criteria = criteria;
	}
	
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	public int getLocalId() {
		return localId;
	}


	public void setLocalId(int localId) {
		this.localId = localId;
	}


	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}


	public long getOrgId() {
		return orgId;
	}


	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}


	public long getIntentId() {
		return intentId;
	}


	public void setIntentId(long intentId) {
		this.intentId = intentId;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getAskAs() {
		return askAs;
	}


	public void setAskAs(String askAs) {
		this.askAs = askAs;
	}


	public int getDataType() {
		if(dataType != null) {
			return dataType.getTypeAsInt();
		}
		return -1;
	}


	public void setDataType(int dataType) {
		this.dataType = FieldType.getCFType(dataType);
	}


	public int getMlType() {
		if(mlType != null) {
			return mlType.getIntVal();
		}
		return -1;
	}
	
	public ML_Type getMlTypeEnum() {
		return mlType;
	}


	public void setMlType(int mlType) {
		this.mlType = ML_Type.getAllMLTypes().get(mlType);
	}
	
	public Date_Period getDatePeriodEnum() {
		return datePeriod;
	}

	public void setDatePeriod(Date_Period datePeriod) {
		this.datePeriod = datePeriod;
	}


	public enum ML_Type {
		
		PERSON(1, "Person","PERSON",true),
		NORP(2, "Nationalities or religious or political groups","NORP",true),
		FAC(3, "Facility","FAC",true),
		ORG(4, "Companies, agencies, institutions","ORG",true),
		GPE(5, "Countries, cities, states","GPE",true),
		LOC(6, "Non-GPE locations","LOC",true),
		PRODUCT(7, "Product","PRODUCT",true),
		EVENT(8, "Events","EVENT",true),
		WORK_OF_ART(9, "Titles of books, songs","WORK_OF_ART",true),
		LAW(10, "Law","LAW",true),
		LANGUAGE(11, "Language","LANGUAGE",true),
		DATE(12, "Date","DATE",true),
		TIME(13, "Time","TIME",true),
		PERCENT(14, "Percentage","PERCENT",true),
		MONEY(15, "Money","MONEY",true),
		QUANTITY(16, "Measurements","QUANTITY",true),
		ORDINAL(17, "Ordinal","ORDINAL",true),
		CARDINAL(18, "Cardinal","CARDINAL",true),
		
		SUBJECT(19, "Subject","message_subject",false),
		;

		int intVal;
		String name;
		String mlname;
		boolean systemType;

		public String getMLName() {
			return mlname;
		}

		public int getIntVal() {
			return intVal;
		}

		public String getName() {
			return name;
		}
		
		public boolean isSystemType() {
			return systemType;
		}

		private ML_Type(int intVal, String name,String mlname,boolean systemType) {
			this.intVal = intVal;
			this.name = name;
			this.mlname = mlname;
			this.systemType = systemType;
		}

		private static final Map<Integer, ML_Type> optionMap = Collections.unmodifiableMap(initTypeMap());

		private static Map<Integer, ML_Type> initTypeMap() {
			Map<Integer, ML_Type> typeMap = new HashMap<>();

			for (ML_Type type : values()) {
				typeMap.put(type.getIntVal(), type);
			}
			return typeMap;
		}

		public static Map<Integer, ML_Type> getAllMLTypes() {
			return optionMap;
		}
	}
	
	public enum Date_Period {
		
		UPCOMING(1, "Upcoming"),
		PREVIOUS(2, "Prevoius"),
		;

		int intVal;
		String name;

		public int getIntVal() {
			return intVal;
		}

		public String getName() {
			return name;
		}

		private Date_Period(int intVal, String name) {
			this.intVal = intVal;
			this.name = name;
		}

		private static final Map<Integer, Date_Period> optionMap = Collections.unmodifiableMap(initTypeMap());

		private static Map<Integer, Date_Period> initTypeMap() {
			Map<Integer, Date_Period> typeMap = new HashMap<>();

			for (Date_Period type : values()) {
				typeMap.put(type.getIntVal(), type);
			}
			return typeMap;
		}

		public static Map<Integer, Date_Period> getAllTypeConfig() {
			return optionMap;
		}
	}
	
	public enum Type_Config {
		
		MANDATORY(1, "Mandatory"),
		OPTIONAL(2, "optional"),
		AFTER_FILL(3, "After fill"),
		DUMMY(4,"Dummy"),
		;

		int intVal;
		String name;

		public int getIntVal() {
			return intVal;
		}

		public String getName() {
			return name;
		}

		private Type_Config(int intVal, String name) {
			this.intVal = intVal;
			this.name = name;
		}

		private static final Map<Integer, Type_Config> optionMap = Collections.unmodifiableMap(initTypeMap());

		private static Map<Integer, Type_Config> initTypeMap() {
			Map<Integer, Type_Config> typeMap = new HashMap<>();

			for (Type_Config type : values()) {
				typeMap.put(type.getIntVal(), type);
			}
			return typeMap;
		}

		public static Map<Integer, Type_Config> getAllTypeConfig() {
			return optionMap;
		}
	}
}
