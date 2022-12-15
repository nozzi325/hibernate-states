package com.example.hibernatestates.service;

import com.example.hibernatestates.entity.Contact;
import com.example.hibernatestates.entity.Employee;
import org.hibernate.Session;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {
    public void persistEmployee(Employee employee, Session session){
        session.persist(employee);
    }

    public void deleteEmployee(Employee employee, Session session){
        if(session.find(Employee.class, employee.getId()) == null){
            throw new RuntimeException("Employee with id " + employee.getId() + " not found");
        }
        session.remove(employee);
    }

    public void updateEmployee(Employee employee, Session session){
        if(session.find(Employee.class, employee.getId()) == null){
            throw new RuntimeException("Employee with id " + employee.getId() + " not found");
        }
        session.merge(employee);
    }
}
