module.exports = {
  devServer: {
    proxy: {
      '/api': {
        target: 'http://localhost:8081/community',
        changeOrigin: true,
        pathRewrite: { '^/api': '' }
      }
    }
  }
};