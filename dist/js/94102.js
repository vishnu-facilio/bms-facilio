"use strict";(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[94102],{
/***/494102:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return DeveloperPublishApplicationList}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/pages/setup/developer/DeveloperPublishApplicationList.vue?vue&type=template&id=2070a935
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"fc-bundle-summary-page"},[_vm.loading?_c("div",{staticClass:"flex-middle fc-empty-white"},[_c("spinner",{attrs:{show:_vm.loading,size:"80"}})],1):_c("div",[_c("el-header",{attrs:{height:"80"}},[_c("div",[_c("div",{staticClass:"pointer pB10 fc-dark-blue-txt12 f14",on:{click:_vm.backToList}},[_c("i",{staticClass:"el-icon-back fc-dark-blue-txt12 fwBold"}),_vm._v(" Back ")]),_c("div",{staticClass:"fc-black3-16 bold text-capitalize"},[_vm._v(" "+_vm._s(_vm.$t("commissioning.list.publish"))+" ")])]),_c("button",{staticClass:"setup-el-btn pL30 pR30",attrs:{type:"button"},on:{click:function($event){return _vm.bundleCreateVersion()}}},[_vm._v(" "+_vm._s(_vm.$t("setup.setupLabel.create_version"))+" ")])]),_c("div",{staticClass:"fc-bundle-summary"},[_c("el-table",{staticClass:"fc-bundle-list-table",staticStyle:{width:"100%"},attrs:{data:_vm.bundleChangeSetList,"empty-text":_vm.$t("setup.setup_empty_state.publish_empty")}},[_c("template",{slot:"empty"},[_c("img",{staticClass:"mT50",attrs:{src:__webpack_require__(15482),width:"100",height:"100"}}),_c("div",{staticClass:"mT10 label-txt-black f14 op6"},[_vm._v(" "+_vm._s(_vm.$t("setup.setup_empty_state.publish_empty"))+" ")])]),_c("el-table-column",{attrs:{label:"name"},scopedSlots:_vm._u([{key:"default",fn:function(publish){return[_vm._v(" "+_vm._s(publish.row.componentDisplayName)+" ")]}}])}),_c("el-table-column",{attrs:{prop:"enum",label:"Enum"},scopedSlots:_vm._u([{key:"default",fn:function(publish){return[_vm._v(" "+_vm._s(publish.row.componentTypeEnum)+" ")]}}])}),_c("el-table-column",{attrs:{prop:"Mode Enum",label:"Enum"},scopedSlots:_vm._u([{key:"default",fn:function(publish){return[_vm._v(" "+_vm._s(publish.row.modeEnum)+" ")]}}])})],2)],1)],1),_c("el-dialog",{staticClass:"fc-dialog-center-container",attrs:{title:"Create Version",visible:_vm.dialogVisible,width:"30%","before-close":_vm.handleClose,"append-to-body":!0},on:{"update:visible":function($event){_vm.dialogVisible=$event}}},[_c("div",{staticClass:"height180"},[_c("el-form",{ref:"bundleForm",attrs:{model:_vm.bundle}},[_c("el-form-item",{attrs:{label:_vm.$t("setup.approvalprocess.name"),prop:"name"}},[_c("el-input",{staticClass:"width100 fc-input-full-border2",attrs:{autofocus:"",type:"text",placeholder:_vm.$t("setup.placeholder.enter_bundle_name")},model:{value:_vm.bundle.version,callback:function($$v){_vm.$set(_vm.bundle,"version",$$v)},expression:"bundle.version"}})],1)],1),_c("div",{staticClass:"modal-dialog-footer"},[_c("el-button",{staticClass:"modal-btn-cancel",on:{click:_vm.closeDialog}},[_vm._v(" "+_vm._s(_vm.$t("setup.users_management.cancel"))+" ")]),_c("el-button",{staticClass:"modal-btn-save",attrs:{type:"primary",loading:_vm.saving},on:{click:function($event){return _vm.createVersion()}}},[_vm._v(" "+_vm._s(_vm.$t("panel.dashboard.confirm"))+" ")])],1)],1)])],1)},staticRenderFns=[],regeneratorRuntime=__webpack_require__(150124),asyncToGenerator=__webpack_require__(548534),api=(__webpack_require__(670560),__webpack_require__(32284)),DeveloperPublishApplicationListvue_type_script_lang_js={data:function(){return{bundle:[{version:""}],bundleChangeSetList:[],dialogVisible:!1,loading:!1}},computed:{bundleId:function(){return this.$route.params.id?parseInt(this.$route.params.id):-1}},created:function(){this.bundlePublish()},methods:{bundlePublish:function(){var _this=this;return(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee(){var params,_yield$API$post,error,data;return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context){while(1)switch(_context.prev=_context.next){case 0:return _this.loading=!0,params={bundle:{id:_this.bundleId}},_context.next=4,api/* API */.bl.post("v3/bundle/getChangeSet",params);case 4:_yield$API$post=_context.sent,error=_yield$API$post.error,data=_yield$API$post.data,error?_this.$message.error(error.message||_this.$t("common._common.error_occured")):_this.bundleChangeSetList=data.bundleChangeSetList||[],_this.loading=!1;case 9:case"end":return _context.stop()}}),_callee)})))()},bundleCreateVersion:function(){this.dialogVisible=!0},closeDialog:function(){this.dialogVisible=!1},createVersion:function(){var _this2=this;this.$refs["bundleForm"].validate(function(){var _ref=(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee2(valid){var url,params,_yield$API$post2,error,data;return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context2){while(1)switch(_context2.prev=_context2.next){case 0:if(valid){_context2.next=2;break}return _context2.abrupt("return",!1);case 2:return _this2.saving=!0,url="v3/bundle/createVersion",params={bundle:{id:_this2.bundleId},version:_this2.bundle.version},_context2.next=7,api/* API */.bl.post(url,params);case 7:_yield$API$post2=_context2.sent,error=_yield$API$post2.error,data=_yield$API$post2.data,error?_this2.$message.error(error.message||_this2.$t("common._common.error_occured")):(_this2.$message.success(_this2.$t("common._common.trigger_saved_successfully")),_this2.$emit("onSave",data.bundle),_this2.closeDialog(),_this2.$router.push({name:"developerSummary",params:{id:_this2.bundleId}})),_this2.saving=!1;case 12:case"end":return _context2.stop()}}),_callee2)})));return function(_x){return _ref.apply(this,arguments)}}())},backToList:function(){this.$router.go(-1)}}},developer_DeveloperPublishApplicationListvue_type_script_lang_js=DeveloperPublishApplicationListvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(developer_DeveloperPublishApplicationListvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,DeveloperPublishApplicationList=component.exports},
/***/15482:
/***/function(module,__unused_webpack_exports,__webpack_require__){module.exports=__webpack_require__.p+"img/noData-light.3b5d6592.png";
/***/}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/94102.js.map