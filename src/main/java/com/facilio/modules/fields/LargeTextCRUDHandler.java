package com.facilio.modules.fields;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;

import com.amazonaws.util.IOUtils;
import com.facilio.beans.ModuleBean;
import com.facilio.fs.FileInfo;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FacilioFileStore;
import com.facilio.services.filestore.FileStore;

public class LargeTextCRUDHandler extends BaseMultiValueCRUDHandler<String> {
	
	LargeTextField field;

	public LargeTextCRUDHandler(LargeTextField largeTextField) {
		// TODO Auto-generated constructor stub
		this.field = largeTextField;
	}

	@Override
	protected FacilioModule getRelModule() {
		// TODO Auto-generated method stub
		return field.getRelModule();
	}

	@Override
	protected String getFieldName() {
		// TODO Auto-generated method stub
		return field.getName();
	}

	@Override
	protected String getParentFieldName() {
		// TODO Auto-generated method stub
		return "parentId";
	}

	@Override
	protected String getValueFieldName() {
		// TODO Auto-generated method stub
		return "fileId";
	}

	@Override
	protected String parseValueField(Object value, String action) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void fetchSupplements(boolean isMap, SelectRecordsBuilder relBuilder, FacilioField valueField)
			throws Exception {
		List<Map<String, Object>> props = relBuilder.getAsProps();
        if (CollectionUtils.isNotEmpty(props)) {
        	Map<String, Object> record = props.get(0);
            Long recordId = (Long) ((Map<String, Object>)record.get(getParentFieldName())).get("id");
            Long fileId = (Long) record.get(valueField.getName());
            
            FileStore fs = FacilioFactory.getFileStore();
            
            String value = null;
            try (InputStream is = fs.readFile(fileId)) {
            	value = IOUtils.toString(is);
	        }
            
            addToRecordMap(recordId, value);
        }
		
	}
	
	private Map<Long, String> recordMap = null;
    private Map<Long, String> recordMap() {
        if (recordMap == null) {
            recordMap = new HashMap<>();
        }
        return recordMap;
    }
	
	protected void addToRecordMap (long recordId, String value) {
        Map<Long, String> recordMap = recordMap();
        if(!recordMap.containsKey(recordId)) {
        	recordMap.put(recordId, value);
        }
    }
	
	@Override
    public void updateRecordWithSupplement(Map<String, Object> record) {
        if (MapUtils.isNotEmpty(recordMap)) {
            Long recordId = (Long) record.get("id");
            String textValue = recordMap.get(recordId);
            record.put(getFieldName(), textValue);
        }
    }
	 
	@Override
    public void insertSupplements(List<Map<String, Object>> records) throws Exception {
		if (CollectionUtils.isNotEmpty(records)) {
            List<Map<String, Object>> rels = new ArrayList<>();
            
            for (Map<String, Object> record : records) {
                String largeTextValue = (String) record.get(getFieldName());
                if (StringUtils.isNotBlank(largeTextValue)) {
                	
                    long parentId = (long) record.get("id");
                    
                    Map<String, Object> relRecord = createRelRecord(parentId,largeTextValue);
                    
                    rels.add(relRecord);
                }
            }
            addData(rels);
        }
    }
	
	private void addData(List<Map<String, Object>> rels) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modBean.getAllFields(getRelModule().getName());
        insertData(rels, fields);
	}
	
	protected Map<String, Object> createRelRecord(long parentId, String fileContent) throws Exception {
        Map<String, Object> relRecord = new HashMap<>();
        
        File newFile = File.createTempFile("largeTextFile.txt",null);
    	
		try(FileOutputStream fileOut = new FileOutputStream(newFile);) {
			fileOut.write(fileContent.getBytes());
		}
		
		FileStore fs = FacilioFactory.getFileStore();
		
		long fileID = fs.addFile("largeTextFile.txt", newFile, "text");
        
        relRecord.put(getParentFieldName(), parentId);
        relRecord.put(getValueFieldName(), fileID);
        return relRecord;
    }

    @Override
    public void updateSupplements(Map<String, Object> record, Collection<Long> ids) throws Exception {
        if (CollectionUtils.isNotEmpty(ids)) {
            String values = (String) record.get(getFieldName());
            if (values != null) {//During update if null value is returned from map, no change will be made
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                List<FacilioField> fields = modBean.getAllFields(getRelModule().getName());

                deleteOldData(ids, fields);
                
                List<Map<String, Object>> rels = new ArrayList<>();
                for(Long id : ids) {
                	Map<String, Object> relRecord = createRelRecord(id, values);
                	rels.add(relRecord);
                }
                addData(rels);
            }
        }
    }

}
