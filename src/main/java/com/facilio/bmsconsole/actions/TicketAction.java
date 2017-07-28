package com.facilio.bmsconsole.actions;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.ActionForm;
import com.facilio.bmsconsole.context.FormLayout;
import com.facilio.bmsconsole.context.RecordSummaryLayout;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.context.ViewLayout;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class TicketAction extends ActionSupport {
	
	//New Ticket props
	public String newTicket() throws Exception {
		FacilioContext context = new FacilioContext();
		Chain newTicket = FacilioChainFactory.getNewTicketChain();
		newTicket.execute(context);
		
		setModuleName((String) context.get(FacilioConstants.ContextNames.MODULE_DISPLAY_NAME));
		setActionForm((ActionForm) context.get(FacilioConstants.ContextNames.ACTION_FORM));
		
		fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		
		Map mp = ActionContext.getContext().getParameters();
		String isajax =((org.apache.struts2.dispatcher.Parameter)mp.get("ajax")).getValue();
		
		System.out.println("isajaxisajax"+isajax);;

		if(isajax!=null && isajax.equals("true")){
			return "ajaxsuccess";
		}
		return SUCCESS;
	}
	
	private List<FacilioField> fields;
	
	public List getFormlayout()
	{
		return FormLayout.getNewTicketLayout(fields);
	}
	public void setFormlayout(List l)
	{
		
	}
	private ActionForm actionForm;
	public ActionForm getActionForm() {
		return actionForm;
	}
	public void setActionForm(ActionForm actionForm) {
		this.actionForm = actionForm;
	}
	
	private String moduleName;
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	private List<Long> attachmentId;
	public List<Long> getAttachmentId() {
		return attachmentId;
	}
	public void setAttachmentId(List<Long> attachmentId) {
		this.attachmentId = attachmentId;
	}
	
	//Add Ticket Props
	public String addTicket() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.TICKET, ticket);
		context.put(FacilioConstants.ContextNames.ATTACHMENT_ID_LIST, getAttachmentId());
		
		Command addTicket = FacilioChainFactory.getAddTicketChain();
		addTicket.execute(context);
		setTicketId(ticket.getId());
		return SUCCESS;
	}
 	
 	//View Ticket Props
	public String viewTicket() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, getTicketId());
		
		Chain getTicketChain = FacilioChainFactory.getTicketDetailsChain();
		getTicketChain.execute(context);
		
		setTicket((TicketContext) context.get(FacilioConstants.ContextNames.TICKET));
		
		List<TaskContext> tasks = (List<TaskContext>) context.get(FacilioConstants.ContextNames.TASK_LIST);
		
		if(tasks != null && tasks.size() > 0) {
			setTasks(tasks);
		}
		
		return SUCCESS;
	}
	
	private TicketContext ticket;
	public TicketContext getTicket() {
		return ticket;
	}
	public void setTicket(TicketContext ticket) {
		this.ticket = ticket;
	}
	
	private List<TaskContext> tasks;
	public List<TaskContext> getTasks() {
		return tasks;
	}
	public void setTasks(List<TaskContext> tasks) {
		this.tasks = tasks;
	}
	
	private long ticketId;
 	public long getTicketId() {
 		return ticketId;
 	}
 	public void setTicketId(long ticketId) {
 		this.ticketId = ticketId;
 	}
	
 	//Ticket List Props
 	public String ticketList() throws Exception {
		// TODO Auto-generated method stub
 		FacilioContext context = new FacilioContext();
 		context.put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		
		System.out.println("View Name : "+getViewName());
 		Chain ticketListChain = FacilioChainFactory.getTicketListChain();
 		ticketListChain.execute(context);
 		
		setModuleName((String) context.get(FacilioConstants.ContextNames.MODULE_DISPLAY_NAME));
		setTickets((List<TicketContext>) context.get(FacilioConstants.ContextNames.TICKET_LIST));
		
		FacilioView cv = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
		if(cv != null) {
			setViewDisplayName(cv.getDisplayName());
		}
		
		return SUCCESS;
	}
 	
 	private List<TicketContext> tickets = null;
	public List<TicketContext> getTickets() {
		return tickets;
	}
	public void setTickets(List<TicketContext> tickets) {
		this.tickets = tickets;
	}
	
	public String getModuleLinkName()
	{
		return FacilioConstants.ContextNames.TICKET;
	}
	
	public ViewLayout getViewlayout()
	{
		return ViewLayout.getViewTicketLayout();
	}
	
	public List<TicketContext> getRecords() 
	{
		return tickets;
	}
	
	private String viewName = null;
	public String getViewName() {
		return viewName;
	}
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}
	
	private String displayName = "All Work Orders";
	public String getViewDisplayName() {
		return displayName;
	}
	public void setViewDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	public RecordSummaryLayout getRecordSummaryLayout()
	{
		return RecordSummaryLayout.getRecordSummaryTicketLayout();
	}
	
	public TicketContext getRecord() 
	{
		return ticket;
	}
}
