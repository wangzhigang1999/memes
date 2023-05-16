package com.bupt.dailyhaha.controller.doc;

import com.bupt.dailyhaha.anno.AuthRequired;
import com.bupt.dailyhaha.pojo.common.PageResult;
import com.bupt.dailyhaha.pojo.common.ResultData;
import com.bupt.dailyhaha.pojo.doc.Document;
import com.bupt.dailyhaha.service.Interface.Doc;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/doc")
@CrossOrigin(origins = "*")
public class DocAdmin {

    final Doc doc;

    public DocAdmin(Doc doc) {
        this.doc = doc;
    }

    @AuthRequired
    @PostMapping("/create")
    public ResultData<Document> upsert(@RequestBody Document doc) {
        return ResultData.success(this.doc.create(doc));
    }

    @PostMapping("/update")
    @AuthRequired
    public ResultData<Document> update(@RequestBody Document doc) {
        return ResultData.success(this.doc.update(doc));
    }

    @PostMapping("/delete")
    @AuthRequired
    public ResultData<Boolean> delete(String docID) {
        return ResultData.success(doc.delete(docID));
    }


    @PostMapping("/private")
    @AuthRequired
    public ResultData<Boolean> setPrivate(String docID, boolean isPrivate) {
        return ResultData.success(doc.setPrivate(docID, isPrivate));
    }

    @GetMapping("/page")
    @AuthRequired
    public ResultData<PageResult<Document>> getPrivateDocs(@RequestParam String lastID, @RequestParam Integer pageSize, @RequestParam Integer pageNum) {
        return ResultData.success(doc.getDocs(lastID, pageSize, pageNum, true));
    }
}
