package sg.edu.nus.iss.ssfproject.constant;

public class Url {
    public static final String animeGenres = "https://api.jikan.moe/v4/genres/anime";
    public static final String animesByGenreId = "https://api.jikan.moe/v4/anime?genres=%s&order_by=score&sort=desc&limit=10";
    public static final String animesByQuery = "https://api.jikan.moe/v4/anime?q=%s&limit=15&sfw=true";
    public static final String animeById = "https://api.jikan.moe/v4/anime/%s";
}
