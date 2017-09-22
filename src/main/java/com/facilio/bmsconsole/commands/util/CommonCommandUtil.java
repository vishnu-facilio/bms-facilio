package com.facilio.bmsconsole.commands.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.GroupContext;
import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsole.context.RequesterContext;
import com.facilio.bmsconsole.context.SupportEmailContext;
import com.facilio.bmsconsole.context.TicketCategoryContext;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.context.TicketPriorityContext;
import com.facilio.bmsconsole.context.TicketStatusContext;
import com.facilio.bmsconsole.context.UserContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.GroupAPI;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsole.util.UserAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.transaction.FacilioConnectionPool;

public class CommonCommandUtil {
	public static NoteContext getNoteContextFromRS(ResultSet rs) throws SQLException {
		NoteContext context = new NoteContext();
		context.setNoteId(rs.getLong("NOTEID"));
		context.setOrgId(rs.getLong("ORGID"));
		context.setOwnerId(rs.getLong("OWNERID"));
		context.setCreationTime(rs.getLong("CREATION_TIME"));
		context.setTitle(rs.getString("TITLE"));
		context.setBody(rs.getString("BODY"));
		return context;
	}
	
	public static FacilioField getFieldFromRS(ResultSet rs) throws SQLException {
		FacilioField field = new FacilioField();
		field.setFieldId(rs.getLong("Fields.FIELDID"));
		field.setOrgId(rs.getLong("Fields.ORGID"));
		field.setModuleId(rs.getLong("Fields.MODULEID"));
		field.setName(rs.getString("Fields.NAME"));
		field.setDisplayName(rs.getString("Fields.DISPLAY_NAME"));
		field.setDisplayType(rs.getInt("Fields.DISPLAY_TYPE"));
		field.setColumnName(rs.getString("Fields.COLUMN_NAME"));
		field.setSequenceNumber(rs.getInt("Fields.SEQUENCE_NUMBER"));
		field.setDataType(FieldType.getCFType(rs.getInt("Fields.DATA_TYPE")));
		field.setDataTypeCode(rs.getInt("Fields.DATA_TYPE"));
		field.setDefault(rs.getBoolean("Fields.IS_DEFAULT"));
		field.setMainField(rs.getBoolean("Fields.IS_MAIN_FIELD"));
		field.setRequired(rs.getBoolean("Fields.REQUIRED"));
		field.setDisabled(rs.getBoolean("Fields.DISABLED"));
		field.setStyleClass(rs.getString("Fields.STYLE_CLASS"));
		field.setIcon(rs.getString("Fields.ICON"));
		field.setPlaceHolder(rs.getString("Fields.PLACE_HOLDER"));
		
		return field;
	}
	
	public static FacilioView getViewFromRS(ResultSet rs) throws SQLException {
		FacilioView view = new FacilioView();
		view.setViewId(rs.getLong("VIEWID"));
		view.setOrgId(rs.getLong("ORGID"));
		view.setName(rs.getString("NAME"));
		view.setDisplayName(rs.getString("DISPLAY_NAME"));
		view.setModuleId(rs.getLong("MODULEID"));
		view.setCriteriaId(rs.getLong("CRITERIAID"));
		return view;
	}
	
	public static FacilioModule getModuleFromRS(ResultSet rs) throws SQLException {
		long moduleId = rs.getLong("MODULEID");
		if(moduleId != 0) {
		FacilioModule module = new FacilioModule();
			module.setModuleId(moduleId);
			module.setOrgId(rs.getLong("ORGID"));
			module.setName(rs.getString("NAME"));
			module.setDisplayName(rs.getString("DISPLAY_NAME"));
			module.setTableName(rs.getString("TABLE_NAME"));
			return module;
		}
		return null;
	}
	
