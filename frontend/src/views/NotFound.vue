<template>
  <div class="notfound">
    <h1>404</h1>
    <p>页面不存在</p>
    <el-button type="primary" @click="$router.push('/')">返回首页</el-button>
  </div>
</template>

<script>
export default { name: "NotFound" };
</script>

<style scoped>
.notfound {
  text-align: center;
  padding: 80px 0;
}
h1 {
  font-size: 64px;
  margin: 0 0 12px;
}
p {
  color: #666;
  margin-bottom: 16px;
}
</style>
