"use strict";(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[26854],{
/***/297157:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// EXPORTS
__webpack_require__.d(__webpack_exports__,{Z:function(){/* binding */return ErrorBanner}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/components/ErrorBanner.vue?vue&type=template&id=3d9e5288
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",[_vm.hasError?_c("div",{staticClass:"error-block",attrs:{id:"error"}},[_c("p",{staticClass:"error-txt"},[_c("i",{staticClass:"fa fa-info-circle",attrs:{"aria-hidden":"true"}}),_vm._v(" "+_vm._s(_vm.errorMessage?_vm.errorMessage:"Please fill all mandatory fields.")+" ")]),_c("i",{staticClass:"el-icon-close error-close-icon",on:{click:_vm.closeBanner}})]):_vm._e()])},staticRenderFns=[],ErrorBannervue_type_script_lang_js={props:["error","errorMessage"],computed:{hasError:{get:function(){return this.error},set:function(value){this.$emit("update:error",value)}}},methods:{closeBanner:function(){this.hasError=!1,this.$emit("update:errorMessage","")}}},components_ErrorBannervue_type_script_lang_js=ErrorBannervue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(components_ErrorBannervue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,ErrorBanner=component.exports},
/***/604947:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// EXPORTS
__webpack_require__.d(__webpack_exports__,{Z:function(){/* binding */return Spinner}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/components/Spinner.vue?vue&type=template&id=06a81286&scoped=true
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("transition",[_c("svg",{directives:[{name:"show",rawName:"v-show",value:_vm.show,expression:"show"}],staticClass:"spinner",class:{show:_vm.show},attrs:{width:_vm.spinnerSize,height:_vm.spinnerSize,viewBox:"0 0 44 44"}},[_c("circle",{staticClass:"path",style:{stroke:_vm.strokeColor},attrs:{fill:"none","stroke-width":"4","stroke-linecap":"round",cx:"22",cy:"22",r:"20"}})])])},staticRenderFns=[],Spinnervue_type_script_lang_js={name:"spinner",props:["show","size","colour"],computed:{spinnerSize:function(){return this.size?this.size+"px":"50px"},strokeColor:function(){return this.colour?this.colour:"#fd4b92"}}},components_Spinnervue_type_script_lang_js=Spinnervue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(components_Spinnervue_type_script_lang_js,render,staticRenderFns,!1,null,"06a81286",null)
/* harmony default export */,Spinner=component.exports},
/***/626854:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return ModuleList}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/pages/setup/modules/ModuleList.vue?vue&type=template&id=09b23841
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"height100 overflow-hidden"},[_c("div",{staticClass:"setting-header2"},[_c("div",{staticClass:"setting-title-block"},[_c("div",{staticClass:"setting-form-title"},[_vm._v(" "+_vm._s(_vm.$t("setup.setupLabel.modules"))+" ")]),_c("div",{staticClass:"heading-description"},[_vm._v(" "+_vm._s(_vm.$t("setup.list.list_modules"))+" ")])]),"customModules"===_vm.activeTabName?_c("div",{staticClass:"action-btn setting-page-btn"},[_c("el-button",{staticClass:"setup-el-btn",attrs:{type:"primary"},on:{click:function($event){return _vm.openModuleCreation()}}},[_vm._v(_vm._s(_vm.$t("setup.add.add_moduele")))])],1):_vm._e()]),_c("el-tabs",{staticClass:"mL30",on:{"tab-click":_vm.handleClick},model:{value:_vm.activeTabName,callback:function($$v){_vm.activeTabName=$$v},expression:"activeTabName"}},[_c("el-tab-pane",{attrs:{label:_vm.$t("setup.setup.systemModules"),name:"systemModules"}}),_c("el-tab-pane",{attrs:{label:_vm.$t("setup.setup.customModules"),name:"customModules"}})],1),_c("div",{staticClass:"container-scroll"},[_c("ModulesListTemplate",{attrs:{isLoading:_vm.isLoading,moduleList:_vm.moduleList,allowModulesEdit:_vm.allowModulesEdit},on:{redirect:_vm.redirectToModule,openModuleCreation:_vm.openModuleCreation}})],1),_vm.canShowModuleCreation?_c("ModuleNew",{attrs:{canShowModuleCreation:_vm.canShowModuleCreation,moduleObj:_vm.selectedModule,isEdit:_vm.isEdit,updateModule:_vm.updateSelectedModule},on:{"update:canShowModuleCreation":function($event){_vm.canShowModuleCreation=$event},"update:can-show-module-creation":function($event){_vm.canShowModuleCreation=$event}}}):_vm._e()],1)},staticRenderFns=[],regeneratorRuntime=__webpack_require__(150124),asyncToGenerator=__webpack_require__(548534),ModulesListTemplate=(__webpack_require__(434284),__webpack_require__(670560),__webpack_require__(339772),__webpack_require__(162506),__webpack_require__(225517)),ModuleNewvue_type_template_id_c76b8868_render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",[_c("el-dialog",{staticClass:"fc-dialog-center-container",attrs:{title:_vm.isEdit?"EDIT MODULE":"NEW MODULE",visible:_vm.canShowDialog,width:"30%","append-to-body":!0},on:{"update:visible":function($event){_vm.canShowDialog=$event}}},[_c("ErrorBanner",{attrs:{error:_vm.error,errorMessage:_vm.errorMessage},on:{"update:error":function($event){_vm.error=$event},"update:errorMessage":function($event){_vm.errorMessage=$event},"update:error-message":function($event){_vm.errorMessage=$event}}}),_c("div",{staticClass:"height330"},[_c("el-form",{ref:"moduleNew",attrs:{rules:_vm.rules,model:_vm.moduleObj}},[_c("el-form-item",{staticClass:"mB10",attrs:{label:"Name",prop:"displayName",required:!0}},[_c("el-input",{staticClass:"fc-input-full-border-select2",attrs:{placeholder:_vm.$t("common._common.enter_name")},model:{value:_vm.moduleObj.displayName,callback:function($$v){_vm.$set(_vm.moduleObj,"displayName",$$v)},expression:"moduleObj.displayName"}})],1),_c("el-form-item",{staticClass:"mB10",attrs:{label:"Description",prop:"description",required:!0}},[_c("el-input",{staticClass:"fc-input-full-border-select2",attrs:{type:"textarea",autosize:{minRows:2,maxRows:4},placeholder:_vm.$t("common._common.enter_name")},model:{value:_vm.moduleObj.description,callback:function($$v){_vm.$set(_vm.moduleObj,"description",$$v)},expression:"moduleObj.description"}})],1),_c("el-form-item",{staticClass:"mB10"},[_c("el-checkbox",{model:{value:_vm.moduleObj.stateFlowEnabled,callback:function($$v){_vm.$set(_vm.moduleObj,"stateFlowEnabled",$$v)},expression:"moduleObj.stateFlowEnabled"}},[_vm._v("Enable Stateflow")])],1)],1)],1),_c("div",{staticClass:"modal-dialog-footer"},[_c("el-button",{staticClass:"modal-btn-cancel",on:{click:_vm.closeModuleCreation}},[_vm._v("Cancel")]),_c("el-button",{staticClass:"modal-btn-save",attrs:{type:"primary",loading:_vm.isSaving},on:{click:function($event){return _vm.addNewModule()}}},[_vm._v("Confirm")])],1)],1)],1)},ModuleNewvue_type_template_id_c76b8868_staticRenderFns=[],ErrorBanner=(__webpack_require__(659749),__webpack_require__(386544),__webpack_require__(634338),__webpack_require__(821057),__webpack_require__(260228),__webpack_require__(821694),__webpack_require__(76265),__webpack_require__(297157)),http=__webpack_require__(218430),ModuleNewvue_type_script_lang_js={components:{ErrorBanner:ErrorBanner/* default */.Z},props:{canShowModuleCreation:{type:Boolean,required:!0},moduleObj:{type:Object},isEdit:{type:Boolean},updateModule:{type:Function}},computed:{canShowDialog:{get:function(){return this.canShowModuleCreation},set:function(value){this.$emit("update:canShowModuleCreation",value)}}},data:function(){return{error:!1,errorMessage:null,rules:{displayName:[{required:!0,message:"Please input module name",trigger:"change"}],description:[{required:!0,message:"Please input module description",trigger:"change"}]},isSaving:!1}},methods:{addNewModule:function(){var _this=this,isEdit=this.isEdit;this.setFormValid(),this.$refs["moduleNew"].validate((function(valid){if(valid){var _this$moduleObj=_this.moduleObj,displayName=_this$moduleObj.displayName,description=_this$moduleObj.description,stateFlowEnabled=_this$moduleObj.stateFlowEnabled,name=_this$moduleObj.name,data={moduleDisplayName:displayName,description:description,stateFlowEnabled:stateFlowEnabled};isEdit&&(data.moduleName=name),_this.saveRecord(data)}}))},setFormValid:function(){this.error=!1,this.errorMessage=""},setFormInvalid:function(msg){this.error=!0,this.errorMessage=msg},closeModuleCreation:function(){this.canShowDialog=!1},saveRecord:function(data){var _this2=this,isEdit=this.isEdit,url=isEdit?"/v2/module/update":"/v2/module/add";this.isSaving=!0;var promise=http/* default */.ZP.post(url,data).then((function(_ref){var _ref$data=_ref.data,message=_ref$data.message,responseCode=_ref$data.responseCode,_ref$data$result=_ref$data.result,result=void 0===_ref$data$result?{}:_ref$data$result;if(0!==responseCode)throw new Error(message);var moduleName=result.module.name,moduleObj=result.module;if(isEdit)_this2.updateModule&&_this2.updateModule(moduleObj),_this2.closeModuleCreation(),_this2.$message.success("Module updated successfully");else{var _ref2=result||{},id=_ref2.form.id,currentPath=_this2.$router.resolve({name:"modules-details",params:{moduleName:moduleName}}).href;_this2.$router.push({path:"".concat(currentPath,"/layouts/").concat(id,"/edit")})}})).catch((function(_ref3){var message=_ref3.message;_this2.setFormInvalid(message)}));Promise.all([promise]).finally((function(){return _this2.isSaving=!1}))}}},modules_ModuleNewvue_type_script_lang_js=ModuleNewvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(modules_ModuleNewvue_type_script_lang_js,ModuleNewvue_type_template_id_c76b8868_render,ModuleNewvue_type_template_id_c76b8868_staticRenderFns,!1,null,null,null)
/* harmony default export */,ModuleNew=component.exports,validation=__webpack_require__(990260),utility_methods=__webpack_require__(773258),api=__webpack_require__(32284),ModuleListvue_type_script_lang_js={components:{ModuleNew:ModuleNew,ModulesListTemplate:ModulesListTemplate/* default */.Z},data:function(){return{moduleList:[],allowModulesEdit:!1,activeTabName:"systemModules",modulesObj:{},isLoading:!1,canShowModuleCreation:!1,selectedModule:{displayName:null,description:null,stateFlowEnabled:!1},isEdit:!1}},created:function(){var _this=this;return(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee(){var url,_yield$API$get,error,data,modulesObj;return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context){while(1)switch(_context.prev=_context.next){case 0:return _this.isLoading=!0,url="/v2/module/lists?defaultModules=true",_context.next=4,api/* API */.bl.get(url);case 4:_yield$API$get=_context.sent,error=_yield$API$get.error,data=_yield$API$get.data,error?_this.$message.error(error.message||"Error Occurred"):(modulesObj=data.modules,_this.modulesObj=modulesObj,_this.moduleList=modulesObj[_this.activeTabName]),_this.isLoading=!1;case 9:case"end":return _context.stop()}}),_callee)})))()},methods:{handleClick:function(){this.moduleList=this.modulesObj[this.activeTabName],"customModules"===this.activeTabName?this.allowModulesEdit=!0:this.allowModulesEdit=!1},openModuleCreation:function(module){this.$set(this,"canShowModuleCreation",!0),(0,validation/* isEmpty */.xb)(module)?(this.$set(this,"selectedModule",{displayName:null,description:null}),this.$set(this,"isEdit",!1)):(this.$set(this,"selectedModule",(0,utility_methods/* deepCloneObject */.kh)(module)),this.$set(this,"isEdit",!0))},redirectToModule:function(module){var moduleName=module.name;this.$router.push({name:"modules-details",params:{moduleName:moduleName}})},updateSelectedModule:function(selectedModule){var moduleList=this.moduleList,selectedModuleIndex=(moduleList||[]).findIndex((function(module){return module.moduleId===selectedModule.moduleId}));moduleList.splice(selectedModuleIndex,1,selectedModule),this.$set(this,"moduleList",moduleList)}}},modules_ModuleListvue_type_script_lang_js=ModuleListvue_type_script_lang_js,ModuleList_component=(0,componentNormalizer/* default */.Z)(modules_ModuleListvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,ModuleList=ModuleList_component.exports},
/***/225517:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// EXPORTS
__webpack_require__.d(__webpack_exports__,{Z:function(){/* binding */return ModulesListTemplate}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/pages/setup/modules/ModulesListTemplate.vue?vue&type=template&id=10daa121
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _vm.isLoading?_c("div",[_c("Spinner",{attrs:{show:_vm.isLoading}})],1):_c("div",{staticClass:"container-scroll"},[_c("div",{staticClass:"d-flex flex-direction-row mL20 mR20 flex-wrap mB10"},_vm._l(_vm.moduleList,(function(module,index){return _c("div",{key:index,staticClass:"modules-card mT25 mR25",on:{click:function($event){return _vm.redirectToModule(module)}}},[_c("div",{staticClass:"d-flex"},[_c("div",{staticClass:"fc-black-color f16 bold letter-spacing0_5 text-capitalize"},[_vm._v(" "+_vm._s(module.displayName)+" ")]),_vm.allowModulesEdit?_c("div",{staticClass:"mL-auto module-edit",on:{click:function($event){return $event.stopPropagation(),_vm.openModuleCreation(module)}}},[_c("inline-svg",{staticClass:"vertical-middle",attrs:{src:"svgs/edit",iconClass:"icon icon-sm mR5 icon-edit"}})],1):_vm._e()]),module.custom?_c("div",{staticClass:"created-time f12 letter-spacing0_5 mT5"},[_vm._v(" "+_vm._s(_vm._f("formatDate")(module.createdTime))+" ")]):_vm._e(),_c("div",{staticClass:"d-flex mT30"},[_c("div",[_c("div",{staticClass:"fc-black-color f14 bold letter-spacing0_5"},[_vm._v(" "+_vm._s(_vm.$t("custommodules.list.state_flows"))+" ")]),_c("div",{staticClass:"stateflow-status f13 mT5",class:module.stateFlowEnabled||!module.custom?"":"disabled"},[_vm._v(" "+_vm._s(module.stateFlowEnabled||!module.custom?_vm.$t("custommodules.list.enabled"):_vm.$t("custommodules.list.disabled"))+" ")])])]),_c("div",{staticClass:"modules-description fc-black-color f13 letter-spacing0_5 mT20"},[_vm._v(" "+_vm._s(module.description)+" ")])])})),0)])},staticRenderFns=[],Spinner=__webpack_require__(604947),ModulesListTemplatevue_type_script_lang_js={components:{Spinner:Spinner/* default */.Z},props:{isLoading:{type:Boolean},allowModulesEdit:{type:Boolean},moduleList:{type:Array}},methods:{redirectToModule:function(module){this.$emit("redirect",module)},openModuleCreation:function(module){this.$emit("openModuleCreation",module)}}},modules_ModulesListTemplatevue_type_script_lang_js=ModulesListTemplatevue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(modules_ModulesListTemplatevue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,ModulesListTemplate=component.exports}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/26854.js.map