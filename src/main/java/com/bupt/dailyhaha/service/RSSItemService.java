package com.bupt.dailyhaha.service;

import com.bupt.dailyhaha.pojo.RSSItem;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class RSSItemService {
    final MongoTemplate template;

    public RSSItemService(MongoTemplate template) {
        this.template = template;
    }

    public List<RSSItem> getByAuthorAndBoard(String author, String board) {
        return getByMap(Map.of("author", author, "board", board), Set.of("description", "comments", "guid"));
    }

    public List<RSSItem> getByAuthor(String author) {
        return getByMap(Map.of("author", author), Set.of("description", "comments", "guid"));
    }

    public List<RSSItem> getByBoard(String board) {
        return getByMap(Map.of("board", board), Set.of("description", "comments", "guid"));
    }


    public List<RSSItem> getByKeyword(String keyword) {
        Query query = new Query();
        query.addCriteria(Criteria.where("title").regex(keyword));
        query.fields().exclude("description", "comments", "guid");
        // sort by date
        query.with(Sort.by(Sort.Direction.DESC, "pubDate"));
        query.limit(50);
        return template.find(query, RSSItem.class);
    }

    public List<RSSItem> getLatest(Integer limit) {
        Query query = new Query();
        query.fields().exclude("description", "comments", "guid");
        // sort by date
        query.with(Sort.by(Sort.Direction.DESC, "pubDate"));
        query.limit(limit);
        return template.find(query, RSSItem.class);
    }

    private List<RSSItem> getByMap(Map<String, String> map, Set<String> ignoreFields) {
        Query query = new Query();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            query.addCriteria(Criteria.where(entry.getKey()).is(entry.getValue()));
        }
        for (String field : ignoreFields) {
            query.fields().exclude(field);
        }

        // sort by date
        query.with(Sort.by(Sort.Direction.DESC, "pubDate"));

        query.limit(50);
        template.getDb().getCollection("rssitem").createIndex(new org.bson.Document("title", "text"));
        return template.find(query, RSSItem.class);
    }

}
