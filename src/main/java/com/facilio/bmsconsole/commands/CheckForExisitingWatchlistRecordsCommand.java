package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.WatchListContext;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsole.util.VisitorManagementAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;

public class CheckForExisitingWatchlistRecordsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<WatchListContext> watchListRecords = (List<WatchListContext>)context.get(FacilioConstants.ContextNames.RECORD_LIST);
		List<WatchListContext> toBeAdded = new ArrayList<WatchListContext>();
		
		if(CollectionUtils.isNotEmpty(watchListRecords)) {
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WATCHLIST);
			List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.WATCHLIST);
		
			for(WatchListContext wL : watchListRecords) {
				WatchListContext existing = VisitorManagementAPI.getBlockedWatchListRecordForPhoneNumber(wL.getPhone(), wL.getEmail());
				if(existing != null) {
					wL.setId(existing.getId());
					if(wL.getIsBlocked() == null) {
						wL.setIsBlocked(false);
					}
					if(wL.getIsVip() == null) {
						wL.setIsVip(false);
					}
					RecordAPI.updateRecord(wL, module, fields);
				}
				else {
					toBeAdded.add(wL);
				}
			}
			if(CollectionUtils.isNotEmpty(toBeAdded)) {
				RecordAPI.addRecord(true, toBeAdded, module, fields);
			}
		}
		return false;
	}

}
