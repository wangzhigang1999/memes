package com.memes.service;

import com.memes.model.common.PageResult;
import io.micrometer.common.util.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * MongoDB 分页查询工具类.
 *
 * @author wanz
 **/
@Component
@SuppressWarnings("null")
public class MongoPageHelper {

    public static final String ID = "_id";
    private final MongoTemplate mongoTemplate;

    @Autowired
    public MongoPageHelper(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * 分页查询，直接返回集合类型的结果.
     */
    public <T> PageResult<T> pageQuery(Query query, Class<T> entityClass, Integer pageSize, String lastId) {
        return pageQuery(query, entityClass, Function.identity(), pageSize, lastId);
    }

    /**
     * 分页查询.
     *
     * @param query
     *            Mongo Query 对象，构造你自己的查询条件.
     * @param entityClass
     *            Mongo collection 定义的 entity class，用来确定查询哪个集合.
     * @param mapper
     *            映射器，你从 db 查出来的 list 的元素类型是 entityClass,
     *            如果你想要转换成另一个对象，比如去掉敏感字段等，可以使用 mapper 来决定如何转换.
     * @param pageSize
     *            分页的大小.
     * @param <T>
     *            collection 定义的 class 类型.
     * @param <R>
     *            最终返回时，展现给页面时的一条记录的类型。
     * @return PageResult, 一个封装 page 信息的对象.
     */
    public <T, R> PageResult<R> pageQuery(Query query, Class<T> entityClass, Function<T, R> mapper, Integer pageSize,
            String lastId) {
        if (StringUtils.isNotBlank(lastId)) {
            query.addCriteria(Criteria.where(ID).lt(new ObjectId(lastId)));
        }
        query.with(Sort.by(Sort.Direction.DESC, ID)).limit(pageSize);
        List<T> entityList = mongoTemplate.find(query, entityClass);
        PageResult<R> pageResult = new PageResult<>();
        pageResult.setPageSize(pageSize);
        pageResult.setList(entityList.stream().map(mapper).collect(Collectors.toList()));
        return pageResult;
    }

}
