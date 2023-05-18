# memes

专为北邮人设计的贴图秀投稿系统.

![image-20230405181022055](https://wanz-bucket.oss-cn-beijing.aliyuncs.com/typora/image-20230405181022055.png)

![Untitled-1](https://wanz-bucket.oss-cn-beijing.aliyuncs.com/typora/Untitled-1.jpeg)

## 如何使用

> 直接 Ctrl+V 上传剪切板的图片或视频

 针对于复制的图片，直接 Ctrl+V 然后点击上传即可

> 从本地选择一张图片/视频

 针对本地的图片和视频，选择上传即可

> 当日的投稿会逆序，越靠前的是越新的投稿

> 历史投稿按照投稿的时间顺序排列

## 架构

![image-20230408225703781](https://wanz-bucket.oss-cn-beijing.aliyuncs.com/typora/image-20230408225703781.png)

> 技术一览

|                  |       用途        |
| :--------------: | :---------------: |
|    Cloudflare    |        CDN        |
|      七牛云      |        OSS        |
|     Cron Job     |     定时爬虫      |
|      Redis       |  爬虫进度，去重   |
|     Angular      |     前端实现      |
|      Tauri       | window 客户端实现 |
|     MongoDB      |     后端存储      |
|     InfluxDB     |   监控数据存储    |
|     Grafana      |     监控看板      |
|     RobotAlp     |     健康检查      |
|    **Render**    |   **后端部署**    |
|   **Netlify**    |   **前端部署**    |
| **Azure Devops** | **Cron Job 部署** |


