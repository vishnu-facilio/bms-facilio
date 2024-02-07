"use strict";(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[88801,76712],{
/***/268468:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return mv_MVList}});
// EXTERNAL MODULE: ./node_modules/core-js/modules/es.array.push.js
__webpack_require__(670560),__webpack_require__(634338);
// EXTERNAL MODULE: ./node_modules/core-js/modules/es.array.concat.js
var render,staticRenderFns,MVList=__webpack_require__(251911),router=__webpack_require__(329435),MVListvue_type_script_lang_js={extends:MVList["default"],data:function(){return{views:[{name:"open",label:"Open Projects",path:"/app/en/mv/open"},{name:"closed",label:"Closed Projects",path:"/app/en/mv/closed"}]}},methods:{redirectToFormCreation:function(){if((0,router/* isWebTabsEnabled */.tj)()){var parentPath=this.parentPath,path="".concat(parentPath,"/new");this.$router.push({path:path})}else this.$router.push({path:"/app/en/mv/project/new"})},redirectToSummary:function(projectId){if((0,router/* isWebTabsEnabled */.tj)()){var _this$currentView=this.currentView,currentView=void 0===_this$currentView?"open":_this$currentView,parentPath=this.parentPath,path="".concat(parentPath,"/").concat(currentView,"/").concat(projectId,"/overview");this.$router.push({path:path})}else this.$router.push({name:"mv-energy-project-summary",params:{id:projectId}})},editMVProject:function(projectId){if((0,router/* isWebTabsEnabled */.tj)()){var parentPath=this.parentPath,path="".concat(parentPath,"/edit/").concat(projectId);this.$router.push({path:path})}else this.$router.push({name:"mv-energy-project-edit",params:{id:projectId}})}}},mv_MVListvue_type_script_lang_js=MVListvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(mv_MVListvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,mv_MVList=component.exports}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/88801.js.map