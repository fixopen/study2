package com.baremind;

import com.baremind.data.*;
import com.baremind.utils.CharacterEncodingFilter;
import com.baremind.utils.IdGenerator;
import com.baremind.utils.JPAEntry;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.util.*;

@Path("users")
public class Users {
    static String hostname = "https://sapi.253.com";
    static String username = "zhibo1";
    static String password = "Tch243450";

    @GET
    @Path("telephones/{telephone}/code")
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryValidCode(@PathParam("telephone") String telephone) {
        Response result = Response.ok("{\"state\":\"ok\"}").build();
        Random rand = new Random();
        int x = rand.nextInt(899999);
        int y = x + 100000;
        String sjs = String.valueOf(y);
        Date now = new Date();
        ValidationCode message = new ValidationCode();
        message.setId(IdGenerator.getNewId());
        message.setPhoneNumber(telephone);
        message.setValidCode(sjs);
        message.setTimestamp(now);
        JPAEntry.genericPost(message);
        SendMessageResult r = sendMessage(telephone, "《小雨知时》" + sjs + "(动态验证码),请在3分钟内使用");
        if (r.messageId == null) {
            switch (r.code) {
                case "0": //提交成功
                    break;
                case "101": //无此用户
                    result = Response.status(523).entity("{\"state\":\"无此用户\"}").build();
                    break;
                case "102": //密码错
                    result = Response.status(523).entity("{\"state\":\"密码错\"}").build();
                    break;
                case "103": //提交过快（提交速度超过流速限制）
                    result = Response.status(523).entity("{\"state\":\"提交过快（提交速度超过流速限制）\"}").build();
                    break;
                case "104": //系统忙（因平台侧原因，暂时无法处理提交的短信）
                    result = Response.status(523).entity("{\"state\":\"系统忙（因平台侧原因，暂时无法处理提交的短信）\"}").build();
                    break;
                case "105": //敏感短信（短信内容包含敏感词）
                    result = Response.status(523).entity("{\"state\":\"敏感短信（短信内容包含敏感词)\"}").build();
                    break;
                case "106": //消息长度错（>536或<=0）
                    result = Response.status(523).entity("{\"state\":\"消息长度错（>536或<=0）\"}").build();
                    break;
                case "107": //包含错误的手机号码
                    result = Response.status(523).entity("{\"state\":\"包含错误的手机号码\"}").build();
                    break;
                case "108": //手机号码个数错（群发>50000或<=0;单发>200或<=0）
                    result = Response.status(523).entity("{\"state\":\"手机号码个数错（群发>50000或<=0;单发>200或<=0）\"}").build();
                    break;
                case "109": //无发送额度（该用户可用短信数已使用完）
                    result = Response.status(523).entity("{\"state\":\"无发送额度（该用户可用短信数已使用完）\"}").build();
                    break;
                case "110": //不在发送时间内
                    result = Response.status(523).entity("{\"state\":\"不在发送时间内\"}").build();
                    break;
                case "111": //超出该账户当月发送额度限制
                    result = Response.status(523).entity("{\"state\":\"超出该账户当月发送额度限制\"}").build();
                    break;
                case "112": //无此产品，用户没有订购该产品
                    result = Response.status(523).entity("{\"state\":\"无此产品，用户没有订购该产品\"}").build();
                    break;
                case "113": //extno格式错（非数字或者长度不对）
                    result = Response.status(523).entity("{\"state\":\"extno格式错（非数字或者长度不对）\"}").build();
                    break;
                case "115": //自动审核驳回
                    result = Response.status(523).entity("{\"state\":\"自动审核驳回\"}").build();
                    break;
                case "116": //签名不合法，未带签名（用户必须带签名的前提下）
                    result = Response.status(523).entity("{\"state\":\"签名不合法，未带签名（用户必须带签名的前提下）\"}").build();
                    break;
                case "117": //IP地址认证错,请求调用的IP地址不是系统登记的IP地址
                    result = Response.status(523).entity("{\"state\":\"IP地址认证错,请求调用的IP地址不是系统登记的IP地址\"}").build();
                    break;
                case "118": //用户没有相应的发送权限
                    result = Response.status(523).entity("{\"state\":\"用户没有相应的发送权限\"}").build();
                    break;
                case "119": //用户已过期
                    result = Response.status(523).entity("{\"state\":\"用户已过期\"}").build();
                    break;
                case "120": //测试内容不是白名单
                    result = Response.status(523).entity("{\"state\":\"测试内容不是白名单\"}").build();
                    break;
            }
        } else {
            result = Response.ok("{\"messageId\": \"" + r.messageId + "\"}").build();
        }
        return result;
    }

