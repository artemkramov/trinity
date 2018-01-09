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
import org.jsoup.select.Elements;

/**
 *
 * @author admin
 */
public class Parser {

    public ArrayList<String> getIPRIData(String html) {
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
        else {
            int a = 4;
        }
        return data;
    }

    public ArrayList<String> fetchData(String siteName, String html) {
        ArrayList<String> list;
        switch (siteName) {
            case "ipri.kiev.ua":
                list = this.getIPRIData(html);
                break;
            default:
                list = this.getIPRIData(html);
                break;
        }
        return list;
    }

}
