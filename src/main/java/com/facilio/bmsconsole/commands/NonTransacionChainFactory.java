package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.impl.ChainBase;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;

public class NonTransacionChainFactory {
	private static Logger LOGGER = LogManager.getLogger(NonTransacionChainFactory.class.getName());
	
	public static Chain fetchReportDataChain() {
		Chain c = new ChainBase();
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
}
