package com.facilio.modules.fields;

public class BooleanField extends FacilioField {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public BooleanField() {
		// TODO Auto-generated constructor stub
		super();
	}
	
	protected BooleanField(BooleanField field) { // Do not forget to Handle here if new property is added
		// TODO Auto-generated constructor stub
		super(field);
		this.trueVal = field.trueVal;
		this.falseVal = field.falseVal;
	}
	
	@Override
	public BooleanField clone() {
		// TODO Auto-generated method stub
		return new BooleanField(this);
	}
	
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
