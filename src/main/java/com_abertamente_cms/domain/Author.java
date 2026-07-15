package com_abertamente_cms.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "authors")
@SQLDelete(sql = "UPDATE authors SET deleted_at = CURRENT_TIMESTAMP WHERE id=?")
@SQLRestriction("deleted_at IS NULL")
public class Author extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

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

    public Author(String name, String email, String bio, String mainTitle) {
        this.name = name;
        this.email = email;
        this.bio = bio;
        this.mainTitle = mainTitle;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
