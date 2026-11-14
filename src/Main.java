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

    static String randomString(int length, Random rand) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append((char) ('a' + rand.nextInt(4)));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        AhoCorasick demo = new AhoCorasick();
        String[] lecturePatterns = {"he", "she", "his", "hers"};
        for (String p : lecturePatterns) {
            demo.addPattern(p);
        }
        demo.build();
        String demoText = "ushers";
        System.out.println("Text:" + demoText);
        demo.search(demoText, true);

        int kmpCheck = KMP.KMPSearch("ABABCABAB", "ABABDABACDABABCABAB");
        System.out.println("KMP check on 'ABABDABACDABABCABAB':found " + kmpCheck + " match\n");

        int[] patternCounts = {10, 100, 1000};
        int[] textLengths = {1000, 10000, 100000};
        int runs = 5;
        Random rand = new Random(42);

        System.out.println("Benchmark Results 'Average of 5 runs'");
        System.out.printf("%-10s %-10s %-18s %-18s %-10s%n",
                "Patterns", "TextLen", "Aho-Corasick ms", "KMP Separate ms", "Matches");

        for (int n : textLengths) {
            String text = randomString(n, rand);

            for (int k : patternCounts) {
                List<String> patterns = new ArrayList<>();
                for (int i = 0; i < k; i++) {
                    patterns.add(randomString(6, rand));
                }

                long totalTimeAC = 0;
                long totalTimeKMP = 0;
                int matchesAC = 0;

                for (int r = 0; r < runs; r++) {
                    long startAC = System.nanoTime();
                    AhoCorasick ac = new AhoCorasick();
                    for (String p : patterns) {
                        ac.addPattern(p);
                    }
                    ac.build();
                    matchesAC = ac.search(text, false);
                    long endAC = System.nanoTime();
                    totalTimeAC += (endAC - startAC);

                    long startKMP = System.nanoTime();
                    int matchesKMP = 0;
                    for (String p : patterns) {
                        matchesKMP += KMP.KMPSearch(p, text);
                    }
                    long endKMP = System.nanoTime();
                    totalTimeKMP += (endKMP - startKMP);
                }

                double avgAC = (totalTimeAC / (double) runs) / 1_000_000.0;
                double avgKMP = (totalTimeKMP / (double) runs) / 1_000_000.0;

                System.out.printf("%-10d %-10d %-18.4f %-18.4f %-10d%n",
                        k, n, avgAC, avgKMP, matchesAC);
            }
        }
    }
}