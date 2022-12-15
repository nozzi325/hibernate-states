package com.example.hibernatestates.service;

import com.example.hibernatestates.entity.Contact;
import com.example.hibernatestates.entity.Employee;
import jakarta.persistence.PersistenceException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ContactServiceTest {
    @Autowired
    ContactService contactService;

    @Autowired
    SessionFactory sessionFactory;

    @Test
    void persistContact_ThrowException_WhenNoEmployeeEntered() {
        //given
        Session session = sessionFactory.openSession();
        Contact contact = new Contact();
        contact.setPhoneNumber("9999999");

        //when & verify
        assertThrows(PersistenceException.class,() -> contactService.persistContact(contact,session));
    }

    @Test
    void persistContact_ThrowException_WhenEmployeeNotPersistedYet() {
        //given
        Session session = sessionFactory.openSession();
        Employee employee = new Employee();
        employee.setName("Employee 1");

        Contact contact = new Contact();
        contact.setPhoneNumber("9999999");
        contact.setEmployee(employee);

        //when & verify
        assertThrows(IllegalStateException.class,() -> contactService.persistContact(contact,session));
    }

    @Test
    void persistContact_OK(){
        //given
        Session session = sessionFactory.openSession();
        Employee employee = new Employee();
        employee.setName("Employee 1");
        session.save(employee);

        Contact contact = new Contact();
        contact.setPhoneNumber("9999999");
        contact.setEmployee(employee);

        //when
        contactService.persistContact(contact,session);

        //verify
        assertTrue(session.contains(contact));
    }

    @Test
    void updateContact_OK() {
        //given
        Session session = sessionFactory.openSession();
        Employee employee = new Employee();
        employee.setName("Employee");
        session.save(employee);

        Contact contactBefore = new Contact();
        contactBefore.setPhoneNumber("contact_number_before");
        contactBefore.setEmployee(employee);
        session.save(contactBefore);

        Contact contactAfter = new Contact();
        contactAfter.setId(contactBefore.getId());
        contactAfter.setPhoneNumber("contact_number_after");
        contactAfter.setEmployee(employee);

        //when
        contactService.updateContact(contactAfter, session);

        //verify
        assertTrue(session.contains(contactBefore));
        assertEquals(contactAfter,contactBefore);
    }

    @Test
    void updateContact_ThrowException_WhenNonExistingContact() {
        //given
        Session session = sessionFactory.openSession();
        Contact contact = new Contact();
        contact.setId(99999);
        contact.setPhoneNumber("phone_number");

        //when & verify
        assertThrows(RuntimeException.class,() -> contactService.updateContact(contact, session));
    }

    @Test
    void deleteContact_OK() {
        //given
        Session session = sessionFactory.openSession();
        Employee employee = new Employee();
        employee.setName("Employee");
        session.save(employee);

        Contact contact = new Contact();
        contact.setPhoneNumber("To be deleted");
        contact.setEmployee(employee);
        session.save(contact);

        contact = session.get(Contact.class, contact.getId());

        //when
        contactService.deleteContact(contact, session);

        //verify
        assertFalse(session.contains(contact));
        assertNull(session.find(Contact.class, contact.getId()));
    }

    @Test
    void deleteContact_ThrowException_WhenNonExistingContact() {
        //given
        Session session = sessionFactory.openSession();
        Contact contact = new Contact();
        contact.setId(99999);

        //when & verify
        assertThrows(RuntimeException.class,() -> contactService.updateContact(contact, session));
    }
}