/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import DAOs.AssetDAO;
import model.Asset;
import java.util.List;

/**
 * Controlador para la lógica de activos.
 */
public class AssetController {

    private AssetDAO dao = new AssetDAO();

    public List<Asset> getAll(String search, String status) {
        return dao.findAll(search, status);
    }

    public Asset getById(int id) {
        return dao.findById(id);
    }

    public boolean create(Asset a) {
        return dao.create(a);
    }

    public boolean update(Asset a) {
        return dao.update(a);
    }

    public boolean delete(int id) {
        return dao.delete(id);
    }

    public List<Asset> available() {
        return dao.available();
    }
}

