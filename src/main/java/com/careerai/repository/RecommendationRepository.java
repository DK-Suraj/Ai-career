package com.careerai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.careerai.model.Recommendation;

public interface RecommendationRepository extends JpaRepository<Recommendation, Integer> {

    Recommendation findByUserId(Integer userId);

}