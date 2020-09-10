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
import java.util.List;
import java.util.ArrayList;

public class SelectData {


    public static List<String> selectdatabase(String sql, String databasename) {

        Connection connection = null;
        Statement statement = null;
        List<String> selectdata = new ArrayList<String>(); //?��????����
        try {
            String url = "jdbc:postgresql://202.114.114.47:5432/" + databasename;
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(url, "postgres", "webgis327");
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            ResultSetMetaData metaData = resultSet.getMetaData();

           // int columnCount = metaData.getColumnCount();

            //int subposition=6;
            while (resultSet.next()) {
                String lat=resultSet.getString(1);
                String lng=resultSet.getString(2);
                selectdata.add(lat+"-"+lng);
                //savedate+=lat+",";
                //savedate+=lng+";";
                //array.put(jsonObj);
            }

            System.out.println("selectdata result" + selectdata.toString().replace(" ",""));
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
