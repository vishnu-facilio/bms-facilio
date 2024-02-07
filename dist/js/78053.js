"use strict";(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[78053],{
/***/647690:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// EXPORTS
__webpack_require__.d(__webpack_exports__,{Z:function(){/* binding */return AlarmBar}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/components/AlarmBar.vue?vue&type=template&id=79c81abb
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",[_c("div",{staticClass:"alarm-bar-cell position-relative"},[_vm.chartDataLoadStatus?_c("div",{staticClass:"text-fc-grey pL0 width100"},[_c("div",{staticClass:"mL30 mR30 animated-background booleancard-chart widget-alarmbar",staticStyle:{height:"26px"}})]):_vm.chartData&&_vm.chartData.data?_c("f-multi-chart",{key:"alarmbar"+_vm.parentId,ref:"barRefrs",staticClass:"booleancard-chart widget-alarmbar",attrs:{data:_vm.chartData.data,options:_vm.chartOptions,alarms:_vm.chartData.alarms,booleanAlarmsData:_vm.chartData.alarms,booleanData:_vm.chartData.booleanData,dateRange:_vm.chartData.dateRange,hidecharttypechanger:!0}}):_vm._e(),_vm.isNoDropButtom?_c("div",{staticClass:"empty-div-cell"}):_vm._e()],1)])},staticRenderFns=[],defineProperty=__webpack_require__(482482),NewDataFormatHelper=__webpack_require__(804911),NewDateHelper=__webpack_require__(105534),FMultiChart=__webpack_require__(649206),AlarmBarvue_type_script_lang_js={props:["parentId","sourceKey","dateOperator","dateValue","isResize","isRca","resourceId","parentAlarmId","rca","isOpen","isNoDropButtom"],mixins:[NewDataFormatHelper/* default */.Z,NewDateHelper/* default */.Z],components:{FMultiChart:FMultiChart/* default */.Z},data:function(){return{localDateFormat:[22,25,31,30,28,27,44,45],configData:{height:40,width:100,dateFilter:NewDateHelper/* default */.Z.getDatePickerObject(22)},dateObj:NewDateHelper/* default */.Z.getDatePickerObject(28,null),chartDataLoadStatus:!1,chartData:null,chartOptions:{common:{mode:1,buildingIds:[],type:1},hideAlarmTitle:!0,general:{grid:{y:!1,x:!1},point:{show:!1},labels:!1,normalizeStack:!1,dataOrder:null,hideZeroes:!1},settings:{chartMode:"multi",alarm:!0,chart:!0,table:!1,safelimit:!1,enableAlarm:!0,autoGroup:!1,booleanAlarms:!0},tooltip:{grouped:!1,sortOrder:"none",showNullValues:!1},donut:{labelType:"percentage",centerText:{primaryText:null,secondaryText:null}},area:{above:!1,linearGradient:!0},axis:{rotated:!1,showy2axis:!0,x:{show:!1,label:{text:"Timestamp",position:"outer-center"},tick:{direction:"auto"},range:{min:null,max:null},datatype:"date_time",time:{period:null,format:{period:"minute",interval:"minutes",format:"MM-DD-YYYY HH:mm",d3Format:"%m-%d-%Y %H:%M",tooltip:"LLL"}}},y:{show:!1,label:{text:"PRE FILTER ALARM",position:"outer-middle",type:"auto"},unit:null,scale:"linear",range:{min:null,max:null},ticks:{count:5},format:{decimals:0},padding:{bottom:0},datatype:null},y2:{show:!1,label:{text:null,position:"outer-middle"},unit:null,scale:"linear",range:{min:null,max:null},ticks:{count:5},format:{decimals:0},padding:{bottom:0}}},legend:{show:!0,position:"top",width:180},widgetLegend:{show:!1},colorPalette:"auto",style:{pie:{label:{show:!0}},donut:{width:null,label:{show:!0}},gauge:{width:null,label:{show:!0},min:0,max:100,unit:" %"},line:{point:{show:!0,radius:5},lineMode:"default",stepType:"step",stroke:{width:1,opacity:1,dashed:{length:2,space:2}},connectNull:!1},area:{point:{show:!0,radius:5},lineMode:"default",stepType:"step",fillOpacity:null,stroke:{width:1,opacity:1,dashed:{length:2,space:2}},connectNull:!1},bar:{width:null},scatter:{point:{radius:5}}},type:"area",multichart:{},isSystemGroup:!1,dataPoints:[{label:"Status",children:[],type:"group",chartType:"",groupKey:1,unit:null,dataType:"BOOLEAN"}],safeLimit:[]}}},watch:{parentId:function(newVal){this.initializeCharts()},dateOperator:function(newVal){this.initializeCharts()},dateValue:function(newVal){this.initializeCharts()},isResize:function(newVal){
// if (newVal) {
// this.$refs
this.$refs["barRefrs"]&&this.$refs["barRefrs"].resize();
// }
}},mounted:function(){this.initializeCharts()},methods:{prepareGraph:function(result){this.chartDataLoadStatus=!1;var reportObj={data:{}};return reportObj.alarms=this.prepareBooleanReport(result),reportObj.alarms.barSize="small",this.configData.dateFilter=NewDateHelper/* default */.Z.getDatePickerObject(this.dateOperator,this.dateValue),reportObj.dateRange=NewDateHelper/* default */.Z.getDatePickerObject(this.dateOperator,this.dateValue),reportObj.xAggr=0,reportObj.report={xAggr:0},reportObj.dateRange=this.prepareHighResDateRange(reportObj),reportObj.data.x=reportObj.dateRange.range.domain,this.chartOptions.axis.x.range=reportObj.dateRange.range,reportObj},getChartData:function(){var _paramsJson,_this=this;this.chartDataLoadStatus=!0;var params={paramsJson:(_paramsJson={isRca:this.isRca},(0,defineProperty/* default */.Z)(_paramsJson,this.sourceKey,this.parentId),(0,defineProperty/* default */.Z)(_paramsJson,"dateOperator",this.dateOperator),(0,defineProperty/* default */.Z)(_paramsJson,"dateValue",this.dateValue),_paramsJson),staticKey:"resourceAlarmBar"};this.rca&&(params.paramsJson["isRca"]=!0),this.resourceId&&(params.paramsJson["parentId"]=this.resourceId),this.parentAlarmId&&(params.paramsJson["parentAlarmId"]=this.parentAlarmId),this.$http.post("dashboard/getCardData",params).then((function(response){_this.chartData=_this.chartData||{},_this.chartData=_this.prepareGraph(response.data.cardResult),_this.$nextTick((function(){_this.$refs["barRefrs"]&&_this.$refs["barRefrs"].resize()}))}))},initializeCharts:function(){this.getChartData()}}},components_AlarmBarvue_type_script_lang_js=AlarmBarvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(components_AlarmBarvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,AlarmBar=component.exports},
/***/604947:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// EXPORTS
__webpack_require__.d(__webpack_exports__,{Z:function(){/* binding */return Spinner}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/components/Spinner.vue?vue&type=template&id=06a81286&scoped=true
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("transition",[_c("svg",{directives:[{name:"show",rawName:"v-show",value:_vm.show,expression:"show"}],staticClass:"spinner",class:{show:_vm.show},attrs:{width:_vm.spinnerSize,height:_vm.spinnerSize,viewBox:"0 0 44 44"}},[_c("circle",{staticClass:"path",style:{stroke:_vm.strokeColor},attrs:{fill:"none","stroke-width":"4","stroke-linecap":"round",cx:"22",cy:"22",r:"20"}})])])},staticRenderFns=[],Spinnervue_type_script_lang_js={name:"spinner",props:["show","size","colour"],computed:{spinnerSize:function(){return this.size?this.size+"px":"50px"},strokeColor:function(){return this.colour?this.colour:"#fd4b92"}}},components_Spinnervue_type_script_lang_js=Spinnervue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(components_Spinnervue_type_script_lang_js,render,staticRenderFns,!1,null,"06a81286",null)
/* harmony default export */,Spinner=component.exports},
/***/806339:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// EXPORTS
__webpack_require__.d(__webpack_exports__,{Z:function(){/* binding */return WidgetPagination}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/components/page/widget/utils/WidgetPagination.vue?vue&type=template&id=6b10cd5b
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"fc-widget-pagination"},[_vm.total>0?_c("div",{class:{"fc-black-small-txt-12":!_vm.hideToggle}},[_vm.from!==_vm.to?_c("span",[_vm._v(_vm._s(_vm.from)+" -")]):_vm._e(),_c("span",[_vm._v(_vm._s(_vm.to))]),_vm.hideToggle?_vm._e():[_vm._v(" of "+_vm._s(_vm.total)+" "),_c("span",{staticClass:"el-icon-arrow-left fc-black-small-txt-12 fw-bold f16 pointer mL10 mR5",class:{disable:_vm.from<=1},on:{click:function($event){_vm.from>1&&_vm.prev()}}}),_c("span",{staticClass:"el-icon-arrow-right fc-black-small-txt-12 f16 pointer mR10 fw-bold",class:{disable:_vm.to===_vm.total},on:{click:function($event){_vm.to!==_vm.total&&_vm.next()}}})]],2):_vm._e()])},staticRenderFns=[],WidgetPaginationvue_type_script_lang_js={props:["currentPage","perPage","total","hideToggle"],data:function(){return{from:0,to:0,page:1}},mounted:function(){this.init()},watch:{total:function(){this.init()},currentPage:function(val){val!==this.page&&this.init()}},methods:{init:function(){this.page=this.currentPage||1,this.from=(this.page-1)*this.perPage+1;var to=this.from+this.perPage-1;this.to=this.total>to?to:this.total},next:function(){this.from=this.to+1,this.to+=this.perPage,this.to>this.total&&(this.to=this.total),this.page++,this.updateCurrentPage()},prev:function(){this.to=this.from-1,this.from-=this.perPage,this.from<=1?this.from=this.page=1:this.page--,this.updateCurrentPage()},updateCurrentPage:function(){
// Update currentPage value
this.$emit("update:currentPage",this.page),this.$emit("onChange")}}},utils_WidgetPaginationvue_type_script_lang_js=WidgetPaginationvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(utils_WidgetPaginationvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,WidgetPagination=component.exports},
/***/649206:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// EXPORTS
__webpack_require__.d(__webpack_exports__,{Z:function(){/* binding */return FMultiChart}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/newcharts/components/FMultiChart.vue?vue&type=template&id=79e8165a
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return"multi"===_vm.options.settings.chartMode||_vm.ismulti?_c("div",{staticClass:"height100"},[_vm.c3ParamsList&&!1!==_vm.options.settings.chart?_c("div",{staticClass:"f-multichart"},[_vm.showAlarms&&_vm.showAlarmTitle?_c("div",{staticClass:"fc-alarms-chart-title",style:"sensorrollupalarm"===_vm.moduleName?"color: #324056 !important;font-weight: bold;":""},[_vm._v(" "+_vm._s(_vm.$validation.isEmpty(_vm.alarms.barTitle)?"Alarms":_vm.alarms.barTitle)+" ")]):_vm._e(),_c("div",{directives:[{name:"show",rawName:"v-show",value:_vm.showAlarms,expression:"showAlarms"}],ref:"alarmsChartEle",staticClass:"fc-alarms-chart pdf-chart",on:{click:_vm.alarmDrilldown}}),_vm.relatedAlarms?_c("div",[_vm._l(_vm.relatedAlarms,(function(relAlarm,idx){return[_c("div",{key:idx,style:_vm.isWidget?"position: relative; top: 15px;":""},[_c("div",{staticClass:"fc-alarms-chart-title",staticStyle:{"text-transform":"capitalize",color:"#324056 !important"}},[_vm._v(" "+_vm._s(relAlarm?relAlarm.alarmTitle:"")+" ")]),_c("div",{ref:"relatedAlarmsChartEle",refInFor:!0,staticClass:"fc-alarms-chart pdf-chart"})])]}))],2):_vm._e(),_vm._l(_vm.c3ParamsList,(function(c3Param,index){return _c("div",{key:index,class:["f-multichart-print"]},[_vm.hidecharttypechanger||c3Param.data.booleanChart||c3Param.data.enumChart?_vm._e():_c("f-chart-type",{staticClass:"pT10",attrs:{options:_vm.options,multichartkey:c3Param.key}}),_vm.chartContext?_c("div",{staticStyle:{"text-align":"center"}},[_vm.chartList&&_vm.chartList[index]&&_vm.chartList[index].chart&&!_vm.options.legend.show?_c("div",{staticClass:"boolean-chart-title",style:{color:"#000"}},[_vm._v(" "+_vm._s(c3Param.label)+" ")]):_vm._e(),_vm.options.legend.show?_c("f-chart-legends",{ref:"chartLegend",refInFor:!0,class:{"boolean-chart":c3Param.data.booleanChart||c3Param.data.enumChart},attrs:{chart:_vm.chartList&&_vm.chartList[index]&&_vm.chartList[index].chart?_vm.chartList[index].chart:null,options:_vm.options,multichart:c3Param.key,config:_vm.config},on:{removeFilter:_vm.removeFilter}}):_vm._e(),_c("div",{staticClass:"fc-newchart-container"},[_c("div",{ref:"newChartEle",refInFor:!0,staticClass:"fc-new-chart",class:{"fc-boolean-chart":c3Param.data.booleanChart||c3Param.data.enumChart,"hide-y2-axis":_vm.options&&_vm.options.multichart&&_vm.options.multichart[c3Param.key]&&_vm.options.multichart[c3Param.key].axis&&!1===_vm.options.multichart[c3Param.key].axis.showy2axis}})])],1):_vm._e()],1)})),_c("f-widget-legends",{ref:"newWidget",staticClass:"widget-legends",attrs:{reportVarianceData:_vm.resultObj?_vm.resultObj.reportData.aggr:null,data:_vm.data,options:_vm.options}})],2):_vm._e()]):_vm._e()},staticRenderFns=[],createForOfIteratorHelper=__webpack_require__(566347),billboard=(__webpack_require__(260228),__webpack_require__(212826),__webpack_require__(241830),__webpack_require__(670560),__webpack_require__(538077),__webpack_require__(872695)),basechart=__webpack_require__(503162),c3_helper=__webpack_require__(167021),FChartLegends=__webpack_require__(906575),FWidgetLegends=__webpack_require__(547301),JumpToHelper=__webpack_require__(921466),FChartType=__webpack_require__(294309),tooltip=__webpack_require__(460895),validation=__webpack_require__(990260),FMultiChartvue_type_script_lang_js={mixins:[basechart/* default */.Z,JumpToHelper/* default */.Z],props:["resultObj","isWidget","booleanData","booleanAlarmsData","config","ismulti","relatedAlarms","alarms","moduleName"],components:{FChartLegends:FChartLegends/* default */.Z,FWidgetLegends:FWidgetLegends/* default */.Z,FChartType:FChartType/* default */.Z},data:function(){return{chartContext:null,c3:{params:null,chart:null},c3ParamsList:null,chartList:[],showAlarms:!1,showBooleanAlarms:!1,updateTimeout:null}},mounted:function(){this.render()},computed:{showAlarmTitle:function(){return!this.chartContext||!this.chartContext.options||!this.chartContext.options.hideAlarmTitle}},beforeDestroy:function(){if(tooltip/* default */.Z.hideTooltip(),this.updateTimeout&&clearTimeout(this.updateTimeout),this.chartList){var _step,_iterator=(0,createForOfIteratorHelper/* default */.Z)(this.chartList);try{for(_iterator.s();!(_step=_iterator.n()).done;){var chart=_step.value;chart.chart&&chart.chart.destroy()}}catch(err){_iterator.e(err)}finally{_iterator.f()}}this.chartContext=null,this.c3ParamsList=null,this.chartList=null},methods:{render:function(){var _this=this;if(this.data){var chartContext={};chartContext.multichartUniqueKey=Math.random().toString(36).substr(2,9),chartContext.options=this.getOptions(),chartContext.dateRange=this.dateRange,chartContext.data=Object.freeze(this.data),this.resultObj&&this.resultObj.Readingdp&&(chartContext.Readingdp=this.resultObj.Readingdp),this.resultObj&&(this.resultObj.scatterConfig&&(chartContext.scatterConfig=this.resultObj.scatterConfig,chartContext.xMap=this.resultObj.xMap),this.resultObj.groupByMetrics&&(chartContext.groupByMetrics=!0)),chartContext.openAlarm=function(alarmList){if(alarmList){var _step2,alarmId=[],_iterator2=(0,createForOfIteratorHelper/* default */.Z)(alarmList);try{for(_iterator2.s();!(_step2=_iterator2.n()).done;){var a=_step2.value;alarmId.push(a.id)}}catch(err){_iterator2.e(err)}finally{_iterator2.f()}_this.$mobile||_this.jumpToAlarmsNew(alarmId)}},chartContext.booleanData=Object.freeze(this.booleanData),chartContext.unitMap=this.getUnitMap(this.options.dataPoints),chartContext.enumMap=this.getEnumMap(this.options.dataPoints),this.showAlarms=1===chartContext.options.common.mode&&chartContext.options.settings.alarm,this.showAlarms&&(chartContext.alarms=this.alarms,chartContext.options.size.height=chartContext.options.size.height-100),this.showBooleanAlarms=this.booleanAlarmsData&&this.booleanAlarmsData.regions&&this.booleanAlarmsData.regions.length&&1===chartContext.options.common.mode&&chartContext.options.settings.booleanAlarms,this.showBooleanAlarms&&(chartContext.booleanAlarmsData=this.booleanAlarmsData,chartContext.options.size.height=chartContext.options.size.height-100),this.isWidget&&(chartContext.options.legend.show?chartContext.options.size.height=chartContext.options.size.height-50:chartContext.options.size.height=chartContext.options.size.height-20,this.options.widgetLegend.show&&(chartContext.options.size.height=chartContext.options.size.height-100)),this.c3ParamsList=c3_helper/* default */.Z.prepareMultichart(chartContext),this.resultObj&&(this.resultObj.Readingdp||this.resultObj.scatterConfig)&&(this.c3ParamsList=this.c3ParamsList.filter((function(params){return!(0,validation/* isEmpty */.xb)(params.data.axes)}))),this.chartList=[],this.chartContext=chartContext;var self=this;this.$nextTick((function(){if(self.showAlarms){var alarmsParams=this.showBooleanAlarms?c3_helper/* default */.Z.prepareBooleanAlarmChart(chartContext,self.c3ParamsList[0].data.json.x,this.alarms):c3_helper/* default */.Z.prepareBooleanChart(chartContext,self.c3ParamsList[0].data.json.x,this.alarms);if(self.$refs["alarmsChartEle"]&&(alarmsParams.bindto=self.$refs["alarmsChartEle"],billboard.bb.generate(Object.freeze(alarmsParams))),self.relatedAlarms&&self.relatedAlarms.length)for(var idx in self.relatedAlarms)if(self.$refs["relatedAlarmsChartEle"]&&self.$refs["relatedAlarmsChartEle"][idx]){var relalarmsParams=c3_helper/* default */.Z.prepareBooleanChart(this.chartContext,self.c3ParamsList[0].data.json.x,self.relatedAlarms[idx]);relalarmsParams.bindto=self.$refs["relatedAlarmsChartEle"][idx],billboard.bb.generate(relalarmsParams)}}self.$nextTick((function(){for(var j=0,i=0;i<self.c3ParamsList.length;i++){var c3Param=self.c3ParamsList[i];self.$refs["newChartEle"]&&self.$refs["newChartEle"][i]&&(c3Param.bindto=self.$refs["newChartEle"][i],!c3Param.data.booleanChart&&!c3Param.data.enumChart&&self.$refs["chartLegend"]&&self.$refs["chartLegend"][j]&&(c3Param.size.height=c3Param.size.height-self.$refs["chartLegend"][j++].$el.clientHeight,self.isWidget&&self.options&&self.options.widgetLegend.show&&(c3Param.size.height=c3Param.size.height+20)),self.chartList.push({params:c3Param,chart:billboard.bb.generate(Object.freeze(c3Param))}))}}))}))}},getIndividualChartHeight:function(){var height=100;if(this.size.height){var calcHeight=this.size.height/this.chartList.length;calcHeight>=100&&(height=calcHeight)}return height},reRender:function(){var _this2=this;this.updateTimeout&&clearTimeout(this.updateTimeout),this.updateTimeout=setTimeout((function(){return _this2.render()}),1e3)},resize:function(){this.reRender(),this.$refs["newWidget"]&&this.$refs["newWidget"].resize()},update:function(){this.reRender()},alarmDrilldown:function(){this.$emit("alarmDrilldown")},removeFilter:function(parentKey){this.$emit("removeFilters",parentKey)}}},components_FMultiChartvue_type_script_lang_js=FMultiChartvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(components_FMultiChartvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,FMultiChart=component.exports},
/***/802302:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return ReadingRuleRcaReadings}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/pages/alarm/rule-creation/component/ReadingRuleRcaReadings.vue?vue&type=template&id=088685b1&scoped=true
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"d-flex flex-direction-column height-100"},[_c("div",{staticClass:"top-bar height50"},[_c("div",{staticClass:"f15 bold pL15"},[_vm._v(" "+_vm._s(_vm.$t("rule.create.root_cause"))+" ")]),_c("div",{staticClass:"flex-middle"},[_c("Pagination",{attrs:{currentPage:_vm.page,total:_vm.recordCount,perPage:_vm.perPage},on:{"update:currentPage":[function($event){_vm.page=$event},_vm.loadNextPage],"update:current-page":function($event){_vm.page=$event}}}),_c("NewDatePicker",{staticClass:"filter-field date-filter-comp",staticStyle:{"margin-left":"auto"},attrs:{zone:_vm.$timezone,dateObj:_vm.dateObj},on:{"update:dateObj":function($event){_vm.dateObj=$event},"update:date-obj":function($event){_vm.dateObj=$event},date:_vm.changeDateFilter}})],1)]),_vm.isLoading?_c("div",{staticClass:"white-background  width-100 flex-middle justify-content-center flex-direction-column flex-grow"},[_c("Spinner",{attrs:{show:_vm.isLoading,size:"80"}})],1):_vm.$validation.isEmpty(_vm.records)?_c("div",{staticClass:"white-background width-100 flex-middle justify-content-center flex-direction-column flex-grow empty-logo"},[_c("inline-svg",{attrs:{src:"svgs/list-empty",iconClass:"icon text-center icon-xxxxlg"}}),_c("div",{staticClass:"q-item-label nowo-label"},[_vm._v(" "+_vm._s(_vm.$t("common._common.nodata"))+" ")])],1):_c("el-table",{staticClass:"rule-rca-score-readings-table",attrs:{data:_vm.records,"header-cell-style":{background:"#f3f1fc",padding:"15px","font-weight":500}}},[_c("el-table-column",{attrs:{label:"FAULT"},scopedSlots:_vm._u([{key:"default",fn:function(record){return[_c("div",{on:{click:function($event){return _vm.redirectToAlarmOverview(record)}}},[_vm._v(" "+_vm._s(_vm.getRcaFaultName(record))+" ")])]}}])}),_c("el-table-column",{attrs:{label:"ASSET"},scopedSlots:_vm._u([{key:"default",fn:function(record){return[_vm._v(" "+_vm._s(_vm.getAssetCatName(record))+" ")]}}])}),_c("el-table-column",{attrs:{label:"OCCURRENCES"},scopedSlots:_vm._u([{key:"default",fn:function(record){return[_vm._v(" "+_vm._s(_vm.getCount(record))+" ")]}}])}),_c("el-table-column",{attrs:{label:"DURATION"},scopedSlots:_vm._u([{key:"default",fn:function(record){return[_vm._v(" "+_vm._s(_vm.getDuration(record))+" ")]}}])}),_c("el-table-column",{attrs:{label:"SCORE"},scopedSlots:_vm._u([{key:"default",fn:function(record){return[_vm._v(" "+_vm._s(_vm.getRank(record))+" ")]}}])}),_c("el-table-column",{attrs:{type:"expand"},scopedSlots:_vm._u([{key:"default",fn:function(record){return[_c("div",{staticClass:"pT16"},[_c("AlarmBar",{staticClass:"fc-v1-alarm-cell",attrs:{parentId:_vm.getRcaFaultId(record),sourceKey:"alarmId",dateOperator:_vm.dateOperator,dateValue:_vm.dateValue}})],1)]}}])})],1)],1)},staticRenderFns=[],regeneratorRuntime=__webpack_require__(150124),asyncToGenerator=__webpack_require__(548534),AlarmBar=(__webpack_require__(648324),__webpack_require__(506203),__webpack_require__(976801),__webpack_require__(843843),__webpack_require__(434284),__webpack_require__(670560),__webpack_require__(647690)),moment_timezone=__webpack_require__(480008),moment_timezone_default=__webpack_require__.n(moment_timezone),validation=__webpack_require__(990260),CustomModuleData=__webpack_require__(689565),WidgetPagination=__webpack_require__(806339),api=__webpack_require__(32284),Spinner=__webpack_require__(604947),NewDateHelper=__webpack_require__(105534),NewDatePicker=__webpack_require__(900773),router=__webpack_require__(329435),ReadingRuleRcaReadingsvue_type_script_lang_js={components:{Pagination:WidgetPagination/* default */.Z,
// AdvancedSearch,
// FTags,
Spinner:Spinner/* default */.Z,AlarmBar:AlarmBar/* default */.Z,NewDatePicker:NewDatePicker/* default */.Z},props:["details","widget","tab"],data:function(){return{isLoading:!1,recordCount:null,page:1,perPage:50,records:[],filters:{},rcaScoreModuleName:"readingrulerca_score_readings",rcaScoreModuleDisplayName:"Reading Rule RCA Readings",localDateFormat:[22,25,31,30,28,27,44,45],configData:{height:40,width:100,dateFilter:NewDateHelper/* default */.Z.getDatePickerObject(20)},dateObj:null,dateValue:null,dateOperator:20}},computed:{modelDataClass:function(){return CustomModuleData/* CustomModuleData */.u},currentAlarmId:function(){var _this$details=this.details,_this$details2=void 0===_this$details?{}:_this$details,id=_this$details2.id;return id}},watch:{details:{handler:function(newVal){var _ref=newVal||{},alarm=_ref.alarm,_ref2=alarm||{},lastOccurredTime=_ref2.lastOccurredTime;(0,validation/* isEmpty */.xb)(lastOccurredTime)||this.init()},deep:!0,immediate:!0}},methods:{init:function(){var _this=this;return(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee(){return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context){while(1)switch(_context.prev=_context.next){case 0:return _context.next=2,_this.initDateObj();case 2:return _context.next=4,_this.loadRecords();case 4:case"end":return _context.stop()}}),_callee)})))()},initDateObj:function(){var _this2=this;return(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee2(){var _this2$details,_this2$details2,alarm,_ref3,lastOccurredTime,endTime;return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context2){while(1)switch(_context2.prev=_context2.next){case 0:_this2$details=_this2.details,_this2$details2=void 0===_this2$details?{}:_this2$details,alarm=_this2$details2.alarm,_ref3=alarm||{},lastOccurredTime=_ref3.lastOccurredTime,(0,validation/* isEmpty */.xb)(lastOccurredTime)||(endTime=moment_timezone_default()(lastOccurredTime).endOf("day").valueOf(),_this2.dateValue=[endTime-5184e5,endTime],_this2.dateObj=NewDateHelper/* default */.Z.getDatePickerObject(20,[endTime-5184e5,endTime]));case 3:case"end":return _context2.stop()}}),_callee2)})))()},loadRecords:function(){var _this3=this;return(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee3(){var page,currentAlarmId,dateObj,dateOperator,perPage,filters,params,_ref4,operatorId,value,_operatorId,_yield$API$get,data,error,_ref5,result,_ref6,records,recordCount;return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context3){while(1)switch(_context3.prev=_context3.next){case 0:if(_context3.prev=0,page=_this3.page,currentAlarmId=_this3.currentAlarmId,dateObj=_this3.dateObj,dateOperator=_this3.dateOperator,perPage=_this3.perPage,filters=_this3.filters,(0,validation/* isEmpty */.xb)(currentAlarmId)){_context3.next=13;break}return params={page:page,perPage:perPage,alarmId:currentAlarmId,filters:JSON.stringify(filters)},_ref4=dateObj||{},operatorId=_ref4.operatorId,value=_ref4.value,(0,validation/* isEmpty */.xb)(dateObj)||(0,validation/* isEmpty */.xb)(operatorId)||(_operatorId=dateOperator,_this3.dateValue=value.join(),NewDateHelper/* default */.Z.isValueRequired(_operatorId)?(params.dateOperator=_operatorId,params.dateOperatorValue=_this3.dateValue):params.dateOperator=_operatorId),_this3.isLoading=!0,_context3.next=9,api/* API */.bl.get("/v3/readingrule/fetchRcaReadings",params);case 9:_yield$API$get=_context3.sent,data=_yield$API$get.data,error=_yield$API$get.error,(0,validation/* isEmpty */.xb)(error)?(_ref5=data||{},result=_ref5.result,_ref6=result||{},records=_ref6.records,recordCount=_ref6.recordCount,_this3.records=records,_this3.recordCount=recordCount):_this3.$message.error(error.message);case 13:_context3.next=18;break;case 15:_context3.prev=15,_context3.t0=_context3["catch"](0),_this3.$message.error(_context3.t0);case 18:_this3.isLoading=!1;case 19:case"end":return _context3.stop()}}),_callee3,null,[[0,15]])})))()},applyFiltersFromTags:function(_ref7){var filters=_ref7.filters;this.filters=filters,this.resetPage(),this.loadRecords()},setAppliedfilter:function(_ref8){var filters=_ref8.filters;this.filters=filters||{},this.loadRecords()},resetFilters:function(){this.filters={},this.resetPage(),this.loadRecords()},resetPage:function(){this.page=1},getAssetCatName:function(record){var _ref9=record||{},_ref9$row=_ref9.row,_ref9$row2=void 0===_ref9$row?{}:_ref9$row,_ref9$row2$rcaFault=_ref9$row2.rcaFault,_ref9$row2$rcaFault2=void 0===_ref9$row2$rcaFault?{}:_ref9$row2$rcaFault,readingAlarmAssetCategory=_ref9$row2$rcaFault2.readingAlarmAssetCategory;return this.$getProperty(readingAlarmAssetCategory,"displayName","---")},getRcaFaultId:function(record){var _ref10=record||{},_ref10$row=_ref10.row,_ref10$row2=void 0===_ref10$row?{}:_ref10$row,rcaFault=_ref10$row2.rcaFault;return this.$getProperty(rcaFault,"id","---")},getRcaFaultName:function(record){var _ref11=record||{},_ref11$row=_ref11.row,_ref11$row2=void 0===_ref11$row?{}:_ref11$row,rcaFault=_ref11$row2.rcaFault;return this.$getProperty(rcaFault,"subject","---")},getDuration:function(record){var _ref12=record||{},_ref12$row=_ref12.row,_ref12$row2=void 0===_ref12$row?{}:_ref12$row,duration=_ref12$row2.duration;return(0,validation/* isEmpty */.xb)(duration)?"---":this.$helpers.getFormattedDuration(duration)},getCount:function(record){var _ref13=record||{},row=_ref13.row;return this.$getProperty(row,"count","---")},getRank:function(record){var _ref14=record||{},row=_ref14.row;return this.$getProperty(row,"score","---")},loadNextPage:function(page){var _this4=this;return(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee4(){return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context4){while(1)switch(_context4.prev=_context4.next){case 0:return _this4.page=page,_context4.next=3,_this4.loadRecords();case 3:case"end":return _context4.stop()}}),_callee4)})))()},changeDateFilter:function(dateFilter){this.dateObj=dateFilter,
// TODO...use alarmbarmixin for booleancard and alarminsight
this.localDateFormat.includes(dateFilter.operatorId)?(this.dateOperator=dateFilter.operatorId,this.dateValue=null):(this.dateOperator=20,this.dateValue=dateFilter.value.join()),this.loadRecords()},redirectToAlarmOverview:function(record){var _ref15=record||{},row=_ref15.row,_ref16=row||{},rcaFault=_ref16.rcaFault,_ref17=rcaFault||{},id=_ref17.id;if(id)if((0,router/* isWebTabsEnabled */.tj)()){var _ref18=(0,router/* findRouteForModule */.Jp)("newreadingalarm",router/* pageTypes */.As.OVERVIEW)||{},name=_ref18.name;name&&this.$router.push({name:name,params:{id:id,viewname:"all"}})}else this.$router.push({name:"newreadingalarm-summary",params:{id:id,viewname:"all"}})}}},component_ReadingRuleRcaReadingsvue_type_script_lang_js=ReadingRuleRcaReadingsvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(component_ReadingRuleRcaReadingsvue_type_script_lang_js,render,staticRenderFns,!1,null,"088685b1",null)
/* harmony default export */,ReadingRuleRcaReadings=component.exports}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/78053.js.map