package com.bupt.dailyhaha.pojo.doc;


import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "bbs_record")
public class BBSRecord {

    public BBSRecord(Post post) {
        this.post = post;
        this.articleLink = post.getArticleLink();
        this.createdTime = System.currentTimeMillis();
        this.status = Status.Created;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Post {
        private int id;
        private int group_id;
        private int reply_id;
        private String flag;
        private String position;
        private boolean is_top;
        private boolean is_subject;
        private boolean has_attachment;
        private boolean is_admin;
        private String title;
        private Map<String, Object> user;
        private int post_time;
        private String board_name;
        private String board_description;
        private int previous_id;
        private int next_id;
        private int threads_previous_id;
        private int threads_next_id;
        private boolean allow_post;
        private boolean is_bm;
        private String content;
        private int ajax_st;
        private String ajax_code;
        private String ajax_msg;

        public String getArticleLink() {
            return "https://bbs.byr.cn/#!article/%s/%s".formatted(board_name, group_id);
        }
    }


    public enum Status {
        Created, Running, Success, Failed
    }

    String id;


    Post post;

    long createdTime;
    long firstUpdatedTime;

    long lastUpdatedTime;

    String articleLink;

    Status status;

    public static void main(String[] args) {
        String s = "{\"id\":174199,\"group_id\":173829,\"reply_id\":173829,\"flag\":\"\",\"position\":\"174199\",\"is_top\":false,\"is_subject\":false,\"has_attachment\":false,\"is_admin\":false,\"title\":\"Re: 西土城校区公路车停哪里？\",\"user\":{\"id\":\"wangzhigang\",\"user_name\":\"幸运的Ubuntu\",\"face_url\":\"https://bbs.byr.cn/uploadFace/W/wangzhigang.3433.jpg\",\"face_width\":120,\"face_height\":97,\"gender\":\"m\",\"astro\":\"处女座\",\"life\":365,\"qq\":\"\",\"msn\":\"\",\"home_page\":\"\",\"level\":\"用户\",\"is_online\":true,\"post_count\":610,\"last_login_time\":1683797437,\"last_login_ip\":\"10.128.246.*\",\"is_hide\":false,\"is_register\":true,\"score\":21145,\"follow_num\":0,\"fans_num\":0,\"is_follow\":false,\"is_fan\":false},\"post_time\":1683797473,\"board_name\":\"Cycling\",\"board_description\":\"梦想单车\",\"previous_id\":174198,\"next_id\":0,\"threads_previous_id\":173845,\"threads_next_id\":null,\"allow_post\":true,\"is_bm\":false,\"content\":\"发信人: wangzhigang (幸运的Ubuntu), 信区: Cycling<br />标&nbsp;&nbsp;题: Re: 西土城校区公路车停哪里？<br />发信站: 北邮人论坛 (Thu May 11 17:31:13 2023), 站内<br /><br />@hahaMonster memit<br />--<br /><br /><font class=\\\"f000\\\"></font><font class=\\\"f002\\\">※ 来源:·<a target=\\\"_blank\\\" href=\\\"http://developers.byr.cn/mobile\\\">北邮人论坛手机客户端</a> bbs.byr.cn·[FROM: 10.128.246.*]</font><font class=\\\"f000\\\"><br /></font>\",\"ajax_st\":1,\"ajax_code\":\"0005\",\"ajax_msg\":\"操作成功\"}\n";

        Post post1 = new Gson().fromJson(s, Post.class);

        System.out.println(post1);


    }


}



