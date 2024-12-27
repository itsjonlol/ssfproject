package sg.edu.nus.iss.ssfproject.models;

import java.util.List;

public class Anime {
    
    private Integer mal_id;
    private String large_image_url;
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
    private String trailer;


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
            Integer episodes, String status, String duration, Double score, Integer rank, String synopsis, Integer year,
            List<String> producers, List<String> studios, List<String> genres, String trailer) {
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
        this.trailer = trailer;
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
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Anime anime = (Anime) obj;
        if (mal_id == null) {
            if (anime.mal_id != null)
                return false;
        } else if (!mal_id.equals(anime.mal_id))
            return false;
        return true;
    }
    @Override
    public int hashCode() {
        return mal_id == null ? 0 : mal_id.hashCode(); // Use the same unique identifier
    }





    @Override
    public String toString() {
        return "Anime [mal_id=" + mal_id + ", title=" + title
                + ", type=" + type + ", genres=" + genres + "]";
    }





    public String getTrailer() {
        return trailer;
    }





    public void setTrailer(String trailer) {
        this.trailer = trailer;
    }
    
    
}
