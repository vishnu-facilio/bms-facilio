"use strict";(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[22144],{
/***/322144:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return SafetyPlanCommonList}});
// EXTERNAL MODULE: ./node_modules/core-js/modules/es.function.name.js
__webpack_require__(434284),__webpack_require__(670560);
// EXTERNAL MODULE: ./node_modules/core-js/modules/es.array.push.js
var render,staticRenderFns,CommonModuleList=__webpack_require__(347446),router=__webpack_require__(329435),SafetyPlanCommonListvue_type_script_lang_js={extends:CommonModuleList["default"],computed:{parentPath:function(){return"/app/sf/safetyplan/"}},methods:{redirectToOverview:function(id){var moduleName=this.moduleName,viewname=this.viewname;if((0,router/* isWebTabsEnabled */.tj)()){var _ref=(0,router/* findRouteForModule */.Jp)(moduleName,router/* pageTypes */.As.OVERVIEW)||{},name=_ref.name,route={name:name,params:{viewname:viewname,id:id},query:this.$route.query};return name&&route}return{name:"safetyPlanSummary",params:{viewname:viewname,id:id},query:this.$route.query}},editModule:function(row){var moduleName=this.moduleName,id=row.id;if((0,router/* isWebTabsEnabled */.tj)()){var _ref2=(0,router/* findRouteForModule */.Jp)(moduleName,router/* pageTypes */.As.EDIT)||{},name=_ref2.name;name&&this.$router.push({name:name,params:{id:id}})}else this.$router.push({name:"safetyplan-edit",params:{id:id}})},redirectToFormCreation:function(){var moduleName=this.moduleName;if((0,router/* isWebTabsEnabled */.tj)()){var _ref3=(0,router/* findRouteForModule */.Jp)(moduleName,router/* pageTypes */.As.CREATE)||{},name=_ref3.name;name&&this.$router.push({name:name})}else this.$router.push({name:"safetyplan-new"})},openList:function(){var viewname=this.viewname;if((0,router/* isWebTabsEnabled */.tj)()){var _ref4=(0,router/* findRouteForModule */.Jp)(this.moduleName,router/* pageTypes */.As.LIST)||{},name=_ref4.name;name&&this.$router.push({name:name,params:{viewname:viewname},query:this.$route.query})}else this.$router.push({name:"safetyplan",params:{viewname:viewname},query:this.$route.query})}}},sp_SafetyPlanCommonListvue_type_script_lang_js=SafetyPlanCommonListvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(sp_SafetyPlanCommonListvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,SafetyPlanCommonList=component.exports}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/22144.js.map