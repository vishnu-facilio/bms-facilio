package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

public class FetchOldAssetsCommand extends FacilioCommand {


	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		AssetContext asset = (AssetContext) context.get(FacilioConstants.ContextNames.RECORD);
		List<Long> assetsId = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		new ArrayList<>();
		if(asset != null && assetsId != null && !assetsId.isEmpty()) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			List<FacilioField> fields = modBean.getAllFields(moduleName);
			SelectRecordsBuilder<AssetContext> builder = new SelectRecordsBuilder<AssetContext>()
					.moduleName(FacilioConstants.ContextNames.WORK_ORDER)
					.beanClass(AssetContext.class)
					.select(fields)
					.andCondition(CriteriaAPI.getIdCondition(assetsId, module))
					.orderBy("ID");
			context.put(FacilioConstants.ContextNames.OLD_ASSET_LIST, builder.get());
			System.out.println("builder.get()" + builder.get());
		}
		return false;
	}

}
