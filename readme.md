# memes

专为北邮人设计的贴图秀投稿系统.

![show](https://wanz-bucket.oss-cn-beijing.aliyuncs.com/typora/show.jpg)

![show](https://wanz-bucket.oss-cn-beijing.aliyuncs.com/typora/image-20230807234030216.png)

## 架构

![arch](https://wanz-bucket.oss-cn-beijing.aliyuncs.com/typora/arch.png)

前端使用 **Angular + Tailwind CSS** 实现,部署在 **Netlify** 上.

静态资源(图片、视频)存储在**七牛云**,同时使用 **Cloudflare** 代理所有的流量，作为 CDN 使用,减少流量的消耗.

爬虫使用 **Python** 实现,利用 **Azure DevOps Pipeline** 定时执行,爬取的图片、视频存储在 OSS 中,元信息通过后端 API 存储在 **MongoDB** 中, **Redis** 会保存爬虫的 checkpoint,防止重复爬取.

CronJob 的部署方式和爬虫类似,负责在北邮人论坛自动发帖.

后端使用 **Spring Boot** 实现,部署在 **Render** 上,使用 **MongoDB** 作为数据库. 后端会主动向 **InfluxDB** 发送 Metric 数据,用于监控,同时使用 **Grafana** 展示监控数据.**Robotalp** 会主动探测后端的健康状态,如果后端不可用,Robotalp 会发送邮件通知.

## 如何使用

> 直接 Ctrl+V 上传剪切板的图片或视频

 针对于复制的图片，直接 Ctrl+V 然后点击上传即可

> 从本地选择一张图片/视频

 针对本地的图片和视频，选择上传即可

## FAQ

> 为什么我的客户端看不到图片?

 可以考虑更新app的版本: https://github.com/BYR-App-Dev/BYR_App_Android_Release

> 为什么上传的图片看起来有点糊

 前端对要上传的图片进行了压缩,因此分辨率会降低
