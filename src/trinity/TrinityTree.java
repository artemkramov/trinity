/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trinity;

import com.sun.javafx.scene.control.skin.VirtualFlow;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Artem Kramov
 */
public class TrinityTree {

    private TrinityNode root = new TrinityNode();

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
                this.buildTreeByNode(leave, minRange, currentSize);
            }
        }
    }
    
    public String learnTemplate() {
        return this.root.learnTemplate("");
    }

    /**
     * Node class
     */
    private class TrinityNode {

        private String text;
        private TrinityNode prefix;
        private TrinityNode separator;
        private TrinityNode suffix;
        private ArrayList<String> documents;

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
            return this.prefix == null;
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
            for (int i = 0; i < base.length() - patternSize; i++) {
                pattern.clearMap();
                boolean isFound = true;
                for (String document : this.documents) {
                    if (document.equals(base) || document.isEmpty()) {
                        continue;
                    }
                    List<Integer> matches = findMatches(document, base, i, patternSize);
                    isFound = matches.size() > 0;
                    if (!isFound) {
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
                if (base.length() < this.documents.get(i).length()) {
                    base = this.documents.get(i);
                }
            }
            return base;
        }

        private List<Integer> findMatches(String text, String base, int offset, int patternSize) {
            String substring = base.substring(offset, offset + patternSize);
            KMPStringSearch kmpModel = new KMPStringSearch();
            return kmpModel.searchString(text, substring);
        }

        private void createChildren(TrinityPattern trinityPattern) {
            TrinityNode prefix = new TrinityNode();
            TrinityNode separator = new TrinityNode();
            TrinityNode suffix = new TrinityNode();
            this.setText(trinityPattern.getPattern());
            this.documents.forEach((document) -> {
                List<Integer> matches = trinityPattern.getMapByKey(document);
                prefix.computePrefix(matches, document);
                separator.computeSeparator(matches, document, trinityPattern.getPattern().length());
                suffix.computeSuffix(matches, document, trinityPattern.getPattern().length());
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
            }
            this.getPrefix().getDocuments().add(prefixString);
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
            this.getSeparator().getDocuments().add(separatorString);
        }

        public void computeSuffix(List<Integer> matches, String document, int patternSize) {
            String suffixString;
            int lastOccurrence = matches.get(matches.size() - 1);
            if (lastOccurrence + patternSize == matches.size() - 1) {
                suffixString = "";
            } else {
                suffixString = document.substring(lastOccurrence, lastOccurrence + patternSize);
            }
            this.getSuffix().getDocuments().add(suffixString);
        }

        public String learnTemplate(String regex) {
            if (this.isOptional()) {
                regex += "(";
            }
            if (this.isLeaf()) {
                if (this.hasVariability()) {
                    regex += "A";
                }
            }
            else {
                regex += this.prefix.learnTemplate(regex);
                regex += this.text;
                if (this.isRepeatable()) {
                    regex += "(" + this.separator.learnTemplate(regex) + this.text;
                    if (this.separator.containsNull()) {
                        regex += ")*";
                    }
                    else {
                        regex += ")+";
                    }
                }
                regex += this.suffix.learnTemplate(regex);
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
            if (counter > 0 && counter < this.documents.size() - 2) {
                result = true;
            }
            return result;
        }

        private boolean hasVariability() {
            boolean result = true;
            if (this.documents.size() > 1) {
                String previousDocument = this.documents.get(0);
                for (int i = 1; i < this.documents.size(); i++) {
                    if (!previousDocument.equals(this.documents.get(i))) {
                        result = false;
                        break;
                    }
                    previousDocument = this.documents.get(i);
                }
            } else {
                result = false;
            }
            return result;
        }

        private boolean isRepeatable() {
            boolean result = false;
            KMPStringSearch kmp = new KMPStringSearch();
            for (String document : this.documents) {
                List<Integer> matches = kmp.searchString(this.text, document);
                if (matches.size() > 1) {
                    result = true;
                    break;
                }
            }
            return result;
        }

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