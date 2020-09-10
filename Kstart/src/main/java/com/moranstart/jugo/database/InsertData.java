package com.moranstart.jugo.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;

public class InsertData {


    public Boolean insertdatabase(String sql, String databasename) {

        Connection connection = null;
        Statement statement = null;

        Boolean sqlrecall=false;

        try {
            String url = "jdbc:postgresql://202.114.114.46:5432/"+databasename;
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(url, "postgres", "webgis327");
            System.out.println("存入数据库" + connection);
            statement = connection.createStatement();
            sqlrecall = statement.execute(sql);
            System.out.println(sqlrecall);

        } catch (Exception e) {
            System.out.println("错误错误1" + e);
            throw new RuntimeException(e);
        } finally {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("错误错误2" + e);
                throw new RuntimeException(e);
            } finally {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("错误错误3" + e);
                    throw new RuntimeException(e);
                }
            }
        }
        return sqlrecall;
    }
}
