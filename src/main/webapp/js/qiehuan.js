$(document).ready(function () {

    //切换选项
    $(".order-area li").not('.JqIcon').click(function () {

        $(".order-area li").removeClass('show');
        $(this).addClass('show');
        var i = $(this).index();
        switch (i) {

            case 0:
                $(".searchBox").show();
                $(".typeRadioTeam,.comp").hide();
                break;
            case 1:
                $(".comp").css('display', 'table');
                $(".typeRadioTeam,.searchBox").hide();
                break;
            case 2:
                $(".typeRadioTeam").css('display', 'table');
                $(".comp,.searchBox").hide();
                break;

        }

    })


    //模拟单选按钮
    $(".comp .t-cell-css").click(function () {

        $(".comp").find(".myRadio").removeClass('radioChecked');
        $(this).find(".myRadio").addClass('radioChecked');


    })
    $(".typeRadioTeam .t-cell-css").click(function () {

        $(".typeRadioTeam").find(".myRadio").removeClass('radioChecked');
        $(this).find(".myRadio").addClass('radioChecked');


    })


    //大图、列表切换
    var x = 0;
    $(".JqIcon").click(function () {

        if (x == 0) {

            $(this).removeClass('goPic').addClass('goList');
            $(".listBox").hide();
            $(".picBox").show();
            x = 1;

        } else {
            $(this).removeClass('goList').addClass('goPic');
            $(".listBox").show();
            $(".picBox").hide();
            x = 0;
        }

    })

    let volumesL
    filter = {
        subjectId: 1,
        grade: 20
    };
    $.ajax({
        type: "get",
        url: 'api/volumes?filter=' + JSON.stringify(filter),
        async: false,
        success: function (vs) {
            volumesL = vs

        }
    })

    let volumesH
    filter = {
        subjectId: 1,
        grade: 21
    };
    $.ajax({
        type: "get",
        url: 'api/volumes?filter=' + JSON.stringify(filter),
        async: false,
        success: function (vs) {

            volumesH = vs

        }
    })

    let doc = document

    let getTemplate = function (templateId) {
        let result
        let template = doc.getElementById(templateId)
        if (template) {
            result = template.content.children[0]
        }
        return result
    }

    let bind = function (element, data) {
        if (element) {
            element.innerHTML = element.innerHTML.replace('%7B', '{').replace('%7D', '}').replace(/\$\{(\w+)\}/g, function (all, variable) {
                let result = ''
                if (data && variable) {
                    result = data[variable]
                }
                return result
            })
        }
    }

    let proc = function (option) {
        //prepare template
        let template = option.template
        if (!template) {
            template = getTemplate(option.templateId)
        }
        //prepare alt templates
        let templates
        if (option.alterTemplates) {
            templates = []
            for (let i = 0; i < option.alterTemplates.length; ++i) {
                let altTemplate = getTemplate(option.alterTemplates[i].templateId)
                if (altTemplate) {
                    templates.push({type: option.alterTemplates[i].type, template: altTemplate})
                }
            }
        }
        //prepare container
        let container = option.container
        if (!container) {
            container = doc.getElementById(option.containerId)
        }
        if ((template || templates) && container) {
            //clone element via template or alt templates
            let cloneElement = function (type) {
                let element
                if (template) {
                    element = template.cloneNode(true)
                } else if (templates) {
                    for (let j = 0; j < templates.length; ++j) {
                        if (type == templates[j].type) {
                            element = templates[j].template.cloneNode(true)
                            break
                        }
                    }
                }
                return element
            }
            //proc second bind
            let procSecond = function (data, element) {
                if (option.secondBind) {
                    //prepare second templates
                    let secondTemplates = []
                    if (Array.isArray(option.secondBind)) {
                        for (let i = 0; i < option.secondBind.length; ++i) {
                            let secondTemplate = getTemplate(option.secondBind[i].templateId)
                            secondTemplates.push({
                                extPoint: option.secondBind[i].extPoint,
                                template: secondTemplate
                            })
                        }
                    } else {
                        let secondTemplate = getTemplate(option.secondBind.templateId)
                        secondTemplates.push({extPoint: option.secondBind.extPoint, template: secondTemplate})
                    }
                    //get second template from second templates
                    let getSecondTemplate = function (secondTemplates, extPoint) {
                        let secondTemplate
                        for (let j = 0; j < secondTemplates.length; ++j) {
                            if (secondTemplates[j].extPoint == extPoint) {
                                secondTemplate = secondTemplates[j].template
                            }
                        }
                        return secondTemplate
                    }
                    if (Array.isArray(option.secondBind)) {
                        for (let i = 0; i < option.secondBind.length; ++i) {
                            proc({
                                container: element.querySelector('*[data-ext-point="' + option.secondBind[i].extPoint + '"]'),
                                template: getSecondTemplate(secondTemplates, option.secondBind[i].extPoint),
                                data: data[option.secondBind[i].dataFieldName]
                            })
                        }
                    } else {
                        proc({
                            container: element.querySelector('*[data-ext-point="' + option.secondBind.extPoint + '"]'),
                            template: getSecondTemplate(secondTemplates, option.secondBind.extPoint),
                            data: data[option.secondBind.dataFieldName]
                        })
                    }
                }
            }
            if (Array.isArray(option.data)) { //data is array of object
                for (let i = 0; i < option.data.length; ++i) {
                    let element = cloneElement(option.data[i].type)
                    if (element) {
                        bind(element, option.data[i])
                        procSecond(option.data[i], element)
                        container.appendChild(element)
                    }
                }
            } else { //data is object
                let element = cloneElement()
                if (element) {
                    bind(element, option.data)
                    procSecond(option.data, element)
                    container.appendChild(element)
                }
            }
        }
    }

    proc({
        templateId: 'volumes-template',
        data: volumesL,
        containerId: 'mathStudy-volumesL'
    })

    proc({
        templateId: 'volumes-template',
        data: volumesH,
        containerId: 'mathStudy-volumesH'
    })


});
