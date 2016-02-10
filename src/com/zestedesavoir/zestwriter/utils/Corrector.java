package com.zestedesavoir.zestwriter.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import org.languagetool.JLanguageTool;
import org.languagetool.language.French;
import org.languagetool.markup.AnnotatedText;
import org.languagetool.markup.AnnotatedTextBuilder;
import org.languagetool.rules.Rule;
import org.languagetool.rules.RuleMatch;
import org.languagetool.rules.spelling.SpellingCheckRule;

public class Corrector {

	JLanguageTool langTool;
	List<String> wordsToIgnore;

	public Corrector() {
		langTool = new JLanguageTool(new French());
		wordsToIgnore= new ArrayList<String>();
		for (Rule rule : langTool.getAllRules()) {
		    langTool.disableRule("WHITESPACE_RULE");
		}
	}

	public void ignoreRule(String str) {
		for (Rule rule : langTool.getAllRules()) {
		    langTool.disableRule(str);
		}
	}

	public static String HtmlToTextWithoutCode(String htmlText) {
		AnnotatedTextBuilder builder = new AnnotatedTextBuilder();
	    StringTokenizer tokenizer = new StringTokenizer(htmlText, "<>", true);
	    boolean inMarkup = false;
	    int CountCode=0, CountPre=0, CountEm=0, CountSup=0;
	    while (tokenizer.hasMoreTokens()) {
	      String part = tokenizer.nextToken();
	      if (inMarkup) {
		      if (part.equals("code")) {
		    	  CountCode++;
		      } else if (part.equals("pre")) {
		    	  CountPre++;
		      } else if (part.equals("/code")) {
		    	  CountCode--;
		      } else if (part.equals("/pre")) {
		    	  CountPre--;
		      }
	      }
	      if (part.startsWith("<")) {
	        builder.addMarkup(part);
	        inMarkup = true;
	      } else if (part.startsWith(">")) {
	        inMarkup = false;
	        builder.addMarkup(part);
	      } else {
	        if (inMarkup) {
	          builder.addMarkup(part);
	        } else {
	          if (CountPre == 0 && CountCode == 0 ) {
	        	  builder.addText(part);
	          }
	        }
	      }
	    }
	    return builder.build().getPlainText();
	}

	private String generate(int k, char c) {
		StringBuffer st = new StringBuffer();
		for(int i=0; i<k; i++) {
			st.append(c);
		}
		return st.toString();
	}

	private AnnotatedText makeAnnotatedText(String pseudoXml) {
	    AnnotatedTextBuilder builder = new AnnotatedTextBuilder();
	    StringTokenizer tokenizer = new StringTokenizer(pseudoXml, "<>", true);
	    boolean inMarkup = false;
	    int CountCode=0, CountPre=0, CountEm=0, CountSup=0;
	    while (tokenizer.hasMoreTokens()) {
	      String part = tokenizer.nextToken();
	      if (inMarkup) {
		      if (part.equals("code")) {
		    	  CountCode++;
		      } else if (part.equals("pre")) {
		    	  CountPre++;
		      } else if (part.equals("em")) {
		    	  CountEm++;
		      } else if (part.equals("sup")) {
		    	  CountSup++;
		      } else if (part.equals("/code")) {
		    	  CountCode--;
		      } else if (part.equals("/pre")) {
		    	  CountPre--;
		      } else if (part.equals("/em")) {
		    	  CountEm--;
		      } else if (part.equals("/sup")) {
		    	  CountSup--;
		      }
	      }
	      if (part.startsWith("<")) {
	        builder.addMarkup(part);
	        inMarkup = true;
	      } else if (part.startsWith(">")) {
	        inMarkup = false;
	        builder.addMarkup(part);
	      } else {
	        if (inMarkup) {
	          builder.addMarkup(part);
	        } else {
	          if (CountPre == 0 && CountSup == 0) {
	        	  builder.addText(part);
	        	  if (CountCode > 0 || CountEm == 0 ) {
		        	  wordsToIgnore.addAll(Arrays.asList(part.replaceAll("[^a-zA-Z0-9 ]", "").split(" ")));
		          }
	          }
	          else {
	        	  builder.addText(generate(part.length(), ' '));
	          }
	        }
	      }
	    }
	    return builder.build();
	  }

	public String checkHtmlContent(String htmlContent) throws IOException {
		AnnotatedText markup = makeAnnotatedText(htmlContent);
		StringBuffer bf = new StringBuffer(htmlContent);

		for (Rule rule : langTool.getAllActiveRules()) {
		  if (rule instanceof SpellingCheckRule) {
		    ((SpellingCheckRule)rule).addIgnoreTokens(wordsToIgnore);
		  }
		}

		List<RuleMatch> matches = langTool.check(markup);
		int offset = 0;
		for(RuleMatch match:matches) {
			String desc = "Note : "+match.getRule().getDescription();
			if (match.getSuggestedReplacements().size() > 0) {
				desc += "; Suggestion(s) : "+match.getSuggestedReplacements();
			}
			String before = "<span class=\"error-french\" title=\""+desc+"\">";
			bf.insert(match.getFromPos()+offset, before);
			offset += before.length();

			String after= "</span> ";
			bf.insert(match.getToPos()+offset, after);
			offset += after.length();

		}
		return bf.toString();
	}

	public String checkHtmlContentToText(String htmlContent, String source) throws IOException {
		AnnotatedText markup = makeAnnotatedText(htmlContent);
		StringBuffer bf = new StringBuffer();

		for (Rule rule : langTool.getAllActiveRules()) {
		  if (rule instanceof SpellingCheckRule) {
		    ((SpellingCheckRule)rule).addIgnoreTokens(wordsToIgnore);
		  }
		}

		List<RuleMatch> matches = langTool.check(markup);
		int offset = 0;
		for(RuleMatch match:matches) {
			String txt = htmlContent.substring(match.getFromPos(), match.getToPos());
			bf.append("\n\n");
			bf.append("> ");
			bf.append(markup.getPlainText().split("[\n|\r]")[match.getLine()].replace(txt, "**"+txt+"**"));
			bf.append("\n");
			bf.append("Source : "+source);
			bf.append("\n\n");
			bf.append(match.getRule().getDescription());
			bf.append("\n\n");
			for (String s:match.getSuggestedReplacements()) {
				bf.append("- "+s+"\n");
			}
		}
		return bf.toString();
	}

	public static void main(String[] args) throws IOException {
		Corrector cr = new Corrector();
		String html = "Je vais au <code>sea sex and sun</code>, <i>car</i> je n'<strong>aime</strong> \n<pre>pas</pre>\n la source du coeur coeur:";
		cr.checkHtmlContent(html);
	}
}
