package sg.edu.nus.iss.ssfproject.models;

import java.util.List;
import java.util.Objects;

public class Anime {
    
    private Integer mal_id;
    //which image
    private String large_image_url;

    // youtube trailer omit first
    private String title;
    private String title_japanese;
    private String type;
    private Integer episodes;
    private String status;
    private String duration;
    private Double score;
    private Integer rank;
    private String synopsis;
    private Integer year;
    private List<String> producers;
    private List<String> studios;
    private List<String> genres;

    public Anime() {

    }

    

    

    public Anime(Integer mal_id, String large_image_url, String title) {
        this.mal_id = mal_id;
        this.large_image_url = large_image_url;
        this.title = title;
    }



    public Anime(Integer mal_id, String large_image_url, String title, List<String> genres) {
        this.mal_id = mal_id;
        this.large_image_url = large_image_url;
        this.title = title;
        this.genres = genres;
    }





    public Anime(Integer mal_id, String large_image_url, String title, String title_japanese, String type,
            Integer episodes, String status, String duration, Double score, Integer rank, String synopsis,
            Integer year) {
        this.mal_id = mal_id;
        this.large_image_url = large_image_url;
        this.title = title;
        this.title_japanese = title_japanese;
        this.type = type;
        this.episodes = episodes;
        this.status = status;
        this.duration = duration;
        this.score = score;
        this.rank = rank;
        this.synopsis = synopsis;
        this.year = year;
    }




    public Anime(Integer mal_id, String large_image_url, String title, String title_japanese, String type,
            Integer episodes, String status, String duration, Double score, Integer rank, String synopsis, Integer year,
            List<String> producers, List<String> studios, List<String> genres) {
        this.mal_id = mal_id;
        this.large_image_url = large_image_url;
        this.title = title;
        this.title_japanese = title_japanese;
        this.type = type;
        this.episodes = episodes;
        this.status = status;
        this.duration = duration;
        this.score = score;
        this.rank = rank;
        this.synopsis = synopsis;
        this.year = year;
        this.producers = producers;
        this.studios = studios;
        this.genres = genres;
    }



    public Integer getMal_id() {
        return mal_id;
    }
    public void setMal_id(Integer mal_id) {
        this.mal_id = mal_id;
    }
    public String getLarge_image_url() {
        return large_image_url;
    }
    public void setLarge_image_url(String large_image_url) {
        this.large_image_url = large_image_url;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getTitle_japanese() {
        return title_japanese;
    }
    public void setTitle_japanese(String title_japanese) {
        this.title_japanese = title_japanese;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public Integer getEpisodes() {
        return episodes;
    }
    public void setEpisodes(Integer episodes) {
        this.episodes = episodes;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getDuration() {
        return duration;
    }
    public void setDuration(String duration) {
        this.duration = duration;
    }
    public Double getScore() {
        return score;
    }
    public void setScore(Double score) {
        this.score = score;
    }
    public Integer getRank() {
        return rank;
    }
    public void setRank(Integer rank) {
        this.rank = rank;
    }
   
    public Integer getYear() {
        return year;
    }
    public void setYear(Integer year) {
        this.year = year;
    }
    public List<String> getProducers() {
        return producers;
    }
    public void setProducers(List<String> producers) {
        this.producers = producers;
    }
    public List<String> getStudios() {
        return studios;
    }
    public void setStudios(List<String> studios) {
        this.studios = studios;
    }
    public List<String> getGenres() {
        return genres;
    }
    public void setGenres(List<String> genres) {
        this.genres = genres;
    }


    public String getSynopsis() {
        return synopsis;
    }


    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Anime anime = (Anime) o;
        return Objects.equals(mal_id, anime.mal_id); // Compare by unique identifier
    }

    @Override
    public int hashCode() {
        return Objects.hash(mal_id); // Use the same unique identifier
    }





    @Override
    public String toString() {
        return "Anime [mal_id=" + mal_id + ", large_image_url=" + large_image_url + ", title=" + title
                + ", title_japanese=" + title_japanese + ", type=" + type + ", episodes=" + episodes + ", status="
                + status + ", duration=" + duration + ", score=" + score + ", rank=" + rank + ", synopsis=" + synopsis
                + ", year=" + year + ", producers=" + producers + ", studios=" + studios + ", genres=" + genres + "]";
    }
    
    
}
