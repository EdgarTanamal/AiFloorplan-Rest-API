package com.aifloorplan.aifloorplanrestapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import java.sql.Timestamp;

@Setter
@Getter
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idUser;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(columnDefinition = "tinyint(1) default 0", nullable = false)
    private boolean isPremium;

    @Column(columnDefinition = "tinyint(1) default 0", nullable = false)
    private boolean isDeleted;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", nullable = false)
    private Timestamp createTime;

}
