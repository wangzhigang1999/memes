package com.bupt.dailyhaha.service.impl;

import com.bupt.dailyhaha.mapper.MSubmission;
import com.bupt.dailyhaha.pojo.common.PageResult;
import com.bupt.dailyhaha.pojo.media.Submission;
import com.bupt.dailyhaha.service.IStorage;
import com.bupt.dailyhaha.service.ISubmission;
import com.bupt.dailyhaha.util.Utils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
@AllArgsConstructor
public class SubmissionServiceImpl implements ISubmission {

    final static ConcurrentHashMap<Integer, Submission> cache = new ConcurrentHashMap<>();
    final MSubmission mSubmission;
    final IStorage IStorage;

    /**
     * 对投稿点赞/点踩
     *
     * @param hashcode 对应投稿的id
     * @param up       true为点赞，false为点踩
     * @return 是否成功
     */
    @Override
    public boolean vote(int hashcode, boolean up) {
        return mSubmission.vote(hashcode, up);
    }


    /**
     * 存储文本类型的投稿
     *
     * @param uri  url  text
     * @param mime mime
     * @return 存储后的投稿
     */
    @Override
    public Submission storeTextFormatSubmission(String uri, String mime) {

        // check if the submission already exists
        var submission = mSubmission.findByHash(uri.hashCode());
        if (submission != null) {
            return submission;
        }

        submission = new Submission();
        submission.setSubmissionType(mime);
        submission.setName(uri);
        submission.setUrl(uri);
        submission.setHash(uri.hashCode());
        submission.setDate(Utils.getYMD());

        return insertSubmission(submission);
    }

    /**
     * 存储二进制类型的投稿
     *
     * @param stream   输入流
     * @param mime     mime
     * @param personal 是否私有
     * @return 存储后的投稿
     */
    @Override
    public Submission storeStreamSubmission(InputStream stream, String mime, boolean personal) {
        byte[] bytes = Utils.readAllBytes(stream);
        if (bytes == null) {
            return null;
        }
        int code = Arrays.hashCode(bytes);
        if (cache.containsKey(code)) {
            return cache.get(code);
        }

        // 为什么需要这个？因为Pod会重启，重启之后会丢失缓存的信息，所以需要从数据库中查询
        // 也可以使用redis来做缓存，但是这个项目没有必要
        Submission submission = mSubmission.findByHash(code);
        if (submission != null) {
            cache.put(code, submission);
            return submission;
        }

        submission = IStorage.store(bytes, mime);
        if (submission == null) {
            return null;
        }
        submission.setHash(code);
        submission.setDate(Utils.getYMD());

        // 当做图床用的时候，不入库
        if (personal) {
            return submission;
        }

        cache.put(code, insertSubmission(submission));
        return submission;
    }

    /**
     * 获取被标记为删除的投稿
     */
    @Override
    public List<Submission> getDeletedSubmission() {
        return mSubmission.find(true, null);
    }

    /**
     * 硬删除
     */
    @Override
    public void hardDeleteSubmission(int hashcode) {
        assert mSubmission.hardDeleteByHash(hashcode);
    }

    /**
     * 分页查询
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @param lastID   上一页最后一个元素的id
     * @return 分页结果
     */
    @Override
    public PageResult<Submission> getSubmissionByPage(int pageNum, int pageSize, String lastID) {
        return mSubmission.find(pageNum, pageSize, lastID);
    }


    /**
     * 插入投稿
     *
     * @param submission 投稿
     * @return 插入后的投稿
     */
    private Submission insertSubmission(Submission submission) {
        return mSubmission.create(submission);
    }
}
