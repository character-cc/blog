package com.example.blog.util;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class ExtractHtmlContent {

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

    public static List<String> extractImageUrls(String htmlContent) {
        List<String> imageUrls = new ArrayList<>();
        Document doc = Jsoup.parse(htmlContent);
        Elements imgElements = doc.select("img");
        for (Element img : imgElements) {
            String src = img.attr("src");
            if (!src.isEmpty()) {
                imageUrls.add(src);
            }
        }
        return imageUrls;
    }

}
