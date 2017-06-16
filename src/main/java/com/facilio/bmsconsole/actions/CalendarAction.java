package com.facilio.bmsconsole.actions;

import java.util.logging.Logger;

import com.opensymphony.xwork2.ActionSupport;

public class CalendarAction extends ActionSupport
{
	private static Logger logger = Logger.getLogger(CalendarAction.class.getName());
	
	public String show()
	{
		return SUCCESS;
	}
}
