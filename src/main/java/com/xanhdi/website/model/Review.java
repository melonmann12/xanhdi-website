package com.xanhdi.website.model;

import jakarta.persistence.*;

@Entity
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String author;
    private Integer rating;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "time_ago")
    private String timeAgo;

    @Column(name = "image_url1")
    private String imageUrl1;

    @Column(name = "image_url2")
    private String imageUrl2;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id")
    private Tour tour;

    public Review() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getTimeAgo() { return timeAgo; }
    public void setTimeAgo(String timeAgo) { this.timeAgo = timeAgo; }

    public String getImageUrl1() { return imageUrl1; }
    public void setImageUrl1(String imageUrl1) { this.imageUrl1 = imageUrl1; }

    public String getImageUrl2() { return imageUrl2; }
    public void setImageUrl2(String imageUrl2) { this.imageUrl2 = imageUrl2; }

    public Tour getTour() { return tour; }
    public void setTour(Tour tour) { this.tour = tour; }
}
