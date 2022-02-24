package com.facilio.storm.action;

//import com.facilio.dps.example.ex1.LiveFormulaBolt;
//import com.facilio.dps.example.ex1.RootSpout;
//import com.facilio.dps.example.ex1.RuleExecutorBolt;
//import com.facilio.dps.example.ex1.SensorMain;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.storm.Config;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.topology.TopologyBuilder;
import org.json.simple.JSONObject;

public class StormAdminAction extends ActionSupport {

    private String ruleId;

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getRuleId() {
        return this.ruleId;
    }

    public String submitTopology() throws AuthorizationException, InvalidTopologyException, AlreadyAliveException {

//        long start = System.currentTimeMillis();
//        TopologyBuilder builder = new TopologyBuilder();
//        builder.setSpout("root-spout", new RootSpout(), 1);
//        builder.setBolt("sensor", new SensorMain(), 1).shuffleGrouping("root-spout");
//
//        builder.setBolt("live-formula", new LiveFormulaBolt(), 2).allGrouping("sensor");
//        builder.setBolt("rule-executor", new RuleExecutorBolt(), 2).allGrouping("sensor");
//
//        System.setProperty("storm.jar", "/Users/prabhakarans/workspace/data-processing-service/target/data-processing-service-1.0-SNAPSHOT.jar");
//
//        for (int i = 0; i < 100; i++) {
//            StormSubmitter.submitTopology("Rule_" + i, new Config(), builder.createTopology());
//        }
//
//        System.out.println("Time taken for 100 topologies : " + (System.currentTimeMillis() - start));
//
        return new JSONObject().toString();
    }
}
