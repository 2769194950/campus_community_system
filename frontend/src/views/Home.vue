<template>
  <div class="home">
    <div class="create-post-bar">
      <el-button type="primary" @click="goToCreatePost">发布新帖</el-button>
    </div>
    <el-row :gutter="20">
      <el-col :span="18">
        <PostList :partition-id="selectedPartition" />
      </el-col>
      <el-col :span="6">
        <PartitionList v-model="selectedPartition" />
      </el-col>
    </el-row>
  </div>
</template>

<script>
import PostList from "@/components/PostList.vue";
import PartitionList from "@/components/PartitionList.vue";

export default {
  name: "Home",
  components: {
    PostList,
    PartitionList,
  },
  data() {
    return {
      selectedPartition: null, // null means all partitions
    };
  },
  methods: {
    goToCreatePost() {
      this.$router.push("/create-post");
    },
  },
};
</script>

<style scoped>
.home {
  width: 100%;
}

.create-post-bar {
  margin-bottom: 20px;
  text-align: right;
}
</style>
