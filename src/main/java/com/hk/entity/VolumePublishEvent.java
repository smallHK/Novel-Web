package com.hk.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * @author smallHK
 * 2019/5/3 22:24
 */
@Getter
@Setter
@Entity
@Table(name = "t_volume_publish_event")
public class VolumePublishEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "volume_id")
    private Integer volumeId;

    @Column(name = "author_id")
    private Integer authorId;

    @Column(name = "editor_id")
    private Integer editorId;

    private Integer status;

    @Column(name = "apply_time")
    private Timestamp applyTime;
}
