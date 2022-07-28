package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.relation.context.RelationRequestContext;
import com.facilio.relation.util.RelationUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class GetRelationshipMetaCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {

		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		if(module == null) {
			throw new IllegalArgumentException("Invalid module name");
		}

		List<RelationRequestContext> relationships = RelationUtil.getAllRelations(module);
		if(CollectionUtils.isNotEmpty(relationships)) {
			context.put(FacilioConstants.ContextNames.RELATIONSHIP_META, relationships);
		}
		return false;
	}

}
