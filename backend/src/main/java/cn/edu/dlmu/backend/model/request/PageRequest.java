package cn.edu.dlmu.backend.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用分页请求参数
 */
@Data
public class PageRequest implements Serializable {

    /**
     * 当前页号
     */
    private int pageNum;

    /**
     * 页大小
     */
    private int pageSize;

}
