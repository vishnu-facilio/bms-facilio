package com.facilio.bmsconsole.workflow.rule;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.accounts.bean.UserBean;
import com.facilio.accounts.dto.Group;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.context.AlarmSeverityContext;
import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsole.context.NotificationContext;
import com.facilio.bmsconsole.context.PMTriggerContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.TicketContext.SourceType;
import com.facilio.bmsconsole.context.TicketStatusContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.bmsconsole.util.NotificationAPI;
import com.facilio.bmsconsole.util.SMSUtil;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.util.WorkOrderAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.events.constants.EventConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericSelectRecordBuilder;

public enum ActionType {

	EMAIL_NOTIFICATION(1) {
		@Override
		public void performAction(JSONObject obj, Context context, WorkflowRuleContext currentRule,
				Object currentRecord) {
			// TODO Auto-generated method stub
			if (obj != null) {
				try {
					String to = (String) obj.get("to");
					if (to != null && !to.isEmpty() && checkIfActiveUserFromEmail(to)) {
						List<String> emails = new ArrayList<>();
						AwsUtil.sendEmail(obj);

						emails.add(to);
						if (context != null) {
							context.put(FacilioConstants.Workflow.NOTIFIED_EMAILS, emails);
						}
					}
				} catch (Exception e) {
					LOGGER.error("Exception occurred ", e);
				}
			}
		}
	},
	SMS_NOTIFICATION(2) {
		@Override
		public void performAction(JSONObject obj, Context context, WorkflowRuleContext currentRule,
				Object currentRecord) {
			// TODO Auto-generated method stub
			if (obj != null) {
				try {
					String to = (String) obj.get("to");
					if (to != null && !to.isEmpty() && checkIfActiveUserFromPhone(to)) {
						List<String> sms = new ArrayList<>();
						SMSUtil.sendSMS(obj);

						sms.add(to);
						if (context != null) {
							context.put(FacilioConstants.Workflow.NOTIFIED_SMS, sms);
						}
					}
				} catch (Exception e) {
					LOGGER.error("Exception occurred ", e);
				}
			}
		}
	},
	BULK_EMAIL_NOTIFICATION(3) {
		@Override
		public void performAction(JSONObject obj, Context context, WorkflowRuleContext currentRule,
				Object currentRecord) {
			// TODO Auto-generated method stub
			if (obj != null) {
				try {
					JSONArray toEmails = null;
					Object toAddr = obj.remove("to");

					if (toAddr instanceof JSONArray) {
						toEmails = (JSONArray) toAddr;
					} else if (toAddr instanceof String) {
						toEmails = new JSONArray();
						toEmails.add(toAddr);
					}

					if (toEmails != null && !toEmails.isEmpty()) {
						List<String> emails = new ArrayList<>();
						for (Object toEmail : toEmails) {
							String to = (String) toEmail;
							if (to != null && !to.isEmpty() && checkIfActiveUserFromEmail(to)) {
								obj.put("to", to);
								
								if (AccountUtil.getCurrentOrg().getId() == 104) {
									LOGGER.info("Gonna Email : "+obj.toJSONString());
								}
								
								AwsUtil.sendEmail(obj);
								emails.add(to);
							}
						}
						if (context != null) {
							context.put(FacilioConstants.Workflow.NOTIFIED_EMAILS, emails);
						}
					}
				} catch (Exception e) {
					LOGGER.error("Exception occurred ", e);
				}
			}
		}
	},
	BULK_SMS_NOTIFICATION(4) {

		@Override
		public void performAction(JSONObject obj, Context context, WorkflowRuleContext currentRule,
				Object currentRecord) {
			// TODO Auto-generated method stub
			if (obj != null) {
				try {
					JSONArray tos = null;
					Object toNums = obj.remove("to");

					if (toNums instanceof JSONArray) {
						tos = (JSONArray) toNums;
					} else if (toNums instanceof String) {
						tos = new JSONArray();
						tos.add(toNums);
					}
					if (tos != null && !tos.isEmpty()) {
						List<String> sms = new ArrayList<>();
						for (Object toObj : tos) {
							String to = (String) toObj;
							if (to != null && !to.isEmpty() && checkIfActiveUserFromPhone(to)) {
								obj.put("to", to);
								SMSUtil.sendSMS(obj);
								sms.add(to);
							}
						}
						if (context != null) {
							context.put(FacilioConstants.Workflow.NOTIFIED_SMS, sms);
						}
					}
				} catch (Exception e) {
					LOGGER.error("Exception occurred ", e);
				}
			}
		}

	},
	WEB_NOTIFICATION(5) {

		@Override
		public void performAction(JSONObject obj, Context context, WorkflowRuleContext currentRule,
				Object currentRecord) {
			// TODO Auto-generated method stub
			if (obj != null) {
				try {
					List<Long> reciepents = null;
					String toIds = (String) obj.get("to");
					if (toIds != null) {
						String[] toList = toIds.trim().split(",");
						for (String toId : toList) {
							if (toId.isEmpty()) {
								continue;
							}
							long id = Long.valueOf(toId);
							if (checkIfActiveUserFromId(id)) {
								reciepents.add(id);
							}
						}

						NotificationContext notification = new NotificationContext();
						notification.setInfo((String) obj.get("message"));
						int type = (int) obj.get("activityType");
						notification.setNotificationType(ActivityType.valueOf(type));
						NotificationAPI.sendNotification(reciepents, notification);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					LOGGER.error("Exception occurred ", e);
				}
			}
		}
	},
	ADD_ALARM(6) {
		@Override
		public void performAction(JSONObject obj, Context context, WorkflowRuleContext currentRule,
				Object currentRecord) {
			// System.out.println(">>>>>>>>>>>>>>> jsonobject :
			// "+obj.toJSONString());
			if (obj != null) {
				try {
					if (obj.containsKey("subject")) {
						String subject = (String) obj.get("subject");
						obj.put("message", subject);
					}

					if (currentRule instanceof ReadingRuleContext) {
						AlarmAPI.addReadingAlarmProps(obj, (ReadingRuleContext) currentRule,
								(ReadingContext) currentRecord);
					}

					FacilioContext addEventContext = new FacilioContext();
					addEventContext.put(EventConstants.EventContextNames.EVENT_PAYLOAD, obj);
					Chain getAddEventChain = EventConstants.EventChainFactory.getAddEventChain();
					getAddEventChain.execute(addEventContext);
				} catch (Exception e) {
					LOGGER.error("Exception occurred ", e);
				}
			}
		}
	},
	PUSH_NOTIFICATION(7) {

		@SuppressWarnings("unchecked")
		@Override
		public void performAction(JSONObject obj, Context context, WorkflowRuleContext currentRule,
				Object currentRecord) {
			// TODO Auto-generated method stub
			try {
				if (obj != null) {
					String ids = (String) obj.get("id");

					if (ids != null) {
						List<String> mobileInstanceIds = getMobileInstanceIDs(ids);

						if (mobileInstanceIds != null && !mobileInstanceIds.isEmpty()) {
							for (String mobileInstanceId : mobileInstanceIds) {
								if (mobileInstanceId != null) {
									// content.put("to",
									// "exA12zxrItk:APA91bFzIR6XWcacYh24RgnTwtsyBDGa5oCs5DVM9h3AyBRk7GoWPmlZ51RLv4DxPt2Dq2J4HDTRxW6_j-RfxwAVl9RT9uf9-d9SzQchMO5DHCbJs7fLauLIuwA5XueDuk7p5P7k9PfV");
									obj.put("to", mobileInstanceId);
									Map<String, String> headers = new HashMap<>();
									headers.put("Content-Type", "application/json");
									headers.put("Authorization",
											"key=AAAAMZz7GzM:APA91bGGZjl_YGNfo9OfEP5kgFiBp3Z0dHq_oa0yHLjgoogHXdPqDWwF2Z1IHYq6T9poGCS-JOwdMEIBqRPxExfemOlJmjOAcdfVlD7qT0IGjLr5gReqwefjBjmPg0Re1O7o0_gC0mYx");

									String url = "https://fcm.googleapis.com/fcm/send";

									AwsUtil.doHttpPost(url, headers, null, obj.toJSONString());
									System.out.println("Push notification sent");
									System.out.println(obj.toJSONString());
								}
							}
						}
					}
				}
			} catch (Exception e) {
				LOGGER.error("Exception occurred ", e);
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

			// Condition condition = CriteriaAPI.getCondition("EMAIL", "email",
			// StringUtils.join(emails, ","), StringOperators.IS);

			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder().select(fields).table("Users")
					.innerJoin("ORG_Users").on("Users.USERID = ORG_Users.USERID").innerJoin("User_Mobile_Setting")
					.on("ORG_Users.USERID = User_Mobile_Setting.USERID")
					.andCondition(CriteriaAPI.getCondition("ORG_Users.ORG_USERID", "ouid", idList, NumberOperators.EQUALS))
					.andCustomWhere("ORG_Users.USER_STATUS = true and ORG_Users.DELETED_TIME = -1")
					.orderBy("USER_MOBILE_SETTING_ID");

			List<Map<String, Object>> props = selectBuilder.get();
			if (props != null && !props.isEmpty()) {
				for (Map<String, Object> prop : props) {
					mobileInstanceIds.add((String) prop.get("mobileInstanceId"));
					// emailToMobileId.put((String) prop.get("email"), (String)
					// prop.get("mobileInstanceId"));
				}
			}
			return mobileInstanceIds;
		}
	},
	EXECUTE_PM(8) {
		@Override
		public void performAction(JSONObject obj, Context context, WorkflowRuleContext currentRule,
				Object currentRecord) {
			// TODO Auto-generated method stub
			try {
				long ruleId = (long) obj.get("rule.id");

				FacilioModule module = ModuleFactory.getPMTriggersModule();
				GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
						.select(FieldFactory.getPMTriggerFields()).table(module.getTableName())
						.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
						.andCustomWhere("READING_RULE_ID = ?", ruleId);
				List<Map<String, Object>> pmProps = selectRecordBuilder.get();
				if (pmProps != null && !pmProps.isEmpty()) {
					PMTriggerContext trigger = FieldUtil.getAsBeanFromMap(pmProps.get(0), PMTriggerContext.class);

					FacilioContext pmContext = new FacilioContext();
					pmContext.put(FacilioConstants.ContextNames.RECORD_ID, trigger.getPmId());
					pmContext.put(FacilioConstants.ContextNames.PM_CURRENT_TRIGGER, trigger);
					pmContext.put(FacilioConstants.ContextNames.CURRENT_EXECUTION_TIME, Instant.now().getEpochSecond());
					pmContext.put(FacilioConstants.ContextNames.PM_RESET_TRIGGERS, true);

					Chain executePm = FacilioChainFactory.getExecutePreventiveMaintenanceChain();
					executePm.execute(pmContext);

					if (context != null) {
						context.put(FacilioConstants.ContextNames.WORK_ORDER,
								pmContext.get(FacilioConstants.ContextNames.WORK_ORDER));
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				LOGGER.error("Exception occurred ", e);
			}

		}
		
		@Override
		public boolean isTemplateNeeded() {
			return false;
		}
	},
	ASSIGNMENT_ACTION(9) {

		@Override
		public void performAction(JSONObject obj, Context context, WorkflowRuleContext currentRule,
				Object currentRecord) {
			// TODO Auto-generated method stub
			long assignedToUserId = -1, assignGroupId = -1;

			assignedToUserId = (long) obj.get("assignedUserId");
			assignGroupId = (long) obj.get("assignedGroupId");

			WorkOrderContext workOrder = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);
			WorkOrderContext updateWO = new WorkOrderContext();

			if (assignedToUserId != -1) {
				User user = new User();
				user.setOuid(assignedToUserId);
				workOrder.setAssignedTo(user);
				updateWO.setAssignedTo(user);
			}

			if (assignGroupId != -1) {
				Group group = new Group();
				group.setId(assignGroupId);
				workOrder.setAssignmentGroup(group);
				updateWO.setAssignmentGroup(group);
			}
			try {
				if (assignedToUserId != -1 || assignGroupId != -1) {
					TicketStatusContext status = TicketAPI.getStatus("Assigned");
					workOrder.setStatus(status);
					updateWO.setStatus(status);
				}
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule woModule = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
				UpdateRecordBuilder<WorkOrderContext> updateBuilder = new UpdateRecordBuilder<WorkOrderContext>()
						.module(woModule).fields(modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER))
						.andCondition(CriteriaAPI.getIdCondition(workOrder.getId(), woModule));
				updateBuilder.update(updateWO);
			} catch (Exception e) {
				LOGGER.error("Exception occurred ", e);
			}

		}

	},
	SLA_ACTION(10) {
		@Override
		public void performAction(JSONObject obj, Context context, WorkflowRuleContext currentRule,
				Object currentRecord) {

			WorkOrderContext workOrder = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);
			if (workOrder.getPriority() == null) {
				return;
			}
			if (workOrder.getDueDate() != -1) {
				return;
			}
			long workorderpriority = workOrder.getPriority().getId();
			Long duration = null;

			JSONArray slaPolicyJson = (JSONArray) obj.get("slaPolicyJson");
			Iterator iter = slaPolicyJson.iterator();
			while (iter.hasNext()) {
				JSONObject slaPolicy = (JSONObject) iter.next();
				long priorityId = Long.parseLong((String) slaPolicy.get("priority"));
				if (priorityId == workorderpriority) {
					duration = Long.parseLong(slaPolicy.get("duration").toString()) * 1000;
				}

			}

			// duration =
			// (Long)slaPolicyJson.get(String.valueOf(workorderpriority));
			if (duration != null) {
				long dueDate = ((WorkOrderContext)currentRecord).getCreatedTime() + duration;

				WorkOrderContext updateWO = new WorkOrderContext();
				((WorkOrderContext)currentRecord).setDueDate(dueDate);
				updateWO.setDueDate(dueDate);

				try {
					ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
					FacilioModule woModule = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
					UpdateRecordBuilder<WorkOrderContext> updateBuilder = new UpdateRecordBuilder<WorkOrderContext>()
							.module(woModule).fields(modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER))
							.andCondition(CriteriaAPI.getIdCondition(((WorkOrderContext)currentRecord).getId(), woModule));
					updateBuilder.update(updateWO);

				} catch (Exception e) {
					LOGGER.error("Exception occurred ", e);
				}

			}

		}
	},
	CREATE_WO_FROM_ALARM(11) {

		@Override
		public void performAction(JSONObject obj, Context context, WorkflowRuleContext currentRule,
				Object currentRecord) {
			// TODO Auto-generated method stub
			try {
				WorkOrderContext wo = WorkOrderAPI.getWorkOrder(((AlarmContext) currentRecord).getId());
				if (wo == null) {
					updateAlarm(obj, ((AlarmContext) currentRecord).getId());
					AlarmContext alarm = AlarmAPI.getAlarm(((AlarmContext) currentRecord).getId());
					wo = AlarmAPI.getNewWOForAlarm(alarm);
					FacilioContext woContext = new FacilioContext();
					woContext.put(FacilioConstants.ContextNames.WORK_ORDER, wo);
					woContext.put(FacilioConstants.ContextNames.INSERT_LEVEL, 2);

					Chain addWorkOrder = TransactionChainFactory.getAddWorkOrderChain();
					addWorkOrder.execute(woContext);
				} else {
					AlarmContext alarm = AlarmAPI.getAlarm(wo.getId());
					fetchSeverities(alarm);
					NoteContext note = new NoteContext();
					note.setBody("Alarm associated with this automated work order updated from "+alarm.getPreviousSeverity().getSeverity()+" to "+alarm.getSeverity().getSeverity()+" at "+alarm.getModifiedTimeString());
					note.setParentId(wo.getId());
					
					FacilioContext noteContext = new FacilioContext();
					noteContext.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.TICKET_NOTES);
					noteContext.put(FacilioConstants.ContextNames.TICKET_MODULE, FacilioConstants.ContextNames.WORK_ORDER);
					noteContext.put(FacilioConstants.ContextNames.NOTE, note);

					Chain addNote = FacilioChainFactory.getAddNoteChain();
					addNote.execute(noteContext);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				LOGGER.error("Exception occurred during creating Workorder from Alarm", e);
			}
		}
		
		@SuppressWarnings("unchecked")
		private void updateAlarm(JSONObject woJson, long id) throws Exception {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ALARM);
			List<FacilioField> fields = modBean.getAllFields(module.getName());
			woJson.remove("subject");
			woJson.put("isWoCreated", true);
			UpdateRecordBuilder<WorkOrderContext> updateBuilder = new UpdateRecordBuilder<WorkOrderContext>()
																		.module(module)
																		.fields(fields)
																		.andCondition(CriteriaAPI.getIdCondition(id, module))
																		;
			updateBuilder.update(woJson);
		}
		
