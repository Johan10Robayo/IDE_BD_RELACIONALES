/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.software.Models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author drago
 */
public class Consultas {

    public void InfoTablas(JTable tableDescribe, JTable tableInfo, String sqlDescribe, String sqlDatos) {
        //Table_Describe
        DefaultTableModel modelDescribe;
        DefaultTableModel modelInfo;

        String[] columnsDescribe = {"Nombre", "Tipo"};
        ArrayList<String> columnsInfo = new ArrayList<>();

        modelDescribe = new DefaultTableModel(null, columnsDescribe);
        String[] rowsDescribe = new String[2];
        Statement st;
        ResultSet rs;
        try {

            st = SingletonDataBase.getSingleton().connect().createStatement();
            rs = st.executeQuery(sqlDescribe);
            while (rs.next()) {
                for (int i = 0; i < 2; i++) {
                    rowsDescribe[i] = rs.getString(i + 1);
                    if (i == 0) {
                        columnsInfo.add(rowsDescribe[0]);
                    }
                }
                modelDescribe.addRow(rowsDescribe);
            }
            tableDescribe.setModel(modelDescribe);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage() + sqlDescribe);
        }

        //Table_Info
        String[] columnsData = new String[columnsInfo.size()];
        for (int i = 0; i < columnsInfo.size(); i++) {
            columnsData[i] = columnsInfo.get(i);
        }

        modelInfo = new DefaultTableModel(null, columnsData);

        String[] rowsInfo = new String[columnsInfo.size()];

        try {

            st = SingletonDataBase.getSingleton().connect().createStatement();
            rs = st.executeQuery(sqlDatos);

            while (rs.next()) {
                for (int i = 0; i < columnsInfo.size(); i++) {
                    rowsInfo[i] = rs.getString(i + 1);
                }
                modelInfo.addRow(rowsInfo);
            }
            tableInfo.setModel(modelInfo);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage() + sqlDatos);
        }
    }

    public ArrayList InfoMetodosSQLServer(JTable tableInfo, String sql) {

        DefaultTableModel modelInfo;
        ArrayList<String> Codigo = new ArrayList<>();

        String[] columnsDescribe = {"Codigo"};

        modelInfo = new DefaultTableModel(null, columnsDescribe);
        String[] rowsDescribe = new String[4];

        Statement st;
        ResultSet rs;
        try {

            st = SingletonDataBase.getSingleton().connect().createStatement();
            rs = st.executeQuery(sql);
            while (rs.next()) {
                for (int i = 0; i < 1; i++) {
                    rowsDescribe[i] = rs.getString(i + 1);

                    Codigo.add(rowsDescribe[i]);

                }
                modelInfo.addRow(rowsDescribe);
            }
            //con esto se mandaba la info a la tabla de abajo 
            //tableInfo.setModel(modelInfo);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        return Codigo;
    }

    public ArrayList InfoMetodos(JTable tableInfo, String sql) {

        DefaultTableModel modelInfo;
        ArrayList<String> Codigo = new ArrayList<>();

        String[] columnsDescribe = {"Nombre", "Tipo", "Linea", "Codigo"};

        modelInfo = new DefaultTableModel(null, columnsDescribe);
        String[] rowsDescribe = new String[4];

        Statement st;
        ResultSet rs;
        try {

            st = SingletonDataBase.getSingleton().connect().createStatement();
            rs = st.executeQuery(sql);
            while (rs.next()) {
                for (int i = 0; i < 4; i++) {
                    rowsDescribe[i] = rs.getString(i + 1);
                    if (i == 3) {
                        Codigo.add(rowsDescribe[i]);
                    }
                }
                modelInfo.addRow(rowsDescribe);
            }
            tableInfo.setModel(modelInfo);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        return Codigo;
    }

    public static ArrayList<String> getInfo(String data) {
 
        Statement st;
        ResultSet rt;
        ArrayList<String> info = new ArrayList<>();

        try {

            String sql_procedures = data;
            st = SingletonDataBase.getSingleton().connect().createStatement();
            rt = st.executeQuery(sql_procedures);

            while (rt.next()) {
                info.add(rt.getString(1));
            }
        } catch (SQLException e) {
        }
        return info;
    }

    public String LlamarDDL(String sql, int num) {
        //Num es el numero de la columna donde se encuentra la informacion, se agrega porque en mysql
        //no se podía limitar las colummnas de resultado
        Statement st;
        ResultSet rt;

        String answer = "";
        try {

            st = SingletonDataBase.getSingleton().connect().createStatement();
            rt = st.executeQuery(sql);
            if (rt.next()) {
                answer = rt.getString(num);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return answer;
    }

    public ArrayList InfoMetodosMysql(JTable tableInfo, String sql) {

        DefaultTableModel modelInfo;
        ArrayList<String> Codigo = new ArrayList<>();

        String[] columnsDescribe = {"Nombre", "SQL Mode", "Codigo"};

        modelInfo = new DefaultTableModel(null, columnsDescribe);
        String[] rowsDescribe = new String[3];

        Statement st;
        ResultSet rs;
        try {

            st = SingletonDataBase.getSingleton().connect().createStatement();
            rs = st.executeQuery(sql);
            while (rs.next()) {
                for (int i = 0; i < 3; i++) {
                    rowsDescribe[i] = rs.getString(i + 1);
                    if (i == 2) {
                        Codigo.add(rowsDescribe[i]);
                    }
                }
                modelInfo.addRow(rowsDescribe);
            }
            tableInfo.setModel(modelInfo);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        return Codigo;
    }

    public ArrayList InfoVistasMysql(String sql) {

        ArrayList<String> Codigo = new ArrayList<>();

        String[] rowsDescribe = new String[2];
        Statement st;
        ResultSet rs;
        try {
            st = SingletonDataBase.getSingleton().connect().createStatement();
            rs = st.executeQuery(sql);
            while (rs.next()) {
                for (int i = 0; i < 2; i++) {
                    rowsDescribe[i] = rs.getString(i + 1);
                    if (i == 1) {
                        Codigo.add(rowsDescribe[i]);
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        return Codigo;
    }
}
