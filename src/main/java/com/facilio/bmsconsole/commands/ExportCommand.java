package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.ExportUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.fs.FileInfo.FileFormat;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;

public class ExportCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		FileFormat fileFormat = (FileFormat) context.get(FacilioConstants.ContextNames.FILE_FORMAT);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		String viewName = (String) context.get(FacilioConstants.ContextNames.SUB_VIEW);
		String filters = (String) context.get(FacilioConstants.ContextNames.FILTERS);
		boolean isS3Value = (boolean) context.get(FacilioConstants.ContextNames.IS_S3_VALUE);
		boolean specialFields = (boolean) context.get(FacilioConstants.ContextNames.SPECIAL_FIELDS);
		Integer viewLimit = (Integer) context.get(FacilioConstants.ContextNames.VIEW_LIMIT);
		Criteria criteria = (Criteria) context.get(FacilioConstants.ContextNames.CLIENT_FILTER_CRITERIA);
		
		if(criteria != null) {
			fillfieldInCriteria(criteria,moduleName);
		}
		
		String fileUrl = ExportUtil.exportModule(fileFormat, moduleName, viewName, filters,criteria, isS3Value, specialFields, viewLimit);
		context.put(FacilioConstants.ContextNames.FILE_URL, fileUrl);
		
		return false;
	}

	private void fillfieldInCriteria(Criteria clientFilterCriteria,String moduleName) throws Exception {
		
		if(clientFilterCriteria != null && moduleName != null) {
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			
			for (String key : clientFilterCriteria.getConditions().keySet()) {
				Condition condition = clientFilterCriteria.getConditions().get(key);
				FacilioField field = modBean.getField(condition.getFieldName(), moduleName);
				condition.setField(field);
			}
		}
		
	}

}