    public static class SendMessageResult {
        public String time;
        public String code;
        public String messageId;
    }

    public static SendMessageResult sendMessage(String phoneNumber, String validInfo) {
        //platform: http://222.73.117.158/msg/index.jsp
        //username: jiekou-clcs-13
        //password: THYnk464hu
        //http://222.73.117.158:80/msg/HttpBatchSendSM?account=a&pswd=p&mobile=m&msg=m&needstatus=true

        //resptime,respstatus
        //msgid
//        respstatus
//        * 代码 说明
//        0 提交成功
//        101 无此用户
//        102 密码错
//        103 提交过快（提交速度超过流速限制）
//        104 系统忙（因平台侧原因，暂时无法处理提交的短信）
//        105 敏感短信（短信内容包含敏感词）
//        106 消息长度错（>536或<=0）
//        107 包含错误的手机号码
//        108 手机号码个数错（群发>50000或<=0;单发>200或<=0）
//        109 无发送额度（该用户可用短信数已使用完）
//        110 不在发送时间内
//        111 超出该账户当月发送额度限制
//        112 无此产品，用户没有订购该产品
//        113 extno格式错（非数字或者长度不对）
//        115 自动审核驳回
//        116 签名不合法，未带签名（用户必须带签名的前提下）
//        117 IP地址认证错,请求调用的IP地址不是系统登记的IP地址
//        118 用户没有相应的发送权限
//        119 用户已过期
//        120 测试内容不是白名单

        SendMessageResult result = new SendMessageResult();
        // Default instance of client
        Client client = ClientBuilder.newClient();
        Response response = client.target(hostname)
                .path("/msg/HttpBatchSendSM")
                .queryParam("account", username)
                .queryParam("pswd", password)
                .queryParam("mobile", phoneNumber)
                .queryParam("msg", validInfo)
                .queryParam("needstatus", true)
                .request("text/plain").get();
        String responseBody = response.readEntity(String.class);
        if (responseBody.contains("\n")) {
            String[] lines = responseBody.split("\n");
            if (lines.length == 2) {
                String[] timeCode = lines[0].split(",");
                result.time = timeCode[0];
                result.code = timeCode[1];
            }
            result.messageId = lines[1];
        } else {
            String[] timeCode = responseBody.split(",");
            result.time = timeCode[0];
            result.code = timeCode[1];
        }
        return result;
    }

    public static class ActiveCard {
        private String cardNo;
        private String password;
        private String phoneNumber;
        private String validCode;

        public String getCardNo() {
            return cardNo;
        }

        public void setCardNo(String cardCode) {
            this.cardNo = cardCode;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getValidCode() {
            return validCode;
        }

        public void setValidCode(String validCode) {
            this.validCode = validCode;
        }
    }

    @GET
    @Path("{id}/cards")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCards(@CookieParam("userId") String userId, @PathParam("id") Long id) {
        Response result = Response.status(404).build();
        User admin = JPAEntry.getObject(User.class, "id", Long.parseLong(userId));
        if (admin != null && admin.getIsAdministrator()) {
            List<Card> cards = JPAEntry.getList(Card.class, "userId", id);
            if (!cards.isEmpty()) {
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                //Gson gson = new GsonBuilder().registerTypeAdapter(java.sql.Time.class, new TimeTypeAdapter()).create();
                // result = Response.ok(gson.toJson(scheduler)).build();
                result = Response.ok(gson.toJson(cards)).build();
            }
        }
        return result;
    }

