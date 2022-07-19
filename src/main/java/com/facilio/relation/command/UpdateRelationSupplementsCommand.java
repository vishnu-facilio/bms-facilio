package com.facilio.relation.command;

import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.relation.context.RelationDataContext;
import com.facilio.relation.context.RelationMappingContext;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UpdateRelationSupplementsCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String relationModuleName = Constants.getModuleName(context);

        RelationMappingContext relationMapping = (RelationMappingContext) context.get(FacilioConstants.ContextNames.RELATION_MAPPING);
        Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(context);

        if(!recordMap.isEmpty() && recordMap.containsKey(relationModuleName)) {
            List<ModuleBaseWithCustomFields> recordList = recordMap.get(relationModuleName);
            Map<Long, ModuleBaseWithCustomFields> idVsRecordMap = new HashMap<>();
            if(CollectionUtils.isNotEmpty(recordList)) {
                List<Long> recordIds = new ArrayList();
                for(ModuleBaseWithCustomFields record : recordList) {
                    RelationDataContext relationData = (RelationDataContext) record;
                    ModuleBaseWithCustomFields recordData = ( relationMapping.getReversePosition().equals(RelationMappingContext.Position.LEFT)) ? relationData.getLeft() : relationData.getRight();
                    recordIds.add(recordData.getId());
                }

                //Fetching summary data for all the records
                FacilioContext summaryContext = V3Util.getSummary(relationMapping.getToModule().getName(), recordIds);
                Map<String, List<ModuleBaseWithCustomFields>> summaryMap = Constants.getRecordMap(summaryContext);
                if(!summaryMap.isEmpty() && summaryMap.containsKey(relationMapping.getToModule().getName())) {
                    List<ModuleBaseWithCustomFields> relatedDataList = summaryMap.get(relationMapping.getToModule().getName());
                    if(CollectionUtils.isNotEmpty(relatedDataList)) {
                        idVsRecordMap = relatedDataList.stream().collect(Collectors.toMap(ModuleBaseWithCustomFields::getId, Function.identity()));
                    }
                }
            }
            //Reupdating data with all the lookup information
            if(!idVsRecordMap.isEmpty()) {
                for(ModuleBaseWithCustomFields record : recordList) {
                    RelationDataContext relationData = (RelationDataContext) record;
                    if( relationMapping.getReversePosition().equals(RelationMappingContext.Position.LEFT)) {
                        relationData.setLeft(idVsRecordMap.get(relationData.getLeft().getId()));
                    } else {
                        relationData.setRight(idVsRecordMap.get(relationData.getRight().getId()));
                    }
                }
            }
        }
        return false;
    }
}
