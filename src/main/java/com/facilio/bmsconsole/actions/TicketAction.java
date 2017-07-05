package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.ActionForm;
import com.facilio.bmsconsole.context.FormLayout;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.fields.FacilioField;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.OrgInfo;
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
		
		List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		customFieldNames = new ArrayList<>();
		for(int i=TicketContext.DEFAULT_TICKET_FIELDS.length; i<fields.size(); i++) {
			FacilioField field = fields.get(i);
			customFieldNames.add(field.getName());
		}
		
		Map mp = ActionContext.getContext().getParameters();
		String isajax =((org.apache.struts2.dispatcher.Parameter)mp.get("ajax")).getValue();
		
		System.out.println("isajaxisajax"+isajax);;

		if(isajax!=null && isajax.equals("true")){
			return "ajaxsuccess";
		}
		
		return SUCCESS;
	}
	public List getFormlayout()
	{
		return FormLayout.getNewTicketLayout();
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
	
	private List<String> customFieldNames;
	public List<String> getCustomFieldNames() {
		return customFieldNames;
	}
	public void setCustomFieldNames(List<String> customFieldNames) {
		this.customFieldNames = customFieldNames;
	}
	
	//Add Ticket Props
	public String addTicket() throws Exception {
		
		System.out.println(OrgInfo.getCurrentOrgInfo().getOrgid());
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.TICKET, ticket);
		
		Command addTicket = FacilioChainFactory.getAddTicketChain();
		addTicket.execute(context);
		setTicketId(ticket.getTicketId());
		return SUCCESS;
	}
 	
 	//View Ticket Props
	public String viewTicket() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.TICKET_ID, getTicketId());
		
		Chain getTicketChain = FacilioChainFactory.getTicketDetailsChain();
		getTicketChain.execute(context);
		
		setTicket((TicketContext) context.get(FacilioConstants.ContextNames.TICKET));
		setTasks((List<TaskContext>) context.get(FacilioConstants.ContextNames.TASK_LIST));
		
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
 		Chain getAllTicketsChain = FacilioChainFactory.getAllTicketsChain();
 		getAllTicketsChain.execute(context);
 		
		setModuleName((String) context.get(FacilioConstants.ContextNames.MODULE_DISPLAY_NAME));
		setTickets((List<TicketContext>) context.get(FacilioConstants.ContextNames.TICKET_LIST));
		
		return SUCCESS;
	}
 	
 	private List<TicketContext> tickets = null;
	public List<TicketContext> getTickets() {
		return tickets;
	}
	public void setTickets(List<TicketContext> tickets) {
		this.tickets = tickets;
	}
}
