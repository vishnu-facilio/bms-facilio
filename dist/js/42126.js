(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[42126],{
/***/42126:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){"use strict";
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return BimFiles}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/pages/bim/BimFiles.vue?vue&type=template&id=59f27ee9
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"layout-padding"},[_c("div",{staticClass:"fc-form"},[_c("div",{staticClass:"fc-form-title setting-form-title"},[_c("el-button",{staticClass:"modal-btn-save",attrs:{type:"primary"},on:{click:function($event){return _vm.importNewFile()}}},[_vm._v(_vm._s("IMPORT NEW FILE")+" ")]),_c("table",{staticClass:" pT15 fc-list-view-table border-right-table"},[_vm._m(0),_c("tbody",_vm._l(_vm.bimIntegrationLogs,(function(bim){return _c("tr",{key:bim.id,staticClass:"tablerow"},[_c("td",[_vm._v(" "+_vm._s(bim.fileName)+" ")]),_c("td",[_vm._v(" "+_vm._s(bim.siteName)+" ")]),_c("td",{staticClass:"contentrow"},[1==bim.status?_c("img",{staticClass:"svg-icon",attrs:{src:__webpack_require__(532984)}}):2==bim.status?_c("img",{staticClass:"svg-icon",attrs:{src:__webpack_require__(505133)}}):_c("img",{staticClass:"svg-icon",attrs:{src:__webpack_require__(37498)}})]),_c("td",[0==bim.importedTime?_c("span",[_vm._v(" --- ")]):_c("span",[_vm._v(_vm._s(_vm._f("formatDate")(bim.importedTime)))])])])})),0)])],1)])])},staticRenderFns=[function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("thead",[_c("tr",{staticClass:"tablerow pT30",staticStyle:{"background-color":"#ffffff",padding:"15px"}},[_c("td",{staticClass:"headerrow"},[_vm._v("FILE NAME")]),_c("td",{staticClass:"headerrow"},[_vm._v("SITE NAME")]),_c("td",{staticClass:"headerrow "},[_vm._v("STATUS")]),_c("td",{staticClass:"headerrow "},[_vm._v("IMPORTED TIME")])])])}],BimFilesvue_type_script_lang_js=(__webpack_require__(670560),{data:function(){return{bimIntegrationLogs:[]}},mounted:function(){this.getBimIntegrationLogs()},methods:{getBimIntegrationLogs:function(){var _this=this;this.$http.get("/v2/bimIntegration/get").then((function(response){response.data&&(0!==response.data.responseCode?_this.$message.error(response.data.message):_this.bimIntegrationLogs=response.data.result.bimIntegrationList)})).catch((function(error){_this.$message.error(response.data.message)}))},importNewFile:function(){this.$router.push({path:"/app/setup/bim/bimintegration"})}}}),bim_BimFilesvue_type_script_lang_js=BimFilesvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(bim_BimFilesvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,BimFiles=component.exports},
/***/37498:
/***/function(module,__unused_webpack_exports,__webpack_require__){module.exports=__webpack_require__.p+"7396d9cc2b4519903784b74ba528ef9c.svg";
/***/},
/***/532984:
/***/function(module,__unused_webpack_exports,__webpack_require__){module.exports=__webpack_require__.p+"3ccddbef834dba5422ecc727eae2044d.svg";
/***/},
/***/505133:
/***/function(module,__unused_webpack_exports,__webpack_require__){module.exports=__webpack_require__.p+"f41d25ffa8c522e2f49990db0706c1c9.svg";
/***/}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/42126.js.map