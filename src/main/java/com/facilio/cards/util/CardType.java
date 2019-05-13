package com.facilio.cards.util;

import com.facilio.bmsconsole.context.TicketStatusContext;

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
			"    <parameter name=\"maxPercentage\" type=\"Number\"/>\n" + 
			"    <parameter name=\"maxConstant\" type=\"Number\"/>\n" + 
			"   <expression name=\"val1\">\n" + 
			"      <module name=\"${moduleName}\" /> \n" + 
			"      <criteria pattern=\"1 and 2\"> \n" + 
			"         <condition sequence=\"1\">parentId`=`${parentId}</condition> \n" + 
			"         <condition sequence=\"2\">TTIME`${dateOperator}`</condition>  \n" + 
			"      </criteria>  \n" + 
			"      <field name=\"${fieldName}\" aggregate=\"${aggregateOpperator}\" /> \n" + 
			"   </expression>\n" + 
			"   <conditions>\n" + 
			"		<if criteria=\"constant &lt; 0\">\n" + 
			"			<expression name=\"constant\">\n" + 
			"      			<module name=\"${moduleName1}\" /> \n" + 
			"      			<criteria pattern=\"1 and 2\"> \n" + 
			"         			<condition sequence=\"1\">parentId`=`${parentId1}</condition> \n" + 
			"         			<condition sequence=\"2\">TTIME`${dateOperator}`</condition>  \n" + 
			"     			 </criteria>  \n" + 
			"     			 <field name=\"${fieldName1}\" aggregate=\"${aggregateOpperator1}\" /> \n" + 
			"   			</expression>\n" + 
			"   			<expression name=\"unit1\">\n" + 
			"    			<function>default.getUnit(fieldName1,moduleName1)</function>\n" + 
			"			</expression>\n" + 
			"		</if>\n" + 
			"   </conditions>\n" + 
			"   <conditions>\n" + 
			"		<if criteria=\"constant IS NOT NULL\">\n" + 
			"			<conditions>\n" + 
			"				<if criteria=\"val1 IS NOT NULL\">\n" + 
			"					<conditions>\n" + 
			"						<if criteria=\"constant &gt; 0\">\n" + 
			"							<expression name=\"percent\">\n" + 
			"    							<expr>(val1/constant)*100</expr>\n" + 
			"    						</expression>\n" + 
			"						</if>\n" + 
			"					</conditions>\n" + 
			"				</if>\n" + 
			"			</conditions>\n" + 
			"			<conditions>\n" + 
			"				<if criteria=\"maxPercentage IS NOT NULL\">\n" + 
			"					<conditions>\n" + 
			"						<if criteria=\"maxPercentage &gt; 0\">\n" + 
			"							<expression name=\"maxConstant\">\n" + 
			"    							<expr>constant+(constant*maxPercentage/100)</expr>\n" + 
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
			"    			<if criteria=\"percent &gt; 100\">\n" + 
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
			"    <expression name=\"unit\">\n" + 
			"    		<function>default.getUnit(fieldName,moduleName)</function>\n" + 
			"	</expression>\n" + 
			"</workflow>",false),
	
	FAHU_STATUS_CARD_3(8,"fahuStatusCard3","<workflow> 	\n" + 
			"	<parameter name=\"parentId\" type = \"Number\"/> \n" + 
			"	<expression name=\"name\"> 		\n" + 
			"		<module name=\"resource\"/> 		\n" + 
			"		<criteria pattern=\"1\"> 			\n" + 
			"			<condition sequence=\"1\">id`=`${parentId}</condition> 		\n" + 
			"		</criteria> 		\n" + 
			"		<field name=\"name\" aggregate = \"[0]\"/> 	\n" + 
			"	</expression> \n" + 
			"	<expression name=\"runStatus\"> 		\n" + 
			"		<module name=\"automanualstatus\"/> 		\n" + 
			"		<criteria pattern=\"1\"> 			\n" + 
			"			<condition sequence=\"1\">parentId`=`${parentId}</condition> 		\n" + 
			"		</criteria> 		\n" + 
			"		<field name=\"runstatus\" aggregate = \"lastValue\"/> 	\n" + 
			"	</expression> \n" + 
			"	<expression name=\"valveFeedback\"> 		\n" + 
			"		<module name=\"returntemperature\"/> 		\n" + 
			"		<criteria pattern=\"1\"> 			\n" + 
			"			<condition sequence=\"1\">parentId`=`${parentId}</condition> 		\n" + 
			"		</criteria> 		\n" + 
			"		<field name=\"valvecommand\" aggregate = \"lastValue\"/> 	\n" + 
			"	</expression> \n" + 
			"	<expression name=\"tripStatus\"> 		\n" + 
			"		<module name=\"returntemperature\"/> 		\n" + 
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
	FAHU_STATUS_CARD_NEW(9,"fahuStatusCardNew","<workflow> 	\n" + 
			"	<parameter name=\"parentId\" type = \"Number\"/> \n" + 
			"	<parameter name=\"runStatusModule\" type = \"String\"/> \n" + 
			"	<parameter name=\"runStatusField\" type = \"String\"/> \n" + 
			"	<parameter name=\"valveFeedbackModule\" type = \"String\"/> \n" + 
			"	<parameter name=\"valveFeedbackField\" type = \"String\"/> \n" + 
			"	<parameter name=\"tripStatusModule\" type = \"String\"/> \n" + 
			"	<parameter name=\"tripStatusField\" type = \"String\"/> \n" + 
			"	<parameter name=\"autoStatusModule\" type = \"String\"/> \n" + 
			"	<parameter name=\"autoStatusField\" type = \"String\"/> \n" + 
			"	<expression name=\"name\"> 		\n" + 
			"		<module name=\"resource\"/> 		\n" + 
			"		<criteria pattern=\"1\"> 			\n" + 
			"			<condition sequence=\"1\">id`=`${parentId}</condition> 		\n" + 
			"		</criteria> 		\n" + 
			"		<field name=\"name\" aggregate = \"[0]\"/> 	\n" + 
			"	</expression> \n" + 
			"	<expression name=\"runStatus\"> 		\n" + 
			"		<module name=\"${runStatusModule}\"/> 		\n" + 
			"		<criteria pattern=\"1\"> 			\n" + 
			"			<condition sequence=\"1\">parentId`=`${parentId}</condition> 		\n" + 
			"		</criteria> 		\n" + 
			"		<field name=\"${runStatusField}\" aggregate = \"lastValue\"/> 	\n" + 
			"	</expression> \n" + 
			"	<expression name=\"valveFeedback\"> 		\n" + 
			"		<module name=\"${valveFeedbackModule}\"/> 		\n" + 
			"		<criteria pattern=\"1\"> 			\n" + 
			"			<condition sequence=\"1\">parentId`=`${parentId}</condition> 		\n" + 
			"		</criteria> 		\n" + 
			"		<field name=\"${valveFeedbackField}\" aggregate = \"lastValue\"/> 	\n" + 
			"	</expression> \n" + 
			"	<expression name=\"tripStatus\"> 		\n" + 
			"		<module name=\"${tripStatusModule}\"/> 		\n" + 
			"		<criteria pattern=\"1\"> 			\n" + 
			"			<condition sequence=\"1\">parentId`=`${parentId}</condition> 		\n" + 
			"		</criteria> 		\n" + 
			"		<field name=\"${tripStatusField}\" aggregate = \"lastValue\"/> 	\n" + 
			"	</expression> \n" + 
			"	<expression name=\"autoStatusModule\"> 		\n" + 
			"		<module name=\"${autoStatusModule}\"/> 		\n" + 
			"		<criteria pattern=\"1\"> 			\n" + 
			"			<condition sequence=\"1\">parentId`=`${parentId}</condition> 		\n" + 
			"		</criteria> 		\n" + 
			"		<field name=\"${autoStatusField}\" aggregate = \"lastValue\"/> 	\n" + 
			"	</expression> \n" + 
			"</workflow>",false),
	READING_COMBO_CARD(10,"readingComboCard",null,false,true),
	WORK_ORDER_SUMMARY(11,"workorderSummary","<workflow>\n" + 
			"	<parameter name=\"orgId\" type=\"Number\" />"+
			"    <expression name=\"dueToday\">\n" + 
			"        <module name=\"workorder\" />\n" + 
			"        <criteria pattern=\"(1 and 2)\">\n" + 
			"            <condition sequence=\"1\">dueDate`Today`</condition>\n" + 
			"            <condition sequence=\"2\">status`lookup`orgid = ${orgId} and STATUS_TYPE = "+TicketStatusContext.StatusType.OPEN.getIntVal()+"</condition>\n" + 
			"        </criteria>\n" + 
			"        <field aggregate=\"count\" name=\"subject\" />\n" + 
			"    </expression>\n" + 
			"    <expression name=\"unassigned\">\n" + 
			"        <module name=\"workorder\" />\n" + 
			"        <criteria pattern=\"(1 and 2)\">\n" + 
			"            <condition sequence=\"1\">assignedTo`is empty`</condition>\n" +
			"            <condition sequence=\"2\">status`lookup`orgid = ${orgId} and STATUS_TYPE = "+TicketStatusContext.StatusType.OPEN.getIntVal()+"</condition>\n" + 
			"        </criteria>\n" + 
			"        <field aggregate=\"count\" name=\"subject\" />\n" + 
			"    </expression>\n" + 
			"    <expression name=\"open\">\n" + 
			"        <module name=\"workorder\" />\n" + 
			"        <criteria pattern=\"(1)\">\n" + 
			"            <condition sequence=\"1\">status`lookup`orgid = ${orgId} and STATUS_TYPE = "+TicketStatusContext.StatusType.OPEN.getIntVal()+"</condition>\n" + 
			"        </criteria>\n" + 
			"        <field aggregate=\"count\" name=\"subject\" />\n" + 
			"    </expression>\n" + 
			"    <expression name=\"overdue\">\n" + 
			"        <module name=\"workorder\" />\n" + 
			"        <criteria pattern=\"(1 and 2)\">\n" + 
			"            <condition sequence=\"1\">status`lookup`orgid = ${orgId} and STATUS_TYPE = "+TicketStatusContext.StatusType.OPEN.getIntVal()+"</condition>\n" + 
			"            <condition sequence=\"2\">dueDate`Till Now`</condition>\n" + 
			"        </criteria>\n" + 
			"        <field aggregate=\"count\" name=\"subject\" />\n" + 
			"    </expression>\n" + 
			"</workflow>",false),
	MY_WORK_ORDER_SUMMARY(12,"mywosummary","<workflow>\n" + 
			"	<parameter name=\"orgId\" type=\"Number\" />"+
			"	<parameter name=\"currentUserId\" type=\"Number\" />"+
			"    <expression name=\"dueToday\">\n" + 
			"        <module name=\"workorder\" />\n" + 
			"        <criteria pattern=\"(1 and 2 and 3)\">\n" + 
			"            <condition sequence=\"1\">dueDate`Today`</condition>\n" + 
			"            <condition sequence=\"2\">status`lookup`orgid = ${orgId} and STATUS_TYPE = "+TicketStatusContext.StatusType.OPEN.getIntVal()+"</condition>\n" +
			"            <condition sequence=\"3\">assignedTo`is`${currentUserId}</condition>\n" +
			"        </criteria>\n" + 
			"        <field aggregate=\"count\" name=\"subject\" />\n" + 
			"    </expression>\n" + 
			"    <expression name=\"openHighPriority\">\n" + 
			"        <module name=\"workorder\" />\n" + 
			"        <criteria pattern=\"(1 and 2 and 3)\">\n" + 
			"            <condition sequence=\"1\">status`lookup`orgid = ${orgId} and STATUS_TYPE = "+TicketStatusContext.StatusType.OPEN.getIntVal()+"</condition>\n" +
			"            <condition sequence=\"2\">assignedTo`is`${currentUserId}</condition>\n" +
			"            <condition sequence=\"3\">priority`lookup`orgid = ${orgId} and PRIORITY = 'High'</condition>\n" +
			"        </criteria>\n" + 
			"        <field aggregate=\"count\" name=\"subject\" />\n" + 
			"    </expression>\n" + 
			"    <expression name=\"open\">\n" + 
			"        <module name=\"workorder\" />\n" + 
			"        <criteria pattern=\"(1 and 2)\">\n" + 
			"            <condition sequence=\"1\">status`lookup`orgid = ${orgId} and STATUS_TYPE = "+TicketStatusContext.StatusType.OPEN.getIntVal()+"</condition>\n" +
			"            <condition sequence=\"2\">assignedTo`is`${currentUserId}</condition>\n" +
			"        </criteria>\n" + 
			"        <field aggregate=\"count\" name=\"subject\" />\n" + 
			"    </expression>\n" + 
			"    <expression name=\"overdue\">\n" + 
			"        <module name=\"workorder\" />\n" + 
			"        <criteria pattern=\"(1 and 2 and 3)\">\n" + 
			"            <condition sequence=\"1\">status`lookup`orgid = ${orgId} and STATUS_TYPE = "+TicketStatusContext.StatusType.OPEN.getIntVal()+"</condition>\n" + 
			"            <condition sequence=\"2\">dueDate`Till Now`</condition>\n" + 
			"            <condition sequence=\"3\">assignedTo`is`${currentUserId}</condition>\n" +
			"        </criteria>\n" + 
			"        <field aggregate=\"count\" name=\"subject\" />\n" + 
			"    </expression>\n" + 
			"</workflow>",false),
	OPEN_ALARMS(13,"openalarms","<workflow>\n" + 
			"	<parameter name=\"orgId\" type=\"Number\" />\n" + 
			"    <expression name=\"active\">\n" + 
			"        <module name=\"alarm\" />\n" + 
			"        <criteria pattern=\"(1 and 2)\">\n" + 
			"            <condition sequence=\"1\">sourceType`!=`9</condition>\n" + 
			"            <condition sequence=\"2\">severity`lookup`orgid = ${orgId} and Alarm_Severity.SEVERITY != 'Clear'</condition>\n" + 
			"        </criteria>\n" + 
			"        <field aggregate=\"count\" name=\"subject\" />\n" + 
			"    </expression>\n" + 
			"    <expression name=\"unacknowledged\">\n" + 
			"        <module name=\"alarm\" />\n" + 
			"        <criteria pattern=\"(1 and 2 and 3)\">\n" + 
			"            <condition sequence=\"1\">sourceType`!=`9</condition>\n" + 
			"            <condition sequence=\"2\">severity`lookup`orgid = ${orgId} and Alarm_Severity.SEVERITY != 'Clear'</condition>\n" + 
			"            <condition sequence=\"3\">isAcknowledged`is`false</condition>\n" + 
			"        </criteria>\n" + 
			"        <field aggregate=\"count\" name=\"subject\" />\n" + 
			"    </expression>\n" + 
			"</workflow>",false),
	BUILDING_OPEN_ALARMS(14,"buildingopenalarms","<workflow>\n" + 
			"	<parameter name=\"orgId\" type=\"Number\" />\n" + 
			"	<parameter name=\"buildingId\" type=\"Number\" />\n" + 
			"    <expression name=\"active\">\n" + 
			"        <module name=\"alarm\" />\n" + 
			"        <criteria pattern=\"(1 and 2 and 3)\">\n" + 
			"            <condition sequence=\"1\">sourceType`!=`9</condition>\n" + 
			"            <condition sequence=\"2\">severity`lookup`orgid = ${orgId} and Alarm_Severity.SEVERITY != 'Clear'</condition>\n" + 
			"            <condition sequence=\"3\">resource`building_is`${buildingId}</condition>\n" + 
			"        </criteria>\n" + 
			"        <field aggregate=\"count\" name=\"subject\" />\n" + 
			"    </expression>\n" + 
			"    <expression name=\"unacknowledged\">\n" + 
			"        <module name=\"alarm\" />\n" + 
			"        <criteria pattern=\"(1 and 2 and 3 and 4)\">\n" + 
			"            <condition sequence=\"1\">sourceType`!=`9</condition>\n" + 
			"            <condition sequence=\"2\">severity`lookup`orgid = ${orgId} and Alarm_Severity.SEVERITY != 'Clear'</condition>\n" + 
			"            <condition sequence=\"3\">isAcknowledged`is`false</condition>\n" + 
			"            <condition sequence=\"4\">resource`building_is`${buildingId}</condition>\n" + 
			"        </criteria>\n" + 
			"        <field aggregate=\"count\" name=\"subject\" />\n" + 
			"    </expression>\n" + 
			"</workflow>",false),
	KPI_CARD(15,"kpiCard",null,true,true),
	KPI_CARD_MULTI_RES(16,"kpiMultiResultCard",null,false,true),
	;
	
	
	private Integer value;
	private String name;
	private String workflow;
	private boolean isSingleResultWorkFlow;
	private boolean isDynamicWfGeneratingCard;
	
	public boolean isDynamicWfGeneratingCard() {
		return isDynamicWfGeneratingCard;
	}
	public void setDynamicWfGeneratingCard(boolean isDynamicWfGeneratingCard) {
		this.isDynamicWfGeneratingCard = isDynamicWfGeneratingCard;
	}
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
	
	CardType(Integer value,String name,String workflow,boolean isSingleResultWorkFlow,boolean isDynamicWfGeneratingCard) {
		this.value = value;
		this.name= name;
		this.workflow = workflow;
		this.isSingleResultWorkFlow = isSingleResultWorkFlow;
		this.isDynamicWfGeneratingCard = isDynamicWfGeneratingCard;
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
