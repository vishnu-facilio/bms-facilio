package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.context.BusinessHourContext;
import com.facilio.bmsconsole.util.BusinessHoursAPI;
import com.opensymphony.xwork2.ActionSupport;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

public class BusinessHoursAction extends ActionSupport {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String addBussinessHours() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SQLException, RuntimeException {
		if(businessHours != null && !businessHours.isEmpty()) {
			id = BusinessHoursAPI.addBusinessHours(businessHours);
		}
		return SUCCESS;
	}
	
	public String getBusinessHoursList() throws Exception {
		if(id != -1) {
			businessHours = BusinessHoursAPI.getBusinessHours(id);
		}
		return SUCCESS;
	}
	
	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	private List<BusinessHourContext> businessHours;
	public List<BusinessHourContext> getBusinessHours() {
		return businessHours;
	}
	public void setBusinessHours(List<BusinessHourContext> businessHours) {
		this.businessHours = businessHours;
	}
}
