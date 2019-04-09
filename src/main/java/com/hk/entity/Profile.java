package com.hk.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * smallHK
 * 2019/4/3 21:34
 */
@Table(name = "t_profile")
@Entity
@Setter
@Getter
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "real_name")
    String realName;

    @Column(name = "brief_intro")
    String briefIntro;

    @Column(name = "phone_number")
    String phoneNumber;

    @Column(name = "id_number")
    String idNumber;

    @Column(name = "e_mail")
    String eMail;

    @Column(insertable = false)
    Integer status;

}
