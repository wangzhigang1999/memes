package com.bupt.memes.service.impl.rss;

import com.bupt.memes.model.rss.RSSChannel;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Service
@SuppressWarnings("null")
public class RSSChannelService {

    final MongoTemplate template;

    public RSSChannelService(MongoTemplate template) {
        this.template = template;
    }

    public List<RSSChannel> list() {
        return template.findAll(RSSChannel.class);
    }

    public List<RSSChannel> listByKeyword(String keyword) {
        return template.find(query(where("title").regex(keyword)), RSSChannel.class);
    }

    public boolean deleteById(String id) {
        return template.remove(query(where("id").is(id)), RSSChannel.class).getDeletedCount() > 0;
    }

    public RSSChannel add(RSSChannel channel) {
        if (channel.valid()) {
            template.save(channel);
            return channel;
        }
        return null;
    }
}
