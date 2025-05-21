<script setup lang="ts">
import {ref} from 'vue';
import {useRouter} from 'vue-router';
import {showFailToast, showLoadingToast, showSuccessToast} from 'vant';
import myAxios from "../plugins/myAxios.ts";
import {BaseResponse} from "../models/response";

const router = useRouter();

const userAccount = ref('');
const userPassword = ref('');
const checkPassword = ref('');

const handleRegister = async () => {
  if (userPassword.value !== checkPassword.value) {
    showFailToast("两次输入密码不一致");
    return;
  }
  const toast = showLoadingToast({
    message: '注册...',
    forbidClick: true,
  });

  try {
    const res: BaseResponse = await myAxios.post('/user/register', {
      userAccount: userAccount.value,
      userPassword: userPassword.value,
      checkPassword: checkPassword.value,
    })
    if (res.code === 0 && res.data) {
      showSuccessToast('注册成功！');
      router.replace('/user/login');
    } else {
      const msg = res.description
      showFailToast(msg);
    }
  } finally {
    setTimeout(() => {
      toast.close();
    }, 1500)
  }

};
</script>

<template>
  <div class="register-container">
    <!-- 顶部导航栏 -->
    <van-nav-bar
        title="Similarity"
        left-arrow
        @click-left="router.back()"
        class="nav-bar"
    />

    <!-- 主要内容区域 -->
    <div class="content">
      <h2 class="welcome-text">Welcome Back</h2>
      <p class="sub-text">Register to continue exploring</p>

      <!-- 登录表单 -->
      <van-form @submit="handleRegister" class="register-form">
        <van-cell-group inset>
          <van-field
              v-model="userAccount"
              name="username"
              placeholder="请输入账户"
              :rules="[{ required: true, message: '请输入账户' }]"
          />
          <van-field
              v-model="userPassword"
              type="password"
              name="password"
              placeholder="请输入密码"
              :rules="[{ required: true, message: '请输入密码' }]"
          />
          <van-field
              v-model="checkPassword"
              type="password"
              name="password"
              placeholder="请再次确认密码"
              :rules="[{ required: true, message: '请再次确认密码' }]"
          />
        </van-cell-group>

        <!-- 注册按钮 -->
        <div class="register-btn-wrapper">
          <van-button
              round
              block
              type="primary"
              native-type="submit"
              class="register-btn"
          >
            注册
          </van-button>
        </div>
      </van-form>
    </div>
  </div>
</template>

<style lang="less" scoped>
.register-container {
  height: 100vh;
  background: #f8f9fa;

  .nav-bar {
    background: transparent;

    :deep(.van-nav-bar__title) {
      font-weight: 600;
      color: #1989FA;
    }
  }

  .content {
    padding: 0 24px;

    .welcome-text {
      margin-top: 40px;
      font-size: 32px;
      color: #333;
      line-height: 1.2;
    }

    .sub-text {
      margin-top: 12px;
      color: #666;
      font-size: 14px;
    }

    .register-form {
      margin-top: 40px;

      .form-options {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin: 16px 0;

        .forgot-password {
          color: #1989FA;
          font-size: 12px;
        }
      }

      .register-btn-wrapper {
        margin-top: 30px;

        .register-btn {
          background: linear-gradient(135deg, #1989FA, #1989FA);
          border: none;
          font-weight: 500;
        }
      }

      .other-register {
        margin-top: 40px;

        .divider {
          position: relative;
          text-align: center;
          color: #999;

          &::before,
          &::after {
            content: '';
            position: absolute;
            top: 50%;
            width: 30%;
            height: 1px;
            background: #eee;
          }

          &::before {
            left: 0;
          }

          &::after {
            right: 0;
          }
        }

        .third-party {
          display: flex;
          justify-content: center;
          gap: 30px;
          margin-top: 20px;

          .van-icon {
            font-size: 32px;
            padding: 12px;
            border-radius: 50%;
            background: #fff;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

            &.wechat-icon {
              color: #07c160;
            }

            &.alipay-icon {
              color: #1677ff;
            }
          }
        }
      }
    }

    .signup-tip {
      text-align: center;
      margin-top: 30px;
      color: #666;

      .signup-link {
        color: #1989FA;
        font-weight: 500;
      }
    }
  }
}
</style>