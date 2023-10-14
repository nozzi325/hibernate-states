package com.example.hibernatestates.service;

import com.example.hibernatestates.entity.Contact;
import com.example.hibernatestates.entity.Employee;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class EmployeeServiceTest {
    @Autowired
    EmployeeService employeeService;

    @Autowired
    SessionFactory sessionFactory;

    @Test
    void givenTransientEmployeeWithContacts_whenPersisted_thenAllEntitiesBecomePersistent() {
        // Create a session
        Session session = sessionFactory.openSession();

        // Create an employee and inner contact entities
        Employee employee = new Employee();
        employee.setName("Employee 1");

        Contact contact1 = new Contact();
        contact1.setPhoneNumber("phone_1");
        Contact contact2 = new Contact();
        contact2.setPhoneNumber("phone_2");

        // Establish associations between employee and contacts
        contact1.setEmployee(employee);
        contact2.setEmployee(employee);

        List<Contact> contacts = new ArrayList<>();
        contacts.add(contact1);
        contacts.add(contact2);

        employee.setContacts(contacts);

        // Call the method to persist the employee and its inner entities, transitioning them to Persistent state
        employeeService.persistEmployee(employee, session);

        // Verify that employee and contact entities are in Persistent state
        assertTrue(session.contains(employee));
        assertTrue(session.contains(contact1));
        assertTrue(session.contains(contact2));
    }

    @Test
    void givenTransientEmployee_whenPersisted_thenShouldBeInPersistentState() {
        // Create a session
        Session session = sessionFactory.openSession();

        // Create an Employee entity in Transient state
        Employee employee = new Employee();
        employee.setName("Employee 1");

        // Call the method to persist the Employee, transitioning it to Persistent state
        employeeService.persistEmployee(employee, session);

        // Verify that the Employee entity is now in the Persistent state
        assertTrue(session.contains(employee));
    }

    @Test
    void givenPersistentEmployee_whenDeleted_thenShouldBeInRemovedState() {
        // Create a session
        Session session = sessionFactory.openSession();

        // Create an Employee entity and save it to the database
        Employee employee = new Employee();
        employee.setName("Employee 1");
        session.save(employee);

        // Call the method to delete the Employee, transitioning it to the Removed state
        employeeService.deleteEmployee(employee, session);

        // Verify that the Employee is no longer in the session and has been deleted from the database
        assertFalse(session.contains(employee));
        assertNull(session.find(Employee.class, employee.getId()));
    }

    @Test
    void givenEmployeeWithChangedName_whenUpdated_thenShouldReflectChanges() {
        // Create a session
        Session session = sessionFactory.openSession();

        // Create an Employee entity and save it with the name "Name_before"
        Employee employeeBefore = new Employee();
        employeeBefore.setName("Name_before");
        session.save(employeeBefore);

        // Create another Employee entity with the same identifier and a name "Name_after"
        Employee employeeAfter = new Employee();
        employeeAfter.setId(employeeBefore.getId());
        employeeAfter.setName("Name_after");

        // Call the method to update the Employee, transitioning it to Persistent state
        employeeService.updateEmployee(employeeAfter, session);

        // Verify that the employee's name has been updated
        assertFalse(employeeBefore.getName().equals("Name_before"));
        // Check that the updated employee is not in Persistent state and the original employee is still in the session
        assertFalse(session.contains(employeeAfter));
        assertTrue(session.contains(employeeBefore));
    }
}