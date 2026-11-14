import java.util.*;

public class Main {

    static class AhoCorasick {
        static class Node {
            Map<Character, Node> next = new HashMap<>();
            Node fail;
            List<String> out = new ArrayList<>();
        }

        Node root = new Node();

        void addPattern(String p) {
            Node v = root;
            for (char c : p.toCharArray()) {
                v = v.next.computeIfAbsent(c, k -> new Node());
            }
            v.out.add(p);
        }


    }


}