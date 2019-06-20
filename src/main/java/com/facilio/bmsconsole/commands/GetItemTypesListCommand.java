package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.context.ItemTypesContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.unitconversion.Unit;

public class GetItemTypesListCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<ItemTypesContext> records = (List<ItemTypesContext>)context.get(FacilioConstants.ContextNames.RECORD_LIST);
		if(CollectionUtils.isNotEmpty(records)) {
			for(ItemTypesContext item: records) {
				if (item.getUnit() > 0) {
					item.setUnit(Unit.valueOf(item.getUnit()));
				}
			}
		}
		return false;
	}
}
