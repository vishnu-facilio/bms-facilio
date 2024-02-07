"use strict";(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[25803],{
/***/425803:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return MultiTrend}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/pages/dashboard/widget/cards/v2/MultiTrend.vue?vue&type=template&id=0409d67e
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"multi-trend pT5 dragabale-card fc-widget height-100"},[_c("div",{staticClass:"pT15 pB15 pL25 title-border"},[_c("div",{staticClass:"fc-black-color f16 bold letter-spacing0_4 text-left"},[_vm._v(" "+_vm._s(_vm.$t("panel.layout.trends"))+" ")])]),_c("div",{staticClass:"d-flex pL25 pR25 pT15 pB15 title-border"},[_c("div",{staticClass:"f11 fw6 fc-black-color letter-spacing1 column-width-name text-uppercase text-left"},[_vm._v(" "+_vm._s(_vm.$t("panel.layout.metrics"))+" ")]),_c("div",{staticClass:"f11 fw6 fc-black-color letter-spacing1 column-width-value text-uppercase text-left"},[_vm._v(" "+_vm._s(_vm.$t("panel.layout.max"))+" ")]),_c("div",{staticClass:"f11 fw6 fc-black-color letter-spacing1 sparkline-trend text-uppercase text-left"},[_vm._v(" "+_vm._s(_vm.$t("panel.layout.today"))+" ")])]),_vm.isLoading?_c("div",{staticClass:"loading-container"},[_c("Spinner",{attrs:{show:_vm.isLoading}})],1):_vm._l(_vm.dataParams,(function(data,index){return _c("div",{key:index,staticClass:"d-flex pT15 pL25 pR25"},[_c("div",{staticClass:"f13 bold leading-normal letter-spacing0_4 fc-black-color column-width-name text-left d-flex flex-direction-column justify-center"},[_vm._v(" "+_vm._s(data.name)+" ")]),_c("div",{staticClass:"f13 fc-black-color column-width-value text-left d-flex flex-direction-column justify-center"},[_vm._v(" "+_vm._s(data.maxValue)+" ")]),_c("div",{ref:"sparkline-trend",refInFor:!0,staticClass:"sparkline-trend"},[_c("sparkline",{attrs:{height:_vm.sparkLineStyleObj.height,width:_vm.sparkLineStyleObj.width,tooltipProps:data.sparkline.label,aliases:data.unitStr}},[_c("sparklineCurve",{attrs:{data:data.sparkline.dataObj,limit:data.sparkline.dataObj.length,styles:_vm.sparkLineStyleObj.style,textStyles:data.sparkline.label}})],1)],1)])}))],2)},staticRenderFns=[],Sparkline=(__webpack_require__(169358),__webpack_require__(260228),__webpack_require__(947522),__webpack_require__(434284),__webpack_require__(425728),__webpack_require__(634338),__webpack_require__(450886),__webpack_require__(879288),__webpack_require__(670560),__webpack_require__(380390)),helpers_formatter=__webpack_require__(676240),Spinner=__webpack_require__(604947),validation=__webpack_require__(990260),api=__webpack_require__(32284),MultiTrendvue_type_script_lang_js={props:["widget"],components:{Sparkline:Sparkline/* default */.Z,Spinner:Spinner/* default */.Z},data:function(){return{dataParams:[],sparkline:{labelArray:[],unitArray:[]},isLoading:!1}},computed:{sparkLineStyleObj:function(){var $refs=this.$refs,width=300;return(0,validation/* isEmpty */.xb)($refs)||(width=$refs["sparkline-trend"].scrollWidth),{width:width,height:35,style:{stroke:"#886cff",fill:"#886cff"}}},reportId:function(){var widget=this.widget,dataOptions=widget.dataOptions,id="";if(!(0,validation/* isEmpty */.xb)(dataOptions)){var metaJson=dataOptions.metaJson,parsedMetaJson=JSON.parse(metaJson),reportId=parsedMetaJson.reportId;id=reportId}return id}},created:function(){var _this=this,reportId=this.reportId,url="v3/report/reading/view?reportId=".concat(reportId,"&newFormat=true");this.isLoading=!0,api/* API */.bl.get(url).then((function(resp){if(resp.error)_this.isLoading=!1,_this.$message.error("Error while fetching Report Data");else{var _resp$data=resp.data,reportData=_resp$data.reportData,report=_resp$data.report;if(_this.isLoading=!1,!(0,validation/* isEmpty */.xb)(reportData)){var dataParams=_this.constructDataParams(reportData,report);_this.$set(_this,"dataParams",dataParams)}}})).catch((function(_ref){var message=_ref.message;_this.isLoading=!1,_this.$message.error(message)}))},methods:{constructDataParams:function(reportData,report){var _this2=this,self=this,dataParams=[],data=reportData.data,aggr=reportData.aggr,dataPoints=report.dataPoints,stateDataPoints=JSON.parse(report.chartState).dataPoints;return(0,validation/* isEmpty */.xb)(data)||(0,validation/* isEmpty */.xb)(dataPoints)||dataPoints.forEach((function(dataPoint){var actual=dataPoint.aliases.actual,unitStr=dataPoint.yAxis.unitStr,dataObj={sparkline:{}};unitStr=(0,validation/* isEmpty */.xb)(unitStr)?"":unitStr,dataObj.name=stateDataPoints.find((function(rt){return rt.alias===actual}))?stateDataPoints.find((function(rt){return rt.alias===actual})).label:"",dataObj.actual=actual,dataObj.maxValue="".concat(aggr["".concat(actual,".max")]," ").concat(unitStr),dataObj.unitStr=unitStr,dataObj.sparkline.dataObj=data.map((function(sparkData){return Number(sparkData[actual])})),_this2.sparkline.labelArray=data.map((function(sparkData){return Number(sparkData["X"])})),dataObj.sparkline.label={formatter:function(val){var data='<div style="padding:3px;"><div><label>'.concat(helpers_formatter/* default */.ZP.formatCardTime(self.sparkline.labelArray[val.index],20,22),"</label></div>");return data="".concat(data,'<div><label style="color:#fff;font-weight:bold;">').concat(val.value,"</label>&nbsp;<label>\n                ").concat(val.aliases,"</label></div></div>"),data}},dataParams.push(dataObj)})),dataParams}}},v2_MultiTrendvue_type_script_lang_js=MultiTrendvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(v2_MultiTrendvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,MultiTrend=component.exports}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/25803.js.map