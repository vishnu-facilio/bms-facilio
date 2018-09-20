package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Chain;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;

public class ReadOnlyChainFactory extends FacilioChainFactory {
	private static Logger LOGGER = LogManager.getLogger(ReadOnlyChainFactory.class.getName());
	   protected static Chain getDefaultChain()
	    {
	    	    return new FacilioChain(false);
	    }
	
	public static Chain fetchReportDataChain() {
		Chain c = getDefaultChain();
		c.addCommand(new FilterXFieldCommand());
		c.addCommand(new FetchReportDataCommand());
		c.addCommand(new TransformReportDataCommand());
		c.addCommand(new CalculateVarianceCommand());
		c.addCommand(new FetchReportExtraMeta());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain fetchReadingReportChain() {
		Chain c = getDefaultChain();
		c.addCommand(new CreateReadingAnalyticsReportCommand());
		c.addCommand(fetchReportDataChain());
		return c;
	}
	
	public static Chain fetchWorkorderReportChain() {
		Chain c = getDefaultChain();
		c.addCommand(new CreateWorkOrderAnalyticsReportCommand());
		c.addCommand(fetchReportDataChain());
		return c;
	}
	
	public static Chain fetchModuleDataListChain() {
		Chain c = getDefaultChain();
		c.addCommand(new LoadViewCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new GenericGetModuleDataListCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain fetchModuleDataDetailsChain() {
		Chain c = getDefaultChain();
		c.addCommand(new GenericGetModuleDataDetailCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain fetchLatestReadingDataChain() {
		Chain c = getDefaultChain();
		c.addCommand(new GetLatestReadingDataCommand());
		return c;
	}
	
	public static Chain fetchScheduledReportsChain() {
		Chain c = getDefaultChain();
		c.addCommand(new ScheduledV2ReportListCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
}
