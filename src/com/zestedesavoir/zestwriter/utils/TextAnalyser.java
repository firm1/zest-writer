package com.zestedesavoir.zestwriter.utils;

import java.util.StringTokenizer;

public class TextAnalyser {

    public static float getFleshIndex(String content) {

        int syllables = 0;
        int words = 0;

        String delimiters = ".,':;?{}[]=-+_!@#$%^&*() ";
        StringTokenizer tokenizer = new StringTokenizer(content, delimiters);
        // go through all words
        while (tokenizer.hasMoreTokens()) {
            String word = tokenizer.nextToken();
            syllables += countSyllables(word);
            words++;
        }
        // look for sentence delimiters
        String sentenceDelim = ".:;?!";
        StringTokenizer sentenceTokenizer = new StringTokenizer(content, sentenceDelim);
        int sentences = sentenceTokenizer.countTokens();

        // calculate flesch index
        final float f1 = (float) 206.835;
        final float f2 = (float) 84.6;
        final float f3 = (float) 1.015;
        float r1 = (float) syllables / (float) words;
        float r2 = (float) words / (float) sentences;
        float flesch = f1 - (f2 * r1) - (f3 * r2);

        // Write Report
        String report = "";

        report += "Total Syllables: " + syllables + "\n";
        report += "Total Words    : " + words + "\n";
        report += "Total Sentences: " + sentences + "\n";
        report += "Flesch Index   : " + flesch + "\n";
        System.out.println(report);

        System.out.println(report);

        return flesch;
    }

    // A method to count the number of syllables in a word
    // Pretty basic, just based off of the number of vowels
    // This could be improved
    public static int countSyllables(String word) {
        int syl = 0;
        boolean vowel = false;
        int length = word.length();

        // check each word for vowels (don't count more than one vowel in a row)
        for (int i = 0; i < length; i++) {
            if (isVowel(word.charAt(i)) && (!vowel)) {
                vowel = true;
                syl++;
            } else vowel = isVowel(word.charAt(i)) && (vowel);
        }

        char tempChar = word.charAt(word.length() - 1);
        // check for 'e' at the end, as long as not a word w/ one syllable
        if (((tempChar == 'e') || (tempChar == 'E')) && (syl != 1)) {
            syl--;
        }
        return syl;
    }

    // check if a char is a vowel (count y)
    public static boolean isVowel(char c) {
        return "AEIOUYaeiouy".indexOf(c) != -1;
    }

    public static void main(String[] args) {
        String test_string = "bonjour mon nom est mister blabla,. \n\nje danse dans la course avec mon petit chaperon rouge.";
        System.out.println(" ==> " + getFleshIndex(test_string));
    }
}
