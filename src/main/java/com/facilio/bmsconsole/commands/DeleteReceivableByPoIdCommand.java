package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.chargebee.internal.StringJoiner;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReceivableContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;

public class DeleteReceivableByPoIdCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		//poId List
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		StringJoiner idString = new StringJoiner(",");
		for(long id: recordIds) {
			idString.add(String.valueOf(id));
		}
		if (CollectionUtils.isNotEmpty(recordIds)) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.RECEIVABLE);
			
			DeleteRecordBuilder<ReceivableContext> deleteRecordBuilder = new DeleteRecordBuilder<ReceivableContext>()
					.moduleName(module.getName())
					.andCondition(CriteriaAPI.getCondition(module.getTableName()+".PO_ID", "poId" , idString.toString(), NumberOperators.EQUALS))
					;
			
			int updatedCount = deleteRecordBuilder.markAsDelete();
			context.put(FacilioConstants.ContextNames.ROWS_UPDATED, updatedCount);
		}
		return false;
	}

	
}
