package com.baremind;

import com.baremind.data.Scheduler;
import com.baremind.data.User;
import com.baremind.utils.CharacterEncodingFilter;
import com.baremind.utils.Impl;
import com.baremind.utils.JPAEntry;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;


//GET /api/schedulers/this-week
//GET /api/schedulers/34
//GET /api/schedulers?filter={"week":34,"year":2014}

@Path("schedulers")
public class Schedulers {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@CookieParam("sessionId") String sessionId, @QueryParam("filter") @DefaultValue("") String filter) {
        Map<String, String> orders = new HashMap<>();
        orders.put("startTime", "DESC");
        return Impl.get(sessionId, filter, orders, Scheduler.class, null, null);
    }

    @GET //根据id查询
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        return Impl.getById(sessionId, id, Scheduler.class, null);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@CookieParam("sessionId") String sessionId, Scheduler entity) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            User admin = JPAEntry.getObject(User.class, "id", JPAEntry.getLoginId(sessionId));
            if (admin != null && admin.getIsAdministrator()) {
                result = Impl.create(sessionId, entity, null);
            }
        }
        return result;
    }

    @PUT //根据id修改
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id, Scheduler newData) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            User admin = JPAEntry.getObject(User.class, "id", JPAEntry.getLoginId(sessionId));
            if (admin != null && admin.getIsAdministrator()) {
                result = Impl.updateById(sessionId, id, newData, Scheduler.class, (exist, scheduler) -> {
                    Integer year = scheduler.getYear();
                    if (year != null) {
                        exist.setYear(year);
                    }
                    Integer week = scheduler.getWeek();
                    if (week != null) {
                        exist.setWeek(week);
                    }
                    Integer day = scheduler.getDay();
                    if (day != null) {
                        exist.setDay(day);
                    }
                    Date startTime = scheduler.getStartTime();
                    if (startTime != null) {
                        exist.setStartTime(startTime);
                    }
                    Date endTime = scheduler.getEndTime();
                    if (endTime != null) {
                        exist.setEndTime(endTime);
                    }
                    Long subjectId = scheduler.getSubjectId();
                    if (subjectId != null) {
                        exist.setSubjectId(subjectId);
                    }
                    Integer grade = scheduler.getGrade();
                    if (grade != null) {
                        exist.setGrade(grade);
                    }
                    String title = scheduler.getName();
                    if (title != null) {
                        exist.setName(title);
                    }
                    Long cover = scheduler.getCoverId();
                    if (cover != null) {
                        exist.setCoverId(cover);
                    }
                    String cdnLink = scheduler.getContentLink();
                    if (cdnLink != null) {
                        exist.setContentLink(cdnLink);
                    }
                    String directLink = scheduler.getDirectLink();
                    if (directLink != null) {
                        exist.setDirectLink(directLink);
                    }
                    String description = scheduler.getDescription();
                    if (description != null) {
                        exist.setDescription(description);
                    }
                    String teacher = scheduler.getTeacher();
                    if (teacher != null) {
                        exist.setTeacher(teacher);
                    }
                    String teacherDescription = scheduler.getTeacherDescription();
                    if (teacherDescription != null) {
                        exist.setTeacherDescription(teacherDescription);
                    }
                }, null);
            }
        }
        return result;
    }

    @DELETE
    @Path("{id}")
    public Response deleteById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            User admin = JPAEntry.getObject(User.class, "id", JPAEntry.getLoginId(sessionId));
            if (admin != null && admin.getIsAdministrator()) {
                result = Impl.deleteById(sessionId, id, Scheduler.class);
            }
        }
        return result;
    }

    private Long findSubjectIdByName(String k) {
        Long result = null;
        switch (k) {
            case "语文":
                result = 1L;
                break;
            case "数学":
                result = 2L;
                break;
        }
        return result;
    }

    private Long findGradeByName(String k) {
        Long result = null;
        switch (k) {
            case "低年级":
                result = 20L;
                break;
            case "高年级":
                result = 21L;
                break;
            case "一年级":
                result = 1L;
                break;
            case "二年级":
                result = 2L;
                break;
            case "三年级":
                result = 3L;
                break;
            case "四年级":
                result = 4L;
                break;
            case "五年级":
                result = 5L;
                break;
            case "六年级":
                result = 6L;
                break;
        }
        return result;
    }

    @GET //根据科目查询老师
    @Path("teachers")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTeacher(@CookieParam("sessionId") String sessionId, @QueryParam("filter") String filter) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            Map<String, Object> filterObject = CharacterEncodingFilter.getFilters(filter);
            EntityManager em = JPAEntry.getEntityManager();
            String stats = "SELECT l.teacher FROM Scheduler l WHERE l.subjectId = :subjectId GROUP BY l.teacher";
            TypedQuery<String> q = em.createQuery(stats, String.class);
            q.setParameter("subjectId", filterObject.get("subjectId"));
            result = Response.ok(new Gson().toJson(q.getResultList())).build();
        }
        return result;
    }

    @GET //根据科目查询年级
    @Path("grades")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGrades(@CookieParam("sessionId") String sessionId, @QueryParam("filter") String filter) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            Map<String, Object> filterObject = CharacterEncodingFilter.getFilters(filter);
            EntityManager em = JPAEntry.getEntityManager();
            String stats = "SELECT l.grade FROM Scheduler l WHERE l.subjectId = :subjectId GROUP BY l.grade";
            TypedQuery<Long> q = em.createQuery(stats, Long.class);
            q.setParameter("subjectId", filterObject.get("subjectId"));
            result = Response.ok(new Gson().toJson(q.getResultList())).build();
        }
        return result;
    }

    @GET //根据周查询课表
    @Path("keywords/{keywords}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getByKeywords(@CookieParam("sessionId") String sessionId, @PathParam("keywords") String keywords) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            String[] keywordArray = keywords.split(" ");
            EntityManager em = JPAEntry.getEntityManager();
            String stats = "SELECT s FROM Scheduler s";
            boolean isFirst = true;
            for (String aKeywordArray : keywordArray) {
                if (isFirst) {
                    stats += " WHERE ";
                    isFirst = false;
                } else {
                    stats += " AND ";
                }
                stats += "((s.teacher = '" + aKeywordArray + "')";
                Long subjectId = findSubjectIdByName(aKeywordArray);
                if (subjectId != null) {
                    stats += " OR (s.subjectId = " + subjectId.toString() + ")";
                }
                Long grade = findGradeByName(aKeywordArray);
                if (grade != null) {
                    stats += " OR (s.grade = " + grade.toString() + ")";
                }
                stats += ")";
            }
            TypedQuery<Scheduler> q = em.createQuery(stats, Scheduler.class);
            List<Scheduler> schedulers = q.getResultList();
            result = Response.ok(new Gson().toJson(schedulers)).build();
        }
        return result;
    }

    @GET //根据周查询课表
    @Path("years/{year}/weeks/{week}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getWeekScheduler(@CookieParam("sessionId") String sessionId, @PathParam("year") Integer year, @PathParam("week") Integer week) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            Map<String, Object> filterObject = new HashMap<>(2);
            filterObject.put("year", year);
            filterObject.put("week", week);

            List<Scheduler> schedulers = JPAEntry.getList(Scheduler.class, filterObject);
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            //Gson gson = new GsonBuilder().registerTypeAdapter(java.sql.Time.class, new TimeTypeAdapter()).create();
            result = Response.ok(gson.toJson(schedulers)).build();
        }
        return result;
    }

