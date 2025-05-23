// src/models/domain/FriendRequest.ts
export interface FriendRequest {
    id: number;
    senderId: number;
    receiverId: number;
    status: number;
    remark: string;
    createTime: string;
    senderUsername?: string; // 扩展字段，需后端返回
    senderAvatarUrl?: string; // 扩展字段，需后端返回
}