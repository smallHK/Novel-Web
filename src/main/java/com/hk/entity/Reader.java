package com.hk.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ValueGenerationType;

import javax.persistence.*;

/**
 * @author smallHK
 * 2019/4/8 14:44
 */
@Getter
@Setter
@Entity
@Table(name = "t_reader")
public class Reader {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String username;

    private String password;

}