	public static void setFwdMail(SupportEmailContext supportEmail) {
		String actualEmail = supportEmail.getActualEmail();
		String orgEmailDomain = "@"+OrgInfo.getCurrentOrgInfo().getOrgDomain()+".facilio.com";
		
		if(actualEmail.toLowerCase().endsWith(orgEmailDomain)) {
			supportEmail.setFwdEmail(actualEmail);
			supportEmail.setVerified(true);
		}
		else {
			String[] emailSplit = actualEmail.toLowerCase().split("@");
			if(emailSplit.length < 2) {
				throw new IllegalArgumentException("Actual email address of SupportEmail is not valid");
			}
			supportEmail.setFwdEmail(emailSplit[1].replaceAll("\\.", "")+emailSplit[0]+orgEmailDomain);
			supportEmail.setVerified(false);
		}
	}
	
	public static Map<Long, RequesterContext> getRequesters(String ids, Connection conn) throws Exception {
		
		FacilioField field = new FacilioField();
		field.setName("requesterId");
		field.setDataType(FieldType.NUMBER);
		field.setColumnName("REQUESTER_ID");
		field.setModuleTableName("Requester");
		
		Condition idCondition = new Condition();
		idCondition.setField(field);
		idCondition.setOperator(NumberOperators.EQUALS);
		idCondition.setValue(ids);
		
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
											.connection(conn)
											.table("Requester")
											.select(FieldFactory.getRequesterFields())
											.andCondition(idCondition);
		List<Map<String, Object>> requesterList = builder.get();
		
		Map<Long, RequesterContext> requesters = new HashMap<>();
		for(Map<String, Object> requester : requesterList)
		{
			requesters.put((Long) requester.get("requesterId"), getRequesterObject(requester));
		}
		return requesters;
	}
	
	private static RequesterContext getRequesterObject(Map<String, Object> requester) throws SQLException {
		
		RequesterContext rc = new RequesterContext();
		rc.setEmail((String) requester.get("email"));
		rc.setName((String) requester.get("name"));
		rc.setId((Long) requester.get("requesterId"));
		return rc;
	}
	
	public static Map<Long, TicketContext> getTickets(String ids, Connection conn) throws Exception {
		
		Condition idCondition = new Condition();
		idCondition.setField(FieldFactory.getIdField("Tickets"));
		idCondition.setOperator(NumberOperators.EQUALS);
		idCondition.setValue(ids);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TICKET);
		
		SelectRecordsBuilder<TicketContext> selectBuilder = new SelectRecordsBuilder<TicketContext>()
																	.connection(conn)
																	.select(fields)
																	.table("Tickets")
																	.moduleName(FacilioConstants.ContextNames.TICKET)
																	.beanClass(TicketContext.class)
																	.andCondition(idCondition)
																	.maxLevel(0);
		Map<Long, TicketContext> tickets = selectBuilder.getAsMap();
		
		CommonCommandUtil.loadTicketStatus(tickets.values());
		CommonCommandUtil.loadTicketPriority(tickets.values());
		CommonCommandUtil.loadTicketCategory(tickets.values());
		CommonCommandUtil.loadTicketUsers(tickets.values());
		CommonCommandUtil.loadTicketGroups(tickets.values());
		CommonCommandUtil.loadTicketSpaces(tickets.values());
		
