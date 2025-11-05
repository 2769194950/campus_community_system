import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";
import store from "./store"; // 若有 Vuex/Pinia

import ElementPlus from "element-plus";
import "element-plus/dist/index.css";
import axios from "axios";

// 标记所有请求为AJAX，便于后端异常统一返回JSON而非重定向到/error
axios.defaults.headers.common["X-Requested-With"] = "XMLHttpRequest";

const app = createApp(App);
app.use(store); // 若有
app.use(router);
app.use(ElementPlus);
app.mount("#app");
