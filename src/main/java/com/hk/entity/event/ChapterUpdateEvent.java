package com.hk.entity.event;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * @author smallHK
 * 2019/5/9 10:39
 */
@Entity
@Setter
@Getter
@Table(name = "t_chapter_update_event")
public class ChapterUpdateEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "chapter_id")
    private Integer chapterId;

    @Column(name = "update_time")
    private Timestamp updateTime;

    @Column(name = "editor_id")
    private Integer editorId;

    private Integer status;
}
