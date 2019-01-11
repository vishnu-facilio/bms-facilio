package com.facilio.sql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.fs.FileInfo;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;

public abstract class DBSelectRecordBuilder {
	
	protected List<FacilioField> selectFields;
	protected String tableName;
	protected StringBuilder joinBuilder = new StringBuilder();
	protected WhereBuilder where = new WhereBuilder();
	protected String groupBy;
	protected String having;
	protected String orderBy;
	protected int limit = -1;
	protected int offset = -1;
	protected String sql;
	
	protected DBSelectRecordBuilder(GenericSelectRecordBuilder selectBuilder) {
		// TODO Auto-generated constructor stub
		this.tableName = selectBuilder.getTableName();
		this.groupBy = selectBuilder.getGroupBy();
		this.having = selectBuilder.getHaving();
		this.orderBy = selectBuilder.getOrderBy();
		this.limit = selectBuilder.getLimit();
		this.offset = selectBuilder.getOffset();
		
		this.joinBuilder = new StringBuilder(selectBuilder.getJoinBuilder());
		if (selectBuilder.getSelectFields() != null) {
			this.selectFields = new ArrayList<>(selectBuilder.getSelectFields());
		}
		this.selectFields = selectBuilder.getSelectFields();
		if (selectBuilder.getWhere() != null) {
			this.where = new WhereBuilder(selectBuilder.getWhere());
		}
	}
	
	public abstract List<Map<String, Object>> get() throws Exception;
	public abstract String constructSelectStatement();
	
	protected void checkForNull(boolean checkBean) {
		if(tableName == null || tableName.isEmpty()) {
			throw new IllegalArgumentException("Table Name cannot be empty");
		}
		
		if(selectFields == null || selectFields.size() <= 0) {
			throw new IllegalArgumentException("Select Fields cannot be null or empty");
		}
	}

	protected void fetchFileUrl(List<Map<String,Object>> records, List<Long> fileIds) throws Exception {
		FileStore fs = FileStoreFactory.getInstance().getFileStore();
		
		// TODO get filePrivateUrl in bulk
		Map<Long, String> fileUrls = new HashMap<>();
		for(Long fileId: fileIds) {
			fileUrls.put(fileId, fs.getPrivateUrl(fileId));
		}
		Map<Long, FileInfo> files = fs.getFileInfoAsMap(fileIds);
		
		for(Map<String, Object> record: records) {
			for(FacilioField field : selectFields) {
				if(field != null && field.getDataTypeEnum() == FieldType.FILE && record.containsKey(field.getName()+"Id")) {
					Long id = (Long) record.get(field.getName()+"Id");
					record.put(field.getName()+"Url", fileUrls.get(id));
					record.put(field.getName()+"FileName", files.get(id).getFileName());
					record.put(field.getName()+"ContentType", files.get(id).getContentType());
				}
			}
		}
	}
}
