$(window).keydown(function (event) {
    // var key = window.event?e.keyCode:e.which;
    // if(key.toString() == "13"){
    //     return false;
    // }
    var target, code, tag;
    if (!event) {
        event = window.event; //针对ie浏览器
        target = event.srcElement;
        code = event.keyCode;
        if (code == 13) {
            tag = target.tagName;
            if (tag == "TEXTAREA") {
                return true;
            }
            else {
                return false;
            }
        }
    }
    else {
        target = event.target; //针对遵循w3c标准的浏览器，如Firefox
        code = event.keyCode;
        if (code == 13) {
            tag = target.tagName;
            if (tag == "INPUT") {
                return false;
            }
            else {
                return true;
            }
        }
    }

});


//var domain = 'http://192.168.10.96:8087/cloud/'
var domain = '/cloud/'

function ajax(option) {
    $(document).unbind('ajaxStart')
    $(document).ajaxStart(function () {
        layer.load()
    })
    var url = option.url;


    $.ajax({
        url: url,
        type: option.type || 'get',
        cache: false,
        processData: option.processData,
        contentType: option.contentType,
        // headers: {
        // 	token: $.cookie('uuid'),
        // 	'Menu-Url': menuUrl
        // },
        dataType: 'json',
        data: option.data,
        timeout: option.timeout || 60 * 1000,
        beforeSend: function (xhr, settings) {
            option.beforeSend && option.beforeSend(xhr, settings)
        },
        success: function (res) {
            layer.closeAll();
            if (res.result === -2) {
                layer.open({
                    type: 0,
                    closeBtn: 0,
                    title: '提示',
                    icon: 7,
                    content: '您没有权限'
                })
            } else if (res.result === 1) {
                layer.open({
                    type: 0,
                    closeBtn: 0,
                    title: '提示',
                    icon: 7,
                    content: res.message
                })
            } else if (res.result === 0) {
                option.success && option.success(res)
            } else {
                layer.open({
                    type: 0,
                    closeBtn: 0,
                    title: '提示',
                    icon: 7,
                    content: '出现错误'
                })
            }
        },
        error: function (xhr, status) {
            layer.closeAll();
            if (xhr.status === 401) {
                layer.msg("长时间未操作，请重新登录！")
                //window.location.href = "/login.html"
                parent.location.href = "/login.html"
            } else {
                option.error ? option.error(xhr, status) : errorFunc(xhr, status)
            }
        },
    })
}

// Date对象格式化
Date.prototype.Format = function (fmt) { //author: meizz
    var o = {
        "M+": this.getMonth() + 1, //月份
        "d+": this.getDate(), //日
        "h+": this.getHours(), //小时
        "m+": this.getMinutes(), //分
        "s+": this.getSeconds(), //秒
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度
        "S": this.getMilliseconds() //毫秒
    };
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
        if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}

//将时间戳转换为'yyyy-MM-dd hh:mm:ss'形式
function timeFormat(time) {
    time = (time && time.toString().length == 10) ? time * 1000 : time //若是10位，则转换为13位
    return new Date(time).Format('yyyy-MM-dd hh:mm:ss')
}

function dateFormat(time) {
    time = (time && time.toString().length == 10) ? time * 1000 : time //若是10位，则转换为13位
    return new Date(time).Format('yyyy-MM-dd')
}


function dateFormat2(time, format) {
    time = (time && time.toString().length == 10) ? time * 1000 : time //若是10位，则转换为13位
    return new Date(time).Format(format)
}

//取得10位时间戳
function getTime10(timeStr) {
    var tempDate = new Date(timeStr)
    tempDate.setHours(0)
    return Date.parse(tempDate) / 1000
}

Array.prototype.remove = function (val) {
    var index = this.indexOf(val)
    if (index > -1) {
        this.splice(index, 1);
        return val;
    }
    return null
}

function removeRepeat(array) {
    var resultArray = []
    for (var i = 0; i < array.length; i++) {
        if (resultArray.indexOf(array[i]) == -1) {
            resultArray.push(array[i]);
        }
    }
    return resultArray;
}

var authLevelStatus = ["一级未审核", "一级审核拒绝", "一级审核通过", "二级未审核", "二级审核拒绝", "二级审核通过", "excel或手工添加"];


function relogin() {
    sessionStorage.removeItem('uuid')
    sessionStorage.removeItem('login')
    sessionStorage.removeItem('username')
    top.layer.open({
        type: 0,
        closeBtn: 0,
        icon: 7,
        title: '重新登录',
        content: '超过30分钟未操作，已自动登出，请重新登录',
        yes: function () {
            top.open('login.html', '_self')
        }
    })
}

function showNodata() {
    $('#nodata').show()
    $('tbody').children().remove()
    $('#data-num').text(0)
}

//隐藏“暂无数据”的图片
function hideNodata() {
    $('#nodata').hide();
}

