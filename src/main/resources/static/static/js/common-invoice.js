function getUser(selected) {
    ajax
    ({
        url: '/system/user/all',
        success: function (res) {
            if (res.result === 0) {
                showUser(res.data,selected);
            } else if (res.result === 1) {
                layer.msg(res.error);
            }
        }
    })
}

function getDep() {
    // ajax
    // ({
    //     url: '/system/dep/all',
    //     success: function (res) {
    //         if (res.result === 0) {
    //             showDep(res.data);
    //         } else if (res.result === 1) {
    //             layer.msg(res.error);
    //         }
    //     }
    // })

    ajax
    ({
        url: '/system/dep/tree',
        success: function (res) {
            if (res.result === 0) {
                showDep(res.data);
            } else if (res.result === 1) {
                layer.msg(res.error);
            }
        }
    })

}

function showDep(list) {
    // for (var i = 0; i < list.length; i++) {
    //     $('#depId').append("<option value='" + list[i].id + "'>" + list[i].name + "</option>");
    // }
    // $('#depId').searchableSelect();
    var options = '<option value="">请选择</option>';
    list.forEach(function (item) {
        if (item.childrenDep.length === 0) {
            options += '<optgroup label="'+ item.name +'">'
                + '<option value="'+ item.id+ '">'+ item.name+ '</option></optgroup>'
        } else {
            options += '<optgroup label="'+ item.name +'">'
            item.childrenDep.forEach(function (c) {
                options +=  '<option value="'+ c.id+ '">'+ c.name+ '</option>'
            })
            options += '</optgroup>'
        }
    });
    $('#depId-select').html(options);
}


function showUser(list,selected) {

    var options ;
    for (var i = 0; i < list.length; i++) {
        if (selected === list[i].id){
            options += '<option value="' + list[i].id + '" selected >' + list[i].name + '</option>'
            continue;
        }
         options += '<option value="' + list[i].id + '" >' + list[i].name + '</option>'


    }
    $('#userId').html(options);
}

function onblus(tag) {
    var id = $(tag).attr("id");
    var invoiceAmount = $("#invoiceAmount").val();
    var receivedAmount = $("#receivedAmount").val();
    var badAmount = $("#badAmount").val();

    if (invoiceAmount !== '' && !validateNum(invoiceAmount,"发票金额")) {
        return;
    }
    if (receivedAmount !== '' && !validateNum(receivedAmount,"回款金额")) {
        return;
    }
    if (badAmount !== '' && !validateNum(badAmount,"坏账金额")) {
        return;
    }

    if (badAmount === ''){
        badAmount = 0
    }
    if (receivedAmount === ''){
        receivedAmount = 0
    }

    $("#noReceivedAmount").val(invoiceAmount - receivedAmount);

    if (id === 'invoiceAmount'){
        var taxRate = $("#taxRate").val();
        var num = (100 + parseInt(taxRate)) / 100;
        var noTaxAmount = invoiceAmount / num  ;
        $("#noTaxAmount").val(noTaxAmount.toFixed(2));
    }
}

function validateNum(num,string) {
    var patrn = /^\d+(\.\d+)?$/;
    if (!patrn.exec(num)) {
        layer.msg( string + "为数字", {icon: 2, time: 1000});
        return false;
    }
    return true
}

function validateNull(con, string) {
    if (con === null || con === "") {
        layer.msg(string + "不能为空", {icon: 2, time: 1000});
        return false;
    }
    return true;
}

function validateYear(startDate,endDate){
    if (!startDate || startDate == '') {
        layer.msg('请选择开始时间', {icon: 2, time: 1000});
        return false;
    }
    if (!endDate || endDate == '') {
        layer.msg('请选择结束时间', {icon: 2, time: 1000});
        return false;
    }

    if (startDate !== '' && endDate !== '') {

        var startYear = startDate.substring(0,4);
        var endYear = endDate.substring(0,4);

        if (startYear !== endYear){
            layer.msg('日期不能跨越年份', {icon: 2, time: 1000});
            return false;
        }

        var start = new Date(startDate);
        var end = new Date(endDate);
        if (start.getMilliseconds() > end.getMilliseconds()) {
            layer.msg('结束日期不能小于开始日期', {icon: 2, time: 1000});
            return false
        }
    }
    return true;
}


function validateYear2(startDate,endDate){
    if (!startDate || startDate == '') {
        layer.msg('请选择开始时间', {icon: 2, time: 1000});
        return false;
    }
    if (!endDate || endDate == '') {
        layer.msg('请选择结束时间', {icon: 2, time: 1000});
        return false;
    }

    if (startDate !== '' && endDate !== '') {
        var start = new Date(startDate);
        var end = new Date(endDate);
        if (start.getMilliseconds() > end.getMilliseconds()) {
            layer.msg('结束日期不能小于开始日期', {icon: 2, time: 1000});
            return false
        }
    }
    return true;
}


function getTaxRate(taxRate){
    ajax
    ({
        url: '/taxrate/all',
        success: function (res) {
            if (res.result === 0) {
                var list = res.data;
                var options = '';
                for (var i = 0; i < list.length; i++) {
                    if(taxRate === list[i].taxRate){
                        options += '<option value="' + list[i].taxRate + '" selected >' + list[i].taxRate + '</option>'
                        continue;
                    }
                    options += '<option value="' + list[i].taxRate + '" >' + list[i].taxRate + '</option>'

                }
                $('#taxRate').html(options);
            } else if (res.result === 1) {
                layer.msg(res.error);
            }
        }
    })
}


$('#taxRate').on('change', function () {
    var taxRate = $(this).val();
    var invoiceAmount = $("#invoiceAmount").val();
    var num = (100 + parseInt(taxRate)) / 100;
    var noTaxAmount = invoiceAmount / num  ;
    $("#noTaxAmount").val(noTaxAmount.toFixed(2));
});

$('#depId-select').on('change', function () {
    var depId = $(this).val();
    getProject(depId,null)
});

function getProject(depId,projectId) {
    ajax({
        url:"project/dep?depId="+depId,
        type: 'get',
        data: {},
        success: function (res) {
            if (res.result === 0) {
                var list = res.data;
                var options = '';

                for (var i = 0; i < list.length; i++) {

                    if(projectId === list[i].id){
                        options += '<option value="' + list[i].id + '" selected >' + list[i].projectName + '</option>'
                        continue;
                    }
                    options += '<option value="' + list[i].id + '" >' + list[i].projectName + '</option>'

                }
                $('#projectId').html(options);
            } else if (res.code === 0) {
                layer.msg(res.error);
            }
        }
    })
}
