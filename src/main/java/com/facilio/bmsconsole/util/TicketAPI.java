package com.facilio.bmsconsole.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AttachmentContext;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.GroupContext;
import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.TicketCategoryContext;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.context.TicketPriorityContext;
import com.facilio.bmsconsole.context.TicketStatusContext;
import com.facilio.bmsconsole.context.UserContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.DBUtil;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.transaction.FacilioConnectionPool;

public class TicketAPI {
	
	private static Logger logger = Logger.getLogger(TicketAPI.class.getName());
	
	public static List<AttachmentContext> getRelatedAttachments(long ticketId, Connection conn) throws SQLException 
	{
		List<AttachmentContext> attachments = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try 
		{
			pstmt = conn.prepareStatement("SELECT * FROM File "
					+ " INNER JOIN Ticket_Attachment ON File.FILE_ID = Ticket_Attachment.FILE_ID"
					+ " WHERE Ticket_Attachment.TICKET_ID = ?");
			pstmt.setLong(1, ticketId);
			rs = pstmt.executeQuery();
			while(rs.next()) 
			{
				AttachmentContext ac = new AttachmentContext();
				ac.setAttachmentId(rs.getLong("TICKET_ATTACHMENT_ID"));
				ac.setFileId(rs.getLong("FILE_ID"));
				ac.setOrgId(rs.getLong("ORGID"));
				ac.setFileName(rs.getString("FILE_NAME"));
				ac.setFileSize(rs.getLong("FILE_SIZE"));
				ac.setContentType(rs.getString("CONTENT_TYPE"));
				ac.setUploadedBy(rs.getLong("UPLOADED_BY"));
				ac.setUploadedTime(rs.getLong("UPLOADED_TIME"));
				attachments.add(ac);
			}
		}
		catch (SQLException e) 
		{
			logger.log(Level.SEVERE, "Exception while getting related attachments." +e.getMessage(), e);
			throw e;
		}
		finally 
		{
			DBUtil.closeAll(pstmt, rs);
		}
		return attachments;
	}
	
