<script setup lang="ts">
import {onMounted, ref} from "vue";
import myAxios from "../plugins/myAxios.ts";
import {BaseResponse} from "../models/response";
import {showFailToast, showSuccessToast} from "vant";
import {useRoute, useRouter} from "vue-router";
import {TeamType} from "../models/team";

const route = useRoute();
const oldTeam = ref<TeamType>({});

onMounted(async () => {
  const teamId = route.query.teamId;
  const res:BaseResponse = await myAxios.get('/team/get', {
    params: {
      teamId: teamId
    }
  });
  if (res.code === 0 && res.data) {
    oldTeam.value = res.data;
  } else {
    showFailToast("获取队伍信息失败")
  }
});
// 添加密码可见状态
const isPasswordVisible = ref(false);
const router = useRouter();
const onSubmit = async () => {
  const res: BaseResponse = await myAxios.post("/team/update", oldTeam.value);
  if (res.code === 0 && res.data > 0) {
    showSuccessToast('更新队伍成功');
    router.back()
  } else {
    showFailToast('更新队伍失败')
  }
}
const minDate = new Date();
const showPicker = ref(false);
const onConfirm = ({selectedValues}) => {
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

  oldTeam.value.expireTime = formattedDate;
  showPicker.value = false;
};
</script>

<template>
  <van-form @submit="onSubmit">
    <van-cell-group inset>
      <van-field
          v-model="oldTeam.teamName"
          name="teamName"
          label="队伍名称"
          placeholder="请输入队伍名称"
          :rules="[{ required: true, message: '请输入队伍名称' }]"
      />
      <van-field
          v-model="oldTeam.description"
          rows="2"
          autosize
          label="队伍描述"
          type="textarea"
          placeholder="请输入队伍描述"
      />
      <van-field name="maxNum" label="队伍最大人数">
        <template #input>
          <van-stepper v-model="oldTeam.maxNum" :min="oldTeam.currentNum === 1 ? 2 : oldTeam.currentNum" max="8"/>
        </template>
      </van-field>
      <van-field name="teamStatus" label="队伍状态">
        <template #input>
          <van-radio-group v-model="oldTeam.teamStatus" direction="horizontal">
            <van-radio :name="0">公开</van-radio>
            <van-radio :name="1">私有</van-radio>
            <van-radio :name="2">加密</van-radio>
          </van-radio-group>
        </template>
      </van-field>
      <van-field
          v-if="oldTeam.teamStatus === 2"
          v-model="oldTeam.teamPassword"
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
          v-model="oldTeam.expireTime"
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