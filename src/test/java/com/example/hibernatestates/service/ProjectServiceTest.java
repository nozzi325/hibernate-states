package com.example.hibernatestates.service;

import com.example.hibernatestates.entity.Project;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class ProjectServiceTest {
    @Autowired
    ProjectService projectService;
    @Autowired
    SessionFactory sessionFactory;

    @Test
    void givenTransientProject_whenNotPersisted_thenShouldNotBeInSessionAndHaveNoId() {
        // Create a session
        Session session = sessionFactory.openSession();

        // Create a project in the Transient state
        Project project = new Project();
        project.setName("project_name");
        project.setCompleted(true);

        // Verify that the project is in the Transient state before calling the persist() method
        assertFalse(session.contains(project));
        assertTrue(project.getId() == 0);
    }

    @Test
    void givenTransientProject_whenPersisted_thenShouldBeInPersistentState() {
        // Create a session
        Session session = sessionFactory.openSession();

        // Create a project in the Transient state
        Project project = new Project();
        project.setName("project_name");
        project.setCompleted(true);

        // Call the method to persist the project, transitioning it to Persistent state
        projectService.persistProject(project, session);

        // Verify that the project is in Persistent state
        assertTrue(session.contains(project));
    }

    @Test
    void givenTransientProject_whenSaved_thenShouldBeInPersistentStateAndHaveGeneratedId() {
        // Create a session
        Session session = sessionFactory.openSession();

        // Create a project in the Transient state
        Project project = new Project();
        project.setName("project_name");
        project.setCompleted(true);

        // Call the method to save the project, transitioning it to Persistent state and obtaining an identifier
        projectService.saveProject(project, session);

        // Verify that the project is in the Persistent state and has a non-zero identifier
        assertTrue(session.contains(project));
        assertFalse(project.getId() == 0);
    }

    @Test
    void givenPersistentProject_whenUpdated_thenShouldReflectChanges() {
        // Create a session
        Session session = sessionFactory.openSession();

        // Create a project and save it with a name "project_name_before"
        Project projectBefore = new Project();
        projectBefore.setName("project_name_before");
        projectBefore.setCompleted(true);
        session.save(projectBefore);

        // Create another project with the same identifier and a different name "project_name_after"
        Project projectAfter = new Project();
        projectAfter.setId(projectBefore.getId());
        projectAfter.setName("project_name_after");
        projectAfter.setCompleted(false);

        // Call the method to update the project, transitioning it to Persistent state
        projectService.updateProject(projectAfter, session);

        // Verify that the project is still in Persistent state and its name has changed
        assertTrue(session.contains(projectBefore));
        assertFalse(projectBefore.getName().equals("project_name_before"));
    }

    @Test
    void givenPersistentProject_whenDeleted_thenShouldBeInRemovedState() {
        // Create a session
        Session session = sessionFactory.openSession();

        // Create a project and save it, then get it by its identifier
        Project project = new Project();
        project.setName("project_name");
        project.setCompleted(true);
        session.save(project);
        project = session.get(Project.class, 1);

        // Call the method to delete the project, transitioning it to the Removed state
        projectService.deleteProject(project, session);

        // Verify that the project is no longer in the session and has been deleted from the database
        assertFalse(session.contains(project));
        assertNull(session.find(Project.class, project.getId()));
    }

    @Test
    void givenPersistentProject_whenDetached_thenShouldBeInDetachedState() {
        // Create a session
        Session session = sessionFactory.openSession();

        // Create a project and save it
        Project project = new Project();
        project.setName("project_name");
        project.setCompleted(true);
        session.save(project);

        // Call the method to get a project by its identifier, transitioning it to Persistent state
        Project actualProject = projectService.getProjectById(project.getId(), session);

        // Check status before detach
        assertTrue(session.contains(actualProject));

        // Detach the project from the session
        session.detach(actualProject);

        // Verify that the project is in Detached state and no longer associated with the session
        assertFalse(session.contains(actualProject));

        // Check that no collections are associated with the session
        assertEquals(0, session.getStatistics().getCollectionCount());
    }
}