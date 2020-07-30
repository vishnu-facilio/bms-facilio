package com.facilio.elasticsearch.util;

import com.facilio.accounts.util.AccountUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ESQuery {
    private JSONObject query = new JSONObject();
    private JSONArray array = new JSONArray();

    private JSONArray filters = new JSONArray();

    private ESQuery(boolean must) {
        JSONObject json = new JSONObject();
        if (must) {
            json.put("must", array);
        }
        else {
            json.put("should", array);
        }

        json.put("filter", filters);
        query.put("bool", json);
    }

    public void addQuery(JSONObject match) {
        array.add(match);
    }

    public void addFilter(JSONObject filter) {
        filters.add(filter);
    }

    public JSONObject getQuery() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("query", query);
        return jsonObject;
    }

    public static class ESQueryBuilder {
        private ESQuery query;

        public ESQueryBuilder(boolean must) {
            query = new ESQuery(must);
            addFilter("orgId", AccountUtil.getCurrentOrg().getOrgId());
        }

        public ESQueryBuilder addQueryString(String value) {
            JSONObject jsonObject = getQueryObject("query_string", "query", "*" + value + "*");
            query.addQuery(jsonObject);
            return this;
        }

        public ESQueryBuilder addMatch(String key, Object value) {
            JSONObject jsonObject = getQueryObject("match", key, value);
            query.addQuery(jsonObject);
            return this;
        }

        public ESQueryBuilder addFilter(String key, Object value) {
            JSONObject jsonObject = getQueryObject("match", key, value);
            query.addFilter(jsonObject);
            return this;
        }

        public ESQueryBuilder addFilter(JSONObject filter) {
            query.addFilter(filter);
            return this;
        }

        private JSONObject getQueryObject(String esKey, String key, Object value) {
            JSONObject jsonObject = new JSONObject();
            JSONObject matchJSON = new JSONObject();
            matchJSON.put(key, value);
            jsonObject.put(esKey, matchJSON);
            return jsonObject;
        }

        public JSONObject build() {
            return query.getQuery();
        }
    }
}
