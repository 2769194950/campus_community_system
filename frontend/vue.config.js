module.exports = {
  transpileDependencies: [],
  devServer: {
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
