package com.xanhdi.website.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tour_timelines")
public class TourTimeline {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "time_slot")
    private String timeSlot;

    private String title;

    @Column(name = "location_title")
    private String locationTitle;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    private String icon;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id")
    private Tour tour;

    public TourTimeline() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTimeSlot() { return timeSlot; }
    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getLocationTitle() { return locationTitle; }
    public void setLocationTitle(String locationTitle) { this.locationTitle = locationTitle; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public Tour getTour() { return tour; }
    public void setTour(Tour tour) { this.tour = tour; }
}
