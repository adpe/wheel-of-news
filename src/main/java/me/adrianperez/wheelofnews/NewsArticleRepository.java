package me.adrianperez.wheelofnews;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface NewsArticleRepository extends JpaRepository<NewsArticle, Long> {

    @Query(value = "SELECT n FROM NewsArticle n WHERE n.publishedAt BETWEEN :startDate AND :endDate")
    List<NewsArticle> findByPublishedBetween(@Param("startDate") String startDate, @Param("endDate") String endDate);

    @Query(value = "SELECT n FROM NewsArticle n WHERE n.publishedAt = :date")
    List<NewsArticle> findByPublished(@Param("date") String date);
}