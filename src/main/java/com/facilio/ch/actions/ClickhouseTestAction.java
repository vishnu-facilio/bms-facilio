package com.facilio.ch.actions;

import lombok.Setter;

import com.facilio.ch.ClickhouseUtil;
import com.facilio.v3.V3Action;

/*
this action class is purely for testing purpose. will remove it later
 */
@Setter
public class ClickhouseTestAction extends V3Action {

    public String fetchSampleSelectQuery() throws Exception {
        setData("clickhouse-true",ClickhouseUtil.fetchSampleSelectQuery(true));
        setData("clickhouse-false",ClickhouseUtil.fetchSampleSelectQuery(false));
        return V3Action.SUCCESS;
    }

    public String fetchSampleAggrQuery() throws Exception {
        setData(ClickhouseUtil.fetchSampleAggrQuery());
        setData("sample1", ClickhouseUtil.getAggregatedTableName("Electricity_Data", "Europe/London", "Daily"));
        setData("sample2", ClickhouseUtil.getAggregatedTableName("Electricity_Data", "Asia/Kolkata", "Daily"));
        setData("sample3", ClickhouseUtil.getAggregatedTableName("Readings_3", "Europe/London", "Daily"));
        return V3Action.SUCCESS;
    }

}
