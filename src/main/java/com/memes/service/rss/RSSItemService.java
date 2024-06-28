package com.memes.service.rss;

import com.memes.config.AppConfig;
import com.memes.exception.AppException;
import com.memes.model.common.PageResult;
import com.memes.model.param.ListRSSItemRequest;
import com.memes.model.rss.RSSItem;
import com.memes.service.MongoPageHelper;
import com.memes.util.Preconditions;
import io.micrometer.common.util.StringUtils;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
public class RSSItemService {
    final MongoPageHelper template;

    AppConfig appConfig;

    private final String[] ignoreFieldsArray = { "description", "comments", "guid" };

    public PageResult<RSSItem> listRSSItem(ListRSSItemRequest request) {
        Preconditions.checkArgument(request.getPageSize() > 0, AppException.invalidParam("limit"));

        Criteria criteria = new Criteria();
        List<Criteria> conditions = new ArrayList<>();

        if (StringUtils.isNotBlank(request.getAuthor())) {
            conditions.add(Criteria.where("author").is(request.getAuthor()));
        }
        if (StringUtils.isNotBlank(request.getBoard())) {
            conditions.add(Criteria.where("board").is(request.getBoard()));
        }
        if (StringUtils.isNotBlank(request.getKeyword())) {
            conditions.add(Criteria.where("title").regex(request.getKeyword()));
        }

        Query query = new Query();
        if (!conditions.isEmpty()) {
            var orOperator = criteria.orOperator(conditions.toArray(new Criteria[0]));
            query.addCriteria(new Criteria().andOperator(orOperator));
        }

        query.fields().exclude(ignoreFieldsArray);
        query.with(Sort.by(Sort.Direction.DESC, "pubDate"));
        return template.pageQuery(query, RSSItem.class, request.getPageSize(), request.getLastID());
    }
}
