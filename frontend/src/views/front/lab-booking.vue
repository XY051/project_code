<script setup lang="ts">
import http from "../../utils/http";
import { ElMessage } from "element-plus";
import { onMounted, ref } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();
const labOptions = ref([]);
const form = ref({
  labId: null as number | null,
  userId: null as number | null,
  userName: "", purpose: "", bookDate: "", startTime: "", endTime: "", status: 0
});

const getLabOptions = async () => {
  const res = await http.get("/lab/list");
  if (res.code === 200) { labOptions.value = res.data; }
};

const submitBooking = async () => {
  if (!form.value.labId) { ElMessage.warning("请选择实验室"); return; }
  if (!form.value.bookDate) { ElMessage.warning("请选择预约日期"); return; }
  if (!form.value.startTime || !form.value.endTime) { ElMessage.warning("请选择预约时间段"); return; }
  
  // Get current user info
  const userRes = await http.get("/user/current");
  if (userRes.code === 200) {
    form.value.userId = userRes.data.id;
    form.value.userName = userRes.data.nickName;
  }
  
  const res = await http.post("/labBooking/add", form.value);
  if (res.code === 200) {
    ElMessage.success("预约提交成功，请等待审核");
    form.value = { labId: null, userId: null, userName: "", purpose: "", bookDate: "", startTime: "", endTime: "", status: 0 };
  } else {
    ElMessage.error("预约失败");
  }
};

onMounted(async () => { await getLabOptions(); });
</script>

<template>
  <div class="booking-container">
    <div class="page-header">
      <h2>实验室预约</h2>
      <p>选择实验室和预约时间，提交预约申请</p>
    </div>
    <el-card class="booking-card">
      <el-form :model="form" label-width="120">
        <el-form-item label="选择实验室" required>
          <el-select v-model="form.labId" placeholder="请选择实验室" style="width:100%">
            <el-option v-for="lab in labOptions" :key="lab.id" :label="lab.name + ' (' + lab.location + ')'" :value="lab.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="预约用途" required>
          <el-input v-model="form.purpose" placeholder="请简要说明预约用途" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="预约日期" required>
              <el-date-picker v-model="form.bookDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="开始时间" required>
              <el-time-picker v-model="form.startTime" placeholder="开始" value-format="HH:mm:ss" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="结束时间" required>
              <el-time-picker v-model="form.endTime" placeholder="结束" value-format="HH:mm:ss" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div class="submit-box">
        <el-button type="primary" size="large" @click="submitBooking">提交预约申请</el-button>
        <el-button size="large" @click="router.back()">返回</el-button>
      </div>
    </el-card>
  </div>
</template>

<style lang="scss" scoped>
.booking-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}
.page-header {
  text-align: center;
  margin-bottom: 30px;
  h2 { font-size: 24px; color: #1a237e; margin-bottom: 8px; }
  p { color: #666; font-size: 14px; }
}
.booking-card {
  padding: 20px;
}
.submit-box {
  text-align: center;
  margin-top: 30px;
}
</style>
