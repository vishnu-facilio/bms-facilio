package com.facilio.qa.displaylogic.context;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DisplayLogicAction {

	Long id;
	Long displayLogicId;
	String actionMeta;
	DisplayLogicActionType actionType;
	
	public DisplayLogicActionType getActionTypeEnum() {
		return actionType;
	}
	public int getActionType() {
		if(actionType != null) {
			return actionType.getVal();
		}
		return -1;
	}
	public void setActionType(int actionType) {
		this.actionType = DisplayLogicActionType.getActionType(actionType);
	}
}
