package com.hk.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * smallHK
 * 2019/3/27 20:15
 */
@Setter
@Getter
@Table(name = "t_chapter")
@Entity
public class Chapter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;

    @Column(name = "word_count")
    private Integer wordCount;

    @Column(name = "order_num")
    private Integer orderNum;

    @Column(name = "novel_id")
    private Integer novelId;

    @Column(name = "volume_id")
    private Integer volumeId;

    @Column(insertable = false)
    private Integer status;

    @Column(name = "click_count", insertable = false)
    private Integer clickCount;


}