    @GET
    @Path("self/cards")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSelfCards(@CookieParam("userId") String userId) {
        Response result = Response.status(404).build();
        List<Card> cards = JPAEntry.getList(Card.class, "userId", Long.parseLong(userId));
        if (!cards.isEmpty()) {
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            result = Response.ok(gson.toJson(cards)).build();
        }
        return result;
    }

    private Response activeCardImpl(Long id, ActiveCard ac) {
        Response result = Response.status(412).build();
        User user = JPAEntry.getObject(User.class, "id", id);
        if (user != null) {
            //Logs.insert(id, "log", logId, "user exist");
            Map<String, Object> validCodeConditions = new HashMap<>();
            String phoneNumber = ac.getPhoneNumber();
            //Logs.insert(id, "log", logId, "create map");
            validCodeConditions.put("phoneNumber", phoneNumber);
            //Logs.insert(id, "log", logId, "put phone number");
            validCodeConditions.put("validCode", ac.getValidCode());
            //Logs.insert(id, "log", logId, "start query validation_codes table");
            List<ValidationCode> validationCodes = JPAEntry.getList(ValidationCode.class, validCodeConditions);
            //Logs.insert(id, "log", logId, "end query validation_codes table");
            switch (validationCodes.size()) {
                case 0:
                    //Logs.insert(id, "log", logId, "validation code not exist");
                    result = Response.status(401).build();
                    break;
                case 1:
                    //Logs.insert(id, "log", logId, "validation code exist");
                    Date now = new Date();
                    Date sendTime = validationCodes.get(0).getTimestamp();
                    if (now.getTime() < 60 * 3 * 1000 + sendTime.getTime()) {
                        //Logs.insert(id, "log", logId, "validation code success");
                        Map<String, Object> cardConditions = new HashMap<>();
                        cardConditions.put("no", ac.getCardNo());
                        cardConditions.put("password", ac.getPassword());
                        List<Card> cs = JPAEntry.getList(Card.class, cardConditions);
                        switch (cs.size()) {
                            case 0:
                                //Logs.insert(id, "log", logId, "card not exists");
                                result = Response.status(404).build();
                                break;
                            case 1:
                                //Logs.insert(id, "log", logId, "card exists");
                                User errorUser = JPAEntry.getObject(User.class, "telephone", phoneNumber);
                                if (errorUser != null) {
                                    if (errorUser.getId().longValue() != user.getId().longValue()) {
                                        WechatUser wechatUser = JPAEntry.getObject(WechatUser.class, "userId", id);
                                        WechatUser errorWechatUser = JPAEntry.getObject(WechatUser.class, "userId", errorUser.getId());
                                        if (wechatUser.getOpenId().equals(errorWechatUser.getOpenId())) {
                                            JPAEntry.genericDelete(WechatUser.class, "userId", errorUser.getId());
                                            JPAEntry.genericDelete(User.class, "id", errorUser.getId());
                                            EntityManager em = JPAEntry.getNewEntityManager();
                                            List<Card> bindedCards = JPAEntry.getList(Card.class, "userId", errorUser.getId());
                                            for (Card card : bindedCards) {
                                                card.setUserId(id);
                                                em.merge(card);
                                            }
                                            em.getTransaction().commit();
                                            em.close();
                                            Logs.insert(errorUser.getId(), "move-card", errorUser.getId(), phoneNumber);
                                        }
                                    }
                                }

                                user.setTelephone(phoneNumber);
                                JPAEntry.genericPut(user);
                                Card c = cs.get(0);
                                if (c.getActiveTime() == null) {
                                    c.setActiveTime(now);

                                    Calendar cal = Calendar.getInstance();
                                    cal.setTime(now);
                                    int year = cal.get(Calendar.YEAR);
                                    ++year;
                                    cal.set(Calendar.YEAR, year);
                                    Date oneYearAfter = cal.getTime();

                                    c.setEndTime(oneYearAfter);
                                    c.setAmount(5880L);
                                    if (ac.getCardNo().startsWith("03")) {
                                        c.setAmount(1680L);
                                    }
                                    User users = JPAEntry.getObject(User.class, "telephone", ac.getPhoneNumber());
                                    if (users == null) {
                                        user = new User();
                                        user.setId(IdGenerator.getNewId());
                                        user.setTelephone(ac.getPhoneNumber());
                                        user.setLoginName(ac.getPhoneNumber());
                                        user.setCreateTime(now);
                                        user.setUpdateTime(now);
                                        user.setName("");
                                        user.setSex(0l);
                                        JPAEntry.genericPost(user);
                                        c.setUserId(user.getId());
                                        JPAEntry.genericPut(c);
                                        Session s = PublicAccounts.putSession(new Date(), user.getId());
                                        result = Response.ok()
                                                .cookie(new NewCookie("userId", user.getId().toString(), "/api", null, null, NewCookie.DEFAULT_MAX_AGE, false))
                                                .cookie(new NewCookie("sessionId", s.getIdentity(), "/api", null, null, NewCookie.DEFAULT_MAX_AGE, false))
                                                .build();
                                    } else {
                                        c.setUserId(user.getId());
                                        JPAEntry.genericPut(c);
                                        result = Response.ok().build();
                                    }
                                } else {
                                    //Logs.insert(id, "log", logId, "card already active");
                                    result = Response.status(405).build();
                                }
                                break;
                            default:
                                //Logs.insert(id, "log", logId, "card multiple exists");
                                break;
                        }
                    } else {
                        //Logs.insert(id, "log", logId, "validation code timeout");
                        result = Response.status(410).build();
                    }
                    break;
                default:
                    //Logs.insert(id, "log", logId, "validation code multiple exist");
                    result = Response.status(520).build();
                    break;
            }
            //JPAEntry.genericDelete(ValidationCode.class, "phoneNumber", phoneNumber);
            //Logs.insert(id, "log", logId, "remove validation codes");
        }
        return result;
    }

