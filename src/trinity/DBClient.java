/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trinity;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;

/**
 *
 * @author admin
 */
public class DBClient {

    private String driver = "org.gjt.mm.mysql.Driver";

    private String url = "jdbc:mysql://localhost/asp?useUnicode=true&characterEncoding=utf-8";

    private String username = "root";

    private String password = "";

    private Connection connection;

    public DBClient() {
        try {
            Class.forName(this.driver);
        } catch (ClassNotFoundException ex) {
            System.err.println(ex.getMessage());
        }
    }

    public void insertIPRI(ArrayList<String> data, ArrayList<String> dataParser) {
        try {
            this.connection = DriverManager.getConnection(this.url, this.username, this.password);

            // the mysql insert statement
            String query = " insert into ipri (TitlePractical, AuthorsPractical, AnnotationPractical, KeywordsPractical, TitleTheoretical, AuthorsTheoretical, AnnotationTheoretical, KeywordsTheoretical)"
                    + " values (?, ?, ?, ?, ?, ?, ?, ?)";

            // create the mysql insert preparedstatement
            PreparedStatement preparedStmt = this.connection.prepareStatement(query);
            for (int i = 0; i < data.size(); i++) {
                preparedStmt.setString(i + 1, data.get(i));
            }
            
            for (int i = 0; i < dataParser.size(); i++) {
                preparedStmt.setString(i + 1 + data.size(), dataParser.get(i));
            }

            // execute the preparedstatement
            preparedStmt.execute();

            this.connection.close();
        } catch (SQLException e) {
            System.err.println("Got an exception!");
            System.err.println(e.getMessage());
        }
    }

}
