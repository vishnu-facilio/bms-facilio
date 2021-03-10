package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.PrinterContext;
import com.facilio.bmsconsole.context.VisitorKioskContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class FetchVisitorKioskDetailsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {

		long deviceId = (long) context.get(FacilioConstants.ContextNames.ID);

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getVisitorKioskFields())
				.table(ModuleFactory.getVisitorKioskModule().getTableName());

		selectBuilder.andCondition(CriteriaAPI.getIdCondition(deviceId, ModuleFactory.getVisitorKioskModule()));

		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			VisitorKioskContext visitorKioskContext = FieldUtil.getAsBeanFromMap(props.get(0),
					VisitorKioskContext.class);

			// fill printer Lookup
			if (visitorKioskContext.getPrinterId() > 0) {
				GenericSelectRecordBuilder selectPrinterBuilder = new GenericSelectRecordBuilder()
						.select(FieldFactory.getPrinterFields()).table(ModuleFactory.getPrinterModule().getTableName())
						.andCondition(CriteriaAPI.getIdCondition(visitorKioskContext.getPrinterId(),
								ModuleFactory.getPrinterModule()));

				List<Map<String, Object>> printerProps = selectPrinterBuilder.get();
				if (printerProps != null && !printerProps.isEmpty()) {

					PrinterContext printerCtx = FieldUtil.getAsBeanFromMap(printerProps.get(0), PrinterContext.class);

					visitorKioskContext.setPrinter(printerCtx);
				}

			}

			context.put(FacilioConstants.ContextNames.RECORD, visitorKioskContext);

		}

		return false;
	}

}
