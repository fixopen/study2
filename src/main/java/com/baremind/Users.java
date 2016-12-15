package com.baremind;

import com.baremind.data.*;
import com.baremind.utils.IdGenerator;
import com.baremind.utils.Impl;
import com.baremind.utils.JPAEntry;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@Path("users")
public class Users {
    @GET //根据id查询
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        Response result = Impl.validationAdmin(sessionId);
        if (result.getStatus() == 202) {
            result = Response.status(404).build();
            User user = JPAEntry.getObject(User.class, "id", id);
            if (user != null) {
                result = Impl.finalResult(user, null);
            }
        }
        return result;
    }

    @GET //根据id查询
    @Path("self")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSelf(@CookieParam("sessionId") String sessionId) {
        return Impl.getUserSelf(sessionId);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@CookieParam("sessionId") String sessionId, User entity) {
        return Impl.create(sessionId, entity, (user) -> {
            Date now = new Date();
            user.setCreateTime(now);
            user.setUpdateTime(now);
            return user;
        }, null);
    }

    private static class Updater implements BiConsumer<User, User> {
        @Override
        public void accept(User existUser, User userData) {
            Long amount = userData.getAmount();
            if (amount != null) {
                existUser.setAmount(amount);
            }

            Date birthday = userData.getBirthday();
            if (birthday != null) {
                existUser.setBirthday(birthday);
            }
            String classname = userData.getClassname();
            if (classname != null) {
                existUser.setClassname(classname);
            }
            String description = userData.getDescription();
            if (description != null) {
                existUser.setDescription(description);
            }
            String email = userData.getEmail();
            if (email != null) {
                existUser.setEmail(email);
            }
            String grade = userData.getGrade();
            if (grade != null) {
                existUser.setGrade(grade);
            }
            String head = userData.getHead();
            if (head != null) {
                existUser.setHead(head);
            }
            Boolean isAdministrator = userData.getIsAdministrator();
            if (isAdministrator != null) {
                existUser.setIsAdministrator(isAdministrator);
            }
            String location = userData.getAddress();
            if (location != null) {
                existUser.setAddress(location);
            }
            String loginName = userData.getLoginName();
            if (loginName != null) {
                existUser.setLoginName(loginName);
            }
            String name = userData.getName();
            if (name != null) {
                existUser.setName(name);
            }
            String password = userData.getPassword();
            if (password != null) {
                existUser.setPassword(password);
            }
            String school = userData.getSchool();
            if (school != null) {
                existUser.setSchool(school);
            }
            Integer sex = userData.getSex();
            if (sex != null) {
                existUser.setSex(sex);
            }
            String telephone = userData.getTelephone();
            if (telephone != null) {
                existUser.setTelephone(telephone);
            }
            String timezone = userData.getTimezone();
            if (timezone != null) {
                existUser.setTimezone(timezone);
            }
            Date now = new Date();
            existUser.setUpdateTime(now);
            String site = userData.getSite();
            if (site != null) {
                existUser.getSite();
            }
        }
    }

    @PUT //根据id修改
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id, User newData) {
        return Impl.updateById(sessionId, id, newData, User.class, new Updater(), null);
    }


    @PUT //根据token修改
    @Path("self")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateSelf(@CookieParam("sessionId") String sessionId, User newData) {
        return Impl.updateUserSelf(sessionId, newData, new Updater());
    }

    @DELETE
    @Path("{id}")
    public Response deleteById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        return Impl.deleteById(sessionId, id, User.class);
    }

    @DELETE
    @Path("self")
    public Response deleteSelf(@CookieParam("sessionId") String sessionId) {
        return Impl.deleteUserSelf(sessionId);
    }

    @GET
    @Path("{id}/cards")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCards(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        Response result = Impl.validationAdmin(sessionId);
        if (result.getStatus() == 202) {
            result = Response.status(404).build();
            List<Card> cards = JPAEntry.getList(Card.class, "userId", id);
            if (!cards.isEmpty()) {
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                result = Response.ok(gson.toJson(cards)).build();
            }
        }
        return result;
    }

    @GET
    @Path("self/cards")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSelfCards(@CookieParam("sessionId") String sessionId) {
        Response result = Impl.validationUser(sessionId);
        if (result.getStatus() == 202) {
            result = Response.status(404).build();
            List<Card> cards = JPAEntry.getList(Card.class, "userId", JPAEntry.getLoginUser(sessionId).getId());
            if (!cards.isEmpty()) {
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                result = Response.ok(gson.toJson(cards)).build();
            }
        }
        return result;
    }

    private static class ActiveCard {
        private String cardNo;
        private String password;
        private String phoneNumber;
        private String validCode;
        private String wechatUserId;

        public String getWechatUserId() {
            return wechatUserId;
        }

        public void setWechatUserId(String wechatUserId) {
            this.wechatUserId = wechatUserId;
        }

        String getCardNo() {
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

        String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        String getValidCode() {
            return validCode;
        }

        public void setValidCode(String validCode) {
            this.validCode = validCode;
        }
    }

    private Response activeCardImpl(Long id, ActiveCard ac) {
        Response result = Response.status(412).build();
        User user = JPAEntry.getObject(User.class, "id", id);
        if (user != null) {
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
                    String phoneNumber = user.getTelephone();
                    //Logs.insert(id, "log", logId, "card exists");
                    User telephoneUser = JPAEntry.getObject(User.class, "telephone", phoneNumber);
                    if (telephoneUser != null && telephoneUser.getId().longValue() != id.longValue()) {
                        WechatUser wechatUser = JPAEntry.getObject(WechatUser.class, "userId", id);
                        WechatUser errorWechatUser = JPAEntry.getObject(WechatUser.class, "userId", telephoneUser.getId());
                        if (wechatUser.getOpenId().equals(errorWechatUser.getOpenId())) {
                            List<Card> bindedCards = JPAEntry.getList(Card.class, "userId", id);
                            EntityManager em = JPAEntry.getNewEntityManager();
                            em.getTransaction().begin();
                            for (Card card : bindedCards) {
                                card.setUserId(id);
                                em.merge(card);
                            }
                            em.remove(errorWechatUser);
                            em.remove(telephoneUser);
                            em.getTransaction().commit();
                            em.close();
                            Logs.insert(telephoneUser.getId(), "move-card", telephoneUser.getId(), phoneNumber);
                        }
                    }
                    user.setTelephone(phoneNumber);
                    JPAEntry.genericPut(user);
                    Card c = cs.get(0);
                    if (c.getActiveTime() == null) {
                        //Logs.insert(id, "log", logId, "card success");
                        Date now = new Date();
                        c.setActiveTime(now);
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(now);
                        cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + 1);
                        Date oneYearAfter = cal.getTime();
                        c.setEndTime(oneYearAfter);
                        c.setAmount(5880L);
                        if (ac.getCardNo().startsWith("03")) {
                            c.setAmount(1680L);
                        }
                        c.setUserId(id);
                        JPAEntry.genericPut(c);
                        result = Response.ok(c).build();
                    } else {
                        //Logs.insert(id, "log", logId, "card already active");
                        result = Response.status(405).build();
                    }
                    break;
                default:
                    //Logs.insert(id, "log", logId, "card multiple exists");
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
    public Response activeCard(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id, ActiveCard ac) {
        Response result = Impl.validationAdmin(sessionId);
        if (result.getStatus() == 202) {
            result = activeCardImpl(id, ac);
        }
        return result;
    }

    @POST
    @Path("self/cards")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response activeCard(@CookieParam("sessionId") String sessionId, ActiveCard ac) {
        Response result = Impl.validationAdmin(sessionId);
        if (result.getStatus() == 202) {
            User user = JPAEntry.getLoginUser(sessionId);
            result = activeCardImpl(user.getId(), ac);
        }
        return result;
    }

    @POST
    @Path("simplify-card")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response simplifyCard(@CookieParam("sessionId") String sessionId,ActiveCard ac) {
        Response result = Response.status(412).build();
        User user = JPAEntry.getObject(User.class, "id", JPAEntry.getLoginId(sessionId));
        Date now = new Date();
        if(user.getTelephone() != null){
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
                        c.setUserId(user.getId());
                        c.setActiveTime(now);
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(now);
                        cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + 1);
                        Date oneYearAfter = cal.getTime();
                        c.setEndTime(oneYearAfter);
                        c.setAmount(5880L);
                        if (ac.getCardNo().startsWith("03")) {
                            c.setAmount(1680L);
                        }
                        JPAEntry.genericPut(c);
                        result = Response.ok(c).build();
                        break;
                    }
                default:
                    break;
            }
        }
        return  result;
    }
    @POST
    @Path("cards")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response firstActiveCard(ActiveCard ac) {
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
                                cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + 1);
                                Date oneYearAfter = cal.getTime();
                                c.setEndTime(oneYearAfter);
                                c.setAmount(5880L);
                                if (ac.getCardNo().startsWith("03")) {
                                    c.setAmount(1680L);
                                }
                                User user = JPAEntry.getObject(User.class, "telephone", ac.getPhoneNumber());
                                WechatUser wechatUser = JPAEntry.getObject(WechatUser.class, "id", ac.getWechatUserId());
                                if (user == null && wechatUser != null) {
                                    user = new User();

                                    user.setId(IdGenerator.getNewId());
                                    user.setTelephone(ac.getPhoneNumber());
                                    user.setLoginName(ac.getPhoneNumber());
                                    user.setCreateTime(now);
                                    user.setUpdateTime(now);
                                    user.setName(wechatUser.getNickname());
                                    user.setSex(wechatUser.getSex());
                                    user.setAmount(0l);
                                    user.setHead(wechatUser.getHead());
                                    wechatUser.setUserId(user.getId());
                                    JPAEntry.genericPut(wechatUser);
                                    JPAEntry.genericPost(user);
                                    c.setUserId(user.getId());
                                    JPAEntry.genericPut(c);
                                } else {
                                    c.setUserId(user.getId());
                                    JPAEntry.genericPut(c);
                                }
                                Session s = PublicAccounts.putSession(new Date(), user.getId(), 0L); //@@deviceId is temp zero
                                result = Response.ok(c)
                                    //.cookie(new NewCookie("userId", user.getId().toString(), "/api", null, null, NewCookie.DEFAULT_MAX_AGE, false))
                                    .cookie(new NewCookie("sessionId", s.getIdentity(), "/api", null, null, NewCookie.DEFAULT_MAX_AGE, false))
                                    .build();
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
        return result;
    }

    private static class Sms {
        private static class SendMessageResult {
            private String time;
            private String code;
            private String messageId;
        }

        private static SendMessageResult sendMessage(String phoneNumber, String validInfo) {
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
            String hostname = "https://sapi.253.com";
            String username = "zhibo1";
            String password = "Tch243450";
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

        static void queryBalance() {
            //http://IP:PORT/msg/QueryBalance?account=a&pswd=p

//        return body
//        20130303180000,0
//        102 密码错
//        103 查询过快（10秒查询一次）
//
        }

        private static class SmsState {
            public String receiver;
            public String password;
            public String messageId;
            public String reportTime;
            public String mobile;
            public String status;
        }

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

        private static class SmsReceiverState {
            public String receiver;
            public String password;
            public String message;
            public String moTime;
            public String mobile;
            public String destinationCode;
        }

        @GET
        @Path("smsReceiver")
        public SmsReceiverState smsReceiver() {
            //http://pushMoUrl?receiver=admin&pswd=12345&moTime=1208212205&mobile=13800210021&msg=hello&destcode=10657109012345
            return null;
        }
    }

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
        Sms.SendMessageResult r = Sms.sendMessage(telephone, "《小雨知时》" + sjs + "(动态验证码),请在3分钟内使用");
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

    @GET
    @Path("teachers")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTeachers(@CookieParam("sessionId") String sessionId) {
        Response result = Impl.validationUser(sessionId);
        if (result.getStatus() == 202) {
            result = Response.status(404).build();
            EntityManager em = JPAEntry.getEntityManager();
            String contentCountQuery = "SELECT s.teacherId FROM Scheduler s";
            TypedQuery<Long> cq = em.createQuery(contentCountQuery, Long.TYPE);
            final List<Long> teacherIds = cq.getResultList();
            final List<String> teacherIdsString = teacherIds.stream().map(Object::toString).collect(Collectors.toList());
            List<User> teachers = Resources.getList(em, "id", teacherIdsString, User.class);
            if (!teachers.isEmpty()) {
                result = Impl.finalResult(teachers, null, null);
            }
        }
        return result;
    }

    static TransferObject getByTypeAndId(String type, Long id) {
        TransferObject result = null;
        switch (type) {
            case "user":
                result = JPAEntry.getObject(User.class, "id", id);
                break;
            case "card":
                result = JPAEntry.getObject(Card.class, "id", id);
                break;
        }
        return result;
    }


    /*@POST
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
    }*/
}
