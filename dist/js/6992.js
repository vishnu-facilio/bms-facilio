"use strict";(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[6992],{
/***/372587:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// EXPORTS
__webpack_require__.d(__webpack_exports__,{Z:function(){/* binding */return SpaceAssetChooser}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/components/SpaceAssetChooser.vue?vue&type=template&id=daefb110
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("el-dialog",{attrs:{visible:_vm.visibility,fullscreen:!1,open:"top","custom-class":" fc-dialog-up spaceassetchooser setup-dialog60","append-to-body":!0,"before-close":_vm.handleclose},on:{"update:visible":function($event){_vm.visibility=$event}}},[_c("div",{attrs:{id:"spaceassetchooser"}},[_c("div",{staticStyle:{height:"600px","max-height":"600px"}},[_vm.hideSidebar?_vm._e():_c("div",{staticClass:"sidebar",staticStyle:{height:"100%",width:"33%",display:"inline-block"}},[_c("div",{staticClass:"pT30",staticStyle:{"margin-left":"30px","z-index":"100"}},[_c("div",{staticClass:"flLeft",staticStyle:{"padding-right":"30px"},attrs:{disabled:"asset"===_vm.picktype},on:{click:function($event){"asset"!==_vm.picktype&&_vm.openSpace()}}},[_c("div",{staticClass:"flLeft",staticStyle:{"padding-top":"1px"}},[_c("i",{staticClass:"el-icon-circle-check",class:_vm.isAsset?"normalspace":"selectedspace"})]),_c("div",{staticClass:"txtstyle pointer flLeft",staticStyle:{"padding-left":"5px"}},[_vm._v(" "+_vm._s(_vm.$t("common.space_asset_chooser.space"))+" ")]),_c("div",{staticClass:"clearboth"})]),_c("div",{staticClass:"flLeft",attrs:{disabled:"space"===_vm.picktype},on:{click:function($event){"space"!==_vm.picktype&&_vm.openAsset()}}},[_c("div",{staticClass:"flLeft",staticStyle:{"padding-top":"1px"}},[_c("i",{staticClass:"el-icon-circle-check",class:_vm.isAsset?"selectedspace":"normalspace"})]),_c("div",{staticClass:"flLeft"},[_c("span",{staticClass:"txtstyle pointer",staticStyle:{"padding-left":"5px"}},[_vm._v(_vm._s(_vm.$t("common.space_asset_chooser.asset")))])]),_c("div",{staticClass:"clearboth"})]),_c("div",{staticClass:"clearboth"})]),_c("div",{staticStyle:{"margin-left":"30px"}},[_c("div",{staticClass:"space-heading pT30 pB5"},[_vm._v(" "+_vm._s(_vm.$t("common.space_asset_chooser.by_location"))+" ")]),_vm._l(_vm.resourceConfig,(function(field){return _c("div",{key:field.spaceType,staticClass:"pB15"},[_c("el-select",{attrs:{filterable:"",clearable:"",placeholder:field.placeHolder,"data-test-selector":""+field.placeHolder,disabled:4===field.spaceType&&!_vm.isAsset||field.disabled},on:{change:function($event){return _vm.onLocationSelected(field)}},model:{value:field.value,callback:function($$v){_vm.$set(field,"value",$$v)},expression:"field.value"}},_vm._l(field.options,(function(option){return _c("el-option",{key:option.id,attrs:{label:option.name,value:option.id,"data-test-selector":field.placeHolder+"_"+option.name}})})),1)],1)})),_c("div",{staticClass:"pT30"},[_c("div",{staticClass:"space-heading pT15 pB5"},[_vm._v(" "+_vm._s(_vm.$t("common.space_asset_chooser.by_category"))+" ")]),_c("div",[_vm.isAsset?_c("el-select",{attrs:{filterable:"",clearable:"",placeholder:_vm.assetCategory.placeHolder,"data-test-selector":""+_vm.$t("common.space_asset_chooser.by_category"),disabled:!_vm.isAsset},on:{change:_vm.onCategorySelected},model:{value:_vm.assetCategory.value,callback:function($$v){_vm.$set(_vm.assetCategory,"value",$$v)},expression:"assetCategory.value"}},_vm._l(_vm.assetCategory.options,(function(label,value){return _c("el-option",{key:value,attrs:{label:label,value:value,"data-test-selector":_vm.$t("common.space_asset_chooser.by_category")+"_"+label}})})),1):_c("el-select",{staticClass:"inline asset-category fc-input-full-border2",attrs:{filterable:"",clearable:"",placeholder:_vm.spaceCategory.placeHolder},on:{change:_vm.onSpaceCategorySelected},model:{value:_vm.spaceCategory.value,callback:function($$v){_vm.$set(_vm.spaceCategory,"value",$$v)},expression:"spaceCategory.value"}},_vm._l(_vm.spaceCategory.options,(function(label,value){return _c("el-option",{key:value,attrs:{label:label,value:value}})})),1)],1)])],2)]),_c("div",{staticClass:"pull-right heightlist",style:{width:_vm.hideSidebar?"100%":"67%"}},[_c("div",{staticStyle:{"padding-left":"20px","padding-right":"20px"}},[_vm.showQuickSearch?_c("div",{staticStyle:{display:"inline-block",width:"100%"}},[_c("div",{staticClass:"col-6 fc-list-search"},[_c("div",{staticClass:"fc-list-search-wrapper relative chooser-search"},[_c("svg",{staticClass:"search-icon",attrs:{xmlns:"http://www.w3.org/2000/svg",width:"32",height:"32",viewBox:"0 0 32 32"}},[_c("title",[_vm._v(_vm._s(_vm.$t("common._common.search")))]),_c("path",{attrs:{d:"M21.487 22.927l7.037 7.037c.473.473 1.238.475 1.71.003s.47-1.237-.003-1.71l-7.037-7.037c3.96-4.82 3.675-11.967-.846-16.487C17.539-.076 9.757-.092 4.966 4.699S.191 17.272 5 22.081c4.52 4.52 11.667 4.805 16.487.846zM6.679 6.413c3.848-3.848 10.099-3.836 13.962.027s3.875 10.114.027 13.962c-3.848 3.848-10.099 3.836-13.962-.027S2.831 10.261 6.679 6.413z"}})]),_c("input",{directives:[{name:"model",rawName:"v-model",value:_vm.quickSearchQuery,expression:"quickSearchQuery"}],ref:"quickSearchQuery",staticClass:"quick-search-input",attrs:{autofocus:"autofocus",type:"text",placeholder:"Search"},domProps:{value:_vm.quickSearchQuery},on:{keyup:function($event){return!$event.type.indexOf("key")&&_vm._k($event.keyCode,"enter",13,$event.key,"Enter")?null:_vm.loadResourceData.apply(null,arguments)},input:function($event){$event.target.composing||(_vm.quickSearchQuery=$event.target.value)}}}),_c("svg",{staticClass:"close-icon",attrs:{xmlns:"http://www.w3.org/2000/svg",width:"32",height:"32",viewBox:"0 0 32 32","aria-hidden":"true"},on:{click:_vm.toggleQuickSearch}},[_c("title",[_vm._v("close")]),_c("path",{attrs:{d:"M17.992 16l8.796-8.796a1.409 1.409 0 0 0-1.992-1.992L16 14.008 7.204 5.212a1.409 1.409 0 0 0-1.992 1.992L14.008 16l-8.796 8.796a1.409 1.409 0 0 0 1.992 1.992L16 17.992l8.796 8.796a1.409 1.409 0 0 0 1.992-1.992L17.992 16z"}})])])])]):_c("div",{staticClass:"flex-middle justify-content-space"},[_c("span",{staticClass:"spaceheading",staticStyle:{display:"inline-block"}},[_vm._v(_vm._s(_vm.isAsset?this.$t("common.space_asset_chooser.asset_list"):this.$t("common.space_asset_chooser.space_list")))]),_c("span",{staticClass:"pull-right pointer"},[_c("span",{on:{click:_vm.toggleQuickSearch}},[_c("i",{staticClass:"el-icon-search"})])])])]),_c("div",{directives:[{name:"infinite-scroll",rawName:"v-infinite-scroll",value:_vm.loadMores,expression:"loadMores"}],staticClass:"scroll",attrs:{"infinite-scroll-distance":"10","infinite-scroll-immediate-check":"true"}},[_c("v-infinite-scroll",{staticStyle:{"max-height":"500px",height:"500px","overflow-y":"scroll"},attrs:{loading:_vm.loading,offset:20},on:{bottom:_vm.nextPage}},[_c("table",{staticClass:" pT15 fc-list-view-table"},[_c("thead",[_c("tr",{staticClass:"tablerow pT30",staticStyle:{"background-color":"#ffffff",padding:"15px"}},[_c("td",{staticClass:"headerrow width30"},[_vm._v(" #"+_vm._s(_vm.$t("common._common.id"))+" ")]),_c("td",{staticClass:"headerrow width30"},[_vm._v(" "+_vm._s(_vm.$t("common.space_asset_chooser.name"))+" ")]),_vm.hideSidebar?_vm._e():_c("td",{staticClass:"headerrow width30"},[_vm._v(" "+_vm._s(_vm.$t("common.space_asset_chooser.category"))+" ")]),_c("td"),_c("td")])]),_c("tbody",[_vm._l(_vm.spaceList,(function(space){return _vm.isAsset?_vm._e():_c("tr",{key:space.id,staticClass:"tablerow",class:{selectedrow:_vm.selectedObj===space},attrs:{"data-test-selector":""+space.name},on:{click:function($event){return _vm.select(space,1)}}},[_c("td",{staticClass:"contentrow width30"},[_vm._v(" #"+_vm._s(1==space.spaceType?"SI"+space.id:2==space.spaceType?"BU"+space.id:3==space.spaceType?"FL"+space.id:4==space.spaceType?"SP"+space.id:space.id)+" ")]),_c("td",{staticClass:"contentrow width30"},[_vm._v(_vm._s(space.name))]),_c("td",{staticClass:"contentrow width30"},[_vm._v(_vm._s(space.spaceTypeVal))]),_c("td"),_c("td",[_c("i",{staticClass:"el-icon-check",staticStyle:{color:"#39b2c2","padding-left":"20px"},style:{visibility:_vm.selectedObj===space?"visible":"hidden"}})])])})),_vm._l(_vm.assetList,(function(asset){return _vm.isAsset?_c("tr",{key:"asset_"+asset.id,staticClass:"tablerow",class:{selectedrow:_vm.selectedObj===asset},attrs:{"data-test-selector":""+asset.name},on:{click:function($event){return _vm.select(asset,2)}}},[_c("td",{staticClass:"contentrow width30"},[_vm._v("#"+_vm._s(asset.id))]),_c("td",{staticClass:"contentrow width30"},[_vm._v(_vm._s(asset.name))]),_vm.hideSidebar?_vm._e():_c("td",{staticClass:"contentrow width30"},[_vm._v(" "+_vm._s(asset.category?_vm.getAssetCategory(asset.category.id).displayName:"---")+" ")]),_c("td"),_c("td",[_c("i",{staticClass:"el-icon-check",staticStyle:{color:"#39b2c2","padding-left":"20px"},style:{visibility:_vm.selectedObj===asset?"visible":"hidden"}})])]):_vm._e()})),_vm.spaceList.length<1&&_vm.assetList.length<1?_c("tr",{staticStyle:{background:"none",border:"none","margin-top":"20px"}},[_c("td",{staticClass:"text-center label-txt-black",staticStyle:{"border-bottom":"none","font-size":"14px","padding-top":"40px",cursor:"auto"},attrs:{colspan:"100%"}},[_vm._v(" No "+_vm._s(_vm.isAsset?"Asset":"Space")+" to display! ")])]):_vm._e()],2)])])],1),_c("div",{staticClass:"space-asset-footer"},[_c("div",{staticClass:"space-asset-footer-align"},[_c("el-button",{staticStyle:{"font-size":"13px","font-weight":"bold",border:"2px solid #cde2e5",color:"#8eb1b6","letter-spacing":"0.5px",padding:"8px",width:"100px","text-transform":"uppercase"},attrs:{"data-test-selector":"SpaceAsset_cancel"},on:{click:_vm.handleclose}},[_vm._v(_vm._s(_vm.$t("common._common.cancel")))]),_c("el-button",{staticStyle:{"font-size":"13px","font-weight":"bold","letter-spacing":"0.5px","background-color":"#39b2c2","border-color":"#39b2c2",padding:"8px","text-transform":"uppercase"},attrs:{"data-test-selector":"SpaceAsset_save",type:"primary"},on:{click:_vm.associate}},[_vm._v(_vm._s(_vm.$t("common._common.select")))])],1)])])]),_c("div",{staticStyle:{position:"absolute",top:"40%",left:"55%","z-index":"5"}},[_c("spinner",{attrs:{show:_vm.resourceLoading,size:"70"}})],1)])])},staticRenderFns=[],objectSpread2=__webpack_require__(595082),vue_infinite_scroll=__webpack_require__(49026),vue_infinite_scroll_default=__webpack_require__.n(vue_infinite_scroll),ResourceMixin=__webpack_require__(22637),v_infinite_scroll=__webpack_require__(561364),v_infinite_scroll_default=__webpack_require__.n(v_infinite_scroll),Spinner=__webpack_require__(604947),vuex_esm=__webpack_require__(420629),SpaceAssetChooservue_type_script_lang_js={mixins:[ResourceMixin/* default */.Z],props:["visibility","appendToBody","query","picktype","hideSidebar","isService"],directives:{infiniteScroll:vue_infinite_scroll_default()},components:{Spinner:Spinner/* default */.Z,VInfiniteScroll:v_infinite_scroll_default()},data:function(){return{fetchingMore:!1,selected:!1,selectedObj:{},showQuickSearch:!1}},created:function(){this.$store.dispatch("loadAssetCategory"),this.$store.dispatch("loadSpaceCategory")},mounted:function(){
// quickSearchQuery
"asset"===this.picktype&&(this.isAsset=!0),this.initResourceData()},computed:(0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)({},(0,vuex_esm/* mapGetters */.Se)(["getAssetCategory"])),{},{scrollDisabled:function(){return!1}}),component:{Spinner:Spinner/* default */.Z},watch:{visibility:function(val){var _this=this;val&&!this.query?(this.reInit(),this.hideSidebar&&(this.showQuickSearch=!0,this.$nextTick((function(){_this.$refs.quickSearchQuery&&_this.$refs.quickSearchQuery.focus()})))):val||!this.query||this.selectedObj&&this.selectedObj.id||this.$emit("associate",{},this.isAsset?"asset":"space")},query:function(val){var _this2=this;this.quickSearchQuery=val,val&&this.$refs.quickSearchQuery&&(this.selectedObj={},this.showQuickSearch=!0,this.$nextTick((function(){_this2.$refs.quickSearchQuery.focus()})),this.loadResourceData())}},methods:{loadMores:function(){this.fetchingMore=!0,this.openAsset(!0)},reInit:function(){this.showQuickSearch=!1,this.quickSearchQuery=null,this.paging.page=1,this.checkAndInitResourceData()},handleclose:function(){this.$emit("update:visibility",!1)},openSpace:function(){this.isAsset=!1,this.paging.page=1,this.loadSpace()},openAsset:function(isLoad){this.isAsset=!0,this.paging.page=1,this.loadAsset(isLoad)},select:function(obj,type){this.selectedObj=obj},associate:function(){this.$emit("associate",this.selectedObj,this.isAsset?"asset":"space")},toggleQuickSearch:function(){var _this3=this;this.showQuickSearch=!this.showQuickSearch,this.showQuickSearch?this.$nextTick((function(){_this3.$refs.quickSearchQuery.focus()})):(this.quickSearchQuery=null,this.loadResourceData())}}},components_SpaceAssetChooservue_type_script_lang_js=SpaceAssetChooservue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(components_SpaceAssetChooservue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,SpaceAssetChooser=component.exports},
/***/604947:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// EXPORTS
__webpack_require__.d(__webpack_exports__,{Z:function(){/* binding */return Spinner}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/components/Spinner.vue?vue&type=template&id=06a81286&scoped=true
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("transition",[_c("svg",{directives:[{name:"show",rawName:"v-show",value:_vm.show,expression:"show"}],staticClass:"spinner",class:{show:_vm.show},attrs:{width:_vm.spinnerSize,height:_vm.spinnerSize,viewBox:"0 0 44 44"}},[_c("circle",{staticClass:"path",style:{stroke:_vm.strokeColor},attrs:{fill:"none","stroke-width":"4","stroke-linecap":"round",cx:"22",cy:"22",r:"20"}})])])},staticRenderFns=[],Spinnervue_type_script_lang_js={name:"spinner",props:["show","size","colour"],computed:{spinnerSize:function(){return this.size?this.size+"px":"50px"},strokeColor:function(){return this.colour?this.colour:"#fd4b92"}}},components_Spinnervue_type_script_lang_js=Spinnervue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(components_Spinnervue_type_script_lang_js,render,staticRenderFns,!1,null,"06a81286",null)
/* harmony default export */,Spinner=component.exports},
/***/406992:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return Graphics}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/pages/energy/graphics/Graphics.vue?vue&type=template&id=f077a188&scoped=true
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("el-row",{staticClass:"reports-layout height100 graphics-page"},[_c("el-col",{attrs:{span:4}},[_c("div",{staticClass:"reports-sidebar"},[_c("div",{staticClass:"reports-sidebar-header"},[_c("div",{staticClass:"fc-black-13",attrs:{size:"mini","split-button":"",type:"text"}},[_vm._v(" "+_vm._s(_vm.$t("common.header.all_graphics"))+" ")]),_c("div",{staticClass:"mR10"},[_c("button",{staticClass:"sh-button button-add shadow-1",staticStyle:{padding:"6px 9px"},on:{click:function($event){_vm.newGraphicsVisibility=!0}}},[_c("i",{staticClass:"el-icon-plus",staticStyle:{"font-weight":"700","font-size":"12px"}})])])]),_vm.graphicsFolderList&&_vm.graphicsFolderList.length>0?_c("div",{staticClass:"report-sidebar-scroll"},[_c("div",{staticClass:"folder-scroll-container"},_vm._l(_vm.graphicsFolderList,(function(folder,index){return _c("div",{key:index,staticClass:"folder-container"},[_c("div",{staticClass:"rfolder-name uppercase report-new-folder",on:{click:function($event){return _vm.expand(folder)}}},[_c("div",{staticClass:"rfolder-icon fL"},[folder.expand?_c("i",{staticClass:"el-icon-arrow-down"}):_c("i",{staticClass:"el-icon-arrow-right"})]),folder.id!==_vm.editFolderId?_c("div",{staticClass:"mL5"},[_vm._v(" "+_vm._s(folder.name)+" ")]):_c("input",{directives:[{name:"model",rawName:"v-model",value:_vm.folderState[folder.id].newName,expression:"folderState[folder.id].newName"}],ref:"FolderName",refInFor:!0,domProps:{value:_vm.folderState[folder.id].newName},on:{keyup:function($event){return!$event.type.indexOf("key")&&_vm._k($event.keyCode,"enter",13,$event.key,"Enter")?null:_vm.editFolderName(folder)},blur:function($event){return _vm.editFolderName(folder)},input:function($event){$event.target.composing||_vm.$set(_vm.folderState[folder.id],"newName",$event.target.value)}}})]),_c("div",{staticClass:"edit-icon-set"},[_c("i",{staticClass:"el-icon-edit icon-display pointer",on:{click:function($event){return _vm.toggleEditFolder(folder)}}}),_c("i",{staticClass:"el-icon-delete icon-display pointer",on:{click:function($event){return _vm.deleteReportFolder(folder)}}})]),_c("div",{directives:[{name:"show",rawName:"v-show",value:folder.expand,expression:"folder.expand"}],staticClass:"rfolder-children"},[folder.graphics&&!folder.graphics.length?_c("div",{staticClass:"rempty"},[_vm._v(" "+_vm._s(_vm.$t("common.products.no_graphics"))+" ")]):_vm._e(),_vm._l(folder.graphics,(function(graphic,gId){return _c("router-link",{key:gId,attrs:{tag:"div",to:"/app/em/graphics/view/"+graphic.id}},[_vm._v(_vm._s(graphic.name))])}))],2)])})),0)]):_vm.loading||_vm.graphicsFolderList&&0!==_vm.graphicsFolderList.length?_vm._e():_c("div",{staticClass:"no-graphics-folder"},[_vm._v(" "+_vm._s(_vm.$T("common.products.no_graphics_folder_avialable"))+" ")])])]),_c("el-col",{staticClass:"reports-summary height100",attrs:{span:20}},[_vm.graphicsId?_c("router-view",{key:_vm.graphicsId,on:{refresh:_vm.loadGraphicsFolders,deleted:_vm.loadGraphicsFolders}}):_vm._e()],1),_vm.newGraphicsVisibility?_c("new-graphics-form",{attrs:{formVisibility:_vm.newGraphicsVisibility},on:{"update:formVisibility":function($event){_vm.newGraphicsVisibility=$event},"update:form-visibility":function($event){_vm.newGraphicsVisibility=$event},saved:_vm.loadGraphicsFolders}}):_vm._e(),_vm.editGraphicsObj?_c("f-graphics-builder",{attrs:{graphicsContext:_vm.editGraphicsObj},on:{close:_vm.editorClose}}):_vm._e()],1)},staticRenderFns=[],createForOfIteratorHelper=__webpack_require__(566347),NewGraphicsForm=(__webpack_require__(260228),__webpack_require__(821694),__webpack_require__(76265),__webpack_require__(434284),__webpack_require__(339772),__webpack_require__(162506),__webpack_require__(670560),__webpack_require__(705351)),Graphicsvue_type_script_lang_js={components:{NewGraphicsForm:NewGraphicsForm/* default */.Z,FGraphicsBuilder:function(){return Promise.all(/* import() */[__webpack_require__.e(93208),__webpack_require__.e(50232),__webpack_require__.e(93765),__webpack_require__.e(95953),__webpack_require__.e(35309),__webpack_require__.e(18778),__webpack_require__.e(12303),__webpack_require__.e(84290),__webpack_require__.e(31721),__webpack_require__.e(64418),__webpack_require__.e(5603),__webpack_require__.e(57432),__webpack_require__.e(435),__webpack_require__.e(72230),__webpack_require__.e(94255),__webpack_require__.e(67683),__webpack_require__.e(44178),__webpack_require__.e(91132),__webpack_require__.e(20280),__webpack_require__.e(78256),__webpack_require__.e(22200),__webpack_require__.e(49081),__webpack_require__.e(89472),__webpack_require__.e(12085),__webpack_require__.e(55754),__webpack_require__.e(86494),__webpack_require__.e(3037),__webpack_require__.e(49908),__webpack_require__.e(22061),__webpack_require__.e(31602),__webpack_require__.e(73609),__webpack_require__.e(99751),__webpack_require__.e(773),__webpack_require__.e(65545),__webpack_require__.e(59850),__webpack_require__.e(42208),__webpack_require__.e(33015),__webpack_require__.e(2950),__webpack_require__.e(94925),__webpack_require__.e(23943),__webpack_require__.e(11801),__webpack_require__.e(64687),__webpack_require__.e(221)]).then(__webpack_require__.bind(__webpack_require__,264687))}},title:function(){return"Graphics"},data:function(){return{loading:!0,graphicsList:null,graphicsFolderList:null,newGraphicsVisibility:!1,editGraphicsObj:null,editFolderId:null,folderState:{}}},computed:{graphicsId:function(){return this.$route.params.graphicsid?parseInt(this.$route.params.graphicsid):null}},mounted:function(){this.loadGraphicsFolders()},methods:{loadGraphicsFolders:function(){var _this=this;this.loading=!0,this.$http.post("/v2/graphicsFolder/list",{showChildrenGraphics:!0}).then((function(response){if(0===response.data.responseCode){for(var key in _this.graphicsFolderList=response.data.result.graphicsFolders,_this.graphicsFolderList){_this.$set(_this.graphicsFolderList[key],"expand",!1);var folder=_this.graphicsFolderList[key],folderState={};folderState["name"]=folder.name,folderState["newName"]=folder.name,_this.folderState[folder.id]=folderState}_this.openGraphics()}else _this.$message.error(response.data.message);_this.loading=!1})).catch((function(error){_this.$message.error(error),_this.loading=!1}))},deleteReportFolder:function(folder){var _this2=this;if(folder.graphics.length>0)this.$message({message:this.$t("common.wo_report.cannot_delete_graphics"),type:"error"});else{var promptObj={title:this.$t("common.wo_report.delete_folder"),message:this.$t("common.wo_report.delete_folder_msg"),rbDanger:!0,rbLabel:this.$t("common._common.delete")};this.$dialog.confirm(promptObj).then((function(value){value&&_this2.$http.post("/v2/graphicsFolder/delete",{recordId:folder.id}).then((function(response){if(1===response.data.responseCode)
// delete failed
_this2.$message({message:_this2.$t("common.wo_report.cannot_delete_graphics"),type:"error"});else{var index=_this2.graphicsFolderList.findIndex((function(obj){return obj.id===folder.id}));_this2.graphicsFolderList.splice(index,1),_this2.$message({message:_this2.$t("common.wo_report.folder_delete_success"),type:"success"})}})).catch((function(error){_this2.$message({message:_this2.$t("common.wo_report.cannot_delete_graphics"),type:"error"})}))}))}},editFolderName:function(folder){var _this3=this,newName=this.folderState[folder.id].newName;if(""!==newName&&null!==newName){var gFolder={};gFolder["name"]=newName,gFolder["id"]=folder.id,this.editFolderId=null,this.$http.post("/v2/graphicsFolder/update",{graphicsFolder:gFolder}).then((function(response){folder.name=response.data.result.graphicsFolder.name,_this3.folderState[folder.id].name=folder.name,_this3.folderState[folder.id].newName=folder.name})).catch((function(){_this3.$message({message:_this3.$t("common.wo_report.cannot_rename_folder"),type:"error"}),folder.name=_this3.folderState[folder.id].name}))}else this.$message({message:this.$t("common.wo_report.foldername_cannot_empty"),type:"error"})},openGraphics:function(){if(this.$route.params.graphicsid){if(this.graphicsId){var _step2,_iterator2=(0,createForOfIteratorHelper/* default */.Z)(this.graphicsFolderList);try{for(_iterator2.s();!(_step2=_iterator2.n()).done;){var _step3,_folder=_step2.value,_iterator3=(0,createForOfIteratorHelper/* default */.Z)(_folder.graphics);try{for(_iterator3.s();!(_step3=_iterator3.n()).done;){var grap=_step3.value;if(grap.id===this.graphicsId)return void(_folder.expand=!0)}}catch(err){_iterator3.e(err)}finally{_iterator3.f()}}}catch(err){_iterator2.e(err)}finally{_iterator2.f()}}}else{var _step,_iterator=(0,createForOfIteratorHelper/* default */.Z)(this.graphicsFolderList);try{for(_iterator.s();!(_step=_iterator.n()).done;){var folder=_step.value;if(folder.graphics.length>0)return this.$router.push({path:"/app/em/graphics/view/"+folder.graphics[0].id}),void(folder.expand=!0)}}catch(err){_iterator.e(err)}finally{_iterator.f()}}},toggleEditFolder:function(folder){var _this4=this;this.editFolderId=folder.id,this.$nextTick((function(){Array.isArray(_this4.$refs["FolderName"])?_this4.$refs["FolderName"][0].focus():_this4.$refs["FolderName"].focus()}))},loadGraphics:function(editGraphicsObj){var _this5=this;this.loading=!0,this.$http.get("/v2/graphics/list").then((function(response){0===response.data.responseCode?(_this5.graphicsList=response.data.result.graphics_list,_this5.$route.params.graphicsid||_this5.$router.push({path:"/app/em/graphics/view/"+_this5.graphicsList[0].id}),_this5.loading=!1):_this5.$message.error(response.data.message)})).catch((function(error){_this5.$message.error(error),_this5.loading=!1})),editGraphicsObj&&(this.editGraphicsObj=editGraphicsObj)},editGraphics:function(editGraphicsObj){this.editGraphicsObj=editGraphicsObj},deleteGraphics:function(index,graphics){var _this6=this,confirmed=confirm(this.$t("common._common.are_you_want_delete_graphics"));confirmed&&this.$http.post("/v2/graphics/delete",{recordId:graphics.id}).then((function(response){response&&_this6.graphicsList.splice(index,1)}))},editorClose:function(){this.editGraphicsObj=null},expand:function(folder){folder.expand=!folder.expand}}},graphics_Graphicsvue_type_script_lang_js=Graphicsvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(graphics_Graphicsvue_type_script_lang_js,render,staticRenderFns,!1,null,"f077a188",null)
/* harmony default export */,Graphics=component.exports},
/***/705351:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// EXPORTS
__webpack_require__.d(__webpack_exports__,{Z:function(){/* binding */return NewGraphicsForm}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/pages/energy/graphics/NewGraphicsForm.vue?vue&type=template&id=73e5803a
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",[_vm.formVisibility?_c("div",{staticClass:"position-relative"},[_c("el-dialog",{attrs:{"append-to-body":!0,visible:!0,"custom-class":"fc-dialog-center-container fc-quick-add-dialog fc-web-form-dialog"},on:{close:_vm.cancelForm}},[_c("div",{staticClass:"fc-pm-main-content-H"},[_vm._v(" GRAPHICS "),_c("div",{staticClass:"fc-heading-border-width43 mT15"})]),_vm.loading.graphics||_vm.loading.graphicsFolder?_c("div",[_c("spinner",{staticClass:"mT20",attrs:{show:_vm.loading.graphics||_vm.loading.graphicsFolder,size:"80"}})],1):_c("div",{staticStyle:{padding:"20px 40px"}},[_c("el-row",{staticClass:"mB20",attrs:{gutter:20}},[_c("el-col",{attrs:{span:24}},[_c("p",{staticClass:"fc-input-label-txt"},[_vm._v("Name")]),_c("el-input",{staticClass:"fc-input-full-border2 width100",attrs:{placeholder:"Name"},model:{value:_vm.graphicsData.name,callback:function($$v){_vm.$set(_vm.graphicsData,"name",$$v)},expression:"graphicsData.name"}})],1)],1),_c("el-row",{staticClass:"mB20",attrs:{gutter:20}},[_c("el-col",{attrs:{span:24}},[_c("p",{staticClass:"fc-input-label-txt"},[_vm._v("Description")]),_c("el-input",{staticClass:"fc-input-full-border-textarea",attrs:{type:"textarea",autosize:{minRows:4,maxRows:4},placeholder:"Enter Description"},model:{value:_vm.graphicsData.description,callback:function($$v){_vm.$set(_vm.graphicsData,"description",$$v)},expression:"graphicsData.description"}})],1)],1),_c("el-row",{staticClass:"mB20",attrs:{gutter:20}},[_c("el-col",{attrs:{span:12}},[_c("p",{staticClass:"fc-input-label-txt"},[_vm._v(" "+_vm._s(_vm.graphicsContext&&!_vm.duplicateGraphics?"Asset Category":"Asset")+" ")]),_vm.graphicsContext&&!_vm.duplicateGraphics?_c("div",{staticClass:"form-input fc-input-full-border-select2"},[_c("el-input",{staticClass:"el-input-textbox-full-border",attrs:{type:"text",readonly:!0},model:{value:_vm.getAssetCategory(_vm.graphicsData.assetCategoryId).displayName,callback:function($$v){_vm.$set(_vm.getAssetCategory(_vm.graphicsData.assetCategoryId),"displayName",$$v)},expression:"\n                  getAssetCategory(graphicsData.assetCategoryId).displayName\n                "}})],1):_c("div",{staticClass:"form-input fc-input-full-border-select2"},[_c("el-input",{staticClass:"el-input-textbox-full-border",attrs:{type:"text",placeholder:"Select Asset",readonly:!0},model:{value:_vm.graphicsData.assetName,callback:function($$v){_vm.$set(_vm.graphicsData,"assetName",$$v)},expression:"graphicsData.assetName"}},[_c("i",{staticClass:"el-input__icon el-icon-search",staticStyle:{"line-height":"0px !important","font-size":"16px !important",cursor:"pointer"},attrs:{slot:"suffix"},on:{click:function($event){_vm.openAssetChooser=!0}},slot:"suffix"})])],1)]),_c("el-col",{attrs:{span:12}},[_c("p",{staticClass:"fc-input-label-txt"},[_vm._v("Folder")]),_c("el-select",{staticClass:"fc-input-full-border2 width100",attrs:{filterable:!0,"allow-create":!0,placeholder:"Enter new folder name"},model:{value:_vm.graphicsData.parentFolderId,callback:function($$v){_vm.$set(_vm.graphicsData,"parentFolderId",$$v)},expression:"graphicsData.parentFolderId"}},_vm._l(_vm.graphicsFolders,(function(folder,id){return _c("el-option",{key:id,attrs:{label:folder.name,value:folder.id}})})),1)],1)],1)],1),_c("div",{staticClass:"modal-dialog-footer"},[_c("el-button",{staticClass:"modal-btn-cancel",on:{click:_vm.cancelForm}},[_vm._v("CANCEL")]),_c("el-button",{staticClass:"modal-btn-save",attrs:{type:"primary",loading:_vm.saving},on:{click:_vm.saveGraphicsActions}},[_vm._v(_vm._s(_vm.saving?"Saving...":"SAVE"))])],1)])],1):_vm._e(),_c("space-asset-chooser",{attrs:{showAsset:!0,visibility:_vm.openAssetChooser,resourceType:[2],initialValues:_vm.assetChooserInitialValue,appendToBody:!1,hideSidebar:!0},on:{associate:_vm.associate,"update:visibility":function($event){_vm.openAssetChooser=$event}}})],1)},staticRenderFns=[],objectSpread2=__webpack_require__(595082),SpaceAssetChooser=(__webpack_require__(434284),__webpack_require__(821057),__webpack_require__(372587)),vuex_esm=__webpack_require__(420629),NewGraphicsFormvue_type_script_lang_js={props:["formVisibility","graphicsContext","duplicateGraphics"],data:function(){return{editData:null,saving:!1,graphicsData:{name:null,description:null,assetId:null,assetCategoryId:null,assetName:null,parentFolderId:null},openAssetChooser:!1,graphicsFolders:null,loading:{graphicsFolder:!1,graphics:!1},assetChooserInitialValue:null}},components:{SpaceAssetChooser:SpaceAssetChooser/* default */.Z},mounted:function(){this.graphicsContext&&this.fillEditObject(),this.loadGraphicsFolders(),this.$store.dispatch("loadAssetCategory")},computed:(0,objectSpread2/* default */.Z)({},(0,vuex_esm/* mapGetters */.Se)(["getAssetCategory"])),methods:{loadAsset:function(assetId){var _this=this;assetId&&this.$http.get("/asset/summary/"+assetId).then((function(response){_this.graphicsData.assetName=response.data.asset.name,_this.graphicsData.assetCategoryId=response.data.asset.category?response.data.asset.category.id:null,_this.duplicateGraphics&&(_this.assetChooserInitialValue={assetCategory:_this.graphicsData.assetCategoryId})}))},fillEditObject:function(){this.graphicsData=this.$helpers.cloneObject(this.graphicsContext),this.graphicsData.assetId&&this.loadAsset(this.graphicsContext.assetId)},cancelForm:function(){this.$emit("update:formVisibility",!1)},associate:function(data){this.graphicsData.assetId=data.id,this.graphicsData.assetCategoryId=data.category?data.category.id:null,this.graphicsData.assetName=data.name,this.openAssetChooser=!1},saveGraphicsActions:function(){var _this2=this;if(this.graphicsData.parentFolderId){var graphicsFolderId=-1;try{graphicsFolderId=parseInt(this.graphicsData.parentFolderId)}catch(err){}if(isNaN(graphicsFolderId)||graphicsFolderId<0){var folderParam={graphicsFolder:{name:this.graphicsData.parentFolderId}};this.$http.post("/v2/graphicsFolder/add",folderParam).then((function(response){if(0!==response.data.responseCode)throw new Error(response.data.message);_this2.graphicsData.parentFolderId=response.data.result.graphicsFolder.id,_this2.addGraphics()})).catch((function(error){_this2.$message.error(error)}))}else this.addGraphics()}else this.$message.error("Please Choose the folder or enter new folder name.")},addGraphics:function(){var _this3=this,url="/v2/graphics/add",message="Graphics created successfully.";this.graphicsContext&&!this.duplicateGraphics&&(url="/v2/graphics/update",message="Graphics updated successfully."),this.duplicateGraphics&&(message="Graphics Duplicated successfully");var temp=this.$helpers.cloneObject(this.graphicsData);delete temp.assetName;var param={graphics:temp};this.saving=!0,this.$http.post(url,param).then((function(response){_this3.saving=!1,0===response.data.responseCode?(_this3.$message.success(message),_this3.$emit("saved",response.data.result.graphics),_this3.cancelForm()):_this3.$message.error(response.data.message)})).catch((function(error){_this3.saving=!1,_this3.$message.error(error)})),this.emitForm=!1},loadGraphicsFolders:function(){var _this4=this;this.loading.graphicsFolder=!0,this.$http.get("/v2/graphicsFolder/list").then((function(response){if(0!==response.data.responseCode)throw new Error(response.data.message);_this4.graphicsFolders=response.data.result.graphicsFolders,_this4.loading.graphicsFolder=!1})).catch((function(error){_this4.$message.error(error),_this4.loading.graphicsFolder=!1}))}}},graphics_NewGraphicsFormvue_type_script_lang_js=NewGraphicsFormvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(graphics_NewGraphicsFormvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,NewGraphicsForm=component.exports}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/6992.js.map