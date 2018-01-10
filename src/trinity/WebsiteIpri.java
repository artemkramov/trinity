/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trinity;

import java.sql.JDBCType;
import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author admin
 */
public class WebsiteIpri extends Website {
    
    public WebsiteIpri() {
        this.url = "ipri.kiev.ua";
        this.tableName = "ipri";
    }
    
    /**
     * Get data from the html page of the given site
     * @param html
     * @return 
     */
    @Override
    public ArrayList<String> fetchData(String html) {
        ArrayList<String> data = new ArrayList<>();
        Document document = Jsoup.parse(html);
        Elements tables = document.select("table");
        if (tables.size() > 1) {
            Element table = tables.get(1);
            Elements tableRows = table.child(0).children();
            if (tableRows.size() > 7) {
                // Add title
                data.add(tableRows.get(4).child(1).child(0).text());
                // Add authors
                data.add(tableRows.get(5).child(1).child(0).text());
                // Add annotation
                data.add(tableRows.get(6).child(1).text());
                // Add keywords
                data.add(tableRows.get(7).child(1).text());
            }
        }
        return data;
    }
    
    /**
     * Write all matches to the database
     * @param matches
     * @param dataParser 
     */
    @Override
    public void writeMatchesToDB(ArrayList<String> matches, ArrayList<String> dataParser) {
        ArrayList<String> data = new ArrayList<>();
        DBClient client = new DBClient();
        if (matches.size() > 10 && dataParser.size() > 0) {
            // Retrieve necessary attributes like title, annotation, authors, keywords
            data.add(matches.get(4));
            data.add(matches.get(5));
            data.add(matches.get(6));
            data.add(matches.get(7));
            client.insertIPRI(data, dataParser);
        }
    }
    
}
