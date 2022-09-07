package org.stypox.tridenta.util;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * <a href="https://github.com/Stypox/dicio-android/blob/c2bdb04fd6bad4399ced3583b4518dd78d033bbd/app/src/main/java/org/dicio/dicio_android/util/StringUtils.java">
 *     Class taken from Dicio</a>
 */
public final class StringUtils {
    private static final Pattern WORD_DELIMITERS_PATTERN = Pattern.compile("[^\\p{L}0-9]");
    private static final Pattern DIACRITICAL_MARKS_PATTERN =
            Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

    private StringUtils() {
    }


    /**
     * @param word a lowercase string
     * @return the unicode NFKD normalized value for the provided word
     * @implNote the normalization process could be slow
     */
    public static String nfkdNormalizeWord(final String word) {
        final String normalized = Normalizer.normalize(word, Normalizer.Form.NFKD);
        return DIACRITICAL_MARKS_PATTERN.matcher(normalized).replaceAll("");
    }

    private static String cleanStringForDistance(final String s) {
        return WORD_DELIMITERS_PATTERN.matcher(nfkdNormalizeWord(s.toLowerCase()))
                .replaceAll("");
    }

    /**
     * Returns the dynamic programming memory obtained when calculating the Levenshtein distance.
     * The solution lies at {@code memory[a.length()][b.length()]}. This memory can be used to find
     * the set of actions (insertion, deletion or substitution) to be done on the two strings to
     * turn one into the other.
     * @param a the first string, maybe cleaned with {@link #cleanStringForDistance(String)}
     * @param b the second string, maybe cleaned with {@link #cleanStringForDistance(String)}
     * @return the memory of size {@code (a.length()+1) x (b.length()+1)}
     */
    private static int[][] levenshteinDistanceMemory(final String a, final String b) {
        // memory already filled with zeros, as it's the default value for int
        final int[][] memory = new int[a.length() + 1][b.length() + 1];

        for (int i = 0; i <= a.length(); ++i) {
            memory[i][0] = i;
        }
        for (int j = 0; j <= b.length(); ++j) {
            memory[0][j] = j;
        }

        for (int i = 0; i < a.length(); ++i) {
            for (int j = 0; j < b.length(); ++j) {
                final int substitutionCost = Character.toLowerCase(a.codePointAt(i))
                        == Character.toLowerCase(b.codePointAt(j)) ? 0 : 1;
                memory[i + 1][j + 1] = Math.min(Math.min(
                        memory[i][j + 1] + 1,
                        memory[i + 1][j] + 1),
                        memory[i][j] + substitutionCost);
            }
        }

        return memory;
    }

    private static class LevenshteinMemoryPos {
        final int i;
        final int j;
        LevenshteinMemoryPos(final int i, final int j) {
            this.i = i;
            this.j = j;
        }
    }

    private static List<LevenshteinMemoryPos> pathInLevenshteinMemory(
            final String a, final String b, final int[][] memory) {
        // follow the path from bottom right (score==distance) to top left (where score==0)
        final List<LevenshteinMemoryPos> positions = new ArrayList<>();
        int i = a.length() - 1;
        int j = b.length() - 1;
        while (i >= 0 && j >= 0) {
            positions.add(new LevenshteinMemoryPos(i, j));
            if (memory[i + 1][j + 1] == memory[i][j + 1] + 1) {
                // the path goes up
                --i;
            } else if (memory[i + 1][j + 1] == memory[i + 1][j] + 1) {
                // the path goes left
                --j;
            } else  {
                // the path goes up-left diagonally (surely either
                // memory[i+1][j+1] == memory[i][j] or memory[i+1][j+1] == memory[i][j] + 1)
                --i;
                --j;
            }
        }
        return positions;
    }

    /**
     * Finds the
     * <a href="https://en.wikipedia.org/wiki/Levenshtein_distance">Levenshtein distance</a>
     * between two strings, that is the number of characters that need to be changed to turn one
     * string into the other. The two strings will be cleaned with {@link
     * #cleanStringForDistance(String)} before calculating the distance. Use {@link
     * #customStringDistance(String, String)} for better results when e.g. comparing app names.
     * @see #customStringDistance(String, String)
     * @param aNotCleaned the first string
     * @param bNotCleaned the second string
     * @return the Levenshtein distance between the two cleaned strings, lower is better, values are
     *         always greater than or equal to 0
     */
    public static int levenshteinDistance(final String aNotCleaned, final String bNotCleaned) {
        final String a = cleanStringForDistance(aNotCleaned);
        final String b = cleanStringForDistance(bNotCleaned);
        return levenshteinDistanceMemory(a, b)[a.length()][b.length()];
    }

    /**
     * Calculates a custom string distance between the two provided strings. Internally calculates
     * the dynamic programming memory of the
     * <a href="https://en.wikipedia.org/wiki/Levenshtein_distance">Levenshtein distance</a>, then
     * follows the path chosen by the dynamic programming algorithm and draws some statistics about
     * the total number of matched character and the maximum number of roughly subsequent characters
     * matched. The result is a combination of the latter statistics and the actual Levenshtein
     * distance. The two strings will be cleaned with {@link #cleanStringForDistance(String)} before
     * calculating the distance.
     * @param aNotCleaned the first string
     * @param bNotCleaned the second string
     * @return the custom string distance between the two cleaned strings as described above, lower
     *         is better, values can be lower than 0, values are always less than or equal to the
     *         {@link #levenshteinDistance(String, String)} between the two strings
     */
    public static int customStringDistance(final String aNotCleaned, final String bNotCleaned) {
        final String a = cleanStringForDistance(aNotCleaned);
        final String b = cleanStringForDistance(bNotCleaned);
        final int[][] memory = levenshteinDistanceMemory(a, b);

        int matchingCharCount = 0;
        int subsequentChars = 0;
        int maxSubsequentChars = 0;
        for (final LevenshteinMemoryPos pos : pathInLevenshteinMemory(a, b, memory)) {
            if (Character.toLowerCase(a.codePointAt(pos.i))
                    == Character.toLowerCase(b.codePointAt(pos.j))) {
                ++matchingCharCount;
                ++subsequentChars;
                maxSubsequentChars = Math.max(maxSubsequentChars, subsequentChars);
            } else {
                subsequentChars = Math.max(0, subsequentChars - 1);
            }
        }

        return memory[a.length()][b.length()] - maxSubsequentChars - matchingCharCount;
    }
}
