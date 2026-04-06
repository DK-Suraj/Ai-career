package com.careerai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.careerai.model.Skill;

import java.util.List;

public interface SkillRepository extends JpaRepository<Skill, Integer> {

    List<Skill> findByUserId(Integer userId);

}