package com.facilio.modules;

import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.fields.FacilioField;

import java.util.List;

public abstract class ValueGenerator {

	  public abstract Object generateValueForCondition (int appType);
	  public abstract String getValueGeneratorName ();
	  public abstract String getLinkName ();
	  public abstract String getModuleName();
	  public abstract Boolean getIsHidden();
	  public abstract Integer getOperatorId();
	  public abstract Criteria getCriteria(FacilioField field,List<Long> value);
}
