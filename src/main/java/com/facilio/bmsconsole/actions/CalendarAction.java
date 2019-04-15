package com.facilio.bmsconsole.actions;

import com.opensymphony.xwork2.ActionSupport;

import java.util.logging.Logger;

public class CalendarAction extends ActionSupport
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(CalendarAction.class.getName());
	
	public String show()
	{
		return SUCCESS;
	}
	
	public String centraldispatch()
	{
		return SUCCESS;
	}
}
