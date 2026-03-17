package com.careerai.model;

import jakarta.persistence.*;

@Entity
@Table(name="skills")
public class Skill {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Integer id;

private Integer userId;

private String skillName;

public Skill(){}

public Skill(Integer id,Integer userId,String skillName){
this.id=id;
this.userId=userId;
this.skillName=skillName;
}

public Integer getId() {
return id;
}

public void setId(Integer id) {
this.id = id;
}

public Integer getUserId() {
return userId;
}

public void setUserId(Integer userId) {
this.userId = userId;
}

public String getSkillName() {
return skillName;
}

public void setSkillName(String skillName) {
this.skillName = skillName;
}

}