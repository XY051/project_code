<script setup lang="ts">
import { ref, reactive } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { User, Lock } from "@element-plus/icons-vue";
import type { FormInstance } from "element-plus";
import http from "../utils/http";
import config from "../config/index.js";

const router = useRouter();
const registerFormRef = ref<FormInstance>();
const loading = ref(false);

const registerForm = reactive({
  username: "",
  password: "",
  confirmPassword: ""
});

// 确认密码验证
const validateConfirmPassword = (rule: any, value: any, callback: any) => {
  if (value === '') {
    callback(new Error('请再次输入密码'));
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入密码不一致'));
  } else {
    callback();
  }
};

const registerRules = {
  username: [
    { required: true, message: "请输入用户名", trigger: "blur" }
  ],
  password: [
    { required: true, message: "请输入密码", trigger: "blur" }
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: "blur" }
  ]
};

const handleRegister = async () => {
  if (!registerFormRef.value) return;

  try {
    await registerFormRef.value.validate();
    loading.value = true;

    const res = await http.post("/register", {
      username: registerForm.username,
      password: registerForm.password
    });
    
    if (res.code === 200) {
      ElMessage.success("注册成功，请登录");
      await router.push("/login");
    } else {
      ElMessage.error(res.message || "注册失败");
    }
  } catch (error) {
    console.error("注册失败:", error);
    ElMessage.error("注册失败，请稍后重试");
  } finally {
    loading.value = false;
  }
};

// 跳转到登录页面
const goToLogin = () => {
  router.push("/login");
};
</script>

<template>
  <div class="register-container">
    <!-- 背景装饰元素 -->
    <div class="left-section">
      <div class="image-wrapper">
        <img src="@/assets/05.jpg" class="background-image" />
        <div class="image-overlay">
          <h2>{{ config.frontName }}</h2>
          <p>{{ config.welcome }}</p>
        </div>
      </div>
    </div>


    <!-- 右侧登录表单 -->
    <div class="right-section">
      <div class="auth-container">
        <!-- 传统装饰边框 -->
        <div class="decoration-border">
          <div class="corner corner-tl"></div>
          <div class="corner corner-tr"></div>
          <div class="corner corner-bl"></div>
          <div class="corner corner-br"></div>
        </div>

        <div class="auth-header">
          <div class="logo-wrapper">
            <div class="logo-ornament"></div>
            <h1 class="site-title">非遗文化</h1>
            <div class="logo-ornament"></div>
          </div>
          <p class="site-description">用户注册</p>
          <div class="decorative-line"></div>
        </div>
        <div class="auth-content">

          <el-form
            ref="registerFormRef"
            :model="registerForm"
            :rules="registerRules"
            label-width="0"
            size="large"
          >
            <el-form-item prop="username">
              <el-input
                v-model="registerForm.username"
                placeholder="请输入用户名"
                :prefix-icon="User"
              />
            </el-form-item>

            <el-form-item prop="password">
              <el-input
                v-model="registerForm.password"
                type="password"
                placeholder="请输入密码"
                :prefix-icon="Lock"
                show-password
              />
            </el-form-item>

            <el-form-item prop="confirmPassword">
              <el-input
                v-model="registerForm.confirmPassword"
                type="password"
                placeholder="请确认密码"
                :prefix-icon="Lock"
                show-password
                @keyup.enter="handleRegister"
              />
            </el-form-item>

            <el-form-item>
              <el-button
                type="primary"
                :loading="loading"
                class="register-button"
                @click="handleRegister"
              >
                {{ loading ? "注册中..." : "注 册" }}
              </el-button>
            </el-form-item>
          </el-form>

          <div class="form-footer">
            <div class="login-link">
              <span>已有账号？</span>
              <el-button type="text" @click="goToLogin">立即登录</el-button>
            </div>
            <p class="copyright">© 2026 {{ config.frontName }} 版权所有</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 背景样式 */
