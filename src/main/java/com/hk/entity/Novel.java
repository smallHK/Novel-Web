package com.hk.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * smallHK
 * 2019/3/21 16:08
 * <p>
 * 数据库小说表实体
 */
@Entity
@Getter
@Setter
@Table(name = "t_novel")
public class Novel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "novel_name")
    private String novelName;

    @Column(name = "brief_intro")
    private String briefIntro;

    @Column(name = "cover_img")
    private String coverImg;

    @Column(name = "creator_id")
    private Integer authorId;

    @Column(insertable = false)
    private Integer status;

    @Column(name = "create_time")
    private Timestamp createTime;

    @Column(name = "update_time")
    private Timestamp updateTime;

    @Column(name = "complete_status")
    private Integer completeStatus;
}
