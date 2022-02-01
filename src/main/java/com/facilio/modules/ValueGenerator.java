package com.facilio.modules;

public abstract class ValueGenerator {

	  public abstract Object generateValueForCondition (int appType);
	  public abstract String getValueGeneratorName ();
	  public abstract String getLinkName ();
	  public abstract String getModuleName();
	  public abstract Boolean getIsHidden();
	  public abstract Integer getOperatorId();


}