.register-container {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100vh;
  min-height: 600px;
  background: url('@/assets/bg.jpg') no-repeat;
  background-size: 100% 100%;
  position: relative;

  .left-section {
    flex: 1;
    display: flex;
    justify-content: center;
    align-items: center;
    background: none;
    padding: 20px;
  }

  .image-wrapper {
    width: 420px;
    height: 540px;
    position: relative;
    display: flex;
    justify-content: center;
    align-items: center;
    background: #fff;
    border-radius: 15px;
    box-shadow: 0 8px 24px rgba(0,0,0,0.08);
    overflow: hidden;
    transition: all 0.3s ease;
  }

  .image-wrapper:hover {
    transform: translateY(-5px);
    box-shadow: 0 12px 28px rgba(0,0,0,0.12);
  }

  .background-image {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.3s ease;
  }

  .image-wrapper:hover .background-image {
    transform: scale(1.05);
  }

  .image-overlay {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    padding: 20px;
    background: linear-gradient(to top, rgba(0,0,0,0.7), transparent);
    color: white;
    text-align: center;
  }

  .image-overlay h2 {
    margin: 0;
    font-size: 24px;
    font-weight: 600;
  }

  .image-overlay p {
    margin: 5px 0 0;
    font-size: 16px;
    opacity: 0.9;
  }

  /* 右侧登录表单 */

  .right-section {
    flex: 1;
    display: flex;
    justify-content: center;
    align-items: center;
    background: none;
    padding: 20px;
  }
  .auth-container {
    max-width: 360px;
    width: 100%;
    margin: 0 auto;
    padding: 3rem 2.5rem;
    background: rgba(255, 255, 255, 0.6);
    backdrop-filter: blur(10px);
    border-radius: 0;
    box-shadow:
        0 8px 32px rgba(44, 44, 44, 0.08),
        0 2px 8px rgba(44, 44, 44, 0.04);
    position: relative;
    z-index: 1;
    animation: fadeIn 0.5s ease-out;
  }
  @keyframes fadeIn {
    from {
      opacity: 0;
      transform: translateY(20px);
    }
    to {
      opacity: 1;
      transform: translateY(0);
    }
  }
  /* 传统装饰边框 */
  .decoration-border {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    pointer-events: none;
  }

  .corner {
    position: absolute;
    width: 60px;
    height: 60px;
    border-color: #C5A572;
    border-style: solid;
    border-width: 0;
  }

  .corner-tl {
    top: 0;
    left: 0;
    border-top-width: 2px;
    border-left-width: 2px;
  }

  .corner-tr {
    top: 0;
    right: 0;
    border-top-width: 2px;
    border-right-width: 2px;
  }

  .corner-bl {
    bottom: 0;
    left: 0;
    border-bottom-width: 2px;
    border-left-width: 2px;
  }

  .corner-br {
    bottom: 0;
    right: 0;
    border-bottom-width: 2px;
    border-right-width: 2px;
  }

  .auth-header {
    text-align: center;
    margin-bottom: 2.5rem;
  }
  .logo-wrapper {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 1rem;
    margin-bottom: 0.8rem;
  }

  .logo-ornament {
    width: 40px;
    height: 2px;
    background: linear-gradient(90deg, transparent, #C5A572, transparent);
  }

  .site-title {
    margin: 0;
    font-family: 'Noto Serif SC', '思源宋体', serif;
    font-size: 2rem;
    font-weight: 700;
    color: #2C2C2C;
    letter-spacing: 0.3rem;
    position: relative;
  }

  .site-description {
    margin: 0 0 1rem 0;
    color: #666666;
    font-size: 0.95rem;
    letter-spacing: 0.1rem;
    line-height: 1.6;
  }

  .decorative-line {
    width: 80px;
    height: 3px;
    margin: 0 auto;
    background: linear-gradient(90deg, transparent, #D4282D, transparent);
    border-radius: 2px;
  }
  .auth-content {
    margin-bottom: 2rem;
    animation: slideUp 0.5s ease-out 0.2s both;
  }

.register-button {
  width: 100%;
  height: 48px;
  font-size: 16px;
  border-radius: 8px;
  margin-top: 10px;
}

.form-footer {
  margin-top: 30px;
  text-align: center;
}

.login-link {
  margin-bottom: 20px;
  font-size: 14px;
  color: #666;
}

.login-link span {
  margin-right: 8px;
}

.copyright {
  margin: 0;
  font-size: 12px;
  color: #999;
}

/* 输入框样式优化 */
:deep(.el-input__wrapper) {
  height: 48px;
  border-radius: 8px;
  box-shadow: 0 0 0 1px #dcdfe6 inset;
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #c0c4cc inset;
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #409eff inset;
}

:deep(.el-input__inner) {
  height: 100%;
}

/* 动画 */
@keyframes modalFadeIn {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 响应式设计 */
@media (max-width: 960px) {
  .register-box {
    width: 90%;
    height: auto;
    flex-direction: column;
  }

  .register-left,
  .register-right {
    width: 100%;
  }

  .register-left {
    padding: 40px;
    height: 200px;
  }

  .register-right {
    padding: 40px 20px;
  }
}
}
</style>