"use strict";(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[6707],{
/***/406707:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return AnomalyDetailsWidget}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/components/page/widget/anomalies/AnomalyDetailsWidget.vue?vue&type=template&id=2ae8bfbf&scoped=true
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"asset-details-widget pT0"},[_c("div",{staticClass:"container flex"},[_c("div",{staticClass:"field"},[_c("el-row",{staticClass:"border-bottom3 pB15 pT5"},[_c("el-col",{attrs:{span:12}},[_c("el-col",{attrs:{span:8}},[_c("div",{staticClass:"fc-black-13 text-left bold"},[_vm._v(" "+_vm._s(_vm.$t("common._common.asset"))+" ")])]),_c("el-col",{attrs:{span:14}},[_c("div",{staticClass:"fc-black-13 text-left pointer",on:{click:function($event){return _vm.openAsset(_vm.alarmDetails.resource.id)}}},[_vm._v(" "+_vm._s(_vm.resourceName)+" ")])])],1),_c("el-col",{attrs:{span:12}},[_c("el-col",{attrs:{span:8}},[_c("div",{staticClass:"fc-black-13 text-left bold"},[_vm._v(" "+_vm._s(_vm.$t("common._common.category"))+" ")])]),_c("el-col",{attrs:{span:14}},[_c("div",{staticClass:"fc-black-13 text-left"},[_vm._v(" "+_vm._s(_vm.$validation.isEmpty(_vm.assetCategory)?"---":_vm.assetCategory)+" ")])])],1)],1),_c("el-row",{staticClass:"border-bottom3 pB15 pT15"},[_c("el-col",{attrs:{span:12}},[_c("el-col",{attrs:{span:8}},[_c("div",{staticClass:"fc-black-13 text-left bold"},[_vm._v(" "+_vm._s(_vm.$t("common.products.site"))+" ")])]),_c("el-col",{attrs:{span:14}},[_c("div",{staticClass:"fc-black-13 text-left pointer",on:{click:function($event){return _vm.openSite(_vm.spaceDetails.site.id)}}},[_vm._v(" "+_vm._s(_vm.siteName)+" ")])])],1),_c("el-col",{attrs:{span:12}},[_c("el-col",{attrs:{span:8}},[_c("div",{staticClass:"fc-black-13 text-left bold"},[_vm._v(" "+_vm._s(_vm.$t("common._common.building"))+" ")])]),_c("el-col",{attrs:{span:14}},[_c("div",{staticClass:"fc-black-13 text-left pointer",on:{click:function($event){return _vm.openBuilding(_vm.spaceDetails.site.id,_vm.spaceDetails.building.id)}}},[_vm._v(" "+_vm._s(_vm.buildingName)+" ")])])],1)],1),_c("el-row",{staticClass:"pB15 pT15"},[_c("el-col",{attrs:{span:12}},[_c("el-col",{attrs:{span:8}},[_c("div",{staticClass:"fc-black-13 text-left bold"},[_vm._v(" "+_vm._s(_vm.$t("common._common.floor"))+" ")])]),_c("el-col",{attrs:{span:14}},[_c("div",{staticClass:"fc-black-13 text-left pointer",on:{click:function($event){return _vm.openFloor(_vm.spaceDetails.site.id,_vm.spaceDetails.floor.id)}}},[_vm._v(" "+_vm._s(_vm.floorName)+" ")])])],1),_c("el-col",{attrs:{span:12}},[_c("el-col",{attrs:{span:8}},[_c("div",{staticClass:"fc-black-13 text-left bold"},[_vm._v(" "+_vm._s(_vm.$t("common.space_asset_chooser.space"))+" ")])]),_c("el-col",{attrs:{span:14}},[_c("div",{staticClass:"fc-dark-blue3-13 text-left"},[_vm._v(" --- ")])])],1)],1)],1)])])},staticRenderFns=[],regeneratorRuntime=__webpack_require__(150124),asyncToGenerator=__webpack_require__(548534),objectSpread2=__webpack_require__(595082),vuex_esm=(__webpack_require__(434284),__webpack_require__(564043),__webpack_require__(857267),__webpack_require__(634338),__webpack_require__(425728),__webpack_require__(260228),__webpack_require__(420629)),router=__webpack_require__(329435),validation=__webpack_require__(990260),api=__webpack_require__(32284),log_map_state=__webpack_require__(766331),AnomalyDetailsWidgetvue_type_script_lang_js={props:["moduleName","details","layoutParams","resizeWidget","primaryFields"],data:function(){return{loading:!1,lookupValue:null,isAllVisible:!1,assetCategory:null}},created:function(){this.getAssetCategory()},computed:(0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)({},(0,vuex_esm/* mapState */.rn)({metaInfo:function(state){return state.view.metaInfo}})),(0,log_map_state/* mapStateWithLogging */.g)({spaces:function(state){return state.spaces}})),{},{alarmDetails:function(){var details=this.details;return this.$getProperty(details,"alarm")},resourceDetails:function(){var details=this.details,_ref=details||{},alarm=_ref.alarm;return this.$getProperty(alarm,"resource")},spaceDetails:function(){var details=this.details,_ref2=details||{},alarm=_ref2.alarm;return this.$getProperty(alarm,"spaceDetails")},occurenceDetails:function(){var details=this.details;return this.$getProperty(details,"occurrence")},resourceName:function(){return this.$getProperty(this.resourceDetails,"name","---")},siteName:function(){return this.$getProperty(this,"spaceDetails.site.name","---")},buildingName:function(){return this.$getProperty(this,"spaceDetails.building.name","---")},floorName:function(){return this.$getProperty(this,"spaceDetails.floor.name","---")}}),methods:{findRoute:function(){if((0,router/* isWebTabsEnabled */.tj)()){var $router=this.$router,tabType=router/* tabTypes */.VF.CUSTOM,config={type:"portfolio"},route=(0,router/* findRouteForTab */.OD)(tabType,{config:config})||{};return(0,validation/* isEmpty */.xb)(route)?null:$router.resolve({name:route.name}).href}return"/app/home/portfolio"},openAsset:function(id){if(id)if((0,router/* isWebTabsEnabled */.tj)()){var _ref3=(0,router/* findRouteForModule */.Jp)("asset",router/* pageTypes */.As.OVERVIEW)||{},name=_ref3.name;name&&this.$router.replace({name:name,params:{viewname:"all",id:id}})}else{var url="/app/at/assets/all/"+id+"/overview";this.$router.replace({path:url})}},openSite:function(id){if(id){var parentPath=this.findRoute();if(parentPath){var url="".concat(parentPath,"/site/").concat(id,"/overview");this.$router.replace({path:url})}}},openBuilding:function(siteId,id){if(id){var parentPath=this.findRoute();if(parentPath){var url="".concat(parentPath,"/site/").concat(siteId,"/building/").concat(id);this.$router.replace({path:url})}}},openFloor:function(siteId,id){if(id){var parentPath=this.findRoute();if(parentPath){var url="".concat(parentPath,"/site/").concat(siteId,"/floor/").concat(id);this.$router.replace({path:url})}}},loadSpaceDetails:function(){var _this=this;return(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee(){var _this$resourceDetails,spaceId,buildingId,siteId,locationId,url,_yield$API$get,data,error;return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context){while(1)switch(_context.prev=_context.next){case 0:if(!_this.resourceDetails){_context.next=11;break}if(_this$resourceDetails=_this.resourceDetails,spaceId=_this$resourceDetails.spaceId,buildingId=_this$resourceDetails.buildingId,siteId=_this$resourceDetails.siteId,locationId=spaceId>0?spaceId:buildingId>0?buildingId:siteId,!(locationId>0)){_context.next=11;break}return url="v2/basespaces/".concat(spaceId),_context.next=7,api/* API */.bl.get(url);case 7:_yield$API$get=_context.sent,data=_yield$API$get.data,error=_yield$API$get.error,(0,validation/* isEmpty */.xb)(error)?_this.spaceDetails=_this.$getProperty(data,"basespace"):_this.$message.error(error.message);case 11:case"end":return _context.stop()}}),_callee)})))()},getAssetCategory:function(){var _this2=this;return(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee2(){var assetCategoryId,url,_yield$API$get2,data,error,assetCategory;return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context2){while(1)switch(_context2.prev=_context2.next){case 0:if(_this2.assetCategory=_this2.$getProperty(_this2,"details.alarm.readingAlarmAssetCategory.displayName"),(0,validation/* isEmpty */.xb)(_this2.assetCategory)){_context2.next=5;break}return _context2.abrupt("return");case 5:return assetCategoryId=_this2.$getProperty(_this2,"alarmDetails.rule.assetCategoryId"),url="v2/module/data/list?moduleName=assetcategory",_context2.next=9,api/* API */.bl.get(url);case 9:_yield$API$get2=_context2.sent,data=_yield$API$get2.data,error=_yield$API$get2.error,(0,validation/* isEmpty */.xb)(error)?(assetCategory=(_this2.$getProperty(data,"details")||[]).find((function(assetCategory){return assetCategory.id===assetCategoryId})),_this2.assetCategory=_this2.$getProperty(assetCategory,"displayName")):_this2.$message.error(error.message);case 13:case"end":return _context2.stop()}}),_callee2)})))()}}},anomalies_AnomalyDetailsWidgetvue_type_script_lang_js=AnomalyDetailsWidgetvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(anomalies_AnomalyDetailsWidgetvue_type_script_lang_js,render,staticRenderFns,!1,null,"2ae8bfbf",null)
/* harmony default export */,AnomalyDetailsWidget=component.exports}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/6707.js.map