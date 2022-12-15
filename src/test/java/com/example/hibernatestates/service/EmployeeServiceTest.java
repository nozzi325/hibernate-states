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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EmployeeServiceTest {
    @Autowired
    EmployeeService employeeService;

    @Autowired
    SessionFactory sessionFactory;

    @Test
    void persistEmployee_PersistInnerEntities() {
        //given
        Session session = sessionFactory.openSession();
        Employee employee = new Employee();
        employee.setName("Employee 1");

        Contact contact1 = new Contact();
        contact1.setPhoneNumber("phone_1");
        Contact contact2 = new Contact();
        contact2.setPhoneNumber("phone_2");

        contact1.setEmployee(employee);
        contact2.setEmployee(employee);

        List<Contact> list = new ArrayList<>();
        list.add(contact1);
        list.add(contact2);

        employee.setContacts(list);

        //when
        employeeService.persistEmployee(employee,session);


        //verify
        assertTrue(session.contains(employee));
        assertTrue(session.contains(contact1));
        assertTrue(session.contains(contact2));
    }

    @Test
    void persistEmployee_OneEntity(){
        //given
        Session session = sessionFactory.openSession();
        Employee employee = new Employee();
        employee.setName("Employee 1");

        //when
        employeeService.persistEmployee(employee,session);

        //verify
        assertTrue(session.contains(employee));
    }

    @Test
    void deleteEmployee_ThrowException_WhenNonExistingEmployee() {
        //given
        Session session = sessionFactory.openSession();
        Employee employee = new Employee();
        employee.setId(999999);

        //when
        assertThrows(RuntimeException.class, () -> employeeService.deleteEmployee(employee,session));
    }

    @Test
    void deleteEmployee_OK() {
        //given
        Session session = sessionFactory.openSession();
        Employee employee = new Employee();
        employee.setName("Employee 1");
        session.save(employee);

        //when
        employeeService.deleteEmployee(employee,session);

        //verify
        assertFalse(session.contains(employee));
        assertNull(session.find(Employee.class, employee.getId()));
    }

    @Test
    void updateEmployee_OK(){
        //given
        Session session = sessionFactory.openSession();
        Employee employeeBefore = new Employee();
        employeeBefore.setName("Name_before");
        session.save(employeeBefore);

        Employee employeeAfter = new Employee();
        employeeAfter.setId(employeeBefore.getId());
        employeeAfter.setName("Name_after");

        //when
        employeeService.updateEmployee(employeeAfter,session);

        //verify
        assertEquals(employeeBefore.getName(),employeeAfter.getName());
    }

    @Test
    void updateEmployee_ThrowException_WhenNonExistingEmployee(){
        //given
        Session session = sessionFactory.openSession();
        Employee employee = new Employee();
        employee.setId(99999);

        //when & verify
        assertThrows(RuntimeException.class, () -> employeeService.updateEmployee(employee,session));
    }
}