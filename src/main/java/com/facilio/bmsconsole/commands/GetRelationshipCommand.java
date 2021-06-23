package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.RelationshipContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class GetRelationshipCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
			FacilioModule module = ModuleFactory.getRelationshipModule();
			
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.select(FieldFactory.getRelationshipFields())
					.table(module.getTableName());
			
			List<Map<String, Object>> props = selectBuilder.get();
			List<RelationshipContext> relationships = new ArrayList<RelationshipContext>();
			if (props != null && !props.isEmpty()) {
		    	relationships.addAll(FieldUtil.getAsBeanListFromMapList(props, RelationshipContext.class));
			}
			context.put(FacilioConstants.ContextNames.RELATIONSHIP_LIST, relationships);
		return false;
	}

}
