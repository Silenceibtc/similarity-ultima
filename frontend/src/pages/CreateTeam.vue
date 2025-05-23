<script setup lang="ts">
import {ref} from "vue";
import myAxios from "../plugins/myAxios.ts";
import {BaseResponse} from "../models/response";
import {showFailToast, showSuccessToast} from "vant";
import {useRouter} from "vue-router";

const initParam = {
  teamName: "",
  description: "",
  expireTime: null,
  maxNum: 3,
  teamPassword: "",
  teamStatus: 0,
}
const createTeamParam = ref({...initParam})
// 添加密码可见状态
const isPasswordVisible = ref(false);
const router = useRouter();
// 新增创建群聊逻辑
const createGroupChat = async (teamId: number) => {
  try {
    await myAxios.post("/group/create", { teamId });
  } catch (error) {
    console.error("自动创建群聊失败", error);
  }
};

const onSubmit = async () => {
  const res = await myAxios.post("/team/create", createTeamParam.value);
  if (res.code === 0) {
    // 队伍创建成功后自动创建群聊
    await createGroupChat(res.data); // res.data为队伍ID
    showSuccessToast("队伍创建成功");
    router.push("/user/teamCreate");
  } else {
    showFailToast(res.description);
  }
}
const minDate = new Date();
const showPicker = ref(false);
const onConfirm = ({ selectedValues }) => {
  // 提取年月日（Vant的日期选择器返回值结构为[年,月,日,时,分]）
  const [year, month, day] = selectedValues;

  // 创建日期对象（注意月份要-1，JS的Date月份从0开始）
  const expireDate = new Date(year, month - 1, day);

  // 设置为当天23:59:59
  expireDate.setHours(23);
  expireDate.setMinutes(59);
  expireDate.setSeconds(59);

  // 格式化为YYYY-MM-DD HH:mm:ss
  const formatNumber = (n: number) => n.toString().padStart(2, '0');
  const formattedDate =
      `${year}-${formatNumber(month)}-${formatNumber(day)} ` +
      `${formatNumber(23)}:${formatNumber(59)}:${formatNumber(59)}`;

  createTeamParam.value.expireTime = formattedDate;
  showPicker.value = false;
};
</script>

<template>
  <van-form @submit="onSubmit">
    <van-cell-group inset>
      <van-field
          v-model="createTeamParam.teamName"
          name="teamName"
          label="队伍名称"
          placeholder="请输入队伍名称"
          :rules="[{ required: true, message: '请输入队伍名称' }]"
      />
      <van-field
          v-model="createTeamParam.description"
          rows="2"
          autosize
          label="队伍描述"
          type="textarea"
          placeholder="请输入队伍描述"
      />
      <van-field name="maxNum" label="队伍最大人数">
        <template #input>
          <van-stepper v-model="createTeamParam.maxNum" min="2" max="8"/>
        </template>
      </van-field>
      <van-field name="teamStatus" label="队伍状态">
        <template #input>
          <van-radio-group v-model="createTeamParam.teamStatus" direction="horizontal">
            <van-radio :name="0">公开</van-radio>
            <van-radio :name="1">私有</van-radio>
            <van-radio :name="2">加密</van-radio>
          </van-radio-group>
        </template>
      </van-field>
      <van-field
          v-if="createTeamParam.teamStatus === 2"
          v-model="createTeamParam.teamPassword"
          :type="isPasswordVisible ? 'text' : 'password'"
          name="teamPassword"
          label="队伍密码"
          placeholder="请输入队伍密码"
          :rules="[{ required: true, message: '请输入队伍密码' }]"
      >
        <template #right-icon>
          <van-icon
              :name="isPasswordVisible ? 'eye-o' : 'closed-eye'"
              @click="isPasswordVisible = !isPasswordVisible"
              style="padding-left: 8px;"
          />
        </template>
      </van-field>
      <van-field
          v-model="createTeamParam.expireTime"
          is-link
          readonly
          name="expireTime"
          label="过期时间"
          placeholder="点击选择过期时间"
          @click="showPicker = true"
      />
      <van-popup v-model:show="showPicker" destroy-on-close position="bottom">
        <van-date-picker
            type="datetime"
            :min-date="minDate"
            @confirm="onConfirm"
            @cancel="showPicker = false"
        />
      </van-popup>
    </van-cell-group>
    <div style="margin: 16px;">
      <van-button round block type="primary" native-type="submit">
        提交
      </van-button>
    </div>
  </van-form>
</template>

<style scoped>

</style>