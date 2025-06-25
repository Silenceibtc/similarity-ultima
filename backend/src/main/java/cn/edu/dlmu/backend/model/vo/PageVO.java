package cn.edu.dlmu.backend.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class PageVO<T> {

    private Long total;

    private T records;
}