	public static TicketStatusContext getStatus(long orgId, String status) throws Exception
	{
		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection())
		{
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			SelectRecordsBuilder<TicketStatusContext> builder = new SelectRecordsBuilder<TicketStatusContext>()
																.connection(conn)
																.table("TicketStatus")
																.moduleName("ticketstatus")
																.beanClass(TicketStatusContext.class)
																.select(modBean.getAllFields("ticketstatus"))
																.andCustomWhere("ORGID = ? AND STATUS = ?", orgId, status)
																.orderBy("ID");
			List<TicketStatusContext> statuses = builder.get();
			return statuses.get(0);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public static TicketCategoryContext getCategory(long orgId, String category) throws Exception
	{
		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection())
		{
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			SelectRecordsBuilder<TicketCategoryContext> builder = new SelectRecordsBuilder<TicketCategoryContext>()
																.connection(conn)
																.table("TicketCategory")
																.moduleName(FacilioConstants.ContextNames.TICKET_CATEGORY)
																.beanClass(TicketCategoryContext.class)
																.select(modBean.getAllFields(FacilioConstants.ContextNames.TICKET_CATEGORY))
																.andCustomWhere("ORGID = ? AND NAME = ?", orgId, category)
																.orderBy("ID");
			List<TicketCategoryContext> categories = builder.get();
			return categories.get(0);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public static TicketStatusContext getStatus(long orgId, long id) throws Exception
	{
		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection())
		{
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			SelectRecordsBuilder<TicketStatusContext> builder = new SelectRecordsBuilder<TicketStatusContext>()
																.connection(conn)
																.table("TicketStatus")
																.moduleName("ticketstatus")
																.beanClass(TicketStatusContext.class)
																.select(modBean.getAllFields("ticketstatus"))
																.andCustomWhere("ORGID = ? AND ID = ?", orgId, id)
																.orderBy("ID");
			List<TicketStatusContext> statuses = builder.get();
			return statuses.get(0);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public static List<TaskContext> getRelatedTasks(long ticketId, Connection conn) throws Exception 
	{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean", OrgInfo.getCurrentOrgInfo().getOrgid());
		List<FacilioField> fields = modBean.getAllFields("task");
		
		SelectRecordsBuilder<TaskContext> builder = new SelectRecordsBuilder<TaskContext>()
				.connection(conn)
				.table("Tasks")
				.moduleName(FacilioConstants.ContextNames.TASK)
				.beanClass(TaskContext.class)
				.select(fields)
				.andCustomWhere("PARENT_TICKET_ID = ?", ticketId)
				.orderBy("ID");

		List<TaskContext> tasks = builder.get();	
		return tasks;
	}
	
	public static List<NoteContext> getRelatedNotes(long ticketId, Connection conn) throws SQLException 
	{
		List<NoteContext> notes = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try 
		{
			pstmt = conn.prepareStatement("SELECT * FROM Notes "
					+ " INNER JOIN Ticket_Note ON Notes.NOTEID = Ticket_Note.NOTEID"
					+ " WHERE Ticket_Note.TICKET_ID = ?");
			pstmt.setLong(1, ticketId);
			rs = pstmt.executeQuery();
			while(rs.next()) 
			{
				NoteContext nc = new NoteContext();
				nc.setNoteId(rs.getLong("NOTEID"));
				nc.setBody(rs.getString("BODY"));
				nc.setCreationTime(rs.getLong("CREATION_TIME"));
				nc.setOwnerId(rs.getLong("OWNERID"));
				notes.add(nc);
			}
		}
		catch (SQLException e) 
		{
			logger.log(Level.SEVERE, "Exception while getting all tasks" +e.getMessage(), e);
			throw e;
		}
		finally 
		{
			DBUtil.closeAll(pstmt, rs);
		}
		return notes;
	}
	
	public static long addTicketNote(long ticketId, long noteId, Connection conn) throws Exception
	{
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Long areaId = null;
		try
		{
			pstmt = conn.prepareStatement("INSERT INTO Ticket_Note (TICKET_ID, NOTEID) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
			pstmt.setLong(1, ticketId);
			pstmt.setLong(2, noteId);
			
			if(pstmt.executeUpdate() < 1) 
			{
				throw new RuntimeException("Unable to add Ticket note");
			}
			else 
			{
				rs = pstmt.getGeneratedKeys();
				rs.next();
				areaId = rs.getLong(1);
			}
		}
		catch(SQLException | RuntimeException e) 
		{
			logger.log(Level.SEVERE, "Exception while adding work order note" +e.getMessage(), e);
			throw e;
		}
		finally 
		{
			DBUtil.closeAll(pstmt, rs);
		}
		return areaId;
	}
	
	public static void loadRelatedModules(TicketContext ticket, Connection conn) throws Exception {
		if(ticket != null) {
			ticket.setTasks(getRelatedTasks(ticket.getId(), conn));
			ticket.setNotes(getRelatedNotes(ticket.getId(), conn));
			ticket.setAttachments(getRelatedAttachments(ticket.getId(), conn));
		}
	}
	
	private static List<FacilioField> maxSerialNumberField = null;
	public static long getMaxSerialNumberOfOrg(long orgId) throws Exception {
		
		if(maxSerialNumberField == null) {
			FacilioField field = new FacilioField();
			field.setColumnName("MAX(SERIAL_NUMBER)");
			field.setName("maxSerialNumber");
			field.setDataType(FieldType.NUMBER);
			
			maxSerialNumberField = new ArrayList<>();
			maxSerialNumberField.add(field);
		}
		
		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
			GenericSelectRecordBuilder builder =  new GenericSelectRecordBuilder()
														.connection(conn)
														.select(maxSerialNumberField)
														.table("Tickets")
														.andCustomWhere("ORGID = ?", orgId);
			
			List<Map<String, Object>> maxValue = builder.get();
			if(maxValue != null && !maxValue.isEmpty() && maxValue.get(0).get("maxSerialNumber") != null) {
				return (long) maxValue.get(0).get("maxSerialNumber");
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
		return 0;
	}
	
	public static void updateTicketStatus(TicketContext ticket) throws Exception {
		TicketStatusContext status = ticket.getStatus();
		
		if(status != null) {
			status = TicketAPI.getStatus(OrgInfo.getCurrentOrgInfo().getOrgid(), status.getId());
		}
		else {
			ticket.setStatus(TicketAPI.getStatus(OrgInfo.getCurrentOrgInfo().getOrgid(), "Submitted"));
		}
		
		if(ticket.getAssignedTo() != null && (status == null || status.getStatus().equals("Submitted"))) {
			ticket.setStatus(TicketAPI.getStatus(OrgInfo.getCurrentOrgInfo().getOrgid(), "Assigned"));
		}
	}
	
public static Map<Long, TicketContext> getTickets(String ids, Connection conn) throws Exception {
		
		Condition idCondition = new Condition();
		idCondition.setField(FieldFactory.getIdField(ModuleFactory.getTicketsModule()));
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
		
		loadTicketStatus(tickets.values());
		loadTicketPriority(tickets.values());
		loadTicketCategory(tickets.values());
		loadTicketUsers(tickets.values());
		loadTicketGroups(tickets.values());
		loadTicketSpaces(tickets.values());
		
		return tickets;
	}
	
	
	public static void loadTicketLookups(Collection<? extends TicketContext> tickets) throws Exception {
		loadTicketStatus(tickets);
		loadTicketPriority(tickets);
		loadTicketCategory(tickets);
		loadTicketUsers(tickets);
		loadTicketGroups(tickets);
		loadTicketSpaces(tickets);
	}
	
	private static void loadTicketStatus(Collection<? extends TicketContext> tickets) throws Exception {
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
	
	private static void loadTicketPriority(Collection<? extends TicketContext> tickets) throws Exception {
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
	
	private static void loadTicketCategory(Collection<? extends TicketContext> tickets) throws Exception {
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
	
	private static void loadTicketUsers(Collection<? extends TicketContext> tickets) throws Exception {
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
	
	private static void loadTicketGroups(Collection<? extends TicketContext> tickets) throws Exception {
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
	
	private static void loadTicketSpaces(Collection<? extends TicketContext> tickets) throws Exception {
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
