"use strict";(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[83726],{
/***/983726:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return ConnectedAppView}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/pages/connectedapps/ConnectedAppView.vue?vue&type=template&id=91be84d0
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("connected-app-view-widget",{ref:"ref-"+_vm.widgetid,attrs:{widgetId:_vm.widgetid,handlers:_vm.handlers}})},staticRenderFns=[],validation=(__webpack_require__(260228),__webpack_require__(821694),__webpack_require__(76265),__webpack_require__(990260)),ConnectedAppViewvue_type_script_lang_js={props:["widgetid"],components:{ConnectedAppViewWidget:function(){return __webpack_require__.e(/* import() */67004).then(__webpack_require__.bind(__webpack_require__,767004))}},data:function(){return{handlers:{}}},created:function(){this.initWidgetHandlers()},methods:{initWidgetHandlers:function(){var _this=this,subscribe=function(_ref){var topic=_ref.topic;topic&&_this.$wms.subscribe(topic,_this.onLiveData)},unsubscribe=function(_ref2){var topic=_ref2.topic;topic&&_this.$wms.unsubscribe(topic,_this.onLiveData)};
// live data subscribe
this.handlers={subscribe:subscribe,unsubscribe:unsubscribe}},onLiveData:function(eventData){this.sendEvent("live.data.received",eventData)},sendEvent:function(eventName,eventData){var id=this.widgetid,refElement=this.$refs["ref-".concat(id)];(0,validation/* isEmpty */.xb)(refElement)||refElement.sendEvent(eventName,eventData)}}},connectedapps_ConnectedAppViewvue_type_script_lang_js=ConnectedAppViewvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(connectedapps_ConnectedAppViewvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,ConnectedAppView=component.exports}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/83726.js.map