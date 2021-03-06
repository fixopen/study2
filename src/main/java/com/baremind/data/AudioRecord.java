package com.baremind.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by fixopen on 10/11/2016.
 */
@Entity
@Table(name = "audio_records")
public class AudioRecord {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "book_id")
    private Long bookId;

//    @Column(name = "subject_no")
//    private String subjectNo;
//
//    @Column(name = "grade_no")
//    private String gradeNo;
//
//    @Column(name = "book_no")
//    private String bookNo;

    @Column(name = "page_no")
    private String pageNo;

    @Column(name = "unit_no")
    private String unitNo;

    @Column(name = "start_time")
    private Integer startTime;

    @Column(name = "end_time")
    private Integer endTime;

    @Column(name = "\"left\"")
    private Integer left;

    @Column(name = "top")
    private Integer top;

    @Column(name = "\"right\"")
    private Integer right;

    @Column(name = "bottom")
    private Integer bottom;

    @Column(name = "chinese")
    private String chinese;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

//    public String getSubjectNo() {
//        return subjectNo;
//    }
//
//    public void setSubjectNo(String subjectNo) {
//        this.subjectNo = subjectNo;
//    }
//
//    public String getGradeNo() {
//        return gradeNo;
//    }
//
//    public void setGradeNo(String gradeNo) {
//        this.gradeNo = gradeNo;
//    }
//
//    public String getBookNo() {
//        return bookNo;
//    }
//
//    public void setBookNo(String bookNo) {
//        this.bookNo = bookNo;
//    }

    public String getPageNo() {
        return pageNo;
    }

    public void setPageNo(String pageNo) {
        this.pageNo = pageNo;
    }

    public String getUnitNo() {
        return unitNo;
    }

    public void setUnitNo(String unitNo) {
        this.unitNo = unitNo;
    }

    public Integer getStartTime() {
        return startTime;
    }

    public void setStartTime(Integer startTime) {
        this.startTime = startTime;
    }

    public Integer getEndTime() {
        return endTime;
    }

    public void setEndTime(Integer endTime) {
        this.endTime = endTime;
    }

    public Integer getLeft() {
        return left;
    }

    public void setLeft(Integer left) {
        this.left = left;
    }

    public Integer getTop() {
        return top;
    }

    public void setTop(Integer top) {
        this.top = top;
    }

    public Integer getRight() {
        return right;
    }

    public void setRight(Integer right) {
        this.right = right;
    }

    public Integer getBottom() {
        return bottom;
    }

    public void setBottom(Integer bottom) {
        this.bottom = bottom;
    }

    public String getChinese() {
        return chinese;
    }

    public void setChinese(String chinese) {
        this.chinese = chinese;
    }
}
