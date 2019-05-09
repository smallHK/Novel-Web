package com.hk.entity.event;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * @author smallHK
 * 2019/5/3 16:19
 */
@Entity
@Getter
@Setter
@Table(name = "t_chapter_publish_event")
public class ChapterPublishEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "chapter_id")
    private Integer chapterId;

    private Integer status;

    @Column(name = "apply_time")
    private Timestamp applyTime;

    @Column(name = "editor_id")
    private Integer editorId;

    @Column(name = "author_id")
    private Integer authorId;
}
