package com.zestedesavoir.zestwriter.view.task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zestedesavoir.zestwriter.model.ExtractFile;
import com.zestedesavoir.zestwriter.utils.Corrector;
import com.zestedesavoir.zestwriter.view.MdTextController;
import com.zestedesavoir.zestwriter.view.MenuController;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class CorrectionService extends Service<String>{

	private final Logger logger;
	private List<ExtractFile> myExtracts;
	private Corrector corrector;
	private MdTextController mdText;

	public CorrectionService(MdTextController mdText, List<ExtractFile> myExtracts) {
		logger = LoggerFactory.getLogger(getClass());
		this.mdText = mdText;
		this.myExtracts = myExtracts;
		corrector = new Corrector();
        corrector.ignoreRule("FRENCH_WHITESPACE");
	}



	@Override
    protected Task<String> createTask() {
        return new Task<String>() {
            @Override
            protected String call(){
                updateMessage("Préparation du rapport de validation ...");
                StringBuilder resultCorrect = new StringBuilder();
                for(ExtractFile extract:myExtracts) {
                    updateMessage("Préparation du rapport de validation de "+extract.getTitle().getValue());
                    String markdown = "";
                    // load mdText
                    Path path = Paths.get(extract.getFilePath());
                    Scanner scanner;
                    StringBuilder bfString = new StringBuilder();
                    try {
                        scanner = new Scanner(path, StandardCharsets.UTF_8.name());
                        while (scanner.hasNextLine()) {
                            bfString.append(scanner.nextLine());
                            bfString.append("\n");
                        }
                        markdown = bfString.toString();
                    } catch (IOException e) {
                        logger.error("", e);
                    }

                    String htmlText = StringEscapeUtils.unescapeHtml(MenuController.markdownToHtml(mdText, markdown));
                    String note = corrector.checkHtmlContentToText(htmlText, extract.getTitle().getValue());
                    resultCorrect.append(note);
                }
                return resultCorrect.toString();
            }
        };
    }
}
