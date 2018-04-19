package com.github.TwrpBuilder.model;

/**
 * Created by androidlover5842 on 12.3.2018.
 */

public class Developer {
    private String email;
    private String name;
    private String photoUrl;
    private String xdaUrl;
    private String gitId;
    private String donationUrl;
    private String description;
    public Developer(){}
    public Developer(String email,String name,String photoUrl,String xdaUrl,String gitId,String donationUrl,String description ){
        this.email=email;
        this.name=name;
        this.photoUrl=photoUrl;
        this.xdaUrl=xdaUrl;
        this.gitId=gitId;
        this.donationUrl=donationUrl;
        this.description=description;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public void setXdaUrl(String xdaUrl) {
        this.xdaUrl = xdaUrl;
    }

    public void setGitId(String gitId) {
        this.gitId = gitId;
    }

    public void setDonationUrl(String donationUrl) {
        this.donationUrl = donationUrl;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getXdaUrl() {
        return xdaUrl;
    }

    public String getGitId() {
        return gitId;
    }

    public String getDonationUrl() {
        return donationUrl;
    }

    public String getDescription() {
        return description;
    }
}
