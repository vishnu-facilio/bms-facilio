package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Chain;

public class TransactionChainFactory extends FacilioChainFactory {

	
	
	    protected static Chain getDefaultChain()
	    {
	    	    return new TransactionChain();
	    }
}
