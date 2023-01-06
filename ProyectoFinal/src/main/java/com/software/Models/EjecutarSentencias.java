/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.software.Models;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author drago
 */
public class EjecutarSentencias {

    static ArrayList<String> errores = new ArrayList<>();
    
    public void EjecutarSelect(JTable tableInfo, String sql) {
        //Table_Describe
        DefaultTableModel modelInfo;

        String[] filas;
        String[] columnas;

        Statement st;
        ResultSet rs;
        try {

            st = SingletonDataBase.getSingleton().connect().createStatement();
            rs = st.executeQuery(sql);

            int Ncolumns = rs.getMetaData().getColumnCount();

            filas = new String[Ncolumns];
            columnas = new String[Ncolumns];

            for (int i = 0; i < Ncolumns; i++) {
                columnas[i] = rs.getMetaData().getColumnName(i + 1);
            }

            modelInfo = new DefaultTableModel(null, columnas);

            while (rs.next()) {
                for (int i = 1; i <= Ncolumns; i++) {
                    filas[i - 1] = rs.getString(i);
                }
                modelInfo.addRow(filas);
            }
            if (modelInfo.getRowCount() > 0) {
                tableInfo.setModel(modelInfo);
                errores.add("Ejecutado correctamente\n");
            } else {
                errores.add("Tabla no existe o no tiene información\n");
            }

        } catch (SQLException e) {
            errores.add(e.getMessage() + "\n");
        }
    }

    public void EjecutarSentencia(String sql, String item) {
        PreparedStatement pst;
        try {

            pst = SingletonDataBase.getSingleton().connect().prepareStatement(sql);
            pst.executeUpdate();
            if (item.equals("Tablas")) {
                errores.add("Ejecutado correctamente.\n");
            } else {
                if (pst.getWarnings() != null) {
                    errores.add(item + " creado con errores de Sintaxis)\n");
                } else {
                    errores.add(item + " creado correctamente.\n");
                }
            }

        } catch (SQLException e) {
            errores.add(e.getMessage() + "\n");
        }
    }
    public void EjecutarSentencia(String sql) {
        PreparedStatement pst;
        try {

            pst = SingletonDataBase.getSingleton().connect().prepareStatement(sql);
            pst.executeUpdate();
            
        } catch (SQLException e) {
            errores.add(e.getMessage() + "\n");
        }
    }

    public ArrayList getErrores() {
        return errores;
    }
}
