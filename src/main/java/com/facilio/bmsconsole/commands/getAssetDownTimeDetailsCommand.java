package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetBreakdownContext;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;

public class getAssetDownTimeDetailsCommand implements Command {
       @Override
       public boolean execute(Context context) throws Exception {
               AssetBreakdownContext assetBreakdown = (AssetBreakdownContext) context
                               .get(FacilioConstants.ContextNames.ASSET_BREAKDOWN);
               if (assetBreakdown.getParentId() != -1) {
                       ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                       FacilioModule module = modBean.getModule("asset");
                       List<FacilioField> fields = new ArrayList<>();
                       fields.add(FieldFactory.getIdField(module));
                       FacilioField field = FieldFactory.getField("downtimeStatus", "DOWNTIME_STATUS", FieldType.BOOLEAN);
                       fields.add(field);
                       field = FieldFactory.getField("lastDowntimeId", "LAST_DOWNTIME_ID", FieldType.NUMBER);
                       fields.add(field);
                       GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder().select(fields)
                                       .table(module.getTableName()).andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
                                       .andCondition(CriteriaAPI.getIdCondition(assetBreakdown.getParentId(), module));
                       List<Map<String, Object>> props = selectBuilder.get();
                       if (props != null && !props.isEmpty()) {
                               for (Map<String, Object> prop : props) {
                                       AssetContext asset = FieldUtil.getAsBeanFromMap(prop, AssetContext.class);
                                       context.put(FacilioConstants.ContextNames.ASSET_DOWNTIME_STATUS,
                                                       asset.getDowntimeStatus() == null ? Boolean.FALSE : asset.getDowntimeStatus());
                                       context.put(FacilioConstants.ContextNames.ASSET_DOWNTIME_ID, asset.getLastDowntimeId());
                                       assetBreakdown.setId(asset.getLastDowntimeId());
                               }
                       }

               }
               return false;
       }
}