/* @Author: zhaoxq*/
function msgTap() {//点击消息按钮
    $(".msg-btn").tap(function () {
        var $softKeyboard = $(".soft-keyboard");
        if ($softKeyboard.is(":hidden")) {
            $(this).parents(".input-area").hide();
            $softKeyboard.show();
        } else {
            $softKeyboard.hide();
            $(this).parents(".input-area").show();
        }
    });
}

function barrgeTap() { //点击弹幕按钮
    $(".barrge").tap(function () {
        var left = parseInt($(".barrge-btn").css("left"));
        if (left == 0) {
            $(this).children(".barrge-btn").css({"left": 20, "backgroundColor": "#ffa230"});
            $(".discuss-area").hide();
        } else {
            $(this).children(".barrge-btn").css({"left": 0, "backgroundColor": "rgba(220, 220, 220, 0.5)"});
            $(".discuss-area").show();
        }
    });
}

function liveDetail() { //老师直播窗口高度
    var $upHeight = $(window).height() - $(".pc-sync").height() - 60;
    var $liveDetail = $(".live-detail");
    $liveDetail.css("height", $upHeight);
}