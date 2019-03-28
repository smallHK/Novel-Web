package com.hk.entity;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


/**
 * smallHK
 * 2019/3/20 21:36
 */
@Entity
@Getter
@Setter
@Table(name = "t_admin")
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String username;

    private String password;

}
