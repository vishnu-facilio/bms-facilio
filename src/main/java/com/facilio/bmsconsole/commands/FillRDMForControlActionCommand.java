package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.tuple.Pair;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.controlaction.context.ControlActionCommandContext;
import com.facilio.controlaction.util.ControlActionUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;

public class FillRDMForControlActionCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		List<ControlActionCommandContext> commands = (List<ControlActionCommandContext>)context.get(ControlActionUtil.CONTROL_ACTION_COMMANDS);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		if(commands != null) {
			List<Pair<Long, FacilioField>> rdmPairs = new ArrayList<>();
			for(ControlActionCommandContext command :commands) {
				if(command.getRdm() == null) {
					FacilioField field = modBean.getField(command.getFieldId());
					command.setField(field);
					rdmPairs.add(Pair.of(command.getResource().getId(), field));
				}
			}
			
			if (!rdmPairs.isEmpty()) {
				Map<String, ReadingDataMeta> rdmMap = new HashMap<>();
				List<ReadingDataMeta> rdmList = ReadingsAPI.getReadingDataMetaList(rdmPairs);
				rdmMap.putAll(rdmList.stream().collect(Collectors.toMap(rdm -> ReadingsAPI.getRDMKey(rdm), Function.identity())));
				for(ControlActionCommandContext command :commands) {
					if(command.getRdm() == null) {
						ReadingDataMeta rdm = rdmMap.get(ReadingsAPI.getRDMKey(command.getResource().getId(), command.getField()));
						command.setRdm(rdm);
					}
				}
			}
		}
		else {
			return true;
		}
		
		return false;
	}

}
