import { createStore } from "vuex";
import axios from "axios";
import { ElMessage } from "element-plus";

export default createStore({
  state: {
    user: JSON.parse(localStorage.getItem("user")) || null,
    token: localStorage.getItem("token") || null,
  },
  getters: {
    isLoggedIn: (state) => !!state.user && !!state.token,
  },
  mutations: {
    setUser(state, user) {
      state.user = user;
      if (user) {
        localStorage.setItem("user", JSON.stringify(user));
      } else {
        localStorage.removeItem("user");
      }
    },
    setToken(state, token) {
      state.token = token;
      if (token) {
        localStorage.setItem("token", token);
        axios.defaults.headers.common["Authorization"] = token;
      } else {
        localStorage.removeItem("token");
        delete axios.defaults.headers.common["Authorization"];
      }
    },
  },
  actions: {
    initAuth({ state }) {
      // 初始化时设置axios的Authorization header
      if (state.token) {
        axios.defaults.headers.common["Authorization"] = state.token;
      }
    },
    async login({ commit }, credentials) {
      try {
        const response = await axios.post("/api/user/login", credentials);
        if (response.data.code === 200) {
          const { user, ticket: token } = response.data.data;
          commit("setUser", user);
          commit("setToken", token);
          ElMessage.success("登录成功！");
          return true;
        } else {
          ElMessage.error(response.data.message || "登录失败！");
          return false;
        }
      } catch (error) {
        console.error("Login failed:", error);
        ElMessage.error("登录服务异常，请稍后再试。");
        return false;
      }
    },
    async logout({ commit, state }) {
      try {
        // 调用后端登出接口，使服务端登录凭证失效
        if (state.token) {
          await axios.get("/api/user/logout", {
            headers: {
              Authorization: state.token,
            },
          });
        }
        // 清除本地状态
        commit("setUser", null);
        commit("setToken", null);
        ElMessage.success("已成功退出登录");
        return true;
      } catch (error) {
        console.error("Logout failed:", error);
        // 即使后端调用失败，也要清除本地状态
        commit("setUser", null);
        commit("setToken", null);
        ElMessage.success("已成功退出登录");
        return true;
      }
    },
  },
  modules: {},
});
