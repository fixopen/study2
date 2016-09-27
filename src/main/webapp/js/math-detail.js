function like() {
    let data ={
        //userId: 1,
        objectType:'knowledge-point',
        objectId:g.getUrlParameter("id"),
        action:'like'
    }
    // let data ={
    //     //userId: 1,
    //     objectType:'knowledge-point',
    //     objectId:g.getUrlParameter("id"),
    //     action:'unlike'
    // }

    $.ajax({
        type: "post",
        url: "/api/logs",
        data: JSON.stringify(data),
        dataType: "json",
        contentType: "application/json; charset=utf-8",
        success: function like() {
            alert(JSON.stringify(data))
        }
    })

    // $.ajax({
    //     type: "post",
    //     url: "/api/comments",
    //     data: JSON.stringify({objectType:'knowledge-point', objectId:g.getUrlParameter("id"), content: '...'}),
    //     dataType: "json",
    //     contentType: "application/json; charset=utf-8",
    //     success: function like() {
    //         alert(JSON.stringify(data))
    //     }
    // })
}


$(function () {
    // let liked = false
    // $.ajax({
    //     type: "get",
    //     url: "/api/logs?filter=" + JSON.stringify({objectType: 'knowledge-point', objectId: id, action: 'like'}),
    //     dataType: "json",
    //     success: function like(like) {
    //         liked = true
    //     },
    //     error: function like(unlike) {
    //         liked = false
    //     }
    // })
    // //change icon via liked state
    // let icon = document.getElementById('icon')
    // icon.addEventListener('click', function(e) {
    //     if (liked) {
    //         liked = false
    //     // *   unlike
    //     //     *   //event processor unlike
    //     //     *       notification unlike
    //     //     *       icon change to like, unliked
    //     //     *       likeCount - 1
    //     } else {
    //         liked = true
    //         //     *   like
    //         //     *   //event processor like
    //         //     *       notification like
    //         // *       icon change to unlike, liked
    //         // *       likeCount + 1
    //     }
    // }, false)

    let volumeId = g.getUrlParameter("volumeId");
    $.ajax({
        type: 'get',
        url: 'api/knowledge-points?filter=' + JSON.stringify({
            volumeId: parseInt(volumeId)
        }),
        dataType: 'json',
        success: function (knowledgePointList) {
            let id = g.getUrlParameter('id');



            $.ajax({
                type: "get",
                url: 'api/knowledge-points/' + id + '/contents',
                dataType: 'json',
                async: false,
                success: function (data) {
                    //alert(JSON.stringify(data))
                    for (let i = 0; i < data.problems.length; ++i) {
                        let p = data.problems[i];
                        p.options[0].title = 'A';
                        p.options[1].title = 'B';
                        p.options[2].title = 'C';
                        p.options[3].title = 'D'
                    }
                    for (let i = 0; i < knowledgePointList.length; ++i) {
                        if (knowledgePointList[i].id == id) {
                            proc({
                                templateId: 'title1-template',
                                data: {title1: knowledgePointList[i].order},
                                containerId: 'title1'
                            });
                            proc({
                                templateId: 'title2-template',
                                data: {title: knowledgePointList[i].title},
                                containerId: 'title2'
                            });
                            break
                        }
                    }

                    proc({
                        templateId: 'challenge-template',
                        data: data.quotes,
                        containerId: 'challenge'
                    });

                    proc({
                        data: data.contents,
                        containerId: 'content',
                        alterTemplates: [
                            {type: 'text', templateId: 'content-text-template'},
                            {type: 'imageText', templateId: 'content-image-template'}
                        ]
                    });

                    proc({
                        templateId: 'video-template',
                        data: data.video,
                        containerId: 'video'
                    });

                    //data.problems
                    let pk = data.problems[data.problems.length - 1]
                    let strongestBrains = data.problems.pop()

                    proc({
                        templateId: 'strongest-brain-template',
                        data: strongestBrains,
                        containerId: 'strongest-brain',
                        secondBind: [
                            {
                                extPoint: 'options',
                                dataFieldName: 'options',
                                templateId: 'strongest-brain-option-template'
                            },
                            {
                                extPoint: 'explain',
                                dataFieldName: 'video',
                                templateId: 'video-template'
                            }
                        ]
                    });

                    proc({
                        templateId: 'pk-template',
                        data: pk,
                        containerId: 'pk',
                        secondBind: [
                            {
                                extPoint: 'options',
                                dataFieldName: 'options',
                                templateId: 'strongest-brain-option-template'
                            }
                        ]
                    });

                    let baseUrl = 'mathKnowledgePointsDetail.html?volumeId=' + volumeId + "&id="

                    for (let i = 0; i < knowledgePointList.length; ++i) {
                        if (knowledgePointList[i].id == id) {
                            let prevIndex = i;
                            let nextIndex = i;
                            if (i > 0) {
                                prevIndex = i - 1
                            }
                            if (i < knowledgePointList.length - 1) {
                                nextIndex = i + 1
                            }
                            data.interaction.previous = baseUrl + knowledgePointList[prevIndex].id;
                            data.interaction.next = baseUrl + knowledgePointList[nextIndex].id;
                            break
                        }
                    }

                    proc({
                        templateId: 'interaction-template',
                        data: data.interaction,
                        containerId: 'interaction'
                    });

                    proc({
                        templateId: 'comment-template',
                        data: data.comments,
                        containerId: 'comments'
                    })
                }
            })
        }
    })
})


