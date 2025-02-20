package me.adrianperez.wheelofnews.api.news.fetcher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.logging.Logger;

@Component
public class NewsFetchTask {

    private final NewsFetchService newsFetchService;

    @Autowired
    public NewsFetchTask(NewsFetchService newsFetchService) {
        this.newsFetchService = newsFetchService;
    }

    private static final Logger logger = Logger.getLogger(NewsFetchTask.class.getName());

    @Scheduled(cron = "${news.fetch.cron}")
    public void fetchAndSaveNews() {
        logger.info("Fetching news at " + LocalDateTime.now());
        newsFetchService.fetchTechNews().subscribe(
                null,
                error -> logger.warning("Error during scheduled fetch: " + error.getMessage())
        );
    }
}
