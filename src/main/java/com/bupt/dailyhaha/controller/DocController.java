package com.bupt.dailyhaha.controller;

import com.bupt.dailyhaha.anno.AuthRequired;
import com.bupt.dailyhaha.pojo.ResultData;
import com.bupt.dailyhaha.pojo.doc.Document;
import com.bupt.dailyhaha.service.DocService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/doc")
@CrossOrigin(origins = "*")
public class DocController {

    final DocService docService;


    public DocController(DocService docService) {
        this.docService = docService;
    }

    @GetMapping("")
    public ResultData<List<Document>> getDocs() {
        return ResultData.success(docService.getDocs());
    }

    @GetMapping("/{id}")
    public ResultData<Document> getDoc(@PathVariable String id) {
        return ResultData.success(docService.getDoc(id));
    }

    @AuthRequired
    @PostMapping("/create")
    public ResultData<Document> upsert(@RequestBody Document doc) {
        return ResultData.success(docService.create(doc));
    }

    @PostMapping("/update")
    @AuthRequired
    public ResultData<Document> update(@RequestBody Document doc) {
        return ResultData.success(docService.update(doc));
    }

}
