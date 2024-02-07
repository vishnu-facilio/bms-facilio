"use strict";(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[24622],{
/***/124622:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return PmReadingsWidget}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/pages/dashboard/widget/cards/v2/PmReadingsWidget.vue?vue&type=template&id=2c9ae28a&scoped=true
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"dragabale-card height100 "},[_vm.loading?_c("div",{staticClass:"d-flex flex-direction-column justify-center"},[_c("spinner",{attrs:{show:_vm.loading}})],1):_c("div",{staticClass:"linear-gauge dragabale-card fc-widget height-100"},[_c("div",[_c("div",{staticClass:"pm-reading-table-overlay"}),_c("f-pm-readingtable",{attrs:{pmObject:_vm.pmObject,pm:_vm.pm,workorder:_vm.workorder,config:_vm.data.config,settings:_vm.settings}})],1)])])},staticRenderFns=[],validation=(__webpack_require__(648324),__webpack_require__(169358),__webpack_require__(990260)),PmReadingsable=__webpack_require__(131792),PmReadingsWidgetvue_type_script_lang_js={props:["widget","config"],data:function(){return{loading:!1,result:null,settings:null,data:{style:{color:"red",bgcolor:"pink"},unitString:"",workflowV2String:""},pm:null,workorder:null,pmObject:null}},components:{FPmReadingtable:PmReadingsable/* default */.Z},mounted:function(){this.getClientProps(),this.getParams(),this.loadpm()},methods:{getClientProps:function(){var _this=this;this.$nextTick((function(){_this.$el?_this.settings={height:_this.$el.clientHeight-67}:_this.settings={height:200}}))},loadpm:function(){var _this2=this;this.loading=!0,this.$http.get("/workorder/preventiveMaintenanceSummary?id="+parseInt(this.data.pmId)).then((function(response){_this2.pm=response.data.preventivemaintenance,_this2.workorder=response.data.workorder,_this2.pmObject=response.data,_this2.loading=!1}))},refresh:function(){this.updateCard()},getParams:function(){this.widget.dataOptions.data?(this.widget.dataOptions.metaJson=JSON.stringify(this.widget.dataOptions.data),this.data=this.widget.dataOptions.data,this.setParams()):this.widget.id>-1&&this.widget.dataOptions.metaJson&&(this.data=JSON.parse(this.widget.dataOptions.metaJson),this.setParams())},setParams:function(){this.data&&!this.data.style?this.data.style={color:this.color,bgcolor:this.bgcolor}:this.style=this.data.style,this.widget.dataOptions.metaJson=JSON.stringify(this.data)},updateCard:function(){var self=this,params=null;self.getParams(),params={workflow:{isV2Script:!0,workflowV2String:this.data.workflowV2String},staticKey:"kpiCard"},this.loading=!0,self.$http.post("dashboard/getCardData",params).then((function(response){self.getData(response.data.cardResult),self.loading=!1})).catch((function(error){self.loading=!1}))},
// loadCardData() {
//   let self = this
//   let params = null
//   if (this.widget && this.widget.id > -1) {
//     params = {
//       widgetId: self.widget.id,
//     }
//   } else {
//     params = {
//       workflow: {
//         isV2Script: true,
//         workflowV2String: this.data.workflowV2String,
//       },
//       staticKey: 'kpiCard',
//     }
//   }
//   this.loading = true
//   return self.$http
//     .post('dashboard/getCardData', params)
//     .then(function(response) {
//       self.getData(response.data.cardResult)
//       self.loading = false
//     })
//     .catch(function(error) {
//       console.log('******** error', error)
//       self.loading = false
//     })
// },
getData:function(data){data.hasOwnProperty("result")&&(this.result=data.result)},getFormattedValue:function(value){return(0,validation/* isEmpty */.xb)(value)?"--":"".concat(value)}}},v2_PmReadingsWidgetvue_type_script_lang_js=PmReadingsWidgetvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(v2_PmReadingsWidgetvue_type_script_lang_js,render,staticRenderFns,!1,null,"2c9ae28a",null)
/* harmony default export */,PmReadingsWidget=component.exports}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/24622.js.map