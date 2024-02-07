"use strict";(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[98574,40106],{
/***/940106:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return ReadingKpiList}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/pages/energy/kpi/ReadingKpiList.vue?vue&type=template&id=28393e1e&scoped=true
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("CommonListLayout",{class:["reading-kpi",_vm.isInPageBuilder&&"summary-page"],attrs:{moduleName:"formulaField",getPageTitle:function(){return"Reading KPI Viewer"}},scopedSlots:_vm._u([{key:"views",fn:function(){return[_vm.isInPageBuilder?_c("div"):_c("KpiViews")]},proxy:!0},_vm.isInPageBuilder?null:{key:"header",fn:function(){return[!_vm.showSearch&&_vm.totalCount>0?[_c("pagination",{staticClass:"pL15 fc-black-small-txt-12",attrs:{total:_vm.totalCount,perPage:50}}),_c("span",{staticClass:"separator"},[_vm._v("|")])]:_vm._e(),_vm.isFilterOptionsLoading?_vm._e():_c("search",{attrs:{config:_vm.filterConfig,moduleName:_vm.filterConfig.moduleName,defaultFilter:"name",includeAllMetaFields:!1}}),_vm.showSearch?_vm._e():[_c("span",{staticClass:"separator"},[_vm._v("|")]),_c("div",{staticClass:"pL10"},[_c("button",{staticClass:"fc-create-btn outline-none",staticStyle:{"margin-top":"-10px"},on:{click:function($event){_vm.showCreationForm=!0}}},[_vm._v(" "+_vm._s(_vm.$t("common.products.new_kpi"))+" ")])])]]},proxy:!0}],null,!0)},[_c("div",{staticClass:"p10 pT0 mT10 kpi-viewer"},[_vm.isInPageBuilder?_vm._e():_c("div",[_c("tags",{attrs:{disableSaveFilters:!0}})],1),_c("div",{staticClass:"width100 fc-rp-top-bar white-bg inline-flex justify-content-space"},[_vm.isInPageBuilder?_vm._e():_c("span",{staticClass:"mT10 mR10 bold"},[_vm._v(_vm._s(_vm.$t("common._common.frequency")))]),_vm.isInPageBuilder?_vm._e():_c("el-select",{staticClass:"fc-input-full-border2",on:{change:_vm.handleViewTabClick},model:{value:_vm.currentView,callback:function($$v){_vm.currentView=$$v},expression:"currentView"}},_vm._l(_vm.views,(function(view,index){return _c("el-option",{key:index,staticClass:"text-capitalize reading-kpi",attrs:{label:_vm.$helpers.capitalize(view),value:view}})})),1),!_vm.isInPageBuilder&&_vm.lastUpdatedTime?_c("div",{staticClass:"llast-updated f12 p15 mL20 letter-spacing0_5 pT12"},[_vm._v(" "+_vm._s(_vm.$t("common.header.last_updated"))+" "),_c("span",{staticClass:"bold"},[_vm._v(_vm._s(_vm.lastUpdatedTime))])]):_vm._e(),_c("div",{staticClass:"marginL-auto d-flex"},[_c("div",{staticClass:"width: 220px; text-align: right;"},[_vm.showPicker?_c("new-date-picker",{staticClass:"facilio-resource-date-picker",attrs:{dateObj:_vm.dateObj,zone:_vm.$timezone},on:{date:_vm.onDateChange}}):_vm._e()],1)])],1),_vm.loading?_c("div",{staticClass:"flex-middle fc-empty-white"},[_c("spinner",{attrs:{show:_vm.loading,size:"80"}})],1):_vm.$validation.isEmpty(_vm.list)?_c("div",{staticClass:"width100 text-center empty-container pB30 pT30"},[_c("inline-svg",{staticClass:"vertical-middle",attrs:{src:"svgs/emptystate/kpi",iconClass:"icon icon-256"}}),_c("div",{staticClass:"f18 bold",staticStyle:{"margin-bottom":"7px"}},[_vm._v(" "+_vm._s(_vm.$t("common.products.no_kpis_found"))+" ")]),_c("div",{staticClass:"f12 grey-text2"},[_vm._v(" "+_vm._s(_vm.$t("common.header.you_dont_seem_to_have_kpis_created_view"))+" ")])],1):_c("div",{class:["table-container",!_vm.isInPageBuilder&&"full-height"]},[_c("table",{attrs:{cellpadding:"0"}},[_c("thead",{ref:"tableHeader",staticClass:"letter-spacing08"},[_c("tr",[_c("th",{staticClass:"text-uppercase header",staticStyle:{width:"26%"}},[_c("div",{staticClass:"header-cell"},[_vm.isInPageBuilder?[_vm._v(_vm._s(_vm.kpiType))]:[_vm._v(" "+_vm._s(_vm.isGroupedByKpi?"KPI":_vm.kpiType)+" ")],_vm.isInPageBuilder?_vm._e():_c("a",{staticClass:"f11 fR font-normal line-height18 text-capitalize",on:{click:function($event){return _vm.changeGrouping(_vm.isGroupedByKpi)}}},[_vm._v(" "+_vm._s(_vm.isGroupedByKpi?"Group By Resource":"Group By KPI")+" ")])],2)]),_c("th",{staticClass:"text-uppercase text-center header",staticStyle:{width:"8%"}},[_c("div",{staticClass:"header-cell"},[_vm._v(" "+_vm._s(_vm.$t("common._common.safe_limit"))+" ")])]),_c("th",{staticClass:"text-uppercase text-center header",staticStyle:{width:"10%"}},[_c("div",{staticClass:"header-cell"},[_vm._v(" "+_vm._s(_vm.$t("common.header.current_value"))+" ")])]),_c("th",{staticClass:"text-uppercase text-center header secondary",staticStyle:{width:"8%"}},[_c("div",{staticClass:"header-cell"},[_vm._v(_vm._s(_vm.$t("common._common.sum")))])]),_c("th",{staticClass:"text-uppercase text-center header secondary",staticStyle:{width:"8%"}},[_c("div",{staticClass:"header-cell"},[_vm._v(_vm._s(_vm.$t("common.header.avg")))])]),_c("th",{staticClass:"text-uppercase text-center header secondary",staticStyle:{width:"12%"}},[_c("div",{staticClass:"header-cell"},[_vm._v(" "+_vm._s(_vm.$t("common.products.min_max"))+" ")])]),_c("th",{staticClass:"text-uppercase header secondary",staticStyle:{width:"22%"}},[_c("div",{staticClass:"header-cell"},[_vm._v(" "+_vm._s(_vm.$t("common.header.kpi_trend"))+" ")])]),_c("th",{staticClass:"text-uppercase text-right header secondary",staticStyle:{width:"6%"}},[_c("div",{staticClass:"header-cell"},[_vm._v(" "+_vm._s(_vm.$t("common.tabs.violations"))+" ")])])])]),_c("tbody",{ref:"tableBody"},[_vm._l(_vm.list,(function(item){return[_vm.isInPageBuilder?_vm._e():_c("tr",{key:item.id,staticClass:"separator-row pointer"},[_c("th",{attrs:{colspan:"8"}},[_c("div",{staticClass:"cell p10 pT5 pB5 f12",on:{click:function($event){return _vm.toggleDetails(item)}}},[item.canShow?_c("i",{staticClass:"el-icon-arrow-up"}):_c("i",{staticClass:"el-icon-arrow-down"}),_vm._v(" "+_vm._s(item.name)+" "),_vm.isGroupedByKpi&&_vm.$getProperty(item,"data.0.unit",null)?_c("span",{staticClass:"pL5 fR fw-normal bold"},[_vm._v(_vm._s(_vm.$t("common.header.all_values_in"))+" "+_vm._s(_vm.$getProperty(item,"data.0.unit",null)))]):_vm._e()])])]),item.canShow?_vm._l(item.data,(function(row){return _c("tr",{key:item.id+"-"+(_vm.isGroupedByKpi?row.resourceId:row.id),staticClass:"data-row"},[_c("td",[_c("div",{class:["cell",!_vm.isInPageBuilder&&"pL40"]},[_vm._v(" "+_vm._s(_vm.isGroupedByKpi?row.resourceName:row.name)+" ")])]),_c("td",{staticClass:"text-right"},[_c("div",{staticClass:"cell"},[_vm._v(" "+_vm._s(_vm.getSafeLimit(row.target,row.minTarget))+" ")])]),_c("td",{staticClass:"text-right"},[_c("div",{staticClass:"cell"},[_vm._v(" "+_vm._s(Number.parseFloat(row.value).toFixed(2)||"--")+" "),!_vm.isGroupedByKpi&&row.unit?_c("span",[_vm._v(" "+_vm._s(row.unit)+" ")]):_vm._e()])]),_vm.shimmerLoading?_c("td",{staticClass:"text-right"},[_c("div",{staticClass:"loading-shimmer lines"})]):_c("td",{staticClass:"text-right"},[_c("div",{staticClass:"cell"},[_vm._v(" "+_vm._s(row.sum||"--")+" "),!_vm.isGroupedByKpi&&row.unit?_c("span",[_vm._v(" "+_vm._s(row.unit)+" ")]):_vm._e()])]),_vm.shimmerLoading?_c("td",{staticClass:"text-right"},[_c("div",{staticClass:"loading-shimmer lines"})]):_c("td",{staticClass:"text-right"},[_c("div",{staticClass:"cell"},[_vm._v(" "+_vm._s(row.avg||"--")+" "),!_vm.isGroupedByKpi&&row.unit?_c("span",[_vm._v(" "+_vm._s(row.unit)+" ")]):_vm._e()])]),_vm.shimmerLoading?_c("td",{staticClass:"text-center"},[_c("div",{staticClass:"loading-shimmer lines"})]):_c("td",{staticClass:"text-center"},[row.min||row.max?_c("div",{staticClass:"cell p5"},[_vm._v(" "+_vm._s(row.min||"--")+" "),!_vm.isGroupedByKpi&&row.unit?_c("span",[_vm._v(" "+_vm._s(row.unit)+" ")]):_vm._e(),_vm._v(" - "+_vm._s(row.max||"--")+" "),!_vm.isGroupedByKpi&&row.unit?_c("span",[_vm._v(" "+_vm._s(row.unit)+" ")]):_vm._e()]):_c("div",[_vm._v("---")])]),_vm.shimmerLoading?_c("td",[_c("div",{staticClass:"sparkline-trend loading-shimmer lines"})]):_c("td",{staticClass:"p0"},[_c("div",{ref:"sparkline-trend",refInFor:!0,staticClass:"sparkline-trend"},[row.sparkline&&row.sparkline.dataObj?_c("sparkline",{attrs:{height:_vm.sparkLineStyleObj.height,width:_vm.sparkLineStyleObj.width,tooltipProps:row.sparkline.label}},[_c("sparklineCurve",{attrs:{data:row.sparkline.dataObj,limit:row.sparkline.dataObj.length,styles:_vm.sparkLineStyleObj.style,textStyles:row.sparkline.label,refLineType:"custom",refLineStyles:row.target?_vm.sparkLineStyleObj.targetLineStyle:null,refLineProps:row.target?{value:row.target}:null}})],1):_vm._e()],1)]),_vm.shimmerLoading?_c("td",{staticClass:"text-right"},[_c("div",{staticClass:"loading-shimmer lines"})]):_c("td",{staticClass:"text-right"},[_c("div",{staticClass:"cell"},[_vm._v(_vm._s(row.violations||"--"))])])])})):_vm._e()]}))],2)])])]),_vm.showCreationForm?_c("new-kpi",{attrs:{isNew:!0},on:{onSave:function($event){return _vm.loadData()},onClose:function($event){_vm.showCreationForm=!1}}}):_vm._e()],1)},staticRenderFns=[],regeneratorRuntime=__webpack_require__(150124),asyncToGenerator=__webpack_require__(548534),objectSpread2=__webpack_require__(595082),ViewMixin=(__webpack_require__(564043),__webpack_require__(61514),__webpack_require__(169358),__webpack_require__(260228),__webpack_require__(947522),__webpack_require__(976801),__webpack_require__(843843),__webpack_require__(857267),__webpack_require__(648324),__webpack_require__(634338),__webpack_require__(450886),__webpack_require__(670560),__webpack_require__(425728),__webpack_require__(897389),__webpack_require__(52362),__webpack_require__(879288),__webpack_require__(821694),__webpack_require__(76265),__webpack_require__(453877)),NewDatePicker=__webpack_require__(900773),NewDateHelper=__webpack_require__(105534),validation=__webpack_require__(990260),Sparkline=__webpack_require__(380390),FPagination=__webpack_require__(929593),AnalyticsMixin=__webpack_require__(244936),kpiConstants=__webpack_require__(361251),helpers_formatter=__webpack_require__(676240),moment_timezone=__webpack_require__(480008),moment_timezone_default=__webpack_require__.n(moment_timezone),vue_runtime_esm=__webpack_require__(720144),DeprecatedCommonLayout=__webpack_require__(714218),Search=__webpack_require__(718452),Tags=__webpack_require__(482912),AddKpi=__webpack_require__(169251),vuex_esm=__webpack_require__(420629),kpiViews=__webpack_require__(294438),picklist=__webpack_require__(961358),ReadingKpiListvue_type_script_lang_js={props:["isInPageBuilder","details"],mixins:[NewDateHelper/* default */.Z,AnalyticsMixin/* default */.Z,ViewMixin/* default */.Z],components:{NewDatePicker:NewDatePicker/* default */.Z,sparkline:Sparkline/* default */.Z,Pagination:FPagination/* default */.Z,CommonListLayout:DeprecatedCommonLayout/* default */.Z,Search:Search/* default */.Z,Tags:Tags/* default */.Z,NewKpi:AddKpi/* default */.Z,KpiViews:kpiViews/* default */.Z},data:function(){return{loading:!1,shimmerLoading:!1,perPage:50,totalCount:null,dateObj:null,list:[],viewState:{},views:["HOURLY","DAILY","WEEKLY","MONTHLY","YEARLY"],currentView:null,showPicker:!0,groupBy:"kpi",lastUpdatedTime:null,showCreationForm:!1,isFilterOptionsLoading:!0,filterConfig:{moduleName:"formulaField",path:"/app/em/kpi/",includeParentCriteria:!0,excludeModuleStateField:!0,data:{name:{label:this.$t("common._common.name"),displayType:"string",value:""},kpiCategory:{label:this.$t("common._common.category"),displayType:"select",options:{},value:null},siteId:{label:this.$t("common.products.site"),options:{},value:null,displayType:"select",key:"siteId"},space:{label:this.$t("common.space_asset_chooser.space"),displayType:"space",options:{},value:[]},assetCategoryId:{label:this.$t("common._common.asset_category"),displayType:"select",options:{},value:null}},availableColumns:["name","kpiCategory","siteId","space","assetCategoryId"],fixedCols:["name"],saveView:!1}}},created:function(){this.intiViewStateObj(),this.loadFilterOptions()},computed:(0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)({},(0,vuex_esm/* mapState */.rn)({showSearch:function(state){return state.search.active}})),{},{defaultView:function(){return this.views[1]},currentViewState:function(){var currentView=this.currentView,viewState=this.viewState[currentView];return(0,validation/* isEmpty */.xb)(viewState)?this.viewState[this.defaultView]:(0,objectSpread2/* default */.Z)({},viewState)},page:function(){return this.$route.query.page||1},selectedKpiId:function(){return this.isInPageBuilder&&this.details.id},isSpaceKpi:function(){var _this$details$matched=this.details.matchedResources,matchedResources=void 0===_this$details$matched?[]:_this$details$matched,firstMatchedResource=matchedResources[0]||{};return"SPACE"===firstMatchedResource.resourceTypeEnum},kpiType:function(){return this.isInPageBuilder?this.isSpaceKpi?"space":"asset":"all"},isGroupedByKpi:function(){return"kpi"===this.groupBy},filters:function(){var $route=this.$route,_ref=$route||{},search=_ref.query.search;return(0,validation/* isEmpty */.xb)(search)?null:JSON.parse(this.$route.query.search)},sparkLineStyleObj:function(){var width=400,height=55,$refs=this.$refs,loading=this.loading;return loading||(0,validation/* isEmpty */.xb)($refs["sparkline-trend"])||(width=$refs["sparkline-trend"][0].scrollWidth),{width:width,height:height,style:{stroke:"#886cff",fill:"#886cff"},targetLineStyle:{stroke:"#d14",strokeOpacity:1,strokeDasharray:"2, 2"}}},watchedProps:function(){return this.page,this.kpiType,this.viewState,this.groupBy,Date.now()}}),watch:{watchedProps:function(){this.loadData()},page:function(){this.loadData()},filters:{handler:function(newVal,oldVal){newVal!==oldVal&&this.loadData()}}},methods:{intiViewStateObj:function(){var _this=this;this.views.forEach((function(view){_this.$set(_this.viewState,view,kpiConstants/* viewStateMeta */.C[view])})),this.currentView=this.defaultView;var _this$$route$query=this.$route.query,_this$$route$query$fr=_this$$route$query.frequency,frequency=void 0===_this$$route$query$fr?null:_this$$route$query$fr,_this$$route$query$gr=_this$$route$query.groupBy,groupBy=void 0===_this$$route$query$gr?null:_this$$route$query$gr;frequency&&this.views.includes(frequency)&&(this.currentView=frequency),groupBy&&["kpi","resource"].includes(groupBy)&&(this.groupBy=groupBy),this.setPicker()},syncTabInQuery:function(){this.$router.replace({query:(0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)({},this.$route.query),{},{frequency:this.currentView})})},syncGroupingInQuery:function(){this.$router.replace({query:(0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)({},this.$route.query),{},{groupBy:this.groupBy})})},switchView:function(view){this.currentView=view,this.syncTabInQuery(),this.loadData()},handleViewTabClick:function(view){this.switchView(view),this.setPicker()},setPicker:function(){var _this2=this;this.showPicker=!1,this.dateObj=NewDateHelper/* default */.Z.getDatePickerObject(this.currentViewState.operatorID,""+this.currentViewState.start),this.$nextTick((function(){return _this2.showPicker=!0}))},changeGrouping:function(isGroupedByKpi){this.groupBy=isGroupedByKpi?"resource":"kpi",this.syncGroupingInQuery()},loadFilterOptions:function(){var _this3=this;return(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee(){var _yield$getFieldOption,error,options;return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context){while(1)switch(_context.prev=_context.next){case 0:return _this3.isFilterOptionsLoading=!0,_context.next=3,(0,picklist/* getFieldOptions */._K)({field:{lookupModuleName:"kpiCategory",skipDeserialize:!0}});case 3:_yield$getFieldOption=_context.sent,error=_yield$getFieldOption.error,options=_yield$getFieldOption.options,error?_this3.$message.error(error.message||"Error Occured"):(_this3.$set(_this3.filterConfig.data,"kpiCategory",(0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)({},_this3.filterConfig.data.kpiCategory),{},{options:options})),_this3.isFilterOptionsLoading=!1);case 7:case"end":return _context.stop()}}),_callee)})))()},getUrl:function(){var parsedFilters,kpiType=this.kpiType,_this$currentViewStat=this.currentViewState,currentViewState=void 0===_this$currentViewStat?{}:_this$currentViewStat,page=this.page,perPage=this.perPage,groupBy=this.groupBy,filters=this.filters,selectedKpiId=this.selectedKpiId,filterObj=(0,objectSpread2/* default */.Z)({},filters),_filterObj=filterObj,resource=_filterObj.resource,url="/v2/kpi/viewer/".concat(kpiType,"?");(0,validation/* isEmpty */.xb)(selectedKpiId)||(filterObj=filterObj||{},filterObj.id={operatorId:9,value:["".concat(selectedKpiId)]}),(0,validation/* isEmpty */.xb)(filterObj)||(filterObj.resource&&delete filterObj.resource,parsedFilters=encodeURIComponent(JSON.stringify(filterObj))),(0,validation/* isEmpty */.xb)(parsedFilters)||(url="".concat(url,"filters=").concat(parsedFilters,"&"));var _currentViewState$fre=currentViewState.frequencyId,frequencyId=void 0===_currentViewState$fre?null:_currentViewState$fre;if(this.isInPageBuilder&&(frequencyId=this.details.frequency),!(0,validation/* isEmpty */.xb)(frequencyId)){var params={frequency:frequencyId,page:page,perPage:perPage,groupBy:groupBy};return(0,validation/* isEmpty */.xb)(resource)||(params.resource=resource[0].value),Object.keys(params).reduce((function(res,key){return res+=key+"="+params[key]+"&",res}),url)}},loadData:function(){var _this4=this,url=this.getUrl();(0,validation/* isEmpty */.xb)(url)||(this.loading=!0,this.$http.get(url).then((function(_ref2){var data=_ref2.data;if(_this4.loading=!1,0===data.responseCode){var _data$result=data.result,lastUpdatedTime=_data$result.lastUpdatedTime,_data$result$kpis=_data$result.kpis,kpis=void 0===_data$result$kpis?[]:_data$result$kpis,items=kpis.map((function(item){return _this4.$set(item,"canShow",!0),item}));_this4.list=items,_this4.lastUpdatedTime=moment_timezone_default()(lastUpdatedTime).tz(vue_runtime_esm["default"].prototype.$timezone).format("ddd DD MMM YYYY"),_this4.$nextTick((function(){_this4.loadViolationsCount(),_this4.loadChartData()}))}else _this4.list=[],_this4.lastUpdatedTime=null})).catch((function(){_this4.list=[],_this4.lastUpdatedTime=null,_this4.loading=!1})),this.$http.get("".concat(url,"&fetchCount=true")).then((function(_ref3){var data=_ref3.data;0===data.responseCode&&(_this4.totalCount=data.result.count)})))},loadViolationsCount:function(){var _this5=this,list=this.list,dateObj=this.dateObj,idMap=list.reduce((function(acc,item){return item.data&&item.data.forEach((function(_ref4){var id=_ref4.id,resourceId=_ref4.resourceId;acc.push("".concat(id,"_").concat(resourceId))})),acc}),[]);return this.$http.post("/v2/kpi/viewer/violations",{kpiResourceIds:idMap,dateRange:{startTime:dateObj.value[0],endTime:dateObj.value[1]}}).then((function(_ref5){var data=_ref5.data;if(0===data.responseCode){var violations=data.result.violations;list.forEach((function(item){item.data&&item.data.forEach((function(resource){var id=resource.id,resourceId=resource.resourceId,key="".concat(id,"_").concat(resourceId);_this5.$set(resource,"violations",violations[key])}))}))}})).catch((function(){_this5.$message.error(_this5.$t("common._common.could_not_fetch_violations"))}))},loadChartData:function(){var _this6=this;if(!(0,validation/* isEmpty */.xb)(this.dateObj)){var points=[];this.list.forEach((function(_ref6){var _ref6$data=_ref6.data,data=void 0===_ref6$data?[]:_ref6$data;data.forEach((function(resource){points.push({parentId:[resource.resourceId],yAxis:{fieldId:resource.readingFieldId}})}))})),this.setAlias(points);var params={startTime:this.dateObj.value[0],endTime:this.dateObj.value[1],fields:JSON.stringify(points),analyticsType:2,newFormat:!0};return this.$http.post("/v2/report/readingReport",params).then((function(_ref7){var data=_ref7.data;if(0===data.responseCode){var result=data.result,reportData=result.reportData,aggr=result.reportData.aggr,report=result.report,dataParams=_this6.constructChartData(reportData,report);_this6.list.forEach((function(_ref8){var _ref8$data=_ref8.data,data=void 0===_ref8$data?[]:_ref8$data;return data.forEach((function(resource){var point=points.find((function(point){return point.parentId.includes(resource.resourceId)&&point.yAxis.fieldId===resource.readingFieldId})),_ref9=point||{},_ref9$aliases=_ref9.aliases,_ref9$aliases2=void 0===_ref9$aliases?{}:_ref9$aliases,alias=_ref9$aliases2.actual;if(alias){
// Set Sparkline Data
var _ref10=dataParams.find((function(p){return p.alias===alias}))||{},_ref10$sparkline=_ref10.sparkline,_sparkline=void 0===_ref10$sparkline?null:_ref10$sparkline;_this6.$set(resource,"sparkline",_sparkline);
// Set other props from reportData
var props=["avg","min","max","sum"];props.forEach((function(prop){var value=aggr["".concat(alias,".").concat(prop)];value=(0,validation/* isEmpty */.xb)(value)?null:Number.parseFloat(value).toFixed(2),!(0,validation/* isEmpty */.xb)(value)&&_this6.$set(resource,prop,value)}))}else _this6.$set(resource,"sparkline",null)}))}))}})).catch((function(error){}))}},constructChartData:function(reportData,report){var _this7=this,data=reportData.data,dataPoints=report.dataPoints,dataParams=[];return(0,validation/* isEmpty */.xb)(data)||(0,validation/* isEmpty */.xb)(dataPoints)||dataPoints.forEach((function(dataPoint){var actual=dataPoint.aliases.actual,sparkline={};sparkline.dataObj=data.map((function(sparkData){return Number(sparkData[actual])})),sparkline.labelArray=data.map((function(sparkData){return Number(sparkData["X"])}));var currentView=_this7.currentView;sparkline.label={formatter:function(val){var date;"DAILY"===currentView||"WEEKLY"===currentView?date=helpers_formatter/* default */.ZP.formatCardTime(sparkline.labelArray[val.index],12,22):"HOURLY"===currentView?date=helpers_formatter/* default */.ZP.formatCardTime(sparkline.labelArray[val.index],20,22):"MONTHLY"!==currentView&&"YEARLY"!==currentView||(date=moment_timezone_default()(sparkline.labelArray[val.index]).tz(vue_runtime_esm["default"].prototype.$timezone).format("MMM YYYY"));var templateValue='<div style="padding:3px;">\n                            <div>\n                              <label>'.concat(date,'</label>\n                            </div>\n                          </div>\n                          <div>\n                            <label style="color:#fff;font-weight:bold;">').concat(val.value,"</label>&nbsp;");return templateValue}},dataParams.push({alias:actual,sparkline:sparkline})})),dataParams},toggleDetails:function(item){item.canShow=!item.canShow},onDateChange:function(dateFilter){var _this8=this,promises=[];this.shimmerLoading=!0,this.$set(this,"dateObj",dateFilter),promises.push(this.loadChartData()),promises.push(this.loadViolationsCount()),promises.push(this.$helpers.delay(3e3)),Promise.all(promises).then((function(){_this8.shimmerLoading=!1}))},getSafeLimit:function(target,mintarget){return(0,validation/* isEmpty */.xb)(target)||(0,validation/* isEmpty */.xb)(mintarget)?(0,validation/* isEmpty */.xb)(target)?(0,validation/* isEmpty */.xb)(mintarget)?"--":"> ".concat(mintarget):"< ".concat(target):"".concat(mintarget," - ").concat(target)}}},kpi_ReadingKpiListvue_type_script_lang_js=ReadingKpiListvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(kpi_ReadingKpiListvue_type_script_lang_js,render,staticRenderFns,!1,null,"28393e1e",null)
/* harmony default export */,ReadingKpiList=component.exports}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/98574.js.map