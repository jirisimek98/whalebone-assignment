package io.whalebone.teams.pojo;

public class Team {

    private String name;
    private String location;
    private int founded;
    private int firstYearOfPlay;
    private Division division;
    private String officialSiteUrl;

    public Team() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getFounded() {
        return founded;
    }

    public void setFounded(int founded) {
        this.founded = founded;
    }

    public int getFirstYearOfPlay() {
        return firstYearOfPlay;
    }

    public void setFirstYearOfPlay(int firstYearOfPlay) {
        this.firstYearOfPlay = firstYearOfPlay;
    }

    public Division getDivision() {
        return division;
    }

    public void setDivision(Division division) {
        this.division = division;
    }

    public String getOfficialSiteUrl() {
        return officialSiteUrl;
    }

    public void setOfficialSiteUrl(String officialSite) {
        this.officialSiteUrl = officialSite;
    }
    
}
