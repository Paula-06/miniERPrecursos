/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import DAOs.AssignmentDAO;
import model.Assignment;
import java.util.List;

/**
 * Controlador para la lógica de asignaciones.
 */
public class AssignmentController {

    private AssignmentDAO dao = new AssignmentDAO();

    public List<Assignment> findByEmployeeId(int employeeId) {
        return dao.findByEmployeeId(employeeId);
    }

    public boolean assign(int employeeId, int assetId) {
        return dao.assign(employeeId, assetId);
    }

    public boolean returnAsset(int assignmentId) {
        return dao.returnAsset(assignmentId);
    }
    
    public int countActiveAssignments() {
        return dao.countActive();
    }

}

