package com.facilio.control;

import java.util.ArrayList;
import java.util.List;

import com.facilio.v3.context.V3Context;

public class ControlScheduleSlot extends V3Context implements Comparable<ControlScheduleSlot> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	ControlScheduleContext schedule;
	ControlScheduleExceptionContext exception;
	ControlGroupRoutineContext routine;
	ControlGroupContext group;
	Long startTime;
	Long endTime;
	Boolean offSchedule;
	Integer routineSeq;
	
	public ControlScheduleSlot() {
		
	}
	
	public ControlScheduleSlot(ControlGroupContext group,ControlScheduleContext schedule,Long startTime,Long endTime) {
		this.schedule = schedule;
		this.startTime = startTime;
		this.endTime = endTime;
		this.group = group;
	}
	
	public ControlScheduleSlot(ControlGroupContext group,ControlScheduleContext schedule,ControlScheduleExceptionContext exception,Long startTime,Long endTime) {
		
		this.schedule = schedule;
		this.exception = exception;
		this.startTime = startTime;
		this.endTime = endTime;
		this.group = group;
		this.offSchedule = exception.getOffSchedule();
	}
	
	public ControlScheduleSlot(ControlGroupContext group,ControlGroupRoutineContext routine,Long startTime) {
		this.routine = routine;
		this.startTime = startTime;
		this.group = group;
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
				
				if(that.getStartTime() >= this.getEndTime()) {
					wrapper = new ControlScheduleWrapper(that);
				}
				else if(that.getEndTime() > this.getEndTime()) {
					that.startTime = this.getEndTime(); 
					wrapper = new ControlScheduleWrapper(that);
				}
			}
			else {
				if(that.startTime < this.endTime) {
					if(that.endTime >= this.endTime) {
						wrapper = new ControlScheduleWrapper(this);
					}
					else {
						ControlScheduleSlot trimed = new ControlScheduleSlot(that.getGroup(),that.getSchedule(),that.getEndTime(),this.getEndTime());
						wrapper = new ControlScheduleWrapper(trimed,this);
					}
					this.endTime = that.startTime;
				}
			}
		}
		return wrapper;
	}
	
	public class ControlScheduleWrapper {
		
		public ControlScheduleSlot trimed;
		public ControlScheduleSlot previous;
		public ControlScheduleSlot thirdBlock;
		public ControlScheduleWrapper() {
			
		}
		public ControlScheduleWrapper(ControlScheduleSlot trimed) {
			this.trimed = trimed;
		}
		public ControlScheduleWrapper(ControlScheduleSlot trimed,ControlScheduleSlot previous) {
			this.trimed = trimed;
			this.previous = previous;
		}
		public ControlScheduleWrapper(ControlScheduleSlot trimed,ControlScheduleSlot previous,ControlScheduleSlot thirdBlock) {
			this.trimed = trimed;
			this.previous = previous;
			this.thirdBlock = thirdBlock;
		}
		public ControlScheduleSlot getTrimed() {
			return trimed;
		}
		public void setTrimed(ControlScheduleSlot trimed) {
			this.trimed = trimed;
		}
		public ControlScheduleSlot getPrevious() {
			return previous;
		}
		public void setPrevious(ControlScheduleSlot previous) {
			this.previous = previous;
		}
		public ControlScheduleSlot getThirdBlock() {
			return thirdBlock;
		}
		public void setThirdBlock(ControlScheduleSlot thirdBlock) {
			this.thirdBlock = thirdBlock;
		}
	}
	
	public class ControlScheduleSlotWrapperForDisplay {
		
		public ControlScheduleSlot firstBlock;
		public ControlScheduleSlot secondBlock;
		public ControlScheduleSlot thirdBlock;
		public ControlScheduleSlotWrapperForDisplay(ControlScheduleSlot firstBlock) {
			this.firstBlock = firstBlock;
		}
		public ControlScheduleSlotWrapperForDisplay(ControlScheduleSlot firstBlock,ControlScheduleSlot secondBlock) {
			this.firstBlock = firstBlock;
			this.secondBlock = secondBlock;
		}
		public ControlScheduleSlotWrapperForDisplay(ControlScheduleSlot firstBlock,ControlScheduleSlot secondBlock,ControlScheduleSlot thirdBlock) {
			this.firstBlock = firstBlock;
			this.secondBlock = secondBlock;
			this.thirdBlock = thirdBlock;
		}
		public ControlScheduleSlot getThirdBlock() {
			return thirdBlock;
		}
		public void setThirdBlock(ControlScheduleSlot thirdBlock) {
			this.thirdBlock = thirdBlock;
		}
		public ControlScheduleSlot getFirstBlock() {
			return firstBlock;
		}
		public void setFirstBlock(ControlScheduleSlot firstBlock) {
			this.firstBlock = firstBlock;
		}
		public ControlScheduleSlot getSecondBlock() {
			return secondBlock;
		}
		public void setSecondBlock(ControlScheduleSlot secondBlock) {
			this.secondBlock = secondBlock;
		}
		
		public ControlScheduleSlot getLast() {
			if(thirdBlock != null) {
				return thirdBlock;
			}
			else if(secondBlock != null) {
				return secondBlock;
			}
			return firstBlock;
		}
		
		public List<ControlScheduleSlot> getAsList() {
			List<ControlScheduleSlot> slots = new ArrayList<ControlScheduleSlot>();
			
			if(firstBlock != null) {
				slots.add(firstBlock);
				if(secondBlock != null) {
					slots.add(secondBlock);
					if(thirdBlock != null) {
						slots.add(thirdBlock);
					}
				}
				
			}
			
			return slots;
		}
	}

	public ControlGroupContext getGroup() {
		return group;
	}

	public void setGroup(ControlGroupContext group) {
		this.group = group;
	}

	public ControlScheduleSlotWrapperForDisplay mergeForDisplay(ControlScheduleSlot that) {
		
		ControlScheduleSlotWrapperForDisplay slotForDisplay = null; 
		if(this.getException() != null) {
			
			if(this.endTime < that.endTime) {
				that.startTime = this.endTime;
				slotForDisplay = new ControlScheduleSlotWrapperForDisplay(this,that);
			}
			else {
				//ignore that;
				slotForDisplay = new ControlScheduleSlotWrapperForDisplay(this);
			}
		}
		else {
			if(this.endTime <= that.endTime) {
				slotForDisplay = new ControlScheduleSlotWrapperForDisplay(this,that);
			}
			else {
				ControlScheduleSlot newSlot = new ControlScheduleSlot();
				newSlot.setStartTime(that.getEndTime());
				newSlot.setEndTime(this.getEndTime());
				slotForDisplay = new ControlScheduleSlotWrapperForDisplay(this,that,newSlot);
			}
			this.endTime = that.startTime;
		}
		
		return slotForDisplay;
	}
}
