/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.software.Models;

import com.software.DTO.DTOUser;
import java.util.ArrayList;

public class MySql extends Consultas {

    static DTOUser dtouser;
    //String DB
    String Driver = "com.mysql.cj.jdbc.Driver";

    //Strings listados
    static String listarTablas = "show full tables where Table_type = 'BASE TABLE'";
    static String listarFunciones = "SELECT routine_name FROM INFORMATION_SCHEMA.ROUTINES where routine_type = 'FUNCTION' and routine_schema = '";
    static String listarProcedimientos = "SELECT routine_name FROM INFORMATION_SCHEMA.ROUTINES where routine_type = 'PROCEDURE' and routine_schema = '";
    static String listarTriggers = "show triggers";
    static String listarViews = "SHOW FULL TABLES where Table_type='VIEW'";
    static String listarUsers = "SELECT user FROM mysql.user";
    public String[] datosConexion() {
        String[] datos = new String[4];
        datos[0] = Driver;
        datos[1] = "jdbc:" + dtouser.getDB().toLowerCase() + "://" + dtouser.getIpServidor() + ":" + dtouser.getPuerto() + "/" + dtouser.getInstancia().toUpperCase() + "?serverTimezone=UTC";
        datos[2] = dtouser.getUSER();
        datos[3] = dtouser.getPASSWORD();
        return datos;
    }

    public void setDTO(DTOUser dtouser) {
        this.dtouser = dtouser;
    }

    public static ArrayList<String> getTables() {
        return getInfo(listarTablas);
    }

    public static ArrayList<String> getFunctions() {
        String cadena = listarFunciones + dtouser.getInstancia().toUpperCase() + "'";
        return getInfo(cadena);
    }

    public static ArrayList<String> getViews() {
        return getInfo(listarViews);
    }

    public static ArrayList<String> getTriggers() {
        return getInfo(listarTriggers);
    }

    public static ArrayList<String> getProcedures() {
        String cadena = listarProcedimientos + dtouser.getInstancia().toUpperCase() + "'";
        return getInfo(cadena);
    }

    public static ArrayList<String> getUsers() {
        return getInfo(listarUsers);
    }

    public String[] getSentenciasTablas(String tablaSelected) {
        String[] sqls = new String[2];
        sqls[0] = "DESCRIBE " + tablaSelected.toUpperCase();
        sqls[1] = "SELECT * FROM " + tablaSelected + " ORDER BY 1";
        return sqls;
    }

    //Sentencia DDL Tablas
    public String getDDL(String tablaSelected) {
        return "SHOW CREATE TABLE " + tablaSelected;
    }

    //Sentencia DDL Funciones, vistas, procedimientos y triggers
    public String getSentenciaMetodo(String tablaSelected, String tipo) {
        return "SHOW CREATE " + tipo + " " + tablaSelected.toUpperCase();
    }
}
