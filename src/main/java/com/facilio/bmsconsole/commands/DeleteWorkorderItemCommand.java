package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.ItemContext;
import com.facilio.bmsconsole.context.WorkorderItemContext;
import com.facilio.bmsconsole.util.TransactionState;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

;

public class DeleteWorkorderItemCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
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
						.fetchSupplements(lookUpfields);

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

						if (assets != null && !assets.isEmpty() && workorderItem.getTransactionStateEnum() != TransactionState.USE) {
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
