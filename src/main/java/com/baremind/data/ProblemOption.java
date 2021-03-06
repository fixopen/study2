package com.baremind.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by User on 2016/9/19.
 */
@Entity
@Table(name = "problem_options")
public class ProblemOption {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "problem_id")
    private Long problemId;

    @Column(name = "image_id")
    private Long imageId;

    @Column(name = "\"order\"")
    private Long order;

    @Column(name = "name")
    private String name;

    public Long getOrder() {
        return order;
    }

    public void setOrder(Long order) {
        this.order = order;
    }

    public Long getImageId() {
        return imageId;
    }

    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProblemId() {
        return problemId;
    }

    public void setProblemId(Long problemId) {
        this.problemId = problemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
