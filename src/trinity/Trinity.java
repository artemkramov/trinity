/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trinity;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author admin
 */
public class Trinity {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        TrinityTree tree = new TrinityTree();
        ArrayList<String> documents = readFiles();
        tree.setDocuments(documents);
        tree.buildTree(3, 115);
        //tree.traverse();
        String template = tree.learnTemplate();
        System.out.println("Template: ");
        System.out.println(template);
        // TODO code application logic here
    }
    
    public static ArrayList<String> readFiles() {
        ArrayList<String> list = new ArrayList<>();
        list.add(readFile("E:\\DATA(G)\\workspace\\Trinity\\example\\1.txt"));
        list.add(readFile("E:\\DATA(G)\\workspace\\Trinity\\example\\2.txt"));
        list.add(readFile("E:\\DATA(G)\\workspace\\Trinity\\example\\3.txt"));
        return list;
    }
    
    public static String readFile(String filePath) {
        StringBuilder contentBuilder = new StringBuilder();
        try {
            BufferedReader in = new BufferedReader(new FileReader(filePath));
            String str;
            while ((str = in.readLine()) != null) {
                contentBuilder.append(str);
            }
            in.close();
        } catch (IOException e) {
        }
        String content = contentBuilder.toString();
        return content;
    }
    
}
