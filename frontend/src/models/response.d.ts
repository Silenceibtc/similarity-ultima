/**
 * 响应类型
 */
export type BaseResponse = {
    code: number;
    data: any;
    message: string;
    description: string;
}