package com.facilio.bmsconsole.actions;

import com.facilio.fw.OrgInfo;
import com.opensymphony.xwork2.ActionSupport;

public class ApiAction extends ActionSupport {

	@Override
	public String execute() throws Exception {
		// TODO Auto-generated method stub
		System.out.println("Org Info is :"+OrgInfo.getCurrentOrgInfo());
		return super.execute();
	}
	

}
