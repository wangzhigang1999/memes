package com.bupt.dailyhaha.mapper;

import com.bupt.dailyhaha.pojo.common.PageResult;
import com.bupt.dailyhaha.pojo.media.Submission;

import java.util.List;

public interface MSubmission {

    /**
     * 创建一个新的投稿，需要保证ID的自增，否则分页查询会出现问题
     *
     * @param submission 投稿
     * @return 投稿
     */
    Submission create(Submission submission);

    /**
     * 根据hash查找投稿
     *
     * @param hash hash，可以唯一的标识一个投稿
     * @return 投稿
     */
    Submission findByHash(Integer hash);

    /**
     * 根据hash查找投稿
     *
     * @param deleted  是否已删除
     * @param reviewed 是否已审核
     * @return 投稿列表
     */
    List<Submission> find(Boolean deleted, Boolean reviewed);

    /**
     * 根据hash查找投稿
     *
     * @param from     开始时间
     * @param to       结束时间
     * @param deleted  是否已删除
     * @param reviewed 是否已审核
     * @return 投稿列表
     */
    List<Submission> find(long from, long to, Boolean deleted, Boolean reviewed);

    /**
     * 统计投稿数量
     *
     * @param from     开始时间
     * @param to       结束时间
     * @param deleted  是否已删除
     * @param reviewed 是否已审核
     * @return 投稿数量
     */
    Long count(long from, long to, Boolean deleted, Boolean reviewed);

    /**
     * 分页查询投稿
     * <p>
     * 实现分页查询有很多的方式,例如通过offset和limit来实现分页查询，但是这种方式在数据量大的时候会有性能问题
     * <p>
     * 由于我们的投稿的ID是自增的，所以我们可以通过上一页最后一个投稿的ID来实现分页查询，当ID不存在时，认为是第一页
     * </p>
     *
     * @param pageNum  页码
     * @param pageSize 页大小
     * @param lastID   上一页最后一个投稿的ID
     * @return 投稿列表
     */
    PageResult<Submission> find(int pageNum, int pageSize, String lastID);

    /**
     * 更新投稿状态，注意，在更新删除状态的时候，应该把 <strong> reviewed <strong/> 字段设置成true
     *
     * @param hashcode hashcode
     * @param deleted  是否已删除
     * @return 更新成功的数量
     */
    Long updateStatus(int hashcode, boolean deleted);


    /**
     * 点赞/点踩
     *
     * @param hashcode hashcode
     * @param up       是否赞同
     * @return 是否成功
     */
    boolean vote(int hashcode, boolean up);

    /**
     * 根据hash删除投稿，物理删除
     *
     * @param hash hash
     * @return 是否成功
     */
    boolean hardDeleteByHash(Integer hash);

}
