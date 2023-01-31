package com.joannava.video.dao;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Film {

    public enum Type {
        RELEASE,
        REGULAR,
        OLD
    }

    @Id
    private String id;
    private String name;
    
    @Enumerated(EnumType.STRING)
    private Type type;
    private boolean rented;
}
