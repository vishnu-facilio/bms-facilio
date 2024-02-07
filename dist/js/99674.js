"use strict";(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[99674],{
/***/407658:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return SupplierElectricityWidget}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/pages/etisalat/suppliers/SupplierElectricityWidget.vue?vue&type=template&id=1525185a
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _vm.loading?_c("div",[_c("spinner",{attrs:{show:_vm.loading}})],1):_c("div",[_c("div",{staticClass:"visibility-visible-actions width100"},[_c("div",{staticClass:"width100 flex-center-row-space pL15 pT20 inline-block"},[_c("div",{staticClass:"fc-black-14 text-left bold"},[_c("el-select",{staticClass:"fc-input-full-border2 width30",attrs:{placeholder:"Select tarrif"},on:{change:function($event){return _vm.selectTarrif()}},model:{value:_vm.selectedSlabName,callback:function($$v){_vm.selectedSlabName=$$v},expression:"selectedSlabName"}},_vm._l(_vm.tariffs,(function(tariff,index){return _c("el-option",{key:index,attrs:{label:tariff.name,value:tariff.name}})})),1)],1),_vm.tariff?_c("div",{staticClass:"width100 flex-center-row-space pT20"},[_c("div",{},[_vm.getFieldData("date")||_vm.getFieldData("date_1")?_c("div",{staticClass:"bold label-txt-black"},[_vm._v(" "+_vm._s(_vm.getFieldData("date")?_vm.getFieldData("date"):"All Time")+" - "+_vm._s(_vm.getFieldData("date_1")?_vm.getFieldData("date_1"):"All Time")+" ")]):_c("div",{staticClass:"bold label-txt-black"},[_vm._v(" All Time ")]),_c("div",{staticClass:"fc-black-12 text-left line-height30"},[_vm._v(" Cost Type : "+_vm._s(_vm.getFieldData("picklist_1"))+" ")])])]):_vm._e()]),_vm.tariff?_c("div",[_c("div",["Fixed"===_vm.getFieldData("picklist_1")?_c("table",{staticClass:"fc-etislat-tariff-table mT10"},[_c("thead",[_c("th",{staticStyle:{width:"17%"}},[_vm._v(" "+_vm._s("RATE")+" ")]),_c("th",{staticStyle:{width:"17%"}},[_vm._v(" "+_vm._s("Unit")+" ")])]),_c("tbody",[_c("tr",[_c("td",[_vm._v(" "+_vm._s(_vm.tariff.decimal?_vm.formatCurrency(_vm.tariff.decimal)+" AED":"0 AED")+" ")]),_c("td",[_vm._v(" "+_vm._s(_vm.tariff.singleline?_vm.tariff.singleline:"")+" ")])])])]):_c("table",{staticClass:"fc-etislat-tariff-table mT10"},[_c("thead",[_c("th",{staticStyle:{width:"17%"}},[_vm._v(" "+_vm._s(_vm.$t("etisalat.etisalat.from"))+" ")]),_c("th",{staticStyle:{width:"17%"}},[_vm._v(" "+_vm._s(_vm.$t("etisalat.etisalat.to"))+" ")]),_c("th",[_vm._v(" "+_vm._s(_vm.$t("etisalat.etisalat.rete_in_aed"))+" ")])]),_vm.tariff.slabs&&_vm.tariff.slabs.length>0?_c("tbody",_vm._l(_vm.tariff.slabs,(function(slab,index){return _c("tr",{key:index},[_c("td",[_vm._v(" "+_vm._s(slab.decimal?slab.decimal:"---")+" ")]),_c("td",[_vm._v(" "+_vm._s(slab.decimal_1?slab.decimal_1:"---")+" ")]),_c("td",[_vm._v(" "+_vm._s(slab.decimal_2?_vm.formatCurrency(slab.decimal_2)+" AED":"0 AED")+" ")])])})),0):_c("tbody",[_vm.loading?_c("tr",{staticClass:"nodata"},[_c("td",{staticClass:"text-center p30imp",attrs:{colspan:"100%"}},[_c("spinner",{attrs:{show:_vm.loading}})],1)]):_c("tr",{staticClass:"nodata"},[_c("td",{staticClass:"text-center p30imp",attrs:{colspan:"100%"}},[_c("div",{staticClass:"mT40"},[_c("InlineSvg",{attrs:{src:"svgs/emptystate/readings-empty",iconClass:"icon text-center icon-xxxxlg emptystate-icon-size"}}),_c("div",{staticClass:"pT20 fc-black-dark f18 bold"},[_vm._v(" No tariff slabs available! ")])],1)])])])])])]):_c("div",{staticClass:"mT100 self-center text-center"},[_c("InlineSvg",{attrs:{src:"svgs/emptystate/readings-empty",iconClass:"icon text-center icon-xxxxlg emptystate-icon-size"}}),_c("div",{staticClass:"pT20 fc-black-dark f18 bold"},[_vm._v(" No tariff slabs available! ")])],1)])])},staticRenderFns=[],formatter=(__webpack_require__(670560),__webpack_require__(425728),__webpack_require__(260228),__webpack_require__(434284),__webpack_require__(676240)),moment_timezone=__webpack_require__(480008),moment_timezone_default=__webpack_require__.n(moment_timezone),SupplierElectricityWidgetvue_type_script_lang_js={props:["details","moduleName"],data:function(){return{pdfopen:!1,loading:!1,currentPage:0,pageCount:0,result:{},tariff:null,tariffs:[],selectedSlabName:null,params:{nameSpace:"SupplierSummary",functionName:"getSummaryData",paramList:[]}}},mounted:function(){this.getData()},methods:{selectTarrif:function(){this.getTarrif()},formatCurrency:function(date){return(0,formatter/* formatCurrency */.xG)(date,2)},getData:function(){var _this=this,supplierrid=this.details.id;this.loading=!0,this.params.paramList.push(supplierrid),this.$http.post("/v2/workflow/runWorkflow",this.params).then((function(resp){resp.data.result&&resp.data.result.workflow&&resp.data.result.workflow.returnValue&&(_this.result=resp.data.result.workflow.returnValue,_this.tariffs=resp.data.result.workflow.returnValue.tariffs||[],_this.getTarrif()),_this.loading=!1}))},getTarrif:function(){var _this2=this;this.selectedSlabName?this.tariff=this.tariffs.find((function(rt){return rt.name===_this2.selectedSlabName})):this.tariffs.length&&(this.tariff=this.tariffs[0],this.selectedSlabName=this.tariff.name)},getFieldData:function(fieldName){if(fieldName){if("date"===fieldName)return this.tariff[fieldName]?moment_timezone_default()(this.tariff[fieldName]).tz(this.$timezone).format("DD MMM YYYY"):"";if("date_1"===fieldName)return this.tariff[fieldName]?moment_timezone_default()(this.tariff[fieldName]).tz(this.$timezone).format("DD MMM YYYY"):"";if("picklist_1"===fieldName){var picklist=this.tariff.picklist_1;if(1===picklist)return"Slab";if(2===picklist)return"Fixed"}else{if("number"===fieldName)return this.tariff.number||"---";if("singleline"===fieldName)return this.tariff.number||"---"}}return""}}},suppliers_SupplierElectricityWidgetvue_type_script_lang_js=SupplierElectricityWidgetvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(suppliers_SupplierElectricityWidgetvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,SupplierElectricityWidget=component.exports}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/99674.js.map