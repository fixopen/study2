package com.baremind;

import com.baremind.data.*;
import com.baremind.utils.CharacterEncodingFilter;
import com.baremind.utils.IdGenerator;
import com.baremind.utils.Impl;
import com.baremind.utils.JPAEntry;
import com.google.gson.Gson;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Predicate;

@Path("knowledge-points")
public class KnowledgePoints {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@Context HttpServletRequest req, @CookieParam("userId") String userId, @QueryParam("filter") @DefaultValue("") String filter) {
        req.getRemoteAddr();
        List<KnowledgePoint> r = JPAEntry.getList(KnowledgePoint.class, CharacterEncodingFilter.getFilters(filter));
        List<String> ids = new ArrayList<>();
        for (KnowledgePoint ri : r) {
            ids.add(ri.getId().toString());
        }
        List<KnowledgePoint.ContentStats> x = new ArrayList<>();
        KnowledgePoint.ContentStats v = new KnowledgePoint.ContentStats();
        v.setId(100L);
        v.setType("problem");
        v.setCount(3L);
        x.add(v);
        EntityManager em = JPAEntry.getEntityManager();
        String contentCountQuery = "SELECT m.knowledgePointId, m.type, count(m) FROM KnowledgePointContentMap m WHERE m.knowledgePointId IN (" + Resources.join(ids) + ") GROUP BY m.knowledgePointId, m.type";
        Query cq = em.createQuery(contentCountQuery, KnowledgePoint.ContentStats.class);
        final List<Object[]> contentStats = cq.getResultList();
        String likeCountQuery = "SELECT l.objectId, count(l) FROM Log l WHERE l.objectType = 'knowledge-point' AND l.objectId IN (" + Resources.join(ids) + ") AND l.action = 'like' GROUP BY l.objectId";
        Query lq = em.createQuery(likeCountQuery, KnowledgePoint.BaseStats.class);
        final List<Object[]> likeStats = lq.getResultList();
        String readCountQuery = "SELECT l.objectId, count(l) FROM Log l WHERE l.objectType = 'knowledge-point' AND l.objectId IN (" + Resources.join(ids) + ") AND l.action = 'read' GROUP BY l.objectId";
        Query rq = em.createQuery(readCountQuery, KnowledgePoint.BaseStats.class);
        final List<Object[]> readStats = lq.getResultList();
        String likedQuery = "SELECT l.objectId, count(l) FROM Log l WHERE l.objectType = 'knowledge-point' AND l.objectId IN (" + Resources.join(ids) + ") AND l.action = 'read' AND l.userId = " + JPAEntry.getLoginId(userId).toString() + " GROUP BY l.objectId";
        Query ldq = em.createQuery(likedQuery, KnowledgePoint.BaseStats.class);
        final List<Object[]> likedStats = ldq.getResultList();

