$('.div0_one').show();
$('.table_li').eq(0).css({"box-shadow": "0rem 0rem 1.25rem #fe6569"});
$('.table_li').eq(0).children('img').css("display", "inherit");
;


$('.table_li').eq(0).click(function () {
    $('.table_li').eq(0).children('img').css("display", "inherit");
    $('.table_li').eq(1).children('img').css("display", "none");
    $(this).css({"box-shadow": "0rem 0rem 1.25rem #fe6569"});
    $('.table_li').eq(1).css({"box-shadow": "0rem 0rem 0rem #fff"});
    $('.div0_one').show();
    $('.div0_two').hide();
});


$('.table_li').eq(1).click(function () {

    $('.table_li').eq(0).children('img').css("display", "none");
    $('.table_li').eq(1).children('img').css("display", "inherit");
    $(this).css({"box-shadow": "0rem 0rem 1.25rem #23cbd1"});
    $('.table_li').eq(0).css({"box-shadow": "0rem 0rem 0rem #fff"});
    $('.div0_one').hide();
    $('.div0_two').show();
});

function newClass() { //课程更新弹窗
    $('.tan').show();
    $('.new-class').show();

    $(".tan,.new-class").bind('touchmove', function (event) {
        event.preventDefault();
    });
    $(".new-class").click(function () {
        $(this).hide();
        $(".tan").hide();

    });
}

