package com.facilio.cards.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum CardType {
	
	READING_CARD(1,"readingcard","<workflow>\n" + 
			"	<parameter name=\"moduleName\" type=\"String\"/> 	\n" + 
			"	<parameter name=\"parentId\" type=\"Number\"/> 	\n" + 
			"	<parameter name=\"dateOperator\" type=\"String\"/> 	\n" + 
			"	<parameter name=\"fieldName\" type=\"String\"/> 	\n" + 
			"	<parameter name=\"aggregateOpperator\" type=\"String\"/> 	\n" + 
			"   <expression name=\"a\">\n" + 
			"      <module name=\"${moduleName}\" />\n" + 
			"      <criteria pattern=\"1 and 2\">\n" + 
			"         <condition sequence=\"1\">parentId`=`${parentId}</condition>\n" + 
			"         <condition sequence=\"2\">TTIME`${dateOperator}`</condition>\n" + 
			"      </criteria>\n" + 
			"      <field name=\"${fieldName}\" aggregate=\"${aggregateOpperator}\" />\n" + 
			"   </expression>\n" + 
			"   <result>a</result>\n" + 
			"</workflow>"),
	
	ENERGY_READING_CARD(2,"energyDataCard","<workflow> \n" + 
			"	<parameter name=\"baseSpaceId\" type=\"Number\"/> 	\n" + 
			"	<parameter name=\"dateOperator\" type=\"Number\"/> 	\n" + 
			"	<parameter name=\"dateValue\" type=\"Number\"/> 	\n" + 
			"	<expression name=\"pow\"> 		 		 		\n" + 
			"		<function>readings.getEnergyReading(baseSpaceId,dateOperator,dateValue)</function>	 	\n" + 
			"	</expression>\n" + 
			"	 <result>pow</result>\n" + 
			"</workflow>"),
	;
	
	private Integer value;
	private String name;
	private String workflow;
	
	public Integer getValue() {
		return value;
	}
	public void setValue(Integer value) {
		this.value = value;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getWorkflow() {
		return workflow;
	}
	public void setWorkflow(String workflow) {
		this.workflow = workflow;
	}
	
	CardType(Integer value,String name,String workflow) {
		this.value = value;
		this.name= name;
		this.workflow = workflow;
	}
	public static CardType getCardType(String name) {
		return CARD_TYPE_BY_NAME.get(name);
	}
	public static final Map<String, CardType> CARD_TYPE_BY_NAME = Collections.unmodifiableMap(initTypeMap());
	private static Map<String, CardType> initTypeMap() {
		Map<String, CardType> typeMap = new HashMap<>();
		for(CardType type : CardType.values()) {
			typeMap.put(type.getName(), type);
		}
		return typeMap;
	}
	
	private static final Map<Integer, CardType> CARD_TYPE_BY_VALUE = Collections.unmodifiableMap(initTypeMapByValue());
	private static Map<Integer, CardType> initTypeMapByValue() {
		Map<Integer, CardType> typeMap = new HashMap<>();
		for(CardType type : CardType.values()) {
			typeMap.put(type.getValue(), type);
		}
		return typeMap;
	}
	
	public static CardType valueOf(int value) {
		return CARD_TYPE_BY_VALUE.get(value);
	}
}
