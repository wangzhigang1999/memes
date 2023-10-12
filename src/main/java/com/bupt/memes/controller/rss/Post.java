package com.bupt.memes.controller.rss;

import com.bupt.memes.model.RSSItem;
import com.bupt.memes.service.impl.rss.RSSItemService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/post")
@CrossOrigin(origins = "*")
public class Post {
    final RSSItemService service;

    public Post(RSSItemService service) {
        this.service = service;
    }

    @GetMapping("/author/{author}/board/{board}")
    public List<RSSItem> getByAuthorAndBoard(@PathVariable String author, @PathVariable String board) {
        return service.getByAuthorAndBoard(author, board);
    }

    @GetMapping("/author/{author}")
    public List<RSSItem> getByAuthor(@PathVariable String author) {
        return service.getByAuthor(author);
    }

    @GetMapping("/board/{board}")
    public List<RSSItem> getByBoard(@PathVariable String board) {
        return service.getByBoard(board);
    }

    @GetMapping("/keyword/{keyword}")
    public List<RSSItem> getByKeyword(@PathVariable String keyword) {
        return service.getByKeyword(keyword);
    }

    @GetMapping("/latest/{limit}")
    public List<RSSItem> getLatest(@PathVariable Integer limit) {
        return service.getLatest(limit);
    }
}
