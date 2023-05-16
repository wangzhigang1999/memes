package com.bupt.dailyhaha.controller.doc;

import com.bupt.dailyhaha.pojo.common.PageResult;
import com.bupt.dailyhaha.pojo.common.ResultData;
import com.bupt.dailyhaha.pojo.doc.Document;
import com.bupt.dailyhaha.service.Interface.Doc;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/doc")
@CrossOrigin(origins = "*")
public class DocUser {
    final Doc doc;


    public DocUser(Doc doc) {
        this.doc = doc;
    }


    /**
     * 获取文档，分页查询
     *
     * @param lastID   最后一个文档的ID
     * @param pageSize 每页的大小
     * @param pageNum  页码
     * @return ResultData
     */
    @GetMapping("/page")
    public ResultData<PageResult<Document>> getDocs(@RequestParam String lastID, @RequestParam Integer pageSize, @RequestParam Integer pageNum) {
        return ResultData.success(doc.getDocs(lastID, pageSize, pageNum, false));
    }

    /**
     * 获取指定的文档
     *
     * @param id 文档ID
     * @return ResultData
     */
    @GetMapping("/{id}")
    public ResultData<Document> getDoc(@PathVariable String id) {
        return ResultData.success(doc.getDoc(id));
    }
}
