$(function () {
//添加评论
    var createComment=document.getElementById('createComment');
    createComment .addEventListener('click', writeMessage, false);
    function writeMessage() {
        $('#commentWriter').toggle();
        var btn=document.getElementById('btn');
        btn.addEventListener('click', submit, false);
        function submit(e) {
            var textarea=document.getElementById('textarea');
            var value=textarea.value;
            textarea.value='';
            e.target.style.color = '#f5f5f5';
            // e.target.style.backgroundColor = '#3e8f3e';
            $.ajax({
                type: "post",
                url: "/api/comments",
                data: JSON.stringify({objectType:'knowledge-point', objectId:g.getUrlParameter("id"), content: value}),
                dataType: "json",
                contentType: "application/json; charset=utf-8",
                success: function (data) {
                    alert(JSON.stringify(data))
                }
            })
        }
    }

    var trueImage = document.createElement('img');
    trueImage.setAttribute('class', 'daan_error');
    trueImage.setAttribute('src', 'img/true.png');
    trueImage.setAttribute('alt', '');

    var falseImage = document.createElement('img');
    falseImage.setAttribute('class', 'daan_error');
    falseImage.setAttribute('src', 'img/error.png');
    falseImage.setAttribute('alt', '');

    // // //change icon via liked state
    // var icon = document.getElementById('icon')
    // icon.addEventListener('click', function(e) {
    //     if (liked) {
    //         liked = false
    // //     // *   unlike
    // //     //     *   //event processor unlike
    // //     //     *       notification unlike
    // //     //     *       icon change to like, unliked
    // //     //     *       likeCount - 1
    //     } else {
    //         liked = true
    // //         //     *   like
    // //         //     *   //event processor like
    // //         //     *       notification like
    // //         // *       icon change to unlike, liked
    // //         // *       likeCount + 1
    //     }
    // }, false)
    // message---------



    var volumeId = g.getUrlParameter("volumeId");
    $.ajax({
        type: 'get',
        url: 'api/knowledge-points?filter=' + JSON.stringify({
            volumeId: parseInt(volumeId)
        }),
        dataType: 'json',
        success: function (knowledgePointList) {
            var id = g.getUrlParameter('id');
			alert(JSON.stringify(knowledgePointList))
            $.ajax({
                type: "get",
                url: 'api/knowledge-points/' + id + '/contents',
                dataType: 'json',
                async: false,
                success: function (data) {
                    alert(JSON.stringify(data))
                   proc({
                        templateId: 'video-template',
                        data: data.video,
                        containerId: 'video'
                    })
					/*proc({
                        templateId: 'challenge-template',
                        data: data.quotes,
                        containerId: 'challenge'
                    });*/
					proc({
                        templateId: 'interaction-template',
                        data: data.interaction,
                        containerId: 'interaction'
                    })
					proc({
                        templateId: 'comment-template',
                        data: data.comments,
                        containerId: 'comments'
                    })
					/*proc({
						data: data.contents,
						containerId: 'content',
						alterTemplates: [
							{type: 'text', templateId: 'content-text-template'},
							{type: 'imageText', templateId: 'content-image-template'}
						]
                    });*/
					// GET /knowledge-points/.../is-self-like
                    var id = g.getUrlParameter("id")
                    $.ajax({
                        type: "get",
                        url: 'api/knowledge-points/' + id + '/is-self-like',
                        dataType: "json",
                        success: function (like) {
                            var liked = like.like
                            var icon = document.getElementById('icon');
                            icon.addEventListener('click', function (e) {
                                if (liked) {
                                    $.ajax({
                                        type: "put",
                                        url: '/api/knowledge-points/' + id + '/unlike',
                                        data: JSON.stringify({}),
                                        dataType: "json",
                                        contentType: "application/json; charset=utf-8",
                                        success: function (unlike) {
                                            icon.setAttribute('src', 'img/zan.png');
                                            --data.interaction.likeCount
                                            e.target.nextElementSibling.textContent = data.interaction.likeCount
                                            liked = false;
                                        }
                                    })
                                } else {
                                    $.ajax({
                                        type: "put",
                                        url: '/api/knowledge-points/' + id + '/like',
                                        data: JSON.stringify({}),
                                        dataType: "json",
                                        contentType: "application/json; charset=utf-8",
                                        success: function (like) {
                                            icon.setAttribute('src', 'img/zan-over.png');
                                            ++data.interaction.likeCount;
                                            e.target.nextElementSibling.textContent = data.interaction.likeCount
                                            liked = true;
                                        }
                                    })
                                }
                            }, false)
                        },
                        error: function (unlike) {
                            //liked = false
                        }
                    })
					
                    //评论点赞
                    

                    $('.ul01_imgzan').on('click', function (e) {
                        var id = e.target.parentNode.dataset.id
                        $.ajax({
                            type: "get",
                            url: 'api/comments/' + id + '/is-self-like',
                            dataType: "json",
                            success: function (like) {
                                var  likeds = like.like;
                                if(likeds){
                                    var id = e.target.parentNode.dataset.id
                                    $.ajax({
                                        type: "put",
                                        url: '/api/comments/' + id + '/unlike',
                                        data: JSON.stringify({}),
                                        dataType: "json",
                                        contentType: "application/json; charset=utf-8",
                                        success: function (unlike) {
                                            for (var i = 0; i < data.comments.length; ++i) {
                                                if (data.comments[i].id == id) {
                                                    e.target.setAttribute('src', 'img/zan.png');
                                                    --data.comments[i].likeCount;
                                                    e.target.nextElementSibling.textContent = data.comments[i].likeCount;
                                                    likeds = false;
                                                    break
                                                }
                                            }
                                        }
                                    })

                                } else {

                                    var id = e.target.parentNode.dataset.id
                                    $.ajax({
                                        type: "put",
                                        url: '/api/comments/' + id + '/like',
                                        data: JSON.stringify({}),
                                        dataType: "json",
                                        contentType: "application/json; charset=utf-8",
                                        success: function (like) {
                                            for (var i = 0; i < data.comments.length; ++i) {
                                                if (data.comments[i].id == id) {
                                                    e.target.setAttribute('src', 'img/zan-over.png');
                                                    ++data.comments[i].likeCount;
                                                    e.target.nextElementSibling.textContent = data.comments[i].likeCount;
                                                    likeds = true;
                                                    break
                                                }
                                            }
                                        }
                                    })
                                }
                            }
                        })

                    })
					
					
					// 上一个，下一个-----------------------------------------------------------
					 var baseUrl = 'mathKnowledgePointsDetail.html?volumeId=' + volumeId + "&id="

                    for (var i = 0; i < knowledgePointList.length; ++i) {
                        var id = g.getUrlParameter('id')
                        if (knowledgePointList[i].id == id) {
                            var prevIndex = i;
                            var nextIndex = i;
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
				
					
				/*	for (var i = 0; i < knowledgePointList.length; ++i) {
						var id = g.getUrlParameter('id');
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
                    }*/
					
                    //-------------------------------------------------------------------------------
                    
					for (var i = 0; i < data.problems.length; ++i) {
                        var p = data.problems[i];
                        p.options[0].title = 'A';
                        p.options[1].title = 'B';
                        p.options[2].title = 'C';
                        p.options[3].title = 'D'
                    }

                    for (var i = 0; i < knowledgePointList.length; ++i) {
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

                    //data.problems
                    var ps = []
                    for (var i = 0; i < data.problems.length; ++i) {
                        ps[i] = data.problems[i]
                    }
                    var pk = null
                    var strongestBrains = []
                    if (ps.length > 0) {
                        pk = ps[ps.length - 1]
                        ps.pop()
                        strongestBrains = ps
                    }

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
                            // {
                            //     extPoint: 'explain',
                            //     dataFieldName: 'video',
                            //     templateId: 'video-template'
                            // }
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
                   
                



                    // 选项判错--------------------------------------------------------
                    var findProblem = function (problemId) {
                        var problem = null
                        for (var i = 0; i < data.problems.length; ++i) {
                            if (data.problems[i].id == problemId) {
                                problem = data.problems[i]
                                break
                            }
                        }
                        return problem
                    }

                    var getIndex = function (content) {
                        var index = -1
                        switch (content) {
                            case 'A':
                                index = 0
                                break
                            case 'B':
                                index = 1
                                break
                            case 'C':
                                index = 2
                                break
                            case 'D':
                                index = 3
                                break
                            default:
                                break
                        }
                        return index
                    }

                    var compareAnswer = function (index, standardAnswers) {
                        var finded = false
                        for (var j = 0; j < standardAnswers.length; ++j) {
                            if (index == standardAnswers[j].name) {
                                finded = true
                                break
                            }
                        }
                        return finded
                    }

                    var judgement = function (e) {
                        //e.currentTarget == problemContainer
                        var clickedElement = e.target;

                        if (clickedElement.hasClass('daan_quan')) { // == [class="daan_quan"]
                            var problemId = clickedElement.parentNode.parentNode.dataset.id
                            var problem = findProblem(problemId);
                            if (problem) {
                                var index = getIndex(clickedElement.textContent);
                                var r = compareAnswer(index, problem.standardAnswers);
                                if (r) {
                                    clickedElement.parentNode.addClass('daanLi_true');
                                    clickedElement.innerHTML = '';
                                    clickedElement.appendChild(trueImage.cloneNode(true))
                                } else {
                                    clickedElement.parentNode.addClass('daanLi_error')
                                    clickedElement.innerHTML = ''
                                    clickedElement.appendChild(falseImage.cloneNode(true))
                                }
                            }
                        }
                        var data = {
                            objectType: 'knowledge-point',
                            objectId: 'problemId',
                            objectName: 'index',
                            action: 'click'
                        };
                        $.ajax({
                            type: "post",
                            url: 'api/answer-records',
                            async: false,
                            data: data,
                            success: function (data) {
                                alert(JSON.stringify(data))
                            }
                        })
                    };
                    var problemContainer = document.getElementById('strongest-brain')
                    problemContainer.addEventListener('click', judgement, false)

                    var pkContainer = document.getElementById('pk')
                    pkContainer.addEventListener('click', judgement, false) 
                    
                }
            })
        }
    })
})


