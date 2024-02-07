"use strict";(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[2483],{
/***/102483:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return Node_AssetMapping}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/pages/setup/Node-AssetMapping.vue?vue&type=template&id=5c88c54d
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"height100"},[_vm._m(0),_c("div",{staticClass:"container-scroll"},[_c("div",{staticClass:"row setting-Rlayout mT30"},[_c("div",{staticClass:"col-lg-12 col-md-12"},[_c("table",{staticClass:"setting-list-view-table"},[_vm._m(1),_vm.loading?_c("tbody",[_c("tr",[_c("td",{staticClass:"text-center",attrs:{colspan:"600px"}},[_c("spinner",{attrs:{show:_vm.loading,size:"80"}})],1)])]):this.nodes.length?_c("tbody",_vm._l(_vm.nodes,(function(node,index){return _c("tr",{key:index,staticClass:"tablerow"},[_c("td",[_vm._v(_vm._s(node.source))]),node.resourceId?_c("td",[_vm._v(" "+_vm._s(_vm.resources[node.resourceId].name)+" ")]):_c("td",{staticStyle:{width:"300px"}},[_c("div",{staticClass:"link-text"},[_vm._v("Map Asset")]),_c("q-popover",{ref:"assetpopover",refInFor:!0},[_c("q-list",{staticClass:"scroll",staticStyle:{"min-width":"150px"},attrs:{link:""}},_vm._l(_vm.assets,(function(asset,index){return _c("q-item",{key:index,on:{click:function($event){return _vm.updatenode(node.id,index)}}},[_c("q-item-main",{attrs:{label:asset}})],1)})),1)],1)],1)])})),0):_c("tbody",[_vm._m(2)])])])])])])},staticRenderFns=[function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"setting-header2"},[_c("div",{staticClass:"setting-title-block"},[_c("div",{staticClass:"setting-form-title"},[_vm._v("Source - Resource Mapping")]),_c("div",{staticClass:"heading-description"},[_vm._v(" List of all Source - Resource Mapping ")])])])},function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("thead",[_c("tr",[_c("th",{staticClass:"setting-table-th setting-th-text"},[_vm._v("SOURCE")]),_c("th",{staticClass:"setting-table-th setting-th-text"},[_vm._v("ASSET")])])])},function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("tr",[_c("td",{staticClass:"text-center",attrs:{colspan:"600px"}},[_vm._v(" No nodes created yet. ")])])}],regeneratorRuntime=__webpack_require__(150124),asyncToGenerator=__webpack_require__(548534),quasar_esm=__webpack_require__(641358),api=__webpack_require__(32284),validation=__webpack_require__(990260),picklist=__webpack_require__(961358),Node_AssetMappingvue_type_script_lang_js={components:{QList:quasar_esm/* QList */.tu,QItem:quasar_esm/* QItem */.ry,QItemMain:quasar_esm/* QItemMain */.Ui,QPopover:quasar_esm/* QPopover */.oe},data:function(){return{assets:[],loading:!0,nodes:[],assetId:null,resources:{}}},title:function(){return"Node-Asset Mappings"},created:function(){this.loadAssetPickListData()},mounted:function(){this.loadNodes()},methods:{loadAssetPickListData:function(){var _this=this;return(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee(){var _yield$getFieldOption,error,options;return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context){while(1)switch(_context.prev=_context.next){case 0:return _context.next=2,(0,picklist/* getFieldOptions */._K)({field:{lookupModuleName:"asset",skipDeserialize:!0}});case 2:_yield$getFieldOption=_context.sent,error=_yield$getFieldOption.error,options=_yield$getFieldOption.options,error?_this.$message.error(error.message||"Error Occured"):_this.assets=options;case 6:case"end":return _context.stop()}}),_callee)})))()},loadNodes:function(){var _this2=this;this.loading=!0,api/* API */.bl.get("/v2/event/sources").then((function(_ref){var data=_ref.data,error=_ref.error;if((0,validation/* isEmpty */.xb)(error)){var sources=data.sources,resources=data.resources;_this2.nodes=sources,_this2.resources=resources}_this2.loading=!1}))},updatenode:function(id,assetId){var _this3=this;api/* API */.bl.post("/event/updatesource",{id:id,resourceId:assetId}).then((function(_ref2){var error=_ref2.error;(0,validation/* isEmpty */.xb)(error)?_this3.loadNodes():_this3.$message.error(error)}))}}},setup_Node_AssetMappingvue_type_script_lang_js=Node_AssetMappingvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(setup_Node_AssetMappingvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,Node_AssetMapping=component.exports}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/2483.js.map