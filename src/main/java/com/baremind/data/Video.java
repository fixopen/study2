package com.baremind.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by lenovo on 2016/8/17.
 */
@Entity
@Table(name = "videos")
public class Video {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "mime_type")
    private String mimeType;

    @Column(name = "size")
    private Long size;

    @Column(name = "name")
    private String name;

    @Column(name = "ext")
    private String ext;

    @Column(name = "store_path")
    private String storePath;

    @Column(name = "cover")
    private Long cover;

    @Column(name = "bit_rate")
    private Double bitRate;

    public Long getCover() {
        return cover;
    }

    public void setCover(Long cover) {
        this.cover = cover;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getStorePath() {
        return storePath;
    }

    public void setStorePath(String storePath) {
        this.storePath = storePath;
    }

  /*  public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }*/

    public Double getBitRate() {
        return bitRate;
    }

    public void setBitRate(Double bitRate) {
        this.bitRate = bitRate;
    }


}
