package com.facilio.bmsconsole.workflow;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.text.WordUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.accounts.dto.Group;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.aws.util.AwsUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.BaseLineContext;
import com.facilio.bmsconsole.context.NotificationContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.TicketStatusContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.criteria.DateRange;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.modules.NumberField;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.bmsconsole.util.BaseLineAPI;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.bmsconsole.util.NotificationAPI;
import com.facilio.bmsconsole.util.SMSUtil;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.view.ReadingRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.events.constants.EventConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.leed.context.PMTriggerContext;
import com.facilio.sql.GenericSelectRecordBuilder;

public enum ActionType {
	EMAIL_NOTIFICATION(1) {
		@Override
		public void performAction(JSONObject obj, Context context) {
			// TODO Auto-generated method stub
			if(obj != null) {
				try {
					String to = (String) obj.get("to");
					if(to != null && !to.isEmpty()) {
						List<String> emails = new ArrayList<>();
						AwsUtil.sendEmail(obj);
						
						emails.add(to);
						if(context != null) {
							context.put(FacilioConstants.Workflow.NOTIFIED_EMAILS, emails);
						}
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	},
	SMS_NOTIFICATION(2)
	{
		@Override
		public void performAction(JSONObject obj, Context context) {
			// TODO Auto-generated method stub
			if(obj != null) {
				try {
					String to = (String) obj.get("to");
					if(to != null && !to.isEmpty()) {
						List<String> sms = new ArrayList<>();
						SMSUtil.sendSMS(obj);
						
						sms.add(to);
						if(context != null) {
							context.put(FacilioConstants.Workflow.NOTIFIED_SMS, sms);
						}
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	},
	BULK_EMAIL_NOTIFICATION(3) {
		@Override
		public void performAction(JSONObject obj, Context context) {
			// TODO Auto-generated method stub
			if(obj != null) {
				try {
					JSONArray toEmails = null;
					Object toAddr = obj.remove("to");
					
					if(toAddr instanceof JSONArray) {
						toEmails = (JSONArray) toAddr;
					}
					else if(toAddr instanceof String) {
						toEmails = new JSONArray();
						toEmails.add((String) toAddr);
					}
					
					if(toEmails != null && !toEmails.isEmpty()) {
						List<String> emails = new ArrayList<>();
						for(Object toEmail : toEmails) {
							String to = (String) toEmail;
							if(to != null && !to.isEmpty()) {
								obj.put("to", (String)to);
								AwsUtil.sendEmail(obj);
								emails.add(to);
							}
						}
						if(context != null) {
							context.put(FacilioConstants.Workflow.NOTIFIED_EMAILS, emails);
						}
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	},
	BULK_SMS_NOTIFICATION(4) {

		@Override
		public void performAction(JSONObject obj, Context context) {
			// TODO Auto-generated method stub
			if(obj != null) {
				try {
					JSONArray tos = null;
					Object toNums = obj.remove("to");
					
					if(toNums instanceof JSONArray) {
						tos = (JSONArray) toNums;
					}
					else if(toNums instanceof String) {
						tos = new JSONArray();
						tos.add((String) toNums);
					}
					if(tos != null && !tos.isEmpty()) {
						List<String> sms = new ArrayList<>();
						for(Object toObj : tos) {
							String to = (String) toObj;
							if(to != null && !to.isEmpty()) {
								obj.put("to", (String) to);
								SMSUtil.sendSMS(obj);
								sms.add(to);
							}
						}
						if(context != null) {
							context.put(FacilioConstants.Workflow.NOTIFIED_SMS, sms);
						}
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		
	},
	WEB_NOTIFICATION(5) {

		@Override
		public void performAction(JSONObject obj, Context context) {
			// TODO Auto-generated method stub
			if(obj != null) {
				try {
					List<Long> reciepents = null;
					String toIds = (String) obj.get("to");
					if (toIds != null) {
						String[] toList = toIds.trim().split(",");
						for ( String toId : toList) {
							reciepents.add(Long.valueOf(toId));
						}
						
						NotificationContext notification = new NotificationContext();
						notification.setInfo((String) obj.get("message"));
						int type = (int) obj.get("activityType");
						notification.setNotificationType(ActivityType.valueOf(type));
						NotificationAPI.sendNotification(reciepents, notification);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	},
	ADD_ALARM(6){
		@Override
		public void performAction(JSONObject obj, Context context) {
//			System.out.println(">>>>>>>>>>>>>>> jsonobject : "+obj.toJSONString());
			if(obj != null) {
				try {
					if(obj.containsKey("alarmType")) {
						obj.remove("alarmType");
					}
					if(obj.containsKey("subject")) {
						String subject = (String) obj.get("subject");
						obj.put("message", subject);
					}
					obj.put("alarmType", 5);
					
					WorkflowRuleContext currentRule = (WorkflowRuleContext) context.get(FacilioConstants.ContextNames.CURRENT_WORKFLOW_RULE);
					if (currentRule instanceof ReadingRuleContext) {
						addReadingAlarmProps(obj, (ReadingRuleContext) currentRule, (ReadingContext) context.get(FacilioConstants.ContextNames.CURRENT_RECORD));
					}
					
					FacilioContext addEventContext = new FacilioContext();
					addEventContext.put(EventConstants.EventContextNames.EVENT_PAYLOAD, obj);
					Chain getAddEventChain = EventConstants.EventChainFactory.getAddEventChain();
					getAddEventChain.execute(addEventContext);
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		private void addReadingAlarmProps(JSONObject obj, ReadingRuleContext rule, ReadingContext reading) throws Exception {
			obj.put("readingFieldId", rule.getReadingFieldId());
			
			if (rule.getBaselineId() != -1) {
				obj.put("baselineId", rule.getBaselineId());
			}
			obj.put("sourceType", 6);
			DateRange range = null;
			if (rule.getAggregation() != null) {
				range = DateOperators.LAST_N_HOURS.getRange(String.valueOf(rule.getDateRange()));
				obj.put("startTime", range.getStartTime());
				obj.put("endTime", range.getEndTime());
			}
			else {
				obj.put("startTime", reading.getTtime());
				range.setStartTime(reading.getTtime());
			}
			obj.put("readingMessage", getMessage(rule, range));
		}
		
		private String getMessage(ReadingRuleContext rule, DateRange range) throws Exception {
			StringBuilder msgBuilder = new StringBuilder();
			if (rule.getAggregation() != null) {
				if(rule.getDateRange() == 1) {
					msgBuilder.append("Hourly ")
								.append(rule.getAggregation());
				}
				else {
					msgBuilder.append(WordUtils.capitalize(rule.getAggregation()));
				}
				msgBuilder.append(" of ");
			}
			
			msgBuilder.append("'")
					.append(rule.getReadingField().getDisplayName())
					.append("' ");
			
			NumberOperators operator = (NumberOperators) FieldType.NUMBER.getOperator(rule.getOperator());
			
			if (rule.getBaselineId() != -1) {
				switch (operator) {
					case EQUALS:
						msgBuilder.append("was along ");
						updatePercentage(rule.getPercentage(), msgBuilder);
						break;
					case NOT_EQUALS:
						msgBuilder.append("wasn't alone ");
						updatePercentage(rule.getPercentage(), msgBuilder);
						break;
					case LESS_THAN:
					case LESS_THAN_EQUAL:
						msgBuilder.append("went ");
						updatePercentage(rule.getPercentage(), msgBuilder);
						msgBuilder.append("lower than ");
						break;
					case GREATER_THAN:
					case GREATER_THAN_EQUAL:
						msgBuilder.append("went ");
						updatePercentage(rule.getPercentage(), msgBuilder);
						msgBuilder.append("higher than ");
						break;
				}
				
				BaseLineContext bl = BaseLineAPI.getBaseLine(rule.getBaselineId());
				msgBuilder.append("the ");
				msgBuilder.append("base line ")
							.append("'")
							.append(bl.getName())
							.append("'");
			}
			else {
				switch (operator) {
					case EQUALS:
						msgBuilder.append("was ");
						break;
					case NOT_EQUALS:
						msgBuilder.append("wasn't ");
						break;
					case LESS_THAN:
					case LESS_THAN_EQUAL:
						msgBuilder.append("went below ");
						break;
					case GREATER_THAN:
					case GREATER_THAN_EQUAL:
						msgBuilder.append("exceeded ");
						break;
				}
				
				msgBuilder.append(rule.getPercentage());
				if (rule.getReadingField() instanceof NumberField && ((NumberField)rule.getReadingField()).getUnit() != null) {
					msgBuilder.append(((NumberField)rule.getReadingField()).getUnit());
				}
			}
			
			if (range.getEndTime() != -1) {
				msgBuilder.append(" between ")
						.append(DateTimeUtil.getZonedDateTime(range.getStartTime()).format(FacilioConstants.READABLE_DATE_FORMAT))
						.append(" and ")
						.append(DateTimeUtil.getZonedDateTime(range.getEndTime()).format(FacilioConstants.READABLE_DATE_FORMAT));
			}
			else {
				msgBuilder.append(" at ")
							.append(DateTimeUtil.getZonedDateTime(range.getStartTime()).format(FacilioConstants.READABLE_DATE_FORMAT));
			}
			
			return msgBuilder.toString();
		}
		
		private void updatePercentage(long percentage, StringBuilder msgBuilder) {
			if (percentage != 0) {
				msgBuilder.append(percentage)
							.append("% ");
			}
		}
	},
	PUSH_NOTIFICATION(7) {

		@SuppressWarnings("unchecked")
		@Override
		public void performAction(JSONObject obj, Context context) {
			// TODO Auto-generated method stub
			try {
				if (obj != null) {
					String ids = (String) obj.get("id");
					
					if(ids != null) {
						List<String> mobileInstanceIds = getMobileInstanceIDs(ids);
						
						if (mobileInstanceIds != null && !mobileInstanceIds.isEmpty()) {
							for(String mobileInstanceId : mobileInstanceIds) {
								if(mobileInstanceId != null) {
	//								content.put("to", "exA12zxrItk:APA91bFzIR6XWcacYh24RgnTwtsyBDGa5oCs5DVM9h3AyBRk7GoWPmlZ51RLv4DxPt2Dq2J4HDTRxW6_j-RfxwAVl9RT9uf9-d9SzQchMO5DHCbJs7fLauLIuwA5XueDuk7p5P7k9PfV");
									obj.put("to", mobileInstanceId);
									Map<String, String> headers = new HashMap<>();
									headers.put("Content-Type","application/json");
									headers.put("Authorization","key=AAAA7I5dN-o:APA91bE70uJ4z21h9jh3A3TfExeHmtsESVYR0W79qbgcW8iyJZ1hKFzTkqV9xXJU-KPqpO1TstbqufHBp8tTCJRjiRAHP2ghNN49T6W0e13pYvtLd_qfPn_dhiKkTpE_BrpVg0WrxxVG");
									
									String url = "https://fcm.googleapis.com/fcm/send";
									
									AwsUtil.doHttpPost(url, headers, null, obj.toJSONString());
									System.out.println("Push notification sent");
									System.out.println(obj.toJSONString());
								}
							}
						}
					}
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		private List<String> getMobileInstanceIDs(String idList) throws Exception {
			List<String> mobileInstanceIds = new ArrayList<String>();
			List<FacilioField> fields = new ArrayList<>();
			
			FacilioField email = new FacilioField();
			email.setName("email");
			email.setColumnName("EMAIL");
			email.setDataType(FieldType.STRING);
			email.setModule(ModuleFactory.getUserModule());
			fields.add(email);
			
			FacilioField mobileInstanceId = new FacilioField();
			mobileInstanceId.setName("mobileInstanceId");
			mobileInstanceId.setColumnName("MOBILE_INSTANCE_ID");
			mobileInstanceId.setDataType(FieldType.STRING);
			mobileInstanceId.setModule(AccountConstants.getUserMobileSettingModule());
			fields.add(mobileInstanceId);
			
//			Condition condition = CriteriaAPI.getCondition("EMAIL", "email", StringUtils.join(emails, ","), StringOperators.IS);
			
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
															.select(fields)
															.table("Users")
															.innerJoin("ORG_Users")
															.on("Users.USERID = ORG_Users.USERID")
															.innerJoin("User_Mobile_Setting")
															.on("ORG_Users.USERID = User_Mobile_Setting.USERID")
															.andCondition(CriteriaAPI.getCondition("ORG_Users.ORG_USERID", "ouid", idList, NumberOperators.EQUALS))
															.andCustomWhere("ORG_Users.USER_STATUS = true and ORG_Users.DELETED_TIME = -1")
															.orderBy("USER_MOBILE_SETTING_ID");
			
			List<Map<String, Object>> props = selectBuilder.get();
			if(props != null && !props.isEmpty()) {
				for(Map<String, Object> prop : props) {
					mobileInstanceIds.add((String) prop.get("mobileInstanceId"));
//					emailToMobileId.put((String) prop.get("email"), (String) prop.get("mobileInstanceId"));
				}
			}
			return mobileInstanceIds;
		}
	},
	EXECUTE_PM(8) {
		@Override
		public void performAction(JSONObject obj, Context context) {
			// TODO Auto-generated method stub
			try {
				long ruleId = (long) obj.get("rule.id");
				
				FacilioModule module = ModuleFactory.getPMTriggersModule();
				GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
																	.select(FieldFactory.getPMTriggerFields())
																	.table(module.getTableName())
																	.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
																	.andCustomWhere("READING_RULE_ID = ?", ruleId);
				List<Map<String, Object>> pmProps = selectRecordBuilder.get();
				if(pmProps != null && !pmProps.isEmpty()) {
					PMTriggerContext trigger = FieldUtil.getAsBeanFromMap(pmProps.get(0), PMTriggerContext.class);
					
					FacilioContext pmContext = new FacilioContext();
					pmContext.put(FacilioConstants.ContextNames.RECORD_ID, trigger.getPmId());
					pmContext.put(FacilioConstants.ContextNames.PM_CURRENT_TRIGGER, trigger);
					pmContext.put(FacilioConstants.ContextNames.CURRENT_EXECUTION_TIME, Instant.now().getEpochSecond());
					pmContext.put(FacilioConstants.ContextNames.PM_RESET_TRIGGERS, true);
					
					Chain executePm = FacilioChainFactory.getExecutePreventiveMaintenanceChain();
					executePm.execute(pmContext);
					
					if(context != null) {
						context.put(FacilioConstants.ContextNames.WORK_ORDER, (WorkOrderContext) pmContext.get(FacilioConstants.ContextNames.WORK_ORDER));
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
																
		}
	},
	ASSIGNMENT_ACTION(9) {

		@Override
		public void performAction(JSONObject obj, Context context) {
			// TODO Auto-generated method stub
			long assignedToUserId = -1, assignGroupId = -1;
			
			assignedToUserId = (long)obj.get("assignedUserId");
			assignGroupId = (long)obj.get("assignedGroupId");
						
			WorkOrderContext workOrder = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);
			WorkOrderContext updateWO = new WorkOrderContext();
			
			if(assignedToUserId != -1) {
				User user = new User();
				user.setOuid(assignedToUserId);
				workOrder.setAssignedTo(user);
				updateWO.setAssignedTo(user);
			}
			
			if(assignGroupId != -1) {
				Group group = new Group();
				group.setId(assignGroupId);
				workOrder.setAssignmentGroup(group);
				updateWO.setAssignmentGroup(group);
			}
			try {
				if(assignedToUserId != -1 || assignGroupId != -1) {
					TicketStatusContext status = TicketAPI.getStatus("Assigned");
					workOrder.setStatus(status);
					updateWO.setStatus(status);
				}
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule woModule = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
				UpdateRecordBuilder<WorkOrderContext> updateBuilder = new UpdateRecordBuilder<WorkOrderContext>()
																	.module(woModule)
																	.fields(modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER))
																	.andCondition(CriteriaAPI.getIdCondition(workOrder.getId(), woModule))
																	;
				updateBuilder.update(updateWO);
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			
		}
		
	},
	SLA_ACTION(10) {
		@Override
		public void performAction(JSONObject obj, Context context) {
			
			JSONArray slaPolicyJson = (JSONArray)obj.get("slaPolicyJson");
			
			WorkOrderContext workOrder = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);
			long workorderpriority = workOrder.getPriority().getId();
			Long duration = null ;
			
			Iterator iter = slaPolicyJson.iterator();
			while(iter.hasNext())
			{
				JSONObject slaPolicy = (JSONObject)iter.next();
				long priorityId = Long.parseLong((String)slaPolicy.get("priority"));
				if(priorityId == workorderpriority)
				{
					duration = Long.parseLong((String)slaPolicy.get("duration"));
				}
				
			}
			
			//duration = (Long)slaPolicyJson.get(String.valueOf(workorderpriority));
			if(duration != null)
			{
				long dueDate = workOrder.getCreatedTime()+duration;

				WorkOrderContext updateWO = new WorkOrderContext();
				workOrder.setDueDate(dueDate);
				updateWO.setDueDate(dueDate);
				
				try {
					ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
					FacilioModule woModule = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
					UpdateRecordBuilder<WorkOrderContext> updateBuilder = new UpdateRecordBuilder<WorkOrderContext>()
																		.module(woModule)
																		.fields(modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER))
																		.andCondition(CriteriaAPI.getIdCondition(workOrder.getId(), woModule))
																		;
					updateBuilder.update(updateWO);
					
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			
			}
			
			
		}	
	}
	;
	
	private int val;
	private ActionType(int val) {
		// TODO Auto-generated constructor stub
		this.val = val;
	}
	
	public int getVal() {
		return val;
	}
	
	abstract public void performAction(JSONObject obj, Context context);
	
	public static ActionType getActionType(int actionTypeVal) {
		return TYPE_MAP.get(actionTypeVal);
	}
	
	private static final Map<Integer, ActionType> TYPE_MAP = Collections.unmodifiableMap(initTypeMap());
	private static Map<Integer, ActionType> initTypeMap() {
		Map<Integer, ActionType> typeMap = new HashMap<>();
		for(ActionType type : values()) {
			typeMap.put(type.getVal(), type);
		}
		return typeMap;
	}
	public static void main(String arg[])
	{
		System.out.println("hello world");
		ActionType t = ActionType.SMS_NOTIFICATION;
		JSONObject json = new JSONObject();
		json.put("to", "+919840425388");
		json.put("message", "hello world");
		t.performAction(json, null);
	}
}