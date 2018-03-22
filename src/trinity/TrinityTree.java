/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trinity;

import com.google.gson.Gson;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Artem Kramov
 */
public class TrinityTree {

    private TrinityNode root = new TrinityNode();
    
    public int labelCount = 0;

    public TrinityTree() {
    }

    public void setDocuments(ArrayList<String> _documents) {
        this.root.setDocuments(_documents);
    }

    public void buildTree(int minRange, int maxRange) {
        this.buildTreeByNode(this.root, minRange, maxRange);
    }

    private void buildTreeByNode(TrinityNode node, int minRange, int maxRange) {
        boolean isExpanded = false;
        int currentSize = maxRange;
        while (currentSize >= minRange && !isExpanded) {
            isExpanded = node.expand(currentSize);
            currentSize--;
        }
        if (isExpanded) {
            ArrayList<TrinityNode> leaves = node.getLeaves();
            for (TrinityNode leave : leaves) {
                this.buildTreeByNode(leave, minRange, maxRange);
            }
        }
    }

    public String learnTemplate() {
        return this.root.learnTemplate(this).replace("\\E\\Q", "");
    }

    public void traverseForPrint() {
        List<TrinityExportItem> items = new ArrayList<>();
        this.traverseRecursiveForPrint(root, null, items);
        Gson gson = new Gson();
        try {
            String jsonString = gson.toJson(items);
            FileWriter writer = new FileWriter("C:\\wamp\\www\\test.ak\\basic-example\\file.json");
            writer.write(jsonString);
            writer.flush();
        } catch (Exception ex) {
            int t = 4;
        }
        int a = 4;
    }

    private void traverseRecursiveForPrint(TrinityNode node, TrinityNode parent, List<TrinityExportItem> items) {
        if (node != null) {
            node.printForDraw(parent, items);
            traverseRecursiveForPrint(node.prefix, node, items);
            traverseRecursiveForPrint(node.separator, node, items);
            traverseRecursiveForPrint(node.suffix, node, items);
        }
    }

    /**
     * Node class
     */
    private class TrinityNode {

        private String text;
        private TrinityNode prefix;
        private TrinityNode separator;
        private TrinityNode suffix;
        private ArrayList<String> documents = new ArrayList<>();

        public void setText(String _text) {
            this.text = _text;
        }

        public String getText() {
            return this.text;
        }

        /**
         * {@link TrinityNode#prefix}
         *
         * @param _prefix
         * @return TrinityNode
         */
        public TrinityNode setPrefix(TrinityNode _prefix) {
            this.prefix = _prefix;
            return this;
        }

        public TrinityNode getPrefix() {
            return this.prefix;
        }

        public void setSuffix(TrinityNode _suffix) {
            this.suffix = _suffix;
        }

        public TrinityNode getSuffix() {
            return this.suffix;
        }

        public void setSeparator(TrinityNode _separator) {
            this.separator = _separator;
        }

        public TrinityNode getSeparator() {
            return this.separator;
        }

        public void setDocuments(ArrayList<String> _documents) {
            this.documents = _documents;
        }

        public ArrayList<String> getDocuments() {
            return this.documents;
        }

        public boolean isLeaf() {
            return StringUtils.isEmpty(this.text) || this.text == null;
        }

        public ArrayList<TrinityNode> getLeaves() {
            ArrayList<TrinityNode> leaves = new ArrayList<>();
            if (!this.isLeaf()) {
                leaves.add(this.prefix);
                leaves.add(this.separator);
                leaves.add(this.suffix);
            }
            return leaves;
        }

        public boolean expand(int patternSize) {
            boolean isExpanded = false;
            if (this.getSize() > 1) {
                TrinityPattern trinityPattern = findPattern(patternSize);
                if (trinityPattern.getMap().size() > 0) {
                    isExpanded = true;
                    this.createChildren(trinityPattern);
                }
            }
            return isExpanded;
        }