		private void fetchSeverities(AlarmContext alarm) throws Exception {
			List<Long> ids = new ArrayList<>();
			ids.add(alarm.getPreviousSeverity().getId());
			ids.add(alarm.getSeverity().getId());
			Map<Long, AlarmSeverityContext> severityMap = AlarmAPI.getAlarmSeverityMap(ids);
			
			alarm.setPreviousSeverity(severityMap.get(alarm.getPreviousSeverity().getId()));
			alarm.setSeverity(severityMap.get(alarm.getSeverity().getId()));
		}

	},
	CLOSE_WO_FROM_ALARM(12) {

		@Override
		public void performAction(JSONObject obj, Context context, WorkflowRuleContext currentRule,
				Object currentRecord) throws Exception {
			// TODO Auto-generated method stub
			try {
				WorkOrderContext wo = WorkOrderAPI.getWorkOrder(((AlarmContext) currentRecord).getId());
				if (wo != null) {
					FacilioContext updateContext = new FacilioContext();
					updateContext.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, ActivityType.CLOSE_WORK_ORDER);

					WorkOrderContext workorder = new WorkOrderContext();
					workorder.setStatus(TicketAPI.getStatus("Closed"));
					
					context.put(FacilioConstants.ContextNames.WORK_ORDER, workorder);
					context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(wo.getId()));

					Chain updateWorkOrder = TransactionChainFactory.getUpdateWorkOrderChain();
					updateWorkOrder.execute(context);
				}
			}
			catch (Exception e) {
				LOGGER.error("Exception occurred during closing Workorder from Alarm", e);
			}
		}
		
		@Override
		public boolean isTemplateNeeded() {
			// TODO Auto-generated method stub
			return false;
		}
		
	},
	FIELD_CHANGE(13) {

		@Override
		public void performAction(JSONObject obj, Context context, WorkflowRuleContext currentRule,
				Object currentRecord) throws Exception {
			// TODO Auto-generated method stub
			if (currentRule.getEvent() == null) {
				currentRule.setEvent(WorkflowRuleAPI.getWorkflowEvent(currentRule.getEventId()));
			}
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			WorkflowEventContext event = currentRule.getEvent();
			List<FacilioField> fields = new ArrayList<>();
			for (Object key : obj.keySet()) {
				FacilioField field = modBean.getField((String) key, event.getModule().getName());
				if (field != null) {
					fields.add(field);
				}
			}
			
			UpdateRecordBuilder<ModuleBaseWithCustomFields> updateBuilder = new UpdateRecordBuilder<ModuleBaseWithCustomFields>()
																				.fields(fields)
																				.module(event.getModule())
																				.andCondition(CriteriaAPI.getIdCondition(((ModuleBaseWithCustomFields) currentRecord).getId(), event.getModule()))
																				;
			updateBuilder.update(obj);
			
		}
		
	},
	CREATE_WORK_ORDER(14) {

		@Override
		public void performAction(JSONObject obj, Context context, WorkflowRuleContext currentRule,
				Object currentRecord) throws Exception {
			// TODO Auto-generated method stub
			
			LOGGER.info("Action::Add Workorder::"+obj);
			
			WorkOrderContext wo = FieldUtil.getAsBeanFromJson(obj, WorkOrderContext.class);
			wo.setSourceType(SourceType.WORKFLOW_RULE);
			FacilioContext woContext = new FacilioContext();
			woContext.put(FacilioConstants.ContextNames.WORK_ORDER, wo);

			Chain addWorkOrder = TransactionChainFactory.getAddWorkOrderChain();
			addWorkOrder.execute(woContext);
			
		}
		
	}
	;

	private int val;

	private ActionType(int val) {
		this.val = val;
	}

	public int getVal() {
		return val;
	}
	
	public boolean isTemplateNeeded() {
		return true;
	}

	abstract public void performAction(JSONObject obj, Context context, WorkflowRuleContext currentRule,
			Object currentRecord) throws Exception;

	public static ActionType getActionType(int actionTypeVal) {
		return TYPE_MAP.get(actionTypeVal);
	}

	private static final Map<Integer, ActionType> TYPE_MAP = Collections.unmodifiableMap(initTypeMap());

	private static final Logger LOGGER = LogManager.getLogger(ActionType.class.getName());

	private static Map<Integer, ActionType> initTypeMap() {
		Map<Integer, ActionType> typeMap = new HashMap<>();
		for (ActionType type : values()) {
			typeMap.put(type.getVal(), type);
		}
		return typeMap;
	}

	public static void main(String arg[]) throws Exception {
		System.out.println("hello world");
		ActionType t = ActionType.SMS_NOTIFICATION;
		JSONObject json = new JSONObject();
		json.put("to", "+919840425388");
		json.put("message", "hello world");
		t.performAction(json, null, null, null);
	}
	
	private static boolean checkIfActiveUserFromEmail(String email) throws Exception {
		UserBean userBean = (UserBean) BeanFactory.lookup("UserBean");
		User user = userBean.getUserFromEmail(email);
		return user != null && user.getUserStatus();
	}
	
	private static boolean checkIfActiveUserFromPhone(String phone) throws Exception {
		UserBean userBean = (UserBean) BeanFactory.lookup("UserBean");
		User user = userBean.getUserFromPhone(phone);
		return user != null && user.getUserStatus();
	}
	
	private static boolean checkIfActiveUserFromId(long ouid) throws Exception {
		UserBean userBean = (UserBean) BeanFactory.lookup("UserBean");
		User user = userBean.getUser(ouid);
		return user != null && user.getUserStatus();
	}
}