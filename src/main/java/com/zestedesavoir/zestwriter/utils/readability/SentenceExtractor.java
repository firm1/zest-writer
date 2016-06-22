package com.zestedesavoir.zestwriter.utils.readability;

import com.aliasi.sentences.IndoEuropeanSentenceModel;
import com.aliasi.sentences.SentenceModel;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.aliasi.tokenizer.Tokenizer;
import com.aliasi.tokenizer.TokenizerFactory;

import java.util.ArrayList;

/** Use SentenceModel to find sentence boundaries in text */
public class SentenceExtractor {

    final TokenizerFactory TOKENIZER_FACTORY = new IndoEuropeanTokenizerFactory();
    final SentenceModel SENTENCE_MODEL = new IndoEuropeanSentenceModel();

    public String[] getSentences(String text) {

        ArrayList<String> tokenList = new ArrayList<>();
        ArrayList<String> whiteList = new ArrayList<>();
        Tokenizer tokenizer = TOKENIZER_FACTORY.tokenizer(text.toCharArray(), 0, text.length());
        tokenizer.tokenize(tokenList, whiteList);

        String[] tokens = new String[tokenList.size()];
        String[] whites = new String[whiteList.size()];
        tokenList.toArray(tokens);
        whiteList.toArray(whites);
        int[] sentenceBoundaries = SENTENCE_MODEL.boundaryIndices(tokens, whites);

        if (sentenceBoundaries.length < 1) {
            return new String[0];
        }

        String[] result = new String[sentenceBoundaries.length];

        int sentStartTok = 0;
        int sentEndTok;
        for (int i = 0; i < sentenceBoundaries.length; ++i) {
            sentEndTok = sentenceBoundaries[i];
            StringBuilder sb = new StringBuilder();
            for (int j = sentStartTok; j <= sentEndTok; j++) {
                sb.append(tokens[j]).append(whites[j + 1]);

                // System.out.print(tokens[j]+whites[j+1]);
            }
            result[i] = sb.toString();
            sentStartTok = sentEndTok + 1;
        }
        return result;
    }
}