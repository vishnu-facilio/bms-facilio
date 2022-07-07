package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j
public class UpdateSurveyMarkAsDeleteCommand extends FacilioCommand{
	@Override
	public boolean executeCommand(Context context) throws Exception{

		List<Long> qandaResponseIds = (List<Long>) context.get("responseIds");

		if(CollectionUtils.isEmpty(qandaResponseIds)){
			return false;
		}

		ModuleBean moduleBean = Constants.getModBean();
		FacilioModule module = moduleBean.getModule(FacilioConstants.QAndA.RESPONSE);

		List<FacilioField> fields = new ArrayList<>(4);
		fields.add(FieldFactory.getIdField(module));
		fields.add(FieldFactory.getSysDeletedByField(module));
		fields.add(FieldFactory.getSysDeletedTimeField(module));
		fields.add(FieldFactory.getIsDeletedField(module));

		Map<String, Object> props = new HashMap<>();
		props.put("deleted", true);
		props.put("sysDeletedBy", AccountUtil.getCurrentUser() != null ? AccountUtil.getCurrentUser().getId() : -1);
		props.put("sysDeletedTime", System.currentTimeMillis());

		GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder().table(module.getTableName())
													 .fields(fields)
													 .andCondition(CriteriaAPI.getIdCondition(qandaResponseIds,module));
		int count = builder.update(props);

		LOGGER.debug("Survey response markAsDeleted count : "+count);

		return false;
	}
}
