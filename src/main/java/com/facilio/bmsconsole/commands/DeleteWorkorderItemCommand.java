package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.ItemContext;
import com.facilio.bmsconsole.context.PurchasedItemContext;
import com.facilio.bmsconsole.context.WorkorderItemContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.*;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DeleteWorkorderItemCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		List<WorkorderItemContext> deletedWorkorderItems = (List<WorkorderItemContext>) context
				.get(FacilioConstants.ContextNames.RECORD_LIST);

		if (deletedWorkorderItems != null && !deletedWorkorderItems.isEmpty()) {
			for (WorkorderItemContext workorderItem : deletedWorkorderItems) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

				FacilioModule itemModule = modBean.getModule(FacilioConstants.ContextNames.ITEM);
				List<FacilioField> itemFields = modBean.getAllFields(FacilioConstants.ContextNames.ITEM);

				Map<String, FacilioField> itemFieldsMap = FieldFactory.getAsMap(itemFields);
				List<LookupField>lookUpfields = new ArrayList<>();
				lookUpfields.add((LookupField) itemFieldsMap.get("itemType"));

				SelectRecordsBuilder<ItemContext> itemSelectBuilder = new SelectRecordsBuilder<ItemContext>()
						.select(itemFields).table(itemModule.getTableName()).moduleName(itemModule.getName())
						.beanClass(ItemContext.class).andCustomWhere(itemModule.getTableName() + ".ID = ?",
								workorderItem.getItem().getId())
						.fetchLookups(lookUpfields);

				List<ItemContext> items = itemSelectBuilder.get();
				if (items != null && !items.isEmpty()) {
					ItemContext item = items.get(0);
					if (item.getItemType().isRotating()) {
						FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET);
						List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ASSET);

						SelectRecordsBuilder<AssetContext> selectBuilder = new SelectRecordsBuilder<AssetContext>()
								.select(fields).table(module.getTableName()).moduleName(module.getName())
								.beanClass(AssetContext.class).andCustomWhere(module.getTableName() + ".ID = ?",
										workorderItem.getAsset().getId());

						List<AssetContext> assets = selectBuilder.get();

						if (assets != null && !assets.isEmpty()) {
							AssetContext asset = assets.get(0);
							asset.setIsUsed(false);
							updateAsset(asset);
						}
					}
				}

			}
		}

		return false;
	}

	private void updateAsset(AssetContext asset) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ASSET);
		UpdateRecordBuilder<AssetContext> updateBuilder = new UpdateRecordBuilder<AssetContext>()
				.module(module).fields(fields).andCondition(CriteriaAPI.getIdCondition(asset.getId(), module));
		updateBuilder.update(asset);

		System.err.println(Thread.currentThread().getName() + "Exiting updateReadings in  AddorUpdateCommand#######  ");

	}

}
