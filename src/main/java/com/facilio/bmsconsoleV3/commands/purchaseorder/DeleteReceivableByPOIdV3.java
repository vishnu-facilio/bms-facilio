package com.facilio.bmsconsoleV3.commands.purchaseorder;

import com.chargebee.internal.StringJoiner;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.purchaseorder.V3ReceivableContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class DeleteReceivableByPOIdV3 extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> recordIds = Constants.getRecordIds(context);

        if (CollectionUtils.isNotEmpty(recordIds)) {
            StringJoiner idString = new StringJoiner(",");
            for (long id : recordIds) {
                idString.add(String.valueOf(id));
            }
            if (CollectionUtils.isNotEmpty(recordIds)) {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.RECEIVABLE);

                DeleteRecordBuilder<V3ReceivableContext> deleteRecordBuilder = new DeleteRecordBuilder<V3ReceivableContext>()
                        .moduleName(module.getName())
                        .andCondition(CriteriaAPI.getCondition(module.getTableName() + ".PO_ID", "poId", idString.toString(), NumberOperators.EQUALS));

                deleteRecordBuilder.markAsDelete();

            }
        }
        return false;
    }
}
