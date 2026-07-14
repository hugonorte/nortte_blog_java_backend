package com_abertamente_cms.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "authors")
public class Author extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(name = "main_title")
    private String mainTitle;

    @Column(name = "preferred_social_network")
    private String preferredSocialNetwork;

    @Column(name = "preferred_social_network_username")
    private String preferredSocialNetworkUsername;

    public Author() {
    }

    public Author(User user, String bio, String mainTitle) {
        this.user = user;
        this.bio = bio;
        this.mainTitle = mainTitle;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getMainTitle() {
        return mainTitle;
    }

    public void setMainTitle(String mainTitle) {
        this.mainTitle = mainTitle;
    }

    public String getPreferredSocialNetwork() {
        return preferredSocialNetwork;
    }

    public void setPreferredSocialNetwork(String preferredSocialNetwork) {
        this.preferredSocialNetwork = preferredSocialNetwork;
    }

    public String getPreferredSocialNetworkUsername() {
        return preferredSocialNetworkUsername;
    }

    public void setPreferredSocialNetworkUsername(String preferredSocialNetworkUsername) {
        this.preferredSocialNetworkUsername = preferredSocialNetworkUsername;
    }
}
