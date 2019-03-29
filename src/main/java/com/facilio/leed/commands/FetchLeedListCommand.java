package com.facilio.leed.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.billing.util.TenantBillingAPI;
import com.facilio.leed.constants.LeedConstants;
import com.facilio.leed.context.LeedConfigurationContext;
import com.facilio.leed.util.LeedAPI;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.List;

public class FetchLeedListCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		boolean isLoginRequired = false;
		List<LeedConfigurationContext> leedList = null;
		long orgId = (long)context.get(LeedConstants.ContextNames.ORGID);
		
		TenantBillingAPI.getTenant(1);
		if(LeedAPI.checkIfLoginPresent(orgId))
		{
			isLoginRequired = true;
		}
		else
		{
			isLoginRequired = false;
			leedList = LeedAPI.getLeedConfigurationList(AccountUtil.getCurrentOrg().getOrgId());
		}
		context.put(LeedConstants.ContextNames.LOGINREQ, isLoginRequired);
		context.put(LeedConstants.ContextNames.LEEDLIST, leedList);
		return false;
	}
	
}
