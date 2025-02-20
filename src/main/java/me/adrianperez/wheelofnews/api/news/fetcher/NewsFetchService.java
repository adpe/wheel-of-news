package me.adrianperez.wheelofnews.api.news.fetcher;

import me.adrianperez.wheelofnews.NewsArticle;
import me.adrianperez.wheelofnews.NewsArticleRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class NewsFetchService {

    private final WebClient webClient;
    private final NewsArticleRepository newsArticleRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${news.api.key}")
    private String apiKey;

    public NewsFetchService(WebClient webClient, NewsArticleRepository newsArticleRepository) {
        this.webClient = webClient;
        this.newsArticleRepository = newsArticleRepository;
    }

    public Mono<Void> fetchTechNews() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return webClient.get().uri(uriBuilder -> uriBuilder.path("/everything").queryParam("q", "web development technology").queryParam("from", today.minusDays(1).format(formatter)).queryParam("to", today.format(formatter)).queryParam("sortBy", "popularity").queryParam("apiKey", apiKey).build()).retrieve().bodyToMono(String.class).flatMap(response -> {
            List<NewsArticle> articles = parseJsonToNewsArticles(response);
            return saveArticlesToDatabase(articles);
        });
    }

    private List<NewsArticle> parseJsonToNewsArticles(String json) {
        List<NewsArticle> articles = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(json);
            JsonNode articlesNode = root.get("articles");

            articlesNode.forEach(articleNode -> {
                NewsArticle article = new NewsArticle();
                article.setTitle(articleNode.get("title").asText());
                article.setUrl(articleNode.get("url").asText());

                OffsetDateTime offsetDateTime = OffsetDateTime.parse(articleNode.get("publishedAt").asText(), DateTimeFormatter.ISO_DATE_TIME);
                article.setPublishedAt(offsetDateTime.toLocalDate().toString());

                articles.add(article);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return articles;
    }

    private Mono<Void> saveArticlesToDatabase(List<NewsArticle> articles) {
        return Mono.fromRunnable(() -> articles.stream().filter(article -> !newsArticleRepository.existsByUrl(article.getUrl())).forEach(newsArticleRepository::save));
    }
}


