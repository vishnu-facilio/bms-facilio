"use strict";(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[45817],{
/***/28922:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return OutOfScheduleChart}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/components/page/widget/alarms/OutOfScheduleChart.vue?vue&type=template&id=32ba1e8f
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"height100 d-flex rule-impact-page"},[_vm.widget.widgetParams.isEmpty?_c("div",{staticClass:"block"},[_c("div",{staticClass:"mT40 mB40 text-center p30imp"},[_c("InlineSvg",{attrs:{src:"svgs/emptystate/alarmEmpty",iconClass:"icon text-center icon-xxxxlg emptystate-icon-size"}}),_c("div",{staticClass:"pT20 fc-black-dark f18 bold"},[_vm._v("No Alarm!")])],1)]):_c("div",{staticClass:"height100"},[_c("common-widget-chart",_vm._b({key:"impactdetails",attrs:{type:"stackedbar",showPeriodSelect:"true",customizeChartOptions:_vm.getDatePickerObject(),moduleName:"operationalarmoccurrence",isWidget:"true"}},"common-widget-chart",_vm.$props,!1),[_c("template",{slot:"title"},[_vm._v("Operation Schedule Violation Report")])],2)],1)])},staticRenderFns=[],CommonWidgetChart=(__webpack_require__(648324),__webpack_require__(190491)),NewDateHelper=__webpack_require__(105534),OutOfScheduleChartvue_type_script_lang_js={props:["details","layoutParams","resizeWidget","hideTitleSection","groupKey","activeTab","widget"],components:{CommonWidgetChart:CommonWidgetChart/* default */.Z},data:function(){return{}},methods:{getDatePickerObject:function(){var dateObj=NewDateHelper/* default */.Z.getDatePickerObject(20,this.details.alarm?[this.details.occurrence.createdTime,this.details.alarm.lastOccurredTime]:null);return{dateFilter:JSON.stringify(dateObj)}}}},alarms_OutOfScheduleChartvue_type_script_lang_js=OutOfScheduleChartvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(alarms_OutOfScheduleChartvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,OutOfScheduleChart=component.exports},
/***/190491:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// EXPORTS
__webpack_require__.d(__webpack_exports__,{Z:function(){/* binding */return CommonWidgetChart}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/components/page/widget/performance/charts/CommonWidgetChart.vue?vue&type=template&id=76d6eca4
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"d-flex flex-direction-column"},[_c("div",{directives:[{name:"show",rawName:"v-show",value:!_vm.datePickerHide,expression:"datePickerHide ? false : true"}],staticClass:"border-bottom1px d-flex justify-content-space"},[_c("div",{staticClass:"f16 bold mT20 mB20 mL30 inline"},[_vm._t("title")],2),_c("div",{class:{mT15:_vm.isDateFixed,mT10:!_vm.isDateFixed}},[_c("new-date-picker",{staticClass:"filter-field date-filter-comp",attrs:{zone:_vm.$timezone,dateObj:_vm.dateObj,isDateFixed:_vm.isDateFixed},on:{date:_vm.changeDateFilter}})],1)]),_vm.$validation.isEmpty(_vm.moduleMeta)||!_vm.isChartPrepared||_vm.loading?_vm._e():_c("div",{staticClass:"widget-chart-container"},[_c("f-new-analytic-modular-report",{attrs:{serverConfig:_vm.serverConfig,module:_vm.moduleObj,defaultChartType:_vm.type||"spline",hideTabs:!0,hideHeader:!0,hidecharttypechanger:!0,chartType:_vm.type,isWidget:_vm.isWidget,showPeriodSelect:_vm.showPeriodSelect},on:{"update:serverConfig":function($event){_vm.serverConfig=$event},"update:server-config":function($event){_vm.serverConfig=$event},reportLoaded:_vm.onReportLoaded}})],1)])},staticRenderFns=[],createForOfIteratorHelper=__webpack_require__(566347),objectSpread2=__webpack_require__(595082),validation=(__webpack_require__(434284),__webpack_require__(260228),__webpack_require__(947522),__webpack_require__(186466),__webpack_require__(425728),__webpack_require__(506203),__webpack_require__(670560),__webpack_require__(990260)),NewDatePicker=__webpack_require__(900773),FNewAnalyticModularReport=__webpack_require__(247942),NewDateHelper=__webpack_require__(105534),CommonWidgetChartvue_type_script_lang_js={props:["details","layoutParams","resizeWidget","hideTitleSection","groupKey","activeTab","widget","moduleName","type","refresh","datePickerHide","isWidget","customizeChartOptions","showPeriodSelect","isDateFixed"],components:{NewDatePicker:NewDatePicker/* default */.Z,FNewAnalyticModularReport:FNewAnalyticModularReport/* default */.Z},data:function(){return{loading:!1,isChartPrepared:!1,moduleMeta:{},chartType:"line",serverConfig:{criteria:null,dateFilter:null,xField:null,yField:null},moduleObj:null,dateObj:NewDateHelper/* default */.Z.getDatePickerObject(44,null)}},created:function(){this.initializeReport(),this.getMeta().then(this.prepareReportData)},methods:{initializeReport:function(){var _this$widget$widgetPa=this.widget.widgetParams.chartParams,dateOperator=_this$widget$widgetPa.dateOperator,dateOperatorValue=_this$widget$widgetPa.dateOperatorValue;this.dateObj=NewDateHelper/* default */.Z.getDatePickerObject(dateOperator,dateOperatorValue)},getMeta:function(){var _this=this;this.loading=!0;var _ref=this||this.widget.widgetParams.chartParams,moduleName=_ref.moduleName;return this.$http.get("/module/meta?moduleName="+moduleName).then((function(response){_this.moduleMeta=response.data.meta,_this.moduleObj={moduleName:response.data.meta.name,moduleId:response.data.meta.module.moduleId,meta:{fieldMeta:{}}},_this.loading=!1}))},prepareReportData:function(){var _this2=this,customizeChartOptions=this.customizeChartOptions;if(!(0,validation/* isEmpty */.xb)(this.moduleMeta)){var _this$widget$widgetPa2=this.widget.widgetParams.chartParams,criteria=_this$widget$widgetPa2.criteria,chartType=_this$widget$widgetPa2.chartType,xField=_this$widget$widgetPa2.xField,yField=_this$widget$widgetPa2.yField,groupBy=_this$widget$widgetPa2.groupBy,isMultipleMetric=_this$widget$widgetPa2.isMultipleMetric,dateField=_this$widget$widgetPa2.dateField;criteria&&Object.values(criteria.conditions).forEach((function(condition){delete condition.operator}));var xFieldObj=(this.moduleMeta.fields||{}).find((function(field){return field.name===xField.fieldName})),groupByFieldObj=null,yFieldObj=null,dateFieldObj=null;dateField&&(dateFieldObj=(this.moduleMeta.fields||{}).find((function(field){return field.name===dateField.fieldName})));var newConfig={criteria:criteria,xField:{field_id:xFieldObj.fieldId,module_id:xFieldObj.moduleId,aggr:xField.aggr},dateFilter:(0,objectSpread2/* default */.Z)({},this.dateObj)};if(xFieldObj&&6===xFieldObj.dataType&&newConfig.dateFilter){var tempdateField={field_id:xFieldObj.fieldId,fieldName:xFieldObj.name,module_id:xFieldObj.moduleId,operator:newConfig.dateFilter.operatorId};49===newConfig.dateFilter.operatorId||50===newConfig.dateFilter.operatorId||51===newConfig.dateFilter.operatorId?tempdateField["date_value"]=Math.abs(newConfig.dateFilter.offset)+"":20===newConfig.dateFilter.operatorId?tempdateField["date_value"]=newConfig.dateFilter.value.join(","):tempdateField["date_value"]=newConfig.dateFilter.value[0]+"",newConfig.dateField=tempdateField,newConfig.isTime=!0}else if(dateFieldObj){var tempDateField={field_id:dateFieldObj.fieldId,fieldName:dateFieldObj.name,module_id:dateFieldObj.moduleId,operator:newConfig.dateFilter.operatorId};49===newConfig.dateFilter.operatorId||50===newConfig.dateFilter.operatorId||51===newConfig.dateFilter.operatorId?tempDateField["date_value"]=Math.abs(newConfig.dateFilter.offset)+"":20===newConfig.dateFilter.operatorId?tempDateField["date_value"]=newConfig.dateFilter.value.join(","):tempDateField["date_value"]=newConfig.dateFilter.value[0]+"",newConfig.dateField=tempDateField,newConfig.isTime=!0}else newConfig.dateField=null,newConfig.isTime=!1;if(isMultipleMetric){newConfig["yField"]=[];var _step,_iterator=(0,createForOfIteratorHelper/* default */.Z)(yField);try{var _loop=function(){var yObj=_step.value;yObj?(yFieldObj=(_this2.moduleMeta.fields||{}).find((function(field){return field.name===yObj.fieldName})),newConfig.yField.push({field_id:yFieldObj.fieldId,module_id:yFieldObj.moduleId,aggr:yObj.aggr})):newConfig.yField.push(null)};for(_iterator.s();!(_step=_iterator.n()).done;)_loop()}catch(err){_iterator.e(err)}finally{_iterator.f()}}else yField?(yFieldObj=(this.moduleMeta.fields||{}).find((function(field){return field.name===yField.fieldName})),newConfig["yField"]=[{field_id:yFieldObj.fieldId,module_id:yFieldObj.moduleId,aggr:yField.aggr}]):newConfig["yField"]=null;groupBy.fieldName&&(groupByFieldObj="plannedvsunplanned"===groupBy.fieldName?{fieldId:"plannedvsunplanned",module_id:xFieldObj.moduleId}:(this.moduleMeta.fields||{}).find((function(field){return field.name===groupBy.fieldName})),newConfig["groupBy"]=[{field_id:groupByFieldObj.fieldId,module_id:groupByFieldObj.moduleId}]),this.serverConfig=(0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)({},this.serverConfig),newConfig),(0,validation/* isEmpty */.xb)(customizeChartOptions)||this.$set(this.serverConfig,"customizeChartOptions",customizeChartOptions),this.chartType=chartType,this.isChartPrepared=!0}},changeDateFilter:function(dateFilter){this.dateObj=dateFilter},onReportLoaded:function(report,result){this.$emit("reportLoaded",report,result)}},watch:{dateObj:function(){this.prepareReportData()},refresh:function(){this.prepareReportData()}}},charts_CommonWidgetChartvue_type_script_lang_js=CommonWidgetChartvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(charts_CommonWidgetChartvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,CommonWidgetChart=component.exports},
/***/23369:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// EXPORTS
__webpack_require__.d(__webpack_exports__,{Z:function(){/* binding */return FChartTitle}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/newcharts/components/FChartTitle.vue?vue&type=template&id=1781dc98
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"f-new-chart-title"},[_vm.titeList&&_vm.titeList&&_vm.titeList.length&&!_vm.loading?_c("div",{staticClass:"flex-middle justify-center"},_vm._l(_vm.titeList,(function(data,index){return _c("div",{key:index,staticClass:"f-chart-mandv-title",style:_vm.getStyle(data)},[_vm._v(" "+_vm._s(data.title)+" ")])})),0):_vm._e()])},staticRenderFns=[],FChartTitlevue_type_script_lang_js={props:["chartContext","config"],data:function(){return{titeList:[],loading:!1}},mounted:function(){var self=this;this.loading=!0,setTimeout((function(){self.titeList=self.chartContext.regionsTitles,self.loading=!1}),1e3)},methods:{getStyle:function(data){return"width:"+data.diff+"px"}}},components_FChartTitlevue_type_script_lang_js=FChartTitlevue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(components_FChartTitlevue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,FChartTitle=component.exports},
/***/875963:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// EXPORTS
__webpack_require__.d(__webpack_exports__,{Z:function(){/* binding */return FNewChart}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/newcharts/components/FNewChart.vue?vue&type=template&id=8078524e
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"height100 f-singlechart"},[_vm.hidecharttypechanger?_vm._e():_c("f-chart-type",{staticClass:"charttypechanger fc-new-chart-type-single top-minus-86",attrs:{options:_vm.options},on:{getOptions:_vm.getOptionsFromTypeChart}}),_vm.chartContext&&!1!==_vm.options.settings.chart?_c("div",{staticStyle:{"text-align":"center"}},[_c("div",{style:_vm.isWidget?"position: relative; top: 15px;":""},[_vm.showAlarms&&_vm.showAlarmTitle?_c("div",{staticClass:"fc-alarms-chart-title",style:"sensorrollupalarm"===_vm.moduleName?"color: #324056 !important;font-weight: bold;":""},[_vm._v(" Alarms ")]):_vm._e(),_c("div",{ref:"alarmsChartEle",staticClass:"fc-alarms-chart pdf-chart"})]),_vm.relatedAlarms?_c("div",[_vm._l(_vm.relatedAlarms,(function(relAlarm,idx){return[_c("div",{key:idx,style:_vm.isWidget?"position: relative; top: 15px;":""},[_c("div",{staticClass:"fc-alarms-chart-title",staticStyle:{"text-transform":"capitalize",color:"#324056 !important"}},[_vm._v(" "+_vm._s(relAlarm?relAlarm.alarmTitle:"")+" ")]),_c("div",{ref:"relatedAlarmsChartEle",refInFor:!0,staticClass:"fc-alarms-chart pdf-chart"})])]}))],2):_vm._e(),_vm.chartContext.options.legend.show&&"top"===_vm.chartContext.options.legend.position?_c("f-chart-legends",{ref:"chartLegend",attrs:{chart:_vm.c3.chart,options:_vm.options,resultObj:_vm.resultObj,xValueMode:_vm.chartContext.xValueMode,xLabelMap:_vm.chartContext.xLabelMap,config:_vm.config,isScatterPlot:_vm.chartContext&&_vm.chartContext.scatterConfig},on:{ontoggle:_vm.ontoggle}}):_vm._e(),_vm.chartContext.options.regionConfig?_c("f-chart-title",{ref:"chartTitle",attrs:{chart:_vm.c3.chart,options:_vm.options,xValueMode:_vm.chartContext.xValueMode,xLabelMap:_vm.chartContext.xLabelMap,config:_vm.config,chartContext:_vm.chartContext},on:{ontoggle:_vm.ontoggle}}):_vm._e(),_vm.drilldownParams?_c("drilldown-breadcrumb",{attrs:{drilldownParams:_vm.drilldownParams},on:{crumbClick:function($event){return _vm.$emit("crumbClick",$event)}}}):_vm._e(),_c("div",{staticClass:"fc-newchart-container",class:{"scatter-full-opacity":_vm.c3.params&&_vm.c3.params.data&&_vm.c3.params.data.types&&!Object.values(_vm.c3.params.data.types).includes("bubble")}},[_c("div",{ref:"newChartEle",staticClass:"fc-new-chart pdf-chart",class:{"hide-y2-axis":_vm.options&&_vm.options.axis&&!1===_vm.options.axis.showy2axis}}),_vm.chartContext.options.legend.show&&"right"===_vm.chartContext.options.legend.position?_c("f-chart-legends",{style:{width:_vm.chartContext.options.legend.width+"px"},attrs:{chart:_vm.c3.chart,options:_vm.options,xValueMode:_vm.chartContext.xValueMode,xLabelMap:_vm.chartContext.xLabelMap,config:_vm.config},on:{ontoggle:_vm.ontoggle}}):_vm._e()],1),_vm.chartContext.options.legend.show&&"bottom"===_vm.chartContext.options.legend.position?_c("f-chart-legends",{ref:"chartLegend",attrs:{chart:_vm.c3.chart,options:_vm.options,xValueMode:_vm.chartContext.xValueMode,xLabelMap:_vm.chartContext.xLabelMap},on:{ontoggle:_vm.ontoggle}}):_vm._e(),_vm.showWidget?_c("f-widget-legends",{ref:"newWidget",staticClass:"widget-legends",attrs:{chart:_vm.c3.chart,reportVarianceData:_vm.resultObj?_vm.resultObj.reportData.aggr:null,data:_vm.data,options:_vm.options},on:{ontoggle:_vm.ontoggle}}):_vm._e()],1):_vm._e()],1)},staticRenderFns=[],createForOfIteratorHelper=__webpack_require__(566347),billboard=(__webpack_require__(976801),__webpack_require__(843843),__webpack_require__(260228),__webpack_require__(212826),__webpack_require__(670560),__webpack_require__(76265),__webpack_require__(169358),__webpack_require__(795765),__webpack_require__(879288),__webpack_require__(872695)),basechart=__webpack_require__(503162),ReportDataUtil=__webpack_require__(760396),c3_helper=__webpack_require__(167021),FChartLegends=__webpack_require__(906575),DrilldownBreadcrumb=__webpack_require__(374520),FChartTitle=__webpack_require__(23369),FWidgetLegends=__webpack_require__(547301),FChartType=__webpack_require__(294309),JumpToHelper=__webpack_require__(921466),tooltip=__webpack_require__(460895),color_helper=__webpack_require__(794211),deepmerge=__webpack_require__(708634),d3=__webpack_require__(348727),formatter=__webpack_require__(676240),validation=__webpack_require__(990260),FNewChartvue_type_script_lang_js={mixins:[basechart/* default */.Z,JumpToHelper/* default */.Z,ReportDataUtil/* default */.Z],props:["resultObj","isWidget","showWidgetLegends","config","specialScatter","relatedAlarms","moduleName","drilldownParams"],components:{FChartLegends:FChartLegends/* default */.Z,FWidgetLegends:FWidgetLegends/* default */.Z,FChartType:FChartType/* default */.Z,FChartTitle:FChartTitle/* default */.Z,DrilldownBreadcrumb:DrilldownBreadcrumb/* default */.Z},computed:{showWidget:function(){return!("undefined"!==typeof this.showWidgetLegends&&!this.showWidgetLegends)},isRegression:function(){return!!(this.$route.path.includes("regression")||this.resultObj&&this.resultObj.regressionConfig&&0!==this.resultObj.regressionConfig.length&&!0===this.resultObj.regression)},showAlarmTitle:function(){return!this.chartContext||!this.chartContext.options||!this.chartContext.options.hideAlarmTitle}},data:function(){return{chartContext:null,chartId:Math.random().toString(36).substr(2,9),c3:{params:null,chart:null},showAlarms:!1,updateTimeout:null}},mounted:function(){this.isRegression&&(this.hidecharttypechanger=!0),this.render()},beforeDestroy:function(){tooltip/* default */.Z.hideTooltip(),this.c3&&this.c3.chart&&this.c3.chart.destroy(),this.chartContext=null,this.c3.params=null,this.c3.chart=null},methods:{render:function(){var _this=this;this.data&&(this.chartContext=null,this.$nextTick((function(){_this.chartContext={},_this.chartContext.options=_this.getOptions(),_this.chartContext.dateRange=_this.dateRange,_this.chartContext.options.general&&_this.chartContext.options.general.hideZeroes?_this.chartContext.data=_this.formatZeroesInData(_this.data):_this.chartContext.data=_this.data,_this.chartContext.options.benchmark&&_this.chartContext.options.benchmark.show&&(_this.chartContext.reportVarianceData=_this.resultObj?_this.resultObj.reportData.aggr:null),_this.chartContext.xValueMode=_this.isXValueMode(_this.chartContext.options,_this.data),_this.options.defaultDurationUnit&&(_this.chartContext.defaultDurationUnit=_this.options.defaultDurationUnit),_this.chartContext.specialScatter=!!_this.specialScatter,_this.chartContext.unitMap=_this.getUnitMap(_this.options.dataPoints),_this.chartContext.enumMap=_this.getEnumMap(_this.options.dataPoints),_this.resultObj&&_this.resultObj.baselineData&&(_this.chartContext.baselineColors=_this.resultObj.baselineDataColors,_this.chartContext.baselineData=_this.resultObj.baselineData),_this.chartContext.openAlarm=function(alarmList){if(alarmList){var _step,alarmId=[],_iterator=(0,createForOfIteratorHelper/* default */.Z)(alarmList);try{for(_iterator.s();!(_step=_iterator.n()).done;){var a=_step.value;alarmId.push(a.id)}}catch(err){_iterator.e(err)}finally{_iterator.f()}_this.jumpToAlarms(alarmId)}},"pie"!==_this.chartContext.options.type&&"donut"!==_this.chartContext.options.type||(_this.chartContext.options.padding.top=20),_this.resultObj&&_this.resultObj.Readingdp&&(_this.chartContext["Readingdp"]=_this.resultObj.Readingdp),_this.resultObj&&_this.resultObj.scatterConfig&&(_this.chartContext["scatterConfig"]=_this.resultObj.scatterConfig,_this.chartContext["xMap"]=_this.resultObj.xMap);var c3Params=c3_helper/* default */.Z.prepare(_this.chartContext);c3Params&&(c3Params.data.onclick=function(d){var _d;if(null==(null===(_d=d)||void 0===_d?void 0:_d.index))for(var i=0;i<d.values.length;i++){var _d2;if((null===(_d2=d)||void 0===_d2?void 0:_d2.values[i].id)==d.id){d=d.values[i];break}}_this.$emit("drilldown",d)}),_this.mergeOption&&(_this.chartContext.options.padding.left=_this.mergeOption.padding.left,_this.chartContext.options.padding.right=_this.mergeOption.padding.right,_this.chartContext.options.axis.x.label.text="Month",c3Params.axis.y.label.text="No of Work Request"),_this.isWidget&&(_this.chartContext.options.legend.show?c3Params.size.height=c3Params.size.height-50:c3Params.size.height=c3Params.size.height-20,_this.options.widgetLegend.show&&(c3Params.size.height=c3Params.size.height-75),_this.resultObj&&_this.resultObj.report.userFilters&&(c3Params.size.height=c3Params.size.height-65)),_this.drilldownParams&&(c3Params.size.height=c3Params.size.height-25,_this.isWidget&&(c3Params.size.height=c3Params.size.height-25)),"gauge"===_this.chartContext.options.type&&c3Params.size.height>350&&(c3Params.size.height=350);var self=_this;self.showAlarms=1===_this.getOptions().common.mode&&_this.getOptions().settings.alarm,_this.config&&_this.config.hasOwnProperty("showAlarms")&&(self.showAlarms=_this.config.showAlarms),self.showAlarms&&(_this.chartContext.alarms=_this.alarms,c3Params.size.height=c3Params.size.height-100);var alarmsParams=null;if(self.showAlarms&&(alarmsParams=c3_helper/* default */.Z.prepareBooleanChart(_this.chartContext,c3Params.data.json.x,_this.alarms),self.$refs["alarmsChartEle"]&&(alarmsParams.bindto=self.$refs["alarmsChartEle"],billboard.bb.generate(alarmsParams))),self.relatedAlarms&&self.relatedAlarms.length)for(var idx in self.relatedAlarms)if(self.$refs["relatedAlarmsChartEle"]&&self.$refs["relatedAlarmsChartEle"][idx]){var relalarmsParams=c3_helper/* default */.Z.prepareBooleanChart(_this.chartContext,c3Params.data.json.x,self.relatedAlarms[idx]);relalarmsParams.bindto=self.$refs["relatedAlarmsChartEle"][idx],billboard.bb.generate(relalarmsParams)}if(_this.chartContext.options.customizeC3){var defaultOptions={},mergedOptions=deepmerge/* default */.Z.objectAssignDeep(defaultOptions,c3Params,_this.chartContext.options.customizeC3);c3Params=mergedOptions}var print=JSON.parse(_this.$getProperty(_this,"$route.query.printing")||null);if(self.$refs["newChartEle"]){c3Params.bindto=self.$refs["newChartEle"],self.isWidget&&self.$refs["chartLegend"]&&(c3Params.size.height=c3Params.size.height-(self.$refs["chartLegend"].$el.clientHeight-20)),self.fixedChartHeight&&(c3Params.size.height=self.fixedChartHeight);var width=self.$refs["newChartEle"].clientWidth;print&&!(0,validation/* isEmpty */.xb)(width)&&(c3Params.size.width=self.$refs["newChartEle"].clientWidth),self.c3.params=c3Params,self.c3.chart=billboard.bb.generate(_this.byPassSetting(c3Params))}else _this.$nextTick((function(){if(self.showAlarms&&self.$refs["alarmsChartEle"]&&(alarmsParams.bindto=self.$refs["alarmsChartEle"],billboard.bb.generate(alarmsParams)),self.relatedAlarms&&self.relatedAlarms.length)for(var _idx in self.relatedAlarms)if(self.$refs["relatedAlarmsChartEle"]&&self.$refs["relatedAlarmsChartEle"][_idx]){var _relalarmsParams=c3_helper/* default */.Z.prepareBooleanChart(this.chartContext,c3Params.data.json.x,self.relatedAlarms[_idx]);_relalarmsParams.bindto=self.$refs["relatedAlarmsChartEle"][_idx],billboard.bb.generate(_relalarmsParams)}c3Params.bindto=self.$refs["newChartEle"];var width=self.$refs["newChartEle"].clientWidth;print&&!(0,validation/* isEmpty */.xb)(width)&&(c3Params.size.width=self.$refs["newChartEle"].clientWidth),self.isWidget&&self.$refs["chartLegend"]&&(c3Params.size.height=c3Params.size.height-(self.$refs["chartLegend"].$el.clientHeight-20)),self.fixedChartHeight&&(c3Params.size.height=self.fixedChartHeight),self.c3.params=c3Params,self.c3.chart=billboard.bb.generate(this.byPassSetting(c3Params))}))})))},ontoggle:function(key,hiddenLegends){if(this.c3.params.ontoggle&&"function"===typeof this.c3.params.ontoggle){var callback=this.c3.params.ontoggle.bind(this.c3.chart.internal,key);callback()}if("bar"===this.chartContext.options.type&&this.chartContext.options.bar.showGroupTotal){var groupTotalData=c3_helper/* default */.Z.getGroupTotalDataPoint(this.c3.params,hiddenLegends);groupTotalData&&this.c3.chart.load({json:{group_total:groupTotalData}})}},byPassSetting:function(params){if(params&&params.data&&params.data.hasOwnProperty("labels")&&!0===params.data.labels){var formattedLabel={centered:!1,format:function(v){return Number.isInteger(v)?d3.format(",")(v):v?(0,formatter/* formatCurrency */.xG)(v):null}};this.$set(params.data,"labels",formattedLabel)}return params},getOptionsFromTypeChart:function(options,ctype){
//to set the chart type based on the widget
this.config&&this.config.widget&&this.config.widget.dataOptions&&(this.config.widget.dataOptions.chartType=ctype.chartTypeInt,this.config.widget.dataOptions.chartTypeInt=ctype.chartTypeInt)},isXValueMode:function(options,data){if(("pie"===options.type||"donut"===options.type)&&data&&data.x&&2===Object.keys(data).length){if(options.xColorMap)return!0;for(var colors=color_helper/* default */.Z.newColorPicker(data.x.length),xColorMap={},i=0;i<data.x.length;i++){var xVal=data.x[i];xColorMap[xVal]=options.xColorMap&&options.xColorMap[xVal]?options.xColorMap[xVal]:colors[i]}return options.xColorMap=xColorMap,!0}return options.xColorMap=null,!1},reRender:function(){var _this2=this;this.updateTimeout&&clearTimeout(this.updateTimeout),this.updateTimeout=setTimeout((function(){return _this2.render()}),500)},resize:function(){this.reRender(),this.$refs["newWidget"]&&this.$refs["newWidget"].resize()},update:function(){this.reRender()}}},components_FNewChartvue_type_script_lang_js=FNewChartvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(components_FNewChartvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,FNewChart=component.exports},
/***/374520:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// EXPORTS
__webpack_require__.d(__webpack_exports__,{Z:function(){/* binding */return DrilldownBreadcrumb}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/pages/report/components/DrilldownBreadcrumb.vue?vue&type=template&id=77c09de1
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"drilldown-breadcrumb  p5 mL90 d-flex"},[_c("div",{staticClass:"crumb-item"},[_c("span",{staticClass:"f12 bold breadcrumb-text mR5 letter-spacing0_5 ",on:{click:function($event){return _vm.handleClick(-1)}}},[_vm._v(" "+_vm._s(_vm.$t("reportdrilldown.all"))+" ")]),_c("i",{staticClass:"el-icon-arrow-right f12 mR3"})]),_vm._l(_vm.drilldownParams.drilldownCriteria,(function(drilldownCriteria,stepIndex){return _c("div",{key:stepIndex,staticClass:"crumb-item",class:[{"is-last":stepIndex==_vm.drilldownParams.drilldownCriteria.length-1}]},[_c("span",{key:"crumb-text"+stepIndex,staticClass:"f12 bold breadcrumb-text mR5 letter-spacing0_5 ",class:[{"is-last":stepIndex==_vm.drilldownParams.drilldownCriteria.length-1}],on:{click:function($event){return _vm.handleClick(stepIndex)}}},[_vm._v(" "+_vm._s(drilldownCriteria.breadcrumbLabel))]),stepIndex!=_vm.drilldownParams.drilldownCriteria.length-1?_c("i",{key:"crumb-icon"+stepIndex,staticClass:"el-icon-arrow-right f12 mR3"}):_c("i",{key:"crumb-close"+stepIndex,staticClass:"el-icon-circle-close pointer f12 hide-v",on:{click:function($event){return _vm.handleClick(stepIndex-1)}}})])}))],2)},staticRenderFns=[],DrilldownBreadcrumbvue_type_script_lang_js={props:{drilldownParams:{type:Object,default:null}},data:function(){return{}},methods:{handleClick:function(crumbIndex){
//ignore click on last item
crumbIndex!=this.drilldownParams.length-1&&this.$emit("crumbClick",crumbIndex)}}},components_DrilldownBreadcrumbvue_type_script_lang_js=DrilldownBreadcrumbvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(components_DrilldownBreadcrumbvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,DrilldownBreadcrumb=component.exports}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/45817.js.map