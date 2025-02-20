package me.adrianperez.wheelofnews;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class NewsArticleService {

    @Autowired
    private NewsArticleRepository newsArticleRepository;

    public Optional<NewsArticle> getRandomNews() {
        Random random = new Random();
        LocalDate now = LocalDate.now();
        LocalDate yesterday = now.minusDays(1);

        List<NewsArticle> newsArticles = newsArticleRepository.findByPublishedBetween(yesterday.toString(), now.toString());
        if (newsArticles.isEmpty()) {
            return Optional.empty();
        }

        if (newsArticles.size() == 1) {
            return Optional.of(newsArticles.getFirst());
        }

        return Optional.ofNullable(newsArticles.get(random.nextInt(newsArticles.size())));
    }
}