    @POST
    @Path("{id}/cards")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response activeCard(@PathParam("id") Long id, ActiveCard ac) {
        Random rand = new Random();
        Long logId = rand.nextLong();
        //Logs.insert(id, "log", logId, "start");
        return activeCardImpl(id, ac);
    }

    @POST
    @Path("self/cards")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response activeCard(@CookieParam("userId") String userId, ActiveCard ac) {
        Random rand = new Random();
        Long logId = rand.nextLong();
        //Logs.insert(id, "log", logId, "start");
        return activeCardImpl(Long.parseLong(userId), ac);
    }

    public static class Reissue{
        private String name;
        private String school;
        private String grade;
        private String phone;
        private String code;
        private String password;
        private Long subject;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSchool() {
            return school;
        }

        public void setSchool(String school) {
            this.school = school;
        }

        public String getGrade() {
            return grade;
        }

        public void setGrade(String grade) {
            this.grade = grade;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public Long getSubject() {
            return subject;
        }

        public void setSubject(Long subject) {
            this.subject = subject;
        }
    }

    @POST
    @Path("reissue")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response activeCard(@CookieParam("userId") String userId, Reissue reissue) {
        Response result = Response.status(412).build();
        Date now = new Date();
        Card card = JPAEntry.getObject(Card.class, "no", reissue.getCode());
        User user = JPAEntry.getObject(User.class, "telephone", reissue.getPhone());
        if(card == null){
             result = Response.status(404).build();
        }else
        if(user == null){
            Map map = new HashMap();
            map.put("学校",reissue.getSchool());
            map.put("班级",reissue.getGrade());
            map.put("姓名",reissue.getName());
            JPAEntry.log(Long.valueOf(userId),new Gson().toJson(map),"reissue",Long.valueOf(reissue.getPhone()));
            SendMessageResult r = sendMessage(reissue.getPhone(), "《小雨知时》" +reissue.getName()+"补办的卡号是"+reissue.getCode()+",密码是"+reissue.getPassword()+"");
            result = Response.ok().build();
        }else{
            String classname = user.getClassname();
            if (classname != null) {
                user.setClassname(classname);
            }
            String grade = user.getGrade();
            if (grade != null) {
                user.setGrade(grade);
            }
            String name = user.getName();
            if (name != null) {
                user.setName(name);
            }
            String school = user.getSchool();
            if (school != null) {
                user.setSchool(school);
            }
            String telephone = user.getTelephone();
            if (telephone != null) {
                user.setTelephone(telephone);
            }
            user.setUpdateTime(now);
            JPAEntry.genericPut(user);
            SendMessageResult r = sendMessage(reissue.getPhone(), "《小雨知时》" +reissue.getName()+"补办的卡号是"+reissue.getCode()+",密码是"+reissue.getPassword()+"");
            result = Response.ok().build();
        }
        return result;
    }

