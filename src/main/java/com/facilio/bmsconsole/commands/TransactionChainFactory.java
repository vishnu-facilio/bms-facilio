package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;

public class TransactionChainFactory extends FacilioChainFactory {

		public static Chain historicalFormulaCalculationChain() {
			Chain c = getDefaultChain();
			c.addCommand(new HistoricalFormulaCalculationCommand());
			CommonCommandUtil.addCleanUpCommand(c);
			return c;
		}
		
		public static Chain runThroughReadingRuleChain() {
			Chain c = getDefaultChain();
			c.addCommand(new RunThroughReadingRulesCommand());
			CommonCommandUtil.addCleanUpCommand(c);
			return c;
		}
	
	    protected static Chain getDefaultChain()
	    {
	    	    return new TransactionChain();
	    }
}
