/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trinity;

import java.util.ArrayList;

/**
 *
 * @author admin
 */
public class Website {
    
    protected String url;
    
    protected String tableName;
    
    public String getUrl() {
        return this.url;
    }
    
    /**
     * Get data from the html page of the given site
     * @param html
     * @return 
     */
    public ArrayList<String> fetchData(String html) {
        ArrayList<String> list = new ArrayList<>();
        return list;
    }
    
    /**
     * Write all matches to the database
     * @param matches
     * @param dataParser 
     */
    public void writeMatchesToDB(ArrayList<String> matches, ArrayList<String> dataParser) {
    }
    
    /**
     * Truncate table
     */
    public void cleanTable() {
        DBClient client = new DBClient();
        client.truncateTable(this.tableName);
    }
    
}