    @POST
    @Path("update/material")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response actizzveCard(@CookieParam("userId") String userId, ActiveCard ac) {
        Response result = Response.status(412).build();
        Map<String, Object> validationCodeConditions = new HashMap<>();
        validationCodeConditions.put("phoneNumber", ac.getPhoneNumber());
        validationCodeConditions.put("validCode", ac.getValidCode());
        List<ValidationCode> validationCodes = JPAEntry.getList(ValidationCode.class, validationCodeConditions);
        switch (validationCodes.size()) {
            case 0:
                result = Response.status(401).build();
                break;
            case 1:
                Date now = new Date();
                Date sendTime = validationCodes.get(0).getTimestamp();
                if (now.getTime() < 60 * 3 * 1000 + sendTime.getTime()) {
                    result = Response.ok().build();
                } else {
                    result = Response.status(405).build();
                }
                break;
        }
        return result;
    }

  /*  @POST
    @Path("cards")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response activeCard(ActiveCard ac) {
        Response result = Response.status(412).build();
        Map<String, Object> validationCodeConditions = new HashMap<>();
        validationCodeConditions.put("phoneNumber", ac.getPhoneNumber());
        validationCodeConditions.put("validCode", ac.getValidCode());
        List<ValidationCode> validationCodes = JPAEntry.getList(ValidationCode.class, validationCodeConditions);
        switch (validationCodes.size()) {
            case 0:
                result = Response.status(401).build();
                break;
            case 1:
                Date now = new Date();
                Date sendTime = validationCodes.get(0).getTimestamp();
                if (now.getTime() < 60 * 3 * 1000 + sendTime.getTime()) {
                    Map<String, Object> condition = new HashMap<>();
                    condition.put("no", ac.getCardNo());
                    condition.put("password", ac.getPassword());
                    List<Card> cs = JPAEntry.getList(Card.class, condition);
                    switch (cs.size()) {
                        case 0:
                            result = Response.status(404).build();
                            break;
                        case 1:
                            Card c = cs.get(0);
                            if (c.getActiveTime() == null) {
                                c.setActiveTime(now);

                                Calendar cal = Calendar.getInstance();
                                cal.setTime(now);
                                int year = cal.get(Calendar.YEAR);
                                ++year;
                                cal.set(Calendar.YEAR, year);
                                Date oneYearAfter = cal.getTime();

                                c.setEndTime(oneYearAfter);
                                c.setAmount(588.0);
                                User user = JPAEntry.getObject(User.class, "telephone", ac.getPhoneNumber());
                                if (user == null) {
                                    user = new User();
                                    user.setId(IdGenerator.getNewId());
                                    user.setTelephone(ac.getPhoneNumber());
                                    user.setLoginName(ac.getPhoneNumber());
                                    user.setCreateTime(now);
                                    user.setUpdateTime(now);
                                    user.setName("");
                                    user.setSex(0l);
                                    JPAEntry.genericPost(user);
                                    c.setUserId(user.getId());
                                    JPAEntry.genericPut(c);
                                    Session s = PublicAccounts.putSession(new Date(), user.getId());
                                    result = Response.ok()
                                            .cookie(new NewCookie("userId", user.getId().toString(), "/api", null, null, NewCookie.DEFAULT_MAX_AGE, false))
                                            .cookie(new NewCookie("sessionId", s.getIdentity(), "/api", null, null, NewCookie.DEFAULT_MAX_AGE, false))
                                            .build();
                                } else {
                                    c.setUserId(user.getId());
                                    JPAEntry.genericPut(c);
                                    result = Response.ok().build();
                                }
                            } else {
                                result = Response.status(405).build();
                            }
                            break;
                        default:
                            break;
                    }
                } else {
                    result = Response.status(410).build();
                }
                break;
            default:
                result = Response.status(520).build();
                break;
        }
       *//* EntityManager em = JPAEntry.getNewEntityManager();
        em.getTransaction().begin();
        for (ValidationCode validationCode : validationCodes) {
            em.remove(validationCode);
        }
        em.getTransaction().commit();
        em.close();*//*
        //Response result = Response.status(500).build();
        return result;
    }*/