$('input').on('input', function () {
    var val = $(this).val()
    var jsReg = /.*(<script>).*(<\/script>)*.*/i
    if (jsReg.test(val)) {
        $(this).val('')
    }
})

//Ajax error函数
function errorFunc(xhr, status) {
    layer.open({
        type: 0,
        closeBtn: 0,
        icon: 7,
        title: '出现错误',
        content: status
    })
}


//查询到没有数据时显示“暂无数据”的图片
function showNodata() {
    $('#nodata').show();
    $('tbody').children().remove();
    $('#data-num').text(0);
}

//隐藏“暂无数据”的图片
function hideNodata() {
    $('#nodata').hide();
}

//Ajax error函数
function errorFunc(xhr, status) {
    layer.open({
        type: 0,
        closeBtn: 0,
        icon: 7,
        title: '出现错误',
        content: status
    })
}

//获取应用列表,参数leadOption为true则添加value为空的option
function getAppList(callBack, leadOption) {
    ajax({
        url: 'app/getAppList',
        type: 'post',
        data: {
            domainId: $.cookie('domainId'),
            domainName: $.cookie('domainName')
        },
        success: function (response) {
            if (response.code === 1) {
                //当leadOption为true，添加value为空的option
                var options = leadOption ? '<option value="">所有APP</option> ' : '';
                response.data.forEach(function (item) {
                    options += '<option value="' + item.id + '">' + item.appName + '</option> ';
                });
                $('#search-app').html(options);
                callBack && callBack();
            }
        }
    });
}


//根据请求url获取参数
function GetQueryString(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    var currUrl = window.location.search;
    var r = currUrl.substr(1).match(reg);
    if (r != null) return decodeURIComponent(r[2]);
    return null;
}

// durationType值定义： 1: 最近一个月 2：最近半年 3：自定义
function setDuration(durationType) {
    if (durationType != 3) {
        var nowTime = new Date()
        var endTime = nowTime.Format('yyyy-MM-dd')
        var startTime = new Date()
        if (durationType == 1) {
            startTime.setMonth(nowTime.getMonth() - 1)
        } else if (durationType == 2) {
            startTime.setMonth(nowTime.getMonth() - 6)
        }

        startTime = startTime.Format('yyyy-MM-dd')

        searchPara.startTime = getTime10(startTime)
        searchPara.endTime = getTime10(nowTime.Format('yyyy-MM-dd')) + 24 * 60 * 60 - 1
        $('#search-start-date').val(startTime)
        $('#search-end-date').val(endTime)
    }
}

$('input').on('input', function () {
    var val = $(this).val();
    var jsReg = /.*(<script>).*(<\/script>)*.*/i;
    if (jsReg.test(val)) {
        $(this).val('');
    }
    if (val.length > 50) {
        layer.msg('内容限制为50字');
        $(this).val(val.slice(0, 50));
    }
});

$('textarea').on('input', function () {
    var val = $(this).val();
    var jsReg = /.*(<script>).*(<\/script>)*.*/i;
    if (jsReg.test(val)) {
        $(this).val('');
    }

    // if(val.length > 150){
    //     layer.msg('内容限制为150字');
    //     $(this).val(val.slice(0,150));
    // }
});

function formatSeconds(value) {
    var theTime = parseInt(value);// 秒
    var theTime1 = 0;// 分
    var theTime2 = 0;// 小时
// alert(theTime);
    if (theTime > 60) {
        theTime1 = parseInt(theTime / 60);
        theTime = parseInt(theTime % 60);
// alert(theTime1+"-"+theTime);
        if (theTime1 > 60) {
            theTime2 = parseInt(theTime1 / 60);
            theTime1 = parseInt(theTime1 % 60);
        }
    }
    var result = "" + parseInt(theTime) + "秒";
    if (theTime1 > 0) {
        result = "" + parseInt(theTime1) + "分" + result;
    }
    if (theTime2 > 0) {
        result = "" + parseInt(theTime2) + "小时" + result;
    }
    return result;
}

var ROLE = {
    SYSTEM: 'ROLE_SYSTEM',
    ORGANIZATION: 'ROLE_ORGANIZATION',
    ADMIN: 'ROLE_ADMIN',
    TAX: 'ROLE_TAX'
}


var ROLE_ARRAY = new Array('ROLE_SYSTEM', 'ROLE_ORGANIZATION', 'ROLE_ADMIN', 'ROLE_TAX')

function getRoleName(role) {
    var x = '';
    switch (role) {
        case 'ROLE_SYSTEM':
            x = "系统管理员";
            break;
        case 'ROLE_ORGANIZATION':
            x = "组织管理员";
            break;
        case 'ROLE_ADMIN':
            x = "超级管理员";
            break;
        default:
            x = '管理员';
            break;
    }
    return x;
}


