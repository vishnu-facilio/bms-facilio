package com.facilio.bmsconsoleV3.commands.utilityType;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.context.meter.V3UtilityTypeContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.relation.context.RelationContext;
import com.facilio.relation.context.RelationRequestContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AddRelationshipForCustomUtilityTypeCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(context);
        V3UtilityTypeContext utilityType = (V3UtilityTypeContext) recordMap.get("utilitytype").get(0);

        if(utilityType != null && utilityType.getMeterModuleID() != null && utilityType.getDisplayName() != null) {
            RelationRequestContext parentMeterVsChildMeterRelation = new RelationRequestContext();
            parentMeterVsChildMeterRelation.setName(utilityType.getDisplayName() + " Hierarchy");
            parentMeterVsChildMeterRelation.setDescription("Relationship between Parent and Child meters");
            parentMeterVsChildMeterRelation.setFromModuleId(utilityType.getMeterModuleID());
            parentMeterVsChildMeterRelation.setToModuleId(utilityType.getMeterModuleID());
            parentMeterVsChildMeterRelation.setRelationType(RelationRequestContext.RelationType.ONE_TO_MANY);
            parentMeterVsChildMeterRelation.setRelationName("Parent to Child " + utilityType.getDisplayName());
            parentMeterVsChildMeterRelation.setReverseRelationName("Child to Parent " + utilityType.getDisplayName());
            parentMeterVsChildMeterRelation.setRelationCategory(RelationContext.RelationCategory.METER);
            FacilioChain chain = TransactionChainFactory.getAddOrUpdateRelationChain();
            FacilioContext relationContext = chain.getContext();
            relationContext.put(FacilioConstants.ContextNames.RELATION, parentMeterVsChildMeterRelation);
            chain.execute();
        }

        return false;
    }
}
