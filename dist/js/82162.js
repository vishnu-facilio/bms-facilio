"use strict";(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[82162],{
/***/182162:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){__webpack_require__.r(__webpack_exports__),
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */fc_image:function(){/* binding */return FcImage}
/* harmony export */});
/* harmony import */var _index_daa4148f_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(53291);const fcImageCss=":host{display:inline-block}",FcImage=class{constructor(hostRef){(0,_index_daa4148f_js__WEBPACK_IMPORTED_MODULE_0__.r)(this,hostRef),this.name=void 0,this.width=void 0,this.height=void 0,this.sym=void 0,this.baseURL=void 0,this.url=void 0}loadIcon(){var _a;this.sym=Symbol.for("@facilio/icons/config");let cdnUrl="https://static.facilio.com/icons/svg/",baseUrl=(null===(_a=window[this.sym])||void 0===_a?void 0:_a.baseUrl)?window[this.sym].baseURL:cdnUrl;this.url=`${baseUrl}images/${this.name}.webp`}render(){return this.loadIcon(),(0,_index_daa4148f_js__WEBPACK_IMPORTED_MODULE_0__.h)("img",{src:this.url,alt:this.name,style:{maxHeight:this.height+"px",maxWidth:this.width+"px",width:"auto",height:"auto"}})}static get watchers(){return{name:["loadIcon"]}}};FcImage.style=fcImageCss}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/82162.js.map