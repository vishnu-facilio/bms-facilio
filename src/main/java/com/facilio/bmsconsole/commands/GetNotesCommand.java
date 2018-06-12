package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsole.criteria.BooleanOperators;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class GetNotesCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
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
			
			SelectRecordsBuilder<NoteContext> selectBuilder = new SelectRecordsBuilder<NoteContext>()
																		.select(fields)
																		.module(module)
																		.beanClass(NoteContext.class)
																		.andCondition(idCondition)
																		.maxLevel(0);
			System.out.println("getPortalId" + AccountUtil.getCurrentUser().getPortalId());
			long portalID =  AccountUtil.getCurrentUser().getPortalId();
			if (portalID > 0)
			{
				System.out.println("AccountUtil	 + " + portalID);
				Condition createdByCond = new Condition();
				createdByCond.setField(modBean.getField("createdBy", moduleName));
				createdByCond.setOperator(NumberOperators.EQUALS);
				createdByCond.setValue(AccountUtil.getCurrentUser().getId() + "");
				
				Condition notifyCondition = new Condition();
				notifyCondition.setField(modBean.getField("notifyRequester", moduleName));
				notifyCondition.setOperator(BooleanOperators.IS);
				notifyCondition.setValue(String.valueOf(true));
				
				Criteria cri = new Criteria();
				cri.addOrCondition(createdByCond);
				cri.addOrCondition(notifyCondition);
				
				selectBuilder.andCriteria(cri);
			}
			
			context.put(FacilioConstants.ContextNames.NOTE_LIST, selectBuilder.get());
		}
		return false;
	}

}
