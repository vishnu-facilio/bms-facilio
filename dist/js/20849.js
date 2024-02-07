"use strict";(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[20849],{
/***/381056:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// EXPORTS
__webpack_require__.d(__webpack_exports__,{Z:function(){/* binding */return AnomalyMixin}});
// EXTERNAL MODULE: ./node_modules/core-js/modules/es.object.to-string.js
__webpack_require__(260228),__webpack_require__(670560);
// EXTERNAL MODULE: ./node_modules/core-js/modules/es.array.push.js
var render,staticRenderFns,moment_timezone=__webpack_require__(480008),moment_timezone_default=__webpack_require__.n(moment_timezone),NewDateHelper=__webpack_require__(105534),AnomalyMixinvue_type_script_lang_js={computed:{lastMonth:function(){return moment_timezone_default()().tz(this.$timezone).subtract(1,"months").format("MMMM")},currentMonth:function(){return moment_timezone_default()().tz(this.$timezone).startOf("month").format("MMMM")},isMetricsApiLoading:function(){return this.$store.state.anomalies.isLoading},currMntNLastMntRanges:function(){var thisMonthdateRange=NewDateHelper/* default */.Z.getDatePickerObject(28,this.$timezone),lastMonthDateRange=NewDateHelper/* default */.Z.getDatePickerObject(27,this.$timezone),dateRanges={};return dateRanges.startTime=lastMonthDateRange.value[0],dateRanges.endTime=thisMonthdateRange.value[1],dateRanges}},methods:{loadEnrgyByCdd:function(resourcesIds){return this.$util.getWorkFlowResult(49,[resourcesIds,this.details.alarm.resource.siteId])},loadEnergyWastage:function(resourceIds){return this.$util.getWorkFlowResult(99,[resourceIds,this.details.alarm.id])},loadAnomaliesCount:function(resourceIds,isRCA){return this.$util.getWorkFlowResult(100,[resourceIds,this.details.alarm.id,isRCA||!1])},loadDeviation:function(resourceIds){return this.$util.getWorkFlowResult(46,[resourceIds,this.details.alarm.id])},loadMetrics:function(resourceId,alarmId,siteId,dateRange,rca){var _this=this,url="/v2/mlAnomalyAlarm/metrics",paramJson={};return paramJson.alarmId=alarmId,paramJson.resourceId=resourceId,paramJson.siteId=siteId,null!=dateRange&&(paramJson.dateRange=dateRange),rca&&(paramJson.rca=rca),this.loading=!0,new Promise((function(resolve,reject){_this.$http.post(url,paramJson).then((function(response){resolve(response.data.result.metrics)})).catch((function(error){reject(error)}))}))},loadMeanMetrics:function(){var workflowID,_this2=this;workflowID="readingalarm"===this.moduleName?8:"newreadingalarm"===this.moduleName?108:"agentAlarm"===this.moduleName||"operationalarm"===this.moduleName?11:47;
// if (!this.details.metricsPromise) {
var params=[this.details.alarm.id];"newreadingalarm"===this.moduleName&&params.push(this.cardId);var promise=this.$util.getWorkFlowResult(workflowID,params).finally((function(_){_this2.$set(_this2.details,"metricsPromise",null)}));
// }
return this.$set(this.details,"metricsPromise",promise),this.details.metricsPromise}}},mixins_AnomalyMixinvue_type_script_lang_js=AnomalyMixinvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(mixins_AnomalyMixinvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,AnomalyMixin=component.exports},
/***/320849:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return MLAlarmEnergyCdd}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/components/page/widget/anomalies/MLAlarmEnergyCdd.vue?vue&type=template&id=2fad166a
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"p30 pT30 d-flex flex-direction-column metrics-card"},[_vm.loading?_c("spinner",{attrs:{show:_vm.loading}}):[_c("div",{staticClass:"f12 bold text-uppercase mB10 fc-black-13 text-left letter-spacing1"},[_vm._v(" ENERGY/CDD ")]),_c("div",{staticClass:"d-flex pT20"},[_c("div",{staticClass:"items-baseline width50 pR8"},[_c("div",{staticClass:"f18 bold"},[_vm._v(" "+_vm._s(_vm.thisMonthData||0)+" "),_c("span",{staticClass:"pL5 f12 fw4"},[_vm._v("kWh/CDD")])]),_c("div",{staticClass:"secondary-text pT5"},[_vm._v("This Month")])]),_c("div",{staticClass:"items-baseline width50 pL10 border-left2"},[_c("div",{staticClass:"f18 bold"},[_vm._v(" "+_vm._s(_vm.lastMonthData||0)+" "),_c("span",{staticClass:"pL5 f12 fw4"},[_vm._v("kWh/CDD")])]),_c("div",{staticClass:"letter-spacing0_5 fc-blue-label f12 text-capitalize pT5"},[_vm._v(" Last Month ")])])])]],2)},staticRenderFns=[],AnomalyMixin=(__webpack_require__(897389),__webpack_require__(381056)),MLAlarmEnergyCddvue_type_script_lang_js={props:["details","layoutParams","hideTitleSection","activeTab","widget","resizeWidget","eventBus"],data:function(){return{thisMonthData:0,lastMonthData:0,loading:!1}},mounted:function(){this.loadData()},mixins:[AnomalyMixin/* default */.Z],computed:{},watch:{isMetricsApiLoading:function(){this.loading=this.isMetricsApiLoading,this.loadData()}},methods:{loadData:function(){
// this.loading = true
// if (this.details && this.details.alarm) {
//   let resourceId = this.details.alarm.resource.id
//   this.$util
//     .getWorkFlowResult('49', [
//       [resourceId],
//       this.details.alarm.resource.siteId,
//     ])
//     .then(response => {
//       this.loading = false
// if (response) {
if(!this.isMetricsApiLoading){var energyCdd=this.$store.getters["anomalies/getEnergyByCdd"];this.thisMonthData=this.$getProperty(energyCdd[this.currentMonth],"value"),this.thisMonthData>0&&(this.thisMonthData=this.thisMonthData.toFixed(2)),this.lastMonthData=this.$getProperty(energyCdd[this.lastMonth],"value"),this.lastMonthData>0&&(this.lastMonthData=this.lastMonthData.toFixed(2))}
// }
// })
// .catch(() => {
//   this.loading = false
// })
// }
}}},anomalies_MLAlarmEnergyCddvue_type_script_lang_js=MLAlarmEnergyCddvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(anomalies_MLAlarmEnergyCddvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,MLAlarmEnergyCdd=component.exports}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/20849.js.map