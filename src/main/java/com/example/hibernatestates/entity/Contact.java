package com.example.hibernatestates.entity;

import jakarta.persistence.*;

import lombok.Data;

@Entity
@Data
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String phoneNumber;
    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Override
    public String toString() {
        return "Contact{" +
                "id=" + id +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", employee=" + employee.getId() +
                '}';
    }
}
