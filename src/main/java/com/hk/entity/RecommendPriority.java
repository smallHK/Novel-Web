package com.hk.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * @author smallHK
 * 2019/5/8 10:22
 */
@Getter
@Setter
@Entity
@Table(name = "t_recommend_priority")
public class RecommendPriority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reader_id")
    private Integer readerId;

    @Column(name = "novel_id")
    private Integer novelId;

    private Double priority;
}
