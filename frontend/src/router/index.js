// Vue 3 + vue-router 4
import { createRouter, createWebHistory } from "vue-router";
import store from "@/store";

// 基础页面按需加载
const Home = () => import("@/views/Home.vue");
const Login = () => import("@/views/Login.vue");
const Register = () => import("@/views/Register.vue");

// 个人中心相关
const UserProfile = () => import("@/views/UserProfile.vue");
const ProfileEdit = () => import("@/views/ProfileEdit.vue"); // 若无可删
const MessageCenter = () => import("@/views/MessageCenter.vue");

// 帖子相关
const PostList = () => import("@/views/PostList.vue");
const PostDetail = () => import("@/views/PostDetail.vue");
const CreatePost = () => import("@/views/CreatePost.vue");

// 搜索相关
const SearchResults = () => import("@/views/SearchResults.vue");

// 管理员相关
const AdminDashboard = () => import("@/views/AdminDashboard.vue");

// 排行榜
const Leaderboard = () => import("@/views/Leaderboard.vue");

// 404
const NotFound = () => import("@/views/NotFound.vue");

const routes = [
    { path: "/", name: "Home", component: Home },

    { path: "/login", name: "Login", component: Login },
    { path: "/register", name: "Register", component: Register },

    // 用户主页（查看自己或他人）
    {
        path: "/profile/:id",
        name: "UserProfile",
        component: UserProfile,
        props: true,
    },

    { path: "/profile/edit", name: "ProfileEdit", component: ProfileEdit },
    { path: "/messages", name: "MessageCenter", component: MessageCenter },

    // 帖子相关
    { path: "/post", name: "PostList", component: PostList },
    { path: "/post/:id", name: "PostDetail", component: PostDetail, props: true },
    { path: "/create-post", name: "CreatePost", component: CreatePost },

    // 搜索结果页
    { path: "/search", name: "SearchResults", component: SearchResults },

    // 管理员后台
    { path: "/admin", name: "AdminDashboard", component: AdminDashboard },

    // 排行榜
    { path: "/leaderboard", name: "Leaderboard", component: Leaderboard },

    // 404
    { path: "/:pathMatch(.*)*", name: "NotFound", component: NotFound },
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

/** 路由守卫：
 *  - 管理员访问任何非 /admin 路由 → 强制跳后台
 *  - 普通用户访问 /admin → 拉回首页
 */
router.beforeEach((to, from, next) => {
    const isAdmin = store.getters.isAdmin;
    // 未登录时，不做折返（保持你原有的登录逻辑）
    if (!store.getters.isLoggedIn) return next();

    if (isAdmin) {
        if (!to.path.startsWith("/admin")) return next("/admin");
        return next();
    } else {
        if (to.path.startsWith("/admin")) return next("/");
        return next();
    }
});

export default router;
