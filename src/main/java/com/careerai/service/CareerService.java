package com.careerai.service;

import com.careerai.model.Skill;
import com.careerai.repository.SkillRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CareerService {

    @Autowired
    private SkillRepository skillRepository;

    public void saveSkill(Skill skill) {
        skillRepository.save(skill);
    }

    public List<Skill> getUserSkills(Integer userId) {
        return skillRepository.findByUserId(userId);
    }

}