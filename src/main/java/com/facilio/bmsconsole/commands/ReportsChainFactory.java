package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Chain;

import com.facilio.chain.FacilioChain;

public class ReportsChainFactory {
	
	private static void addReportCommands(Chain c) {
		//c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadSelectFieldsCommand());
		c.addCommand(new GenerateCondtionsFromFiltersCommand());
		c.addCommand(new SetOrderByCommand());
	}
	private static void addSummaryReportCommand(Chain c) {
		c.addCommand(new GenerateGrpByQueryCommand());
		c.addCommand(new GetSummaryReportCommand());
		c.addCommand(new SetReportResultCommand());
	}
	private static void addTopNReportCommand(Chain c) {
		c.addCommand(new SetTopNReportCommand());
	}
	public static Chain getSummaryReportChain() {
		Chain c = getDefaultChain();
		addReportCommands(c);
		addSummaryReportCommand(c);
		return c;
	}
	public static Chain getNumericReportChain() {
		Chain c = getDefaultChain();
		addReportCommands(c);
		addSummaryReportCommand(c);
		return c;
	}
	public static Chain getTabularReportChain() {
		Chain c = getDefaultChain();
		addReportCommands(c);
		addSummaryReportCommand(c);
		return c;
	}
	public static Chain getTopNTabularReportChain() {
		Chain c = getDefaultChain();
		addTopNReportCommand(c);
		addReportCommands(c);
		addSummaryReportCommand(c);
		return c;
	}
	public static Chain getTopNSummaryReportChain() {
		Chain c = getDefaultChain();
		addTopNReportCommand(c);
		addReportCommands(c);
		addSummaryReportCommand(c);
		return c;
	}
	public static Chain getWorkOrderReportChain() {
		Chain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForWorkOrder());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new SetJoinsCommand());
		c.addCommand(new SetOrgIdConditionCommand());
		c.addCommand(new LoadReportTypeChainCommand());
		return c;
	}
	public static Chain getWorkOrderRequestReportChain() {
		Chain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForWorkOrderRequest());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new SetJoinsCommand());
		c.addCommand(new SetOrgIdConditionCommand());
		c.addCommand(new LoadReportTypeChainCommand());
		return c;
	}
	
	public static Chain getSendMailReportChain() {
		Chain c = getDefaultChain();
//		c.addCommand(new LoadViewCommand());
//		c.addCommand(new GetReportUnderlyingDataCommand());
		c.addCommand(new SendReportMailCommand());
		return c;
	}
	
	public static Chain getReportUnderlyingDataChain() {
		Chain c = getDefaultChain();
		c.addCommand(new LoadViewCommand());
		c.addCommand(new GetReportUnderlyingDataCommand());
		return c;
	}
	
	public static Chain getReportScheduleChain() {
		Chain c = getDefaultChain();
		c.addCommand(new AddTemplateCommand());
		c.addCommand(new ScheduleReportCommand());
		return c;
	}
	
	public static Chain getScheduledReportsChain() {
		Chain c = getDefaultChain();
		c.addCommand(new ReportScheduledListCommand());
		return c;
	}
	
	public static Chain deleteScheduledReportsChain() {
		Chain c = getDefaultChain();
		c.addCommand(new DeleteScheduledReportsCommand());
		return c;
	}
	
	public static Chain updateScheduledReportsChain() {
		Chain c = getDefaultChain();
		c.addCommand(new AddTemplateCommand());
		c.addCommand(new DeleteScheduledReportsCommand());
		c.addCommand(new ScheduleReportCommand());
		return c;
	}
	
	private static Chain getDefaultChain() {
		return new FacilioChain(false);
    }

}