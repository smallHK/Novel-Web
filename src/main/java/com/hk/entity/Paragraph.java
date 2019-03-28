package com.hk.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * smallHK
 * 2019/3/27 20:13
 */
@Entity
@Table(name = "t_paragraph")
@Setter
@Getter
public class Paragraph {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String content;

    @Column(name = "chapter_id")
    private Integer chapterId;

    @Column(name = "order_num")
    private Integer orderNum;

    private Integer status;
}
