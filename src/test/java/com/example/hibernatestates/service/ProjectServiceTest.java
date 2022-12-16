package com.example.hibernatestates.service;

import com.example.hibernatestates.entity.Project;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProjectServiceTest {
    @Autowired ProjectService projectService;
    @Autowired
    SessionFactory sessionFactory;

    @Test
    void persistProject_TransientState(){
        //given
        Session session = sessionFactory.openSession();
        Project project = new Project();
        project.setName("project_name");
        project.setCompleted(true);

        //Verify that project is in Transient state before calling persist() method
        assertFalse(session.contains(project));
        assertTrue(project.getId() == 0);
    }
    @Test
    void persistProject_PersistentState() {
        //given
        Session session = sessionFactory.openSession();
        Project project = new Project();
        project.setName("project_name");
        project.setCompleted(true);

        //when
        projectService.persistProject(project, session);

        //verify
        assertTrue(session.contains(project));
    }

    @Test
    void saveProject_PersistentState() {
        //given
        Session session = sessionFactory.openSession();
        Project project = new Project();
        project.setName("project_name");
        project.setCompleted(true);

        //when
        projectService.saveProject(project, session);

        //verify
        assertTrue(session.contains(project));
        assertFalse(project.getId() == 0);
    }

    @Test
    void updateProject_OK() {
        //given
        Session session = sessionFactory.openSession();
        Project projectBefore = new Project();
        projectBefore.setName("project_name_before");
        projectBefore.setCompleted(true);
        session.save(projectBefore);

        Project projectAfter = new Project();
        projectAfter.setId(projectBefore.getId());
        projectAfter.setName("project_name_after");
        projectAfter.setCompleted(false);

        //when
        projectService.updateProject(projectAfter, session);

        //verify
        assertTrue(session.contains(projectBefore));
        assertFalse(projectBefore.getName().equals("project_name_before"));
    }

    @Test
    void updateProject_ThrowException_WhenNonExistingProject() {
        //given
        Session session = sessionFactory.openSession();
        Project projectAfter = new Project();
        projectAfter.setId(99999);
        projectAfter.setName("project_name_after");
        projectAfter.setCompleted(false);

        //when & verify
        assertThrows(RuntimeException.class,() -> projectService.updateProject(projectAfter, session));
    }

    @Test
    void deleteProject_RemovedState(){
        //given
        Session session = sessionFactory.openSession();
        Project project = new Project();
        project.setName("project_name");
        project.setCompleted(true);
        session.save(project);
        project = session.get(Project.class, 1);

        //when
        projectService.deleteProject(project, session);

        //verify
        assertFalse(session.contains(project));
        assertNull(session.find(Project.class, project.getId()));
    }

    @Test
    void getProject_DetachedState(){
        //given
        Session session = sessionFactory.openSession();
        Project project = new Project();
        project.setName("project_name");
        project.setCompleted(true);
        session.save(project);

        //when
        Project actualProject = projectService.getProjectById(project.getId(), session);


        //Check status before detach
        assertTrue(session.contains(actualProject));

        session.detach(actualProject);

        //verify after detach
        assertFalse(session.contains(actualProject));
        assertEquals(0,session.getStatistics().getCollectionCount());
    }
}