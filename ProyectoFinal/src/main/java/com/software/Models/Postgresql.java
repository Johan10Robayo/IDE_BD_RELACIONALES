/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.software.Models;

import com.software.DTO.DTOUser;
import static com.software.Models.Consultas.getInfo;
import java.util.ArrayList;

/**
 *
 * @author drago
 */
public class Postgresql {

    DTOUser dtouser;

    //Strings listados
    static String listarEsquemas = "select schema_name from information_schema.schemata";
    static String listarFunciones = "select name from user_source where type='FUNCTION' group by name";
    static String listarProcedimientos = "select name from user_source where type='PROCEDURE' group by name";
    static String listarTriggers = "select * from USER_TRIGGERS";
    static String listarViews = "select name from user_source where type='PROCEDURE' group by name";
    static String listarPackages = "select * from user_dependencies where type = 'PACKAGE BODY' and referenced_type = 'TABLE'";
    static String listarUsuarios = "SELECT usename FROM pg_user";

    //String DB
    String Driver = "org.postgresql.Driver";

    public String[] datosConexion() {
        String[] datos = new String[4];
        datos[0] = Driver;
        datos[1] = "jdbc:" + dtouser.getDB().toLowerCase() + "ql://" + dtouser.getIpServidor() + ":" + dtouser.getPuerto() + "/" + dtouser.getInstancia();
        datos[2] = dtouser.getUSER();
        datos[3] = dtouser.getPASSWORD();
        return datos;
    }

    public String[] getSentenciasTablas(String tablaSelected) {
        String[] sqls = new String[2];
        sqls[0] = "SELECT COLUMN_NAME, DATA_TYPE FROM information_schema.columns where table_name='" + tablaSelected + "'";
        sqls[1] = "SELECT * FROM " + tablaSelected + " ORDER BY 1";
        return sqls;
    }

    public String[] getSentenciasTablas(String esquema, String tablaSelected) {
        String[] sqls = new String[2];
        sqls[0] = "SELECT COLUMN_NAME, DATA_TYPE FROM information_schema.columns where table_name='" + tablaSelected + "'";
        sqls[1] = "SELECT * FROM " + '"' + esquema + '"' + '.' + '"' + tablaSelected + '"' + " ORDER BY 1";
        return sqls;
    }

    public String getSentenciaMetodo(String tablaSelected) {
        return "Select * from user_source where name='" + tablaSelected.toUpperCase() + "'";
    }

    public String getDDLFuntProc(String FuncSelected) {
        return "SELECT pg_get_functiondef(( SELECT oid FROM pg_proc where proname= '" + FuncSelected + "'))";
    }

    public String getDDLTrigger(String TrigSelected) {
        return "select pg_get_triggerdef(oid) "
                + "from pg_trigger "
                + "where tgname = '" + TrigSelected + "'";
    }

    public String getDDLView(String ViewSelected) {
        return "select definition from pg_views "
                + "where viewname='" + ViewSelected + "';";
    }

    public String getDDLTable(String TableSelected) {
        return "select generar_ddl_tabla('" + TableSelected + "')";
    }

    public String getDDLFunction() {
        return this.ddl_function;
    }

    public static ArrayList<String> getUsers() {
        return getInfo(listarUsuarios);
    }

    public static ArrayList<String> getTables(String esquema) {
        String listarTablas = "SELECT table_name FROM information_schema.tables WHERE table_schema='" + esquema + "' AND table_type='BASE TABLE'";
        return getInfo(listarTablas);
    }

    public static ArrayList<String> getSchemas() {
        return getInfo(listarEsquemas);
    }

    public static ArrayList<String> getFunctions(String esquema) {
        String sqlFunciones = "SELECT ROUTINE_NAME FROM INFORMATION_SCHEMA.ROUTINES "
                + "WHERE ROUTINE_TYPE = 'FUNCTION' and routines.specific_schema='" + esquema + "' "
                + "ORDER BY ROUTINE_NAME";
        return getInfo(sqlFunciones);
    }

    public static ArrayList<String> getProcedures(String esquema) {
        String sqlProcedimientos = "SELECT ROUTINE_NAME FROM INFORMATION_SCHEMA.ROUTINES "
                + "WHERE ROUTINE_TYPE = 'PROCEDURE' and routines.specific_schema='" + esquema + "' "
                + "ORDER BY ROUTINE_NAME";
        return getInfo(sqlProcedimientos);
    }

    public static ArrayList<String> getTriggers(String esquema) {

        String sqlTriggers = "SELECT trigger_name "
                + "FROM information_schema.triggers "
                + "WHERE trigger_schema='" + esquema + "'";
        return getInfo(sqlTriggers);
    }

    public static ArrayList<String> getViews(String esquema) {

        String sqlViews = "select table_name from INFORMATION_SCHEMA.views "
                + "where table_schema='" + esquema + "'";
        return getInfo(sqlViews);
    }

    public static ArrayList<String> getPackages() {
        return getInfo(listarPackages);
    }

    public void setDTO(DTOUser dtouser) {
        this.dtouser = dtouser;
    }

