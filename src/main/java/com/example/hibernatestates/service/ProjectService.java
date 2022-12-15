package com.example.hibernatestates.service;


import com.example.hibernatestates.entity.Project;
import org.hibernate.Session;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProjectService {
    public void persistProject(Project project, Session session){
        session.persist(project);
    }

    public void saveProject(Project project, Session session){
        session.save(project);
    }

    public void updateProject(Project project, Session session){
        if(session.find(Project.class, project.getId()) == null){
           throw new RuntimeException("Project with id " + project.getId() + " not found");
        }
        session.merge(project);
    }

    public void deleteProject(Project project, Session session){
        if(session.find(Project.class, project.getId()) == null){
            throw new RuntimeException("Project with id " + project.getId() + " not found");
        }
        session.remove(project);
    }

    public Project getProjectById(Integer id, Session session){
        Optional<Project> project = Optional.of(session.find(Project.class, id));
        if (project.isEmpty()){
            throw new RuntimeException("Project with id " + id + " not found");
        }
        return project.get();
    }
}
