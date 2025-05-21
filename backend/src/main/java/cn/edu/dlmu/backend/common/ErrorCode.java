package cn.edu.dlmu.backend.common;

import lombok.Getter;

/**
 * 错误码
 *
 * @author silenceibtc
 */
@Getter
public enum ErrorCode {
    PARAMS_ERROR(4000, "请求参数错误", ""),
    NULL_ERROR(4001, "返回数据为空", ""),
    DB_ERROR(4002, "数据库异常", ""),
    NOT_LOGIN(4010, "未登录", ""),
    NO_AUTH(4011, "无权限", ""),
    SYSTEM_ERROR(5000, "系统内部异常", "");

    /**
     * 错误码
     */
    private final int code;

    /**
     * 错误信息
     */
    private final String message;

    /**
     * 具体描述
     */
    private final String description;

    ErrorCode(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

}
