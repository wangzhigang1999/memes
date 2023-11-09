package com.bupt.memes.controller.rss;

import com.bupt.memes.anno.AuthRequired;
import com.bupt.memes.model.rss.RSSChannel;
import com.bupt.memes.service.impl.rss.RSSChannelService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/channel")
@CrossOrigin(origins = "*")
public class Channel {

    final RSSChannelService service;

    public Channel(RSSChannelService service) {
        this.service = service;
    }

    // list all channels
    @GetMapping("")
    @AuthRequired
    List<RSSChannel> list() {
        return service.list();
    }

    // list by keyword
    @RequestMapping("/keyword/{keyword}")
    @AuthRequired
    List<RSSChannel> listByKeyword(@PathVariable String keyword) {
        return service.listByKeyword(keyword);
    }

    // delete by id restful api
    @DeleteMapping("/{id}")
    @AuthRequired
    boolean deleteById(@PathVariable String id) {
        return service.deleteById(id);
    }

    // add
    @PostMapping("/{title}")
    @AuthRequired
    RSSChannel add(@RequestBody RSSChannel channel) {
        return service.add(channel);
    }

}
