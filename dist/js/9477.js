(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[9477],{
/***/720096:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){"use strict";
// EXPORTS
__webpack_require__.d(__webpack_exports__,{Z:function(){/* binding */return PreviewFile}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/components/PreviewFile.vue?vue&type=template&id=447c7e89
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _vm.currentFile?_c("div",{directives:[{name:"show",rawName:"v-show",value:_vm.show,expression:"show"}],staticClass:"preview-file"},[_c("div",{staticClass:"top-toolbar"},[_c("div",{staticClass:"a-top-toolbar"},[_c("div",{staticClass:"file-name word-break-keep"},[_vm._v(" "+_vm._s(_vm.currentFile.fileName)+" ")]),_c("div",{staticClass:"top-toolbar-icon-right flex align-center"},[_vm.currentFile.contentType.includes("image")?[_c("div",{staticClass:"flex-middle pL15"},[_vm.showOriginalImage?_c("div",{staticClass:"fc-white-14 word-break-keep pointer",on:{click:_vm.showOriginalPhoto}},[_vm._v(" "+_vm._s(_vm.$t("common._common.view_original"))+" ")]):_vm._e(),_vm.showCompressedImage?_c("div",{staticClass:"fc-white-14 word-break-keep pointer",on:{click:_vm.showCompressedPhoto}},[_vm._v(" "+_vm._s(_vm.$t("common._common.view_default"))+" ")]):_vm._e()]),_c("div",{staticClass:"download-icon round flex-middle pL15",on:{click:function($event){return _vm.downloadAttachment(_vm.currentFile)}}},[_c("i",{staticClass:"el-icon-download op8",staticStyle:{color:"#ffff"}})])]:_vm._e(),_vm.currentFile.contentType.includes("pdf")?[_c("div",{staticClass:"download-icon round flex-middle pL15",on:{click:function($event){return _vm.downloadAttachment(_vm.currentFile)}}},[_c("i",{staticClass:"el-icon-download op8",staticStyle:{color:"#ffff"}})])]:_vm._e(),_vm.showDeleteOption()?_c("div",{staticClass:"flex-middle pL15",on:{click:function($event){return _vm.deletePreview()}}},[_c("inline-svg",{staticClass:"pointer edit-icon-color close vertical-text-top",attrs:{src:"svgs/delete",iconClass:"icon icon-md"}})],1):_vm._e(),_c("div",{staticClass:"pL15"},[_c("i",{staticClass:"el-icon-close close",on:{click:_vm.closePreview}})])],2)])]),_vm.currentFileIndex>0?_c("a",{staticClass:"el-icon-arrow-left prev",staticStyle:{"font-size":"30px"},on:{click:function($event){return _vm.plusSlides(-1)}}}):_vm._e(),_vm.currentFileIndex<_vm.files.length-1?_c("a",{staticClass:"el-icon-arrow-right next",staticStyle:{"font-size":"30px"},on:{click:function($event){return _vm.plusSlides(1)}}}):_vm._e(),_c("div",{staticClass:"preview-content"},[_vm.currentFile.contentType.includes("image")?[_vm.compressedImage?_c("img",{staticClass:"content",staticStyle:{"box-shadow":"none"},attrs:{src:_vm.$prependBaseUrl(_vm.currentFile.previewUrl)}}):_vm.originalImage?_c("img",{staticClass:"content original",staticStyle:{"box-shadow":"none"},attrs:{src:_vm.$prependBaseUrl(_vm.currentFile.previewUrl+"?fetchOriginal=true")}}):_vm._e()]:_vm.currentFile.contentType.includes("pdf")?_c("div",{staticClass:"width100 height-100"},[_c("iframe",{staticClass:"fc-preview-pdf",attrs:{src:_vm.$prependBaseUrl(_vm.currentFile.previewUrl),height:"100%",width:"100%"}})]):_vm.isVideoSupported(_vm.currentFile.contentType)?_c("div",{staticClass:"width100 height-100 fc-preview-video"},[_c("video",{attrs:{controls:""}},[_c("source",{attrs:{src:_vm.$prependBaseUrl(_vm.$getProperty(_vm.currentFile,"downloadUrl",null)),type:_vm.$getProperty(_vm.currentFile,"contentType")}}),_vm._v(" "+_vm._s(_vm.$t("common._common.browser_not_supported"))+" ")])]):_c("div",{staticClass:"no-preview"},[_c("div",{staticClass:"flex justify-center pT24"},[_c("inline-svg",{staticClass:"d-flex",attrs:{src:_vm.getFileIcon(_vm.currentFile),iconClass:"icon icon-60 overlay"}})],1),_c("div",{staticClass:"f14 pT12 pB12"},[_vm._v("No preview available")]),_c("div",{staticClass:"download-button"},[_c("a",{staticClass:"flex flex-row",staticStyle:{color:"#ffff"},on:{click:function($event){return _vm.downloadAttachment(_vm.currentFile)}}},[_c("div",[_vm._v("Download")])])])])],2),_vm.exportDownloadUrl?_c("iframe",{staticStyle:{display:"none"},attrs:{src:_vm.exportDownloadUrl}}):_vm._e()]):_vm._e()},staticRenderFns=[],validation=(__webpack_require__(339772),__webpack_require__(260228),__webpack_require__(976801),__webpack_require__(843843),__webpack_require__(228436),__webpack_require__(990260)),PreviewFilevue_type_script_lang_js={props:["previewFile","files","visibility","showDelete","isLockedState","isTaskClosed"],data:function(){return{show:!0,previewUrl:null,currentFileIndex:0,currentFile:null,imageMargin:null,exportDownloadUrl:null,originalImage:!1,compressedImage:!0,showOriginalImage:!0,showCompressedImage:!1}},destroyed:function(){window.removeEventListener("keyup",this.handler)},mounted:function(){window.addEventListener("keyup",this.handler),this.preloadImages()},watch:{},computed:{},methods:{handler:function(){
// If escape key is pressed...
27===event.keyCode?this.closePreview():37===event.keyCode?this.plusSlides(-1):39===event.keyCode&&this.plusSlides(1)},preloadImages:function(){this.currentFile=this.previewFile?this.previewFile:this.files[0],this.currentFileIndex=this.files.indexOf(this.currentFile)},plusSlides:function(n){var index=this.currentFileIndex+n;index<0&&(index=0),index>=this.files.length&&(index=this.files.length-1),this.currentFileIndex=index,this.currentFile=this.files[index]},closePreview:function(){this.$emit("update:visibility",!1)},deletePreview:function(){var currentFile=this.currentFile;this.$emit("onDelete",currentFile)},downloadAttachment:function(file){var _this=this,_ref=file||{},url=_ref.downloadUrl;this.exportDownloadUrl&&(this.exportDownloadUrl=null),this.$nextTick((function(){_this.exportDownloadUrl=url}))},showOriginalPhoto:function(){this.originalImage=!0,this.showCompressedImage=!0,this.showOriginalImage=!1,this.compressedImage=!1},showCompressedPhoto:function(){this.showCompressedImage=!1,this.originalImage=!1,this.showOriginalImage=!0,this.compressedImage=!0},getFileIcon:function(attachment){var contentType=attachment.contentType,FILE_TYPE_ICONS=this.$constants.FILE_TYPE_ICONS,selectedIndex=FILE_TYPE_ICONS.findIndex((function(icons){var fileTypes=icons.fileTypes;return fileTypes.some((function(type){return contentType.includes(type)}))}));return(0,validation/* isEmpty */.xb)(selectedIndex)?FILE_TYPE_ICONS[0].path:FILE_TYPE_ICONS[selectedIndex].path},isVideoSupported:function(contentType){contentType=contentType||"";var supportedTypes=["video/webm","video/mp4","video/ogg"];return supportedTypes.includes(contentType.trim().toLowerCase())},showDeleteOption:function(){var showDelete=this.showDelete,isLockedState=this.isLockedState,isTaskClosed=this.isTaskClosed;return showDelete&&!isLockedState&&!isTaskClosed}}},components_PreviewFilevue_type_script_lang_js=PreviewFilevue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(components_PreviewFilevue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,PreviewFile=component.exports},
/***/809477:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){"use strict";
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return WorkorderAttachments}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/pages/workorder/workorders/v3/widgets/WorkorderAttachments.vue?vue&type=template&id=f99e29aa
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"white-bg-block new-summary-block pL30 pR30 mT20"},[_c("div",{staticClass:"label-txt-black pB10 fw-bold"},[_vm._v(" "+_vm._s(_vm.$t("maintenance.wr_list.attachments"))+" ")]),_c("div",{staticClass:"fc-attachments new-attachments"},[_vm.exportDownloadUrl?_c("iframe",{staticStyle:{display:"none"},attrs:{src:_vm.exportDownloadUrl}}):_vm._e(),_vm.loading?_c("div",{staticClass:"text-center"},[_c("spinner",{attrs:{show:_vm.loading}})],1):_c("div",[_c("div",{staticClass:"row fc-summary-attchment max-height130 overflo-auto"},_vm._l(_vm.attachments,(function(attachment,index){return _c("el-row",{key:attachment.attachmentId,staticClass:"attchment-row visibility-visible-actions"},[_c("el-col",{attrs:{span:22}},[_c("el-col",{staticClass:"mT5",attrs:{span:1}},[_c("img",{staticClass:"svg-icon",staticStyle:{width:"20px"},attrs:{src:__webpack_require__(250620)}})]),_c("el-col",{attrs:{span:18}},[_c("div",{staticClass:"pL10"},[attachment.previewUrl?_c("a",{directives:[{name:"tippy",rawName:"v-tippy",value:{placement:"top",animation:"shift-away",arrow:!0},expression:"{\n                    placement: 'top',\n                    animation: 'shift-away',\n                    arrow: true,\n                  }"}],staticClass:"self-center show text-left fc-black-13 textoverflow-ellipsis max-width300px",staticStyle:{border:"none"},attrs:{title:attachment.fileName},on:{click:function($event){return _vm.selectedFile(attachment)}}},[_vm._v(_vm._s(attachment.fileName))]):_c("a",{directives:[{name:"tippy",rawName:"v-tippy",value:{placement:"top",animation:"shift-away",arrow:!0},expression:"{\n                    placement: 'top',\n                    animation: 'shift-away',\n                    arrow: true,\n                  }"}],staticClass:"self-center show text-left fc-black-13 textoverflow-ellipsis max-width300px",staticStyle:{border:"none"},attrs:{title:attachment.fileName}},[_vm._v(_vm._s(attachment.fileName))]),_vm.getUser(attachment)>0?_c("div",{staticClass:"col-4 comment-file mT2",staticStyle:{border:"none"}},[_vm._v(" "+_vm._s(_vm.getUser(attachment))+" "),attachment.createdTime?_c("span",[_vm._v("| "+_vm._s(_vm._f("formatDate")(attachment.createdTime,"DD-MM_YYYY")))]):_vm._e()]):_c("div",{staticClass:"col-4 comment-file mT2",staticStyle:{border:"none"}},[_vm._v(" "+_vm._s(_vm._f("formatDate")(attachment.createdTime,"DD-MM_YYYY"))+" ")])])])],1),_c("el-col",{staticClass:"mT5 pointer",attrs:{span:2}},[_c("img",{staticClass:"mR10",attrs:{src:__webpack_require__(365565),width:"16",height:"15"},on:{click:function($event){return _vm.downloadAttachment(attachment.downloadUrl)}}}),_vm.canShowDelete(attachment)?_c("span",{on:{click:function($event){return _vm.deleteAttachment(attachment,index)}}},[_c("inline-svg",{staticClass:"pointer edit-icon-color mL10",attrs:{src:"svgs/delete",iconClass:"icon icon-xs fill-red"}})],1):_vm._e()])],1)})),1),_c("form",{attrs:{enctype:"multipart/form-data",novalidate:""}},[_c("div",{staticClass:"dropbox"},[_c("input",{staticClass:"input-file",attrs:{type:"file",multiple:""},on:{change:function($event){return _vm.filesChange($event.target.files)}}}),_c("img",{staticClass:"mT10 mB10 opacity-05",attrs:{src:__webpack_require__(581121),width:"20",height:"20"}}),_c("p",[_vm._v(" "+_vm._s(_vm.$t("common.attachment_form.drag_and_drop_files"))),_c("br"),_vm._v(" "+_vm._s(_vm.$t("common.attachment_form.click_to_browse"))+" ")])])])]),_vm.visibility&&_vm.selectedAttachedFile?_c("preview-file",{attrs:{visibility:_vm.visibility,previewFile:_vm.selectedAttachedFile,files:_vm.attachments},on:{"update:visibility":function($event){_vm.visibility=$event}}}):_vm._e()],1)])},staticRenderFns=[],objectSpread2=__webpack_require__(595082),PreviewFile=(__webpack_require__(670560),__webpack_require__(162506),__webpack_require__(677049),__webpack_require__(821694),__webpack_require__(260228),__webpack_require__(947522),__webpack_require__(434284),__webpack_require__(891719),__webpack_require__(339772),__webpack_require__(720096)),validation=__webpack_require__(990260),api=__webpack_require__(32284),workorderHelper=__webpack_require__(865764),WorkorderAttachmentsvue_type_script_lang_js={name:"WorkorderAttachments",props:["moduleName","details"],components:{PreviewFile:PreviewFile/* default */.Z},mixins:[workorderHelper/* default */.Z],mounted:function(){this.loadAttachments()},data:function(){return{loading:!0,status:{UPLOADING:1,SUCCESS:2,FAILED:3},attachments:[],formFieldName:"attachment",exportDownloadUrl:null,selectedAttachedFile:null,visibility:!1}},computed:{currModuleName:function(){return"Workorder Attachments"},widgetTitle:function(){return"Attachments"},workorder:function(){return this.details.workorder}},methods:{canShowDelete:function(attachment){var _ref=attachment||{},uploadedBy=_ref.uploadedBy,status=_ref.status,SUCCESS=this.status.SUCCESS;return((0,validation/* isEmpty */.xb)(status)||status===SUCCESS)&&uploadedBy===this.$account.user.id},deleteAttachment:function(attachment,index){var _this=this,ids=[];ids.push(attachment.id);var payload={attachmentId:ids,module:"ticketattachments"};this.$dialog.confirm({title:this.$t("common.header.delete_attachment"),message:this.$t("common.header.are_you_sure_you_want_to_delete_this_attachment"),rbDanger:!0,rbLabel:this.$t("common._common.delete")}).then((function(value){value&&api/* API */.bl.post("/v2/attachments/delete",payload).then((function(_ref2){var error=_ref2.error;error||_this.attachments.splice(index,1)}))})).then((function(){_this.loadAttachments()}))},selectedFile:function(inList){this.selectedAttachedFile=inList,this.visibility=!0},loadAttachments:function(){var _this2=this,uri="/attachment?module=ticketattachments&recordId="+this.workorder.id;this.loading=!0,api/* API */.bl.get(uri).then((function(_ref3){var data=_ref3.data,error=_ref3.error;error||(_this2.attachments=data.attachments?data.attachments:[],_this2.loading=!1)})).catch((function(error){error&&(this.loading=!1,this.attachments=null)}))},filesChange:function(files){var _this3=this,fileList=Array.from(files);if(fileList.length){var formData=new FormData;formData.append("module","ticketattachments"),formData.append("recordId",this.workorder.id);var _this$status=this.status,UPLOADING=_this$status.UPLOADING,SUCCESS=_this$status.SUCCESS,FAILED=_this$status.FAILED;fileList.forEach((function(file){var _ref4=file||{},name=_ref4.name,size=_ref4.size,type=_ref4.type,fileEntry={fileId:-1,fileName:name,fileSize:size,contentType:type,status:UPLOADING,error:null,previewUrl:null,uploadedBy:_this3.$account.user.id};formData.append(_this3.formFieldName,file,name),_this3.attachments.unshift(fileEntry)}));var uri="/attachment/add";api/* API */.bl.post(uri,formData).then((function(_ref5){var data=_ref5.data;data.attachments.forEach((function(attachment){var fileId=attachment.fileId,fileName=attachment.fileName,fileSize=attachment.fileSize,attachmentIdx=_this3.attachments.findIndex((function(a){return a.fileName===fileName&&a.fileSize===fileSize}));if(!(0,validation/* isEmpty */.xb)(attachmentIdx)){var attachmentFile=_this3.attachments[attachmentIdx];attachmentFile=fileId?(0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)({},attachmentFile),attachment),{},{status:SUCCESS}):(0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)({},attachmentFile),{},{status:FAILED,error:"Upload failed."}),_this3.$set(_this3.attachments,attachmentIdx,attachmentFile)}}))})).catch((function(){fileList.forEach((function(file){var _ref6=file||{},name=_ref6.name,size=_ref6.size,attachmentIdx=_this3.attachments.findIndex((function(a){return a.fileName===name&&a.fileSize===size}));if(!(0,validation/* isEmpty */.xb)(attachmentIdx)){var attachmentFile=_this3.attachments[attachmentIdx];attachmentFile=(0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)({},attachmentFile),{},{status:FAILED,error:"Upload failed."}),_this3.$set(_this3.attachments,attachmentIdx,attachmentFile)}}))}))}},getUser:function(assign){if(null!==assign&&assign.id){var assignto=this.$store.getters.getUser(assign.id);return assignto?assignto.name:"---"}return"---"},downloadAttachment:function(url){this.exportDownloadUrl=url},icon:function(file){return file&&"image/png"===file?"statics/icons/png.svg":"statics/icons/pdf.svg"}}},widgets_WorkorderAttachmentsvue_type_script_lang_js=WorkorderAttachmentsvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(widgets_WorkorderAttachmentsvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,WorkorderAttachments=component.exports},
/***/365565:
/***/function(module,__unused_webpack_exports,__webpack_require__){module.exports=__webpack_require__.p+"d022638208d4baab3e4d8fbec8ac38ca.svg";
/***/},
/***/250620:
/***/function(module,__unused_webpack_exports,__webpack_require__){module.exports=__webpack_require__.p+"03daa5e7a5fe4964f41cd5e0c6dbcdb3.svg";
/***/},
/***/581121:
/***/function(module,__unused_webpack_exports,__webpack_require__){module.exports=__webpack_require__.p+"88cd7135ef1417b08c9498c51d2cda59.svg";
/***/}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/9477.js.map