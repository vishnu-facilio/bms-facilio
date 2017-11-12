package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.impl.ChainBase;

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
		Chain c = new ChainBase();
		addReportCommands(c);
		addSummaryReportCommand(c);
		addCleanUpCommand(c);
		return c;
	}
	public static Chain getNumericReportChain() {
		Chain c = new ChainBase();
		addReportCommands(c);
		addSummaryReportCommand(c);
		addCleanUpCommand(c);
		return c;
	}
	public static Chain getTabularReportChain() {
		Chain c = new ChainBase();
		addReportCommands(c);
		addSummaryReportCommand(c);
		addCleanUpCommand(c);
		return c;
	}
	public static Chain getTopNSummaryReportChain() {
		Chain c = new ChainBase();
		addTopNReportCommand(c);
		addReportCommands(c);
		addSummaryReportCommand(c);
		addCleanUpCommand(c);
		return c;
	}
	public static Chain getWorkOrderReportChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForWorkOrder());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new SetJoinsCommand());
		c.addCommand(new SetOrgIdConditionCommand());
		c.addCommand(new LoadReportTypeChainCommand());
		addCleanUpCommand(c);
		return c;
	}
	public static Chain getWorkOrderRequestReportChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForWorkOrderRequest());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new SetJoinsCommand());
		c.addCommand(new SetOrgIdConditionCommand());
		c.addCommand(new LoadReportTypeChainCommand());
		addCleanUpCommand(c);
		return c;
	}

	private static void addCleanUpCommand(Chain c)
	{
		c.addCommand(new TransactionExceptionHandler());
	}
	
}