package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.relation.context.RelationMappingContext;
import com.facilio.relation.util.RelationUtil;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FetchRelatedParentIdsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long parentId = (Long) context.get(FacilioConstants.ContextNames.PARENT_ID);
        String relMapLinkName = (String) context.get(FacilioConstants.ContextNames.RELATION_MAPPING);
        RelationMappingContext relationMapping = RelationUtil.getRelationMapping(relMapLinkName);
        if (relationMapping == null) {
            throw new Exception("Relation mapping link name is not found!!");
        }

        ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule resModule = bean.getModule(relationMapping.getFromModuleId());

        List<Long> parentIds = new ArrayList<>();

        if (relMapLinkName != null) {
            JSONObject recordsWithRelationship = RelationUtil.getRecordsWithRelationship(relMapLinkName, resModule.getName(), parentId, -1, -1);
            JSONObject data = (JSONObject) recordsWithRelationship.get("data");
            if (data != null) {
                List<Map> resources = (ArrayList<Map>) data.get(resModule.getName());
                for (Map res : resources) {
                    parentIds.add((Long) res.get("id"));
                }
            }
            context.remove(FacilioConstants.ContextNames.PARENT_ID);
            context.put(FacilioConstants.ContextNames.PARENT_ID_LIST, parentIds);
        }
        return false;
    }
}
