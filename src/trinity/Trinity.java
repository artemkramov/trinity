/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trinity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author admin
 */
public class Trinity {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String siteName = "essuir.sumdu.edu.ua";
        String folderPath = String.format("./example/%s/", siteName);
         String outputFolderPath = folderPath + "/output/";
        ArrayList<String> documents = readFiles(folderPath + "/input/");
        TrinityTree tree = new TrinityTree();
        tree.setDocuments(documents);
        tree.buildTree(15, 600);
//        tree.traverseForPrint();
        String template = tree.learnTemplate();
//        
        
        writeFile(outputFolderPath + "pattern.txt", template);
//        String template = readFile(outputFolderPath + "pattern.txt");
        Pattern TAG_REGEX = Pattern.compile(template);
        documents = new ArrayList<>();
        documents.add(readFile(folderPath + "/input/test1.txt"));
        documents.add(readFile(folderPath + "/input/test2.txt"));
        documents.add(readFile(folderPath + "/input/test3.txt"));
        documents.add(readFile(folderPath + "/input/test4.txt"));
        documents.add(readFile(folderPath + "/input/test5.txt"));
        documents.add(readFile(folderPath + "/input/test6.txt"));
        documents.add(readFile(folderPath + "/input/test7.txt"));
        documents.add(readFile(folderPath + "/input/test8.txt"));
        documents.add(readFile(folderPath + "/input/test9.txt"));
        documents.add(readFile(folderPath + "/input/test10.txt"));
        documents.add(readFile(folderPath + "/input/test11.txt"));
        documents.add(readFile(folderPath + "/input/test12.txt"));
        for (int i = 0; i < documents.size(); i++) {
            ArrayList<String> tags = new ArrayList<>();
            Matcher matcher = TAG_REGEX.matcher(documents.get(i));
            while (matcher.find()) {
                int a = matcher.groupCount();
                for (int j = 1; j <= matcher.groupCount(); j++) {
                    tags.add(matcher.group(j));
                }
            }
            System.gc();
            writeMatches(tags, String.format("%s/matches-%d.txt", outputFolderPath, i));
        }
    }

    public static ArrayList<String> readFiles(String folderPath) {
        ArrayList<String> list = new ArrayList<>();
        list.add(readFile(folderPath + "input1.txt"));
        list.add(readFile(folderPath + "input2.txt"));
//        list.add(readFile(folderPath + "input3.txt"));
        list.add(readFile(folderPath + "input4.txt"));
        list.add(readFile(folderPath + "input5.txt"));
        list.add(readFile(folderPath + "input6.txt"));
        list.add(readFile(folderPath + "input7.txt"));
        list.add(readFile(folderPath + "input8.txt"));
        list.add(readFile(folderPath + "input9.txt"));
        list.add(readFile(folderPath + "input10.txt"));
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
            String t = e.getMessage();
            int a = 3;
        }
        String content = contentBuilder.toString().replaceAll(">\\s+<", "><");
        return content;
    }

    public static void writeFile(String filePath, String pattern) {
        try {
            FileOutputStream fileStream = new FileOutputStream(new File(filePath));
            OutputStreamWriter writer = new OutputStreamWriter(fileStream, StandardCharsets.UTF_8);
            writer.write("\uFEFF");
            writer.write(pattern);
            writer.flush();
        } catch (Exception ex) {
        }
    }

    public static void writeMatches(ArrayList<String> matches, String folderPath) {
        String listString = "";
        for (String match : matches) {
            listString += match + "\r\n";
        }
        writeFile(folderPath, listString);
    }

}
