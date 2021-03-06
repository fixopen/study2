$(function () {
    // getUrlParameter = function (name) {
    //     var result = null
    //     var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)") //构造一个含有目标参数的正则表达式对象
    //     var r = window.location.search.substr(1).match(reg) //匹配目标参数
    //     if (r != null) {
    //         result = decodeURI(r[2])
    //     }
    //     return result;//返回参数值
    // }
    proc({
        templateId: 'title-template',
        data: {title: g.getUrlParameter('volume')},
        containerId: 'title'
    })
    var volumeId = parseInt(g.getUrlParameter('volumeId'));
    $.ajax({
        type: "get",
        // url: 'api/knowledge-points?filter=' + JSON.stringify({
        //     subjectId: 2,
        //     volumeId: parseInt(getUrlParameter('volumeId')),
        //     grade: parseInt(getUrlParameter('grade'))
        // }),
        url: 'api/volumes/' + volumeId + '/knowledge-points',
        dataType: 'json',
        async: false,
        success: function (knowledgePoints) {
            //alert(JSON.stringify(knowledgePoints))
            // var kp = []
            // for (var i = 0; i < knowledgePoints.length; ++i) {
            //     kp[i] = knowledgePoints[i]
            // }
            // var ktt = null
            // var kpt = []
            // if (kp.length > 0) {
            //     ktt = kp[kp.length - 1]
            //     kp.pop()
            //     kpt = kp
            // }
            // function select(knowledgePoints) {
            //     if(data.knowledgePoints.name=="挑战百分百"){
            //
            //     }
            // }
            var now = new Date()
            for (var i = 0; i < knowledgePoints.length; ++i) {
                var kp = knowledgePoints[i];
                var t = new Date(kp.showTime.replace(/-/g, "/"));
                var h = (now.getTime() - t.getTime()) / ( 60 * 60 * 1000);
                if (h < 24) {
                    if (kp.type) {
                        kp.type += 'New'
                    } else {
                        kp.type = 'normalNew'
                    }
                    knowledgePoints.splice(i, 1);
                    knowledgePoints.unshift(kp);
                    // $('.neir_lidiv a').eq(i).removeClass('neir_pzi').addClass('neir_pzi_');
                } else {
                    if (kp.type) {
                        kp.type += 'Old'
                    } else {
                        kp.type = 'normalOld'
                    }
                    //   $('.neir_lidiv a').eq(i).removeClass('neir_pzi_').addClass('neir_pzi');
                }
            }
            proc({
                data: knowledgePoints,
                containerId: 'knowledge',
                alterTemplates: [
                    {type: 'normalNew', templateId: 'knowledge-point-normalNew-template'},
                    {type: 'normalOld', templateId: 'knowledge-point-normalOld-template'},
                    {type: 'pk', templateId: 'knowledge-test-template'}
                ]
            });
            // proc({
            //     data: kpt,
            //     containerId: 'knowledge-point',
            //     templateId: 'knowledge-point-template'
            // });
            //
            // proc({
            //     data: ktt,
            //     containerId: 'knowledge-test',
            //     templateId: 'knowledge-test-template'
            // });
        }
    })
})
