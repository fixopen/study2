package com.baremind.data;


import com.baremind.Resources;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by lenovo on 2016/8/18.
 */
@Entity
@Table(name = "schedulers")
public class Scheduler {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "day")
    private int day;

    @Column(name = "year")
    private int year;

    @Column(name = "week")
    private int week;

  /*  @Column(name = "state")
    private int state;*/

    @Column(name = "start_time")
    private Date startTime;

    @Column(name = "end_time")
    private Date endTime;

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "subject_id")
    private Long subjectId;

    @Column(name = "grade")
    private int grade;

    @Column(name = "title")
    private String title;

    @Column(name = "prepare")
    private String prepare;

    @Column(name = "outline")
    private String outline;

    @Column(name = "generalization")
    private String generalization;

    public String getPrepare() {
        return prepare;
    }

    public void setPrepare(String prepare) {
        this.prepare = prepare;
    }

    public String getOutline() {
        return outline;
    }

    public void setOutline(String outline) {
        this.outline = outline;
    }

    public String getGeneralization() {
        return generalization;
    }

    public void setGeneralization(String generalization) {
        this.generalization = generalization;
    }

    @Column(name = "cover")
    private String cover;

    @Column(name = "cdn_link")
    private String cdnLink;

    @Column(name = "direct_link")
    private String directLink;

    @Column(name = "description")
    private String description;

    @Column(name = "teacher")
    private String teacher;

    @Column(name = "teacher_description")
    private String teacherDescription;

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getCdnLink() {
        return cdnLink;
    }

    public void setCdnLink(String cdnLink) {
        this.cdnLink = cdnLink;
    }

    public String getDirectLink() {
        return directLink;
    }

    public void setDirectLink(String directLink) {
        this.directLink = directLink;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    /*   public int getState() {
           return state;
       }

       public void setState(int state) {
           this.state = state;
       }
   */
    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getTeacherDescription() {
        return teacherDescription;
    }

    public void setTeacherDescription(String teacherDescription) {
        this.teacherDescription = teacherDescription;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public static Long findUntypedItem(List<Object[]> container, Long id) {
        Long result = 0L;
        for (Object[] item : container) {
            if (((Long) item[0]).longValue() == id.longValue()) {
                result = (Long) item[1];
                break;
            }
        }
        return result;
    }

    public static List<Object[]> findUntypedItems(List<Object[]> container, Long id) {
        return container.stream().filter((item) -> ((Long) item[0]).longValue() == id.longValue()).collect(Collectors.toList());
    }

    public static Map<String, Object> convertToMap(Scheduler scheduler, List<User> teachers, List<Image> covers, List<Object[]> likeCount, List<Object[]> likedCount, List<Object[]> readCount, List<Comment> comments) {
        Map<String, Object> schedulerMap = new HashMap<>();
        schedulerMap.put("id", scheduler.getId());
        schedulerMap.put("year", scheduler.getYear());
        schedulerMap.put("week", scheduler.getWeek());
        schedulerMap.put("day", scheduler.getDay());
        schedulerMap.put("startTime", scheduler.getStartTime());
        schedulerMap.put("endTime", scheduler.getEndTime());
        schedulerMap.put("subjectId", scheduler.getSubjectId());
        schedulerMap.put("grade", scheduler.getGrade());
        schedulerMap.put("title", scheduler.getTitle());
        schedulerMap.put("abstraction", scheduler.getGeneralization());
        schedulerMap.put("outline", scheduler.getOutline());
        schedulerMap.put("description", scheduler.getDescription());
        schedulerMap.put("prepare", scheduler.getPrepare());
        schedulerMap.put("cover", scheduler.getCover());
//        Image cover = Resources.findItem(covers, item -> item.getId() == scheduler.getCoverId());
//        if (cover != null) {
//            schedulerMap.put("cover", cover.getStorePath());
//        }
        schedulerMap.put("teacher", scheduler.getTeacher());
        schedulerMap.put("teacherDescription", scheduler.getTeacherDescription());
//        User teacher = Resources.findItem(teachers, item -> item.getId() == scheduler.getTeacherId());
//        if (teacher != null) {
//            schedulerMap.put("teacher", teacher.getName());
//            schedulerMap.put("teacherDescription", teacher.getDescription());
//        }
//        schedulerMap.put("price", scheduler.getPrice());
//        schedulerMap.put("discount", scheduler.getDiscount());
//        //schedulerMap.put("price", scheduler.getAmount());
        schedulerMap.put("likeCount", findUntypedItem(likeCount, scheduler.getId()));
        schedulerMap.put("readCount", findUntypedItem(readCount, scheduler.getId()));
        schedulerMap.put("liked", findUntypedItem(likedCount, scheduler.getId()) != null);
        schedulerMap.put("comments", Resources.findItems(comments, c -> c.getObjectId() == scheduler.getId()));
        schedulerMap.put("cdnLink", scheduler.getCdnLink());
        schedulerMap.put("directLink", scheduler.getDirectLink());
        return schedulerMap;
    }
}
