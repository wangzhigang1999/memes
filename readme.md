# memes

专为北邮人设计的贴图秀投稿系统.

![image-20230303204657603](https://wanz-bucket.oss-cn-beijing.aliyuncs.com/typora/image-20230303204657603.png)

## 如何使用

如果勾选了  **同时投稿**，那么可能在凌晨被投稿至论坛的合集中，不勾选则上传信息不会存储到数据库中.

> 直接 Ctrl+V 上传剪切板的图片

 针对于复制的图片，直接 Ctrl+V 然后点击上传即可

> 从本地选择一张图片

 针对本地的图片，选择上传

> 点击上传之后，对应的 **markdown** 链接会自动的复制到剪切板

 如果没有自动复制，手动复制即可

Tips ： **~~悄悄话版面也能发图~~**

![image-20230301205401299](https://wanz-bucket.oss-cn-beijing.aliyuncs.com/typora/image-20230301205401299.png)

## 如何运行

无论通过哪种方式运行，配置信息都需要通过**环境变量**的形式传入。所需的配置项包括：

|   名称    |                        含义                         |     必须？      |
| :-------: | :-------------------------------------------------: | :-------------: |
|    env    |             当前是什么环境，dev/prod/……             |        👎        |
|  storage  |              选用哪种存储，qiniu/local              |        👍        |
|    ak     |                     七牛云的ak                      | storage=qiniu时 |
|    sk     |                     七牛云的sk                      | storage=qiniu时 |
|  bucket   |                七牛云中对应的bucket                 | storage=qiniu时 |
| mongoUri  |                    mongodb 的uri                    |        👍        |
| urlPrefix | urlPrefix+（storage中的地址）= 用户可访问的真实地址 |        👍        |
|   token   |                用于一些小功能的鉴权                 |        👎        |

### Docker







## FAQ

> 为什么我的客户端看不到图片?

 可以考虑更新app的版本: https://github.com/BYR-App-Dev/BYR_App_Android_Release

> 为什么上传的图片看起来有点糊

 前端对要上传的图片进行了压缩,因此分辨率会降低
