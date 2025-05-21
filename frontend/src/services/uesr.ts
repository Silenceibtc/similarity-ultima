import myAxios from "../plugins/myAxios.ts";
import {getCurrentUser, setCurrentUser} from "../global/UserState.ts"
import {BaseResponse} from "../models/response";

export const getCurrentUserInfo = async () => {
    const currentUser = getCurrentUser();
    if (currentUser) {
        const res: BaseResponse = await myAxios.get("/user/current");
        if (res.code === 0) {
            setCurrentUser(res.data);
            return res.data;
        }
        return null;
    }
    return currentUser;
}

/**
 * 上传用户头像
 * @param file 头像文件
 */
export const uploadAvatar = async (file: File) => {
    const formData = new FormData();
    formData.append('file', file);
    const res = await myAxios.post<{ url: string }>('/user/upload', formData, {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    });
    return res.data ? res.data : '';
};