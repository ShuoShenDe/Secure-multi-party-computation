package com.localmoran.example.demo.database;


import org.json.JSONObject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SelectData {


    public static List<Double> selectdatabase(String sql, String databasename) {




        Connection connection = null;
        Statement statement = null;
        List<Double> selectdata = new ArrayList<Double>(); //?��????����

        try {
            String url = "jdbc:postgresql://202.114.114.47:5432/" + databasename;
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(url, "postgres", "webgis327");
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            ResultSetMetaData metaData = resultSet.getMetaData();

            int columnCount = metaData.getColumnCount();

          while (resultSet.next()) {
                JSONObject jsonObj = new JSONObject();
                System.out.println("resultSet" + resultSet);
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnLabel(i);
                    String value = resultSet.getString(i);
                    //jsonObj.put(columnName, value);
                    selectdata.add(Double.valueOf(value));
                }
                //array.put(jsonObj);
           }
            System.out.println("columnCount" + columnCount);
            System.out.println("select result" + selectdata);
        } catch (Exception e) {
            System.out.println("error" + e);
            throw new RuntimeException(e);
        } finally {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("error" + e);
                throw new RuntimeException(e);
            } finally {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("error" + e);
                    throw new RuntimeException(e);
                }
            }
        }
        return selectdata;
    }

}
