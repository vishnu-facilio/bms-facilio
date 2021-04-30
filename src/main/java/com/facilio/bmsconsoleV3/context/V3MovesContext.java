package com.facilio.bmsconsoleV3.context;

import com.facilio.bmsconsoleV3.context.floorplan.V3DeskContext;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.v3.context.V3Context;

public class V3MovesContext extends V3Context{

    private static final long serialVersionUID = 1L;
    
    private MoveType moveType;
	public int getMoveType() {
		if (moveType != null) {
			return moveType.getIndex();
		}
		return -1;
	}
	public void setMoveType(int moveType) {
		this.moveType = MoveType.valueOf(moveType);
	}
	public MoveType getMoveTypeEnum() {
		return moveType;
	}
	public void setMoveType(MoveType moveType) {
		this.moveType = moveType;
	}

	public static enum MoveType implements FacilioIntEnum {
		INSTANT, SCHEDULED;

		@Override
		public Integer getIndex() {
			return ordinal() + 1;
		}

		@Override
		public String getValue() {
			return name();
		}

		public static MoveType valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}

	private V3EmployeeContext employee;

    public V3EmployeeContext getEmployee() {
		return employee;
	}
	public void setEmployee(V3EmployeeContext employee) {
		this.employee = employee;
	}
	
	private V3EmployeeContext movedBy;

    public V3EmployeeContext getMovedBy() {
		return movedBy;
	}
	public void setMovedBy(V3EmployeeContext movedBy) {
		this.movedBy = movedBy;
	}
    
	private V3DepartmentContext department;
    
    public V3DepartmentContext getDepartment() {
		return department;
	}
	public void setDepartment(V3DepartmentContext department) {
		this.department = department;
	}
	
	private V3DeskContext from;
    
    public V3DeskContext getFrom() {
		return from;
	}
	public void setFrom(V3DeskContext from) {
		this.from = from;
	}
	
	private V3DeskContext to;
    
    public V3DeskContext getTo() {
		return to;
	}
	public void setTo(V3DeskContext to) {
		this.to = to;
	}
	
	private Long scheduledTime;
	
	public Long getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(Long scheduledTime) {
        this.scheduledTime = scheduledTime;
    }
    
    private Long timeOfMove;
	
	public Long getTimeOfMove() {
        return timeOfMove;
    }

    public void setTimeOfMove(Long timeOfMove) {
        this.timeOfMove = timeOfMove;
    }
	
}