        private int getSize() {
            int size = 0;
            for (String text : this.documents) {
                if (!StringUtils.isEmpty(text)) {
                    size++;
                }
            }
            return size;
        }

        private TrinityPattern findPattern(int patternSize) {
            TrinityPattern pattern = new TrinityPattern();
            pattern.pattern = "";
            String base = this.findShortestPath();
            for (int i = 0; i < base.length() - patternSize + 1; i++) {
                pattern.clearMap();
                boolean isFound = true;
                for (String document : this.documents) {
                    if (document == null || document.isEmpty()) {
                        continue;
                    }
                    List<Integer> matches = findMatches(document, base, i, patternSize);
                    isFound = matches.size() > 0;
                   
                    if (!isFound) {
                        pattern.clearMap();
                        break;
                    }
                    TrinityMap mapItem = new TrinityMap();
                    mapItem.setText(document);
                    mapItem.setMap(matches);
                    pattern.getMap().add(mapItem);
                }
                if (isFound) {
                    pattern.pattern = base.substring(i, i + patternSize);
                    break;
                }
            }
            return pattern;
        }

        private String findShortestPath() {
            if (this.documents.isEmpty()) {
                return "";
            }
            String base = this.documents.get(0);
            for (int i = 1; i < this.documents.size(); i++) {
                if (base == null || (this.documents.get(i) != null && base.length() > this.documents.get(i).length())) {
                    base = this.documents.get(i);
                }
            }
            return base;
        }

        private List<Integer> findMatches(String text, String base, int offset, int patternSize) {
            String substring = base.substring(offset, offset + patternSize);
            if (!substring.contains(">") || !substring.contains("<")) {
                return new ArrayList<>();
            }
            KMPStringSearch kmpModel = new KMPStringSearch();
            return kmpModel.searchString(substring, text);
        }

        private void createChildren(TrinityPattern trinityPattern) {
            TrinityNode prefix = new TrinityNode();
            TrinityNode separator = new TrinityNode();
            TrinityNode suffix = new TrinityNode();
            this.setText(trinityPattern.getPattern());
            this.documents.forEach((document) -> {
                if (document != null) {
                    List<Integer> matches = trinityPattern.getMapByKey(document);
                    prefix.computePrefix(matches, document);
                    separator.computeSeparator(matches, document, trinityPattern.getPattern().length());
                    suffix.computeSuffix(matches, document, trinityPattern.getPattern().length());
                }
            });
            this.setPrefix(prefix);
            this.setSeparator(separator);
            this.setSuffix(suffix);
        }

        public void computePrefix(List<Integer> matches, String document) {
            String prefixString;
            int firstOccurrence = matches.get(0);
            if (firstOccurrence == 0) {
                prefixString = "";
            } else {
                prefixString = document.substring(0, firstOccurrence);
                int a = 2;
            }
            this.getDocuments().add(prefixString);

        }

        public void computeSeparator(List<Integer> matches, String document, int patternSize) {
            String separatorString;
            if (matches.size() == 1) {
                separatorString = null;
            } else {
                int firstOccurrence = matches.get(0);
                int secondOccurrence = matches.get(1);
                if (secondOccurrence - firstOccurrence + patternSize == 1) {
                    separatorString = "";
                } else {
                    separatorString = document.substring(firstOccurrence + patternSize, secondOccurrence);
                }
            }
            this.getDocuments().add(separatorString);
        }

        public void computeSuffix(List<Integer> matches, String document, int patternSize) {
            String suffixString;
            int lastOccurrence = matches.get(matches.size() - 1);
            if (lastOccurrence + patternSize == document.length()) {
                suffixString = "";
            } else {
                suffixString = document.substring(lastOccurrence + patternSize, document.length());
            }
            int a = document.length();
            this.getDocuments().add(suffixString);
        }