    static void queryBalance() {
        //http://IP:PORT/msg/QueryBalance?account=a&pswd=p

//        return body
//        20130303180000,0
//        102 密码错
//        103 查询过快（10秒查询一次）
//        
    }

//    @POST
//    @Path("cards")
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response queryValidCode(@CookieParam("sessionId") String sessionId, ActiveCard ac) {
//        Response result = Response.status(500).build();
//        //默认有数据库表有多个用户名和密码重复的数据
//        Map<String, Object> condition = new HashMap<>();
//        condition.put("no", ac.cardCode);
//        condition.put("password", ac.password);
//        List<Card> cs = JPAEntry.getList(Card.class, condition);
//        switch (cs.size()) {
//            case 0:
//                result = Response.status(404).build();
//                //找不到用户名和密码
//                break;
//            case 1:
//                //step2: query valid table, va   lid valid code
//                Map<String, Object> ValidationCodecondition = new HashMap<>();
//                ValidationCodecondition.put("phoneNumber", ac.phonecode);
//                ValidationCodecondition.put("validCode", ac.validationCode);
//                List<ValidationCode> validationCodes = JPAEntry.getList(ValidationCode.class, ValidationCodecondition);
//                switch (validationCodes.size()) {
//                    case 0:
//                        result = Response.status(401).build();
//                        //找不到该手机号和验证码
//                        break;
//                    case 1:
//                        Date now = new Date();
//                        Date sendTime = validationCodes.get(0).getTimestamp();
//                        if (now.getTime() > 60 * 3 * 1000 + sendTime.getTime()) {
//                            result = Response.status(410).build();
//                            //一验证码时间超时
//                        } else {
//                            //step3.0: query sessions table, get user.id
////                            Session s = JPAEntry.getObject(Session.class, "identity", sessionId);
////                            if (s == null) {
////                                result = Response.status(412).build();
////                                //缺少sessionID
////                            } else {
//                                //step3: update cards table, state, amount, cards'user_id -> user.id
//                                Card card = new Card();
//                                Card c = cs.get(0);
//                                if (c.getUserId() == null) {
//                                    c.setActiveTime(now);
//                                    c.setEndTime(now);
//                                    c.setAmount(588.0);
//                                    c.setUserId(ac.getUserId());
//                                    JPAEntry.genericPut(c);
//                                    result = Response.ok().build();
//                                    //代码执行成功
//                                } else {
//                                    result = Response.status(405).build();
//                                }
////                            }
//                        }
//                        break;
//                    default:
//                        result = Response.status(520).build();
//                        //数据库表有多个手机号和验证码重复的数据
//                        break;
//                }
//                EntityManager em = JPAEntry.getEntityManager();
//                em.getTransaction().begin();
//                for (ValidationCode validationCode : validationCodes) {
//                    em.remove(validationCode);
//                }
//                em.getTransaction().commit();
//                break;
//        }
//        return result;
//    }

    @GET
    @Path("smsStateNotification")
    public SmsState smsStateNotification() {
        //http://pushUrl?receiver=admin&pswd=12345&msgid=12345&reportTime=1012241002&mobile=13900210021&status=DELIVRD

//        DELIVRD 短消息转发成功
//        EXPIRED 短消息超过有效期
//        UNDELIV 短消息是不可达的
//        UNKNOWN 未知短消息状态
//        REJECTD 短消息被短信中心拒绝
//        DTBLACK 目的号码是黑名单号码
//        ERR:104 系统忙
//        REJECT 审核驳回
//        其他 网关内部状态
//
        return null;
    }

    @GET
    @Path("smsReceiver")
    public SmsReceiverState smsReceiver() {
        //http://pushMoUrl?receiver=admin&pswd=12345&moTime=1208212205&mobile=13800210021&msg=hello&destcode=10657109012345
        return null;
    }

    @POST //添
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(@CookieParam("userId") String userId, User user) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            user.setId(IdGenerator.getNewId());
            Date now = new Date();
            user.setCreateTime(now);
            user.setUpdateTime(now);
            JPAEntry.genericPost(user);
            result = Response.ok(user).build();
        }
        return result;
    }

