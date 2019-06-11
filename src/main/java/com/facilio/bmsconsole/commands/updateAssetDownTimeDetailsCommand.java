package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetBDSourceDetailsContext;
import com.facilio.bmsconsole.context.AssetBreakdownContext;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;

public class updateAssetDownTimeDetailsCommand implements Command {
       @Override
       public boolean execute(Context context) throws Exception {
    	   AssetBDSourceDetailsContext assetBDSourceDetail = (AssetBDSourceDetailsContext) context
                               .get(FacilioConstants.ContextNames.ASSET_BD_SOURCE_DETAILS);
               Boolean assetDowntimeStatus = (Boolean) context.get(FacilioConstants.ContextNames.ASSET_DOWNTIME_STATUS);
               Long assetDowntimeId = (Long) context.get(FacilioConstants.ContextNames.ASSET_DOWNTIME_ID);
               if (assetBDSourceDetail.getAssetid() != -1) {
                       AssetContext asset=new AssetContext();
                       asset.setDowntimeStatus(assetDowntimeStatus);
                       asset.setLastDowntimeId(assetDowntimeId);
                       Map<String, Object> props = FieldUtil.getAsProperties(asset);
                       ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                       FacilioModule module = modBean.getModule(ContextNames.ASSET);
                       List<FacilioField> fields = new ArrayList<>();
                       fields.add(modBean.getField("downtimeStatus", ContextNames.ASSET));
                       fields.add(modBean.getField("lastDowntimeId", ContextNames.ASSET));
                       GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder().table(module.getTableName())
                                       .fields(fields).andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
                                       .andCondition(CriteriaAPI.getIdCondition(assetBDSourceDetail.getAssetid(), module));
                       updateBuilder.update(props);
               }
               return false;
       }
}