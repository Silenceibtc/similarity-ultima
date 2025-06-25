import {createRouter, createWebHashHistory} from 'vue-router'
import UserManage from '../views/UserManage.vue'
import TeamManage from '../views/TeamManage.vue'
import Login from '../views/Login.vue'
import AdminHomeLayout from '../layout/AdminHomeLayout.vue'
import EditUser from "../views/EditUser.vue";
import EditTeam from "../views/EditTeam.vue";

const router = createRouter({
    history: createWebHashHistory(),
    routes: [
        {
            path: '/',
            component: AdminHomeLayout,
            meta: { requiresAdmin: true },
            children: [  // 添加子路由
                { path: '/user', component: UserManage },  // 子路由路径：/user
                { path: '/team', component: TeamManage },   // 子路由路径：/team
                { path: '/team/edit', component: EditTeam },
                { path: '/user/edit', component: EditUser },
            ]
        },
        { path: '/login', component: Login },

    ]
})

// 导航守卫保持不变
router.beforeEach((to, from, next) => {
    const requiresAdmin = to.meta.requiresAdmin
    const user = sessionStorage.getItem('user')

    if (requiresAdmin) {
        if (!user) {
            next({ path: '/login' })
        } else {
            const currentUser = JSON.parse(user)
            if (currentUser.identity !== 0) {
                sessionStorage.removeItem('user')
                next({ path: '/login', query: { noAuth: true } })
            } else {
                next()
            }
        }
    } else {
        next()
    }
})

export default router