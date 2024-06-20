package com.bupt.memes.model.rss;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "rss_channel")
public class RSSChannel {

	private String id;
	private String title;
	private String description;
	private String link;
	private String rssLink;
	private String language;
	private String generator;
	private String lastBuildDate;

	public boolean valid() {
		return title != null && !title.isEmpty() &&
				description != null && !description.isEmpty() &&
				link != null && !link.isEmpty() &&
				rssLink != null && !rssLink.isEmpty();
	}
}
