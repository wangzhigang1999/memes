package com.memes.controller.rss;

import com.memes.model.common.PageResult;
import com.memes.model.param.ListRSSItemRequest;
import com.memes.model.rss.RSSItem;
import com.memes.service.rss.RSSItemService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/post")
@CrossOrigin(origins = "*")
public class RSSItemController {
    final RSSItemService service;

    public RSSItemController(RSSItemService service) {
        this.service = service;
    }

    @GetMapping("")
    public PageResult<RSSItem> listRSSItem(ListRSSItemRequest request) {
        return service.listRSSItem(request);
    }
}
