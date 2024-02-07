(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[5603],{
/***/680645:
/***/function(__unused_webpack_module,exports){
/*! ieee754. BSD-3-Clause License. Feross Aboukhadijeh <https://feross.org/opensource> */
exports.read=function(buffer,offset,isLE,mLen,nBytes){var e,m,eLen=8*nBytes-mLen-1,eMax=(1<<eLen)-1,eBias=eMax>>1,nBits=-7,i=isLE?nBytes-1:0,d=isLE?-1:1,s=buffer[offset+i];for(i+=d,e=s&(1<<-nBits)-1,s>>=-nBits,nBits+=eLen;nBits>0;e=256*e+buffer[offset+i],i+=d,nBits-=8);for(m=e&(1<<-nBits)-1,e>>=-nBits,nBits+=mLen;nBits>0;m=256*m+buffer[offset+i],i+=d,nBits-=8);if(0===e)e=1-eBias;else{if(e===eMax)return m?NaN:1/0*(s?-1:1);m+=Math.pow(2,mLen),e-=eBias}return(s?-1:1)*m*Math.pow(2,e-mLen)},exports.write=function(buffer,value,offset,isLE,mLen,nBytes){var e,m,c,eLen=8*nBytes-mLen-1,eMax=(1<<eLen)-1,eBias=eMax>>1,rt=23===mLen?Math.pow(2,-24)-Math.pow(2,-77):0,i=isLE?0:nBytes-1,d=isLE?1:-1,s=value<0||0===value&&1/value<0?1:0;for(value=Math.abs(value),isNaN(value)||value===1/0?(m=isNaN(value)?1:0,e=eMax):(e=Math.floor(Math.log(value)/Math.LN2),value*(c=Math.pow(2,-e))<1&&(e--,c*=2),value+=e+eBias>=1?rt/c:rt*Math.pow(2,1-eBias),value*c>=2&&(e++,c/=2),e+eBias>=eMax?(m=0,e=eMax):e+eBias>=1?(m=(value*c-1)*Math.pow(2,mLen),e+=eBias):(m=value*Math.pow(2,eBias-1)*Math.pow(2,mLen),e=0));mLen>=8;buffer[offset+i]=255&m,i+=d,m/=256,mLen-=8);for(e=e<<mLen|m,eLen+=mLen;eLen>0;buffer[offset+i]=255&e,i+=d,e/=256,eLen-=8);buffer[offset+i-d]|=128*s}
/***/},
/***/163805:
/***/function(module){"use strict";module.exports=isMobile,module.exports.isMobile=isMobile,module.exports["default"]=isMobile;var mobileRE=/(android|bb\d+|meego).+mobile|avantgo|bada\/|blackberry|blazer|compal|elaine|fennec|hiptop|iemobile|ip(hone|od)|iris|kindle|lge |maemo|midp|mmp|mobile.+firefox|netfront|opera m(ob|in)i|palm( os)?|phone|p(ixi|re)\/|plucker|pocket|psp|series[46]0|symbian|treo|up\.(browser|link)|vodafone|wap|windows (ce|phone)|xda|xiino/i,tabletRE=/(android|bb\d+|meego).+mobile|avantgo|bada\/|blackberry|blazer|compal|elaine|fennec|hiptop|iemobile|ip(hone|od)|iris|kindle|lge |maemo|midp|mmp|mobile.+firefox|netfront|opera m(ob|in)i|palm( os)?|phone|p(ixi|re)\/|plucker|pocket|psp|series[46]0|symbian|treo|up\.(browser|link)|vodafone|wap|windows (ce|phone)|xda|xiino|android|ipad|playbook|silk/i;function isMobile(opts){opts||(opts={});var ua=opts.ua;if(ua||"undefined"===typeof navigator||(ua=navigator.userAgent),ua&&ua.headers&&"string"===typeof ua.headers["user-agent"]&&(ua=ua.headers["user-agent"]),"string"!==typeof ua)return!1;var result=opts.tablet?tabletRE.test(ua):mobileRE.test(ua);return!result&&opts.tablet&&opts.featureDetect&&navigator&&navigator.maxTouchPoints>1&&-1!==ua.indexOf("Macintosh")&&-1!==ua.indexOf("Safari")&&(result=!0),result}
/***/},
/***/619755:
/***/function(module,exports){var __WEBPACK_AMD_DEFINE_ARRAY__,__WEBPACK_AMD_DEFINE_RESULT__;
/*!
 * jQuery JavaScript Library v3.7.1
 * https://jquery.com/
 *
 * Copyright OpenJS Foundation and other contributors
 * Released under the MIT license
 * https://jquery.org/license
 *
 * Date: 2023-08-28T13:37Z
 */(function(global,factory){"use strict";"object"===typeof module.exports?
// For CommonJS and CommonJS-like environments where a proper `window`
// is present, execute the factory and get jQuery.
// For environments that do not have a `window` with a `document`
// (such as Node.js), expose a factory as module.exports.
// This accentuates the need for the creation of a real `window`.
// e.g. var jQuery = require("jquery")(window);
// See ticket trac-14549 for more info.
module.exports=global.document?factory(global,!0):function(w){if(!w.document)throw new Error("jQuery requires a window with a document");return factory(w)}:factory(global);
// Pass this if window is not defined yet
})("undefined"!==typeof window?window:this,(function(window,noGlobal){
// Edge <= 12 - 13+, Firefox <=18 - 45+, IE 10 - 11, Safari 5.1 - 9+, iOS 6 - 9.1
// throw exceptions when non-strict code (e.g., ASP.NET 4.5) accesses strict mode
// arguments.callee.caller (trac-13335). But as of jQuery 3.0 (2016), strict mode should be common
// enough that all such attempts are guarded in a try block.
"use strict";var arr=[],getProto=Object.getPrototypeOf,slice=arr.slice,flat=arr.flat?function(array){return arr.flat.call(array)}:function(array){return arr.concat.apply([],array)},push=arr.push,indexOf=arr.indexOf,class2type={},toString=class2type.toString,hasOwn=class2type.hasOwnProperty,fnToString=hasOwn.toString,ObjectFunctionString=fnToString.call(Object),support={},isFunction=function(obj){
// Support: Chrome <=57, Firefox <=52
// In some browsers, typeof returns "function" for HTML <object> elements
// (i.e., `typeof document.createElement( "object" ) === "function"`).
// We don't want to classify *any* DOM node as a function.
// Support: QtWeb <=3.8.5, WebKit <=534.34, wkhtmltopdf tool <=0.12.5
// Plus for old WebKit, typeof returns "function" for HTML collections
// (e.g., `typeof document.getElementsByTagName("div") === "function"`). (gh-4756)
return"function"===typeof obj&&"number"!==typeof obj.nodeType&&"function"!==typeof obj.item},isWindow=function(obj){return null!=obj&&obj===obj.window},document=window.document,preservedScriptAttributes={type:!0,src:!0,nonce:!0,noModule:!0};function DOMEval(code,node,doc){doc=doc||document;var i,val,script=doc.createElement("script");if(script.text=code,node)for(i in preservedScriptAttributes)
// Support: Firefox 64+, Edge 18+
// Some browsers don't support the "nonce" property on scripts.
// On the other hand, just using `getAttribute` is not enough as
// the `nonce` attribute is reset to an empty string whenever it
// becomes browsing-context connected.
// See https://github.com/whatwg/html/issues/2369
// See https://html.spec.whatwg.org/#nonce-attributes
// The `node.getAttribute` check was added for the sake of
// `jQuery.globalEval` so that it can fake a nonce-containing node
// via an object.
val=node[i]||node.getAttribute&&node.getAttribute(i),val&&script.setAttribute(i,val);doc.head.appendChild(script).parentNode.removeChild(script)}function toType(obj){return null==obj?obj+"":"object"===typeof obj||"function"===typeof obj?class2type[toString.call(obj)]||"object":typeof obj;
// Support: Android <=2.3 only (functionish RegExp)
}
/* global Symbol */
// Defining this global in .eslintrc.json would create a danger of using the global
// unguarded in another place, it seems safer to define global only for this module
var version="3.7.1",rhtmlSuffix=/HTML$/i,
// Define a local copy of jQuery
jQuery=function(selector,context){
// The jQuery object is actually just the init constructor 'enhanced'
// Need init if jQuery is called (just allow error to be thrown if not included)
return new jQuery.fn.init(selector,context)};function isArrayLike(obj){
// Support: real iOS 8.2 only (not reproducible in simulator)
// `in` check used to prevent JIT error (gh-2145)
// hasOwn isn't used here due to false negatives
// regarding Nodelist length in IE
var length=!!obj&&"length"in obj&&obj.length,type=toType(obj);return!isFunction(obj)&&!isWindow(obj)&&("array"===type||0===length||"number"===typeof length&&length>0&&length-1 in obj)}function nodeName(elem,name){return elem.nodeName&&elem.nodeName.toLowerCase()===name.toLowerCase()}jQuery.fn=jQuery.prototype={
// The current version of jQuery being used
jquery:version,constructor:jQuery,
// The default length of a jQuery object is 0
length:0,toArray:function(){return slice.call(this)},
// Get the Nth element in the matched element set OR
// Get the whole matched element set as a clean array
get:function(num){
// Return all the elements in a clean array
return null==num?slice.call(this):num<0?this[num+this.length]:this[num];
// Return just the one element from the set
},
// Take an array of elements and push it onto the stack
// (returning the new matched element set)
pushStack:function(elems){
// Build a new jQuery matched element set
var ret=jQuery.merge(this.constructor(),elems);
// Add the old object onto the stack (as a reference)
// Return the newly-formed element set
return ret.prevObject=this,ret},
// Execute a callback for every element in the matched set.
each:function(callback){return jQuery.each(this,callback)},map:function(callback){return this.pushStack(jQuery.map(this,(function(elem,i){return callback.call(elem,i,elem)})))},slice:function(){return this.pushStack(slice.apply(this,arguments))},first:function(){return this.eq(0)},last:function(){return this.eq(-1)},even:function(){return this.pushStack(jQuery.grep(this,(function(_elem,i){return(i+1)%2})))},odd:function(){return this.pushStack(jQuery.grep(this,(function(_elem,i){return i%2})))},eq:function(i){var len=this.length,j=+i+(i<0?len:0);return this.pushStack(j>=0&&j<len?[this[j]]:[])},end:function(){return this.prevObject||this.constructor()},
// For internal use only.
// Behaves like an Array's method, not like a jQuery method.
push:push,sort:arr.sort,splice:arr.splice},jQuery.extend=jQuery.fn.extend=function(){var options,name,src,copy,copyIsArray,clone,target=arguments[0]||{},i=1,length=arguments.length,deep=!1;
// Handle a deep copy situation
for("boolean"===typeof target&&(deep=target,
// Skip the boolean and the target
target=arguments[i]||{},i++),
// Handle case when target is a string or something (possible in deep copy)
"object"===typeof target||isFunction(target)||(target={}),
// Extend jQuery itself if only one argument is passed
i===length&&(target=this,i--);i<length;i++)
// Only deal with non-null/undefined values
if(null!=(options=arguments[i]))
// Extend the base object
for(name in options)copy=options[name],
// Prevent Object.prototype pollution
// Prevent never-ending loop
"__proto__"!==name&&target!==copy&&(
// Recurse if we're merging plain objects or arrays
deep&&copy&&(jQuery.isPlainObject(copy)||(copyIsArray=Array.isArray(copy)))?(src=target[name],
// Ensure proper type for the source value
clone=copyIsArray&&!Array.isArray(src)?[]:copyIsArray||jQuery.isPlainObject(src)?src:{},copyIsArray=!1,
// Never move original objects, clone them
target[name]=jQuery.extend(deep,clone,copy)):void 0!==copy&&(target[name]=copy));
// Return the modified object
return target},jQuery.extend({
// Unique for each copy of jQuery on the page
expando:"jQuery"+(version+Math.random()).replace(/\D/g,""),
// Assume jQuery is ready without the ready module
isReady:!0,error:function(msg){throw new Error(msg)},noop:function(){},isPlainObject:function(obj){var proto,Ctor;
// Detect obvious negatives
// Use toString instead of jQuery.type to catch host objects
return!(!obj||"[object Object]"!==toString.call(obj))&&(
// Objects with no prototype (e.g., `Object.create( null )`) are plain
proto=getProto(obj),!proto||(
// Objects with prototype are plain iff they were constructed by a global Object function
Ctor=hasOwn.call(proto,"constructor")&&proto.constructor,"function"===typeof Ctor&&fnToString.call(Ctor)===ObjectFunctionString))},isEmptyObject:function(obj){var name;for(name in obj)return!1;return!0},
// Evaluates a script in a provided context; falls back to the global one
// if not specified.
globalEval:function(code,options,doc){DOMEval(code,{nonce:options&&options.nonce},doc)},each:function(obj,callback){var length,i=0;if(isArrayLike(obj)){for(length=obj.length;i<length;i++)if(!1===callback.call(obj[i],i,obj[i]))break}else for(i in obj)if(!1===callback.call(obj[i],i,obj[i]))break;return obj},
// Retrieve the text value of an array of DOM nodes
text:function(elem){var node,ret="",i=0,nodeType=elem.nodeType;if(!nodeType)
// If no nodeType, this is expected to be an array
while(node=elem[i++])
// Do not traverse comment nodes
ret+=jQuery.text(node);return 1===nodeType||11===nodeType?elem.textContent:9===nodeType?elem.documentElement.textContent:3===nodeType||4===nodeType?elem.nodeValue:ret},
// results is for internal usage only
makeArray:function(arr,results){var ret=results||[];return null!=arr&&(isArrayLike(Object(arr))?jQuery.merge(ret,"string"===typeof arr?[arr]:arr):push.call(ret,arr)),ret},inArray:function(elem,arr,i){return null==arr?-1:indexOf.call(arr,elem,i)},isXMLDoc:function(elem){var namespace=elem&&elem.namespaceURI,docElem=elem&&(elem.ownerDocument||elem).documentElement;
// Assume HTML when documentElement doesn't yet exist, such as inside
// document fragments.
return!rhtmlSuffix.test(namespace||docElem&&docElem.nodeName||"HTML")},
// Support: Android <=4.0 only, PhantomJS 1 only
// push.apply(_, arraylike) throws on ancient WebKit
merge:function(first,second){for(var len=+second.length,j=0,i=first.length;j<len;j++)first[i++]=second[j];return first.length=i,first},grep:function(elems,callback,invert){
// Go through the array, only saving the items
// that pass the validator function
for(var callbackInverse,matches=[],i=0,length=elems.length,callbackExpect=!invert;i<length;i++)callbackInverse=!callback(elems[i],i),callbackInverse!==callbackExpect&&matches.push(elems[i]);return matches},
// arg is for internal usage only
map:function(elems,callback,arg){var length,value,i=0,ret=[];
// Go through the array, translating each of the items to their new values
if(isArrayLike(elems))for(length=elems.length;i<length;i++)value=callback(elems[i],i,arg),null!=value&&ret.push(value);
// Go through every key on the object,
else for(i in elems)value=callback(elems[i],i,arg),null!=value&&ret.push(value);
// Flatten any nested arrays
return flat(ret)},
// A global GUID counter for objects
guid:1,
// jQuery.support is not used in Core but other projects attach their
// properties to it so it needs to exist.
support:support}),"function"===typeof Symbol&&(jQuery.fn[Symbol.iterator]=arr[Symbol.iterator]),
// Populate the class2type map
jQuery.each("Boolean Number String Function Array Date RegExp Object Error Symbol".split(" "),(function(_i,name){class2type["[object "+name+"]"]=name.toLowerCase()}));var pop=arr.pop,sort=arr.sort,splice=arr.splice,whitespace="[\\x20\\t\\r\\n\\f]",rtrimCSS=new RegExp("^"+whitespace+"+|((?:^|[^\\\\])(?:\\\\.)*)"+whitespace+"+$","g");
// Note: an element does not contain itself
jQuery.contains=function(a,b){var bup=b&&b.parentNode;return a===bup||!(!bup||1!==bup.nodeType||
// Support: IE 9 - 11+
// IE doesn't have `contains` on SVG.
!(a.contains?a.contains(bup):a.compareDocumentPosition&&16&a.compareDocumentPosition(bup)))};
// CSS string/identifier serialization
// https://drafts.csswg.org/cssom/#common-serializing-idioms
var rcssescape=/([\0-\x1f\x7f]|^-?\d)|^-$|[^\x80-\uFFFF\w-]/g;function fcssescape(ch,asCodePoint){return asCodePoint?
// U+0000 NULL becomes U+FFFD REPLACEMENT CHARACTER
"\0"===ch?"�":ch.slice(0,-1)+"\\"+ch.charCodeAt(ch.length-1).toString(16)+" ":"\\"+ch;
// Other potentially-special ASCII characters get backslash-escaped
}jQuery.escapeSelector=function(sel){return(sel+"").replace(rcssescape,fcssescape)};var preferredDoc=document,pushNative=push;(function(){var i,Expr,outermostContext,sortInput,hasDuplicate,
// Local document vars
document,documentElement,documentIsHTML,rbuggyQSA,matches,push=pushNative,
// Instance-specific data
expando=jQuery.expando,dirruns=0,done=0,classCache=createCache(),tokenCache=createCache(),compilerCache=createCache(),nonnativeSelectorCache=createCache(),sortOrder=function(a,b){return a===b&&(hasDuplicate=!0),0},booleans="checked|selected|async|autofocus|autoplay|controls|defer|disabled|hidden|ismap|loop|multiple|open|readonly|required|scoped",
// Regular expressions
// https://www.w3.org/TR/css-syntax-3/#ident-token-diagram
identifier="(?:\\\\[\\da-fA-F]{1,6}"+whitespace+"?|\\\\[^\\r\\n\\f]|[\\w-]|[^\0-\\x7f])+",
// Attribute selectors: https://www.w3.org/TR/selectors/#attribute-selectors
attributes="\\["+whitespace+"*("+identifier+")(?:"+whitespace+
// Operator (capture 2)
"*([*^$|!~]?=)"+whitespace+
// "Attribute values must be CSS identifiers [capture 5] or strings [capture 3 or capture 4]"
"*(?:'((?:\\\\.|[^\\\\'])*)'|\"((?:\\\\.|[^\\\\\"])*)\"|("+identifier+"))|)"+whitespace+"*\\]",pseudos=":("+identifier+")(?:\\((('((?:\\\\.|[^\\\\'])*)'|\"((?:\\\\.|[^\\\\\"])*)\")|((?:\\\\.|[^\\\\()[\\]]|"+attributes+")*)|.*)\\)|)",
// Leading and non-escaped trailing whitespace, capturing some non-whitespace characters preceding the latter
rwhitespace=new RegExp(whitespace+"+","g"),rcomma=new RegExp("^"+whitespace+"*,"+whitespace+"*"),rleadingCombinator=new RegExp("^"+whitespace+"*([>+~]|"+whitespace+")"+whitespace+"*"),rdescend=new RegExp(whitespace+"|>"),rpseudo=new RegExp(pseudos),ridentifier=new RegExp("^"+identifier+"$"),matchExpr={ID:new RegExp("^#("+identifier+")"),CLASS:new RegExp("^\\.("+identifier+")"),TAG:new RegExp("^("+identifier+"|[*])"),ATTR:new RegExp("^"+attributes),PSEUDO:new RegExp("^"+pseudos),CHILD:new RegExp("^:(only|first|last|nth|nth-last)-(child|of-type)(?:\\("+whitespace+"*(even|odd|(([+-]|)(\\d*)n|)"+whitespace+"*(?:([+-]|)"+whitespace+"*(\\d+)|))"+whitespace+"*\\)|)","i"),bool:new RegExp("^(?:"+booleans+")$","i"),
// For use in libraries implementing .is()
// We use this for POS matching in `select`
needsContext:new RegExp("^"+whitespace+"*[>+~]|:(even|odd|eq|gt|lt|nth|first|last)(?:\\("+whitespace+"*((?:-\\d)?\\d*)"+whitespace+"*\\)|)(?=[^-]|$)","i")},rinputs=/^(?:input|select|textarea|button)$/i,rheader=/^h\d$/i,
// Easily-parseable/retrievable ID or TAG or CLASS selectors
rquickExpr=/^(?:#([\w-]+)|(\w+)|\.([\w-]+))$/,rsibling=/[+~]/,
// CSS escapes
// https://www.w3.org/TR/CSS21/syndata.html#escaped-characters
runescape=new RegExp("\\\\[\\da-fA-F]{1,6}"+whitespace+"?|\\\\([^\\r\\n\\f])","g"),funescape=function(escape,nonHex){var high="0x"+escape.slice(1)-65536;return nonHex||(high<0?String.fromCharCode(high+65536):String.fromCharCode(high>>10|55296,1023&high|56320));
// Replace a hexadecimal escape sequence with the encoded Unicode code point
// Support: IE <=11+
// For values outside the Basic Multilingual Plane (BMP), manually construct a
// surrogate pair
},
// Used for iframes; see `setDocument`.
// Support: IE 9 - 11+, Edge 12 - 18+
// Removing the function wrapper causes a "Permission Denied"
// error in IE/Edge.
unloadHandler=function(){setDocument()},inDisabledFieldset=addCombinator((function(elem){return!0===elem.disabled&&nodeName(elem,"fieldset")}),{dir:"parentNode",next:"legend"});
// Support: IE <=9 only
// Accessing document.activeElement can throw unexpectedly
// https://bugs.jquery.com/ticket/13393
function safeActiveElement(){try{return document.activeElement}catch(err){}}
// Optimize for push.apply( _, NodeList )
try{push.apply(arr=slice.call(preferredDoc.childNodes),preferredDoc.childNodes),
// Support: Android <=4.0
// Detect silently failing push.apply
// eslint-disable-next-line no-unused-expressions
arr[preferredDoc.childNodes.length].nodeType}catch(e){push={apply:function(target,els){pushNative.apply(target,slice.call(els))},call:function(target){pushNative.apply(target,slice.call(arguments,1))}}}function find(selector,context,results,seed){var m,i,elem,nid,match,groups,newSelector,newContext=context&&context.ownerDocument,
// nodeType defaults to 9, since context defaults to document
nodeType=context?context.nodeType:9;
// Return early from calls with invalid selector or context
if(results=results||[],"string"!==typeof selector||!selector||1!==nodeType&&9!==nodeType&&11!==nodeType)return results;
// Try to shortcut find operations (as opposed to filters) in HTML documents
if(!seed&&(setDocument(context),context=context||document,documentIsHTML)){
// If the selector is sufficiently simple, try using a "get*By*" DOM method
// (excepting DocumentFragment context, where the methods don't exist)
if(11!==nodeType&&(match=rquickExpr.exec(selector)))
// ID selector
if(m=match[1]){
// Document context
if(9===nodeType){if(!(elem=context.getElementById(m)))return results;
// Element context
// Support: IE 9 only
// getElementById can match elements by name instead of ID
if(elem.id===m)return push.call(results,elem),results}else
// Support: IE 9 only
// getElementById can match elements by name instead of ID
if(newContext&&(elem=newContext.getElementById(m))&&find.contains(context,elem)&&elem.id===m)return push.call(results,elem),results;
// Type selector
}else{if(match[2])return push.apply(results,context.getElementsByTagName(selector)),results;
// Class selector
if((m=match[3])&&context.getElementsByClassName)return push.apply(results,context.getElementsByClassName(m)),results}
// Take advantage of querySelectorAll
if(!nonnativeSelectorCache[selector+" "]&&(!rbuggyQSA||!rbuggyQSA.test(selector))){
// qSA considers elements outside a scoping root when evaluating child or
// descendant combinators, which is not what we want.
// In such cases, we work around the behavior by prefixing every selector in the
// list with an ID selector referencing the scope context.
// The technique has to be used as well when a leading combinator is used
// as such selectors are not recognized by querySelectorAll.
// Thanks to Andrew Dupont for this technique.
if(newSelector=selector,newContext=context,1===nodeType&&(rdescend.test(selector)||rleadingCombinator.test(selector))){
// Expand context for sibling selectors
newContext=rsibling.test(selector)&&testContext(context.parentNode)||context,
// We can use :scope instead of the ID hack if the browser
// supports it & if we're not changing the context.
// Support: IE 11+, Edge 17 - 18+
// IE/Edge sometimes throw a "Permission denied" error when
// strict-comparing two documents; shallow comparisons work.
// eslint-disable-next-line eqeqeq
newContext==context&&support.scope||(
// Capture the context ID, setting it first if necessary
(nid=context.getAttribute("id"))?nid=jQuery.escapeSelector(nid):context.setAttribute("id",nid=expando)),
// Prefix every selector in the list
groups=tokenize(selector),i=groups.length;while(i--)groups[i]=(nid?"#"+nid:":scope")+" "+toSelector(groups[i]);newSelector=groups.join(",")}try{return push.apply(results,newContext.querySelectorAll(newSelector)),results}catch(qsaError){nonnativeSelectorCache(selector,!0)}finally{nid===expando&&context.removeAttribute("id")}}}
// All others
return select(selector.replace(rtrimCSS,"$1"),context,results,seed)}
/**
 * Create key-value caches of limited size
 * @returns {function(string, object)} Returns the Object data after storing it on itself with
 *	property name the (space-suffixed) string and (if the cache is larger than Expr.cacheLength)
 *	deleting the oldest entry
 */function createCache(){var keys=[];function cache(key,value){
// Use (key + " ") to avoid collision with native prototype properties
// (see https://github.com/jquery/sizzle/issues/157)
return keys.push(key+" ")>Expr.cacheLength&&
// Only keep the most recent entries
delete cache[keys.shift()],cache[key+" "]=value}return cache}
/**
 * Mark a function for special use by jQuery selector module
 * @param {Function} fn The function to mark
 */function markFunction(fn){return fn[expando]=!0,fn}
/**
 * Support testing using an element
 * @param {Function} fn Passed the created element and returns a boolean result
 */function assert(fn){var el=document.createElement("fieldset");try{return!!fn(el)}catch(e){return!1}finally{
// Remove from its parent by default
el.parentNode&&el.parentNode.removeChild(el),
// release memory in IE
el=null}}
/**
 * Returns a function to use in pseudos for input types
 * @param {String} type
 */function createInputPseudo(type){return function(elem){return nodeName(elem,"input")&&elem.type===type}}
/**
 * Returns a function to use in pseudos for buttons
 * @param {String} type
 */function createButtonPseudo(type){return function(elem){return(nodeName(elem,"input")||nodeName(elem,"button"))&&elem.type===type}}
/**
 * Returns a function to use in pseudos for :enabled/:disabled
 * @param {Boolean} disabled true for :disabled; false for :enabled
 */function createDisabledPseudo(disabled){
// Known :disabled false positives: fieldset[disabled] > legend:nth-of-type(n+2) :can-disable
return function(elem){
// Only certain elements can match :enabled or :disabled
// https://html.spec.whatwg.org/multipage/scripting.html#selector-enabled
// https://html.spec.whatwg.org/multipage/scripting.html#selector-disabled
return"form"in elem?
// Check for inherited disabledness on relevant non-disabled elements:
// * listed form-associated elements in a disabled fieldset
//   https://html.spec.whatwg.org/multipage/forms.html#category-listed
//   https://html.spec.whatwg.org/multipage/forms.html#concept-fe-disabled
// * option elements in a disabled optgroup
//   https://html.spec.whatwg.org/multipage/forms.html#concept-option-disabled
// All such elements have a "form" property.
elem.parentNode&&!1===elem.disabled?
// Option elements defer to a parent optgroup if present
"label"in elem?"label"in elem.parentNode?elem.parentNode.disabled===disabled:elem.disabled===disabled:elem.isDisabled===disabled||
// Where there is no isDisabled, check manually
elem.isDisabled!==!disabled&&inDisabledFieldset(elem)===disabled:elem.disabled===disabled:"label"in elem&&elem.disabled===disabled;
// Remaining elements are neither :enabled nor :disabled
}}
/**
 * Returns a function to use in pseudos for positionals
 * @param {Function} fn
 */function createPositionalPseudo(fn){return markFunction((function(argument){return argument=+argument,markFunction((function(seed,matches){var j,matchIndexes=fn([],seed.length,argument),i=matchIndexes.length;
// Match elements found at the specified indexes
while(i--)seed[j=matchIndexes[i]]&&(seed[j]=!(matches[j]=seed[j]))}))}))}
/**
 * Checks a node for validity as a jQuery selector context
 * @param {Element|Object=} context
 * @returns {Element|Object|Boolean} The input node if acceptable, otherwise a falsy value
 */function testContext(context){return context&&"undefined"!==typeof context.getElementsByTagName&&context}
/**
 * Sets document-related variables once based on the current document
 * @param {Element|Object} [node] An element or document object to use to set the document
 * @returns {Object} Returns the current document
 */function setDocument(node){var subWindow,doc=node?node.ownerDocument||node:preferredDoc;
// Return early if doc is invalid or already selected
// Support: IE 11+, Edge 17 - 18+
// IE/Edge sometimes throw a "Permission denied" error when strict-comparing
// two documents; shallow comparisons work.
// eslint-disable-next-line eqeqeq
return doc!=document&&9===doc.nodeType&&doc.documentElement?(
// Update global variables
document=doc,documentElement=document.documentElement,documentIsHTML=!jQuery.isXMLDoc(document),
// Support: iOS 7 only, IE 9 - 11+
// Older browsers didn't support unprefixed `matches`.
matches=documentElement.matches||documentElement.webkitMatchesSelector||documentElement.msMatchesSelector,
// Support: IE 9 - 11+, Edge 12 - 18+
// Accessing iframe documents after unload throws "permission denied" errors
// (see trac-13936).
// Limit the fix to IE & Edge Legacy; despite Edge 15+ implementing `matches`,
// all IE 9+ and Edge Legacy versions implement `msMatchesSelector` as well.
documentElement.msMatchesSelector&&
// Support: IE 11+, Edge 17 - 18+
// IE/Edge sometimes throw a "Permission denied" error when strict-comparing
// two documents; shallow comparisons work.
// eslint-disable-next-line eqeqeq
preferredDoc!=document&&(subWindow=document.defaultView)&&subWindow.top!==subWindow&&
// Support: IE 9 - 11+, Edge 12 - 18+
subWindow.addEventListener("unload",unloadHandler),
// Support: IE <10
// Check if getElementById returns elements by name
// The broken getElementById methods don't pick up programmatically-set names,
// so use a roundabout getElementsByName test
support.getById=assert((function(el){return documentElement.appendChild(el).id=jQuery.expando,!document.getElementsByName||!document.getElementsByName(jQuery.expando).length})),
// Support: IE 9 only
// Check to see if it's possible to do matchesSelector
// on a disconnected node.
support.disconnectedMatch=assert((function(el){return matches.call(el,"*")})),
// Support: IE 9 - 11+, Edge 12 - 18+
// IE/Edge don't support the :scope pseudo-class.
support.scope=assert((function(){return document.querySelectorAll(":scope")})),
// Support: Chrome 105 - 111 only, Safari 15.4 - 16.3 only
// Make sure the `:has()` argument is parsed unforgivingly.
// We include `*` in the test to detect buggy implementations that are
// _selectively_ forgiving (specifically when the list includes at least
// one valid selector).
// Note that we treat complete lack of support for `:has()` as if it were
// spec-compliant support, which is fine because use of `:has()` in such
// environments will fail in the qSA path and fall back to jQuery traversal
// anyway.
support.cssHas=assert((function(){try{return document.querySelector(":has(*,:jqfake)"),!1}catch(e){return!0}})),
// ID filter and find
support.getById?(Expr.filter.ID=function(id){var attrId=id.replace(runescape,funescape);return function(elem){return elem.getAttribute("id")===attrId}},Expr.find.ID=function(id,context){if("undefined"!==typeof context.getElementById&&documentIsHTML){var elem=context.getElementById(id);return elem?[elem]:[]}}):(Expr.filter.ID=function(id){var attrId=id.replace(runescape,funescape);return function(elem){var node="undefined"!==typeof elem.getAttributeNode&&elem.getAttributeNode("id");return node&&node.value===attrId}},
// Support: IE 6 - 7 only
// getElementById is not reliable as a find shortcut
Expr.find.ID=function(id,context){if("undefined"!==typeof context.getElementById&&documentIsHTML){var node,i,elems,elem=context.getElementById(id);if(elem){if(
// Verify the id attribute
node=elem.getAttributeNode("id"),node&&node.value===id)return[elem];
// Fall back on getElementsByName
elems=context.getElementsByName(id),i=0;while(elem=elems[i++])if(node=elem.getAttributeNode("id"),node&&node.value===id)return[elem]}return[]}}),
// Tag
Expr.find.TAG=function(tag,context){return"undefined"!==typeof context.getElementsByTagName?context.getElementsByTagName(tag):context.querySelectorAll(tag)},
// Class
Expr.find.CLASS=function(className,context){if("undefined"!==typeof context.getElementsByClassName&&documentIsHTML)return context.getElementsByClassName(className)},
/* QSA/matchesSelector
	---------------------------------------------------------------------- */
// QSA and matchesSelector support
rbuggyQSA=[],
// Build QSA regex
// Regex strategy adopted from Diego Perini
assert((function(el){var input;documentElement.appendChild(el).innerHTML="<a id='"+expando+"' href='' disabled='disabled'></a><select id='"+expando+"-\r\\' disabled='disabled'><option selected=''></option></select>",
// Support: iOS <=7 - 8 only
// Boolean attributes and "value" are not treated correctly in some XML documents
el.querySelectorAll("[selected]").length||rbuggyQSA.push("\\["+whitespace+"*(?:value|"+booleans+")"),
// Support: iOS <=7 - 8 only
el.querySelectorAll("[id~="+expando+"-]").length||rbuggyQSA.push("~="),
// Support: iOS 8 only
// https://bugs.webkit.org/show_bug.cgi?id=136851
// In-page `selector#id sibling-combinator selector` fails
el.querySelectorAll("a#"+expando+"+*").length||rbuggyQSA.push(".#.+[+~]"),
// Support: Chrome <=105+, Firefox <=104+, Safari <=15.4+
// In some of the document kinds, these selectors wouldn't work natively.
// This is probably OK but for backwards compatibility we want to maintain
// handling them through jQuery traversal in jQuery 3.x.
el.querySelectorAll(":checked").length||rbuggyQSA.push(":checked"),
// Support: Windows 8 Native Apps
// The type and name attributes are restricted during .innerHTML assignment
input=document.createElement("input"),input.setAttribute("type","hidden"),el.appendChild(input).setAttribute("name","D"),
// Support: IE 9 - 11+
// IE's :disabled selector does not pick up the children of disabled fieldsets
// Support: Chrome <=105+, Firefox <=104+, Safari <=15.4+
// In some of the document kinds, these selectors wouldn't work natively.
// This is probably OK but for backwards compatibility we want to maintain
// handling them through jQuery traversal in jQuery 3.x.
documentElement.appendChild(el).disabled=!0,2!==el.querySelectorAll(":disabled").length&&rbuggyQSA.push(":enabled",":disabled"),
// Support: IE 11+, Edge 15 - 18+
// IE 11/Edge don't find elements on a `[name='']` query in some cases.
// Adding a temporary attribute to the document before the selection works
// around the issue.
// Interestingly, IE 10 & older don't seem to have the issue.
input=document.createElement("input"),input.setAttribute("name",""),el.appendChild(input),el.querySelectorAll("[name='']").length||rbuggyQSA.push("\\["+whitespace+"*name"+whitespace+"*="+whitespace+"*(?:''|\"\")")})),support.cssHas||
// Support: Chrome 105 - 110+, Safari 15.4 - 16.3+
// Our regular `try-catch` mechanism fails to detect natively-unsupported
// pseudo-classes inside `:has()` (such as `:has(:contains("Foo"))`)
// in browsers that parse the `:has()` argument as a forgiving selector list.
// https://drafts.csswg.org/selectors/#relational now requires the argument
// to be parsed unforgivingly, but browsers have not yet fully adjusted.
rbuggyQSA.push(":has"),rbuggyQSA=rbuggyQSA.length&&new RegExp(rbuggyQSA.join("|")),
/* Sorting
	---------------------------------------------------------------------- */
// Document order sorting
sortOrder=function(a,b){
// Flag for duplicate removal
if(a===b)return hasDuplicate=!0,0;
// Sort on method existence if only one input has compareDocumentPosition
var compare=!a.compareDocumentPosition-!b.compareDocumentPosition;return compare||(
// Calculate position if both inputs belong to the same document
// Support: IE 11+, Edge 17 - 18+
// IE/Edge sometimes throw a "Permission denied" error when strict-comparing
// two documents; shallow comparisons work.
// eslint-disable-next-line eqeqeq
compare=(a.ownerDocument||a)==(b.ownerDocument||b)?a.compareDocumentPosition(b):
// Otherwise we know they are disconnected
1,
// Disconnected nodes
1&compare||!support.sortDetached&&b.compareDocumentPosition(a)===compare?
// Choose the first element that is related to our preferred document
// Support: IE 11+, Edge 17 - 18+
// IE/Edge sometimes throw a "Permission denied" error when strict-comparing
// two documents; shallow comparisons work.
// eslint-disable-next-line eqeqeq
a===document||a.ownerDocument==preferredDoc&&find.contains(preferredDoc,a)?-1:
// Support: IE 11+, Edge 17 - 18+
// IE/Edge sometimes throw a "Permission denied" error when strict-comparing
// two documents; shallow comparisons work.
// eslint-disable-next-line eqeqeq
b===document||b.ownerDocument==preferredDoc&&find.contains(preferredDoc,b)?1:sortInput?indexOf.call(sortInput,a)-indexOf.call(sortInput,b):0:4&compare?-1:1)},document):document}
// Add button/input type pseudos
for(i in find.matches=function(expr,elements){return find(expr,null,null,elements)},find.matchesSelector=function(elem,expr){if(setDocument(elem),documentIsHTML&&!nonnativeSelectorCache[expr+" "]&&(!rbuggyQSA||!rbuggyQSA.test(expr)))try{var ret=matches.call(elem,expr);
// IE 9's matchesSelector returns false on disconnected nodes
if(ret||support.disconnectedMatch||
// As well, disconnected nodes are said to be in a document
// fragment in IE 9
elem.document&&11!==elem.document.nodeType)return ret}catch(e){nonnativeSelectorCache(expr,!0)}return find(expr,document,null,[elem]).length>0},find.contains=function(context,elem){
// Set document vars if needed
// Support: IE 11+, Edge 17 - 18+
// IE/Edge sometimes throw a "Permission denied" error when strict-comparing
// two documents; shallow comparisons work.
// eslint-disable-next-line eqeqeq
return(context.ownerDocument||context)!=document&&setDocument(context),jQuery.contains(context,elem)},find.attr=function(elem,name){
// Set document vars if needed
// Support: IE 11+, Edge 17 - 18+
// IE/Edge sometimes throw a "Permission denied" error when strict-comparing
// two documents; shallow comparisons work.
// eslint-disable-next-line eqeqeq
(elem.ownerDocument||elem)!=document&&setDocument(elem);var fn=Expr.attrHandle[name.toLowerCase()],
// Don't get fooled by Object.prototype properties (see trac-13807)
val=fn&&hasOwn.call(Expr.attrHandle,name.toLowerCase())?fn(elem,name,!documentIsHTML):void 0;return void 0!==val?val:elem.getAttribute(name)},find.error=function(msg){throw new Error("Syntax error, unrecognized expression: "+msg)},
/**
 * Document sorting and removing duplicates
 * @param {ArrayLike} results
 */
jQuery.uniqueSort=function(results){var elem,duplicates=[],j=0,i=0;
// Unless we *know* we can detect duplicates, assume their presence

// Support: Android <=4.0+
// Testing for detecting duplicates is unpredictable so instead assume we can't
// depend on duplicate detection in all browsers without a stable sort.
if(hasDuplicate=!support.sortStable,sortInput=!support.sortStable&&slice.call(results,0),sort.call(results,sortOrder),hasDuplicate){while(elem=results[i++])elem===results[i]&&(j=duplicates.push(i));while(j--)splice.call(results,duplicates[j],1)}
// Clear input after sorting to release objects
// See https://github.com/jquery/sizzle/pull/225
return sortInput=null,results},jQuery.fn.uniqueSort=function(){return this.pushStack(jQuery.uniqueSort(slice.apply(this)))},Expr=jQuery.expr={
// Can be adjusted by the user
cacheLength:50,createPseudo:markFunction,match:matchExpr,attrHandle:{},find:{},relative:{">":{dir:"parentNode",first:!0}," ":{dir:"parentNode"},"+":{dir:"previousSibling",first:!0},"~":{dir:"previousSibling"}},preFilter:{ATTR:function(match){return match[1]=match[1].replace(runescape,funescape),
// Move the given value to match[3] whether quoted or unquoted
match[3]=(match[3]||match[4]||match[5]||"").replace(runescape,funescape),"~="===match[2]&&(match[3]=" "+match[3]+" "),match.slice(0,4)},CHILD:function(match){
/* matches from matchExpr["CHILD"]
				1 type (only|nth|...)
				2 what (child|of-type)
				3 argument (even|odd|\d*|\d*n([+-]\d+)?|...)
				4 xn-component of xn+y argument ([+-]?\d*n|)
				5 sign of xn-component
				6 x of xn-component
				7 sign of y-component
				8 y of y-component
			*/
return match[1]=match[1].toLowerCase(),"nth"===match[1].slice(0,3)?(
// nth-* requires argument
match[3]||find.error(match[0]),
// numeric x and y parameters for Expr.filter.CHILD
// remember that false/true cast respectively to 0/1
match[4]=+(match[4]?match[5]+(match[6]||1):2*("even"===match[3]||"odd"===match[3])),match[5]=+(match[7]+match[8]||"odd"===match[3])):match[3]&&find.error(match[0]),match},PSEUDO:function(match){var excess,unquoted=!match[6]&&match[2];return matchExpr.CHILD.test(match[0])?null:(
// Accept quoted arguments as-is
match[3]?match[2]=match[4]||match[5]||"":unquoted&&rpseudo.test(unquoted)&&(
// Get excess from tokenize (recursively)
excess=tokenize(unquoted,!0))&&(
// advance to the next closing parenthesis
excess=unquoted.indexOf(")",unquoted.length-excess)-unquoted.length)&&(
// excess is a negative index
match[0]=match[0].slice(0,excess),match[2]=unquoted.slice(0,excess)),match.slice(0,3))}},filter:{TAG:function(nodeNameSelector){var expectedNodeName=nodeNameSelector.replace(runescape,funescape).toLowerCase();return"*"===nodeNameSelector?function(){return!0}:function(elem){return nodeName(elem,expectedNodeName)}},CLASS:function(className){var pattern=classCache[className+" "];return pattern||(pattern=new RegExp("(^|"+whitespace+")"+className+"("+whitespace+"|$)"))&&classCache(className,(function(elem){return pattern.test("string"===typeof elem.className&&elem.className||"undefined"!==typeof elem.getAttribute&&elem.getAttribute("class")||"")}))},ATTR:function(name,operator,check){return function(elem){var result=find.attr(elem,name);return null==result?"!="===operator:!operator||(result+="","="===operator?result===check:"!="===operator?result!==check:"^="===operator?check&&0===result.indexOf(check):"*="===operator?check&&result.indexOf(check)>-1:"$="===operator?check&&result.slice(-check.length)===check:"~="===operator?(" "+result.replace(rwhitespace," ")+" ").indexOf(check)>-1:"|="===operator&&(result===check||result.slice(0,check.length+1)===check+"-"))}},CHILD:function(type,what,_argument,first,last){var simple="nth"!==type.slice(0,3),forward="last"!==type.slice(-4),ofType="of-type"===what;return 1===first&&0===last?
// Shortcut for :nth-*(n)
function(elem){return!!elem.parentNode}:function(elem,_context,xml){var cache,outerCache,node,nodeIndex,start,dir=simple!==forward?"nextSibling":"previousSibling",parent=elem.parentNode,name=ofType&&elem.nodeName.toLowerCase(),useCache=!xml&&!ofType,diff=!1;if(parent){
// :(first|last|only)-(child|of-type)
if(simple){while(dir){node=elem;while(node=node[dir])if(ofType?nodeName(node,name):1===node.nodeType)return!1;
// Reverse direction for :only-* (if we haven't yet done so)
start=dir="only"===type&&!start&&"nextSibling"}return!0}
// non-xml :nth-child(...) stores cache data on `parent`
if(start=[forward?parent.firstChild:parent.lastChild],forward&&useCache){
// Seek `elem` from a previously-cached index
outerCache=parent[expando]||(parent[expando]={}),cache=outerCache[type]||[],nodeIndex=cache[0]===dirruns&&cache[1],diff=nodeIndex&&cache[2],node=nodeIndex&&parent.childNodes[nodeIndex];while(node=++nodeIndex&&node&&node[dir]||(
// Fallback to seeking `elem` from the start
diff=nodeIndex=0)||start.pop())
// When found, cache indexes on `parent` and break
if(1===node.nodeType&&++diff&&node===elem){outerCache[type]=[dirruns,nodeIndex,diff];break}}else
// xml :nth-child(...)
// or :nth-last-child(...) or :nth(-last)?-of-type(...)
if(
// Use previously-cached element index if available
useCache&&(outerCache=elem[expando]||(elem[expando]={}),cache=outerCache[type]||[],nodeIndex=cache[0]===dirruns&&cache[1],diff=nodeIndex),!1===diff)
// Use the same loop as above to seek `elem` from the start
while(node=++nodeIndex&&node&&node[dir]||(diff=nodeIndex=0)||start.pop())if((ofType?nodeName(node,name):1===node.nodeType)&&++diff&&(
// Cache the index of each encountered element
useCache&&(outerCache=node[expando]||(node[expando]={}),outerCache[type]=[dirruns,diff]),node===elem))break;
// Incorporate the offset, then check against cycle size
return diff-=last,diff===first||diff%first===0&&diff/first>=0}}},PSEUDO:function(pseudo,argument){
// pseudo-class names are case-insensitive
// https://www.w3.org/TR/selectors/#pseudo-classes
// Prioritize by case sensitivity in case custom pseudos are added with uppercase letters
// Remember that setFilters inherits from pseudos
var args,fn=Expr.pseudos[pseudo]||Expr.setFilters[pseudo.toLowerCase()]||find.error("unsupported pseudo: "+pseudo);
// The user may use createPseudo to indicate that
// arguments are needed to create the filter function
// just as jQuery does
return fn[expando]?fn(argument):
// But maintain support for old signatures
fn.length>1?(args=[pseudo,pseudo,"",argument],Expr.setFilters.hasOwnProperty(pseudo.toLowerCase())?markFunction((function(seed,matches){var idx,matched=fn(seed,argument),i=matched.length;while(i--)idx=indexOf.call(seed,matched[i]),seed[idx]=!(matches[idx]=matched[i])})):function(elem){return fn(elem,0,args)}):fn}},pseudos:{
// Potentially complex pseudos
not:markFunction((function(selector){
// Trim the selector passed to compile
// to avoid treating leading and trailing
// spaces as combinators
var input=[],results=[],matcher=compile(selector.replace(rtrimCSS,"$1"));return matcher[expando]?markFunction((function(seed,matches,_context,xml){var elem,unmatched=matcher(seed,null,xml,[]),i=seed.length;
// Match elements unmatched by `matcher`
while(i--)(elem=unmatched[i])&&(seed[i]=!(matches[i]=elem))})):function(elem,_context,xml){return input[0]=elem,matcher(input,null,xml,results),
// Don't keep the element
// (see https://github.com/jquery/sizzle/issues/299)
input[0]=null,!results.pop()}})),has:markFunction((function(selector){return function(elem){return find(selector,elem).length>0}})),contains:markFunction((function(text){return text=text.replace(runescape,funescape),function(elem){return(elem.textContent||jQuery.text(elem)).indexOf(text)>-1}})),
// "Whether an element is represented by a :lang() selector
// is based solely on the element's language value
// being equal to the identifier C,
// or beginning with the identifier C immediately followed by "-".
// The matching of C against the element's language value is performed case-insensitively.
// The identifier C does not have to be a valid language name."
// https://www.w3.org/TR/selectors/#lang-pseudo
lang:markFunction((function(lang){
// lang value must be a valid identifier
return ridentifier.test(lang||"")||find.error("unsupported lang: "+lang),lang=lang.replace(runescape,funescape).toLowerCase(),function(elem){var elemLang;do{if(elemLang=documentIsHTML?elem.lang:elem.getAttribute("xml:lang")||elem.getAttribute("lang"))return elemLang=elemLang.toLowerCase(),elemLang===lang||0===elemLang.indexOf(lang+"-")}while((elem=elem.parentNode)&&1===elem.nodeType);return!1}})),
// Miscellaneous
target:function(elem){var hash=window.location&&window.location.hash;return hash&&hash.slice(1)===elem.id},root:function(elem){return elem===documentElement},focus:function(elem){return elem===safeActiveElement()&&document.hasFocus()&&!!(elem.type||elem.href||~elem.tabIndex)},
// Boolean properties
enabled:createDisabledPseudo(!1),disabled:createDisabledPseudo(!0),checked:function(elem){
// In CSS3, :checked should return both checked and selected elements
// https://www.w3.org/TR/2011/REC-css3-selectors-20110929/#checked
return nodeName(elem,"input")&&!!elem.checked||nodeName(elem,"option")&&!!elem.selected},selected:function(elem){
// Support: IE <=11+
// Accessing the selectedIndex property
// forces the browser to treat the default option as
// selected when in an optgroup.
return elem.parentNode&&
// eslint-disable-next-line no-unused-expressions
elem.parentNode.selectedIndex,!0===elem.selected},
// Contents
empty:function(elem){
// https://www.w3.org/TR/selectors/#empty-pseudo
// :empty is negated by element (1) or content nodes (text: 3; cdata: 4; entity ref: 5),
//   but not by others (comment: 8; processing instruction: 7; etc.)
// nodeType < 6 works because attributes (2) do not appear as children
for(elem=elem.firstChild;elem;elem=elem.nextSibling)if(elem.nodeType<6)return!1;return!0},parent:function(elem){return!Expr.pseudos.empty(elem)},
// Element/input types
header:function(elem){return rheader.test(elem.nodeName)},input:function(elem){return rinputs.test(elem.nodeName)},button:function(elem){return nodeName(elem,"input")&&"button"===elem.type||nodeName(elem,"button")},text:function(elem){var attr;return nodeName(elem,"input")&&"text"===elem.type&&(
// Support: IE <10 only
// New HTML5 attribute values (e.g., "search") appear
// with elem.type === "text"
null==(attr=elem.getAttribute("type"))||"text"===attr.toLowerCase())},
// Position-in-collection
first:createPositionalPseudo((function(){return[0]})),last:createPositionalPseudo((function(_matchIndexes,length){return[length-1]})),eq:createPositionalPseudo((function(_matchIndexes,length,argument){return[argument<0?argument+length:argument]})),even:createPositionalPseudo((function(matchIndexes,length){for(var i=0;i<length;i+=2)matchIndexes.push(i);return matchIndexes})),odd:createPositionalPseudo((function(matchIndexes,length){for(var i=1;i<length;i+=2)matchIndexes.push(i);return matchIndexes})),lt:createPositionalPseudo((function(matchIndexes,length,argument){var i;for(i=argument<0?argument+length:argument>length?length:argument;--i>=0;)matchIndexes.push(i);return matchIndexes})),gt:createPositionalPseudo((function(matchIndexes,length,argument){for(var i=argument<0?argument+length:argument;++i<length;)matchIndexes.push(i);return matchIndexes}))}},Expr.pseudos.nth=Expr.pseudos.eq,{radio:!0,checkbox:!0,file:!0,password:!0,image:!0})Expr.pseudos[i]=createInputPseudo(i);for(i in{submit:!0,reset:!0})Expr.pseudos[i]=createButtonPseudo(i);
// Easy API for creating new setFilters
function setFilters(){}function tokenize(selector,parseOnly){var matched,match,tokens,type,soFar,groups,preFilters,cached=tokenCache[selector+" "];if(cached)return parseOnly?0:cached.slice(0);soFar=selector,groups=[],preFilters=Expr.preFilter;while(soFar){
// Filters
for(type in
// Comma and first run
matched&&!(match=rcomma.exec(soFar))||(match&&(
// Don't consume trailing commas as valid
soFar=soFar.slice(match[0].length)||soFar),groups.push(tokens=[])),matched=!1,
// Combinators
(match=rleadingCombinator.exec(soFar))&&(matched=match.shift(),tokens.push({value:matched,
// Cast descendant combinators to space
type:match[0].replace(rtrimCSS," ")}),soFar=soFar.slice(matched.length)),Expr.filter)!(match=matchExpr[type].exec(soFar))||preFilters[type]&&!(match=preFilters[type](match))||(matched=match.shift(),tokens.push({value:matched,type:type,matches:match}),soFar=soFar.slice(matched.length));if(!matched)break}
// Return the length of the invalid excess
// if we're just parsing
// Otherwise, throw an error or return tokens
return parseOnly?soFar.length:soFar?find.error(selector):
// Cache the tokens
tokenCache(selector,groups).slice(0)}function toSelector(tokens){for(var i=0,len=tokens.length,selector="";i<len;i++)selector+=tokens[i].value;return selector}function addCombinator(matcher,combinator,base){var dir=combinator.dir,skip=combinator.next,key=skip||dir,checkNonElements=base&&"parentNode"===key,doneName=done++;return combinator.first?
// Check against closest ancestor/preceding element
function(elem,context,xml){while(elem=elem[dir])if(1===elem.nodeType||checkNonElements)return matcher(elem,context,xml);return!1}:
// Check against all ancestor/preceding elements
function(elem,context,xml){var oldCache,outerCache,newCache=[dirruns,doneName];
// We can't set arbitrary data on XML nodes, so they don't benefit from combinator caching
if(xml){while(elem=elem[dir])if((1===elem.nodeType||checkNonElements)&&matcher(elem,context,xml))return!0}else while(elem=elem[dir])if(1===elem.nodeType||checkNonElements)if(outerCache=elem[expando]||(elem[expando]={}),skip&&nodeName(elem,skip))elem=elem[dir]||elem;else{if((oldCache=outerCache[key])&&oldCache[0]===dirruns&&oldCache[1]===doneName)
// Assign to newCache so results back-propagate to previous elements
return newCache[2]=oldCache[2];
// A match means we're done; a fail means we have to keep checking
if(
// Reuse newcache so results back-propagate to previous elements
outerCache[key]=newCache,newCache[2]=matcher(elem,context,xml))return!0}return!1}}function elementMatcher(matchers){return matchers.length>1?function(elem,context,xml){var i=matchers.length;while(i--)if(!matchers[i](elem,context,xml))return!1;return!0}:matchers[0]}function multipleContexts(selector,contexts,results){for(var i=0,len=contexts.length;i<len;i++)find(selector,contexts[i],results);return results}function condense(unmatched,map,filter,context,xml){for(var elem,newUnmatched=[],i=0,len=unmatched.length,mapped=null!=map;i<len;i++)(elem=unmatched[i])&&(filter&&!filter(elem,context,xml)||(newUnmatched.push(elem),mapped&&map.push(i)));return newUnmatched}function setMatcher(preFilter,selector,matcher,postFilter,postFinder,postSelector){return postFilter&&!postFilter[expando]&&(postFilter=setMatcher(postFilter)),postFinder&&!postFinder[expando]&&(postFinder=setMatcher(postFinder,postSelector)),markFunction((function(seed,results,context,xml){var temp,i,elem,matcherOut,preMap=[],postMap=[],preexisting=results.length,
// Get initial elements from seed or context
elems=seed||multipleContexts(selector||"*",context.nodeType?[context]:context,[]),
// Prefilter to get matcher input, preserving a map for seed-results synchronization
matcherIn=!preFilter||!seed&&selector?elems:condense(elems,preMap,preFilter,context,xml);
// Apply postFilter
if(matcher?(
// If we have a postFinder, or filtered seed, or non-seed postFilter
// or preexisting results,
matcherOut=postFinder||(seed?preFilter:preexisting||postFilter)?
// ...intermediate processing is necessary
[]:
// ...otherwise use results directly
results,
// Find primary matches
matcher(matcherIn,matcherOut,context,xml)):matcherOut=matcherIn,postFilter){temp=condense(matcherOut,postMap),postFilter(temp,[],context,xml),
// Un-match failing elements by moving them back to matcherIn
i=temp.length;while(i--)(elem=temp[i])&&(matcherOut[postMap[i]]=!(matcherIn[postMap[i]]=elem))}if(seed){if(postFinder||preFilter){if(postFinder){
// Get the final matcherOut by condensing this intermediate into postFinder contexts
temp=[],i=matcherOut.length;while(i--)(elem=matcherOut[i])&&
// Restore matcherIn since elem is not yet a final match
temp.push(matcherIn[i]=elem);postFinder(null,matcherOut=[],temp,xml)}
// Move matched elements from seed to results to keep them synchronized
i=matcherOut.length;while(i--)(elem=matcherOut[i])&&(temp=postFinder?indexOf.call(seed,elem):preMap[i])>-1&&(seed[temp]=!(results[temp]=elem))}
// Add elements to results, through postFinder if defined
}else matcherOut=condense(matcherOut===results?matcherOut.splice(preexisting,matcherOut.length):matcherOut),postFinder?postFinder(null,results,matcherOut,xml):push.apply(results,matcherOut)}))}function matcherFromTokens(tokens){for(var checkContext,matcher,j,len=tokens.length,leadingRelative=Expr.relative[tokens[0].type],implicitRelative=leadingRelative||Expr.relative[" "],i=leadingRelative?1:0,
// The foundational matcher ensures that elements are reachable from top-level context(s)
matchContext=addCombinator((function(elem){return elem===checkContext}),implicitRelative,!0),matchAnyContext=addCombinator((function(elem){return indexOf.call(checkContext,elem)>-1}),implicitRelative,!0),matchers=[function(elem,context,xml){
// Support: IE 11+, Edge 17 - 18+
// IE/Edge sometimes throw a "Permission denied" error when strict-comparing
// two documents; shallow comparisons work.
// eslint-disable-next-line eqeqeq
var ret=!leadingRelative&&(xml||context!=outermostContext)||((checkContext=context).nodeType?matchContext(elem,context,xml):matchAnyContext(elem,context,xml));
// Avoid hanging onto element
// (see https://github.com/jquery/sizzle/issues/299)
return checkContext=null,ret}];i<len;i++)if(matcher=Expr.relative[tokens[i].type])matchers=[addCombinator(elementMatcher(matchers),matcher)];else{
// Return special upon seeing a positional matcher
if(matcher=Expr.filter[tokens[i].type].apply(null,tokens[i].matches),matcher[expando]){for(
// Find the next relative operator (if any) for proper handling
j=++i;j<len;j++)if(Expr.relative[tokens[j].type])break;return setMatcher(i>1&&elementMatcher(matchers),i>1&&toSelector(
// If the preceding token was a descendant combinator, insert an implicit any-element `*`
tokens.slice(0,i-1).concat({value:" "===tokens[i-2].type?"*":""})).replace(rtrimCSS,"$1"),matcher,i<j&&matcherFromTokens(tokens.slice(i,j)),j<len&&matcherFromTokens(tokens=tokens.slice(j)),j<len&&toSelector(tokens))}matchers.push(matcher)}return elementMatcher(matchers)}function matcherFromGroupMatchers(elementMatchers,setMatchers){var bySet=setMatchers.length>0,byElement=elementMatchers.length>0,superMatcher=function(seed,context,xml,results,outermost){var elem,j,matcher,matchedCount=0,i="0",unmatched=seed&&[],setMatched=[],contextBackup=outermostContext,
// We must always have either seed elements or outermost context
elems=seed||byElement&&Expr.find.TAG("*",outermost),
// Use integer dirruns iff this is the outermost matcher
dirrunsUnique=dirruns+=null==contextBackup?1:Math.random()||.1,len=elems.length;
// Add elements passing elementMatchers directly to results
// Support: iOS <=7 - 9 only
// Tolerate NodeList properties (IE: "length"; Safari: <number>) matching
// elements by id. (see trac-14142)
for(outermost&&(
// Support: IE 11+, Edge 17 - 18+
// IE/Edge sometimes throw a "Permission denied" error when strict-comparing
// two documents; shallow comparisons work.
// eslint-disable-next-line eqeqeq
outermostContext=context==document||context||outermost);i!==len&&null!=(elem=elems[i]);i++){if(byElement&&elem){j=0,
// Support: IE 11+, Edge 17 - 18+
// IE/Edge sometimes throw a "Permission denied" error when strict-comparing
// two documents; shallow comparisons work.
// eslint-disable-next-line eqeqeq
context||elem.ownerDocument==document||(setDocument(elem),xml=!documentIsHTML);while(matcher=elementMatchers[j++])if(matcher(elem,context||document,xml)){push.call(results,elem);break}outermost&&(dirruns=dirrunsUnique)}
// Track unmatched elements for set filters
bySet&&(
// They will have gone through all possible matchers
(elem=!matcher&&elem)&&matchedCount--,
// Lengthen the array for every element, matched or not
seed&&unmatched.push(elem))}
// `i` is now the count of elements visited above, and adding it to `matchedCount`
// makes the latter nonnegative.
// Apply set filters to unmatched elements
// NOTE: This can be skipped if there are no unmatched elements (i.e., `matchedCount`
// equals `i`), unless we didn't visit _any_ elements in the above loop because we have
// no element matchers and no seed.
// Incrementing an initially-string "0" `i` allows `i` to remain a string only in that
// case, which will result in a "00" `matchedCount` that differs from `i` but is also
// numerically zero.
if(matchedCount+=i,bySet&&i!==matchedCount){j=0;while(matcher=setMatchers[j++])matcher(unmatched,setMatched,context,xml);if(seed){
// Reintegrate element matches to eliminate the need for sorting
if(matchedCount>0)while(i--)unmatched[i]||setMatched[i]||(setMatched[i]=pop.call(results));
// Discard index placeholder values to get only actual matches
setMatched=condense(setMatched)}
// Add matches to results
push.apply(results,setMatched),
// Seedless set matches succeeding multiple successful matchers stipulate sorting
outermost&&!seed&&setMatched.length>0&&matchedCount+setMatchers.length>1&&jQuery.uniqueSort(results)}
// Override manipulation of globals by nested matchers
return outermost&&(dirruns=dirrunsUnique,outermostContext=contextBackup),unmatched};return bySet?markFunction(superMatcher):superMatcher}function compile(selector,match/* Internal Use Only */){var i,setMatchers=[],elementMatchers=[],cached=compilerCache[selector+" "];if(!cached){
// Generate a function of recursive functions that can be used to check each element
match||(match=tokenize(selector)),i=match.length;while(i--)cached=matcherFromTokens(match[i]),cached[expando]?setMatchers.push(cached):elementMatchers.push(cached);
// Cache the compiled function
cached=compilerCache(selector,matcherFromGroupMatchers(elementMatchers,setMatchers)),
// Save selector and tokenization
cached.selector=selector}return cached}
/**
 * A low-level selection function that works with jQuery's compiled
 *  selector functions
 * @param {String|Function} selector A selector or a pre-compiled
 *  selector function built with jQuery selector compile
 * @param {Element} context
 * @param {Array} [results]
 * @param {Array} [seed] A set of elements to match against
 */function select(selector,context,results,seed){var i,tokens,token,type,find,compiled="function"===typeof selector&&selector,match=!seed&&tokenize(selector=compiled.selector||selector);
// Try to minimize operations if there is only one selector in the list and no seed
// (the latter of which guarantees us context)
if(results=results||[],1===match.length){if(
// Reduce context if the leading compound selector is an ID
tokens=match[0]=match[0].slice(0),tokens.length>2&&"ID"===(token=tokens[0]).type&&9===context.nodeType&&documentIsHTML&&Expr.relative[tokens[1].type]){if(context=(Expr.find.ID(token.matches[0].replace(runescape,funescape),context)||[])[0],!context)return results;
// Precompiled matchers will still verify ancestry, so step up a level
compiled&&(context=context.parentNode),selector=selector.slice(tokens.shift().value.length)}
// Fetch a seed set for right-to-left matching
i=matchExpr.needsContext.test(selector)?0:tokens.length;while(i--){
// Abort if we hit a combinator
if(token=tokens[i],Expr.relative[type=token.type])break;if((find=Expr.find[type])&&(seed=find(token.matches[0].replace(runescape,funescape),rsibling.test(tokens[0].type)&&testContext(context.parentNode)||context))){if(
// If seed is empty or no tokens remain, we can return early
tokens.splice(i,1),selector=seed.length&&toSelector(tokens),!selector)return push.apply(results,seed),results;break}}}
// Compile and execute a filtering function if one is not provided
// Provide `match` to avoid retokenization if we modified the selector above
return(compiled||compile(selector,match))(seed,context,!documentIsHTML,results,!context||rsibling.test(selector)&&testContext(context.parentNode)||context),results}
// One-time assignments
// Support: Android <=4.0 - 4.1+
// Sort stability
setFilters.prototype=Expr.filters=Expr.pseudos,Expr.setFilters=new setFilters,support.sortStable=expando.split("").sort(sortOrder).join("")===expando,
// Initialize against the default document
setDocument(),
// Support: Android <=4.0 - 4.1+
// Detached nodes confoundingly follow *each other*
support.sortDetached=assert((function(el){
// Should return 1, but returns 4 (following)
return 1&el.compareDocumentPosition(document.createElement("fieldset"))})),jQuery.find=find,
// Deprecated
jQuery.expr[":"]=jQuery.expr.pseudos,jQuery.unique=jQuery.uniqueSort,
// These have always been private, but they used to be documented as part of
// Sizzle so let's maintain them for now for backwards compatibility purposes.
find.compile=compile,find.select=select,find.setDocument=setDocument,find.tokenize=tokenize,find.escape=jQuery.escapeSelector,find.getText=jQuery.text,find.isXML=jQuery.isXMLDoc,find.selectors=jQuery.expr,find.support=jQuery.support,find.uniqueSort=jQuery.uniqueSort})();var dir=function(elem,dir,until){var matched=[],truncate=void 0!==until;while((elem=elem[dir])&&9!==elem.nodeType)if(1===elem.nodeType){if(truncate&&jQuery(elem).is(until))break;matched.push(elem)}return matched},siblings=function(n,elem){for(var matched=[];n;n=n.nextSibling)1===n.nodeType&&n!==elem&&matched.push(n);return matched},rneedsContext=jQuery.expr.match.needsContext,rsingleTag=/^<([a-z][^\/\0>:\x20\t\r\n\f]*)[\x20\t\r\n\f]*\/?>(?:<\/\1>|)$/i;
// Implement the identical functionality for filter and not
function winnow(elements,qualifier,not){return isFunction(qualifier)?jQuery.grep(elements,(function(elem,i){return!!qualifier.call(elem,i,elem)!==not})):
// Single element
qualifier.nodeType?jQuery.grep(elements,(function(elem){return elem===qualifier!==not})):
// Arraylike of elements (jQuery, arguments, Array)
"string"!==typeof qualifier?jQuery.grep(elements,(function(elem){return indexOf.call(qualifier,elem)>-1!==not})):jQuery.filter(qualifier,elements,not)}jQuery.filter=function(expr,elems,not){var elem=elems[0];return not&&(expr=":not("+expr+")"),1===elems.length&&1===elem.nodeType?jQuery.find.matchesSelector(elem,expr)?[elem]:[]:jQuery.find.matches(expr,jQuery.grep(elems,(function(elem){return 1===elem.nodeType})))},jQuery.fn.extend({find:function(selector){var i,ret,len=this.length,self=this;if("string"!==typeof selector)return this.pushStack(jQuery(selector).filter((function(){for(i=0;i<len;i++)if(jQuery.contains(self[i],this))return!0})));for(ret=this.pushStack([]),i=0;i<len;i++)jQuery.find(selector,self[i],ret);return len>1?jQuery.uniqueSort(ret):ret},filter:function(selector){return this.pushStack(winnow(this,selector||[],!1))},not:function(selector){return this.pushStack(winnow(this,selector||[],!0))},is:function(selector){return!!winnow(this,
// If this is a positional/relative selector, check membership in the returned set
// so $("p:first").is("p:last") won't return true for a doc with two "p".
"string"===typeof selector&&rneedsContext.test(selector)?jQuery(selector):selector||[],!1).length}});
// Initialize a jQuery object
// A central reference to the root jQuery(document)
var rootjQuery,
// A simple way to check for HTML strings
// Prioritize #id over <tag> to avoid XSS via location.hash (trac-9521)
// Strict HTML recognition (trac-11290: must start with <)
// Shortcut simple #id case for speed
rquickExpr=/^(?:\s*(<[\w\W]+>)[^>]*|#([\w-]+))$/,init=jQuery.fn.init=function(selector,context,root){var match,elem;
// HANDLE: $(""), $(null), $(undefined), $(false)
if(!selector)return this;
// Method init() accepts an alternate rootjQuery
// so migrate can support jQuery.sub (gh-2101)
// Handle HTML strings
if(root=root||rootjQuery,"string"===typeof selector){
// Match html or make sure no context is specified for #id
if(
// Assume that strings that start and end with <> are HTML and skip the regex check
match="<"===selector[0]&&">"===selector[selector.length-1]&&selector.length>=3?[null,selector,null]:rquickExpr.exec(selector),!match||!match[1]&&context)return!context||context.jquery?(context||root).find(selector):this.constructor(context).find(selector);
// HANDLE: $(DOMElement)
// HANDLE: $(html) -> $(array)
if(match[1]){
// HANDLE: $(html, props)
if(context=context instanceof jQuery?context[0]:context,
// Option to run scripts is true for back-compat
// Intentionally let the error be thrown if parseHTML is not present
jQuery.merge(this,jQuery.parseHTML(match[1],context&&context.nodeType?context.ownerDocument||context:document,!0)),rsingleTag.test(match[1])&&jQuery.isPlainObject(context))for(match in context)
// Properties of context are called as methods if possible
isFunction(this[match])?this[match](context[match]):this.attr(match,context[match]);return this;
// HANDLE: $(#id)
}
// HANDLE: $(expr, $(...))
return elem=document.getElementById(match[2]),elem&&(
// Inject the element directly into the jQuery object
this[0]=elem,this.length=1),this}return selector.nodeType?(this[0]=selector,this.length=1,this):isFunction(selector)?void 0!==root.ready?root.ready(selector):
// Execute immediately if ready is not present
selector(jQuery):jQuery.makeArray(selector,this)};
// Give the init function the jQuery prototype for later instantiation
init.prototype=jQuery.fn,
// Initialize central reference
rootjQuery=jQuery(document);var rparentsprev=/^(?:parents|prev(?:Until|All))/,
// Methods guaranteed to produce a unique set when starting from a unique set
guaranteedUnique={children:!0,contents:!0,next:!0,prev:!0};function sibling(cur,dir){while((cur=cur[dir])&&1!==cur.nodeType);return cur}jQuery.fn.extend({has:function(target){var targets=jQuery(target,this),l=targets.length;return this.filter((function(){for(var i=0;i<l;i++)if(jQuery.contains(this,targets[i]))return!0}))},closest:function(selectors,context){var cur,i=0,l=this.length,matched=[],targets="string"!==typeof selectors&&jQuery(selectors);
// Positional selectors never match, since there's no _selection_ context
if(!rneedsContext.test(selectors))for(;i<l;i++)for(cur=this[i];cur&&cur!==context;cur=cur.parentNode)
// Always skip document fragments
if(cur.nodeType<11&&(targets?targets.index(cur)>-1:
// Don't pass non-elements to jQuery#find
1===cur.nodeType&&jQuery.find.matchesSelector(cur,selectors))){matched.push(cur);break}return this.pushStack(matched.length>1?jQuery.uniqueSort(matched):matched)},
// Determine the position of an element within the set
index:function(elem){
// No argument, return index in parent
return elem?
// Index in selector
"string"===typeof elem?indexOf.call(jQuery(elem),this[0]):indexOf.call(this,
// If it receives a jQuery object, the first element is used
elem.jquery?elem[0]:elem):this[0]&&this[0].parentNode?this.first().prevAll().length:-1},add:function(selector,context){return this.pushStack(jQuery.uniqueSort(jQuery.merge(this.get(),jQuery(selector,context))))},addBack:function(selector){return this.add(null==selector?this.prevObject:this.prevObject.filter(selector))}}),jQuery.each({parent:function(elem){var parent=elem.parentNode;return parent&&11!==parent.nodeType?parent:null},parents:function(elem){return dir(elem,"parentNode")},parentsUntil:function(elem,_i,until){return dir(elem,"parentNode",until)},next:function(elem){return sibling(elem,"nextSibling")},prev:function(elem){return sibling(elem,"previousSibling")},nextAll:function(elem){return dir(elem,"nextSibling")},prevAll:function(elem){return dir(elem,"previousSibling")},nextUntil:function(elem,_i,until){return dir(elem,"nextSibling",until)},prevUntil:function(elem,_i,until){return dir(elem,"previousSibling",until)},siblings:function(elem){return siblings((elem.parentNode||{}).firstChild,elem)},children:function(elem){return siblings(elem.firstChild)},contents:function(elem){return null!=elem.contentDocument&&
// Support: IE 11+
// <object> elements with no `data` attribute has an object
// `contentDocument` with a `null` prototype.
getProto(elem.contentDocument)?elem.contentDocument:(
// Support: IE 9 - 11 only, iOS 7 only, Android Browser <=4.3 only
// Treat the template element as a regular one in browsers that
// don't support it.
nodeName(elem,"template")&&(elem=elem.content||elem),jQuery.merge([],elem.childNodes))}},(function(name,fn){jQuery.fn[name]=function(until,selector){var matched=jQuery.map(this,fn,until);return"Until"!==name.slice(-5)&&(selector=until),selector&&"string"===typeof selector&&(matched=jQuery.filter(selector,matched)),this.length>1&&(
// Remove duplicates
guaranteedUnique[name]||jQuery.uniqueSort(matched),
// Reverse order for parents* and prev-derivatives
rparentsprev.test(name)&&matched.reverse()),this.pushStack(matched)}}));var rnothtmlwhite=/[^\x20\t\r\n\f]+/g;
// Convert String-formatted options into Object-formatted ones
function createOptions(options){var object={};return jQuery.each(options.match(rnothtmlwhite)||[],(function(_,flag){object[flag]=!0})),object}
/*
 * Create a callback list using the following parameters:
 *
 *	options: an optional list of space-separated options that will change how
 *			the callback list behaves or a more traditional option object
 *
 * By default a callback list will act like an event callback list and can be
 * "fired" multiple times.
 *
 * Possible options:
 *
 *	once:			will ensure the callback list can only be fired once (like a Deferred)
 *
 *	memory:			will keep track of previous values and will call any callback added
 *					after the list has been fired right away with the latest "memorized"
 *					values (like a Deferred)
 *
 *	unique:			will ensure a callback can only be added once (no duplicate in the list)
 *
 *	stopOnFalse:	interrupt callings when a callback returns false
 *
 */function Identity(v){return v}function Thrower(ex){throw ex}function adoptValue(value,resolve,reject,noValue){var method;try{
// Check for promise aspect first to privilege synchronous behavior
value&&isFunction(method=value.promise)?method.call(value).done(resolve).fail(reject):value&&isFunction(method=value.then)?method.call(value,resolve,reject):
// Control `resolve` arguments by letting Array#slice cast boolean `noValue` to integer:
// * false: [ value ].slice( 0 ) => resolve( value )
// * true: [ value ].slice( 1 ) => resolve()
resolve.apply(void 0,[value].slice(noValue));
// For Promises/A+, convert exceptions into rejections
// Since jQuery.when doesn't unwrap thenables, we can skip the extra checks appearing in
// Deferred#then to conditionally suppress rejection.
}catch(value){
// Support: Android 4.0 only
// Strict mode functions invoked without .call/.apply get global-object context
reject.apply(void 0,[value])}}jQuery.Callbacks=function(options){
// Convert options from String-formatted to Object-formatted if needed
// (we check in cache first)
options="string"===typeof options?createOptions(options):jQuery.extend({},options);var// Flag to know if list is currently firing
firing,
// Last fire value for non-forgettable lists
memory,
// Flag to know if list was already fired
fired,
// Flag to prevent firing
locked,
// Actual callback list
list=[],
// Queue of execution data for repeatable lists
queue=[],
// Index of currently firing callback (modified by add/remove as needed)
firingIndex=-1,
// Fire callbacks
fire=function(){for(
// Enforce single-firing
locked=locked||options.once,
// Execute callbacks for all pending executions,
// respecting firingIndex overrides and runtime changes
fired=firing=!0;queue.length;firingIndex=-1){memory=queue.shift();while(++firingIndex<list.length)
// Run callback and check for early termination
!1===list[firingIndex].apply(memory[0],memory[1])&&options.stopOnFalse&&(
// Jump to end and forget the data so .add doesn't re-fire
firingIndex=list.length,memory=!1)}
// Forget the data if we're done with it
options.memory||(memory=!1),firing=!1,
// Clean up if we're done firing for good
locked&&(
// Keep an empty list if we have data for future add calls
list=memory?[]:"")},
// Actual Callbacks object
self={
// Add a callback or a collection of callbacks to the list
add:function(){return list&&(
// If we have memory from a past run, we should fire after adding
memory&&!firing&&(firingIndex=list.length-1,queue.push(memory)),function add(args){jQuery.each(args,(function(_,arg){isFunction(arg)?options.unique&&self.has(arg)||list.push(arg):arg&&arg.length&&"string"!==toType(arg)&&
// Inspect recursively
add(arg)}))}(arguments),memory&&!firing&&fire()),this},
// Remove a callback from the list
remove:function(){return jQuery.each(arguments,(function(_,arg){var index;while((index=jQuery.inArray(arg,list,index))>-1)list.splice(index,1),
// Handle firing indexes
index<=firingIndex&&firingIndex--})),this},
// Check if a given callback is in the list.
// If no argument is given, return whether or not list has callbacks attached.
has:function(fn){return fn?jQuery.inArray(fn,list)>-1:list.length>0},
// Remove all callbacks from the list
empty:function(){return list&&(list=[]),this},
// Disable .fire and .add
// Abort any current/pending executions
// Clear all callbacks and values
disable:function(){return locked=queue=[],list=memory="",this},disabled:function(){return!list},
// Disable .fire
// Also disable .add unless we have memory (since it would have no effect)
// Abort any pending executions
lock:function(){return locked=queue=[],memory||firing||(list=memory=""),this},locked:function(){return!!locked},
// Call all callbacks with the given context and arguments
fireWith:function(context,args){return locked||(args=args||[],args=[context,args.slice?args.slice():args],queue.push(args),firing||fire()),this},
// Call all the callbacks with the given arguments
fire:function(){return self.fireWith(this,arguments),this},
// To know if the callbacks have already been called at least once
fired:function(){return!!fired}};return self},jQuery.extend({Deferred:function(func){var tuples=[
// action, add listener, callbacks,
// ... .then handlers, argument index, [final state]
["notify","progress",jQuery.Callbacks("memory"),jQuery.Callbacks("memory"),2],["resolve","done",jQuery.Callbacks("once memory"),jQuery.Callbacks("once memory"),0,"resolved"],["reject","fail",jQuery.Callbacks("once memory"),jQuery.Callbacks("once memory"),1,"rejected"]],state="pending",promise={state:function(){return state},always:function(){return deferred.done(arguments).fail(arguments),this},catch:function(fn){return promise.then(null,fn)},
// Keep pipe for back-compat
pipe:function(){var fns=arguments;return jQuery.Deferred((function(newDefer){jQuery.each(tuples,(function(_i,tuple){
// Map tuples (progress, done, fail) to arguments (done, fail, progress)
var fn=isFunction(fns[tuple[4]])&&fns[tuple[4]];
// deferred.progress(function() { bind to newDefer or newDefer.notify })
// deferred.done(function() { bind to newDefer or newDefer.resolve })
// deferred.fail(function() { bind to newDefer or newDefer.reject })
deferred[tuple[1]]((function(){var returned=fn&&fn.apply(this,arguments);returned&&isFunction(returned.promise)?returned.promise().progress(newDefer.notify).done(newDefer.resolve).fail(newDefer.reject):newDefer[tuple[0]+"With"](this,fn?[returned]:arguments)}))})),fns=null})).promise()},then:function(onFulfilled,onRejected,onProgress){var maxDepth=0;function resolve(depth,deferred,handler,special){return function(){var that=this,args=arguments,mightThrow=function(){var returned,then;
// Support: Promises/A+ section 2.3.3.3.3
// https://promisesaplus.com/#point-59
// Ignore double-resolution attempts
if(!(depth<maxDepth)){
// Support: Promises/A+ section 2.3.1
// https://promisesaplus.com/#point-48
if(returned=handler.apply(that,args),returned===deferred.promise())throw new TypeError("Thenable self-resolution");
// Support: Promises/A+ sections 2.3.3.1, 3.5
// https://promisesaplus.com/#point-54
// https://promisesaplus.com/#point-75
// Retrieve `then` only once
then=returned&&(
// Support: Promises/A+ section 2.3.4
// https://promisesaplus.com/#point-64
// Only check objects and functions for thenability
"object"===typeof returned||"function"===typeof returned)&&returned.then,
// Handle a returned thenable
isFunction(then)?
// Special processors (notify) just wait for resolution
special?then.call(returned,resolve(maxDepth,deferred,Identity,special),resolve(maxDepth,deferred,Thrower,special)):(
// ...and disregard older resolution values
maxDepth++,then.call(returned,resolve(maxDepth,deferred,Identity,special),resolve(maxDepth,deferred,Thrower,special),resolve(maxDepth,deferred,Identity,deferred.notifyWith))):(
// Only substitute handlers pass on context
// and multiple values (non-spec behavior)
handler!==Identity&&(that=void 0,args=[returned]),
// Process the value(s)
// Default process is resolve
(special||deferred.resolveWith)(that,args))}},
// Only normal processors (resolve) catch and reject exceptions
process=special?mightThrow:function(){try{mightThrow()}catch(e){jQuery.Deferred.exceptionHook&&jQuery.Deferred.exceptionHook(e,process.error),
// Support: Promises/A+ section 2.3.3.3.4.1
// https://promisesaplus.com/#point-61
// Ignore post-resolution exceptions
depth+1>=maxDepth&&(
// Only substitute handlers pass on context
// and multiple values (non-spec behavior)
handler!==Thrower&&(that=void 0,args=[e]),deferred.rejectWith(that,args))}};
// Support: Promises/A+ section 2.3.3.3.1
// https://promisesaplus.com/#point-57
// Re-resolve promises immediately to dodge false rejection from
// subsequent errors
depth?process():(
// Call an optional hook to record the error, in case of exception
// since it's otherwise lost when execution goes async
jQuery.Deferred.getErrorHook?process.error=jQuery.Deferred.getErrorHook():jQuery.Deferred.getStackHook&&(process.error=jQuery.Deferred.getStackHook()),window.setTimeout(process))}}return jQuery.Deferred((function(newDefer){
// progress_handlers.add( ... )
tuples[0][3].add(resolve(0,newDefer,isFunction(onProgress)?onProgress:Identity,newDefer.notifyWith)),
// fulfilled_handlers.add( ... )
tuples[1][3].add(resolve(0,newDefer,isFunction(onFulfilled)?onFulfilled:Identity)),
// rejected_handlers.add( ... )
tuples[2][3].add(resolve(0,newDefer,isFunction(onRejected)?onRejected:Thrower))})).promise()},
// Get a promise for this deferred
// If obj is provided, the promise aspect is added to the object
promise:function(obj){return null!=obj?jQuery.extend(obj,promise):promise}},deferred={};
// Add list-specific methods
// All done!
return jQuery.each(tuples,(function(i,tuple){var list=tuple[2],stateString=tuple[5];
// promise.progress = list.add
// promise.done = list.add
// promise.fail = list.add
promise[tuple[1]]=list.add,
// Handle state
stateString&&list.add((function(){
// state = "resolved" (i.e., fulfilled)
// state = "rejected"
state=stateString}),
// rejected_callbacks.disable
// fulfilled_callbacks.disable
tuples[3-i][2].disable,
// rejected_handlers.disable
// fulfilled_handlers.disable
tuples[3-i][3].disable,
// progress_callbacks.lock
tuples[0][2].lock,
// progress_handlers.lock
tuples[0][3].lock),
// progress_handlers.fire
// fulfilled_handlers.fire
// rejected_handlers.fire
list.add(tuple[3].fire),
// deferred.notify = function() { deferred.notifyWith(...) }
// deferred.resolve = function() { deferred.resolveWith(...) }
// deferred.reject = function() { deferred.rejectWith(...) }
deferred[tuple[0]]=function(){return deferred[tuple[0]+"With"](this===deferred?void 0:this,arguments),this},
// deferred.notifyWith = list.fireWith
// deferred.resolveWith = list.fireWith
// deferred.rejectWith = list.fireWith
deferred[tuple[0]+"With"]=list.fireWith})),
// Make the deferred a promise
promise.promise(deferred),
// Call given func if any
func&&func.call(deferred,deferred),deferred},
// Deferred helper
when:function(singleValue){var
// count of uncompleted subordinates
remaining=arguments.length,
// count of unprocessed arguments
i=remaining,
// subordinate fulfillment data
resolveContexts=Array(i),resolveValues=slice.call(arguments),
// the primary Deferred
primary=jQuery.Deferred(),
// subordinate callback factory
updateFunc=function(i){return function(value){resolveContexts[i]=this,resolveValues[i]=arguments.length>1?slice.call(arguments):value,--remaining||primary.resolveWith(resolveContexts,resolveValues)}};
// Single- and empty arguments are adopted like Promise.resolve
if(remaining<=1&&(adoptValue(singleValue,primary.done(updateFunc(i)).resolve,primary.reject,!remaining),"pending"===primary.state()||isFunction(resolveValues[i]&&resolveValues[i].then)))return primary.then();
// Multiple arguments are aggregated like Promise.all array elements
while(i--)adoptValue(resolveValues[i],updateFunc(i),primary.reject);return primary.promise()}});
// These usually indicate a programmer mistake during development,
// warn about them ASAP rather than swallowing them by default.
var rerrorNames=/^(Eval|Internal|Range|Reference|Syntax|Type|URI)Error$/;
// If `jQuery.Deferred.getErrorHook` is defined, `asyncError` is an error
// captured before the async barrier to get the original error cause
// which may otherwise be hidden.
jQuery.Deferred.exceptionHook=function(error,asyncError){
// Support: IE 8 - 9 only
// Console exists when dev tools are open, which can happen at any time
window.console&&window.console.warn&&error&&rerrorNames.test(error.name)&&window.console.warn("jQuery.Deferred exception: "+error.message,error.stack,asyncError)},jQuery.readyException=function(error){window.setTimeout((function(){throw error}))};
// The deferred used on DOM ready
var readyList=jQuery.Deferred();
// The ready event handler and self cleanup method
function completed(){document.removeEventListener("DOMContentLoaded",completed),window.removeEventListener("load",completed),jQuery.ready()}
// Catch cases where $(document).ready() is called
// after the browser event has already occurred.
// Support: IE <=9 - 10 only
// Older IE sometimes signals "interactive" too soon
jQuery.fn.ready=function(fn){return readyList.then(fn).catch((function(error){jQuery.readyException(error)})),this},jQuery.extend({
// Is the DOM ready to be used? Set to true once it occurs.
isReady:!1,
// A counter to track how many items to wait for before
// the ready event fires. See trac-6781
readyWait:1,
// Handle when the DOM is ready
ready:function(wait){
// Abort if there are pending holds or we're already ready
(!0===wait?--jQuery.readyWait:jQuery.isReady)||(
// Remember that the DOM is ready
jQuery.isReady=!0,
// If a normal DOM Ready event fired, decrement, and wait if need be
!0!==wait&&--jQuery.readyWait>0||
// If there are functions bound, to execute
readyList.resolveWith(document,[jQuery]))}}),jQuery.ready.then=readyList.then,"complete"===document.readyState||"loading"!==document.readyState&&!document.documentElement.doScroll?
// Handle it asynchronously to allow scripts the opportunity to delay ready
window.setTimeout(jQuery.ready):(
// Use the handy event callback
document.addEventListener("DOMContentLoaded",completed),
// A fallback to window.onload, that will always work
window.addEventListener("load",completed));
// Multifunctional method to get and set values of a collection
// The value/s can optionally be executed if it's a function
var access=function(elems,fn,key,value,chainable,emptyGet,raw){var i=0,len=elems.length,bulk=null==key;
// Sets many values
if("object"===toType(key))for(i in chainable=!0,key)access(elems,fn,i,key[i],!0,emptyGet,raw);
// Sets one value
else if(void 0!==value&&(chainable=!0,isFunction(value)||(raw=!0),bulk&&(
// Bulk operations run against the entire set
raw?(fn.call(elems,value),fn=null):(bulk=fn,fn=function(elem,_key,value){return bulk.call(jQuery(elem),value)})),fn))for(;i<len;i++)fn(elems[i],key,raw?value:value.call(elems[i],i,fn(elems[i],key)));return chainable?elems:
// Gets
bulk?fn.call(elems):len?fn(elems[0],key):emptyGet},rmsPrefix=/^-ms-/,rdashAlpha=/-([a-z])/g;
// Matches dashed string for camelizing
// Used by camelCase as callback to replace()
function fcamelCase(_all,letter){return letter.toUpperCase()}
// Convert dashed to camelCase; used by the css and data modules
// Support: IE <=9 - 11, Edge 12 - 15
// Microsoft forgot to hump their vendor prefix (trac-9572)
function camelCase(string){return string.replace(rmsPrefix,"ms-").replace(rdashAlpha,fcamelCase)}var acceptData=function(owner){
// Accepts only:
//  - Node
//    - Node.ELEMENT_NODE
//    - Node.DOCUMENT_NODE
//  - Object
//    - Any
return 1===owner.nodeType||9===owner.nodeType||!+owner.nodeType};function Data(){this.expando=jQuery.expando+Data.uid++}Data.uid=1,Data.prototype={cache:function(owner){
// Check if the owner object already has a cache
var value=owner[this.expando];
// If not, create one
return value||(value={},
// We can accept data for non-element nodes in modern browsers,
// but we should not, see trac-8335.
// Always return an empty object.
acceptData(owner)&&(
// If it is a node unlikely to be stringify-ed or looped over
// use plain assignment
owner.nodeType?owner[this.expando]=value:Object.defineProperty(owner,this.expando,{value:value,configurable:!0}))),value},set:function(owner,data,value){var prop,cache=this.cache(owner);
// Handle: [ owner, key, value ] args
// Always use camelCase key (gh-2257)
if("string"===typeof data)cache[camelCase(data)]=value;
// Handle: [ owner, { properties } ] args
else
// Copy the properties one-by-one to the cache object
for(prop in data)cache[camelCase(prop)]=data[prop];return cache},get:function(owner,key){return void 0===key?this.cache(owner):
// Always use camelCase key (gh-2257)
owner[this.expando]&&owner[this.expando][camelCase(key)]},access:function(owner,key,value){
// In cases where either:
//   1. No key was specified
//   2. A string key was specified, but no value provided
// Take the "read" path and allow the get method to determine
// which value to return, respectively either:
//   1. The entire cache object
//   2. The data stored at the key
return void 0===key||key&&"string"===typeof key&&void 0===value?this.get(owner,key):(
// When the key is not a string, or both a key and value
// are specified, set or extend (existing objects) with either:
//   1. An object of properties
//   2. A key and value
this.set(owner,key,value),void 0!==value?value:key)},remove:function(owner,key){var i,cache=owner[this.expando];if(void 0!==cache){if(void 0!==key){
// Support array or space separated string of keys
Array.isArray(key)?
// If key is an array of keys...
// We always set camelCase keys, so remove that.
key=key.map(camelCase):(key=camelCase(key),
// If a key with the spaces exists, use it.
// Otherwise, create an array by matching non-whitespace
key=key in cache?[key]:key.match(rnothtmlwhite)||[]),i=key.length;while(i--)delete cache[key[i]]}
// Remove the expando if there's no more data
(void 0===key||jQuery.isEmptyObject(cache))&&(
// Support: Chrome <=35 - 45
// Webkit & Blink performance suffers when deleting properties
// from DOM nodes, so set to undefined instead
// https://bugs.chromium.org/p/chromium/issues/detail?id=378607 (bug restricted)
owner.nodeType?owner[this.expando]=void 0:delete owner[this.expando])}},hasData:function(owner){var cache=owner[this.expando];return void 0!==cache&&!jQuery.isEmptyObject(cache)}};var dataPriv=new Data,dataUser=new Data,rbrace=/^(?:\{[\w\W]*\}|\[[\w\W]*\])$/,rmultiDash=/[A-Z]/g;function getData(data){return"true"===data||"false"!==data&&("null"===data?null:
// Only convert to a number if it doesn't change the string
data===+data+""?+data:rbrace.test(data)?JSON.parse(data):data)}function dataAttr(elem,key,data){var name;
// If nothing was found internally, try to fetch any
// data from the HTML5 data-* attribute
if(void 0===data&&1===elem.nodeType)if(name="data-"+key.replace(rmultiDash,"-$&").toLowerCase(),data=elem.getAttribute(name),"string"===typeof data){try{data=getData(data)}catch(e){}
// Make sure we set the data so it isn't changed later
dataUser.set(elem,key,data)}else data=void 0;return data}jQuery.extend({hasData:function(elem){return dataUser.hasData(elem)||dataPriv.hasData(elem)},data:function(elem,name,data){return dataUser.access(elem,name,data)},removeData:function(elem,name){dataUser.remove(elem,name)},
// TODO: Now that all calls to _data and _removeData have been replaced
// with direct calls to dataPriv methods, these can be deprecated.
_data:function(elem,name,data){return dataPriv.access(elem,name,data)},_removeData:function(elem,name){dataPriv.remove(elem,name)}}),jQuery.fn.extend({data:function(key,value){var i,name,data,elem=this[0],attrs=elem&&elem.attributes;
// Gets all values
if(void 0===key){if(this.length&&(data=dataUser.get(elem),1===elem.nodeType&&!dataPriv.get(elem,"hasDataAttrs"))){i=attrs.length;while(i--)
// Support: IE 11 only
// The attrs elements can be null (trac-14894)
attrs[i]&&(name=attrs[i].name,0===name.indexOf("data-")&&(name=camelCase(name.slice(5)),dataAttr(elem,name,data[name])));dataPriv.set(elem,"hasDataAttrs",!0)}return data}
// Sets multiple values
return"object"===typeof key?this.each((function(){dataUser.set(this,key)})):access(this,(function(value){var data;
// The calling jQuery object (element matches) is not empty
// (and therefore has an element appears at this[ 0 ]) and the
// `value` parameter was not undefined. An empty jQuery object
// will result in `undefined` for elem = this[ 0 ] which will
// throw an exception if an attempt to read a data cache is made.
if(elem&&void 0===value)
// Attempt to get data from the cache
// The key will always be camelCased in Data
return data=dataUser.get(elem,key),void 0!==data?data:(
// Attempt to "discover" the data in
// HTML5 custom data-* attrs
data=dataAttr(elem,key),void 0!==data?data:
// We tried really hard, but the data doesn't exist.
void 0);
// Set the data...
this.each((function(){
// We always store the camelCased key
dataUser.set(this,key,value)}))}),null,value,arguments.length>1,null,!0)},removeData:function(key){return this.each((function(){dataUser.remove(this,key)}))}}),jQuery.extend({queue:function(elem,type,data){var queue;if(elem)return type=(type||"fx")+"queue",queue=dataPriv.get(elem,type),
// Speed up dequeue by getting out quickly if this is just a lookup
data&&(!queue||Array.isArray(data)?queue=dataPriv.access(elem,type,jQuery.makeArray(data)):queue.push(data)),queue||[]},dequeue:function(elem,type){type=type||"fx";var queue=jQuery.queue(elem,type),startLength=queue.length,fn=queue.shift(),hooks=jQuery._queueHooks(elem,type),next=function(){jQuery.dequeue(elem,type)};
// If the fx queue is dequeued, always remove the progress sentinel
"inprogress"===fn&&(fn=queue.shift(),startLength--),fn&&(
// Add a progress sentinel to prevent the fx queue from being
// automatically dequeued
"fx"===type&&queue.unshift("inprogress"),
// Clear up the last queue stop function
delete hooks.stop,fn.call(elem,next,hooks)),!startLength&&hooks&&hooks.empty.fire()},
// Not public - generate a queueHooks object, or return the current one
_queueHooks:function(elem,type){var key=type+"queueHooks";return dataPriv.get(elem,key)||dataPriv.access(elem,key,{empty:jQuery.Callbacks("once memory").add((function(){dataPriv.remove(elem,[type+"queue",key])}))})}}),jQuery.fn.extend({queue:function(type,data){var setter=2;return"string"!==typeof type&&(data=type,type="fx",setter--),arguments.length<setter?jQuery.queue(this[0],type):void 0===data?this:this.each((function(){var queue=jQuery.queue(this,type,data);
// Ensure a hooks for this queue
jQuery._queueHooks(this,type),"fx"===type&&"inprogress"!==queue[0]&&jQuery.dequeue(this,type)}))},dequeue:function(type){return this.each((function(){jQuery.dequeue(this,type)}))},clearQueue:function(type){return this.queue(type||"fx",[])},
// Get a promise resolved when queues of a certain type
// are emptied (fx is the type by default)
promise:function(type,obj){var tmp,count=1,defer=jQuery.Deferred(),elements=this,i=this.length,resolve=function(){--count||defer.resolveWith(elements,[elements])};"string"!==typeof type&&(obj=type,type=void 0),type=type||"fx";while(i--)tmp=dataPriv.get(elements[i],type+"queueHooks"),tmp&&tmp.empty&&(count++,tmp.empty.add(resolve));return resolve(),defer.promise(obj)}});var pnum=/[+-]?(?:\d*\.|)\d+(?:[eE][+-]?\d+|)/.source,rcssNum=new RegExp("^(?:([+-])=|)("+pnum+")([a-z%]*)$","i"),cssExpand=["Top","Right","Bottom","Left"],documentElement=document.documentElement,isAttached=function(elem){return jQuery.contains(elem.ownerDocument,elem)},composed={composed:!0};
// Support: IE 9 - 11+, Edge 12 - 18+, iOS 10.0 - 10.2 only
// Check attachment across shadow DOM boundaries when possible (gh-3504)
// Support: iOS 10.0-10.2 only
// Early iOS 10 versions support `attachShadow` but not `getRootNode`,
// leading to errors. We need to check for `getRootNode`.
documentElement.getRootNode&&(isAttached=function(elem){return jQuery.contains(elem.ownerDocument,elem)||elem.getRootNode(composed)===elem.ownerDocument});var isHiddenWithinTree=function(elem,el){
// Inline style trumps all
// isHiddenWithinTree might be called from jQuery#filter function;
// in that case, element will be second argument
return elem=el||elem,"none"===elem.style.display||""===elem.style.display&&
// Otherwise, check computed style
// Support: Firefox <=43 - 45
// Disconnected elements can have computed display: none, so first confirm that elem is
// in the document.
isAttached(elem)&&"none"===jQuery.css(elem,"display")};function adjustCSS(elem,prop,valueParts,tween){var adjusted,scale,maxIterations=20,currentValue=tween?function(){return tween.cur()}:function(){return jQuery.css(elem,prop,"")},initial=currentValue(),unit=valueParts&&valueParts[3]||(jQuery.cssNumber[prop]?"":"px"),
// Starting value computation is required for potential unit mismatches
initialInUnit=elem.nodeType&&(jQuery.cssNumber[prop]||"px"!==unit&&+initial)&&rcssNum.exec(jQuery.css(elem,prop));if(initialInUnit&&initialInUnit[3]!==unit){
// Support: Firefox <=54
// Halve the iteration target value to prevent interference from CSS upper bounds (gh-2144)
initial/=2,
// Trust units reported by jQuery.css
unit=unit||initialInUnit[3],
// Iteratively approximate from a nonzero starting point
initialInUnit=+initial||1;while(maxIterations--)
// Evaluate and update our best guess (doubling guesses that zero out).
// Finish if the scale equals or crosses 1 (making the old*new product non-positive).
jQuery.style(elem,prop,initialInUnit+unit),(1-scale)*(1-(scale=currentValue()/initial||.5))<=0&&(maxIterations=0),initialInUnit/=scale;initialInUnit*=2,jQuery.style(elem,prop,initialInUnit+unit),
// Make sure we update the tween properties later on
valueParts=valueParts||[]}return valueParts&&(initialInUnit=+initialInUnit||+initial||0,
// Apply relative offset (+=/-=) if specified
adjusted=valueParts[1]?initialInUnit+(valueParts[1]+1)*valueParts[2]:+valueParts[2],tween&&(tween.unit=unit,tween.start=initialInUnit,tween.end=adjusted)),adjusted}var defaultDisplayMap={};function getDefaultDisplay(elem){var temp,doc=elem.ownerDocument,nodeName=elem.nodeName,display=defaultDisplayMap[nodeName];return display||(temp=doc.body.appendChild(doc.createElement(nodeName)),display=jQuery.css(temp,"display"),temp.parentNode.removeChild(temp),"none"===display&&(display="block"),defaultDisplayMap[nodeName]=display,display)}function showHide(elements,show){
// Determine new display value for elements that need to change
for(var display,elem,values=[],index=0,length=elements.length;index<length;index++)elem=elements[index],elem.style&&(display=elem.style.display,show?(
// Since we force visibility upon cascade-hidden elements, an immediate (and slow)
// check is required in this first loop unless we have a nonempty display value (either
// inline or about-to-be-restored)
"none"===display&&(values[index]=dataPriv.get(elem,"display")||null,values[index]||(elem.style.display="")),""===elem.style.display&&isHiddenWithinTree(elem)&&(values[index]=getDefaultDisplay(elem))):"none"!==display&&(values[index]="none",
// Remember what we're overwriting
dataPriv.set(elem,"display",display)));
// Set the display of the elements in a second loop to avoid constant reflow
for(index=0;index<length;index++)null!=values[index]&&(elements[index].style.display=values[index]);return elements}jQuery.fn.extend({show:function(){return showHide(this,!0)},hide:function(){return showHide(this)},toggle:function(state){return"boolean"===typeof state?state?this.show():this.hide():this.each((function(){isHiddenWithinTree(this)?jQuery(this).show():jQuery(this).hide()}))}});var rcheckableType=/^(?:checkbox|radio)$/i,rtagName=/<([a-z][^\/\0>\x20\t\r\n\f]*)/i,rscriptType=/^$|^module$|\/(?:java|ecma)script/i;(function(){var fragment=document.createDocumentFragment(),div=fragment.appendChild(document.createElement("div")),input=document.createElement("input");
// Support: Android 4.0 - 4.3 only
// Check state lost if the name is set (trac-11217)
// Support: Windows Web Apps (WWA)
// `name` and `type` must use .setAttribute for WWA (trac-14901)
input.setAttribute("type","radio"),input.setAttribute("checked","checked"),input.setAttribute("name","t"),div.appendChild(input),
// Support: Android <=4.1 only
// Older WebKit doesn't clone checked state correctly in fragments
support.checkClone=div.cloneNode(!0).cloneNode(!0).lastChild.checked,
// Support: IE <=11 only
// Make sure textarea (and checkbox) defaultValue is properly cloned
div.innerHTML="<textarea>x</textarea>",support.noCloneChecked=!!div.cloneNode(!0).lastChild.defaultValue,
// Support: IE <=9 only
// IE <=9 replaces <option> tags with their contents when inserted outside of
// the select element.
div.innerHTML="<option></option>",support.option=!!div.lastChild})();
// We have to close these tags to support XHTML (trac-13200)
var wrapMap={
// XHTML parsers do not magically insert elements in the
// same way that tag soup parsers do. So we cannot shorten
// this by omitting <tbody> or other required elements.
thead:[1,"<table>","</table>"],col:[2,"<table><colgroup>","</colgroup></table>"],tr:[2,"<table><tbody>","</tbody></table>"],td:[3,"<table><tbody><tr>","</tr></tbody></table>"],_default:[0,"",""]};function getAll(context,tag){
// Support: IE <=9 - 11 only
// Use typeof to avoid zero-argument method invocation on host objects (trac-15151)
var ret;return ret="undefined"!==typeof context.getElementsByTagName?context.getElementsByTagName(tag||"*"):"undefined"!==typeof context.querySelectorAll?context.querySelectorAll(tag||"*"):[],void 0===tag||tag&&nodeName(context,tag)?jQuery.merge([context],ret):ret}
// Mark scripts as having already been evaluated
function setGlobalEval(elems,refElements){for(var i=0,l=elems.length;i<l;i++)dataPriv.set(elems[i],"globalEval",!refElements||dataPriv.get(refElements[i],"globalEval"))}wrapMap.tbody=wrapMap.tfoot=wrapMap.colgroup=wrapMap.caption=wrapMap.thead,wrapMap.th=wrapMap.td,
// Support: IE <=9 only
support.option||(wrapMap.optgroup=wrapMap.option=[1,"<select multiple='multiple'>","</select>"]);var rhtml=/<|&#?\w+;/;function buildFragment(elems,context,scripts,selection,ignored){for(var elem,tmp,tag,wrap,attached,j,fragment=context.createDocumentFragment(),nodes=[],i=0,l=elems.length;i<l;i++)if(elem=elems[i],elem||0===elem)
// Add nodes directly
if("object"===toType(elem))
// Support: Android <=4.0 only, PhantomJS 1 only
// push.apply(_, arraylike) throws on ancient WebKit
jQuery.merge(nodes,elem.nodeType?[elem]:elem);
// Convert non-html into a text node
else if(rhtml.test(elem)){tmp=tmp||fragment.appendChild(context.createElement("div")),
// Deserialize a standard representation
tag=(rtagName.exec(elem)||["",""])[1].toLowerCase(),wrap=wrapMap[tag]||wrapMap._default,tmp.innerHTML=wrap[1]+jQuery.htmlPrefilter(elem)+wrap[2],
// Descend through wrappers to the right content
j=wrap[0];while(j--)tmp=tmp.lastChild;
// Support: Android <=4.0 only, PhantomJS 1 only
// push.apply(_, arraylike) throws on ancient WebKit
jQuery.merge(nodes,tmp.childNodes),
// Remember the top-level container
tmp=fragment.firstChild,
// Ensure the created nodes are orphaned (trac-12392)
tmp.textContent=""}else nodes.push(context.createTextNode(elem));
// Convert html into DOM nodes
// Remove wrapper from fragment
fragment.textContent="",i=0;while(elem=nodes[i++])
// Skip elements already in the context collection (trac-4087)
if(selection&&jQuery.inArray(elem,selection)>-1)ignored&&ignored.push(elem);else
// Capture executables
if(attached=isAttached(elem),
// Append to fragment
tmp=getAll(fragment.appendChild(elem),"script"),
// Preserve script evaluation history
attached&&setGlobalEval(tmp),scripts){j=0;while(elem=tmp[j++])rscriptType.test(elem.type||"")&&scripts.push(elem)}return fragment}var rtypenamespace=/^([^.]*)(?:\.(.+)|)/;function returnTrue(){return!0}function returnFalse(){return!1}function on(elem,types,selector,data,fn,one){var origFn,type;
// Types can be a map of types/handlers
if("object"===typeof types){for(type in
// ( types-Object, selector, data )
"string"!==typeof selector&&(
// ( types-Object, data )
data=data||selector,selector=void 0),types)on(elem,type,selector,data,types[type],one);return elem}if(null==data&&null==fn?(
// ( types, fn )
fn=selector,data=selector=void 0):null==fn&&("string"===typeof selector?(
// ( types, selector, fn )
fn=data,data=void 0):(
// ( types, data, fn )
fn=data,data=selector,selector=void 0)),!1===fn)fn=returnFalse;else if(!fn)return elem;return 1===one&&(origFn=fn,fn=function(event){
// Can use an empty set, since event contains the info
return jQuery().off(event),origFn.apply(this,arguments)},
// Use same guid so caller can remove using origFn
fn.guid=origFn.guid||(origFn.guid=jQuery.guid++)),elem.each((function(){jQuery.event.add(this,types,fn,data,selector)}))}
/*
 * Helper functions for managing events -- not part of the public interface.
 * Props to Dean Edwards' addEvent library for many of the ideas.
 */
// Ensure the presence of an event listener that handles manually-triggered
// synthetic events by interrupting progress until reinvoked in response to
// *native* events that it fires directly, ensuring that state changes have
// already occurred before other listeners are invoked.
function leverageNative(el,type,isSetup){
// Missing `isSetup` indicates a trigger call, which must force setup through jQuery.event.add
isSetup?(
// Register the controller as a special universal handler for all event namespaces
dataPriv.set(el,type,!1),jQuery.event.add(el,type,{namespace:!1,handler:function(event){var result,saved=dataPriv.get(this,type);if(1&event.isTrigger&&this[type]){
// Interrupt processing of the outer synthetic .trigger()ed event
if(saved)(jQuery.event.special[type]||{}).delegateType&&event.stopPropagation();
// If this is a native event triggered above, everything is now in order
// Fire an inner synthetic event with the original arguments
else if(
// Store arguments for use when handling the inner native event
// There will always be at least one argument (an event object), so this array
// will not be confused with a leftover capture object.
saved=slice.call(arguments),dataPriv.set(this,type,saved),
// Trigger the native event and capture its result
this[type](),result=dataPriv.get(this,type),dataPriv.set(this,type,!1),saved!==result)
// Cancel the outer synthetic event
return event.stopImmediatePropagation(),event.preventDefault(),result;
// If this is an inner synthetic event for an event with a bubbling surrogate
// (focus or blur), assume that the surrogate already propagated from triggering
// the native event and prevent that from happening again here.
// This technically gets the ordering wrong w.r.t. to `.trigger()` (in which the
// bubbling surrogate propagates *after* the non-bubbling base), but that seems
// less bad than duplication.
}else saved&&(
// ...and capture the result
dataPriv.set(this,type,jQuery.event.trigger(saved[0],saved.slice(1),this)),
// Abort handling of the native event by all jQuery handlers while allowing
// native handlers on the same element to run. On target, this is achieved
// by stopping immediate propagation just on the jQuery event. However,
// the native event is re-wrapped by a jQuery one on each level of the
// propagation so the only way to stop it for jQuery is to stop it for
// everyone via native `stopPropagation()`. This is not a problem for
// focus/blur which don't bubble, but it does also stop click on checkboxes
// and radios. We accept this limitation.
event.stopPropagation(),event.isImmediatePropagationStopped=returnTrue)}})):void 0===dataPriv.get(el,type)&&jQuery.event.add(el,type,returnTrue)}jQuery.event={global:{},add:function(elem,types,handler,data,selector){var handleObjIn,eventHandle,tmp,events,t,handleObj,special,handlers,type,namespaces,origType,elemData=dataPriv.get(elem);
// Only attach events to objects that accept data
if(acceptData(elem)){
// Caller can pass in an object of custom data in lieu of the handler
handler.handler&&(handleObjIn=handler,handler=handleObjIn.handler,selector=handleObjIn.selector),
// Ensure that invalid selectors throw exceptions at attach time
// Evaluate against documentElement in case elem is a non-element node (e.g., document)
selector&&jQuery.find.matchesSelector(documentElement,selector),
// Make sure that the handler has a unique ID, used to find/remove it later
handler.guid||(handler.guid=jQuery.guid++),
// Init the element's event structure and main handler, if this is the first
(events=elemData.events)||(events=elemData.events=Object.create(null)),(eventHandle=elemData.handle)||(eventHandle=elemData.handle=function(e){
// Discard the second event of a jQuery.event.trigger() and
// when an event is called after a page has unloaded
return"undefined"!==typeof jQuery&&jQuery.event.triggered!==e.type?jQuery.event.dispatch.apply(elem,arguments):void 0}),
// Handle multiple events separated by a space
types=(types||"").match(rnothtmlwhite)||[""],t=types.length;while(t--)tmp=rtypenamespace.exec(types[t])||[],type=origType=tmp[1],namespaces=(tmp[2]||"").split(".").sort(),
// There *must* be a type, no attaching namespace-only handlers
type&&(
// If event changes its type, use the special event handlers for the changed type
special=jQuery.event.special[type]||{},
// If selector defined, determine special event api type, otherwise given type
type=(selector?special.delegateType:special.bindType)||type,
// Update special based on newly reset type
special=jQuery.event.special[type]||{},
// handleObj is passed to all event handlers
handleObj=jQuery.extend({type:type,origType:origType,data:data,handler:handler,guid:handler.guid,selector:selector,needsContext:selector&&jQuery.expr.match.needsContext.test(selector),namespace:namespaces.join(".")},handleObjIn),
// Init the event handler queue if we're the first
(handlers=events[type])||(handlers=events[type]=[],handlers.delegateCount=0,
// Only use addEventListener if the special events handler returns false
special.setup&&!1!==special.setup.call(elem,data,namespaces,eventHandle)||elem.addEventListener&&elem.addEventListener(type,eventHandle)),special.add&&(special.add.call(elem,handleObj),handleObj.handler.guid||(handleObj.handler.guid=handler.guid)),
// Add to the element's handler list, delegates in front
selector?handlers.splice(handlers.delegateCount++,0,handleObj):handlers.push(handleObj),
// Keep track of which events have ever been used, for event optimization
jQuery.event.global[type]=!0)}},
// Detach an event or set of events from an element
remove:function(elem,types,handler,selector,mappedTypes){var j,origCount,tmp,events,t,handleObj,special,handlers,type,namespaces,origType,elemData=dataPriv.hasData(elem)&&dataPriv.get(elem);if(elemData&&(events=elemData.events)){
// Once for each type.namespace in types; type may be omitted
types=(types||"").match(rnothtmlwhite)||[""],t=types.length;while(t--)
// Unbind all events (on this namespace, if provided) for the element
if(tmp=rtypenamespace.exec(types[t])||[],type=origType=tmp[1],namespaces=(tmp[2]||"").split(".").sort(),type){special=jQuery.event.special[type]||{},type=(selector?special.delegateType:special.bindType)||type,handlers=events[type]||[],tmp=tmp[2]&&new RegExp("(^|\\.)"+namespaces.join("\\.(?:.*\\.|)")+"(\\.|$)"),
// Remove matching events
origCount=j=handlers.length;while(j--)handleObj=handlers[j],!mappedTypes&&origType!==handleObj.origType||handler&&handler.guid!==handleObj.guid||tmp&&!tmp.test(handleObj.namespace)||selector&&selector!==handleObj.selector&&("**"!==selector||!handleObj.selector)||(handlers.splice(j,1),handleObj.selector&&handlers.delegateCount--,special.remove&&special.remove.call(elem,handleObj));
// Remove generic event handler if we removed something and no more handlers exist
// (avoids potential for endless recursion during removal of special event handlers)
origCount&&!handlers.length&&(special.teardown&&!1!==special.teardown.call(elem,namespaces,elemData.handle)||jQuery.removeEvent(elem,type,elemData.handle),delete events[type])}else for(type in events)jQuery.event.remove(elem,type+types[t],handler,selector,!0);
// Remove data and the expando if it's no longer used
jQuery.isEmptyObject(events)&&dataPriv.remove(elem,"handle events")}},dispatch:function(nativeEvent){var i,j,ret,matched,handleObj,handlerQueue,args=new Array(arguments.length),
// Make a writable jQuery.Event from the native event object
event=jQuery.event.fix(nativeEvent),handlers=(dataPriv.get(this,"events")||Object.create(null))[event.type]||[],special=jQuery.event.special[event.type]||{};
// Use the fix-ed jQuery.Event rather than the (read-only) native event
for(args[0]=event,i=1;i<arguments.length;i++)args[i]=arguments[i];
// Call the preDispatch hook for the mapped type, and let it bail if desired
if(event.delegateTarget=this,!special.preDispatch||!1!==special.preDispatch.call(this,event)){
// Determine handlers
handlerQueue=jQuery.event.handlers.call(this,event,handlers),
// Run delegates first; they may want to stop propagation beneath us
i=0;while((matched=handlerQueue[i++])&&!event.isPropagationStopped()){event.currentTarget=matched.elem,j=0;while((handleObj=matched.handlers[j++])&&!event.isImmediatePropagationStopped())
// If the event is namespaced, then each handler is only invoked if it is
// specially universal or its namespaces are a superset of the event's.
event.rnamespace&&!1!==handleObj.namespace&&!event.rnamespace.test(handleObj.namespace)||(event.handleObj=handleObj,event.data=handleObj.data,ret=((jQuery.event.special[handleObj.origType]||{}).handle||handleObj.handler).apply(matched.elem,args),void 0!==ret&&!1===(event.result=ret)&&(event.preventDefault(),event.stopPropagation()))}
// Call the postDispatch hook for the mapped type
return special.postDispatch&&special.postDispatch.call(this,event),event.result}},handlers:function(event,handlers){var i,handleObj,sel,matchedHandlers,matchedSelectors,handlerQueue=[],delegateCount=handlers.delegateCount,cur=event.target;
// Find delegate handlers
if(delegateCount&&
// Support: IE <=9
// Black-hole SVG <use> instance trees (trac-13180)
cur.nodeType&&
// Support: Firefox <=42
// Suppress spec-violating clicks indicating a non-primary pointer button (trac-3861)
// https://www.w3.org/TR/DOM-Level-3-Events/#event-type-click
// Support: IE 11 only
// ...but not arrow key "clicks" of radio inputs, which can have `button` -1 (gh-2343)
!("click"===event.type&&event.button>=1))for(;cur!==this;cur=cur.parentNode||this)
// Don't check non-elements (trac-13208)
// Don't process clicks on disabled elements (trac-6911, trac-8165, trac-11382, trac-11764)
if(1===cur.nodeType&&("click"!==event.type||!0!==cur.disabled)){for(matchedHandlers=[],matchedSelectors={},i=0;i<delegateCount;i++)handleObj=handlers[i],
// Don't conflict with Object.prototype properties (trac-13203)
sel=handleObj.selector+" ",void 0===matchedSelectors[sel]&&(matchedSelectors[sel]=handleObj.needsContext?jQuery(sel,this).index(cur)>-1:jQuery.find(sel,this,null,[cur]).length),matchedSelectors[sel]&&matchedHandlers.push(handleObj);matchedHandlers.length&&handlerQueue.push({elem:cur,handlers:matchedHandlers})}
// Add the remaining (directly-bound) handlers
return cur=this,delegateCount<handlers.length&&handlerQueue.push({elem:cur,handlers:handlers.slice(delegateCount)}),handlerQueue},addProp:function(name,hook){Object.defineProperty(jQuery.Event.prototype,name,{enumerable:!0,configurable:!0,get:isFunction(hook)?function(){if(this.originalEvent)return hook(this.originalEvent)}:function(){if(this.originalEvent)return this.originalEvent[name]},set:function(value){Object.defineProperty(this,name,{enumerable:!0,configurable:!0,writable:!0,value:value})}})},fix:function(originalEvent){return originalEvent[jQuery.expando]?originalEvent:new jQuery.Event(originalEvent)},special:{load:{
// Prevent triggered image.load events from bubbling to window.load
noBubble:!0},click:{
// Utilize native event to ensure correct state for checkable inputs
setup:function(data){
// For mutual compressibility with _default, replace `this` access with a local var.
// `|| data` is dead code meant only to preserve the variable through minification.
var el=this||data;
// Claim the first handler
// Return false to allow normal processing in the caller
return rcheckableType.test(el.type)&&el.click&&nodeName(el,"input")&&
// dataPriv.set( el, "click", ... )
leverageNative(el,"click",!0),!1},trigger:function(data){
// For mutual compressibility with _default, replace `this` access with a local var.
// `|| data` is dead code meant only to preserve the variable through minification.
var el=this||data;
// Force setup before triggering a click
// Return non-false to allow normal event-path propagation
return rcheckableType.test(el.type)&&el.click&&nodeName(el,"input")&&leverageNative(el,"click"),!0},
// For cross-browser consistency, suppress native .click() on links
// Also prevent it if we're currently inside a leveraged native-event stack
_default:function(event){var target=event.target;return rcheckableType.test(target.type)&&target.click&&nodeName(target,"input")&&dataPriv.get(target,"click")||nodeName(target,"a")}},beforeunload:{postDispatch:function(event){
// Support: Firefox 20+
// Firefox doesn't alert if the returnValue field is not set.
void 0!==event.result&&event.originalEvent&&(event.originalEvent.returnValue=event.result)}}}},jQuery.removeEvent=function(elem,type,handle){
// This "if" is needed for plain objects
elem.removeEventListener&&elem.removeEventListener(type,handle)},jQuery.Event=function(src,props){
// Allow instantiation without the 'new' keyword
if(!(this instanceof jQuery.Event))return new jQuery.Event(src,props);
// Event object
src&&src.type?(this.originalEvent=src,this.type=src.type,
// Events bubbling up the document may have been marked as prevented
// by a handler lower down the tree; reflect the correct value.
this.isDefaultPrevented=src.defaultPrevented||void 0===src.defaultPrevented&&
// Support: Android <=2.3 only
!1===src.returnValue?returnTrue:returnFalse,
// Create target properties
// Support: Safari <=6 - 7 only
// Target should not be a text node (trac-504, trac-13143)
this.target=src.target&&3===src.target.nodeType?src.target.parentNode:src.target,this.currentTarget=src.currentTarget,this.relatedTarget=src.relatedTarget):this.type=src,
// Put explicitly provided properties onto the event object
props&&jQuery.extend(this,props),
// Create a timestamp if incoming event doesn't have one
this.timeStamp=src&&src.timeStamp||Date.now(),
// Mark it as fixed
this[jQuery.expando]=!0},
// jQuery.Event is based on DOM3 Events as specified by the ECMAScript Language Binding
// https://www.w3.org/TR/2003/WD-DOM-Level-3-Events-20030331/ecma-script-binding.html
jQuery.Event.prototype={constructor:jQuery.Event,isDefaultPrevented:returnFalse,isPropagationStopped:returnFalse,isImmediatePropagationStopped:returnFalse,isSimulated:!1,preventDefault:function(){var e=this.originalEvent;this.isDefaultPrevented=returnTrue,e&&!this.isSimulated&&e.preventDefault()},stopPropagation:function(){var e=this.originalEvent;this.isPropagationStopped=returnTrue,e&&!this.isSimulated&&e.stopPropagation()},stopImmediatePropagation:function(){var e=this.originalEvent;this.isImmediatePropagationStopped=returnTrue,e&&!this.isSimulated&&e.stopImmediatePropagation(),this.stopPropagation()}},
// Includes all common event props including KeyEvent and MouseEvent specific props
jQuery.each({altKey:!0,bubbles:!0,cancelable:!0,changedTouches:!0,ctrlKey:!0,detail:!0,eventPhase:!0,metaKey:!0,pageX:!0,pageY:!0,shiftKey:!0,view:!0,char:!0,code:!0,charCode:!0,key:!0,keyCode:!0,button:!0,buttons:!0,clientX:!0,clientY:!0,offsetX:!0,offsetY:!0,pointerId:!0,pointerType:!0,screenX:!0,screenY:!0,targetTouches:!0,toElement:!0,touches:!0,which:!0},jQuery.event.addProp),jQuery.each({focus:"focusin",blur:"focusout"},(function(type,delegateType){function focusMappedHandler(nativeEvent){if(document.documentMode){
// Support: IE 11+
// Attach a single focusin/focusout handler on the document while someone wants
// focus/blur. This is because the former are synchronous in IE while the latter
// are async. In other browsers, all those handlers are invoked synchronously.
// `handle` from private data would already wrap the event, but we need
// to change the `type` here.
var handle=dataPriv.get(this,"handle"),event=jQuery.event.fix(nativeEvent);event.type="focusin"===nativeEvent.type?"focus":"blur",event.isSimulated=!0,
// First, handle focusin/focusout
handle(nativeEvent),
// ...then, handle focus/blur
// focus/blur don't bubble while focusin/focusout do; simulate the former by only
// invoking the handler at the lower level.
event.target===event.currentTarget&&
// The setup part calls `leverageNative`, which, in turn, calls
// `jQuery.event.add`, so event handle will already have been set
// by this point.
handle(event)}else
// For non-IE browsers, attach a single capturing handler on the document
// while someone wants focusin/focusout.
jQuery.event.simulate(delegateType,nativeEvent.target,jQuery.event.fix(nativeEvent))}jQuery.event.special[type]={
// Utilize native event if possible so blur/focus sequence is correct
setup:function(){var attaches;
// Claim the first handler
// dataPriv.set( this, "focus", ... )
// dataPriv.set( this, "blur", ... )
if(leverageNative(this,type,!0),!document.documentMode)
// Return false to allow normal processing in the caller
return!1;
// Support: IE 9 - 11+
// We use the same native handler for focusin & focus (and focusout & blur)
// so we need to coordinate setup & teardown parts between those events.
// Use `delegateType` as the key as `type` is already used by `leverageNative`.
attaches=dataPriv.get(this,delegateType),attaches||this.addEventListener(delegateType,focusMappedHandler),dataPriv.set(this,delegateType,(attaches||0)+1)},trigger:function(){
// Return non-false to allow normal event-path propagation
// Force setup before trigger
return leverageNative(this,type),!0},teardown:function(){var attaches;if(!document.documentMode)
// Return false to indicate standard teardown should be applied
return!1;attaches=dataPriv.get(this,delegateType)-1,attaches?dataPriv.set(this,delegateType,attaches):(this.removeEventListener(delegateType,focusMappedHandler),dataPriv.remove(this,delegateType))},
// Suppress native focus or blur if we're currently inside
// a leveraged native-event stack
_default:function(event){return dataPriv.get(event.target,type)},delegateType:delegateType},
// Support: Firefox <=44
// Firefox doesn't have focus(in | out) events
// Related ticket - https://bugzilla.mozilla.org/show_bug.cgi?id=687787
// Support: Chrome <=48 - 49, Safari <=9.0 - 9.1
// focus(in | out) events fire after focus & blur events,
// which is spec violation - http://www.w3.org/TR/DOM-Level-3-Events/#events-focusevent-event-order
// Related ticket - https://bugs.chromium.org/p/chromium/issues/detail?id=449857
// Support: IE 9 - 11+
// To preserve relative focusin/focus & focusout/blur event order guaranteed on the 3.x branch,
// attach a single handler for both events in IE.
jQuery.event.special[delegateType]={setup:function(){
// Handle: regular nodes (via `this.ownerDocument`), window
// (via `this.document`) & document (via `this`).
var doc=this.ownerDocument||this.document||this,dataHolder=document.documentMode?this:doc,attaches=dataPriv.get(dataHolder,delegateType);
// Support: IE 9 - 11+
// We use the same native handler for focusin & focus (and focusout & blur)
// so we need to coordinate setup & teardown parts between those events.
// Use `delegateType` as the key as `type` is already used by `leverageNative`.
attaches||(document.documentMode?this.addEventListener(delegateType,focusMappedHandler):doc.addEventListener(type,focusMappedHandler,!0)),dataPriv.set(dataHolder,delegateType,(attaches||0)+1)},teardown:function(){var doc=this.ownerDocument||this.document||this,dataHolder=document.documentMode?this:doc,attaches=dataPriv.get(dataHolder,delegateType)-1;attaches?dataPriv.set(dataHolder,delegateType,attaches):(document.documentMode?this.removeEventListener(delegateType,focusMappedHandler):doc.removeEventListener(type,focusMappedHandler,!0),dataPriv.remove(dataHolder,delegateType))}}})),
// Create mouseenter/leave events using mouseover/out and event-time checks
// so that event delegation works in jQuery.
// Do the same for pointerenter/pointerleave and pointerover/pointerout
// Support: Safari 7 only
// Safari sends mouseenter too often; see:
// https://bugs.chromium.org/p/chromium/issues/detail?id=470258
// for the description of the bug (it existed in older Chrome versions as well).
jQuery.each({mouseenter:"mouseover",mouseleave:"mouseout",pointerenter:"pointerover",pointerleave:"pointerout"},(function(orig,fix){jQuery.event.special[orig]={delegateType:fix,bindType:fix,handle:function(event){var ret,target=this,related=event.relatedTarget,handleObj=event.handleObj;
// For mouseenter/leave call the handler if related is outside the target.
// NB: No relatedTarget if the mouse left/entered the browser window
return related&&(related===target||jQuery.contains(target,related))||(event.type=handleObj.origType,ret=handleObj.handler.apply(this,arguments),event.type=fix),ret}}})),jQuery.fn.extend({on:function(types,selector,data,fn){return on(this,types,selector,data,fn)},one:function(types,selector,data,fn){return on(this,types,selector,data,fn,1)},off:function(types,selector,fn){var handleObj,type;if(types&&types.preventDefault&&types.handleObj)
// ( event )  dispatched jQuery.Event
return handleObj=types.handleObj,jQuery(types.delegateTarget).off(handleObj.namespace?handleObj.origType+"."+handleObj.namespace:handleObj.origType,handleObj.selector,handleObj.handler),this;if("object"===typeof types){
// ( types-object [, selector] )
for(type in types)this.off(type,selector,types[type]);return this}return!1!==selector&&"function"!==typeof selector||(
// ( types [, fn] )
fn=selector,selector=void 0),!1===fn&&(fn=returnFalse),this.each((function(){jQuery.event.remove(this,types,fn,selector)}))}});var
// Support: IE <=10 - 11, Edge 12 - 13 only
// In IE/Edge using regex groups here causes severe slowdowns.
// See https://connect.microsoft.com/IE/feedback/details/1736512/
rnoInnerhtml=/<script|<style|<link/i,
// checked="checked" or checked
rchecked=/checked\s*(?:[^=]|=\s*.checked.)/i,rcleanScript=/^\s*<!\[CDATA\[|\]\]>\s*$/g;
// Prefer a tbody over its parent table for containing new rows
function manipulationTarget(elem,content){return nodeName(elem,"table")&&nodeName(11!==content.nodeType?content:content.firstChild,"tr")&&jQuery(elem).children("tbody")[0]||elem}
// Replace/restore the type attribute of script elements for safe DOM manipulation
function disableScript(elem){return elem.type=(null!==elem.getAttribute("type"))+"/"+elem.type,elem}function restoreScript(elem){return"true/"===(elem.type||"").slice(0,5)?elem.type=elem.type.slice(5):elem.removeAttribute("type"),elem}function cloneCopyEvent(src,dest){var i,l,type,pdataOld,udataOld,udataCur,events;if(1===dest.nodeType){
// 1. Copy private data: events, handlers, etc.
if(dataPriv.hasData(src)&&(pdataOld=dataPriv.get(src),events=pdataOld.events,events))for(type in dataPriv.remove(dest,"handle events"),events)for(i=0,l=events[type].length;i<l;i++)jQuery.event.add(dest,type,events[type][i]);
// 2. Copy user data
dataUser.hasData(src)&&(udataOld=dataUser.access(src),udataCur=jQuery.extend({},udataOld),dataUser.set(dest,udataCur))}}
// Fix IE bugs, see support tests
function fixInput(src,dest){var nodeName=dest.nodeName.toLowerCase();
// Fails to persist the checked state of a cloned checkbox or radio button.
"input"===nodeName&&rcheckableType.test(src.type)?dest.checked=src.checked:"input"!==nodeName&&"textarea"!==nodeName||(dest.defaultValue=src.defaultValue)}function domManip(collection,args,callback,ignored){
// Flatten any nested arrays
args=flat(args);var fragment,first,scripts,hasScripts,node,doc,i=0,l=collection.length,iNoClone=l-1,value=args[0],valueIsFunction=isFunction(value);
// We can't cloneNode fragments that contain checked, in WebKit
if(valueIsFunction||l>1&&"string"===typeof value&&!support.checkClone&&rchecked.test(value))return collection.each((function(index){var self=collection.eq(index);valueIsFunction&&(args[0]=value.call(this,index,self.html())),domManip(self,args,callback,ignored)}));if(l&&(fragment=buildFragment(args,collection[0].ownerDocument,!1,collection,ignored),first=fragment.firstChild,1===fragment.childNodes.length&&(fragment=first),first||ignored)){
// Use the original fragment for the last item
// instead of the first because it can end up
// being emptied incorrectly in certain situations (trac-8070).
for(scripts=jQuery.map(getAll(fragment,"script"),disableScript),hasScripts=scripts.length;i<l;i++)node=fragment,i!==iNoClone&&(node=jQuery.clone(node,!0,!0),
// Keep references to cloned scripts for later restoration
hasScripts&&
// Support: Android <=4.0 only, PhantomJS 1 only
// push.apply(_, arraylike) throws on ancient WebKit
jQuery.merge(scripts,getAll(node,"script"))),callback.call(collection[i],node,i);if(hasScripts)
// Evaluate executable scripts on first document insertion
for(doc=scripts[scripts.length-1].ownerDocument,
// Re-enable scripts
jQuery.map(scripts,restoreScript),i=0;i<hasScripts;i++)node=scripts[i],rscriptType.test(node.type||"")&&!dataPriv.access(node,"globalEval")&&jQuery.contains(doc,node)&&(node.src&&"module"!==(node.type||"").toLowerCase()?
// Optional AJAX dependency, but won't run scripts if not present
jQuery._evalUrl&&!node.noModule&&jQuery._evalUrl(node.src,{nonce:node.nonce||node.getAttribute("nonce")},doc):
// Unwrap a CDATA section containing script contents. This shouldn't be
// needed as in XML documents they're already not visible when
// inspecting element contents and in HTML documents they have no
// meaning but we're preserving that logic for backwards compatibility.
// This will be removed completely in 4.0. See gh-4904.
DOMEval(node.textContent.replace(rcleanScript,""),node,doc))}return collection}function remove(elem,selector,keepData){for(var node,nodes=selector?jQuery.filter(selector,elem):elem,i=0;null!=(node=nodes[i]);i++)keepData||1!==node.nodeType||jQuery.cleanData(getAll(node)),node.parentNode&&(keepData&&isAttached(node)&&setGlobalEval(getAll(node,"script")),node.parentNode.removeChild(node));return elem}jQuery.extend({htmlPrefilter:function(html){return html},clone:function(elem,dataAndEvents,deepDataAndEvents){var i,l,srcElements,destElements,clone=elem.cloneNode(!0),inPage=isAttached(elem);
// Fix IE cloning issues
if(!support.noCloneChecked&&(1===elem.nodeType||11===elem.nodeType)&&!jQuery.isXMLDoc(elem))for(
// We eschew jQuery#find here for performance reasons:
// https://jsperf.com/getall-vs-sizzle/2
destElements=getAll(clone),srcElements=getAll(elem),i=0,l=srcElements.length;i<l;i++)fixInput(srcElements[i],destElements[i]);
// Copy the events from the original to the clone
if(dataAndEvents)if(deepDataAndEvents)for(srcElements=srcElements||getAll(elem),destElements=destElements||getAll(clone),i=0,l=srcElements.length;i<l;i++)cloneCopyEvent(srcElements[i],destElements[i]);else cloneCopyEvent(elem,clone);
// Preserve script evaluation history
// Return the cloned set
return destElements=getAll(clone,"script"),destElements.length>0&&setGlobalEval(destElements,!inPage&&getAll(elem,"script")),clone},cleanData:function(elems){for(var data,elem,type,special=jQuery.event.special,i=0;void 0!==(elem=elems[i]);i++)if(acceptData(elem)){if(data=elem[dataPriv.expando]){if(data.events)for(type in data.events)special[type]?jQuery.event.remove(elem,type):jQuery.removeEvent(elem,type,data.handle);
// Support: Chrome <=35 - 45+
// Assign undefined instead of using delete, see Data#remove
elem[dataPriv.expando]=void 0}elem[dataUser.expando]&&(
// Support: Chrome <=35 - 45+
// Assign undefined instead of using delete, see Data#remove
elem[dataUser.expando]=void 0)}}}),jQuery.fn.extend({detach:function(selector){return remove(this,selector,!0)},remove:function(selector){return remove(this,selector)},text:function(value){return access(this,(function(value){return void 0===value?jQuery.text(this):this.empty().each((function(){1!==this.nodeType&&11!==this.nodeType&&9!==this.nodeType||(this.textContent=value)}))}),null,value,arguments.length)},append:function(){return domManip(this,arguments,(function(elem){if(1===this.nodeType||11===this.nodeType||9===this.nodeType){var target=manipulationTarget(this,elem);target.appendChild(elem)}}))},prepend:function(){return domManip(this,arguments,(function(elem){if(1===this.nodeType||11===this.nodeType||9===this.nodeType){var target=manipulationTarget(this,elem);target.insertBefore(elem,target.firstChild)}}))},before:function(){return domManip(this,arguments,(function(elem){this.parentNode&&this.parentNode.insertBefore(elem,this)}))},after:function(){return domManip(this,arguments,(function(elem){this.parentNode&&this.parentNode.insertBefore(elem,this.nextSibling)}))},empty:function(){for(var elem,i=0;null!=(elem=this[i]);i++)1===elem.nodeType&&(
// Prevent memory leaks
jQuery.cleanData(getAll(elem,!1)),
// Remove any remaining nodes
elem.textContent="");return this},clone:function(dataAndEvents,deepDataAndEvents){return dataAndEvents=null!=dataAndEvents&&dataAndEvents,deepDataAndEvents=null==deepDataAndEvents?dataAndEvents:deepDataAndEvents,this.map((function(){return jQuery.clone(this,dataAndEvents,deepDataAndEvents)}))},html:function(value){return access(this,(function(value){var elem=this[0]||{},i=0,l=this.length;if(void 0===value&&1===elem.nodeType)return elem.innerHTML;
// See if we can take a shortcut and just use innerHTML
if("string"===typeof value&&!rnoInnerhtml.test(value)&&!wrapMap[(rtagName.exec(value)||["",""])[1].toLowerCase()]){value=jQuery.htmlPrefilter(value);try{for(;i<l;i++)elem=this[i]||{},
// Remove element nodes and prevent memory leaks
1===elem.nodeType&&(jQuery.cleanData(getAll(elem,!1)),elem.innerHTML=value);elem=0}catch(e){}}elem&&this.empty().append(value)}),null,value,arguments.length)},replaceWith:function(){var ignored=[];
// Make the changes, replacing each non-ignored context element with the new content
return domManip(this,arguments,(function(elem){var parent=this.parentNode;jQuery.inArray(this,ignored)<0&&(jQuery.cleanData(getAll(this)),parent&&parent.replaceChild(elem,this));
// Force callback invocation
}),ignored)}}),jQuery.each({appendTo:"append",prependTo:"prepend",insertBefore:"before",insertAfter:"after",replaceAll:"replaceWith"},(function(name,original){jQuery.fn[name]=function(selector){for(var elems,ret=[],insert=jQuery(selector),last=insert.length-1,i=0;i<=last;i++)elems=i===last?this:this.clone(!0),jQuery(insert[i])[original](elems),
// Support: Android <=4.0 only, PhantomJS 1 only
// .get() because push.apply(_, arraylike) throws on ancient WebKit
push.apply(ret,elems.get());return this.pushStack(ret)}}));var rnumnonpx=new RegExp("^("+pnum+")(?!px)[a-z%]+$","i"),rcustomProp=/^--/,getStyles=function(elem){
// Support: IE <=11 only, Firefox <=30 (trac-15098, trac-14150)
// IE throws on elements created in popups
// FF meanwhile throws on frame elements through "defaultView.getComputedStyle"
var view=elem.ownerDocument.defaultView;return view&&view.opener||(view=window),view.getComputedStyle(elem)},swap=function(elem,options,callback){var ret,name,old={};
// Remember the old values, and insert the new ones
for(name in options)old[name]=elem.style[name],elem.style[name]=options[name];
// Revert the old values
for(name in ret=callback.call(elem),options)elem.style[name]=old[name];return ret},rboxStyle=new RegExp(cssExpand.join("|"),"i");function curCSS(elem,name,computed){var width,minWidth,maxWidth,ret,isCustomProp=rcustomProp.test(name),
// Support: Firefox 51+
// Retrieving style before computed somehow
// fixes an issue with getting wrong values
// on detached elements
style=elem.style;return computed=computed||getStyles(elem),
// getPropertyValue is needed for:
//   .css('filter') (IE 9 only, trac-12537)
//   .css('--customProperty) (gh-3144)
computed&&(
// Support: IE <=9 - 11+
// IE only supports `"float"` in `getPropertyValue`; in computed styles
// it's only available as `"cssFloat"`. We no longer modify properties
// sent to `.css()` apart from camelCasing, so we need to check both.
// Normally, this would create difference in behavior: if
// `getPropertyValue` returns an empty string, the value returned
// by `.css()` would be `undefined`. This is usually the case for
// disconnected elements. However, in IE even disconnected elements
// with no styles return `"none"` for `getPropertyValue( "float" )`
ret=computed.getPropertyValue(name)||computed[name],isCustomProp&&ret&&(
// Support: Firefox 105+, Chrome <=105+
// Spec requires trimming whitespace for custom properties (gh-4926).
// Firefox only trims leading whitespace. Chrome just collapses
// both leading & trailing whitespace to a single space.
// Fall back to `undefined` if empty string returned.
// This collapses a missing definition with property defined
// and set to an empty string but there's no standard API
// allowing us to differentiate them without a performance penalty
// and returning `undefined` aligns with older jQuery.
// rtrimCSS treats U+000D CARRIAGE RETURN and U+000C FORM FEED
// as whitespace while CSS does not, but this is not a problem
// because CSS preprocessing replaces them with U+000A LINE FEED
// (which *is* CSS whitespace)
// https://www.w3.org/TR/css-syntax-3/#input-preprocessing
ret=ret.replace(rtrimCSS,"$1")||void 0),""!==ret||isAttached(elem)||(ret=jQuery.style(elem,name)),
// A tribute to the "awesome hack by Dean Edwards"
// Android Browser returns percentage for some values,
// but width seems to be reliably pixels.
// This is against the CSSOM draft spec:
// https://drafts.csswg.org/cssom/#resolved-values
!support.pixelBoxStyles()&&rnumnonpx.test(ret)&&rboxStyle.test(name)&&(
// Remember the original values
width=style.width,minWidth=style.minWidth,maxWidth=style.maxWidth,
// Put in the new values to get a computed value out
style.minWidth=style.maxWidth=style.width=ret,ret=computed.width,
// Revert the changed values
style.width=width,style.minWidth=minWidth,style.maxWidth=maxWidth)),void 0!==ret?
// Support: IE <=9 - 11 only
// IE returns zIndex value as an integer.
ret+"":ret}function addGetHookIf(conditionFn,hookFn){
// Define the hook, we'll check on the first run if it's really needed.
return{get:function(){if(!conditionFn())
// Hook needed; redefine it so that the support test is not executed again.
return(this.get=hookFn).apply(this,arguments);
// Hook not needed (or it's not possible to use it due
// to missing dependency), remove it.
delete this.get}}}(function(){
// Executing both pixelPosition & boxSizingReliable tests require only one layout
// so they're executed at the same time to save the second computation.
function computeStyleTests(){
// This is a singleton, we need to execute it only once
if(div){container.style.cssText="position:absolute;left:-11111px;width:60px;margin-top:1px;padding:0;border:0",div.style.cssText="position:relative;display:block;box-sizing:border-box;overflow:scroll;margin:auto;border:1px;padding:1px;width:60%;top:1%",documentElement.appendChild(container).appendChild(div);var divStyle=window.getComputedStyle(div);pixelPositionVal="1%"!==divStyle.top,
// Support: Android 4.0 - 4.3 only, Firefox <=3 - 44
reliableMarginLeftVal=12===roundPixelMeasures(divStyle.marginLeft),
// Support: Android 4.0 - 4.3 only, Safari <=9.1 - 10.1, iOS <=7.0 - 9.3
// Some styles come back with percentage values, even though they shouldn't
div.style.right="60%",pixelBoxStylesVal=36===roundPixelMeasures(divStyle.right),
// Support: IE 9 - 11 only
// Detect misreporting of content dimensions for box-sizing:border-box elements
boxSizingReliableVal=36===roundPixelMeasures(divStyle.width),
// Support: IE 9 only
// Detect overflow:scroll screwiness (gh-3699)
// Support: Chrome <=64
// Don't get tricked when zoom affects offsetWidth (gh-4029)
div.style.position="absolute",scrollboxSizeVal=12===roundPixelMeasures(div.offsetWidth/3),documentElement.removeChild(container),
// Nullify the div so it wouldn't be stored in the memory and
// it will also be a sign that checks already performed
div=null}}function roundPixelMeasures(measure){return Math.round(parseFloat(measure))}var pixelPositionVal,boxSizingReliableVal,scrollboxSizeVal,pixelBoxStylesVal,reliableTrDimensionsVal,reliableMarginLeftVal,container=document.createElement("div"),div=document.createElement("div");
// Finish early in limited (non-browser) environments
div.style&&(
// Support: IE <=9 - 11 only
// Style of cloned element affects source element cloned (trac-8908)
div.style.backgroundClip="content-box",div.cloneNode(!0).style.backgroundClip="",support.clearCloneStyle="content-box"===div.style.backgroundClip,jQuery.extend(support,{boxSizingReliable:function(){return computeStyleTests(),boxSizingReliableVal},pixelBoxStyles:function(){return computeStyleTests(),pixelBoxStylesVal},pixelPosition:function(){return computeStyleTests(),pixelPositionVal},reliableMarginLeft:function(){return computeStyleTests(),reliableMarginLeftVal},scrollboxSize:function(){return computeStyleTests(),scrollboxSizeVal},
// Support: IE 9 - 11+, Edge 15 - 18+
// IE/Edge misreport `getComputedStyle` of table rows with width/height
// set in CSS while `offset*` properties report correct values.
// Behavior in IE 9 is more subtle than in newer versions & it passes
// some versions of this test; make sure not to make it pass there!
// Support: Firefox 70+
// Only Firefox includes border widths
// in computed dimensions. (gh-4529)
reliableTrDimensions:function(){var table,tr,trChild,trStyle;return null==reliableTrDimensionsVal&&(table=document.createElement("table"),tr=document.createElement("tr"),trChild=document.createElement("div"),table.style.cssText="position:absolute;left:-11111px;border-collapse:separate",tr.style.cssText="box-sizing:content-box;border:1px solid",
// Support: Chrome 86+
// Height set through cssText does not get applied.
// Computed height then comes back as 0.
tr.style.height="1px",trChild.style.height="9px",
// Support: Android 8 Chrome 86+
// In our bodyBackground.html iframe,
// display for all div elements is set to "inline",
// which causes a problem only in Android 8 Chrome 86.
// Ensuring the div is `display: block`
// gets around this issue.
trChild.style.display="block",documentElement.appendChild(table).appendChild(tr).appendChild(trChild),trStyle=window.getComputedStyle(tr),reliableTrDimensionsVal=parseInt(trStyle.height,10)+parseInt(trStyle.borderTopWidth,10)+parseInt(trStyle.borderBottomWidth,10)===tr.offsetHeight,documentElement.removeChild(table)),reliableTrDimensionsVal}}))})();var cssPrefixes=["Webkit","Moz","ms"],emptyStyle=document.createElement("div").style,vendorProps={};
// Return a vendor-prefixed property or undefined
function vendorPropName(name){
// Check for vendor prefixed names
var capName=name[0].toUpperCase()+name.slice(1),i=cssPrefixes.length;while(i--)if(name=cssPrefixes[i]+capName,name in emptyStyle)return name}
// Return a potentially-mapped jQuery.cssProps or vendor prefixed property
function finalPropName(name){var final=jQuery.cssProps[name]||vendorProps[name];return final||(name in emptyStyle?name:vendorProps[name]=vendorPropName(name)||name)}var
// Swappable if display is none or starts with table
// except "table", "table-cell", or "table-caption"
// See here for display values: https://developer.mozilla.org/en-US/docs/CSS/display
rdisplayswap=/^(none|table(?!-c[ea]).+)/,cssShow={position:"absolute",visibility:"hidden",display:"block"},cssNormalTransform={letterSpacing:"0",fontWeight:"400"};function setPositiveNumber(_elem,value,subtract){
// Any relative (+/-) values have already been
// normalized at this point
var matches=rcssNum.exec(value);return matches?
// Guard against undefined "subtract", e.g., when used as in cssHooks
Math.max(0,matches[2]-(subtract||0))+(matches[3]||"px"):value}function boxModelAdjustment(elem,dimension,box,isBorderBox,styles,computedVal){var i="width"===dimension?1:0,extra=0,delta=0,marginDelta=0;
// Adjustment may not be necessary
if(box===(isBorderBox?"border":"content"))return 0;for(;i<4;i+=2)
// Both box models exclude margin
// Count margin delta separately to only add it after scroll gutter adjustment.
// This is needed to make negative margins work with `outerHeight( true )` (gh-3982).
"margin"===box&&(marginDelta+=jQuery.css(elem,box+cssExpand[i],!0,styles)),
// If we get here with a content-box, we're seeking "padding" or "border" or "margin"
isBorderBox?(
// For "content", subtract padding
"content"===box&&(delta-=jQuery.css(elem,"padding"+cssExpand[i],!0,styles)),
// For "content" or "padding", subtract border
"margin"!==box&&(delta-=jQuery.css(elem,"border"+cssExpand[i]+"Width",!0,styles))):(
// Add padding
delta+=jQuery.css(elem,"padding"+cssExpand[i],!0,styles),
// For "border" or "margin", add border
"padding"!==box?delta+=jQuery.css(elem,"border"+cssExpand[i]+"Width",!0,styles):extra+=jQuery.css(elem,"border"+cssExpand[i]+"Width",!0,styles));
// Account for positive content-box scroll gutter when requested by providing computedVal
return!isBorderBox&&computedVal>=0&&(
// offsetWidth/offsetHeight is a rounded sum of content, padding, scroll gutter, and border
// Assuming integer scroll gutter, subtract the rest and round down
delta+=Math.max(0,Math.ceil(elem["offset"+dimension[0].toUpperCase()+dimension.slice(1)]-computedVal-delta-extra-.5))||0),delta+marginDelta}function getWidthOrHeight(elem,dimension,extra){
// Start with computed style
var styles=getStyles(elem),
// To avoid forcing a reflow, only fetch boxSizing if we need it (gh-4322).
// Fake content-box until we know it's needed to know the true value.
boxSizingNeeded=!support.boxSizingReliable()||extra,isBorderBox=boxSizingNeeded&&"border-box"===jQuery.css(elem,"boxSizing",!1,styles),valueIsBorderBox=isBorderBox,val=curCSS(elem,dimension,styles),offsetProp="offset"+dimension[0].toUpperCase()+dimension.slice(1);
// Support: Firefox <=54
// Return a confounding non-pixel value or feign ignorance, as appropriate.
if(rnumnonpx.test(val)){if(!extra)return val;val="auto"}
// Support: IE 9 - 11 only
// Use offsetWidth/offsetHeight for when box sizing is unreliable.
// In those cases, the computed value can be trusted to be border-box.
// Adjust for the element's box model
return(!support.boxSizingReliable()&&isBorderBox||
// Support: IE 10 - 11+, Edge 15 - 18+
// IE/Edge misreport `getComputedStyle` of table rows with width/height
// set in CSS while `offset*` properties report correct values.
// Interestingly, in some cases IE 9 doesn't suffer from this issue.
!support.reliableTrDimensions()&&nodeName(elem,"tr")||
// Fall back to offsetWidth/offsetHeight when value is "auto"
// This happens for inline elements with no explicit setting (gh-3571)
"auto"===val||
// Support: Android <=4.1 - 4.3 only
// Also use offsetWidth/offsetHeight for misreported inline dimensions (gh-3602)
!parseFloat(val)&&"inline"===jQuery.css(elem,"display",!1,styles))&&
// Make sure the element is visible & connected
elem.getClientRects().length&&(isBorderBox="border-box"===jQuery.css(elem,"boxSizing",!1,styles),
// Where available, offsetWidth/offsetHeight approximate border box dimensions.
// Where not available (e.g., SVG), assume unreliable box-sizing and interpret the
// retrieved value as a content box dimension.
valueIsBorderBox=offsetProp in elem,valueIsBorderBox&&(val=elem[offsetProp])),
// Normalize "" and auto
val=parseFloat(val)||0,val+boxModelAdjustment(elem,dimension,extra||(isBorderBox?"border":"content"),valueIsBorderBox,styles,
// Provide the current computed size to request scroll gutter calculation (gh-3589)
val)+"px"}function Tween(elem,options,prop,end,easing){return new Tween.prototype.init(elem,options,prop,end,easing)}jQuery.extend({
// Add in style property hooks for overriding the default
// behavior of getting and setting a style property
cssHooks:{opacity:{get:function(elem,computed){if(computed){
// We should always get a number back from opacity
var ret=curCSS(elem,"opacity");return""===ret?"1":ret}}}},
// Don't automatically add "px" to these possibly-unitless properties
cssNumber:{animationIterationCount:!0,aspectRatio:!0,borderImageSlice:!0,columnCount:!0,flexGrow:!0,flexShrink:!0,fontWeight:!0,gridArea:!0,gridColumn:!0,gridColumnEnd:!0,gridColumnStart:!0,gridRow:!0,gridRowEnd:!0,gridRowStart:!0,lineHeight:!0,opacity:!0,order:!0,orphans:!0,scale:!0,widows:!0,zIndex:!0,zoom:!0,
// SVG-related
fillOpacity:!0,floodOpacity:!0,stopOpacity:!0,strokeMiterlimit:!0,strokeOpacity:!0},
// Add in properties whose names you wish to fix before
// setting or getting the value
cssProps:{},
// Get and set the style property on a DOM Node
style:function(elem,name,value,extra){
// Don't set styles on text and comment nodes
if(elem&&3!==elem.nodeType&&8!==elem.nodeType&&elem.style){
// Make sure that we're working with the right name
var ret,type,hooks,origName=camelCase(name),isCustomProp=rcustomProp.test(name),style=elem.style;
// Make sure that we're working with the right name. We don't
// want to query the value if it is a CSS custom property
// since they are user-defined.
// Check if we're setting a value
if(isCustomProp||(name=finalPropName(origName)),
// Gets hook for the prefixed version, then unprefixed version
hooks=jQuery.cssHooks[name]||jQuery.cssHooks[origName],void 0===value)
// If a hook was provided get the non-computed value from there
return hooks&&"get"in hooks&&void 0!==(ret=hooks.get(elem,!1,extra))?ret:style[name];
// Otherwise just get the value from the style object
type=typeof value,
// Convert "+=" or "-=" to relative numbers (trac-7345)
"string"===type&&(ret=rcssNum.exec(value))&&ret[1]&&(value=adjustCSS(elem,name,ret),
// Fixes bug trac-9237
type="number"),
// Make sure that null and NaN values aren't set (trac-7116)
null!=value&&value===value&&(
// If a number was passed in, add the unit (except for certain CSS properties)
// The isCustomProp check can be removed in jQuery 4.0 when we only auto-append
// "px" to a few hardcoded values.
"number"!==type||isCustomProp||(value+=ret&&ret[3]||(jQuery.cssNumber[origName]?"":"px")),
// background-* props affect original clone's values
support.clearCloneStyle||""!==value||0!==name.indexOf("background")||(style[name]="inherit"),
// If a hook was provided, use that value, otherwise just set the specified value
hooks&&"set"in hooks&&void 0===(value=hooks.set(elem,value,extra))||(isCustomProp?style.setProperty(name,value):style[name]=value))}},css:function(elem,name,extra,styles){var val,num,hooks,origName=camelCase(name),isCustomProp=rcustomProp.test(name);
// Make sure that we're working with the right name. We don't
// want to modify the value if it is a CSS custom property
// since they are user-defined.
// Make numeric if forced or a qualifier was provided and val looks numeric
return isCustomProp||(name=finalPropName(origName)),
// Try prefixed name followed by the unprefixed name
hooks=jQuery.cssHooks[name]||jQuery.cssHooks[origName],
// If a hook was provided get the computed value from there
hooks&&"get"in hooks&&(val=hooks.get(elem,!0,extra)),
// Otherwise, if a way to get the computed value exists, use that
void 0===val&&(val=curCSS(elem,name,styles)),
// Convert "normal" to computed value
"normal"===val&&name in cssNormalTransform&&(val=cssNormalTransform[name]),""===extra||extra?(num=parseFloat(val),!0===extra||isFinite(num)?num||0:val):val}}),jQuery.each(["height","width"],(function(_i,dimension){jQuery.cssHooks[dimension]={get:function(elem,computed,extra){if(computed)
// Certain elements can have dimension info if we invisibly show them
// but it must have a current display style that would benefit
return!rdisplayswap.test(jQuery.css(elem,"display"))||
// Support: Safari 8+
// Table columns in Safari have non-zero offsetWidth & zero
// getBoundingClientRect().width unless display is changed.
// Support: IE <=11 only
// Running getBoundingClientRect on a disconnected node
// in IE throws an error.
elem.getClientRects().length&&elem.getBoundingClientRect().width?getWidthOrHeight(elem,dimension,extra):swap(elem,cssShow,(function(){return getWidthOrHeight(elem,dimension,extra)}))},set:function(elem,value,extra){var matches,styles=getStyles(elem),
// Only read styles.position if the test has a chance to fail
// to avoid forcing a reflow.
scrollboxSizeBuggy=!support.scrollboxSize()&&"absolute"===styles.position,
// To avoid forcing a reflow, only fetch boxSizing if we need it (gh-3991)
boxSizingNeeded=scrollboxSizeBuggy||extra,isBorderBox=boxSizingNeeded&&"border-box"===jQuery.css(elem,"boxSizing",!1,styles),subtract=extra?boxModelAdjustment(elem,dimension,extra,isBorderBox,styles):0;
// Account for unreliable border-box dimensions by comparing offset* to computed and
// faking a content-box to get border and padding (gh-3699)
return isBorderBox&&scrollboxSizeBuggy&&(subtract-=Math.ceil(elem["offset"+dimension[0].toUpperCase()+dimension.slice(1)]-parseFloat(styles[dimension])-boxModelAdjustment(elem,dimension,"border",!1,styles)-.5)),
// Convert to pixels if value adjustment is needed
subtract&&(matches=rcssNum.exec(value))&&"px"!==(matches[3]||"px")&&(elem.style[dimension]=value,value=jQuery.css(elem,dimension)),setPositiveNumber(elem,value,subtract)}}})),jQuery.cssHooks.marginLeft=addGetHookIf(support.reliableMarginLeft,(function(elem,computed){if(computed)return(parseFloat(curCSS(elem,"marginLeft"))||elem.getBoundingClientRect().left-swap(elem,{marginLeft:0},(function(){return elem.getBoundingClientRect().left})))+"px"})),
// These hooks are used by animate to expand properties
jQuery.each({margin:"",padding:"",border:"Width"},(function(prefix,suffix){jQuery.cssHooks[prefix+suffix]={expand:function(value){for(var i=0,expanded={},
// Assumes a single number if not a string
parts="string"===typeof value?value.split(" "):[value];i<4;i++)expanded[prefix+cssExpand[i]+suffix]=parts[i]||parts[i-2]||parts[0];return expanded}},"margin"!==prefix&&(jQuery.cssHooks[prefix+suffix].set=setPositiveNumber)})),jQuery.fn.extend({css:function(name,value){return access(this,(function(elem,name,value){var styles,len,map={},i=0;if(Array.isArray(name)){for(styles=getStyles(elem),len=name.length;i<len;i++)map[name[i]]=jQuery.css(elem,name[i],!1,styles);return map}return void 0!==value?jQuery.style(elem,name,value):jQuery.css(elem,name)}),name,value,arguments.length>1)}}),jQuery.Tween=Tween,Tween.prototype={constructor:Tween,init:function(elem,options,prop,end,easing,unit){this.elem=elem,this.prop=prop,this.easing=easing||jQuery.easing._default,this.options=options,this.start=this.now=this.cur(),this.end=end,this.unit=unit||(jQuery.cssNumber[prop]?"":"px")},cur:function(){var hooks=Tween.propHooks[this.prop];return hooks&&hooks.get?hooks.get(this):Tween.propHooks._default.get(this)},run:function(percent){var eased,hooks=Tween.propHooks[this.prop];return this.options.duration?this.pos=eased=jQuery.easing[this.easing](percent,this.options.duration*percent,0,1,this.options.duration):this.pos=eased=percent,this.now=(this.end-this.start)*eased+this.start,this.options.step&&this.options.step.call(this.elem,this.now,this),hooks&&hooks.set?hooks.set(this):Tween.propHooks._default.set(this),this}},Tween.prototype.init.prototype=Tween.prototype,Tween.propHooks={_default:{get:function(tween){var result;
// Use a property on the element directly when it is not a DOM element,
// or when there is no matching style property that exists.
return 1!==tween.elem.nodeType||null!=tween.elem[tween.prop]&&null==tween.elem.style[tween.prop]?tween.elem[tween.prop]:(
// Passing an empty string as a 3rd parameter to .css will automatically
// attempt a parseFloat and fallback to a string if the parse fails.
// Simple values such as "10px" are parsed to Float;
// complex values such as "rotate(1rad)" are returned as-is.
result=jQuery.css(tween.elem,tween.prop,""),result&&"auto"!==result?result:0)},set:function(tween){
// Use step hook for back compat.
// Use cssHook if its there.
// Use .style if available and use plain properties where available.
jQuery.fx.step[tween.prop]?jQuery.fx.step[tween.prop](tween):1!==tween.elem.nodeType||!jQuery.cssHooks[tween.prop]&&null==tween.elem.style[finalPropName(tween.prop)]?tween.elem[tween.prop]=tween.now:jQuery.style(tween.elem,tween.prop,tween.now+tween.unit)}}},
// Support: IE <=9 only
// Panic based approach to setting things on disconnected nodes
Tween.propHooks.scrollTop=Tween.propHooks.scrollLeft={set:function(tween){tween.elem.nodeType&&tween.elem.parentNode&&(tween.elem[tween.prop]=tween.now)}},jQuery.easing={linear:function(p){return p},swing:function(p){return.5-Math.cos(p*Math.PI)/2},_default:"swing"},jQuery.fx=Tween.prototype.init,
// Back compat <1.8 extension point
jQuery.fx.step={};var fxNow,inProgress,rfxtypes=/^(?:toggle|show|hide)$/,rrun=/queueHooks$/;function schedule(){inProgress&&(!1===document.hidden&&window.requestAnimationFrame?window.requestAnimationFrame(schedule):window.setTimeout(schedule,jQuery.fx.interval),jQuery.fx.tick())}
// Animations created synchronously will run synchronously
function createFxNow(){return window.setTimeout((function(){fxNow=void 0})),fxNow=Date.now()}
// Generate parameters to create a standard animation
function genFx(type,includeWidth){var which,i=0,attrs={height:type};
// If we include width, step value is 1 to do all cssExpand values,
// otherwise step value is 2 to skip over Left and Right
for(includeWidth=includeWidth?1:0;i<4;i+=2-includeWidth)which=cssExpand[i],attrs["margin"+which]=attrs["padding"+which]=type;return includeWidth&&(attrs.opacity=attrs.width=type),attrs}function createTween(value,prop,animation){for(var tween,collection=(Animation.tweeners[prop]||[]).concat(Animation.tweeners["*"]),index=0,length=collection.length;index<length;index++)if(tween=collection[index].call(animation,prop,value))
// We're done with this property
return tween}function defaultPrefilter(elem,props,opts){var prop,value,toggle,hooks,oldfire,propTween,restoreDisplay,display,isBox="width"in props||"height"in props,anim=this,orig={},style=elem.style,hidden=elem.nodeType&&isHiddenWithinTree(elem),dataShow=dataPriv.get(elem,"fxshow");
// Queue-skipping animations hijack the fx hooks
// Detect show/hide animations
for(prop in opts.queue||(hooks=jQuery._queueHooks(elem,"fx"),null==hooks.unqueued&&(hooks.unqueued=0,oldfire=hooks.empty.fire,hooks.empty.fire=function(){hooks.unqueued||oldfire()}),hooks.unqueued++,anim.always((function(){
// Ensure the complete handler is called before this completes
anim.always((function(){hooks.unqueued--,jQuery.queue(elem,"fx").length||hooks.empty.fire()}))}))),props)if(value=props[prop],rfxtypes.test(value)){if(delete props[prop],toggle=toggle||"toggle"===value,value===(hidden?"hide":"show")){
// Pretend to be hidden if this is a "show" and
// there is still data from a stopped show/hide
if("show"!==value||!dataShow||void 0===dataShow[prop])continue;hidden=!0}orig[prop]=dataShow&&dataShow[prop]||jQuery.style(elem,prop)}
// Bail out if this is a no-op like .hide().hide()
if(propTween=!jQuery.isEmptyObject(props),propTween||!jQuery.isEmptyObject(orig))for(prop in
// Restrict "overflow" and "display" styles during box animations
isBox&&1===elem.nodeType&&(
// Support: IE <=9 - 11, Edge 12 - 15
// Record all 3 overflow attributes because IE does not infer the shorthand
// from identically-valued overflowX and overflowY and Edge just mirrors
// the overflowX value there.
opts.overflow=[style.overflow,style.overflowX,style.overflowY],
// Identify a display type, preferring old show/hide data over the CSS cascade
restoreDisplay=dataShow&&dataShow.display,null==restoreDisplay&&(restoreDisplay=dataPriv.get(elem,"display")),display=jQuery.css(elem,"display"),"none"===display&&(restoreDisplay?display=restoreDisplay:(
// Get nonempty value(s) by temporarily forcing visibility
showHide([elem],!0),restoreDisplay=elem.style.display||restoreDisplay,display=jQuery.css(elem,"display"),showHide([elem]))),
// Animate inline elements as inline-block
("inline"===display||"inline-block"===display&&null!=restoreDisplay)&&"none"===jQuery.css(elem,"float")&&(
// Restore the original display value at the end of pure show/hide animations
propTween||(anim.done((function(){style.display=restoreDisplay})),null==restoreDisplay&&(display=style.display,restoreDisplay="none"===display?"":display)),style.display="inline-block")),opts.overflow&&(style.overflow="hidden",anim.always((function(){style.overflow=opts.overflow[0],style.overflowX=opts.overflow[1],style.overflowY=opts.overflow[2]}))),
// Implement show/hide animations
propTween=!1,orig)
// General show/hide setup for this element animation
propTween||(dataShow?"hidden"in dataShow&&(hidden=dataShow.hidden):dataShow=dataPriv.access(elem,"fxshow",{display:restoreDisplay}),
// Store hidden/visible for toggle so `.stop().toggle()` "reverses"
toggle&&(dataShow.hidden=!hidden),
// Show elements before animating them
hidden&&showHide([elem],!0)
/* eslint-disable no-loop-func */,anim.done((function(){for(prop in
/* eslint-enable no-loop-func */
// The final step of a "hide" animation is actually hiding the element
hidden||showHide([elem]),dataPriv.remove(elem,"fxshow"),orig)jQuery.style(elem,prop,orig[prop])}))),
// Per-property setup
propTween=createTween(hidden?dataShow[prop]:0,prop,anim),prop in dataShow||(dataShow[prop]=propTween.start,hidden&&(propTween.end=propTween.start,propTween.start=0))}function propFilter(props,specialEasing){var index,name,easing,value,hooks;
// camelCase, specialEasing and expand cssHook pass
for(index in props)if(name=camelCase(index),easing=specialEasing[name],value=props[index],Array.isArray(value)&&(easing=value[1],value=props[index]=value[0]),index!==name&&(props[name]=value,delete props[index]),hooks=jQuery.cssHooks[name],hooks&&"expand"in hooks)
// Not quite $.extend, this won't overwrite existing keys.
// Reusing 'index' because we have the correct "name"
for(index in value=hooks.expand(value),delete props[name],value)index in props||(props[index]=value[index],specialEasing[index]=easing);else specialEasing[name]=easing}function Animation(elem,properties,options){var result,stopped,index=0,length=Animation.prefilters.length,deferred=jQuery.Deferred().always((function(){
// Don't match elem in the :animated selector
delete tick.elem})),tick=function(){if(stopped)return!1;for(var currentTime=fxNow||createFxNow(),remaining=Math.max(0,animation.startTime+animation.duration-currentTime),
// Support: Android 2.3 only
// Archaic crash bug won't allow us to use `1 - ( 0.5 || 0 )` (trac-12497)
temp=remaining/animation.duration||0,percent=1-temp,index=0,length=animation.tweens.length;index<length;index++)animation.tweens[index].run(percent);
// If there's more to do, yield
return deferred.notifyWith(elem,[animation,percent,remaining]),percent<1&&length?remaining:(
// If this was an empty animation, synthesize a final progress notification
length||deferred.notifyWith(elem,[animation,1,0]),
// Resolve the animation and report its conclusion
deferred.resolveWith(elem,[animation]),!1)},animation=deferred.promise({elem:elem,props:jQuery.extend({},properties),opts:jQuery.extend(!0,{specialEasing:{},easing:jQuery.easing._default},options),originalProperties:properties,originalOptions:options,startTime:fxNow||createFxNow(),duration:options.duration,tweens:[],createTween:function(prop,end){var tween=jQuery.Tween(elem,animation.opts,prop,end,animation.opts.specialEasing[prop]||animation.opts.easing);return animation.tweens.push(tween),tween},stop:function(gotoEnd){var index=0,
// If we are going to the end, we want to run all the tweens
// otherwise we skip this part
length=gotoEnd?animation.tweens.length:0;if(stopped)return this;for(stopped=!0;index<length;index++)animation.tweens[index].run(1);
// Resolve when we played the last frame; otherwise, reject
return gotoEnd?(deferred.notifyWith(elem,[animation,1,0]),deferred.resolveWith(elem,[animation,gotoEnd])):deferred.rejectWith(elem,[animation,gotoEnd]),this}}),props=animation.props;for(propFilter(props,animation.opts.specialEasing);index<length;index++)if(result=Animation.prefilters[index].call(animation,elem,props,animation.opts),result)return isFunction(result.stop)&&(jQuery._queueHooks(animation.elem,animation.opts.queue).stop=result.stop.bind(result)),result;return jQuery.map(props,createTween,animation),isFunction(animation.opts.start)&&animation.opts.start.call(elem,animation),
// Attach callbacks from options
animation.progress(animation.opts.progress).done(animation.opts.done,animation.opts.complete).fail(animation.opts.fail).always(animation.opts.always),jQuery.fx.timer(jQuery.extend(tick,{elem:elem,anim:animation,queue:animation.opts.queue})),animation}jQuery.Animation=jQuery.extend(Animation,{tweeners:{"*":[function(prop,value){var tween=this.createTween(prop,value);return adjustCSS(tween.elem,prop,rcssNum.exec(value),tween),tween}]},tweener:function(props,callback){isFunction(props)?(callback=props,props=["*"]):props=props.match(rnothtmlwhite);for(var prop,index=0,length=props.length;index<length;index++)prop=props[index],Animation.tweeners[prop]=Animation.tweeners[prop]||[],Animation.tweeners[prop].unshift(callback)},prefilters:[defaultPrefilter],prefilter:function(callback,prepend){prepend?Animation.prefilters.unshift(callback):Animation.prefilters.push(callback)}}),jQuery.speed=function(speed,easing,fn){var opt=speed&&"object"===typeof speed?jQuery.extend({},speed):{complete:fn||!fn&&easing||isFunction(speed)&&speed,duration:speed,easing:fn&&easing||easing&&!isFunction(easing)&&easing};
// Go to the end state if fx are off
return jQuery.fx.off?opt.duration=0:"number"!==typeof opt.duration&&(opt.duration in jQuery.fx.speeds?opt.duration=jQuery.fx.speeds[opt.duration]:opt.duration=jQuery.fx.speeds._default),
// Normalize opt.queue - true/undefined/null -> "fx"
null!=opt.queue&&!0!==opt.queue||(opt.queue="fx"),
// Queueing
opt.old=opt.complete,opt.complete=function(){isFunction(opt.old)&&opt.old.call(this),opt.queue&&jQuery.dequeue(this,opt.queue)},opt},jQuery.fn.extend({fadeTo:function(speed,to,easing,callback){
// Show any hidden elements after setting opacity to 0
return this.filter(isHiddenWithinTree).css("opacity",0).show().end().animate({opacity:to},speed,easing,callback)},animate:function(prop,speed,easing,callback){var empty=jQuery.isEmptyObject(prop),optall=jQuery.speed(speed,easing,callback),doAnimation=function(){
// Operate on a copy of prop so per-property easing won't be lost
var anim=Animation(this,jQuery.extend({},prop),optall);
// Empty animations, or finishing resolves immediately
(empty||dataPriv.get(this,"finish"))&&anim.stop(!0)};return doAnimation.finish=doAnimation,empty||!1===optall.queue?this.each(doAnimation):this.queue(optall.queue,doAnimation)},stop:function(type,clearQueue,gotoEnd){var stopQueue=function(hooks){var stop=hooks.stop;delete hooks.stop,stop(gotoEnd)};return"string"!==typeof type&&(gotoEnd=clearQueue,clearQueue=type,type=void 0),clearQueue&&this.queue(type||"fx",[]),this.each((function(){var dequeue=!0,index=null!=type&&type+"queueHooks",timers=jQuery.timers,data=dataPriv.get(this);if(index)data[index]&&data[index].stop&&stopQueue(data[index]);else for(index in data)data[index]&&data[index].stop&&rrun.test(index)&&stopQueue(data[index]);for(index=timers.length;index--;)timers[index].elem!==this||null!=type&&timers[index].queue!==type||(timers[index].anim.stop(gotoEnd),dequeue=!1,timers.splice(index,1));
// Start the next in the queue if the last step wasn't forced.
// Timers currently will call their complete callbacks, which
// will dequeue but only if they were gotoEnd.
!dequeue&&gotoEnd||jQuery.dequeue(this,type)}))},finish:function(type){return!1!==type&&(type=type||"fx"),this.each((function(){var index,data=dataPriv.get(this),queue=data[type+"queue"],hooks=data[type+"queueHooks"],timers=jQuery.timers,length=queue?queue.length:0;
// Enable finishing flag on private data
// Look for any active animations, and finish them
for(data.finish=!0,
// Empty the queue first
jQuery.queue(this,type,[]),hooks&&hooks.stop&&hooks.stop.call(this,!0),index=timers.length;index--;)timers[index].elem===this&&timers[index].queue===type&&(timers[index].anim.stop(!0),timers.splice(index,1));
// Look for any animations in the old queue and finish them
for(index=0;index<length;index++)queue[index]&&queue[index].finish&&queue[index].finish.call(this);
// Turn off finishing flag
delete data.finish}))}}),jQuery.each(["toggle","show","hide"],(function(_i,name){var cssFn=jQuery.fn[name];jQuery.fn[name]=function(speed,easing,callback){return null==speed||"boolean"===typeof speed?cssFn.apply(this,arguments):this.animate(genFx(name,!0),speed,easing,callback)}})),
// Generate shortcuts for custom animations
jQuery.each({slideDown:genFx("show"),slideUp:genFx("hide"),slideToggle:genFx("toggle"),fadeIn:{opacity:"show"},fadeOut:{opacity:"hide"},fadeToggle:{opacity:"toggle"}},(function(name,props){jQuery.fn[name]=function(speed,easing,callback){return this.animate(props,speed,easing,callback)}})),jQuery.timers=[],jQuery.fx.tick=function(){var timer,i=0,timers=jQuery.timers;for(fxNow=Date.now();i<timers.length;i++)timer=timers[i],
// Run the timer and safely remove it when done (allowing for external removal)
timer()||timers[i]!==timer||timers.splice(i--,1);timers.length||jQuery.fx.stop(),fxNow=void 0},jQuery.fx.timer=function(timer){jQuery.timers.push(timer),jQuery.fx.start()},jQuery.fx.interval=13,jQuery.fx.start=function(){inProgress||(inProgress=!0,schedule())},jQuery.fx.stop=function(){inProgress=null},jQuery.fx.speeds={slow:600,fast:200,
// Default speed
_default:400},
// Based off of the plugin by Clint Helfers, with permission.
jQuery.fn.delay=function(time,type){return time=jQuery.fx&&jQuery.fx.speeds[time]||time,type=type||"fx",this.queue(type,(function(next,hooks){var timeout=window.setTimeout(next,time);hooks.stop=function(){window.clearTimeout(timeout)}}))},function(){var input=document.createElement("input"),select=document.createElement("select"),opt=select.appendChild(document.createElement("option"));input.type="checkbox",
// Support: Android <=4.3 only
// Default value for a checkbox should be "on"
support.checkOn=""!==input.value,
// Support: IE <=11 only
// Must access selectedIndex to make default options select
support.optSelected=opt.selected,
// Support: IE <=11 only
// An input loses its value after becoming a radio
input=document.createElement("input"),input.value="t",input.type="radio",support.radioValue="t"===input.value}();var boolHook,attrHandle=jQuery.expr.attrHandle;jQuery.fn.extend({attr:function(name,value){return access(this,jQuery.attr,name,value,arguments.length>1)},removeAttr:function(name){return this.each((function(){jQuery.removeAttr(this,name)}))}}),jQuery.extend({attr:function(elem,name,value){var ret,hooks,nType=elem.nodeType;
// Don't get/set attributes on text, comment and attribute nodes
if(3!==nType&&8!==nType&&2!==nType)
// Fallback to prop when attributes are not supported
return"undefined"===typeof elem.getAttribute?jQuery.prop(elem,name,value):(
// Attribute hooks are determined by the lowercase version
// Grab necessary hook if one is defined
1===nType&&jQuery.isXMLDoc(elem)||(hooks=jQuery.attrHooks[name.toLowerCase()]||(jQuery.expr.match.bool.test(name)?boolHook:void 0)),void 0!==value?null===value?void jQuery.removeAttr(elem,name):hooks&&"set"in hooks&&void 0!==(ret=hooks.set(elem,value,name))?ret:(elem.setAttribute(name,value+""),value):hooks&&"get"in hooks&&null!==(ret=hooks.get(elem,name))?ret:(ret=jQuery.find.attr(elem,name),null==ret?void 0:ret))},attrHooks:{type:{set:function(elem,value){if(!support.radioValue&&"radio"===value&&nodeName(elem,"input")){var val=elem.value;return elem.setAttribute("type",value),val&&(elem.value=val),value}}}},removeAttr:function(elem,value){var name,i=0,
// Attribute names can contain non-HTML whitespace characters
// https://html.spec.whatwg.org/multipage/syntax.html#attributes-2
attrNames=value&&value.match(rnothtmlwhite);if(attrNames&&1===elem.nodeType)while(name=attrNames[i++])elem.removeAttribute(name)}}),
// Hooks for boolean attributes
boolHook={set:function(elem,value,name){return!1===value?
// Remove boolean attributes when set to false
jQuery.removeAttr(elem,name):elem.setAttribute(name,name),name}},jQuery.each(jQuery.expr.match.bool.source.match(/\w+/g),(function(_i,name){var getter=attrHandle[name]||jQuery.find.attr;attrHandle[name]=function(elem,name,isXML){var ret,handle,lowercaseName=name.toLowerCase();return isXML||(
// Avoid an infinite loop by temporarily removing this function from the getter
handle=attrHandle[lowercaseName],attrHandle[lowercaseName]=ret,ret=null!=getter(elem,name,isXML)?lowercaseName:null,attrHandle[lowercaseName]=handle),ret}}));var rfocusable=/^(?:input|select|textarea|button)$/i,rclickable=/^(?:a|area)$/i;
// Strip and collapse whitespace according to HTML spec
// https://infra.spec.whatwg.org/#strip-and-collapse-ascii-whitespace
function stripAndCollapse(value){var tokens=value.match(rnothtmlwhite)||[];return tokens.join(" ")}function getClass(elem){return elem.getAttribute&&elem.getAttribute("class")||""}function classesToArray(value){return Array.isArray(value)?value:"string"===typeof value&&value.match(rnothtmlwhite)||[]}jQuery.fn.extend({prop:function(name,value){return access(this,jQuery.prop,name,value,arguments.length>1)},removeProp:function(name){return this.each((function(){delete this[jQuery.propFix[name]||name]}))}}),jQuery.extend({prop:function(elem,name,value){var ret,hooks,nType=elem.nodeType;
// Don't get/set properties on text, comment and attribute nodes
if(3!==nType&&8!==nType&&2!==nType)return 1===nType&&jQuery.isXMLDoc(elem)||(
// Fix name and attach hooks
name=jQuery.propFix[name]||name,hooks=jQuery.propHooks[name]),void 0!==value?hooks&&"set"in hooks&&void 0!==(ret=hooks.set(elem,value,name))?ret:elem[name]=value:hooks&&"get"in hooks&&null!==(ret=hooks.get(elem,name))?ret:elem[name]},propHooks:{tabIndex:{get:function(elem){
// Support: IE <=9 - 11 only
// elem.tabIndex doesn't always return the
// correct value when it hasn't been explicitly set
// Use proper attribute retrieval (trac-12072)
var tabindex=jQuery.find.attr(elem,"tabindex");return tabindex?parseInt(tabindex,10):rfocusable.test(elem.nodeName)||rclickable.test(elem.nodeName)&&elem.href?0:-1}}},propFix:{for:"htmlFor",class:"className"}}),
// Support: IE <=11 only
// Accessing the selectedIndex property
// forces the browser to respect setting selected
// on the option
// The getter ensures a default option is selected
// when in an optgroup
// eslint rule "no-unused-expressions" is disabled for this code
// since it considers such accessions noop
support.optSelected||(jQuery.propHooks.selected={get:function(elem){
/* eslint no-unused-expressions: "off" */
var parent=elem.parentNode;return parent&&parent.parentNode&&parent.parentNode.selectedIndex,null},set:function(elem){
/* eslint no-unused-expressions: "off" */
var parent=elem.parentNode;parent&&(parent.selectedIndex,parent.parentNode&&parent.parentNode.selectedIndex)}}),jQuery.each(["tabIndex","readOnly","maxLength","cellSpacing","cellPadding","rowSpan","colSpan","useMap","frameBorder","contentEditable"],(function(){jQuery.propFix[this.toLowerCase()]=this})),jQuery.fn.extend({addClass:function(value){var classNames,cur,curValue,className,i,finalValue;return isFunction(value)?this.each((function(j){jQuery(this).addClass(value.call(this,j,getClass(this)))})):(classNames=classesToArray(value),classNames.length?this.each((function(){if(curValue=getClass(this),cur=1===this.nodeType&&" "+stripAndCollapse(curValue)+" ",cur){for(i=0;i<classNames.length;i++)className=classNames[i],cur.indexOf(" "+className+" ")<0&&(cur+=className+" ");
// Only assign if different to avoid unneeded rendering.
finalValue=stripAndCollapse(cur),curValue!==finalValue&&this.setAttribute("class",finalValue)}})):this)},removeClass:function(value){var classNames,cur,curValue,className,i,finalValue;return isFunction(value)?this.each((function(j){jQuery(this).removeClass(value.call(this,j,getClass(this)))})):arguments.length?(classNames=classesToArray(value),classNames.length?this.each((function(){if(curValue=getClass(this),
// This expression is here for better compressibility (see addClass)
cur=1===this.nodeType&&" "+stripAndCollapse(curValue)+" ",cur){for(i=0;i<classNames.length;i++){className=classNames[i];
// Remove *all* instances
while(cur.indexOf(" "+className+" ")>-1)cur=cur.replace(" "+className+" "," ")}
// Only assign if different to avoid unneeded rendering.
finalValue=stripAndCollapse(cur),curValue!==finalValue&&this.setAttribute("class",finalValue)}})):this):this.attr("class","")},toggleClass:function(value,stateVal){var classNames,className,i,self,type=typeof value,isValidValue="string"===type||Array.isArray(value);return isFunction(value)?this.each((function(i){jQuery(this).toggleClass(value.call(this,i,getClass(this),stateVal),stateVal)})):"boolean"===typeof stateVal&&isValidValue?stateVal?this.addClass(value):this.removeClass(value):(classNames=classesToArray(value),this.each((function(){if(isValidValue)for(
// Toggle individual class names
self=jQuery(this),i=0;i<classNames.length;i++)className=classNames[i],
// Check each className given, space separated list
self.hasClass(className)?self.removeClass(className):self.addClass(className);
// Toggle whole class name
else void 0!==value&&"boolean"!==type||(className=getClass(this),className&&
// Store className if set
dataPriv.set(this,"__className__",className),
// If the element has a class name or if we're passed `false`,
// then remove the whole classname (if there was one, the above saved it).
// Otherwise bring back whatever was previously saved (if anything),
// falling back to the empty string if nothing was stored.
this.setAttribute&&this.setAttribute("class",className||!1===value?"":dataPriv.get(this,"__className__")||""))})))},hasClass:function(selector){var className,elem,i=0;className=" "+selector+" ";while(elem=this[i++])if(1===elem.nodeType&&(" "+stripAndCollapse(getClass(elem))+" ").indexOf(className)>-1)return!0;return!1}});var rreturn=/\r/g;jQuery.fn.extend({val:function(value){var hooks,ret,valueIsFunction,elem=this[0];return arguments.length?(valueIsFunction=isFunction(value),this.each((function(i){var val;1===this.nodeType&&(val=valueIsFunction?value.call(this,i,jQuery(this).val()):value,
// Treat null/undefined as ""; convert numbers to string
null==val?val="":"number"===typeof val?val+="":Array.isArray(val)&&(val=jQuery.map(val,(function(value){return null==value?"":value+""}))),hooks=jQuery.valHooks[this.type]||jQuery.valHooks[this.nodeName.toLowerCase()],
// If set returns undefined, fall back to normal setting
hooks&&"set"in hooks&&void 0!==hooks.set(this,val,"value")||(this.value=val))}))):elem?(hooks=jQuery.valHooks[elem.type]||jQuery.valHooks[elem.nodeName.toLowerCase()],hooks&&"get"in hooks&&void 0!==(ret=hooks.get(elem,"value"))?ret:(ret=elem.value,
// Handle most common string cases
"string"===typeof ret?ret.replace(rreturn,""):null==ret?"":ret)):void 0}}),jQuery.extend({valHooks:{option:{get:function(elem){var val=jQuery.find.attr(elem,"value");return null!=val?val:
// Support: IE <=10 - 11 only
// option.text throws exceptions (trac-14686, trac-14858)
// Strip and collapse whitespace
// https://html.spec.whatwg.org/#strip-and-collapse-whitespace
stripAndCollapse(jQuery.text(elem))}},select:{get:function(elem){var value,option,i,options=elem.options,index=elem.selectedIndex,one="select-one"===elem.type,values=one?null:[],max=one?index+1:options.length;
// Loop through all the selected options
for(i=index<0?max:one?index:0;i<max;i++)
// Support: IE <=9 only
// IE8-9 doesn't update selected after form reset (trac-2551)
if(option=options[i],(option.selected||i===index)&&
// Don't return options that are disabled or in a disabled optgroup
!option.disabled&&(!option.parentNode.disabled||!nodeName(option.parentNode,"optgroup"))){
// We don't need an array for one selects
if(
// Get the specific value for the option
value=jQuery(option).val(),one)return value;
// Multi-Selects return an array
values.push(value)}return values},set:function(elem,value){var optionSet,option,options=elem.options,values=jQuery.makeArray(value),i=options.length;while(i--)option=options[i],
/* eslint-disable no-cond-assign */
(option.selected=jQuery.inArray(jQuery.valHooks.option.get(option),values)>-1)&&(optionSet=!0)
/* eslint-enable no-cond-assign */;
// Force browsers to behave consistently when non-matching value is set
return optionSet||(elem.selectedIndex=-1),values}}}}),
// Radios and checkboxes getter/setter
jQuery.each(["radio","checkbox"],(function(){jQuery.valHooks[this]={set:function(elem,value){if(Array.isArray(value))return elem.checked=jQuery.inArray(jQuery(elem).val(),value)>-1}},support.checkOn||(jQuery.valHooks[this].get=function(elem){return null===elem.getAttribute("value")?"on":elem.value})}));
// Return jQuery for attributes-only inclusion
var location=window.location,nonce={guid:Date.now()},rquery=/\?/;
// Cross-browser xml parsing
jQuery.parseXML=function(data){var xml,parserErrorElem;if(!data||"string"!==typeof data)return null;
// Support: IE 9 - 11 only
// IE throws on parseFromString with invalid input.
try{xml=(new window.DOMParser).parseFromString(data,"text/xml")}catch(e){}return parserErrorElem=xml&&xml.getElementsByTagName("parsererror")[0],xml&&!parserErrorElem||jQuery.error("Invalid XML: "+(parserErrorElem?jQuery.map(parserErrorElem.childNodes,(function(el){return el.textContent})).join("\n"):data)),xml};var rfocusMorph=/^(?:focusinfocus|focusoutblur)$/,stopPropagationCallback=function(e){e.stopPropagation()};jQuery.extend(jQuery.event,{trigger:function(event,data,elem,onlyHandlers){var i,cur,tmp,bubbleType,ontype,handle,special,lastElement,eventPath=[elem||document],type=hasOwn.call(event,"type")?event.type:event,namespaces=hasOwn.call(event,"namespace")?event.namespace.split("."):[];
// Don't do events on text and comment nodes
if(cur=lastElement=tmp=elem=elem||document,3!==elem.nodeType&&8!==elem.nodeType&&!rfocusMorph.test(type+jQuery.event.triggered)&&(type.indexOf(".")>-1&&(
// Namespaced trigger; create a regexp to match event type in handle()
namespaces=type.split("."),type=namespaces.shift(),namespaces.sort()),ontype=type.indexOf(":")<0&&"on"+type,
// Caller can pass in a jQuery.Event object, Object, or just an event type string
event=event[jQuery.expando]?event:new jQuery.Event(type,"object"===typeof event&&event),
// Trigger bitmask: & 1 for native handlers; & 2 for jQuery (always true)
event.isTrigger=onlyHandlers?2:3,event.namespace=namespaces.join("."),event.rnamespace=event.namespace?new RegExp("(^|\\.)"+namespaces.join("\\.(?:.*\\.|)")+"(\\.|$)"):null,
// Clean up the event in case it is being reused
event.result=void 0,event.target||(event.target=elem),
// Clone any incoming data and prepend the event, creating the handler arg list
data=null==data?[event]:jQuery.makeArray(data,[event]),
// Allow special events to draw outside the lines
special=jQuery.event.special[type]||{},onlyHandlers||!special.trigger||!1!==special.trigger.apply(elem,data))){
// Determine event propagation path in advance, per W3C events spec (trac-9951)
// Bubble up to document, then to window; watch for a global ownerDocument var (trac-9724)
if(!onlyHandlers&&!special.noBubble&&!isWindow(elem)){for(bubbleType=special.delegateType||type,rfocusMorph.test(bubbleType+type)||(cur=cur.parentNode);cur;cur=cur.parentNode)eventPath.push(cur),tmp=cur;
// Only add window if we got to document (e.g., not plain obj or detached DOM)
tmp===(elem.ownerDocument||document)&&eventPath.push(tmp.defaultView||tmp.parentWindow||window)}
// Fire handlers on the event path
i=0;while((cur=eventPath[i++])&&!event.isPropagationStopped())lastElement=cur,event.type=i>1?bubbleType:special.bindType||type,
// jQuery handler
handle=(dataPriv.get(cur,"events")||Object.create(null))[event.type]&&dataPriv.get(cur,"handle"),handle&&handle.apply(cur,data),
// Native handler
handle=ontype&&cur[ontype],handle&&handle.apply&&acceptData(cur)&&(event.result=handle.apply(cur,data),!1===event.result&&event.preventDefault());return event.type=type,
// If nobody prevented the default action, do it now
onlyHandlers||event.isDefaultPrevented()||special._default&&!1!==special._default.apply(eventPath.pop(),data)||!acceptData(elem)||
// Call a native DOM method on the target with the same name as the event.
// Don't do default actions on window, that's where global variables be (trac-6170)
ontype&&isFunction(elem[type])&&!isWindow(elem)&&(
// Don't re-trigger an onFOO event when we call its FOO() method
tmp=elem[ontype],tmp&&(elem[ontype]=null),
// Prevent re-triggering of the same event, since we already bubbled it above
jQuery.event.triggered=type,event.isPropagationStopped()&&lastElement.addEventListener(type,stopPropagationCallback),elem[type](),event.isPropagationStopped()&&lastElement.removeEventListener(type,stopPropagationCallback),jQuery.event.triggered=void 0,tmp&&(elem[ontype]=tmp)),event.result}
// focus/blur morphs to focusin/out; ensure we're not firing them right now
},
// Piggyback on a donor event to simulate a different one
// Used only for `focus(in | out)` events
simulate:function(type,elem,event){var e=jQuery.extend(new jQuery.Event,event,{type:type,isSimulated:!0});jQuery.event.trigger(e,null,elem)}}),jQuery.fn.extend({trigger:function(type,data){return this.each((function(){jQuery.event.trigger(type,data,this)}))},triggerHandler:function(type,data){var elem=this[0];if(elem)return jQuery.event.trigger(type,data,elem,!0)}});var rbracket=/\[\]$/,rCRLF=/\r?\n/g,rsubmitterTypes=/^(?:submit|button|image|reset|file)$/i,rsubmittable=/^(?:input|select|textarea|keygen)/i;function buildParams(prefix,obj,traditional,add){var name;if(Array.isArray(obj))
// Serialize array item.
jQuery.each(obj,(function(i,v){traditional||rbracket.test(prefix)?
// Treat each array item as a scalar.
add(prefix,v):
// Item is non-scalar (array or object), encode its numeric index.
buildParams(prefix+"["+("object"===typeof v&&null!=v?i:"")+"]",v,traditional,add)}));else if(traditional||"object"!==toType(obj))
// Serialize scalar item.
add(prefix,obj);else
// Serialize object item.
for(name in obj)buildParams(prefix+"["+name+"]",obj[name],traditional,add)}
// Serialize an array of form elements or a set of
// key/values into a query string
jQuery.param=function(a,traditional){var prefix,s=[],add=function(key,valueOrFunction){
// If value is a function, invoke it and use its return value
var value=isFunction(valueOrFunction)?valueOrFunction():valueOrFunction;s[s.length]=encodeURIComponent(key)+"="+encodeURIComponent(null==value?"":value)};if(null==a)return"";
// If an array was passed in, assume that it is an array of form elements.
if(Array.isArray(a)||a.jquery&&!jQuery.isPlainObject(a))
// Serialize the form elements
jQuery.each(a,(function(){add(this.name,this.value)}));else
// If traditional, encode the "old" way (the way 1.3.2 or older
// did it), otherwise encode params recursively.
for(prefix in a)buildParams(prefix,a[prefix],traditional,add);
// Return the resulting serialization
return s.join("&")},jQuery.fn.extend({serialize:function(){return jQuery.param(this.serializeArray())},serializeArray:function(){return this.map((function(){
// Can add propHook for "elements" to filter or add form elements
var elements=jQuery.prop(this,"elements");return elements?jQuery.makeArray(elements):this})).filter((function(){var type=this.type;
// Use .is( ":disabled" ) so that fieldset[disabled] works
return this.name&&!jQuery(this).is(":disabled")&&rsubmittable.test(this.nodeName)&&!rsubmitterTypes.test(type)&&(this.checked||!rcheckableType.test(type))})).map((function(_i,elem){var val=jQuery(this).val();return null==val?null:Array.isArray(val)?jQuery.map(val,(function(val){return{name:elem.name,value:val.replace(rCRLF,"\r\n")}})):{name:elem.name,value:val.replace(rCRLF,"\r\n")}})).get()}});var r20=/%20/g,rhash=/#.*$/,rantiCache=/([?&])_=[^&]*/,rheaders=/^(.*?):[ \t]*([^\r\n]*)$/gm,
// trac-7653, trac-8125, trac-8152: local protocol detection
rlocalProtocol=/^(?:about|app|app-storage|.+-extension|file|res|widget):$/,rnoContent=/^(?:GET|HEAD)$/,rprotocol=/^\/\//,
/* Prefilters
	 * 1) They are useful to introduce custom dataTypes (see ajax/jsonp.js for an example)
	 * 2) These are called:
	 *    - BEFORE asking for a transport
	 *    - AFTER param serialization (s.data is a string if s.processData is true)
	 * 3) key is the dataType
	 * 4) the catchall symbol "*" can be used
	 * 5) execution will start with transport dataType and THEN continue down to "*" if needed
	 */
prefilters={},
/* Transports bindings
	 * 1) key is the dataType
	 * 2) the catchall symbol "*" can be used
	 * 3) selection will start with transport dataType and THEN go to "*" if needed
	 */
transports={},
// Avoid comment-prolog char sequence (trac-10098); must appease lint and evade compression
allTypes="*/".concat("*"),
// Anchor tag for parsing the document origin
originAnchor=document.createElement("a");
// Base "constructor" for jQuery.ajaxPrefilter and jQuery.ajaxTransport
function addToPrefiltersOrTransports(structure){
// dataTypeExpression is optional and defaults to "*"
return function(dataTypeExpression,func){"string"!==typeof dataTypeExpression&&(func=dataTypeExpression,dataTypeExpression="*");var dataType,i=0,dataTypes=dataTypeExpression.toLowerCase().match(rnothtmlwhite)||[];if(isFunction(func))
// For each dataType in the dataTypeExpression
while(dataType=dataTypes[i++])
// Prepend if requested
"+"===dataType[0]?(dataType=dataType.slice(1)||"*",(structure[dataType]=structure[dataType]||[]).unshift(func)):(structure[dataType]=structure[dataType]||[]).push(func)}}
// Base inspection function for prefilters and transports
function inspectPrefiltersOrTransports(structure,options,originalOptions,jqXHR){var inspected={},seekingTransport=structure===transports;function inspect(dataType){var selected;return inspected[dataType]=!0,jQuery.each(structure[dataType]||[],(function(_,prefilterOrFactory){var dataTypeOrTransport=prefilterOrFactory(options,originalOptions,jqXHR);return"string"!==typeof dataTypeOrTransport||seekingTransport||inspected[dataTypeOrTransport]?seekingTransport?!(selected=dataTypeOrTransport):void 0:(options.dataTypes.unshift(dataTypeOrTransport),inspect(dataTypeOrTransport),!1)})),selected}return inspect(options.dataTypes[0])||!inspected["*"]&&inspect("*")}
// A special extend for ajax options
// that takes "flat" options (not to be deep extended)
// Fixes trac-9887
function ajaxExtend(target,src){var key,deep,flatOptions=jQuery.ajaxSettings.flatOptions||{};for(key in src)void 0!==src[key]&&((flatOptions[key]?target:deep||(deep={}))[key]=src[key]);return deep&&jQuery.extend(!0,target,deep),target}
/* Handles responses to an ajax request:
 * - finds the right dataType (mediates between content-type and expected dataType)
 * - returns the corresponding response
 */function ajaxHandleResponses(s,jqXHR,responses){var ct,type,finalDataType,firstDataType,contents=s.contents,dataTypes=s.dataTypes;
// Remove auto dataType and get content-type in the process
while("*"===dataTypes[0])dataTypes.shift(),void 0===ct&&(ct=s.mimeType||jqXHR.getResponseHeader("Content-Type"));
// Check if we're dealing with a known content-type
if(ct)for(type in contents)if(contents[type]&&contents[type].test(ct)){dataTypes.unshift(type);break}
// Check to see if we have a response for the expected dataType
if(dataTypes[0]in responses)finalDataType=dataTypes[0];else{
// Try convertible dataTypes
for(type in responses){if(!dataTypes[0]||s.converters[type+" "+dataTypes[0]]){finalDataType=type;break}firstDataType||(firstDataType=type)}
// Or just use first one
finalDataType=finalDataType||firstDataType}
// If we found a dataType
// We add the dataType to the list if needed
// and return the corresponding response
if(finalDataType)return finalDataType!==dataTypes[0]&&dataTypes.unshift(finalDataType),responses[finalDataType]}
/* Chain conversions given the request and the original response
 * Also sets the responseXXX fields on the jqXHR instance
 */function ajaxConvert(s,response,jqXHR,isSuccess){var conv2,current,conv,tmp,prev,converters={},
// Work with a copy of dataTypes in case we need to modify it for conversion
dataTypes=s.dataTypes.slice();
// Create converters map with lowercased keys
if(dataTypes[1])for(conv in s.converters)converters[conv.toLowerCase()]=s.converters[conv];current=dataTypes.shift();
// Convert to each sequential dataType
while(current)if(s.responseFields[current]&&(jqXHR[s.responseFields[current]]=response),
// Apply the dataFilter if provided
!prev&&isSuccess&&s.dataFilter&&(response=s.dataFilter(response,s.dataType)),prev=current,current=dataTypes.shift(),current)
// There's only work to do if current dataType is non-auto
if("*"===current)current=prev;
// Convert response if prev dataType is non-auto and differs from current
else if("*"!==prev&&prev!==current){
// If none found, seek a pair
if(
// Seek a direct converter
conv=converters[prev+" "+current]||converters["* "+current],!conv)for(conv2 in converters)if(
// If conv2 outputs current
tmp=conv2.split(" "),tmp[1]===current&&(
// If prev can be converted to accepted input
conv=converters[prev+" "+tmp[0]]||converters["* "+tmp[0]],conv)){
// Condense equivalence converters
!0===conv?conv=converters[conv2]:!0!==converters[conv2]&&(current=tmp[0],dataTypes.unshift(tmp[1]));break}
// Apply converter (if not an equivalence)
if(!0!==conv)
// Unless errors are allowed to bubble, catch and return them
if(conv&&s.throws)response=conv(response);else try{response=conv(response)}catch(e){return{state:"parsererror",error:conv?e:"No conversion from "+prev+" to "+current}}}return{state:"success",data:response}}originAnchor.href=location.href,jQuery.extend({
// Counter for holding the number of active queries
active:0,
// Last-Modified header cache for next request
lastModified:{},etag:{},ajaxSettings:{url:location.href,type:"GET",isLocal:rlocalProtocol.test(location.protocol),global:!0,processData:!0,async:!0,contentType:"application/x-www-form-urlencoded; charset=UTF-8",
/*
		timeout: 0,
		data: null,
		dataType: null,
		username: null,
		password: null,
		cache: null,
		throws: false,
		traditional: false,
		headers: {},
		*/
accepts:{"*":allTypes,text:"text/plain",html:"text/html",xml:"application/xml, text/xml",json:"application/json, text/javascript"},contents:{xml:/\bxml\b/,html:/\bhtml/,json:/\bjson\b/},responseFields:{xml:"responseXML",text:"responseText",json:"responseJSON"},
// Data converters
// Keys separate source (or catchall "*") and destination types with a single space
converters:{
// Convert anything to text
"* text":String,
// Text to html (true = no transformation)
"text html":!0,
// Evaluate text as a json expression
"text json":JSON.parse,
// Parse text as xml
"text xml":jQuery.parseXML},
// For options that shouldn't be deep extended:
// you can add your own custom options here if
// and when you create one that shouldn't be
// deep extended (see ajaxExtend)
flatOptions:{url:!0,context:!0}},
// Creates a full fledged settings object into target
// with both ajaxSettings and settings fields.
// If target is omitted, writes into ajaxSettings.
ajaxSetup:function(target,settings){return settings?
// Building a settings object
ajaxExtend(ajaxExtend(target,jQuery.ajaxSettings),settings):
// Extending ajaxSettings
ajaxExtend(jQuery.ajaxSettings,target)},ajaxPrefilter:addToPrefiltersOrTransports(prefilters),ajaxTransport:addToPrefiltersOrTransports(transports),
// Main method
ajax:function(url,options){
// If url is an object, simulate pre-1.5 signature
"object"===typeof url&&(options=url,url=void 0),
// Force options to be an object
options=options||{};var transport,
// URL without anti-cache param
cacheURL,
// Response headers
responseHeadersString,responseHeaders,
// timeout handle
timeoutTimer,
// Url cleanup var
urlAnchor,
// Request state (becomes false upon send and true upon completion)
completed,
// To know if global events are to be dispatched
fireGlobals,
// Loop variable
i,
// uncached part of the url
uncached,
// Create the final options object
s=jQuery.ajaxSetup({},options),
// Callbacks context
callbackContext=s.context||s,
// Context for global events is callbackContext if it is a DOM node or jQuery collection
globalEventContext=s.context&&(callbackContext.nodeType||callbackContext.jquery)?jQuery(callbackContext):jQuery.event,
// Deferreds
deferred=jQuery.Deferred(),completeDeferred=jQuery.Callbacks("once memory"),
// Status-dependent callbacks
statusCode=s.statusCode||{},
// Headers (they are sent all at once)
requestHeaders={},requestHeadersNames={},
// Default abort message
strAbort="canceled",
// Fake xhr
jqXHR={readyState:0,
// Builds headers hashtable if needed
getResponseHeader:function(key){var match;if(completed){if(!responseHeaders){responseHeaders={};while(match=rheaders.exec(responseHeadersString))responseHeaders[match[1].toLowerCase()+" "]=(responseHeaders[match[1].toLowerCase()+" "]||[]).concat(match[2])}match=responseHeaders[key.toLowerCase()+" "]}return null==match?null:match.join(", ")},
// Raw string
getAllResponseHeaders:function(){return completed?responseHeadersString:null},
// Caches the header
setRequestHeader:function(name,value){return null==completed&&(name=requestHeadersNames[name.toLowerCase()]=requestHeadersNames[name.toLowerCase()]||name,requestHeaders[name]=value),this},
// Overrides response content-type header
overrideMimeType:function(type){return null==completed&&(s.mimeType=type),this},
// Status-dependent callbacks
statusCode:function(map){var code;if(map)if(completed)
// Execute the appropriate callbacks
jqXHR.always(map[jqXHR.status]);else
// Lazy-add the new callbacks in a way that preserves old ones
for(code in map)statusCode[code]=[statusCode[code],map[code]];return this},
// Cancel the request
abort:function(statusText){var finalText=statusText||strAbort;return transport&&transport.abort(finalText),done(0,finalText),this}};
// Attach deferreds
// A cross-domain request is in order when the origin doesn't match the current origin.
if(deferred.promise(jqXHR),
// Add protocol if not provided (prefilters might expect it)
// Handle falsy url in the settings object (trac-10093: consistency with old signature)
// We also use the url parameter if available
s.url=((url||s.url||location.href)+"").replace(rprotocol,location.protocol+"//"),
// Alias method option to type as per ticket trac-12004
s.type=options.method||options.type||s.method||s.type,
// Extract dataTypes list
s.dataTypes=(s.dataType||"*").toLowerCase().match(rnothtmlwhite)||[""],null==s.crossDomain){urlAnchor=document.createElement("a");
// Support: IE <=8 - 11, Edge 12 - 15
// IE throws exception on accessing the href property if url is malformed,
// e.g. http://example.com:80x/
try{urlAnchor.href=s.url,
// Support: IE <=8 - 11 only
// Anchor's host property isn't correctly set when s.url is relative
urlAnchor.href=urlAnchor.href,s.crossDomain=originAnchor.protocol+"//"+originAnchor.host!==urlAnchor.protocol+"//"+urlAnchor.host}catch(e){
// If there is an error parsing the URL, assume it is crossDomain,
// it can be rejected by the transport if it is invalid
s.crossDomain=!0}}
// Convert data if not already a string
// If request was aborted inside a prefilter, stop there
if(s.data&&s.processData&&"string"!==typeof s.data&&(s.data=jQuery.param(s.data,s.traditional)),
// Apply prefilters
inspectPrefiltersOrTransports(prefilters,s,options,jqXHR),completed)return jqXHR;
// We can fire global events as of now if asked to
// Don't fire events if jQuery.event is undefined in an AMD-usage scenario (trac-15118)
// Check for headers option
for(i in fireGlobals=jQuery.event&&s.global,
// Watch for a new set of requests
fireGlobals&&0===jQuery.active++&&jQuery.event.trigger("ajaxStart"),
// Uppercase the type
s.type=s.type.toUpperCase(),
// Determine if request has content
s.hasContent=!rnoContent.test(s.type),
// Save the URL in case we're toying with the If-Modified-Since
// and/or If-None-Match header later on
// Remove hash to simplify url manipulation
cacheURL=s.url.replace(rhash,""),
// More options handling for requests with no content
s.hasContent?s.data&&s.processData&&0===(s.contentType||"").indexOf("application/x-www-form-urlencoded")&&(s.data=s.data.replace(r20,"+")):(
// Remember the hash so we can put it back
uncached=s.url.slice(cacheURL.length),
// If data is available and should be processed, append data to url
s.data&&(s.processData||"string"===typeof s.data)&&(cacheURL+=(rquery.test(cacheURL)?"&":"?")+s.data,
// trac-9682: remove data so that it's not used in an eventual retry
delete s.data),
// Add or update anti-cache param if needed
!1===s.cache&&(cacheURL=cacheURL.replace(rantiCache,"$1"),uncached=(rquery.test(cacheURL)?"&":"?")+"_="+nonce.guid+++uncached),
// Put hash and anti-cache on the URL that will be requested (gh-1732)
s.url=cacheURL+uncached),
// Set the If-Modified-Since and/or If-None-Match header, if in ifModified mode.
s.ifModified&&(jQuery.lastModified[cacheURL]&&jqXHR.setRequestHeader("If-Modified-Since",jQuery.lastModified[cacheURL]),jQuery.etag[cacheURL]&&jqXHR.setRequestHeader("If-None-Match",jQuery.etag[cacheURL])),
// Set the correct header, if data is being sent
(s.data&&s.hasContent&&!1!==s.contentType||options.contentType)&&jqXHR.setRequestHeader("Content-Type",s.contentType),
// Set the Accepts header for the server, depending on the dataType
jqXHR.setRequestHeader("Accept",s.dataTypes[0]&&s.accepts[s.dataTypes[0]]?s.accepts[s.dataTypes[0]]+("*"!==s.dataTypes[0]?", "+allTypes+"; q=0.01":""):s.accepts["*"]),s.headers)jqXHR.setRequestHeader(i,s.headers[i]);
// Allow custom headers/mimetypes and early abort
if(s.beforeSend&&(!1===s.beforeSend.call(callbackContext,jqXHR,s)||completed))
// Abort if not done already and return
return jqXHR.abort();
// Aborting is no longer a cancellation
// If no transport, we auto-abort
if(strAbort="abort",
// Install callbacks on deferreds
completeDeferred.add(s.complete),jqXHR.done(s.success),jqXHR.fail(s.error),
// Get transport
transport=inspectPrefiltersOrTransports(transports,s,options,jqXHR),transport){
// If request was aborted inside ajaxSend, stop there
if(jqXHR.readyState=1,
// Send global event
fireGlobals&&globalEventContext.trigger("ajaxSend",[jqXHR,s]),completed)return jqXHR;
// Timeout
s.async&&s.timeout>0&&(timeoutTimer=window.setTimeout((function(){jqXHR.abort("timeout")}),s.timeout));try{completed=!1,transport.send(requestHeaders,done)}catch(e){
// Rethrow post-completion exceptions
if(completed)throw e;
// Propagate others as results
done(-1,e)}}
// Callback for when everything is done
else done(-1,"No Transport");function done(status,nativeStatusText,responses,headers){var isSuccess,success,error,response,modified,statusText=nativeStatusText;
// Ignore repeat invocations
completed||(completed=!0,
// Clear timeout if it exists
timeoutTimer&&window.clearTimeout(timeoutTimer),
// Dereference transport for early garbage collection
// (no matter how long the jqXHR object will be used)
transport=void 0,
// Cache response headers
responseHeadersString=headers||"",
// Set readyState
jqXHR.readyState=status>0?4:0,
// Determine if successful
isSuccess=status>=200&&status<300||304===status,
// Get response data
responses&&(response=ajaxHandleResponses(s,jqXHR,responses)),
// Use a noop converter for missing script but not if jsonp
!isSuccess&&jQuery.inArray("script",s.dataTypes)>-1&&jQuery.inArray("json",s.dataTypes)<0&&(s.converters["text script"]=function(){}),
// Convert no matter what (that way responseXXX fields are always set)
response=ajaxConvert(s,response,jqXHR,isSuccess),
// If successful, handle type chaining
isSuccess?(
// Set the If-Modified-Since and/or If-None-Match header, if in ifModified mode.
s.ifModified&&(modified=jqXHR.getResponseHeader("Last-Modified"),modified&&(jQuery.lastModified[cacheURL]=modified),modified=jqXHR.getResponseHeader("etag"),modified&&(jQuery.etag[cacheURL]=modified)),
// if no content
204===status||"HEAD"===s.type?statusText="nocontent":304===status?statusText="notmodified":(statusText=response.state,success=response.data,error=response.error,isSuccess=!error)):(
// Extract error from statusText and normalize for non-aborts
error=statusText,!status&&statusText||(statusText="error",status<0&&(status=0))),
// Set data for the fake xhr object
jqXHR.status=status,jqXHR.statusText=(nativeStatusText||statusText)+"",
// Success/Error
isSuccess?deferred.resolveWith(callbackContext,[success,statusText,jqXHR]):deferred.rejectWith(callbackContext,[jqXHR,statusText,error]),
// Status-dependent callbacks
jqXHR.statusCode(statusCode),statusCode=void 0,fireGlobals&&globalEventContext.trigger(isSuccess?"ajaxSuccess":"ajaxError",[jqXHR,s,isSuccess?success:error]),
// Complete
completeDeferred.fireWith(callbackContext,[jqXHR,statusText]),fireGlobals&&(globalEventContext.trigger("ajaxComplete",[jqXHR,s]),
// Handle the global AJAX counter
--jQuery.active||jQuery.event.trigger("ajaxStop")))}return jqXHR},getJSON:function(url,data,callback){return jQuery.get(url,data,callback,"json")},getScript:function(url,callback){return jQuery.get(url,void 0,callback,"script")}}),jQuery.each(["get","post"],(function(_i,method){jQuery[method]=function(url,data,callback,type){
// The url can be an options object (which then must have .url)
// Shift arguments if data argument was omitted
return isFunction(data)&&(type=type||callback,callback=data,data=void 0),jQuery.ajax(jQuery.extend({url:url,type:method,dataType:type,data:data,success:callback},jQuery.isPlainObject(url)&&url))}})),jQuery.ajaxPrefilter((function(s){var i;for(i in s.headers)"content-type"===i.toLowerCase()&&(s.contentType=s.headers[i]||"")})),jQuery._evalUrl=function(url,options,doc){return jQuery.ajax({url:url,
// Make this explicit, since user can override this through ajaxSetup (trac-11264)
type:"GET",dataType:"script",cache:!0,async:!1,global:!1,
// Only evaluate the response if it is successful (gh-4126)
// dataFilter is not invoked for failure responses, so using it instead
// of the default converter is kludgy but it works.
converters:{"text script":function(){}},dataFilter:function(response){jQuery.globalEval(response,options,doc)}})},jQuery.fn.extend({wrapAll:function(html){var wrap;return this[0]&&(isFunction(html)&&(html=html.call(this[0])),
// The elements to wrap the target around
wrap=jQuery(html,this[0].ownerDocument).eq(0).clone(!0),this[0].parentNode&&wrap.insertBefore(this[0]),wrap.map((function(){var elem=this;while(elem.firstElementChild)elem=elem.firstElementChild;return elem})).append(this)),this},wrapInner:function(html){return isFunction(html)?this.each((function(i){jQuery(this).wrapInner(html.call(this,i))})):this.each((function(){var self=jQuery(this),contents=self.contents();contents.length?contents.wrapAll(html):self.append(html)}))},wrap:function(html){var htmlIsFunction=isFunction(html);return this.each((function(i){jQuery(this).wrapAll(htmlIsFunction?html.call(this,i):html)}))},unwrap:function(selector){return this.parent(selector).not("body").each((function(){jQuery(this).replaceWith(this.childNodes)})),this}}),jQuery.expr.pseudos.hidden=function(elem){return!jQuery.expr.pseudos.visible(elem)},jQuery.expr.pseudos.visible=function(elem){return!!(elem.offsetWidth||elem.offsetHeight||elem.getClientRects().length)},jQuery.ajaxSettings.xhr=function(){try{return new window.XMLHttpRequest}catch(e){}};var xhrSuccessStatus={
// File protocol always yields status code 0, assume 200
0:200,
// Support: IE <=9 only
// trac-1450: sometimes IE returns 1223 when it should be 204
1223:204},xhrSupported=jQuery.ajaxSettings.xhr();support.cors=!!xhrSupported&&"withCredentials"in xhrSupported,support.ajax=xhrSupported=!!xhrSupported,jQuery.ajaxTransport((function(options){var callback,errorCallback;
// Cross domain only allowed if supported through XMLHttpRequest
if(support.cors||xhrSupported&&!options.crossDomain)return{send:function(headers,complete){var i,xhr=options.xhr();
// Apply custom fields if provided
if(xhr.open(options.type,options.url,options.async,options.username,options.password),options.xhrFields)for(i in options.xhrFields)xhr[i]=options.xhrFields[i];
// Override mime type if needed
// Set headers
for(i in options.mimeType&&xhr.overrideMimeType&&xhr.overrideMimeType(options.mimeType),
// X-Requested-With header
// For cross-domain requests, seeing as conditions for a preflight are
// akin to a jigsaw puzzle, we simply never set it to be sure.
// (it can always be set on a per-request basis or even using ajaxSetup)
// For same-domain requests, won't change header if already provided.
options.crossDomain||headers["X-Requested-With"]||(headers["X-Requested-With"]="XMLHttpRequest"),headers)xhr.setRequestHeader(i,headers[i]);
// Callback
callback=function(type){return function(){callback&&(callback=errorCallback=xhr.onload=xhr.onerror=xhr.onabort=xhr.ontimeout=xhr.onreadystatechange=null,"abort"===type?xhr.abort():"error"===type?
// Support: IE <=9 only
// On a manual native abort, IE9 throws
// errors on any property access that is not readyState
"number"!==typeof xhr.status?complete(0,"error"):complete(
// File: protocol always yields status 0; see trac-8605, trac-14207
xhr.status,xhr.statusText):complete(xhrSuccessStatus[xhr.status]||xhr.status,xhr.statusText,
// Support: IE <=9 only
// IE9 has no XHR2 but throws on binary (trac-11426)
// For XHR2 non-text, let the caller handle it (gh-2498)
"text"!==(xhr.responseType||"text")||"string"!==typeof xhr.responseText?{binary:xhr.response}:{text:xhr.responseText},xhr.getAllResponseHeaders()))}},
// Listen to events
xhr.onload=callback(),errorCallback=xhr.onerror=xhr.ontimeout=callback("error"),
// Support: IE 9 only
// Use onreadystatechange to replace onabort
// to handle uncaught aborts
void 0!==xhr.onabort?xhr.onabort=errorCallback:xhr.onreadystatechange=function(){
// Check readyState before timeout as it changes
4===xhr.readyState&&
// Allow onerror to be called first,
// but that will not handle a native abort
// Also, save errorCallback to a variable
// as xhr.onerror cannot be accessed
window.setTimeout((function(){callback&&errorCallback()}))},
// Create the abort callback
callback=callback("abort");try{
// Do send the request (this may raise an exception)
xhr.send(options.hasContent&&options.data||null)}catch(e){
// trac-14683: Only rethrow if this hasn't been notified as an error yet
if(callback)throw e}},abort:function(){callback&&callback()}}})),
// Prevent auto-execution of scripts when no explicit dataType was provided (See gh-2432)
jQuery.ajaxPrefilter((function(s){s.crossDomain&&(s.contents.script=!1)})),
// Install script dataType
jQuery.ajaxSetup({accepts:{script:"text/javascript, application/javascript, application/ecmascript, application/x-ecmascript"},contents:{script:/\b(?:java|ecma)script\b/},converters:{"text script":function(text){return jQuery.globalEval(text),text}}}),
// Handle cache's special case and crossDomain
jQuery.ajaxPrefilter("script",(function(s){void 0===s.cache&&(s.cache=!1),s.crossDomain&&(s.type="GET")})),
// Bind script tag hack transport
jQuery.ajaxTransport("script",(function(s){var script,callback;
// This transport only deals with cross domain or forced-by-attrs requests
if(s.crossDomain||s.scriptAttrs)return{send:function(_,complete){script=jQuery("<script>").attr(s.scriptAttrs||{}).prop({charset:s.scriptCharset,src:s.url}).on("load error",callback=function(evt){script.remove(),callback=null,evt&&complete("error"===evt.type?404:200,evt.type)}),
// Use native DOM manipulation to avoid our domManip AJAX trickery
document.head.appendChild(script[0])},abort:function(){callback&&callback()}}}));var oldCallbacks=[],rjsonp=/(=)\?(?=&|$)|\?\?/;
// Default jsonp settings
jQuery.ajaxSetup({jsonp:"callback",jsonpCallback:function(){var callback=oldCallbacks.pop()||jQuery.expando+"_"+nonce.guid++;return this[callback]=!0,callback}}),
// Detect, normalize options and install callbacks for jsonp requests
jQuery.ajaxPrefilter("json jsonp",(function(s,originalSettings,jqXHR){var callbackName,overwritten,responseContainer,jsonProp=!1!==s.jsonp&&(rjsonp.test(s.url)?"url":"string"===typeof s.data&&0===(s.contentType||"").indexOf("application/x-www-form-urlencoded")&&rjsonp.test(s.data)&&"data");
// Handle iff the expected data type is "jsonp" or we have a parameter to set
if(jsonProp||"jsonp"===s.dataTypes[0])
// Delegate to script
// Get callback name, remembering preexisting value associated with it
return callbackName=s.jsonpCallback=isFunction(s.jsonpCallback)?s.jsonpCallback():s.jsonpCallback,
// Insert callback into url or form data
jsonProp?s[jsonProp]=s[jsonProp].replace(rjsonp,"$1"+callbackName):!1!==s.jsonp&&(s.url+=(rquery.test(s.url)?"&":"?")+s.jsonp+"="+callbackName),
// Use data converter to retrieve json after script execution
s.converters["script json"]=function(){return responseContainer||jQuery.error(callbackName+" was not called"),responseContainer[0]},
// Force json dataType
s.dataTypes[0]="json",
// Install callback
overwritten=window[callbackName],window[callbackName]=function(){responseContainer=arguments},
// Clean-up function (fires after converters)
jqXHR.always((function(){
// If previous value didn't exist - remove it
void 0===overwritten?jQuery(window).removeProp(callbackName):window[callbackName]=overwritten,
// Save back as free
s[callbackName]&&(
// Make sure that re-using the options doesn't screw things around
s.jsonpCallback=originalSettings.jsonpCallback,
// Save the callback name for future use
oldCallbacks.push(callbackName)),
// Call if it was a function and we have a response
responseContainer&&isFunction(overwritten)&&overwritten(responseContainer[0]),responseContainer=overwritten=void 0})),"script"})),
// Support: Safari 8 only
// In Safari 8 documents created via document.implementation.createHTMLDocument
// collapse sibling forms: the second one becomes a child of the first one.
// Because of that, this security measure has to be disabled in Safari 8.
// https://bugs.webkit.org/show_bug.cgi?id=137337
support.createHTMLDocument=function(){var body=document.implementation.createHTMLDocument("").body;return body.innerHTML="<form></form><form></form>",2===body.childNodes.length}(),
// Argument "data" should be string of html
// context (optional): If specified, the fragment will be created in this context,
// defaults to document
// keepScripts (optional): If true, will include scripts passed in the html string
jQuery.parseHTML=function(data,context,keepScripts){return"string"!==typeof data?[]:(
// Single tag
"boolean"===typeof context&&(keepScripts=context,context=!1),context||(
// Stop scripts or inline event handlers from being executed immediately
// by using document.implementation
support.createHTMLDocument?(context=document.implementation.createHTMLDocument(""),
// Set the base href for the created document
// so any parsed elements with URLs
// are based on the document's URL (gh-2965)
base=context.createElement("base"),base.href=document.location.href,context.head.appendChild(base)):context=document),parsed=rsingleTag.exec(data),scripts=!keepScripts&&[],parsed?[context.createElement(parsed[1])]:(parsed=buildFragment([data],context,scripts),scripts&&scripts.length&&jQuery(scripts).remove(),jQuery.merge([],parsed.childNodes)));var base,parsed,scripts},
/**
 * Load a url into a page
 */
jQuery.fn.load=function(url,params,callback){var selector,type,response,self=this,off=url.indexOf(" ");return off>-1&&(selector=stripAndCollapse(url.slice(off)),url=url.slice(0,off)),
// If it's a function
isFunction(params)?(
// We assume that it's the callback
callback=params,params=void 0):params&&"object"===typeof params&&(type="POST"),
// If we have elements to modify, make the request
self.length>0&&jQuery.ajax({url:url,
// If "type" variable is undefined, then "GET" method will be used.
// Make value of this field explicit since
// user can override it through ajaxSetup method
type:type||"GET",dataType:"html",data:params}).done((function(responseText){
// Save response for use in complete callback
response=arguments,self.html(selector?
// If a selector was specified, locate the right elements in a dummy div
// Exclude scripts to avoid IE 'Permission Denied' errors
jQuery("<div>").append(jQuery.parseHTML(responseText)).find(selector):
// Otherwise use the full result
responseText)})).always(callback&&function(jqXHR,status){self.each((function(){callback.apply(this,response||[jqXHR.responseText,status,jqXHR])}))}),this},jQuery.expr.pseudos.animated=function(elem){return jQuery.grep(jQuery.timers,(function(fn){return elem===fn.elem})).length},jQuery.offset={setOffset:function(elem,options,i){var curPosition,curLeft,curCSSTop,curTop,curOffset,curCSSLeft,calculatePosition,position=jQuery.css(elem,"position"),curElem=jQuery(elem),props={};
// Set position first, in-case top/left are set even on static elem
"static"===position&&(elem.style.position="relative"),curOffset=curElem.offset(),curCSSTop=jQuery.css(elem,"top"),curCSSLeft=jQuery.css(elem,"left"),calculatePosition=("absolute"===position||"fixed"===position)&&(curCSSTop+curCSSLeft).indexOf("auto")>-1,
// Need to be able to calculate position if either
// top or left is auto and position is either absolute or fixed
calculatePosition?(curPosition=curElem.position(),curTop=curPosition.top,curLeft=curPosition.left):(curTop=parseFloat(curCSSTop)||0,curLeft=parseFloat(curCSSLeft)||0),isFunction(options)&&(
// Use jQuery.extend here to allow modification of coordinates argument (gh-1848)
options=options.call(elem,i,jQuery.extend({},curOffset))),null!=options.top&&(props.top=options.top-curOffset.top+curTop),null!=options.left&&(props.left=options.left-curOffset.left+curLeft),"using"in options?options.using.call(elem,props):curElem.css(props)}},jQuery.fn.extend({
// offset() relates an element's border box to the document origin
offset:function(options){
// Preserve chaining for setter
if(arguments.length)return void 0===options?this:this.each((function(i){jQuery.offset.setOffset(this,options,i)}));var rect,win,elem=this[0];return elem?
// Return zeros for disconnected and hidden (display: none) elements (gh-2310)
// Support: IE <=11 only
// Running getBoundingClientRect on a
// disconnected node in IE throws an error
elem.getClientRects().length?(
// Get document-relative position by adding viewport scroll to viewport-relative gBCR
rect=elem.getBoundingClientRect(),win=elem.ownerDocument.defaultView,{top:rect.top+win.pageYOffset,left:rect.left+win.pageXOffset}):{top:0,left:0}:void 0},
// position() relates an element's margin box to its offset parent's padding box
// This corresponds to the behavior of CSS absolute positioning
position:function(){if(this[0]){var offsetParent,offset,doc,elem=this[0],parentOffset={top:0,left:0};
// position:fixed elements are offset from the viewport, which itself always has zero offset
if("fixed"===jQuery.css(elem,"position"))
// Assume position:fixed implies availability of getBoundingClientRect
offset=elem.getBoundingClientRect();else{offset=this.offset(),
// Account for the *real* offset parent, which can be the document or its root element
// when a statically positioned element is identified
doc=elem.ownerDocument,offsetParent=elem.offsetParent||doc.documentElement;while(offsetParent&&(offsetParent===doc.body||offsetParent===doc.documentElement)&&"static"===jQuery.css(offsetParent,"position"))offsetParent=offsetParent.parentNode;offsetParent&&offsetParent!==elem&&1===offsetParent.nodeType&&(
// Incorporate borders into its offset, since they are outside its content origin
parentOffset=jQuery(offsetParent).offset(),parentOffset.top+=jQuery.css(offsetParent,"borderTopWidth",!0),parentOffset.left+=jQuery.css(offsetParent,"borderLeftWidth",!0))}
// Subtract parent offsets and element margins
return{top:offset.top-parentOffset.top-jQuery.css(elem,"marginTop",!0),left:offset.left-parentOffset.left-jQuery.css(elem,"marginLeft",!0)}}},
// This method will return documentElement in the following cases:
// 1) For the element inside the iframe without offsetParent, this method will return
//    documentElement of the parent window
// 2) For the hidden or detached element
// 3) For body or html element, i.e. in case of the html node - it will return itself
// but those exceptions were never presented as a real life use-cases
// and might be considered as more preferable results.
// This logic, however, is not guaranteed and can change at any point in the future
offsetParent:function(){return this.map((function(){var offsetParent=this.offsetParent;while(offsetParent&&"static"===jQuery.css(offsetParent,"position"))offsetParent=offsetParent.offsetParent;return offsetParent||documentElement}))}}),
// Create scrollLeft and scrollTop methods
jQuery.each({scrollLeft:"pageXOffset",scrollTop:"pageYOffset"},(function(method,prop){var top="pageYOffset"===prop;jQuery.fn[method]=function(val){return access(this,(function(elem,method,val){
// Coalesce documents and windows
var win;if(isWindow(elem)?win=elem:9===elem.nodeType&&(win=elem.defaultView),void 0===val)return win?win[prop]:elem[method];win?win.scrollTo(top?win.pageXOffset:val,top?val:win.pageYOffset):elem[method]=val}),method,val,arguments.length)}})),
// Support: Safari <=7 - 9.1, Chrome <=37 - 49
// Add the top/left cssHooks using jQuery.fn.position
// Webkit bug: https://bugs.webkit.org/show_bug.cgi?id=29084
// Blink bug: https://bugs.chromium.org/p/chromium/issues/detail?id=589347
// getComputedStyle returns percent when specified for top/left/bottom/right;
// rather than make the css module depend on the offset module, just check for it here
jQuery.each(["top","left"],(function(_i,prop){jQuery.cssHooks[prop]=addGetHookIf(support.pixelPosition,(function(elem,computed){if(computed)
// If curCSS returns percentage, fallback to offset
return computed=curCSS(elem,prop),rnumnonpx.test(computed)?jQuery(elem).position()[prop]+"px":computed}))})),
// Create innerHeight, innerWidth, height, width, outerHeight and outerWidth methods
jQuery.each({Height:"height",Width:"width"},(function(name,type){jQuery.each({padding:"inner"+name,content:type,"":"outer"+name},(function(defaultExtra,funcName){
// Margin is only for outerHeight, outerWidth
jQuery.fn[funcName]=function(margin,value){var chainable=arguments.length&&(defaultExtra||"boolean"!==typeof margin),extra=defaultExtra||(!0===margin||!0===value?"margin":"border");return access(this,(function(elem,type,value){var doc;return isWindow(elem)?0===funcName.indexOf("outer")?elem["inner"+name]:elem.document.documentElement["client"+name]:
// Get document width or height
9===elem.nodeType?(doc=elem.documentElement,Math.max(elem.body["scroll"+name],doc["scroll"+name],elem.body["offset"+name],doc["offset"+name],doc["client"+name])):void 0===value?
// Get width or height on the element, requesting but not forcing parseFloat
jQuery.css(elem,type,extra):
// Set width or height on the element
jQuery.style(elem,type,value,extra)}),type,chainable?margin:void 0,chainable)}}))})),jQuery.each(["ajaxStart","ajaxStop","ajaxComplete","ajaxError","ajaxSuccess","ajaxSend"],(function(_i,type){jQuery.fn[type]=function(fn){return this.on(type,fn)}})),jQuery.fn.extend({bind:function(types,data,fn){return this.on(types,null,data,fn)},unbind:function(types,fn){return this.off(types,null,fn)},delegate:function(selector,types,data,fn){return this.on(types,selector,data,fn)},undelegate:function(selector,types,fn){
// ( namespace ) or ( selector, types [, fn] )
return 1===arguments.length?this.off(selector,"**"):this.off(types,selector||"**",fn)},hover:function(fnOver,fnOut){return this.on("mouseenter",fnOver).on("mouseleave",fnOut||fnOver)}}),jQuery.each("blur focus focusin focusout resize scroll click dblclick mousedown mouseup mousemove mouseover mouseout mouseenter mouseleave change select submit keydown keypress keyup contextmenu".split(" "),(function(_i,name){
// Handle event binding
jQuery.fn[name]=function(data,fn){return arguments.length>0?this.on(name,null,data,fn):this.trigger(name)}}));
// Support: Android <=4.0 only
// Make sure we trim BOM and NBSP
// Require that the "whitespace run" starts from a non-whitespace
// to avoid O(N^2) behavior when the engine would try matching "\s+$" at each space position.
var rtrim=/^[\s\uFEFF\xA0]+|([^\s\uFEFF\xA0])[\s\uFEFF\xA0]+$/g;
// Bind a function to a context, optionally partially applying any
// arguments.
// jQuery.proxy is deprecated to promote standards (specifically Function#bind)
// However, it is not slated for removal any time soon
jQuery.proxy=function(fn,context){var tmp,args,proxy;
// Quick check to determine if target is callable, in the spec
// this throws a TypeError, but we will just return undefined.
if("string"===typeof context&&(tmp=fn[context],context=fn,fn=tmp),isFunction(fn))
// Simulated bind
return args=slice.call(arguments,2),proxy=function(){return fn.apply(context||this,args.concat(slice.call(arguments)))},
// Set the guid of unique handler to the same of original handler, so it can be removed
proxy.guid=fn.guid=fn.guid||jQuery.guid++,proxy},jQuery.holdReady=function(hold){hold?jQuery.readyWait++:jQuery.ready(!0)},jQuery.isArray=Array.isArray,jQuery.parseJSON=JSON.parse,jQuery.nodeName=nodeName,jQuery.isFunction=isFunction,jQuery.isWindow=isWindow,jQuery.camelCase=camelCase,jQuery.type=toType,jQuery.now=Date.now,jQuery.isNumeric=function(obj){
// As of jQuery 3.0, isNumeric is limited to
// strings and numbers (primitives or objects)
// that can be coerced to finite numbers (gh-2662)
var type=jQuery.type(obj);return("number"===type||"string"===type)&&
// parseFloat NaNs numeric-cast false positives ("")
// ...but misinterprets leading-number strings, particularly hex literals ("0x...")
// subtraction forces infinities to NaN
!isNaN(obj-parseFloat(obj))},jQuery.trim=function(text){return null==text?"":(text+"").replace(rtrim,"$1")},__WEBPACK_AMD_DEFINE_ARRAY__=[],__WEBPACK_AMD_DEFINE_RESULT__=function(){return jQuery}.apply(exports,__WEBPACK_AMD_DEFINE_ARRAY__),void 0===__WEBPACK_AMD_DEFINE_RESULT__||(module.exports=__WEBPACK_AMD_DEFINE_RESULT__);var
// Map over jQuery in case of overwrite
_jQuery=window.jQuery,
// Map over the $ in case of overwrite
_$=window.$;return jQuery.noConflict=function(deep){return window.$===jQuery&&(window.$=_$),deep&&window.jQuery===jQuery&&(window.jQuery=_jQuery),jQuery},
// Expose jQuery and $ identifiers, even in AMD
// (trac-7102#comment:10, https://github.com/jquery/jquery/pull/557)
// and CommonJS for browser emulators (trac-13566)
"undefined"===typeof noGlobal&&(window.jQuery=window.$=jQuery),jQuery}))},
/***/255733:
/***/function(module,__unused_webpack_exports,__webpack_require__){
/*!

JSZip v3.10.1 - A JavaScript class for generating and reading zip files
<http://stuartk.com/jszip>

(c) 2009-2016 Stuart Knightley <stuart [at] stuartk.com>
Dual licenced under the MIT license or GPLv3. See https://raw.github.com/Stuk/jszip/main/LICENSE.markdown.

JSZip uses the library pako released under the MIT license :
https://github.com/nodeca/pako/blob/main/LICENSE
*/
!function(e){module.exports=e()}((function(){return function s(a,o,h){function u(r,e){if(!o[r]){if(!a[r]){var t=void 0;if(!e&&t)return require(r,!0);if(l)return l(r,!0);var n=new Error("Cannot find module '"+r+"'");throw n.code="MODULE_NOT_FOUND",n}var i=o[r]={exports:{}};a[r][0].call(i.exports,(function(e){var t=a[r][1][e];return u(t||e)}),i,i.exports,s,a,o,h)}return o[r].exports}for(var l=void 0,e=0;e<h.length;e++)u(h[e]);return u}({1:[function(e,t,r){"use strict";var d=e("./utils"),c=e("./support"),p="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";r.encode=function(e){for(var t,r,n,i,s,a,o,h=[],u=0,l=e.length,f=l,c="string"!==d.getTypeOf(e);u<e.length;)f=l-u,n=c?(t=e[u++],r=u<l?e[u++]:0,u<l?e[u++]:0):(t=e.charCodeAt(u++),r=u<l?e.charCodeAt(u++):0,u<l?e.charCodeAt(u++):0),i=t>>2,s=(3&t)<<4|r>>4,a=1<f?(15&r)<<2|n>>6:64,o=2<f?63&n:64,h.push(p.charAt(i)+p.charAt(s)+p.charAt(a)+p.charAt(o));return h.join("")},r.decode=function(e){var t,r,n,i,s,a,o=0,h=0,u="data:";if(e.substr(0,u.length)===u)throw new Error("Invalid base64 input, it looks like a data url.");var l,f=3*(e=e.replace(/[^A-Za-z0-9+/=]/g,"")).length/4;if(e.charAt(e.length-1)===p.charAt(64)&&f--,e.charAt(e.length-2)===p.charAt(64)&&f--,f%1!=0)throw new Error("Invalid base64 input, bad content length.");for(l=c.uint8array?new Uint8Array(0|f):new Array(0|f);o<e.length;)t=p.indexOf(e.charAt(o++))<<2|(i=p.indexOf(e.charAt(o++)))>>4,r=(15&i)<<4|(s=p.indexOf(e.charAt(o++)))>>2,n=(3&s)<<6|(a=p.indexOf(e.charAt(o++))),l[h++]=t,64!==s&&(l[h++]=r),64!==a&&(l[h++]=n);return l}},{"./support":30,"./utils":32}],2:[function(e,t,r){"use strict";var n=e("./external"),i=e("./stream/DataWorker"),s=e("./stream/Crc32Probe"),a=e("./stream/DataLengthProbe");function o(e,t,r,n,i){this.compressedSize=e,this.uncompressedSize=t,this.crc32=r,this.compression=n,this.compressedContent=i}o.prototype={getContentWorker:function(){var e=new i(n.Promise.resolve(this.compressedContent)).pipe(this.compression.uncompressWorker()).pipe(new a("data_length")),t=this;return e.on("end",(function(){if(this.streamInfo.data_length!==t.uncompressedSize)throw new Error("Bug : uncompressed data size mismatch")})),e},getCompressedWorker:function(){return new i(n.Promise.resolve(this.compressedContent)).withStreamInfo("compressedSize",this.compressedSize).withStreamInfo("uncompressedSize",this.uncompressedSize).withStreamInfo("crc32",this.crc32).withStreamInfo("compression",this.compression)}},o.createWorkerFrom=function(e,t,r){return e.pipe(new s).pipe(new a("uncompressedSize")).pipe(t.compressWorker(r)).pipe(new a("compressedSize")).withStreamInfo("compression",t)},t.exports=o},{"./external":6,"./stream/Crc32Probe":25,"./stream/DataLengthProbe":26,"./stream/DataWorker":27}],3:[function(e,t,r){"use strict";var n=e("./stream/GenericWorker");r.STORE={magic:"\0\0",compressWorker:function(){return new n("STORE compression")},uncompressWorker:function(){return new n("STORE decompression")}},r.DEFLATE=e("./flate")},{"./flate":7,"./stream/GenericWorker":28}],4:[function(e,t,r){"use strict";var n=e("./utils"),o=function(){for(var e,t=[],r=0;r<256;r++){e=r;for(var n=0;n<8;n++)e=1&e?3988292384^e>>>1:e>>>1;t[r]=e}return t}();t.exports=function(e,t){return void 0!==e&&e.length?"string"!==n.getTypeOf(e)?function(e,t,r,n){var i=o,s=n+r;e^=-1;for(var a=n;a<s;a++)e=e>>>8^i[255&(e^t[a])];return-1^e}(0|t,e,e.length,0):function(e,t,r,n){var i=o,s=n+r;e^=-1;for(var a=n;a<s;a++)e=e>>>8^i[255&(e^t.charCodeAt(a))];return-1^e}(0|t,e,e.length,0):0}},{"./utils":32}],5:[function(e,t,r){"use strict";r.base64=!1,r.binary=!1,r.dir=!1,r.createFolders=!0,r.date=null,r.compression=null,r.compressionOptions=null,r.comment=null,r.unixPermissions=null,r.dosPermissions=null},{}],6:[function(e,t,r){"use strict";var n=null;n="undefined"!=typeof Promise?Promise:e("lie"),t.exports={Promise:n}},{lie:37}],7:[function(e,t,r){"use strict";var n="undefined"!=typeof Uint8Array&&"undefined"!=typeof Uint16Array&&"undefined"!=typeof Uint32Array,i=e("pako"),s=e("./utils"),a=e("./stream/GenericWorker"),o=n?"uint8array":"array";function h(e,t){a.call(this,"FlateWorker/"+e),this._pako=null,this._pakoAction=e,this._pakoOptions=t,this.meta={}}r.magic="\b\0",s.inherits(h,a),h.prototype.processChunk=function(e){this.meta=e.meta,null===this._pako&&this._createPako(),this._pako.push(s.transformTo(o,e.data),!1)},h.prototype.flush=function(){a.prototype.flush.call(this),null===this._pako&&this._createPako(),this._pako.push([],!0)},h.prototype.cleanUp=function(){a.prototype.cleanUp.call(this),this._pako=null},h.prototype._createPako=function(){this._pako=new i[this._pakoAction]({raw:!0,level:this._pakoOptions.level||-1});var t=this;this._pako.onData=function(e){t.push({data:e,meta:t.meta})}},r.compressWorker=function(e){return new h("Deflate",e)},r.uncompressWorker=function(){return new h("Inflate",{})}},{"./stream/GenericWorker":28,"./utils":32,pako:38}],8:[function(e,t,r){"use strict";function A(e,t){var r,n="";for(r=0;r<t;r++)n+=String.fromCharCode(255&e),e>>>=8;return n}function n(e,t,r,n,i,s){var a,o,h=e.file,u=e.compression,l=s!==O.utf8encode,f=I.transformTo("string",s(h.name)),c=I.transformTo("string",O.utf8encode(h.name)),d=h.comment,p=I.transformTo("string",s(d)),m=I.transformTo("string",O.utf8encode(d)),_=c.length!==h.name.length,g=m.length!==d.length,b="",v="",y="",w=h.dir,k=h.date,x={crc32:0,compressedSize:0,uncompressedSize:0};t&&!r||(x.crc32=e.crc32,x.compressedSize=e.compressedSize,x.uncompressedSize=e.uncompressedSize);var S=0;t&&(S|=8),l||!_&&!g||(S|=2048);var z=0,C=0;w&&(z|=16),"UNIX"===i?(C=798,z|=function(e,t){var r=e;return e||(r=t?16893:33204),(65535&r)<<16}(h.unixPermissions,w)):(C=20,z|=function(e){return 63&(e||0)}(h.dosPermissions)),a=k.getUTCHours(),a<<=6,a|=k.getUTCMinutes(),a<<=5,a|=k.getUTCSeconds()/2,o=k.getUTCFullYear()-1980,o<<=4,o|=k.getUTCMonth()+1,o<<=5,o|=k.getUTCDate(),_&&(v=A(1,1)+A(B(f),4)+c,b+="up"+A(v.length,2)+v),g&&(y=A(1,1)+A(B(p),4)+m,b+="uc"+A(y.length,2)+y);var E="";return E+="\n\0",E+=A(S,2),E+=u.magic,E+=A(a,2),E+=A(o,2),E+=A(x.crc32,4),E+=A(x.compressedSize,4),E+=A(x.uncompressedSize,4),E+=A(f.length,2),E+=A(b.length,2),{fileRecord:R.LOCAL_FILE_HEADER+E+f+b,dirRecord:R.CENTRAL_FILE_HEADER+A(C,2)+E+A(p.length,2)+"\0\0\0\0"+A(z,4)+A(n,4)+f+b+p}}var I=e("../utils"),i=e("../stream/GenericWorker"),O=e("../utf8"),B=e("../crc32"),R=e("../signature");function s(e,t,r,n){i.call(this,"ZipFileWorker"),this.bytesWritten=0,this.zipComment=t,this.zipPlatform=r,this.encodeFileName=n,this.streamFiles=e,this.accumulate=!1,this.contentBuffer=[],this.dirRecords=[],this.currentSourceOffset=0,this.entriesCount=0,this.currentFile=null,this._sources=[]}I.inherits(s,i),s.prototype.push=function(e){var t=e.meta.percent||0,r=this.entriesCount,n=this._sources.length;this.accumulate?this.contentBuffer.push(e):(this.bytesWritten+=e.data.length,i.prototype.push.call(this,{data:e.data,meta:{currentFile:this.currentFile,percent:r?(t+100*(r-n-1))/r:100}}))},s.prototype.openedSource=function(e){this.currentSourceOffset=this.bytesWritten,this.currentFile=e.file.name;var t=this.streamFiles&&!e.file.dir;if(t){var r=n(e,t,!1,this.currentSourceOffset,this.zipPlatform,this.encodeFileName);this.push({data:r.fileRecord,meta:{percent:0}})}else this.accumulate=!0},s.prototype.closedSource=function(e){this.accumulate=!1;var t=this.streamFiles&&!e.file.dir,r=n(e,t,!0,this.currentSourceOffset,this.zipPlatform,this.encodeFileName);if(this.dirRecords.push(r.dirRecord),t)this.push({data:function(e){return R.DATA_DESCRIPTOR+A(e.crc32,4)+A(e.compressedSize,4)+A(e.uncompressedSize,4)}(e),meta:{percent:100}});else for(this.push({data:r.fileRecord,meta:{percent:0}});this.contentBuffer.length;)this.push(this.contentBuffer.shift());this.currentFile=null},s.prototype.flush=function(){for(var e=this.bytesWritten,t=0;t<this.dirRecords.length;t++)this.push({data:this.dirRecords[t],meta:{percent:100}});var r=this.bytesWritten-e,n=function(e,t,r,n,i){var s=I.transformTo("string",i(n));return R.CENTRAL_DIRECTORY_END+"\0\0\0\0"+A(e,2)+A(e,2)+A(t,4)+A(r,4)+A(s.length,2)+s}(this.dirRecords.length,r,e,this.zipComment,this.encodeFileName);this.push({data:n,meta:{percent:100}})},s.prototype.prepareNextSource=function(){this.previous=this._sources.shift(),this.openedSource(this.previous.streamInfo),this.isPaused?this.previous.pause():this.previous.resume()},s.prototype.registerPrevious=function(e){this._sources.push(e);var t=this;return e.on("data",(function(e){t.processChunk(e)})),e.on("end",(function(){t.closedSource(t.previous.streamInfo),t._sources.length?t.prepareNextSource():t.end()})),e.on("error",(function(e){t.error(e)})),this},s.prototype.resume=function(){return!!i.prototype.resume.call(this)&&(!this.previous&&this._sources.length?(this.prepareNextSource(),!0):this.previous||this._sources.length||this.generatedError?void 0:(this.end(),!0))},s.prototype.error=function(e){var t=this._sources;if(!i.prototype.error.call(this,e))return!1;for(var r=0;r<t.length;r++)try{t[r].error(e)}catch(e){}return!0},s.prototype.lock=function(){i.prototype.lock.call(this);for(var e=this._sources,t=0;t<e.length;t++)e[t].lock()},t.exports=s},{"../crc32":4,"../signature":23,"../stream/GenericWorker":28,"../utf8":31,"../utils":32}],9:[function(e,t,r){"use strict";var u=e("../compressions"),n=e("./ZipFileWorker");r.generateWorker=function(e,a,t){var o=new n(a.streamFiles,t,a.platform,a.encodeFileName),h=0;try{e.forEach((function(e,t){h++;var r=function(e,t){var r=e||t,n=u[r];if(!n)throw new Error(r+" is not a valid compression method !");return n}(t.options.compression,a.compression),n=t.options.compressionOptions||a.compressionOptions||{},i=t.dir,s=t.date;t._compressWorker(r,n).withStreamInfo("file",{name:e,dir:i,date:s,comment:t.comment||"",unixPermissions:t.unixPermissions,dosPermissions:t.dosPermissions}).pipe(o)})),o.entriesCount=h}catch(e){o.error(e)}return o}},{"../compressions":3,"./ZipFileWorker":8}],10:[function(e,t,r){"use strict";function n(){if(!(this instanceof n))return new n;if(arguments.length)throw new Error("The constructor with parameters has been removed in JSZip 3.0, please check the upgrade guide.");this.files=Object.create(null),this.comment=null,this.root="",this.clone=function(){var e=new n;for(var t in this)"function"!=typeof this[t]&&(e[t]=this[t]);return e}}(n.prototype=e("./object")).loadAsync=e("./load"),n.support=e("./support"),n.defaults=e("./defaults"),n.version="3.10.1",n.loadAsync=function(e,t){return(new n).loadAsync(e,t)},n.external=e("./external"),t.exports=n},{"./defaults":5,"./external":6,"./load":11,"./object":15,"./support":30}],11:[function(e,t,r){"use strict";var u=e("./utils"),i=e("./external"),n=e("./utf8"),s=e("./zipEntries"),a=e("./stream/Crc32Probe"),l=e("./nodejsUtils");function f(n){return new i.Promise((function(e,t){var r=n.decompressed.getContentWorker().pipe(new a);r.on("error",(function(e){t(e)})).on("end",(function(){r.streamInfo.crc32!==n.decompressed.crc32?t(new Error("Corrupted zip : CRC32 mismatch")):e()})).resume()}))}t.exports=function(e,o){var h=this;return o=u.extend(o||{},{base64:!1,checkCRC32:!1,optimizedBinaryString:!1,createFolders:!1,decodeFileName:n.utf8decode}),l.isNode&&l.isStream(e)?i.Promise.reject(new Error("JSZip can't accept a stream when loading a zip file.")):u.prepareContent("the loaded zip file",e,!0,o.optimizedBinaryString,o.base64).then((function(e){var t=new s(o);return t.load(e),t})).then((function(e){var t=[i.Promise.resolve(e)],r=e.files;if(o.checkCRC32)for(var n=0;n<r.length;n++)t.push(f(r[n]));return i.Promise.all(t)})).then((function(e){for(var t=e.shift(),r=t.files,n=0;n<r.length;n++){var i=r[n],s=i.fileNameStr,a=u.resolve(i.fileNameStr);h.file(a,i.decompressed,{binary:!0,optimizedBinaryString:!0,date:i.date,dir:i.dir,comment:i.fileCommentStr.length?i.fileCommentStr:null,unixPermissions:i.unixPermissions,dosPermissions:i.dosPermissions,createFolders:o.createFolders}),i.dir||(h.file(a).unsafeOriginalName=s)}return t.zipComment.length&&(h.comment=t.zipComment),h}))}},{"./external":6,"./nodejsUtils":14,"./stream/Crc32Probe":25,"./utf8":31,"./utils":32,"./zipEntries":33}],12:[function(e,t,r){"use strict";var n=e("../utils"),i=e("../stream/GenericWorker");function s(e,t){i.call(this,"Nodejs stream input adapter for "+e),this._upstreamEnded=!1,this._bindStream(t)}n.inherits(s,i),s.prototype._bindStream=function(e){var t=this;(this._stream=e).pause(),e.on("data",(function(e){t.push({data:e,meta:{percent:0}})})).on("error",(function(e){t.isPaused?this.generatedError=e:t.error(e)})).on("end",(function(){t.isPaused?t._upstreamEnded=!0:t.end()}))},s.prototype.pause=function(){return!!i.prototype.pause.call(this)&&(this._stream.pause(),!0)},s.prototype.resume=function(){return!!i.prototype.resume.call(this)&&(this._upstreamEnded?this.end():this._stream.resume(),!0)},t.exports=s},{"../stream/GenericWorker":28,"../utils":32}],13:[function(e,t,r){"use strict";var i=e("readable-stream").Readable;function n(e,t,r){i.call(this,t),this._helper=e;var n=this;e.on("data",(function(e,t){n.push(e)||n._helper.pause(),r&&r(t)})).on("error",(function(e){n.emit("error",e)})).on("end",(function(){n.push(null)}))}e("../utils").inherits(n,i),n.prototype._read=function(){this._helper.resume()},t.exports=n},{"../utils":32,"readable-stream":16}],14:[function(e,t,r){"use strict";t.exports={isNode:"undefined"!=typeof Buffer,newBufferFrom:function(e,t){if(Buffer.from&&Buffer.from!==Uint8Array.from)return Buffer.from(e,t);if("number"==typeof e)throw new Error('The "data" argument must not be a number');return new Buffer(e,t)},allocBuffer:function(e){if(Buffer.alloc)return Buffer.alloc(e);var t=new Buffer(e);return t.fill(0),t},isBuffer:function(e){return Buffer.isBuffer(e)},isStream:function(e){return e&&"function"==typeof e.on&&"function"==typeof e.pause&&"function"==typeof e.resume}}},{}],15:[function(e,t,r){"use strict";function s(e,t,r){var n,i=u.getTypeOf(t),s=u.extend(r||{},f);s.date=s.date||new Date,null!==s.compression&&(s.compression=s.compression.toUpperCase()),"string"==typeof s.unixPermissions&&(s.unixPermissions=parseInt(s.unixPermissions,8)),s.unixPermissions&&16384&s.unixPermissions&&(s.dir=!0),s.dosPermissions&&16&s.dosPermissions&&(s.dir=!0),s.dir&&(e=g(e)),s.createFolders&&(n=_(e))&&b.call(this,n,!0);var a="string"===i&&!1===s.binary&&!1===s.base64;r&&void 0!==r.binary||(s.binary=!a),(t instanceof c&&0===t.uncompressedSize||s.dir||!t||0===t.length)&&(s.base64=!1,s.binary=!0,t="",s.compression="STORE",i="string");var o=null;o=t instanceof c||t instanceof l?t:p.isNode&&p.isStream(t)?new m(e,t):u.prepareContent(e,t,s.binary,s.optimizedBinaryString,s.base64);var h=new d(e,o,s);this.files[e]=h}var i=e("./utf8"),u=e("./utils"),l=e("./stream/GenericWorker"),a=e("./stream/StreamHelper"),f=e("./defaults"),c=e("./compressedObject"),d=e("./zipObject"),o=e("./generate"),p=e("./nodejsUtils"),m=e("./nodejs/NodejsStreamInputAdapter"),_=function(e){"/"===e.slice(-1)&&(e=e.substring(0,e.length-1));var t=e.lastIndexOf("/");return 0<t?e.substring(0,t):""},g=function(e){return"/"!==e.slice(-1)&&(e+="/"),e},b=function(e,t){return t=void 0!==t?t:f.createFolders,e=g(e),this.files[e]||s.call(this,e,null,{dir:!0,createFolders:t}),this.files[e]};function h(e){return"[object RegExp]"===Object.prototype.toString.call(e)}var n={load:function(){throw new Error("This method has been removed in JSZip 3.0, please check the upgrade guide.")},forEach:function(e){var t,r,n;for(t in this.files)n=this.files[t],(r=t.slice(this.root.length,t.length))&&t.slice(0,this.root.length)===this.root&&e(r,n)},filter:function(r){var n=[];return this.forEach((function(e,t){r(e,t)&&n.push(t)})),n},file:function(e,t,r){if(1!==arguments.length)return e=this.root+e,s.call(this,e,t,r),this;if(h(e)){var n=e;return this.filter((function(e,t){return!t.dir&&n.test(e)}))}var i=this.files[this.root+e];return i&&!i.dir?i:null},folder:function(r){if(!r)return this;if(h(r))return this.filter((function(e,t){return t.dir&&r.test(e)}));var e=this.root+r,t=b.call(this,e),n=this.clone();return n.root=t.name,n},remove:function(r){r=this.root+r;var e=this.files[r];if(e||("/"!==r.slice(-1)&&(r+="/"),e=this.files[r]),e&&!e.dir)delete this.files[r];else for(var t=this.filter((function(e,t){return t.name.slice(0,r.length)===r})),n=0;n<t.length;n++)delete this.files[t[n].name];return this},generate:function(){throw new Error("This method has been removed in JSZip 3.0, please check the upgrade guide.")},generateInternalStream:function(e){var t,r={};try{if((r=u.extend(e||{},{streamFiles:!1,compression:"STORE",compressionOptions:null,type:"",platform:"DOS",comment:null,mimeType:"application/zip",encodeFileName:i.utf8encode})).type=r.type.toLowerCase(),r.compression=r.compression.toUpperCase(),"binarystring"===r.type&&(r.type="string"),!r.type)throw new Error("No output type specified.");u.checkSupport(r.type),"darwin"!==r.platform&&"freebsd"!==r.platform&&"linux"!==r.platform&&"sunos"!==r.platform||(r.platform="UNIX"),"win32"===r.platform&&(r.platform="DOS");var n=r.comment||this.comment||"";t=o.generateWorker(this,r,n)}catch(e){(t=new l("error")).error(e)}return new a(t,r.type||"string",r.mimeType)},generateAsync:function(e,t){return this.generateInternalStream(e).accumulate(t)},generateNodeStream:function(e,t){return(e=e||{}).type||(e.type="nodebuffer"),this.generateInternalStream(e).toNodejsStream(t)}};t.exports=n},{"./compressedObject":2,"./defaults":5,"./generate":9,"./nodejs/NodejsStreamInputAdapter":12,"./nodejsUtils":14,"./stream/GenericWorker":28,"./stream/StreamHelper":29,"./utf8":31,"./utils":32,"./zipObject":35}],16:[function(e,t,r){"use strict";t.exports=e("stream")},{stream:void 0}],17:[function(e,t,r){"use strict";var n=e("./DataReader");function i(e){n.call(this,e);for(var t=0;t<this.data.length;t++)e[t]=255&e[t]}e("../utils").inherits(i,n),i.prototype.byteAt=function(e){return this.data[this.zero+e]},i.prototype.lastIndexOfSignature=function(e){for(var t=e.charCodeAt(0),r=e.charCodeAt(1),n=e.charCodeAt(2),i=e.charCodeAt(3),s=this.length-4;0<=s;--s)if(this.data[s]===t&&this.data[s+1]===r&&this.data[s+2]===n&&this.data[s+3]===i)return s-this.zero;return-1},i.prototype.readAndCheckSignature=function(e){var t=e.charCodeAt(0),r=e.charCodeAt(1),n=e.charCodeAt(2),i=e.charCodeAt(3),s=this.readData(4);return t===s[0]&&r===s[1]&&n===s[2]&&i===s[3]},i.prototype.readData=function(e){if(this.checkOffset(e),0===e)return[];var t=this.data.slice(this.zero+this.index,this.zero+this.index+e);return this.index+=e,t},t.exports=i},{"../utils":32,"./DataReader":18}],18:[function(e,t,r){"use strict";var n=e("../utils");function i(e){this.data=e,this.length=e.length,this.index=0,this.zero=0}i.prototype={checkOffset:function(e){this.checkIndex(this.index+e)},checkIndex:function(e){if(this.length<this.zero+e||e<0)throw new Error("End of data reached (data length = "+this.length+", asked index = "+e+"). Corrupted zip ?")},setIndex:function(e){this.checkIndex(e),this.index=e},skip:function(e){this.setIndex(this.index+e)},byteAt:function(){},readInt:function(e){var t,r=0;for(this.checkOffset(e),t=this.index+e-1;t>=this.index;t--)r=(r<<8)+this.byteAt(t);return this.index+=e,r},readString:function(e){return n.transformTo("string",this.readData(e))},readData:function(){},lastIndexOfSignature:function(){},readAndCheckSignature:function(){},readDate:function(){var e=this.readInt(4);return new Date(Date.UTC(1980+(e>>25&127),(e>>21&15)-1,e>>16&31,e>>11&31,e>>5&63,(31&e)<<1))}},t.exports=i},{"../utils":32}],19:[function(e,t,r){"use strict";var n=e("./Uint8ArrayReader");function i(e){n.call(this,e)}e("../utils").inherits(i,n),i.prototype.readData=function(e){this.checkOffset(e);var t=this.data.slice(this.zero+this.index,this.zero+this.index+e);return this.index+=e,t},t.exports=i},{"../utils":32,"./Uint8ArrayReader":21}],20:[function(e,t,r){"use strict";var n=e("./DataReader");function i(e){n.call(this,e)}e("../utils").inherits(i,n),i.prototype.byteAt=function(e){return this.data.charCodeAt(this.zero+e)},i.prototype.lastIndexOfSignature=function(e){return this.data.lastIndexOf(e)-this.zero},i.prototype.readAndCheckSignature=function(e){return e===this.readData(4)},i.prototype.readData=function(e){this.checkOffset(e);var t=this.data.slice(this.zero+this.index,this.zero+this.index+e);return this.index+=e,t},t.exports=i},{"../utils":32,"./DataReader":18}],21:[function(e,t,r){"use strict";var n=e("./ArrayReader");function i(e){n.call(this,e)}e("../utils").inherits(i,n),i.prototype.readData=function(e){if(this.checkOffset(e),0===e)return new Uint8Array(0);var t=this.data.subarray(this.zero+this.index,this.zero+this.index+e);return this.index+=e,t},t.exports=i},{"../utils":32,"./ArrayReader":17}],22:[function(e,t,r){"use strict";var n=e("../utils"),i=e("../support"),s=e("./ArrayReader"),a=e("./StringReader"),o=e("./NodeBufferReader"),h=e("./Uint8ArrayReader");t.exports=function(e){var t=n.getTypeOf(e);return n.checkSupport(t),"string"!==t||i.uint8array?"nodebuffer"===t?new o(e):i.uint8array?new h(n.transformTo("uint8array",e)):new s(n.transformTo("array",e)):new a(e)}},{"../support":30,"../utils":32,"./ArrayReader":17,"./NodeBufferReader":19,"./StringReader":20,"./Uint8ArrayReader":21}],23:[function(e,t,r){"use strict";r.LOCAL_FILE_HEADER="PK",r.CENTRAL_FILE_HEADER="PK",r.CENTRAL_DIRECTORY_END="PK",r.ZIP64_CENTRAL_DIRECTORY_LOCATOR="PK",r.ZIP64_CENTRAL_DIRECTORY_END="PK",r.DATA_DESCRIPTOR="PK\b"},{}],24:[function(e,t,r){"use strict";var n=e("./GenericWorker"),i=e("../utils");function s(e){n.call(this,"ConvertWorker to "+e),this.destType=e}i.inherits(s,n),s.prototype.processChunk=function(e){this.push({data:i.transformTo(this.destType,e.data),meta:e.meta})},t.exports=s},{"../utils":32,"./GenericWorker":28}],25:[function(e,t,r){"use strict";var n=e("./GenericWorker"),i=e("../crc32");function s(){n.call(this,"Crc32Probe"),this.withStreamInfo("crc32",0)}e("../utils").inherits(s,n),s.prototype.processChunk=function(e){this.streamInfo.crc32=i(e.data,this.streamInfo.crc32||0),this.push(e)},t.exports=s},{"../crc32":4,"../utils":32,"./GenericWorker":28}],26:[function(e,t,r){"use strict";var n=e("../utils"),i=e("./GenericWorker");function s(e){i.call(this,"DataLengthProbe for "+e),this.propName=e,this.withStreamInfo(e,0)}n.inherits(s,i),s.prototype.processChunk=function(e){if(e){var t=this.streamInfo[this.propName]||0;this.streamInfo[this.propName]=t+e.data.length}i.prototype.processChunk.call(this,e)},t.exports=s},{"../utils":32,"./GenericWorker":28}],27:[function(e,t,r){"use strict";var n=e("../utils"),i=e("./GenericWorker");function s(e){i.call(this,"DataWorker");var t=this;this.dataIsReady=!1,this.index=0,this.max=0,this.data=null,this.type="",this._tickScheduled=!1,e.then((function(e){t.dataIsReady=!0,t.data=e,t.max=e&&e.length||0,t.type=n.getTypeOf(e),t.isPaused||t._tickAndRepeat()}),(function(e){t.error(e)}))}n.inherits(s,i),s.prototype.cleanUp=function(){i.prototype.cleanUp.call(this),this.data=null},s.prototype.resume=function(){return!!i.prototype.resume.call(this)&&(!this._tickScheduled&&this.dataIsReady&&(this._tickScheduled=!0,n.delay(this._tickAndRepeat,[],this)),!0)},s.prototype._tickAndRepeat=function(){this._tickScheduled=!1,this.isPaused||this.isFinished||(this._tick(),this.isFinished||(n.delay(this._tickAndRepeat,[],this),this._tickScheduled=!0))},s.prototype._tick=function(){if(this.isPaused||this.isFinished)return!1;var e=null,t=Math.min(this.max,this.index+16384);if(this.index>=this.max)return this.end();switch(this.type){case"string":e=this.data.substring(this.index,t);break;case"uint8array":e=this.data.subarray(this.index,t);break;case"array":case"nodebuffer":e=this.data.slice(this.index,t)}return this.index=t,this.push({data:e,meta:{percent:this.max?this.index/this.max*100:0}})},t.exports=s},{"../utils":32,"./GenericWorker":28}],28:[function(e,t,r){"use strict";function n(e){this.name=e||"default",this.streamInfo={},this.generatedError=null,this.extraStreamInfo={},this.isPaused=!0,this.isFinished=!1,this.isLocked=!1,this._listeners={data:[],end:[],error:[]},this.previous=null}n.prototype={push:function(e){this.emit("data",e)},end:function(){if(this.isFinished)return!1;this.flush();try{this.emit("end"),this.cleanUp(),this.isFinished=!0}catch(e){this.emit("error",e)}return!0},error:function(e){return!this.isFinished&&(this.isPaused?this.generatedError=e:(this.isFinished=!0,this.emit("error",e),this.previous&&this.previous.error(e),this.cleanUp()),!0)},on:function(e,t){return this._listeners[e].push(t),this},cleanUp:function(){this.streamInfo=this.generatedError=this.extraStreamInfo=null,this._listeners=[]},emit:function(e,t){if(this._listeners[e])for(var r=0;r<this._listeners[e].length;r++)this._listeners[e][r].call(this,t)},pipe:function(e){return e.registerPrevious(this)},registerPrevious:function(e){if(this.isLocked)throw new Error("The stream '"+this+"' has already been used.");this.streamInfo=e.streamInfo,this.mergeStreamInfo(),this.previous=e;var t=this;return e.on("data",(function(e){t.processChunk(e)})),e.on("end",(function(){t.end()})),e.on("error",(function(e){t.error(e)})),this},pause:function(){return!this.isPaused&&!this.isFinished&&(this.isPaused=!0,this.previous&&this.previous.pause(),!0)},resume:function(){if(!this.isPaused||this.isFinished)return!1;var e=this.isPaused=!1;return this.generatedError&&(this.error(this.generatedError),e=!0),this.previous&&this.previous.resume(),!e},flush:function(){},processChunk:function(e){this.push(e)},withStreamInfo:function(e,t){return this.extraStreamInfo[e]=t,this.mergeStreamInfo(),this},mergeStreamInfo:function(){for(var e in this.extraStreamInfo)Object.prototype.hasOwnProperty.call(this.extraStreamInfo,e)&&(this.streamInfo[e]=this.extraStreamInfo[e])},lock:function(){if(this.isLocked)throw new Error("The stream '"+this+"' has already been used.");this.isLocked=!0,this.previous&&this.previous.lock()},toString:function(){var e="Worker "+this.name;return this.previous?this.previous+" -> "+e:e}},t.exports=n},{}],29:[function(e,t,r){"use strict";var h=e("../utils"),i=e("./ConvertWorker"),s=e("./GenericWorker"),u=e("../base64"),n=e("../support"),a=e("../external"),o=null;if(n.nodestream)try{o=e("../nodejs/NodejsStreamOutputAdapter")}catch(e){}function l(e,o){return new a.Promise((function(t,r){var n=[],i=e._internalType,s=e._outputType,a=e._mimeType;e.on("data",(function(e,t){n.push(e),o&&o(t)})).on("error",(function(e){n=[],r(e)})).on("end",(function(){try{var e=function(e,t,r){switch(e){case"blob":return h.newBlob(h.transformTo("arraybuffer",t),r);case"base64":return u.encode(t);default:return h.transformTo(e,t)}}(s,function(e,t){var r,n=0,i=null,s=0;for(r=0;r<t.length;r++)s+=t[r].length;switch(e){case"string":return t.join("");case"array":return Array.prototype.concat.apply([],t);case"uint8array":for(i=new Uint8Array(s),r=0;r<t.length;r++)i.set(t[r],n),n+=t[r].length;return i;case"nodebuffer":return Buffer.concat(t);default:throw new Error("concat : unsupported type '"+e+"'")}}(i,n),a);t(e)}catch(e){r(e)}n=[]})).resume()}))}function f(e,t,r){var n=t;switch(t){case"blob":case"arraybuffer":n="uint8array";break;case"base64":n="string"}try{this._internalType=n,this._outputType=t,this._mimeType=r,h.checkSupport(n),this._worker=e.pipe(new i(n)),e.lock()}catch(e){this._worker=new s("error"),this._worker.error(e)}}f.prototype={accumulate:function(e){return l(this,e)},on:function(e,t){var r=this;return"data"===e?this._worker.on(e,(function(e){t.call(r,e.data,e.meta)})):this._worker.on(e,(function(){h.delay(t,arguments,r)})),this},resume:function(){return h.delay(this._worker.resume,[],this._worker),this},pause:function(){return this._worker.pause(),this},toNodejsStream:function(e){if(h.checkSupport("nodestream"),"nodebuffer"!==this._outputType)throw new Error(this._outputType+" is not supported by this method");return new o(this,{objectMode:"nodebuffer"!==this._outputType},e)}},t.exports=f},{"../base64":1,"../external":6,"../nodejs/NodejsStreamOutputAdapter":13,"../support":30,"../utils":32,"./ConvertWorker":24,"./GenericWorker":28}],30:[function(e,t,r){"use strict";if(r.base64=!0,r.array=!0,r.string=!0,r.arraybuffer="undefined"!=typeof ArrayBuffer&&"undefined"!=typeof Uint8Array,r.nodebuffer="undefined"!=typeof Buffer,r.uint8array="undefined"!=typeof Uint8Array,"undefined"==typeof ArrayBuffer)r.blob=!1;else{var n=new ArrayBuffer(0);try{r.blob=0===new Blob([n],{type:"application/zip"}).size}catch(e){try{var i=new(self.BlobBuilder||self.WebKitBlobBuilder||self.MozBlobBuilder||self.MSBlobBuilder);i.append(n),r.blob=0===i.getBlob("application/zip").size}catch(e){r.blob=!1}}}try{r.nodestream=!!e("readable-stream").Readable}catch(e){r.nodestream=!1}},{"readable-stream":16}],31:[function(e,t,s){"use strict";for(var o=e("./utils"),h=e("./support"),r=e("./nodejsUtils"),n=e("./stream/GenericWorker"),u=new Array(256),i=0;i<256;i++)u[i]=252<=i?6:248<=i?5:240<=i?4:224<=i?3:192<=i?2:1;function a(){n.call(this,"utf-8 decode"),this.leftOver=null}function l(){n.call(this,"utf-8 encode")}u[254]=u[254]=1,s.utf8encode=function(e){return h.nodebuffer?r.newBufferFrom(e,"utf-8"):function(e){var t,r,n,i,s,a=e.length,o=0;for(i=0;i<a;i++)55296==(64512&(r=e.charCodeAt(i)))&&i+1<a&&56320==(64512&(n=e.charCodeAt(i+1)))&&(r=65536+(r-55296<<10)+(n-56320),i++),o+=r<128?1:r<2048?2:r<65536?3:4;for(t=h.uint8array?new Uint8Array(o):new Array(o),i=s=0;s<o;i++)55296==(64512&(r=e.charCodeAt(i)))&&i+1<a&&56320==(64512&(n=e.charCodeAt(i+1)))&&(r=65536+(r-55296<<10)+(n-56320),i++),r<128?t[s++]=r:(r<2048?t[s++]=192|r>>>6:(r<65536?t[s++]=224|r>>>12:(t[s++]=240|r>>>18,t[s++]=128|r>>>12&63),t[s++]=128|r>>>6&63),t[s++]=128|63&r);return t}(e)},s.utf8decode=function(e){return h.nodebuffer?o.transformTo("nodebuffer",e).toString("utf-8"):function(e){var t,r,n,i,s=e.length,a=new Array(2*s);for(t=r=0;t<s;)if((n=e[t++])<128)a[r++]=n;else if(4<(i=u[n]))a[r++]=65533,t+=i-1;else{for(n&=2===i?31:3===i?15:7;1<i&&t<s;)n=n<<6|63&e[t++],i--;1<i?a[r++]=65533:n<65536?a[r++]=n:(n-=65536,a[r++]=55296|n>>10&1023,a[r++]=56320|1023&n)}return a.length!==r&&(a.subarray?a=a.subarray(0,r):a.length=r),o.applyFromCharCode(a)}(e=o.transformTo(h.uint8array?"uint8array":"array",e))},o.inherits(a,n),a.prototype.processChunk=function(e){var t=o.transformTo(h.uint8array?"uint8array":"array",e.data);if(this.leftOver&&this.leftOver.length){if(h.uint8array){var r=t;(t=new Uint8Array(r.length+this.leftOver.length)).set(this.leftOver,0),t.set(r,this.leftOver.length)}else t=this.leftOver.concat(t);this.leftOver=null}var n=function(e,t){var r;for((t=t||e.length)>e.length&&(t=e.length),r=t-1;0<=r&&128==(192&e[r]);)r--;return r<0||0===r?t:r+u[e[r]]>t?r:t}(t),i=t;n!==t.length&&(h.uint8array?(i=t.subarray(0,n),this.leftOver=t.subarray(n,t.length)):(i=t.slice(0,n),this.leftOver=t.slice(n,t.length))),this.push({data:s.utf8decode(i),meta:e.meta})},a.prototype.flush=function(){this.leftOver&&this.leftOver.length&&(this.push({data:s.utf8decode(this.leftOver),meta:{}}),this.leftOver=null)},s.Utf8DecodeWorker=a,o.inherits(l,n),l.prototype.processChunk=function(e){this.push({data:s.utf8encode(e.data),meta:e.meta})},s.Utf8EncodeWorker=l},{"./nodejsUtils":14,"./stream/GenericWorker":28,"./support":30,"./utils":32}],32:[function(e,t,a){"use strict";var o=e("./support"),h=e("./base64"),r=e("./nodejsUtils"),u=e("./external");function n(e){return e}function l(e,t){for(var r=0;r<e.length;++r)t[r]=255&e.charCodeAt(r);return t}e("setimmediate"),a.newBlob=function(t,r){a.checkSupport("blob");try{return new Blob([t],{type:r})}catch(e){try{var n=new(self.BlobBuilder||self.WebKitBlobBuilder||self.MozBlobBuilder||self.MSBlobBuilder);return n.append(t),n.getBlob(r)}catch(e){throw new Error("Bug : can't construct the Blob.")}}};var i={stringifyByChunk:function(e,t,r){var n=[],i=0,s=e.length;if(s<=r)return String.fromCharCode.apply(null,e);for(;i<s;)"array"===t||"nodebuffer"===t?n.push(String.fromCharCode.apply(null,e.slice(i,Math.min(i+r,s)))):n.push(String.fromCharCode.apply(null,e.subarray(i,Math.min(i+r,s)))),i+=r;return n.join("")},stringifyByChar:function(e){for(var t="",r=0;r<e.length;r++)t+=String.fromCharCode(e[r]);return t},applyCanBeUsed:{uint8array:function(){try{return o.uint8array&&1===String.fromCharCode.apply(null,new Uint8Array(1)).length}catch(e){return!1}}(),nodebuffer:function(){try{return o.nodebuffer&&1===String.fromCharCode.apply(null,r.allocBuffer(1)).length}catch(e){return!1}}()}};function s(e){var t=65536,r=a.getTypeOf(e),n=!0;if("uint8array"===r?n=i.applyCanBeUsed.uint8array:"nodebuffer"===r&&(n=i.applyCanBeUsed.nodebuffer),n)for(;1<t;)try{return i.stringifyByChunk(e,r,t)}catch(e){t=Math.floor(t/2)}return i.stringifyByChar(e)}function f(e,t){for(var r=0;r<e.length;r++)t[r]=e[r];return t}a.applyFromCharCode=s;var c={};c.string={string:n,array:function(e){return l(e,new Array(e.length))},arraybuffer:function(e){return c.string.uint8array(e).buffer},uint8array:function(e){return l(e,new Uint8Array(e.length))},nodebuffer:function(e){return l(e,r.allocBuffer(e.length))}},c.array={string:s,array:n,arraybuffer:function(e){return new Uint8Array(e).buffer},uint8array:function(e){return new Uint8Array(e)},nodebuffer:function(e){return r.newBufferFrom(e)}},c.arraybuffer={string:function(e){return s(new Uint8Array(e))},array:function(e){return f(new Uint8Array(e),new Array(e.byteLength))},arraybuffer:n,uint8array:function(e){return new Uint8Array(e)},nodebuffer:function(e){return r.newBufferFrom(new Uint8Array(e))}},c.uint8array={string:s,array:function(e){return f(e,new Array(e.length))},arraybuffer:function(e){return e.buffer},uint8array:n,nodebuffer:function(e){return r.newBufferFrom(e)}},c.nodebuffer={string:s,array:function(e){return f(e,new Array(e.length))},arraybuffer:function(e){return c.nodebuffer.uint8array(e).buffer},uint8array:function(e){return f(e,new Uint8Array(e.length))},nodebuffer:n},a.transformTo=function(e,t){if(t=t||"",!e)return t;a.checkSupport(e);var r=a.getTypeOf(t);return c[r][e](t)},a.resolve=function(e){for(var t=e.split("/"),r=[],n=0;n<t.length;n++){var i=t[n];"."===i||""===i&&0!==n&&n!==t.length-1||(".."===i?r.pop():r.push(i))}return r.join("/")},a.getTypeOf=function(e){return"string"==typeof e?"string":"[object Array]"===Object.prototype.toString.call(e)?"array":o.nodebuffer&&r.isBuffer(e)?"nodebuffer":o.uint8array&&e instanceof Uint8Array?"uint8array":o.arraybuffer&&e instanceof ArrayBuffer?"arraybuffer":void 0},a.checkSupport=function(e){if(!o[e.toLowerCase()])throw new Error(e+" is not supported by this platform")},a.MAX_VALUE_16BITS=65535,a.MAX_VALUE_32BITS=-1,a.pretty=function(e){var t,r,n="";for(r=0;r<(e||"").length;r++)n+="\\x"+((t=e.charCodeAt(r))<16?"0":"")+t.toString(16).toUpperCase();return n},a.delay=function(e,t,r){setImmediate((function(){e.apply(r||null,t||[])}))},a.inherits=function(e,t){function r(){}r.prototype=t.prototype,e.prototype=new r},a.extend=function(){var e,t,r={};for(e=0;e<arguments.length;e++)for(t in arguments[e])Object.prototype.hasOwnProperty.call(arguments[e],t)&&void 0===r[t]&&(r[t]=arguments[e][t]);return r},a.prepareContent=function(r,e,n,i,s){return u.Promise.resolve(e).then((function(n){return o.blob&&(n instanceof Blob||-1!==["[object File]","[object Blob]"].indexOf(Object.prototype.toString.call(n)))&&"undefined"!=typeof FileReader?new u.Promise((function(t,r){var e=new FileReader;e.onload=function(e){t(e.target.result)},e.onerror=function(e){r(e.target.error)},e.readAsArrayBuffer(n)})):n})).then((function(e){var t=a.getTypeOf(e);return t?("arraybuffer"===t?e=a.transformTo("uint8array",e):"string"===t&&(s?e=h.decode(e):n&&!0!==i&&(e=function(e){return l(e,o.uint8array?new Uint8Array(e.length):new Array(e.length))}(e))),e):u.Promise.reject(new Error("Can't read the data of '"+r+"'. Is it in a supported JavaScript type (String, Blob, ArrayBuffer, etc) ?"))}))}},{"./base64":1,"./external":6,"./nodejsUtils":14,"./support":30,setimmediate:54}],33:[function(e,t,r){"use strict";var n=e("./reader/readerFor"),i=e("./utils"),s=e("./signature"),a=e("./zipEntry"),o=e("./support");function h(e){this.files=[],this.loadOptions=e}h.prototype={checkSignature:function(e){if(!this.reader.readAndCheckSignature(e)){this.reader.index-=4;var t=this.reader.readString(4);throw new Error("Corrupted zip or bug: unexpected signature ("+i.pretty(t)+", expected "+i.pretty(e)+")")}},isSignature:function(e,t){var r=this.reader.index;this.reader.setIndex(e);var n=this.reader.readString(4)===t;return this.reader.setIndex(r),n},readBlockEndOfCentral:function(){this.diskNumber=this.reader.readInt(2),this.diskWithCentralDirStart=this.reader.readInt(2),this.centralDirRecordsOnThisDisk=this.reader.readInt(2),this.centralDirRecords=this.reader.readInt(2),this.centralDirSize=this.reader.readInt(4),this.centralDirOffset=this.reader.readInt(4),this.zipCommentLength=this.reader.readInt(2);var e=this.reader.readData(this.zipCommentLength),t=o.uint8array?"uint8array":"array",r=i.transformTo(t,e);this.zipComment=this.loadOptions.decodeFileName(r)},readBlockZip64EndOfCentral:function(){this.zip64EndOfCentralSize=this.reader.readInt(8),this.reader.skip(4),this.diskNumber=this.reader.readInt(4),this.diskWithCentralDirStart=this.reader.readInt(4),this.centralDirRecordsOnThisDisk=this.reader.readInt(8),this.centralDirRecords=this.reader.readInt(8),this.centralDirSize=this.reader.readInt(8),this.centralDirOffset=this.reader.readInt(8),this.zip64ExtensibleData={};for(var e,t,r,n=this.zip64EndOfCentralSize-44;0<n;)e=this.reader.readInt(2),t=this.reader.readInt(4),r=this.reader.readData(t),this.zip64ExtensibleData[e]={id:e,length:t,value:r}},readBlockZip64EndOfCentralLocator:function(){if(this.diskWithZip64CentralDirStart=this.reader.readInt(4),this.relativeOffsetEndOfZip64CentralDir=this.reader.readInt(8),this.disksCount=this.reader.readInt(4),1<this.disksCount)throw new Error("Multi-volumes zip are not supported")},readLocalFiles:function(){var e,t;for(e=0;e<this.files.length;e++)t=this.files[e],this.reader.setIndex(t.localHeaderOffset),this.checkSignature(s.LOCAL_FILE_HEADER),t.readLocalPart(this.reader),t.handleUTF8(),t.processAttributes()},readCentralDir:function(){var e;for(this.reader.setIndex(this.centralDirOffset);this.reader.readAndCheckSignature(s.CENTRAL_FILE_HEADER);)(e=new a({zip64:this.zip64},this.loadOptions)).readCentralPart(this.reader),this.files.push(e);if(this.centralDirRecords!==this.files.length&&0!==this.centralDirRecords&&0===this.files.length)throw new Error("Corrupted zip or bug: expected "+this.centralDirRecords+" records in central dir, got "+this.files.length)},readEndOfCentral:function(){var e=this.reader.lastIndexOfSignature(s.CENTRAL_DIRECTORY_END);if(e<0)throw this.isSignature(0,s.LOCAL_FILE_HEADER)?new Error("Corrupted zip: can't find end of central directory"):new Error("Can't find end of central directory : is this a zip file ? If it is, see https://stuk.github.io/jszip/documentation/howto/read_zip.html");this.reader.setIndex(e);var t=e;if(this.checkSignature(s.CENTRAL_DIRECTORY_END),this.readBlockEndOfCentral(),this.diskNumber===i.MAX_VALUE_16BITS||this.diskWithCentralDirStart===i.MAX_VALUE_16BITS||this.centralDirRecordsOnThisDisk===i.MAX_VALUE_16BITS||this.centralDirRecords===i.MAX_VALUE_16BITS||this.centralDirSize===i.MAX_VALUE_32BITS||this.centralDirOffset===i.MAX_VALUE_32BITS){if(this.zip64=!0,(e=this.reader.lastIndexOfSignature(s.ZIP64_CENTRAL_DIRECTORY_LOCATOR))<0)throw new Error("Corrupted zip: can't find the ZIP64 end of central directory locator");if(this.reader.setIndex(e),this.checkSignature(s.ZIP64_CENTRAL_DIRECTORY_LOCATOR),this.readBlockZip64EndOfCentralLocator(),!this.isSignature(this.relativeOffsetEndOfZip64CentralDir,s.ZIP64_CENTRAL_DIRECTORY_END)&&(this.relativeOffsetEndOfZip64CentralDir=this.reader.lastIndexOfSignature(s.ZIP64_CENTRAL_DIRECTORY_END),this.relativeOffsetEndOfZip64CentralDir<0))throw new Error("Corrupted zip: can't find the ZIP64 end of central directory");this.reader.setIndex(this.relativeOffsetEndOfZip64CentralDir),this.checkSignature(s.ZIP64_CENTRAL_DIRECTORY_END),this.readBlockZip64EndOfCentral()}var r=this.centralDirOffset+this.centralDirSize;this.zip64&&(r+=20,r+=12+this.zip64EndOfCentralSize);var n=t-r;if(0<n)this.isSignature(t,s.CENTRAL_FILE_HEADER)||(this.reader.zero=n);else if(n<0)throw new Error("Corrupted zip: missing "+Math.abs(n)+" bytes.")},prepareReader:function(e){this.reader=n(e)},load:function(e){this.prepareReader(e),this.readEndOfCentral(),this.readCentralDir(),this.readLocalFiles()}},t.exports=h},{"./reader/readerFor":22,"./signature":23,"./support":30,"./utils":32,"./zipEntry":34}],34:[function(e,t,r){"use strict";var n=e("./reader/readerFor"),s=e("./utils"),i=e("./compressedObject"),a=e("./crc32"),o=e("./utf8"),h=e("./compressions"),u=e("./support");function l(e,t){this.options=e,this.loadOptions=t}l.prototype={isEncrypted:function(){return 1==(1&this.bitFlag)},useUTF8:function(){return 2048==(2048&this.bitFlag)},readLocalPart:function(e){var t,r;if(e.skip(22),this.fileNameLength=e.readInt(2),r=e.readInt(2),this.fileName=e.readData(this.fileNameLength),e.skip(r),-1===this.compressedSize||-1===this.uncompressedSize)throw new Error("Bug or corrupted zip : didn't get enough information from the central directory (compressedSize === -1 || uncompressedSize === -1)");if(null===(t=function(e){for(var t in h)if(Object.prototype.hasOwnProperty.call(h,t)&&h[t].magic===e)return h[t];return null}(this.compressionMethod)))throw new Error("Corrupted zip : compression "+s.pretty(this.compressionMethod)+" unknown (inner file : "+s.transformTo("string",this.fileName)+")");this.decompressed=new i(this.compressedSize,this.uncompressedSize,this.crc32,t,e.readData(this.compressedSize))},readCentralPart:function(e){this.versionMadeBy=e.readInt(2),e.skip(2),this.bitFlag=e.readInt(2),this.compressionMethod=e.readString(2),this.date=e.readDate(),this.crc32=e.readInt(4),this.compressedSize=e.readInt(4),this.uncompressedSize=e.readInt(4);var t=e.readInt(2);if(this.extraFieldsLength=e.readInt(2),this.fileCommentLength=e.readInt(2),this.diskNumberStart=e.readInt(2),this.internalFileAttributes=e.readInt(2),this.externalFileAttributes=e.readInt(4),this.localHeaderOffset=e.readInt(4),this.isEncrypted())throw new Error("Encrypted zip are not supported");e.skip(t),this.readExtraFields(e),this.parseZIP64ExtraField(e),this.fileComment=e.readData(this.fileCommentLength)},processAttributes:function(){this.unixPermissions=null,this.dosPermissions=null;var e=this.versionMadeBy>>8;this.dir=!!(16&this.externalFileAttributes),0==e&&(this.dosPermissions=63&this.externalFileAttributes),3==e&&(this.unixPermissions=this.externalFileAttributes>>16&65535),this.dir||"/"!==this.fileNameStr.slice(-1)||(this.dir=!0)},parseZIP64ExtraField:function(){if(this.extraFields[1]){var e=n(this.extraFields[1].value);this.uncompressedSize===s.MAX_VALUE_32BITS&&(this.uncompressedSize=e.readInt(8)),this.compressedSize===s.MAX_VALUE_32BITS&&(this.compressedSize=e.readInt(8)),this.localHeaderOffset===s.MAX_VALUE_32BITS&&(this.localHeaderOffset=e.readInt(8)),this.diskNumberStart===s.MAX_VALUE_32BITS&&(this.diskNumberStart=e.readInt(4))}},readExtraFields:function(e){var t,r,n,i=e.index+this.extraFieldsLength;for(this.extraFields||(this.extraFields={});e.index+4<i;)t=e.readInt(2),r=e.readInt(2),n=e.readData(r),this.extraFields[t]={id:t,length:r,value:n};e.setIndex(i)},handleUTF8:function(){var e=u.uint8array?"uint8array":"array";if(this.useUTF8())this.fileNameStr=o.utf8decode(this.fileName),this.fileCommentStr=o.utf8decode(this.fileComment);else{var t=this.findExtraFieldUnicodePath();if(null!==t)this.fileNameStr=t;else{var r=s.transformTo(e,this.fileName);this.fileNameStr=this.loadOptions.decodeFileName(r)}var n=this.findExtraFieldUnicodeComment();if(null!==n)this.fileCommentStr=n;else{var i=s.transformTo(e,this.fileComment);this.fileCommentStr=this.loadOptions.decodeFileName(i)}}},findExtraFieldUnicodePath:function(){var e=this.extraFields[28789];if(e){var t=n(e.value);return 1!==t.readInt(1)||a(this.fileName)!==t.readInt(4)?null:o.utf8decode(t.readData(e.length-5))}return null},findExtraFieldUnicodeComment:function(){var e=this.extraFields[25461];if(e){var t=n(e.value);return 1!==t.readInt(1)||a(this.fileComment)!==t.readInt(4)?null:o.utf8decode(t.readData(e.length-5))}return null}},t.exports=l},{"./compressedObject":2,"./compressions":3,"./crc32":4,"./reader/readerFor":22,"./support":30,"./utf8":31,"./utils":32}],35:[function(e,t,r){"use strict";function n(e,t,r){this.name=e,this.dir=r.dir,this.date=r.date,this.comment=r.comment,this.unixPermissions=r.unixPermissions,this.dosPermissions=r.dosPermissions,this._data=t,this._dataBinary=r.binary,this.options={compression:r.compression,compressionOptions:r.compressionOptions}}var s=e("./stream/StreamHelper"),i=e("./stream/DataWorker"),a=e("./utf8"),o=e("./compressedObject"),h=e("./stream/GenericWorker");n.prototype={internalStream:function(e){var t=null,r="string";try{if(!e)throw new Error("No output type specified.");var n="string"===(r=e.toLowerCase())||"text"===r;"binarystring"!==r&&"text"!==r||(r="string"),t=this._decompressWorker();var i=!this._dataBinary;i&&!n&&(t=t.pipe(new a.Utf8EncodeWorker)),!i&&n&&(t=t.pipe(new a.Utf8DecodeWorker))}catch(e){(t=new h("error")).error(e)}return new s(t,r,"")},async:function(e,t){return this.internalStream(e).accumulate(t)},nodeStream:function(e,t){return this.internalStream(e||"nodebuffer").toNodejsStream(t)},_compressWorker:function(e,t){if(this._data instanceof o&&this._data.compression.magic===e.magic)return this._data.getCompressedWorker();var r=this._decompressWorker();return this._dataBinary||(r=r.pipe(new a.Utf8EncodeWorker)),o.createWorkerFrom(r,e,t)},_decompressWorker:function(){return this._data instanceof o?this._data.getContentWorker():this._data instanceof h?this._data:new i(this._data)}};for(var u=["asText","asBinary","asNodeBuffer","asUint8Array","asArrayBuffer"],l=function(){throw new Error("This method has been removed in JSZip 3.0, please check the upgrade guide.")},f=0;f<u.length;f++)n.prototype[u[f]]=l;t.exports=n},{"./compressedObject":2,"./stream/DataWorker":27,"./stream/GenericWorker":28,"./stream/StreamHelper":29,"./utf8":31}],36:[function(e,l,t){(function(t){"use strict";var r,n,e=t.MutationObserver||t.WebKitMutationObserver;if(e){var i=0,s=new e(u),a=t.document.createTextNode("");s.observe(a,{characterData:!0}),r=function(){a.data=i=++i%2}}else if(t.setImmediate||void 0===t.MessageChannel)r="document"in t&&"onreadystatechange"in t.document.createElement("script")?function(){var e=t.document.createElement("script");e.onreadystatechange=function(){u(),e.onreadystatechange=null,e.parentNode.removeChild(e),e=null},t.document.documentElement.appendChild(e)}:function(){setTimeout(u,0)};else{var o=new t.MessageChannel;o.port1.onmessage=u,r=function(){o.port2.postMessage(0)}}var h=[];function u(){var e,t;n=!0;for(var r=h.length;r;){for(t=h,h=[],e=-1;++e<r;)t[e]();r=h.length}n=!1}l.exports=function(e){1!==h.push(e)||n||r()}}).call(this,"undefined"!=typeof __webpack_require__.g?__webpack_require__.g:"undefined"!=typeof self?self:"undefined"!=typeof window?window:{})},{}],37:[function(e,t,r){"use strict";var i=e("immediate");function u(){}var l={},s=["REJECTED"],a=["FULFILLED"],n=["PENDING"];function o(e){if("function"!=typeof e)throw new TypeError("resolver must be a function");this.state=n,this.queue=[],this.outcome=void 0,e!==u&&d(this,e)}function h(e,t,r){this.promise=e,"function"==typeof t&&(this.onFulfilled=t,this.callFulfilled=this.otherCallFulfilled),"function"==typeof r&&(this.onRejected=r,this.callRejected=this.otherCallRejected)}function f(t,r,n){i((function(){var e;try{e=r(n)}catch(e){return l.reject(t,e)}e===t?l.reject(t,new TypeError("Cannot resolve promise with itself")):l.resolve(t,e)}))}function c(e){var t=e&&e.then;if(e&&("object"==typeof e||"function"==typeof e)&&"function"==typeof t)return function(){t.apply(e,arguments)}}function d(t,e){var r=!1;function n(e){r||(r=!0,l.reject(t,e))}function i(e){r||(r=!0,l.resolve(t,e))}var s=p((function(){e(i,n)}));"error"===s.status&&n(s.value)}function p(e,t){var r={};try{r.value=e(t),r.status="success"}catch(e){r.status="error",r.value=e}return r}(t.exports=o).prototype.finally=function(t){if("function"!=typeof t)return this;var r=this.constructor;return this.then((function(e){return r.resolve(t()).then((function(){return e}))}),(function(e){return r.resolve(t()).then((function(){throw e}))}))},o.prototype.catch=function(e){return this.then(null,e)},o.prototype.then=function(e,t){if("function"!=typeof e&&this.state===a||"function"!=typeof t&&this.state===s)return this;var r=new this.constructor(u);return this.state!==n?f(r,this.state===a?e:t,this.outcome):this.queue.push(new h(r,e,t)),r},h.prototype.callFulfilled=function(e){l.resolve(this.promise,e)},h.prototype.otherCallFulfilled=function(e){f(this.promise,this.onFulfilled,e)},h.prototype.callRejected=function(e){l.reject(this.promise,e)},h.prototype.otherCallRejected=function(e){f(this.promise,this.onRejected,e)},l.resolve=function(e,t){var r=p(c,t);if("error"===r.status)return l.reject(e,r.value);var n=r.value;if(n)d(e,n);else{e.state=a,e.outcome=t;for(var i=-1,s=e.queue.length;++i<s;)e.queue[i].callFulfilled(t)}return e},l.reject=function(e,t){e.state=s,e.outcome=t;for(var r=-1,n=e.queue.length;++r<n;)e.queue[r].callRejected(t);return e},o.resolve=function(e){return e instanceof this?e:l.resolve(new this(u),e)},o.reject=function(e){var t=new this(u);return l.reject(t,e)},o.all=function(e){var r=this;if("[object Array]"!==Object.prototype.toString.call(e))return this.reject(new TypeError("must be an array"));var n=e.length,i=!1;if(!n)return this.resolve([]);for(var s=new Array(n),a=0,t=-1,o=new this(u);++t<n;)h(e[t],t);return o;function h(e,t){r.resolve(e).then((function(e){s[t]=e,++a!==n||i||(i=!0,l.resolve(o,s))}),(function(e){i||(i=!0,l.reject(o,e))}))}},o.race=function(e){var t=this;if("[object Array]"!==Object.prototype.toString.call(e))return this.reject(new TypeError("must be an array"));var r=e.length,n=!1;if(!r)return this.resolve([]);for(var a,i=-1,s=new this(u);++i<r;)a=e[i],t.resolve(a).then((function(e){n||(n=!0,l.resolve(s,e))}),(function(e){n||(n=!0,l.reject(s,e))}));return s}},{immediate:36}],38:[function(e,t,r){"use strict";var n={};(0,e("./lib/utils/common").assign)(n,e("./lib/deflate"),e("./lib/inflate"),e("./lib/zlib/constants")),t.exports=n},{"./lib/deflate":39,"./lib/inflate":40,"./lib/utils/common":41,"./lib/zlib/constants":44}],39:[function(e,t,r){"use strict";var a=e("./zlib/deflate"),o=e("./utils/common"),h=e("./utils/strings"),i=e("./zlib/messages"),s=e("./zlib/zstream"),u=Object.prototype.toString,l=0,f=-1,c=0,d=8;function p(e){if(!(this instanceof p))return new p(e);this.options=o.assign({level:f,method:d,chunkSize:16384,windowBits:15,memLevel:8,strategy:c,to:""},e||{});var t=this.options;t.raw&&0<t.windowBits?t.windowBits=-t.windowBits:t.gzip&&0<t.windowBits&&t.windowBits<16&&(t.windowBits+=16),this.err=0,this.msg="",this.ended=!1,this.chunks=[],this.strm=new s,this.strm.avail_out=0;var r=a.deflateInit2(this.strm,t.level,t.method,t.windowBits,t.memLevel,t.strategy);if(r!==l)throw new Error(i[r]);if(t.header&&a.deflateSetHeader(this.strm,t.header),t.dictionary){var n;if(n="string"==typeof t.dictionary?h.string2buf(t.dictionary):"[object ArrayBuffer]"===u.call(t.dictionary)?new Uint8Array(t.dictionary):t.dictionary,(r=a.deflateSetDictionary(this.strm,n))!==l)throw new Error(i[r]);this._dict_set=!0}}function n(e,t){var r=new p(t);if(r.push(e,!0),r.err)throw r.msg||i[r.err];return r.result}p.prototype.push=function(e,t){var r,n,i=this.strm,s=this.options.chunkSize;if(this.ended)return!1;n=t===~~t?t:!0===t?4:0,"string"==typeof e?i.input=h.string2buf(e):"[object ArrayBuffer]"===u.call(e)?i.input=new Uint8Array(e):i.input=e,i.next_in=0,i.avail_in=i.input.length;do{if(0===i.avail_out&&(i.output=new o.Buf8(s),i.next_out=0,i.avail_out=s),1!==(r=a.deflate(i,n))&&r!==l)return this.onEnd(r),!(this.ended=!0);0!==i.avail_out&&(0!==i.avail_in||4!==n&&2!==n)||("string"===this.options.to?this.onData(h.buf2binstring(o.shrinkBuf(i.output,i.next_out))):this.onData(o.shrinkBuf(i.output,i.next_out)))}while((0<i.avail_in||0===i.avail_out)&&1!==r);return 4===n?(r=a.deflateEnd(this.strm),this.onEnd(r),this.ended=!0,r===l):2!==n||(this.onEnd(l),!(i.avail_out=0))},p.prototype.onData=function(e){this.chunks.push(e)},p.prototype.onEnd=function(e){e===l&&("string"===this.options.to?this.result=this.chunks.join(""):this.result=o.flattenChunks(this.chunks)),this.chunks=[],this.err=e,this.msg=this.strm.msg},r.Deflate=p,r.deflate=n,r.deflateRaw=function(e,t){return(t=t||{}).raw=!0,n(e,t)},r.gzip=function(e,t){return(t=t||{}).gzip=!0,n(e,t)}},{"./utils/common":41,"./utils/strings":42,"./zlib/deflate":46,"./zlib/messages":51,"./zlib/zstream":53}],40:[function(e,t,r){"use strict";var c=e("./zlib/inflate"),d=e("./utils/common"),p=e("./utils/strings"),m=e("./zlib/constants"),n=e("./zlib/messages"),i=e("./zlib/zstream"),s=e("./zlib/gzheader"),_=Object.prototype.toString;function a(e){if(!(this instanceof a))return new a(e);this.options=d.assign({chunkSize:16384,windowBits:0,to:""},e||{});var t=this.options;t.raw&&0<=t.windowBits&&t.windowBits<16&&(t.windowBits=-t.windowBits,0===t.windowBits&&(t.windowBits=-15)),!(0<=t.windowBits&&t.windowBits<16)||e&&e.windowBits||(t.windowBits+=32),15<t.windowBits&&t.windowBits<48&&0==(15&t.windowBits)&&(t.windowBits|=15),this.err=0,this.msg="",this.ended=!1,this.chunks=[],this.strm=new i,this.strm.avail_out=0;var r=c.inflateInit2(this.strm,t.windowBits);if(r!==m.Z_OK)throw new Error(n[r]);this.header=new s,c.inflateGetHeader(this.strm,this.header)}function o(e,t){var r=new a(t);if(r.push(e,!0),r.err)throw r.msg||n[r.err];return r.result}a.prototype.push=function(e,t){var r,n,i,s,a,o,h=this.strm,u=this.options.chunkSize,l=this.options.dictionary,f=!1;if(this.ended)return!1;n=t===~~t?t:!0===t?m.Z_FINISH:m.Z_NO_FLUSH,"string"==typeof e?h.input=p.binstring2buf(e):"[object ArrayBuffer]"===_.call(e)?h.input=new Uint8Array(e):h.input=e,h.next_in=0,h.avail_in=h.input.length;do{if(0===h.avail_out&&(h.output=new d.Buf8(u),h.next_out=0,h.avail_out=u),(r=c.inflate(h,m.Z_NO_FLUSH))===m.Z_NEED_DICT&&l&&(o="string"==typeof l?p.string2buf(l):"[object ArrayBuffer]"===_.call(l)?new Uint8Array(l):l,r=c.inflateSetDictionary(this.strm,o)),r===m.Z_BUF_ERROR&&!0===f&&(r=m.Z_OK,f=!1),r!==m.Z_STREAM_END&&r!==m.Z_OK)return this.onEnd(r),!(this.ended=!0);h.next_out&&(0!==h.avail_out&&r!==m.Z_STREAM_END&&(0!==h.avail_in||n!==m.Z_FINISH&&n!==m.Z_SYNC_FLUSH)||("string"===this.options.to?(i=p.utf8border(h.output,h.next_out),s=h.next_out-i,a=p.buf2string(h.output,i),h.next_out=s,h.avail_out=u-s,s&&d.arraySet(h.output,h.output,i,s,0),this.onData(a)):this.onData(d.shrinkBuf(h.output,h.next_out)))),0===h.avail_in&&0===h.avail_out&&(f=!0)}while((0<h.avail_in||0===h.avail_out)&&r!==m.Z_STREAM_END);return r===m.Z_STREAM_END&&(n=m.Z_FINISH),n===m.Z_FINISH?(r=c.inflateEnd(this.strm),this.onEnd(r),this.ended=!0,r===m.Z_OK):n!==m.Z_SYNC_FLUSH||(this.onEnd(m.Z_OK),!(h.avail_out=0))},a.prototype.onData=function(e){this.chunks.push(e)},a.prototype.onEnd=function(e){e===m.Z_OK&&("string"===this.options.to?this.result=this.chunks.join(""):this.result=d.flattenChunks(this.chunks)),this.chunks=[],this.err=e,this.msg=this.strm.msg},r.Inflate=a,r.inflate=o,r.inflateRaw=function(e,t){return(t=t||{}).raw=!0,o(e,t)},r.ungzip=o},{"./utils/common":41,"./utils/strings":42,"./zlib/constants":44,"./zlib/gzheader":47,"./zlib/inflate":49,"./zlib/messages":51,"./zlib/zstream":53}],41:[function(e,t,r){"use strict";var n="undefined"!=typeof Uint8Array&&"undefined"!=typeof Uint16Array&&"undefined"!=typeof Int32Array;r.assign=function(e){for(var t=Array.prototype.slice.call(arguments,1);t.length;){var r=t.shift();if(r){if("object"!=typeof r)throw new TypeError(r+"must be non-object");for(var n in r)r.hasOwnProperty(n)&&(e[n]=r[n])}}return e},r.shrinkBuf=function(e,t){return e.length===t?e:e.subarray?e.subarray(0,t):(e.length=t,e)};var i={arraySet:function(e,t,r,n,i){if(t.subarray&&e.subarray)e.set(t.subarray(r,r+n),i);else for(var s=0;s<n;s++)e[i+s]=t[r+s]},flattenChunks:function(e){var t,r,n,i,s,a;for(t=n=0,r=e.length;t<r;t++)n+=e[t].length;for(a=new Uint8Array(n),t=i=0,r=e.length;t<r;t++)s=e[t],a.set(s,i),i+=s.length;return a}},s={arraySet:function(e,t,r,n,i){for(var s=0;s<n;s++)e[i+s]=t[r+s]},flattenChunks:function(e){return[].concat.apply([],e)}};r.setTyped=function(e){e?(r.Buf8=Uint8Array,r.Buf16=Uint16Array,r.Buf32=Int32Array,r.assign(r,i)):(r.Buf8=Array,r.Buf16=Array,r.Buf32=Array,r.assign(r,s))},r.setTyped(n)},{}],42:[function(e,t,r){"use strict";var h=e("./common"),i=!0,s=!0;try{String.fromCharCode.apply(null,[0])}catch(e){i=!1}try{String.fromCharCode.apply(null,new Uint8Array(1))}catch(e){s=!1}for(var u=new h.Buf8(256),n=0;n<256;n++)u[n]=252<=n?6:248<=n?5:240<=n?4:224<=n?3:192<=n?2:1;function l(e,t){if(t<65537&&(e.subarray&&s||!e.subarray&&i))return String.fromCharCode.apply(null,h.shrinkBuf(e,t));for(var r="",n=0;n<t;n++)r+=String.fromCharCode(e[n]);return r}u[254]=u[254]=1,r.string2buf=function(e){var t,r,n,i,s,a=e.length,o=0;for(i=0;i<a;i++)55296==(64512&(r=e.charCodeAt(i)))&&i+1<a&&56320==(64512&(n=e.charCodeAt(i+1)))&&(r=65536+(r-55296<<10)+(n-56320),i++),o+=r<128?1:r<2048?2:r<65536?3:4;for(t=new h.Buf8(o),i=s=0;s<o;i++)55296==(64512&(r=e.charCodeAt(i)))&&i+1<a&&56320==(64512&(n=e.charCodeAt(i+1)))&&(r=65536+(r-55296<<10)+(n-56320),i++),r<128?t[s++]=r:(r<2048?t[s++]=192|r>>>6:(r<65536?t[s++]=224|r>>>12:(t[s++]=240|r>>>18,t[s++]=128|r>>>12&63),t[s++]=128|r>>>6&63),t[s++]=128|63&r);return t},r.buf2binstring=function(e){return l(e,e.length)},r.binstring2buf=function(e){for(var t=new h.Buf8(e.length),r=0,n=t.length;r<n;r++)t[r]=e.charCodeAt(r);return t},r.buf2string=function(e,t){var r,n,i,s,a=t||e.length,o=new Array(2*a);for(r=n=0;r<a;)if((i=e[r++])<128)o[n++]=i;else if(4<(s=u[i]))o[n++]=65533,r+=s-1;else{for(i&=2===s?31:3===s?15:7;1<s&&r<a;)i=i<<6|63&e[r++],s--;1<s?o[n++]=65533:i<65536?o[n++]=i:(i-=65536,o[n++]=55296|i>>10&1023,o[n++]=56320|1023&i)}return l(o,n)},r.utf8border=function(e,t){var r;for((t=t||e.length)>e.length&&(t=e.length),r=t-1;0<=r&&128==(192&e[r]);)r--;return r<0||0===r?t:r+u[e[r]]>t?r:t}},{"./common":41}],43:[function(e,t,r){"use strict";t.exports=function(e,t,r,n){for(var i=65535&e|0,s=e>>>16&65535|0,a=0;0!==r;){for(r-=a=2e3<r?2e3:r;s=s+(i=i+t[n++]|0)|0,--a;);i%=65521,s%=65521}return i|s<<16|0}},{}],44:[function(e,t,r){"use strict";t.exports={Z_NO_FLUSH:0,Z_PARTIAL_FLUSH:1,Z_SYNC_FLUSH:2,Z_FULL_FLUSH:3,Z_FINISH:4,Z_BLOCK:5,Z_TREES:6,Z_OK:0,Z_STREAM_END:1,Z_NEED_DICT:2,Z_ERRNO:-1,Z_STREAM_ERROR:-2,Z_DATA_ERROR:-3,Z_BUF_ERROR:-5,Z_NO_COMPRESSION:0,Z_BEST_SPEED:1,Z_BEST_COMPRESSION:9,Z_DEFAULT_COMPRESSION:-1,Z_FILTERED:1,Z_HUFFMAN_ONLY:2,Z_RLE:3,Z_FIXED:4,Z_DEFAULT_STRATEGY:0,Z_BINARY:0,Z_TEXT:1,Z_UNKNOWN:2,Z_DEFLATED:8}},{}],45:[function(e,t,r){"use strict";var o=function(){for(var e,t=[],r=0;r<256;r++){e=r;for(var n=0;n<8;n++)e=1&e?3988292384^e>>>1:e>>>1;t[r]=e}return t}();t.exports=function(e,t,r,n){var i=o,s=n+r;e^=-1;for(var a=n;a<s;a++)e=e>>>8^i[255&(e^t[a])];return-1^e}},{}],46:[function(e,t,r){"use strict";var h,c=e("../utils/common"),u=e("./trees"),d=e("./adler32"),p=e("./crc32"),n=e("./messages"),l=0,f=4,m=0,_=-2,g=-1,b=4,i=2,v=8,y=9,s=286,a=30,o=19,w=2*s+1,k=15,x=3,S=258,z=S+x+1,C=42,E=113,A=1,I=2,O=3,B=4;function R(e,t){return e.msg=n[t],t}function T(e){return(e<<1)-(4<e?9:0)}function D(e){for(var t=e.length;0<=--t;)e[t]=0}function F(e){var t=e.state,r=t.pending;r>e.avail_out&&(r=e.avail_out),0!==r&&(c.arraySet(e.output,t.pending_buf,t.pending_out,r,e.next_out),e.next_out+=r,t.pending_out+=r,e.total_out+=r,e.avail_out-=r,t.pending-=r,0===t.pending&&(t.pending_out=0))}function N(e,t){u._tr_flush_block(e,0<=e.block_start?e.block_start:-1,e.strstart-e.block_start,t),e.block_start=e.strstart,F(e.strm)}function U(e,t){e.pending_buf[e.pending++]=t}function P(e,t){e.pending_buf[e.pending++]=t>>>8&255,e.pending_buf[e.pending++]=255&t}function L(e,t){var r,n,i=e.max_chain_length,s=e.strstart,a=e.prev_length,o=e.nice_match,h=e.strstart>e.w_size-z?e.strstart-(e.w_size-z):0,u=e.window,l=e.w_mask,f=e.prev,c=e.strstart+S,d=u[s+a-1],p=u[s+a];e.prev_length>=e.good_match&&(i>>=2),o>e.lookahead&&(o=e.lookahead);do{if(u[(r=t)+a]===p&&u[r+a-1]===d&&u[r]===u[s]&&u[++r]===u[s+1]){s+=2,r++;do{}while(u[++s]===u[++r]&&u[++s]===u[++r]&&u[++s]===u[++r]&&u[++s]===u[++r]&&u[++s]===u[++r]&&u[++s]===u[++r]&&u[++s]===u[++r]&&u[++s]===u[++r]&&s<c);if(n=S-(c-s),s=c-S,a<n){if(e.match_start=t,o<=(a=n))break;d=u[s+a-1],p=u[s+a]}}}while((t=f[t&l])>h&&0!=--i);return a<=e.lookahead?a:e.lookahead}function j(e){var t,r,n,i,s,a,o,h,u,l,f=e.w_size;do{if(i=e.window_size-e.lookahead-e.strstart,e.strstart>=f+(f-z)){for(c.arraySet(e.window,e.window,f,f,0),e.match_start-=f,e.strstart-=f,e.block_start-=f,t=r=e.hash_size;n=e.head[--t],e.head[t]=f<=n?n-f:0,--r;);for(t=r=f;n=e.prev[--t],e.prev[t]=f<=n?n-f:0,--r;);i+=f}if(0===e.strm.avail_in)break;if(a=e.strm,o=e.window,h=e.strstart+e.lookahead,u=i,l=void 0,l=a.avail_in,u<l&&(l=u),r=0===l?0:(a.avail_in-=l,c.arraySet(o,a.input,a.next_in,l,h),1===a.state.wrap?a.adler=d(a.adler,o,l,h):2===a.state.wrap&&(a.adler=p(a.adler,o,l,h)),a.next_in+=l,a.total_in+=l,l),e.lookahead+=r,e.lookahead+e.insert>=x)for(s=e.strstart-e.insert,e.ins_h=e.window[s],e.ins_h=(e.ins_h<<e.hash_shift^e.window[s+1])&e.hash_mask;e.insert&&(e.ins_h=(e.ins_h<<e.hash_shift^e.window[s+x-1])&e.hash_mask,e.prev[s&e.w_mask]=e.head[e.ins_h],e.head[e.ins_h]=s,s++,e.insert--,!(e.lookahead+e.insert<x)););}while(e.lookahead<z&&0!==e.strm.avail_in)}function Z(e,t){for(var r,n;;){if(e.lookahead<z){if(j(e),e.lookahead<z&&t===l)return A;if(0===e.lookahead)break}if(r=0,e.lookahead>=x&&(e.ins_h=(e.ins_h<<e.hash_shift^e.window[e.strstart+x-1])&e.hash_mask,r=e.prev[e.strstart&e.w_mask]=e.head[e.ins_h],e.head[e.ins_h]=e.strstart),0!==r&&e.strstart-r<=e.w_size-z&&(e.match_length=L(e,r)),e.match_length>=x)if(n=u._tr_tally(e,e.strstart-e.match_start,e.match_length-x),e.lookahead-=e.match_length,e.match_length<=e.max_lazy_match&&e.lookahead>=x){for(e.match_length--;e.strstart++,e.ins_h=(e.ins_h<<e.hash_shift^e.window[e.strstart+x-1])&e.hash_mask,r=e.prev[e.strstart&e.w_mask]=e.head[e.ins_h],e.head[e.ins_h]=e.strstart,0!=--e.match_length;);e.strstart++}else e.strstart+=e.match_length,e.match_length=0,e.ins_h=e.window[e.strstart],e.ins_h=(e.ins_h<<e.hash_shift^e.window[e.strstart+1])&e.hash_mask;else n=u._tr_tally(e,0,e.window[e.strstart]),e.lookahead--,e.strstart++;if(n&&(N(e,!1),0===e.strm.avail_out))return A}return e.insert=e.strstart<x-1?e.strstart:x-1,t===f?(N(e,!0),0===e.strm.avail_out?O:B):e.last_lit&&(N(e,!1),0===e.strm.avail_out)?A:I}function W(e,t){for(var r,n,i;;){if(e.lookahead<z){if(j(e),e.lookahead<z&&t===l)return A;if(0===e.lookahead)break}if(r=0,e.lookahead>=x&&(e.ins_h=(e.ins_h<<e.hash_shift^e.window[e.strstart+x-1])&e.hash_mask,r=e.prev[e.strstart&e.w_mask]=e.head[e.ins_h],e.head[e.ins_h]=e.strstart),e.prev_length=e.match_length,e.prev_match=e.match_start,e.match_length=x-1,0!==r&&e.prev_length<e.max_lazy_match&&e.strstart-r<=e.w_size-z&&(e.match_length=L(e,r),e.match_length<=5&&(1===e.strategy||e.match_length===x&&4096<e.strstart-e.match_start)&&(e.match_length=x-1)),e.prev_length>=x&&e.match_length<=e.prev_length){for(i=e.strstart+e.lookahead-x,n=u._tr_tally(e,e.strstart-1-e.prev_match,e.prev_length-x),e.lookahead-=e.prev_length-1,e.prev_length-=2;++e.strstart<=i&&(e.ins_h=(e.ins_h<<e.hash_shift^e.window[e.strstart+x-1])&e.hash_mask,r=e.prev[e.strstart&e.w_mask]=e.head[e.ins_h],e.head[e.ins_h]=e.strstart),0!=--e.prev_length;);if(e.match_available=0,e.match_length=x-1,e.strstart++,n&&(N(e,!1),0===e.strm.avail_out))return A}else if(e.match_available){if((n=u._tr_tally(e,0,e.window[e.strstart-1]))&&N(e,!1),e.strstart++,e.lookahead--,0===e.strm.avail_out)return A}else e.match_available=1,e.strstart++,e.lookahead--}return e.match_available&&(n=u._tr_tally(e,0,e.window[e.strstart-1]),e.match_available=0),e.insert=e.strstart<x-1?e.strstart:x-1,t===f?(N(e,!0),0===e.strm.avail_out?O:B):e.last_lit&&(N(e,!1),0===e.strm.avail_out)?A:I}function M(e,t,r,n,i){this.good_length=e,this.max_lazy=t,this.nice_length=r,this.max_chain=n,this.func=i}function H(){this.strm=null,this.status=0,this.pending_buf=null,this.pending_buf_size=0,this.pending_out=0,this.pending=0,this.wrap=0,this.gzhead=null,this.gzindex=0,this.method=v,this.last_flush=-1,this.w_size=0,this.w_bits=0,this.w_mask=0,this.window=null,this.window_size=0,this.prev=null,this.head=null,this.ins_h=0,this.hash_size=0,this.hash_bits=0,this.hash_mask=0,this.hash_shift=0,this.block_start=0,this.match_length=0,this.prev_match=0,this.match_available=0,this.strstart=0,this.match_start=0,this.lookahead=0,this.prev_length=0,this.max_chain_length=0,this.max_lazy_match=0,this.level=0,this.strategy=0,this.good_match=0,this.nice_match=0,this.dyn_ltree=new c.Buf16(2*w),this.dyn_dtree=new c.Buf16(2*(2*a+1)),this.bl_tree=new c.Buf16(2*(2*o+1)),D(this.dyn_ltree),D(this.dyn_dtree),D(this.bl_tree),this.l_desc=null,this.d_desc=null,this.bl_desc=null,this.bl_count=new c.Buf16(k+1),this.heap=new c.Buf16(2*s+1),D(this.heap),this.heap_len=0,this.heap_max=0,this.depth=new c.Buf16(2*s+1),D(this.depth),this.l_buf=0,this.lit_bufsize=0,this.last_lit=0,this.d_buf=0,this.opt_len=0,this.static_len=0,this.matches=0,this.insert=0,this.bi_buf=0,this.bi_valid=0}function G(e){var t;return e&&e.state?(e.total_in=e.total_out=0,e.data_type=i,(t=e.state).pending=0,t.pending_out=0,t.wrap<0&&(t.wrap=-t.wrap),t.status=t.wrap?C:E,e.adler=2===t.wrap?0:1,t.last_flush=l,u._tr_init(t),m):R(e,_)}function K(e){var t=G(e);return t===m&&function(e){e.window_size=2*e.w_size,D(e.head),e.max_lazy_match=h[e.level].max_lazy,e.good_match=h[e.level].good_length,e.nice_match=h[e.level].nice_length,e.max_chain_length=h[e.level].max_chain,e.strstart=0,e.block_start=0,e.lookahead=0,e.insert=0,e.match_length=e.prev_length=x-1,e.match_available=0,e.ins_h=0}(e.state),t}function Y(e,t,r,n,i,s){if(!e)return _;var a=1;if(t===g&&(t=6),n<0?(a=0,n=-n):15<n&&(a=2,n-=16),i<1||y<i||r!==v||n<8||15<n||t<0||9<t||s<0||b<s)return R(e,_);8===n&&(n=9);var o=new H;return(e.state=o).strm=e,o.wrap=a,o.gzhead=null,o.w_bits=n,o.w_size=1<<o.w_bits,o.w_mask=o.w_size-1,o.hash_bits=i+7,o.hash_size=1<<o.hash_bits,o.hash_mask=o.hash_size-1,o.hash_shift=~~((o.hash_bits+x-1)/x),o.window=new c.Buf8(2*o.w_size),o.head=new c.Buf16(o.hash_size),o.prev=new c.Buf16(o.w_size),o.lit_bufsize=1<<i+6,o.pending_buf_size=4*o.lit_bufsize,o.pending_buf=new c.Buf8(o.pending_buf_size),o.d_buf=1*o.lit_bufsize,o.l_buf=3*o.lit_bufsize,o.level=t,o.strategy=s,o.method=r,K(e)}h=[new M(0,0,0,0,(function(e,t){var r=65535;for(r>e.pending_buf_size-5&&(r=e.pending_buf_size-5);;){if(e.lookahead<=1){if(j(e),0===e.lookahead&&t===l)return A;if(0===e.lookahead)break}e.strstart+=e.lookahead,e.lookahead=0;var n=e.block_start+r;if((0===e.strstart||e.strstart>=n)&&(e.lookahead=e.strstart-n,e.strstart=n,N(e,!1),0===e.strm.avail_out))return A;if(e.strstart-e.block_start>=e.w_size-z&&(N(e,!1),0===e.strm.avail_out))return A}return e.insert=0,t===f?(N(e,!0),0===e.strm.avail_out?O:B):(e.strstart>e.block_start&&(N(e,!1),e.strm.avail_out),A)})),new M(4,4,8,4,Z),new M(4,5,16,8,Z),new M(4,6,32,32,Z),new M(4,4,16,16,W),new M(8,16,32,32,W),new M(8,16,128,128,W),new M(8,32,128,256,W),new M(32,128,258,1024,W),new M(32,258,258,4096,W)],r.deflateInit=function(e,t){return Y(e,t,v,15,8,0)},r.deflateInit2=Y,r.deflateReset=K,r.deflateResetKeep=G,r.deflateSetHeader=function(e,t){return e&&e.state?2!==e.state.wrap?_:(e.state.gzhead=t,m):_},r.deflate=function(e,t){var r,n,i,s;if(!e||!e.state||5<t||t<0)return e?R(e,_):_;if(n=e.state,!e.output||!e.input&&0!==e.avail_in||666===n.status&&t!==f)return R(e,0===e.avail_out?-5:_);if(n.strm=e,r=n.last_flush,n.last_flush=t,n.status===C)if(2===n.wrap)e.adler=0,U(n,31),U(n,139),U(n,8),n.gzhead?(U(n,(n.gzhead.text?1:0)+(n.gzhead.hcrc?2:0)+(n.gzhead.extra?4:0)+(n.gzhead.name?8:0)+(n.gzhead.comment?16:0)),U(n,255&n.gzhead.time),U(n,n.gzhead.time>>8&255),U(n,n.gzhead.time>>16&255),U(n,n.gzhead.time>>24&255),U(n,9===n.level?2:2<=n.strategy||n.level<2?4:0),U(n,255&n.gzhead.os),n.gzhead.extra&&n.gzhead.extra.length&&(U(n,255&n.gzhead.extra.length),U(n,n.gzhead.extra.length>>8&255)),n.gzhead.hcrc&&(e.adler=p(e.adler,n.pending_buf,n.pending,0)),n.gzindex=0,n.status=69):(U(n,0),U(n,0),U(n,0),U(n,0),U(n,0),U(n,9===n.level?2:2<=n.strategy||n.level<2?4:0),U(n,3),n.status=E);else{var a=v+(n.w_bits-8<<4)<<8;a|=(2<=n.strategy||n.level<2?0:n.level<6?1:6===n.level?2:3)<<6,0!==n.strstart&&(a|=32),a+=31-a%31,n.status=E,P(n,a),0!==n.strstart&&(P(n,e.adler>>>16),P(n,65535&e.adler)),e.adler=1}if(69===n.status)if(n.gzhead.extra){for(i=n.pending;n.gzindex<(65535&n.gzhead.extra.length)&&(n.pending!==n.pending_buf_size||(n.gzhead.hcrc&&n.pending>i&&(e.adler=p(e.adler,n.pending_buf,n.pending-i,i)),F(e),i=n.pending,n.pending!==n.pending_buf_size));)U(n,255&n.gzhead.extra[n.gzindex]),n.gzindex++;n.gzhead.hcrc&&n.pending>i&&(e.adler=p(e.adler,n.pending_buf,n.pending-i,i)),n.gzindex===n.gzhead.extra.length&&(n.gzindex=0,n.status=73)}else n.status=73;if(73===n.status)if(n.gzhead.name){i=n.pending;do{if(n.pending===n.pending_buf_size&&(n.gzhead.hcrc&&n.pending>i&&(e.adler=p(e.adler,n.pending_buf,n.pending-i,i)),F(e),i=n.pending,n.pending===n.pending_buf_size)){s=1;break}s=n.gzindex<n.gzhead.name.length?255&n.gzhead.name.charCodeAt(n.gzindex++):0,U(n,s)}while(0!==s);n.gzhead.hcrc&&n.pending>i&&(e.adler=p(e.adler,n.pending_buf,n.pending-i,i)),0===s&&(n.gzindex=0,n.status=91)}else n.status=91;if(91===n.status)if(n.gzhead.comment){i=n.pending;do{if(n.pending===n.pending_buf_size&&(n.gzhead.hcrc&&n.pending>i&&(e.adler=p(e.adler,n.pending_buf,n.pending-i,i)),F(e),i=n.pending,n.pending===n.pending_buf_size)){s=1;break}s=n.gzindex<n.gzhead.comment.length?255&n.gzhead.comment.charCodeAt(n.gzindex++):0,U(n,s)}while(0!==s);n.gzhead.hcrc&&n.pending>i&&(e.adler=p(e.adler,n.pending_buf,n.pending-i,i)),0===s&&(n.status=103)}else n.status=103;if(103===n.status&&(n.gzhead.hcrc?(n.pending+2>n.pending_buf_size&&F(e),n.pending+2<=n.pending_buf_size&&(U(n,255&e.adler),U(n,e.adler>>8&255),e.adler=0,n.status=E)):n.status=E),0!==n.pending){if(F(e),0===e.avail_out)return n.last_flush=-1,m}else if(0===e.avail_in&&T(t)<=T(r)&&t!==f)return R(e,-5);if(666===n.status&&0!==e.avail_in)return R(e,-5);if(0!==e.avail_in||0!==n.lookahead||t!==l&&666!==n.status){var o=2===n.strategy?function(e,t){for(var r;;){if(0===e.lookahead&&(j(e),0===e.lookahead)){if(t===l)return A;break}if(e.match_length=0,r=u._tr_tally(e,0,e.window[e.strstart]),e.lookahead--,e.strstart++,r&&(N(e,!1),0===e.strm.avail_out))return A}return e.insert=0,t===f?(N(e,!0),0===e.strm.avail_out?O:B):e.last_lit&&(N(e,!1),0===e.strm.avail_out)?A:I}(n,t):3===n.strategy?function(e,t){for(var r,n,i,s,a=e.window;;){if(e.lookahead<=S){if(j(e),e.lookahead<=S&&t===l)return A;if(0===e.lookahead)break}if(e.match_length=0,e.lookahead>=x&&0<e.strstart&&(n=a[i=e.strstart-1])===a[++i]&&n===a[++i]&&n===a[++i]){s=e.strstart+S;do{}while(n===a[++i]&&n===a[++i]&&n===a[++i]&&n===a[++i]&&n===a[++i]&&n===a[++i]&&n===a[++i]&&n===a[++i]&&i<s);e.match_length=S-(s-i),e.match_length>e.lookahead&&(e.match_length=e.lookahead)}if(e.match_length>=x?(r=u._tr_tally(e,1,e.match_length-x),e.lookahead-=e.match_length,e.strstart+=e.match_length,e.match_length=0):(r=u._tr_tally(e,0,e.window[e.strstart]),e.lookahead--,e.strstart++),r&&(N(e,!1),0===e.strm.avail_out))return A}return e.insert=0,t===f?(N(e,!0),0===e.strm.avail_out?O:B):e.last_lit&&(N(e,!1),0===e.strm.avail_out)?A:I}(n,t):h[n.level].func(n,t);if(o!==O&&o!==B||(n.status=666),o===A||o===O)return 0===e.avail_out&&(n.last_flush=-1),m;if(o===I&&(1===t?u._tr_align(n):5!==t&&(u._tr_stored_block(n,0,0,!1),3===t&&(D(n.head),0===n.lookahead&&(n.strstart=0,n.block_start=0,n.insert=0))),F(e),0===e.avail_out))return n.last_flush=-1,m}return t!==f?m:n.wrap<=0?1:(2===n.wrap?(U(n,255&e.adler),U(n,e.adler>>8&255),U(n,e.adler>>16&255),U(n,e.adler>>24&255),U(n,255&e.total_in),U(n,e.total_in>>8&255),U(n,e.total_in>>16&255),U(n,e.total_in>>24&255)):(P(n,e.adler>>>16),P(n,65535&e.adler)),F(e),0<n.wrap&&(n.wrap=-n.wrap),0!==n.pending?m:1)},r.deflateEnd=function(e){var t;return e&&e.state?(t=e.state.status)!==C&&69!==t&&73!==t&&91!==t&&103!==t&&t!==E&&666!==t?R(e,_):(e.state=null,t===E?R(e,-3):m):_},r.deflateSetDictionary=function(e,t){var r,n,i,s,a,o,h,u,l=t.length;if(!e||!e.state)return _;if(2===(s=(r=e.state).wrap)||1===s&&r.status!==C||r.lookahead)return _;for(1===s&&(e.adler=d(e.adler,t,l,0)),r.wrap=0,l>=r.w_size&&(0===s&&(D(r.head),r.strstart=0,r.block_start=0,r.insert=0),u=new c.Buf8(r.w_size),c.arraySet(u,t,l-r.w_size,r.w_size,0),t=u,l=r.w_size),a=e.avail_in,o=e.next_in,h=e.input,e.avail_in=l,e.next_in=0,e.input=t,j(r);r.lookahead>=x;){for(n=r.strstart,i=r.lookahead-(x-1);r.ins_h=(r.ins_h<<r.hash_shift^r.window[n+x-1])&r.hash_mask,r.prev[n&r.w_mask]=r.head[r.ins_h],r.head[r.ins_h]=n,n++,--i;);r.strstart=n,r.lookahead=x-1,j(r)}return r.strstart+=r.lookahead,r.block_start=r.strstart,r.insert=r.lookahead,r.lookahead=0,r.match_length=r.prev_length=x-1,r.match_available=0,e.next_in=o,e.input=h,e.avail_in=a,r.wrap=s,m},r.deflateInfo="pako deflate (from Nodeca project)"},{"../utils/common":41,"./adler32":43,"./crc32":45,"./messages":51,"./trees":52}],47:[function(e,t,r){"use strict";t.exports=function(){this.text=0,this.time=0,this.xflags=0,this.os=0,this.extra=null,this.extra_len=0,this.name="",this.comment="",this.hcrc=0,this.done=!1}},{}],48:[function(e,t,r){"use strict";t.exports=function(e,t){var r,n,i,s,a,o,h,u,l,f,c,d,p,m,_,g,b,v,y,w,k,x,S,z,C;r=e.state,n=e.next_in,z=e.input,i=n+(e.avail_in-5),s=e.next_out,C=e.output,a=s-(t-e.avail_out),o=s+(e.avail_out-257),h=r.dmax,u=r.wsize,l=r.whave,f=r.wnext,c=r.window,d=r.hold,p=r.bits,m=r.lencode,_=r.distcode,g=(1<<r.lenbits)-1,b=(1<<r.distbits)-1;e:do{p<15&&(d+=z[n++]<<p,p+=8,d+=z[n++]<<p,p+=8),v=m[d&g];t:for(;;){if(d>>>=y=v>>>24,p-=y,0===(y=v>>>16&255))C[s++]=65535&v;else{if(!(16&y)){if(0==(64&y)){v=m[(65535&v)+(d&(1<<y)-1)];continue t}if(32&y){r.mode=12;break e}e.msg="invalid literal/length code",r.mode=30;break e}w=65535&v,(y&=15)&&(p<y&&(d+=z[n++]<<p,p+=8),w+=d&(1<<y)-1,d>>>=y,p-=y),p<15&&(d+=z[n++]<<p,p+=8,d+=z[n++]<<p,p+=8),v=_[d&b];r:for(;;){if(d>>>=y=v>>>24,p-=y,!(16&(y=v>>>16&255))){if(0==(64&y)){v=_[(65535&v)+(d&(1<<y)-1)];continue r}e.msg="invalid distance code",r.mode=30;break e}if(k=65535&v,p<(y&=15)&&(d+=z[n++]<<p,(p+=8)<y&&(d+=z[n++]<<p,p+=8)),h<(k+=d&(1<<y)-1)){e.msg="invalid distance too far back",r.mode=30;break e}if(d>>>=y,p-=y,(y=s-a)<k){if(l<(y=k-y)&&r.sane){e.msg="invalid distance too far back",r.mode=30;break e}if(S=c,(x=0)===f){if(x+=u-y,y<w){for(w-=y;C[s++]=c[x++],--y;);x=s-k,S=C}}else if(f<y){if(x+=u+f-y,(y-=f)<w){for(w-=y;C[s++]=c[x++],--y;);if(x=0,f<w){for(w-=y=f;C[s++]=c[x++],--y;);x=s-k,S=C}}}else if(x+=f-y,y<w){for(w-=y;C[s++]=c[x++],--y;);x=s-k,S=C}for(;2<w;)C[s++]=S[x++],C[s++]=S[x++],C[s++]=S[x++],w-=3;w&&(C[s++]=S[x++],1<w&&(C[s++]=S[x++]))}else{for(x=s-k;C[s++]=C[x++],C[s++]=C[x++],C[s++]=C[x++],2<(w-=3););w&&(C[s++]=C[x++],1<w&&(C[s++]=C[x++]))}break}}break}}while(n<i&&s<o);n-=w=p>>3,d&=(1<<(p-=w<<3))-1,e.next_in=n,e.next_out=s,e.avail_in=n<i?i-n+5:5-(n-i),e.avail_out=s<o?o-s+257:257-(s-o),r.hold=d,r.bits=p}},{}],49:[function(e,t,r){"use strict";var I=e("../utils/common"),O=e("./adler32"),B=e("./crc32"),R=e("./inffast"),T=e("./inftrees"),D=1,F=2,N=0,U=-2,P=1,n=852,i=592;function L(e){return(e>>>24&255)+(e>>>8&65280)+((65280&e)<<8)+((255&e)<<24)}function s(){this.mode=0,this.last=!1,this.wrap=0,this.havedict=!1,this.flags=0,this.dmax=0,this.check=0,this.total=0,this.head=null,this.wbits=0,this.wsize=0,this.whave=0,this.wnext=0,this.window=null,this.hold=0,this.bits=0,this.length=0,this.offset=0,this.extra=0,this.lencode=null,this.distcode=null,this.lenbits=0,this.distbits=0,this.ncode=0,this.nlen=0,this.ndist=0,this.have=0,this.next=null,this.lens=new I.Buf16(320),this.work=new I.Buf16(288),this.lendyn=null,this.distdyn=null,this.sane=0,this.back=0,this.was=0}function a(e){var t;return e&&e.state?(t=e.state,e.total_in=e.total_out=t.total=0,e.msg="",t.wrap&&(e.adler=1&t.wrap),t.mode=P,t.last=0,t.havedict=0,t.dmax=32768,t.head=null,t.hold=0,t.bits=0,t.lencode=t.lendyn=new I.Buf32(n),t.distcode=t.distdyn=new I.Buf32(i),t.sane=1,t.back=-1,N):U}function o(e){var t;return e&&e.state?((t=e.state).wsize=0,t.whave=0,t.wnext=0,a(e)):U}function h(e,t){var r,n;return e&&e.state?(n=e.state,t<0?(r=0,t=-t):(r=1+(t>>4),t<48&&(t&=15)),t&&(t<8||15<t)?U:(null!==n.window&&n.wbits!==t&&(n.window=null),n.wrap=r,n.wbits=t,o(e))):U}function u(e,t){var r,n;return e?(n=new s,(e.state=n).window=null,(r=h(e,t))!==N&&(e.state=null),r):U}var l,f,c=!0;function j(e){if(c){var t;for(l=new I.Buf32(512),f=new I.Buf32(32),t=0;t<144;)e.lens[t++]=8;for(;t<256;)e.lens[t++]=9;for(;t<280;)e.lens[t++]=7;for(;t<288;)e.lens[t++]=8;for(T(D,e.lens,0,288,l,0,e.work,{bits:9}),t=0;t<32;)e.lens[t++]=5;T(F,e.lens,0,32,f,0,e.work,{bits:5}),c=!1}e.lencode=l,e.lenbits=9,e.distcode=f,e.distbits=5}function Z(e,t,r,n){var i,s=e.state;return null===s.window&&(s.wsize=1<<s.wbits,s.wnext=0,s.whave=0,s.window=new I.Buf8(s.wsize)),n>=s.wsize?(I.arraySet(s.window,t,r-s.wsize,s.wsize,0),s.wnext=0,s.whave=s.wsize):(n<(i=s.wsize-s.wnext)&&(i=n),I.arraySet(s.window,t,r-n,i,s.wnext),(n-=i)?(I.arraySet(s.window,t,r-n,n,0),s.wnext=n,s.whave=s.wsize):(s.wnext+=i,s.wnext===s.wsize&&(s.wnext=0),s.whave<s.wsize&&(s.whave+=i))),0}r.inflateReset=o,r.inflateReset2=h,r.inflateResetKeep=a,r.inflateInit=function(e){return u(e,15)},r.inflateInit2=u,r.inflate=function(e,t){var r,n,i,s,a,o,h,u,l,f,c,d,p,m,_,g,b,v,y,w,k,x,S,z,C=0,E=new I.Buf8(4),A=[16,17,18,0,8,7,9,6,10,5,11,4,12,3,13,2,14,1,15];if(!e||!e.state||!e.output||!e.input&&0!==e.avail_in)return U;12===(r=e.state).mode&&(r.mode=13),a=e.next_out,i=e.output,h=e.avail_out,s=e.next_in,n=e.input,o=e.avail_in,u=r.hold,l=r.bits,f=o,c=h,x=N;e:for(;;)switch(r.mode){case P:if(0===r.wrap){r.mode=13;break}for(;l<16;){if(0===o)break e;o--,u+=n[s++]<<l,l+=8}if(2&r.wrap&&35615===u){E[r.check=0]=255&u,E[1]=u>>>8&255,r.check=B(r.check,E,2,0),l=u=0,r.mode=2;break}if(r.flags=0,r.head&&(r.head.done=!1),!(1&r.wrap)||(((255&u)<<8)+(u>>8))%31){e.msg="incorrect header check",r.mode=30;break}if(8!=(15&u)){e.msg="unknown compression method",r.mode=30;break}if(l-=4,k=8+(15&(u>>>=4)),0===r.wbits)r.wbits=k;else if(k>r.wbits){e.msg="invalid window size",r.mode=30;break}r.dmax=1<<k,e.adler=r.check=1,r.mode=512&u?10:12,l=u=0;break;case 2:for(;l<16;){if(0===o)break e;o--,u+=n[s++]<<l,l+=8}if(r.flags=u,8!=(255&r.flags)){e.msg="unknown compression method",r.mode=30;break}if(57344&r.flags){e.msg="unknown header flags set",r.mode=30;break}r.head&&(r.head.text=u>>8&1),512&r.flags&&(E[0]=255&u,E[1]=u>>>8&255,r.check=B(r.check,E,2,0)),l=u=0,r.mode=3;case 3:for(;l<32;){if(0===o)break e;o--,u+=n[s++]<<l,l+=8}r.head&&(r.head.time=u),512&r.flags&&(E[0]=255&u,E[1]=u>>>8&255,E[2]=u>>>16&255,E[3]=u>>>24&255,r.check=B(r.check,E,4,0)),l=u=0,r.mode=4;case 4:for(;l<16;){if(0===o)break e;o--,u+=n[s++]<<l,l+=8}r.head&&(r.head.xflags=255&u,r.head.os=u>>8),512&r.flags&&(E[0]=255&u,E[1]=u>>>8&255,r.check=B(r.check,E,2,0)),l=u=0,r.mode=5;case 5:if(1024&r.flags){for(;l<16;){if(0===o)break e;o--,u+=n[s++]<<l,l+=8}r.length=u,r.head&&(r.head.extra_len=u),512&r.flags&&(E[0]=255&u,E[1]=u>>>8&255,r.check=B(r.check,E,2,0)),l=u=0}else r.head&&(r.head.extra=null);r.mode=6;case 6:if(1024&r.flags&&(o<(d=r.length)&&(d=o),d&&(r.head&&(k=r.head.extra_len-r.length,r.head.extra||(r.head.extra=new Array(r.head.extra_len)),I.arraySet(r.head.extra,n,s,d,k)),512&r.flags&&(r.check=B(r.check,n,d,s)),o-=d,s+=d,r.length-=d),r.length))break e;r.length=0,r.mode=7;case 7:if(2048&r.flags){if(0===o)break e;for(d=0;k=n[s+d++],r.head&&k&&r.length<65536&&(r.head.name+=String.fromCharCode(k)),k&&d<o;);if(512&r.flags&&(r.check=B(r.check,n,d,s)),o-=d,s+=d,k)break e}else r.head&&(r.head.name=null);r.length=0,r.mode=8;case 8:if(4096&r.flags){if(0===o)break e;for(d=0;k=n[s+d++],r.head&&k&&r.length<65536&&(r.head.comment+=String.fromCharCode(k)),k&&d<o;);if(512&r.flags&&(r.check=B(r.check,n,d,s)),o-=d,s+=d,k)break e}else r.head&&(r.head.comment=null);r.mode=9;case 9:if(512&r.flags){for(;l<16;){if(0===o)break e;o--,u+=n[s++]<<l,l+=8}if(u!==(65535&r.check)){e.msg="header crc mismatch",r.mode=30;break}l=u=0}r.head&&(r.head.hcrc=r.flags>>9&1,r.head.done=!0),e.adler=r.check=0,r.mode=12;break;case 10:for(;l<32;){if(0===o)break e;o--,u+=n[s++]<<l,l+=8}e.adler=r.check=L(u),l=u=0,r.mode=11;case 11:if(0===r.havedict)return e.next_out=a,e.avail_out=h,e.next_in=s,e.avail_in=o,r.hold=u,r.bits=l,2;e.adler=r.check=1,r.mode=12;case 12:if(5===t||6===t)break e;case 13:if(r.last){u>>>=7&l,l-=7&l,r.mode=27;break}for(;l<3;){if(0===o)break e;o--,u+=n[s++]<<l,l+=8}switch(r.last=1&u,l-=1,3&(u>>>=1)){case 0:r.mode=14;break;case 1:if(j(r),r.mode=20,6!==t)break;u>>>=2,l-=2;break e;case 2:r.mode=17;break;case 3:e.msg="invalid block type",r.mode=30}u>>>=2,l-=2;break;case 14:for(u>>>=7&l,l-=7&l;l<32;){if(0===o)break e;o--,u+=n[s++]<<l,l+=8}if((65535&u)!=(u>>>16^65535)){e.msg="invalid stored block lengths",r.mode=30;break}if(r.length=65535&u,l=u=0,r.mode=15,6===t)break e;case 15:r.mode=16;case 16:if(d=r.length){if(o<d&&(d=o),h<d&&(d=h),0===d)break e;I.arraySet(i,n,s,d,a),o-=d,s+=d,h-=d,a+=d,r.length-=d;break}r.mode=12;break;case 17:for(;l<14;){if(0===o)break e;o--,u+=n[s++]<<l,l+=8}if(r.nlen=257+(31&u),u>>>=5,l-=5,r.ndist=1+(31&u),u>>>=5,l-=5,r.ncode=4+(15&u),u>>>=4,l-=4,286<r.nlen||30<r.ndist){e.msg="too many length or distance symbols",r.mode=30;break}r.have=0,r.mode=18;case 18:for(;r.have<r.ncode;){for(;l<3;){if(0===o)break e;o--,u+=n[s++]<<l,l+=8}r.lens[A[r.have++]]=7&u,u>>>=3,l-=3}for(;r.have<19;)r.lens[A[r.have++]]=0;if(r.lencode=r.lendyn,r.lenbits=7,S={bits:r.lenbits},x=T(0,r.lens,0,19,r.lencode,0,r.work,S),r.lenbits=S.bits,x){e.msg="invalid code lengths set",r.mode=30;break}r.have=0,r.mode=19;case 19:for(;r.have<r.nlen+r.ndist;){for(;g=(C=r.lencode[u&(1<<r.lenbits)-1])>>>16&255,b=65535&C,!((_=C>>>24)<=l);){if(0===o)break e;o--,u+=n[s++]<<l,l+=8}if(b<16)u>>>=_,l-=_,r.lens[r.have++]=b;else{if(16===b){for(z=_+2;l<z;){if(0===o)break e;o--,u+=n[s++]<<l,l+=8}if(u>>>=_,l-=_,0===r.have){e.msg="invalid bit length repeat",r.mode=30;break}k=r.lens[r.have-1],d=3+(3&u),u>>>=2,l-=2}else if(17===b){for(z=_+3;l<z;){if(0===o)break e;o--,u+=n[s++]<<l,l+=8}l-=_,k=0,d=3+(7&(u>>>=_)),u>>>=3,l-=3}else{for(z=_+7;l<z;){if(0===o)break e;o--,u+=n[s++]<<l,l+=8}l-=_,k=0,d=11+(127&(u>>>=_)),u>>>=7,l-=7}if(r.have+d>r.nlen+r.ndist){e.msg="invalid bit length repeat",r.mode=30;break}for(;d--;)r.lens[r.have++]=k}}if(30===r.mode)break;if(0===r.lens[256]){e.msg="invalid code -- missing end-of-block",r.mode=30;break}if(r.lenbits=9,S={bits:r.lenbits},x=T(D,r.lens,0,r.nlen,r.lencode,0,r.work,S),r.lenbits=S.bits,x){e.msg="invalid literal/lengths set",r.mode=30;break}if(r.distbits=6,r.distcode=r.distdyn,S={bits:r.distbits},x=T(F,r.lens,r.nlen,r.ndist,r.distcode,0,r.work,S),r.distbits=S.bits,x){e.msg="invalid distances set",r.mode=30;break}if(r.mode=20,6===t)break e;case 20:r.mode=21;case 21:if(6<=o&&258<=h){e.next_out=a,e.avail_out=h,e.next_in=s,e.avail_in=o,r.hold=u,r.bits=l,R(e,c),a=e.next_out,i=e.output,h=e.avail_out,s=e.next_in,n=e.input,o=e.avail_in,u=r.hold,l=r.bits,12===r.mode&&(r.back=-1);break}for(r.back=0;g=(C=r.lencode[u&(1<<r.lenbits)-1])>>>16&255,b=65535&C,!((_=C>>>24)<=l);){if(0===o)break e;o--,u+=n[s++]<<l,l+=8}if(g&&0==(240&g)){for(v=_,y=g,w=b;g=(C=r.lencode[w+((u&(1<<v+y)-1)>>v)])>>>16&255,b=65535&C,!(v+(_=C>>>24)<=l);){if(0===o)break e;o--,u+=n[s++]<<l,l+=8}u>>>=v,l-=v,r.back+=v}if(u>>>=_,l-=_,r.back+=_,r.length=b,0===g){r.mode=26;break}if(32&g){r.back=-1,r.mode=12;break}if(64&g){e.msg="invalid literal/length code",r.mode=30;break}r.extra=15&g,r.mode=22;case 22:if(r.extra){for(z=r.extra;l<z;){if(0===o)break e;o--,u+=n[s++]<<l,l+=8}r.length+=u&(1<<r.extra)-1,u>>>=r.extra,l-=r.extra,r.back+=r.extra}r.was=r.length,r.mode=23;case 23:for(;g=(C=r.distcode[u&(1<<r.distbits)-1])>>>16&255,b=65535&C,!((_=C>>>24)<=l);){if(0===o)break e;o--,u+=n[s++]<<l,l+=8}if(0==(240&g)){for(v=_,y=g,w=b;g=(C=r.distcode[w+((u&(1<<v+y)-1)>>v)])>>>16&255,b=65535&C,!(v+(_=C>>>24)<=l);){if(0===o)break e;o--,u+=n[s++]<<l,l+=8}u>>>=v,l-=v,r.back+=v}if(u>>>=_,l-=_,r.back+=_,64&g){e.msg="invalid distance code",r.mode=30;break}r.offset=b,r.extra=15&g,r.mode=24;case 24:if(r.extra){for(z=r.extra;l<z;){if(0===o)break e;o--,u+=n[s++]<<l,l+=8}r.offset+=u&(1<<r.extra)-1,u>>>=r.extra,l-=r.extra,r.back+=r.extra}if(r.offset>r.dmax){e.msg="invalid distance too far back",r.mode=30;break}r.mode=25;case 25:if(0===h)break e;if(d=c-h,r.offset>d){if((d=r.offset-d)>r.whave&&r.sane){e.msg="invalid distance too far back",r.mode=30;break}p=d>r.wnext?(d-=r.wnext,r.wsize-d):r.wnext-d,d>r.length&&(d=r.length),m=r.window}else m=i,p=a-r.offset,d=r.length;for(h<d&&(d=h),h-=d,r.length-=d;i[a++]=m[p++],--d;);0===r.length&&(r.mode=21);break;case 26:if(0===h)break e;i[a++]=r.length,h--,r.mode=21;break;case 27:if(r.wrap){for(;l<32;){if(0===o)break e;o--,u|=n[s++]<<l,l+=8}if(c-=h,e.total_out+=c,r.total+=c,c&&(e.adler=r.check=r.flags?B(r.check,i,c,a-c):O(r.check,i,c,a-c)),c=h,(r.flags?u:L(u))!==r.check){e.msg="incorrect data check",r.mode=30;break}l=u=0}r.mode=28;case 28:if(r.wrap&&r.flags){for(;l<32;){if(0===o)break e;o--,u+=n[s++]<<l,l+=8}if(u!==(4294967295&r.total)){e.msg="incorrect length check",r.mode=30;break}l=u=0}r.mode=29;case 29:x=1;break e;case 30:x=-3;break e;case 31:return-4;case 32:default:return U}return e.next_out=a,e.avail_out=h,e.next_in=s,e.avail_in=o,r.hold=u,r.bits=l,(r.wsize||c!==e.avail_out&&r.mode<30&&(r.mode<27||4!==t))&&Z(e,e.output,e.next_out,c-e.avail_out)?(r.mode=31,-4):(f-=e.avail_in,c-=e.avail_out,e.total_in+=f,e.total_out+=c,r.total+=c,r.wrap&&c&&(e.adler=r.check=r.flags?B(r.check,i,c,e.next_out-c):O(r.check,i,c,e.next_out-c)),e.data_type=r.bits+(r.last?64:0)+(12===r.mode?128:0)+(20===r.mode||15===r.mode?256:0),(0==f&&0===c||4===t)&&x===N&&(x=-5),x)},r.inflateEnd=function(e){if(!e||!e.state)return U;var t=e.state;return t.window&&(t.window=null),e.state=null,N},r.inflateGetHeader=function(e,t){var r;return e&&e.state?0==(2&(r=e.state).wrap)?U:((r.head=t).done=!1,N):U},r.inflateSetDictionary=function(e,t){var r,n=t.length;return e&&e.state?0!==(r=e.state).wrap&&11!==r.mode?U:11===r.mode&&O(1,t,n,0)!==r.check?-3:Z(e,t,n,n)?(r.mode=31,-4):(r.havedict=1,N):U},r.inflateInfo="pako inflate (from Nodeca project)"},{"../utils/common":41,"./adler32":43,"./crc32":45,"./inffast":48,"./inftrees":50}],50:[function(e,t,r){"use strict";var D=e("../utils/common"),F=[3,4,5,6,7,8,9,10,11,13,15,17,19,23,27,31,35,43,51,59,67,83,99,115,131,163,195,227,258,0,0],N=[16,16,16,16,16,16,16,16,17,17,17,17,18,18,18,18,19,19,19,19,20,20,20,20,21,21,21,21,16,72,78],U=[1,2,3,4,5,7,9,13,17,25,33,49,65,97,129,193,257,385,513,769,1025,1537,2049,3073,4097,6145,8193,12289,16385,24577,0,0],P=[16,16,16,16,17,17,18,18,19,19,20,20,21,21,22,22,23,23,24,24,25,25,26,26,27,27,28,28,29,29,64,64];t.exports=function(e,t,r,n,i,s,a,o){var h,u,l,f,c,d,p,m,_,g=o.bits,b=0,v=0,y=0,w=0,k=0,x=0,S=0,z=0,C=0,E=0,A=null,I=0,O=new D.Buf16(16),B=new D.Buf16(16),R=null,T=0;for(b=0;b<=15;b++)O[b]=0;for(v=0;v<n;v++)O[t[r+v]]++;for(k=g,w=15;1<=w&&0===O[w];w--);if(w<k&&(k=w),0===w)return i[s++]=20971520,i[s++]=20971520,o.bits=1,0;for(y=1;y<w&&0===O[y];y++);for(k<y&&(k=y),b=z=1;b<=15;b++)if(z<<=1,(z-=O[b])<0)return-1;if(0<z&&(0===e||1!==w))return-1;for(B[1]=0,b=1;b<15;b++)B[b+1]=B[b]+O[b];for(v=0;v<n;v++)0!==t[r+v]&&(a[B[t[r+v]]++]=v);if(d=0===e?(A=R=a,19):1===e?(A=F,I-=257,R=N,T-=257,256):(A=U,R=P,-1),b=y,c=s,S=v=E=0,l=-1,f=(C=1<<(x=k))-1,1===e&&852<C||2===e&&592<C)return 1;for(;;){for(p=b-S,_=a[v]<d?(m=0,a[v]):a[v]>d?(m=R[T+a[v]],A[I+a[v]]):(m=96,0),h=1<<b-S,y=u=1<<x;i[c+(E>>S)+(u-=h)]=p<<24|m<<16|_|0,0!==u;);for(h=1<<b-1;E&h;)h>>=1;if(0!==h?(E&=h-1,E+=h):E=0,v++,0==--O[b]){if(b===w)break;b=t[r+a[v]]}if(k<b&&(E&f)!==l){for(0===S&&(S=k),c+=y,z=1<<(x=b-S);x+S<w&&!((z-=O[x+S])<=0);)x++,z<<=1;if(C+=1<<x,1===e&&852<C||2===e&&592<C)return 1;i[l=E&f]=k<<24|x<<16|c-s|0}}return 0!==E&&(i[c+E]=b-S<<24|64<<16|0),o.bits=k,0}},{"../utils/common":41}],51:[function(e,t,r){"use strict";t.exports={2:"need dictionary",1:"stream end",0:"","-1":"file error","-2":"stream error","-3":"data error","-4":"insufficient memory","-5":"buffer error","-6":"incompatible version"}},{}],52:[function(e,t,r){"use strict";var i=e("../utils/common"),o=0,h=1;function n(e){for(var t=e.length;0<=--t;)e[t]=0}var s=0,a=29,u=256,l=u+1+a,f=30,c=19,_=2*l+1,g=15,d=16,p=7,m=256,b=16,v=17,y=18,w=[0,0,0,0,0,0,0,0,1,1,1,1,2,2,2,2,3,3,3,3,4,4,4,4,5,5,5,5,0],k=[0,0,0,0,1,1,2,2,3,3,4,4,5,5,6,6,7,7,8,8,9,9,10,10,11,11,12,12,13,13],x=[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,3,7],S=[16,17,18,0,8,7,9,6,10,5,11,4,12,3,13,2,14,1,15],z=new Array(2*(l+2));n(z);var C=new Array(2*f);n(C);var E=new Array(512);n(E);var A=new Array(256);n(A);var I=new Array(a);n(I);var O,B,R,T=new Array(f);function D(e,t,r,n,i){this.static_tree=e,this.extra_bits=t,this.extra_base=r,this.elems=n,this.max_length=i,this.has_stree=e&&e.length}function F(e,t){this.dyn_tree=e,this.max_code=0,this.stat_desc=t}function N(e){return e<256?E[e]:E[256+(e>>>7)]}function U(e,t){e.pending_buf[e.pending++]=255&t,e.pending_buf[e.pending++]=t>>>8&255}function P(e,t,r){e.bi_valid>d-r?(e.bi_buf|=t<<e.bi_valid&65535,U(e,e.bi_buf),e.bi_buf=t>>d-e.bi_valid,e.bi_valid+=r-d):(e.bi_buf|=t<<e.bi_valid&65535,e.bi_valid+=r)}function L(e,t,r){P(e,r[2*t],r[2*t+1])}function j(e,t){for(var r=0;r|=1&e,e>>>=1,r<<=1,0<--t;);return r>>>1}function Z(e,t,r){var n,i,s=new Array(g+1),a=0;for(n=1;n<=g;n++)s[n]=a=a+r[n-1]<<1;for(i=0;i<=t;i++){var o=e[2*i+1];0!==o&&(e[2*i]=j(s[o]++,o))}}function W(e){var t;for(t=0;t<l;t++)e.dyn_ltree[2*t]=0;for(t=0;t<f;t++)e.dyn_dtree[2*t]=0;for(t=0;t<c;t++)e.bl_tree[2*t]=0;e.dyn_ltree[2*m]=1,e.opt_len=e.static_len=0,e.last_lit=e.matches=0}function M(e){8<e.bi_valid?U(e,e.bi_buf):0<e.bi_valid&&(e.pending_buf[e.pending++]=e.bi_buf),e.bi_buf=0,e.bi_valid=0}function H(e,t,r,n){var i=2*t,s=2*r;return e[i]<e[s]||e[i]===e[s]&&n[t]<=n[r]}function G(e,t,r){for(var n=e.heap[r],i=r<<1;i<=e.heap_len&&(i<e.heap_len&&H(t,e.heap[i+1],e.heap[i],e.depth)&&i++,!H(t,n,e.heap[i],e.depth));)e.heap[r]=e.heap[i],r=i,i<<=1;e.heap[r]=n}function K(e,t,r){var n,i,s,a,o=0;if(0!==e.last_lit)for(;n=e.pending_buf[e.d_buf+2*o]<<8|e.pending_buf[e.d_buf+2*o+1],i=e.pending_buf[e.l_buf+o],o++,0===n?L(e,i,t):(L(e,(s=A[i])+u+1,t),0!==(a=w[s])&&P(e,i-=I[s],a),L(e,s=N(--n),r),0!==(a=k[s])&&P(e,n-=T[s],a)),o<e.last_lit;);L(e,m,t)}function Y(e,t){var r,n,i,s=t.dyn_tree,a=t.stat_desc.static_tree,o=t.stat_desc.has_stree,h=t.stat_desc.elems,u=-1;for(e.heap_len=0,e.heap_max=_,r=0;r<h;r++)0!==s[2*r]?(e.heap[++e.heap_len]=u=r,e.depth[r]=0):s[2*r+1]=0;for(;e.heap_len<2;)s[2*(i=e.heap[++e.heap_len]=u<2?++u:0)]=1,e.depth[i]=0,e.opt_len--,o&&(e.static_len-=a[2*i+1]);for(t.max_code=u,r=e.heap_len>>1;1<=r;r--)G(e,s,r);for(i=h;r=e.heap[1],e.heap[1]=e.heap[e.heap_len--],G(e,s,1),n=e.heap[1],e.heap[--e.heap_max]=r,e.heap[--e.heap_max]=n,s[2*i]=s[2*r]+s[2*n],e.depth[i]=(e.depth[r]>=e.depth[n]?e.depth[r]:e.depth[n])+1,s[2*r+1]=s[2*n+1]=i,e.heap[1]=i++,G(e,s,1),2<=e.heap_len;);e.heap[--e.heap_max]=e.heap[1],function(e,t){var r,n,i,s,a,o,h=t.dyn_tree,u=t.max_code,l=t.stat_desc.static_tree,f=t.stat_desc.has_stree,c=t.stat_desc.extra_bits,d=t.stat_desc.extra_base,p=t.stat_desc.max_length,m=0;for(s=0;s<=g;s++)e.bl_count[s]=0;for(h[2*e.heap[e.heap_max]+1]=0,r=e.heap_max+1;r<_;r++)p<(s=h[2*h[2*(n=e.heap[r])+1]+1]+1)&&(s=p,m++),h[2*n+1]=s,u<n||(e.bl_count[s]++,a=0,d<=n&&(a=c[n-d]),o=h[2*n],e.opt_len+=o*(s+a),f&&(e.static_len+=o*(l[2*n+1]+a)));if(0!==m){do{for(s=p-1;0===e.bl_count[s];)s--;e.bl_count[s]--,e.bl_count[s+1]+=2,e.bl_count[p]--,m-=2}while(0<m);for(s=p;0!==s;s--)for(n=e.bl_count[s];0!==n;)u<(i=e.heap[--r])||(h[2*i+1]!==s&&(e.opt_len+=(s-h[2*i+1])*h[2*i],h[2*i+1]=s),n--)}}(e,t),Z(s,u,e.bl_count)}function X(e,t,r){var n,i,s=-1,a=t[1],o=0,h=7,u=4;for(0===a&&(h=138,u=3),t[2*(r+1)+1]=65535,n=0;n<=r;n++)i=a,a=t[2*(n+1)+1],++o<h&&i===a||(o<u?e.bl_tree[2*i]+=o:0!==i?(i!==s&&e.bl_tree[2*i]++,e.bl_tree[2*b]++):o<=10?e.bl_tree[2*v]++:e.bl_tree[2*y]++,s=i,u=(o=0)===a?(h=138,3):i===a?(h=6,3):(h=7,4))}function V(e,t,r){var n,i,s=-1,a=t[1],o=0,h=7,u=4;for(0===a&&(h=138,u=3),n=0;n<=r;n++)if(i=a,a=t[2*(n+1)+1],!(++o<h&&i===a)){if(o<u)for(;L(e,i,e.bl_tree),0!=--o;);else 0!==i?(i!==s&&(L(e,i,e.bl_tree),o--),L(e,b,e.bl_tree),P(e,o-3,2)):o<=10?(L(e,v,e.bl_tree),P(e,o-3,3)):(L(e,y,e.bl_tree),P(e,o-11,7));s=i,u=(o=0)===a?(h=138,3):i===a?(h=6,3):(h=7,4)}}n(T);var q=!1;function J(e,t,r,n){P(e,(s<<1)+(n?1:0),3),function(e,t,r,n){M(e),n&&(U(e,r),U(e,~r)),i.arraySet(e.pending_buf,e.window,t,r,e.pending),e.pending+=r}(e,t,r,!0)}r._tr_init=function(e){q||(function(){var e,t,r,n,i,s=new Array(g+1);for(n=r=0;n<a-1;n++)for(I[n]=r,e=0;e<1<<w[n];e++)A[r++]=n;for(A[r-1]=n,n=i=0;n<16;n++)for(T[n]=i,e=0;e<1<<k[n];e++)E[i++]=n;for(i>>=7;n<f;n++)for(T[n]=i<<7,e=0;e<1<<k[n]-7;e++)E[256+i++]=n;for(t=0;t<=g;t++)s[t]=0;for(e=0;e<=143;)z[2*e+1]=8,e++,s[8]++;for(;e<=255;)z[2*e+1]=9,e++,s[9]++;for(;e<=279;)z[2*e+1]=7,e++,s[7]++;for(;e<=287;)z[2*e+1]=8,e++,s[8]++;for(Z(z,l+1,s),e=0;e<f;e++)C[2*e+1]=5,C[2*e]=j(e,5);O=new D(z,w,u+1,l,g),B=new D(C,k,0,f,g),R=new D(new Array(0),x,0,c,p)}(),q=!0),e.l_desc=new F(e.dyn_ltree,O),e.d_desc=new F(e.dyn_dtree,B),e.bl_desc=new F(e.bl_tree,R),e.bi_buf=0,e.bi_valid=0,W(e)},r._tr_stored_block=J,r._tr_flush_block=function(e,t,r,n){var i,s,a=0;0<e.level?(2===e.strm.data_type&&(e.strm.data_type=function(e){var t,r=4093624447;for(t=0;t<=31;t++,r>>>=1)if(1&r&&0!==e.dyn_ltree[2*t])return o;if(0!==e.dyn_ltree[18]||0!==e.dyn_ltree[20]||0!==e.dyn_ltree[26])return h;for(t=32;t<u;t++)if(0!==e.dyn_ltree[2*t])return h;return o}(e)),Y(e,e.l_desc),Y(e,e.d_desc),a=function(e){var t;for(X(e,e.dyn_ltree,e.l_desc.max_code),X(e,e.dyn_dtree,e.d_desc.max_code),Y(e,e.bl_desc),t=c-1;3<=t&&0===e.bl_tree[2*S[t]+1];t--);return e.opt_len+=3*(t+1)+5+5+4,t}(e),i=e.opt_len+3+7>>>3,(s=e.static_len+3+7>>>3)<=i&&(i=s)):i=s=r+5,r+4<=i&&-1!==t?J(e,t,r,n):4===e.strategy||s===i?(P(e,2+(n?1:0),3),K(e,z,C)):(P(e,4+(n?1:0),3),function(e,t,r,n){var i;for(P(e,t-257,5),P(e,r-1,5),P(e,n-4,4),i=0;i<n;i++)P(e,e.bl_tree[2*S[i]+1],3);V(e,e.dyn_ltree,t-1),V(e,e.dyn_dtree,r-1)}(e,e.l_desc.max_code+1,e.d_desc.max_code+1,a+1),K(e,e.dyn_ltree,e.dyn_dtree)),W(e),n&&M(e)},r._tr_tally=function(e,t,r){return e.pending_buf[e.d_buf+2*e.last_lit]=t>>>8&255,e.pending_buf[e.d_buf+2*e.last_lit+1]=255&t,e.pending_buf[e.l_buf+e.last_lit]=255&r,e.last_lit++,0===t?e.dyn_ltree[2*r]++:(e.matches++,t--,e.dyn_ltree[2*(A[r]+u+1)]++,e.dyn_dtree[2*N(t)]++),e.last_lit===e.lit_bufsize-1},r._tr_align=function(e){P(e,2,3),L(e,m,z),function(e){16===e.bi_valid?(U(e,e.bi_buf),e.bi_buf=0,e.bi_valid=0):8<=e.bi_valid&&(e.pending_buf[e.pending++]=255&e.bi_buf,e.bi_buf>>=8,e.bi_valid-=8)}(e)}},{"../utils/common":41}],53:[function(e,t,r){"use strict";t.exports=function(){this.input=null,this.next_in=0,this.avail_in=0,this.total_in=0,this.output=null,this.next_out=0,this.avail_out=0,this.total_out=0,this.msg="",this.state=null,this.data_type=2,this.adler=0}},{}],54:[function(e,t,r){(function(e){!function(r,n){"use strict";if(!r.setImmediate){var i,s,t,a,o=1,h={},u=!1,l=r.document,e=Object.getPrototypeOf&&Object.getPrototypeOf(r);e=e&&e.setTimeout?e:r,i="[object process]"==={}.toString.call(r.process)?function(e){process.nextTick((function(){c(e)}))}:function(){if(r.postMessage&&!r.importScripts){var e=!0,t=r.onmessage;return r.onmessage=function(){e=!1},r.postMessage("","*"),r.onmessage=t,e}}()?(a="setImmediate$"+Math.random()+"$",r.addEventListener?r.addEventListener("message",d,!1):r.attachEvent("onmessage",d),function(e){r.postMessage(a+e,"*")}):r.MessageChannel?((t=new MessageChannel).port1.onmessage=function(e){c(e.data)},function(e){t.port2.postMessage(e)}):l&&"onreadystatechange"in l.createElement("script")?(s=l.documentElement,function(e){var t=l.createElement("script");t.onreadystatechange=function(){c(e),t.onreadystatechange=null,s.removeChild(t),t=null},s.appendChild(t)}):function(e){setTimeout(c,0,e)},e.setImmediate=function(e){"function"!=typeof e&&(e=new Function(""+e));for(var t=new Array(arguments.length-1),r=0;r<t.length;r++)t[r]=arguments[r+1];var n={callback:e,args:t};return h[o]=n,i(o),o++},e.clearImmediate=f}function f(e){delete h[e]}function c(e){if(u)setTimeout(c,0,e);else{var t=h[e];if(t){u=!0;try{!function(e){var t=e.callback,r=e.args;switch(r.length){case 0:t();break;case 1:t(r[0]);break;case 2:t(r[0],r[1]);break;case 3:t(r[0],r[1],r[2]);break;default:t.apply(n,r)}}(t)}finally{f(e),u=!1}}}}function d(e){e.source===r&&"string"==typeof e.data&&0===e.data.indexOf(a)&&c(+e.data.slice(a.length))}}("undefined"==typeof self?void 0===e?this:e:self)}).call(this,"undefined"!=typeof __webpack_require__.g?__webpack_require__.g:"undefined"!=typeof self?self:"undefined"!=typeof window?window:{})},{}]},{},[10])(10)}));
/***/},
/***/744261:
/***/function(module,__unused_webpack_exports,__webpack_require__){
/* eslint no-console:0 */
/**
 * This is the main entry point for KaTeX. Here, we expose functions for
 * rendering expressions either to DOM nodes or to markup strings.
 *
 * We also expose the ParseError class to check if errors thrown from KaTeX are
 * errors in the expression, or errors in javascript handling.
 */
var ParseError=__webpack_require__(16856),Settings=__webpack_require__(594974),buildTree=__webpack_require__(502486),parseTree=__webpack_require__(581122),utils=__webpack_require__(25182),render=function(expression,baseNode,options){utils.clearNode(baseNode);var settings=new Settings(options),tree=parseTree(expression,settings),node=buildTree(tree,expression,settings).toNode();baseNode.appendChild(node)};
// KaTeX's styles don't work properly in quirks mode. Print out an error, and
// disable rendering.
"undefined"!==typeof document&&"CSS1Compat"!==document.compatMode&&(render=function(){throw new ParseError("KaTeX doesn't work in quirks mode.")})
/**
 * Parse and build an expression, and return the markup for that.
 */;var renderToString=function(expression,options){var settings=new Settings(options),tree=parseTree(expression,settings);return buildTree(tree,expression,settings).toMarkup()},generateParseTree=function(expression,options){var settings=new Settings(options);return parseTree(expression,settings)};
/**
 * Parse an expression and return the parse tree.
 */module.exports={render:render,renderToString:renderToString,
/**
     * NOTE: This method is not currently recommended for public use.
     * The internal tree representation is unstable and is very likely
     * to change. Use at your own risk.
     */
__parse:generateParseTree,ParseError:ParseError}},
/***/227584:
/***/function(module,__unused_webpack_exports,__webpack_require__){
/**
 * The Lexer class handles tokenizing the input in various ways. Since our
 * parser expects us to be able to backtrack, the lexer allows lexing from any
 * given starting point.
 *
 * Its main exposed function is the `lex` function, which takes a position to
 * lex from and a type of token to lex. It defers to the appropriate `_innerLex`
 * function.
 *
 * The various `_innerLex` functions perform the actual lexing of different
 * kinds.
 */
var matchAt=__webpack_require__(661412),ParseError=__webpack_require__(16856);
// The main lexer class
function Lexer(input){this._input=input}
// The resulting token returned from `lex`.
function Token(text,data,position){this.text=text,this.data=data,this.position=position}
/* The following tokenRegex
 * - matches typical whitespace (but not NBSP etc.) using its first group
 * - matches symbol combinations which result in a single output character
 * - does not match any control character \x00-\x1f except whitespace
 * - does not match a bare backslash
 * - matches any ASCII character except those just mentioned
 * - does not match the BMP private use area \uE000-\uF8FF
 * - does not match bare surrogate code units
 * - matches any BMP character except for those just described
 * - matches any valid Unicode surrogate pair
 * - matches a backslash followed by one or more letters
 * - matches a backslash followed by any BMP character, including newline
 * Just because the Lexer matches something doesn't mean it's valid input:
 * If there is no matching function or symbol definition, the Parser will
 * still reject the input.
 */var tokenRegex=new RegExp("([ \r\n\t]+)|(---?|[!-\\[\\]-‧‪-퟿豈-￿]|[\ud800-\udbff][\udc00-\udfff]|\\\\(?:[a-zA-Z]+|[^\ud800-\udfff]))"),whitespaceRegex=/\s*/;
/**
 * This function lexes a single normal token. It takes a position and
 * whether it should completely ignore whitespace or not.
 */
Lexer.prototype._innerLex=function(pos,ignoreWhitespace){var input=this._input;if(pos===input.length)return new Token("EOF",null,pos);var match=matchAt(tokenRegex,input,pos);if(null===match)throw new ParseError("Unexpected character: '"+input[pos]+"'",this,pos);return match[2]?new Token(match[2],null,pos+match[2].length):ignoreWhitespace?this._innerLex(pos+match[1].length,!0):new Token(" ",null,pos+match[1].length)};
// A regex to match a CSS color (like #ffffff or BlueViolet)
var cssColor=/#[a-z0-9]+|[a-z]+/i;
/**
 * This function lexes a CSS color.
 */Lexer.prototype._innerLexColor=function(pos){var match,input=this._input,whitespace=matchAt(whitespaceRegex,input,pos)[0];
// Ignore whitespace
if(pos+=whitespace.length,match=matchAt(cssColor,input,pos))
// If we look like a color, return a color
return new Token(match[0],null,pos+match[0].length);throw new ParseError("Invalid color",this,pos)};
// A regex to match a dimension. Dimensions look like
// "1.2em" or ".4pt" or "1 ex"
var sizeRegex=/(-?)\s*(\d+(?:\.\d*)?|\.\d+)\s*([a-z]{2})/;
/**
 * This function lexes a dimension.
 */Lexer.prototype._innerLexSize=function(pos){var match,input=this._input,whitespace=matchAt(whitespaceRegex,input,pos)[0];
// Ignore whitespace
if(pos+=whitespace.length,match=matchAt(sizeRegex,input,pos)){var unit=match[3];
// We only currently handle "em" and "ex" units
if("em"!==unit&&"ex"!==unit)throw new ParseError("Invalid unit: '"+unit+"'",this,pos);return new Token(match[0],{number:+(match[1]+match[2]),unit:unit},pos+match[0].length)}throw new ParseError("Invalid size",this,pos)},
/**
 * This function lexes a string of whitespace.
 */
Lexer.prototype._innerLexWhitespace=function(pos){var input=this._input,whitespace=matchAt(whitespaceRegex,input,pos)[0];return pos+=whitespace.length,new Token(whitespace[0],null,pos)},
/**
 * This function lexes a single token starting at `pos` and of the given mode.
 * Based on the mode, we defer to one of the `_innerLex` functions.
 */
Lexer.prototype.lex=function(pos,mode){return"math"===mode?this._innerLex(pos,!0):"text"===mode?this._innerLex(pos,!1):"color"===mode?this._innerLexColor(pos):"size"===mode?this._innerLexSize(pos):"whitespace"===mode?this._innerLexWhitespace(pos):void 0},module.exports=Lexer},
/***/757599:
/***/function(module){
/**
 * This file contains information about the options that the Parser carries
 * around with it while parsing. Data is held in an `Options` object, and when
 * recursing, a new `Options` object can be created with the `.with*` and
 * `.reset` functions.
 */
/**
 * This is the main options class. It contains the style, size, color, and font
 * of the current parse level. It also contains the style and size of the parent
 * parse level, so size changes can be handled efficiently.
 *
 * Each of the `.with*` and `.reset` functions passes its current style and size
 * as the parentStyle and parentSize of the new options class, so parent
 * handling is taken care of automatically.
 */
function Options(data){this.style=data.style,this.color=data.color,this.size=data.size,this.phantom=data.phantom,this.font=data.font,void 0===data.parentStyle?this.parentStyle=data.style:this.parentStyle=data.parentStyle,void 0===data.parentSize?this.parentSize=data.size:this.parentSize=data.parentSize}
/**
 * Returns a new options object with the same properties as "this".  Properties
 * from "extension" will be copied to the new options object.
 */Options.prototype.extend=function(extension){var data={style:this.style,size:this.size,color:this.color,parentStyle:this.style,parentSize:this.size,phantom:this.phantom,font:this.font};for(var key in extension)extension.hasOwnProperty(key)&&(data[key]=extension[key]);return new Options(data)},
/**
 * Create a new options object with the given style.
 */
Options.prototype.withStyle=function(style){return this.extend({style:style})},
/**
 * Create a new options object with the given size.
 */
Options.prototype.withSize=function(size){return this.extend({size:size})},
/**
 * Create a new options object with the given color.
 */
Options.prototype.withColor=function(color){return this.extend({color:color})},
/**
 * Create a new options object with "phantom" set to true.
 */
Options.prototype.withPhantom=function(){return this.extend({phantom:!0})},
/**
 * Create a new options objects with the give font.
 */
Options.prototype.withFont=function(font){return this.extend({font:font})},
/**
 * Create a new options object with the same style, size, and color. This is
 * used so that parent style and size changes are handled correctly.
 */
Options.prototype.reset=function(){return this.extend({})};
/**
 * A map of color names to CSS colors.
 * TODO(emily): Remove this when we have real macros
 */
var colorMap={"katex-blue":"#6495ed","katex-orange":"#ffa500","katex-pink":"#ff00af","katex-red":"#df0030","katex-green":"#28ae7b","katex-gray":"gray","katex-purple":"#9d38bd","katex-blueA":"#c7e9f1","katex-blueB":"#9cdceb","katex-blueC":"#58c4dd","katex-blueD":"#29abca","katex-blueE":"#1c758a","katex-tealA":"#acead7","katex-tealB":"#76ddc0","katex-tealC":"#5cd0b3","katex-tealD":"#55c1a7","katex-tealE":"#49a88f","katex-greenA":"#c9e2ae","katex-greenB":"#a6cf8c","katex-greenC":"#83c167","katex-greenD":"#77b05d","katex-greenE":"#699c52","katex-goldA":"#f7c797","katex-goldB":"#f9b775","katex-goldC":"#f0ac5f","katex-goldD":"#e1a158","katex-goldE":"#c78d46","katex-redA":"#f7a1a3","katex-redB":"#ff8080","katex-redC":"#fc6255","katex-redD":"#e65a4c","katex-redE":"#cf5044","katex-maroonA":"#ecabc1","katex-maroonB":"#ec92ab","katex-maroonC":"#c55f73","katex-maroonD":"#a24d61","katex-maroonE":"#94424f","katex-purpleA":"#caa3e8","katex-purpleB":"#b189c6","katex-purpleC":"#9a72ac","katex-purpleD":"#715582","katex-purpleE":"#644172","katex-mintA":"#f5f9e8","katex-mintB":"#edf2df","katex-mintC":"#e0e5cc","katex-grayA":"#fdfdfd","katex-grayB":"#f7f7f7","katex-grayC":"#eeeeee","katex-grayD":"#dddddd","katex-grayE":"#cccccc","katex-grayF":"#aaaaaa","katex-grayG":"#999999","katex-grayH":"#555555","katex-grayI":"#333333","katex-kaBlue":"#314453","katex-kaGreen":"#639b24"};
/**
 * Gets the CSS color of the current options object, accounting for the
 * `colorMap`.
 */Options.prototype.getColor=function(){return this.phantom?"transparent":colorMap[this.color]||this.color},module.exports=Options},
/***/16856:
/***/function(module){
/**
 * This is the ParseError class, which is the main error thrown by KaTeX
 * functions when something has gone wrong. This is used to distinguish internal
 * errors from errors in the expression that the user provided.
 */
function ParseError(message,lexer,position){var error="KaTeX parse error: "+message;if(void 0!==lexer&&void 0!==position){
// If we have the input and a position, make the error a bit fancier
// Prepend some information
error+=" at position "+position+": ";
// Get the input
var input=lexer._input;
// Insert a combining underscore at the correct position
input=input.slice(0,position)+"̲"+input.slice(position);
// Extract some context from the input and add it to the error
var begin=Math.max(0,position-15),end=position+15;error+=input.slice(begin,end)}
// Some hackery to make ParseError a prototype of Error
// See http://stackoverflow.com/a/8460753
var self=new Error(error);return self.name="ParseError",self.__proto__=ParseError.prototype,self.position=position,self}
// More hackery
ParseError.prototype.__proto__=Error.prototype,module.exports=ParseError},
/***/762717:
/***/function(module,__unused_webpack_exports,__webpack_require__){
/* eslint no-constant-condition:0 */
var functions=__webpack_require__(15193),environments=__webpack_require__(345707),Lexer=__webpack_require__(227584),symbols=__webpack_require__(710471),utils=__webpack_require__(25182),parseData=__webpack_require__(791758),ParseError=__webpack_require__(16856);
/**
 * This file contains the parser used to parse out a TeX expression from the
 * input. Since TeX isn't context-free, standard parsers don't work particularly
 * well.
 *
 * The strategy of this parser is as such:
 *
 * The main functions (the `.parse...` ones) take a position in the current
 * parse string to parse tokens from. The lexer (found in Lexer.js, stored at
 * this.lexer) also supports pulling out tokens at arbitrary places. When
 * individual tokens are needed at a position, the lexer is called to pull out a
 * token, which is then used.
 *
 * The parser has a property called "mode" indicating the mode that
 * the parser is currently in. Currently it has to be one of "math" or
 * "text", which denotes whether the current environment is a math-y
 * one or a text-y one (e.g. inside \text). Currently, this serves to
 * limit the functions which can be used in text mode.
 *
 * The main functions then return an object which contains the useful data that
 * was parsed at its given point, and a new position at the end of the parsed
 * data. The main functions can call each other and continue the parsing by
 * using the returned position as a new starting point.
 *
 * There are also extra `.handle...` functions, which pull out some reused
 * functionality into self-contained functions.
 *
 * The earlier functions return ParseNodes.
 * The later functions (which are called deeper in the parse) sometimes return
 * ParseFuncOrArgument, which contain a ParseNode as well as some data about
 * whether the parsed object is a function which is missing some arguments, or a
 * standalone object which can be used as an argument to another function.
 */
/**
 * Main Parser class
 */
function Parser(input,settings){
// Make a new lexer
this.lexer=new Lexer(input),
// Store the settings for use in parsing
this.settings=settings}var ParseNode=parseData.ParseNode;
/**
 * An initial function (without its arguments), or an argument to a function.
 * The `result` argument should be a ParseNode.
 */function ParseFuncOrArgument(result,isFunction){this.result=result,
// Is this a function (i.e. is it something defined in functions.js)?
this.isFunction=isFunction}
/**
 * Checks a result to make sure it has the right type, and throws an
 * appropriate error otherwise.
 *
 * @param {boolean=} consume whether to consume the expected token,
 *                           defaults to true
 */Parser.prototype.expect=function(text,consume){if(this.nextToken.text!==text)throw new ParseError("Expected '"+text+"', got '"+this.nextToken.text+"'",this.lexer,this.nextToken.position);!1!==consume&&this.consume()},
/**
 * Considers the current look ahead token as consumed,
 * and fetches the one after that as the new look ahead.
 */
Parser.prototype.consume=function(){this.pos=this.nextToken.position,this.nextToken=this.lexer.lex(this.pos,this.mode)},
/**
 * Main parsing function, which parses an entire input.
 *
 * @return {?Array.<ParseNode>}
 */
Parser.prototype.parse=function(){
// Try to parse the input
this.mode="math",this.pos=0,this.nextToken=this.lexer.lex(this.pos,this.mode);var parse=this.parseInput();return parse},
/**
 * Parses an entire input tree.
 */
Parser.prototype.parseInput=function(){
// Parse an expression
var expression=this.parseExpression(!1);
// If we succeeded, make sure there's an EOF at the end
return this.expect("EOF",!1),expression};var endOfExpression=["}","\\end","\\right","&","\\\\","\\cr"];
/**
 * Parses an "expression", which is a list of atoms.
 *
 * @param {boolean} breakOnInfix Should the parsing stop when we hit infix
 *                  nodes? This happens when functions have higher precendence
 *                  than infix nodes in implicit parses.
 *
 * @param {?string} breakOnToken The token that the expression should end with,
 *                  or `null` if something else should end the expression.
 *
 * @return {ParseNode}
 */Parser.prototype.parseExpression=function(breakOnInfix,breakOnToken){var body=[];
// Keep adding atoms to the body until we can't parse any more atoms (either
// we reached the end, a }, or a \right)
while(1){var lex=this.nextToken,pos=this.pos;if(-1!==endOfExpression.indexOf(lex.text))break;if(breakOnToken&&lex.text===breakOnToken)break;var atom=this.parseAtom();if(!atom){if(!this.settings.throwOnError&&"\\"===lex.text[0]){var errorNode=this.handleUnsupportedCmd();body.push(errorNode),pos=lex.position;continue}break}if(breakOnInfix&&"infix"===atom.type){
// rewind so we can parse the infix atom again
this.pos=pos,this.nextToken=lex;break}body.push(atom)}return this.handleInfixNodes(body)},
/**
 * Rewrites infix operators such as \over with corresponding commands such
 * as \frac.
 *
 * There can only be one infix operator per group.  If there's more than one
 * then the expression is ambiguous.  This can be resolved by adding {}.
 *
 * @returns {Array}
 */
Parser.prototype.handleInfixNodes=function(body){for(var funcName,overIndex=-1,i=0;i<body.length;i++){var node=body[i];if("infix"===node.type){if(-1!==overIndex)throw new ParseError("only one infix operator per group",this.lexer,-1);overIndex=i,funcName=node.value.replaceWith}}if(-1!==overIndex){var numerNode,denomNode,numerBody=body.slice(0,overIndex),denomBody=body.slice(overIndex+1);numerNode=1===numerBody.length&&"ordgroup"===numerBody[0].type?numerBody[0]:new ParseNode("ordgroup",numerBody,this.mode),denomNode=1===denomBody.length&&"ordgroup"===denomBody[0].type?denomBody[0]:new ParseNode("ordgroup",denomBody,this.mode);var value=this.callFunction(funcName,[numerNode,denomNode],null);return[new ParseNode(value.type,value,this.mode)]}return body};
// The greediness of a superscript or subscript
var SUPSUB_GREEDINESS=1;
/**
 * Handle a subscript or superscript with nice errors.
 */Parser.prototype.handleSupSubscript=function(name){var symbol=this.nextToken.text,symPos=this.pos;this.consume();var group=this.parseGroup();if(group){if(group.isFunction){
// ^ and _ have a greediness, so handle interactions with functions'
// greediness
var funcGreediness=functions[group.result].greediness;if(funcGreediness>SUPSUB_GREEDINESS)return this.parseFunction(group);throw new ParseError("Got function '"+group.result+"' with no arguments as "+name,this.lexer,symPos+1)}return group.result}if(this.settings.throwOnError||"\\"!==this.nextToken.text[0])throw new ParseError("Expected group after '"+symbol+"'",this.lexer,symPos+1);return this.handleUnsupportedCmd()},
/**
 * Converts the textual input of an unsupported command into a text node
 * contained within a color node whose color is determined by errorColor
 */
Parser.prototype.handleUnsupportedCmd=function(){for(var text=this.nextToken.text,textordArray=[],i=0;i<text.length;i++)textordArray.push(new ParseNode("textord",text[i],"text"));var textNode=new ParseNode("text",{body:textordArray,type:"text"},this.mode),colorNode=new ParseNode("color",{color:this.settings.errorColor,value:[textNode],type:"color"},this.mode);return this.consume(),colorNode},
/**
 * Parses a group with optional super/subscripts.
 *
 * @return {?ParseNode}
 */
Parser.prototype.parseAtom=function(){
// The body of an atom is an implicit group, so that things like
// \left(x\right)^2 work correctly.
var superscript,subscript,base=this.parseImplicitGroup();
// In text mode, we don't have superscripts or subscripts
if("text"===this.mode)return base;
// Note that base may be empty (i.e. null) at this point.
while(1){
// Lex the first token
var lex=this.nextToken;if("\\limits"===lex.text||"\\nolimits"===lex.text){
// We got a limit control
if(!base||"op"!==base.type)throw new ParseError("Limit controls must follow a math operator",this.lexer,this.pos);var limits="\\limits"===lex.text;base.value.limits=limits,base.value.alwaysHandleSupSub=!0,this.consume()}else if("^"===lex.text){
// We got a superscript start
if(superscript)throw new ParseError("Double superscript",this.lexer,this.pos);superscript=this.handleSupSubscript("superscript")}else if("_"===lex.text){
// We got a subscript start
if(subscript)throw new ParseError("Double subscript",this.lexer,this.pos);subscript=this.handleSupSubscript("subscript")}else{if("'"!==lex.text)
// If it wasn't ^, _, or ', stop parsing super/subscripts
break;
// We got a prime
var prime=new ParseNode("textord","\\prime",this.mode),primes=[prime];
// Many primes can be grouped together, so we handle this here
this.consume();
// Keep lexing tokens until we get something that's not a prime
while("'"===this.nextToken.text)
// For each one, add another prime to the list
primes.push(prime),this.consume();
// Put them into an ordgroup as the superscript
superscript=new ParseNode("ordgroup",primes,this.mode)}}return superscript||subscript?new ParseNode("supsub",{base:base,sup:superscript,sub:subscript},this.mode):base};
// A list of the size-changing functions, for use in parseImplicitGroup
var sizeFuncs=["\\tiny","\\scriptsize","\\footnotesize","\\small","\\normalsize","\\large","\\Large","\\LARGE","\\huge","\\Huge"],styleFuncs=["\\displaystyle","\\textstyle","\\scriptstyle","\\scriptscriptstyle"];
// A list of the style-changing functions, for use in parseImplicitGroup
/**
 * Parses an implicit group, which is a group that starts at the end of a
 * specified, and ends right before a higher explicit group ends, or at EOL. It
 * is used for functions that appear to affect the current style, like \Large or
 * \textrm, where instead of keeping a style we just pretend that there is an
 * implicit grouping after it until the end of the group. E.g.
 *   small text {\Large large text} small text again
 * It is also used for \left and \right to get the correct grouping.
 *
 * @return {?ParseNode}
 */
Parser.prototype.parseImplicitGroup=function(){var start=this.parseSymbol();if(null==start)
// If we didn't get anything we handle, fall back to parseFunction
return this.parseFunction();var body,func=start.result;if("\\left"===func){
// If we see a left:
// Parse the entire left function (including the delimiter)
var left=this.parseFunction(start);
// Parse out the implicit body
body=this.parseExpression(!1),
// Check the next token
this.expect("\\right",!1);var right=this.parseFunction();return new ParseNode("leftright",{body:body,left:left.value.value,right:right.value.value},this.mode)}if("\\begin"===func){
// begin...end is similar to left...right
var begin=this.parseFunction(start),envName=begin.value.name;if(!environments.hasOwnProperty(envName))throw new ParseError("No such environment: "+envName,this.lexer,begin.value.namepos);
// Build the environment object. Arguments and other information will
// be made available to the begin and end methods using properties.
var env=environments[envName],args=this.parseArguments("\\begin{"+envName+"}",env),context={mode:this.mode,envName:envName,parser:this,lexer:this.lexer,positions:args.pop()},result=env.handler(context,args);this.expect("\\end",!1);var end=this.parseFunction();if(end.value.name!==envName)throw new ParseError("Mismatch: \\begin{"+envName+"} matched by \\end{"+end.value.name+"}",this.lexer/* , end.value.namepos */);
// TODO: Add position to the above line and adjust test case,
// requires #385 to get merged first
return result.position=end.position,result}return utils.contains(sizeFuncs,func)?(
// If we see a sizing function, parse out the implict body
body=this.parseExpression(!1),new ParseNode("sizing",{
// Figure out what size to use based on the list of functions above
size:"size"+(utils.indexOf(sizeFuncs,func)+1),value:body},this.mode)):utils.contains(styleFuncs,func)?(
// If we see a styling function, parse out the implict body
body=this.parseExpression(!0),new ParseNode("styling",{
// Figure out what style to use by pulling out the style from
// the function name
style:func.slice(1,func.length-5),value:body},this.mode)):this.parseFunction(start)},
/**
 * Parses an entire function, including its base and all of its arguments.
 * The base might either have been parsed already, in which case
 * it is provided as an argument, or it's the next group in the input.
 *
 * @param {ParseFuncOrArgument=} baseGroup optional as described above
 * @return {?ParseNode}
 */
Parser.prototype.parseFunction=function(baseGroup){if(baseGroup||(baseGroup=this.parseGroup()),baseGroup){if(baseGroup.isFunction){var func=baseGroup.result,funcData=functions[func];if("text"===this.mode&&!funcData.allowedInText)throw new ParseError("Can't use function '"+func+"' in text mode",this.lexer,baseGroup.position);var args=this.parseArguments(func,funcData),result=this.callFunction(func,args,args.pop());return new ParseNode(result.type,result,this.mode)}return baseGroup.result}return null},
/**
 * Call a function handler with a suitable context and arguments.
 */
Parser.prototype.callFunction=function(name,args,positions){var context={funcName:name,parser:this,lexer:this.lexer,positions:positions};return functions[name].handler(context,args)},
/**
 * Parses the arguments of a function or environment
 *
 * @param {string} func  "\name" or "\begin{name}"
 * @param {{numArgs:number,numOptionalArgs:number|undefined}} funcData
 * @return the array of arguments, with the list of positions as last element
 */
Parser.prototype.parseArguments=function(func,funcData){var totalArgs=funcData.numArgs+funcData.numOptionalArgs;if(0===totalArgs)return[[this.pos]];for(var baseGreediness=funcData.greediness,positions=[this.pos],args=[],i=0;i<totalArgs;i++){var arg,argNode,argType=funcData.argTypes&&funcData.argTypes[i];if(i<funcData.numOptionalArgs){if(arg=argType?this.parseSpecialGroup(argType,!0):this.parseOptionalGroup(),!arg){args.push(null),positions.push(this.pos);continue}}else if(arg=argType?this.parseSpecialGroup(argType):this.parseGroup(),!arg){if(this.settings.throwOnError||"\\"!==this.nextToken.text[0])throw new ParseError("Expected group after '"+func+"'",this.lexer,this.pos);arg=new ParseFuncOrArgument(this.handleUnsupportedCmd(this.nextToken.text),!1)}if(arg.isFunction){var argGreediness=functions[arg.result].greediness;if(!(argGreediness>baseGreediness))throw new ParseError("Got function '"+arg.result+"' as argument to '"+func+"'",this.lexer,this.pos-1);argNode=this.parseFunction(arg)}else argNode=arg.result;args.push(argNode),positions.push(this.pos)}return args.push(positions),args},
/**
 * Parses a group when the mode is changing. Takes a position, a new mode, and
 * an outer mode that is used to parse the outside.
 *
 * @return {?ParseFuncOrArgument}
 */
Parser.prototype.parseSpecialGroup=function(innerMode,optional){var res,outerMode=this.mode;
// Handle `original` argTypes
if("original"===innerMode&&(innerMode=outerMode),"color"===innerMode||"size"===innerMode){
// color and size modes are special because they should have braces and
// should only lex a single symbol inside
var openBrace=this.nextToken;if(optional&&"["!==openBrace.text)
// optional arguments should return null if they don't exist
return null;
// The call to expect will lex the token after the '{' in inner mode
this.mode=innerMode,this.expect(optional?"[":"{");var data,inner=this.nextToken;return this.mode=outerMode,data="color"===innerMode?inner.text:inner.data,this.consume(),// consume the token stored in inner
this.expect(optional?"]":"}"),new ParseFuncOrArgument(new ParseNode(innerMode,data,outerMode),!1)}
// By the time we get here, innerMode is one of "text" or "math".
// We switch the mode of the parser, recurse, then restore the old mode.
if("text"===innerMode){
// text mode is special because it should ignore the whitespace before
// it
var whitespace=this.lexer.lex(this.pos,"whitespace");this.pos=whitespace.position}return this.mode=innerMode,this.nextToken=this.lexer.lex(this.pos,innerMode),res=optional?this.parseOptionalGroup():this.parseGroup(),this.mode=outerMode,this.nextToken=this.lexer.lex(this.pos,outerMode),res},
/**
 * Parses a group, which is either a single nucleus (like "x") or an expression
 * in braces (like "{x+y}")
 *
 * @return {?ParseFuncOrArgument}
 */
Parser.prototype.parseGroup=function(){
// Try to parse an open brace
if("{"===this.nextToken.text){
// If we get a brace, parse an expression
this.consume();var expression=this.parseExpression(!1);
// Make sure we get a close brace
return this.expect("}"),new ParseFuncOrArgument(new ParseNode("ordgroup",expression,this.mode),!1)}
// Otherwise, just return a nucleus
return this.parseSymbol()},
/**
 * Parses a group, which is an expression in brackets (like "[x+y]")
 *
 * @return {?ParseFuncOrArgument}
 */
Parser.prototype.parseOptionalGroup=function(){
// Try to parse an open bracket
if("["===this.nextToken.text){
// If we get a brace, parse an expression
this.consume();var expression=this.parseExpression(!1,"]");
// Make sure we get a close bracket
return this.expect("]"),new ParseFuncOrArgument(new ParseNode("ordgroup",expression,this.mode),!1)}
// Otherwise, return null,
return null},
/**
 * Parse a single symbol out of the string. Here, we handle both the functions
 * we have defined, as well as the single character symbols
 *
 * @return {?ParseFuncOrArgument}
 */
Parser.prototype.parseSymbol=function(){var nucleus=this.nextToken;return functions[nucleus.text]?(this.consume(),new ParseFuncOrArgument(nucleus.text,!0)):symbols[this.mode][nucleus.text]?(this.consume(),new ParseFuncOrArgument(new ParseNode(symbols[this.mode][nucleus.text].group,nucleus.text,this.mode),!1)):null},Parser.prototype.ParseNode=ParseNode,module.exports=Parser},
/***/594974:
/***/function(module){
/**
 * This is a module for storing settings passed into KaTeX. It correctly handles
 * default settings.
 */
/**
 * Helper function for getting a default value if the value is undefined
 */
function get(option,defaultValue){return void 0===option?defaultValue:option}
/**
 * The main Settings object
 *
 * The current options stored are:
 *  - displayMode: Whether the expression should be typeset by default in
 *                 textstyle or displaystyle (default false)
 */function Settings(options){
// allow null options
options=options||{},this.displayMode=get(options.displayMode,!1),this.throwOnError=get(options.throwOnError,!0),this.errorColor=get(options.errorColor,"#cc0000")}module.exports=Settings},
/***/627741:
/***/function(module){
/**
 * This file contains information and classes for the various kinds of styles
 * used in TeX. It provides a generic `Style` class, which holds information
 * about a specific style. It then provides instances of all the different kinds
 * of styles possible, and provides functions to move between them and get
 * information about them.
 */
/**
 * The main style class. Contains a unique id for the style, a size (which is
 * the same for cramped and uncramped version of a style), a cramped flag, and a
 * size multiplier, which gives the size difference between a style and
 * textstyle.
 */
function Style(id,size,multiplier,cramped){this.id=id,this.size=size,this.cramped=cramped,this.sizeMultiplier=multiplier}
/**
 * Get the style of a superscript given a base in the current style.
 */Style.prototype.sup=function(){return styles[sup[this.id]]},
/**
 * Get the style of a subscript given a base in the current style.
 */
Style.prototype.sub=function(){return styles[sub[this.id]]},
/**
 * Get the style of a fraction numerator given the fraction in the current
 * style.
 */
Style.prototype.fracNum=function(){return styles[fracNum[this.id]]},
/**
 * Get the style of a fraction denominator given the fraction in the current
 * style.
 */
Style.prototype.fracDen=function(){return styles[fracDen[this.id]]},
/**
 * Get the cramped version of a style (in particular, cramping a cramped style
 * doesn't change the style).
 */
Style.prototype.cramp=function(){return styles[cramp[this.id]]},
/**
 * HTML class name, like "displaystyle cramped"
 */
Style.prototype.cls=function(){return sizeNames[this.size]+(this.cramped?" cramped":" uncramped")},
/**
 * HTML Reset class name, like "reset-textstyle"
 */
Style.prototype.reset=function(){return resetNames[this.size]};
// IDs of the different styles
var D=0,Dc=1,T=2,Tc=3,S=4,Sc=5,SS=6,SSc=7,sizeNames=["displaystyle textstyle","textstyle","scriptstyle","scriptscriptstyle"],resetNames=["reset-textstyle","reset-textstyle","reset-scriptstyle","reset-scriptscriptstyle"],styles=[new Style(D,0,1,!1),new Style(Dc,0,1,!0),new Style(T,1,1,!1),new Style(Tc,1,1,!0),new Style(S,2,.7,!1),new Style(Sc,2,.7,!0),new Style(SS,3,.5,!1),new Style(SSc,3,.5,!0)],sup=[S,Sc,S,Sc,SS,SSc,SS,SSc],sub=[Sc,Sc,Sc,Sc,SSc,SSc,SSc,SSc],fracNum=[T,Tc,S,Sc,SS,SSc,SS,SSc],fracDen=[Tc,Tc,Sc,Sc,SSc,SSc,SSc,SSc],cramp=[Dc,Dc,Tc,Tc,Sc,Sc,SSc,SSc];
// We only export some of the styles. Also, we don't export the `Style` class so
// no more styles can be generated.
module.exports={DISPLAY:styles[D],TEXT:styles[T],SCRIPT:styles[S],SCRIPTSCRIPT:styles[SS]}},
/***/642084:
/***/function(module,__unused_webpack_exports,__webpack_require__){
/* eslint no-console:0 */
/**
 * This module contains general functions that can be used for building
 * different kinds of domTree nodes in a consistent manner.
 */
var domTree=__webpack_require__(304104),fontMetrics=__webpack_require__(26386),symbols=__webpack_require__(710471),utils=__webpack_require__(25182),greekCapitals=["\\Gamma","\\Delta","\\Theta","\\Lambda","\\Xi","\\Pi","\\Sigma","\\Upsilon","\\Phi","\\Psi","\\Omega"],dotlessLetters=["ı",// dotless i, \imath
"ȷ"],makeSymbol=function(value,style,mode,color,classes){
// Replace the value with its replaced value from symbol.js
symbols[mode][value]&&symbols[mode][value].replace&&(value=symbols[mode][value].replace);var symbolNode,metrics=fontMetrics.getCharacterMetrics(value,style);return symbolNode=metrics?new domTree.symbolNode(value,metrics.height,metrics.depth,metrics.italic,metrics.skew,classes):new domTree.symbolNode(value,0,0,0,0,classes),color&&(symbolNode.style.color=color),symbolNode},mathsym=function(value,mode,color,classes){
// Decide what font to render the symbol in by its entry in the symbols
// table.
// Have a special case for when the value = \ because the \ is used as a
// textord in unsupported command errors but cannot be parsed as a regular
// text ordinal and is therefore not present as a symbol in the symbols
// table for text
return"\\"===value||"main"===symbols[mode][value].font?makeSymbol(value,"Main-Regular",mode,color,classes):makeSymbol(value,"AMS-Regular",mode,color,classes.concat(["amsrm"]))},mathDefault=function(value,mode,color,classes,type){if("mathord"===type)return mathit(value,mode,color,classes);if("textord"===type)return makeSymbol(value,"Main-Regular",mode,color,classes.concat(["mathrm"]));throw new Error("unexpected type: "+type+" in mathDefault")},mathit=function(value,mode,color,classes){return/[0-9]/.test(value.charAt(0))||
// glyphs for \imath and \jmath do not exist in Math-Italic so we
// need to use Main-Italic instead
utils.contains(dotlessLetters,value)||utils.contains(greekCapitals,value)?makeSymbol(value,"Main-Italic",mode,color,classes.concat(["mainit"])):makeSymbol(value,"Math-Italic",mode,color,classes.concat(["mathit"]))},makeOrd=function(group,options,type){var mode=group.mode,value=group.value;symbols[mode][value]&&symbols[mode][value].replace&&(value=symbols[mode][value].replace);var classes=["mord"],color=options.getColor(),font=options.font;if(font){if("mathit"===font||utils.contains(dotlessLetters,value))return mathit(value,mode,color,classes);var fontName=fontMap[font].fontName;return fontMetrics.getCharacterMetrics(value,fontName)?makeSymbol(value,fontName,mode,color,classes.concat([font])):mathDefault(value,mode,color,classes,type)}return mathDefault(value,mode,color,classes,type)},sizeElementFromChildren=function(elem){var height=0,depth=0,maxFontSize=0;if(elem.children)for(var i=0;i<elem.children.length;i++)elem.children[i].height>height&&(height=elem.children[i].height),elem.children[i].depth>depth&&(depth=elem.children[i].depth),elem.children[i].maxFontSize>maxFontSize&&(maxFontSize=elem.children[i].maxFontSize);elem.height=height,elem.depth=depth,elem.maxFontSize=maxFontSize},makeSpan=function(classes,children,color){var span=new domTree.span(classes,children);return sizeElementFromChildren(span),color&&(span.style.color=color),span},makeFragment=function(children){var fragment=new domTree.documentFragment(children);return sizeElementFromChildren(fragment),fragment},makeFontSizer=function(options,fontSize){var fontSizeInner=makeSpan([],[new domTree.symbolNode("​")]);fontSizeInner.style.fontSize=fontSize/options.style.sizeMultiplier+"em";var fontSizer=makeSpan(["fontsize-ensurer","reset-"+options.size,"size5"],[fontSizeInner]);return fontSizer},makeVList=function(children,positionType,positionData,options){var depth,currPos,i;if("individualShift"===positionType){var oldChildren=children;for(children=[oldChildren[0]],
// Add in kerns to the list of children to get each element to be
// shifted to the correct specified shift
depth=-oldChildren[0].shift-oldChildren[0].elem.depth,currPos=depth,i=1;i<oldChildren.length;i++){var diff=-oldChildren[i].shift-currPos-oldChildren[i].elem.depth,size=diff-(oldChildren[i-1].elem.height+oldChildren[i-1].elem.depth);currPos+=diff,children.push({type:"kern",size:size}),children.push(oldChildren[i])}}else if("top"===positionType){
// We always start at the bottom, so calculate the bottom by adding up
// all the sizes
var bottom=positionData;for(i=0;i<children.length;i++)"kern"===children[i].type?bottom-=children[i].size:bottom-=children[i].elem.height+children[i].elem.depth;depth=bottom}else depth="bottom"===positionType?-positionData:"shift"===positionType?-children[0].elem.depth-positionData:"firstBaseline"===positionType?-children[0].elem.depth:0;
// Make the fontSizer
var maxFontSize=0;for(i=0;i<children.length;i++)"elem"===children[i].type&&(maxFontSize=Math.max(maxFontSize,children[i].elem.maxFontSize));var fontSizer=makeFontSizer(options,maxFontSize),realChildren=[];
// Create a new list of actual children at the correct offsets
for(currPos=depth,i=0;i<children.length;i++)if("kern"===children[i].type)currPos+=children[i].size;else{var child=children[i].elem,shift=-child.depth-currPos;currPos+=child.height+child.depth;var childWrap=makeSpan([],[fontSizer,child]);childWrap.height-=shift,childWrap.depth+=shift,childWrap.style.top=shift+"em",realChildren.push(childWrap)}
// Add in an element at the end with no offset to fix the calculation of
// baselines in some browsers (namely IE, sometimes safari)
var baselineFix=makeSpan(["baseline-fix"],[fontSizer,new domTree.symbolNode("​")]);realChildren.push(baselineFix);var vlist=makeSpan(["vlist"],realChildren);
// Fix the final height and depth, in case there were kerns at the ends
// since the makeSpan calculation won't take that in to account.
return vlist.height=Math.max(currPos,vlist.height),vlist.depth=Math.max(-depth,vlist.depth),vlist},sizingMultiplier={size1:.5,size2:.7,size3:.8,size4:.9,size5:1,size6:1.2,size7:1.44,size8:1.73,size9:2.07,size10:2.49},spacingFunctions={"\\qquad":{size:"2em",className:"qquad"},"\\quad":{size:"1em",className:"quad"},"\\enspace":{size:"0.5em",className:"enspace"},"\\;":{size:"0.277778em",className:"thickspace"},"\\:":{size:"0.22222em",className:"mediumspace"},"\\,":{size:"0.16667em",className:"thinspace"},"\\!":{size:"-0.16667em",className:"negativethinspace"}},fontMap={
// styles
mathbf:{variant:"bold",fontName:"Main-Bold"},mathrm:{variant:"normal",fontName:"Main-Regular"},
// "mathit" is missing because it requires the use of two fonts: Main-Italic
// and Math-Italic.  This is handled by a special case in makeOrd which ends
// up calling mathit.
// families
mathbb:{variant:"double-struck",fontName:"AMS-Regular"},mathcal:{variant:"script",fontName:"Caligraphic-Regular"},mathfrak:{variant:"fraktur",fontName:"Fraktur-Regular"},mathscr:{variant:"script",fontName:"Script-Regular"},mathsf:{variant:"sans-serif",fontName:"SansSerif-Regular"},mathtt:{variant:"monospace",fontName:"Typewriter-Regular"}};module.exports={fontMap:fontMap,makeSymbol:makeSymbol,mathsym:mathsym,makeSpan:makeSpan,makeFragment:makeFragment,makeVList:makeVList,makeOrd:makeOrd,sizingMultiplier:sizingMultiplier,spacingFunctions:spacingFunctions}},
/***/460522:
/***/function(module,__unused_webpack_exports,__webpack_require__){
/* eslint no-console:0 */
/**
 * This file does the main work of building a domTree structure from a parse
 * tree. The entry point is the `buildHTML` function, which takes a parse tree.
 * Then, the buildExpression, buildGroup, and various groupTypes functions are
 * called, to produce a final HTML tree.
 */
var ParseError=__webpack_require__(16856),Style=__webpack_require__(627741),buildCommon=__webpack_require__(642084),delimiter=__webpack_require__(337870),domTree=__webpack_require__(304104),fontMetrics=__webpack_require__(26386),utils=__webpack_require__(25182),makeSpan=buildCommon.makeSpan,buildExpression=function(expression,options,prev){for(var groups=[],i=0;i<expression.length;i++){var group=expression[i];groups.push(buildGroup(group,options,prev)),prev=group}return groups},groupToType={mathord:"mord",textord:"mord",bin:"mbin",rel:"mrel",text:"mord",open:"mopen",close:"mclose",inner:"minner",genfrac:"mord",array:"mord",spacing:"mord",punct:"mpunct",ordgroup:"mord",op:"mop",katex:"mord",overline:"mord",underline:"mord",rule:"mord",leftright:"minner",sqrt:"mord",accent:"mord"},getTypeOfGroup=function(group){return null==group?groupToType.mathord:"supsub"===group.type?getTypeOfGroup(group.value.base):"llap"===group.type||"rlap"===group.type?getTypeOfGroup(group.value):"color"===group.type||"sizing"===group.type||"styling"===group.type?getTypeOfGroup(group.value.value):"delimsizing"===group.type?groupToType[group.value.delimType]:groupToType[group.type]},shouldHandleSupSub=function(group,options){return!!group&&("op"===group.type?group.value.limits&&(options.style.size===Style.DISPLAY.size||group.value.alwaysHandleSupSub):"accent"===group.type?isCharacterBox(group.value.base):null)},getBaseElem=function(group){return!!group&&("ordgroup"===group.type?1===group.value.length?getBaseElem(group.value[0]):group:"color"===group.type&&1===group.value.value.length?getBaseElem(group.value.value[0]):group)},isCharacterBox=function(group){var baseElem=getBaseElem(group);
// These are all they types of groups which hold single characters
return"mathord"===baseElem.type||"textord"===baseElem.type||"bin"===baseElem.type||"rel"===baseElem.type||"inner"===baseElem.type||"open"===baseElem.type||"close"===baseElem.type||"punct"===baseElem.type},makeNullDelimiter=function(options){return makeSpan(["sizing","reset-"+options.size,"size5",options.style.reset(),Style.TEXT.cls(),"nulldelimiter"])},groupTypes={mathord:function(group,options,prev){return buildCommon.makeOrd(group,options,"mathord")},textord:function(group,options,prev){return buildCommon.makeOrd(group,options,"textord")},bin:function(group,options,prev){var className="mbin",prevAtom=prev;
// Pull out the most recent element. Do some special handling to find
// things at the end of a \color group. Note that we don't use the same
// logic for ordgroups (which count as ords).
while(prevAtom&&"color"===prevAtom.type){var atoms=prevAtom.value.value;prevAtom=atoms[atoms.length-1]}
// See TeXbook pg. 442-446, Rules 5 and 6, and the text before Rule 19.
// Here, we determine whether the bin should turn into an ord. We
// currently only apply Rule 5.
return prev&&!utils.contains(["mbin","mopen","mrel","mop","mpunct"],getTypeOfGroup(prevAtom))||(group.type="textord",className="mord"),buildCommon.mathsym(group.value,group.mode,options.getColor(),[className])},rel:function(group,options,prev){return buildCommon.mathsym(group.value,group.mode,options.getColor(),["mrel"])},open:function(group,options,prev){return buildCommon.mathsym(group.value,group.mode,options.getColor(),["mopen"])},close:function(group,options,prev){return buildCommon.mathsym(group.value,group.mode,options.getColor(),["mclose"])},inner:function(group,options,prev){return buildCommon.mathsym(group.value,group.mode,options.getColor(),["minner"])},punct:function(group,options,prev){return buildCommon.mathsym(group.value,group.mode,options.getColor(),["mpunct"])},ordgroup:function(group,options,prev){return makeSpan(["mord",options.style.cls()],buildExpression(group.value,options.reset()))},text:function(group,options,prev){return makeSpan(["text","mord",options.style.cls()],buildExpression(group.value.body,options.reset()))},color:function(group,options,prev){var elements=buildExpression(group.value.value,options.withColor(group.value.color),prev);
// \color isn't supposed to affect the type of the elements it contains.
// To accomplish this, we wrap the results in a fragment, so the inner
// elements will be able to directly interact with their neighbors. For
// example, `\color{red}{2 +} 3` has the same spacing as `2 + 3`
return new buildCommon.makeFragment(elements)},supsub:function(group,options,prev){
// Superscript and subscripts are handled in the TeXbook on page
// 445-446, rules 18(a-f).
// Here is where we defer to the inner group if it should handle
// superscripts and subscripts itself.
if(shouldHandleSupSub(group.value.base,options))return groupTypes[group.value.base.type](group,options,prev);var supmid,submid,sup,sub,supShift,subShift,minSupShift,base=buildGroup(group.value.base,options.reset());group.value.sup&&(sup=buildGroup(group.value.sup,options.withStyle(options.style.sup())),supmid=makeSpan([options.style.reset(),options.style.sup().cls()],[sup])),group.value.sub&&(sub=buildGroup(group.value.sub,options.withStyle(options.style.sub())),submid=makeSpan([options.style.reset(),options.style.sub().cls()],[sub])),isCharacterBox(group.value.base)?(supShift=0,subShift=0):(supShift=base.height-fontMetrics.metrics.supDrop,subShift=base.depth+fontMetrics.metrics.subDrop),minSupShift=options.style===Style.DISPLAY?fontMetrics.metrics.sup1:options.style.cramped?fontMetrics.metrics.sup3:fontMetrics.metrics.sup2;
// scriptspace is a font-size-independent size, so scale it
// appropriately
var supsub,multiplier=Style.TEXT.sizeMultiplier*options.style.sizeMultiplier,scriptspace=.5/fontMetrics.metrics.ptPerEm/multiplier+"em";if(group.value.sup)if(group.value.sub){supShift=Math.max(supShift,minSupShift,sup.depth+.25*fontMetrics.metrics.xHeight),subShift=Math.max(subShift,fontMetrics.metrics.sub2);var ruleWidth=fontMetrics.metrics.defaultRuleThickness;
// Rule 18e
if(supShift-sup.depth-(sub.height-subShift)<4*ruleWidth){subShift=4*ruleWidth-(supShift-sup.depth)+sub.height;var psi=.8*fontMetrics.metrics.xHeight-(supShift-sup.depth);psi>0&&(supShift+=psi,subShift-=psi)}supsub=buildCommon.makeVList([{type:"elem",elem:submid,shift:subShift},{type:"elem",elem:supmid,shift:-supShift}],"individualShift",null,options),
// See comment above about subscripts not being shifted
base instanceof domTree.symbolNode&&(supsub.children[0].style.marginLeft=-base.italic+"em"),supsub.children[0].style.marginRight=scriptspace,supsub.children[1].style.marginRight=scriptspace}else
// Rule 18c, d
supShift=Math.max(supShift,minSupShift,sup.depth+.25*fontMetrics.metrics.xHeight),supsub=buildCommon.makeVList([{type:"elem",elem:supmid}],"shift",-supShift,options),supsub.children[0].style.marginRight=scriptspace;else
// Rule 18b
subShift=Math.max(subShift,fontMetrics.metrics.sub1,sub.height-.8*fontMetrics.metrics.xHeight),supsub=buildCommon.makeVList([{type:"elem",elem:submid}],"shift",subShift,options),supsub.children[0].style.marginRight=scriptspace,
// Subscripts shouldn't be shifted by the base's italic correction.
// Account for that by shifting the subscript back the appropriate
// amount. Note we only do this when the base is a single symbol.
base instanceof domTree.symbolNode&&(supsub.children[0].style.marginLeft=-base.italic+"em");return makeSpan([getTypeOfGroup(group.value.base)],[base,supsub])},genfrac:function(group,options,prev){
// Fractions are handled in the TeXbook on pages 444-445, rules 15(a-e).
// Figure out what style this fraction should be in based on the
// function used
var fstyle=options.style;"display"===group.value.size?fstyle=Style.DISPLAY:"text"===group.value.size&&(fstyle=Style.TEXT);var ruleWidth,numShift,clearance,denomShift,frac,delimSize,leftDelim,rightDelim,nstyle=fstyle.fracNum(),dstyle=fstyle.fracDen(),numer=buildGroup(group.value.numer,options.withStyle(nstyle)),numerreset=makeSpan([fstyle.reset(),nstyle.cls()],[numer]),denom=buildGroup(group.value.denom,options.withStyle(dstyle)),denomreset=makeSpan([fstyle.reset(),dstyle.cls()],[denom]);if(ruleWidth=group.value.hasBarLine?fontMetrics.metrics.defaultRuleThickness/options.style.sizeMultiplier:0,fstyle.size===Style.DISPLAY.size?(numShift=fontMetrics.metrics.num1,clearance=ruleWidth>0?3*ruleWidth:7*fontMetrics.metrics.defaultRuleThickness,denomShift=fontMetrics.metrics.denom1):(ruleWidth>0?(numShift=fontMetrics.metrics.num2,clearance=ruleWidth):(numShift=fontMetrics.metrics.num3,clearance=3*fontMetrics.metrics.defaultRuleThickness),denomShift=fontMetrics.metrics.denom2),0===ruleWidth){
// Rule 15c
var candiateClearance=numShift-numer.depth-(denom.height-denomShift);candiateClearance<clearance&&(numShift+=.5*(clearance-candiateClearance),denomShift+=.5*(clearance-candiateClearance)),frac=buildCommon.makeVList([{type:"elem",elem:denomreset,shift:denomShift},{type:"elem",elem:numerreset,shift:-numShift}],"individualShift",null,options)}else{
// Rule 15d
var axisHeight=fontMetrics.metrics.axisHeight;numShift-numer.depth-(axisHeight+.5*ruleWidth)<clearance&&(numShift+=clearance-(numShift-numer.depth-(axisHeight+.5*ruleWidth))),axisHeight-.5*ruleWidth-(denom.height-denomShift)<clearance&&(denomShift+=clearance-(axisHeight-.5*ruleWidth-(denom.height-denomShift)));var mid=makeSpan([options.style.reset(),Style.TEXT.cls(),"frac-line"]);
// Manually set the height of the line because its height is
// created in CSS
mid.height=ruleWidth;var midShift=-(axisHeight-.5*ruleWidth);frac=buildCommon.makeVList([{type:"elem",elem:denomreset,shift:denomShift},{type:"elem",elem:mid,shift:midShift},{type:"elem",elem:numerreset,shift:-numShift}],"individualShift",null,options)}
// Since we manually change the style sometimes (with \dfrac or \tfrac),
// account for the possible size change here.
return frac.height*=fstyle.sizeMultiplier/options.style.sizeMultiplier,frac.depth*=fstyle.sizeMultiplier/options.style.sizeMultiplier,delimSize=fstyle.size===Style.DISPLAY.size?fontMetrics.metrics.delim1:fontMetrics.metrics.getDelim2(fstyle),leftDelim=null==group.value.leftDelim?makeNullDelimiter(options):delimiter.customSizedDelim(group.value.leftDelim,delimSize,!0,options.withStyle(fstyle),group.mode),rightDelim=null==group.value.rightDelim?makeNullDelimiter(options):delimiter.customSizedDelim(group.value.rightDelim,delimSize,!0,options.withStyle(fstyle),group.mode),makeSpan(["mord",options.style.reset(),fstyle.cls()],[leftDelim,makeSpan(["mfrac"],[frac]),rightDelim],options.getColor())},array:function(group,options,prev){var r,c,nr=group.value.body.length,nc=0,body=new Array(nr),pt=1/fontMetrics.metrics.ptPerEm,arraycolsep=5*pt,baselineskip=12*pt,arraystretch=utils.deflt(group.value.arraystretch,1),arrayskip=arraystretch*baselineskip,arstrutHeight=.7*arrayskip,arstrutDepth=.3*arrayskip,totalHeight=0;for(r=0;r<group.value.body.length;++r){var inrow=group.value.body[r],height=arstrutHeight,depth=arstrutDepth;// to each tow (via the template)
nc<inrow.length&&(nc=inrow.length);var outrow=new Array(inrow.length);for(c=0;c<inrow.length;++c){var elt=buildGroup(inrow[c],options);depth<elt.depth&&(depth=elt.depth),height<elt.height&&(height=elt.height),outrow[c]=elt}var gap=0;if(group.value.rowGaps[r]){switch(gap=group.value.rowGaps[r].value,gap.unit){case"em":gap=gap.number;break;case"ex":gap=gap.number*fontMetrics.metrics.emPerEx;break;default:gap=0}gap>0&&(// \@argarraycr
gap+=arstrutDepth,depth<gap&&(depth=gap),gap=0)}outrow.height=height,outrow.depth=depth,totalHeight+=height,outrow.pos=totalHeight,totalHeight+=depth+gap,// \@yargarraycr
body[r]=outrow}var colSep,colDescrNum,offset=totalHeight/2+fontMetrics.metrics.axisHeight,colDescriptions=group.value.cols||[],cols=[];for(c=0,colDescrNum=0;
// Continue while either there are more columns or more column
// descriptions, so trailing separators don't get lost.
c<nc||colDescrNum<colDescriptions.length;++c,++colDescrNum){var colDescr=colDescriptions[colDescrNum]||{},firstSeparator=!0;while("separator"===colDescr.type){if(
// If there is more than one separator in a row, add a space
// between them.
firstSeparator||(colSep=makeSpan(["arraycolsep"],[]),colSep.style.width=fontMetrics.metrics.doubleRuleSep+"em",cols.push(colSep)),"|"!==colDescr.separator)throw new ParseError("Invalid separator type: "+colDescr.separator);var separator=makeSpan(["vertical-separator"],[]);separator.style.height=totalHeight+"em",separator.style.verticalAlign=-(totalHeight-offset)+"em",cols.push(separator),colDescrNum++,colDescr=colDescriptions[colDescrNum]||{},firstSeparator=!1}if(!(c>=nc)){var sepwidth;(c>0||group.value.hskipBeforeAndAfter)&&(sepwidth=utils.deflt(colDescr.pregap,arraycolsep),0!==sepwidth&&(colSep=makeSpan(["arraycolsep"],[]),colSep.style.width=sepwidth+"em",cols.push(colSep)));var col=[];for(r=0;r<nr;++r){var row=body[r],elem=row[c];if(elem){var shift=row.pos-offset;elem.depth=row.depth,elem.height=row.height,col.push({type:"elem",elem:elem,shift:shift})}}col=buildCommon.makeVList(col,"individualShift",null,options),col=makeSpan(["col-align-"+(colDescr.align||"c")],[col]),cols.push(col),(c<nc-1||group.value.hskipBeforeAndAfter)&&(sepwidth=utils.deflt(colDescr.postgap,arraycolsep),0!==sepwidth&&(colSep=makeSpan(["arraycolsep"],[]),colSep.style.width=sepwidth+"em",cols.push(colSep)))}}return body=makeSpan(["mtable"],cols),makeSpan(["mord"],[body],options.getColor())},spacing:function(group,options,prev){return"\\ "===group.value||"\\space"===group.value||" "===group.value||"~"===group.value?makeSpan(["mord","mspace"],[buildCommon.mathsym(group.value,group.mode)]):makeSpan(["mord","mspace",buildCommon.spacingFunctions[group.value].className])},llap:function(group,options,prev){var inner=makeSpan(["inner"],[buildGroup(group.value.body,options.reset())]),fix=makeSpan(["fix"],[]);return makeSpan(["llap",options.style.cls()],[inner,fix])},rlap:function(group,options,prev){var inner=makeSpan(["inner"],[buildGroup(group.value.body,options.reset())]),fix=makeSpan(["fix"],[]);return makeSpan(["rlap",options.style.cls()],[inner,fix])},op:function(group,options,prev){
// Operators are handled in the TeXbook pg. 443-444, rule 13(a).
var supGroup,subGroup,hasLimits=!1;"supsub"===group.type&&(
// If we have limits, supsub will pass us its group to handle. Pull
// out the superscript and subscript and set the group to the op in
// its base.
supGroup=group.value.sup,subGroup=group.value.sub,group=group.value.base,hasLimits=!0);
// Most operators have a large successor symbol, but these don't.
var base,noSuccessor=["\\smallint"],large=!1;options.style.size===Style.DISPLAY.size&&group.value.symbol&&!utils.contains(noSuccessor,group.value.body)&&(
// Most symbol operators get larger in displaystyle (rule 13)
large=!0);var baseShift=0,slant=0;if(group.value.symbol){
// If this is a symbol, create the symbol.
var style=large?"Size2-Regular":"Size1-Regular";base=buildCommon.makeSymbol(group.value.body,style,"math",options.getColor(),["op-symbol",large?"large-op":"small-op","mop"]),
// Shift the symbol so its center lies on the axis (rule 13). It
// appears that our fonts have the centers of the symbols already
// almost on the axis, so these numbers are very small. Note we
// don't actually apply this here, but instead it is used either in
// the vlist creation or separately when there are no limits.
baseShift=(base.height-base.depth)/2-fontMetrics.metrics.axisHeight*options.style.sizeMultiplier,
// The slant of the symbol is just its italic correction.
slant=base.italic}else{for(
// Otherwise, this is a text operator. Build the text from the
// operator's name.
// TODO(emily): Add a space in the middle of some of these
// operators, like \limsup
var output=[],i=1;i<group.value.body.length;i++)output.push(buildCommon.mathsym(group.value.body[i],group.mode));base=makeSpan(["mop"],output,options.getColor())}if(hasLimits){var supmid,supKern,submid,subKern,finalGroup,top,bottom;
// We manually have to handle the superscripts and subscripts. This,
// aside from the kern calculations, is copied from supsub.
if(
// IE 8 clips \int if it is in a display: inline-block. We wrap it
// in a new span so it is an inline, and works.
base=makeSpan([],[base]),supGroup){var sup=buildGroup(supGroup,options.withStyle(options.style.sup()));supmid=makeSpan([options.style.reset(),options.style.sup().cls()],[sup]),supKern=Math.max(fontMetrics.metrics.bigOpSpacing1,fontMetrics.metrics.bigOpSpacing3-sup.depth)}if(subGroup){var sub=buildGroup(subGroup,options.withStyle(options.style.sub()));submid=makeSpan([options.style.reset(),options.style.sub().cls()],[sub]),subKern=Math.max(fontMetrics.metrics.bigOpSpacing2,fontMetrics.metrics.bigOpSpacing4-sub.height)}
// Build the final group as a vlist of the possible subscript, base,
// and possible superscript.
if(supGroup)if(subGroup){if(!supGroup&&!subGroup)
// This case probably shouldn't occur (this would mean the
// supsub was sending us a group with no superscript or
// subscript) but be safe.
return base;bottom=fontMetrics.metrics.bigOpSpacing5+submid.height+submid.depth+subKern+base.depth+baseShift,finalGroup=buildCommon.makeVList([{type:"kern",size:fontMetrics.metrics.bigOpSpacing5},{type:"elem",elem:submid},{type:"kern",size:subKern},{type:"elem",elem:base},{type:"kern",size:supKern},{type:"elem",elem:supmid},{type:"kern",size:fontMetrics.metrics.bigOpSpacing5}],"bottom",bottom,options),
// See comment above about slants
finalGroup.children[0].style.marginLeft=-slant+"em",finalGroup.children[2].style.marginLeft=slant+"em"}else bottom=base.depth+baseShift,finalGroup=buildCommon.makeVList([{type:"elem",elem:base},{type:"kern",size:supKern},{type:"elem",elem:supmid},{type:"kern",size:fontMetrics.metrics.bigOpSpacing5}],"bottom",bottom,options),
// See comment above about slants
finalGroup.children[1].style.marginLeft=slant+"em";else top=base.height-baseShift,finalGroup=buildCommon.makeVList([{type:"kern",size:fontMetrics.metrics.bigOpSpacing5},{type:"elem",elem:submid},{type:"kern",size:subKern},{type:"elem",elem:base}],"top",top,options),
// Here, we shift the limits by the slant of the symbol. Note
// that we are supposed to shift the limits by 1/2 of the slant,
// but since we are centering the limits adding a full slant of
// margin will shift by 1/2 that.
finalGroup.children[0].style.marginLeft=-slant+"em";return makeSpan(["mop","op-limits"],[finalGroup])}return group.value.symbol&&(base.style.top=baseShift+"em"),base},katex:function(group,options,prev){
// The KaTeX logo. The offsets for the K and a were chosen to look
// good, but the offsets for the T, E, and X were taken from the
// definition of \TeX in TeX (see TeXbook pg. 356)
var k=makeSpan(["k"],[buildCommon.mathsym("K",group.mode)]),a=makeSpan(["a"],[buildCommon.mathsym("A",group.mode)]);a.height=.75*(a.height+.2),a.depth=.75*(a.height-.2);var t=makeSpan(["t"],[buildCommon.mathsym("T",group.mode)]),e=makeSpan(["e"],[buildCommon.mathsym("E",group.mode)]);e.height=e.height-.2155,e.depth=e.depth+.2155;var x=makeSpan(["x"],[buildCommon.mathsym("X",group.mode)]);return makeSpan(["katex-logo","mord"],[k,a,t,e,x],options.getColor())},overline:function(group,options,prev){
// Overlines are handled in the TeXbook pg 443, Rule 9.
// Build the inner group in the cramped style.
var innerGroup=buildGroup(group.value.body,options.withStyle(options.style.cramp())),ruleWidth=fontMetrics.metrics.defaultRuleThickness/options.style.sizeMultiplier,line=makeSpan([options.style.reset(),Style.TEXT.cls(),"overline-line"]);line.height=ruleWidth,line.maxFontSize=1;
// Generate the vlist, with the appropriate kerns
var vlist=buildCommon.makeVList([{type:"elem",elem:innerGroup},{type:"kern",size:3*ruleWidth},{type:"elem",elem:line},{type:"kern",size:ruleWidth}],"firstBaseline",null,options);return makeSpan(["overline","mord"],[vlist],options.getColor())},underline:function(group,options,prev){
// Underlines are handled in the TeXbook pg 443, Rule 10.
// Build the inner group.
var innerGroup=buildGroup(group.value.body,options),ruleWidth=fontMetrics.metrics.defaultRuleThickness/options.style.sizeMultiplier,line=makeSpan([options.style.reset(),Style.TEXT.cls(),"underline-line"]);line.height=ruleWidth,line.maxFontSize=1;
// Generate the vlist, with the appropriate kerns
var vlist=buildCommon.makeVList([{type:"kern",size:ruleWidth},{type:"elem",elem:line},{type:"kern",size:3*ruleWidth},{type:"elem",elem:innerGroup}],"top",innerGroup.height,options);return makeSpan(["underline","mord"],[vlist],options.getColor())},sqrt:function(group,options,prev){
// Square roots are handled in the TeXbook pg. 443, Rule 11.
// First, we do the same steps as in overline to build the inner group
// and line
var inner=buildGroup(group.value.body,options.withStyle(options.style.cramp())),ruleWidth=fontMetrics.metrics.defaultRuleThickness/options.style.sizeMultiplier,line=makeSpan([options.style.reset(),Style.TEXT.cls(),"sqrt-line"],[],options.getColor());line.height=ruleWidth,line.maxFontSize=1;var phi=ruleWidth;options.style.id<Style.TEXT.id&&(phi=fontMetrics.metrics.xHeight);
// Calculate the clearance between the body and line
var lineClearance=ruleWidth+phi/4,innerHeight=(inner.height+inner.depth)*options.style.sizeMultiplier,minDelimiterHeight=innerHeight+lineClearance+ruleWidth,delim=makeSpan(["sqrt-sign"],[delimiter.customSizedDelim("\\surd",minDelimiterHeight,!1,options,group.mode)],options.getColor()),delimDepth=delim.height+delim.depth-ruleWidth;
// Adjust the clearance based on the delimiter size
delimDepth>inner.height+inner.depth+lineClearance&&(lineClearance=(lineClearance+delimDepth-inner.height-inner.depth)/2);
// Shift the delimiter so that its top lines up with the top of the line
var body,delimShift=-(inner.height+lineClearance+ruleWidth)+delim.height;if(delim.style.top=delimShift+"em",delim.height-=delimShift,delim.depth+=delimShift,body=0===inner.height&&0===inner.depth?makeSpan():buildCommon.makeVList([{type:"elem",elem:inner},{type:"kern",size:lineClearance},{type:"elem",elem:line},{type:"kern",size:ruleWidth}],"firstBaseline",null,options),group.value.index){
// Handle the optional root index
// The index is always in scriptscript style
var root=buildGroup(group.value.index,options.withStyle(Style.SCRIPTSCRIPT)),rootWrap=makeSpan([options.style.reset(),Style.SCRIPTSCRIPT.cls()],[root]),innerRootHeight=Math.max(delim.height,body.height),innerRootDepth=Math.max(delim.depth,body.depth),toShift=.6*(innerRootHeight-innerRootDepth),rootVList=buildCommon.makeVList([{type:"elem",elem:rootWrap}],"shift",-toShift,options),rootVListWrap=makeSpan(["root"],[rootVList]);return makeSpan(["sqrt","mord"],[rootVListWrap,delim,body])}return makeSpan(["sqrt","mord"],[delim,body])},sizing:function(group,options,prev){
// Handle sizing operators like \Huge. Real TeX doesn't actually allow
// these functions inside of math expressions, so we do some special
// handling.
var inner=buildExpression(group.value.value,options.withSize(group.value.size),prev),span=makeSpan(["mord"],[makeSpan(["sizing","reset-"+options.size,group.value.size,options.style.cls()],inner)]),fontSize=buildCommon.sizingMultiplier[group.value.size];return span.maxFontSize=fontSize*options.style.sizeMultiplier,span},styling:function(group,options,prev){
// Style changes are handled in the TeXbook on pg. 442, Rule 3.
// Figure out what style we're changing to.
var style={display:Style.DISPLAY,text:Style.TEXT,script:Style.SCRIPT,scriptscript:Style.SCRIPTSCRIPT},newStyle=style[group.value.style],inner=buildExpression(group.value.value,options.withStyle(newStyle),prev);return makeSpan([options.style.reset(),newStyle.cls()],inner)},font:function(group,options,prev){var font=group.value.font;return buildGroup(group.value.body,options.withFont(font),prev)},delimsizing:function(group,options,prev){var delim=group.value.value;return"."===delim?makeSpan([groupToType[group.value.delimType]]):makeSpan([groupToType[group.value.delimType]],[delimiter.sizedDelim(delim,group.value.size,options,group.mode)]);
// Use delimiter.sizedDelim to generate the delimiter.
},leftright:function(group,options,prev){
// Calculate its height and depth
for(
// Build the inner expression
var leftDelim,rightDelim,inner=buildExpression(group.value.body,options.reset()),innerHeight=0,innerDepth=0,i=0;i<inner.length;i++)innerHeight=Math.max(inner[i].height,innerHeight),innerDepth=Math.max(inner[i].depth,innerDepth);
// The size of delimiters is the same, regardless of what style we are
// in. Thus, to correctly calculate the size of delimiter we need around
// a group, we scale down the inner size based on the size.
return innerHeight*=options.style.sizeMultiplier,innerDepth*=options.style.sizeMultiplier,
// Empty delimiters in \left and \right make null delimiter spaces.
leftDelim="."===group.value.left?makeNullDelimiter(options):delimiter.leftRightDelim(group.value.left,innerHeight,innerDepth,options,group.mode),
// Add it to the beginning of the expression
inner.unshift(leftDelim),
// Same for the right delimiter
rightDelim="."===group.value.right?makeNullDelimiter(options):delimiter.leftRightDelim(group.value.right,innerHeight,innerDepth,options,group.mode),
// Add it to the end of the expression.
inner.push(rightDelim),makeSpan(["minner",options.style.cls()],inner,options.getColor())},rule:function(group,options,prev){
// Make an empty span for the rule
var rule=makeSpan(["mord","rule"],[],options.getColor()),shift=0;
// Calculate the shift, width, and height of the rule, and account for units
group.value.shift&&(shift=group.value.shift.number,"ex"===group.value.shift.unit&&(shift*=fontMetrics.metrics.xHeight));var width=group.value.width.number;"ex"===group.value.width.unit&&(width*=fontMetrics.metrics.xHeight);var height=group.value.height.number;return"ex"===group.value.height.unit&&(height*=fontMetrics.metrics.xHeight),
// The sizes of rules are absolute, so make it larger if we are in a
// smaller style.
shift/=options.style.sizeMultiplier,width/=options.style.sizeMultiplier,height/=options.style.sizeMultiplier,
// Style the rule to the right size
rule.style.borderRightWidth=width+"em",rule.style.borderTopWidth=height+"em",rule.style.bottom=shift+"em",
// Record the height and width
rule.width=width,rule.height=height+shift,rule.depth=-shift,rule},accent:function(group,options,prev){
// Accents are handled in the TeXbook pg. 443, rule 12.
var supsubGroup,base=group.value.base;if("supsub"===group.type){
// If our base is a character box, and we have superscripts and
// subscripts, the supsub will defer to us. In particular, we want
// to attach the superscripts and subscripts to the inner body (so
// that the position of the superscripts and subscripts won't be
// affected by the height of the accent). We accomplish this by
// sticking the base of the accent into the base of the supsub, and
// rendering that, while keeping track of where the accent is.
// The supsub group is the group that was passed in
var supsub=group;
// The real accent group is the base of the supsub group
group=supsub.value.base,
// The character box is the base of the accent group
base=group.value.base,
// Stick the character box into the base of the supsub group
supsub.value.base=base,
// Rerender the supsub group with its new base, and store that
// result.
supsubGroup=buildGroup(supsub,options.reset(),prev)}
// Build the base group
var skew,body=buildGroup(base,options.withStyle(options.style.cramp()));
// Calculate the skew of the accent. This is based on the line "If the
// nucleus is not a single character, let s = 0; otherwise set s to the
// kern amount for the nucleus followed by the \skewchar of its font."
// Note that our skew metrics are just the kern between each character
// and the skewchar.
if(isCharacterBox(base)){
// If the base is a character box, then we want the skew of the
// innermost character. To do that, we find the innermost character:
var baseChar=getBaseElem(base),baseGroup=buildGroup(baseChar,options.withStyle(options.style.cramp()));
// Then, we render its group to get the symbol inside it
// Finally, we pull the skew off of the symbol.
skew=baseGroup.skew}else skew=0;
// calculate the amount of space between the body and the accent
var clearance=Math.min(body.height,fontMetrics.metrics.xHeight),accent=buildCommon.makeSymbol(group.value.accent,"Main-Regular","math",options.getColor());
// Build the accent
// Remove the italic correction of the accent, because it only serves to
// shift the accent over to a place we don't want.
accent.italic=0;
// The \vec character that the fonts use is a combining character, and
// thus shows up much too far to the left. To account for this, we add a
// specific class which shifts the accent over to where we want it.
// TODO(emily): Fix this in a better way, like by changing the font
var vecClass="\\vec"===group.value.accent?"accent-vec":null,accentBody=makeSpan(["accent-body",vecClass],[makeSpan([],[accent])]);accentBody=buildCommon.makeVList([{type:"elem",elem:body},{type:"kern",size:-clearance},{type:"elem",elem:accentBody}],"firstBaseline",null,options),
// Shift the accent over by the skew. Note we shift by twice the skew
// because we are centering the accent, so by adding 2*skew to the left,
// we shift it to the right by 1*skew.
accentBody.children[1].style.marginLeft=2*skew+"em";var accentWrap=makeSpan(["mord","accent"],[accentBody]);return supsubGroup?(
// Here, we replace the "base" child of the supsub with our newly
// generated accent.
supsubGroup.children[0]=accentWrap,
// Since we don't rerun the height calculation after replacing the
// accent, we manually recalculate height.
supsubGroup.height=Math.max(accentWrap.height,supsubGroup.height),
// Accents should always be ords, even when their innards are not.
supsubGroup.classes[0]="mord",supsubGroup):accentWrap},phantom:function(group,options,prev){var elements=buildExpression(group.value.value,options.withPhantom(),prev);
// \phantom isn't supposed to affect the elements it contains.
// See "color" for more details.
return new buildCommon.makeFragment(elements)}},buildGroup=function(group,options,prev){if(!group)return makeSpan();if(groupTypes[group.type]){
// Call the groupTypes function
var multiplier,groupNode=groupTypes[group.type](group,options,prev);
// If the style changed between the parent and the current group,
// account for the size difference
return options.style!==options.parentStyle&&(multiplier=options.style.sizeMultiplier/options.parentStyle.sizeMultiplier,groupNode.height*=multiplier,groupNode.depth*=multiplier),
// If the size changed between the parent and the current group, account
// for that size difference.
options.size!==options.parentSize&&(multiplier=buildCommon.sizingMultiplier[options.size]/buildCommon.sizingMultiplier[options.parentSize],groupNode.height*=multiplier,groupNode.depth*=multiplier),groupNode}throw new ParseError("Got group of unknown type: '"+group.type+"'")},buildHTML=function(tree,options){
// buildExpression is destructive, so we need to make a clone
// of the incoming tree so that it isn't accidentally changed
tree=JSON.parse(JSON.stringify(tree));
// Build the expression contained in the tree
var expression=buildExpression(tree,options),body=makeSpan(["base",options.style.cls()],expression),topStrut=makeSpan(["strut"]),bottomStrut=makeSpan(["strut","bottom"]);topStrut.style.height=body.height+"em",bottomStrut.style.height=body.height+body.depth+"em",
// We'd like to use `vertical-align: top` but in IE 9 this lowers the
// baseline of the box to the bottom of this strut (instead staying in the
// normal place) so we use an absolute value for vertical-align instead
bottomStrut.style.verticalAlign=-body.depth+"em";
// Wrap the struts and body together
var htmlNode=makeSpan(["katex-html"],[topStrut,bottomStrut,body]);return htmlNode.setAttribute("aria-hidden","true"),htmlNode};module.exports=buildHTML},
/***/385843:
/***/function(module,__unused_webpack_exports,__webpack_require__){
/**
 * This file converts a parse tree into a cooresponding MathML tree. The main
 * entry point is the `buildMathML` function, which takes a parse tree from the
 * parser.
 */
var buildCommon=__webpack_require__(642084),fontMetrics=__webpack_require__(26386),mathMLTree=__webpack_require__(570284),ParseError=__webpack_require__(16856),symbols=__webpack_require__(710471),utils=__webpack_require__(25182),makeSpan=buildCommon.makeSpan,fontMap=buildCommon.fontMap,makeText=function(text,mode){return symbols[mode][text]&&symbols[mode][text].replace&&(text=symbols[mode][text].replace),new mathMLTree.TextNode(text)},getVariant=function(group,options){var font=options.font;if(!font)return null;var mode=group.mode;if("mathit"===font)return"italic";var value=group.value;if(utils.contains(["\\imath","\\jmath"],value))return null;symbols[mode][value]&&symbols[mode][value].replace&&(value=symbols[mode][value].replace);var fontName=fontMap[font].fontName;return fontMetrics.getCharacterMetrics(value,fontName)?fontMap[options.font].variant:null},groupTypes={mathord:function(group,options){var node=new mathMLTree.MathNode("mi",[makeText(group.value,group.mode)]),variant=getVariant(group,options);return variant&&node.setAttribute("mathvariant",variant),node},textord:function(group,options){var node,text=makeText(group.value,group.mode),variant=getVariant(group,options)||"normal";return/[0-9]/.test(group.value)?(
// TODO(kevinb) merge adjacent <mn> nodes
// do it as a post processing step
node=new mathMLTree.MathNode("mn",[text]),options.font&&node.setAttribute("mathvariant",variant)):(node=new mathMLTree.MathNode("mi",[text]),node.setAttribute("mathvariant",variant)),node},bin:function(group){var node=new mathMLTree.MathNode("mo",[makeText(group.value,group.mode)]);return node},rel:function(group){var node=new mathMLTree.MathNode("mo",[makeText(group.value,group.mode)]);return node},open:function(group){var node=new mathMLTree.MathNode("mo",[makeText(group.value,group.mode)]);return node},close:function(group){var node=new mathMLTree.MathNode("mo",[makeText(group.value,group.mode)]);return node},inner:function(group){var node=new mathMLTree.MathNode("mo",[makeText(group.value,group.mode)]);return node},punct:function(group){var node=new mathMLTree.MathNode("mo",[makeText(group.value,group.mode)]);return node.setAttribute("separator","true"),node},ordgroup:function(group,options){var inner=buildExpression(group.value,options),node=new mathMLTree.MathNode("mrow",inner);return node},text:function(group,options){var inner=buildExpression(group.value.body,options),node=new mathMLTree.MathNode("mtext",inner);return node},color:function(group,options){var inner=buildExpression(group.value.value,options),node=new mathMLTree.MathNode("mstyle",inner);return node.setAttribute("mathcolor",group.value.color),node},supsub:function(group,options){var nodeType,children=[buildGroup(group.value.base,options)];group.value.sub&&children.push(buildGroup(group.value.sub,options)),group.value.sup&&children.push(buildGroup(group.value.sup,options)),nodeType=group.value.sub?group.value.sup?"msubsup":"msub":"msup";var node=new mathMLTree.MathNode(nodeType,children);return node},genfrac:function(group,options){var node=new mathMLTree.MathNode("mfrac",[buildGroup(group.value.numer,options),buildGroup(group.value.denom,options)]);if(group.value.hasBarLine||node.setAttribute("linethickness","0px"),null!=group.value.leftDelim||null!=group.value.rightDelim){var withDelims=[];if(null!=group.value.leftDelim){var leftOp=new mathMLTree.MathNode("mo",[new mathMLTree.TextNode(group.value.leftDelim)]);leftOp.setAttribute("fence","true"),withDelims.push(leftOp)}if(withDelims.push(node),null!=group.value.rightDelim){var rightOp=new mathMLTree.MathNode("mo",[new mathMLTree.TextNode(group.value.rightDelim)]);rightOp.setAttribute("fence","true"),withDelims.push(rightOp)}var outerNode=new mathMLTree.MathNode("mrow",withDelims);return outerNode}return node},array:function(group,options){return new mathMLTree.MathNode("mtable",group.value.body.map((function(row){return new mathMLTree.MathNode("mtr",row.map((function(cell){return new mathMLTree.MathNode("mtd",[buildGroup(cell,options)])})))})))},sqrt:function(group,options){var node;return node=group.value.index?new mathMLTree.MathNode("mroot",[buildGroup(group.value.body,options),buildGroup(group.value.index,options)]):new mathMLTree.MathNode("msqrt",[buildGroup(group.value.body,options)]),node},leftright:function(group,options){var inner=buildExpression(group.value.body,options);if("."!==group.value.left){var leftNode=new mathMLTree.MathNode("mo",[makeText(group.value.left,group.mode)]);leftNode.setAttribute("fence","true"),inner.unshift(leftNode)}if("."!==group.value.right){var rightNode=new mathMLTree.MathNode("mo",[makeText(group.value.right,group.mode)]);rightNode.setAttribute("fence","true"),inner.push(rightNode)}var outerNode=new mathMLTree.MathNode("mrow",inner);return outerNode},accent:function(group,options){var accentNode=new mathMLTree.MathNode("mo",[makeText(group.value.accent,group.mode)]),node=new mathMLTree.MathNode("mover",[buildGroup(group.value.base,options),accentNode]);return node.setAttribute("accent","true"),node},spacing:function(group){var node;return"\\ "===group.value||"\\space"===group.value||" "===group.value||"~"===group.value?node=new mathMLTree.MathNode("mtext",[new mathMLTree.TextNode(" ")]):(node=new mathMLTree.MathNode("mspace"),node.setAttribute("width",buildCommon.spacingFunctions[group.value].size)),node},op:function(group){var node;
// TODO(emily): handle big operators using the `largeop` attribute
// This is a symbol. Just add the symbol.
return node=group.value.symbol?new mathMLTree.MathNode("mo",[makeText(group.value.body,group.mode)]):new mathMLTree.MathNode("mi",[new mathMLTree.TextNode(group.value.body.slice(1))]),node},katex:function(group){var node=new mathMLTree.MathNode("mtext",[new mathMLTree.TextNode("KaTeX")]);return node},font:function(group,options){var font=group.value.font;return buildGroup(group.value.body,options.withFont(font))},delimsizing:function(group){var children=[];"."!==group.value.value&&children.push(makeText(group.value.value,group.mode));var node=new mathMLTree.MathNode("mo",children);return"open"===group.value.delimType||"close"===group.value.delimType?
// Only some of the delimsizing functions act as fences, and they
// return "open" or "close" delimTypes.
node.setAttribute("fence","true"):
// Explicitly disable fencing if it's not a fence, to override the
// defaults.
node.setAttribute("fence","false"),node},styling:function(group,options){var inner=buildExpression(group.value.value,options),node=new mathMLTree.MathNode("mstyle",inner),styleAttributes={display:["0","true"],text:["0","false"],script:["1","false"],scriptscript:["2","false"]},attr=styleAttributes[group.value.style];return node.setAttribute("scriptlevel",attr[0]),node.setAttribute("displaystyle",attr[1]),node},sizing:function(group,options){var inner=buildExpression(group.value.value,options),node=new mathMLTree.MathNode("mstyle",inner);
// TODO(emily): This doesn't produce the correct size for nested size
// changes, because we don't keep state of what style we're currently
// in, so we can't reset the size to normal before changing it.  Now
// that we're passing an options parameter we should be able to fix
// this.
return node.setAttribute("mathsize",buildCommon.sizingMultiplier[group.value.size]+"em"),node},overline:function(group,options){var operator=new mathMLTree.MathNode("mo",[new mathMLTree.TextNode("‾")]);operator.setAttribute("stretchy","true");var node=new mathMLTree.MathNode("mover",[buildGroup(group.value.body,options),operator]);return node.setAttribute("accent","true"),node},underline:function(group,options){var operator=new mathMLTree.MathNode("mo",[new mathMLTree.TextNode("‾")]);operator.setAttribute("stretchy","true");var node=new mathMLTree.MathNode("munder",[buildGroup(group.value.body,options),operator]);return node.setAttribute("accentunder","true"),node},rule:function(group){
// TODO(emily): Figure out if there's an actual way to draw black boxes
// in MathML.
var node=new mathMLTree.MathNode("mrow");return node},llap:function(group,options){var node=new mathMLTree.MathNode("mpadded",[buildGroup(group.value.body,options)]);return node.setAttribute("lspace","-1width"),node.setAttribute("width","0px"),node},rlap:function(group,options){var node=new mathMLTree.MathNode("mpadded",[buildGroup(group.value.body,options)]);return node.setAttribute("width","0px"),node},phantom:function(group,options,prev){var inner=buildExpression(group.value.value,options);return new mathMLTree.MathNode("mphantom",inner)}},buildExpression=function(expression,options){for(var groups=[],i=0;i<expression.length;i++){var group=expression[i];groups.push(buildGroup(group,options))}return groups},buildGroup=function(group,options){if(!group)return new mathMLTree.MathNode("mrow");if(groupTypes[group.type])
// Call the groupTypes function
return groupTypes[group.type](group,options);throw new ParseError("Got group of unknown type: '"+group.type+"'")},buildMathML=function(tree,texExpression,options){var expression=buildExpression(tree,options),wrapper=new mathMLTree.MathNode("mrow",expression),annotation=new mathMLTree.MathNode("annotation",[new mathMLTree.TextNode(texExpression)]);
// Wrap up the expression in an mrow so it is presented in the semantics
// tag correctly.
annotation.setAttribute("encoding","application/x-tex");var semantics=new mathMLTree.MathNode("semantics",[wrapper,annotation]),math=new mathMLTree.MathNode("math",[semantics]);
// You can't style <math> nodes, so we wrap the node in a span.
return makeSpan(["katex-mathml"],[math])};module.exports=buildMathML},
/***/502486:
/***/function(module,__unused_webpack_exports,__webpack_require__){var buildHTML=__webpack_require__(460522),buildMathML=__webpack_require__(385843),buildCommon=__webpack_require__(642084),Options=__webpack_require__(757599),Settings=__webpack_require__(594974),Style=__webpack_require__(627741),makeSpan=buildCommon.makeSpan,buildTree=function(tree,expression,settings){settings=settings||new Settings({});var startStyle=Style.TEXT;settings.displayMode&&(startStyle=Style.DISPLAY);
// Setup the default options
var options=new Options({style:startStyle,size:"size5"}),mathMLNode=buildMathML(tree,expression,options),htmlNode=buildHTML(tree,options),katexNode=makeSpan(["katex"],[mathMLNode,htmlNode]);
// `buildHTML` sometimes messes with the parse tree (like turning bins ->
// ords), so we build the MathML version first.
return settings.displayMode?makeSpan(["katex-display"],[katexNode]):katexNode};module.exports=buildTree},
/***/337870:
/***/function(module,__unused_webpack_exports,__webpack_require__){
/**
 * This file deals with creating delimiters of various sizes. The TeXbook
 * discusses these routines on page 441-442, in the "Another subroutine sets box
 * x to a specified variable delimiter" paragraph.
 *
 * There are three main routines here. `makeSmallDelim` makes a delimiter in the
 * normal font, but in either text, script, or scriptscript style.
 * `makeLargeDelim` makes a delimiter in textstyle, but in one of the Size1,
 * Size2, Size3, or Size4 fonts. `makeStackedDelim` makes a delimiter out of
 * smaller pieces that are stacked on top of one another.
 *
 * The functions take a parameter `center`, which determines if the delimiter
 * should be centered around the axis.
 *
 * Then, there are three exposed functions. `sizedDelim` makes a delimiter in
 * one of the given sizes. This is used for things like `\bigl`.
 * `customSizedDelim` makes a delimiter with a given total height+depth. It is
 * called in places like `\sqrt`. `leftRightDelim` makes an appropriate
 * delimiter which surrounds an expression of a given height an depth. It is
 * used in `\left` and `\right`.
 */
var ParseError=__webpack_require__(16856),Style=__webpack_require__(627741),buildCommon=__webpack_require__(642084),fontMetrics=__webpack_require__(26386),symbols=__webpack_require__(710471),utils=__webpack_require__(25182),makeSpan=buildCommon.makeSpan,getMetrics=function(symbol,font){return symbols.math[symbol]&&symbols.math[symbol].replace?fontMetrics.getCharacterMetrics(symbols.math[symbol].replace,font):fontMetrics.getCharacterMetrics(symbol,font)},mathrmSize=function(value,size,mode){return buildCommon.makeSymbol(value,"Size"+size+"-Regular",mode)},styleWrap=function(delim,toStyle,options){var span=makeSpan(["style-wrap",options.style.reset(),toStyle.cls()],[delim]),multiplier=toStyle.sizeMultiplier/options.style.sizeMultiplier;return span.height*=multiplier,span.depth*=multiplier,span.maxFontSize=toStyle.sizeMultiplier,span},makeSmallDelim=function(delim,style,center,options,mode){var text=buildCommon.makeSymbol(delim,"Main-Regular",mode),span=styleWrap(text,style,options);if(center){var shift=(1-options.style.sizeMultiplier/style.sizeMultiplier)*fontMetrics.metrics.axisHeight;span.style.top=shift+"em",span.height-=shift,span.depth+=shift}return span},makeLargeDelim=function(delim,size,center,options,mode){var inner=mathrmSize(delim,size,mode),span=styleWrap(makeSpan(["delimsizing","size"+size],[inner],options.getColor()),Style.TEXT,options);if(center){var shift=(1-options.style.sizeMultiplier)*fontMetrics.metrics.axisHeight;span.style.top=shift+"em",span.height-=shift,span.depth+=shift}return span},makeInner=function(symbol,font,mode){var sizeClass;
// Apply the correct CSS class to choose the right font.
"Size1-Regular"===font?sizeClass="delim-size1":"Size4-Regular"===font&&(sizeClass="delim-size4");var inner=makeSpan(["delimsizinginner",sizeClass],[makeSpan([],[buildCommon.makeSymbol(symbol,font,mode)])]);
// Since this will be passed into `makeVList` in the end, wrap the element
// in the appropriate tag that VList uses.
return{type:"elem",elem:inner}},makeStackedDelim=function(delim,heightTotal,center,options,mode){
// There are four parts, the top, an optional middle, a repeated part, and a
// bottom.
var top,middle,repeat,bottom;top=repeat=bottom=delim,middle=null;
// Also keep track of what font the delimiters are in
var font="Size1-Regular";
// We set the parts and font based on the symbol. Note that we use
// '\u23d0' instead of '|' and '\u2016' instead of '\\|' for the
// repeats of the arrows
"\\uparrow"===delim?repeat=bottom="⏐":"\\Uparrow"===delim?repeat=bottom="‖":"\\downarrow"===delim?top=repeat="⏐":"\\Downarrow"===delim?top=repeat="‖":"\\updownarrow"===delim?(top="\\uparrow",repeat="⏐",bottom="\\downarrow"):"\\Updownarrow"===delim?(top="\\Uparrow",repeat="‖",bottom="\\Downarrow"):"["===delim||"\\lbrack"===delim?(top="⎡",repeat="⎢",bottom="⎣",font="Size4-Regular"):"]"===delim||"\\rbrack"===delim?(top="⎤",repeat="⎥",bottom="⎦",font="Size4-Regular"):"\\lfloor"===delim?(repeat=top="⎢",bottom="⎣",font="Size4-Regular"):"\\lceil"===delim?(top="⎡",repeat=bottom="⎢",font="Size4-Regular"):"\\rfloor"===delim?(repeat=top="⎥",bottom="⎦",font="Size4-Regular"):"\\rceil"===delim?(top="⎤",repeat=bottom="⎥",font="Size4-Regular"):"("===delim?(top="⎛",repeat="⎜",bottom="⎝",font="Size4-Regular"):")"===delim?(top="⎞",repeat="⎟",bottom="⎠",font="Size4-Regular"):"\\{"===delim||"\\lbrace"===delim?(top="⎧",middle="⎨",bottom="⎩",repeat="⎪",font="Size4-Regular"):"\\}"===delim||"\\rbrace"===delim?(top="⎫",middle="⎬",bottom="⎭",repeat="⎪",font="Size4-Regular"):"\\lgroup"===delim?(top="⎧",bottom="⎩",repeat="⎪",font="Size4-Regular"):"\\rgroup"===delim?(top="⎫",bottom="⎭",repeat="⎪",font="Size4-Regular"):"\\lmoustache"===delim?(top="⎧",bottom="⎭",repeat="⎪",font="Size4-Regular"):"\\rmoustache"===delim?(top="⎫",bottom="⎩",repeat="⎪",font="Size4-Regular"):"\\surd"===delim&&(top="",bottom="⎷",repeat="",font="Size4-Regular");
// Get the metrics of the four sections
var topMetrics=getMetrics(top,font),topHeightTotal=topMetrics.height+topMetrics.depth,repeatMetrics=getMetrics(repeat,font),repeatHeightTotal=repeatMetrics.height+repeatMetrics.depth,bottomMetrics=getMetrics(bottom,font),bottomHeightTotal=bottomMetrics.height+bottomMetrics.depth,middleHeightTotal=0,middleFactor=1;if(null!==middle){var middleMetrics=getMetrics(middle,font);middleHeightTotal=middleMetrics.height+middleMetrics.depth,middleFactor=2}
// Calcuate the minimal height that the delimiter can have.
// It is at least the size of the top, bottom, and optional middle combined.
var minHeight=topHeightTotal+bottomHeightTotal+middleHeightTotal,repeatCount=Math.ceil((heightTotal-minHeight)/(middleFactor*repeatHeightTotal)),realHeightTotal=minHeight+repeatCount*middleFactor*repeatHeightTotal,axisHeight=fontMetrics.metrics.axisHeight;
// Compute the number of copies of the repeat symbol we will need
center&&(axisHeight*=options.style.sizeMultiplier);
// Calculate the depth
var i,depth=realHeightTotal/2-axisHeight,inners=[];
// Now, we start building the pieces that will go into the vlist
// Keep a list of the inner pieces
if(
// Add the bottom symbol
inners.push(makeInner(bottom,font,mode)),null===middle)
// Add that many symbols
for(i=0;i<repeatCount;i++)inners.push(makeInner(repeat,font,mode));else{
// When there is a middle bit, we need the middle part and two repeated
// sections
for(i=0;i<repeatCount;i++)inners.push(makeInner(repeat,font,mode));for(inners.push(makeInner(middle,font,mode)),i=0;i<repeatCount;i++)inners.push(makeInner(repeat,font,mode))}
// Add the top symbol
inners.push(makeInner(top,font,mode));
// Finally, build the vlist
var inner=buildCommon.makeVList(inners,"bottom",depth,options);return styleWrap(makeSpan(["delimsizing","mult"],[inner],options.getColor()),Style.TEXT,options)},stackLargeDelimiters=["(",")","[","\\lbrack","]","\\rbrack","\\{","\\lbrace","\\}","\\rbrace","\\lfloor","\\rfloor","\\lceil","\\rceil","\\surd"],stackAlwaysDelimiters=["\\uparrow","\\downarrow","\\updownarrow","\\Uparrow","\\Downarrow","\\Updownarrow","|","\\|","\\vert","\\Vert","\\lvert","\\rvert","\\lVert","\\rVert","\\lgroup","\\rgroup","\\lmoustache","\\rmoustache"],stackNeverDelimiters=["<",">","\\langle","\\rangle","/","\\backslash","\\lt","\\gt"],sizeToMaxHeight=[0,1.2,1.8,2.4,3],makeSizedDelim=function(delim,size,options,mode){
// Sized delimiters are never centered.
if(
// < and > turn into \langle and \rangle in delimiters
"<"===delim||"\\lt"===delim?delim="\\langle":">"!==delim&&"\\gt"!==delim||(delim="\\rangle"),utils.contains(stackLargeDelimiters,delim)||utils.contains(stackNeverDelimiters,delim))return makeLargeDelim(delim,size,!1,options,mode);if(utils.contains(stackAlwaysDelimiters,delim))return makeStackedDelim(delim,sizeToMaxHeight[size],!1,options,mode);throw new ParseError("Illegal delimiter: '"+delim+"'")},stackNeverDelimiterSequence=[{type:"small",style:Style.SCRIPTSCRIPT},{type:"small",style:Style.SCRIPT},{type:"small",style:Style.TEXT},{type:"large",size:1},{type:"large",size:2},{type:"large",size:3},{type:"large",size:4}],stackAlwaysDelimiterSequence=[{type:"small",style:Style.SCRIPTSCRIPT},{type:"small",style:Style.SCRIPT},{type:"small",style:Style.TEXT},{type:"stack"}],stackLargeDelimiterSequence=[{type:"small",style:Style.SCRIPTSCRIPT},{type:"small",style:Style.SCRIPT},{type:"small",style:Style.TEXT},{type:"large",size:1},{type:"large",size:2},{type:"large",size:3},{type:"large",size:4},{type:"stack"}],delimTypeToFont=function(type){return"small"===type.type?"Main-Regular":"large"===type.type?"Size"+type.size+"-Regular":"stack"===type.type?"Size4-Regular":void 0},traverseSequence=function(delim,height,sequence,options){for(
// Here, we choose the index we should start at in the sequences. In smaller
// sizes (which correspond to larger numbers in style.size) we start earlier
// in the sequence. Thus, scriptscript starts at index 3-3=0, script starts
// at index 3-2=1, text starts at 3-1=2, and display starts at min(2,3-0)=2
var start=Math.min(2,3-options.style.size),i=start;i<sequence.length;i++){if("stack"===sequence[i].type)
// This is always the last delimiter, so we just break the loop now.
break;var metrics=getMetrics(delim,delimTypeToFont(sequence[i])),heightDepth=metrics.height+metrics.depth;
// Check if the delimiter at this size works for the given height.
if(
// Small delimiters are scaled down versions of the same font, so we
// account for the style change size.
"small"===sequence[i].type&&(heightDepth*=sequence[i].style.sizeMultiplier),heightDepth>height)return sequence[i]}
// If we reached the end of the sequence, return the last sequence element.
return sequence[sequence.length-1]},makeCustomSizedDelim=function(delim,height,center,options,mode){
// Decide what sequence to use
var sequence;"<"===delim||"\\lt"===delim?delim="\\langle":">"!==delim&&"\\gt"!==delim||(delim="\\rangle"),sequence=utils.contains(stackNeverDelimiters,delim)?stackNeverDelimiterSequence:utils.contains(stackLargeDelimiters,delim)?stackLargeDelimiterSequence:stackAlwaysDelimiterSequence;
// Look through the sequence
var delimType=traverseSequence(delim,height,sequence,options);
// Depending on the sequence element we decided on, call the appropriate
// function.
return"small"===delimType.type?makeSmallDelim(delim,delimType.style,center,options,mode):"large"===delimType.type?makeLargeDelim(delim,delimType.size,center,options,mode):"stack"===delimType.type?makeStackedDelim(delim,height,center,options,mode):void 0},makeLeftRightDelim=function(delim,height,depth,options,mode){
// We always center \left/\right delimiters, so the axis is always shifted
var axisHeight=fontMetrics.metrics.axisHeight*options.style.sizeMultiplier,delimiterFactor=901,delimiterExtend=5/fontMetrics.metrics.ptPerEm,maxDistFromAxis=Math.max(height-axisHeight,depth+axisHeight),totalHeight=Math.max(
// In real TeX, calculations are done using integral values which are
// 65536 per pt, or 655360 per em. So, the division here truncates in
// TeX but doesn't here, producing different results. If we wanted to
// exactly match TeX's calculation, we could do
//   Math.floor(655360 * maxDistFromAxis / 500) *
//    delimiterFactor / 655360
// (To see the difference, compare
//    x^{x^{\left(\rule{0.1em}{0.68em}\right)}}
// in TeX and KaTeX)
maxDistFromAxis/500*delimiterFactor,2*maxDistFromAxis-delimiterExtend);
// Taken from TeX source, tex.web, function make_left_right
// Finally, we defer to `makeCustomSizedDelim` with our calculated total
// height
return makeCustomSizedDelim(delim,totalHeight,!0,options,mode)};module.exports={sizedDelim:makeSizedDelim,customSizedDelim:makeCustomSizedDelim,leftRightDelim:makeLeftRightDelim}},
/***/304104:
/***/function(module,__unused_webpack_exports,__webpack_require__){
/**
 * These objects store the data about the DOM nodes we create, as well as some
 * extra data. They can then be transformed into real DOM nodes with the
 * `toNode` function or HTML markup using `toMarkup`. They are useful for both
 * storing extra properties on the nodes, as well as providing a way to easily
 * work with the DOM.
 *
 * Similar functions for working with MathML nodes exist in mathMLTree.js.
 */
var utils=__webpack_require__(25182),createClass=function(classes){classes=classes.slice();for(var i=classes.length-1;i>=0;i--)classes[i]||classes.splice(i,1);return classes.join(" ")};
/**
 * Create an HTML className based on a list of classes. In addition to joining
 * with spaces, we also remove null or empty classes.
 */
/**
 * This node represents a span node, with a className, a list of children, and
 * an inline style. It also contains information about its height, depth, and
 * maxFontSize.
 */
function span(classes,children,height,depth,maxFontSize,style){this.classes=classes||[],this.children=children||[],this.height=height||0,this.depth=depth||0,this.maxFontSize=maxFontSize||0,this.style=style||{},this.attributes={}}
/**
 * Sets an arbitrary attribute on the span. Warning: use this wisely. Not all
 * browsers support attributes the same, and having too many custom attributes
 * is probably bad.
 */
/**
 * This node represents a document fragment, which contains elements, but when
 * placed into the DOM doesn't have any representation itself. Thus, it only
 * contains children and doesn't have any HTML properties. It also keeps track
 * of a height, depth, and maxFontSize.
 */
function documentFragment(children,height,depth,maxFontSize){this.children=children||[],this.height=height||0,this.depth=depth||0,this.maxFontSize=maxFontSize||0}
/**
 * Convert the fragment into a node
 */
/**
 * A symbol node contains information about a single symbol. It either renders
 * to a single text node, or a span with a single text node in it, depending on
 * whether it has CSS classes, styles, or needs italic correction.
 */
function symbolNode(value,height,depth,italic,skew,classes,style){this.value=value||"",this.height=height||0,this.depth=depth||0,this.italic=italic||0,this.skew=skew||0,this.classes=classes||[],this.style=style||{},this.maxFontSize=0}
/**
 * Creates a text node or span from a symbol node. Note that a span is only
 * created if it is needed.
 */span.prototype.setAttribute=function(attribute,value){this.attributes[attribute]=value},
/**
 * Convert the span into an HTML node
 */
span.prototype.toNode=function(){var span=document.createElement("span");
// Apply the class
// Apply inline styles
for(var style in span.className=createClass(this.classes),this.style)Object.prototype.hasOwnProperty.call(this.style,style)&&(span.style[style]=this.style[style]);
// Apply attributes
for(var attr in this.attributes)Object.prototype.hasOwnProperty.call(this.attributes,attr)&&span.setAttribute(attr,this.attributes[attr]);
// Append the children, also as HTML nodes
for(var i=0;i<this.children.length;i++)span.appendChild(this.children[i].toNode());return span},
/**
 * Convert the span into an HTML markup string
 */
span.prototype.toMarkup=function(){var markup="<span";
// Add the class
this.classes.length&&(markup+=' class="',markup+=utils.escape(createClass(this.classes)),markup+='"');var styles="";
// Add the styles, after hyphenation
for(var style in this.style)this.style.hasOwnProperty(style)&&(styles+=utils.hyphenate(style)+":"+this.style[style]+";");
// Add the attributes
for(var attr in styles&&(markup+=' style="'+utils.escape(styles)+'"'),this.attributes)Object.prototype.hasOwnProperty.call(this.attributes,attr)&&(markup+=" "+attr+'="',markup+=utils.escape(this.attributes[attr]),markup+='"');markup+=">";
// Add the markup of the children, also as markup
for(var i=0;i<this.children.length;i++)markup+=this.children[i].toMarkup();return markup+="</span>",markup},documentFragment.prototype.toNode=function(){
// Append the children
for(
// Create a fragment
var frag=document.createDocumentFragment(),i=0;i<this.children.length;i++)frag.appendChild(this.children[i].toNode());return frag},
/**
 * Convert the fragment into HTML markup
 */
documentFragment.prototype.toMarkup=function(){
// Simply concatenate the markup for the children together
for(var markup="",i=0;i<this.children.length;i++)markup+=this.children[i].toMarkup();return markup},symbolNode.prototype.toNode=function(){var node=document.createTextNode(this.value),span=null;for(var style in this.italic>0&&(span=document.createElement("span"),span.style.marginRight=this.italic+"em"),this.classes.length>0&&(span=span||document.createElement("span"),span.className=createClass(this.classes)),this.style)this.style.hasOwnProperty(style)&&(span=span||document.createElement("span"),span.style[style]=this.style[style]);return span?(span.appendChild(node),span):node},
/**
 * Creates markup for a symbol node.
 */
symbolNode.prototype.toMarkup=function(){
// TODO(alpert): More duplication than I'd like from
// span.prototype.toMarkup and symbolNode.prototype.toNode...
var needsSpan=!1,markup="<span";this.classes.length&&(needsSpan=!0,markup+=' class="',markup+=utils.escape(createClass(this.classes)),markup+='"');var styles="";for(var style in this.italic>0&&(styles+="margin-right:"+this.italic+"em;"),this.style)this.style.hasOwnProperty(style)&&(styles+=utils.hyphenate(style)+":"+this.style[style]+";");styles&&(needsSpan=!0,markup+=' style="'+utils.escape(styles)+'"');var escaped=utils.escape(this.value);return needsSpan?(markup+=">",markup+=escaped,markup+="</span>",markup):escaped},module.exports={span:span,documentFragment:documentFragment,symbolNode:symbolNode}},
/***/345707:
/***/function(module,__unused_webpack_exports,__webpack_require__){
/* eslint no-constant-condition:0 */
var fontMetrics=__webpack_require__(26386),parseData=__webpack_require__(791758),ParseError=__webpack_require__(16856),ParseNode=parseData.ParseNode;
/**
 * Parse the body of the environment, with rows delimited by \\ and
 * columns delimited by &, and create a nested list in row-major order
 * with one group per cell.
 */
function parseArray(parser,result){var row=[],body=[row],rowGaps=[];while(1){var cell=parser.parseExpression(!1,null);row.push(new ParseNode("ordgroup",cell,parser.mode));var next=parser.nextToken.text;if("&"===next)parser.consume();else{if("\\end"===next)break;if("\\\\"!==next&&"\\cr"!==next){
// TODO: Clean up the following hack once #385 got merged
var pos=Math.min(parser.pos+1,parser.lexer._input.length);throw new ParseError("Expected & or \\\\ or \\end",parser.lexer,pos)}var cr=parser.parseFunction();rowGaps.push(cr.value.size),row=[],body.push(row)}}return result.body=body,result.rowGaps=rowGaps,new ParseNode(result.type,result,parser.mode)}
/*
 * An environment definition is very similar to a function definition:
 * it is declared with a name or a list of names, a set of properties
 * and a handler containing the actual implementation.
 *
 * The properties include:
 *  - numArgs: The number of arguments after the \begin{name} function.
 *  - argTypes: (optional) Just like for a function
 *  - allowedInText: (optional) Whether or not the environment is allowed inside
 *                   text mode (default false) (not enforced yet)
 *  - numOptionalArgs: (optional) Just like for a function
 * A bare number instead of that object indicates the numArgs value.
 *
 * The handler function will receive two arguments
 *  - context: information and references provided by the parser
 *  - args: an array of arguments passed to \begin{name}
 * The context contains the following properties:
 *  - envName: the name of the environment, one of the listed names.
 *  - parser: the parser object
 *  - lexer: the lexer object
 *  - positions: the positions associated with these arguments from args.
 * The handler must return a ParseResult.
 */function defineEnvironment(names,props,handler){"string"===typeof names&&(names=[names]),"number"===typeof props&&(props={numArgs:props});for(
// Set default values of environments
var data={numArgs:props.numArgs||0,argTypes:props.argTypes,greediness:1,allowedInText:!!props.allowedInText,numOptionalArgs:props.numOptionalArgs||0,handler:handler},i=0;i<names.length;++i)module.exports[names[i]]=data}
// Arrays are part of LaTeX, defined in lttab.dtx so its documentation
// is part of the source2e.pdf file of LaTeX2e source documentation.
defineEnvironment("array",{numArgs:1},(function(context,args){var colalign=args[0];colalign=colalign.value.map?colalign.value:[colalign];var cols=colalign.map((function(node){var ca=node.value;if(-1!=="lcr".indexOf(ca))return{type:"align",align:ca};if("|"===ca)return{type:"separator",separator:"|"};throw new ParseError("Unknown column alignment: "+node.value,context.lexer,context.positions[1])})),res={type:"array",cols:cols,hskipBeforeAndAfter:!0};return res=parseArray(context.parser,res),res})),
// The matrix environments of amsmath builds on the array environment
// of LaTeX, which is discussed above.
defineEnvironment(["matrix","pmatrix","bmatrix","Bmatrix","vmatrix","Vmatrix"],{},(function(context){var delimiters={matrix:null,pmatrix:["(",")"],bmatrix:["[","]"],Bmatrix:["\\{","\\}"],vmatrix:["|","|"],Vmatrix:["\\Vert","\\Vert"]}[context.envName],res={type:"array",hskipBeforeAndAfter:!1};return res=parseArray(context.parser,res),delimiters&&(res=new ParseNode("leftright",{body:[res],left:delimiters[0],right:delimiters[1]},context.mode)),res})),
// A cases environment (in amsmath.sty) is almost equivalent to
// \def\arraystretch{1.2}%
// \left\{\begin{array}{@{}l@{\quad}l@{}} … \end{array}\right.
defineEnvironment("cases",{},(function(context){var res={type:"array",arraystretch:1.2,cols:[{type:"align",align:"l",pregap:0,postgap:fontMetrics.metrics.quad},{type:"align",align:"l",pregap:0,postgap:0}]};return res=parseArray(context.parser,res),res=new ParseNode("leftright",{body:[res],left:"\\{",right:"."},context.mode),res})),
// An aligned environment is like the align* environment
// except it operates within math mode.
// Note that we assume \nomallineskiplimit to be zero,
// so that \strut@ is the same as \strut.
defineEnvironment("aligned",{},(function(context){var res={type:"array",cols:[]};res=parseArray(context.parser,res);var emptyGroup=new ParseNode("ordgroup",[],context.mode),numCols=0;res.value.body.forEach((function(row){var i;for(i=1;i<row.length;i+=2)row[i].value.unshift(emptyGroup);numCols<row.length&&(numCols=row.length)}));for(var i=0;i<numCols;++i){var align="r",pregap=0;i%2===1?align="l":i>0&&(pregap=2),res.value.cols[i]={type:"align",align:align,pregap:pregap,postgap:0}}return res}))},
/***/26386:
/***/function(module,__unused_webpack_exports,__webpack_require__){
/* eslint no-unused-vars:0 */
var Style=__webpack_require__(627741),sigma5=.431,sigma6=1,sigma8=.677,sigma9=.394,sigma10=.444,sigma11=.686,sigma12=.345,sigma13=.413,sigma14=.363,sigma15=.289,sigma16=.15,sigma17=.247,sigma18=.386,sigma19=.05,sigma20=2.39,sigma21=1.01,sigma21Script=.81,sigma21ScriptScript=.71,sigma22=.25,xi8=.04,xi9=.111,xi10=.166,xi11=.2,xi12=.6,xi13=.1,ptPerEm=10,doubleRuleSep=2/ptPerEm,metrics={xHeight:sigma5,quad:sigma6,num1:sigma8,num2:sigma9,num3:sigma10,denom1:sigma11,denom2:sigma12,sup1:sigma13,sup2:sigma14,sup3:sigma15,sub1:sigma16,sub2:sigma17,supDrop:sigma18,subDrop:sigma19,axisHeight:sigma22,defaultRuleThickness:xi8,bigOpSpacing1:xi9,bigOpSpacing2:xi10,bigOpSpacing3:xi11,bigOpSpacing4:xi12,bigOpSpacing5:xi13,ptPerEm:ptPerEm,emPerEx:sigma5/sigma6,doubleRuleSep:doubleRuleSep,
// TODO(alpert): Missing parallel structure here. We should probably add
// style-specific metrics for all of these.
delim1:sigma20,getDelim2:function(style){if(style.size===Style.TEXT.size)return sigma21;if(style.size===Style.SCRIPT.size)return sigma21Script;if(style.size===Style.SCRIPTSCRIPT.size)return sigma21ScriptScript;throw new Error("Unexpected style size: "+style.size)}},metricMap=__webpack_require__(534875),getCharacterMetrics=function(character,style){var metrics=metricMap[style][character.charCodeAt(0)];if(metrics)return{depth:metrics[0],height:metrics[1],italic:metrics[2],skew:metrics[3],width:metrics[4]}};
/**
 * This file contains metrics regarding fonts and individual symbols. The sigma
 * and xi variables, as well as the metricMap map contain data extracted from
 * TeX, TeX font metrics, and the TTF files. These data are then exposed via the
 * `metrics` variable and the getCharacterMetrics function.
 */
// These font metrics are extracted from TeX by using
// \font\a=cmmi10
// \showthe\fontdimenX\a
// where X is the corresponding variable number. These correspond to the font
// parameters of the symbol fonts. In TeX, there are actually three sets of
// dimensions, one for each of textstyle, scriptstyle, and scriptscriptstyle,
// but we only use the textstyle ones, and scale certain dimensions accordingly.
// See the TeXbook, page 441.
module.exports={metrics:metrics,getCharacterMetrics:getCharacterMetrics}},
/***/534875:
/***/function(module){module.exports={"AMS-Regular":{65:[0,.68889,0,0],66:[0,.68889,0,0],67:[0,.68889,0,0],68:[0,.68889,0,0],69:[0,.68889,0,0],70:[0,.68889,0,0],71:[0,.68889,0,0],72:[0,.68889,0,0],73:[0,.68889,0,0],74:[.16667,.68889,0,0],75:[0,.68889,0,0],76:[0,.68889,0,0],77:[0,.68889,0,0],78:[0,.68889,0,0],79:[.16667,.68889,0,0],80:[0,.68889,0,0],81:[.16667,.68889,0,0],82:[0,.68889,0,0],83:[0,.68889,0,0],84:[0,.68889,0,0],85:[0,.68889,0,0],86:[0,.68889,0,0],87:[0,.68889,0,0],88:[0,.68889,0,0],89:[0,.68889,0,0],90:[0,.68889,0,0],107:[0,.68889,0,0],165:[0,.675,.025,0],174:[.15559,.69224,0,0],240:[0,.68889,0,0],295:[0,.68889,0,0],710:[0,.825,0,0],732:[0,.9,0,0],770:[0,.825,0,0],771:[0,.9,0,0],989:[.08167,.58167,0,0],1008:[0,.43056,.04028,0],8245:[0,.54986,0,0],8463:[0,.68889,0,0],8487:[0,.68889,0,0],8498:[0,.68889,0,0],8502:[0,.68889,0,0],8503:[0,.68889,0,0],8504:[0,.68889,0,0],8513:[0,.68889,0,0],8592:[-.03598,.46402,0,0],8594:[-.03598,.46402,0,0],8602:[-.13313,.36687,0,0],8603:[-.13313,.36687,0,0],8606:[.01354,.52239,0,0],8608:[.01354,.52239,0,0],8610:[.01354,.52239,0,0],8611:[.01354,.52239,0,0],8619:[0,.54986,0,0],8620:[0,.54986,0,0],8621:[-.13313,.37788,0,0],8622:[-.13313,.36687,0,0],8624:[0,.69224,0,0],8625:[0,.69224,0,0],8630:[0,.43056,0,0],8631:[0,.43056,0,0],8634:[.08198,.58198,0,0],8635:[.08198,.58198,0,0],8638:[.19444,.69224,0,0],8639:[.19444,.69224,0,0],8642:[.19444,.69224,0,0],8643:[.19444,.69224,0,0],8644:[.1808,.675,0,0],8646:[.1808,.675,0,0],8647:[.1808,.675,0,0],8648:[.19444,.69224,0,0],8649:[.1808,.675,0,0],8650:[.19444,.69224,0,0],8651:[.01354,.52239,0,0],8652:[.01354,.52239,0,0],8653:[-.13313,.36687,0,0],8654:[-.13313,.36687,0,0],8655:[-.13313,.36687,0,0],8666:[.13667,.63667,0,0],8667:[.13667,.63667,0,0],8669:[-.13313,.37788,0,0],8672:[-.064,.437,0,0],8674:[-.064,.437,0,0],8705:[0,.825,0,0],8708:[0,.68889,0,0],8709:[.08167,.58167,0,0],8717:[0,.43056,0,0],8722:[-.03598,.46402,0,0],8724:[.08198,.69224,0,0],8726:[.08167,.58167,0,0],8733:[0,.69224,0,0],8736:[0,.69224,0,0],8737:[0,.69224,0,0],8738:[.03517,.52239,0,0],8739:[.08167,.58167,0,0],8740:[.25142,.74111,0,0],8741:[.08167,.58167,0,0],8742:[.25142,.74111,0,0],8756:[0,.69224,0,0],8757:[0,.69224,0,0],8764:[-.13313,.36687,0,0],8765:[-.13313,.37788,0,0],8769:[-.13313,.36687,0,0],8770:[-.03625,.46375,0,0],8774:[.30274,.79383,0,0],8776:[-.01688,.48312,0,0],8778:[.08167,.58167,0,0],8782:[.06062,.54986,0,0],8783:[.06062,.54986,0,0],8785:[.08198,.58198,0,0],8786:[.08198,.58198,0,0],8787:[.08198,.58198,0,0],8790:[0,.69224,0,0],8791:[.22958,.72958,0,0],8796:[.08198,.91667,0,0],8806:[.25583,.75583,0,0],8807:[.25583,.75583,0,0],8808:[.25142,.75726,0,0],8809:[.25142,.75726,0,0],8812:[.25583,.75583,0,0],8814:[.20576,.70576,0,0],8815:[.20576,.70576,0,0],8816:[.30274,.79383,0,0],8817:[.30274,.79383,0,0],8818:[.22958,.72958,0,0],8819:[.22958,.72958,0,0],8822:[.1808,.675,0,0],8823:[.1808,.675,0,0],8828:[.13667,.63667,0,0],8829:[.13667,.63667,0,0],8830:[.22958,.72958,0,0],8831:[.22958,.72958,0,0],8832:[.20576,.70576,0,0],8833:[.20576,.70576,0,0],8840:[.30274,.79383,0,0],8841:[.30274,.79383,0,0],8842:[.13597,.63597,0,0],8843:[.13597,.63597,0,0],8847:[.03517,.54986,0,0],8848:[.03517,.54986,0,0],8858:[.08198,.58198,0,0],8859:[.08198,.58198,0,0],8861:[.08198,.58198,0,0],8862:[0,.675,0,0],8863:[0,.675,0,0],8864:[0,.675,0,0],8865:[0,.675,0,0],8872:[0,.69224,0,0],8873:[0,.69224,0,0],8874:[0,.69224,0,0],8876:[0,.68889,0,0],8877:[0,.68889,0,0],8878:[0,.68889,0,0],8879:[0,.68889,0,0],8882:[.03517,.54986,0,0],8883:[.03517,.54986,0,0],8884:[.13667,.63667,0,0],8885:[.13667,.63667,0,0],8888:[0,.54986,0,0],8890:[.19444,.43056,0,0],8891:[.19444,.69224,0,0],8892:[.19444,.69224,0,0],8901:[0,.54986,0,0],8903:[.08167,.58167,0,0],8905:[.08167,.58167,0,0],8906:[.08167,.58167,0,0],8907:[0,.69224,0,0],8908:[0,.69224,0,0],8909:[-.03598,.46402,0,0],8910:[0,.54986,0,0],8911:[0,.54986,0,0],8912:[.03517,.54986,0,0],8913:[.03517,.54986,0,0],8914:[0,.54986,0,0],8915:[0,.54986,0,0],8916:[0,.69224,0,0],8918:[.0391,.5391,0,0],8919:[.0391,.5391,0,0],8920:[.03517,.54986,0,0],8921:[.03517,.54986,0,0],8922:[.38569,.88569,0,0],8923:[.38569,.88569,0,0],8926:[.13667,.63667,0,0],8927:[.13667,.63667,0,0],8928:[.30274,.79383,0,0],8929:[.30274,.79383,0,0],8934:[.23222,.74111,0,0],8935:[.23222,.74111,0,0],8936:[.23222,.74111,0,0],8937:[.23222,.74111,0,0],8938:[.20576,.70576,0,0],8939:[.20576,.70576,0,0],8940:[.30274,.79383,0,0],8941:[.30274,.79383,0,0],8994:[.19444,.69224,0,0],8995:[.19444,.69224,0,0],9416:[.15559,.69224,0,0],9484:[0,.69224,0,0],9488:[0,.69224,0,0],9492:[0,.37788,0,0],9496:[0,.37788,0,0],9585:[.19444,.68889,0,0],9586:[.19444,.74111,0,0],9632:[0,.675,0,0],9633:[0,.675,0,0],9650:[0,.54986,0,0],9651:[0,.54986,0,0],9654:[.03517,.54986,0,0],9660:[0,.54986,0,0],9661:[0,.54986,0,0],9664:[.03517,.54986,0,0],9674:[.11111,.69224,0,0],9733:[.19444,.69224,0,0],10003:[0,.69224,0,0],10016:[0,.69224,0,0],10731:[.11111,.69224,0,0],10846:[.19444,.75583,0,0],10877:[.13667,.63667,0,0],10878:[.13667,.63667,0,0],10885:[.25583,.75583,0,0],10886:[.25583,.75583,0,0],10887:[.13597,.63597,0,0],10888:[.13597,.63597,0,0],10889:[.26167,.75726,0,0],10890:[.26167,.75726,0,0],10891:[.48256,.98256,0,0],10892:[.48256,.98256,0,0],10901:[.13667,.63667,0,0],10902:[.13667,.63667,0,0],10933:[.25142,.75726,0,0],10934:[.25142,.75726,0,0],10935:[.26167,.75726,0,0],10936:[.26167,.75726,0,0],10937:[.26167,.75726,0,0],10938:[.26167,.75726,0,0],10949:[.25583,.75583,0,0],10950:[.25583,.75583,0,0],10955:[.28481,.79383,0,0],10956:[.28481,.79383,0,0],57350:[.08167,.58167,0,0],57351:[.08167,.58167,0,0],57352:[.08167,.58167,0,0],57353:[0,.43056,.04028,0],57356:[.25142,.75726,0,0],57357:[.25142,.75726,0,0],57358:[.41951,.91951,0,0],57359:[.30274,.79383,0,0],57360:[.30274,.79383,0,0],57361:[.41951,.91951,0,0],57366:[.25142,.75726,0,0],57367:[.25142,.75726,0,0],57368:[.25142,.75726,0,0],57369:[.25142,.75726,0,0],57370:[.13597,.63597,0,0],57371:[.13597,.63597,0,0]},"Caligraphic-Regular":{48:[0,.43056,0,0],49:[0,.43056,0,0],50:[0,.43056,0,0],51:[.19444,.43056,0,0],52:[.19444,.43056,0,0],53:[.19444,.43056,0,0],54:[0,.64444,0,0],55:[.19444,.43056,0,0],56:[0,.64444,0,0],57:[.19444,.43056,0,0],65:[0,.68333,0,.19445],66:[0,.68333,.03041,.13889],67:[0,.68333,.05834,.13889],68:[0,.68333,.02778,.08334],69:[0,.68333,.08944,.11111],70:[0,.68333,.09931,.11111],71:[.09722,.68333,.0593,.11111],72:[0,.68333,.00965,.11111],73:[0,.68333,.07382,0],74:[.09722,.68333,.18472,.16667],75:[0,.68333,.01445,.05556],76:[0,.68333,0,.13889],77:[0,.68333,0,.13889],78:[0,.68333,.14736,.08334],79:[0,.68333,.02778,.11111],80:[0,.68333,.08222,.08334],81:[.09722,.68333,0,.11111],82:[0,.68333,0,.08334],83:[0,.68333,.075,.13889],84:[0,.68333,.25417,0],85:[0,.68333,.09931,.08334],86:[0,.68333,.08222,0],87:[0,.68333,.08222,.08334],88:[0,.68333,.14643,.13889],89:[.09722,.68333,.08222,.08334],90:[0,.68333,.07944,.13889]},"Fraktur-Regular":{33:[0,.69141,0,0],34:[0,.69141,0,0],38:[0,.69141,0,0],39:[0,.69141,0,0],40:[.24982,.74947,0,0],41:[.24982,.74947,0,0],42:[0,.62119,0,0],43:[.08319,.58283,0,0],44:[0,.10803,0,0],45:[.08319,.58283,0,0],46:[0,.10803,0,0],47:[.24982,.74947,0,0],48:[0,.47534,0,0],49:[0,.47534,0,0],50:[0,.47534,0,0],51:[.18906,.47534,0,0],52:[.18906,.47534,0,0],53:[.18906,.47534,0,0],54:[0,.69141,0,0],55:[.18906,.47534,0,0],56:[0,.69141,0,0],57:[.18906,.47534,0,0],58:[0,.47534,0,0],59:[.12604,.47534,0,0],61:[-.13099,.36866,0,0],63:[0,.69141,0,0],65:[0,.69141,0,0],66:[0,.69141,0,0],67:[0,.69141,0,0],68:[0,.69141,0,0],69:[0,.69141,0,0],70:[.12604,.69141,0,0],71:[0,.69141,0,0],72:[.06302,.69141,0,0],73:[0,.69141,0,0],74:[.12604,.69141,0,0],75:[0,.69141,0,0],76:[0,.69141,0,0],77:[0,.69141,0,0],78:[0,.69141,0,0],79:[0,.69141,0,0],80:[.18906,.69141,0,0],81:[.03781,.69141,0,0],82:[0,.69141,0,0],83:[0,.69141,0,0],84:[0,.69141,0,0],85:[0,.69141,0,0],86:[0,.69141,0,0],87:[0,.69141,0,0],88:[0,.69141,0,0],89:[.18906,.69141,0,0],90:[.12604,.69141,0,0],91:[.24982,.74947,0,0],93:[.24982,.74947,0,0],94:[0,.69141,0,0],97:[0,.47534,0,0],98:[0,.69141,0,0],99:[0,.47534,0,0],100:[0,.62119,0,0],101:[0,.47534,0,0],102:[.18906,.69141,0,0],103:[.18906,.47534,0,0],104:[.18906,.69141,0,0],105:[0,.69141,0,0],106:[0,.69141,0,0],107:[0,.69141,0,0],108:[0,.69141,0,0],109:[0,.47534,0,0],110:[0,.47534,0,0],111:[0,.47534,0,0],112:[.18906,.52396,0,0],113:[.18906,.47534,0,0],114:[0,.47534,0,0],115:[0,.47534,0,0],116:[0,.62119,0,0],117:[0,.47534,0,0],118:[0,.52396,0,0],119:[0,.52396,0,0],120:[.18906,.47534,0,0],121:[.18906,.47534,0,0],122:[.18906,.47534,0,0],8216:[0,.69141,0,0],8217:[0,.69141,0,0],58112:[0,.62119,0,0],58113:[0,.62119,0,0],58114:[.18906,.69141,0,0],58115:[.18906,.69141,0,0],58116:[.18906,.47534,0,0],58117:[0,.69141,0,0],58118:[0,.62119,0,0],58119:[0,.47534,0,0]},"Main-Bold":{33:[0,.69444,0,0],34:[0,.69444,0,0],35:[.19444,.69444,0,0],36:[.05556,.75,0,0],37:[.05556,.75,0,0],38:[0,.69444,0,0],39:[0,.69444,0,0],40:[.25,.75,0,0],41:[.25,.75,0,0],42:[0,.75,0,0],43:[.13333,.63333,0,0],44:[.19444,.15556,0,0],45:[0,.44444,0,0],46:[0,.15556,0,0],47:[.25,.75,0,0],48:[0,.64444,0,0],49:[0,.64444,0,0],50:[0,.64444,0,0],51:[0,.64444,0,0],52:[0,.64444,0,0],53:[0,.64444,0,0],54:[0,.64444,0,0],55:[0,.64444,0,0],56:[0,.64444,0,0],57:[0,.64444,0,0],58:[0,.44444,0,0],59:[.19444,.44444,0,0],60:[.08556,.58556,0,0],61:[-.10889,.39111,0,0],62:[.08556,.58556,0,0],63:[0,.69444,0,0],64:[0,.69444,0,0],65:[0,.68611,0,0],66:[0,.68611,0,0],67:[0,.68611,0,0],68:[0,.68611,0,0],69:[0,.68611,0,0],70:[0,.68611,0,0],71:[0,.68611,0,0],72:[0,.68611,0,0],73:[0,.68611,0,0],74:[0,.68611,0,0],75:[0,.68611,0,0],76:[0,.68611,0,0],77:[0,.68611,0,0],78:[0,.68611,0,0],79:[0,.68611,0,0],80:[0,.68611,0,0],81:[.19444,.68611,0,0],82:[0,.68611,0,0],83:[0,.68611,0,0],84:[0,.68611,0,0],85:[0,.68611,0,0],86:[0,.68611,.01597,0],87:[0,.68611,.01597,0],88:[0,.68611,0,0],89:[0,.68611,.02875,0],90:[0,.68611,0,0],91:[.25,.75,0,0],92:[.25,.75,0,0],93:[.25,.75,0,0],94:[0,.69444,0,0],95:[.31,.13444,.03194,0],96:[0,.69444,0,0],97:[0,.44444,0,0],98:[0,.69444,0,0],99:[0,.44444,0,0],100:[0,.69444,0,0],101:[0,.44444,0,0],102:[0,.69444,.10903,0],103:[.19444,.44444,.01597,0],104:[0,.69444,0,0],105:[0,.69444,0,0],106:[.19444,.69444,0,0],107:[0,.69444,0,0],108:[0,.69444,0,0],109:[0,.44444,0,0],110:[0,.44444,0,0],111:[0,.44444,0,0],112:[.19444,.44444,0,0],113:[.19444,.44444,0,0],114:[0,.44444,0,0],115:[0,.44444,0,0],116:[0,.63492,0,0],117:[0,.44444,0,0],118:[0,.44444,.01597,0],119:[0,.44444,.01597,0],120:[0,.44444,0,0],121:[.19444,.44444,.01597,0],122:[0,.44444,0,0],123:[.25,.75,0,0],124:[.25,.75,0,0],125:[.25,.75,0,0],126:[.35,.34444,0,0],168:[0,.69444,0,0],172:[0,.44444,0,0],175:[0,.59611,0,0],176:[0,.69444,0,0],177:[.13333,.63333,0,0],180:[0,.69444,0,0],215:[.13333,.63333,0,0],247:[.13333,.63333,0,0],305:[0,.44444,0,0],567:[.19444,.44444,0,0],710:[0,.69444,0,0],711:[0,.63194,0,0],713:[0,.59611,0,0],714:[0,.69444,0,0],715:[0,.69444,0,0],728:[0,.69444,0,0],729:[0,.69444,0,0],730:[0,.69444,0,0],732:[0,.69444,0,0],768:[0,.69444,0,0],769:[0,.69444,0,0],770:[0,.69444,0,0],771:[0,.69444,0,0],772:[0,.59611,0,0],774:[0,.69444,0,0],775:[0,.69444,0,0],776:[0,.69444,0,0],778:[0,.69444,0,0],779:[0,.69444,0,0],780:[0,.63194,0,0],824:[.19444,.69444,0,0],915:[0,.68611,0,0],916:[0,.68611,0,0],920:[0,.68611,0,0],923:[0,.68611,0,0],926:[0,.68611,0,0],928:[0,.68611,0,0],931:[0,.68611,0,0],933:[0,.68611,0,0],934:[0,.68611,0,0],936:[0,.68611,0,0],937:[0,.68611,0,0],8211:[0,.44444,.03194,0],8212:[0,.44444,.03194,0],8216:[0,.69444,0,0],8217:[0,.69444,0,0],8220:[0,.69444,0,0],8221:[0,.69444,0,0],8224:[.19444,.69444,0,0],8225:[.19444,.69444,0,0],8242:[0,.55556,0,0],8407:[0,.72444,.15486,0],8463:[0,.69444,0,0],8465:[0,.69444,0,0],8467:[0,.69444,0,0],8472:[.19444,.44444,0,0],8476:[0,.69444,0,0],8501:[0,.69444,0,0],8592:[-.10889,.39111,0,0],8593:[.19444,.69444,0,0],8594:[-.10889,.39111,0,0],8595:[.19444,.69444,0,0],8596:[-.10889,.39111,0,0],8597:[.25,.75,0,0],8598:[.19444,.69444,0,0],8599:[.19444,.69444,0,0],8600:[.19444,.69444,0,0],8601:[.19444,.69444,0,0],8636:[-.10889,.39111,0,0],8637:[-.10889,.39111,0,0],8640:[-.10889,.39111,0,0],8641:[-.10889,.39111,0,0],8656:[-.10889,.39111,0,0],8657:[.19444,.69444,0,0],8658:[-.10889,.39111,0,0],8659:[.19444,.69444,0,0],8660:[-.10889,.39111,0,0],8661:[.25,.75,0,0],8704:[0,.69444,0,0],8706:[0,.69444,.06389,0],8707:[0,.69444,0,0],8709:[.05556,.75,0,0],8711:[0,.68611,0,0],8712:[.08556,.58556,0,0],8715:[.08556,.58556,0,0],8722:[.13333,.63333,0,0],8723:[.13333,.63333,0,0],8725:[.25,.75,0,0],8726:[.25,.75,0,0],8727:[-.02778,.47222,0,0],8728:[-.02639,.47361,0,0],8729:[-.02639,.47361,0,0],8730:[.18,.82,0,0],8733:[0,.44444,0,0],8734:[0,.44444,0,0],8736:[0,.69224,0,0],8739:[.25,.75,0,0],8741:[.25,.75,0,0],8743:[0,.55556,0,0],8744:[0,.55556,0,0],8745:[0,.55556,0,0],8746:[0,.55556,0,0],8747:[.19444,.69444,.12778,0],8764:[-.10889,.39111,0,0],8768:[.19444,.69444,0,0],8771:[.00222,.50222,0,0],8776:[.02444,.52444,0,0],8781:[.00222,.50222,0,0],8801:[.00222,.50222,0,0],8804:[.19667,.69667,0,0],8805:[.19667,.69667,0,0],8810:[.08556,.58556,0,0],8811:[.08556,.58556,0,0],8826:[.08556,.58556,0,0],8827:[.08556,.58556,0,0],8834:[.08556,.58556,0,0],8835:[.08556,.58556,0,0],8838:[.19667,.69667,0,0],8839:[.19667,.69667,0,0],8846:[0,.55556,0,0],8849:[.19667,.69667,0,0],8850:[.19667,.69667,0,0],8851:[0,.55556,0,0],8852:[0,.55556,0,0],8853:[.13333,.63333,0,0],8854:[.13333,.63333,0,0],8855:[.13333,.63333,0,0],8856:[.13333,.63333,0,0],8857:[.13333,.63333,0,0],8866:[0,.69444,0,0],8867:[0,.69444,0,0],8868:[0,.69444,0,0],8869:[0,.69444,0,0],8900:[-.02639,.47361,0,0],8901:[-.02639,.47361,0,0],8902:[-.02778,.47222,0,0],8968:[.25,.75,0,0],8969:[.25,.75,0,0],8970:[.25,.75,0,0],8971:[.25,.75,0,0],8994:[-.13889,.36111,0,0],8995:[-.13889,.36111,0,0],9651:[.19444,.69444,0,0],9657:[-.02778,.47222,0,0],9661:[.19444,.69444,0,0],9667:[-.02778,.47222,0,0],9711:[.19444,.69444,0,0],9824:[.12963,.69444,0,0],9825:[.12963,.69444,0,0],9826:[.12963,.69444,0,0],9827:[.12963,.69444,0,0],9837:[0,.75,0,0],9838:[.19444,.69444,0,0],9839:[.19444,.69444,0,0],10216:[.25,.75,0,0],10217:[.25,.75,0,0],10815:[0,.68611,0,0],10927:[.19667,.69667,0,0],10928:[.19667,.69667,0,0]},"Main-Italic":{33:[0,.69444,.12417,0],34:[0,.69444,.06961,0],35:[.19444,.69444,.06616,0],37:[.05556,.75,.13639,0],38:[0,.69444,.09694,0],39:[0,.69444,.12417,0],40:[.25,.75,.16194,0],41:[.25,.75,.03694,0],42:[0,.75,.14917,0],43:[.05667,.56167,.03694,0],44:[.19444,.10556,0,0],45:[0,.43056,.02826,0],46:[0,.10556,0,0],47:[.25,.75,.16194,0],48:[0,.64444,.13556,0],49:[0,.64444,.13556,0],50:[0,.64444,.13556,0],51:[0,.64444,.13556,0],52:[.19444,.64444,.13556,0],53:[0,.64444,.13556,0],54:[0,.64444,.13556,0],55:[.19444,.64444,.13556,0],56:[0,.64444,.13556,0],57:[0,.64444,.13556,0],58:[0,.43056,.0582,0],59:[.19444,.43056,.0582,0],61:[-.13313,.36687,.06616,0],63:[0,.69444,.1225,0],64:[0,.69444,.09597,0],65:[0,.68333,0,0],66:[0,.68333,.10257,0],67:[0,.68333,.14528,0],68:[0,.68333,.09403,0],69:[0,.68333,.12028,0],70:[0,.68333,.13305,0],71:[0,.68333,.08722,0],72:[0,.68333,.16389,0],73:[0,.68333,.15806,0],74:[0,.68333,.14028,0],75:[0,.68333,.14528,0],76:[0,.68333,0,0],77:[0,.68333,.16389,0],78:[0,.68333,.16389,0],79:[0,.68333,.09403,0],80:[0,.68333,.10257,0],81:[.19444,.68333,.09403,0],82:[0,.68333,.03868,0],83:[0,.68333,.11972,0],84:[0,.68333,.13305,0],85:[0,.68333,.16389,0],86:[0,.68333,.18361,0],87:[0,.68333,.18361,0],88:[0,.68333,.15806,0],89:[0,.68333,.19383,0],90:[0,.68333,.14528,0],91:[.25,.75,.1875,0],93:[.25,.75,.10528,0],94:[0,.69444,.06646,0],95:[.31,.12056,.09208,0],97:[0,.43056,.07671,0],98:[0,.69444,.06312,0],99:[0,.43056,.05653,0],100:[0,.69444,.10333,0],101:[0,.43056,.07514,0],102:[.19444,.69444,.21194,0],103:[.19444,.43056,.08847,0],104:[0,.69444,.07671,0],105:[0,.65536,.1019,0],106:[.19444,.65536,.14467,0],107:[0,.69444,.10764,0],108:[0,.69444,.10333,0],109:[0,.43056,.07671,0],110:[0,.43056,.07671,0],111:[0,.43056,.06312,0],112:[.19444,.43056,.06312,0],113:[.19444,.43056,.08847,0],114:[0,.43056,.10764,0],115:[0,.43056,.08208,0],116:[0,.61508,.09486,0],117:[0,.43056,.07671,0],118:[0,.43056,.10764,0],119:[0,.43056,.10764,0],120:[0,.43056,.12042,0],121:[.19444,.43056,.08847,0],122:[0,.43056,.12292,0],126:[.35,.31786,.11585,0],163:[0,.69444,0,0],305:[0,.43056,0,.02778],567:[.19444,.43056,0,.08334],768:[0,.69444,0,0],769:[0,.69444,.09694,0],770:[0,.69444,.06646,0],771:[0,.66786,.11585,0],772:[0,.56167,.10333,0],774:[0,.69444,.10806,0],775:[0,.66786,.11752,0],776:[0,.66786,.10474,0],778:[0,.69444,0,0],779:[0,.69444,.1225,0],780:[0,.62847,.08295,0],915:[0,.68333,.13305,0],916:[0,.68333,0,0],920:[0,.68333,.09403,0],923:[0,.68333,0,0],926:[0,.68333,.15294,0],928:[0,.68333,.16389,0],931:[0,.68333,.12028,0],933:[0,.68333,.11111,0],934:[0,.68333,.05986,0],936:[0,.68333,.11111,0],937:[0,.68333,.10257,0],8211:[0,.43056,.09208,0],8212:[0,.43056,.09208,0],8216:[0,.69444,.12417,0],8217:[0,.69444,.12417,0],8220:[0,.69444,.1685,0],8221:[0,.69444,.06961,0],8463:[0,.68889,0,0]},"Main-Regular":{32:[0,0,0,0],33:[0,.69444,0,0],34:[0,.69444,0,0],35:[.19444,.69444,0,0],36:[.05556,.75,0,0],37:[.05556,.75,0,0],38:[0,.69444,0,0],39:[0,.69444,0,0],40:[.25,.75,0,0],41:[.25,.75,0,0],42:[0,.75,0,0],43:[.08333,.58333,0,0],44:[.19444,.10556,0,0],45:[0,.43056,0,0],46:[0,.10556,0,0],47:[.25,.75,0,0],48:[0,.64444,0,0],49:[0,.64444,0,0],50:[0,.64444,0,0],51:[0,.64444,0,0],52:[0,.64444,0,0],53:[0,.64444,0,0],54:[0,.64444,0,0],55:[0,.64444,0,0],56:[0,.64444,0,0],57:[0,.64444,0,0],58:[0,.43056,0,0],59:[.19444,.43056,0,0],60:[.0391,.5391,0,0],61:[-.13313,.36687,0,0],62:[.0391,.5391,0,0],63:[0,.69444,0,0],64:[0,.69444,0,0],65:[0,.68333,0,0],66:[0,.68333,0,0],67:[0,.68333,0,0],68:[0,.68333,0,0],69:[0,.68333,0,0],70:[0,.68333,0,0],71:[0,.68333,0,0],72:[0,.68333,0,0],73:[0,.68333,0,0],74:[0,.68333,0,0],75:[0,.68333,0,0],76:[0,.68333,0,0],77:[0,.68333,0,0],78:[0,.68333,0,0],79:[0,.68333,0,0],80:[0,.68333,0,0],81:[.19444,.68333,0,0],82:[0,.68333,0,0],83:[0,.68333,0,0],84:[0,.68333,0,0],85:[0,.68333,0,0],86:[0,.68333,.01389,0],87:[0,.68333,.01389,0],88:[0,.68333,0,0],89:[0,.68333,.025,0],90:[0,.68333,0,0],91:[.25,.75,0,0],92:[.25,.75,0,0],93:[.25,.75,0,0],94:[0,.69444,0,0],95:[.31,.12056,.02778,0],96:[0,.69444,0,0],97:[0,.43056,0,0],98:[0,.69444,0,0],99:[0,.43056,0,0],100:[0,.69444,0,0],101:[0,.43056,0,0],102:[0,.69444,.07778,0],103:[.19444,.43056,.01389,0],104:[0,.69444,0,0],105:[0,.66786,0,0],106:[.19444,.66786,0,0],107:[0,.69444,0,0],108:[0,.69444,0,0],109:[0,.43056,0,0],110:[0,.43056,0,0],111:[0,.43056,0,0],112:[.19444,.43056,0,0],113:[.19444,.43056,0,0],114:[0,.43056,0,0],115:[0,.43056,0,0],116:[0,.61508,0,0],117:[0,.43056,0,0],118:[0,.43056,.01389,0],119:[0,.43056,.01389,0],120:[0,.43056,0,0],121:[.19444,.43056,.01389,0],122:[0,.43056,0,0],123:[.25,.75,0,0],124:[.25,.75,0,0],125:[.25,.75,0,0],126:[.35,.31786,0,0],160:[0,0,0,0],168:[0,.66786,0,0],172:[0,.43056,0,0],175:[0,.56778,0,0],176:[0,.69444,0,0],177:[.08333,.58333,0,0],180:[0,.69444,0,0],215:[.08333,.58333,0,0],247:[.08333,.58333,0,0],305:[0,.43056,0,0],567:[.19444,.43056,0,0],710:[0,.69444,0,0],711:[0,.62847,0,0],713:[0,.56778,0,0],714:[0,.69444,0,0],715:[0,.69444,0,0],728:[0,.69444,0,0],729:[0,.66786,0,0],730:[0,.69444,0,0],732:[0,.66786,0,0],768:[0,.69444,0,0],769:[0,.69444,0,0],770:[0,.69444,0,0],771:[0,.66786,0,0],772:[0,.56778,0,0],774:[0,.69444,0,0],775:[0,.66786,0,0],776:[0,.66786,0,0],778:[0,.69444,0,0],779:[0,.69444,0,0],780:[0,.62847,0,0],824:[.19444,.69444,0,0],915:[0,.68333,0,0],916:[0,.68333,0,0],920:[0,.68333,0,0],923:[0,.68333,0,0],926:[0,.68333,0,0],928:[0,.68333,0,0],931:[0,.68333,0,0],933:[0,.68333,0,0],934:[0,.68333,0,0],936:[0,.68333,0,0],937:[0,.68333,0,0],8211:[0,.43056,.02778,0],8212:[0,.43056,.02778,0],8216:[0,.69444,0,0],8217:[0,.69444,0,0],8220:[0,.69444,0,0],8221:[0,.69444,0,0],8224:[.19444,.69444,0,0],8225:[.19444,.69444,0,0],8230:[0,.12,0,0],8242:[0,.55556,0,0],8407:[0,.71444,.15382,0],8463:[0,.68889,0,0],8465:[0,.69444,0,0],8467:[0,.69444,0,.11111],8472:[.19444,.43056,0,.11111],8476:[0,.69444,0,0],8501:[0,.69444,0,0],8592:[-.13313,.36687,0,0],8593:[.19444,.69444,0,0],8594:[-.13313,.36687,0,0],8595:[.19444,.69444,0,0],8596:[-.13313,.36687,0,0],8597:[.25,.75,0,0],8598:[.19444,.69444,0,0],8599:[.19444,.69444,0,0],8600:[.19444,.69444,0,0],8601:[.19444,.69444,0,0],8614:[.011,.511,0,0],8617:[.011,.511,0,0],8618:[.011,.511,0,0],8636:[-.13313,.36687,0,0],8637:[-.13313,.36687,0,0],8640:[-.13313,.36687,0,0],8641:[-.13313,.36687,0,0],8652:[.011,.671,0,0],8656:[-.13313,.36687,0,0],8657:[.19444,.69444,0,0],8658:[-.13313,.36687,0,0],8659:[.19444,.69444,0,0],8660:[-.13313,.36687,0,0],8661:[.25,.75,0,0],8704:[0,.69444,0,0],8706:[0,.69444,.05556,.08334],8707:[0,.69444,0,0],8709:[.05556,.75,0,0],8711:[0,.68333,0,0],8712:[.0391,.5391,0,0],8715:[.0391,.5391,0,0],8722:[.08333,.58333,0,0],8723:[.08333,.58333,0,0],8725:[.25,.75,0,0],8726:[.25,.75,0,0],8727:[-.03472,.46528,0,0],8728:[-.05555,.44445,0,0],8729:[-.05555,.44445,0,0],8730:[.2,.8,0,0],8733:[0,.43056,0,0],8734:[0,.43056,0,0],8736:[0,.69224,0,0],8739:[.25,.75,0,0],8741:[.25,.75,0,0],8743:[0,.55556,0,0],8744:[0,.55556,0,0],8745:[0,.55556,0,0],8746:[0,.55556,0,0],8747:[.19444,.69444,.11111,0],8764:[-.13313,.36687,0,0],8768:[.19444,.69444,0,0],8771:[-.03625,.46375,0,0],8773:[-.022,.589,0,0],8776:[-.01688,.48312,0,0],8781:[-.03625,.46375,0,0],8784:[-.133,.67,0,0],8800:[.215,.716,0,0],8801:[-.03625,.46375,0,0],8804:[.13597,.63597,0,0],8805:[.13597,.63597,0,0],8810:[.0391,.5391,0,0],8811:[.0391,.5391,0,0],8826:[.0391,.5391,0,0],8827:[.0391,.5391,0,0],8834:[.0391,.5391,0,0],8835:[.0391,.5391,0,0],8838:[.13597,.63597,0,0],8839:[.13597,.63597,0,0],8846:[0,.55556,0,0],8849:[.13597,.63597,0,0],8850:[.13597,.63597,0,0],8851:[0,.55556,0,0],8852:[0,.55556,0,0],8853:[.08333,.58333,0,0],8854:[.08333,.58333,0,0],8855:[.08333,.58333,0,0],8856:[.08333,.58333,0,0],8857:[.08333,.58333,0,0],8866:[0,.69444,0,0],8867:[0,.69444,0,0],8868:[0,.69444,0,0],8869:[0,.69444,0,0],8872:[.249,.75,0,0],8900:[-.05555,.44445,0,0],8901:[-.05555,.44445,0,0],8902:[-.03472,.46528,0,0],8904:[.005,.505,0,0],8942:[.03,.9,0,0],8943:[-.19,.31,0,0],8945:[-.1,.82,0,0],8968:[.25,.75,0,0],8969:[.25,.75,0,0],8970:[.25,.75,0,0],8971:[.25,.75,0,0],8994:[-.14236,.35764,0,0],8995:[-.14236,.35764,0,0],9136:[.244,.744,0,0],9137:[.244,.744,0,0],9651:[.19444,.69444,0,0],9657:[-.03472,.46528,0,0],9661:[.19444,.69444,0,0],9667:[-.03472,.46528,0,0],9711:[.19444,.69444,0,0],9824:[.12963,.69444,0,0],9825:[.12963,.69444,0,0],9826:[.12963,.69444,0,0],9827:[.12963,.69444,0,0],9837:[0,.75,0,0],9838:[.19444,.69444,0,0],9839:[.19444,.69444,0,0],10216:[.25,.75,0,0],10217:[.25,.75,0,0],10222:[.244,.744,0,0],10223:[.244,.744,0,0],10229:[.011,.511,0,0],10230:[.011,.511,0,0],10231:[.011,.511,0,0],10232:[.024,.525,0,0],10233:[.024,.525,0,0],10234:[.024,.525,0,0],10236:[.011,.511,0,0],10815:[0,.68333,0,0],10927:[.13597,.63597,0,0],10928:[.13597,.63597,0,0]},"Math-BoldItalic":{47:[.19444,.69444,0,0],65:[0,.68611,0,0],66:[0,.68611,.04835,0],67:[0,.68611,.06979,0],68:[0,.68611,.03194,0],69:[0,.68611,.05451,0],70:[0,.68611,.15972,0],71:[0,.68611,0,0],72:[0,.68611,.08229,0],73:[0,.68611,.07778,0],74:[0,.68611,.10069,0],75:[0,.68611,.06979,0],76:[0,.68611,0,0],77:[0,.68611,.11424,0],78:[0,.68611,.11424,0],79:[0,.68611,.03194,0],80:[0,.68611,.15972,0],81:[.19444,.68611,0,0],82:[0,.68611,.00421,0],83:[0,.68611,.05382,0],84:[0,.68611,.15972,0],85:[0,.68611,.11424,0],86:[0,.68611,.25555,0],87:[0,.68611,.15972,0],88:[0,.68611,.07778,0],89:[0,.68611,.25555,0],90:[0,.68611,.06979,0],97:[0,.44444,0,0],98:[0,.69444,0,0],99:[0,.44444,0,0],100:[0,.69444,0,0],101:[0,.44444,0,0],102:[.19444,.69444,.11042,0],103:[.19444,.44444,.03704,0],104:[0,.69444,0,0],105:[0,.69326,0,0],106:[.19444,.69326,.0622,0],107:[0,.69444,.01852,0],108:[0,.69444,.0088,0],109:[0,.44444,0,0],110:[0,.44444,0,0],111:[0,.44444,0,0],112:[.19444,.44444,0,0],113:[.19444,.44444,.03704,0],114:[0,.44444,.03194,0],115:[0,.44444,0,0],116:[0,.63492,0,0],117:[0,.44444,0,0],118:[0,.44444,.03704,0],119:[0,.44444,.02778,0],120:[0,.44444,0,0],121:[.19444,.44444,.03704,0],122:[0,.44444,.04213,0],915:[0,.68611,.15972,0],916:[0,.68611,0,0],920:[0,.68611,.03194,0],923:[0,.68611,0,0],926:[0,.68611,.07458,0],928:[0,.68611,.08229,0],931:[0,.68611,.05451,0],933:[0,.68611,.15972,0],934:[0,.68611,0,0],936:[0,.68611,.11653,0],937:[0,.68611,.04835,0],945:[0,.44444,0,0],946:[.19444,.69444,.03403,0],947:[.19444,.44444,.06389,0],948:[0,.69444,.03819,0],949:[0,.44444,0,0],950:[.19444,.69444,.06215,0],951:[.19444,.44444,.03704,0],952:[0,.69444,.03194,0],953:[0,.44444,0,0],954:[0,.44444,0,0],955:[0,.69444,0,0],956:[.19444,.44444,0,0],957:[0,.44444,.06898,0],958:[.19444,.69444,.03021,0],959:[0,.44444,0,0],960:[0,.44444,.03704,0],961:[.19444,.44444,0,0],962:[.09722,.44444,.07917,0],963:[0,.44444,.03704,0],964:[0,.44444,.13472,0],965:[0,.44444,.03704,0],966:[.19444,.44444,0,0],967:[.19444,.44444,0,0],968:[.19444,.69444,.03704,0],969:[0,.44444,.03704,0],977:[0,.69444,0,0],981:[.19444,.69444,0,0],982:[0,.44444,.03194,0],1009:[.19444,.44444,0,0],1013:[0,.44444,0,0]},"Math-Italic":{47:[.19444,.69444,0,0],65:[0,.68333,0,.13889],66:[0,.68333,.05017,.08334],67:[0,.68333,.07153,.08334],68:[0,.68333,.02778,.05556],69:[0,.68333,.05764,.08334],70:[0,.68333,.13889,.08334],71:[0,.68333,0,.08334],72:[0,.68333,.08125,.05556],73:[0,.68333,.07847,.11111],74:[0,.68333,.09618,.16667],75:[0,.68333,.07153,.05556],76:[0,.68333,0,.02778],77:[0,.68333,.10903,.08334],78:[0,.68333,.10903,.08334],79:[0,.68333,.02778,.08334],80:[0,.68333,.13889,.08334],81:[.19444,.68333,0,.08334],82:[0,.68333,.00773,.08334],83:[0,.68333,.05764,.08334],84:[0,.68333,.13889,.08334],85:[0,.68333,.10903,.02778],86:[0,.68333,.22222,0],87:[0,.68333,.13889,0],88:[0,.68333,.07847,.08334],89:[0,.68333,.22222,0],90:[0,.68333,.07153,.08334],97:[0,.43056,0,0],98:[0,.69444,0,0],99:[0,.43056,0,.05556],100:[0,.69444,0,.16667],101:[0,.43056,0,.05556],102:[.19444,.69444,.10764,.16667],103:[.19444,.43056,.03588,.02778],104:[0,.69444,0,0],105:[0,.65952,0,0],106:[.19444,.65952,.05724,0],107:[0,.69444,.03148,0],108:[0,.69444,.01968,.08334],109:[0,.43056,0,0],110:[0,.43056,0,0],111:[0,.43056,0,.05556],112:[.19444,.43056,0,.08334],113:[.19444,.43056,.03588,.08334],114:[0,.43056,.02778,.05556],115:[0,.43056,0,.05556],116:[0,.61508,0,.08334],117:[0,.43056,0,.02778],118:[0,.43056,.03588,.02778],119:[0,.43056,.02691,.08334],120:[0,.43056,0,.02778],121:[.19444,.43056,.03588,.05556],122:[0,.43056,.04398,.05556],915:[0,.68333,.13889,.08334],916:[0,.68333,0,.16667],920:[0,.68333,.02778,.08334],923:[0,.68333,0,.16667],926:[0,.68333,.07569,.08334],928:[0,.68333,.08125,.05556],931:[0,.68333,.05764,.08334],933:[0,.68333,.13889,.05556],934:[0,.68333,0,.08334],936:[0,.68333,.11,.05556],937:[0,.68333,.05017,.08334],945:[0,.43056,.0037,.02778],946:[.19444,.69444,.05278,.08334],947:[.19444,.43056,.05556,0],948:[0,.69444,.03785,.05556],949:[0,.43056,0,.08334],950:[.19444,.69444,.07378,.08334],951:[.19444,.43056,.03588,.05556],952:[0,.69444,.02778,.08334],953:[0,.43056,0,.05556],954:[0,.43056,0,0],955:[0,.69444,0,0],956:[.19444,.43056,0,.02778],957:[0,.43056,.06366,.02778],958:[.19444,.69444,.04601,.11111],959:[0,.43056,0,.05556],960:[0,.43056,.03588,0],961:[.19444,.43056,0,.08334],962:[.09722,.43056,.07986,.08334],963:[0,.43056,.03588,0],964:[0,.43056,.1132,.02778],965:[0,.43056,.03588,.02778],966:[.19444,.43056,0,.08334],967:[.19444,.43056,0,.05556],968:[.19444,.69444,.03588,.11111],969:[0,.43056,.03588,0],977:[0,.69444,0,.08334],981:[.19444,.69444,0,.08334],982:[0,.43056,.02778,0],1009:[.19444,.43056,0,.08334],1013:[0,.43056,0,.05556]},"Math-Regular":{65:[0,.68333,0,.13889],66:[0,.68333,.05017,.08334],67:[0,.68333,.07153,.08334],68:[0,.68333,.02778,.05556],69:[0,.68333,.05764,.08334],70:[0,.68333,.13889,.08334],71:[0,.68333,0,.08334],72:[0,.68333,.08125,.05556],73:[0,.68333,.07847,.11111],74:[0,.68333,.09618,.16667],75:[0,.68333,.07153,.05556],76:[0,.68333,0,.02778],77:[0,.68333,.10903,.08334],78:[0,.68333,.10903,.08334],79:[0,.68333,.02778,.08334],80:[0,.68333,.13889,.08334],81:[.19444,.68333,0,.08334],82:[0,.68333,.00773,.08334],83:[0,.68333,.05764,.08334],84:[0,.68333,.13889,.08334],85:[0,.68333,.10903,.02778],86:[0,.68333,.22222,0],87:[0,.68333,.13889,0],88:[0,.68333,.07847,.08334],89:[0,.68333,.22222,0],90:[0,.68333,.07153,.08334],97:[0,.43056,0,0],98:[0,.69444,0,0],99:[0,.43056,0,.05556],100:[0,.69444,0,.16667],101:[0,.43056,0,.05556],102:[.19444,.69444,.10764,.16667],103:[.19444,.43056,.03588,.02778],104:[0,.69444,0,0],105:[0,.65952,0,0],106:[.19444,.65952,.05724,0],107:[0,.69444,.03148,0],108:[0,.69444,.01968,.08334],109:[0,.43056,0,0],110:[0,.43056,0,0],111:[0,.43056,0,.05556],112:[.19444,.43056,0,.08334],113:[.19444,.43056,.03588,.08334],114:[0,.43056,.02778,.05556],115:[0,.43056,0,.05556],116:[0,.61508,0,.08334],117:[0,.43056,0,.02778],118:[0,.43056,.03588,.02778],119:[0,.43056,.02691,.08334],120:[0,.43056,0,.02778],121:[.19444,.43056,.03588,.05556],122:[0,.43056,.04398,.05556],915:[0,.68333,.13889,.08334],916:[0,.68333,0,.16667],920:[0,.68333,.02778,.08334],923:[0,.68333,0,.16667],926:[0,.68333,.07569,.08334],928:[0,.68333,.08125,.05556],931:[0,.68333,.05764,.08334],933:[0,.68333,.13889,.05556],934:[0,.68333,0,.08334],936:[0,.68333,.11,.05556],937:[0,.68333,.05017,.08334],945:[0,.43056,.0037,.02778],946:[.19444,.69444,.05278,.08334],947:[.19444,.43056,.05556,0],948:[0,.69444,.03785,.05556],949:[0,.43056,0,.08334],950:[.19444,.69444,.07378,.08334],951:[.19444,.43056,.03588,.05556],952:[0,.69444,.02778,.08334],953:[0,.43056,0,.05556],954:[0,.43056,0,0],955:[0,.69444,0,0],956:[.19444,.43056,0,.02778],957:[0,.43056,.06366,.02778],958:[.19444,.69444,.04601,.11111],959:[0,.43056,0,.05556],960:[0,.43056,.03588,0],961:[.19444,.43056,0,.08334],962:[.09722,.43056,.07986,.08334],963:[0,.43056,.03588,0],964:[0,.43056,.1132,.02778],965:[0,.43056,.03588,.02778],966:[.19444,.43056,0,.08334],967:[.19444,.43056,0,.05556],968:[.19444,.69444,.03588,.11111],969:[0,.43056,.03588,0],977:[0,.69444,0,.08334],981:[.19444,.69444,0,.08334],982:[0,.43056,.02778,0],1009:[.19444,.43056,0,.08334],1013:[0,.43056,0,.05556]},"SansSerif-Regular":{33:[0,.69444,0,0],34:[0,.69444,0,0],35:[.19444,.69444,0,0],36:[.05556,.75,0,0],37:[.05556,.75,0,0],38:[0,.69444,0,0],39:[0,.69444,0,0],40:[.25,.75,0,0],41:[.25,.75,0,0],42:[0,.75,0,0],43:[.08333,.58333,0,0],44:[.125,.08333,0,0],45:[0,.44444,0,0],46:[0,.08333,0,0],47:[.25,.75,0,0],48:[0,.65556,0,0],49:[0,.65556,0,0],50:[0,.65556,0,0],51:[0,.65556,0,0],52:[0,.65556,0,0],53:[0,.65556,0,0],54:[0,.65556,0,0],55:[0,.65556,0,0],56:[0,.65556,0,0],57:[0,.65556,0,0],58:[0,.44444,0,0],59:[.125,.44444,0,0],61:[-.13,.37,0,0],63:[0,.69444,0,0],64:[0,.69444,0,0],65:[0,.69444,0,0],66:[0,.69444,0,0],67:[0,.69444,0,0],68:[0,.69444,0,0],69:[0,.69444,0,0],70:[0,.69444,0,0],71:[0,.69444,0,0],72:[0,.69444,0,0],73:[0,.69444,0,0],74:[0,.69444,0,0],75:[0,.69444,0,0],76:[0,.69444,0,0],77:[0,.69444,0,0],78:[0,.69444,0,0],79:[0,.69444,0,0],80:[0,.69444,0,0],81:[.125,.69444,0,0],82:[0,.69444,0,0],83:[0,.69444,0,0],84:[0,.69444,0,0],85:[0,.69444,0,0],86:[0,.69444,.01389,0],87:[0,.69444,.01389,0],88:[0,.69444,0,0],89:[0,.69444,.025,0],90:[0,.69444,0,0],91:[.25,.75,0,0],93:[.25,.75,0,0],94:[0,.69444,0,0],95:[.35,.09444,.02778,0],97:[0,.44444,0,0],98:[0,.69444,0,0],99:[0,.44444,0,0],100:[0,.69444,0,0],101:[0,.44444,0,0],102:[0,.69444,.06944,0],103:[.19444,.44444,.01389,0],104:[0,.69444,0,0],105:[0,.67937,0,0],106:[.19444,.67937,0,0],107:[0,.69444,0,0],108:[0,.69444,0,0],109:[0,.44444,0,0],110:[0,.44444,0,0],111:[0,.44444,0,0],112:[.19444,.44444,0,0],113:[.19444,.44444,0,0],114:[0,.44444,.01389,0],115:[0,.44444,0,0],116:[0,.57143,0,0],117:[0,.44444,0,0],118:[0,.44444,.01389,0],119:[0,.44444,.01389,0],120:[0,.44444,0,0],121:[.19444,.44444,.01389,0],122:[0,.44444,0,0],126:[.35,.32659,0,0],305:[0,.44444,0,0],567:[.19444,.44444,0,0],768:[0,.69444,0,0],769:[0,.69444,0,0],770:[0,.69444,0,0],771:[0,.67659,0,0],772:[0,.60889,0,0],774:[0,.69444,0,0],775:[0,.67937,0,0],776:[0,.67937,0,0],778:[0,.69444,0,0],779:[0,.69444,0,0],780:[0,.63194,0,0],915:[0,.69444,0,0],916:[0,.69444,0,0],920:[0,.69444,0,0],923:[0,.69444,0,0],926:[0,.69444,0,0],928:[0,.69444,0,0],931:[0,.69444,0,0],933:[0,.69444,0,0],934:[0,.69444,0,0],936:[0,.69444,0,0],937:[0,.69444,0,0],8211:[0,.44444,.02778,0],8212:[0,.44444,.02778,0],8216:[0,.69444,0,0],8217:[0,.69444,0,0],8220:[0,.69444,0,0],8221:[0,.69444,0,0]},"Script-Regular":{65:[0,.7,.22925,0],66:[0,.7,.04087,0],67:[0,.7,.1689,0],68:[0,.7,.09371,0],69:[0,.7,.18583,0],70:[0,.7,.13634,0],71:[0,.7,.17322,0],72:[0,.7,.29694,0],73:[0,.7,.19189,0],74:[.27778,.7,.19189,0],75:[0,.7,.31259,0],76:[0,.7,.19189,0],77:[0,.7,.15981,0],78:[0,.7,.3525,0],79:[0,.7,.08078,0],80:[0,.7,.08078,0],81:[0,.7,.03305,0],82:[0,.7,.06259,0],83:[0,.7,.19189,0],84:[0,.7,.29087,0],85:[0,.7,.25815,0],86:[0,.7,.27523,0],87:[0,.7,.27523,0],88:[0,.7,.26006,0],89:[0,.7,.2939,0],90:[0,.7,.24037,0]},"Size1-Regular":{40:[.35001,.85,0,0],41:[.35001,.85,0,0],47:[.35001,.85,0,0],91:[.35001,.85,0,0],92:[.35001,.85,0,0],93:[.35001,.85,0,0],123:[.35001,.85,0,0],125:[.35001,.85,0,0],710:[0,.72222,0,0],732:[0,.72222,0,0],770:[0,.72222,0,0],771:[0,.72222,0,0],8214:[-99e-5,.601,0,0],8593:[1e-5,.6,0,0],8595:[1e-5,.6,0,0],8657:[1e-5,.6,0,0],8659:[1e-5,.6,0,0],8719:[.25001,.75,0,0],8720:[.25001,.75,0,0],8721:[.25001,.75,0,0],8730:[.35001,.85,0,0],8739:[-.00599,.606,0,0],8741:[-.00599,.606,0,0],8747:[.30612,.805,.19445,0],8748:[.306,.805,.19445,0],8749:[.306,.805,.19445,0],8750:[.30612,.805,.19445,0],8896:[.25001,.75,0,0],8897:[.25001,.75,0,0],8898:[.25001,.75,0,0],8899:[.25001,.75,0,0],8968:[.35001,.85,0,0],8969:[.35001,.85,0,0],8970:[.35001,.85,0,0],8971:[.35001,.85,0,0],9168:[-99e-5,.601,0,0],10216:[.35001,.85,0,0],10217:[.35001,.85,0,0],10752:[.25001,.75,0,0],10753:[.25001,.75,0,0],10754:[.25001,.75,0,0],10756:[.25001,.75,0,0],10758:[.25001,.75,0,0]},"Size2-Regular":{40:[.65002,1.15,0,0],41:[.65002,1.15,0,0],47:[.65002,1.15,0,0],91:[.65002,1.15,0,0],92:[.65002,1.15,0,0],93:[.65002,1.15,0,0],123:[.65002,1.15,0,0],125:[.65002,1.15,0,0],710:[0,.75,0,0],732:[0,.75,0,0],770:[0,.75,0,0],771:[0,.75,0,0],8719:[.55001,1.05,0,0],8720:[.55001,1.05,0,0],8721:[.55001,1.05,0,0],8730:[.65002,1.15,0,0],8747:[.86225,1.36,.44445,0],8748:[.862,1.36,.44445,0],8749:[.862,1.36,.44445,0],8750:[.86225,1.36,.44445,0],8896:[.55001,1.05,0,0],8897:[.55001,1.05,0,0],8898:[.55001,1.05,0,0],8899:[.55001,1.05,0,0],8968:[.65002,1.15,0,0],8969:[.65002,1.15,0,0],8970:[.65002,1.15,0,0],8971:[.65002,1.15,0,0],10216:[.65002,1.15,0,0],10217:[.65002,1.15,0,0],10752:[.55001,1.05,0,0],10753:[.55001,1.05,0,0],10754:[.55001,1.05,0,0],10756:[.55001,1.05,0,0],10758:[.55001,1.05,0,0]},"Size3-Regular":{40:[.95003,1.45,0,0],41:[.95003,1.45,0,0],47:[.95003,1.45,0,0],91:[.95003,1.45,0,0],92:[.95003,1.45,0,0],93:[.95003,1.45,0,0],123:[.95003,1.45,0,0],125:[.95003,1.45,0,0],710:[0,.75,0,0],732:[0,.75,0,0],770:[0,.75,0,0],771:[0,.75,0,0],8730:[.95003,1.45,0,0],8968:[.95003,1.45,0,0],8969:[.95003,1.45,0,0],8970:[.95003,1.45,0,0],8971:[.95003,1.45,0,0],10216:[.95003,1.45,0,0],10217:[.95003,1.45,0,0]},"Size4-Regular":{40:[1.25003,1.75,0,0],41:[1.25003,1.75,0,0],47:[1.25003,1.75,0,0],91:[1.25003,1.75,0,0],92:[1.25003,1.75,0,0],93:[1.25003,1.75,0,0],123:[1.25003,1.75,0,0],125:[1.25003,1.75,0,0],710:[0,.825,0,0],732:[0,.825,0,0],770:[0,.825,0,0],771:[0,.825,0,0],8730:[1.25003,1.75,0,0],8968:[1.25003,1.75,0,0],8969:[1.25003,1.75,0,0],8970:[1.25003,1.75,0,0],8971:[1.25003,1.75,0,0],9115:[.64502,1.155,0,0],9116:[1e-5,.6,0,0],9117:[.64502,1.155,0,0],9118:[.64502,1.155,0,0],9119:[1e-5,.6,0,0],9120:[.64502,1.155,0,0],9121:[.64502,1.155,0,0],9122:[-99e-5,.601,0,0],9123:[.64502,1.155,0,0],9124:[.64502,1.155,0,0],9125:[-99e-5,.601,0,0],9126:[.64502,1.155,0,0],9127:[1e-5,.9,0,0],9128:[.65002,1.15,0,0],9129:[.90001,0,0,0],9130:[0,.3,0,0],9131:[1e-5,.9,0,0],9132:[.65002,1.15,0,0],9133:[.90001,0,0,0],9143:[.88502,.915,0,0],10216:[1.25003,1.75,0,0],10217:[1.25003,1.75,0,0],57344:[-.00499,.605,0,0],57345:[-.00499,.605,0,0],57680:[0,.12,0,0],57681:[0,.12,0,0],57682:[0,.12,0,0],57683:[0,.12,0,0]},"Typewriter-Regular":{33:[0,.61111,0,0],34:[0,.61111,0,0],35:[0,.61111,0,0],36:[.08333,.69444,0,0],37:[.08333,.69444,0,0],38:[0,.61111,0,0],39:[0,.61111,0,0],40:[.08333,.69444,0,0],41:[.08333,.69444,0,0],42:[0,.52083,0,0],43:[-.08056,.53055,0,0],44:[.13889,.125,0,0],45:[-.08056,.53055,0,0],46:[0,.125,0,0],47:[.08333,.69444,0,0],48:[0,.61111,0,0],49:[0,.61111,0,0],50:[0,.61111,0,0],51:[0,.61111,0,0],52:[0,.61111,0,0],53:[0,.61111,0,0],54:[0,.61111,0,0],55:[0,.61111,0,0],56:[0,.61111,0,0],57:[0,.61111,0,0],58:[0,.43056,0,0],59:[.13889,.43056,0,0],60:[-.05556,.55556,0,0],61:[-.19549,.41562,0,0],62:[-.05556,.55556,0,0],63:[0,.61111,0,0],64:[0,.61111,0,0],65:[0,.61111,0,0],66:[0,.61111,0,0],67:[0,.61111,0,0],68:[0,.61111,0,0],69:[0,.61111,0,0],70:[0,.61111,0,0],71:[0,.61111,0,0],72:[0,.61111,0,0],73:[0,.61111,0,0],74:[0,.61111,0,0],75:[0,.61111,0,0],76:[0,.61111,0,0],77:[0,.61111,0,0],78:[0,.61111,0,0],79:[0,.61111,0,0],80:[0,.61111,0,0],81:[.13889,.61111,0,0],82:[0,.61111,0,0],83:[0,.61111,0,0],84:[0,.61111,0,0],85:[0,.61111,0,0],86:[0,.61111,0,0],87:[0,.61111,0,0],88:[0,.61111,0,0],89:[0,.61111,0,0],90:[0,.61111,0,0],91:[.08333,.69444,0,0],92:[.08333,.69444,0,0],93:[.08333,.69444,0,0],94:[0,.61111,0,0],95:[.09514,0,0,0],96:[0,.61111,0,0],97:[0,.43056,0,0],98:[0,.61111,0,0],99:[0,.43056,0,0],100:[0,.61111,0,0],101:[0,.43056,0,0],102:[0,.61111,0,0],103:[.22222,.43056,0,0],104:[0,.61111,0,0],105:[0,.61111,0,0],106:[.22222,.61111,0,0],107:[0,.61111,0,0],108:[0,.61111,0,0],109:[0,.43056,0,0],110:[0,.43056,0,0],111:[0,.43056,0,0],112:[.22222,.43056,0,0],113:[.22222,.43056,0,0],114:[0,.43056,0,0],115:[0,.43056,0,0],116:[0,.55358,0,0],117:[0,.43056,0,0],118:[0,.43056,0,0],119:[0,.43056,0,0],120:[0,.43056,0,0],121:[.22222,.43056,0,0],122:[0,.43056,0,0],123:[.08333,.69444,0,0],124:[.08333,.69444,0,0],125:[.08333,.69444,0,0],126:[0,.61111,0,0],127:[0,.61111,0,0],305:[0,.43056,0,0],567:[.22222,.43056,0,0],768:[0,.61111,0,0],769:[0,.61111,0,0],770:[0,.61111,0,0],771:[0,.61111,0,0],772:[0,.56555,0,0],774:[0,.61111,0,0],776:[0,.61111,0,0],778:[0,.61111,0,0],780:[0,.56597,0,0],915:[0,.61111,0,0],916:[0,.61111,0,0],920:[0,.61111,0,0],923:[0,.61111,0,0],926:[0,.61111,0,0],928:[0,.61111,0,0],931:[0,.61111,0,0],933:[0,.61111,0,0],934:[0,.61111,0,0],936:[0,.61111,0,0],937:[0,.61111,0,0],2018:[0,.61111,0,0],2019:[0,.61111,0,0],8242:[0,.61111,0,0]}};
/***/},
/***/15193:
/***/function(module,__unused_webpack_exports,__webpack_require__){var utils=__webpack_require__(25182),ParseError=__webpack_require__(16856);
/* This file contains a list of functions that we parse, identified by
 * the calls to defineFunction.
 *
 * The first argument to defineFunction is a single name or a list of names.
 * All functions named in such a list will share a single implementation.
 *
 * Each declared function can have associated properties, which
 * include the following:
 *
 *  - numArgs: The number of arguments the function takes.
 *             If this is the only property, it can be passed as a number
 *             instead of an element of a properties object.
 *  - argTypes: (optional) An array corresponding to each argument of the
 *              function, giving the type of argument that should be parsed. Its
 *              length should be equal to `numArgs + numOptionalArgs`. Valid
 *              types:
 *               - "size": A size-like thing, such as "1em" or "5ex"
 *               - "color": An html color, like "#abc" or "blue"
 *               - "original": The same type as the environment that the
 *                             function being parsed is in (e.g. used for the
 *                             bodies of functions like \color where the first
 *                             argument is special and the second argument is
 *                             parsed normally)
 *              Other possible types (probably shouldn't be used)
 *               - "text": Text-like (e.g. \text)
 *               - "math": Normal math
 *              If undefined, this will be treated as an appropriate length
 *              array of "original" strings
 *  - greediness: (optional) The greediness of the function to use ungrouped
 *                arguments.
 *
 *                E.g. if you have an expression
 *                  \sqrt \frac 1 2
 *                since \frac has greediness=2 vs \sqrt's greediness=1, \frac
 *                will use the two arguments '1' and '2' as its two arguments,
 *                then that whole function will be used as the argument to
 *                \sqrt. On the other hand, the expressions
 *                  \frac \frac 1 2 3
 *                and
 *                  \frac \sqrt 1 2
 *                will fail because \frac and \frac have equal greediness
 *                and \sqrt has a lower greediness than \frac respectively. To
 *                make these parse, we would have to change them to:
 *                  \frac {\frac 1 2} 3
 *                and
 *                  \frac {\sqrt 1} 2
 *
 *                The default value is `1`
 *  - allowedInText: (optional) Whether or not the function is allowed inside
 *                   text mode (default false)
 *  - numOptionalArgs: (optional) The number of optional arguments the function
 *                     should parse. If the optional arguments aren't found,
 *                     `null` will be passed to the handler in their place.
 *                     (default 0)
 *
 * The last argument is that implementation, the handler for the function(s).
 * It is called to handle these functions and their arguments.
 * It receives two arguments:
 *  - context contains information and references provided by the parser
 *  - args is an array of arguments obtained from TeX input
 * The context contains the following properties:
 *  - funcName: the text (i.e. name) of the function, including \
 *  - parser: the parser object
 *  - lexer: the lexer object
 *  - positions: the positions in the overall string of the function
 *               and the arguments.
 * The latter three should only be used to produce error messages.
 *
 * The function should return an object with the following keys:
 *  - type: The type of element that this is. This is then used in
 *          buildHTML/buildMathML to determine which function
 *          should be called to build this node into a DOM node
 * Any other data can be added to the object, which will be passed
 * in to the function in buildHTML/buildMathML as `group.value`.
 */
function defineFunction(names,props,handler){"string"===typeof names&&(names=[names]),"number"===typeof props&&(props={numArgs:props});for(
// Set default values of functions
var data={numArgs:props.numArgs,argTypes:props.argTypes,greediness:void 0===props.greediness?1:props.greediness,allowedInText:!!props.allowedInText,numOptionalArgs:props.numOptionalArgs||0,handler:handler},i=0;i<names.length;++i)module.exports[names[i]]=data}
// A normal square root
defineFunction("\\sqrt",{numArgs:1,numOptionalArgs:1},(function(context,args){var index=args[0],body=args[1];return{type:"sqrt",body:body,index:index}})),
// Some non-mathy text
defineFunction("\\text",{numArgs:1,argTypes:["text"],greediness:2},(function(context,args){var inner,body=args[0];
// Since the corresponding buildHTML/buildMathML function expects a
// list of elements, we normalize for different kinds of arguments
// TODO(emily): maybe this should be done somewhere else
return inner="ordgroup"===body.type?body.value:[body],{type:"text",body:inner}})),
// A two-argument custom color
defineFunction("\\color",{numArgs:2,allowedInText:!0,greediness:3,argTypes:["color","original"]},(function(context,args){var inner,color=args[0],body=args[1];return inner="ordgroup"===body.type?body.value:[body],{type:"color",color:color.value,value:inner}})),
// An overline
defineFunction("\\overline",{numArgs:1},(function(context,args){var body=args[0];return{type:"overline",body:body}})),
// An underline
defineFunction("\\underline",{numArgs:1},(function(context,args){var body=args[0];return{type:"underline",body:body}})),
// A box of the width and height
defineFunction("\\rule",{numArgs:2,numOptionalArgs:1,argTypes:["size","size","size"]},(function(context,args){var shift=args[0],width=args[1],height=args[2];return{type:"rule",shift:shift&&shift.value,width:width.value,height:height.value}})),
// A KaTeX logo
defineFunction("\\KaTeX",{numArgs:0},(function(context){return{type:"katex"}})),defineFunction("\\phantom",{numArgs:1},(function(context,args){var inner,body=args[0];return inner="ordgroup"===body.type?body.value:[body],{type:"phantom",value:inner}}));
// Extra data needed for the delimiter handler down below
var delimiterSizes={"\\bigl":{type:"open",size:1},"\\Bigl":{type:"open",size:2},"\\biggl":{type:"open",size:3},"\\Biggl":{type:"open",size:4},"\\bigr":{type:"close",size:1},"\\Bigr":{type:"close",size:2},"\\biggr":{type:"close",size:3},"\\Biggr":{type:"close",size:4},"\\bigm":{type:"rel",size:1},"\\Bigm":{type:"rel",size:2},"\\biggm":{type:"rel",size:3},"\\Biggm":{type:"rel",size:4},"\\big":{type:"textord",size:1},"\\Big":{type:"textord",size:2},"\\bigg":{type:"textord",size:3},"\\Bigg":{type:"textord",size:4}},delimiters=["(",")","[","\\lbrack","]","\\rbrack","\\{","\\lbrace","\\}","\\rbrace","\\lfloor","\\rfloor","\\lceil","\\rceil","<",">","\\langle","\\rangle","\\lt","\\gt","\\lvert","\\rvert","\\lVert","\\rVert","\\lgroup","\\rgroup","\\lmoustache","\\rmoustache","/","\\backslash","|","\\vert","\\|","\\Vert","\\uparrow","\\Uparrow","\\downarrow","\\Downarrow","\\updownarrow","\\Updownarrow","."],fontAliases={"\\Bbb":"\\mathbb","\\bold":"\\mathbf","\\frak":"\\mathfrak"};
// Single-argument color functions
defineFunction(["\\blue","\\orange","\\pink","\\red","\\green","\\gray","\\purple","\\blueA","\\blueB","\\blueC","\\blueD","\\blueE","\\tealA","\\tealB","\\tealC","\\tealD","\\tealE","\\greenA","\\greenB","\\greenC","\\greenD","\\greenE","\\goldA","\\goldB","\\goldC","\\goldD","\\goldE","\\redA","\\redB","\\redC","\\redD","\\redE","\\maroonA","\\maroonB","\\maroonC","\\maroonD","\\maroonE","\\purpleA","\\purpleB","\\purpleC","\\purpleD","\\purpleE","\\mintA","\\mintB","\\mintC","\\grayA","\\grayB","\\grayC","\\grayD","\\grayE","\\grayF","\\grayG","\\grayH","\\grayI","\\kaBlue","\\kaGreen"],{numArgs:1,allowedInText:!0,greediness:3},(function(context,args){var atoms,body=args[0];return atoms="ordgroup"===body.type?body.value:[body],{type:"color",color:"katex-"+context.funcName.slice(1),value:atoms}})),
// There are 2 flags for operators; whether they produce limits in
// displaystyle, and whether they are symbols and should grow in
// displaystyle. These four groups cover the four possible choices.
// No limits, not symbols
defineFunction(["\\arcsin","\\arccos","\\arctan","\\arg","\\cos","\\cosh","\\cot","\\coth","\\csc","\\deg","\\dim","\\exp","\\hom","\\ker","\\lg","\\ln","\\log","\\sec","\\sin","\\sinh","\\tan","\\tanh"],{numArgs:0},(function(context){return{type:"op",limits:!1,symbol:!1,body:context.funcName}})),
// Limits, not symbols
defineFunction(["\\det","\\gcd","\\inf","\\lim","\\liminf","\\limsup","\\max","\\min","\\Pr","\\sup"],{numArgs:0},(function(context){return{type:"op",limits:!0,symbol:!1,body:context.funcName}})),
// No limits, symbols
defineFunction(["\\int","\\iint","\\iiint","\\oint"],{numArgs:0},(function(context){return{type:"op",limits:!1,symbol:!0,body:context.funcName}})),
// Limits, symbols
defineFunction(["\\coprod","\\bigvee","\\bigwedge","\\biguplus","\\bigcap","\\bigcup","\\intop","\\prod","\\sum","\\bigotimes","\\bigoplus","\\bigodot","\\bigsqcup","\\smallint"],{numArgs:0},(function(context){return{type:"op",limits:!0,symbol:!0,body:context.funcName}})),
// Fractions
defineFunction(["\\dfrac","\\frac","\\tfrac","\\dbinom","\\binom","\\tbinom"],{numArgs:2,greediness:2},(function(context,args){var hasBarLine,numer=args[0],denom=args[1],leftDelim=null,rightDelim=null,size="auto";switch(context.funcName){case"\\dfrac":case"\\frac":case"\\tfrac":hasBarLine=!0;break;case"\\dbinom":case"\\binom":case"\\tbinom":hasBarLine=!1,leftDelim="(",rightDelim=")";break;default:throw new Error("Unrecognized genfrac command")}switch(context.funcName){case"\\dfrac":case"\\dbinom":size="display";break;case"\\tfrac":case"\\tbinom":size="text";break}return{type:"genfrac",numer:numer,denom:denom,hasBarLine:hasBarLine,leftDelim:leftDelim,rightDelim:rightDelim,size:size}})),
// Left and right overlap functions
defineFunction(["\\llap","\\rlap"],{numArgs:1,allowedInText:!0},(function(context,args){var body=args[0];return{type:context.funcName.slice(1),body:body}})),
// Delimiter functions
defineFunction(["\\bigl","\\Bigl","\\biggl","\\Biggl","\\bigr","\\Bigr","\\biggr","\\Biggr","\\bigm","\\Bigm","\\biggm","\\Biggm","\\big","\\Big","\\bigg","\\Bigg","\\left","\\right"],{numArgs:1},(function(context,args){var delim=args[0];if(!utils.contains(delimiters,delim.value))throw new ParseError("Invalid delimiter: '"+delim.value+"' after '"+context.funcName+"'",context.lexer,context.positions[1]);
// \left and \right are caught somewhere in Parser.js, which is
// why this data doesn't match what is in buildHTML.
return"\\left"===context.funcName||"\\right"===context.funcName?{type:"leftright",value:delim.value}:{type:"delimsizing",size:delimiterSizes[context.funcName].size,delimType:delimiterSizes[context.funcName].type,value:delim.value}})),
// Sizing functions (handled in Parser.js explicitly, hence no handler)
defineFunction(["\\tiny","\\scriptsize","\\footnotesize","\\small","\\normalsize","\\large","\\Large","\\LARGE","\\huge","\\Huge"],0,null),
// Style changing functions (handled in Parser.js explicitly, hence no
// handler)
defineFunction(["\\displaystyle","\\textstyle","\\scriptstyle","\\scriptscriptstyle"],0,null),defineFunction([
// styles
"\\mathrm","\\mathit","\\mathbf",
// families
"\\mathbb","\\mathcal","\\mathfrak","\\mathscr","\\mathsf","\\mathtt",
// aliases
"\\Bbb","\\bold","\\frak"],{numArgs:1,greediness:2},(function(context,args){var body=args[0],func=context.funcName;return func in fontAliases&&(func=fontAliases[func]),{type:"font",font:func.slice(1),body:body}})),
// Accents
defineFunction(["\\acute","\\grave","\\ddot","\\tilde","\\bar","\\breve","\\check","\\hat","\\vec","\\dot"],{numArgs:1},(function(context,args){var base=args[0];return{type:"accent",accent:context.funcName,base:base}})),
// Infix generalized fractions
defineFunction(["\\over","\\choose"],{numArgs:0},(function(context){var replaceWith;switch(context.funcName){case"\\over":replaceWith="\\frac";break;case"\\choose":replaceWith="\\binom";break;default:throw new Error("Unrecognized infix genfrac command")}return{type:"infix",replaceWith:replaceWith}})),
// Row breaks for aligned data
defineFunction(["\\\\","\\cr"],{numArgs:0,numOptionalArgs:1,argTypes:["size"]},(function(context,args){var size=args[0];return{type:"cr",size:size}})),
// Environment delimiters
defineFunction(["\\begin","\\end"],{numArgs:1,argTypes:["text"]},(function(context,args){var nameGroup=args[0];if("ordgroup"!==nameGroup.type)throw new ParseError("Invalid environment name",context.lexer,context.positions[1]);for(var name="",i=0;i<nameGroup.value.length;++i)name+=nameGroup.value[i].value;return{type:"environment",name:name,namepos:context.positions[1]}}))},
/***/570284:
/***/function(module,__unused_webpack_exports,__webpack_require__){
/**
 * These objects store data about MathML nodes. This is the MathML equivalent
 * of the types in domTree.js. Since MathML handles its own rendering, and
 * since we're mainly using MathML to improve accessibility, we don't manage
 * any of the styling state that the plain DOM nodes do.
 *
 * The `toNode` and `toMarkup` functions work simlarly to how they do in
 * domTree.js, creating namespaced DOM nodes and HTML text markup respectively.
 */
var utils=__webpack_require__(25182);
/**
 * This node represents a general purpose MathML node of any type. The
 * constructor requires the type of node to create (for example, `"mo"` or
 * `"mspace"`, corresponding to `<mo>` and `<mspace>` tags).
 */function MathNode(type,children){this.type=type,this.attributes={},this.children=children||[]}
/**
 * Sets an attribute on a MathML node. MathML depends on attributes to convey a
 * semantic content, so this is used heavily.
 */
/**
 * This node represents a piece of text.
 */
function TextNode(text){this.text=text}
/**
 * Converts the text node into a DOM text node.
 */MathNode.prototype.setAttribute=function(name,value){this.attributes[name]=value},
/**
 * Converts the math node into a MathML-namespaced DOM element.
 */
MathNode.prototype.toNode=function(){var node=document.createElementNS("http://www.w3.org/1998/Math/MathML",this.type);for(var attr in this.attributes)Object.prototype.hasOwnProperty.call(this.attributes,attr)&&node.setAttribute(attr,this.attributes[attr]);for(var i=0;i<this.children.length;i++)node.appendChild(this.children[i].toNode());return node},
/**
 * Converts the math node into an HTML markup string.
 */
MathNode.prototype.toMarkup=function(){var markup="<"+this.type;
// Add the attributes
for(var attr in this.attributes)Object.prototype.hasOwnProperty.call(this.attributes,attr)&&(markup+=" "+attr+'="',markup+=utils.escape(this.attributes[attr]),markup+='"');markup+=">";for(var i=0;i<this.children.length;i++)markup+=this.children[i].toMarkup();return markup+="</"+this.type+">",markup},TextNode.prototype.toNode=function(){return document.createTextNode(this.text)},
/**
 * Converts the text node into HTML markup (which is just the text itself).
 */
TextNode.prototype.toMarkup=function(){return utils.escape(this.text)},module.exports={MathNode:MathNode,TextNode:TextNode}},
/***/791758:
/***/function(module){
/**
 * The resulting parse tree nodes of the parse tree.
 */
function ParseNode(type,value,mode){this.type=type,this.value=value,this.mode=mode}module.exports={ParseNode:ParseNode}},
/***/581122:
/***/function(module,__unused_webpack_exports,__webpack_require__){
/**
 * Provides a single function for parsing an expression using a Parser
 * TODO(emily): Remove this
 */
var Parser=__webpack_require__(762717),parseTree=function(toParse,settings){var parser=new Parser(toParse,settings);return parser.parse()};
/**
 * Parses an expression using a Parser, then returns the parsed result.
 */module.exports=parseTree},
/***/710471:
/***/function(module){function defineSymbol(mode,font,group,replace,name){module.exports[mode][name]={font:font,group:group,replace:replace}}
// Some abbreviations for commonly used strings.
// This helps minify the code, and also spotting typos using jshint.
// modes:
/**
 * This file holds a list of all no-argument functions and single-character
 * symbols (like 'a' or ';').
 *
 * For each of the symbols, there are three properties they can have:
 * - font (required): the font to be used for this symbol. Either "main" (the
     normal font), or "ams" (the ams fonts).
 * - group (required): the ParseNode group type the symbol should have (i.e.
     "textord", "mathord", etc).
     See https://github.com/Khan/KaTeX/wiki/Examining-TeX#group-types
 * - replace: the character that this symbol or function should be
 *   replaced with (i.e. "\phi" has a replace value of "\u03d5", the phi
 *   character in the main font).
 *
 * The outermost map in the table indicates what mode the symbols should be
 * accepted in (e.g. "math" or "text").
 */
module.exports={math:{},text:{}};var i,ch,math="math",text="text",main="main",ams="ams",accent="accent",bin="bin",close="close",inner="inner",mathord="mathord",op="op",open="open",punct="punct",rel="rel",spacing="spacing",textord="textord";
// Now comes the symbol table
// Relation Symbols
defineSymbol(math,main,rel,"≡","\\equiv"),defineSymbol(math,main,rel,"≺","\\prec"),defineSymbol(math,main,rel,"≻","\\succ"),defineSymbol(math,main,rel,"∼","\\sim"),defineSymbol(math,main,rel,"⊥","\\perp"),defineSymbol(math,main,rel,"⪯","\\preceq"),defineSymbol(math,main,rel,"⪰","\\succeq"),defineSymbol(math,main,rel,"≃","\\simeq"),defineSymbol(math,main,rel,"∣","\\mid"),defineSymbol(math,main,rel,"≪","\\ll"),defineSymbol(math,main,rel,"≫","\\gg"),defineSymbol(math,main,rel,"≍","\\asymp"),defineSymbol(math,main,rel,"∥","\\parallel"),defineSymbol(math,main,rel,"⋈","\\bowtie"),defineSymbol(math,main,rel,"⌣","\\smile"),defineSymbol(math,main,rel,"⊑","\\sqsubseteq"),defineSymbol(math,main,rel,"⊒","\\sqsupseteq"),defineSymbol(math,main,rel,"≐","\\doteq"),defineSymbol(math,main,rel,"⌢","\\frown"),defineSymbol(math,main,rel,"∋","\\ni"),defineSymbol(math,main,rel,"∝","\\propto"),defineSymbol(math,main,rel,"⊢","\\vdash"),defineSymbol(math,main,rel,"⊣","\\dashv"),defineSymbol(math,main,rel,"∋","\\owns"),
// Punctuation
defineSymbol(math,main,punct,".","\\ldotp"),defineSymbol(math,main,punct,"⋅","\\cdotp"),
// Misc Symbols
defineSymbol(math,main,textord,"#","\\#"),defineSymbol(math,main,textord,"&","\\&"),defineSymbol(math,main,textord,"ℵ","\\aleph"),defineSymbol(math,main,textord,"∀","\\forall"),defineSymbol(math,main,textord,"ℏ","\\hbar"),defineSymbol(math,main,textord,"∃","\\exists"),defineSymbol(math,main,textord,"∇","\\nabla"),defineSymbol(math,main,textord,"♭","\\flat"),defineSymbol(math,main,textord,"ℓ","\\ell"),defineSymbol(math,main,textord,"♮","\\natural"),defineSymbol(math,main,textord,"♣","\\clubsuit"),defineSymbol(math,main,textord,"℘","\\wp"),defineSymbol(math,main,textord,"♯","\\sharp"),defineSymbol(math,main,textord,"♢","\\diamondsuit"),defineSymbol(math,main,textord,"ℜ","\\Re"),defineSymbol(math,main,textord,"♡","\\heartsuit"),defineSymbol(math,main,textord,"ℑ","\\Im"),defineSymbol(math,main,textord,"♠","\\spadesuit"),
// Math and Text
defineSymbol(math,main,textord,"†","\\dag"),defineSymbol(math,main,textord,"‡","\\ddag"),
// Large Delimiters
defineSymbol(math,main,close,"⎱","\\rmoustache"),defineSymbol(math,main,open,"⎰","\\lmoustache"),defineSymbol(math,main,close,"⟯","\\rgroup"),defineSymbol(math,main,open,"⟮","\\lgroup"),
// Binary Operators
defineSymbol(math,main,bin,"∓","\\mp"),defineSymbol(math,main,bin,"⊖","\\ominus"),defineSymbol(math,main,bin,"⊎","\\uplus"),defineSymbol(math,main,bin,"⊓","\\sqcap"),defineSymbol(math,main,bin,"∗","\\ast"),defineSymbol(math,main,bin,"⊔","\\sqcup"),defineSymbol(math,main,bin,"◯","\\bigcirc"),defineSymbol(math,main,bin,"∙","\\bullet"),defineSymbol(math,main,bin,"‡","\\ddagger"),defineSymbol(math,main,bin,"≀","\\wr"),defineSymbol(math,main,bin,"⨿","\\amalg"),
// Arrow Symbols
defineSymbol(math,main,rel,"⟵","\\longleftarrow"),defineSymbol(math,main,rel,"⇐","\\Leftarrow"),defineSymbol(math,main,rel,"⟸","\\Longleftarrow"),defineSymbol(math,main,rel,"⟶","\\longrightarrow"),defineSymbol(math,main,rel,"⇒","\\Rightarrow"),defineSymbol(math,main,rel,"⟹","\\Longrightarrow"),defineSymbol(math,main,rel,"↔","\\leftrightarrow"),defineSymbol(math,main,rel,"⟷","\\longleftrightarrow"),defineSymbol(math,main,rel,"⇔","\\Leftrightarrow"),defineSymbol(math,main,rel,"⟺","\\Longleftrightarrow"),defineSymbol(math,main,rel,"↦","\\mapsto"),defineSymbol(math,main,rel,"⟼","\\longmapsto"),defineSymbol(math,main,rel,"↗","\\nearrow"),defineSymbol(math,main,rel,"↩","\\hookleftarrow"),defineSymbol(math,main,rel,"↪","\\hookrightarrow"),defineSymbol(math,main,rel,"↘","\\searrow"),defineSymbol(math,main,rel,"↼","\\leftharpoonup"),defineSymbol(math,main,rel,"⇀","\\rightharpoonup"),defineSymbol(math,main,rel,"↙","\\swarrow"),defineSymbol(math,main,rel,"↽","\\leftharpoondown"),defineSymbol(math,main,rel,"⇁","\\rightharpoondown"),defineSymbol(math,main,rel,"↖","\\nwarrow"),defineSymbol(math,main,rel,"⇌","\\rightleftharpoons"),
// AMS Negated Binary Relations
defineSymbol(math,ams,rel,"≮","\\nless"),defineSymbol(math,ams,rel,"","\\nleqslant"),defineSymbol(math,ams,rel,"","\\nleqq"),defineSymbol(math,ams,rel,"⪇","\\lneq"),defineSymbol(math,ams,rel,"≨","\\lneqq"),defineSymbol(math,ams,rel,"","\\lvertneqq"),defineSymbol(math,ams,rel,"⋦","\\lnsim"),defineSymbol(math,ams,rel,"⪉","\\lnapprox"),defineSymbol(math,ams,rel,"⊀","\\nprec"),defineSymbol(math,ams,rel,"⋠","\\npreceq"),defineSymbol(math,ams,rel,"⋨","\\precnsim"),defineSymbol(math,ams,rel,"⪹","\\precnapprox"),defineSymbol(math,ams,rel,"≁","\\nsim"),defineSymbol(math,ams,rel,"","\\nshortmid"),defineSymbol(math,ams,rel,"∤","\\nmid"),defineSymbol(math,ams,rel,"⊬","\\nvdash"),defineSymbol(math,ams,rel,"⊭","\\nvDash"),defineSymbol(math,ams,rel,"⋪","\\ntriangleleft"),defineSymbol(math,ams,rel,"⋬","\\ntrianglelefteq"),defineSymbol(math,ams,rel,"⊊","\\subsetneq"),defineSymbol(math,ams,rel,"","\\varsubsetneq"),defineSymbol(math,ams,rel,"⫋","\\subsetneqq"),defineSymbol(math,ams,rel,"","\\varsubsetneqq"),defineSymbol(math,ams,rel,"≯","\\ngtr"),defineSymbol(math,ams,rel,"","\\ngeqslant"),defineSymbol(math,ams,rel,"","\\ngeqq"),defineSymbol(math,ams,rel,"⪈","\\gneq"),defineSymbol(math,ams,rel,"≩","\\gneqq"),defineSymbol(math,ams,rel,"","\\gvertneqq"),defineSymbol(math,ams,rel,"⋧","\\gnsim"),defineSymbol(math,ams,rel,"⪊","\\gnapprox"),defineSymbol(math,ams,rel,"⊁","\\nsucc"),defineSymbol(math,ams,rel,"⋡","\\nsucceq"),defineSymbol(math,ams,rel,"⋩","\\succnsim"),defineSymbol(math,ams,rel,"⪺","\\succnapprox"),defineSymbol(math,ams,rel,"≆","\\ncong"),defineSymbol(math,ams,rel,"","\\nshortparallel"),defineSymbol(math,ams,rel,"∦","\\nparallel"),defineSymbol(math,ams,rel,"⊯","\\nVDash"),defineSymbol(math,ams,rel,"⋫","\\ntriangleright"),defineSymbol(math,ams,rel,"⋭","\\ntrianglerighteq"),defineSymbol(math,ams,rel,"","\\nsupseteqq"),defineSymbol(math,ams,rel,"⊋","\\supsetneq"),defineSymbol(math,ams,rel,"","\\varsupsetneq"),defineSymbol(math,ams,rel,"⫌","\\supsetneqq"),defineSymbol(math,ams,rel,"","\\varsupsetneqq"),defineSymbol(math,ams,rel,"⊮","\\nVdash"),defineSymbol(math,ams,rel,"⪵","\\precneqq"),defineSymbol(math,ams,rel,"⪶","\\succneqq"),defineSymbol(math,ams,rel,"","\\nsubseteqq"),defineSymbol(math,ams,bin,"⊴","\\unlhd"),defineSymbol(math,ams,bin,"⊵","\\unrhd"),
// AMS Negated Arrows
defineSymbol(math,ams,rel,"↚","\\nleftarrow"),defineSymbol(math,ams,rel,"↛","\\nrightarrow"),defineSymbol(math,ams,rel,"⇍","\\nLeftarrow"),defineSymbol(math,ams,rel,"⇏","\\nRightarrow"),defineSymbol(math,ams,rel,"↮","\\nleftrightarrow"),defineSymbol(math,ams,rel,"⇎","\\nLeftrightarrow"),
// AMS Misc
defineSymbol(math,ams,rel,"△","\\vartriangle"),defineSymbol(math,ams,textord,"ℏ","\\hslash"),defineSymbol(math,ams,textord,"▽","\\triangledown"),defineSymbol(math,ams,textord,"◊","\\lozenge"),defineSymbol(math,ams,textord,"Ⓢ","\\circledS"),defineSymbol(math,ams,textord,"®","\\circledR"),defineSymbol(math,ams,textord,"∡","\\measuredangle"),defineSymbol(math,ams,textord,"∄","\\nexists"),defineSymbol(math,ams,textord,"℧","\\mho"),defineSymbol(math,ams,textord,"Ⅎ","\\Finv"),defineSymbol(math,ams,textord,"⅁","\\Game"),defineSymbol(math,ams,textord,"k","\\Bbbk"),defineSymbol(math,ams,textord,"‵","\\backprime"),defineSymbol(math,ams,textord,"▲","\\blacktriangle"),defineSymbol(math,ams,textord,"▼","\\blacktriangledown"),defineSymbol(math,ams,textord,"■","\\blacksquare"),defineSymbol(math,ams,textord,"⧫","\\blacklozenge"),defineSymbol(math,ams,textord,"★","\\bigstar"),defineSymbol(math,ams,textord,"∢","\\sphericalangle"),defineSymbol(math,ams,textord,"∁","\\complement"),defineSymbol(math,ams,textord,"ð","\\eth"),defineSymbol(math,ams,textord,"╱","\\diagup"),defineSymbol(math,ams,textord,"╲","\\diagdown"),defineSymbol(math,ams,textord,"□","\\square"),defineSymbol(math,ams,textord,"□","\\Box"),defineSymbol(math,ams,textord,"◊","\\Diamond"),defineSymbol(math,ams,textord,"¥","\\yen"),defineSymbol(math,ams,textord,"✓","\\checkmark"),
// AMS Hebrew
defineSymbol(math,ams,textord,"ℶ","\\beth"),defineSymbol(math,ams,textord,"ℸ","\\daleth"),defineSymbol(math,ams,textord,"ℷ","\\gimel"),
// AMS Greek
defineSymbol(math,ams,textord,"ϝ","\\digamma"),defineSymbol(math,ams,textord,"ϰ","\\varkappa"),
// AMS Delimiters
defineSymbol(math,ams,open,"┌","\\ulcorner"),defineSymbol(math,ams,close,"┐","\\urcorner"),defineSymbol(math,ams,open,"└","\\llcorner"),defineSymbol(math,ams,close,"┘","\\lrcorner"),
// AMS Binary Relations
defineSymbol(math,ams,rel,"≦","\\leqq"),defineSymbol(math,ams,rel,"⩽","\\leqslant"),defineSymbol(math,ams,rel,"⪕","\\eqslantless"),defineSymbol(math,ams,rel,"≲","\\lesssim"),defineSymbol(math,ams,rel,"⪅","\\lessapprox"),defineSymbol(math,ams,rel,"≊","\\approxeq"),defineSymbol(math,ams,bin,"⋖","\\lessdot"),defineSymbol(math,ams,rel,"⋘","\\lll"),defineSymbol(math,ams,rel,"≶","\\lessgtr"),defineSymbol(math,ams,rel,"⋚","\\lesseqgtr"),defineSymbol(math,ams,rel,"⪋","\\lesseqqgtr"),defineSymbol(math,ams,rel,"≑","\\doteqdot"),defineSymbol(math,ams,rel,"≓","\\risingdotseq"),defineSymbol(math,ams,rel,"≒","\\fallingdotseq"),defineSymbol(math,ams,rel,"∽","\\backsim"),defineSymbol(math,ams,rel,"⋍","\\backsimeq"),defineSymbol(math,ams,rel,"⫅","\\subseteqq"),defineSymbol(math,ams,rel,"⋐","\\Subset"),defineSymbol(math,ams,rel,"⊏","\\sqsubset"),defineSymbol(math,ams,rel,"≼","\\preccurlyeq"),defineSymbol(math,ams,rel,"⋞","\\curlyeqprec"),defineSymbol(math,ams,rel,"≾","\\precsim"),defineSymbol(math,ams,rel,"⪷","\\precapprox"),defineSymbol(math,ams,rel,"⊲","\\vartriangleleft"),defineSymbol(math,ams,rel,"⊴","\\trianglelefteq"),defineSymbol(math,ams,rel,"⊨","\\vDash"),defineSymbol(math,ams,rel,"⊪","\\Vvdash"),defineSymbol(math,ams,rel,"⌣","\\smallsmile"),defineSymbol(math,ams,rel,"⌢","\\smallfrown"),defineSymbol(math,ams,rel,"≏","\\bumpeq"),defineSymbol(math,ams,rel,"≎","\\Bumpeq"),defineSymbol(math,ams,rel,"≧","\\geqq"),defineSymbol(math,ams,rel,"⩾","\\geqslant"),defineSymbol(math,ams,rel,"⪖","\\eqslantgtr"),defineSymbol(math,ams,rel,"≳","\\gtrsim"),defineSymbol(math,ams,rel,"⪆","\\gtrapprox"),defineSymbol(math,ams,bin,"⋗","\\gtrdot"),defineSymbol(math,ams,rel,"⋙","\\ggg"),defineSymbol(math,ams,rel,"≷","\\gtrless"),defineSymbol(math,ams,rel,"⋛","\\gtreqless"),defineSymbol(math,ams,rel,"⪌","\\gtreqqless"),defineSymbol(math,ams,rel,"≖","\\eqcirc"),defineSymbol(math,ams,rel,"≗","\\circeq"),defineSymbol(math,ams,rel,"≜","\\triangleq"),defineSymbol(math,ams,rel,"∼","\\thicksim"),defineSymbol(math,ams,rel,"≈","\\thickapprox"),defineSymbol(math,ams,rel,"⫆","\\supseteqq"),defineSymbol(math,ams,rel,"⋑","\\Supset"),defineSymbol(math,ams,rel,"⊐","\\sqsupset"),defineSymbol(math,ams,rel,"≽","\\succcurlyeq"),defineSymbol(math,ams,rel,"⋟","\\curlyeqsucc"),defineSymbol(math,ams,rel,"≿","\\succsim"),defineSymbol(math,ams,rel,"⪸","\\succapprox"),defineSymbol(math,ams,rel,"⊳","\\vartriangleright"),defineSymbol(math,ams,rel,"⊵","\\trianglerighteq"),defineSymbol(math,ams,rel,"⊩","\\Vdash"),defineSymbol(math,ams,rel,"∣","\\shortmid"),defineSymbol(math,ams,rel,"∥","\\shortparallel"),defineSymbol(math,ams,rel,"≬","\\between"),defineSymbol(math,ams,rel,"⋔","\\pitchfork"),defineSymbol(math,ams,rel,"∝","\\varpropto"),defineSymbol(math,ams,rel,"◀","\\blacktriangleleft"),defineSymbol(math,ams,rel,"∴","\\therefore"),defineSymbol(math,ams,rel,"∍","\\backepsilon"),defineSymbol(math,ams,rel,"▶","\\blacktriangleright"),defineSymbol(math,ams,rel,"∵","\\because"),defineSymbol(math,ams,rel,"⋘","\\llless"),defineSymbol(math,ams,rel,"⋙","\\gggtr"),defineSymbol(math,ams,bin,"⊲","\\lhd"),defineSymbol(math,ams,bin,"⊳","\\rhd"),defineSymbol(math,ams,rel,"≂","\\eqsim"),defineSymbol(math,main,rel,"⋈","\\Join"),defineSymbol(math,ams,rel,"≑","\\Doteq"),
// AMS Binary Operators
defineSymbol(math,ams,bin,"∔","\\dotplus"),defineSymbol(math,ams,bin,"∖","\\smallsetminus"),defineSymbol(math,ams,bin,"⋒","\\Cap"),defineSymbol(math,ams,bin,"⋓","\\Cup"),defineSymbol(math,ams,bin,"⩞","\\doublebarwedge"),defineSymbol(math,ams,bin,"⊟","\\boxminus"),defineSymbol(math,ams,bin,"⊞","\\boxplus"),defineSymbol(math,ams,bin,"⋇","\\divideontimes"),defineSymbol(math,ams,bin,"⋉","\\ltimes"),defineSymbol(math,ams,bin,"⋊","\\rtimes"),defineSymbol(math,ams,bin,"⋋","\\leftthreetimes"),defineSymbol(math,ams,bin,"⋌","\\rightthreetimes"),defineSymbol(math,ams,bin,"⋏","\\curlywedge"),defineSymbol(math,ams,bin,"⋎","\\curlyvee"),defineSymbol(math,ams,bin,"⊝","\\circleddash"),defineSymbol(math,ams,bin,"⊛","\\circledast"),defineSymbol(math,ams,bin,"⋅","\\centerdot"),defineSymbol(math,ams,bin,"⊺","\\intercal"),defineSymbol(math,ams,bin,"⋒","\\doublecap"),defineSymbol(math,ams,bin,"⋓","\\doublecup"),defineSymbol(math,ams,bin,"⊠","\\boxtimes"),
// AMS Arrows
defineSymbol(math,ams,rel,"⇢","\\dashrightarrow"),defineSymbol(math,ams,rel,"⇠","\\dashleftarrow"),defineSymbol(math,ams,rel,"⇇","\\leftleftarrows"),defineSymbol(math,ams,rel,"⇆","\\leftrightarrows"),defineSymbol(math,ams,rel,"⇚","\\Lleftarrow"),defineSymbol(math,ams,rel,"↞","\\twoheadleftarrow"),defineSymbol(math,ams,rel,"↢","\\leftarrowtail"),defineSymbol(math,ams,rel,"↫","\\looparrowleft"),defineSymbol(math,ams,rel,"⇋","\\leftrightharpoons"),defineSymbol(math,ams,rel,"↶","\\curvearrowleft"),defineSymbol(math,ams,rel,"↺","\\circlearrowleft"),defineSymbol(math,ams,rel,"↰","\\Lsh"),defineSymbol(math,ams,rel,"⇈","\\upuparrows"),defineSymbol(math,ams,rel,"↿","\\upharpoonleft"),defineSymbol(math,ams,rel,"⇃","\\downharpoonleft"),defineSymbol(math,ams,rel,"⊸","\\multimap"),defineSymbol(math,ams,rel,"↭","\\leftrightsquigarrow"),defineSymbol(math,ams,rel,"⇉","\\rightrightarrows"),defineSymbol(math,ams,rel,"⇄","\\rightleftarrows"),defineSymbol(math,ams,rel,"↠","\\twoheadrightarrow"),defineSymbol(math,ams,rel,"↣","\\rightarrowtail"),defineSymbol(math,ams,rel,"↬","\\looparrowright"),defineSymbol(math,ams,rel,"↷","\\curvearrowright"),defineSymbol(math,ams,rel,"↻","\\circlearrowright"),defineSymbol(math,ams,rel,"↱","\\Rsh"),defineSymbol(math,ams,rel,"⇊","\\downdownarrows"),defineSymbol(math,ams,rel,"↾","\\upharpoonright"),defineSymbol(math,ams,rel,"⇂","\\downharpoonright"),defineSymbol(math,ams,rel,"⇝","\\rightsquigarrow"),defineSymbol(math,ams,rel,"⇝","\\leadsto"),defineSymbol(math,ams,rel,"⇛","\\Rrightarrow"),defineSymbol(math,ams,rel,"↾","\\restriction"),defineSymbol(math,main,textord,"‘","`"),defineSymbol(math,main,textord,"$","\\$"),defineSymbol(math,main,textord,"%","\\%"),defineSymbol(math,main,textord,"_","\\_"),defineSymbol(math,main,textord,"∠","\\angle"),defineSymbol(math,main,textord,"∞","\\infty"),defineSymbol(math,main,textord,"′","\\prime"),defineSymbol(math,main,textord,"△","\\triangle"),defineSymbol(math,main,textord,"Γ","\\Gamma"),defineSymbol(math,main,textord,"Δ","\\Delta"),defineSymbol(math,main,textord,"Θ","\\Theta"),defineSymbol(math,main,textord,"Λ","\\Lambda"),defineSymbol(math,main,textord,"Ξ","\\Xi"),defineSymbol(math,main,textord,"Π","\\Pi"),defineSymbol(math,main,textord,"Σ","\\Sigma"),defineSymbol(math,main,textord,"Υ","\\Upsilon"),defineSymbol(math,main,textord,"Φ","\\Phi"),defineSymbol(math,main,textord,"Ψ","\\Psi"),defineSymbol(math,main,textord,"Ω","\\Omega"),defineSymbol(math,main,textord,"¬","\\neg"),defineSymbol(math,main,textord,"¬","\\lnot"),defineSymbol(math,main,textord,"⊤","\\top"),defineSymbol(math,main,textord,"⊥","\\bot"),defineSymbol(math,main,textord,"∅","\\emptyset"),defineSymbol(math,ams,textord,"∅","\\varnothing"),defineSymbol(math,main,mathord,"α","\\alpha"),defineSymbol(math,main,mathord,"β","\\beta"),defineSymbol(math,main,mathord,"γ","\\gamma"),defineSymbol(math,main,mathord,"δ","\\delta"),defineSymbol(math,main,mathord,"ϵ","\\epsilon"),defineSymbol(math,main,mathord,"ζ","\\zeta"),defineSymbol(math,main,mathord,"η","\\eta"),defineSymbol(math,main,mathord,"θ","\\theta"),defineSymbol(math,main,mathord,"ι","\\iota"),defineSymbol(math,main,mathord,"κ","\\kappa"),defineSymbol(math,main,mathord,"λ","\\lambda"),defineSymbol(math,main,mathord,"μ","\\mu"),defineSymbol(math,main,mathord,"ν","\\nu"),defineSymbol(math,main,mathord,"ξ","\\xi"),defineSymbol(math,main,mathord,"o","\\omicron"),defineSymbol(math,main,mathord,"π","\\pi"),defineSymbol(math,main,mathord,"ρ","\\rho"),defineSymbol(math,main,mathord,"σ","\\sigma"),defineSymbol(math,main,mathord,"τ","\\tau"),defineSymbol(math,main,mathord,"υ","\\upsilon"),defineSymbol(math,main,mathord,"ϕ","\\phi"),defineSymbol(math,main,mathord,"χ","\\chi"),defineSymbol(math,main,mathord,"ψ","\\psi"),defineSymbol(math,main,mathord,"ω","\\omega"),defineSymbol(math,main,mathord,"ε","\\varepsilon"),defineSymbol(math,main,mathord,"ϑ","\\vartheta"),defineSymbol(math,main,mathord,"ϖ","\\varpi"),defineSymbol(math,main,mathord,"ϱ","\\varrho"),defineSymbol(math,main,mathord,"ς","\\varsigma"),defineSymbol(math,main,mathord,"φ","\\varphi"),defineSymbol(math,main,bin,"∗","*"),defineSymbol(math,main,bin,"+","+"),defineSymbol(math,main,bin,"−","-"),defineSymbol(math,main,bin,"⋅","\\cdot"),defineSymbol(math,main,bin,"∘","\\circ"),defineSymbol(math,main,bin,"÷","\\div"),defineSymbol(math,main,bin,"±","\\pm"),defineSymbol(math,main,bin,"×","\\times"),defineSymbol(math,main,bin,"∩","\\cap"),defineSymbol(math,main,bin,"∪","\\cup"),defineSymbol(math,main,bin,"∖","\\setminus"),defineSymbol(math,main,bin,"∧","\\land"),defineSymbol(math,main,bin,"∨","\\lor"),defineSymbol(math,main,bin,"∧","\\wedge"),defineSymbol(math,main,bin,"∨","\\vee"),defineSymbol(math,main,textord,"√","\\surd"),defineSymbol(math,main,open,"(","("),defineSymbol(math,main,open,"[","["),defineSymbol(math,main,open,"⟨","\\langle"),defineSymbol(math,main,open,"∣","\\lvert"),defineSymbol(math,main,open,"∥","\\lVert"),defineSymbol(math,main,close,")",")"),defineSymbol(math,main,close,"]","]"),defineSymbol(math,main,close,"?","?"),defineSymbol(math,main,close,"!","!"),defineSymbol(math,main,close,"⟩","\\rangle"),defineSymbol(math,main,close,"∣","\\rvert"),defineSymbol(math,main,close,"∥","\\rVert"),defineSymbol(math,main,rel,"=","="),defineSymbol(math,main,rel,"<","<"),defineSymbol(math,main,rel,">",">"),defineSymbol(math,main,rel,":",":"),defineSymbol(math,main,rel,"≈","\\approx"),defineSymbol(math,main,rel,"≅","\\cong"),defineSymbol(math,main,rel,"≥","\\ge"),defineSymbol(math,main,rel,"≥","\\geq"),defineSymbol(math,main,rel,"←","\\gets"),defineSymbol(math,main,rel,">","\\gt"),defineSymbol(math,main,rel,"∈","\\in"),defineSymbol(math,main,rel,"∉","\\notin"),defineSymbol(math,main,rel,"⊂","\\subset"),defineSymbol(math,main,rel,"⊃","\\supset"),defineSymbol(math,main,rel,"⊆","\\subseteq"),defineSymbol(math,main,rel,"⊇","\\supseteq"),defineSymbol(math,ams,rel,"⊈","\\nsubseteq"),defineSymbol(math,ams,rel,"⊉","\\nsupseteq"),defineSymbol(math,main,rel,"⊨","\\models"),defineSymbol(math,main,rel,"←","\\leftarrow"),defineSymbol(math,main,rel,"≤","\\le"),defineSymbol(math,main,rel,"≤","\\leq"),defineSymbol(math,main,rel,"<","\\lt"),defineSymbol(math,main,rel,"≠","\\ne"),defineSymbol(math,main,rel,"≠","\\neq"),defineSymbol(math,main,rel,"→","\\rightarrow"),defineSymbol(math,main,rel,"→","\\to"),defineSymbol(math,ams,rel,"≱","\\ngeq"),defineSymbol(math,ams,rel,"≰","\\nleq"),defineSymbol(math,main,spacing,null,"\\!"),defineSymbol(math,main,spacing," ","\\ "),defineSymbol(math,main,spacing," ","~"),defineSymbol(math,main,spacing,null,"\\,"),defineSymbol(math,main,spacing,null,"\\:"),defineSymbol(math,main,spacing,null,"\\;"),defineSymbol(math,main,spacing,null,"\\enspace"),defineSymbol(math,main,spacing,null,"\\qquad"),defineSymbol(math,main,spacing,null,"\\quad"),defineSymbol(math,main,spacing," ","\\space"),defineSymbol(math,main,punct,",",","),defineSymbol(math,main,punct,";",";"),defineSymbol(math,main,punct,":","\\colon"),defineSymbol(math,ams,bin,"⊼","\\barwedge"),defineSymbol(math,ams,bin,"⊻","\\veebar"),defineSymbol(math,main,bin,"⊙","\\odot"),defineSymbol(math,main,bin,"⊕","\\oplus"),defineSymbol(math,main,bin,"⊗","\\otimes"),defineSymbol(math,main,textord,"∂","\\partial"),defineSymbol(math,main,bin,"⊘","\\oslash"),defineSymbol(math,ams,bin,"⊚","\\circledcirc"),defineSymbol(math,ams,bin,"⊡","\\boxdot"),defineSymbol(math,main,bin,"△","\\bigtriangleup"),defineSymbol(math,main,bin,"▽","\\bigtriangledown"),defineSymbol(math,main,bin,"†","\\dagger"),defineSymbol(math,main,bin,"⋄","\\diamond"),defineSymbol(math,main,bin,"⋆","\\star"),defineSymbol(math,main,bin,"◃","\\triangleleft"),defineSymbol(math,main,bin,"▹","\\triangleright"),defineSymbol(math,main,open,"{","\\{"),defineSymbol(math,main,close,"}","\\}"),defineSymbol(math,main,open,"{","\\lbrace"),defineSymbol(math,main,close,"}","\\rbrace"),defineSymbol(math,main,open,"[","\\lbrack"),defineSymbol(math,main,close,"]","\\rbrack"),defineSymbol(math,main,open,"⌊","\\lfloor"),defineSymbol(math,main,close,"⌋","\\rfloor"),defineSymbol(math,main,open,"⌈","\\lceil"),defineSymbol(math,main,close,"⌉","\\rceil"),defineSymbol(math,main,textord,"\\","\\backslash"),defineSymbol(math,main,textord,"∣","|"),defineSymbol(math,main,textord,"∣","\\vert"),defineSymbol(math,main,textord,"∥","\\|"),defineSymbol(math,main,textord,"∥","\\Vert"),defineSymbol(math,main,rel,"↑","\\uparrow"),defineSymbol(math,main,rel,"⇑","\\Uparrow"),defineSymbol(math,main,rel,"↓","\\downarrow"),defineSymbol(math,main,rel,"⇓","\\Downarrow"),defineSymbol(math,main,rel,"↕","\\updownarrow"),defineSymbol(math,main,rel,"⇕","\\Updownarrow"),defineSymbol(math,math,op,"∐","\\coprod"),defineSymbol(math,math,op,"⋁","\\bigvee"),defineSymbol(math,math,op,"⋀","\\bigwedge"),defineSymbol(math,math,op,"⨄","\\biguplus"),defineSymbol(math,math,op,"⋂","\\bigcap"),defineSymbol(math,math,op,"⋃","\\bigcup"),defineSymbol(math,math,op,"∫","\\int"),defineSymbol(math,math,op,"∫","\\intop"),defineSymbol(math,math,op,"∬","\\iint"),defineSymbol(math,math,op,"∭","\\iiint"),defineSymbol(math,math,op,"∏","\\prod"),defineSymbol(math,math,op,"∑","\\sum"),defineSymbol(math,math,op,"⨂","\\bigotimes"),defineSymbol(math,math,op,"⨁","\\bigoplus"),defineSymbol(math,math,op,"⨀","\\bigodot"),defineSymbol(math,math,op,"∮","\\oint"),defineSymbol(math,math,op,"⨆","\\bigsqcup"),defineSymbol(math,math,op,"∫","\\smallint"),defineSymbol(math,main,inner,"…","\\ldots"),defineSymbol(math,main,inner,"⋯","\\cdots"),defineSymbol(math,main,inner,"⋱","\\ddots"),defineSymbol(math,main,textord,"⋮","\\vdots"),defineSymbol(math,main,accent,"´","\\acute"),defineSymbol(math,main,accent,"`","\\grave"),defineSymbol(math,main,accent,"¨","\\ddot"),defineSymbol(math,main,accent,"~","\\tilde"),defineSymbol(math,main,accent,"¯","\\bar"),defineSymbol(math,main,accent,"˘","\\breve"),defineSymbol(math,main,accent,"ˇ","\\check"),defineSymbol(math,main,accent,"^","\\hat"),defineSymbol(math,main,accent,"⃗","\\vec"),defineSymbol(math,main,accent,"˙","\\dot"),defineSymbol(math,main,mathord,"ı","\\imath"),defineSymbol(math,main,mathord,"ȷ","\\jmath"),defineSymbol(text,main,spacing," ","\\ "),defineSymbol(text,main,spacing," "," "),defineSymbol(text,main,spacing," ","~");
// All of these are textords in math mode
var mathTextSymbols='0123456789/@."';for(i=0;i<mathTextSymbols.length;i++)ch=mathTextSymbols.charAt(i),defineSymbol(math,main,textord,ch,ch);
// All of these are textords in text mode
var textSymbols="0123456789`!@*()-=+[]'\";:?/.,";for(i=0;i<textSymbols.length;i++)ch=textSymbols.charAt(i),defineSymbol(text,main,textord,ch,ch);
// All of these are textords in text mode, and mathords in math mode
var letters="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";for(i=0;i<letters.length;i++)ch=letters.charAt(i),defineSymbol(math,main,mathord,ch,ch),defineSymbol(text,main,textord,ch,ch);
/***/},
/***/25182:
/***/function(module){
/**
 * This file contains a list of utility functions which are useful in other
 * files.
 */
/**
 * Provide an `indexOf` function which works in IE8, but defers to native if
 * possible.
 */
var setTextContent,nativeIndexOf=Array.prototype.indexOf,indexOf=function(list,elem){if(null==list)return-1;if(nativeIndexOf&&list.indexOf===nativeIndexOf)return list.indexOf(elem);for(var i=0,l=list.length;i<l;i++)if(list[i]===elem)return i;return-1},contains=function(list,elem){return-1!==indexOf(list,elem)},deflt=function(setting,defaultIfUndefined){return void 0===setting?defaultIfUndefined:setting},uppercase=/([A-Z])/g,hyphenate=function(str){return str.replace(uppercase,"-$1").toLowerCase()},ESCAPE_LOOKUP={"&":"&amp;",">":"&gt;","<":"&lt;",'"':"&quot;","'":"&#x27;"},ESCAPE_REGEX=/[&><"']/g;function escaper(match){return ESCAPE_LOOKUP[match]}
/**
 * Escapes text to prevent scripting attacks.
 *
 * @param {*} text Text value to escape.
 * @return {string} An escaped string.
 */function escape(text){return(""+text).replace(ESCAPE_REGEX,escaper)}
/**
 * A function to set the text content of a DOM element in all supported
 * browsers. Note that we don't define this if there is no document.
 */if("undefined"!==typeof document){var testNode=document.createElement("span");setTextContent="textContent"in testNode?function(node,text){node.textContent=text}:function(node,text){node.innerText=text}}
/**
 * A function to clear a node.
 */function clearNode(node){setTextContent(node,"")}module.exports={contains:contains,deflt:deflt,escape:escape,hyphenate:hyphenate,indexOf:indexOf,setTextContent:setTextContent,clearNode:clearNode}},
/***/565155:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){"use strict";
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */mc:function(){/* binding */return reset},
/* harmony export */sE:function(){/* binding */return find},
/* harmony export */zR:function(){/* binding */return registerCustomProtocol}
/* harmony export */});
/* unused harmony exports MultiToken, Options, State, createTokenClass, init, multi, options, regexp, registerPlugin, registerTokenPlugin, stringToArray, test, tokenize */
// THIS FILE IS AUTOMATICALLY GENERATED DO NOT EDIT DIRECTLY
// See update-tlds.js for encoding/decoding format
// https://data.iana.org/TLD/tlds-alpha-by-domain.txt
const encodedTlds="aaa1rp3barth4b0ott3vie4c1le2ogado5udhabi7c0ademy5centure6ountant0s9o1tor4d0s1ult4e0g1ro2tna4f0l1rica5g0akhan5ency5i0g1rbus3force5tel5kdn3l0faromeo7ibaba4pay4lfinanz6state5y2sace3tom5m0azon4ericanexpress7family11x2fam3ica3sterdam8nalytics7droid5quan4z2o0l2partments8p0le4q0uarelle8r0ab1mco4chi3my2pa2t0e3s0da2ia2sociates9t0hleta5torney7u0ction5di0ble3o3spost5thor3o0s4vianca6w0s2x0a2z0ure5ba0by2idu3namex3narepublic11d1k2r0celona5laycard4s5efoot5gains6seball5ketball8uhaus5yern5b0c1t1va3cg1n2d1e0ats2uty4er2ntley5rlin4st0buy5t2f1g1h0arti5i0ble3d1ke2ng0o3o1z2j1lack0friday9ockbuster8g1omberg7ue3m0s1w2n0pparibas9o0ats3ehringer8fa2m1nd2o0k0ing5sch2tik2on4t1utique6x2r0adesco6idgestone9oadway5ker3ther5ussels7s1t1uild0ers6siness6y1zz3v1w1y1z0h3ca0b1fe2l0l1vinklein9m0era3p2non3petown5ital0one8r0avan4ds2e0er0s4s2sa1e1h1ino4t0ering5holic7ba1n1re2s2c1d1enter4o1rn3f0a1d2g1h0anel2nel4rity4se2t2eap3intai5ristmas6ome4urch5i0priani6rcle4sco3tadel4i0c2y0eats7k1l0aims4eaning6ick2nic1que6othing5ud3ub0med6m1n1o0ach3des3ffee4llege4ogne5m0cast4mbank4unity6pany2re3uter5sec4ndos3struction8ulting7tact3ractors9oking0channel11l1p2rsica5untry4pon0s4rses6pa2r0edit0card4union9icket5own3s1uise0s6u0isinella9v1w1x1y0mru3ou3z2dabur3d1nce3ta1e1ing3sun4y2clk3ds2e0al0er2s3gree4livery5l1oitte5ta3mocrat6ntal2ist5si0gn4v2hl2iamonds6et2gital5rect0ory7scount3ver5h2y2j1k1m1np2o0cs1tor4g1mains5t1wnload7rive4tv2ubai3nlop4pont4rban5vag2r2z2earth3t2c0o2deka3u0cation8e1g1mail3erck5nergy4gineer0ing9terprises10pson4quipment8r0icsson6ni3s0q1tate5t0isalat7u0rovision8s2vents5xchange6pert3osed4ress5traspace10fage2il1rwinds6th3mily4n0s2rm0ers5shion4t3edex3edback6rrari3ero6i0at2delity5o2lm2nal1nce1ial7re0stone6mdale6sh0ing5t0ness6j1k1lickr3ghts4r2orist4wers5y2m1o0o0d0network8tball6rd1ex2sale4um3undation8x2r0ee1senius7l1ogans4ntdoor4ier7tr2ujitsu5n0d2rniture7tbol5yi3ga0l0lery3o1up4me0s3p1rden4y2b0iz3d0n2e0a1nt0ing5orge5f1g0ee3h1i0ft0s3ves2ing5l0ass3e1obal2o4m0ail3bh2o1x2n1odaddy5ld0point6f2o0dyear5g0le4p1t1v2p1q1r0ainger5phics5tis4een3ipe3ocery4up4s1t1u0ardian6cci3ge2ide2tars5ru3w1y2hair2mburg5ngout5us3bo2dfc0bank7ealth0care8lp1sinki6re1mes5gtv3iphop4samitsu7tachi5v2k0t2m1n1ockey4ldings5iday5medepot5goods5s0ense7nda3rse3spital5t0ing5t0eles2s3mail5use3w2r1sbc3t1u0ghes5yatt3undai7ibm2cbc2e1u2d1e0ee3fm2kano4l1m0amat4db2mo0bilien9n0c1dustries8finiti5o2g1k1stitute6urance4e4t0ernational10uit4vestments10o1piranga7q1r0ish4s0maili5t0anbul7t0au2v3jaguar4va3cb2e0ep2tzt3welry6io2ll2m0p2nj2o0bs1urg4t1y2p0morgan6rs3uegos4niper7kaufen5ddi3e0rryhotels6logistics9properties14fh2g1h1i0a1ds2m1nder2le4tchen5wi3m1n1oeln3matsu5sher5p0mg2n2r0d1ed3uokgroup8w1y0oto4z2la0caixa5mborghini8er3ncaster5ia3d0rover6xess5salle5t0ino3robe5w0yer5b1c1ds2ease3clerc5frak4gal2o2xus4gbt3i0dl2fe0insurance9style7ghting6ke2lly3mited4o2ncoln4de2k2psy3ve1ing5k1lc1p2oan0s3cker3us3l1ndon4tte1o3ve3pl0financial11r1s1t0d0a3u0ndbeck6xe1ury5v1y2ma0cys3drid4if1son4keup4n0agement7go3p1rket0ing3s4riott5shalls7serati6ttel5ba2c0kinsey7d1e0d0ia3et2lbourne7me1orial6n0u2rckmsd7g1h1iami3crosoft7l1ni1t2t0subishi9k1l0b1s2m0a2n1o0bi0le4da2e1i1m1nash3ey2ster5rmon3tgage6scow4to0rcycles9v0ie4p1q1r1s0d2t0n1r2u0seum3ic3tual5v1w1x1y1z2na0b1goya4me2tura4vy3ba2c1e0c1t0bank4flix4work5ustar5w0s2xt0direct7us4f0l2g0o2hk2i0co2ke1on3nja3ssan1y5l1o0kia3rthwesternmutual14on4w0ruz3tv4p1r0a1w2tt2u1yc2z2obi1server7ffice5kinawa6layan0group9dnavy5lo3m0ega4ne1g1l0ine5oo2pen3racle3nge4g0anic5igins6saka4tsuka4t2vh3pa0ge2nasonic7ris2s1tners4s1y3ssagens7y2ccw3e0t2f0izer5g1h0armacy6d1ilips5one2to0graphy6s4ysio5ics1tet2ures6d1n0g1k2oneer5zza4k1l0ace2y0station9umbing5s3m1n0c2ohl2ker3litie5rn2st3r0america6xi3ess3ime3o0d0uctions8f1gressive8mo2perties3y5tection8u0dential9s1t1ub2w0c2y2qa1pon3uebec3st5racing4dio4e0ad1lestate6tor2y4cipes5d0stone5umbrella9hab3ise0n3t2liance6n0t0als5pair3ort3ublican8st0aurant8view0s5xroth6ich0ardli6oh3l1o1p2o0cher3ks3deo3gers4om3s0vp3u0gby3hr2n2w0e2yukyu6sa0arland6fe0ty4kura4le1on3msclub4ung5ndvik0coromant12ofi4p1rl2s1ve2xo3b0i1s2c0a1b1haeffler7midt4olarships8ol3ule3warz5ience5ot3d1e0arch3t2cure1ity6ek2lect4ner3rvices6ven3w1x0y3fr2g1h0angrila6rp2w2ell3ia1ksha5oes2p0ping5uji3w0time7i0lk2na1gles5te3j1k0i0n2y0pe4l0ing4m0art3ile4n0cf3o0ccer3ial4ftbank4ware6hu2lar2utions7ng1y2y2pa0ce3ort2t3r0l2s1t0ada2ples4r1tebank4farm7c0group6ockholm6rage3e3ream4udio2y3yle4u0cks3pplies3y2ort5rf1gery5zuki5v1watch4iss4x1y0dney4stems6z2tab1ipei4lk2obao4rget4tamotors6r2too4x0i3c0i2d0k2eam2ch0nology8l1masek5nnis4va3f1g1h0d1eater2re6iaa2ckets5enda4ffany5ps2res2ol4j0maxx4x2k0maxx5l1m0all4n1o0day3kyo3ols3p1ray3shiba5tal3urs3wn2yota3s3r0ade1ing4ining5vel0channel7ers0insurance16ust3v2t1ube2i1nes3shu4v0s2w1z2ua1bank3s2g1k1nicom3versity8o2ol2ps2s1y1z2va0cations7na1guard7c1e0gas3ntures6risign5mögensberater2ung14sicherung10t2g1i0ajes4deo3g1king4llas4n1p1rgin4sa1ion4va1o3laanderen9n1odka3lkswagen7vo3te1ing3o2yage5u0elos6wales2mart4ter4ng0gou5tch0es6eather0channel12bcam3er2site5d0ding5ibo2r3f1hoswho6ien2ki2lliamhill9n0dows4e1ners6me2olterskluwer11odside6rk0s2ld3w2s1tc1f3xbox3erox4finity6ihuan4n2xx2yz3yachts4hoo3maxun5ndex5e1odobashi7ga2kohama6u0tube6t1un3za0ppos4ra3ero3ip2m1one3uerich6w2",encodedUtlds="ελ1υ2бг1ел3дети4ею2католик6ом3мкд2он1сква6онлайн5рг3рус2ф2сайт3рб3укр3қаз3հայ3ישראל5קום3ابوظبي5تصالات6رامكو5لاردن4بحرين5جزائر5سعودية6عليان5مغرب5مارات5یران5بارت2زار4يتك3ھارت5تونس4سودان3رية5شبكة4عراق2ب2مان4فلسطين6قطر3كاثوليك6وم3مصر2ليسيا5وريتانيا7قع4همراه5پاکستان7ڀارت4कॉम3नेट3भारत0म्3ोत5संगठन5বাংলা5ভারত2ৰত4ਭਾਰਤ4ભારત4ଭାରତ4இந்தியா6லங்கை6சிங்கப்பூர்11భారత్5ಭಾರತ4ഭാരതം5ලංකා4คอม3ไทย3ລາວ3გე2みんな3アマゾン4クラウド4グーグル4コム2ストア3セール3ファッション6ポイント4世界2中信1国1國1文网3亚马逊3企业2佛山2信息2健康2八卦2公司1益2台湾1灣2商城1店1标2嘉里0大酒店5在线2大拿2天主教3娱乐2家電2广东2微博2慈善2我爱你3手机2招聘2政务1府2新加坡2闻2时尚2書籍2机构2淡马锡3游戏2澳門2点看2移动2组织机构4网址1店1站1络2联通2谷歌2购物2通販2集团2電訊盈科4飞利浦3食品2餐厅2香格里拉3港2닷넷1컴2삼성2한국2",assign=(target,properties)=>{for(const key in properties)target[key]=properties[key];return target},numeric="numeric",ascii="ascii",alpha="alpha",asciinumeric="asciinumeric",alphanumeric="alphanumeric",domain="domain",emoji="emoji",scheme="scheme",slashscheme="slashscheme",whitespace="whitespace";
// Internationalized domain names containing non-ASCII
/**
 * @template T
 * @param {string} name
 * @param {Collections<T>} groups to register in
 * @returns {T[]} Current list of tokens in the given collection
 */
function registerGroup(name,groups){return name in groups||(groups[name]=[]),groups[name]}
/**
 * @template T
 * @param {T} t token to add
 * @param {Collections<T>} groups
 * @param {Flags} flags
 */function addToGroups(t,flags,groups){flags[numeric]&&(flags[asciinumeric]=!0,flags[alphanumeric]=!0),flags[ascii]&&(flags[asciinumeric]=!0,flags[alpha]=!0),flags[asciinumeric]&&(flags[alphanumeric]=!0),flags[alpha]&&(flags[alphanumeric]=!0),flags[alphanumeric]&&(flags[domain]=!0),flags[emoji]&&(flags[domain]=!0);for(const k in flags){const group=registerGroup(k,groups);group.indexOf(t)<0&&group.push(t)}}
/**
 * @template T
 * @param {T} t token to check
 * @param {Collections<T>} groups
 * @returns {Flags} group flags that contain this token
 */function flagsForToken(t,groups){const result={};for(const c in groups)groups[c].indexOf(t)>=0&&(result[c]=!0);return result}
/**
 * @template T
 * @typedef {null | T } Transition
 */
/**
 * Define a basic state machine state. j is the list of character transitions,
 * jr is the list of regex-match transitions, jd is the default state to
 * transition to t is the accepting token type, if any. If this is the terminal
 * state, then it does not emit a token.
 *
 * The template type T represents the type of the token this state accepts. This
 * should be a string (such as of the token exports in `text.js`) or a
 * MultiToken subclass (from `multi.js`)
 *
 * @template T
 * @param {T} [token] Token that this state emits
 */function State(token){void 0===token&&(token=null),
// this.n = null; // DEBUG: State name
/** @type {{ [input: string]: State<T> }} j */
this.j={},// IMPLEMENTATION 1
// this.j = []; // IMPLEMENTATION 2
/** @type {[RegExp, State<T>][]} jr */
this.jr=[],
/** @type {?State<T>} jd */
this.jd=null,
/** @type {?T} t */
this.t=token}
/**
 * Scanner token groups
 * @type Collections<string>
 */State.groups={},State.prototype={accepts(){return!!this.t},
/**
   * Follow an existing transition from the given input to the next state.
   * Does not mutate.
   * @param {string} input character or token type to transition on
   * @returns {?State<T>} the next state, if any
   */
go(input){const state=this,nextState=state.j[input];if(nextState)return nextState;for(let i=0;i<state.jr.length;i++){const regex=state.jr[i][0],nextState=state.jr[i][1];// note: might be empty to prevent default jump
if(nextState&&regex.test(input))return nextState}
// Nowhere left to jump! Return default, if any
return state.jd},
/**
   * Whether the state has a transition for the given input. Set the second
   * argument to true to only look for an exact match (and not a default or
   * regular-expression-based transition)
   * @param {string} input
   * @param {boolean} exactOnly
   */
has(input,exactOnly){return void 0===exactOnly&&(exactOnly=!1),exactOnly?input in this.j:!!this.go(input)},
/**
   * Short for "transition all"; create a transition from the array of items
   * in the given list to the same final resulting state.
   * @param {string | string[]} inputs Group of inputs to transition on
   * @param {Transition<T> | State<T>} [next] Transition options
   * @param {Flags} [flags] Collections flags to add token to
   * @param {Collections<T>} [groups] Master list of token groups
   */
ta(inputs,next,flags,groups){for(let i=0;i<inputs.length;i++)this.tt(inputs[i],next,flags,groups)},
/**
   * Short for "take regexp transition"; defines a transition for this state
   * when it encounters a token which matches the given regular expression
   * @param {RegExp} regexp Regular expression transition (populate first)
   * @param {T | State<T>} [next] Transition options
   * @param {Flags} [flags] Collections flags to add token to
   * @param {Collections<T>} [groups] Master list of token groups
   * @returns {State<T>} taken after the given input
   */
tr(regexp,next,flags,groups){let nextState;return groups=groups||State.groups,next&&next.j?nextState=next:(
// Token with maybe token groups
nextState=new State(next),flags&&groups&&addToGroups(next,flags,groups)),this.jr.push([regexp,nextState]),nextState},
/**
   * Short for "take transitions", will take as many sequential transitions as
   * the length of the given input and returns the
   * resulting final state.
   * @param {string | string[]} input
   * @param {T | State<T>} [next] Transition options
   * @param {Flags} [flags] Collections flags to add token to
   * @param {Collections<T>} [groups] Master list of token groups
   * @returns {State<T>} taken after the given input
   */
ts(input,next,flags,groups){let state=this;const len=input.length;if(!len)return state;for(let i=0;i<len-1;i++)state=state.tt(input[i]);return state.tt(input[len-1],next,flags,groups)},
/**
   * Short for "take transition", this is a method for building/working with
   * state machines.
   *
   * If a state already exists for the given input, returns it.
   *
   * If a token is specified, that state will emit that token when reached by
   * the linkify engine.
   *
   * If no state exists, it will be initialized with some default transitions
   * that resemble existing default transitions.
   *
   * If a state is given for the second argument, that state will be
   * transitioned to on the given input regardless of what that input
   * previously did.
   *
   * Specify a token group flags to define groups that this token belongs to.
   * The token will be added to corresponding entires in the given groups
   * object.
   *
   * @param {string} input character, token type to transition on
   * @param {T | State<T>} [next] Transition options
   * @param {Flags} [flags] Collections flags to add token to
   * @param {Collections<T>} [groups] Master list of groups
   * @returns {State<T>} taken after the given input
   */
tt(input,next,flags,groups){groups=groups||State.groups;const state=this;
// Check if existing state given, just a basic transition
if(next&&next.j)return state.j[input]=next,next;const t=next;
// Take the transition with the usual default mechanisms and use that as
// a template for creating the next state
let nextState,templateState=state.go(input);if(templateState?(nextState=new State,assign(nextState.j,templateState.j),nextState.jr.push.apply(nextState.jr,templateState.jr),nextState.jd=templateState.jd,nextState.t=templateState.t):nextState=new State,t){
// Ensure newly token is in the same groups as the old token
if(groups)if(nextState.t&&"string"===typeof nextState.t){const allFlags=assign(flagsForToken(nextState.t,groups),flags);addToGroups(t,allFlags,groups)}else flags&&addToGroups(t,flags,groups);nextState.t=t}return state.j[input]=nextState,nextState}};
// Helper functions to improve minification (not exported outside linkifyjs module)
/**
 * @template T
 * @param {State<T>} state
 * @param {string | string[]} input
 * @param {Flags} [flags]
 * @param {Collections<T>} [groups]
 */
const ta=(state,input,next,flags,groups)=>state.ta(input,next,flags,groups)
/**
 * @template T
 * @param {State<T>} state
 * @param {RegExp} regexp
 * @param {T | State<T>} [next]
 * @param {Flags} [flags]
 * @param {Collections<T>} [groups]
 */,tr=(state,regexp,next,flags,groups)=>state.tr(regexp,next,flags,groups)
/**
 * @template T
 * @param {State<T>} state
 * @param {string | string[]} input
 * @param {T | State<T>} [next]
 * @param {Flags} [flags]
 * @param {Collections<T>} [groups]
 */,ts=(state,input,next,flags,groups)=>state.ts(input,next,flags,groups)
/**
 * @template T
 * @param {State<T>} state
 * @param {string} input
 * @param {T | State<T>} [next]
 * @param {Collections<T>} [groups]
 * @param {Flags} [flags]
 */,tt=(state,input,next,flags,groups)=>state.tt(input,next,flags,groups)
/******************************************************************************
Text Tokens
Identifiers for token outputs from the regexp scanner
******************************************************************************/
// A valid web domain token
,WORD="WORD",UWORD="UWORD",LOCALHOST="LOCALHOST",TLD="TLD",UTLD="UTLD",SCHEME="SCHEME",SLASH_SCHEME="SLASH_SCHEME",NUM="NUM",WS="WS",NL$1="NL",OPENBRACE="OPENBRACE",OPENBRACKET="OPENBRACKET",OPENANGLEBRACKET="OPENANGLEBRACKET",OPENPAREN="OPENPAREN",CLOSEBRACE="CLOSEBRACE",CLOSEBRACKET="CLOSEBRACKET",CLOSEANGLEBRACKET="CLOSEANGLEBRACKET",CLOSEPAREN="CLOSEPAREN",AMPERSAND="AMPERSAND",APOSTROPHE="APOSTROPHE",ASTERISK="ASTERISK",AT="AT",BACKSLASH="BACKSLASH",BACKTICK="BACKTICK",CARET="CARET",COLON="COLON",COMMA="COMMA",DOLLAR="DOLLAR",DOT="DOT",EQUALS="EQUALS",EXCLAMATION="EXCLAMATION",HYPHEN="HYPHEN",PERCENT="PERCENT",PIPE="PIPE",PLUS="PLUS",POUND="POUND",QUERY="QUERY",QUOTE="QUOTE",SEMI="SEMI",SLASH="SLASH",TILDE="TILDE",UNDERSCORE="UNDERSCORE",EMOJI$1="EMOJI",SYM="SYM";var tk=Object.freeze({__proto__:null,WORD:WORD,UWORD:UWORD,LOCALHOST:LOCALHOST,TLD:TLD,UTLD:UTLD,SCHEME:SCHEME,SLASH_SCHEME:SLASH_SCHEME,NUM:NUM,WS:WS,NL:NL$1,OPENBRACE:OPENBRACE,OPENBRACKET:OPENBRACKET,OPENANGLEBRACKET:OPENANGLEBRACKET,OPENPAREN:OPENPAREN,CLOSEBRACE:CLOSEBRACE,CLOSEBRACKET:CLOSEBRACKET,CLOSEANGLEBRACKET:CLOSEANGLEBRACKET,CLOSEPAREN:CLOSEPAREN,AMPERSAND:AMPERSAND,APOSTROPHE:APOSTROPHE,ASTERISK:ASTERISK,AT:AT,BACKSLASH:BACKSLASH,BACKTICK:BACKTICK,CARET:CARET,COLON:COLON,COMMA:COMMA,DOLLAR:DOLLAR,DOT:DOT,EQUALS:EQUALS,EXCLAMATION:EXCLAMATION,HYPHEN:HYPHEN,PERCENT:PERCENT,PIPE:PIPE,PLUS:PLUS,POUND:POUND,QUERY:QUERY,QUOTE:QUOTE,SEMI:SEMI,SLASH:SLASH,TILDE:TILDE,UNDERSCORE:UNDERSCORE,EMOJI:EMOJI$1,SYM:SYM});
// Note that these two Unicode ones expand into a really big one with Babel
const ASCII_LETTER=/[a-z]/,LETTER=/\p{L}/u,EMOJI=/\p{Emoji}/u,DIGIT=/\d/,SPACE=/\s/;
/**
	The scanner provides an interface that takes a string of text as input, and
	outputs an array of tokens instances that can be used for easy URL parsing.
*/
const NL="\n",EMOJI_VARIATION="️",EMOJI_JOINER="‍";// New line character
// zero-width joiner
let tlds=null,utlds=null;// don't change so only have to be computed once
/**
 * Scanner output token:
 * - `t` is the token name (e.g., 'NUM', 'EMOJI', 'TLD')
 * - `v` is the value of the token (e.g., '123', '❤️', 'com')
 * - `s` is the start index of the token in the original string
 * - `e` is the end index of the token in the original string
 * @typedef {{t: string, v: string, s: number, e: number}} Token
 */
/**
 * @template T
 * @typedef {{ [collection: string]: T[] }} Collections
 */
/**
 * Initialize the scanner character-based state machine for the given start
 * state
 * @param {[string, boolean][]} customSchemes List of custom schemes, where each
 * item is a length-2 tuple with the first element set to the string scheme, and
 * the second element set to `true` if the `://` after the scheme is optional
 */function init$2(customSchemes){void 0===customSchemes&&(customSchemes=[]);
// Frequently used states (name argument removed during minification)
/** @type Collections<string> */const groups={};// of tokens
State.groups=groups;
/** @type State<string> */
const Start=new State;null==tlds&&(tlds=decodeTlds(encodedTlds)),null==utlds&&(utlds=decodeTlds(encodedUtlds)),
// States for special URL symbols that accept immediately after start
tt(Start,"'",APOSTROPHE),tt(Start,"{",OPENBRACE),tt(Start,"[",OPENBRACKET),tt(Start,"<",OPENANGLEBRACKET),tt(Start,"(",OPENPAREN),tt(Start,"}",CLOSEBRACE),tt(Start,"]",CLOSEBRACKET),tt(Start,">",CLOSEANGLEBRACKET),tt(Start,")",CLOSEPAREN),tt(Start,"&",AMPERSAND),tt(Start,"*",ASTERISK),tt(Start,"@",AT),tt(Start,"`",BACKTICK),tt(Start,"^",CARET),tt(Start,":",COLON),tt(Start,",",COMMA),tt(Start,"$",DOLLAR),tt(Start,".",DOT),tt(Start,"=",EQUALS),tt(Start,"!",EXCLAMATION),tt(Start,"-",HYPHEN),tt(Start,"%",PERCENT),tt(Start,"|",PIPE),tt(Start,"+",PLUS),tt(Start,"#",POUND),tt(Start,"?",QUERY),tt(Start,'"',QUOTE),tt(Start,"/",SLASH),tt(Start,";",SEMI),tt(Start,"~",TILDE),tt(Start,"_",UNDERSCORE),tt(Start,"\\",BACKSLASH);const Num=tr(Start,DIGIT,NUM,{[numeric]:!0});tr(Num,DIGIT,Num);
// State which emits a word token
const Word=tr(Start,ASCII_LETTER,WORD,{[ascii]:!0});tr(Word,ASCII_LETTER,Word);
// Same as previous, but specific to non-fsm.ascii alphabet words
const UWord=tr(Start,LETTER,UWORD,{[alpha]:!0});tr(UWord,ASCII_LETTER),// Non-accepting
tr(UWord,LETTER,UWord);
// Whitespace jumps
// Tokens of only non-newline whitespace are arbitrarily long
// If any whitespace except newline, more whitespace!
const Ws=tr(Start,SPACE,WS,{[whitespace]:!0});tt(Start,NL,NL$1,{[whitespace]:!0}),tt(Ws,NL),// non-accepting state to avoid mixing whitespaces
tr(Ws,SPACE,Ws);
// Emoji tokens. They are not grouped by the scanner except in cases where a
// zero-width joiner is present
const Emoji=tr(Start,EMOJI,EMOJI$1,{[emoji]:!0});tr(Emoji,EMOJI,Emoji),tt(Emoji,EMOJI_VARIATION,Emoji);
// tt(Start, EMOJI_VARIATION, Emoji); // This one is sketchy
const EmojiJoiner=tt(Emoji,EMOJI_JOINER);tr(EmojiJoiner,EMOJI,Emoji);
// tt(EmojiJoiner, EMOJI_VARIATION, Emoji); // also sketchy
// Generates states for top-level domains
// Note that this is most accurate when tlds are in alphabetical order
const wordjr=[[ASCII_LETTER,Word]],uwordjr=[[ASCII_LETTER,null],[LETTER,UWord]];for(let i=0;i<tlds.length;i++)fastts(Start,tlds[i],TLD,WORD,wordjr);for(let i=0;i<utlds.length;i++)fastts(Start,utlds[i],UTLD,UWORD,uwordjr);addToGroups(TLD,{tld:!0,ascii:!0},groups),addToGroups(UTLD,{utld:!0,alpha:!0},groups),
// Collect the states generated by different protocols. NOTE: If any new TLDs
// get added that are also protocols, set the token to be the same as the
// protocol to ensure parsing works as expected.
fastts(Start,"file",SCHEME,WORD,wordjr),fastts(Start,"mailto",SCHEME,WORD,wordjr),fastts(Start,"http",SLASH_SCHEME,WORD,wordjr),fastts(Start,"https",SLASH_SCHEME,WORD,wordjr),fastts(Start,"ftp",SLASH_SCHEME,WORD,wordjr),fastts(Start,"ftps",SLASH_SCHEME,WORD,wordjr),addToGroups(SCHEME,{scheme:!0,ascii:!0},groups),addToGroups(SLASH_SCHEME,{slashscheme:!0,ascii:!0},groups),
// Register custom schemes. Assumes each scheme is asciinumeric with hyphens
customSchemes=customSchemes.sort(((a,b)=>a[0]>b[0]?1:-1));for(let i=0;i<customSchemes.length;i++){const sch=customSchemes[i][0],optionalSlashSlash=customSchemes[i][1],flags=optionalSlashSlash?{[scheme]:!0}:{[slashscheme]:!0};sch.indexOf("-")>=0?flags[domain]=!0:ASCII_LETTER.test(sch)?DIGIT.test(sch)?flags[asciinumeric]=!0:flags[ascii]=!0:flags[numeric]=!0,ts(Start,sch,sch,flags)}
// Localhost token
return ts(Start,"localhost",LOCALHOST,{ascii:!0}),
// Set default transition for start state (some symbol)
Start.jd=new State(SYM),{start:Start,tokens:assign({groups:groups},tk)}}
/**
	Given a string, returns an array of TOKEN instances representing the
	composition of that string.

	@method run
	@param {State<string>} start scanner starting state
	@param {string} str input string to scan
	@return {Token[]} list of tokens, each with a type and value
*/function run$1(start,str){
// State machine is not case sensitive, so input is tokenized in lowercased
// form (still returns regular case). Uses selective `toLowerCase` because
// lowercasing the entire string causes the length and character position to
// vary in some non-English strings with V8-based runtimes.
const iterable=stringToArray(str.replace(/[A-Z]/g,(c=>c.toLowerCase()))),charCount=iterable.length,tokens=[];// return value
// cursor through the string itself, accounting for characters that have
// width with length 2 such as emojis
let cursor=0,charCursor=0;
// Cursor through the array-representation of the string
// Tokenize the string
while(charCursor<charCount){let state=start,nextState=null,tokenLength=0,latestAccepting=null,sinceAccepts=-1,charsSinceAccepts=-1;while(charCursor<charCount&&(nextState=state.go(iterable[charCursor])))state=nextState,
// Keep track of the latest accepting state
state.accepts()?(sinceAccepts=0,charsSinceAccepts=0,latestAccepting=state):sinceAccepts>=0&&(sinceAccepts+=iterable[charCursor].length,charsSinceAccepts++),tokenLength+=iterable[charCursor].length,cursor+=iterable[charCursor].length,charCursor++;
// Roll back to the latest accepting state
cursor-=sinceAccepts,charCursor-=charsSinceAccepts,tokenLength-=sinceAccepts,
// No more jumps, just make a new token from the last accepting one
tokens.push({t:latestAccepting.t,
// token type/name
v:str.slice(cursor-tokenLength,cursor),
// string value
s:cursor-tokenLength,
// start index
e:cursor})}return tokens}
/**
 * Convert a String to an Array of characters, taking into account that some
 * characters like emojis take up two string indexes.
 *
 * Adapted from core-js (MIT license)
 * https://github.com/zloirock/core-js/blob/2d69cf5f99ab3ea3463c395df81e5a15b68f49d9/packages/core-js/internals/string-multibyte.js
 *
 * @function stringToArray
 * @param {string} str
 * @returns {string[]}
 */function stringToArray(str){const result=[],len=str.length;let index=0;while(index<len){let second,first=str.charCodeAt(index),char=first<55296||first>56319||index+1===len||(second=str.charCodeAt(index+1))<56320||second>57343?str[index]:str.slice(index,index+2);// two-index characters
result.push(char),index+=char.length}return result}
/**
 * Fast version of ts function for when transition defaults are well known
 * @param {State<string>} state
 * @param {string} input
 * @param {string} t
 * @param {string} defaultt
 * @param {[RegExp, State<string>][]} jr
 * @returns {State<string>}
 */function fastts(state,input,t,defaultt,jr){let next;const len=input.length;for(let i=0;i<len-1;i++){const char=input[i];state.j[char]?next=state.j[char]:(next=new State(defaultt),next.jr=jr.slice(),state.j[char]=next),state=next}return next=new State(t),next.jr=jr.slice(),state.j[input[len-1]]=next,next}
/**
 * Converts a string of Top-Level Domain names encoded in update-tlds.js back
 * into a list of strings.
 * @param {str} encoded encoded TLDs string
 * @returns {str[]} original TLDs list
 */function decodeTlds(encoded){const words=[],stack=[];let i=0,digits="0123456789";while(i<encoded.length){let popDigitCount=0;while(digits.indexOf(encoded[i+popDigitCount])>=0)popDigitCount++;// encountered some digits, have to pop to go one level up trie
if(popDigitCount>0){words.push(stack.join(""));// whatever preceded the pop digits must be a word
for(let popCount=parseInt(encoded.substring(i,i+popDigitCount),10);popCount>0;popCount--)stack.pop();i+=popDigitCount}else stack.push(encoded[i]),// drop down a level into the trie
i++}return words}
/**
 * An object where each key is a valid DOM Event Name such as `click` or `focus`
 * and each value is an event handler function.
 *
 * https://developer.mozilla.org/en-US/docs/Web/API/Element#events
 * @typedef {?{ [event: string]: Function }} EventListeners
 */
/**
 * All formatted properties required to render a link, including `tagName`,
 * `attributes`, `content` and `eventListeners`.
 * @typedef {{ tagName: any, attributes: {[attr: string]: any}, content: string,
 * eventListeners: EventListeners }} IntermediateRepresentation
 */
/**
 * Specify either an object described by the template type `O` or a function.
 *
 * The function takes a string value (usually the link's href attribute), the
 * link type (`'url'`, `'hashtag`', etc.) and an internal token representation
 * of the link. It should return an object of the template type `O`
 * @template O
 * @typedef {O | ((value: string, type: string, token: MultiToken) => O)} OptObj
 */
/**
 * Specify either a function described by template type `F` or an object.
 *
 * Each key in the object should be a link type (`'url'`, `'hashtag`', etc.). Each
 * value should be a function with template type `F` that is called when the
 * corresponding link type is encountered.
 * @template F
 * @typedef {F | { [type: string]: F}} OptFn
 */
/**
 * Specify either a value with template type `V`, a function that returns `V` or
 * an object where each value resolves to `V`.
 *
 * The function takes a string value (usually the link's href attribute), the
 * link type (`'url'`, `'hashtag`', etc.) and an internal token representation
 * of the link. It should return an object of the template type `V`
 *
 * For the object, each key should be a link type (`'url'`, `'hashtag`', etc.).
 * Each value should either have type `V` or a function that returns V. This
 * function similarly takes a string value and a token.
 *
 * Example valid types for `Opt<string>`:
 *
 * ```js
 * 'hello'
 * (value, type, token) => 'world'
 * { url: 'hello', email: (value, token) => 'world'}
 * ```
 * @template V
 * @typedef {V | ((value: string, type: string, token: MultiToken) => V) | { [type: string]: V | ((value: string, token: MultiToken) => V) }} Opt
 */
/**
 * See available options: https://linkify.js.org/docs/options.html
 * @typedef {{
 * 	defaultProtocol?: string,
 *  events?: OptObj<EventListeners>,
 * 	format?: Opt<string>,
 * 	formatHref?: Opt<string>,
 * 	nl2br?: boolean,
 * 	tagName?: Opt<any>,
 * 	target?: Opt<string>,
 * 	rel?: Opt<string>,
 * 	validate?: Opt<boolean>,
 * 	truncate?: Opt<number>,
 * 	className?: Opt<string>,
 * 	attributes?: OptObj<({ [attr: string]: any })>,
 *  ignoreTags?: string[],
 * 	render?: OptFn<((ir: IntermediateRepresentation) => any)>
 * }} Opts
 */
/**
 * @type Required<Opts>
 */const defaults={defaultProtocol:"http",events:null,format:noop,formatHref:noop,nl2br:!1,tagName:"a",target:null,rel:null,validate:!0,truncate:1/0,className:null,attributes:null,ignoreTags:[],render:null};
/**
 * Utility class for linkify interfaces to apply specified
 * {@link Opts formatting and rendering options}.
 *
 * @param {Opts | Options} [opts] Option value overrides.
 * @param {(ir: IntermediateRepresentation) => any} [defaultRender] (For
 *   internal use) default render function that determines how to generate an
 *   HTML element based on a link token's derived tagName, attributes and HTML.
 *   Similar to render option
 */function Options(opts,defaultRender){void 0===defaultRender&&(defaultRender=null);let o=assign({},defaults);opts&&(o=assign(o,opts instanceof Options?opts.o:opts));
// Ensure all ignored tags are uppercase
const ignoredTags=o.ignoreTags,uppercaseIgnoredTags=[];for(let i=0;i<ignoredTags.length;i++)uppercaseIgnoredTags.push(ignoredTags[i].toUpperCase());
/** @protected */this.o=o,defaultRender&&(this.defaultRender=defaultRender),this.ignoreTags=uppercaseIgnoredTags}function noop(val){return val}Options.prototype={o:defaults,
/**
   * @type string[]
   */
ignoreTags:[],
/**
   * @param {IntermediateRepresentation} ir
   * @returns {any}
   */
defaultRender(ir){return ir},
/**
   * Returns true or false based on whether a token should be displayed as a
   * link based on the user options.
   * @param {MultiToken} token
   * @returns {boolean}
   */
check(token){return this.get("validate",token.toString(),token)},
// Private methods
/**
   * Resolve an option's value based on the value of the option and the given
   * params. If operator and token are specified and the target option is
   * callable, automatically calls the function with the given argument.
   * @template {keyof Opts} K
   * @param {K} key Name of option to use
   * @param {string} [operator] will be passed to the target option if it's a
   * function. If not specified, RAW function value gets returned
   * @param {MultiToken} [token] The token from linkify.tokenize
   * @returns {Opts[K] | any}
   */
get(key,operator,token){const isCallable=null!=operator;let option=this.o[key];return option?("object"===typeof option?(option=token.t in option?option[token.t]:defaults[key],"function"===typeof option&&isCallable&&(option=option(operator,token))):"function"===typeof option&&isCallable&&(option=option(operator,token.t,token)),option):option},
/**
   * @template {keyof Opts} L
   * @param {L} key Name of options object to use
   * @param {string} [operator]
   * @param {MultiToken} [token]
   * @returns {Opts[L] | any}
   */
getObj(key,operator,token){let obj=this.o[key];return"function"===typeof obj&&null!=operator&&(obj=obj(operator,token.t,token)),obj},
/**
   * Convert the given token to a rendered element that may be added to the
   * calling-interface's DOM
   * @param {MultiToken} token Token to render to an HTML element
   * @returns {any} Render result; e.g., HTML string, DOM element, React
   *   Component, etc.
   */
render(token){const ir=token.render(this),renderFn=this.get("render",null,token)||this.defaultRender;// intermediate representation
return renderFn(ir,token.t,token)}};
/******************************************************************************
	Multi-Tokens
	Tokens composed of arrays of TextTokens
******************************************************************************/
/**
 * @param {string} value
 * @param {Token[]} tokens
 */
function MultiToken(value,tokens){this.t="token",this.v=value,this.tk=tokens}
/**
 * Abstract class used for manufacturing tokens of text tokens. That is rather
 * than the value for a token being a small string of text, it's value an array
 * of text tokens.
 *
 * Used for grouping together URLs, emails, hashtags, and other potential
 * creations.
 * @class MultiToken
 * @property {string} t
 * @property {string} v
 * @property {Token[]} tk
 * @abstract
 */
/**
 * Create a new token that can be emitted by the parser state machine
 * @param {string} type readable type of the token
 * @param {object} props properties to assign or override, including isLink = true or false
 * @returns {new (value: string, tokens: Token[]) => MultiToken} new token class
 */
function createTokenClass(type,props){class Token extends MultiToken{constructor(value,tokens){super(value,tokens),this.t=type}}for(const p in props)Token.prototype[p]=props[p];return Token.t=type,Token}
/**
	Represents a list of tokens making up a valid email address
*/MultiToken.prototype={isLink:!1,
/**
   * Return the string this token represents.
   * @return {string}
   */
toString(){return this.v},
/**
   * What should the value for this token be in the `href` HTML attribute?
   * Returns the `.toString` value by default.
   * @param {string} [scheme]
   * @return {string}
  */
toHref(scheme){return this.toString()},
/**
   * @param {Options} options Formatting options
   * @returns {string}
   */
toFormattedString(options){const val=this.toString(),truncate=options.get("truncate",val,this),formatted=options.get("format",val,this);return truncate&&formatted.length>truncate?formatted.substring(0,truncate)+"…":formatted},
/**
   *
   * @param {Options} options
   * @returns {string}
   */
toFormattedHref(options){return options.get("formatHref",this.toHref(options.get("defaultProtocol")),this)},
/**
   * The start index of this token in the original input string
   * @returns {number}
   */
startIndex(){return this.tk[0].s},
/**
   * The end index of this token in the original input string (up to this
   * index but not including it)
   * @returns {number}
   */
endIndex(){return this.tk[this.tk.length-1].e},
/**
  	Returns an object  of relevant values for this token, which includes keys
  	* type - Kind of token ('url', 'email', etc.)
  	* value - Original text
  	* href - The value that should be added to the anchor tag's href
  		attribute
  		@method toObject
  	@param {string} [protocol] `'http'` by default
  */
toObject(protocol){return void 0===protocol&&(protocol=defaults.defaultProtocol),{type:this.t,value:this.toString(),isLink:this.isLink,href:this.toHref(protocol),start:this.startIndex(),end:this.endIndex()}},
/**
   *
   * @param {Options} options Formatting option
   */
toFormattedObject(options){return{type:this.t,value:this.toFormattedString(options),isLink:this.isLink,href:this.toFormattedHref(options),start:this.startIndex(),end:this.endIndex()}},
/**
   * Whether this token should be rendered as a link according to the given options
   * @param {Options} options
   * @returns {boolean}
   */
validate(options){return options.get("validate",this.toString(),this)},
/**
   * Return an object that represents how this link should be rendered.
   * @param {Options} options Formattinng options
   */
render(options){const token=this,href=this.toHref(options.get("defaultProtocol")),formattedHref=options.get("formatHref",href,this),tagName=options.get("tagName",href,token),content=this.toFormattedString(options),attributes={},className=options.get("className",href,token),target=options.get("target",href,token),rel=options.get("rel",href,token),attrs=options.getObj("attributes",href,token),eventListeners=options.getObj("events",href,token);return attributes.href=formattedHref,className&&(attributes.class=className),target&&(attributes.target=target),rel&&(attributes.rel=rel),attrs&&assign(attributes,attrs),{tagName:tagName,attributes:attributes,content:content,eventListeners:eventListeners}}};const Email=createTokenClass("email",{isLink:!0,toHref(){return"mailto:"+this.toString()}}),Text=createTokenClass("text"),Nl=createTokenClass("nl"),Url=createTokenClass("url",{isLink:!0,
/**
  	Lowercases relevant parts of the domain and adds the protocol if
  	required. Note that this will not escape unsafe HTML characters in the
  	URL.
  		@param {string} [scheme] default scheme (e.g., 'https')
  	@return {string} the full href
  */
toHref(scheme){
// Check if already has a prefix scheme
return void 0===scheme&&(scheme=defaults.defaultProtocol),this.hasProtocol()?this.v:`${scheme}://${this.v}`},
/**
   * Check whether this URL token has a protocol
   * @return {boolean}
   */
hasProtocol(){const tokens=this.tk;return tokens.length>=2&&tokens[0].t!==LOCALHOST&&tokens[1].t===COLON}});
/**
	Represents some plain text
*/
/**
	Not exactly parser, more like the second-stage scanner (although we can
	theoretically hotswap the code here with a real parser in the future... but
	for a little URL-finding utility abstract syntax trees may be a little
	overkill).

	URL format: http://en.wikipedia.org/wiki/URI_scheme
	Email format: http://en.wikipedia.org/wiki/EmailAddress (links to RFC in
	reference)

	@module linkify
	@submodule parser
	@main run
*/
const makeState=arg=>new State(arg)
/**
 * Generate the parser multi token-based state machine
 * @param {{ groups: Collections<string> }} tokens
 */;function init$1(_ref){let{groups:groups}=_ref;
// Types of characters the URL can definitely end in
const qsAccepting=groups.domain.concat([AMPERSAND,ASTERISK,AT,BACKSLASH,BACKTICK,CARET,DOLLAR,EQUALS,HYPHEN,NUM,PERCENT,PIPE,PLUS,POUND,SLASH,SYM,TILDE,UNDERSCORE]),qsNonAccepting=[APOSTROPHE,CLOSEANGLEBRACKET,CLOSEBRACE,CLOSEBRACKET,CLOSEPAREN,COLON,COMMA,DOT,EXCLAMATION,OPENANGLEBRACKET,OPENBRACE,OPENBRACKET,OPENPAREN,QUERY,QUOTE,SEMI],localpartAccepting=[AMPERSAND,APOSTROPHE,ASTERISK,BACKSLASH,BACKTICK,CARET,CLOSEBRACE,DOLLAR,EQUALS,HYPHEN,OPENBRACE,PERCENT,PIPE,PLUS,POUND,QUERY,SLASH,SYM,TILDE,UNDERSCORE],Start=makeState(),Localpart=tt(Start,TILDE);
// Types of tokens that can follow a URL and be part of the query string
// but cannot be the very last characters
// Characters that cannot appear in the URL at all should be excluded
// Local part of the email address
ta(Localpart,localpartAccepting,Localpart),ta(Localpart,groups.domain,Localpart);const Domain=makeState(),Scheme=makeState(),SlashScheme=makeState();ta(Start,groups.domain,Domain),// parsed string ends with a potential domain name (A)
ta(Start,groups.scheme,Scheme),// e.g., 'mailto'
ta(Start,groups.slashscheme,SlashScheme),// e.g., 'http'
ta(Domain,localpartAccepting,Localpart),ta(Domain,groups.domain,Domain);const LocalpartAt=tt(Domain,AT);// Local part of the email address plus @
tt(Localpart,AT,LocalpartAt),// close to an email address now
// Local part of an email address can be e.g. 'http' or 'mailto'
tt(Scheme,AT,LocalpartAt),tt(SlashScheme,AT,LocalpartAt);const LocalpartDot=tt(Localpart,DOT);// Local part of the email address plus '.' (localpart cannot end in .)
ta(LocalpartDot,localpartAccepting,Localpart),ta(LocalpartDot,groups.domain,Localpart);const EmailDomain=makeState();ta(LocalpartAt,groups.domain,EmailDomain),// parsed string starts with local email info + @ with a potential domain name
ta(EmailDomain,groups.domain,EmailDomain);const EmailDomainDot=tt(EmailDomain,DOT);// domain followed by DOT
ta(EmailDomainDot,groups.domain,EmailDomain);const Email$1=makeState(Email);// Possible email address (could have more tlds)
ta(EmailDomainDot,groups.tld,Email$1),ta(EmailDomainDot,groups.utld,Email$1),tt(LocalpartAt,LOCALHOST,Email$1);
// Hyphen can jump back to a domain name
const EmailDomainHyphen=tt(EmailDomain,HYPHEN);// parsed string starts with local email info + @ with a potential domain name
ta(EmailDomainHyphen,groups.domain,EmailDomain),ta(Email$1,groups.domain,EmailDomain),tt(Email$1,DOT,EmailDomainDot),tt(Email$1,HYPHEN,EmailDomainHyphen);
// Final possible email states
const EmailColon=tt(Email$1,COLON);// URL followed by colon (potential port number here)
/*const EmailColonPort = */ta(EmailColon,groups.numeric,Email);// URL followed by colon and port numner
// Account for dots and hyphens. Hyphens are usually parts of domain names
// (but not TLDs)
const DomainHyphen=tt(Domain,HYPHEN),DomainDot=tt(Domain,DOT);// domain followed by hyphen
// domain followed by DOT
ta(DomainHyphen,groups.domain,Domain),ta(DomainDot,localpartAccepting,Localpart),ta(DomainDot,groups.domain,Domain);const DomainDotTld=makeState(Url);// Simplest possible URL with no query string
ta(DomainDot,groups.tld,DomainDotTld),ta(DomainDot,groups.utld,DomainDotTld),ta(DomainDotTld,groups.domain,Domain),ta(DomainDotTld,localpartAccepting,Localpart),tt(DomainDotTld,DOT,DomainDot),tt(DomainDotTld,HYPHEN,DomainHyphen),tt(DomainDotTld,AT,LocalpartAt);const DomainDotTldColon=tt(DomainDotTld,COLON),DomainDotTldColonPort=makeState(Url);// URL followed by colon (potential port number here)
// TLD followed by a port number
ta(DomainDotTldColon,groups.numeric,DomainDotTldColonPort);
// Long URL with optional port and maybe query string
const Url$1=makeState(Url),UrlNonaccept=makeState();
// URL with extra symbols at the end, followed by an opening bracket
// URL followed by some symbols (will not be part of the final URL)
// Query strings
ta(Url$1,qsAccepting,Url$1),ta(Url$1,qsNonAccepting,UrlNonaccept),ta(UrlNonaccept,qsAccepting,Url$1),ta(UrlNonaccept,qsNonAccepting,UrlNonaccept),
// Become real URLs after `SLASH` or `COLON NUM SLASH`
// Here works with or without scheme:// prefix
tt(DomainDotTld,SLASH,Url$1),tt(DomainDotTldColonPort,SLASH,Url$1);
// Note that domains that begin with schemes are treated slighly differently
const SchemeColon=tt(Scheme,COLON),SlashSchemeColon=tt(SlashScheme,COLON),SlashSchemeColonSlash=tt(SlashSchemeColon,SLASH),UriPrefix=tt(SlashSchemeColonSlash,SLASH);// e.g., 'mailto:'
// e.g., 'http://'
// Scheme states can transition to domain states
ta(Scheme,groups.domain,Domain),tt(Scheme,DOT,DomainDot),tt(Scheme,HYPHEN,DomainHyphen),ta(SlashScheme,groups.domain,Domain),tt(SlashScheme,DOT,DomainDot),tt(SlashScheme,HYPHEN,DomainHyphen),
// Force URL with scheme prefix followed by anything sane
ta(SchemeColon,groups.domain,Url$1),tt(SchemeColon,SLASH,Url$1),ta(UriPrefix,groups.domain,Url$1),ta(UriPrefix,qsAccepting,Url$1),tt(UriPrefix,SLASH,Url$1);
// URL, followed by an opening bracket
const UrlOpenbrace=tt(Url$1,OPENBRACE),UrlOpenbracket=tt(Url$1,OPENBRACKET),UrlOpenanglebracket=tt(Url$1,OPENANGLEBRACKET),UrlOpenparen=tt(Url$1,OPENPAREN);// URL followed by {
// URL followed by (
tt(UrlNonaccept,OPENBRACE,UrlOpenbrace),tt(UrlNonaccept,OPENBRACKET,UrlOpenbracket),tt(UrlNonaccept,OPENANGLEBRACKET,UrlOpenanglebracket),tt(UrlNonaccept,OPENPAREN,UrlOpenparen),
// Closing bracket component. This character WILL be included in the URL
tt(UrlOpenbrace,CLOSEBRACE,Url$1),tt(UrlOpenbracket,CLOSEBRACKET,Url$1),tt(UrlOpenanglebracket,CLOSEANGLEBRACKET,Url$1),tt(UrlOpenparen,CLOSEPAREN,Url$1),tt(UrlOpenbrace,CLOSEBRACE,Url$1);
// URL that beings with an opening bracket, followed by a symbols.
// Note that the final state can still be `UrlOpenbrace` (if the URL only
// has a single opening bracket for some reason).
const UrlOpenbraceQ=makeState(Url),UrlOpenbracketQ=makeState(Url),UrlOpenanglebracketQ=makeState(Url),UrlOpenparenQ=makeState(Url);// URL followed by { and some symbols that the URL can end it
// URL followed by ( and some symbols that the URL can end it
ta(UrlOpenbrace,qsAccepting,UrlOpenbraceQ),ta(UrlOpenbracket,qsAccepting,UrlOpenbracketQ),ta(UrlOpenanglebracket,qsAccepting,UrlOpenanglebracketQ),ta(UrlOpenparen,qsAccepting,UrlOpenparenQ);const UrlOpenbraceSyms=makeState(),UrlOpenbracketSyms=makeState(),UrlOpenanglebracketSyms=makeState(),UrlOpenparenSyms=makeState();// UrlOpenbrace followed by some symbols it cannot end it
// single new line
// UrlOpenparenQ followed by some symbols it cannot end it
return ta(UrlOpenbrace,qsNonAccepting),ta(UrlOpenbracket,qsNonAccepting),ta(UrlOpenanglebracket,qsNonAccepting),ta(UrlOpenparen,qsNonAccepting),
// URL that begins with an opening bracket, followed by some symbols
ta(UrlOpenbraceQ,qsAccepting,UrlOpenbraceQ),ta(UrlOpenbracketQ,qsAccepting,UrlOpenbracketQ),ta(UrlOpenanglebracketQ,qsAccepting,UrlOpenanglebracketQ),ta(UrlOpenparenQ,qsAccepting,UrlOpenparenQ),ta(UrlOpenbraceQ,qsNonAccepting,UrlOpenbraceQ),ta(UrlOpenbracketQ,qsNonAccepting,UrlOpenbracketQ),ta(UrlOpenanglebracketQ,qsNonAccepting,UrlOpenanglebracketQ),ta(UrlOpenparenQ,qsNonAccepting,UrlOpenparenQ),ta(UrlOpenbraceSyms,qsAccepting,UrlOpenbraceSyms),ta(UrlOpenbracketSyms,qsAccepting,UrlOpenbracketQ),ta(UrlOpenanglebracketSyms,qsAccepting,UrlOpenanglebracketQ),ta(UrlOpenparenSyms,qsAccepting,UrlOpenparenQ),ta(UrlOpenbraceSyms,qsNonAccepting,UrlOpenbraceSyms),ta(UrlOpenbracketSyms,qsNonAccepting,UrlOpenbracketSyms),ta(UrlOpenanglebracketSyms,qsNonAccepting,UrlOpenanglebracketSyms),ta(UrlOpenparenSyms,qsNonAccepting,UrlOpenparenSyms),
// Close brace/bracket to become regular URL
tt(UrlOpenbracketQ,CLOSEBRACKET,Url$1),tt(UrlOpenanglebracketQ,CLOSEANGLEBRACKET,Url$1),tt(UrlOpenparenQ,CLOSEPAREN,Url$1),tt(UrlOpenbraceQ,CLOSEBRACE,Url$1),tt(UrlOpenbracketSyms,CLOSEBRACKET,Url$1),tt(UrlOpenanglebracketSyms,CLOSEANGLEBRACKET,Url$1),tt(UrlOpenparenSyms,CLOSEPAREN,Url$1),tt(UrlOpenbraceSyms,CLOSEPAREN,Url$1),tt(Start,LOCALHOST,DomainDotTld),// localhost is a valid URL state
tt(Start,NL$1,Nl),{start:Start,tokens:tk}}
/**
 * Run the parser state machine on a list of scanned string-based tokens to
 * create a list of multi tokens, each of which represents a URL, email address,
 * plain text, etc.
 *
 * @param {State<MultiToken>} start parser start state
 * @param {string} input the original input used to generate the given tokens
 * @param {Token[]} tokens list of scanned tokens
 * @returns {MultiToken[]}
 */function run(start,input,tokens){let len=tokens.length,cursor=0,multis=[],textTokens=[];while(cursor<len){let state=start,secondState=null,nextState=null,multiLength=0,latestAccepting=null,sinceAccepts=-1;while(cursor<len&&!(secondState=state.go(tokens[cursor].t)))
// Starting tokens with nowhere to jump to.
// Consider these to be just plain text
textTokens.push(tokens[cursor++]);while(cursor<len&&(nextState=secondState||state.go(tokens[cursor].t)))
// Get the next state
secondState=null,state=nextState,
// Keep track of the latest accepting state
state.accepts()?(sinceAccepts=0,latestAccepting=state):sinceAccepts>=0&&sinceAccepts++,cursor++,multiLength++;if(sinceAccepts<0)
// No accepting state was found, part of a regular text token add
// the first text token to the text tokens array and try again from
// the next
cursor-=multiLength,cursor<len&&(textTokens.push(tokens[cursor]),cursor++);else{
// Accepting state!
// First close off the textTokens (if available)
textTokens.length>0&&(multis.push(initMultiToken(Text,input,textTokens)),textTokens=[]),
// Roll back to the latest accepting state
cursor-=sinceAccepts,multiLength-=sinceAccepts;
// Create a new multitoken
const Multi=latestAccepting.t,subtokens=tokens.slice(cursor-multiLength,cursor);multis.push(initMultiToken(Multi,input,subtokens))}}
// Finally close off the textTokens (if available)
return textTokens.length>0&&multis.push(initMultiToken(Text,input,textTokens)),multis}
/**
 * Utility function for instantiating a new multitoken with all the relevant
 * fields during parsing.
 * @param {new (value: string, tokens: Token[]) => MultiToken} Multi class to instantiate
 * @param {string} input original input string
 * @param {Token[]} tokens consecutive tokens scanned from input string
 * @returns {MultiToken}
 */function initMultiToken(Multi,input,tokens){const startIdx=tokens[0].s,endIdx=tokens[tokens.length-1].e,value=input.slice(startIdx,endIdx);return new Multi(value,tokens)}const warn="undefined"!==typeof console&&console&&console.warn||(()=>{}),warnAdvice="until manual call of linkify.init(). Register all schemes and plugins before invoking linkify the first time.",INIT={scanner:null,parser:null,tokenQueue:[],pluginQueue:[],customSchemes:[],initialized:!1};
/**
 * @typedef {{
 * 	start: State<string>,
 * 	tokens: { groups: Collections<string> } & typeof tk
 * }} ScannerInit
 */
/**
 * @typedef {{
 * 	start: State<MultiToken>,
 * 	tokens: typeof multi
 * }} ParserInit
 */
/**
 * @typedef {(arg: { scanner: ScannerInit }) => void} TokenPlugin
 */
/**
 * @typedef {(arg: { scanner: ScannerInit, parser: ParserInit }) => void} Plugin
 */
/**
 * De-register all plugins and reset the internal state-machine. Used for
 * testing; not required in practice.
 * @private
 */
function reset(){State.groups={},INIT.scanner=null,INIT.parser=null,INIT.tokenQueue=[],INIT.pluginQueue=[],INIT.customSchemes=[],INIT.initialized=!1}
/**
 * Register a token plugin to allow the scanner to recognize additional token
 * types before the parser state machine is constructed from the results.
 * @param {string} name of plugin to register
 * @param {TokenPlugin} plugin function that accepts the scanner state machine
 * and available scanner tokens and collections and extends the state machine to
 * recognize additional tokens or groups.
 */
/**
 * Detect URLs with the following additional protocol. Anything with format
 * "protocol://..." will be considered a link. If `optionalSlashSlash` is set to
 * `true`, anything with format "protocol:..." will be considered a link.
 * @param {string} protocol
 * @param {boolean} [optionalSlashSlash]
 */
function registerCustomProtocol(scheme,optionalSlashSlash){if(void 0===optionalSlashSlash&&(optionalSlashSlash=!1),INIT.initialized&&warn(`linkifyjs: already initialized - will not register custom scheme "${scheme}" ${warnAdvice}`),!/^[0-9a-z]+(-[0-9a-z]+)*$/.test(scheme))throw new Error('linkifyjs: incorrect scheme format.\n 1. Must only contain digits, lowercase ASCII letters or "-"\n 2. Cannot start or end with "-"\n 3. "-" cannot repeat');INIT.customSchemes.push([scheme,optionalSlashSlash])}
/**
 * Initialize the linkify state machine. Called automatically the first time
 * linkify is called on a string, but may be called manually as well.
 */function init(){
// Initialize scanner state machine and plugins
INIT.scanner=init$2(INIT.customSchemes);for(let i=0;i<INIT.tokenQueue.length;i++)INIT.tokenQueue[i][1]({scanner:INIT.scanner});
// Initialize parser state machine and plugins
INIT.parser=init$1(INIT.scanner.tokens);for(let i=0;i<INIT.pluginQueue.length;i++)INIT.pluginQueue[i][1]({scanner:INIT.scanner,parser:INIT.parser});INIT.initialized=!0}
/**
 * Parse a string into tokens that represent linkable and non-linkable sub-components
 * @param {string} str
 * @return {MultiToken[]} tokens
 */function tokenize(str){return INIT.initialized||init(),run(INIT.parser.start,str,run$1(INIT.scanner.start,str))}
/**
 * Find a list of linkable items in the given string.
 * @param {string} str string to find links in
 * @param {string | Opts} [type] either formatting options or specific type of
 * links to find, e.g., 'url' or 'email'
 * @param {Opts} [opts] formatting options for final output. Cannot be specified
 * if opts already provided in `type` argument
*/function find(str,type,opts){if(void 0===type&&(type=null),void 0===opts&&(opts=null),type&&"object"===typeof type){if(opts)throw Error(`linkifyjs: Invalid link type ${type}; must be a string`);opts=type,type=null}const options=new Options(opts),tokens=tokenize(str),filtered=[];for(let i=0;i<tokens.length;i++){const token=tokens[i];!token.isLink||type&&token.t!==type||filtered.push(token.toFormattedObject(options))}return filtered}
/**
 * Is the given string valid linkable text of some sort. Note that this does not
 * trim the text for you.
 *
 * Optionally pass in a second `type` param, which is the type of link to test
 * for.
 *
 * For example,
 *
 *     linkify.test(str, 'email');
 *
 * Returns `true` if str is a valid email.
 * @param {string} str string to test for links
 * @param {string} [type] optional specific link type to look for
 * @returns boolean true/false
 */}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/chunk-common-b49fab05.js.map