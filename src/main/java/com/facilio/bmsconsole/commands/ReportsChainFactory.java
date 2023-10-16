package com.facilio.bmsconsole.commands;

import com.facilio.chain.FacilioChain;

public class ReportsChainFactory {
	
	private static void addReportCommands(FacilioChain c) {
		//c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadSelectFieldsCommand());
		c.addCommand(new GenerateCondtionsFromFiltersCommand());
		c.addCommand(new SetOrderByCommand());
	}
	private static void addSummaryReportCommand(FacilioChain c) {
		c.addCommand(new GenerateGrpByQueryCommand());
		c.addCommand(new GetSummaryReportCommand());
		c.addCommand(new SetReportResultCommand());
	}
	private static void addTopNReportCommand(FacilioChain c) {
		c.addCommand(new SetTopNReportCommand());
	}
	public static FacilioChain getSummaryReportChain() {
		FacilioChain c = getDefaultChain();
		addReportCommands(c);
		addSummaryReportCommand(c);
		return c;
	}
	public static FacilioChain getNumericReportChain() {
		FacilioChain c = getDefaultChain();
		addReportCommands(c);
		addSummaryReportCommand(c);
		return c;
	}
	public static FacilioChain getTabularReportChain() {
		FacilioChain c = getDefaultChain();
		addReportCommands(c);
		addSummaryReportCommand(c);
		return c;
	}
	public static FacilioChain getTopNTabularReportChain() {
		FacilioChain c = getDefaultChain();
		addTopNReportCommand(c);
		addReportCommands(c);
		addSummaryReportCommand(c);
		return c;
	}
	public static FacilioChain getTopNSummaryReportChain() {
		FacilioChain c = getDefaultChain();
		addTopNReportCommand(c);
		addReportCommands(c);
		addSummaryReportCommand(c);
		return c;
	}
	public static FacilioChain getWorkOrderReportChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForWorkOrder());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new SetJoinsCommand());
		c.addCommand(new SetOrgIdConditionCommand());
		c.addCommand(new LoadReportTypeChainCommand());
		return c;
	}
	public static FacilioChain getWorkOrderRequestReportChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForWorkOrderRequest());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new SetJoinsCommand());
		c.addCommand(new SetOrgIdConditionCommand());
		c.addCommand(new LoadReportTypeChainCommand());
		return c;
	}
	
	
	public static FacilioChain getReportUnderlyingDataChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new LoadViewCommand());
		c.addCommand(new GetReportUnderlyingDataCommand());
		return c;
	}
	
	public static FacilioChain getWoViewScheduleChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new ValidateFromEmailCommand());
		c.addCommand(new AddTemplateCommand());
		c.addCommand(new AddWoViewScheduleCommand());
		return c;
	}
	
	
	public static FacilioChain getWoScheduledViewListChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetWoScheduledViewList());
		return c;
	}
	
	public static FacilioChain updateWoScheduledViewChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new ValidateFromEmailCommand());
		c.addCommand(new AddTemplateCommand());
		c.addCommand(new DeleteWoScheduledViewCommand());
		c.addCommand(new AddWoViewScheduleCommand());
		return c;
	}
	
	public static FacilioChain deleteWoScheduledViewChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new DeleteWoScheduledViewCommand());
		return c;
	}

	public static FacilioChain getScheduledViewChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetScheduledViewCommand());
		return c;
	}
	
	private static FacilioChain getDefaultChain() {
		return FacilioChain.getNonTransactionChain();
    }

}