//    @GET //根据条件查询课表
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response getjkihjhkghuSchedulers(@CookieParam("sessionId") String sessionId, @QueryParam("filter") @DefaultValue("") String filter) {
//        Response r = Response.status(401).build();
//        if (JPAEntry.isLogining(sessionId)) {
//            Map<String, Object> filterObject = CharacterEncodingFilter.getFilters(filter);
//            Map<String, String> orders = new HashMap<>();
//            orders.put("startTime", "DESC");
//            List<Scheduler> schedulers = JPAEntry.getList(Scheduler.class, filterObject, orders);
//            ArrayList<Scheduler> featured = new ArrayList<>();
//            ArrayList<Scheduler> playing = new ArrayList<>();
//            ArrayList<Scheduler> passed = new ArrayList<>();
//            Date now = new Date();
//            for (Scheduler scheduler : schedulers) {
//                if (now.before(scheduler.getStartTime())) {
//                    featured.add(scheduler);
//                } else {
//                    if (now.before(scheduler.getEndTime())) {
//                        playing.add(scheduler);
//                    } else {
//                        passed.add(scheduler);
//                    }
//                }
//            }
//            ArrayList<ArrayList<Scheduler>> result = new ArrayList<>();
//            result.add(playing);//正播
//            Collections.reverse(featured); // 倒序排列
//            result.add(featured); //未播
//            result.add(passed);//播过
//            //Gson gson = new GsonBuilder().registerTypeAdapter(java.sql.Time.class, new TimeTypeAdapter()).create();
//            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
//            r = Response.ok(gson.toJson(result)).build();
//        }
//        return r;
//    }

    @GET //获取classroom-key
    @Path("key")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getKey(@CookieParam("sessionId") String sessionId) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            String key = "";
            result = Response.ok("{\"key\":\"" + key + "\"}").build();
        }
        return result;
    }
}
