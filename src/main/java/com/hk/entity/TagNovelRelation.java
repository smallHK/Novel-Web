package com.hk.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * @author smallHK
 * 2019/5/7 16:55
 */
@Getter
@Setter
@Entity
@Table(name = "t_novel_tag_relation")
public class TagNovelRelation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "novel_id")
    private Integer novelId;

    @Column(name = "tag_id")
    private Integer tagId;

}
