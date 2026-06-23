package com.xanhdi.website.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "tour_guides")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TourGuide {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "avatar_url")
    private String avatarUrl;

    private String specialty;

    private String languages;

    @Column(columnDefinition = "TEXT")
    private String bio;

    private Double rating;

    @Column(name = "review_count")
    private Integer reviewCount;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;
}
