package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FetchAssetDowntimeHistoryCommand extends FacilioCommand {
    FacilioModule module;
    long assetId;

    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        assetId = (long) context.get(FacilioConstants.ContextNames.ASSET_ID);
        module = modBean.getModule(FacilioConstants.ContextNames.ASSET_BD_SOURCE_DETAILS);
        Map<String, Object> metrics = new HashMap<>();

        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ASSET_BD_SOURCE_DETAILS);
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder().select(fields)
                .table(module.getTableName())
                .andCondition(CriteriaAPI.getCondition("SOURCE_ID","sourceId",String.valueOf(assetId), StringOperators.IS));

        List<Map<String, Object>> props = selectBuilder.get();
        context.put(FacilioConstants.ContextNames.RESULT, props);

        return false;
    }

}