        public String learnTemplate(TrinityTree tree) {
            String regex = "";
            if (this.isOptional()) {
                regex += "(";
            }
            if (this.isLeaf()) {
                if (this.hasVariability()) {
                    regex += "(.+)";//"{LABEL" + tree.labelCount + "}";
                    tree.labelCount++;
                }
            } else {
                regex += this.prefix.learnTemplate(tree);
                regex += Pattern.quote(this.text);
                if (this.isRepeatable()) {
                    regex += "(" + this.separator.learnTemplate(tree) + Pattern.quote(this.text);
                    if (this.separator.containsNull()) {
                        regex += ")?";
                    } else {
                        regex += ")*";
                    }
                }
                regex += this.suffix.learnTemplate(tree);
            }
            if (this.isOptional()) {
                regex += ")?";
            }
            return regex;
        }

        public boolean containsNull() {
            boolean result = false;
            for (String document : this.documents) {
                if (document == null) {
                    result = true;
                    break;
                }
            }
            return result;
        }

        private boolean isOptional() {
            boolean result = false;
            int counter = 0;
            for (String document : this.documents) {
                if (StringUtils.isEmpty(document)) {
                    counter++;
                }
            }
            if (counter > 0 && counter < this.documents.size()) {
                result = true;
            }
            return result;
        }

        private boolean hasVariability() {
            boolean result = false;
            if (this.documents.size() > 1) {
                String previousDocument = this.documents.get(0);
                for (int i = 1; i < this.documents.size(); i++) {
                    if (previousDocument != null) {
                        if (!previousDocument.equals(this.documents.get(i))) {
                            result = true;
                            break;
                        }
                    }
                    previousDocument = this.documents.get(i);
                }
            }
            return result;
        }

        private boolean isRepeatable() {
            boolean result = false;
            KMPStringSearch kmp = new KMPStringSearch();
            for (String document : this.documents) {
                if (document == null || document.isEmpty()) {
                    continue;
                }
                List<Integer> matches = kmp.searchString(this.text, document);
                if (matches.size() > 1) {
                    result = true;
                    break;
                }
            }
            return result;
        }

        public void printForDraw(TrinityNode parent, List<TrinityExportItem> items) {
            TrinityExportItem item = new TrinityExportItem();
            item.id = "ID" + System.identityHashCode(this);
            item.documents.add("Pattern: " + StringEscapeUtils.escapeHtml4(this.text));
            String row = "";
            for (int i = 0; i < this.documents.size(); i++) {
                if (documents.get(i) == null) {
                    row = "nill";
                } else {
                    String s = documents.get(i);
                    if (s.isEmpty()) {
                        s = "e";
                    }
                    row = s;
                }
                item.documents.add(StringEscapeUtils.escapeHtml4(row));
            }
            item.parent = "ID" + System.identityHashCode(parent);
            items.add(item);
        }

    }

    private class TrinityExportItem {

        private String id;
        private String parent;
        private List<String> documents = new ArrayList<>();
    }

    private class TrinityPattern {

        private String pattern;
        private List<TrinityMap> map = new ArrayList<>();

        public void setPattern(String _pattern) {
            this.pattern = _pattern;
        }

        public String getPattern() {
            return this.pattern;
        }

        public void clearMap() {
            this.map = new ArrayList<>();
        }

        public List<TrinityMap> getMap() {
            return this.map;
        }

        public List<Integer> getMapByKey(String key) {
            for (TrinityMap mapItem : this.map) {
                if (mapItem.getText().equals(key)) {
                    return mapItem.getMap();
                }
            }
            return new ArrayList<>();
        }
    }

    private class TrinityMap {

        private String text;
        private List<Integer> map = new ArrayList<>();

        public String getText() {
            return this.text;
        }

        public void setText(String _text) {
            this.text = _text;
        }

        public void clearMap() {
            this.map = new ArrayList<>();
        }

        public List<Integer> getMap() {
            return this.map;
        }

        public void setMap(List<Integer> _map) {
            this.map = _map;
        }

    }

}
