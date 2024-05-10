package com.scholarsync.server.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Entity
@Table
@Getter
@Setter
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @DecimalMax(value = "5.0")
    @DecimalMin(value = "0.0")
    private double rating;


    @ManyToOne
    @JoinColumn(name = "answer_id")
    @JsonBackReference
    private Answer answer;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User userId;
}
