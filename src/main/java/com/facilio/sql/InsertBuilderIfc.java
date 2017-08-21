package com.facilio.sql;

import java.sql.Connection;
import java.util.List;

import com.facilio.bmsconsole.modules.FacilioField;

public interface InsertBuilderIfc<E> {
	
	public InsertBuilderIfc<E> table(String tableName);
	
	public InsertBuilderIfc<E> fields(List<FacilioField> fields);
	
	public InsertBuilderIfc<E> addRecord(E value);
	
	public InsertBuilderIfc<E> addRecords(List<E> values);
	
	public void save() throws Exception;
	
	public InsertBuilderIfc<E> connection(Connection conn);

}