		return tickets;
	}
	
	private static void loadTicketStatus(Collection<TicketContext> tickets) throws Exception {
		if(tickets != null && !tickets.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TICKET_STATUS);
			
			try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
				SelectRecordsBuilder<TicketStatusContext> selectBuilder = new SelectRecordsBuilder<TicketStatusContext>()
																				.connection(conn)
																				.select(fields)
																				.table("TicketStatus")
																				.moduleName(FacilioConstants.ContextNames.TICKET_STATUS)
																				.beanClass(TicketStatusContext.class);
				Map<Long, TicketStatusContext> statuses = selectBuilder.getAsMap();
				
				for(TicketContext ticket : tickets) {
					TicketStatusContext status = ticket.getStatus();
					if(status != null) {
						ticket.setStatus(statuses.get(status.getId()));
					}
				}
			}
			catch(Exception e) {
				e.printStackTrace();
				throw e;
			}
		}
	}
	
	private static void loadTicketPriority(Collection<TicketContext> tickets) throws Exception {
		if(tickets != null && !tickets.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TICKET_PRIORITY);
			
			try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
				SelectRecordsBuilder<TicketPriorityContext> selectBuilder = new SelectRecordsBuilder<TicketPriorityContext>()
																				.connection(conn)
																				.select(fields)
																				.table("TicketPriority")
																				.moduleName(FacilioConstants.ContextNames.TICKET_PRIORITY)
																				.beanClass(TicketPriorityContext.class);
				Map<Long, TicketPriorityContext> priorities = selectBuilder.getAsMap();
				
				for(TicketContext ticket : tickets) {
					TicketPriorityContext priority = ticket.getPriority();
					if(priority != null) {
						ticket.setPriority(priorities.get(priority.getId()));
					}
				}
			}
			catch(Exception e) {
				e.printStackTrace();
				throw e;
			}
		}
	}
	
	private static void loadTicketCategory(Collection<TicketContext> tickets) throws Exception {
		if(tickets != null && !tickets.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TICKET_CATEGORY);
			
			try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
				SelectRecordsBuilder<TicketCategoryContext> selectBuilder = new SelectRecordsBuilder<TicketCategoryContext>()
																				.connection(conn)
																				.select(fields)
																				.table("TicketCategory")
																				.moduleName(FacilioConstants.ContextNames.TICKET_CATEGORY)
																				.beanClass(TicketCategoryContext.class);
				Map<Long, TicketCategoryContext> categories = selectBuilder.getAsMap();
				
				for(TicketContext ticket : tickets) {
					TicketCategoryContext category = ticket.getCategory();
					if(category != null) {
						ticket.setCategory(categories.get(category.getId()));
					}
				}
			}
			catch(Exception e) {
				e.printStackTrace();
				throw e;
			}
		}
	}
	
	private static void loadTicketUsers(Collection<TicketContext> tickets) throws Exception {
		if(tickets != null && !tickets.isEmpty()) {
			List<UserContext> users = UserAPI.getUsersOfOrg(OrgInfo.getCurrentOrgInfo().getOrgid());
			
			Map<Long, UserContext> userMap = new HashMap<>();
			for(UserContext user : users) {
				userMap.put(user.getId(), user);
			}
			
			for(TicketContext ticket : tickets) {
				UserContext assignTo = ticket.getAssignedTo();
				if(assignTo != null) {
					ticket.setAssignedTo(userMap.get(assignTo.getId()));
				}
			}
		}
	}
	
	private static void loadTicketGroups(Collection<TicketContext> tickets) throws Exception {
		if(tickets != null && !tickets.isEmpty()) {
			List<GroupContext> groups = GroupAPI.getGroupsOfOrg(OrgInfo.getCurrentOrgInfo().getOrgid());
			
			Map<Long, GroupContext> groupMap = new HashMap<>();
			for(GroupContext group : groups) {
				groupMap.put(group.getId(), group);
			}
			
			for(TicketContext ticket : tickets) {
				GroupContext assignGroup = ticket.getAssignmentGroup();
				if(assignGroup != null) {
					ticket.setAssignmentGroup(groupMap.get(assignGroup.getId()));
				}
			}
		}
	}
	
	private static void loadTicketSpaces(Collection<TicketContext> tickets) throws Exception {
		if(tickets != null && !tickets.isEmpty()) {
			try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
				List<BaseSpaceContext> spaces = SpaceAPI.getAllBaseSpaces(OrgInfo.getCurrentOrgInfo().getOrgid(), conn);
				
				Map<Long, BaseSpaceContext> spaceMap = new HashMap<>();
				for(BaseSpaceContext space : spaces) {
					spaceMap.put(space.getId(), space);
				}
				
				for(TicketContext ticket : tickets) {
					BaseSpaceContext space = ticket.getSpace();
					if(space != null) {
						ticket.setSpace(spaceMap.get(space.getId()));
					}
				}
			}
			catch(Exception e) {
				e.printStackTrace();
				throw e;
			}
		}
	}
}
