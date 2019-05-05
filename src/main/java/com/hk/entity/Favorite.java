package com.hk.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * @author smallHK
 * 2019/5/5 22:05
 */
@Table(name = "Favorite")
@Setter
@Getter
@Entity
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "novel_id")
    private Integer novelId;

    @Column(name = "reader_id")
    private Integer readerId;

}
