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

        void build() {
            Queue<Node> q = new ArrayDeque<>();
            root.fail = root;
            q.add(root);

            while (!q.isEmpty()) {
                Node v = q.poll();
                for (var e : v.next.entrySet()) {
                    char c = e.getKey();
                    Node u = e.getValue();
                    Node f = v.fail;

                    while (f != root && !f.next.containsKey(c)) {
                        f = f.fail;
                    }
                    if (f.next.containsKey(c) && f.next.get(c) != u) {
                        u.fail = f.next.get(c);
                    } else {
                        u.fail = root;
                    }
                    u.out.addAll(u.fail.out);
                    q.add(u);
                }
            }
        }

        int search(String text, boolean printMatches) {
            Node v = root;
            int totalMatches = 0;

            for (int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);
                while (v != root && !v.next.containsKey(c)) {
                    v = v.fail;
                }
                if (v.next.containsKey(c)) {
                    v = v.next.get(c);
                }
                if (!v.out.isEmpty()) {
                    totalMatches += v.out.size();
                    if (printMatches) {
                        System.out.println("At index " + i + " matched: " + v.out);
                    }
                }
            }
            return totalMatches;
        }
    }


}