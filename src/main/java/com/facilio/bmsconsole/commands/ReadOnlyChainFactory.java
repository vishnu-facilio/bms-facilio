package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.impl.ChainBase;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;

public class ReadOnlyChainFactory extends FacilioChainFactory {
	private static Logger LOGGER = LogManager.getLogger(ReadOnlyChainFactory.class.getName());
	   protected static Chain getDefaultChain()
	    {
	    	    return new ChainBase();
	    }
	
	public static Chain fetchReportDataChain() {
		Chain c = new ChainBase();
		c.addCommand(new FetchReportDataCommand());
		c.addCommand(new TransformReportDataCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain fetchReadingReportChain() {
		Chain c = new ChainBase();
		c.addCommand(new CreateReadingAnalyticsReportCommand());
		c.addCommand(fetchReportDataChain());
		return c;
	}
}
