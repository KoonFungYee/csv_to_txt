/**
 * 公用的js函数文件
 */

/**
 * 伪造http referer信息
 * 用 document.all 来判断当前的浏览器是否是IE， 如果是的话就生成一个link，
 * 然后自动执行 onclick 事件，如果不是的话就用JS 跳转。这样在处理页面就可以得到 HTTP_REFERER
 * @param url
 */
function referURL(url){
    var isIe=(document.all)?true:false;
    //console.info("isIe:"+isIe);
    if(isIe) {
        var linka = document.createElement('a');
        linka.href=url;
        document.body.appendChild(linka);
        linka.click();
    }
}

/**
 * 唯一标识 指定长度和基数
 */
function generateUUID(len, radix) {
    var timeData = new Date().getTime();
    var chars = ('0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz').split('');
    var uuid = [], i;
    radix = radix || chars.length;

    if (len) {
        // Compact form
        for (i = 0; i < len; i++) uuid[i] = chars[0 | (Math.random()*radix)];
    } else {
        // rfc4122, version 4 form
        var r;

        // rfc4122 requires these characters
        uuid[8] = uuid[13] = uuid[18] = uuid[23] = '-';
        uuid[14] = '4';

        // Fill in random data.  At i==19 set the high bits of clock sequence as
        // per rfc4122, sec. 4.1.5
        for (i = 0; i < 36; i++) {
            if (!uuid[i]) {
                r = 0 | (Math.random()*16);
                uuid[i] = chars[(i == 19) ? (r & 0x3) | 0x8 : r];
            }
        }
    }
    return uuid.join('');
}

/**
 * GUID是一种由算法生成的二进制长度为128位的数字标识符。
 * GUID 的格式为“xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx”，
 * 其中的 x 是 0-9 或 a-f 范围内的一个32位十六进制数。在理想情况下，任何计算机和计算机集群都不会生成两个相同的GUID。
 * @returns {string}
 */
function uuid() {
    var d = new Date().getTime();
    //var uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
    var uuid = 'xxxx-yxxx'.replace(/[xy]/g, function(c) {
        var r = (d + Math.random()*16)%16 | 0;
        return (c=='x' ? r : (r&0x3|0x8)).toString(16);
    });
    return uuid;
};
//货币格式化
//Extend the default Number object with a formatMoney() method:
//usage: someVar.formatMoney(decimalPlaces, symbol, thousandsSeparator, decimalSeparator)
//defaults: (2, "$", ",", ".")
Number.prototype.formatMoney = function (places, symbol, thousand, decimal) {
 places = !isNaN(places = Math.abs(places)) ? places : 2;
 symbol = symbol !== undefined ? symbol : "$";
 thousand = thousand || ",";
 decimal = decimal || ".";
 var number = this,
     negative = number < 0 ? "-" : "",
     i = parseInt(number = Math.abs(+number || 0).toFixed(places), 10) + "",
     j = (j = i.length) > 3 ? j % 3 : 0;
 return symbol + negative + (j ? i.substr(0, j) + thousand : "") + i.substr(j).replace(/(\d{3})(?=\d)/g, "$1" + thousand) + (places ? decimal + Math.abs(number - i).toFixed(places).slice(2) : "");
};

/**
 * 判断是否登录，没登录刷新当前页，促使Shiro拦截后跳转登录页
 * @param result    ajax请求返回的值
 * @returns {如果没登录，刷新当前页}
 */
function isLogin(result){
    if(result && result.code && (result.code == '1101' || result.code=='1102')){
        window.location.reload(true);//刷新当前页
    }
    return true;//返回true
}

/**
 * 获取get请求参数
 * @param name
 * @returns
 */
function GetQueryString(name){
    var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
    var search=window.location.search;
    if(search!=null && search!=""){
        var r = search.substr(1).match(reg);
        if(r!=null){
            return  unescape(r[2]);
        }
    }
    return null;
}
/**
 * 获取菜单uri
 * @returns
 */
function getCallback(){
    var pathname = window.location.pathname;
    var param=GetQueryString("callback");
    //console.log("pathname:"+pathname);
    //console.log("param:"+param);
    if(param!=null && param != ""){
        return param;
    }else{
        return pathname;
    }
}


/**
 * 针对不同的错误可结合业务自定义处理方式
 * @param result
 * @returns {Boolean}
 */
function isError(result){
    var flag=true;
    if(result && result.status){
        flag=false;
        if(result.status == '-1' || result.status=='-101' || result.status=='400' || result.status=='404' || result.status=='500'){
            layer.alert(result.data);
        }else if(result.status=='403'){
            layer.alert(result.data,function(){
                //跳转到未授权界面
                window.location.href="/403";
            });
        }
    }
    return flag;//返回true
}
// chai改 sit:http://172.16.10.70:8094
/*var ossBaseView = 'http://172.16.10.70:8094/oss/api/entry/view/v1/?';
var ossBaseDownload = 'http://172.16.10.70:8094/oss/api/entry/downloadfile/v1/?';*/

var ossBaseView, ossBaseDownload;
var ossBase = sessionStorage.getItem('ossBase');

var userData = function () {
    return JSON.parse(sessionStorage.getItem("userData"));
}

var orgId = function(){
    return userData()["orgId"];
}

var loginId = function(){
    return userData()["loginAccountId"];
}


var loginName = function () {
    return userData()["loginName"];
}

var userName = function () {
    return userData()["name"];
}

var factoryTypeEnum = function (type) {
    switch (type) {
        case "BRAND":
            return "品牌厂商";
        case "BIG":
            return "大商户";
        case  "AGENT":
            return "代理商"
        default :
            return "";
    }
}

var industryNatureTypeEnum = function (type) {
    switch (type) {
        case "PLATFORM":
            return "平台";
        case "INSURANCE":
            return "保险";
        case  "ASSURE":
            return "担保";
        case  "OTHER":
            return "其他";
        default :
            return "";
    }
}

var accountNatureTypeEnum = function (type) {
    switch (type) {
        case "BASIC":
            return "基本户";
        case "NORMAL":
            return "一般户";
        case  "OTHER":
            return "其他";
        default :
            return "";
    }
}

var removeUserData = function () {
    if (!userData()){
        return;
    }
    sessionStorage.removeItem("userData");
}

layui.define(['layer'], function(exports) {
    "use strict";

    var $ = layui.jquery,
        layer = layui.layer;

    if(!ossBase){
        $.ajax({
            type:"get",
            url:"../../base/oss/url",
            dataType:"json",
            success: function (res) {
                let {code,data,message} = res;
                if (code == 200) {
                    sessionStorage.setItem('ossBase', data);
                    ossBaseView = data + 'view/v1/?';
                    ossBaseDownload = data + 'downloadfile/v1/?';
                } else {
                    console.log(code, message);
                }
            }
        });
    }else{
        ossBaseView = ossBase + 'view/v1/?';
        ossBaseDownload = ossBase + 'downloadfile/v1/?';
    }

    var common = {
        /**
         * 抛出一个异常错误信息
         * @param {String} msg
         */
        throwError: function(msg) {
            throw new Error(msg);
            return;
        },
        /**
         * 弹出一个错误提示
         * @param {String} msg
         */
        msgError: function(msg) {
            layer.msg(msg, {
                icon: 5
            });
            return;
        }
    };

    exports('common', common);
});
