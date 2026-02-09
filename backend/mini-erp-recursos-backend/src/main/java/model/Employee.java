/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.time.LocalDateTime;

/**
 *
 * @author isierra
 */
public class Employee {
    //atributos
    private int id;
    private String name;
    private String surname;
    private String email;
    private String department;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructor vacío (necesario para muchas cosas: frameworks, etc.) 
    public Employee() {} 

    // Constructor con todos los campos (opcional pero muy útil) 
    public Employee(int id, String name, String surname, String email,String department, boolean active,LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.department = department;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters y setters
    //get=devuelve 
    //set=cambia 
    
    public int getId() {
        return id; 
    }
    public void setId(int id) {
        this.id = id; 
    }
    
    public String getName() {
        return name; 
    } 
    public void setName(String name) {
        this.name = name; 
    }
    
    public String getSurname() {
        return surname;
    }
    public void setSurname(String surname) {
        this.surname = surname; 
    }
    
    public String getEmail() {
        return email; 
    } 
    public void setEmail(String email) {
        this.email = email; 
    } 
    
    public String getDepartment() {
        return department; 
    } 
    public void setDepartment(String department) {
        this.department = department; 
    }
    
    public boolean isActive() { //boolean no get, 'is'
        return active; 
    } 
    public void setActive(boolean active) {
        this.active = active; 
    } 
    
    public LocalDateTime getCreatedAt() {
        return createdAt; 
    } 
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt; 
    } 
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt; 
    } 
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt; 
    }
    
    @Override
    public String toString() {
        return "Employee{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", email='" + email + '\'' +
            ", department='" + department + '\'' +
            '}';
    }

}
