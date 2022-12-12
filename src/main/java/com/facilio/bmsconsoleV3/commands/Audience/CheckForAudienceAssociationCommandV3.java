package com.facilio.bmsconsoleV3.commands.Audience;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class CheckForAudienceAssociationCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        Map<String,String> associatedModules = new HashMap<>();
        associatedModules.put(FacilioConstants.ContextNames.Tenant.ANNOUNCEMENT,FacilioConstants.ContextNames.Tenant.ANNOUNCEMENT_AUDIENCE);
        associatedModules.put(FacilioConstants.ContextNames.Tenant.NEIGHBOURHOOD,FacilioConstants.ContextNames.Tenant.NEIGHBOURHOOD_AUDIENCE);
        associatedModules.put(FacilioConstants.ContextNames.Tenant.DEALS_AND_OFFERS,FacilioConstants.ContextNames.Tenant.DEALSANDOFFERS_AUDIENCE);
        associatedModules.put(FacilioConstants.ContextNames.Tenant.NEWS_AND_INFORMATION,FacilioConstants.ContextNames.Tenant.NEWSANDINFORMATION_AUDIENCE);
        associatedModules.put(FacilioConstants.ContextNames.Tenant.ADMIN_DOCUMENTS,FacilioConstants.ContextNames.Tenant.ADMINDOCUMENTS_AUDIENCE);
        associatedModules.put(FacilioConstants.ContextNames.Tenant.CONTACT_DIRECTORY,FacilioConstants.ContextNames.Tenant.CONTACTDIRECTORY_AUDIENCE);

        List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
        if (recordIds != null && recordIds.size() > 0) {
            for (Map.Entry<String,String> associatedModuleInfo: associatedModules.entrySet()) {
                String associatedModuleName = associatedModuleInfo.getKey();
                List<FacilioField> announcementFields = moduleBean.getAllFields(associatedModuleName);
                String associationTableName = associatedModuleInfo.getValue();
                String audienceColumn ="RIGHT_ID";
                String associatedModuleColumn ="LEFT_ID";
                if (associatedModuleName.equals(FacilioConstants.ContextNames.Tenant.ANNOUNCEMENT)){
                    audienceColumn = "AUDIENCE_ID";
                    associatedModuleColumn = "ANNOUNCEMENT_ID";
                }
                FacilioField audienceField = FieldFactory.getField("audienceId", (associationTableName+"."+ audienceColumn), FieldType.NUMBER);

                if(CollectionUtils.isNotEmpty(announcementFields) && announcementFields.size() > 0){
                    FacilioModule associatedModule = moduleBean.getModule(associatedModuleName);
                    if(associatedModule != null){
                        Condition audienceCondition = CriteriaAPI.getCondition(audienceField,recordIds,NumberOperators.EQUALS);
                        String joinCondition = associatedModule.getTableName()+".ID = "+associationTableName+ "." +associatedModuleColumn;
                        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                                .table(associationTableName)
                                .select(Collections.singletonList(audienceField))
                                .innerJoin(associatedModule.getTableName())
                                .on(joinCondition)
                                .andCondition(audienceCondition)
                                .andCondition(CriteriaAPI.getCondition(associatedModule.getTableName()+".SYS_DELETED_TIME","sysDeletedTime",null, CommonOperators.IS_EMPTY));
                        List<Map<String, Object>> props = builder.get();
                        if(props != null) {
                            Integer count = props.size();
                            if(count != null && count > 0) {
                                throw new RESTException(ErrorCode.VALIDATION_ERROR, "Associated Audience cannot be deleted");
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}
