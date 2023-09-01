package com.facilio.wmsv2.actions;

import com.facilio.fw.cache.RedisManager;
import com.facilio.server.ServerInfo;
import com.facilio.v3.V3Action;
import com.facilio.wmsv2.endpoint.LiveSession;
import com.facilio.wmsv2.endpoint.SessionManager;
import lombok.Data;
import org.json.simple.JSONObject;
import redis.clients.jedis.Jedis;

import java.util.*;
import java.util.stream.Collectors;

@Data
public class WmsAction extends V3Action {

    private long orgId = -1;

    public String getWmsStats() {
        Collection<LiveSession> liveSessionList = SessionManager.getInstance().getLiveSessions();
        List<JSONObject> liveSessions = new ArrayList<>();
        Set<String> topics = new HashSet<>();
        for(LiveSession ls : liveSessionList) {
            if(orgId == -1 || ls.getOrgId() == orgId) {
                topics.addAll(ls.getTopics());
                liveSessions.add(ls.toJson());
            }
        }
        setData("liveSessions", liveSessions);
        setData(ServerInfo.getHostname()+"-Topics", topics);

        try(Jedis jedis = RedisManager.getInstance().getJedis()) {
            List<String> channels = jedis.pubsubChannels("*subscribe__channel");
            List<String> allSubscribedTopics = jedis.pubsubChannels("*wms*").stream()
                    .filter(r -> !channels.contains(r)).collect(Collectors.toList());
            setData("channels", channels);
            setData("allActiveTopics", allSubscribedTopics);
        }

        return V3Action.SUCCESS;
    }

    public String getActiveConnections() {
        int activeConnections = 0;
        try(Jedis jedis = RedisManager.getInstance().getJedis()) {
            String info = jedis.info("clients");

            // Parse the INFO output to extract the number of connected clients

            for (String line : info.split("\n")) {
                if (line.startsWith("connected_clients:")) {
                    String[] parts = line.split(":");
                    activeConnections = Integer.parseInt(parts[1].trim());
                    break;
                }
            }
        }
        setData("activeConnections", activeConnections);
        return V3Action.SUCCESS;
    }
}