function validatePass(pass,confirmPassword) {

    var reg = /^[0-9a-zA-Z]+$/;


    if (!pass ||
        !reg.test(pass) ||
        (pass.length < 4 || pass.length > 16)) {
        layer.msg("密码格式不正确，请输入由4-16字母或数字组成的密码", {icon: 2, time: 1000});
        return true;
    }

    if (!confirmPassword ||
        !reg.test(confirmPassword) ||
        (confirmPassword.length < 4 || confirmPassword.length > 16)) {
        layer.msg("确认密码格式不正确，请输入由4-16字母或数字组成的密码", {icon: 2, time: 1000});
        return true;
    }

    if (pass !== confirmPassword) {
        layer.msg("两次密码输入不一致", {icon: 2, time: 1000});
        return true;
    }

    return false;
}


function validateNum(num){
    var patrn = /^\d+(\.\d+)?$/;
    if (!patrn.exec(num)) {
        layer.msg("请输入数字", {icon: 2, time: 1000});
        return false;
    }
    return true
}
//实例化编辑器
//建议使用工厂方法getEditor创建和引用编辑器实例，如果在某个闭包下引用该编辑器，直接调用UE.getEditor('editor')就能拿到相关的实例

try {


    var ue = UE.getEditor('editor');

    ue.ready(function () {
        $(".edui-editor-messageholder.edui-default").css({"visibility": "hidden"});
    })

    function isFocus(e) {
        alert(UE.getEditor('editor').isFocus());
        UE.dom.domUtils.preventDefault(e)
    }

    function setblur(e) {
        UE.getEditor('editor').blur();
        UE.dom.domUtils.preventDefault(e)
    }

    function insertHtml() {
        var value = prompt('插入html代码', '');
        UE.getEditor('editor').execCommand('insertHtml', value)
    }

    function createEditor() {
        enableBtn();
        UE.getEditor('editor');
    }

    function getAllHtml() {
        alert(UE.getEditor('editor').getAllHtml())
    }

    function getContent() {
        var arr = [];
        arr.push("使用editor.getContent()方法可以获得编辑器的内容");
        arr.push("内容为：");
        arr.push(UE.getEditor('editor').getContent());
        alert(arr.join("\n"));
    }

    function getPlainTxt() {
        var arr = [];
        arr.push("使用editor.getPlainTxt()方法可以获得编辑器的带格式的纯文本内容");
        arr.push("内容为：");
        arr.push(UE.getEditor('editor').getPlainTxt());
        alert(arr.join('\n'))
    }

    function setContent(isAppendTo) {
        var arr = [];
        arr.push("使用editor.setContent('欢迎使用ueditor')方法可以设置编辑器的内容");
        UE.getEditor('editor').setContent('欢迎使用ueditor', isAppendTo);
        alert(arr.join("\n"));
    }

    function setDisabled() {
        UE.getEditor('editor').setDisabled('fullscreen');
        disableBtn("enable");
    }

    function setEnabled() {
        UE.getEditor('editor').setEnabled();
        enableBtn();
    }

    function getText() {
        //当你点击按钮时编辑区域已经失去了焦点，如果直接用getText将不会得到内容，所以要在选回来，然后取得内容
        var range = UE.getEditor('editor').selection.getRange();
        range.select();
        var txt = UE.getEditor('editor').selection.getText();
        alert(txt)
    }

    function getContentTxt() {
        var arr = [];
        arr.push("使用editor.getContentTxt()方法可以获得编辑器的纯文本内容");
        arr.push("编辑器的纯文本内容为：");
        arr.push(UE.getEditor('editor').getContentTxt());
        alert(arr.join("\n"));
    }

    function hasContent() {
        var arr = [];
        arr.push("使用editor.hasContents()方法判断编辑器里是否有内容");
        arr.push("判断结果为：");
        arr.push(UE.getEditor('editor').hasContents());
        alert(arr.join("\n"));
    }

    function setFocus() {
        UE.getEditor('editor').focus();
    }

    function deleteEditor() {
        disableBtn();
        UE.getEditor('editor').destroy();
    }

    function disableBtn(str) {
        var div = document.getElementById('btns');
        var btns = UE.dom.domUtils.getElementsByTagName(div, "button");
        for (var i = 0, btn; btn = btns[i++];) {
            if (btn.id == str) {
                UE.dom.domUtils.removeAttributes(btn, ["disabled"]);
            } else {
                btn.setAttribute("disabled", "true");
            }
        }
    }

    function enableBtn() {
        var div = document.getElementById('btns');
        var btns = UE.dom.domUtils.getElementsByTagName(div, "button");
        for (var i = 0, btn; btn = btns[i++];) {
            UE.dom.domUtils.removeAttributes(btn, ["disabled"]);
        }
    }

    function getLocalData() {
        alert(UE.getEditor('editor').execCommand("getlocaldata"));
    }

    function clearLocalData() {
        UE.getEditor('editor').execCommand("clearlocaldata");
        alert("已清空草稿箱")
    }

} catch (e) {

}