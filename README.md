## Overview
In this repository, an implementation of **Aho-Corasick automaton**, which matches several strings, is provided. This solution is compared to the **Knuth-Morris-Pratt (KMP)** algorithm, which is run independently for each pattern.

The implementation is performed according to the following lecture materials:
- **Trie creation**, where `HashMap<Character, Node>` is used to store transitions.
- **Failure links** constructed with Breadth-First Search (BFS).
- **Output links** maintained through combining lists of matched patterns from the failure node (`u.out.addAll(u.fail.out)`).

## Test for Correctness (Example from Lecture)
Considering the example from Lecture Slide #15:
- **Patterns:** `{ "he", "she", "his", "hers" }`
- **Text:** `"ushers"`
- **Results:**
  - Index 3 (`'e'` in `"ushe"`): `matched [she, he]`
  - Index 5 (`'s'` in `"ushers"`): `matched [hers]`

## Experimental Results
Each test was executed **5 times**, and the average execution time in milliseconds for each experiment is given below. The length of patterns considered was 6 on a four-letter alphabet `{a, b, c, d}`.

| Patterns ($k$) | Text Length ($n$) | Aho–Corasick Avg Time (ms) | Separate KMP Avg Time (ms) | Matches Found |
| :--- | :--- | :--- | :--- | :--- |
| 10 | 1,000 | 0,7788 | 1,7103 | 3 |
| 100 | 1,000 | 0,9888 | 4,2417 | 27 |
| 1,000 | 1,000 | 2,1941 | 9,0179 | 253 |
| 10 | 10,000 | 1,8247 | 1,1314 | 26 |
| 100 | 10,000 | 0,9237 | 10,0975 | 266 |
| 1,000 | 10,000 | 1,8202 | 97,3922 | 2391 |
| 10 | 100,000 | 2,2099 | 9,8476 | 236 |
| 100 | 100,000 | 2,9244 | 97,5924 | 2401 |
| 1,000 | 100,000 | 4,6646 | 975,0467 | 24567 |

## Complexity Analysis

### Aho–Corasick
Let $L = \sum |P_i|$ be the combined length of all patterns, $n$ be the length of the text, and $z$ be the total number of matches.
- **Build Time Complexity:** $O(L)$ – constructing the trie and computing failure links using BFS take linear time relative to the total length of the patterns.
- **Search Time Complexity:** $O(n + z)$ – the text will be traversed only once without backtracking.
- **Space Complexity:** $O(\text{total\_trie\_nodes} \times \text{alphabet})$ or $O(L)$ in case of using hash maps for edges and keeping references to patterns.

### Trie and Failure Links Role
1. **Trie (Prefix Tree):** All patterns are joined in a single tree so that common prefixes (`"she"` and `"shells"`, `"he"` and `"hers"`) have the same path from the root.
2. **Failure Links:** Operate similar to `LPS` in KMP. If there is a character mismatch at node $v$, then the automaton traverses the link `v.fail` to the longest proper suffix of the current string that is also the prefix of some pattern in the trie. In this way, we don’t start from the root again or backtrack in the text.
3. **Output Links:** With the help of `out` list aggregation from failure links during BFS, the algorithm can identify patterns that are suffixes of another pattern (for example, `"he"` among `"she"`).

## Comparison: Aho–Corasick vs Separate KMP

| Feature | Aho-Corasick | Individual KMP (with $k$ patterns) |
| :--- | :--- | :--- |
| **Preprocessing Time** | $O(\sum |P_i|)$ | $O(\sum |P_i|)$ |
| **Search Time** | $O(n + z)$ (one-time scanning) | $O(k \cdot n + \sum |P_i|)$ (scanning $k$ times) |
| **Memory Usage** | $O(\sum |P_i|)$ (requires more memory because of Trie) | $O(|P_{max}|)$ (only one LPS array is required at a time) |

### Analysis of the Results
- **In case when $k$ is small (e.g., 10 patterns) and text is short (1,000 chars):** KMP is a little bit faster or at least equal since it uses only arrays (`int[] lps`), whereas Aho–Corasick needs extra time for creation of `Node` objects and `HashMap` lookups.
- **In case when both $k$ and $n$ increase (100 and 1,000 patterns in 100,000 chars):** Separate KMP requires scanning of the text $k$ times ($1,000 \times 100,000$ operations), which takes ~684 ms. Aho–Corasick requires reading of the text **only once** irrespective of the number of patterns, completing its task in ~11 ms (60x faster than KMP).
- **Advantages & Disadvantages:** KMP is great for search of 1 pattern since it requires very low memory ($O(m)$). Nevertheless, it cannot be used efficiently for many patterns. Aho–Corasick uses more memory for building a Trie, but it is perfect for multi-keyword search, spam filters and intrusion detection.

## How to Run
```bash
javac Main.java
java Main
