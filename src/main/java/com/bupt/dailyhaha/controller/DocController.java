package com.bupt.dailyhaha.controller;

import com.bupt.dailyhaha.pojo.common.PageResult;
import com.bupt.dailyhaha.pojo.common.ResultData;
import com.bupt.dailyhaha.pojo.doc.Document;
import com.bupt.dailyhaha.service.DocService;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/doc")
@CrossOrigin(origins = "*")
public class DocController {

    final DocService docService;


    public DocController(DocService docService) {
        this.docService = docService;
    }


    @GetMapping("/page")
    public ResultData<PageResult<Document>> getDocs(@RequestParam String lastID, @RequestParam Integer pageSize, @RequestParam Integer pageNum) {
        return ResultData.success(docService.getDocs(lastID, pageSize, pageNum,false));
    }

    @GetMapping("/{id}")
    public ResultData<Document> getDoc(@PathVariable String id) {
        return ResultData.success(docService.getDoc(id));
    }




}
