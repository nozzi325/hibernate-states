package com.example.hibernatestates.service;

import com.example.hibernatestates.entity.Contact;
import com.example.hibernatestates.entity.Employee;
import jakarta.persistence.PersistenceException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class ContactServiceTest {
    @Autowired
    ContactService contactService;

    @Autowired
    SessionFactory sessionFactory;

    @Test
    void persistContact_shouldThrowPersistenceException_whenNoEmployeeAssociated() {
        // Create a session
        Session session = sessionFactory.openSession();

        // Create a contact without an associated employee
        Contact contact = new Contact();
        contact.setPhoneNumber("9999999");

        // Expect a `PersistenceException` as the object is in a Transient state (not associated with the session)
        // and Hibernate requires entities to be in a Persistent state before they can be saved to the database.
        // In this case, the Employee-Contact relationship is defined as a @OneToMany association in Employee,
        // and the "cascade = CascadeType.PERSIST" setting indicates that the PERSIST operation should cascade
        // to associated Contact entities. However, since the Contact entity is not associated with any Employee,
        // a PersistenceException should be thrown when attempting to persist it.
        assertThrows(PersistenceException.class, () -> contactService.persistContact(contact, session));
    }

    @Test
    void persistContact_shouldThrowIllegalStateException_whenEmployeeNotPersisted() {
        // Create a session
        Session session = sessionFactory.openSession();

        // Create an Employee instance but do not save it to the database
        Employee employee = new Employee();
        employee.setName("Employee 1");

        // Create a Contact instance and associate it with an unsaved Employee
        Contact contact = new Contact();
        contact.setPhoneNumber("9999999");
        contact.setEmployee(employee);

        // Expect an `IllegalStateException` because the associated Employee is in a Transient state as
        // it hasn't been saved to the database yet. To persist Contact, it must have a Persistent
        // Employee association, and since the Employee is not in a Persistent state, the operation is not allowed.
        assertThrows(IllegalStateException.class, () -> contactService.persistContact(contact, session));
    }

    @Test
    void persistContact_shouldPersistContact_whenEmployeeIsPersisted() {
        // Create a session
        Session session = sessionFactory.openSession();

        // Create and save an Employee instance
        Employee employee = new Employee();
        employee.setName("Employee 1");
        session.save(employee);

        // Create a Contact instance and associate it with the saved Employee
        Contact contact = new Contact();
        contact.setPhoneNumber("9999999");
        contact.setEmployee(employee);

        // Call the method to save the Contact, transitioning it to the Persistent state
        contactService.persistContact(contact, session);

        // Check that the object is in the Persistent state
        assertTrue(session.contains(contact));
    }

    @Test
    void updateContact_shouldUpdateContact_whenContactExists() {
        // Create a session
        Session session = sessionFactory.openSession();

        // Create an Employee and save it
        Employee employee = new Employee();
        employee.setName("Employee");
        session.save(employee);

        // Create a Contact and associate it with the Employee
        Contact contactBefore = new Contact();
        contactBefore.setPhoneNumber("contact_number_before");
        contactBefore.setEmployee(employee);
        session.save(contactBefore);

        // Create a new Contact for updating
        Contact contactAfter = new Contact();
        contactAfter.setId(contactBefore.getId());
        contactAfter.setPhoneNumber("contact_number_after");
        contactAfter.setEmployee(employee);

        // Call the method to update the Contact, transitioning it to the Persistent state
        contactService.updateContact(contactAfter, session);

        // Check that the object is still in the Persistent state, and its property has changed
        assertTrue(session.contains(contactBefore));
        assertFalse(contactBefore.getPhoneNumber().equals("contact_number_before"));
    }

    @Test
    void deleteContact_shouldRemoveContact_WhenContactExists() {
        // Create a session
        Session session = sessionFactory.openSession();

        // Create an Employee object and save it in the database using session.save(employee)
        Employee employee = new Employee();
        employee.setName("Employee");
        session.save(employee);

        // Create a Contact object and associate it with an Employee, then save it in the database using session.save(contact)
        Contact contact = new Contact();
        contact.setPhoneNumber("To be deleted");
        contact.setEmployee(employee);
        session.save(contact);

        // Retrieve the Contact object from the database using its identifier with session.get(Contact.class, contact.getId())
        contact = session.get(Contact.class, contact.getId());

        // Call the deleteContact method to remove the Contact object, transitioning it to the Removed state
        contactService.deleteContact(contact, session);

        // After deletion, check that the Contact object is no longer in the Hibernate session, and there's no database record with its identifier
        assertFalse(session.contains(contact));
    }
}