package com.zestedesavoir.zestwriter.view.task;

import com.zestedesavoir.zestwriter.MainApp;
import com.zestedesavoir.zestwriter.model.Content;
import com.zestedesavoir.zestwriter.model.Textual;
import com.zestedesavoir.zestwriter.model.markdown.ZMarkdown;
import com.zestedesavoir.zestwriter.utils.Configuration;
import com.zestedesavoir.zestwriter.utils.Corrector;
import com.zestedesavoir.zestwriter.view.MdTextController;
import com.zestedesavoir.zestwriter.view.MenuController;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.apache.commons.lang3.StringEscapeUtils;

import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

public class CorrectionService extends Service<String>{
	private Corrector corrector;
	private Content content;

	public CorrectionService(MdTextController mdText) {
		this.content = (Content) mdText.getSummary().getRoot().getValue();
		corrector = new Corrector();
	}

	@Override
    protected Task<String> createTask() {
        return new Task<String>() {
            @Override
            protected String call(){
                updateMessage(Configuration.getBundle().getString("ui.task.correction.prepare.label")+" ...");

                Function<Textual, String> prepareValidationReport = (Textual ext) -> {
                    updateMessage(ext.getTitle());
                    String markdown = ext.readMarkdown();
                    String htmlText = StringEscapeUtils.unescapeHtml4(ZMarkdown.markdownToHtml(markdown));
                    return corrector.checkHtmlContentToText(htmlText, ext.getTitle());
                };
                Map<Textual, String> validationResult = content.doOnTextual(prepareValidationReport);

                StringBuilder resultCorrect = new StringBuilder();
                for (Entry<Textual, String> entry : validationResult.entrySet()) {
                    resultCorrect.append(entry.getValue());
                }
                return StringEscapeUtils.unescapeHtml4(resultCorrect.toString());
            }
        };
    }
}
