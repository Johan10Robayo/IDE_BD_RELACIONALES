/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.software.Models;

import com.software.DTO.DTOUser;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Oracle extends Consultas {

    DTOUser dtouser;

    //Strings listados
    static ArrayList<String> textos = new ArrayList<>();
    static ArrayList<String> errores = new ArrayList<>();
    static ArrayList<String> codigos = new ArrayList<>();
    static String listarTablas = "select * from USER_TABLES order by table_name";
    static String listarFunciones = "select name from user_source where type='FUNCTION' group by name order by name";
    static String listarProcedimientos = "select name from user_source where type='PROCEDURE' group by name order by name";
    static String listarTriggers = "select * from USER_TRIGGERS order by trigger_name";
    static String listarViews = "select view_name from user_views order by view_name";
    static String listarPackages = "select * from user_dependencies where type = 'PACKAGE BODY' and referenced_type = 'TABLE' order by name";
    static String listarUsers = "SELECT USERNAME FROM ALL_USERS";
    //String DB
    String Driver = "oracle.jdbc.driver.OracleDriver";

    public String[] datosConexion() {
        String[] datos = new String[4];
        datos[0] = Driver;
        datos[1] = "jdbc:" + dtouser.getDB().toLowerCase() + ":thin:@" + dtouser.getIpServidor() + ":" + dtouser.getPuerto() + ":" + dtouser.getInstancia().toUpperCase();
        datos[2] = dtouser.getUSER();
        datos[3] = dtouser.getPASSWORD();
        return datos;
    }

    public String[] getSentenciasTablas(String tablaSelected) {
        String[] sqls = new String[2];
        sqls[0] = "SELECT COLUMN_NAME, DATA_TYPE FROM user_tab_columns where table_name='" + tablaSelected.toUpperCase() + "'";
        sqls[1] = "SELECT * FROM " + tablaSelected + " ORDER BY 1";
        return sqls;
    }

    public String getDescribeTable(String tabla) {
        return "SELECT COLUMN_NAME, DATA_TYPE FROM user_tab_columns where table_name='" + tabla.toUpperCase() + "'";
    }

    public String getSentenciaMetodo(String tablaSelected) {
        return "Select * from user_source where name='" + tablaSelected.toUpperCase() + "'";
    }

    public String getDDL(String tablaSelected) {
        return "select dbms_metadata.get_ddl('TABLE','" + tablaSelected + "') from dual";
    }

    public static ArrayList<String> getTables() {
        return getInfo(listarTablas);
    }

    public static ArrayList<String> getFunctions() {
        return getInfo(listarFunciones);
    }

    public static ArrayList<String> getProcedures() {
        return getInfo(listarProcedimientos);
    }

    public static ArrayList<String> getTriggers() {
        return getInfo(listarTriggers);
    }

    public static ArrayList<String> getViews() {
        return getInfo(listarViews);
    }

    public static ArrayList<String> getPackages() {
        return getInfo(listarPackages);
    }

    public static ArrayList<String> getUsers() {
        return getInfo(listarUsers);
    }

    public void setDTO(DTOUser dtouser) {
        this.dtouser = dtouser;
    }

    public static ArrayList<String> getData(String data) {
        Statement st = null;
        ResultSet result = null;
        ArrayList<String> procedimientos = new ArrayList<String>();

        try {
            SingletonDataBase c = SingletonDataBase.getSingleton();

            Connection conn = c.connect();

            String sql_procedures = data;
            st = conn.createStatement();
            result = st.executeQuery(sql_procedures);

            while (result.next()) {
                procedimientos.add(result.getString(1));
            }
        } catch (Exception e) {
        }
        return procedimientos;
    }

    public static ArrayList<String> getData2(String data) {
        Statement st = null;
        ResultSet result = null;
        ArrayList<String> procedimientos = new ArrayList<String>();

        try {
            SingletonDataBase c = SingletonDataBase.getSingleton();
            Connection conn = c.connect();

            String sql_procedures = data;
            st = conn.createStatement();
            result = st.executeQuery(sql_procedures);

            while (result.next()) {
                procedimientos.add(result.getString(2));
            }
        } catch (Exception e) {
        }
        return procedimientos;
    }

    public static void createBackup() {
        Statement st = null;
        ResultSet result = null;
        ResultSet result2 = null;
        String aux = "";
        ArrayList<String> objectName = getData("select object_name, object_type from user_objects where object_type='TABLE' and object_type not in ('PACKAGE BODY') order by created");
        ArrayList<String> objectType = getData2("select object_name, object_type from user_objects where object_type='TABLE' and object_type not in ('PACKAGE BODY') order by created");

        codigos.add("");
        errores.add("");
        try {
            SingletonDataBase c = SingletonDataBase.getSingleton();
            Connection conn = c.connect();
            st = conn.createStatement();
            String textoCompleto = "";
            codigos.add(" " + objectName);
            for (int i = 0; i < objectName.size(); i++) {//
                result = st.executeQuery("select dbms_metadata.get_ddl('" + objectType.get(i) + "','" + objectName.get(i) + "') \n"
                        + "  from dual");

                System.out.println("" + objectName.get(i));
                System.out.println("" + objectType.get(i));

                while (result.next()) {
                    textoCompleto += result.getString(1) + ";";
                    result = st.executeQuery("select fn_insertar_t('" + objectName.get(i) + "') from dual");

                    while (result.next()) {
                        if (result.getString(1) != null) {
                            textoCompleto += result.getString(1);

                        } else {
                            textoCompleto += "";
                        }
                    }
                    textoCompleto += "\ncommit;";
                }
            }

            codigos.add(textoCompleto);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
            errores.add(e.getMessage());
        }
    }

    public static void guardarArchivo(String texto) {
        Scanner lector;
        int respuesta;
        JFileChooser eleccion = new JFileChooser("C:\\Users\\drago\\Desktop\\prueba alexandra");
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "SQL FILES", "sql");
        eleccion.setFileFilter(filter);
        String output = null;

        eleccion.setDialogTitle("Guardar archivo");
        //eleccion.setFileFilter(new FileNameExtensionFilter(".sql", "archivo sql"));

        textos.add("");
        errores.add("");

        respuesta = eleccion.showSaveDialog(null);

        if (respuesta == JFileChooser.APPROVE_OPTION) {
            //String contenido = texto.getText();
            String contenido = texto;
            File guardado = eleccion.getSelectedFile();
            try {
                FileWriter escribir = new FileWriter(guardado.getPath() + ".sql");
                escribir.write(contenido);
                escribir.flush();
                escribir.close();
            } catch (Exception e) {
                //error.setText(e.getMessage());
                errores.add(e.getMessage());
            }
        }
    }

    public static String getArregloCodigos() {
        return codigos.get(codigos.size() - 1);
    }
}
