<<<<<<< HEAD
$(function () {
=======
$(document).ready(function() {
>>>>>>> 43500dc9a62f29b4070abe57d8e23cc385996845
  // let data = {
  //   volumesL: [
  //           {id: 1, name: '钟表喊你吃饭啦', title: 'hhhh'},
  //           {id: 2, name: '横式问题找怪物', title: 'sdt'},
  //           {id: 3, name: '火柴竟是运动迷', title: 'xxxx'},
  //           {id: 4, name: '抓住叛徒填等式', title: 'jjjj'},
  //           {id: 5, name: '爱搬家的乘除号', title: 'ddds'},
  //           {id: 6, name: '画图妙解应用题', title: 'hhhh'},
  //           {id: 7, name: '笼里鸡兔知多少', title: 'vccvcv'},
  //           {id: 8, name: '文学也有重口味', title: 'ewr23'}
  //       ],
  //       volumesH: [
  //           {id: 1, name: 'hight', title: 'hhhh'},
  //           {id: 2, name: 'hight', title: 'sdt'},
  //           {id: 3, name: 'hight', title: 'xxxx'},
  //           {id: 4, name: 'hight', title: 'jjjj'},
  //           {id: 5, name: 'hight', title: 'ddds'},
  //           {id: 6, name: 'hight', title: 'hhhh'},
  //           {id: 7, name: 'hight', title: 'vccvcv'},
  //           {id: 8, name: 'hight', title: 'ewr23'}
  //       ]
  // }
<<<<<<< HEAD

    let volumesL;
    filter = {
        subjectId: 2,
        grade: 20
    };
    $.ajax({
        type: "get",
        url: 'api/volumes?filter=' + JSON.stringify(filter),
        success: function (vs) {
            alert( JSON.stringify(vs))
            console.info(vs)
            volumesL= vs;

        },
        async: false
    });
    alert(volumesL)
    let volumesH;
    filter = {
        subjectId: 2,
        grade: 21
    };
    $.ajax({
        type: "get",
        url: 'api/volumes?filter=' + JSON.stringify(filter),
        async: false,
        success: function (vs) {
            volumesH = vs
        }
    });
=======
    let subjects = [];

    // $.get('api/subjects', function(ss) {
    //     subjects = ss;
    //     alert(JSON.stringify(subjects))
    //     for (let i = 0; i < subjects.length; ++i) {
            let filter = {
                subjectId: 2,
                grade: 20
            }
            // alert(JSON.stringify(filter))
            $.get('api/volumes?filter=' + JSON.stringify(filter), function(vs) {
                alert(JSON.stringify(vs))
                subjects.low = vs
            })
            filter.grade = 21;
            // alert(JSON.stringify(filter))
            $.get('api/volumes?filter=' + JSON.stringify(filter), function(vss) {
                subjects.high = vss
                alert(JSON.stringify(subjects.high ))
            })
        // }
    // })
>>>>>>> 43500dc9a62f29b4070abe57d8e23cc385996845


  proc({
    templateId: 'volumes-template',
<<<<<<< HEAD
    data: volumesL,
=======
    data: subjects[i].low,
>>>>>>> 43500dc9a62f29b4070abe57d8e23cc385996845
    containerId: 'volumes-low'
  })

  proc({
    templateId: 'volumes-template',
<<<<<<< HEAD
    data: volumesH,
=======
    data: subjects[i].high,
>>>>>>> 43500dc9a62f29b4070abe57d8e23cc385996845
    containerId: 'volumes-hight'
  })

})