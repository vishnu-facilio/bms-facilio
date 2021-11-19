package com.facilio.bmsconsoleV3.commands.transferRequest;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ItemContext;
import com.facilio.bmsconsoleV3.context.inventory.V3TransferRequestContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateStatusOfShipmentCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3TransferRequestContext> transferRequestContexts = recordMap.get(moduleName);
        if (CollectionUtils.isNotEmpty(transferRequestContexts)){
                if (transferRequestContexts != null && transferRequestContexts.get(0).getData().get("isCompleted").equals(true) && transferRequestContexts.get(0).getData().get("isShipped").equals(true)) {
                    long id =transferRequestContexts.get(0).getId();
                    ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                    String shipmentModuleName = FacilioConstants.ContextNames.TRANSFER_REQUEST_SHIPMENT;
                    FacilioModule module = modBean.getModule(shipmentModuleName);
                    List<FacilioField> fields = modBean.getAllFields(shipmentModuleName);

                    List<FacilioField> updatedFields = new ArrayList<>();
                    Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
                    updatedFields.add(fieldsMap.get("isCompleted"));

                    Map<String, Object> map = new HashMap<>();
                    map.put("isCompleted", true);

                    UpdateRecordBuilder<ItemContext> updateBuilder = new UpdateRecordBuilder<ItemContext>()
                            .module(module).fields(updatedFields)
                            .andCondition(CriteriaAPI.getCondition("TRANSFER_REQUEST_ID","transferRequest", String.valueOf(id), NumberOperators.EQUALS));
                    updateBuilder.updateViaMap(map);
                }
                }
        return false;
    }

}
