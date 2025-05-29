
<script setup lang="ts">
import {ref} from 'vue';
import {useRouter} from 'vue-router';
import {showFailToast, showLoadingToast, showSuccessToast} from 'vant';
import myAxios from "../plugins/myAxios.ts";
import {BaseResponse} from "../models/response";
import {setCurrentUser} from "../global/UserState.ts";

const router = useRouter();

const userAccount = ref('');
const userPassword = ref('');

const handleLogin = async () => {
  const toast = showLoadingToast({
    message: '登录中...',
    forbidClick: true,
  });

  try {
    // 这里替换为真实的登录API调用
    const res: BaseResponse = await myAxios.post('/user/login', {
      userAccount: userAccount.value,
      userPassword: userPassword.value,
    })
    if (res.code === 0 && res.data) {
      showSuccessToast('登录成功！');
      setCurrentUser(res.data)
      router.back()
    } else {
      const msg = res.description
      showFailToast(msg);
    }
  } finally {
    setTimeout(() => {
      toast.close();
    }, 1000)
  }

};
</script>

<template>
  <div class="login-container">
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
      <p class="sub-text">Login to continue exploring</p>

      <!-- 登录表单 -->
      <van-form @submit="handleLogin" class="login-form">
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
        </van-cell-group>

        <!-- 登录按钮 -->
        <div class="login-btn-wrapper">
          <van-button
              round
              block
              type="primary"
              native-type="submit"
              class="login-btn"
          >
            登录
          </van-button>
        </div>
      </van-form>

      <!-- 注册提示 -->
      <p class="signup-tip">
        Don't have an account?
        <router-link to="/user/register" class="signup-link">注册</router-link>
      </p>
    </div>
  </div>
</template>

<style lang="less" scoped>
.login-container {
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

    .login-form {
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

      .login-btn-wrapper {
        margin-top: 30px;

        .login-btn {
          background: linear-gradient(135deg, #1989FA, #1989FA);
          border: none;
          font-weight: 500;
        }
      }

      .other-login {
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
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);

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