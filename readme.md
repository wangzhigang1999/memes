# memes

本来是找乐子的项目，后来发现还有点意思。

遂抽象了一些东西，做了一个简单的框架，来适配不同的数据库和文件存储。

同时直接集成了前端来测试。

## 如何启动

直接`clone framework`分支,默认情况下直接`run SpringBoot` 项目应该是可以成功启动且不报任何错误的。

<img src="https://wanz-bucket.oss-cn-beijing.aliyuncs.com/typora/image-20230608214657391.png" alt="image-20230608214657391" style="zoom: 50%;" />



访问 `localhost:8080` 应该会看到下面的内容，说明成功启动！

<img src="https://wanz-bucket.oss-cn-beijing.aliyuncs.com/typora/image-20230608214826413.png" alt="image-20230608214826413" style="zoom:50%;" />

现在你可以发挥自己的脑洞，修改一些东西~

## 如何开发

### 文件结构

![image-20230608224643400](https://wanz-bucket.oss-cn-beijing.aliyuncs.com/typora/image-20230608224643400.png)

### mapper 包详解

│ ├── MHistoryImpl.java				
**│ ├── MHistory.java 		// 与历史投稿相关的接口**
│ ├── MLogImpl.java
**│ ├── MLog.java 			// 日志、审计、统计相关的接口**
│ ├── MSubmissionImpl.java
**│ ├── MSubmission.java 	 // 与投稿相关的接口**
│ ├── MSysImpl.java
**│ └── MSys.java 			// 与系统配置相关的接口**

可以看到，在Mapper包中定义了一些==接口==，每个接口对应了一个==默认的实现==。

上层的业务逻辑会依赖这些接口；因此，你需要根据自己选择的数据库来实现这些接口定义的行为逻辑。

举个例子，在下边的 `MHistory` 接口中存在一个方法 `findByDate`，如果你选择了`Redis` 作为存储，

```java
/**
 * History 的 Mapper
 */
public interface MHistory {
    /**
     * 查找某一天的记录
     * 当给出某天的日期时，返回该天的记录
     *
     * @param date 日期 YYYY——MM——DD
     * @return History
     */
    History findByDate(String date);
}
```

那么你的实现可能是这样的：

```java
@Service
public class MHistoryRedisImpl implements MHistory {
    
    @Autowired
    RedisTemplate redisTemplate;
    /**
     * 查找某一天的记录
     * 当给出某天的日期时，返回该天的记录
     *
     * @param date 日期 YYYY——MM——DD
     * @return History
     */
    @Override
    public History findByDate(String date) {
        return (History) redisTemplate.opsForValue().get(date);
    }
}
```

可以根据自己的想法尝试一些稀奇古怪的存储方案。

==**所有的接口中的所有方法都需要自行实现！**==

==**所有的接口中的所有方法都需要自行实现！**==

==**所有的接口中的所有方法都需要自行实现！**==

### service 包详解

├── service										
│ ├── impl									
│ │ ├── HistoryServiceImpl.java 		// 默认的历史业务逻辑实现，**不需要改动**
│ │ ├── IReviewServiceImpl.java 		// 默认的审核业务逻辑实现，**不需要改动**
│ │ ├── LocalIStorageImpl.java 		// 一个默认的存储实现
│ │ ├── SubmissionServiceImpl.java 	// 默认的投稿业务逻辑实现，**不需要改动**
│ │ └── release								
│ │ ├── CustomReleaseStrategy.java 	// 一个预先实现的发布策略
│ │ └── DefaultReleaseStrategy.java 	// 一个预先实现的发布策略
│ │
│ ├── IHistory.java 				// 历史相关的业务接口		
│ ├── IReleaseStrategy.java 		// 发布策略的接口
│ ├── IReview.java 				// 审核相关业务的接口
**│ ├── IStorage.java 				// 存储相关业务的接口**， **有可能需要改动**
│ ├── ISubmission.java 			// 投稿相关的业务接口
│ │
│ ├── StatisticService.java 		// 逻辑过于简单，未抽象出接口 统计相关的业务逻辑
│ └── SysConfigService.java 		// 逻辑过于简单，未抽象出接口 系统配置相关的业务逻辑

可以看到，service 包主要分为了一些==接口和对应的实现==，这些接口规定了业务需要做什么，而实现则定义了怎么做。

在这一部分，只有 `IStorage`这个接口是你==有可能需要==自行实现的，其余的接口的默认实现已经经过了一段时间的测试，可以稳定的运行。

你可以直接修改 `LocalIStorageImpl` 中的实现，或者在新的类中实现 `IStorage` 这个接口。



==**这一部分你可以选择保留默认的实现**==

## 如何启动

直接`clone framework`分支,默认情况下直接`run` SpringBoot 项目应该是可以成功启动且不报任何错误的。

<img src="https://wanz-bucket.oss-cn-beijing.aliyuncs.com/typora/image-20230608214657391.png" alt="image-20230608214657391" style="zoom: 50%;" />



访问 `localhost:8080` 应该会看到下面的内容，说明成功启动！

<img src="https://wanz-bucket.oss-cn-beijing.aliyuncs.com/typora/image-20230608214826413.png" alt="image-20230608214826413" style="zoom:50%;" />

## 如何开发

