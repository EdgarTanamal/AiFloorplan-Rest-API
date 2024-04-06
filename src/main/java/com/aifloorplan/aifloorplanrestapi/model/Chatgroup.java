package com.aifloorplan.aifloorplanrestapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import java.sql.Timestamp;

@Setter
@Getter
@Entity
@Table(name = "chatgroup")
public class Chatgroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idChatgroup;

    @Column(columnDefinition = "tinyint(1) default 0", nullable = false)
    private boolean isDeleted;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", nullable = false)
    private Timestamp createTime;

    @ManyToOne
    @JoinColumn(name = "user_idUser", nullable = false)
    private User user;

}