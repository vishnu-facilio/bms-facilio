package com.facilio.bmsconsole.modules;

public class BooleanField extends FacilioField {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String trueVal;
	public String getTrueVal() {
		return trueVal;
	}
	public void setTrueVal(String trueVal) {
		this.trueVal = trueVal;
	}
	
	private String falseVal;
	public String getFalseVal() {
		return falseVal;
	}
	public void setFalseVal(String falseVal) {
		this.falseVal = falseVal;
	}

}