        Map<String, String> orders = new HashMap<>();
        orders.put("order", "ASC");
//        final Date now = new Date();
//        final Date yesterday = Date.from(now.toInstant().plusSeconds(-24 * 3600));
        return Impl.get(userId, filter, orders, KnowledgePoint.class, knowledgePoint -> KnowledgePoint.convertToMap(knowledgePoint, likeStats, likedStats, readStats, contentStats), null);

//        Map<String, String> orders = new HashMap<>();
//        orders.put("order", "ASC");
//        final Date now = new Date();
//        final Date yesterday = Date.from(now.toInstant().plusSeconds(-24 * 3600));
//        return Impl.get(sessionId, filter, orders, KnowledgePoint.class, knowledgePoint -> KnowledgePoint.convertToMap(knowledgePoint, JPAEntry.getLoginId(sessionId), now, yesterday), (knowledgePoint) -> {
//            boolean result = true;
//            EntityManager em = JPAEntry.getEntityManager();
//            String stats = "SELECT COUNT(m) FROM KnowledgePointContentMap m WHERE m.knowledgePointId = " + knowledgePoint.getId().toString();
//            TypedQuery<Long> q = em.createQuery(stats, Long.class);
//            Long c = q.getSingleResult();
//            if (c == 0L) {
//                result = false;
//            }
//            return result;
//        });
    }
    private <T> T findItem(List<T> container, Predicate<T> p) {
        T result = null;
        for (T textItem : container) {
            if (p.test(textItem)) {
                result = textItem;
                break;
            }
        }
        return result;
    }

    private <T> List<T> findItems(List<T> container, Predicate<T> p) {
        List<T> result = new ArrayList<>();
        for (T textItem : container) {
            if (p.test(textItem)) {
                result.add(textItem);
            }
        }
        return result;
    }

    static String join(List<String> ids) {
        String result = "";
        boolean isFirst = true;
        for (String id : ids) {
            if (!isFirst) {
                result += ", ";
            }
            result += id;
            isFirst = false;
        }
        return result;
    }

    private <T> List<T> getList(EntityManager em, List<String> ids, Class<T> type) {
        return getListByColumn(em, "id", ids, type);
    }

    private <T> List<T> getListByColumn(EntityManager em, String columnName, List<String> ids, Class<T> type) {
        List<T> result = null;
        if (!ids.isEmpty()) {
            //String query = "SELECT * FROM " + tableName + " WHERE " + columnName + " IN ( " + join(ids) + " )";
            //Query pq = em.createNativeQuery(query, type);
            //result = pq.getResultList();
            String query = "SELECT o FROM " + type.getSimpleName() + " o WHERE o." + columnName + " IN ( " + join(ids) + " )";
            TypedQuery<T> pq = em.createQuery(query, type);
            result = pq.getResultList();
        }
        return result;
    }

    @PUT
    @Path("{id}/like")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response like(@CookieParam("userId") String userId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            Log log = Logs.insert(Long.parseLong(userId), "knowledge-point", id, "like");
            result = Response.ok(new Gson().toJson(log)).build();
        }
        return result;
    }

    @PUT
    @Path("{id}/unlike")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response unlike(@CookieParam("userId") String userId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            Long count = Logs.deleteLike(Long.parseLong(userId), "knowledge-point", id);
            result = Response.ok("{\"count\":" + count.toString() + "}").build();
        }
        return result;
    }

    @GET
    @Path("{id}/like-count")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLikeCount(@CookieParam("userId") String userId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            Long likeCount = Logs.getStatsCount("knowledge-point", id, "like");
            result = Response.ok("{\"count\":" + likeCount.toString() + "}").build();
        }
        return result;
    }

    @GET
    @Path("{id}/is-self-like")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSelfLike(@CookieParam("userId") String userId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            Boolean has = Logs.has(Long.parseLong(userId), "knowledge-point", id, "like");
            result = Response.ok("{\"like\":" + has.toString() + "}").build();
        }
        return result;
    }

    @GET
    @Path("{id}/read-count")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReadCount(@CookieParam("userId") String userId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            Long readCount = Logs.getStatsCount("knowledge-point", id, "read");
            result = Response.ok("{\"count\":" + readCount.toString() + "}").build();
        }
        return result;
    }

    @GET
    @Path("{id}/contents")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getKnowledgePointsByVolumeId(@CookieParam("userId") String userId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            result = Response.status(404).build();
            KnowledgePoint p = JPAEntry.getObject(KnowledgePoint.class, "id", id);
            if (p != null) {

                JPAEntry.log(Long.parseLong(userId), "read", "knowledge-point", id);

                Map<String, Object> conditions = new HashMap<>();
                conditions.put("knowledgePointId", id);

                Map<String, String> orders = new HashMap<>();
                orders.put("order", "ASC");
                List<KnowledgePointContentMap> maps = JPAEntry.getList(KnowledgePointContentMap.class, conditions, orders);

                List<String> textIds = new ArrayList<>();
                List<String> imageIds = new ArrayList<>();
                List<String> videoIds = new ArrayList<>();
                List<String> problemIds = new ArrayList<>();
                List<String> imageTextIds = new ArrayList<>();
                List<String> quoteIds = new ArrayList<>();
                List<String> pinyinIds = new ArrayList<>();

                for (KnowledgePointContentMap item : maps) {
                    switch (item.getType()) {
                        case "text":
                            textIds.add(item.getObjectId().toString());
                            break;
                        case "image":
                            imageIds.add(item.getObjectId().toString());
                            break;
                        case "video":
                            videoIds.add(item.getObjectId().toString());
                            break;
                        case "problem":
                            problemIds.add(item.getObjectId().toString());
                            break;
                        case "imageText":
                            imageTextIds.add(item.getObjectId().toString());
                            break;
                        case "quote":
                            quoteIds.add(item.getObjectId().toString());
                            break;
                        case "pinyinText":
                            pinyinIds.add(item.getObjectId().toString());
                            break;
                    }
                }

                EntityManager em = JPAEntry.getEntityManager();

                List<Text> textObjects = getList(em, textIds, Text.class);

                List<Image> imageObjects = getList(em, imageIds, Image.class);

                List<Video> videoObjects = getList(em, videoIds, Video.class);

                List<Problem> problemObjects = getList(em, problemIds, Problem.class);
                List<ProblemOption> problemOptionObjects = getListByColumn(em, "problemId", problemIds, ProblemOption.class);
                List<ProblemStandardAnswer> problemStandardAnswerObjects = getListByColumn(em, "problemId", problemIds, ProblemStandardAnswer.class);

                List<ImageText> imageTextObject = getList(em, imageTextIds, ImageText.class);

                List<Quote> quoteObject = getList(em, quoteIds, Quote.class);

                List<PinyinText> pinyinTextObject = getList(em, pinyinIds, PinyinText.class);

                List<Object> orderedContents = new ArrayList<>();
                List<Object> orderedProblems = new ArrayList<>();
                List<Object> orderedQuotes = new ArrayList<>();
                List<Object> orderedPinyins = new ArrayList<>();
                for (final KnowledgePointContentMap item : maps) {
                    switch (item.getType()) {
                        case "text":
                            if (textObjects != null) {
                                Text t = findItem(textObjects, (Text text) -> text.getId().longValue() == item.getObjectId().longValue());
                                Map<String, Object> tm = new HashMap<>();
                                tm.put("id", t.getId());
                                tm.put("content", t.getContent());
                                tm.put("type", "text");
                                orderedContents.add(tm);
                            }
                            break;
                        case "image":
                            if (imageObjects != null) {
                                Image i = findItem(imageObjects, (image) -> image.getId().longValue() == item.getObjectId().longValue());
                                Map<String, Object> im = new HashMap<>();
                                im.put("id", i.getId());
                                im.put("type", "image");
                                im.put("description", "");
                                im.put("href", i.getStorePath());
                                orderedContents.add(im);
                            }
                            break;
                        case "imageText":
                            if (imageTextObject != null) {
                                ImageText it = findItem(imageTextObject, (imageText) -> imageText.getId().longValue() == item.getObjectId().longValue());
                                Map<String, Object> itm = new HashMap<>();
                                itm.put("id", it.getId());
                                itm.put("type", "imageText");
                                itm.put("content", it.getContent());
                                itm.put("href", it.getStorePath());
                                orderedContents.add(itm);
                            }
                            break;
                        case "pinyinText":
                            if (pinyinTextObject != null) {
                                PinyinText q = findItem(pinyinTextObject, (pinyinText) -> pinyinText.getId().longValue() == item.getObjectId().longValue());
                                Map<String, Object> qm = new HashMap<>();
                                qm.put("id", q.getId());
                                qm.put("type", "pinyinText");
                                qm.put("pinyin", q.getPinyin());
                                qm.put("content", q.getContent());
                                orderedContents.add(qm);
                            }
                            break;
                        case "problem":
                            if (problemObjects != null || problemOptionObjects != null || problemStandardAnswerObjects != null) {
                                Problem problemItem = findItem(problemObjects, (problem) -> problem.getId().longValue() == item.getObjectId().longValue());
                                List<ProblemOption> problemOptions = findItems(problemOptionObjects, (ProblemOption problemoption) -> problemoption.getProblemId().longValue() == item.getObjectId().longValue());
                                List<ProblemStandardAnswer> problemStandardAnswers = findItems(problemStandardAnswerObjects, (problemstandardanswers) -> problemstandardanswers.getProblemId().longValue() == item.getObjectId().longValue());

                                Map<String, Object> pm = new HashMap<>();
                                pm.put("id", problemItem.getId());
                                if (problemStandardAnswers.size() > 1) {
                                    pm.put("type", "多选题");
                                } else {
                                    pm.put("type", "单选题");
                                }

                                //获取每一个选项 option
                                Collections.sort(problemOptions, (left, right) -> left.getOrder().intValue() - right.getOrder().intValue());
                                ArrayList<Map<String, Object>> apm = new ArrayList();
                                for (ProblemOption option : problemOptions) {
                                    Map<String, Object> opm = new HashMap<>();
                                    opm.put("optionId", option.getId());
                                    opm.put("name", option.getName());
                                    if (option.getImageId() != null) {
                                        Image storePath = JPAEntry.getObject(Image.class, "id", option.getImageId());
                                        opm.put("optionImagePath", storePath.getStorePath());
                                    }
                                    apm.add(opm);
                                }
                                pm.put("options", apm);
                                pm.put("standardAnswers", problemStandardAnswers);
                                pm.put("title", problemItem.getTitle());
                                pm.put("storePath", problemItem.getStorePath());
                                pm.put("videoUrl", problemItem.getVideoUrl());
                                if (problemItem.getVideoImage() != null) {
                                    Image im = JPAEntry.getObject(Image.class, "id", problemItem.getVideoImage());
                                    pm.put("videoImage", im.getStorePath());
                                }

                                orderedProblems.add(pm);
                            }
                            break;
                        case "quote":
                            if (quoteObject != null) {
                                Quote q = findItem(quoteObject, (quote) -> quote.getId().longValue() == item.getObjectId().longValue());
                                Map<String, Object> qm = new HashMap<>();
                                qm.put("id", q.getId());
                                qm.put("content", q.getContent());
                                qm.put("source", q.getSource());
                                orderedQuotes.add(qm);
                            }
                            break;
                    }
                }

                Map<String, Object> totalResult = new HashMap<>();
                totalResult.put("title", p.getTitle());
//                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
//                String now = gson.toJson(p.getShowTime());
//                totalResult.put("showTime", now);
                totalResult.put("quotes", orderedQuotes);
                totalResult.put("contents", orderedContents);

                if ((videoObjects != null) && !videoObjects.isEmpty()) {
                    Video video = videoObjects.get(0);
                    Image image = JPAEntry.getObject(Image.class, "id", video.getCover());
                    Map<String, Object> vm = new HashMap<>();

                    vm.put("cover", image.getStorePath());
                    vm.put("id", video.getId());
                    vm.put("storePath", video.getStorePath());
                    totalResult.put("video", vm);
                }

                Map<String, Object> interaction = new HashMap<>();
                interaction.put("likeCount", Logs.getStatsCount("knowledge-point", id, "like"));
                interaction.put("readCount", Logs.getStatsCount("knowledge-point", id, "read"));
                totalResult.put("interaction", interaction);

                totalResult.put("problems", orderedProblems);
                /*totalResult.put("pinyins", orderedPinyins);*/

                conditions = new HashMap<>();
                conditions.put("objectType", "knowledge-point");
                conditions.put("objectId", id);
                List<Comment> comments = JPAEntry.getList(Comment.class, conditions);
                List<Map<String, Object>> commentMaps = new ArrayList<>(comments.size());
                for (Comment comment : comments) {
                    Map<String, Object> commentMap = new HashMap<>();
                    commentMap.put("id", comment.getId());
                    commentMap.put("content", comment.getContent());
                    commentMap.put("clientId", comment.getClientId());
                    SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    commentMap.put("createTime", time.format(comment.getCreateTime()));
                    commentMap.put("objectId", comment.getObjectId());
                    commentMap.put("objectType", comment.getObjectType());
                    commentMap.put("updateTime", comment.getUpdateTime());
                    commentMap.put("userId", comment.getUserId());
                    User user = JPAEntry.getObject(User.class, "id", comment.getUserId());
                    if (user != null) {
                        commentMap.put("userName", user.getName());
                        commentMap.put("userAvatar", user.getHead());
                    }
                    commentMap.put("likeCount", Logs.getStatsCount("comment", comment.getId(), "like"));
                    commentMaps.add(commentMap);
                }
                totalResult.put("comments", commentMaps);
                String v = new Gson().toJson(totalResult);
                result = Response.ok(v, "application/json; charset=utf-8").build();
            }
        }
        return result;
    }

    @POST//添
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createKnowledgePoint(@CookieParam("userId") String userId, KnowledgePoint knowledgePoint) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            knowledgePoint.setId(IdGenerator.getNewId());
            JPAEntry.genericPost(knowledgePoint);
            result = Response.ok(knowledgePoint).build();
        }
        return result;
    }

   /* @GET //根据条件查询
    @Produces(MediaType.APPLICATION_JSON)
    public Response getKnowledgePoints(@CookieParam("userId") String userId, @QueryParam("filter") @DefaultValue("") String filter) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            result = Response.status(404).build();
            Map<String, Object> filterObject = CharacterEncodingFilter.getFilters(filter);
            Map<String, String> orders = new HashMap<>();
            orders.put("order", "ASC");
            List<KnowledgePoint> knowledgePoints = JPAEntry.getList(KnowledgePoint.class, filterObject, orders);
            List<Map<String, Object>> knowledgePointMaps = new ArrayList<>(knowledgePoints.size());
            for (KnowledgePoint knowledgePoint : knowledgePoints) {
                Map<String, Object> knowledgePointMap = new HashMap<>();
                knowledgePointMap.put("id", knowledgePoint.getId());
                knowledgePointMap.put("title", knowledgePoint.getTitle());
                SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                knowledgePointMap.put("showTime", time.format(knowledgePoint.getShowTime()));
                knowledgePointMap.put("subjectId", knowledgePoint.getSubjectId());
                knowledgePointMap.put("grade", knowledgePoint.getGrade());
                knowledgePointMap.put("order", knowledgePoint.getOrder());
                knowledgePointMap.put("storePath", knowledgePoint.getStorePath());
                knowledgePointMap.put("videoUrl", knowledgePoint.getVideoUrl());
                knowledgePointMap.put("volumeId", knowledgePoint.getVolumeId());
                knowledgePointMap.put("likeCount", Logs.getStatsCount("knowledge-point", knowledgePoint.getId(), "like"));
                knowledgePointMap.put("readCount", Logs.getStatsCount("knowledge-point", knowledgePoint.getId(), "read"));
                knowledgePointMaps.add(knowledgePointMap);
            }
            result = Response.ok(knowledgePointMaps).build();
        }
        return result;
    }
*/
    @GET //根据id查询
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getKnowledgePointById(@CookieParam("userId") String userId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            result = Response.status(404).build();
            KnowledgePoint knowledgePoint = JPAEntry.getObject(KnowledgePoint.class, "id", id);
            if (knowledgePoint != null) {
                result = Response.ok(new Gson().toJson(knowledgePoint)).build();
            }
        }
        return result;
    }

    @PUT //根据id修改
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateKnowledgePoint(@CookieParam("userId") String userId, @PathParam("id") Long id, KnowledgePoint knowledgePoint) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            result = Response.status(404).build();
            KnowledgePoint existknowledgePoint = JPAEntry.getObject(KnowledgePoint.class, "id", id);
            if (existknowledgePoint != null) {
                int grade = knowledgePoint.getGrade();
                if (grade != 0) {
                    existknowledgePoint.getGrade();
                }
                Date showTime = knowledgePoint.getShowTime();
                if (showTime != null) {
                    existknowledgePoint.setShowTime(showTime);
                }
                String storePath = knowledgePoint.getStorePath();
                if (storePath != null) {
                    existknowledgePoint.setStorePath(storePath);
                }
                int order = knowledgePoint.getOrder();
                if (order != 0) {
                    existknowledgePoint.setOrder(order);
                }
                Long subjectId = knowledgePoint.getSubjectId();
                if (subjectId != null) {
                    existknowledgePoint.setSubjectId(subjectId);
                }
                String title = knowledgePoint.getTitle();
                if (title != null) {
                    existknowledgePoint.setTitle(title);
                }
                String videoUrl = knowledgePoint.getVideoUrl();
                if (videoUrl != null) {
                    existknowledgePoint.setVideoUrl(videoUrl);
                }
                Long volumeId = knowledgePoint.getVolumeId();
                if (volumeId != null) {
                    existknowledgePoint.setVolumeId(volumeId);
                }
                JPAEntry.genericPut(existknowledgePoint);
                result = Response.ok(existknowledgePoint).build();
            }
        }
        return result;
    }
}

