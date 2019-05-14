package com.hk.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * @author smallHK
 * 2019/5/13 21:56
 */
@Entity
@Table(name = "t_editor_recommend")
@Getter
@Setter
public class EditorRecommend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "novel_id")
    private Integer novelId;

    @Column(name = "editor_id")
    private Integer editorId;

    private Integer status;

    @Column(name = "recommend_time")
    private Timestamp recommendTime;

    private String reason;
}
