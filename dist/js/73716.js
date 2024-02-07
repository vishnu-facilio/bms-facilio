"use strict";(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[73716,34512,11358],{
/***/544691:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return NewRelatedCardListComponent}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/components/page/widget/site/NewRelatedCardListComponent.vue?vue&type=template&id=6d7314b4&scoped=true
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{ref:"site-buildings-widget",class:_vm.getWidgetClasses()},[_c("portal",{attrs:{to:_vm.widget.key+"-title-section"}},[_c("div",{staticClass:"flex-middle justify-content-space space-white-header "},[_c("div",{staticClass:"f18 bold"},[_vm._v(" "+_vm._s(""+_vm.moduleHeaderName)+" ")]),_c("div",{staticClass:"flex-middle widget-table-header-actions"},[_vm.showMainFieldSearch?_c("el-input",{ref:"mainFieldSearchInput",staticClass:"fc-input-full-border2 width-auto mL-auto",attrs:{"suffix-icon":"el-icon-search",autofocus:""},on:{blur:_vm.hideMainFieldSearch},model:{value:_vm.searchText,callback:function($$v){_vm.searchText=$$v},expression:"searchText"}}):_c("span",{staticClass:"self-center mL-auto",on:{click:_vm.openMainFieldSearch}},[_c("inline-svg",{staticClass:"vertical-middle cursor-pointer",attrs:{src:"svgs/search",iconClass:"icon icon-sm mT5 mR5 search-icon"}})],1),_vm.canShowSorting?_c("span",{staticClass:"separator self-center"},[_vm._v("|")]):_vm._e(),_vm.canShowSorting?_c("sort",{key:"space-sort",attrs:{config:_vm.sortConfig,sortList:_vm.sortConfigLists},on:{onchange:_vm.updateSort}}):_vm._e(),_vm.canShowSorting?_c("span",{staticClass:"separator self-center"},[_vm._v("|")]):_vm._e(),_c("pagination",{staticClass:"self-center",attrs:{currentPage:_vm.page,total:_vm.totalCount,perPage:_vm.perPage},on:{"update:currentPage":function($event){_vm.page=$event},"update:current-page":function($event){_vm.page=$event}}}),_vm.$hasPermission("space:CREATE")?_c("span",{staticClass:"separator self-center"},[_vm._v("|")]):_vm._e(),_vm.$hasPermission("space:CREATE")?_c("div",{staticClass:"f13 bold pointer",on:{click:_vm.openNewForm}},[_c("el-button",{staticClass:"table-header-btn",attrs:{type:"primary",disabled:_vm.decommission}},[_vm._v(" "+_vm._s("building"===_vm.moduleName?_vm.$t("space.sites.newbuilding"):_vm.$t("space.sites.new_space"))+" ")])],1):_vm._e()],1)])]),_c("div",{ref:"component-container",staticClass:"height100"},[_vm.isLoading||_vm.isSearchDataLoading?_c("div",{staticClass:"loading-container d-flex justify-content-center height100"},[_c("spinner",{attrs:{show:_vm.isLoading||_vm.isSearchDataLoading}})],1):_vm.$validation.isEmpty(_vm.modulesList)?_c("div",{staticClass:"height300"},[_c("ListNoData",{attrs:{iconPath:"svgs/spacemanagement/"+_vm.moduleName,moduleDisplayName:_vm.moduleHeaderName,moduleName:_vm.moduleName}})],1):"card"===_vm.type?_c((_vm.componentMap[_vm.moduleName]||{})[_vm.type],{tag:"component",attrs:{details:_vm.details,modulesList:_vm.modulesList,moduleName:_vm.moduleName}}):"list"===_vm.type?_c("div",{ref:"related-table-list",staticClass:"fc-list-view fc-table-td-height related-list-container"},[_c("div",{staticClass:"view-column-chooser",on:{click:_vm.showColumnCustomization}},[_c("img",{staticClass:"column-customization-icon",attrs:{src:__webpack_require__(266707)}})]),_c("el-table",{attrs:{data:_vm.modulesList,fit:!0,height:_vm.getTableHeight(),"row-class-name":"building-row"}},[_vm.$validation.isEmpty(_vm.mainFieldColumn)?_vm._e():_c("el-table-column",{attrs:{label:_vm.mainFieldColumn.displayName,prop:_vm.mainFieldColumn.name,fixed:"","min-width":"200"},scopedSlots:_vm._u([{key:"default",fn:function(item){return[_c("div",{staticClass:"table-subheading",on:{click:function($event){return _vm.routeToSummary(item.row)}}},[_c("div",{staticClass:"d-flex"},[item.row.avatarUrl?_c("div",{staticClass:"flex-middle"},[_c("img",{staticClass:"img-container",attrs:{src:item.row.avatarUrl}})]):_c("div",[_c("fc-icon",{attrs:{group:"default",name:_vm.moduleName,size:"22"}})],1),_c("div",{staticClass:"self-center name bold mL10"},[_vm._v(" "+_vm._s(_vm.getColumnDisplayValue(_vm.mainFieldColumn,item.row)||"---")+" ")])])])]}}],null,!1,3371607670)}),_vm._l(_vm.filteredViewColumns,(function(field,index){return _c("el-table-column",{key:index,attrs:{prop:field.name,label:field.displayName,"min-width":"200"},scopedSlots:_vm._u([{key:"default",fn:function(scope){return[_vm.isSpecialHandlingField(field)?_c("keep-alive",[_c((_vm.listComponentsMap[_vm.moduleName]||{}).componentName,{tag:"component",attrs:{field:field,moduleData:scope.row}})],1):_c("div",{staticClass:"table-subheading"},[_vm._v(" "+_vm._s(_vm.getColumnDisplayValue(field,scope.row)||"---")+" ")])]}}],null,!0)})})),_c("el-table-column",{staticClass:"visibility-visible-actions",attrs:{prop:"",label:"",width:"130"},scopedSlots:_vm._u([{key:"default",fn:function(item){return[_c("div",{staticClass:"text-center"},[_vm.$hasPermission("space:UPDATE")?_c("span",{on:{click:function($event){return _vm.editItem(item.row)}}},[_c("inline-svg",{staticClass:"edit-icon-color visibility-hide-actions",attrs:{src:"svgs/edit",iconClass:"icon icon-sm mR5 icon-edit"}})],1):_vm._e(),_vm.$hasPermission("space:DELETE")?_c("span",{on:{click:function($event){return _vm.invokeDeleteDialog(item.row)}}},[_c("inline-svg",{staticClass:"pointer edit-icon-color visibility-hide-actions mL10",attrs:{src:"svgs/delete",iconClass:"icon icon-sm icon-remove"}})],1):_vm._e()])]}}])})],2)],1):_vm._e()],1),_c("column-customization",{attrs:{visible:_vm.canShowColumnCustomization,moduleName:_vm.relatedListModuleName,columnConfig:_vm.relatedListColumnConfig,relatedViewDetail:_vm.relatedViewDetail,relatedMetaInfo:_vm.relatedMetaInfo,viewName:_vm.relatedViewName},on:{"update:visible":function($event){_vm.canShowColumnCustomization=$event},refreshRelatedList:_vm.refreshRelatedList}}),_vm.showDialog?_c("DeleteDialog",{attrs:{moduleName:_vm.moduleName,errorMap:_vm.errorMap,id:_vm.deletingRecordId,type:_vm.errorType},on:{closeDialog:function($event){return _vm.closeDialog()},refresh:function($event){return _vm.refreshRelatedList()}}}):_vm._e()],1)},staticRenderFns=[],RelatedCardListComponent=__webpack_require__(668677),validation=__webpack_require__(990260),NewRelatedCardListComponentvue_type_script_lang_js={extends:RelatedCardListComponent["default"],computed:{canShowSorting:function(){return!(0,validation/* isEmpty */.xb)(this.totalCount)&&!(0,validation/* isEmpty */.xb)(this.modulesList)},decommission:function(){return this.$getProperty(this,"details.decommission",!1)}},methods:{getTableHeight:function(){
// this.$nextTick(() => {
var height,widget=this.$refs["site-buildings-widget"];return(0,validation/* isEmpty */.xb)(widget)||(height=widget.clientHeight),height;
// })
},getWidgetClasses:function(){return"site-buildings-main site-buildings-lavender pT0 ".concat("card"===this.type&&(0,validation/* isEmpty */.xb)(this.modulesList)?"building-card-component":"building-list-component")}}},site_NewRelatedCardListComponentvue_type_script_lang_js=NewRelatedCardListComponentvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(site_NewRelatedCardListComponentvue_type_script_lang_js,render,staticRenderFns,!1,null,"6d7314b4",null)
/* harmony default export */,NewRelatedCardListComponent=component.exports},
/***/668677:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return RelatedCardListComponent}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/components/page/widget/site/RelatedCardListComponent.vue?vue&type=template&id=17519224
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{class:"site-buildings-main pT0 "+("card"!==_vm.type||_vm.$validation.isEmpty(_vm.modulesList)?"building-list-component":"building-card-component")},[_c("portal",{attrs:{to:_vm.widget.key+"-title-section"}},[_c("div",{staticClass:"flex-middle justify-content-space space-transparent-header mB10"},[_c("div",{staticClass:"fc-grey2 uppercase f11 fwBold"},[_vm._v(" "+_vm._s(""+_vm.moduleHeaderName)+" ")]),_c("div",{staticClass:"flex-middle"},[_vm.showMainFieldSearch?_c("el-input",{ref:"mainFieldSearchInput",staticClass:"fc-input-full-border2 width-auto mL-auto",staticStyle:{"margin-top":"-10px"},attrs:{"suffix-icon":"el-icon-search",autofocus:""},on:{blur:_vm.hideMainFieldSearch},model:{value:_vm.searchText,callback:function($$v){_vm.searchText=$$v},expression:"searchText"}}):_c("span",{staticClass:"self-center mL-auto",on:{click:_vm.openMainFieldSearch}},[_c("inline-svg",{staticClass:"vertical-middle cursor-pointer",attrs:{src:"svgs/search",iconClass:"icon icon-sm mT5 mR5 search-icon"}})],1),_vm.$validation.isEmpty(_vm.totalCount)||_vm.$validation.isEmpty(_vm.modulesList)?_vm._e():_c("span",{staticClass:"separator self-center"},[_vm._v("|")]),_c("sort",{key:"space-sort",attrs:{config:_vm.sortConfig,sortList:_vm.sortConfigLists},on:{onchange:_vm.updateSort}}),_vm.$validation.isEmpty(_vm.totalCount)||_vm.$validation.isEmpty(_vm.modulesList)?_vm._e():_c("span",{staticClass:"separator self-center"},[_vm._v("|")]),_c("pagination",{staticClass:"self-center",attrs:{currentPage:_vm.page,total:_vm.totalCount,perPage:_vm.perPage},on:{"update:currentPage":function($event){_vm.page=$event},"update:current-page":function($event){_vm.page=$event}}}),_vm.showWidgetTypeSwitch?_c("span",{staticClass:"separator self-center"},[_vm._v("|")]):_vm._e(),_vm.showWidgetTypeSwitch?_c("div",{on:{click:function($event){_vm.type="card"}}},[_c("inline-svg",{staticClass:"vertical-middle pointer",attrs:{src:"svgs/list-col",iconClass:"icon icon-md "+("card"===_vm.type?" stroke-grey-active":"stroke-grey-inactive")}})],1):_vm._e(),_vm.showWidgetTypeSwitch?_c("span",{staticClass:"separator self-center"},[_vm._v("|")]):_vm._e(),_vm.showWidgetTypeSwitch?_c("div",{on:{click:function($event){_vm.type="list"}}},[_c("inline-svg",{staticClass:"vertical-middle pointer",attrs:{src:"svgs/list-view",iconClass:"icon icon-md "+("list"===_vm.type?" fill-grey-active":"fill-grey-inactive")}})],1):_vm._e(),_vm.$hasPermission("space:CREATE")?_c("span",{staticClass:"separator self-center"},[_vm._v("|")]):_vm._e(),_vm.$hasPermission("space:CREATE")?_c("div",{staticClass:"fc-pink f13 bold pointer",on:{click:_vm.openNewForm}},[_c("i",{staticClass:"el-icon-plus pR5"}),_vm._v(" "+_vm._s("building"===_vm.moduleName?_vm.$t("space.sites.newbuilding"):_vm.$t("space.sites.new_space"))+" ")]):_vm._e()],1)])]),_c("div",{ref:"component-container"},[_vm.isLoading||_vm.isSearchDataLoading?_c("div",{staticClass:"loading-container d-flex justify-content-center"},[_c("spinner",{attrs:{show:_vm.isLoading||_vm.isSearchDataLoading}})],1):_vm.$validation.isEmpty(_vm.modulesList)?_c("div",{staticClass:"height300"},[_c("ListNoData",{attrs:{iconPath:"svgs/spacemanagement/"+_vm.moduleName,moduleDisplayName:_vm.moduleHeaderName,moduleName:_vm.moduleName}})],1):"card"===_vm.type?_c((_vm.componentMap[_vm.moduleName]||{})[_vm.type],{tag:"component",attrs:{details:_vm.details,modulesList:_vm.modulesList,moduleName:_vm.moduleName}}):"list"===_vm.type?_c("div",{ref:"related-table-list",staticClass:"fc-list-view fc-table-td-height related-list-container"},[_c("div",{staticClass:"view-column-chooser",on:{click:_vm.showColumnCustomization}},[_c("img",{staticClass:"column-customization-icon",attrs:{src:__webpack_require__(266707)}})]),_c("el-table",{staticClass:"related-list-widget-table",attrs:{data:_vm.modulesList,fit:!0,height:_vm.calculateTableHeight()||_vm.tableHeight}},[_vm.$validation.isEmpty(_vm.mainFieldColumn)?_vm._e():_c("el-table-column",{attrs:{label:_vm.mainFieldColumn.displayName,prop:_vm.mainFieldColumn.name,fixed:"","min-width":"200"},scopedSlots:_vm._u([{key:"default",fn:function(item){return[_c("div",{staticClass:"table-subheading",on:{click:function($event){return _vm.routeToSummary(item.row)}}},[_c("div",{staticClass:"d-flex"},[item.row.avatarUrl?_c("div",[_c("img",{staticClass:"img-container",attrs:{src:item.row.avatarUrl}})]):_c("div",[_c("InlineSvg",{staticClass:"width100",attrs:{src:"svgs/spacemanagement/"+_vm.moduleName,iconClass:"icon icon-xlg"}})],1),_c("div",{staticClass:"self-center name bold mL10"},[_vm._v(" "+_vm._s(_vm.getColumnDisplayValue(_vm.mainFieldColumn,item.row)||"---")+" ")])])])]}}],null,!1,2451785221)}),_vm._l(_vm.filteredViewColumns,(function(field,index){return _c("el-table-column",{key:index,attrs:{prop:field.name,label:field.displayName,align:"DECIMAL"===(field.field||{}).dataTypeEnum?"right":"left","min-width":"200"},scopedSlots:_vm._u([{key:"default",fn:function(scope){return[_vm.isSpecialHandlingField(field)?_c("keep-alive",[_c((_vm.listComponentsMap[_vm.moduleName]||{}).componentName,{tag:"component",attrs:{field:field,moduleData:scope.row}})],1):_c("div",{staticClass:"table-subheading",class:{"text-right":"DECIMAL"===(field.field||{}).dataTypeEnum}},[_vm._v(" "+_vm._s(_vm.getColumnDisplayValue(field,scope.row)||"---")+" ")])]}}],null,!0)})})),_c("el-table-column",{staticClass:"visibility-visible-actions",attrs:{prop:"",label:"",width:"130"},scopedSlots:_vm._u([{key:"default",fn:function(item){return[_c("div",{staticClass:"text-center"},[_vm.$hasPermission("space:UPDATE")?_c("span",{on:{click:function($event){return _vm.editItem(item.row)}}},[_c("inline-svg",{staticClass:"edit-icon-color visibility-hide-actions",attrs:{src:"svgs/edit",iconClass:"icon icon-sm mR5 icon-edit"}})],1):_vm._e(),_vm.$hasPermission("space:DELETE")?_c("span",{on:{click:function($event){return _vm.invokeDeleteDialog(item.row)}}},[_c("inline-svg",{staticClass:"pointer edit-icon-color visibility-hide-actions mL10",attrs:{src:"svgs/delete",iconClass:"icon icon-sm icon-remove"}})],1):_vm._e()])]}}])})],2)],1):_vm._e()],1),_c("column-customization",{attrs:{visible:_vm.canShowColumnCustomization,moduleName:_vm.relatedListModuleName,columnConfig:_vm.relatedListColumnConfig,relatedViewDetail:_vm.relatedViewDetail,relatedMetaInfo:_vm.relatedMetaInfo,viewName:_vm.relatedViewName},on:{"update:visible":function($event){_vm.canShowColumnCustomization=$event},refreshRelatedList:_vm.refreshRelatedList}}),_vm.showDialog?_c("DeleteDialog",{attrs:{moduleName:_vm.moduleName,errorMap:_vm.errorMap,id:_vm.deletingRecordId,type:_vm.errorType},on:{onClose:function($event){return _vm.closeDialog()},refresh:function($event){return _vm.refreshRelatedList()}}}):_vm._e()],1)},staticRenderFns=[],regeneratorRuntime=__webpack_require__(150124),asyncToGenerator=__webpack_require__(548534),objectSpread2=__webpack_require__(595082),RelatedListWidget=(__webpack_require__(434284),__webpack_require__(450886),__webpack_require__(169358),__webpack_require__(670560),__webpack_require__(634338),__webpack_require__(77645)),constant=__webpack_require__(354754),BuildingCardsvue_type_template_id_97ea7e5c_render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"d-flex flex-row width100 flex-wrap"},_vm._l(_vm.modulesList,(function(card,index){return _c("div",{key:index,class:[(index+1)%4!==0&&"mR20","building-card-body mB20"]},[_c("div",{staticClass:"d-flex flex-col",on:{click:function($event){return _vm.openSummary(card)}}},[_c("div",{class:[!card.avatarUrl&&"card-bg","height125 d-flex align-center justify-center"]},[card.photoId>0?_c("img",{staticClass:"image width100",attrs:{src:_vm.$helpers.getImagePreviewUrl(card.photoId),height:"125"}}):_c("InlineSvg",{attrs:{src:"svgs/spacemanagement/building",iconClass:"icon space-card"}})],1),_c("div",{staticClass:"p20"},[_c("div",{staticClass:"card-text"},[_vm._v(_vm._s(card.name))]),_c("div",{staticClass:"pT5 card-text2 d-flex flex-row"},[_c("div",{staticClass:"pR5 border-right5"},[_vm._v(" "+_vm._s((card.noOfFloors>0?card.noOfFloors:"---")+" Floors")+" ")]),_c("div",{staticClass:"pL5"},[_vm._v(" "+_vm._s((card.area>0?card.area:"---")+" sq.ft")+" ")])])])])])})),0)},BuildingCardsvue_type_template_id_97ea7e5c_staticRenderFns=[],eventBus=__webpack_require__(398011),router=__webpack_require__(329435),validation=__webpack_require__(990260),BuildingCardsvue_type_script_lang_js={props:["details","modulesList","moduleName"],mounted:function(){eventBus/* eventBus */.Y.$emit("autoResizeContainer",this.moduleName)},methods:{findRoute:function(){if((0,router/* isWebTabsEnabled */.tj)()){var $router=this.$router,tabType=router/* tabTypes */.VF.CUSTOM,config={type:"portfolio"},route=(0,router/* findRouteForTab */.OD)(tabType,{config:config})||{};return(0,validation/* isEmpty */.xb)(route)?null:$router.resolve({name:route.name}).href}return"/app/home/portfolio"},openSummary:function(record){var siteid=this.$route.params.siteid,id=record.id,parentPath=this.findRoute();parentPath&&this.$router.push({path:"".concat(parentPath,"/site/").concat(siteid,"/building/").concat(id)})}}},site_BuildingCardsvue_type_script_lang_js=BuildingCardsvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(site_BuildingCardsvue_type_script_lang_js,BuildingCardsvue_type_template_id_97ea7e5c_render,BuildingCardsvue_type_template_id_97ea7e5c_staticRenderFns,!1,null,null,null)
/* harmony default export */,BuildingCards=component.exports,SpaceCardsvue_type_template_id_e5eea136_render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"d-flex flex-row width100 flex-wrap"},_vm._l(_vm.modulesList,(function(card,index){return _c("div",{key:index,class:[(index+1)%4!==0&&"mR20","mB20 building-card-body"]},[_c("div",{staticClass:"d-flex flex-col",on:{click:function($event){return _vm.openSummary(card)}}},[_c("div",{class:[!card.avatarUrl&&"card-bg","height125 d-flex align-center justify-center"]},[card.photoId>0?_c("img",{staticClass:"image width100",attrs:{src:_vm.$helpers.getImagePreviewUrl(card.photoId),height:"125"}}):_c("InlineSvg",{attrs:{src:"svgs/spacemanagement/space",iconClass:"icon space-card"}})],1),_c("div",{staticClass:"p20"},[_c("div",{staticClass:"card-text"},[_vm._v(_vm._s(card.name))]),_c("div",{staticClass:"pT5 card-text2 d-flex flex-row"},[_c("div",[_vm._v(" "+_vm._s((card.area>0?card.area:"---")+" sq.ft")+" ")])])])])])})),0)},SpaceCardsvue_type_template_id_e5eea136_staticRenderFns=[],SpaceCardsvue_type_script_lang_js={props:["details","modulesList","moduleName"],mounted:function(){eventBus/* eventBus */.Y.$emit("autoResizeContainer",this.moduleName)},methods:{findRoute:function(){if((0,router/* isWebTabsEnabled */.tj)()){var tabType=router/* tabTypes */.VF.CUSTOM,config={type:"portfolio"},route=(0,router/* findRouteForTab */.OD)(tabType,{config:config})||{};return(0,validation/* isEmpty */.xb)(route)?null:this.$router.resolve({name:route.name}).href}return"/app/home/portfolio"},openSummary:function(record){var siteid=this.$route.params.siteid,id=record.id,parentPath=this.findRoute();parentPath&&this.$router.push({path:"".concat(parentPath,"/site/").concat(siteid,"/space/").concat(id)})}}},site_SpaceCardsvue_type_script_lang_js=SpaceCardsvue_type_script_lang_js,SpaceCards_component=(0,componentNormalizer/* default */.Z)(site_SpaceCardsvue_type_script_lang_js,SpaceCardsvue_type_template_id_e5eea136_render,SpaceCardsvue_type_template_id_e5eea136_staticRenderFns,!1,null,null,null)
/* harmony default export */,SpaceCards=SpaceCards_component.exports,ListNoData=__webpack_require__(956150),ColumnCustomization=__webpack_require__(490966),api=__webpack_require__(32284),DeleteDialog=__webpack_require__(539395),vuex_esm=__webpack_require__(420629),Sort=__webpack_require__(487390),customModulesColumnConfig={fixedColumns:["name"],fixedSelectableColumns:["photo"]},RelatedCardListComponentvue_type_script_lang_js={name:"RelatedCardListComponent",props:["details","widget","resizeWidget","calculateDimensions","layoutParams"],extends:RelatedListWidget/* default */.Z,components:{BuildingCards:BuildingCards,ListNoData:ListNoData/* default */.Z,SpaceCards:SpaceCards,ColumnCustomization:ColumnCustomization/* default */.Z,DeleteDialog:DeleteDialog/* default */.Z,Sort:Sort/* default */.Z},data:function(){return{type:"list",componentMap:{building:{list:"BuildingList",card:"BuildingCards"},space:{card:"SpaceCards"}},viewDetailsExcludedModules:[],listComponentsMap:{},relatedListColumnConfig:null,relatedListModuleName:null,relatedViewDetail:null,relatedMetaInfo:null,relatedViewName:null,canShowColumnCustomization:!1,showDialog:!1,deletingRecordId:null,errorMap:null,errorType:null,sortConfig:{orderBy:{label:"System Created Time",value:"sysCreatedTime"},orderType:"desc"},sortConfigLists:[],tableHeight:"550px"}},computed:(0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)({},(0,vuex_esm/* mapState */.rn)({viewDetail:function(state){return state.view.currentViewDetail}})),(0,vuex_esm/* mapActions */.nv)({loadViewDetail:"view/loadViewDetail"})),{},{parentModuleName:function(){return this.$attrs.moduleName},moduleName:function(){var widget=this.widget,_ref=widget||{},relatedList=_ref.relatedList,_ref2=relatedList||{},module=_ref2.module,_ref3=module||{},name=_ref3.name;return name||""},moduleDisplayName:function(){var widget=this.widget,_ref4=widget||{},relatedList=_ref4.relatedList,_ref5=relatedList||{},module=_ref5.module,_ref6=module||{},displayName=_ref6.displayName;return displayName||""},moduleHeaderName:function(){return this.moduleDisplayName+"s"},filters:function(){var filter,mainField=this.mainField,searchText=this.searchText,_this$details=this.details,details=void 0===_this$details?{}:_this$details,moduleName=this.moduleName,parentModuleName=this.parentModuleName,id=details.id;if("site"===parentModuleName?"building"===moduleName?filter={siteId:{operatorId:36,value:["".concat(id)]}}:"space"===moduleName&&(filter={siteId:{operatorId:36,value:["".concat(id)]},building:{operatorId:1},floor:{operatorId:1},space1:{operatorId:1},space2:{operatorId:1},space3:{operatorId:1}}):"floor"===parentModuleName?"space"===moduleName&&(filter={floor:{operatorId:36,value:["".concat(id)]},space1:{operatorId:1},space2:{operatorId:1},space3:{operatorId:1}}):"space"===parentModuleName&&"space"===moduleName&&(filter=details.spaceId1<0?{space1:{operatorId:36,value:["".concat(id)]},space2:{operatorId:1},space3:{operatorId:1}}:details.spaceId2<0?{space2:{operatorId:36,value:["".concat(id)]},space3:{operatorId:1}}:{space3:{operatorId:36,value:["".concat(id)]}}),!(0,validation/* isEmpty */.xb)(mainField)&&!(0,validation/* isEmpty */.xb)(searchText)&&searchText.length>0){var name=mainField.name,field=mainField.field,dataTypeEnum=field.dataTypeEnum,value=[searchText],operatorId=constant/* default */.Z.FILTER_OPERATORID_HASH[dataTypeEnum];filter[name]={operatorId:operatorId,value:value}}return filter},showWidgetTypeSwitch:function(){var totalCount=this.totalCount,searchText=this.searchText;return!(0,validation/* isEmpty */.xb)(searchText)||totalCount>3}}),created:function(){var _this=this;this.init(),eventBus/* eventBus */.Y.$on("autoResizeContainer",(function(moduleName){moduleName===_this.moduleName&&_this.autoResize()})),"building"===this.moduleName&&eventBus/* eventBus */.Y.$on("refreshRelatedBuildingsList",(function(){_this.refreshRelatedList()})),"space"===this.moduleName&&eventBus/* eventBus */.Y.$on("refreshRelatedSpaceList",(function(){_this.refreshRelatedList()}))},mounted:function(){this.loadFields()},methods:{getViewDetails:function(){this.$store.dispatch("view/loadViewDetail",{viewName:"hidden-all",moduleName:this.moduleName})},loadFields:function(){var _this2=this,moduleName=this.moduleName;this.$util.loadFields(moduleName,!1).then((function(fields){_this2.sortConfigLists=fields.map((function(field){return field.name}))}))},updateSort:function(sorting){var _this3=this;return(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee(){var viewDetail,fields;return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context){while(1)switch(_context.prev=_context.next){case 0:if(viewDetail=_this3.viewDetail,!(0,validation/* isEmpty */.xb)(viewDetail.id)){_context.next=8;break}return fields=viewDetail.fields,_context.next=5,_this3.$store.dispatch("view/customizeColumns",{moduleName:_this3.moduleName,viewName:"hidden-all",fields:fields});case 5:_this3.saveSorting(sorting),_context.next=9;break;case 8:_this3.saveSorting(sorting);case 9:case"end":return _context.stop()}}),_callee)})))()},saveSorting:function(sorting){var _this4=this,moduleName=this.moduleName,sortObj={moduleName:moduleName,viewName:"hidden-all",orderBy:sorting.orderBy,orderType:sorting.orderType,skipDispatch:!0};this.$store.dispatch("view/savesorting",sortObj).then((function(){return _this4.refreshRelatedList()}))},autoResize:function(){var _this5=this;this.$nextTick((function(){var container=_this5.$refs["component-container"];if(container){var height=container.scrollHeight;"list"===_this5.type?height+=60:height+=30;var width=container.scrollWidth,_this5$calculateDimen=_this5.calculateDimensions({height:height,width:width}),h=_this5$calculateDimen.h,params={};params=h<=0?{height:height,width:width}:"list"===_this5.type&&h<7?{h:7}:{h:h},_this5.resizeWidget(params)}}))},closeDialog:function(){this.showDialog=!1},openNewForm:function(){var moduleName=this.moduleName,parentModuleName=this.parentModuleName;"site"===parentModuleName?"building"===moduleName?eventBus/* eventBus */.Y.$emit("openSpaceManagementForm",{isNew:!0,visibility:!0,module:"building",site:this.details}):"space"===moduleName&&eventBus/* eventBus */.Y.$emit("openSpaceManagementForm",{isNew:!0,visibility:!0,module:"space",site:this.details}):"floor"===parentModuleName&&"space"===moduleName?eventBus/* eventBus */.Y.$emit("openSpaceManagementForm",{isNew:!0,visibility:!0,module:"space",floor:this.details}):"space"===parentModuleName&&"space"===moduleName&&eventBus/* eventBus */.Y.$emit("openSpaceManagementForm",{isNew:!0,visibility:!0,module:"space",spaceParent:this.details})},editItem:function(data){var moduleName=this.moduleName;if("building"===moduleName)eventBus/* eventBus */.Y.$emit("openSpaceManagementForm",{visibility:!0,module:"building",buildingObj:data});else if("space"===moduleName){var spaceParent=data.spaceParent?data.spaceParent:data.space4?data.space4:data.space3?data.space3:data.space2?data.space2:data.space1?data.space1:{};eventBus/* eventBus */.Y.$emit("openSpaceManagementForm",{visibility:!0,module:"space",spaceobj:data,floor:data.floor,spaceParent:spaceParent})}},invokeDeleteDialog:function(moduleData){var _this6=this;return(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee2(){var id,moduleName,messageString,_yield$API$fetchRecor,error,value,_yield$API$deleteReco,_error,map;return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context2){while(1)switch(_context2.prev=_context2.next){case 0:return id=moduleData.id,moduleName=_this6.moduleName,messageString="space.sites.delete_".concat(moduleName,"_msg"),_context2.next=5,api/* API */.bl.fetchRecord(_this6.moduleName,{id:id,fetchChildCount:!0},{force:!0});case 5:if(_yield$API$fetchRecor=_context2.sent,error=_yield$API$fetchRecor.error,error){_context2.next=19;break}return _context2.next=10,_this6.$dialog.confirm({title:_this6.$t("space.sites.delete_".concat(moduleName)),message:_this6.$t(messageString),rbDanger:!0,rbLabel:_this6.$t("common._common.delete")});case 10:if(value=_context2.sent,!value){_context2.next=17;break}return _context2.next=14,api/* API */.bl.deleteRecord(moduleName,[id]);case 14:_yield$API$deleteReco=_context2.sent,_error=_yield$API$deleteReco.error,_error?_this6.$message.error(_error):(_this6.$message.success(_this6.$t("space.sites.delete_success")),_this6.refreshRelatedList(),eventBus/* eventBus */.Y.$emit("reloadTree"));case 17:_context2.next=24;break;case 19:_this6.deletingRecordId=id,map=JSON.parse(error.message),_this6.errorMap=map,_this6.errorType=error.code,_this6.showDialog=!0;case 24:case"end":return _context2.stop()}}),_callee2)})))()},calculateTableHeight:function(){var _this7=this;this.$nextTick((function(){var $refs=_this7.$refs,height="250px",tableContainer=$refs["related-table-list"];if(!(0,validation/* isEmpty */.xb)(tableContainer)){var containerHeight=(tableContainer||{}).scrollHeight-90;height="".concat(containerHeight,"px")}return _this7.autoResize(),height}))},findRoute:function(){if((0,router/* isWebTabsEnabled */.tj)()){var $router=this.$router,tabType=router/* tabTypes */.VF.CUSTOM,config={type:"portfolio"},route=(0,router/* findRouteForTab */.OD)(tabType,{config:config})||{};return(0,validation/* isEmpty */.xb)(route)?null:$router.resolve({name:route.name}).href}return"/app/home/portfolio"},routeToSummary:function(record){var siteid=this.$route.params.siteid,id=record.id,parentPath=this.findRoute();parentPath&&siteid&&("building"===this.moduleName?this.$router.push({path:"".concat(parentPath,"/site/").concat(siteid,"/building/").concat(id)}):"floor"===this.moduleName?this.$router.push({path:"".concat(parentPath,"/site/").concat(siteid,"/floor/").concat(id)}):"space"===this.moduleName&&this.$router.push({path:"".concat(parentPath,"/site/").concat(siteid,"/space/").concat(id)}))},showColumnCustomization:function(){var _this8=this,moduleName=this.moduleName,viewDetail=this.viewDetail,url="/module/meta?moduleName=".concat(moduleName),columnConfig=customModulesColumnConfig;this.$http.get(url).then((function(_ref7){var data=_ref7.data;if(!(0,validation/* isEmpty */.xb)(data)){var metaInfo=data.meta;_this8.$set(_this8,"relatedListColumnConfig",columnConfig),_this8.$set(_this8,"relatedListModuleName",moduleName),_this8.$set(_this8,"relatedViewDetail",viewDetail),_this8.$set(_this8,"relatedMetaInfo",metaInfo),_this8.$set(_this8,"relatedViewName","hidden-all"),_this8.$set(_this8,"canShowColumnCustomization",!0)}})).catch((function(errMsg){_this8.$message.error(errMsg)}))}}},site_RelatedCardListComponentvue_type_script_lang_js=RelatedCardListComponentvue_type_script_lang_js,RelatedCardListComponent_component=(0,componentNormalizer/* default */.Z)(site_RelatedCardListComponentvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,RelatedCardListComponent=RelatedCardListComponent_component.exports}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/73716.js.map