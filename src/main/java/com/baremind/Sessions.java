package com.baremind;

import com.baremind.data.*;
import com.baremind.utils.IdGenerator;
import com.baremind.utils.Impl;
import com.baremind.utils.JPAEntry;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

@Path("sessions")
public class Sessions {
    @GET //根据id查询
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            User admin = JPAEntry.getObject(User.class, "id", JPAEntry.getLoginId(sessionId));
            if (admin != null && admin.getIsAdministrator()) {
                result = Impl.getById(sessionId, id, Session.class, null);
            }
        }
        return result;
    }

    @GET //根据id查询
    @Path("self")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSelf(@CookieParam("sessionId") String sessionId) {
        return Impl.getSelf(sessionId);
    }

    private static class LoginInfo {
        private String type;
        private String info;
        private String key;
        private String deviceType;
        private String deviceNo;
        private String openId;
        private String code;
        private String ip;

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        String getDeviceType() {
            return deviceType;
        }

        public void setDeviceType(String deviceType) {
            this.deviceType = deviceType;
        }

        String getDeviceNo() {
            return deviceNo;
        }

        public void setDeviceNo(String deviceNo) {
            this.deviceNo = deviceNo;
        }

        String getOpenId() {
            return openId;
        }

        public void setOpenId(String openId) {
            this.openId = openId;
        }
    }

    private User insertUser(Date now, String telephone, WechatUser wechatUser) {
        User user = new User();
        user.setId(IdGenerator.getNewId());
        user.setCreateTime(now);
        user.setUpdateTime(now);
        user.setTelephone(telephone);
        user.setIsAdministrator(false);
        user.setSite("http://www.xiaoyuzhishi.com");
        user.setAmount(0l);
        PublicAccounts.fillUserByWechatUser(user, wechatUser);
        JPAEntry.genericPost(user);
        return user;
    }

    private Response loginImpl(LoginInfo loginInfo) {
        Response result = Response.status(404).build();
        Date now = new Date();
        User user = null;
        Map<String, Object> conditions = new HashMap<>();
        Map<String, Object> filter = new HashMap<>();

        switch (loginInfo.getType()) {
            case "validationCode":
                conditions.put("phoneNumber", loginInfo.getInfo());
                conditions.put("validCode", loginInfo.getKey());
                List<ValidationCode> validationCodes = JPAEntry.getList(ValidationCode.class, conditions);

                if (!validationCodes.isEmpty()) {

                    Date sendTime = validationCodes.get(0).getTimestamp();
                    if (now.getTime() < 60 * 3 * 1000 + sendTime.getTime()) {
                        user = JPAEntry.getObject(User.class, "telephone", loginInfo.getInfo());
                        if (user == null) {
                            //create user via openId
                            WechatUser wechatUser = JPAEntry.getObject(WechatUser.class, "openId", loginInfo.getOpenId());
                            user = insertUser(now, loginInfo.getInfo(), wechatUser);
                        }
                    }else{
                        return result = Response.status(405).build();
                    }
                }
                break;
            case "telephone":
                User telephone = JPAEntry.getObject(User.class, "telephone", loginInfo.getInfo());
                if(telephone != null){
                    if(telephone.getLogonCount() <= 5){
                        conditions.put("telephone", loginInfo.getInfo());
                        conditions.put("password", loginInfo.getKey());
                        user = JPAEntry.getObject(User.class, conditions);

                        if (user == null) {
                            //create user via openId
                           /* WechatUser wechatUser = JPAEntry.getObject(WechatUser.class, "openId", loginInfo.getOpenId());
                            user = insertUser(now, loginInfo.getInfo(), wechatUser);*/
                            telephone.setLogonCount(telephone.getLogonCount() + 1l);
                            JPAEntry.genericPut(telephone);
                            return  result;//用户名或密码错误
                        }
                    }else
                        if(telephone.getLogonCount() > 5 && loginInfo.getCode() == null) {
                        return  result = Response.status(410).build();//登录次数超过五次
                    }else
                        if(telephone.getLogonCount() > 5 && loginInfo.getCode() != null) {
                            conditions.put("telephone", loginInfo.getInfo());
                            conditions.put("password", loginInfo.getKey());
                            user = JPAEntry.getObject(User.class, conditions);
                            filter.put("ip", loginInfo.getIp());
                            filter.put("code", loginInfo.getCode());
                            ValidationCode validationCode = JPAEntry.getObject(ValidationCode.class, filter);
                            if (user == null) {
                                return result;//用户名或密码错误
                            }
                            if (validationCode == null) {
                                return result = Response.status(407).build();//验证码错误
                            }
                        }
                }else{
                     return  result = Response.status(412).build();//手机没有激活
                }


                break;
            case "name":
                conditions.put("loginName", loginInfo.getInfo());
                conditions.put("password", loginInfo.getKey());
                user = JPAEntry.getObject(User.class, conditions);
                break;
        }
        if (user != null) {
            Map<String, Object> deviceConditions = new HashMap<>();
            conditions.put("platform", loginInfo.getDeviceType());
            conditions.put("platformIdentity", loginInfo.getDeviceNo());
            Device device = JPAEntry.getObject(Device.class, deviceConditions);
            if (device == null) {
                device = new Device();
                device.setId(IdGenerator.getNewId());
                device.setPlatform(loginInfo.getDeviceType());
                device.setPlatformIdentity(loginInfo.getDeviceNo());
                device.setPlatformNotificationToken("");
                device.setUserId(user.getId());
                JPAEntry.genericPost(device);
            }
            Session session = PublicAccounts.putSession(now, user.getId(), device.getId());
            result = Response.ok(session)
                    .cookie(new NewCookie("sessionId", session.getIdentity(), "/api", null, null, NewCookie.DEFAULT_MAX_AGE, false))
                    .build();
        }else{
            result = Response.status(406).build();//手机号或密码错误
        }

        return result;
    }


    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response recreate(LoginInfo loginInfo) {
        return loginImpl(loginInfo);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(LoginInfo loginInfo) {
        return loginImpl(loginInfo);
    }

    private static class Updater implements BiConsumer<Session, Session> {
        @Override
        public void accept(Session exist, Session session) {
            String identity = session.getIdentity();
            if (identity != null) {
                exist.setIdentity(identity);
            }
            Long userId = session.getUserId();
            if (userId != null) {
                exist.setUserId(userId);
            }
            Long deviceId = session.getDeviceId();
            if (deviceId != null) {
                exist.setDeviceId(deviceId);
            }
            String ip = session.getIp();
            if (ip != null) {
                exist.setIp(ip);
            }
            Date lastOperationTime = session.getLastOperationTime();
            if (lastOperationTime != null) {
                exist.setLastOperationTime(lastOperationTime);
            }
        }
    }

    @PUT //根据id修改
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id, Session newData) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            User admin = JPAEntry.getObject(User.class, "id", JPAEntry.getLoginId(sessionId));
            if (admin != null && admin.getIsAdministrator()) {
                result = Impl.updateById(sessionId, id, newData, Session.class, new Updater(), null);
            }
        }
        return result;
    }

    @PUT //根据token修改
    @Path("self")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateSelf(@CookieParam("sessionId") String sessionId, Session newData) {
        return Impl.updateSelf(sessionId, newData, new Updater());
    }

    @DELETE
    @Path("{id}")
    public Response deleteById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            User admin = JPAEntry.getObject(User.class, "id", JPAEntry.getLoginId(sessionId));
            if (admin != null && admin.getIsAdministrator()) {
                result = Impl.deleteById(sessionId, id, Session.class);
            }
        }
        return result;
    }

    @DELETE
    @Path("self")
    public Response deleteSelf(@CookieParam("sessionId") String sessionId) {
        return Impl.deleteSelf(sessionId);
    }
}
