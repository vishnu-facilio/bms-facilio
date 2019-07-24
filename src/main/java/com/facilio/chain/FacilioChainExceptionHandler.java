package com.facilio.chain;

import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;

import com.facilio.bmsconsole.commands.FacilioCommand;

public class FacilioChainExceptionHandler extends FacilioCommand {

	private static org.apache.log4j.Logger log = LogManager.getLogger(FacilioChainExceptionHandler.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean postprocess(Context context, Exception exception) {
		// TODO Auto-generated method stub
		
		try {
			if(context instanceof FacilioContext) {
				FacilioContext fc = (FacilioContext)context;
				if(exception != null) {
					fc.rollback();
				}
				else {
					fc.commit();
				}
			}
		}
		catch(Exception e) {
			log.info("Exception occurred ", e);
		}
		
		return false;
	}

}
