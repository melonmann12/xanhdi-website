package com.xanhdi.website.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tours")
public class Tour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private Double price;

    @Column(name = "image_url")
    private String imageUrl;

    private String tag;
    private String duration;
    private String activity;
    private Double rating;

    @Column(name = "review_count")
    private Integer reviewCount;

    private String departure;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "journal_content", columnDefinition = "TEXT")
    private String journalContent;

    @Column(name = "journal_quote", columnDefinition = "TEXT")
    private String journalQuote;

    @Column(name = "guide_name")
    private String guideName;

    @Column(columnDefinition = "TEXT")
    private String inclusions;

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("sortOrder ASC")
    private List<TourTimeline> timelines = new ArrayList<>();

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Review> reviews = new ArrayList<>();

    public Tour() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getJournalContent() { return journalContent; }
    public void setJournalContent(String journalContent) { this.journalContent = journalContent; }

    public String getJournalQuote() { return journalQuote; }
    public void setJournalQuote(String journalQuote) { this.journalQuote = journalQuote; }

    public String getGuideName() { return guideName; }
    public void setGuideName(String guideName) { this.guideName = guideName; }

    public String getInclusions() { return inclusions; }
    public void setInclusions(String inclusions) { this.inclusions = inclusions; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public String getActivity() { return activity; }
    public void setActivity(String activity) { this.activity = activity; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public Integer getReviewCount() { return reviewCount; }
    public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }

    public String getDeparture() { return departure; }
    public void setDeparture(String departure) { this.departure = departure; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<TourTimeline> getTimelines() { return timelines; }
    public void setTimelines(List<TourTimeline> timelines) { this.timelines = timelines; }

    public List<Review> getReviews() { return reviews; }
    public void setReviews(List<Review> reviews) { this.reviews = reviews; }
}
