package com.zestedesavoir.zestwriter.view.com;

import org.fxmisc.richtext.StyleClassedTextArea;

import java.text.BreakIterator;

public class CustomStyledClassedTextArea extends StyleClassedTextArea{

    public CustomStyledClassedTextArea() {
        super(true);
        setWrapText(true);
    }

    @Override
    public void previousWord(org.fxmisc.richtext.NavigationActions.SelectionPolicy selectionPolicy) {
        if(getLength() == 0) {
            return;
        }

        BreakIterator wordBreakIterator = BreakIterator.getWordInstance();
        wordBreakIterator.setText(getText());
        wordBreakIterator.preceding(getCaretPosition());

        moveTo(wordBreakIterator.current(), selectionPolicy);
    }

    @Override
    public void nextWord(org.fxmisc.richtext.NavigationActions.SelectionPolicy selectionPolicy) {
        if(getLength() == 0) {
            return;
        }

        BreakIterator wordBreakIterator = BreakIterator.getWordInstance();
        wordBreakIterator.setText(getText());
        wordBreakIterator.following(getCaretPosition());

        moveTo(wordBreakIterator.current(), selectionPolicy);
    }
}
