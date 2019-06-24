package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetBreakdownContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;

public class GetAssetBreakdownCommand implements Command {
       @Override
       public boolean execute(Context context) throws Exception {
               AssetBreakdownContext assetBreakdown = (AssetBreakdownContext) context
                               .get(FacilioConstants.ContextNames.ASSET_BREAKDOWN);
               if (assetBreakdown.getId() != -1) {
                       ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                       FacilioModule module = modBean.getModule("assetbreakdown");
                       List<FacilioField> fields = modBean.getAllFields("assetbreakdown");
                       fields.add(FieldFactory.getIdField(module));
                       GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder().select(fields)
                                       .table(module.getTableName())
//                                       .andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
                                       .andCondition(CriteriaAPI.getIdCondition(assetBreakdown.getId(), module));
                       List<Map<String, Object>> props = selectBuilder.get();
                       if (props != null && !props.isEmpty()) {
                               for (Map<String, Object> prop : props) {
                                       assetBreakdown = FieldUtil.getAsBeanFromMap(prop, AssetBreakdownContext.class);
                                       context.put(FacilioConstants.ContextNames.ASSET_BREAKDOWN, assetBreakdown);
                               }
                       }

               }
               return false;
       }
}
