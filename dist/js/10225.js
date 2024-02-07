"use strict";(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[10225],{
/***/210225:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return FunctionsEditor}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/pages/setup/new/FunctionsEditor.vue?vue&type=template&id=3549baa0
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"functions-editor-container"},[_c("div",{staticClass:"fc-email-breadcrumb-header"},[_c("div",[_c("div",{staticClass:"flex-middle fc-setup-breadcrumb"},[_c("div",{staticClass:"fc-setup-breadcrumb-inner pointer",on:{click:_vm.setupHomeRoute}},[_vm._v(" "+_vm._s(_vm.$t("common.products.home"))+" ")]),_vm._m(0),_c("div",{staticClass:"fc-setup-breadcrumb-inner pointer",on:{click:_vm.setupFunctionsRoute}},[_vm._v(" "+_vm._s(_vm.$t("common._common.function"))+" ")]),_vm._m(1),_c("div",{staticClass:"fc-breadcrumbBold-active"},[_vm._v(" "+_vm._s(_vm.namespaceName?_vm.namespaceName:_vm.$t("common._common.namespace"))+" ")])]),_c("div",{staticClass:"pT10"},[_c("el-input",{staticClass:"fc-template-email-input fc-email-top-input-width",attrs:{placeholder:"Function Name",readonly:""},model:{value:_vm.functionName,callback:function($$v){_vm.functionName=$$v},expression:"functionName"}})],1)]),_c("div",[_c("div",{staticClass:"flex-middle"},[_c("el-switch",{staticClass:"mR20",attrs:{"active-color":"rgba(57, 178, 194, 0.8)","inactive-color":"#e5e5e5","active-text":_vm.$t("common._common.run_as_admin")},model:{value:_vm.runAsAdmin,callback:function($$v){_vm.runAsAdmin=$$v},expression:"runAsAdmin"}}),_c("el-switch",{staticClass:"mR20 mL20",attrs:{"active-text":"Diff"},model:{value:_vm.showDiff,callback:function($$v){_vm.showDiff=$$v},expression:"showDiff"}}),_c("el-button",{staticClass:"fc-dropdown-menu-template2 mR10",attrs:{size:"medium"},on:{click:function($event){return _vm.updateFunction()}}},[_vm._v(_vm._s(_vm.$t("common._common.update")))]),_c("el-button",{staticClass:"fc-wo-border-btn height34 pL0 pR0 text-capitalize cancel-btn",on:{click:_vm.setupFunctionsRoute}},[_vm._v(" "+_vm._s(_vm.$t("common._common.cancel"))+" ")])],1)])]),_c("div",{staticClass:"editor-container"},[_vm.loading?_c("spinner",{attrs:{show:_vm.loading}}):_c("script-editor",{attrs:{scriptClass:"script-editor-container",diff:_vm.diffStatus},model:{value:_vm.workflowV2String,callback:function($$v){_vm.workflowV2String=$$v},expression:"workflowV2String"}})],1)])},staticRenderFns=[function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"fc-setup-breadcrumb-inner pL10 pR10"},[_c("i",{staticClass:"el-icon-arrow-right f14 fwBold"})])},function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"fc-setup-breadcrumb-inner pL10 pR10"},[_c("i",{staticClass:"el-icon-arrow-right f14 fwBold"})])}],regeneratorRuntime=__webpack_require__(150124),asyncToGenerator=__webpack_require__(548534),router=(__webpack_require__(538077),__webpack_require__(260228),__webpack_require__(434284),__webpack_require__(425728),__webpack_require__(564043),__webpack_require__(857267),__webpack_require__(329435)),setup=__webpack_require__(572552),api=__webpack_require__(32284),validation=__webpack_require__(990260),FunctionsEditorvue_type_script_lang_js={components:{ScriptEditor:setup/* ScriptEditor */.Y},computed:{appLinkName:function(){var _ref=(0,router/* getApp */.wx)()||{},linkName=_ref.linkName;return linkName},diffStatus:function(){return this.showDiff?{type:"side_by_side"}:""}},data:function(){return{loading:!0,workflowV2String:"",functionName:"",showDiff:!1,namespaceName:"",runAsAdmin:!1}},created:function(){var _this=this;return(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee(){var _yield$API$get,data,error,spaceListData,namespaceArray,namespace,_ref2,namespaceName,namespaceFunctions,functionsArray,functionObjArray,functionName,workflowV2String,runAsAdmin;return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context){while(1)switch(_context.prev=_context.next){case 0:return _context.next=2,api/* API */.bl.get("/v2/workflow/getNameSpaceListWithFunctions");case 2:if(_yield$API$get=_context.sent,data=_yield$API$get.data,error=_yield$API$get.error,!error){_context.next=8;break}return _this.$message.error(error.message||"Error Occured"),_context.abrupt("return");case 8:spaceListData=null===data||void 0===data?void 0:data.workflowNameSpaceList,namespaceArray=null===spaceListData||void 0===spaceListData?void 0:spaceListData.filter((function(namespace){return namespace.id==_this.getParams("namespaceId")})),namespace=namespaceArray[0],_ref2=namespace||{},namespaceName=_ref2.name,namespaceFunctions=_ref2.functions,_this.namespaceName=namespaceName,functionsArray=namespaceFunctions,functionObjArray=functionsArray.find((function(fun){return fun.id===_this.getParams("functionId")})),functionName=functionObjArray.name,workflowV2String=functionObjArray.workflowV2String,runAsAdmin=functionObjArray.runAsAdmin,_this.functionName=functionName,_this.workflowV2String=workflowV2String,_this.loading=!1,_this.runAsAdmin=runAsAdmin;case 20:case"end":return _context.stop()}}),_callee)})))()},methods:{setupHomeRoute:function(){return this.$router.replace({path:"/".concat(this.appLinkName,"/setup/home")})},setupFunctionsRoute:function(){return this.$router.replace({path:"/".concat(this.appLinkName,"/setup/customization/functions")})},updateFunction:function(){var _this2=this;return(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee2(){var params,_yield$API$post,data,error;return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context2){while(1)switch(_context2.prev=_context2.next){case 0:return params={},params["userFunction"]={id:_this2.getParams("functionId"),runAsAdmin:_this2.runAsAdmin,workflowV2String:_this2.workflowV2String,nameSpaceId:_this2.getParams("namespaceId"),isV2Script:!0},_context2.next=4,api/* API */.bl.post("/v2/workflow/updateUserFunction",params);case 4:if(_yield$API$post=_context2.sent,data=_yield$API$post.data,error=_yield$API$post.error,!error){_context2.next=10;break}return _this2.$message.error(error.message||"Error Occured"),_context2.abrupt("return");case 10:_this2.checkErrors(data)?_this2.$router.replace({path:"/".concat(_this2.appLinkName,"/setup/customization/functions")}):_this2.showerrorBanners(data);case 11:case"end":return _context2.stop()}}),_callee2)})))()},checkErrors:function(data){var workflowSyntaxError=data.workflowSyntaxError,_ref3=workflowSyntaxError||[],errors=_ref3.errors;return(0,validation/* isEmpty */.xb)(errors)},showerrorBanners:function(data){var workflowSyntaxError=data.workflowSyntaxError,_ref4=workflowSyntaxError||"Error",errorsAsString=_ref4.errorsAsString;this.$message({showClose:!0,message:"  ".concat(errorsAsString,"  "),type:"error",duration:1e5})},getParams:function(param){var params=this.$route.params,_ref5=params||"",parameter=_ref5[param];return parameter}}},new_FunctionsEditorvue_type_script_lang_js=FunctionsEditorvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(new_FunctionsEditorvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,FunctionsEditor=component.exports}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/10225.js.map