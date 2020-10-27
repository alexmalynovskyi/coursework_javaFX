package sample;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class HtmlParser {
    private String html;
    private Document doc;

    HtmlParser(String html) throws IOException {
        this.html = html;
        getPage();
    }

    private void getPage() throws IOException {
        this.doc = Jsoup.parse(html);
    }

    public String getсClassContent(String classDescriptor) {
        return doc.select(classDescriptor).first().html().trim();
    }

    public String getTagContent(String searchPattern) {
        return doc.select(searchPattern).first().attr("content");
    }
}
