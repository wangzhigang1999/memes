package com.bupt.memes.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 分页结果.
 *
 * @author wanz
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class PageResult<T> {

    private Integer pageNum;

    private Integer pageSize;

    private Long total;

    private Integer pages;

    private List<T> list;

}

