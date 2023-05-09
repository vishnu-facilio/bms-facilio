package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PhotosContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.collections4.CollectionUtils;

public class GetPhotosCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

		if(moduleName != null && !moduleName.isEmpty()) {
			long parentId = (long) context.get(FacilioConstants.ContextNames.PARENT_ID);
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			
			Condition idCondition = new Condition();
			idCondition.setField(modBean.getField("parentId", moduleName));
			idCondition.setOperator(NumberOperators.EQUALS);
			idCondition.setValue(String.valueOf(parentId));
			
			SelectRecordsBuilder<PhotosContext> selectBuilder = new SelectRecordsBuilder<PhotosContext>()
																		.moduleName(moduleName)
																		.beanClass(PhotosContext.class)
																		.select(fields)
																		.table(module.getTableName())
																		.andCondition(idCondition);

			context.put(FacilioConstants.ContextNames.PHOTOS, selectBuilder.get());
		}
		return false;
	}

}
