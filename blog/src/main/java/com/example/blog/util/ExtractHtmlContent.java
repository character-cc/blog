package com.example.blog.util;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashSet;
import java.util.Set;

public class ExtractHtmlContent {

    private static final String BASE_URL = "http://localhost";

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

    public static Set<String> extractImageUrls(String htmlContent) {
        Set<String> imageUrls = new HashSet<>();
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

    public static String convertRelativeToAbsoluteUrls(String htmlContent) {
        if (htmlContent == null || htmlContent.isEmpty()) {
            return htmlContent;
        }
        Document doc = Jsoup.parse(htmlContent);
        Elements imgElements = doc.select("img");

        for (Element img : imgElements) {
            String src = img.attr("src");
            if (src.startsWith("../api/images")) {
                img.attr("src", BASE_URL + "/api/images" + src.substring("../api/images".length()));
            }
            if (src.startsWith("../../api/images")) {
                img.attr("src", BASE_URL + "/api/images" + src.substring("../../api/images".length()));
            }
        }
        return doc.body().html();
    }
}
