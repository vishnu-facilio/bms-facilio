package com.facilio.bmsconsole.commands;
import com.facilio.bmsconsole.context.PageColumnContext;
import com.facilio.bmsconsole.util.CustomPageAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;

public class GetPageColumnCommand extends FacilioCommand {
    private static final Logger LOGGER = Logger.getLogger(GetPageColumnCommand.class.getName());
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long id = (Long) context.get(FacilioConstants.ContextNames.ID);
        if(id == null || id <= 0){
            LOGGER.info("Invalid id to get column");
            throw new IllegalArgumentException("Invalid id");
        }

        PageColumnContext column = CustomPageAPI.getColumn(id);

        if(column == null ){
            LOGGER.info("Column does not exists");
            throw new IllegalArgumentException("Column does not exists");
        }

        context.put(FacilioConstants.CustomPage.COLUMN, column);
        return false;
    }
}
