package com.facilio.urjanet.contants;

import org.apache.commons.chain.impl.ChainBase;

import com.facilio.chain.FacilioChain;
import com.facilio.urjanet.command.UrjanetLoginCommand;

public class UrjanetConstants {
		
		public static class ContextNames {
			
			public static final String CREDENTIAL = "Credential";		
		}
			public static FacilioChain UrjanetLoginChain()
			{
				FacilioChain c = FacilioChain.getTransactionChain();
				c.addCommand(new UrjanetLoginCommand());
				return c;
			}
			
}
