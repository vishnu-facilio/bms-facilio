"use strict";(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[11448],{
/***/911448:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){__webpack_require__.r(__webpack_exports__),
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */conf:function(){/* binding */return conf},
/* harmony export */language:function(){/* binding */return language}
/* harmony export */});
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
var conf={comments:{lineComment:"//",blockComment:["(*","*)"]},brackets:[["{","}"],["[","]"],["(",")"],["<",">"]],autoClosingPairs:[{open:"{",close:"}"},{open:"[",close:"]"},{open:"(",close:")"},{open:"<",close:">"},{open:"'",close:"'"},{open:'"',close:'"'},{open:"(*",close:"*)"}],surroundingPairs:[{open:"{",close:"}"},{open:"[",close:"]"},{open:"(",close:")"},{open:"<",close:">"},{open:"'",close:"'"},{open:'"',close:'"'},{open:"(*",close:"*)"}]},language={defaultToken:"",tokenPostfix:".cameligo",ignoreCase:!0,brackets:[{open:"{",close:"}",token:"delimiter.curly"},{open:"[",close:"]",token:"delimiter.square"},{open:"(",close:")",token:"delimiter.parenthesis"},{open:"<",close:">",token:"delimiter.angle"}],keywords:["abs","assert","block","Bytes","case","Crypto","Current","else","failwith","false","for","fun","if","in","let","let%entry","let%init","List","list","Map","map","match","match%nat","mod","not","operation","Operation","of","record","Set","set","sender","skip","source","String","then","to","true","type","with"],typeKeywords:["int","unit","string","tz","nat","bool"],operators:["=",">","<","<=",">=","<>",":",":=","and","mod","or","+","-","*","/","@","&","^","%","->","<-","&&","||"],
// we include these common regular expressions
symbols:/[=><:@\^&|+\-*\/\^%]+/,
// The main tokenizer for our languages
tokenizer:{root:[
// identifiers and keywords
[/[a-zA-Z_][\w]*/,{cases:{"@keywords":{token:"keyword.$0"},"@default":"identifier"}}],
// whitespace
{include:"@whitespace"},
// delimiters and operators
[/[{}()\[\]]/,"@brackets"],[/[<>](?!@symbols)/,"@brackets"],[/@symbols/,{cases:{"@operators":"delimiter","@default":""}}],
// numbers
[/\d*\.\d+([eE][\-+]?\d+)?/,"number.float"],[/\$[0-9a-fA-F]{1,16}/,"number.hex"],[/\d+/,"number"],
// delimiter: after number because of .\d floats
[/[;,.]/,"delimiter"],
// strings
[/'([^'\\]|\\.)*$/,"string.invalid"],[/'/,"string","@string"],
// characters
[/'[^\\']'/,"string"],[/'/,"string.invalid"],[/\#\d+/,"string"]],

comment:[[/[^\(\*]+/,"comment"],
//[/\(\*/,    'comment', '@push' ],    // nested comment  not allowed :-(
[/\*\)/,"comment","@pop"],[/\(\*/,"comment"]],string:[[/[^\\']+/,"string"],[/\\./,"string.escape.invalid"],[/'/,{token:"string.quote",bracket:"@close",next:"@pop"}]],whitespace:[[/[ \t\r\n]+/,"white"],[/\(\*/,"comment","@comment"],[/\/\/.*$/,"comment"]]}}}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/11448.js.map