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
	
	public static SetTableNamesCommand getForTicketType() {
		return new SetTableNamesCommand("tickettype", "TicketType");
	}
	
	public static SetTableNamesCommand getForAssetCategory() {
		return new SetTableNamesCommand("assetcategory", "AssetCategory");
	}
	
	public static SetTableNamesCommand getForAssetDepartment() {
		return new SetTableNamesCommand("assetdepartment", "AssetDepartment");
	}
	
	public static SetTableNamesCommand getForAssetType() {
		return new SetTableNamesCommand("assettype", "AssetType");
	}
	
	public static SetTableNamesCommand getForTicket() {
		return new SetTableNamesCommand("ticket", "Tickets");
	}
	
	public static SetTableNamesCommand getForWorkOrder() {
		return new SetTableNamesCommand("workorder", "WorkOrders");
	}
	
	public static SetTableNamesCommand getForWorkOrderRequest() {
		return new SetTableNamesCommand("workorderrequest", "WorkOrderRequests");
	}
	
	public static SetTableNamesCommand getForAlarm() {
		return new SetTableNamesCommand("alarm", "Alarms");
	}
	
	public static SetTableNamesCommand getForReadingAlarm() {
		return new SetTableNamesCommand(FacilioConstants.ContextNames.READING_ALARM, "Reading_Alarms");
	}
	
	public static SetTableNamesCommand getForTask() {
		return new SetTableNamesCommand("task", "Tasks");
	}
	
	public static SetTableNamesCommand getForTaskStatus() {
		return new SetTableNamesCommand("taskstatus", "TaskStatus");
	}
	
	public static SetTableNamesCommand getForSite() {
		return new SetTableNamesCommand("site", "Site");
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
	
	public static SetTableNamesCommand getForAsset(){
		return new SetTableNamesCommand("asset","Assets");
	}
	
	public static SetTableNamesCommand getForEnergyMeter(){
		return new SetTableNamesCommand("energymeter","Energy_Meter");
	}
	public static SetTableNamesCommand getForEnergyMeterPurpose(){
		return new SetTableNamesCommand("energymeterpurpose","Energy_Meter_Purpose");
	}
	public static SetTableNamesCommand getForController(){
		return new SetTableNamesCommand("controller","Controller");
	}
	public static SetTableNamesCommand getForSpaceCategory(){
		return new SetTableNamesCommand("spacecategory","Space_Category");
	}
}