    static String ddl_function = "CREATE OR REPLACE FUNCTION public.generar_ddl_tabla(p_table_name character varying)\n"
            + " RETURNS SETOF text\n"
            + " LANGUAGE plpgsql\n"
            + "AS $function$\n"
            + "DECLARE\n"
            + "    v_tabla_ddl   text;\n"
            + "    column_record record;\n"
            + "    tabla_rec record;\n"
            + "    constraint_rec record;\n"
            + "    firstrec boolean;\n"
            + "BEGIN\n"
            + "    FOR tabla_rec IN\n"
            + "        SELECT c.relname FROM pg_catalog.pg_class c\n"
            + "            LEFT JOIN pg_catalog.pg_namespace n ON n.oid = c.relnamespace\n"
            + "                WHERE relkind = 'r'\n"
            + "                AND relname~ ('^('||p_table_name||')$')\n"
            + "                AND n.nspname <> 'pg_catalog'\n"
            + "                AND n.nspname <> 'information_schema'\n"
            + "                AND n.nspname !~ '^pg_toast'\n"
            + "                \n"
            + "          ORDER BY c.relname\n"
            + "    LOOP\n"
            + "\n"
            + "        FOR column_record IN \n"
            + "            SELECT \n"
            + "                b.nspname as schema_name,\n"
            + "                b.relname as table_name,\n"
            + "                a.attname as column_name,\n"
            + "                pg_catalog.format_type(a.atttypid, a.atttypmod) as column_type,\n"
            + "                CASE WHEN \n"
            + "                    (SELECT substring(pg_catalog.pg_get_expr(d.adbin, d.adrelid) for 128)\n"
            + "                     FROM pg_catalog.pg_attrdef d\n"
            + "                     WHERE d.adrelid = a.attrelid AND d.adnum = a.attnum AND a.atthasdef) IS NOT NULL THEN\n"
            + "                    'DEFAULT '|| (SELECT substring(pg_catalog.pg_get_expr(d.adbin, d.adrelid) for 128)\n"
            + "                                  FROM pg_catalog.pg_attrdef d\n"
            + "                                  WHERE d.adrelid = a.attrelid AND d.adnum = a.attnum AND a.atthasdef)\n"
            + "                ELSE\n"
            + "                    ''\n"
            + "                END as column_default_value,\n"
            + "                CASE WHEN a.attnotnull = true THEN \n"
            + "                    'NOT NULL'\n"
            + "                ELSE\n"
            + "                    'NULL'\n"
            + "                END as column_not_null,\n"
            + "                a.attnum as attnum,\n"
            + "                e.max_attnum as max_attnum\n"
            + "            FROM \n"
            + "                pg_catalog.pg_attribute a\n"
            + "                INNER JOIN \n"
            + "                 (SELECT c.oid,\n"
            + "                    n.nspname,\n"
            + "                    c.relname\n"
            + "                  FROM pg_catalog.pg_class c\n"
            + "                       LEFT JOIN pg_catalog.pg_namespace n ON n.oid = c.relnamespace\n"
            + "                  WHERE c.relname = tabla_rec.relname\n"
            + "                    \n"
            + "                  ORDER BY 2, 3) b\n"
            + "                ON a.attrelid = b.oid\n"
            + "                INNER JOIN \n"
            + "                 (SELECT \n"
            + "                      a.attrelid,\n"
            + "                      max(a.attnum) as max_attnum\n"
            + "                  FROM pg_catalog.pg_attribute a\n"
            + "                  WHERE a.attnum > 0 \n"
            + "                    AND NOT a.attisdropped\n"
            + "                  GROUP BY a.attrelid) e\n"
            + "                ON a.attrelid=e.attrelid\n"
            + "            WHERE a.attnum > 0 \n"
            + "              AND NOT a.attisdropped\n"
            + "            ORDER BY a.attnum\n"
            + "        LOOP\n"
            + "            IF column_record.attnum = 1 THEN\n"
            + "                v_tabla_ddl:='CREATE TABLE '||column_record.schema_name||'.'||column_record.table_name||' (';\n"
            + "            ELSE\n"
            + "                v_tabla_ddl:=v_tabla_ddl||',';\n"
            + "            END IF;\n"
            + "\n"
            + "            IF column_record.attnum <= column_record.max_attnum THEN\n"
            + "                v_tabla_ddl:=v_tabla_ddl||chr(10)||\n"
            + "                         '    '||column_record.column_name||' '||column_record.column_type||' '||column_record.column_default_value||' '||column_record.column_not_null;\n"
            + "            END IF;\n"
            + "        END LOOP;\n"
            + "\n"
            + "        firstrec := TRUE;\n"
            + "        FOR constraint_rec IN\n"
            + "            SELECT conname, pg_get_constraintdef(c.oid) as constrainddef \n"
            + "                FROM pg_constraint c \n"
            + "                    WHERE conrelid=(\n"
            + "                        SELECT attrelid FROM pg_attribute\n"
            + "                        WHERE attrelid = (\n"
            + "                            SELECT oid FROM pg_class WHERE relname = tabla_rec.relname\n"
            + "                        ) AND attname='tableoid'\n"
            + "                    )\n"
            + "        LOOP\n"
            + "            v_tabla_ddl:=v_tabla_ddl||','||chr(10);\n"
            + "            v_tabla_ddl:=v_tabla_ddl||'CONSTRAINT '||constraint_rec.conname;\n"
            + "            v_tabla_ddl:=v_tabla_ddl||chr(10)||'    '||constraint_rec.constrainddef;\n"
            + "            firstrec := FALSE;\n"
            + "        END LOOP;\n"
            + "        v_tabla_ddl:=v_tabla_ddl||');';\n"
            + "        RETURN NEXT v_tabla_ddl;\n"
            + "    END LOOP;\n"
            + "END;\n"
            + "$function$";

}
