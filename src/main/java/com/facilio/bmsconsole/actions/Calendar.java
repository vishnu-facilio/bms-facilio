package com.facilio.bmsconsole.actions;

import java.util.logging.Logger;

import com.opensymphony.xwork2.ActionSupport;

public class Calendar extends ActionSupport
{
	private static Logger logger = Logger.getLogger(Device.class.getName());
	
	public String show()
	{
		return SUCCESS;
	}
}
