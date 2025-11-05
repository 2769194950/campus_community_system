<template>
  <el-card class="partition-card">
    <template #header>
      <div class="card-header">
        <span>讨论分区</span>
      </div>
    </template>
    <el-menu :default-active="activePartition" class="partition-menu">
      <el-menu-item index="all" @click="onPartitionSelect(null)">
        <i class="el-icon-menu"></i>
        <span>全部帖子</span>
      </el-menu-item>
      <div v-if="partitions && partitions.length > 0" class="partition-divider"></div>
      <template v-for="(partition, index) in partitions" :key="partition.id">
        <el-menu-item
          :index="partition.id.toString()"
          @click="onPartitionSelect(partition.id)"
        >
          <i class="el-icon-collection-tag"></i>
          <span>{{ partition.name }}</span>
        </el-menu-item>
        <!-- 在每个分区项之间添加明显的分割线，但不在最后一项后添加 -->
        <div v-if="index < partitions.length - 1" class="partition-divider"></div>
      </template>
    </el-menu>
  </el-card>
</template>

<script>
import axios from "axios";

export default {
  name: "PartitionList",
  props: {
    modelValue: {
      type: [Number, String],
      default: "all",
    },
  },
  emits: ["update:modelValue"],
  data() {
    return {
      partitions: [],
      activePartition: this.modelValue ? this.modelValue.toString() : "all",
    };
  },
  watch: {
    modelValue(newVal) {
      this.activePartition = newVal ? newVal.toString() : "all";
    },
  },
  created() {
    this.fetchPartitions();
  },
  methods: {
    fetchPartitions() {
      axios
        .get("/api/partition/list")
        .then((response) => {
          if (response.data && response.data.code === 200) {
            this.partitions = response.data.data;
          } else {
            console.error(
              "获取分区列表失败:",
              response.data ? response.data.message : "No response data"
            );
          }
        })
        .catch((error) => {
          console.error("获取分区列表失败:", error);
        });
    },
    onPartitionSelect(partitionId) {
      this.$emit("update:modelValue", partitionId);
    },
  },
};
</script>

<style scoped>
.partition-card {
  width: 100%;
}
.partition-menu {
  border-right: none;
}
.partition-menu .el-menu-item {
  display: flex;
  align-items: center;
}
.partition-menu .el-menu-item i {
  margin-right: 10px;
}

/* 自定义分割线样式，使其更加明显 */
.partition-menu :deep(.el-divider) {
  margin: 4px 0;
}

.partition-menu :deep(.el-divider__text) {
  background-color: #ffffff;
}

/* 添加自定义分割线类，使分割线更明显 */
.partition-divider {
  height: 1px;
  background-color: #dcdfe6;
  margin: 8px 0;
}
</style>
