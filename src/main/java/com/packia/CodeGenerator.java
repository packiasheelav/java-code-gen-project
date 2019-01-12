package com.packia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CodeGenerator {

    public static final int MAX_NUMBER = 999;
    public static final int MIN_NUMBER = 0;

    private static final String URL ="jdbc:mysql://localhost:3306/mycode";
    private static final String USER = "root";
    private static final String PASSWORD = "*****";
    private static final String DRIVER ="com.mysql.jdbc.Driver";
    private static final String TABLE_NAME = "t_code";


    /*
    * Fetch code from DB
    *
    * */
    public List fetchCodeFromDB() throws ClassNotFoundException, SQLException {
        List fromDb = new ArrayList();
        Class.forName(DRIVER);
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            String SQL = "SELECT DISTINCT code FROM "+TABLE_NAME+" where code >="+MIN_NUMBER+" AND code <= "+MAX_NUMBER;
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(SQL);
            while (resultSet.next()) {
                fromDb.add(resultSet.getInt(1));
            }
        } catch (SQLException ex) {
            throw ex;
        } finally {
            if (resultSet != null)
                resultSet.close();
            if (statement != null)
                statement.close();
            connection.close();
        }

        return fromDb;
    }

    /*insert code to DB*/
    public void insertDummyEntry(Integer code)throws ClassNotFoundException,SQLException {

        Class.forName(DRIVER);
        Statement statement = null;
        ResultSet resultSet = null;
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(URL, USER,PASSWORD);
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO "+TABLE_NAME+"( code ) VALUES("+code+")");
            connection.commit();
            System.out.println("unique Code: "+ code +" inserted to DB" );
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        } finally {
            if (resultSet != null)
                resultSet.close();
            if (statement != null)
                statement.close();
            connection.close();
        }

    }


    List getAllNumbers() {
        List numbers = new ArrayList();
        for (int i = MIN_NUMBER; i < MAX_NUMBER; i++) {
            numbers.add(i + 1);
        }
        return numbers;
    }

    Integer shuffleAndGetUniqueNumber(List fromDb, List numbers) {
        Collections.shuffle(numbers);
        int j = MIN_NUMBER;
        do {
            if (!fromDb.contains(numbers.get(j))) {
                return (Integer) numbers.get(j);
            } else {
                System.out.print("continue \n");
            }
            j++;
        } while (j < MAX_NUMBER);
        return -1;
    }

    public static List findDuplicates(List input) {
        List dup = new ArrayList();
        for (int i = 0; i < input.size(); i++) {
            for (int j = i + 1; j < input.size(); j++) {
                if (input.get(i).equals(input.get(j))) {
                    dup.add(input.get(i));
                }
            }
        }
        return dup;
    }

    public static void main(String[] args) throws Exception {

        CodeGenerator codeGenerator = new CodeGenerator();
        /*Fetch unique(old DB might have duplicate?) entry between (0-999) from db*/
        List fromDb = codeGenerator.fetchCodeFromDB();

        /*safety check*/
        if(!findDuplicates(fromDb).isEmpty()) throw new Exception("Db has Duplicate entry. Any one inserted the duplicate entry manually?");

        /*Code should be between 0-999*/
        if(fromDb.size()<MAX_NUMBER) {
            List numbers = codeGenerator.getAllNumbers();
            Integer result = codeGenerator.shuffleAndGetUniqueNumber(fromDb, numbers);
            System.out.println("Code Entry from DB: " + fromDb);
            System.out.println("Generated new Code : " + result);
            //codeGenerator.insertDummyEntry(result);  //in case if you want to insert DB
        }else{
            System.out.println(" Already Database has all entries between 0-999. No unique number to show ");
        }
    }
}