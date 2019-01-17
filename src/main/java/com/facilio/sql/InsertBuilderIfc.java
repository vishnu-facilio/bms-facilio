package com.facilio.sql;

import java.sql.Connection;
import java.util.List;

import com.facilio.bmsconsole.modules.FacilioField;

public interface InsertBuilderIfc<E> {
	
	public InsertBuilderIfc<E> table(String tableName);
	
	public InsertBuilderIfc<E> useExternalConnection (Connection conn);
	
	public InsertBuilderIfc<E> fields(List<FacilioField> fields);
	
	public InsertBuilderIfc<E> addRecord(E value);
	
	public InsertBuilderIfc<E> addRecords(List<E> values);
	
	public List<E> getRecords();
	
	public long insert(E value) throws Exception;
	
	public void save() throws Exception;
	
}
