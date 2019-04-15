package com.facilio.urjanet.contants;

import com.facilio.urjanet.command.UrjanetLoginCommand;
import org.apache.commons.chain.Chain;
import org.apache.commons.chain.impl.ChainBase;

public class UrjanetConstants {
		
		public static class ContextNames {
			
			public static final String CREDENTIAL = "Credential";		
		}
			public static Chain UrjanetLoginChain()
			{
				Chain c = new ChainBase();
				c.addCommand(new UrjanetLoginCommand());
				return c;
			}
			
}
