package com.facilio.bmsconsoleV3.commands.assetType;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class ValidateAssetTypeDeletionV3 extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        ArrayList<Long> recordIDs = (ArrayList<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
        long recordID = recordIDs.get(0);
        Criteria criteria = new Criteria();
        List<FacilioField> assetFields = moduleBean.getAllFields(FacilioConstants.ContextNames.ASSET);
        if(CollectionUtils.isNotEmpty(assetFields) && assetFields.size() > 0){
            criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getAsMap(assetFields).get("type"), Collections.singleton(recordID),NumberOperators.EQUALS));
            FacilioModule assetModule = moduleBean.getModule(FacilioConstants.ContextNames.ASSET);
            if(assetModule != null){
                FacilioField aggregateField = FieldFactory.getIdField(assetModule);
                List<Map<String, Object>> props = V3RecordAPI.getRecordsAggregateValue(FacilioConstants.ContextNames.ASSET,null, AssetContext.class,criteria, BmsAggregateOperators.CommonAggregateOperator.COUNT,aggregateField,null);
                if(props != null) {
                    Long count = (Long) props.get(0).get(aggregateField.getName());
                    if(count != null && count > 0) {
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "There are exisiting Assets in this Asset type. Cannot delete Asset Type");
                    }
                }
            }
        }
        return false;
    }
}
