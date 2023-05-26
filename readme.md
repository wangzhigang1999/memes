# memes

为了方便使用，将前端和数据库都集成了进来。

数据库使用 **sqlite**，启动时会自动的初始化表结构。

为了提升查询的速度，可以**手动的**创建索引。

```sql
create index date on submission (date);

create index date_del_rev on submission (timestamp, deleted, reviewed);

create index id on submission (id);
```

