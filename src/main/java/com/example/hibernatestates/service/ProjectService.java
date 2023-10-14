package com.example.hibernatestates.service;

import com.example.hibernatestates.entity.Project;
import org.hibernate.Session;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {
    public void persistProject(Project project, Session session) {
        session.persist(project);
    }

    public void saveProject(Project project, Session session) {
        session.save(project);
    }

    public void updateProject(Project project, Session session) {
        if (getProjectById(project.getId(), session) != null) {
            session.merge(project);
        }
    }

    public void deleteProject(Project project, Session session) {
        if (getProjectById(project.getId(), session) != null) {
            session.remove(project);
        }
    }

    public Project getProjectById(Integer id, Session session) {
        Project project = session.find(Project.class, id);
        if (project == null) {
            throw new RuntimeException("Project with id " + id + " not found");
        }
        return project;
    }
}
