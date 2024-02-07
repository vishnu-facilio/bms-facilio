"use strict";(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[29458,62083,8725,59297,18508],{
/***/372587:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// EXPORTS
__webpack_require__.d(__webpack_exports__,{Z:function(){/* binding */return SpaceAssetChooser}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/components/SpaceAssetChooser.vue?vue&type=template&id=daefb110
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("el-dialog",{attrs:{visible:_vm.visibility,fullscreen:!1,open:"top","custom-class":" fc-dialog-up spaceassetchooser setup-dialog60","append-to-body":!0,"before-close":_vm.handleclose},on:{"update:visible":function($event){_vm.visibility=$event}}},[_c("div",{attrs:{id:"spaceassetchooser"}},[_c("div",{staticStyle:{height:"600px","max-height":"600px"}},[_vm.hideSidebar?_vm._e():_c("div",{staticClass:"sidebar",staticStyle:{height:"100%",width:"33%",display:"inline-block"}},[_c("div",{staticClass:"pT30",staticStyle:{"margin-left":"30px","z-index":"100"}},[_c("div",{staticClass:"flLeft",staticStyle:{"padding-right":"30px"},attrs:{disabled:"asset"===_vm.picktype},on:{click:function($event){"asset"!==_vm.picktype&&_vm.openSpace()}}},[_c("div",{staticClass:"flLeft",staticStyle:{"padding-top":"1px"}},[_c("i",{staticClass:"el-icon-circle-check",class:_vm.isAsset?"normalspace":"selectedspace"})]),_c("div",{staticClass:"txtstyle pointer flLeft",staticStyle:{"padding-left":"5px"}},[_vm._v(" "+_vm._s(_vm.$t("common.space_asset_chooser.space"))+" ")]),_c("div",{staticClass:"clearboth"})]),_c("div",{staticClass:"flLeft",attrs:{disabled:"space"===_vm.picktype},on:{click:function($event){"space"!==_vm.picktype&&_vm.openAsset()}}},[_c("div",{staticClass:"flLeft",staticStyle:{"padding-top":"1px"}},[_c("i",{staticClass:"el-icon-circle-check",class:_vm.isAsset?"selectedspace":"normalspace"})]),_c("div",{staticClass:"flLeft"},[_c("span",{staticClass:"txtstyle pointer",staticStyle:{"padding-left":"5px"}},[_vm._v(_vm._s(_vm.$t("common.space_asset_chooser.asset")))])]),_c("div",{staticClass:"clearboth"})]),_c("div",{staticClass:"clearboth"})]),_c("div",{staticStyle:{"margin-left":"30px"}},[_c("div",{staticClass:"space-heading pT30 pB5"},[_vm._v(" "+_vm._s(_vm.$t("common.space_asset_chooser.by_location"))+" ")]),_vm._l(_vm.resourceConfig,(function(field){return _c("div",{key:field.spaceType,staticClass:"pB15"},[_c("el-select",{attrs:{filterable:"",clearable:"",placeholder:field.placeHolder,"data-test-selector":""+field.placeHolder,disabled:4===field.spaceType&&!_vm.isAsset||field.disabled},on:{change:function($event){return _vm.onLocationSelected(field)}},model:{value:field.value,callback:function($$v){_vm.$set(field,"value",$$v)},expression:"field.value"}},_vm._l(field.options,(function(option){return _c("el-option",{key:option.id,attrs:{label:option.name,value:option.id,"data-test-selector":field.placeHolder+"_"+option.name}})})),1)],1)})),_c("div",{staticClass:"pT30"},[_c("div",{staticClass:"space-heading pT15 pB5"},[_vm._v(" "+_vm._s(_vm.$t("common.space_asset_chooser.by_category"))+" ")]),_c("div",[_vm.isAsset?_c("el-select",{attrs:{filterable:"",clearable:"",placeholder:_vm.assetCategory.placeHolder,"data-test-selector":""+_vm.$t("common.space_asset_chooser.by_category"),disabled:!_vm.isAsset},on:{change:_vm.onCategorySelected},model:{value:_vm.assetCategory.value,callback:function($$v){_vm.$set(_vm.assetCategory,"value",$$v)},expression:"assetCategory.value"}},_vm._l(_vm.assetCategory.options,(function(label,value){return _c("el-option",{key:value,attrs:{label:label,value:value,"data-test-selector":_vm.$t("common.space_asset_chooser.by_category")+"_"+label}})})),1):_c("el-select",{staticClass:"inline asset-category fc-input-full-border2",attrs:{filterable:"",clearable:"",placeholder:_vm.spaceCategory.placeHolder},on:{change:_vm.onSpaceCategorySelected},model:{value:_vm.spaceCategory.value,callback:function($$v){_vm.$set(_vm.spaceCategory,"value",$$v)},expression:"spaceCategory.value"}},_vm._l(_vm.spaceCategory.options,(function(label,value){return _c("el-option",{key:value,attrs:{label:label,value:value}})})),1)],1)])],2)]),_c("div",{staticClass:"pull-right heightlist",style:{width:_vm.hideSidebar?"100%":"67%"}},[_c("div",{staticStyle:{"padding-left":"20px","padding-right":"20px"}},[_vm.showQuickSearch?_c("div",{staticStyle:{display:"inline-block",width:"100%"}},[_c("div",{staticClass:"col-6 fc-list-search"},[_c("div",{staticClass:"fc-list-search-wrapper relative chooser-search"},[_c("svg",{staticClass:"search-icon",attrs:{xmlns:"http://www.w3.org/2000/svg",width:"32",height:"32",viewBox:"0 0 32 32"}},[_c("title",[_vm._v(_vm._s(_vm.$t("common._common.search")))]),_c("path",{attrs:{d:"M21.487 22.927l7.037 7.037c.473.473 1.238.475 1.71.003s.47-1.237-.003-1.71l-7.037-7.037c3.96-4.82 3.675-11.967-.846-16.487C17.539-.076 9.757-.092 4.966 4.699S.191 17.272 5 22.081c4.52 4.52 11.667 4.805 16.487.846zM6.679 6.413c3.848-3.848 10.099-3.836 13.962.027s3.875 10.114.027 13.962c-3.848 3.848-10.099 3.836-13.962-.027S2.831 10.261 6.679 6.413z"}})]),_c("input",{directives:[{name:"model",rawName:"v-model",value:_vm.quickSearchQuery,expression:"quickSearchQuery"}],ref:"quickSearchQuery",staticClass:"quick-search-input",attrs:{autofocus:"autofocus",type:"text",placeholder:"Search"},domProps:{value:_vm.quickSearchQuery},on:{keyup:function($event){return!$event.type.indexOf("key")&&_vm._k($event.keyCode,"enter",13,$event.key,"Enter")?null:_vm.loadResourceData.apply(null,arguments)},input:function($event){$event.target.composing||(_vm.quickSearchQuery=$event.target.value)}}}),_c("svg",{staticClass:"close-icon",attrs:{xmlns:"http://www.w3.org/2000/svg",width:"32",height:"32",viewBox:"0 0 32 32","aria-hidden":"true"},on:{click:_vm.toggleQuickSearch}},[_c("title",[_vm._v("close")]),_c("path",{attrs:{d:"M17.992 16l8.796-8.796a1.409 1.409 0 0 0-1.992-1.992L16 14.008 7.204 5.212a1.409 1.409 0 0 0-1.992 1.992L14.008 16l-8.796 8.796a1.409 1.409 0 0 0 1.992 1.992L16 17.992l8.796 8.796a1.409 1.409 0 0 0 1.992-1.992L17.992 16z"}})])])])]):_c("div",{staticClass:"flex-middle justify-content-space"},[_c("span",{staticClass:"spaceheading",staticStyle:{display:"inline-block"}},[_vm._v(_vm._s(_vm.isAsset?this.$t("common.space_asset_chooser.asset_list"):this.$t("common.space_asset_chooser.space_list")))]),_c("span",{staticClass:"pull-right pointer"},[_c("span",{on:{click:_vm.toggleQuickSearch}},[_c("i",{staticClass:"el-icon-search"})])])])]),_c("div",{directives:[{name:"infinite-scroll",rawName:"v-infinite-scroll",value:_vm.loadMores,expression:"loadMores"}],staticClass:"scroll",attrs:{"infinite-scroll-distance":"10","infinite-scroll-immediate-check":"true"}},[_c("v-infinite-scroll",{staticStyle:{"max-height":"500px",height:"500px","overflow-y":"scroll"},attrs:{loading:_vm.loading,offset:20},on:{bottom:_vm.nextPage}},[_c("table",{staticClass:" pT15 fc-list-view-table"},[_c("thead",[_c("tr",{staticClass:"tablerow pT30",staticStyle:{"background-color":"#ffffff",padding:"15px"}},[_c("td",{staticClass:"headerrow width30"},[_vm._v(" #"+_vm._s(_vm.$t("common._common.id"))+" ")]),_c("td",{staticClass:"headerrow width30"},[_vm._v(" "+_vm._s(_vm.$t("common.space_asset_chooser.name"))+" ")]),_vm.hideSidebar?_vm._e():_c("td",{staticClass:"headerrow width30"},[_vm._v(" "+_vm._s(_vm.$t("common.space_asset_chooser.category"))+" ")]),_c("td"),_c("td")])]),_c("tbody",[_vm._l(_vm.spaceList,(function(space){return _vm.isAsset?_vm._e():_c("tr",{key:space.id,staticClass:"tablerow",class:{selectedrow:_vm.selectedObj===space},attrs:{"data-test-selector":""+space.name},on:{click:function($event){return _vm.select(space,1)}}},[_c("td",{staticClass:"contentrow width30"},[_vm._v(" #"+_vm._s(1==space.spaceType?"SI"+space.id:2==space.spaceType?"BU"+space.id:3==space.spaceType?"FL"+space.id:4==space.spaceType?"SP"+space.id:space.id)+" ")]),_c("td",{staticClass:"contentrow width30"},[_vm._v(_vm._s(space.name))]),_c("td",{staticClass:"contentrow width30"},[_vm._v(_vm._s(space.spaceTypeVal))]),_c("td"),_c("td",[_c("i",{staticClass:"el-icon-check",staticStyle:{color:"#39b2c2","padding-left":"20px"},style:{visibility:_vm.selectedObj===space?"visible":"hidden"}})])])})),_vm._l(_vm.assetList,(function(asset){return _vm.isAsset?_c("tr",{key:"asset_"+asset.id,staticClass:"tablerow",class:{selectedrow:_vm.selectedObj===asset},attrs:{"data-test-selector":""+asset.name},on:{click:function($event){return _vm.select(asset,2)}}},[_c("td",{staticClass:"contentrow width30"},[_vm._v("#"+_vm._s(asset.id))]),_c("td",{staticClass:"contentrow width30"},[_vm._v(_vm._s(asset.name))]),_vm.hideSidebar?_vm._e():_c("td",{staticClass:"contentrow width30"},[_vm._v(" "+_vm._s(asset.category?_vm.getAssetCategory(asset.category.id).displayName:"---")+" ")]),_c("td"),_c("td",[_c("i",{staticClass:"el-icon-check",staticStyle:{color:"#39b2c2","padding-left":"20px"},style:{visibility:_vm.selectedObj===asset?"visible":"hidden"}})])]):_vm._e()})),_vm.spaceList.length<1&&_vm.assetList.length<1?_c("tr",{staticStyle:{background:"none",border:"none","margin-top":"20px"}},[_c("td",{staticClass:"text-center label-txt-black",staticStyle:{"border-bottom":"none","font-size":"14px","padding-top":"40px",cursor:"auto"},attrs:{colspan:"100%"}},[_vm._v(" No "+_vm._s(_vm.isAsset?"Asset":"Space")+" to display! ")])]):_vm._e()],2)])])],1),_c("div",{staticClass:"space-asset-footer"},[_c("div",{staticClass:"space-asset-footer-align"},[_c("el-button",{staticStyle:{"font-size":"13px","font-weight":"bold",border:"2px solid #cde2e5",color:"#8eb1b6","letter-spacing":"0.5px",padding:"8px",width:"100px","text-transform":"uppercase"},attrs:{"data-test-selector":"SpaceAsset_cancel"},on:{click:_vm.handleclose}},[_vm._v(_vm._s(_vm.$t("common._common.cancel")))]),_c("el-button",{staticStyle:{"font-size":"13px","font-weight":"bold","letter-spacing":"0.5px","background-color":"#39b2c2","border-color":"#39b2c2",padding:"8px","text-transform":"uppercase"},attrs:{"data-test-selector":"SpaceAsset_save",type:"primary"},on:{click:_vm.associate}},[_vm._v(_vm._s(_vm.$t("common._common.select")))])],1)])])]),_c("div",{staticStyle:{position:"absolute",top:"40%",left:"55%","z-index":"5"}},[_c("spinner",{attrs:{show:_vm.resourceLoading,size:"70"}})],1)])])},staticRenderFns=[],objectSpread2=__webpack_require__(595082),vue_infinite_scroll=__webpack_require__(49026),vue_infinite_scroll_default=__webpack_require__.n(vue_infinite_scroll),ResourceMixin=__webpack_require__(22637),v_infinite_scroll=__webpack_require__(561364),v_infinite_scroll_default=__webpack_require__.n(v_infinite_scroll),Spinner=__webpack_require__(604947),vuex_esm=__webpack_require__(420629),SpaceAssetChooservue_type_script_lang_js={mixins:[ResourceMixin/* default */.Z],props:["visibility","appendToBody","query","picktype","hideSidebar","isService"],directives:{infiniteScroll:vue_infinite_scroll_default()},components:{Spinner:Spinner/* default */.Z,VInfiniteScroll:v_infinite_scroll_default()},data:function(){return{fetchingMore:!1,selected:!1,selectedObj:{},showQuickSearch:!1}},created:function(){this.$store.dispatch("loadAssetCategory"),this.$store.dispatch("loadSpaceCategory")},mounted:function(){
// quickSearchQuery
"asset"===this.picktype&&(this.isAsset=!0),this.initResourceData()},computed:(0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)({},(0,vuex_esm/* mapGetters */.Se)(["getAssetCategory"])),{},{scrollDisabled:function(){return!1}}),component:{Spinner:Spinner/* default */.Z},watch:{visibility:function(val){var _this=this;val&&!this.query?(this.reInit(),this.hideSidebar&&(this.showQuickSearch=!0,this.$nextTick((function(){_this.$refs.quickSearchQuery&&_this.$refs.quickSearchQuery.focus()})))):val||!this.query||this.selectedObj&&this.selectedObj.id||this.$emit("associate",{},this.isAsset?"asset":"space")},query:function(val){var _this2=this;this.quickSearchQuery=val,val&&this.$refs.quickSearchQuery&&(this.selectedObj={},this.showQuickSearch=!0,this.$nextTick((function(){_this2.$refs.quickSearchQuery.focus()})),this.loadResourceData())}},methods:{loadMores:function(){this.fetchingMore=!0,this.openAsset(!0)},reInit:function(){this.showQuickSearch=!1,this.quickSearchQuery=null,this.paging.page=1,this.checkAndInitResourceData()},handleclose:function(){this.$emit("update:visibility",!1)},openSpace:function(){this.isAsset=!1,this.paging.page=1,this.loadSpace()},openAsset:function(isLoad){this.isAsset=!0,this.paging.page=1,this.loadAsset(isLoad)},select:function(obj,type){this.selectedObj=obj},associate:function(){this.$emit("associate",this.selectedObj,this.isAsset?"asset":"space")},toggleQuickSearch:function(){var _this3=this;this.showQuickSearch=!this.showQuickSearch,this.showQuickSearch?this.$nextTick((function(){_this3.$refs.quickSearchQuery.focus()})):(this.quickSearchQuery=null,this.loadResourceData())}}},components_SpaceAssetChooservue_type_script_lang_js=SpaceAssetChooservue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(components_SpaceAssetChooservue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,SpaceAssetChooser=component.exports}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/29458.js.map