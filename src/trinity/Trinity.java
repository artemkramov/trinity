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
import java.util.LinkedHashSet;
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
        String siteName = "ipri.kiev.ua";
        String folderPath = String.format("../Spider/result/%s/", siteName);
        String outputFolderPath = folderPath + "/output/";
        File inputDirectory = new File(folderPath + "/input/");
        ArrayList<String> fileNamesForPattern = getDocumentsForPattern(inputDirectory);
        ArrayList<String> documents = readFiles(fileNamesForPattern);
        TrinityTree tree = new TrinityTree();
        tree.setDocuments(documents);
        tree.buildTree(15, 600);
//        tree.traverseForPrint();
        String template = tree.learnTemplate();
        writeFile(outputFolderPath + "pattern.txt", template);
        Pattern TAG_REGEX = Pattern.compile(template);
        documents = new ArrayList<>();
        int testSize = inputDirectory.listFiles().length;
        Parser parser = new Parser();
        for (final File fileEntry : inputDirectory.listFiles()) {
            if (!fileEntry.isDirectory()) {
                documents.add(readFile(fileEntry.getAbsolutePath()));
            }
            testSize--;
            if (testSize == 0) {
                break;
            }
        }
        
        for (int i = 0; i < documents.size(); i++) {
            ArrayList<String> tags = new ArrayList<>();
            ArrayList<String> tagsParser = parser.fetchData(siteName, documents.get(i));
            Matcher matcher = TAG_REGEX.matcher(documents.get(i));
            while (matcher.find()) {
                for (int j = 1; j <= matcher.groupCount(); j++) {
                    tags.add(matcher.group(j));
                }
            }
            System.gc();
            // Remove duplicates
            tags = new ArrayList<>(new LinkedHashSet<>(tags));
//            writeMatches(tags, String.format("%s/matches-%d.txt", outputFolderPath, i));
            writeMatchesToDB(tags, tagsParser);
        }
    }

    public static ArrayList<String> readFiles(ArrayList<String> fileNames) {
        ArrayList<String> list = new ArrayList<>();
        fileNames.forEach((fileName) -> {
            list.add(readFile(fileName));
        });
        return list;
    }

    public static ArrayList<String> getDocumentsForPattern(File directory) {
        ArrayList<String> fileNames = new ArrayList<>();
        int fileCount = directory.list().length;
        int chunk = (int)(fileCount / 20);
        for (int i = 1; i < fileCount; i += chunk) {
            String fileName = Integer.toString(i) + ".txt";
            fileNames.add(directory.getPath() + "/" + fileName);
        }
        return fileNames;
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
    
    public static void writeMatchesToDB(ArrayList<String> matches, ArrayList<String> dataParser) {
        ArrayList<String> data = new ArrayList<>();
        DBClient client = new DBClient();
        if (matches.size() > 10 && dataParser.size() > 0) {
            data.add(matches.get(4));
            data.add(matches.get(5));
            data.add(matches.get(6));
            data.add(matches.get(7));
            client.insertIPRI(data, dataParser);
        }
        
    }

}
