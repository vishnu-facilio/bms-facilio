package com.facilio.bmsconsole.commands;

import java.util.Collections;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.UpdateRecordBuilder;

public class UpdateBaseSpaceCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		BaseSpaceContext baseSpace = (BaseSpaceContext) context.get(FacilioConstants.ContextNames.BASE_SPACE);
		if(baseSpace != null) 
		{

			String moduleName = (String) context.get(FacilioConstants.ContextNames.SPACE_TYPE);
			context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);	


			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);

				
				UpdateRecordBuilder<BaseSpaceContext> builder = new UpdateRecordBuilder<BaseSpaceContext>()
						.moduleName(moduleName)
						.fields(modBean.getAllFields(moduleName))
						.andCondition(CriteriaAPI.getIdCondition(Collections.singletonList(baseSpace.getId()) ,module));
															
			long id = builder.update(baseSpace);
			baseSpace.setId(id);
			context.put(FacilioConstants.ContextNames.RECORD_ID, id);
		}
		else 
		{
			throw new IllegalArgumentException("Space Object cannot be null for updation");
		}
		return false;
	}

}
