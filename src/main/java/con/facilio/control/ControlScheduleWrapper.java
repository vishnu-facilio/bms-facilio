package con.facilio.control;

import java.util.List;

public class ControlScheduleWrapper {

	private ControlScheduleContext controlScheduleContext;
	private List<ControlScheduleExceptionContext> exceptions;
	
	public ControlScheduleContext getControlScheduleContext() {
		return controlScheduleContext;
	}
	public void setControlScheduleContext(ControlScheduleContext controlScheduleContext) {
		this.controlScheduleContext = controlScheduleContext;
	}
	public List<ControlScheduleExceptionContext> getExceptions() {
		return exceptions;
	}
	public void setExceptions(List<ControlScheduleExceptionContext> exceptions) {
		this.exceptions = exceptions;
	}
}
