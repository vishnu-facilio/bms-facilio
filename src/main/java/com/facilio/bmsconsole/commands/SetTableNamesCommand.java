package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.constants.FacilioConstants;

public class SetTableNamesCommand implements Command {

	private String moduleName, tableName;
	
	public SetTableNamesCommand(String moduleName, String tableName) {
		// TODO Auto-generated constructor stub
		this.moduleName = moduleName;
		this.tableName = tableName;
	}
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME, tableName);
		
		return false;
	}
	
	public static SetTableNamesCommand getForTicketStatus() {
		return new SetTableNamesCommand("ticketstatus", "TicketStatus");
	}
	
	public static SetTableNamesCommand getForTicketPriority() {
		return new SetTableNamesCommand("ticketpriority", "TicketPriority");
	}
	
	public static SetTableNamesCommand getForTicketCategory() {
		return new SetTableNamesCommand("ticketcategory", "TicketCategory");
	}
	
	public static SetTableNamesCommand getForTicket() {
		return new SetTableNamesCommand("ticket", "Tickets");
	}
	
	public static SetTableNamesCommand getForWorkOrder() {
		return new SetTableNamesCommand("workorder", "WorkOrders");
	}
	
	public static SetTableNamesCommand getForTask() {
		return new SetTableNamesCommand("task", "Tasks");
	}
	
	public static SetTableNamesCommand getForTaskStatus() {
		return new SetTableNamesCommand("taskstatus", "TaskStatus");
	}
	
	public static SetTableNamesCommand getForCampus() {
		return new SetTableNamesCommand("campus", "Campus");
	}
	
	public static SetTableNamesCommand getForBuilding() {
		return new SetTableNamesCommand("building", "Building");
	}
	
	public static SetTableNamesCommand getForFloor() {
		return new SetTableNamesCommand("floor", "Floor");
	}
	
	public static SetTableNamesCommand getForSpace() {
		return new SetTableNamesCommand("space", "Space");
	}
	
	public static SetTableNamesCommand getForZone() {
		return new SetTableNamesCommand("zone", "Zone");
	}

	public static SetTableNamesCommand getForLocation(){
		return new SetTableNamesCommand("location","Locations");
	}
	
	public static SetTableNamesCommand getForSkill(){
		return new SetTableNamesCommand("skill","Skills");
	}
}
