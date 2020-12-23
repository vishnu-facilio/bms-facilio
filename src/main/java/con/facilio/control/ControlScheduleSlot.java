package con.facilio.control;

import com.facilio.v3.context.V3Context;

public class ControlScheduleSlot extends V3Context implements Comparable<ControlScheduleSlot> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	ControlScheduleContext schedule;
	ControlScheduleExceptionContext exception;
	ControlGroupRoutineContext routine;
	Long startTime;
	Long endTime;
	Boolean offSchedule;
	Integer routineSeq;
	
	public ControlScheduleSlot() {
		
	}
	
	public ControlScheduleSlot(ControlScheduleContext schedule,Long startTime,Long endTime) {
		this.schedule = schedule;
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	public ControlScheduleSlot(ControlScheduleContext schedule,ControlScheduleExceptionContext exception,Long startTime,Long endTime) {
		
		this.schedule = schedule;
		this.exception = exception;
		this.startTime = startTime;
		this.endTime = endTime;
		this.offSchedule = exception.getOffSchedule();
	}
	
	public ControlScheduleSlot(ControlGroupRoutineContext routine,Long startTime) {
		this.routine = routine;
		this.startTime = startTime;
		this.routineSeq = routine.getSequence();
	}
	
	public ControlScheduleContext getSchedule() {
		return schedule;
	}
	public void setSchedule(ControlScheduleContext schedule) {
		this.schedule = schedule;
	}
	public ControlScheduleExceptionContext getException() {
		return exception;
	}
	public void setException(ControlScheduleExceptionContext exception) {
		this.exception = exception;
	}
	public ControlGroupRoutineContext getRoutine() {
		return routine;
	}
	public void setRoutine(ControlGroupRoutineContext routine) {
		this.routine = routine;
	}
	public Long getStartTime() {
		return startTime;
	}
	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}
	public Long getEndTime() {
		return endTime;
	}
	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}
	public Boolean getOffSchedule() {
		return offSchedule;
	}
	public boolean isOffSchedule() {
		if(offSchedule != null) {
			return offSchedule;
		}
		return Boolean.FALSE;
	}
	public void setOffSchedule(Boolean offSchedule) {
		this.offSchedule = offSchedule;
	}

	@Override
	public int compareTo(ControlScheduleSlot that) {
		if(this.startTime < that.startTime) {
			return -1;
		}
		else if (this.startTime > that.startTime) {
			return 1;
		}
		return 0;
	}
	
	public boolean isTouching(ControlScheduleSlot that) {
		
		if(that.startTime >= this.startTime && that.startTime <= this.endTime) {
			return Boolean.TRUE;
		}
		else if (that.endTime >= this.startTime && that.endTime <= this.endTime) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	
	public void merge(ControlScheduleSlot that) {
		
		if(that.startTime < this.startTime) {
			this.startTime = that.startTime;
		}
		
		if(that.endTime > this.endTime) {
			this.endTime = that.endTime;
		}
	}
	
	public ControlScheduleWrapper mergeONAndOff(ControlScheduleSlot that) {		//assumption this.startTime < that.startTime
		
		ControlScheduleWrapper wrapper = null;
		if(this.startTime <= that.startTime) {
			if(this.isOffSchedule()) {
				
				if(that.getEndTime() > this.getEndTime()) {
					that.startTime = this.getEndTime(); 
					wrapper = new ControlScheduleWrapper(that);
				}
			}
			else {
				if(that.startTime < this.endTime) {
					this.endTime = that.startTime;
					if(that.endTime >= this.endTime) {
						wrapper = new ControlScheduleWrapper(this);
					}
					else {
						ControlScheduleSlot trimed = new ControlScheduleSlot(that.getSchedule(),that.getEndTime(),this.getEndTime());
						wrapper = new ControlScheduleWrapper(trimed,this);
					}
				}
			}
		}
		return wrapper;
	}
	
	class ControlScheduleWrapper {
		
		public ControlScheduleSlot trimed;
		public ControlScheduleSlot previous;
		
		public ControlScheduleWrapper() {
			
		}
		public ControlScheduleWrapper(ControlScheduleSlot trimed) {
			this.trimed = trimed;
		}
		public ControlScheduleWrapper(ControlScheduleSlot trimed,ControlScheduleSlot previous) {
			this.trimed = trimed;
			this.previous = previous;
		}
	}
}
