package com.aifloorplan.aifloorplanrestapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import java.sql.Timestamp;

@Setter
@Getter
@Entity
@Table(name = "floorplan")
public class Floorplan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idFloorplan;

    @Lob
    @Column(nullable = false, length = 2097152) // 2 MB
    private byte[] imageData;

    @Column(nullable = false)
    private String prompt;

    @Column(columnDefinition = "tinyint(1) default 0", nullable = false)
    private boolean isDeleted;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", nullable = false)
    private Timestamp createTime;

    @ManyToOne
    @JoinColumn(name = "chat_idChat", nullable = false)
    private Chat chat;

}
