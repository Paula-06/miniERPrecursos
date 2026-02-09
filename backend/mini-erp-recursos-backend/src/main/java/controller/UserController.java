/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import DAOs.UserDAO;
import model.User;

/**
 * Controlador para autenticación y usuarios.
 */
public class UserController {

    private UserDAO dao = new UserDAO();

    public User validate(String username, String passwordHash) {
        return dao.validate(username, passwordHash);
    }
}
