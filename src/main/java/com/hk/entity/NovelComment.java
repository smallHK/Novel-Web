package com.hk.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * @author smallHK
 * 2019/4/12 11:59
 */
@Table(name = "t_novel_comment")
@Entity
@Getter
@Setter
public class NovelComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String content;

    @Column(name = "novel_id")
    private Integer novelId;

    @Column(name = "reader_id")
    private Integer readerId;

    @Column(name = "order_num")
    private Integer orderNum;

    @Column(name = "agreement_num")
    private Integer agreementNum;

    private Integer status;

}
