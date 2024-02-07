"use strict";(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[43253],{
/***/720096:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// EXPORTS
__webpack_require__.d(__webpack_exports__,{Z:function(){/* binding */return PreviewFile}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/components/PreviewFile.vue?vue&type=template&id=447c7e89
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _vm.currentFile?_c("div",{directives:[{name:"show",rawName:"v-show",value:_vm.show,expression:"show"}],staticClass:"preview-file"},[_c("div",{staticClass:"top-toolbar"},[_c("div",{staticClass:"a-top-toolbar"},[_c("div",{staticClass:"file-name word-break-keep"},[_vm._v(" "+_vm._s(_vm.currentFile.fileName)+" ")]),_c("div",{staticClass:"top-toolbar-icon-right flex align-center"},[_vm.currentFile.contentType.includes("image")?[_c("div",{staticClass:"flex-middle pL15"},[_vm.showOriginalImage?_c("div",{staticClass:"fc-white-14 word-break-keep pointer",on:{click:_vm.showOriginalPhoto}},[_vm._v(" "+_vm._s(_vm.$t("common._common.view_original"))+" ")]):_vm._e(),_vm.showCompressedImage?_c("div",{staticClass:"fc-white-14 word-break-keep pointer",on:{click:_vm.showCompressedPhoto}},[_vm._v(" "+_vm._s(_vm.$t("common._common.view_default"))+" ")]):_vm._e()]),_c("div",{staticClass:"download-icon round flex-middle pL15",on:{click:function($event){return _vm.downloadAttachment(_vm.currentFile)}}},[_c("i",{staticClass:"el-icon-download op8",staticStyle:{color:"#ffff"}})])]:_vm._e(),_vm.currentFile.contentType.includes("pdf")?[_c("div",{staticClass:"download-icon round flex-middle pL15",on:{click:function($event){return _vm.downloadAttachment(_vm.currentFile)}}},[_c("i",{staticClass:"el-icon-download op8",staticStyle:{color:"#ffff"}})])]:_vm._e(),_vm.showDeleteOption()?_c("div",{staticClass:"flex-middle pL15",on:{click:function($event){return _vm.deletePreview()}}},[_c("inline-svg",{staticClass:"pointer edit-icon-color close vertical-text-top",attrs:{src:"svgs/delete",iconClass:"icon icon-md"}})],1):_vm._e(),_c("div",{staticClass:"pL15"},[_c("i",{staticClass:"el-icon-close close",on:{click:_vm.closePreview}})])],2)])]),_vm.currentFileIndex>0?_c("a",{staticClass:"el-icon-arrow-left prev",staticStyle:{"font-size":"30px"},on:{click:function($event){return _vm.plusSlides(-1)}}}):_vm._e(),_vm.currentFileIndex<_vm.files.length-1?_c("a",{staticClass:"el-icon-arrow-right next",staticStyle:{"font-size":"30px"},on:{click:function($event){return _vm.plusSlides(1)}}}):_vm._e(),_c("div",{staticClass:"preview-content"},[_vm.currentFile.contentType.includes("image")?[_vm.compressedImage?_c("img",{staticClass:"content",staticStyle:{"box-shadow":"none"},attrs:{src:_vm.$prependBaseUrl(_vm.currentFile.previewUrl)}}):_vm.originalImage?_c("img",{staticClass:"content original",staticStyle:{"box-shadow":"none"},attrs:{src:_vm.$prependBaseUrl(_vm.currentFile.previewUrl+"?fetchOriginal=true")}}):_vm._e()]:_vm.currentFile.contentType.includes("pdf")?_c("div",{staticClass:"width100 height-100"},[_c("iframe",{staticClass:"fc-preview-pdf",attrs:{src:_vm.$prependBaseUrl(_vm.currentFile.previewUrl),height:"100%",width:"100%"}})]):_vm.isVideoSupported(_vm.currentFile.contentType)?_c("div",{staticClass:"width100 height-100 fc-preview-video"},[_c("video",{attrs:{controls:""}},[_c("source",{attrs:{src:_vm.$prependBaseUrl(_vm.$getProperty(_vm.currentFile,"downloadUrl",null)),type:_vm.$getProperty(_vm.currentFile,"contentType")}}),_vm._v(" "+_vm._s(_vm.$t("common._common.browser_not_supported"))+" ")])]):_c("div",{staticClass:"no-preview"},[_c("div",{staticClass:"flex justify-center pT24"},[_c("inline-svg",{staticClass:"d-flex",attrs:{src:_vm.getFileIcon(_vm.currentFile),iconClass:"icon icon-60 overlay"}})],1),_c("div",{staticClass:"f14 pT12 pB12"},[_vm._v("No preview available")]),_c("div",{staticClass:"download-button"},[_c("a",{staticClass:"flex flex-row",staticStyle:{color:"#ffff"},on:{click:function($event){return _vm.downloadAttachment(_vm.currentFile)}}},[_c("div",[_vm._v("Download")])])])])],2),_vm.exportDownloadUrl?_c("iframe",{staticStyle:{display:"none"},attrs:{src:_vm.exportDownloadUrl}}):_vm._e()]):_vm._e()},staticRenderFns=[],validation=(__webpack_require__(339772),__webpack_require__(260228),__webpack_require__(976801),__webpack_require__(843843),__webpack_require__(228436),__webpack_require__(990260)),PreviewFilevue_type_script_lang_js={props:["previewFile","files","visibility","showDelete","isLockedState","isTaskClosed"],data:function(){return{show:!0,previewUrl:null,currentFileIndex:0,currentFile:null,imageMargin:null,exportDownloadUrl:null,originalImage:!1,compressedImage:!0,showOriginalImage:!0,showCompressedImage:!1}},destroyed:function(){window.removeEventListener("keyup",this.handler)},mounted:function(){window.addEventListener("keyup",this.handler),this.preloadImages()},watch:{},computed:{},methods:{handler:function(){
// If escape key is pressed...
27===event.keyCode?this.closePreview():37===event.keyCode?this.plusSlides(-1):39===event.keyCode&&this.plusSlides(1)},preloadImages:function(){this.currentFile=this.previewFile?this.previewFile:this.files[0],this.currentFileIndex=this.files.indexOf(this.currentFile)},plusSlides:function(n){var index=this.currentFileIndex+n;index<0&&(index=0),index>=this.files.length&&(index=this.files.length-1),this.currentFileIndex=index,this.currentFile=this.files[index]},closePreview:function(){this.$emit("update:visibility",!1)},deletePreview:function(){var currentFile=this.currentFile;this.$emit("onDelete",currentFile)},downloadAttachment:function(file){var _this=this,_ref=file||{},url=_ref.downloadUrl;this.exportDownloadUrl&&(this.exportDownloadUrl=null),this.$nextTick((function(){_this.exportDownloadUrl=url}))},showOriginalPhoto:function(){this.originalImage=!0,this.showCompressedImage=!0,this.showOriginalImage=!1,this.compressedImage=!1},showCompressedPhoto:function(){this.showCompressedImage=!1,this.originalImage=!1,this.showOriginalImage=!0,this.compressedImage=!0},getFileIcon:function(attachment){var contentType=attachment.contentType,FILE_TYPE_ICONS=this.$constants.FILE_TYPE_ICONS,selectedIndex=FILE_TYPE_ICONS.findIndex((function(icons){var fileTypes=icons.fileTypes;return fileTypes.some((function(type){return contentType.includes(type)}))}));return(0,validation/* isEmpty */.xb)(selectedIndex)?FILE_TYPE_ICONS[0].path:FILE_TYPE_ICONS[selectedIndex].path},isVideoSupported:function(contentType){contentType=contentType||"";var supportedTypes=["video/webm","video/mp4","video/ogg"];return supportedTypes.includes(contentType.trim().toLowerCase())},showDeleteOption:function(){var showDelete=this.showDelete,isLockedState=this.isLockedState,isTaskClosed=this.isTaskClosed;return showDelete&&!isLockedState&&!isTaskClosed}}},components_PreviewFilevue_type_script_lang_js=PreviewFilevue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(components_PreviewFilevue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,PreviewFile=component.exports},
/***/543253:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return TenantFaq}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/webViews/pages/TenantFaq.vue?vue&type=template&id=1c46a596
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"tenant-faq-page"},[_c("el-container",[_c("el-header",{staticClass:"tenant-faq-header",attrs:{height:"120px"}},[_c("h1",[_vm._v(" "+_vm._s(_vm.$t("tenant.faq.faq_heading"))+" ")])]),_vm.loading?_c("div",{staticClass:"fc-empty-white flex-middle justify-content-center flex-direction-column m10"},[_c("spinner",{staticClass:"align-center",attrs:{show:!0,size:"80"}})],1):!_vm.loading&&_vm.$validation.isEmpty(_vm.faqModuleDatas)?_c("div",{staticClass:"fc-empty-white flex-middle justify-content-center flex-direction-column m10 fc-empty-state-mobile"},[_c("div",{staticClass:"empty-state-container"},[_c("inline-svg",{attrs:{src:"svgs/emptystate/readings-empty",iconClass:"icon text-center icon-xxxxlg"}}),_c("div",{staticClass:"q-item-label nowo-label"},[_vm._v(" "+_vm._s(_vm.$t("tenant.faq.no_data"))+" ")])],1)]):_c("el-main",_vm._l(_vm.sections,(function(section,idx){return _c("div",{key:idx},[_c("div",{staticClass:"fc-black-18 pT20 pB15"},[_vm._v(" "+_vm._s(section.name)+" ")]),_c("div",{staticClass:"fc-tenant-faq-collapse-section flex-col-reverse d-flex"},[_c("el-collapse",{model:{value:_vm.activeNames,callback:function($$v){_vm.activeNames=$$v},expression:"activeNames"}},_vm._l(section.questions,(function(faq,rdx){return _c("el-collapse-item",{key:rdx,attrs:{name:faq.id}},[_c("template",{slot:"title"},[_c("div",{staticClass:"flex-middle"},[_c("div",{staticClass:"dot-12 fc-dot-blue-bg"}),_c("div",{staticClass:"pL10 fc-collapse-heading"},[_vm._v(" "+_vm._s(faq.name)+" ")])])]),_c("div",{staticClass:"fc-tenant-faq-desc"},[_vm._v(" "+_vm._s(_vm.$getProperty(faq,"data.answer"))+" ")]),_c("div",{staticClass:"fc-faq-image-align"},_vm._l(_vm.attachmentsData[faq.id],(function(attachment,idex){return _c("div",{key:idex,attrs:{value:attachment}},[attachment.contentType.includes("image")?_c("div",{staticClass:"mR20 pointer",on:{click:function($event){return _vm.selectedFile(attachment)}}},[_c("div",[_c("img",{staticClass:"fc-faq-img-size mB10",attrs:{src:_vm.$prependBaseUrl(attachment.previewUrl),alt:"",title:""}})])]):_c("div",{staticClass:"mR20 fc-tenant-faq-file-icon pointer",on:{click:function($event){return _vm.selectedFile(attachment)}}},[_c("InlineSvg",{staticClass:"d-flex ",attrs:{src:_vm.getFileIcon(attachment),iconClass:"icon icon-40"}})],1)])})),0)],2)})),1)],1)])})),0)],1),_vm.visibility&&_vm.selectedAttachedFile?_c("preview-file",{attrs:{visibility:_vm.visibility,previewFile:_vm.selectedAttachedFile,files:_vm.attachments},on:{"update:visibility":function($event){_vm.visibility=$event}}}):_vm._e()],1)},staticRenderFns=[],regeneratorRuntime=__webpack_require__(150124),toConsumableArray=__webpack_require__(488478),asyncToGenerator=__webpack_require__(548534),api=(__webpack_require__(228436),__webpack_require__(450886),__webpack_require__(260228),__webpack_require__(919649),__webpack_require__(821694),__webpack_require__(76265),__webpack_require__(947522),__webpack_require__(670560),__webpack_require__(425728),__webpack_require__(538077),__webpack_require__(339772),__webpack_require__(976801),__webpack_require__(843843),__webpack_require__(32284)),PreviewFile=__webpack_require__(720096),validation=__webpack_require__(990260),sortBy=__webpack_require__(189734),sortBy_default=__webpack_require__.n(sortBy),TenantFaqvue_type_script_lang_js={data:function(){return{faqModuleDatas:[],loading:!1,activeNames:"",attachmentsData:{},faqSectionName:[],faqSectionData:[],attachments:[],faqs:{},sections:[],visibility:!1,selectedAttachedFile:null}},created:function(){this.loadTenantFaq()},filters:{trim:function(value){return value?value.trim():"-"}},components:{PreviewFile:PreviewFile/* default */.Z},methods:{loadTenantFaq:function(){var _this=this;return(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee(){var params,_yield$API$get,error,data,message,ids;return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context){while(1)switch(_context.prev=_context.next){case 0:return _this.loading=!0,params={viewName:"all",perPage:50,page:1,includeParentFilter:!0},_context.next=4,api/* API */.bl.get("v2/module/data/list?moduleName=custom_faq",params);case 4:_yield$API$get=_context.sent,error=_yield$API$get.error,data=_yield$API$get.data,error?(message=error.message,_this.$message.error(message)):(_this.faqModuleDatas=data.moduleDatas,_this.sections=[],_this.faqs={},ids=data.moduleDatas.map((function(rt){return rt.data.section.id})),ids=(0,toConsumableArray/* default */.Z)(new Set(ids)),ids.forEach((function(id){_this.sections.push(data.moduleDatas.find((function(rl){return rl.data.section.id===id})).data.section),_this.faqs[id]=data.moduleDatas.filter((function(rl){return rl.data.section.id===id}))})),_this.faqModuleDatas.forEach((function(data){_this.loadAttachments(data.id)}))),_this.formatSectiondata(),_this.loading=!1;case 10:case"end":return _context.stop()}}),_callee)})))()},formatSectiondata:function(){var _this2=this;this.sections=sortBy_default()(this.sections,"data.number"),this.sections.forEach((function(section){if(section.id){var questions=sortBy_default()(_this2.faqs[section.id],"data.number");_this2.$set(section,"questions",questions)}}))},loadAttachments:function(id){var _this3=this;return(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee2(){var response,message;return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context2){while(1)switch(_context2.prev=_context2.next){case 0:return _this3.loading=!0,_context2.next=3,api/* API */.bl.get("attachment?module=cmdattachments&recordId=".concat(id,"&parentModuleName=custom_faq"));case 3:response=_context2.sent,response.error?(message=response.error.message,_this3.$message.error(message)):_this3.$set(_this3.attachmentsData,id,response.data.attachments),_this3.loading=!1;case 6:case"end":return _context2.stop()}}),_callee2)})))()},selectedFile:function(inSummary){this.selectedAttachedFile=inSummary,this.visibility=!0},getFileIcon:function(attachment){var contentType=attachment.contentType,FILE_TYPE_ICONS=this.$constants.FILE_TYPE_ICONS,selectedIndex=FILE_TYPE_ICONS.findIndex((function(icons){var fileTypes=icons.fileTypes;return fileTypes.some((function(type){return contentType.includes(type)}))}));return(0,validation/* isEmpty */.xb)(selectedIndex)?FILE_TYPE_ICONS[0].path:FILE_TYPE_ICONS[selectedIndex].path}}},pages_TenantFaqvue_type_script_lang_js=TenantFaqvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(pages_TenantFaqvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,TenantFaq=component.exports}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/43253.js.map