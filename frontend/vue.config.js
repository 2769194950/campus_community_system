module.exports = {
  transpileDependencies: [],
  devServer: {
    port: 8080,
    proxy: {
      "/api": {
        target: "http://localhost:8081/community",
        pathRewrite: { "^/api": "" },
        ws: true,
        changeOrigin: true,
      },
    },
  },
};
