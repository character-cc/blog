package com.example.blog.util;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
public class ContentSummary {

    public static String getSummaryFromHtml(String htmlContent, int wordLimit) {
        if (htmlContent == null || htmlContent.isEmpty()) {
            return "";
        }

        Document document = Jsoup.parse(htmlContent);
        String plainText = document.text();
        String[] words = plainText.split("\\s+");
        if (words.length <= wordLimit) {
            return plainText;
        }
        StringBuilder summary = new StringBuilder();
        for (int i = 0; i < wordLimit; i++) {
            summary.append(words[i]).append(" ");
        }
        return summary.toString().trim() + "...";
    }

}
