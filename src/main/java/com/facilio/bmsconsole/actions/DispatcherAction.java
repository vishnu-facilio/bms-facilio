package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.fsm.context.DispatcherSettingsContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DispatcherAction extends FacilioAction{
    private long id;
    private long userId;
    private DispatcherSettingsContext dispatcherConfig;

    public String addDispatcherConfig()throws Exception{

        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.Dispatcher.DISPATCHER_CONFIG,dispatcherConfig);
        FacilioChain addChain = FacilioChainFactory.addDispatcherConfig();
        addChain.execute(context);
        return SUCCESS;
    }

    public String updateDispatcherConfig() throws Exception{
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.Dispatcher.DISPATCHER_CONFIG,dispatcherConfig);
        FacilioChain updateChain = FacilioChainFactory.updateDispatcherConfig();
        updateChain.execute(context);
        return SUCCESS;
    }

    public String fetchDispatcherConfig() throws Exception{
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.Dispatcher.DISPATCHER_ID,id);
        FacilioChain listChain = ReadOnlyChainFactory.getDispatcherConfig();
        listChain.execute(context);
        setResult(FacilioConstants.Dispatcher.DISPATCHER, (DispatcherSettingsContext) context.get(FacilioConstants.Dispatcher.DISPATCHER_CONFIG));
        return SUCCESS;
    }

    public String fetchDispatcherBoardList() throws Exception{
        FacilioContext context = new FacilioContext();
        FacilioChain fetchListChain = ReadOnlyChainFactory.getDispatcherBoardList();
        fetchListChain.execute(context);
        setResult(FacilioConstants.Dispatcher.DISPATCHER_LIST,context.get(FacilioConstants.Dispatcher.DISPATCHER_LIST));
        return SUCCESS;
    }

    public String deleteDispatcherBoard() throws Exception{
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.Dispatcher.DISPATCHER_ID,id);
        FacilioChain deleteChain = FacilioChainFactory.deleteDispatcherConfig();
        deleteChain.execute(context);
        return SUCCESS;
    }
}
