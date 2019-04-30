package com.hk.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Controller;

import javax.persistence.*;

/**
 * @author smallHK
 * 2019/4/3 21:56
 */
@Table(name = "t_editor")
@Setter
@Getter
@Entity
public class Editor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "editor_name")
    String editorName;

    String password;

    @Column(name = "profile_id")
    Integer profileId;

}
