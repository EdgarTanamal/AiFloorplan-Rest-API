package com.aifloorplan.aifloorplanrestapi.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "updatedChat")
public class UpdatedChat {
    @Id
    @ManyToOne
    @JoinColumn(name = "idOriginalChat", nullable = false)
    private Chat originalChat;

    @Id
    @ManyToOne
    @JoinColumn(name = "idUpdatedChat", nullable = false)
    private Chat updatedChat;

}
