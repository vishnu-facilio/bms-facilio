package com.facilio.modules;

import java.util.HashMap;
import java.util.Map;

import com.facilio.accounts.util.AccountUtil;

public class EmaarOrgValueGenerator extends ValueGenerator {

	static Map<Long,Long> appVsOrgMap = new HashMap<>();
	
	static {
		appVsOrgMap.put(2530l, 725647l);		//BK
		appVsOrgMap.put(2531l, 725649l);		//EMG
		appVsOrgMap.put(2532l, 725648l);		//EHG
		appVsOrgMap.put(2533l, 725650l);		//ERG	
		appVsOrgMap.put(2534l, 725651l);		//EP
		appVsOrgMap.put(2535l, 725652l);		//EFM
	}
	
	@Override
	public Object generateValueForCondition(int appType) {
		// TODO Auto-generated method stub
		
		if(AccountUtil.getCurrentAccount().getApp() != null) {
			long appId = AccountUtil.getCurrentAccount().getApp().getId();
			
			return String.valueOf(appVsOrgMap.get(appId));
		}
		return null;
	}

	@Override
	public String getValueGeneratorName() {
		// TODO Auto-generated method stub
		return "Emaar Org Value Generator";
	}

	@Override
	public String getLinkName() {
		// TODO Auto-generated method stub
		return "com.facilio.modules.EmaarOrgValueGenerator";
	}

	@Override
	public String getModuleName() {
		// TODO Auto-generated method stub
		return "custom_organization";
	}

	@Override
	public Boolean getIsHidden() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getOperatorId() {
		// TODO Auto-generated method stub
		return 36;
	}

}
