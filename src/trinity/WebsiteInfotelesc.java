/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trinity;

import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 *
 * @author admin
 */
public class WebsiteInfotelesc extends Website {
    
    public WebsiteInfotelesc() {
        this.url = "infotelesc.kpi.ua";
        this.tableName = "infotelesc";
    }
    
    /**
     * Write all matches to the database
     * @param matches
     * @param dataParser 
     */
    @Override
    public void writeMatchesToDB(ArrayList<String> matches, ArrayList<String> dataParser) {
//        ArrayList<String> data = new ArrayList<>();
//        DBClient client = new DBClient();
//        if (matches.size() > 8 && dataParser.size() > 0) {
//            // Retrieve necessary attributes like title, annotation, authors, keywords
//            data.add(matches.get(5));
//            data.add(matches.get(6));
//            data.add(matches.get(7));
//            client.insertIASA(data, dataParser);
//        }
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
        Element content = document.selectFirst("div").child(1).child(1).child(2);
        data.add(content.selectFirst("h3").text());
        data.add(content.selectFirst("em").text());
        data.add(content.selectFirst("h4").nextElementSibling().nextElementSibling().text());
        try {
            data.add(content.select("h4").get(1).nextElementSibling().nextElementSibling().text());
        }
        catch (Exception ex) {}
        return data;
    }
    
}
