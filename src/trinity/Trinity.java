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
        // Input site for the program
        Website website;
        
        int switchWebsites = 7; // 1 - ipri.kiev.ua, 2 - journal.iasa.kpi.ua
        switch (switchWebsites) {
            case 1:
                website = new WebsiteIpri();
                break;
            case 2:
                website = new WebsiteIasa();
                break;
            case 3:
                website = new WebsiteInfotelesc();
                break;
            case 4:
                website = new WebsiteBulletinEconom();
                break;
            case 5:
                website = new WebsiteVisnykGeo();
                break;
            case 6:
                website = new WebsiteAstroBulletin();
                break;
            case 7:
                website = new WebsiteVisnykSoc();
                break;
            default:
                website = new WebsiteIpri();
                break;
        }
        
        // Path to folder where all related files are located
        String folderPath = String.format("../Spider/result/%s/", website.getUrl());
        
        // Path to output directory
        String outputFolderPath = folderPath + "/output/";
        
        // Input directory
        File inputDirectory = new File(folderPath + "/input/");
        
        // Get documents for pattern generating
        ArrayList<String> fileNamesForPattern = getDocumentsForPattern(inputDirectory);
        ArrayList<String> documents = readFiles(fileNamesForPattern);
        
        // Set and build tree
        TrinityTree tree = new TrinityTree();
        tree.setDocuments(documents);
        tree.buildTree(15, 600);
        
        // Generate pattern from the tree
        String template = tree.learnTemplate();
        writeFile(outputFolderPath + "pattern.txt", template);
        
        // Compile pattern
        Pattern TAG_REGEX = Pattern.compile(template);
        documents = new ArrayList<>();
        
        // Generate the verification set
        int testSize = inputDirectory.listFiles().length;
        for (final File fileEntry : inputDirectory.listFiles()) {
            if (!fileEntry.isDirectory()) {
                documents.add(readFile(fileEntry.getAbsolutePath()));
            }
            testSize--;
            if (testSize == 0) {
                break;
            }
        }
        
        // Clean database table
        website.cleanTable();
        
        // Extract data by the pattern and parser
        for (int i = 0; i < documents.size(); i++) {
            ArrayList<String> tags = new ArrayList<>();
            ArrayList<String> tagsParser = website.fetchData(documents.get(i));
            Matcher matcher = TAG_REGEX.matcher(documents.get(i));
            while (matcher.find()) {
                for (int j = 1; j <= matcher.groupCount(); j++) {
                    tags.add(matcher.group(j));
                }
            }
            System.gc();
            // Remove duplicates
            tags = new ArrayList<>(new LinkedHashSet<>(tags));
            writeMatches(tags, String.format("%s/matches-%d.txt", outputFolderPath, i));
            
            // Write results to the database
            website.writeMatchesToDB(tags, tagsParser);
        }
    }

    /**
     * Read all files from the given list
     * @param fileNames
     * @return
     */
    public static ArrayList<String> readFiles(ArrayList<String> fileNames) {
        ArrayList<String> list = new ArrayList<>();
        fileNames.forEach((fileName) -> {
            list.add(readFile(fileName));
        });
        return list;
    }
    
    /**
     * Get documents for pattern generating
     * @param directory
     * @return 
     */
    public static ArrayList<String> getDocumentsForPattern(File directory) {
        ArrayList<String> fileNames = new ArrayList<>();
        int fileCount = directory.list().length;
        
        // Choose 20% of the input file
        int chunk = (int)(fileCount / 5);
        for (int i = 1; i < fileCount; i += chunk) {
            String fileName = Integer.toString(i) + ".txt";
            String fullFilePath = directory.getPath() + "/" + fileName;
            File f = new File(fullFilePath);
            if (f.exists()) {
                fileNames.add(fullFilePath);
            }
            else {
                int a = 4;
            }
        }
        return fileNames;
    }
    
    /**
     * Read file from the given path
     * @param filePath
     * @return 
     */
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
    
    /**
     * Write UTF-8 content to file
     * @param filePath
     * @param pattern 
     */
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
    
    /**
     * Write all matches to the file
     * @param matches
     * @param folderPath 
     */
    public static void writeMatches(ArrayList<String> matches, String folderPath) {
        String listString = "";
        for (String match : matches) {
            listString += match + "\r\n";
        }
        writeFile(folderPath, listString);
    }
    
    

}
