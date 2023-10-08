package com.bupt.dailyhaha.controller.rss;

import com.bupt.dailyhaha.pojo.RSSItem;
import com.bupt.dailyhaha.service.RSSItemService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/post")
@CrossOrigin(origins = "*")
public class RSS {
    final RSSItemService service;

    public RSS(RSSItemService service) {
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
}
