package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;
import org.apache.commons.chain.Filter;

public class TransactionExceptionHandler implements Filter{

	@Override
	public boolean execute(Context context) throws Exception {
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
			e.printStackTrace();
		}
		
		return false;
	}

}
