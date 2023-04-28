package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.util.IoTMessageAPI;
import com.facilio.controlaction.context.ControlActionCommandContext;
import com.facilio.controlaction.util.ControlActionUtil;

public class PublishIOTMessageControlActionCommand extends FacilioCommand {

	private static final Logger LOGGER = LogManager.getLogger(PublishIOTMessageControlActionCommand.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception {


		List<ControlActionCommandContext> commands = (List<ControlActionCommandContext>)context.get(ControlActionUtil.CONTROL_ACTION_COMMANDS);
		
		List<ControlActionCommandContext> liveCommands = commands.stream().filter(command -> command.getControlActionMode() == ReadingDataMeta.ControlActionMode.LIVE.getIntVal())
				.collect(Collectors.toList());
		if (!liveCommands.isEmpty()) {
			IoTMessageAPI.setReadingValue(liveCommands);
		}
		
		return false;
	}

}
