package com.facilio.bmsconsole.commands;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;

public class GetRelatedAssetCommand extends FacilioCommand {

		public boolean executeCommand(Context context) throws Exception {
			long id = (long) context.get(FacilioConstants.ContextNames.RESOURCE_ID);
			if (id > 0) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule module = ModuleFactory.getRelatedAssetsModule();
				
				GenericSelectRecordBuilder relatedAssetSelectBuilder = new GenericSelectRecordBuilder()
						.select(FieldFactory.getRelatedAssetesFields()).table(module.getTableName())
						.andCondition(CriteriaAPI.getCondition(module.getTableName()+".SOURCE_ID", "sourceId" , String.valueOf(id), StringOperators.IS))
					    ;
				
				List<Map<String, Object>> relatedAssetList = relatedAssetSelectBuilder.get();
				List<Long> assetId = relatedAssetList.stream().map(prop -> (long) prop.get("targetId")).collect(Collectors.toList());
				context.put(FacilioConstants.ContextNames.RESOURCE_LIST, assetId);
				 
			}
			return false;
		}

}
