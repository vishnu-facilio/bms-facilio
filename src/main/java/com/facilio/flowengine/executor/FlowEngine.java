package com.facilio.flowengine.executor;

import com.facilio.blockfactory.blocks.BaseBlock;
import com.facilio.flowengine.observers.Observer;
import com.facilio.flows.context.FlowContext;
import com.facilio.modules.FieldUtil;
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
    private JSONObject currentRecord;

    private int blocksExecuted = 0;
    private List<Observer> observers = new ArrayList<>();
    public FlowEngine(FlowContext flow,JSONObject currentRecord){
        this.flow = Objects.requireNonNull(flow);
        this.currentRecord = Objects.requireNonNull(currentRecord);
    }

    public void addObserver(Observer observer) {
        if (observer != null) {
            observers.add(observer);
        }
    }

    public void execute(BaseBlock startBlock,Map<String,Object> memory) throws Exception {
        long currentMillis = System.currentTimeMillis();

        try{
            emitFlowStart(FieldUtil.cloneBean(memory, LinkedHashMap.class));
            BaseBlock currentBlock = startBlock;
            while (currentBlock != null) {
                blocksExecuted++;
                currentBlock.setFlowEngineInterFace(this);
                emitBlockStart(currentBlock, FieldUtil.cloneBean(memory, LinkedHashMap.class));

                currentBlock.execute(memory);

                emitBlockEnd(currentBlock, FieldUtil.cloneBean(memory, LinkedHashMap.class));
                if (blocksExecuted > maxBlocks) {
                    break;
                }
                currentBlock = currentBlock.getNextBlock();
            }
            emitFlowEnd(FieldUtil.cloneBean(memory, LinkedHashMap.class));
        }catch (Exception e){
            String errorMeg = "Exception occurred in flowEngine for flowId:"+flow.getId()+" message:"+e.getMessage();
            LOGGER.log(Level.SEVERE, errorMeg, e);
            throw e;
        }
        finally {
            long executionTime = System.currentTimeMillis() - currentMillis;
            String msg = MessageFormat.format("### time taken for flow ({0}) is {1}", flow.getId(), executionTime);
            log.debug(msg);
        }

    }

    private void emitFlowStart(Map<String, Object> memory) {
        for (Observer observer : observers) {
            try {
                observer.onFlowStart(memory);
            } catch (Exception e) {

            }
        }
    }

    private void emitFlowEnd(Map<String, Object> memory) {
        for (Observer observer : observers) {
            try {
                observer.onFlowEnd(memory);
            } catch (Exception e) {

            }
        }
    }

    private void emitBlockStart(BaseBlock block, Map<String, Object> memory) {
        for (Observer observer : observers) {
            try {
                observer.onBlockStart(block, memory);
            } catch (Exception e) {

            }
        }
    }

    private void emitBlockEnd(BaseBlock block, Map<String, Object> memory) {
        for (Observer observer : observers) {
            try {
                observer.onBlockEnd(block, memory);
            } catch (Exception e) {

            }
        }
    }

    @Override
    public FlowContext getFlow() {
        return flow;
    }

    @Override
    public JSONObject getCurrentRecord() { return currentRecord; }
}
