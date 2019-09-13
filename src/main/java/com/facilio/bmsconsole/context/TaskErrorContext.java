package com.facilio.bmsconsole.context;

import com.facilio.unitconversion.Unit;

public class TaskErrorContext {

	Mode mode;
	FailType failType;
	SuggestionType suggestionType;
	Unit suggestedUnit;
	
	public int getSuggestedUnit() {
		if(suggestedUnit != null) {
			return suggestedUnit.getUnitId();
		}
		return -1;
	}

	public void setSuggestedUnit(Unit suggestedUnit) {
		this.suggestedUnit = suggestedUnit;
	}

	public int getSuggestionType() {
		if(suggestionType != null) {
			return suggestionType.getValue();
		}
		return -1;
	}

	public void setSuggestionType(int suggestionType) {
		if(suggestionType > 0) {
			this.suggestionType = SuggestionType.valueOf(suggestionType);
		}
	}

	String previousValue;
	String currentValue;
	String averageValue;

	public String getAverageValue() {
		return averageValue;
	}

	public void setAverageValue(String averageValue) {
		this.averageValue = averageValue;
	}

	public String getPreviousValue() {
		return previousValue;
	}

	public void setPreviousValue(String previousValue) {
		this.previousValue = previousValue;
	}

	public String getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(String currentValue) {
		this.currentValue = currentValue;
	}

	public int getFailType() {
		if(failType != null) {
			return failType.getValue();
		}
		return -1;
	}

	public void setFailType(int failType) {
		this.failType = FailType.valueOf(failType);
	}

	String message;
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getMode() {
		if(mode != null) {
			return mode.getValue();
		}
		return -1;
	}

	public void setMode(int mode) {
		this.mode = Mode.valueOf(mode);
	}
	
	public enum FailType {
		
		NON_INCREMENTAL_VALUE,
		;
		
		public int getValue() {
			return ordinal() + 1;
		}
		public static FailType valueOf (int value) {
			if (value > 0 && value <= values().length) {
				return values() [value - 1];
			}
			return null;
		}
	}
	
	public enum SuggestionType {
		
		UNIT_CHANGE,
		LESS_THAN_AVG_VALUE,
		GREATER_THAN_AVG_VALUE,
		;
		
		public int getValue() {
			return ordinal() + 1;
		}
		public static SuggestionType valueOf (int value) {
			if (value > 0 && value <= values().length) {
				return values() [value - 1];
			}
			return null;
		}
	}

	public enum Mode {
		
		ERROR,
		SUGGESTION,
		;
		
		public int getValue() {
			return ordinal() + 1;
		}
		public static Mode valueOf (int value) {
			if (value > 0 && value <= values().length) {
				return values() [value - 1];
			}
			return null;
		}
	}
}
