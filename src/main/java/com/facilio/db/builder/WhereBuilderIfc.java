package com.facilio.db.builder;

import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;

public interface WhereBuilderIfc<E> {

	@Deprecated
	public E andCustomWhere(String whereCondition, Object... values);

	@Deprecated
	public E orCustomWhere(String whereCondition, Object... values);

	public E andCondition(Condition condition);
	
	public E orCondition(Condition condition);
	
	public E andCriteria(Criteria criteria);
	
	public E orCriteria(Criteria criteria);
}
