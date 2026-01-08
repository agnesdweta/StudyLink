package com.example.studylink;

public class SearchResult {

    public long itemId;
    public String itemTitle;
    public String itemDesc;
    public String type;
    public SearchResult() {}


    // ⚡ Constructor harus persis sesuai nama field
    public SearchResult(long itemId, String itemTitle, String itemDesc, String type) {
        this.itemId = itemId;
        this.itemTitle = itemTitle;
        this.itemDesc = itemDesc;
        this.type = type;
    }
}
