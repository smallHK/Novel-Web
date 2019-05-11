package com.hk.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * @author smallHK
 * 2019/5/11 10:30
 */
@Entity
@Setter
@Getter
@Table(name = "t_admin_operation_log")
public class AdminOperationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String log;

    private Integer type;

    @Column(name = "operate_time")
    private Timestamp operateTime;

}
