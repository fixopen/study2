package com.baremind.data;

import com.baremind.utils.JPAEntry;
import javax.persistence.Entity;
import javax.persistence.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lenovo on 2016/8/18.
 */
@Entity
@Table(name = "volumes")
public class Volume {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "subject_id")
    private Long subjectId;

    @Column(name = "grade")
    private int grade;

    @Column(name = "title")
    private String title;

    @Column(name = "\"order\"")
    private int order;

    @Column(name = "bookcover")
    private String bookCover;

    @Column(name = "knowledge_point_count")
    private Long knowledgePointCount;

    public Long getKnowledgePointCount() {
        return knowledgePointCount;
    }

    public void setKnowledgePointCount(Long knowledgePointCount) {
        this.knowledgePointCount = knowledgePointCount;
    }

    public String getBookCover() {
        return bookCover;
    }

    public void setBookCover(String bookCover) {
        this.bookCover = bookCover;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public static Map<String, Object> convertToMap(Volume volume, Date now, Date yesterday) {
        Map<String, Object> vm = new HashMap<>();
        vm.put("id", volume.getId());
        vm.put("grade", volume.getGrade());
        vm.put("order", volume.getOrder());
        vm.put("subjectId", volume.getSubjectId());
        vm.put("bookCover", volume.getBookCover());
        vm.put("title", volume.getTitle());
        vm.put("type", "old");
        vm.put("knowledgePointCount", volume.getKnowledgePointCount());
        EntityManager em = JPAEntry.getEntityManager();
        String stats = "SELECT COUNT(l) FROM KnowledgePoint l WHERE l.volumeId = :volumeId AND l.showTime > :yesterday AND l.showTime < :now";
        Query q = em.createQuery(stats, Long.class);
        q.setParameter("volumeId", volume.getId());
        q.setParameter("yesterday", yesterday);
        q.setParameter("now", now);
        Long count = (Long) q.getSingleResult();
        if (count > 0) {
            vm.put("type", "new");
        }
        return vm;
    }
}
