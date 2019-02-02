package com.antoinebourgeolet.tviscovery;

import com.google.gson.annotations.SerializedName;

class TVShow {
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("genre")
    private String[] genre;

    @SerializedName("image")
    private String image;

    @SerializedName("synopsis")
    private String synopsis;

    @SerializedName("video")
    private String video;

    private Boolean viewed;

    private Boolean liked;

    private Boolean disliked;

    private Boolean addedToList;

    public TVShow(String name, String[] genre, String image, String synopsis, String video) {
        this.name = name;
        this.genre = genre;
        this.image = image;
        this.synopsis = synopsis;
        this.video = video;
        viewed = false;
        liked = false;
        disliked = false;
        addedToList = false;
    }

    public TVShow(String name, String[] genre, String image, String synopsis, String video, Boolean viewed,
                  Boolean liked, Boolean disliked, Boolean addedToList) {
        this.name = name;
        this.genre = genre;
        this.image = image;
        this.synopsis = synopsis;
        this.video = video;
        this.viewed = viewed;
        this.liked = liked;
        this.disliked = disliked;
        this.addedToList = addedToList;
    }

    public TVShow(int id, String name, String strGenre, String image, String synopsis, String video, int intViewed,
                  int intLiked, int intDisliked, int intAddedToList) {
        this.id = id;
        this.name = name;
        this.genre = strGenre.split("_");
        this.image = image;
        this.synopsis = synopsis;
        this.video = video;
        this.viewed = intViewed != 0;
        this.liked = intLiked != 0;
        this.disliked = intDisliked != 0;
        this.addedToList = intAddedToList != 0;
    }


    public String getGenreToString() {
        String strGenre = "";
        for (String value : genre) {
            if (strGenre.isEmpty())
                strGenre = value;
            else
                strGenre += "_" + value;
        }
        return strGenre;
    }

    public String getGenreForDisplay() {
        String strGenre = "";
        for (String value : genre) {
            if (strGenre.isEmpty())
                strGenre = value;
            else
                strGenre += " - " + value;
        }
        return strGenre;
    }

    public int getId() {
        return id;
    }

    public void setGenreFromString(String strGenre) {
        this.genre = strGenre.split("_");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getGenre() {
        return genre;
    }

    public void setGenre(String[] genre) {
        this.genre = genre;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public int getViewedInt() {
        int value;
        if (this.viewed) value = 1;
        else value = 0;
        return value;
    }

    public void setViewed(int viewed) {
        if (viewed == 0) {
            this.viewed = true;
        } else {
            this.viewed = true;
        }
    }

    public int getLikedInt() {
        int value;
        if (this.liked) value = 1;
        else value = 0;
        return value;
    }

    public void setLiked(int liked) {
        if (liked == 0) {
            this.liked = true;
        } else {
            this.liked = true;
        }
    }

    public int getDislikedInt() {
        int value;
        if (this.disliked) value = 1;
        else value = 0;
        return value;
    }

    public void setDisliked(int disliked) {
        if (disliked == 0) {
            this.disliked = true;
        } else {
            this.disliked = true;
        }
    }

    public int getAddedToListInt() {
        int value;
        if (this.addedToList) value = 1;
        else value = 0;
        return value;
    }

    public void setAddedToList(int addedToList) {
        if (addedToList == 0) {
            this.addedToList = true;
        } else {
            this.addedToList = true;
        }
    }

    public Boolean getViewed() {
        return viewed;
    }

    public void setViewed(Boolean viewed) {
        this.viewed = viewed;
    }

    public Boolean getLiked() {
        return liked;
    }

    public void setLiked(Boolean liked) {
        this.liked = liked;
    }

    public Boolean getDisliked() {
        return disliked;
    }

    public void setDisliked(Boolean disliked) {
        this.disliked = disliked;
    }

    public Boolean getAddedToList() {
        return addedToList;
    }

    public void setAddedToList(Boolean addedToList) {
        this.addedToList = addedToList;
    }
}
