"use strict";(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[14188],{
/***/914188:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){__webpack_require__.r(__webpack_exports__),
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */conf:function(){/* binding */return conf},
/* harmony export */language:function(){/* binding */return language}
/* harmony export */});
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
var conf={comments:{blockComment:["/*","*/"],lineComment:"//"},brackets:[["{","}"],["[","]"],["(",")"]],autoClosingPairs:[{open:"{",close:"}",notIn:["string"]},{open:"[",close:"]",notIn:["string"]},{open:"(",close:")",notIn:["string"]},{open:'"',close:'"',notIn:["string"]},{open:"'",close:"'",notIn:["string"]}],surroundingPairs:[{open:"{",close:"}"},{open:"[",close:"]"},{open:"(",close:")"},{open:'"',close:'"'},{open:"'",close:"'"},{open:"<",close:">"}]},language={defaultToken:"",tokenPostfix:".flow",keywords:["import","require","export","forbid","native","if","else","cast","unsafe","switch","default"],types:["io","mutable","bool","int","double","string","flow","void","ref","true","false","with"],operators:["=",">","<","<=",">=","==","!","!=",":=","::=","&&","||","+","-","*","/","@","&","%",":","->","\\","$","??","^"],symbols:/[@$=><!~?:&|+\-*\\\/\^%]+/,escapes:/\\(?:[abfnrtv\\"']|x[0-9A-Fa-f]{1,4}|u[0-9A-Fa-f]{4}|U[0-9A-Fa-f]{8})/,
// The main tokenizer for our languages
tokenizer:{root:[
// identifiers and keywords
[/[a-zA-Z_]\w*/,{cases:{"@keywords":"keyword","@types":"type","@default":"identifier"}}],
// whitespace
{include:"@whitespace"},
// delimiters and operators
[/[{}()\[\]]/,"delimiter"],[/[<>](?!@symbols)/,"delimiter"],[/@symbols/,{cases:{"@operators":"delimiter","@default":""}}],
// numbers
[/((0(x|X)[0-9a-fA-F]*)|(([0-9]+\.?[0-9]*)|(\.[0-9]+))((e|E)(\+|-)?[0-9]+)?)/,"number"],
// delimiter: after number because of .\d floats
[/[;,.]/,"delimiter"],
// strings
[/"([^"\\]|\\.)*$/,"string.invalid"],[/"/,"string","@string"]],whitespace:[[/[ \t\r\n]+/,""],[/\/\*/,"comment","@comment"],[/\/\/.*$/,"comment"]],comment:[[/[^\/*]+/,"comment"],[/\*\//,"comment","@pop"],[/[\/*]/,"comment"]],string:[[/[^\\"]+/,"string"],[/@escapes/,"string.escape"],[/\\./,"string.escape.invalid"],[/"/,"string","@pop"]]}}}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/14188.js.map