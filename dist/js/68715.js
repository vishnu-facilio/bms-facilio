(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[68715],{
/***/720096:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){"use strict";
// EXPORTS
__webpack_require__.d(__webpack_exports__,{Z:function(){/* binding */return PreviewFile}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/components/PreviewFile.vue?vue&type=template&id=447c7e89
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _vm.currentFile?_c("div",{directives:[{name:"show",rawName:"v-show",value:_vm.show,expression:"show"}],staticClass:"preview-file"},[_c("div",{staticClass:"top-toolbar"},[_c("div",{staticClass:"a-top-toolbar"},[_c("div",{staticClass:"file-name word-break-keep"},[_vm._v(" "+_vm._s(_vm.currentFile.fileName)+" ")]),_c("div",{staticClass:"top-toolbar-icon-right flex align-center"},[_vm.currentFile.contentType.includes("image")?[_c("div",{staticClass:"flex-middle pL15"},[_vm.showOriginalImage?_c("div",{staticClass:"fc-white-14 word-break-keep pointer",on:{click:_vm.showOriginalPhoto}},[_vm._v(" "+_vm._s(_vm.$t("common._common.view_original"))+" ")]):_vm._e(),_vm.showCompressedImage?_c("div",{staticClass:"fc-white-14 word-break-keep pointer",on:{click:_vm.showCompressedPhoto}},[_vm._v(" "+_vm._s(_vm.$t("common._common.view_default"))+" ")]):_vm._e()]),_c("div",{staticClass:"download-icon round flex-middle pL15",on:{click:function($event){return _vm.downloadAttachment(_vm.currentFile)}}},[_c("i",{staticClass:"el-icon-download op8",staticStyle:{color:"#ffff"}})])]:_vm._e(),_vm.currentFile.contentType.includes("pdf")?[_c("div",{staticClass:"download-icon round flex-middle pL15",on:{click:function($event){return _vm.downloadAttachment(_vm.currentFile)}}},[_c("i",{staticClass:"el-icon-download op8",staticStyle:{color:"#ffff"}})])]:_vm._e(),_vm.showDeleteOption()?_c("div",{staticClass:"flex-middle pL15",on:{click:function($event){return _vm.deletePreview()}}},[_c("inline-svg",{staticClass:"pointer edit-icon-color close vertical-text-top",attrs:{src:"svgs/delete",iconClass:"icon icon-md"}})],1):_vm._e(),_c("div",{staticClass:"pL15"},[_c("i",{staticClass:"el-icon-close close",on:{click:_vm.closePreview}})])],2)])]),_vm.currentFileIndex>0?_c("a",{staticClass:"el-icon-arrow-left prev",staticStyle:{"font-size":"30px"},on:{click:function($event){return _vm.plusSlides(-1)}}}):_vm._e(),_vm.currentFileIndex<_vm.files.length-1?_c("a",{staticClass:"el-icon-arrow-right next",staticStyle:{"font-size":"30px"},on:{click:function($event){return _vm.plusSlides(1)}}}):_vm._e(),_c("div",{staticClass:"preview-content"},[_vm.currentFile.contentType.includes("image")?[_vm.compressedImage?_c("img",{staticClass:"content",staticStyle:{"box-shadow":"none"},attrs:{src:_vm.$prependBaseUrl(_vm.currentFile.previewUrl)}}):_vm.originalImage?_c("img",{staticClass:"content original",staticStyle:{"box-shadow":"none"},attrs:{src:_vm.$prependBaseUrl(_vm.currentFile.previewUrl+"?fetchOriginal=true")}}):_vm._e()]:_vm.currentFile.contentType.includes("pdf")?_c("div",{staticClass:"width100 height-100"},[_c("iframe",{staticClass:"fc-preview-pdf",attrs:{src:_vm.$prependBaseUrl(_vm.currentFile.previewUrl),height:"100%",width:"100%"}})]):_vm.isVideoSupported(_vm.currentFile.contentType)?_c("div",{staticClass:"width100 height-100 fc-preview-video"},[_c("video",{attrs:{controls:""}},[_c("source",{attrs:{src:_vm.$prependBaseUrl(_vm.$getProperty(_vm.currentFile,"downloadUrl",null)),type:_vm.$getProperty(_vm.currentFile,"contentType")}}),_vm._v(" "+_vm._s(_vm.$t("common._common.browser_not_supported"))+" ")])]):_c("div",{staticClass:"no-preview"},[_c("div",{staticClass:"flex justify-center pT24"},[_c("inline-svg",{staticClass:"d-flex",attrs:{src:_vm.getFileIcon(_vm.currentFile),iconClass:"icon icon-60 overlay"}})],1),_c("div",{staticClass:"f14 pT12 pB12"},[_vm._v("No preview available")]),_c("div",{staticClass:"download-button"},[_c("a",{staticClass:"flex flex-row",staticStyle:{color:"#ffff"},on:{click:function($event){return _vm.downloadAttachment(_vm.currentFile)}}},[_c("div",[_vm._v("Download")])])])])],2),_vm.exportDownloadUrl?_c("iframe",{staticStyle:{display:"none"},attrs:{src:_vm.exportDownloadUrl}}):_vm._e()]):_vm._e()},staticRenderFns=[],validation=(__webpack_require__(339772),__webpack_require__(260228),__webpack_require__(976801),__webpack_require__(843843),__webpack_require__(228436),__webpack_require__(990260)),PreviewFilevue_type_script_lang_js={props:["previewFile","files","visibility","showDelete","isLockedState","isTaskClosed"],data:function(){return{show:!0,previewUrl:null,currentFileIndex:0,currentFile:null,imageMargin:null,exportDownloadUrl:null,originalImage:!1,compressedImage:!0,showOriginalImage:!0,showCompressedImage:!1}},destroyed:function(){window.removeEventListener("keyup",this.handler)},mounted:function(){window.addEventListener("keyup",this.handler),this.preloadImages()},watch:{},computed:{},methods:{handler:function(){
// If escape key is pressed...
27===event.keyCode?this.closePreview():37===event.keyCode?this.plusSlides(-1):39===event.keyCode&&this.plusSlides(1)},preloadImages:function(){this.currentFile=this.previewFile?this.previewFile:this.files[0],this.currentFileIndex=this.files.indexOf(this.currentFile)},plusSlides:function(n){var index=this.currentFileIndex+n;index<0&&(index=0),index>=this.files.length&&(index=this.files.length-1),this.currentFileIndex=index,this.currentFile=this.files[index]},closePreview:function(){this.$emit("update:visibility",!1)},deletePreview:function(){var currentFile=this.currentFile;this.$emit("onDelete",currentFile)},downloadAttachment:function(file){var _this=this,_ref=file||{},url=_ref.downloadUrl;this.exportDownloadUrl&&(this.exportDownloadUrl=null),this.$nextTick((function(){_this.exportDownloadUrl=url}))},showOriginalPhoto:function(){this.originalImage=!0,this.showCompressedImage=!0,this.showOriginalImage=!1,this.compressedImage=!1},showCompressedPhoto:function(){this.showCompressedImage=!1,this.originalImage=!1,this.showOriginalImage=!0,this.compressedImage=!0},getFileIcon:function(attachment){var contentType=attachment.contentType,FILE_TYPE_ICONS=this.$constants.FILE_TYPE_ICONS,selectedIndex=FILE_TYPE_ICONS.findIndex((function(icons){var fileTypes=icons.fileTypes;return fileTypes.some((function(type){return contentType.includes(type)}))}));return(0,validation/* isEmpty */.xb)(selectedIndex)?FILE_TYPE_ICONS[0].path:FILE_TYPE_ICONS[selectedIndex].path},isVideoSupported:function(contentType){contentType=contentType||"";var supportedTypes=["video/webm","video/mp4","video/ogg"];return supportedTypes.includes(contentType.trim().toLowerCase())},showDeleteOption:function(){var showDelete=this.showDelete,isLockedState=this.isLockedState,isTaskClosed=this.isTaskClosed;return showDelete&&!isLockedState&&!isTaskClosed}}},components_PreviewFilevue_type_script_lang_js=PreviewFilevue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(components_PreviewFilevue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,PreviewFile=component.exports},
/***/281375:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){"use strict";
// EXPORTS
__webpack_require__.d(__webpack_exports__,{Z:function(){/* binding */return Attachments}});
// EXTERNAL MODULE: ./node_modules/@babel/runtime/helpers/esm/objectSpread2.js
var render,staticRenderFns,objectSpread2=__webpack_require__(595082),validation=(__webpack_require__(634338),__webpack_require__(677049),__webpack_require__(821694),__webpack_require__(260228),__webpack_require__(947522),__webpack_require__(434284),__webpack_require__(891719),__webpack_require__(339772),__webpack_require__(670560),__webpack_require__(162506),__webpack_require__(821057),__webpack_require__(990260)),Attachmentsvue_type_script_lang_js={props:["module","record","parentModule"],data:function(){return{loading:!0,status:{UPLOADING:1,SUCCESS:2,FAILED:3},attachments:[],formFieldName:"attachment",selectedAttachedFile:null,visibility:!1}},mounted:function(){this.loadAttachments()},watch:{record:function(){this.loadAttachments()},attachments:function(){var _this$attachments=this.attachments,attachments=void 0===_this$attachments?[]:_this$attachments;this.record.noOfAttachments=(attachments||[]).length}},methods:{loadAttachments:function(){var _this=this;this.loading=!0;var url="/attachment?module=".concat(this.module,"&recordId=").concat(this.record.id);return(0,validation/* isEmpty */.xb)(this.parentModule)||(url+="&parentModuleName=".concat(this.parentModule)),this.$http.get(url).then((function(response){_this.loading=!1,_this.attachments=response.data.attachments?response.data.attachments:[]})).catch((function(error){error&&(_this.loading=!1,_this.attachments=null)}))},filesChange:function(file){var _this2=this,fileList=Array.from(file);if(fileList.length){
// Append the files to FormData
var formData=new FormData;formData.append("module",this.module),formData.append("recordId",this.record.id),formData.append("parentModuleName",this.parentModule);var _this$status=this.status,UPLOADING=_this$status.UPLOADING,SUCCESS=_this$status.SUCCESS,FAILED=_this$status.FAILED;fileList.forEach((function(file){var _ref=file||{},name=_ref.name,size=_ref.size,type=_ref.type,fileEntry={fileId:-1,fileName:name,fileSize:size,contentType:type,status:UPLOADING,error:null,previewUrl:null,uploadedBy:_this2.$account.user.id};formData.append(_this2.formFieldName,file,name),_this2.attachments.unshift(fileEntry)})),this.$http.post("/attachment/add",formData).then((function(_ref2){var data=_ref2.data;data.attachments.forEach((function(attachment){var fileId=attachment.fileId,fileName=attachment.fileName,fileSize=attachment.fileSize,attachmentIdx=_this2.attachments.findIndex((function(a){return a.fileName===fileName&&a.fileSize===fileSize}));if(!(0,validation/* isEmpty */.xb)(attachmentIdx)){var attachmentFile=_this2.attachments[attachmentIdx];attachmentFile=fileId?(0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)({},attachmentFile),attachment),{},{status:SUCCESS}):(0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)({},attachmentFile),{},{status:FAILED,error:"Upload failed."}),_this2.$set(_this2.attachments,attachmentIdx,attachmentFile)}}))})).catch((function(){fileList.forEach((function(file){var _ref3=file||{},name=_ref3.name,size=_ref3.size,attachmentIdx=_this2.attachments.findIndex((function(a){return a.fileName===name&&a.fileSize===size}));if(!(0,validation/* isEmpty */.xb)(attachmentIdx)){var attachmentFile=_this2.attachments[attachmentIdx];attachmentFile=(0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)({},attachmentFile),{},{status:FAILED,error:"Upload failed."}),_this2.$set(_this2.attachments,attachmentIdx,attachmentFile)}}))}))}},canShowDelete:function(attachment){var _ref4=attachment||{},uploadedBy=_ref4.uploadedBy,status=_ref4.status,SUCCESS=this.status.SUCCESS;return((0,validation/* isEmpty */.xb)(status)||status===SUCCESS)&&uploadedBy===this.$account.user.id},deleteAttachment:function(attachment,index){var _this3=this,ids=[],module=this.module,parentModule=this.parentModule,record=this.record;ids.push(attachment.id);var obj={attachmentId:ids,module:module,parentModuleName:parentModule,recordId:null===record||void 0===record?void 0:record.id};this.$dialog.confirm({title:"Delete Attachment",message:"Are you sure you want to delete this attachment",rbDanger:!0,rbLabel:"Delete"}).then((function(value){value&&_this3.$http.post("/v2/attachments/delete",obj).then((function(_ref5){var _ref5$data=_ref5.data,message=_ref5$data.message,responseCode=_ref5$data.responseCode;_ref5$data.result;if(0!==responseCode)throw new Error(message);_this3.attachments.splice(index,1),_this3.loadAttachments()}))}))}}},base_Attachmentsvue_type_script_lang_js=Attachmentsvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(base_Attachmentsvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,Attachments=component.exports;
// EXTERNAL MODULE: ./node_modules/core-js/modules/es.array.concat.js
},
/***/268715:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){"use strict";
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return DocumentsWidget}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/components/page/widget/common/DocumentsWidget.vue?vue&type=template&id=1a95f076&scoped=true
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"widget-content"},[_vm.hideTitleSection?_vm._e():[_c("div",{staticClass:"widget-topbar"},[_c("div",{staticClass:"widget-title"},[_vm._v(_vm._s(_vm.widgetTitle))]),_c("div",{staticClass:"flex-end"},[_c("portal-target",{attrs:{name:_vm.widget.key+"-topbar"}})],1)])],_c("documents",{attrs:{module:_vm.attachmentsModuleName,parentModule:_vm.moduleName,record:_vm.details,layoutParams:_vm.layoutParams,resizeWidget:_vm.resizeWidget,groupKey:_vm.groupKey,isActive:_vm.isActive,widget:_vm.widget}})],2)},staticRenderFns=[],Documentsvue_type_template_id_6547dabe_scoped_true_render=(__webpack_require__(434284),function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{ref:"docs-container",staticClass:"fc-attachments h100 position-relative",on:{dragover:function($event){return $event.preventDefault(),_vm.dragOverHandler.apply(null,arguments)},dragleave:function($event){return $event.preventDefault(),_vm.dragLeaveHandler.apply(null,arguments)},drop:_vm.dropLeaveHandler}},[_vm.loading?_c("div",{staticClass:"text-center"},[_c("spinner",{attrs:{show:_vm.loading}})],1):[_c("div",{staticStyle:{"max-width":"900px"}},[_vm.needsShowMore&&_vm.attachments?_c("div",{staticClass:"view-more"},[_c("a",{staticClass:"f12 mL10",on:{click:_vm.showAllAttachments}},[_vm._v(_vm._s(_vm.canShowAllAttachments?"Show Less":"Show More"))]),_vm.canShowAllAttachments?_vm._e():_c("div",{staticClass:"inline-block bold f12"},[_vm._v(" "+_vm._s(_vm.$getProperty(_vm.filteredAttachments,"length"))+" of "+_vm._s(_vm.$getProperty(_vm.attachments,"length"))+" ")])]):_vm._e(),_vm._l(_vm.filteredAttachments,(function(attachment,index){return _c("div",{key:attachment.fileId+"_"+index,staticClass:"fc-attachment-row visibility-visible-actions"},[_c("div",{staticClass:"attachment-label pointer"},[attachment.previewUrl?_c("a",{on:{click:function($event){return _vm.openFilePreview(attachment)}}},[_vm._v(_vm._s(attachment.fileName))]):_c("span",[_vm._v(_vm._s(attachment.fileName))])]),_c("div",{staticClass:"attachment-sublabel pT15 pB10"},[_c("span",{staticClass:"pR10"},[_vm._v(_vm._s(_vm._f("prettyBytes")(attachment.fileSize)))]),1===attachment.status?_c("span",{staticClass:"attachment-sublabel"},[_vm._v(", Uploading...")]):2===attachment.status?_c("span",{staticClass:"attachment-sublabel"},[_vm._v(", Success")]):_c("span",{staticClass:"attachment-sublabel"},[_vm._v(_vm._s(attachment.error))])]),_vm.canShowDelete(attachment)?_c("div",{staticClass:"remove pull-right"},[_c("a",{staticClass:"remove-attachment-link fc-delete visibility-hide-actions",on:{click:function($event){return _vm.deleteAttachment(attachment,index)}}},[_c("img",{attrs:{src:__webpack_require__(984329),width:"15",height:"15"}})])]):_vm._e()])}))],2),_c("form",{class:[_vm.showDragArea?"show-v":"hide-v","drop-container"],attrs:{enctype:"multipart/form-data",novalidate:""}},[_c("div",{staticClass:"dropbox p0"},[_c("input",{ref:"file-input",staticClass:"input-file",attrs:{type:"file",multiple:""},on:{change:function($event){return _vm.filesChange($event.target.files)}}}),_c("img",{staticClass:"mT10 mB20 opacity-05",attrs:{src:__webpack_require__(581121),width:"20",height:"20"}}),_c("p",[_vm._v(" "+_vm._s(_vm.$t("common.attachment_form.drag_and_drop_files"))+" "),_c("br"),_vm._v(_vm._s(_vm.$t("common.attachment_form.click_to_browse"))+" ")])])])],_vm.canShowPreview&&_vm.selectedAttachment?_c("preview-file",{attrs:{visibility:_vm.canShowPreview,previewFile:_vm.selectedAttachment,files:_vm.attachments},on:{"update:visibility":function($event){_vm.canShowPreview=$event}}}):_vm._e(),_vm.isActive&&_vm.groupKey?_c("portal",{attrs:{to:_vm.groupKey+"-topbar",slim:""}},[_c("img",{staticClass:"mR15 pull-right pointer new-attachement",attrs:{src:__webpack_require__(401213)},on:{click:_vm.openFilePicker}})]):(_vm.widget||{}).key?_c("portal",{attrs:{to:_vm.widget.key+"-topbar",slim:""}},[_c("img",{staticClass:"mR15 pull-right pointer new-attachement",attrs:{src:__webpack_require__(401213)},on:{click:_vm.openFilePicker}})]):_vm._e()],2)}),Documentsvue_type_template_id_6547dabe_scoped_true_staticRenderFns=[],Attachments=(__webpack_require__(689730),__webpack_require__(281375)),PreviewFile=__webpack_require__(720096),Documentsvue_type_script_lang_js={extends:Attachments/* default */.Z,components:{PreviewFile:PreviewFile/* default */.Z},props:["groupKey","resizeWidget","layoutParams","isActive","widget","parentModule"],data:function(){return{canShowAllAttachments:!1,canShowPreview:!1,selectedAttachment:null,showDragArea:!1,visibleAttachmentCount:3,needsShowMore:!0,defaultWidgetHeight:this.layoutParams.h||null}},computed:{filteredAttachments:function(){var attachments=this.attachments,count=this.$getProperty(attachments,"length");return!this.canShowAllAttachments&&count>this.visibleAttachmentCount?attachments.slice(0,this.visibleAttachmentCount):attachments}},watch:{attachments:function(){var count=this.$getProperty(this.attachments,"length");this.record.noOfAttachments=count,this.needsShowMore=count>this.visibleAttachmentCount,this.showDragArea=0===count},isActive:function(_isActive){!_isActive&&this.canShowAllAttachments&&this.showAllAttachments()}},methods:{dragOverHandler:function(){this.showDragArea=!0},dragLeaveHandler:function(){this.showDragArea=!0},dropLeaveHandler:function(){this.showDragArea=!1},openFilePreview:function(attachment){this.selectedAttachment=attachment,this.canShowPreview=!0},showAllAttachments:function(){var _this=this;this.canShowAllAttachments=!this.canShowAllAttachments,this.$nextTick((function(){if(_this.canShowAllAttachments){var height=_this.$refs["docs-container"].scrollHeight+70,width=_this.$refs["docs-container"].scrollWidth;_this.resizeWidget({height:height,width:width})}else _this.resizeWidget({h:_this.defaultWidgetHeight})}))},openFilePicker:function(){this.$refs["file-input"].click()}}},widget_Documentsvue_type_script_lang_js=Documentsvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(widget_Documentsvue_type_script_lang_js,Documentsvue_type_template_id_6547dabe_scoped_true_render,Documentsvue_type_template_id_6547dabe_scoped_true_staticRenderFns,!1,null,"6547dabe",null)
/* harmony default export */,Documents=component.exports,DocumentsWidgetvue_type_script_lang_js={props:["details","layoutParams","resizeWidget","hideTitleSection","groupKey","activeTab","moduleName","attachmentsModuleName","widget"],components:{Documents:Documents},computed:{isActive:function(){return this.activeTab===(this.widget.widgetParams?this.widget.widgetParams.module:this.widget.widgetTypeObj.name)},widgetTitle:function(){var widget=this.widget;return(widget||{}).title?widget.title:this.$t("asset.assets.documents")}}},common_DocumentsWidgetvue_type_script_lang_js=DocumentsWidgetvue_type_script_lang_js,DocumentsWidget_component=(0,componentNormalizer/* default */.Z)(common_DocumentsWidgetvue_type_script_lang_js,render,staticRenderFns,!1,null,"1a95f076",null)
/* harmony default export */,DocumentsWidget=DocumentsWidget_component.exports},
/***/984329:
/***/function(module,__unused_webpack_exports,__webpack_require__){module.exports=__webpack_require__.p+"78e9b94d763d88d92daab6faadbef8f9.svg";
/***/},
/***/581121:
/***/function(module,__unused_webpack_exports,__webpack_require__){module.exports=__webpack_require__.p+"88cd7135ef1417b08c9498c51d2cda59.svg";
/***/},
/***/401213:
/***/function(module,__unused_webpack_exports,__webpack_require__){module.exports=__webpack_require__.p+"fdd0e7f1a607dbd85c230219e6d6b6ac.svg";
/***/}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/68715.js.map