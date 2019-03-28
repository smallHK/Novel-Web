package com.hk.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * smallHK
 * 2019/3/25 9:47
 * <p>
 * 数据库创作者实体
 */
@Getter
@Setter
@Entity
@Table(name = "t_creator")
public class Creator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "penname")
    private String penName;

    private String password;

    @Column(name = "brief_introduction")
    private String briefIntro;

}
