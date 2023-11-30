package com.facilio.flowengine.executor;

import com.facilio.blockfactory.blocks.BaseBlock;
import com.facilio.blockfactory.blocks.BaseLoopBlock;
import com.facilio.flowLog.FlowLogLevel;
import com.facilio.flowLog.FlowLogService;
import com.facilio.flowengine.exception.FlowException;
import com.facilio.flowengine.observers.Observer;
import com.facilio.flows.context.FlowContext;
import com.facilio.modules.FieldUtil;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

import java.text.MessageFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FlowEngine implements FlowEngineInterFace {

    private static org.apache.log4j.Logger log = org.apache.log4j.LogManager.getLogger(FlowEngine.class.getName());
    private static final Logger LOGGER = Logger.getLogger(FlowEngine.class.getName());
    private static final int maxBlocks = 50;
    private FlowContext flow;
    private FlowLogService flowLogService;
    private JSONObject currentRecord;
    private BaseBlock currentBlock;
    @Getter
    private BaseLoopBlock rootParentBlock;

    private int blocksExecuted = 0;
    private List<Observer> observers = new LinkedList<>();
    private StringBuilder logString = new StringBuilder();

    @Setter
    private FlowLogLevel level;
    public FlowEngine(FlowContext flow, JSONObject currentRecord){
        this.flow = Objects.requireNonNull(flow);
        this.currentRecord = Objects.requireNonNull(currentRecord);
    }
    public FlowEngine(FlowContext flow){
        this.flow = Objects.requireNonNull(flow);
    }

    public void addObserver(Observer observer) {
        if (observer != null) {
            observers.add(observer);
        }
    }

    public void execute(BaseBlock startBlock, Map<String,Object> memory) throws Exception {
        long currentMillis = System.currentTimeMillis();

        try{
            emitFlowStart(cloneMemory(memory));
            runBlocks(null,startBlock,memory);
            emitFlowEnd(cloneMemory(memory));
        }catch (Exception e){
            String errorMeg = "Exception occurred in flowEngine for flowId:"+flow.getId()+" message:"+e.getMessage();
            LOGGER.log(Level.SEVERE, errorMeg, e);
            emitFlowError(cloneMemory(memory),new FlowException(e.getMessage()));
            throw e;
        }
        finally {
            long executionTime = System.currentTimeMillis() - currentMillis;
            String msg = MessageFormat.format("### time taken for flow ({0}) is {1}", flow.getId(), executionTime);
            log.debug(msg);
        }

    }
    private void runBlocks(BaseLoopBlock rootParent, BaseBlock startBlock, Map<String,Object> memory) throws FlowException {
        currentBlock = startBlock;
        rootParentBlock = rootParent;

        while (currentBlock != null) {
            blocksExecuted++;
            currentBlock.setFlowEngineInterFace(this);

            emitBlockStart(currentBlock, cloneMemory(memory));

            currentBlock.execute(memory);

            if(rootParent!=null && (rootParent.isBreakLoop() || rootParent.isContinueLoop()) ){
                rootParentBlock.setContinueLoop(false);
                break;
            }

            if(currentBlock instanceof BaseLoopBlock){
                BaseLoopBlock forLoopBlock = (BaseLoopBlock) currentBlock;
                handleForLoopBlock(forLoopBlock,memory);
                currentBlock = forLoopBlock;
            }

            emitBlockEnd(currentBlock, cloneMemory(memory));

            if (blocksExecuted > maxBlocks) {
                LOGGER.info("Maximum Block execution reached");
                break;
            }

            currentBlock = currentBlock.getNextBlock();
        }
    }
    private void handleForLoopBlock(BaseLoopBlock forLoopBlock, Map<String,Object> memory) throws FlowException {
        BaseBlock forStartBlock = forLoopBlock.getNextBlock();
        if(forStartBlock == null){
            forLoopBlock.resetExecutablePosition();
            return;
        }
        while (forLoopBlock.hasNext()){
            Object i = forLoopBlock.next();
            forLoopBlock.putToMemory(memory,i);
            runBlocks(forLoopBlock,forStartBlock,memory);
            if(forLoopBlock.isBreakLoop()){
                forLoopBlock.setBreakLoop(false);
                break;
            }
        }
        forLoopBlock.resetExecutablePosition();
    }

    private void emitFlowStart(Map<String, Object> memory) {
        try{
            flowLogService.onFlowStart();
        }catch (Exception e){
            log.debug(e.getMessage());
        }
        for (Observer observer : observers) {
            try {
                observer.onFlowStart(this,memory);
            } catch (Exception e) {
                log.debug(e.getMessage());
            }
        }
    }

    private void emitFlowEnd(Map<String, Object> memory) {
        try{
            flowLogService.onFlowEnd();
            flowLogService.log(logString.toString());
        }catch (Exception e){
            log.debug(e.getMessage());
        }
        for (Observer observer : observers) {
            try {
                observer.onFlowEnd(this,memory);
            } catch (Exception e) {
                log.debug(e.getMessage());
            }
        }
    }

    private void emitBlockStart(BaseBlock block, Map<String, Object> memory) {
        for (Observer observer : observers) {
            try {
                observer.onBlockStart(this,block, memory);
            } catch (Exception e) {
                log.debug(e.getMessage());
            }
        }
    }

    private void emitBlockEnd(BaseBlock block, Map<String, Object> memory) {
        for (Observer observer : observers) {
            try {
                observer.onBlockEnd(this,block, memory);
            } catch (Exception e) {
                log.debug(e.getMessage());
            }
        }
    }
    public void emitBlockError(BaseBlock block,Map<String,Object> memory,FlowException exception) {
        for (Observer observer : observers) {
            try {
                observer.onBlockError(this,block, cloneMemory(memory),exception);
            } catch (Exception e) {
                log.debug(e.getMessage());
            }
        }
    }
    private void emitFlowError(Map<String, Object> memory, FlowException exception){
        try{
            flowLogService.onFlowError();
            flowLogService.log(logString.toString());
        }catch (Exception e){
            log.debug(e.getMessage());
        }
        for (Observer observer : observers) {
            try {
                observer.onFlowError(this,memory,exception);
            } catch (Exception e) {
                log.debug(e.getMessage());
            }
        }
    }

    @Override
    public FlowContext getFlow() {
        return flow;
    }

    @Override
    public JSONObject getCurrentRecord() {
        return currentRecord;
    }

    @Override
    public BaseBlock getCurrentExecutionBlock() {
        return currentBlock;
    }

    @Override
    public void log(FlowLogLevel logLevel, String logs) {
        if(isLoggable(logLevel)){
            logString.append(logs+"\n");
        }
    }
    private boolean isLoggable(FlowLogLevel logLevel){
        switch (logLevel){
            case INFO:
            case SEVERE:
                return true;
            case DEBUG:
                if(level==FlowLogLevel.DEBUG){
                    return true;
                }
        }
        return false;
    }

    @Override
    public void setFlowLogService(FlowLogService flowLogService) {
     this.flowLogService = flowLogService;
    }

    @Override
    public FlowLogService getFlowLogService() {
        return flowLogService;
    }
    private Map<String,Object> cloneMemory(Map<String, Object> memory){
        return FieldUtil.cloneBean(memory, LinkedHashMap.class);
    }
}
