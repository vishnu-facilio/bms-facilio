package con.facilio.control;

import java.util.ArrayList;
import java.util.List;

import com.facilio.bmsconsole.context.BusinessHoursContext;
import com.facilio.v3.context.V3Context;

public class ControlScheduleContext extends V3Context {

	private static final long serialVersionUID = 1L;
	
	private String name;
	private Long businessHour;
	BusinessHoursContext businessHoursContext;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getBusinessHour() {
		return businessHour;
	}
	public void setBusinessHour(Long businessHour) {
		this.businessHour = businessHour;
	}
	public BusinessHoursContext getBusinessHoursContext() {
		return businessHoursContext;
	}
	public void setBusinessHoursContext(BusinessHoursContext businessHoursContext) {
		this.businessHoursContext = businessHoursContext;
	}

	List<ControlScheduleExceptionContext> exceptions;

	public List<ControlScheduleExceptionContext> getExceptions() {
		return exceptions;
	}
	public void setExceptions(List<ControlScheduleExceptionContext> exceptions) {
		this.exceptions = exceptions;
	}
	
	public void addException(ControlScheduleExceptionContext exception) {
		this.exceptions = exceptions == null ? new ArrayList<ControlScheduleExceptionContext>() : exceptions;
		this.exceptions.add(exception);
	}
}
