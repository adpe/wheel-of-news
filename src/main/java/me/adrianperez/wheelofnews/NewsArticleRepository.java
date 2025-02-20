package me.adrianperez.wheelofnews;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsArticleRepository extends JpaRepository<NewsArticle, Long> {

    @Query(value = "SELECT n FROM NewsArticle n WHERE n.publishedAt BETWEEN :startDate AND :endDate")
    List<NewsArticle> findByPublishedBetween(@Param("startDate") String startDate, @Param("endDate") String endDate);

    boolean existsByUrl(String url);
}