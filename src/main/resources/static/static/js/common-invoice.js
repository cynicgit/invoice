function getUser() {
    ajax
    ({
        url: '/system/user/all',
        success: function (res) {
            if (res.result === 0) {
                showUser(res.data);
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
    $('#depId').html(options);
}


function showUser(list) {
    for (var i = 0; i < list.length; i++) {
        $('#userId').append("<option value='" + list[i].id + "'>" + list[i].name + "</option>");
    }
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

    $("#noReceivedAmount").val(invoiceAmount - receivedAmount - badAmount);

    if (id === 'invoiceAmount'){
        var taxRate = $("#taxRate").val();
        var noTaxAmount = (100 - taxRate) * invoiceAmount / 100;
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

$('#taxRate').on('change', function () {
    var taxRate = $(this).val();
    var invoiceAmount = $("#invoiceAmount").val();
    var noTaxAmount = (100 - taxRate) * invoiceAmount / 100;
    $("#noTaxAmount").val(noTaxAmount.toFixed(2));
});