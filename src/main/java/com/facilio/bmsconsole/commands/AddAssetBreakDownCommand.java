package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetBreakdownContext;
import com.facilio.bmsconsole.util.AssetBreakdownAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;

public class AddAssetBreakDownCommand implements Command {
       @Override
       public boolean execute(Context context) throws Exception {
               AssetBreakdownContext assetBreakdown = (AssetBreakdownContext) context
                               .get(FacilioConstants.ContextNames.ASSET_BREAKDOWN);
               Boolean assetBreakdownStatus = (Boolean) context.get(FacilioConstants.ContextNames.ASSET_DOWNTIME_STATUS);
               Long assetBreakdownId = (Long) context.get(FacilioConstants.ContextNames.ASSET_DOWNTIME_ID);
               ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
               FacilioModule module = modBean.getModule("assetbreakdown");
               if (assetBreakdownStatus) {
                       if (assetBreakdown.getTotime() != -1) {
                               assetBreakdown.setDuration(AssetBreakdownAPI.calculateDurationInSeconds(assetBreakdown.getFromtime(),
                                               assetBreakdown.getTotime()));
                               Map<String, Object> props = FieldUtil.getAsProperties(assetBreakdown);
                               List<FacilioField> fields = new ArrayList<>();
                               FacilioField field = FieldFactory.getField("totime", "TO_TIME", FieldType.STRING);
                               fields.add(field);
                               field = FieldFactory.getField("duration", "DURATION", FieldType.NUMBER);
                               fields.add(field);
                               GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder().table(module.getTableName())
                                               .fields(fields).andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
                                               .andCondition(CriteriaAPI.getIdCondition(assetBreakdownId, module));
                               updateBuilder.update(props);
                               context.put(FacilioConstants.ContextNames.ASSET_DOWNTIME_STATUS, Boolean.FALSE);
                       }
               } else {
                       if (assetBreakdown.getTotime() != -1) {
                               assetBreakdown.setDuration(AssetBreakdownAPI.calculateDurationInSeconds(assetBreakdown.getFromtime(),
                                               assetBreakdown.getTotime()));
                       }
                       List<FacilioField> fields = modBean.getAllFields("assetbreakdown");
                       fields.add(FieldFactory.getModuleIdField(module));
                       Map<String, Object> props = FieldUtil.getAsProperties(assetBreakdown);
                       props.put("moduleId", module.getModuleId());
                       GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder().table(module.getTableName())
                                       .fields(fields).addRecord(props);
                       insertBuilder.save();
                       long assetBreakdownid = (long) props.get("id");
                       context.put(FacilioConstants.ContextNames.ASSET_DOWNTIME_ID, assetBreakdownid);
                       if (assetBreakdown.getTotime() != -1) {
                               context.put(FacilioConstants.ContextNames.ASSET_DOWNTIME_STATUS, Boolean.FALSE);
                       } else {
                               context.put(FacilioConstants.ContextNames.ASSET_DOWNTIME_STATUS, Boolean.TRUE);
                       }
               }
               return false;
       }
}