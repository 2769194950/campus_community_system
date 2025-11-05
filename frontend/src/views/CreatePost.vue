<template>
  <div class="create-post">
    <el-card>
      <template #header>
        <h2>发布新帖</h2>
      </template>
      <el-form
        :model="postForm"
        ref="postForm"
        label-width="80px"
        @submit.prevent="submitPost"
      >
        <el-form-item
          label="标题"
          prop="title"
          :rules="{ required: true, message: '标题不能为空', trigger: 'blur' }"
        >
          <el-input
            v-model="postForm.title"
            placeholder="请输入帖子标题"
          ></el-input>
        </el-form-item>
        <el-form-item
          label="分区"
          prop="partitionId"
          :rules="{ required: true, message: '请选择分区', trigger: 'change' }"
        >
          <el-select v-model="postForm.partitionId" placeholder="请选择分区">
            <el-option
              v-for="partition in partitions"
              :key="partition.id"
              :label="partition.name"
              :value="partition.id"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item
          label="内容"
          prop="content"
          :rules="{ required: true, message: '内容不能为空', trigger: 'blur' }"
        >
          <el-input
            type="textarea"
            :rows="10"
            v-model="postForm.content"
            placeholder="请输入帖子内容"
          ></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" native-type="submit">立即发布</el-button>
          <el-button @click="cancel">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import axios from "axios";
import { ElMessage } from "element-plus";

export default {
  name: "CreatePost",
  data() {
    return {
      postForm: {
        title: "",
        partitionId: "",
        content: "",
      },
      partitions: [],
    };
  },
  created() {
    this.fetchPartitions();
  },
  methods: {
    fetchPartitions() {
      axios
        .get("/api/partition/list")
        .then((response) => {
          if (response.data.code === 200) {
            this.partitions = response.data.data;
          }
        })
        .catch((error) => {
          console.error("获取分区列表失败:", error);
          ElMessage.error("获取分区列表失败，请稍后再试。");
        });
    },
    submitPost() {
      this.$refs.postForm.validate((valid) => {
        if (valid) {
          axios
            .post("/api/post/add", this.postForm)
            .then((response) => {
              if (response.data.code === 200) {
                ElMessage.success("帖子发布成功！");
                this.$router.push("/");
              } else {
                ElMessage.error(
                  response.data.message || "发布失败，请稍后再试。"
                );
              }
            })
            .catch((error) => {
              console.error("发布帖子失败:", error);
              ElMessage.error("发布服务异常，请联系管理员。");
            });
        } else {
          return false;
        }
      });
    },
    cancel() {
      this.$router.go(-1);
    },
  },
};
</script>

<style scoped>
.create-post {
  max-width: 800px;
  margin: 0 auto;
}
</style>
