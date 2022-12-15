package com.example.hibernatestates.service;

import com.example.hibernatestates.entity.Contact;
import org.hibernate.Session;
import org.springframework.stereotype.Service;

@Service
public class ContactService {
    public void persistContact(Contact contact, Session session){
        session.persist(contact);
    }

    public void updateContact(Contact contact, Session session){
        if(session.find(Contact.class, contact.getId()) == null){
            throw new RuntimeException("Contact with id " + contact.getId() + " not found");
        }
        session.merge(contact);
    }

    public void deleteContact(Contact contact, Session session){
        if(session.find(Contact.class, contact.getId()) == null){
            throw new RuntimeException("Contact with id " + contact.getId() + " not found");
        }
        session.remove(contact);
    }
}
