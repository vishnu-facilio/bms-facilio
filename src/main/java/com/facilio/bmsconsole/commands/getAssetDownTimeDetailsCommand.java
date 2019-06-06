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
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
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
			fields.add(modBean.getField("downtimeStatus", ContextNames.ASSET));
			fields.add(modBean.getField("lastDowntimeId", ContextNames.ASSET));
			
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder().select(fields)
					.table(module.getTableName()).andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
					.andCondition(CriteriaAPI.getIdCondition(assetBreakdown.getParentId(), module));
			
			Map<String, Object> prop = selectBuilder.fetchFirst();
			if (prop != null && !prop.isEmpty()) {
				AssetContext asset = FieldUtil.getAsBeanFromMap(prop, AssetContext.class);
				context.put(FacilioConstants.ContextNames.ASSET_DOWNTIME_STATUS,
						asset.getDowntimeStatus() == null ? Boolean.FALSE : asset.getDowntimeStatus());
				context.put(FacilioConstants.ContextNames.ASSET_DOWNTIME_ID, asset.getLastDowntimeId());
				assetBreakdown.setId(asset.getLastDowntimeId());
			}

		}
		return false;
	}
}