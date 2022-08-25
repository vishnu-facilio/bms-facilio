package com.facilio.ns.command;

import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.ns.NamespaceConstants;
import com.facilio.ns.context.NameSpaceField;
import com.facilio.readingrule.util.NewReadingRuleAPI;
import com.facilio.relation.context.RelationMappingContext;
import com.facilio.relation.util.RelationUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Map;

@Log4j
public class AddNamespaceFieldsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long nsId = (long) context.get(NamespaceConstants.NAMESPACE_ID);
        Map<Long, ResourceContext> assetsMap = (Map<Long, ResourceContext>) context.get(FacilioConstants.ContextNames.ASSETS);
        List<NameSpaceField> fields = (List<NameSpaceField>) context.get(NamespaceConstants.NAMESPACE_FIELDS);
        if(CollectionUtils.isEmpty(fields)) {
            throw new Exception("Fields cannot be empty!");
        }
        for(NameSpaceField nsFields: fields){
           if(nsFields.getRelMapContext()!=null){
               if(nsFields.getRelMapContext().getMappingLinkName()!=null) {
                   RelationMappingContext mapping = RelationUtil.getRelationMapping(nsFields.getRelMapContext().getMappingLinkName());
                   nsFields.setRelMapId(mapping.getId());
                   nsFields.setRelMapContext(mapping);
               }
           }
        }


        NewReadingRuleAPI.addNamespaceFields(nsId, assetsMap, fields);
        LOGGER.info("id: " + nsId + ", fields : " + fields);
        return false;
    }
}
