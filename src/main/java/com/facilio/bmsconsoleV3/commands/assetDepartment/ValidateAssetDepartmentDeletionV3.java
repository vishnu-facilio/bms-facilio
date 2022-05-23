package com.facilio.bmsconsoleV3.commands.assetDepartment;

import java.util.*;

import com.facilio.bmsconsoleV3.context.V3TenantContactContext;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.bmsconsoleV3.context.asset.V3AssetDepartmentContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;

import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.supercsv.cellprocessor.constraint.Equals;

public class ValidateAssetDepartmentDeletionV3 extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        ArrayList<Long> recordIDs = (ArrayList<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
        long recordID = recordIDs.get(0);
        FacilioModule assetModule = ModuleFactory.getAssetsModule();
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("DEPARTMENT_ID","department_id",String.valueOf(recordID),NumberOperators.EQUALS));
        List<V3AssetContext> result = V3RecordAPI.getRecordsListWithSupplements(assetModule.getName(), null, V3AssetContext.class, criteria, null, null, null, true );
        boolean stopChain = false;
        List<String> moduleNames = new ArrayList<>();
        context.put(FacilioConstants.ContextNames.MODULE_NAMES, moduleNames);
        if (CollectionUtils.isNotEmpty(result)) {
            moduleNames.add(assetModule.getDisplayName());
            stopChain = true;
        }
        if(stopChain){
            StringBuilder builder = new StringBuilder("This Asset Department is already being used in existing Assets");
            throw new RESTException(ErrorCode.VALIDATION_ERROR,builder.toString());
        }
        return stopChain;
    }

}
