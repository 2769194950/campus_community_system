import { createStore } from "vuex";
import axios from "axios";
import { ElMessage } from "element-plus";

/** 统一的管理员判定函数
 * 后端若返回 role / isAdmin 字段可直接用；没有就按用户名 admin 兜底
 */
function isAdminUser(user) {
    if (!user) return false;
    return user.role === "ADMIN" || user.username === "admin" || user.isAdmin === true;
}

// 为每个标签页生成唯一标识符
function generateTabId() {
    return 'tab_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9);
}

// 获取当前标签页ID
function getTabId() {
    let tabId = sessionStorage.getItem('tab_id');
    if (!tabId) {
        tabId = generateTabId();
        sessionStorage.setItem('tab_id', tabId);
    }
    return tabId;
}

// 生成带标签页ID的存储键名
function getStorageKey(key) {
    return key + '_' + getTabId();
}

// 获取当前标签页的用户信息
function getCurrentTabUser() {
    const userKey = getStorageKey('user');
    return JSON.parse(localStorage.getItem(userKey)) || null;
}

// 获取当前标签页的token
function getCurrentTabToken() {
    const tokenKey = getStorageKey('token');
    return localStorage.getItem(tokenKey) || null;
}

export default createStore({
    state: {
        user: getCurrentTabUser(),
        token: getCurrentTabToken(),
        tabId: getTabId()
    },
    getters: {
        isLoggedIn: (state) => !!state.user && !!state.token,
        isAdmin: (state) => isAdminUser(state.user), // ★ 新增
        user: (state) => state.user,
    },
    mutations: {
        setUser(state, user) {
            state.user = user;
            const userKey = getStorageKey('user');
            if (user) {
                localStorage.setItem(userKey, JSON.stringify(user));
            } else {
                localStorage.removeItem(userKey);
            }
        },
        setToken(state, token) {
            state.token = token;
            const tokenKey = getStorageKey('token');
            if (token) {
                localStorage.setItem(tokenKey, token);
                axios.defaults.headers.common["Authorization"] = token;
            } else {
                localStorage.removeItem(tokenKey);
                delete axios.defaults.headers.common["Authorization"];
            }
        },
        // 初始化状态（用于页面刷新时恢复状态）
        initState(state) {
            state.user = getCurrentTabUser();
            state.token = getCurrentTabToken();
            state.tabId = getTabId();
            
            // 设置axios的Authorization header
            if (state.token) {
                axios.defaults.headers.common["Authorization"] = state.token;
            }
        }
    },
    actions: {
        initAuth({ commit }) {
            // 初始化时从当前标签页的存储中恢复状态
            commit('initState');
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
                        headers: { Authorization: state.token },
                    });
                }
                commit("setUser", null);
                commit("setToken", null);
                ElMessage.success("已成功退出登录");
                return true;
            } catch (error) {
                console.error("Logout failed:", error);
                // 即使后端失败也要清本地
                commit("setUser", null);
                commit("setToken", null);
                ElMessage.success("已成功退出登录");
                return true;
            }
        },
    },
    modules: {},
});