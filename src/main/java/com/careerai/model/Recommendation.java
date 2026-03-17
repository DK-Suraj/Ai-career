package com.careerai.model;

import jakarta.persistence.*;

@Entity
@Table(name="recommendation")
public class Recommendation {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Integer id;

private Integer userId;

private String jobRole;

@Column(length = 1000)
private String missingSkills;

@Column(length = 2000)
private String learningRoadmap;

private String salaryPrediction;

public Recommendation(){}

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

public String getJobRole() {
return jobRole;
}

public void setJobRole(String jobRole) {
this.jobRole = jobRole;
}

public String getMissingSkills() {
return missingSkills;
}

public void setMissingSkills(String missingSkills) {
this.missingSkills = missingSkills;
}

public String getLearningRoadmap() {
return learningRoadmap;
}

public void setLearningRoadmap(String learningRoadmap) {
this.learningRoadmap = learningRoadmap;
}

public String getSalaryPrediction() {
return salaryPrediction;
}

public void setSalaryPrediction(String salaryPrediction) {
this.salaryPrediction = salaryPrediction;
}

}