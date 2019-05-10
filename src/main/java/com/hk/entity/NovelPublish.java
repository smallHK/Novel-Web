package com.hk.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * @author smallHK
 * 2019/4/10 19:51
 * <p>
 * 小说发布关系实体
 */
@Table(name = "t_novel_publish")
@Entity
@Getter
@Setter
public class NovelPublish {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "editor_id")
    private Integer editorId;

    @Column(name = "novel_id")
    private Integer novelId;

    @Column(name = "apply_time")
    private Timestamp applyTime;

    @Column(name = "publish_time")
    private Timestamp publishTime;

}
