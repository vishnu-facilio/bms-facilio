package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.impl.ChainBase;

public class FacilioChainFactory {

	public static Chain getOrgSignupChain()
	{
		Chain c =new ChainBase();
		c.addCommand(new CreateUserCommand());
		return c;
	}
	
}