    @GET //根据条件查询
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsers(@CookieParam("userId") String userId, @QueryParam("filter") @DefaultValue("") String filter) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            result = Response.status(404).build();
            Map<String, Object> filterObject = CharacterEncodingFilter.getFilters(filter);
            List<User> users = JPAEntry.getList(User.class, filterObject);
            if (!users.isEmpty()) {
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                result = Response.ok(gson.toJson(users)).build();
            }
        }
        return result;
    }

    @GET //根据条件查询
    @Path("no/userId/code")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser(@QueryParam("filter") @DefaultValue("") String filter) {
        Response result = Response.status(404).build();
        Map<String, Object> filterObject = CharacterEncodingFilter.getFilters(filter);
        List<User> users = JPAEntry.getList(User.class, filterObject);
        if (!users.isEmpty()) {
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            result = Response.ok(gson.toJson(users)).build();
        }
        return result;
    }

    @POST
    @Path("phone/Verification")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getv(ActiveCard activeCard) {
        Response result = result = Response.status(404).build();
        Map<String, Object> validationCodeConditions = new HashMap<>();
        validationCodeConditions.put("phoneNumber", activeCard.getPhoneNumber());
        validationCodeConditions.put("validCode", activeCard.getValidCode());
        List<ValidationCode> validationCodes = JPAEntry.getList(ValidationCode.class, validationCodeConditions);
        Map<String, Object> validationCode = new HashMap<>();
        validationCode.put("telephone", activeCard.getPhoneNumber());
        if (!validationCodes.isEmpty()) {
            Date now = new Date();
            Date sendTime = validationCodes.get(0).getTimestamp();
            if (now.getTime() < 60 * 3 * 1000 + sendTime.getTime()) {
                List<User> users = JPAEntry.getList(User.class, validationCode);
                if (!users.isEmpty()) {
                    Session session = PublicAccounts.putSession(now, users.get(0).getId());
                    result = Response.ok()
                            .cookie(new NewCookie("userId", users.get(0).getId().toString(), "/api", null, null, NewCookie.DEFAULT_MAX_AGE, false))
                            .cookie(new NewCookie("sessionId", session.getIdentity(), "/api", null, null, NewCookie.DEFAULT_MAX_AGE, false))
                            .build();
                } else {
                    User user = new User();
                    user.setId(IdGenerator.getNewId());
                    user.setCreateTime(now);
                    user.setTelephone(activeCard.getPhoneNumber());
                    user.setUpdateTime(now);
                    user.setName("");
                    user.setSex(0l);
                    JPAEntry.genericPost(user);
                    Session session = PublicAccounts.putSession(now, user.getId());
                    result = Response.ok()
                            .cookie(new NewCookie("userId", user.getId().toString(), "/api", null, null, NewCookie.DEFAULT_MAX_AGE, false))
                            .cookie(new NewCookie("sessionId", session.getIdentity(), "/api", null, null, NewCookie.DEFAULT_MAX_AGE, false))
                            .build();
                }
            } else {
                result = Response.status(405).build();
            }
        }
        return result;
    }

    /*  //PublicAccounts.putSession(now,)
      Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
      result = Response.ok(gson.toJson(validationCodes)).build();*/
    @GET //根据id查询
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@CookieParam("userId") String userId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            User admin = JPAEntry.getObject(User.class, "id", Long.parseLong(userId));
            if (admin != null && admin.getIsAdministrator()) {
                result = Response.status(404).build();
                User user = JPAEntry.getObject(User.class, "id", id);
                if (user != null) {
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                    result = Response.ok(gson.toJson(user)).build();
                }
            }
        }
        return result;
    }

    @GET //根据id查询
    @Path("cards/{no}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getByNo(@CookieParam("userId") String userId, @PathParam("no") String no) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            Card card = JPAEntry.getObject(Card.class, "no", no);
            if (card != null) {
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                    result = Response.ok(gson.toJson(card)).build();
                }
            }
             return result;
        }



    @GET //根据id查询
    @Path("self")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSelf(@CookieParam("userId") String userId) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            Long id = Long.parseLong(userId);
            User user = JPAEntry.getObject(User.class, "id", id);
            if (user != null) {
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                result = Response.ok(gson.toJson(user)).build();
            }
        }
        return result;
    }

    @PUT //根据id修改
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUser(@CookieParam("userId") String userId, @PathParam("id") Long id, /*byte[] userInfo*/User user) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            result = Response.status(404).build();
            User existuser = JPAEntry.getObject(User.class, "id", id);
            if (existuser != null) {
                float amount = user.getAmount();
                existuser.setAmount(amount);

                Date birthday = user.getBirthday();
                if (birthday != null) {
                    existuser.setBirthday(birthday);
                }
                String description = user.getDescription();
                if (description != null) {
                    existuser.setDescription(description);
                }
                String email = user.getEmail();
                if (email != null) {
                    existuser.setEmail(email);
                }
                String head = user.getHead();
                if (head != null) {
                    existuser.setHead(head);
                }
                Boolean isAdministrator = user.getIsAdministrator();
                if (isAdministrator != null) {
                    existuser.setIsAdministrator(isAdministrator);
                }
                String location = user.getLocation();
                if (location != null) {
                    existuser.setLocation(location);
                }
                String loginName = user.getLoginName();
                if (loginName != null) {
                    existuser.setLoginName(loginName);
                }
                String password = user.getPassword();
                if (password != null) {
                    existuser.setPassword(password);
                }
                Long sex = user.getSex();
                if (sex != null) {
                    existuser.setSex(sex);
                }
                String timezone = user.getTimezone();
                if (timezone != null) {
                    existuser.setTimezone(timezone);
                }
                String classname = user.getClassname();
                if (classname != null) {
                    existuser.setClassname(classname);
                }
                String grade = user.getGrade();
                if (grade != null) {
                    existuser.setGrade(grade);
                }
                String name = user.getName();
                if (name != null) {
                    existuser.setName(name);
                }
                String school = user.getSchool();
                if (school != null) {
                    existuser.setSchool(school);
                }
                String telephone = user.getTelephone();
                if (telephone != null) {
                    existuser.setTelephone(telephone);
                }
                Date now = new Date();
                existuser.setUpdateTime(now);
                String site = user.getSite();
                if (site != null) {
                    existuser.getSite();
                }
                JPAEntry.genericPut(existuser);
                result = Response.ok(existuser).build();
            }
        }
        return result;
    }

    public static class SmsState {
        public String receiver;
        public String password;
        public String messageId;
        public String reportTime;
        public String mobile;
        public String status;
    }

    public static class SmsReceiverState {
        public String receiver;
        public String password;
        public String message;
        public String moTime;
        public String mobile;
        public String destinationCode;
    }
}
