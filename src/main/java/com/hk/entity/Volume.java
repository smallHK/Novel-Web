package com.hk.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * smallHK
 * 2019/3/27 20:14
 */
@Getter
@Setter
@Entity
@Table(name = "t_volume")
public class Volume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "order_num")
    private Integer orderNum;

    @Column(name = "volume_title")
    private String volumeTitle;

    @Column(name = "novel_id")
    private Integer novelId;

    private Integer status;
}
