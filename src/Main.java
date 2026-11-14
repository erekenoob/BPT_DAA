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

    static class KMP {
        static void computeLPSArray(String pat, int M, int[] lps) {
            int len = 0;
            int i = 1;
            lps[0] = 0;

            while (i < M) {
                if (pat.charAt(i) == pat.charAt(len)) {
                    len++;
                    lps[i] = len;
                    i++;
                } else {
                    if (len != 0) {
                        len = lps[len - 1];
                    } else {
                        lps[i] = 0;
                        i++;
                    }
                }
            }
        }

        static int KMPSearch(String pat, String txt) {
            int M = pat.length();
            int N = txt.length();
            int[] lps = new int[M];
            computeLPSArray(pat, M, lps);

            int i = 0;
            int j = 0;
            int count = 0;

            while (i < N) {
                if (pat.charAt(j) == txt.charAt(i)) {
                    j++;
                    i++;
                }
                if (j == M) {
                    count++;
                    j = lps[j - 1];
                } else if (i < N && pat.charAt(j) != txt.charAt(i)) {
                    if (j != 0) {
                        j = lps[j - 1];
                    } else {
                        i = i + 1;
                    }
                }
            }
            return count;
        }
    }
}