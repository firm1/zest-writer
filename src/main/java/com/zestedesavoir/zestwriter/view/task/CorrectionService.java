package com.zestedesavoir.zestwriter.view.task;

import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zestedesavoir.zestwriter.model.Content;
import com.zestedesavoir.zestwriter.model.Textual;
import com.zestedesavoir.zestwriter.utils.Corrector;
import com.zestedesavoir.zestwriter.view.MdTextController;
import com.zestedesavoir.zestwriter.view.MenuController;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class CorrectionService extends Service<String>{

	private Corrector corrector;
	private MdTextController mdText;
	private Content content;

	public CorrectionService(MdTextController mdText) {
		this.mdText = mdText;
		this.content = (Content) mdText.getSummary().getRoot().getValue();
		corrector = new Corrector();
        corrector.getLangTool().disableRule("FRENCH_WHITESPACE");
	}


	@Override
    protected Task<String> createTask() {
        return new Task<String>() {
            @Override
            protected String call(){
                updateMessage("Préparation du rapport de validation ...");

                Function<Textual, String> prepareValidationReport = (Textual ext) -> {
                    updateMessage("Préparation du rapport de validation de "+ext.getTitle());
                    String markdown = ext.readMarkdown();
                    String htmlText = StringEscapeUtils.unescapeHtml(MenuController.markdownToHtml(mdText, markdown));
                    return corrector.checkHtmlContentToText(htmlText, ext.getTitle());
                };
                Map<Textual, String> validationResult = (content.doOnTextual(prepareValidationReport));

                StringBuilder resultCorrect = new StringBuilder();
                for (Entry<Textual, String> entry : validationResult.entrySet()) {
                    resultCorrect.append(entry.getValue());
                }
                return resultCorrect.toString();
            }
        };
    }
}
