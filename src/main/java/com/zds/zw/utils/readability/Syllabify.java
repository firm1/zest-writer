package com.zds.zw.utils.readability;
/*
 * Created on Jun 5, 2007
 *
 */


/**
 * @author Panos Ipeirotis
 *
 * Java Code to estimate the number of syllables in a word.
 *
 * Translation of the Perl code by Greg Fast, found at:
 * http://search.cpan.org/author/GREGFAST/Lingua-EN-Syllable-0.251/
 *
 * For documentation and comments
 * http://search.cpan.org/src/GREGFAST/Lingua-EN-Syllable-0.251/Syllable.pm
 *
 */
public class Syllabify {

    static String[] subSyl = { "cial", "tia", "cius", "cious", "giu", "ion", "iou", "sia$", ".ely$" };
    static String[] addSyl = { "ia", "riet", "dien", "iu", "io", "ii", "[aeiouym]bl$", "[aeiou]{3}", "^mc", "ism$", "[^aeiouy][^aeiouy]l$", "[^l]lien","^coa[dglx].", "[^gq]ua[^auieo]", "dnt$" };

    private Syllabify() {
    }

    public static int syllable(String word) {

        String currentWord = word.toLowerCase();
        currentWord = currentWord.replaceAll("'", " ");

        if ("i".equals(currentWord) || "a".equals(currentWord)) {
            return 1;
        }

        if (currentWord.endsWith("e")) {
            currentWord = currentWord.substring(0, currentWord.length() - 1);
        }

        String[] phonems = currentWord.split("[^aeiouy]+");

        int syl = 0;
        for (String syllabe : subSyl) {
            if (currentWord.matches(syllabe)) {
                syl--;
            }
        }
        for (String syllabe : addSyl) {
            if (currentWord.matches(syllabe)) {
                syl++;
            }
        }
        if (currentWord.length() == 1) {
            syl++;
        }

        for (String phonem : phonems) {
            if (phonem.length() > 0)
                syl++;
        }

        if (syl == 0) {
            syl = 1;
        }

        return syl;
    }

}