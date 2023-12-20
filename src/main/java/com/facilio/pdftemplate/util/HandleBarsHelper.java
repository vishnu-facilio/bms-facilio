package com.facilio.pdftemplate.util;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public class HandleBarsHelper {

    private Handlebars handlebars;

    public HandleBarsHelper(){
        this.handlebars = new Handlebars();
        registerCustomHelpers();
    }

    public Handlebars getHandlebars() {
        return handlebars;
    }

    private void registerCustomHelpers(){
        registerGroupDataHelper();
        registerValuePlusOneHelper();
        registerFilterListHelper();
    }
    private void registerGroupDataHelper(){
        handlebars.registerHelper("groupData", new Helper<List<Map<String,Object>>>() {
            @Override
            public Map<Object,List<Map<String,Object>>> apply(List<Map<String,Object>> list,Options options){
                String groupByField = options.param(0);
                Map<Object,List<Map<String,Object>>> result = new HashMap<>();
                for(Map<String,Object> item : list){
                    if(item.containsKey(groupByField) && !result.containsKey(item.get(groupByField))){
                        List<Map<String,Object>> values = new ArrayList<>();
                        values.add(item);
                        result.put(item.get(groupByField),values);
                    }else if(result.containsKey(item.get(groupByField))){
                        List<Map<String,Object>> values = result.get(item.get(groupByField));
                        values.add(item);
                        result.put(item.get(groupByField),values);
                    }
                }
                return result;
            }
        });
    }
    private void registerValuePlusOneHelper(){
        handlebars.registerHelper("valuePlusOne", new Helper<Integer>() {
            @Override
            public Integer apply(Integer value, Options options) {
                return value + 1;
            }
        });
    }
    private void registerFilterListHelper(){
        handlebars.registerHelper("filterList", new Helper<List<Map<String,Object>>>() {
            @Override
            public List<Map<String,Object>> apply(List<Map<String,Object>> list,Options options) {
                String filterFieldName = options.param(0);
                String filterFieldValue = options.param(1);
                if(CollectionUtils.isNotEmpty(list)){
                   return list.stream().filter(item -> item.containsKey(filterFieldName) && item.get(filterFieldName).equals(filterFieldValue)).collect(Collectors.toList());
                }
                return list;
            }
        });
    }
}
