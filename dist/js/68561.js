(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[68561],{
/***/68561:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){"use strict";
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return AgentIntegration}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/agent/AgentIntegration.vue?vue&type=template&id=67db3fc8
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"page-width-cal"},[_c("el-header",{staticClass:"fc-agent-main-header",attrs:{height:"80"}},[_c("div",{staticClass:"flex-middle justify-content-space"},[_c("div",{staticClass:"fc-agent-black-26"},[_vm._v(" Integration ")]),_c("div",{},[_c("el-button",{staticClass:"fc-agent-add-btn"},[_c("i",{staticClass:"el-icon-plus pR5 fwBold"}),_vm._v("Add Integration ")])],1)])]),_c("div",{staticClass:"agent-integration-con"},[_c("div",{staticClass:"agent-integration-card",on:{click:function($event){return _vm.showWattsenseform()}}},[_c("img",{attrs:{src:__webpack_require__(446217),width:"200"}}),_c("el-button",{staticClass:"fc-agent-add-btn mT40 width100",on:{click:function($event){_vm.wattsenseFormDialog=!0}}},[_vm._v("Integrate")])],1),_c("div",{staticClass:"agent-integration-card"},[_c("img",{attrs:{src:__webpack_require__(497586),width:"200"}}),_c("el-button",{staticClass:"fc-agent-add-btn mT40 width100",on:{click:function($event){_vm.altairFormdialog=!0}}},[_vm._v("Integrate")])],1)]),_vm.wattsenseFormDialog?_c("watt-sense-form",{attrs:{visibility:_vm.wattsenseFormDialog},on:{"update:visibility":function($event){_vm.wattsenseFormDialog=$event}}}):_vm._e(),_vm.altairFormdialog?_c("altair-form",{attrs:{visibility:_vm.altairFormdialog},on:{"update:visibility":function($event){_vm.altairFormdialog=$event}}}):_vm._e()],1)},staticRenderFns=[],AgentWattSenseFormvue_type_template_id_1313ddbb_render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",[_c("el-dialog",{attrs:{title:"WattSense",visible:_vm.visibility,"before-close":_vm.closeDialog,"append-to-body":!0,"custom-class":"fc-dialog-form fc-dialog-right setup-dialog40 setup-dialog slideInRight"},on:{"update:visible":function($event){_vm.visibility=$event}}},[_c("div",{staticClass:"new-header-container"},[_c("div",{staticClass:"new-header-text"},[_c("div",{staticClass:"fc-setup-modal-title"},[_vm._v(" New Wattsense ")])])]),_c("div",{staticClass:"new-body-modal"}),_c("div",{staticClass:"modal-dialog-footer"},[_c("el-button",{staticClass:"modal-btn-cancel",on:{click:function($event){return _vm.closeDialog()}}},[_vm._v("CANCEL")]),_c("el-button",{staticClass:"modal-btn-save",attrs:{type:"primary"}},[_vm._v("Save")])],1)])],1)},AgentWattSenseFormvue_type_template_id_1313ddbb_staticRenderFns=[],AgentWattSenseFormvue_type_script_lang_js={props:["visibility"],data:function(){return{}},methods:{closeDialog:function(){this.$emit("update:visibility",!1)}}},components_AgentWattSenseFormvue_type_script_lang_js=AgentWattSenseFormvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(components_AgentWattSenseFormvue_type_script_lang_js,AgentWattSenseFormvue_type_template_id_1313ddbb_render,AgentWattSenseFormvue_type_template_id_1313ddbb_staticRenderFns,!1,null,null,null)
/* harmony default export */,AgentWattSenseForm=component.exports,AgentAltairFormvue_type_template_id_27630fa8_render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",[_c("el-dialog",{attrs:{title:"WattSense",visible:_vm.visibility,width:"50%","before-close":_vm.closeDialog,"append-to-body":!0,"custom-class":"fc-dialog-form fc-dialog-right setup-dialog40 setup-dialog slideInRight"},on:{"update:visible":function($event){_vm.visibility=$event}}},[_c("div",{staticClass:"new-header-container"},[_c("div",{staticClass:"new-header-text"},[_c("div",{staticClass:"fc-setup-modal-title"},[_vm._v(" New Altair ")])])]),_c("div",{staticClass:"new-body-modal"}),_c("div",{staticClass:"modal-dialog-footer"},[_c("el-button",{staticClass:"modal-btn-cancel",on:{click:function($event){return _vm.closeDialog()}}},[_vm._v("CANCEL")]),_c("el-button",{staticClass:"modal-btn-save",attrs:{type:"primary"}},[_vm._v("Save")])],1)])],1)},AgentAltairFormvue_type_template_id_27630fa8_staticRenderFns=[],AgentAltairFormvue_type_script_lang_js={props:["visibility"],data:function(){return{}},methods:{closeDialog:function(){this.$emit("update:visibility",!1)}}},components_AgentAltairFormvue_type_script_lang_js=AgentAltairFormvue_type_script_lang_js,AgentAltairForm_component=(0,componentNormalizer/* default */.Z)(components_AgentAltairFormvue_type_script_lang_js,AgentAltairFormvue_type_template_id_27630fa8_render,AgentAltairFormvue_type_template_id_27630fa8_staticRenderFns,!1,null,null,null)
/* harmony default export */,AgentAltairForm=AgentAltairForm_component.exports,AgentIntegrationvue_type_script_lang_js={data:function(){return{wattsenseFormDialog:!1,altairFormdialog:!1}},components:{wattSenseForm:AgentWattSenseForm,altairForm:AgentAltairForm},methods:{showWattsenseform:function(){this.wattsenseFormDialog=!0},showAltairform:function(){this.altairFormdialog=!0}}},agent_AgentIntegrationvue_type_script_lang_js=AgentIntegrationvue_type_script_lang_js,AgentIntegration_component=(0,componentNormalizer/* default */.Z)(agent_AgentIntegrationvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,AgentIntegration=AgentIntegration_component.exports},
/***/497586:
/***/function(module,__unused_webpack_exports,__webpack_require__){module.exports=__webpack_require__.p+"0297e5c693243a174655ee6d052a9a8a.svg";
/***/},
/***/446217:
/***/function(module,__unused_webpack_exports,__webpack_require__){module.exports=__webpack_require__.p+"4440d389417a437f614af028f292c73f.svg";
/***/}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/68561.js.map