<script setup lang="ts">
import {TeamType} from "../models/team";
import myAxios from "../plugins/myAxios.ts";
import {showConfirmDialog, showFailToast, showSuccessToast} from "vant";
import {BaseResponse} from "../models/response";
import {getCurrentUserInfo} from "../services/uesr.ts";
import {onMounted, ref} from "vue";
import {useRouter} from "vue-router";
import {UserType} from "../models/user";

interface UserCardListProps {
  teamList?: TeamType[];
}

const props = withDefaults(defineProps<UserCardListProps>(), {
  teamList: () => []
});

// 加入群聊逻辑
const joinGroupChat = async (teamId: number) => {
  try {
    await myAxios.post("/group/join", { teamId });
  } catch (error) {
    console.error("自动加入群聊失败", error);
  }
};

// 获取队长头像的函数
const getLeaderAvatar = (team: TeamType) => {
  const leader = team.userVOList?.find(user => user.id === team.leaderId);
  return leader?.avatarUrl || '/default-avatar.png';
};
const joinTeamInfo = ref<TeamType | null>(null);
const teamPassword = ref<string>('');
const show = ref(false);
// 修改加入队伍方法
const joinTeam = async (teamId: number) => {
  const res = await myAxios.post("/team/join", {
    teamId: teamId,
    teamPassword: teamPassword.value
  });
  if (res.code === 0) {
    // 加入队伍成功后自动加入群聊
    await joinGroupChat(teamId);
    showSuccessToast("加入成功！");
    emit("updateTeam");
  } else {
    showFailToast("加入失败: " + res.description);
  }
};

const deleteTeam = async (teamId: number) => {
  const res: BaseResponse = await myAxios.post('/team/delete', {
    teamId: teamId,
  })
  if (res.code === 0) {
    emit('updateTeam')
    showSuccessToast('解散成功！')
  } else {
    showFailToast('解散失败' + `,${res.description}`)
  }
}

const quitTeam = async (teamId: number) => {
  const res: BaseResponse = await myAxios.post('/team/quit', {
    teamId: teamId,
  })
  if (res.code === 0) {
    emit('updateTeam')
    showSuccessToast('退出成功！')
  } else {
    showFailToast('退出失败' + `,${res.description}`)
  }
}

const handleJoinTeam = async (team: TeamType) => {
  await showConfirmDialog({
    title: "加入队伍",
    message: "确定要加入该队伍吗？",
  })
      .then(() => {
        if (team.teamStatus !== 2) {
          joinTeam(team.id)
          return
        }
        joinTeamInfo.value = team
        show.value = true;
      })
      .catch(() => {
        // 用户点击取消
      });
};

const onConfirm = async (teamId: number) => {
  joinTeam(teamId)
}

const handleQuitTeam = async (teamId: number) => {
  await showConfirmDialog({
    title: "退出队伍",
    message: "确定要退出该队伍吗？",
    confirmButtonColor: "#ee0a24",
  })
      .then(() => quitTeam(teamId))
      .catch(() => {
      });
};

const handleDeleteTeam = async (teamId: number) => {
  await showConfirmDialog({
    title: "解散队伍",
    message: "确定要解散该队伍吗？此操作不可恢复！",
    confirmButtonText: "确认解散",
    confirmButtonColor: "#ee0a24",
  })
      .then(() => deleteTeam(teamId))
      .catch(() => {
      });
};

const currentUser = ref<UserType>();
onMounted(async () => {
  currentUser.value = await getCurrentUserInfo();
})

const router = useRouter()
const updateTeam = (teamId) => {
  router.push({
    path: '/team/update',
    query: {
      teamId: teamId
    }
  });
}

// 新增：判断用户是否已加入队伍
const isUserInTeam = (team: TeamType) => {
  if (!currentUser.value || !team.userVOList) return false;
  return team.userVOList.some(user => user.id === currentUser.value.id);
};
const emit = defineEmits(['updateTeam']);
</script>

<template>
  <van-card v-for="team in props.teamList"
            :desc="team.description"
            :title="team.teamName"
            :thumb="getLeaderAvatar(team)"
  >
    <template #tags>
      <van-tag style="margin: 1px 2px" v-if="team.teamStatus == 0" plain type="primary">公开</van-tag>
      <van-tag style="margin: 1px 2px" v-else-if="team.teamStatus == 1" plain type="danger">私有</van-tag>
      <van-tag style="margin: 1px 2px" v-else-if="team.teamStatus == 2" plain type="warning">加密</van-tag>
    </template>
    <template #bottom>
      <div>
        队伍人数：{{ team.currentNum }}/{{ team.maxNum }}
      </div>
      <div v-if="team.expireTime">
        过期时间：{{ team.expireTime }}
      </div>
    </template>
    <template #footer>
      <!-- 修改：加入条件判断 -->
      <van-button v-if="!isUserInTeam(team)" size="small" plain type="primary" @click="handleJoinTeam(team)">
        加入队伍
      </van-button>
      <van-button v-if="team.leaderId === currentUser?.id || currentUser?.identity === 0|| currentUser?.identity === 0" size="small" plain type="primary"
                  @click="updateTeam(team.id)">
        更新队伍
      </van-button>
      <van-button v-if="team.leaderId === currentUser?.id" size="small" plain type="warning"
                  @click="handleQuitTeam(team.id)">
        退出队伍
      </van-button>
      <van-button v-if="team.leaderId === currentUser?.id || currentUser?.identity === 0" size="small" plain type="danger"
                  @click="handleDeleteTeam(team.id)">
        解散队伍
      </van-button>
    </template>
  </van-card>
  <van-dialog v-model:show="show" title="请输入队伍密码" show-cancel-button @confirm="onConfirm(joinTeamInfo.id)" @cancel="teamPassword = ''">
    <van-field
        v-model="teamPassword"
        type="password"
        name="password"
        label="队伍密码"
        placeholder="请输入队伍密码"
        :rules="[{ required: true, message: '请输入队伍密码' }]"
    />
  </van-dialog>
</template>

<style scoped>

</style>