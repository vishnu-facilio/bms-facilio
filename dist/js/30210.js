"use strict";(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[30210,78344,11358],{
/***/799680:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return NewRelatedTabListComponent}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/components/page/widget/site/NewRelatedTabListComponent.vue?vue&type=template&id=2a504a48&scoped=true
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"new-related-tab-list"},[_c("el-tabs",{model:{value:_vm.activeTab,callback:function($$v){_vm.activeTab=$$v},expression:"activeTab"}},_vm._l(_vm.tabs,(function(tab){return _c("el-tab-pane",{key:tab.linkName,attrs:{name:tab.linkName,label:tab.displayName,lazy:""}},[_vm.activeTab===tab.linkName?_c("list",{key:tab.linkName,staticClass:"height100 related-tab-list  overflow-x",attrs:{module:tab.moduleName,linkName:tab.linkName,parentModule:_vm.moduleName,displayName:tab.displayName,isActive:tab.isActive,portalName:tab.linkName+"-topbar",details:_vm.details}}):_vm._e()],1)})),1),_c("div",{key:_vm.activeTab+"-topbar",staticClass:"widget-topbar-actions"},[_c("portal-target",{key:_vm.activeTab+"-topbar",attrs:{name:_vm.activeTab+"-topbar"}})],1),_c("portal",{attrs:{to:_vm.widget.key+"-title-section",slim:""}},[_c("div",{staticClass:"flex-middle justify-content-space space-white-header "},[_c("div",{staticClass:"f18 bold"},[_vm._v(" "+_vm._s(""+_vm.moduleHeaderName)+" ")]),_vm.$hasPermission("space:CREATE")?_c("div",{staticClass:"fc-pink f13 bold pointer widget-tabs-header-create",on:{click:_vm.openNewForm}},[_c("el-button",{staticClass:"tab-header-btn",attrs:{type:"primary",disabled:_vm.decommission}},[_vm._v(" "+_vm._s(_vm.buttonText)+" ")])],1):_vm._e()])])],1)},staticRenderFns=[],RelatedTabListComponent=__webpack_require__(350935),NewRelatedTabListComponentvue_type_script_lang_js={extends:RelatedTabListComponent["default"],computed:{moduleDisplayName:function(){var widget=this.widget,_ref=widget||{},relatedList=_ref.relatedList,_ref2=relatedList||{},module=_ref2.module,_ref3=module||{},displayName=_ref3.displayName;return displayName||""},moduleHeaderName:function(){return this.moduleDisplayName+"s"},decommission:function(){return this.$getProperty(this,"details.decommission",!1)}}},site_NewRelatedTabListComponentvue_type_script_lang_js=NewRelatedTabListComponentvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(site_NewRelatedTabListComponentvue_type_script_lang_js,render,staticRenderFns,!1,null,"2a504a48",null)
/* harmony default export */,NewRelatedTabListComponent=component.exports},
/***/350935:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return RelatedTabListComponent}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/components/page/widget/site/RelatedTabListComponent.vue?vue&type=template&id=34a1faac
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",[_c("el-tabs",{model:{value:_vm.activeTab,callback:function($$v){_vm.activeTab=$$v},expression:"activeTab"}},_vm._l(_vm.tabs,(function(tab){return _c("el-tab-pane",{key:tab.linkName,attrs:{name:tab.linkName,label:tab.displayName,lazy:""}},[_vm.activeTab===tab.linkName?_c("list",{key:tab.linkName,staticClass:"height100 related-tab-list overflow-x",attrs:{module:tab.moduleName,linkName:tab.linkName,parentModule:_vm.moduleName,displayName:tab.displayName,isActive:tab.isActive,portalName:tab.linkName+"-topbar",details:_vm.details}}):_vm._e()],1)})),1),_c("div",{key:_vm.activeTab+"-topbar",staticClass:"widget-topbar-actions"},[_c("portal-target",{key:_vm.activeTab+"-topbar",attrs:{name:_vm.activeTab+"-topbar"}})],1),_c("portal",{attrs:{to:_vm.sectionKey+"-title-section",slim:""}},[_vm.$hasPermission("space:CREATE")&&!_vm.decommission?_c("div",{staticClass:"fc-pink f13 bold pointer",on:{click:_vm.openNewForm}},[_c("i",{staticClass:"el-icon-plus pR5"}),_vm._v(" "+_vm._s(_vm.buttonText)+" ")]):_vm._e()])],1)},staticRenderFns=[],SpaceListvue_type_template_id_add6fea4_render=(__webpack_require__(976801),function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{ref:"relatedListContainer",staticClass:"related-list-container"},[_vm.isLoading||_vm.isSearchDataLoading?_c("div",{staticClass:"loading-container d-flex justify-content-center height100"},[_c("spinner",{attrs:{show:_vm.isLoading||_vm.isSearchDataLoading}})],1):_vm.$validation.isEmpty(_vm.modulesList)?_c("div",{staticClass:"height100"},[_c("ListNoData",{attrs:{iconPath:"svgs/spacemanagement/"+_vm.moduleName,moduleDisplayName:_vm.moduleDisplayName}})],1):_c("div",{staticClass:"fc-list-view fc-table-td-height mT1"},[_c("div",{staticClass:"view-column-chooser",on:{click:_vm.showColumnCustomization}},[_c("img",{staticClass:"column-customization-icon",attrs:{src:__webpack_require__(266707)}})]),_c("el-table",{staticClass:"width100",attrs:{data:_vm.modulesList,height:_vm.tableHeight,"row-class-name":"space-row",fit:!0}},[_vm.$validation.isEmpty(_vm.mainFieldColumn)?_vm._e():_c("el-table-column",{attrs:{label:_vm.mainFieldColumn.displayName,prop:_vm.mainFieldColumn.name,fixed:"","min-width":"200"},scopedSlots:_vm._u([{key:"default",fn:function(item){return[_c("div",{staticClass:"table-subheading",on:{click:function($event){return _vm.routeToSummary(item.row)}}},[_c("div",{staticClass:"d-flex"},[item.row.avatarUrl?_c("div",[_c("img",{staticClass:"img-container",attrs:{src:item.row.avatarUrl}})]):_c("div",[_vm.newSiteSummary?_c("fc-icon",{attrs:{group:"default",name:"workspace",size:"22"}}):_c("InlineSvg",{staticClass:"width100",attrs:{src:"svgs/spacemanagement/"+_vm.moduleName,iconClass:"icon icon-xlg"}})],1),_c("div",{staticClass:"self-center name bold mL10"},[_vm._v(" "+_vm._s(_vm.getColumnDisplayValue(_vm.mainFieldColumn,item.row)||"---")+" ")])])])]}}],null,!1,999700539)}),_vm._l(_vm.filteredViewColumns,(function(field,index){return _c("el-table-column",{key:index,attrs:{prop:field.name,label:field.displayName,align:"DECIMAL"===(field.field||{}).dataTypeEnum?"right":"left","min-width":"200"},scopedSlots:_vm._u([{key:"default",fn:function(scope){return[_vm.isSpecialHandlingField(field)?_c("keep-alive",[_c((_vm.listComponentsMap[_vm.moduleName]||{}).componentName,{tag:"component",attrs:{field:field,moduleData:scope.row}})],1):_c("div",{staticClass:"table-subheading",class:{"text-right":"DECIMAL"===(field.field||{}).dataTypeEnum}},[_vm._v(" "+_vm._s(_vm.getColumnDisplayValue(field,scope.row)||"---")+" ")])]}}],null,!0)})})),_c("el-table-column",{staticClass:"visibility-visible-actions",attrs:{prop:"",label:"",width:"130"},scopedSlots:_vm._u([{key:"default",fn:function(item){return[_c("div",{staticClass:"text-center"},[_vm.$hasPermission("space:UPDATE")?_c("span",{on:{click:function($event){return _vm.editItem(item.row)}}},[_c("inline-svg",{staticClass:"edit-icon-color visibility-hide-actions",attrs:{src:"svgs/edit",iconClass:"icon icon-sm mR5 icon-edit"}})],1):_vm._e(),_vm.$hasPermission("space:DELETE")?_c("span",{on:{click:function($event){return _vm.invokeDeleteDialog(item.row)}}},[_c("inline-svg",{staticClass:"pointer edit-icon-color visibility-hide-actions mL10",attrs:{src:"svgs/delete",iconClass:"icon icon-sm icon-remove"}})],1):_vm._e()])]}}])})],2)],1),_c("portal",{key:_vm.linkName+"-portalwrap",attrs:{to:_vm.portalName}},[_vm.showMainFieldSearch?_c("el-input",{key:_vm.linkName+"-input",ref:"mainFieldSearchInput",staticClass:"fc-input-full-border2 width-auto mL-auto",staticStyle:{"margin-top":"-10px"},attrs:{"suffix-icon":"el-icon-search",autofocus:""},on:{blur:_vm.hideMainFieldSearch},model:{value:_vm.searchText,callback:function($$v){_vm.searchText=$$v},expression:"searchText"}}):_c("span",{key:_vm.linkName+"-sep1",staticClass:"self-center mL-auto",on:{click:_vm.openMainFieldSearch}},[_c("inline-svg",{key:_vm.linkName+"-search",staticClass:"vertical-middle cursor-pointer",attrs:{src:"svgs/search",iconClass:"icon icon-sm mT5 mR5 search-icon"}})],1),_vm.$validation.isEmpty(_vm.totalCount)||_vm.$validation.isEmpty(_vm.modulesList)?_vm._e():_c("span",{key:_vm.linkName+"-sep2",staticClass:"separator self-center"},[_vm._v("|")]),_vm.$validation.isEmpty(_vm.modulesList)||"floor"===_vm.linkName?_vm._e():_c("sort",{key:_vm.linkName+"-sort",attrs:{config:_vm.sortConfig,sortList:_vm.sortConfigLists},on:{onchange:_vm.updateSort}}),_vm.$validation.isEmpty(_vm.modulesList)||"floor"===_vm.linkName?_vm._e():_c("span",{key:_vm.linkName+"-sep3",staticClass:"separator self-center"},[_vm._v("|")]),_c("pagination",{key:_vm.linkName+"-pagination",staticClass:"self-center",attrs:{currentPage:_vm.page,total:_vm.totalCount,perPage:_vm.perPage},on:{"update:currentPage":function($event){_vm.page=$event},"update:current-page":function($event){_vm.page=$event}}})],1),_c("column-customization",{attrs:{visible:_vm.canShowColumnCustomization,moduleName:_vm.relatedListModuleName,relatedViewDetail:_vm.relatedViewDetail,relatedMetaInfo:_vm.relatedMetaInfo,viewName:_vm.relatedViewName},on:{"update:visible":function($event){_vm.canShowColumnCustomization=$event},refreshRelatedList:_vm.refreshRelatedList}}),_vm.showDialog?_c("DeleteDialog",{attrs:{moduleName:_vm.moduleName,errorMap:_vm.errorMap,id:_vm.deletingRecordId,type:_vm.errorType},on:{onClose:function($event){return _vm.closeDialog()},refresh:function($event){return _vm.refreshRelatedList()}}}):_vm._e()],1)}),SpaceListvue_type_template_id_add6fea4_staticRenderFns=[],regeneratorRuntime=__webpack_require__(150124),asyncToGenerator=__webpack_require__(548534),objectSpread2=__webpack_require__(595082),RelatedListWidget=(__webpack_require__(434284),__webpack_require__(450886),__webpack_require__(169358),__webpack_require__(670560),__webpack_require__(634338),__webpack_require__(77645)),eventBus=__webpack_require__(398011),validation=__webpack_require__(990260),ListNoData=__webpack_require__(956150),ColumnCustomization=__webpack_require__(490966),constant=__webpack_require__(354754),router=__webpack_require__(329435),api=__webpack_require__(32284),DeleteDialog=__webpack_require__(539395),Sort=__webpack_require__(487390),vuex_esm=__webpack_require__(420629),SpaceHelper=__webpack_require__(436289),SpaceListvue_type_script_lang_js={name:"SpaceList",mixins:[SpaceHelper/* default */.Z],extends:RelatedListWidget/* default */.Z,props:["module","displayName","details","portalName","parentModule","linkName","spaceFields"],components:{ListNoData:ListNoData/* default */.Z,ColumnCustomization:ColumnCustomization/* default */.Z,DeleteDialog:DeleteDialog/* default */.Z,Sort:Sort/* default */.Z},data:function(){return{listComponentsMap:{},viewDetailsExcludedModules:[],relatedListModuleName:null,relatedViewDetail:null,relatedMetaInfo:null,relatedViewName:null,canShowColumnCustomization:!1,showDialog:!1,deletingRecordId:null,errorMap:null,errorType:null,sortConfig:{orderBy:{label:"System Created Time",value:"sysCreatedTime"},orderType:"desc"},sortConfigLists:[]}},created:function(){var _this=this;this.init(),"floor"===this.module&&eventBus/* eventBus */.Y.$on("refreshRelatedFloorsList",(function(){_this.refreshRelatedList()})),"space"===this.module&&eventBus/* eventBus */.Y.$on("refreshRelatedSpaceList",(function(){_this.refreshRelatedList()}))},mounted:function(){this.loadFields()},computed:(0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)({},(0,vuex_esm/* mapState */.rn)({viewDetail:function(state){return state.view.currentViewDetail}})),(0,vuex_esm/* mapActions */.nv)({loadViewDetail:"view/loadViewDetail"})),{},{isV3Api:function(){return!0},moduleName:function(){return this.module},moduleDisplayName:function(){return this.displayName},isCustomModule:function(){return!1},filters:function(){var filters,mainField=this.mainField,searchText=this.searchText,details=this.details,moduleName=this.moduleName,linkName=this.linkName,parentModule=this.parentModule,id=details.id;
// search filter
if("building"===parentModule?"floor"===moduleName?filters={building:{operatorId:36,value:["".concat(id)]}}:"independentspace"===linkName?filters={building:{operatorId:36,value:["".concat(id)]},floor:{operatorId:1},space1:{operatorId:1},space2:{operatorId:1},space3:{operatorId:1}}:"allspace"===linkName&&(filters={building:{operatorId:36,value:["".concat(id)]}}):"site"===parentModule&&("allspace"===linkName?filters={site:{operatorId:36,value:["".concat(id)]}}:"independentspace"===linkName&&(filters={site:{operatorId:36,value:["".concat(id)]},building:{operatorId:1},floor:{operatorId:1},space1:{operatorId:1},space2:{operatorId:1},space3:{operatorId:1}})),!(0,validation/* isEmpty */.xb)(mainField)&&!(0,validation/* isEmpty */.xb)(searchText)&&searchText.length>0){var name=mainField.name,field=mainField.field,dataTypeEnum=field.dataTypeEnum,value=[searchText],operatorId=constant/* default */.Z.FILTER_OPERATORID_HASH[dataTypeEnum];filters[name]={operatorId:operatorId,value:value}}return filters},tableHeight:function(){var $refs=this.$refs,height="250px",tableContainer=$refs["relatedListContainer"];if(!(0,validation/* isEmpty */.xb)(tableContainer)){var containerHeight=(tableContainer||{}).scrollHeight;height="".concat(containerHeight,"px")}return height}}),methods:{getViewDetails:function(){this.$store.dispatch("view/loadViewDetail",{viewName:"hidden-all",moduleName:this.moduleName})},loadFields:function(){var _this2=this,moduleName=this.moduleName;this.$util.loadFields(moduleName,!1).then((function(fields){_this2.sortConfigLists=fields.map((function(field){return field.name}))}))},updateSort:function(sorting){var _this3=this;return(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee(){var viewDetail,fields;return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context){while(1)switch(_context.prev=_context.next){case 0:if(viewDetail=_this3.viewDetail,!(0,validation/* isEmpty */.xb)(viewDetail.id)){_context.next=8;break}return fields=viewDetail.fields,_context.next=5,_this3.$store.dispatch("view/customizeColumns",{moduleName:_this3.moduleName,viewName:"hidden-all",fields:fields});case 5:_this3.saveSorting(sorting),_context.next=9;break;case 8:_this3.saveSorting(sorting);case 9:case"end":return _context.stop()}}),_callee)})))()},saveSorting:function(sorting){var _this4=this,moduleName=this.moduleName,sortObj={moduleName:moduleName,viewName:"hidden-all",orderBy:sorting.orderBy,orderType:sorting.orderType,skipDispatch:!0};this.$store.dispatch("view/savesorting",sortObj).then((function(){return _this4.refreshRelatedList()}))},invokeDeleteDialog:function(moduleData){var _this5=this;return(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee2(){var id,moduleName,messageString,_yield$API$fetchRecor,error,value,_yield$API$deleteReco,_error,map;return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context2){while(1)switch(_context2.prev=_context2.next){case 0:return id=moduleData.id,moduleName=_this5.moduleName,messageString="space.sites.delete_".concat(moduleName,"_msg"),_context2.next=5,api/* API */.bl.fetchRecord(_this5.moduleName,{id:id,fetchChildCount:!0},{force:!0});case 5:if(_yield$API$fetchRecor=_context2.sent,error=_yield$API$fetchRecor.error,error){_context2.next=19;break}return _context2.next=10,_this5.$dialog.confirm({title:_this5.$t("space.sites.delete_".concat(moduleName)),message:_this5.$t(messageString),rbDanger:!0,rbLabel:_this5.$t("common._common.delete")});case 10:if(value=_context2.sent,!value){_context2.next=17;break}return _context2.next=14,api/* API */.bl.deleteRecord(moduleName,[id]);case 14:_yield$API$deleteReco=_context2.sent,_error=_yield$API$deleteReco.error,_error?_this5.$message.error(_error):(_this5.$message.success(_this5.$t("space.sites.delete_success")),_this5.refreshRelatedList(),eventBus/* eventBus */.Y.$emit("reloadTree"));case 17:_context2.next=24;break;case 19:_this5.deletingRecordId=id,map=JSON.parse(error.message),_this5.errorMap=map,_this5.errorType=error.code,_this5.showDialog=!0;case 24:case"end":return _context2.stop()}}),_callee2)})))()},closeDialog:function(){this.showDialog=!1},editItem:function(data){var moduleName=this.moduleName;"floor"===moduleName?eventBus/* eventBus */.Y.$emit("openSpaceManagementForm",{visibility:!0,module:"floor",floorObj:data}):eventBus/* eventBus */.Y.$emit("openSpaceManagementForm",{visibility:!0,module:"space",spaceobj:data,site:data.site,building:data.building,floor:data.floor,spaceParent:data.spaceParent})},showColumnCustomization:function(){var _this6=this,moduleName=this.moduleName,viewDetail=this.viewDetail,url="/module/meta?moduleName=".concat(moduleName);this.$http.get(url).then((function(_ref){var data=_ref.data;if(!(0,validation/* isEmpty */.xb)(data)){var metaInfo=data.meta;_this6.$set(_this6,"relatedListModuleName",moduleName),_this6.$set(_this6,"relatedViewDetail",viewDetail),_this6.$set(_this6,"relatedMetaInfo",metaInfo),_this6.$set(_this6,"relatedViewName","hidden-all"),_this6.$set(_this6,"canShowColumnCustomization",!0)}})).catch((function(errMsg){_this6.$message.error(errMsg)}))},findRoute:function(){if((0,router/* isWebTabsEnabled */.tj)()){var tabType=router/* tabTypes */.VF.CUSTOM,config={type:"portfolio"},route=(0,router/* findRouteForTab */.OD)(tabType,{config:config})||{};return(0,validation/* isEmpty */.xb)(route)?null:this.$router.resolve({name:route.name}).href}return"/app/home/portfolio"},routeToSummary:function(record){var siteid=this.$route.params.siteid,id=record.id,parentPath=this.findRoute();parentPath&&siteid&&("building"===this.moduleName?this.$router.push({path:"".concat(parentPath,"/site/").concat(siteid,"/building/").concat(id)}):"floor"===this.moduleName?this.$router.push({path:"".concat(parentPath,"/site/").concat(siteid,"/floor/").concat(id)}):"space"===this.moduleName&&this.$router.push({path:"".concat(parentPath,"/site/").concat(siteid,"/space/").concat(id)}))}}},site_SpaceListvue_type_script_lang_js=SpaceListvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(site_SpaceListvue_type_script_lang_js,SpaceListvue_type_template_id_add6fea4_render,SpaceListvue_type_template_id_add6fea4_staticRenderFns,!1,null,null,null)
/* harmony default export */,SpaceList=component.exports,RelatedTabListComponentvue_type_script_lang_js={props:["details","layoutParams","hideTitleSection","sectionKey","widget","moduleName"],data:function(){return{activeTab:"building"===this.moduleName?"floor":"independentspace"}},components:{List:SpaceList},computed:{tabs:function(){return"building"===this.moduleName?[{moduleName:"floor",linkName:"floor",displayName:"Floors",isActive:"floor"===this.activeTab},{moduleName:"space",linkName:"independentspace",displayName:"Independent Spaces",isActive:"independentspace"===this.activeTab},{moduleName:"space",linkName:"allspace",displayName:"All Spaces",isActive:"allspace"===this.activeTab}]:"site"===this.moduleName?[{moduleName:"space",linkName:"independentspace",displayName:"Independent Spaces",isActive:"independentspace"===this.activeTab},{moduleName:"space",linkName:"allspace",displayName:"All Spaces",isActive:"allspace"===this.activeTab}]:[]},buttonText:function(){return"building"===this.moduleName?"floor"===this.activeTab?this.$t("space.sites.newfloor"):this.$t("space.sites.new_space"):"site"===this.moduleName?this.$t("space.sites.new_space"):""},decommission:function(){return this.$getProperty(this,"details.decommission",!1)}},methods:{openNewForm:function(){var activeTab=this.activeTab,moduleName=this.moduleName;"building"===moduleName?"floor"===activeTab?eventBus/* eventBus */.Y.$emit("openSpaceManagementForm",{isNew:!0,visibility:!0,module:"floor",building:this.details}):["independentspace","allspace"].includes(activeTab)&&eventBus/* eventBus */.Y.$emit("openSpaceManagementForm",{isNew:!0,visibility:!0,module:"space",building:this.details}):"site"===moduleName&&eventBus/* eventBus */.Y.$emit("openSpaceManagementForm",{isNew:!0,visibility:!0,module:"space",site:this.details})}}},site_RelatedTabListComponentvue_type_script_lang_js=RelatedTabListComponentvue_type_script_lang_js,RelatedTabListComponent_component=(0,componentNormalizer/* default */.Z)(site_RelatedTabListComponentvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,RelatedTabListComponent=RelatedTabListComponent_component.exports},
/***/436289:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// EXPORTS
__webpack_require__.d(__webpack_exports__,{Z:function(){/* binding */return SpaceHelper}});
// EXTERNAL MODULE: ./node_modules/core-js/modules/es.object.to-string.js
__webpack_require__(260228),__webpack_require__(947522),__webpack_require__(670560);
// EXTERNAL MODULE: ./node_modules/core-js/modules/web.dom-collections.for-each.js
var render,staticRenderFns,SpaceHelpervue_type_script_lang_js={computed:{getFormattedFile:function(){var _this=this,photos=this.photos,filePreviewList=[];return(photos||[]).forEach((function(photo){filePreviewList.push({contentType:"image",previewUrl:photo.originalUrl,downloadUrl:_this.$helpers.getFileDownloadUrl(photo.photoId)})})),filePreviewList},newSiteSummary:function(){var $helpers=this.$helpers,_ref=$helpers||{},isLicenseEnabled=_ref.isLicenseEnabled;return isLicenseEnabled("NEW_SITE_SUMMARY")&&isLicenseEnabled("WEATHER_INTEGRATION")}}},helpers_SpaceHelpervue_type_script_lang_js=SpaceHelpervue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(helpers_SpaceHelpervue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,SpaceHelper=component.exports}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/30210.js.map