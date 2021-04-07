package com.facilio.modules.fields;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.facilio.beans.ModuleBean;
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
		// TODO Auto-generated method stub
		
	}
	
	@Override
    public void insertSupplements(List<Map<String, Object>> records) throws Exception {
        if (CollectionUtils.isNotEmpty(records)) {
            List<Map<String, Object>> rels = new ArrayList<>();
            
            for (Map<String, Object> record : records) {
                String largeTextValue = (String) record.get(getFieldName());
                if (StringUtils.isNotBlank(largeTextValue)) {
                	
                    long parentId = (long) record.get("id");
                    
                    File newFile = File.createTempFile("largeTextFile.txt",null);
                	
        			try(FileOutputStream fileOut = new FileOutputStream(newFile);) {
        				fileOut.write(largeTextValue.getBytes());
        			}
        			
        			FileStore fs = FacilioFactory.getFileStore();
        			
        			long fileID = fs.addFile("largeTextFile.txt", newFile, "text");
                    
                    Map<String, Object> relRecord = createRelRecord(parentId, fileID);
                    
                    rels.add(relRecord);
                }
            }

            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            List<FacilioField> fields = modBean.getAllFields(getRelModule().getName());
            insertData(rels, fields);
        }
    }
	
	protected Map<String, Object> createRelRecord(long parentId, long value) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, Exception {
        Map<String, Object> relRecord = new HashMap<>();
        relRecord.put(getParentFieldName(), parentId);
        relRecord.put(getValueFieldName(), value);
        return relRecord;
    }

//    @Override
//    public void updateSupplements(Map<String, Object> record, Collection<Long> ids) throws Exception {
//        if (CollectionUtils.isNotEmpty(ids)) {
//            List values = (List) record.get(getFieldName());
//            if (values != null) {//During update if null value is returned from map, no change will be made
//                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
//                List<FacilioField> fields = modBean.getAllFields(getRelModule().getName());
//
//                deleteOldData(ids, fields);
//                List<Map<String, Object>> rels = new ArrayList<>();
//                for (Long id : ids) {
//                    processValueList(values, id, rels, "update");
//                }
//                insertData(rels, fields);
//            }
//        }
//    }
//
//    @Override
//    public void deleteSupplements(Collection<Long> ids) throws Exception {
//        if (CollectionUtils.isNotEmpty(ids)) {
//            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
//            List<FacilioField> fields = modBean.getAllFields(getRelModule().getName());
//            deleteOldData(ids, fields);
//        }
//    }

}
