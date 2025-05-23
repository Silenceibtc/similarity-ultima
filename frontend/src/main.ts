import {createApp} from 'vue'
import App from './App.vue'
import routes from "./configs/routes.ts";
import {createRouter, createWebHistory} from 'vue-router'
import 'vant/es/toast/style'
import 'vant/es/notify/style'
import 'vant/es/dialog/style'
import {setAxiosRouter} from "./plugins/myAxios.ts";


const router = createRouter({
    history: createWebHistory(),
    routes,
})

// 定义时间格式化过滤器（示例格式：YYYY-MM-DD HH:mm:ss）
const formatTime = (timestamp: number | string, format = 'YYYY-MM-DD HH:mm:ss'): string => {
    const date = new Date(timestamp);

    // 补零函数
    const pad = (num: number) => num.toString().padStart(2, '0');

    // 格式替换映射
    const formatMap: Record<string, string> = {
        'YYYY': date.getFullYear().toString(),
        'MM': pad(date.getMonth() + 1),
        'DD': pad(date.getDate()),
        'HH': pad(date.getHours()),
        'mm': pad(date.getMinutes()),
        'ss': pad(date.getSeconds())
    };

    return Object.entries(formatMap).reduce((acc, [key, value]) => {
        return acc.replace(new RegExp(key, 'g'), value);
    }, format);
};

const app = createApp(App);

// 注册全局过滤器（Vue 3 推荐使用 app.config.globalProperties 替代传统过滤器，但为兼容常见用法保留示例）
app.config.globalProperties.$filters = {
    formatTime
};

// 注入路由实例
setAxiosRouter(router)
app.use(router);
app.mount('#app')


