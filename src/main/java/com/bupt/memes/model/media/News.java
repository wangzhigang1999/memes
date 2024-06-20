package com.bupt.memes.model.media;

import com.google.gson.Gson;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import static com.bupt.memes.util.TimeUtil.getYMD;

@Document(collection = "news")
@Data
public class News {
	private String id;
	private long timestamp;

	private String date;

	private String coverImage;
	private String title;

	private String introduction;
	private String content;
	private String sourceURL;
	private String author;
	private Set<String> tag;
	private int like;
	private int dislike;
	private boolean deleted;

	public boolean validate() {
		return title != null && content != null && sourceURL != null;
	}

	public static News fromJson(String data) {
		return new Gson().fromJson(data, News.class);
	}

	public static News randomNews() {
		Random random = new Random();
		HashSet<String> tags = new HashSet<>();
		for (int i = 0; i < random.nextInt(10); i++) {
			tags.add(i + "");
		}
		News news = new News();
		news.setAuthor("auto");
		news.setContent(UUID.randomUUID().toString());
		news.setCoverImage("https://bbs.bupt.site/shadiao/1688628801659-98ede151-98ba-44b2-b198-9e5bff028350.jpeg");
		news.setDate(getYMD());
		news.setDislike(random.nextInt(100));
		news.setLike(random.nextInt(100));
		news.setSourceURL(UUID.randomUUID().toString());
		news.setTag(tags);
		news.setTimestamp(System.currentTimeMillis());
		news.setTitle(UUID.randomUUID().toString());
		return news;
	}

	public static void main(String[] args) {
		News news = News.randomNews();
		String str = new Gson().toJson(news);
		News fakeNews = News.fromJson(str);
		assert fakeNews.equals(news);
	}

}
