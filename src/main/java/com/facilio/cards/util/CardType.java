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
			"</workflow>",true),
	
	ENERGY_READING_CARD(2,"energyDataCard","<workflow> \n" + 
			"	<parameter name=\"baseSpaceId\" type=\"Number\"/> 	\n" + 
			"	<parameter name=\"dateOperator\" type=\"Number\"/> 	\n" + 
			"	<parameter name=\"dateValue\" type=\"Number\"/> 	\n" + 
			"	<expression name=\"pow\"> 		 		 		\n" + 
			"		<function>readings.getEnergyReading(baseSpaceId,dateOperator,dateValue)</function>	 	\n" + 
			"	</expression>\n" + 
			"	 <result>pow</result>\n" + 
			"</workflow>",true),
	
	FAHU_STATUS_CARD(3,"fahuStatusCard","<workflow> 	\n" + 
			"	<parameter name=\"parentId\" type = \"Number\"/> \n" + 
			"	<expression name=\"name\"> 		\n" + 
			"		<module name=\"resource\"/> 		\n" + 
			"		<criteria pattern=\"1\"> 			\n" + 
			"			<condition sequence=\"1\">id`=`${parentId}</condition> 		\n" + 
			"		</criteria> 		\n" + 
			"		<field name=\"name\" aggregate = \"[0]\"/> 	\n" + 
			"	</expression> \n" + 
			"	<expression name=\"runStatus\"> 		\n" + 
			"		<module name=\"prefilterstatus\"/> 		\n" + 
			"		<criteria pattern=\"1\"> 			\n" + 
			"			<condition sequence=\"1\">parentId`=`${parentId}</condition> 		\n" + 
			"		</criteria> 		\n" + 
			"		<field name=\"runstatus\" aggregate = \"lastValue\"/> 	\n" + 
			"	</expression> \n" + 
			"	<expression name=\"valveFeedback\"> 		\n" + 
			"		<module name=\"prefilterstatus\"/> 		\n" + 
			"		<criteria pattern=\"1\"> 			\n" + 
			"			<condition sequence=\"1\">parentId`=`${parentId}</condition> 		\n" + 
			"		</criteria> 		\n" + 
			"		<field name=\"valvefeedback\" aggregate = \"lastValue\"/> 	\n" + 
			"	</expression> \n" + 
			"	<expression name=\"tripStatus\"> 		\n" + 
			"		<module name=\"bagfilterstatus\"/> 		\n" + 
			"		<criteria pattern=\"1\"> 			\n" + 
			"			<condition sequence=\"1\">parentId`=`${parentId}</condition> 		\n" + 
			"		</criteria> 		\n" + 
			"		<field name=\"tripstatus\" aggregate = \"lastValue\"/> 	\n" + 
			"	</expression> \n" + 
			"	<expression name=\"autoStatus\"> 		\n" + 
			"		<module name=\"prefilterstatus\"/> 		\n" + 
			"		<criteria pattern=\"1\"> 			\n" + 
			"			<condition sequence=\"1\">parentId`=`${parentId}</condition> 		\n" + 
			"		</criteria> 		\n" + 
			"		<field name=\"automanualstatus\" aggregate = \"lastValue\"/> 	\n" + 
			"	</expression> \n" + 
			"</workflow>",false),
	
	IMAGE_CARD(4,"imagecard","<workflow> 	\n" + 
			"	<parameter name=\"photoId\" type = \"Number\"/> \n" + 
			"	<expression name=\"privateUrl\"> 		\n" + 
			"		<function>default.getFilePrivateUrl(photoId)</function> 	\n" + 
			"	</expression> \n" + 
			"	<result>privateUrl</result>\n" + 
			"</workflow>",true),
	FAHU_STATUS_CARD_1(5,"fahuStatusCard1","<workflow> 	\n" + 
			"	<parameter name=\"parentId\" type = \"Number\"/> \n" + 
			"	<expression name=\"name\"> 		\n" + 
			"		<module name=\"resource\"/> 		\n" + 
			"		<criteria pattern=\"1\"> 			\n" + 
			"			<condition sequence=\"1\">id`=`${parentId}</condition> 		\n" + 
			"		</criteria> 		\n" + 
			"		<field name=\"name\" aggregate = \"[0]\"/> 	\n" + 
			"	</expression> \n" + 
			"	<expression name=\"runStatus\"> 		\n" + 
			"		<module name=\"runstatus\"/> 		\n" + 
			"		<criteria pattern=\"1\"> 			\n" + 
			"			<condition sequence=\"1\">parentId`=`${parentId}</condition> 		\n" + 
			"		</criteria> 		\n" + 
			"		<field name=\"runstatus\" aggregate = \"lastValue\"/> 	\n" + 
			"	</expression> \n" + 
			"	<expression name=\"valveFeedback\"> 		\n" + 
			"		<module name=\"runstatus\"/> 		\n" + 
			"		<criteria pattern=\"1\"> 			\n" + 
			"			<condition sequence=\"1\">parentId`=`${parentId}</condition> 		\n" + 
			"		</criteria> 		\n" + 
			"		<field name=\"valvecommand\" aggregate = \"lastValue\"/> 	\n" + 
			"	</expression> \n" + 
			"	<expression name=\"tripStatus\"> 		\n" + 
			"		<module name=\"runstatus\"/> 		\n" + 
			"		<criteria pattern=\"1\"> 			\n" + 
			"			<condition sequence=\"1\">parentId`=`${parentId}</condition> 		\n" + 
			"		</criteria> 		\n" + 
			"		<field name=\"tripstatus\" aggregate = \"lastValue\"/> 	\n" + 
			"	</expression> \n" + 
			"	<expression name=\"autoStatus\"> 		\n" + 
			"		<module name=\"automanualstatus\"/> 		\n" + 
			"		<criteria pattern=\"1\"> 			\n" + 
			"			<condition sequence=\"1\">parentId`=`${parentId}</condition> 		\n" + 
			"		</criteria> 		\n" + 
			"		<field name=\"automanualstatus\" aggregate = \"lastValue\"/> 	\n" + 
			"	</expression> \n" + 
			"</workflow>",false),
	
	FAHU_STATUS_CARD_2(6,"fahuStatusCard2","<workflow> 	\n" + 
			"	<parameter name=\"parentId\" type = \"Number\"/> \n" + 
			"	<expression name=\"name\"> 		\n" + 
			"		<module name=\"resource\"/> 		\n" + 
			"		<criteria pattern=\"1\"> 			\n" + 
			"			<condition sequence=\"1\">id`=`${parentId}</condition> 		\n" + 
			"		</criteria> 		\n" + 
			"		<field name=\"name\" aggregate = \"[0]\"/> 	\n" + 
			"	</expression> \n" + 
			"	<expression name=\"runStatus\"> 		\n" + 
			"		<module name=\"runstatus\"/> 		\n" + 
			"		<criteria pattern=\"1\"> 			\n" + 
			"			<condition sequence=\"1\">parentId`=`${parentId}</condition> 		\n" + 
			"		</criteria> 		\n" + 
			"		<field name=\"runstatus\" aggregate = \"lastValue\"/> 	\n" + 
			"	</expression> \n" + 
			"	<expression name=\"valveFeedback\"> 		\n" + 
			"		<module name=\"supplyairtemperature\"/> 		\n" + 
			"		<criteria pattern=\"1\"> 			\n" + 
			"			<condition sequence=\"1\">parentId`=`${parentId}</condition> 		\n" + 
			"		</criteria> 		\n" + 
			"		<field name=\"valvefeedback\" aggregate = \"lastValue\"/> 	\n" + 
			"	</expression> \n" + 
			"	<expression name=\"tripStatus\"> 		\n" + 
			"		<module name=\"runstatus\"/> 		\n" + 
			"		<criteria pattern=\"1\"> 			\n" + 
			"			<condition sequence=\"1\">parentId`=`${parentId}</condition> 		\n" + 
			"		</criteria> 		\n" + 
			"		<field name=\"tripstatus\" aggregate = \"lastValue\"/> 	\n" + 
			"	</expression> \n" + 
			"	<expression name=\"autoStatus\"> 		\n" + 
			"		<module name=\"runstatus\"/> 		\n" + 
			"		<criteria pattern=\"1\"> 			\n" + 
			"			<condition sequence=\"1\">parentId`=`${parentId}</condition> 		\n" + 
			"		</criteria> 		\n" + 
			"		<field name=\"automanualstatus\" aggregate = \"lastValue\"/> 	\n" + 
			"	</expression> \n" + 
			"</workflow>",false),
	
	READING_GAUGE_CARD(7,"readingGaugeCard","<workflow>\n" + 
			"	<parameter name=\"dateOperator\" type=\"String\"/> \n" + 
			"	<parameter name=\"moduleName\" type=\"String\"/>   \n" + 
			"	<parameter name=\"parentId\" type=\"Number\"/>     \n" + 
			"	<parameter name=\"fieldName\" type=\"String\"/>   \n" + 
			"	<parameter name=\"aggregateOpperator\" type=\"String\"/>   \n" + 
			"	<parameter name=\"moduleName1\" type=\"String\"/>   \n" + 
			"	<parameter name=\"parentId1\" type=\"Number\"/>     \n" + 
			"	<parameter name=\"fieldName1\" type=\"String\"/>   \n" + 
			"	<parameter name=\"aggregateOpperator1\" type=\"String\"/>\n" + 
			"	<parameter name=\"constant\" type=\"Number\"/>\n" + 
			"   <expression name=\"val1\">\n" + 
			"      <module name=\"${moduleName}\" /> \n" + 
			"      <criteria pattern=\"1 and 2\"> \n" + 
			"         <condition sequence=\"1\">parentId`=`${parentId}</condition> \n" + 
			"         <condition sequence=\"2\">TTIME`${dateOperator}`</condition>  \n" + 
			"      </criteria>  \n" + 
			"      <field name=\"${fieldName}\" aggregate=\"${aggregateOpperator}\" /> \n" + 
			"   </expression>\n" + 
			"   <conditions>\n" + 
			"		<if criteria=\"constant<0\">\n" + 
			"			<expression name=\"constant\">\n" + 
			"      			<module name=\"${moduleName1}\" /> \n" + 
			"      			<criteria pattern=\"1 and 2\"> \n" + 
			"         			<condition sequence=\"1\">parentId`=`${parentId1}</condition> \n" + 
			"         			<condition sequence=\"2\">TTIME`${dateOperator}`</condition>  \n" + 
			"     			 </criteria>  \n" + 
			"     			 <field name=\"${fieldName1}\" aggregate=\"${aggregateOpperator1}\" /> \n" + 
			"   			</expression>\n" + 
			"		</if>\n" + 
			"   </conditions>\n" + 
			"   <conditions>\n" + 
			"		<if criteria=\"constant IS NOT NULL\">\n" + 
			"			<conditions>\n" + 
			"				<if criteria=\"val1 IS NOT NULL\">\n" + 
			"					<conditions>\n" + 
			"						<if criteria=\"constant > 0\">\n" + 
			"							<expression name=\"percent\">\n" + 
			"    							<expr>(val1/constant)*100</expr>\n" + 
			"    						</expression>\n" + 
			"						</if>\n" + 
			"					</conditions>\n" + 
			"				</if>\n" + 
			"			</conditions>\n" + 
			"		</if>\n" + 
			"	</conditions>\n" + 
			"     <conditions>\n" + 
			"    	<if criteria=\"percent IS NOT NULL\">\n" + 
			"    		<conditions>\n" + 
			"    			<if criteria=\"percent>100\">\n" + 
			"    				<expression name=\"percent\">\n" + 
			"    					<expr>percent-100</expr>\n" + 
			"    				</expression>\n" + 
			"    				<expression name=\"isPlus\">\n" + 
			"    					<constant>true</constant>\n" + 
			"    				</expression>\n" + 
			"    			</if>\n" + 
			"    		</conditions>\n" + 
			"    	</if>\n" + 
			"    </conditions>\n" + 
			"</workflow>",false),
	;
	
	private Integer value;
	private String name;
	private String workflow;
	private boolean isSingleResultWorkFlow;
	
	public boolean isSingleResultWorkFlow() {
		return isSingleResultWorkFlow;
	}
	public void setSingleResultWorkFlow(boolean isSingleResultWorkFlow) {
		this.isSingleResultWorkFlow = isSingleResultWorkFlow;
	}
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
	
	CardType(Integer value,String name,String workflow,boolean isSingleResultWorkFlow) {
		this.value = value;
		this.name= name;
		this.workflow = workflow;
		this.isSingleResultWorkFlow = isSingleResultWorkFlow;
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
