import Index from "../pages/Index.vue";
import Team from "../pages/Team.vue";
import User from "../pages/User.vue";
import Search from "../pages/Search.vue";
import EditUserInfo from "../pages/EditUserInfo.vue";
import SearchResult from "../pages/SearchResult.vue";
import UserLogin from "../pages/UserLogin.vue";
import CreateTeam from "../pages/CreateTeam.vue";
import UpdateTeam from "../pages/UpdateTeam.vue";
import TeamCreate from "../pages/TeamCreate.vue";
import TeamJoin from "../pages/TeamJoin.vue";
import UserRegister from "../pages/UserRegister.vue";
import FriendList from "../pages/FriendList.vue";
import Message from "../pages/Message.vue";
import Chat from "../pages/Chat.vue";
import FriendRequestList from "../pages/FriendRequestList.vue";

const routes = [
    {
        path: '/',
        component: Index,
        meta: {
            layout: 'basic',         // 使用基础布局
            navTitle: '首页',        // 导航栏标题
            showBack: true,        // 隐藏返回按钮
            showSearch: true        // 显示搜索按钮
        }
    },
    {
        path: '/team',
        component: Team,
        meta: {
            layout: 'basic',
            navTitle: '队伍中心',
            showBack: true,
            showSearch: false
        }
    },
    {
        path: '/user',
        component: User,
        meta: {
            layout: 'basic',
            showBack: false,
            showSearch: false
        }
    },
    {
        path: '/search',
        component: Search,
        meta: {
            layout: 'basic',
            navTitle: '搜索',
            showBack: true,
            showSearch: false
        }
    },
    {
        path: '/user/list',
        component: SearchResult,
        meta: {
            layout: 'basic',
            navTitle: '用户列表',
            showBack: true,
            showSearch: true
        }
    },
    {
        path: '/user/edit',
        component: EditUserInfo,
        meta: {
            layout: 'basic',
            navTitle: '编辑个人信息',
            showBack: true,
            showSearch: false
        }
    },
    {
        path: '/user/login',
        component: UserLogin,
        meta: {
            layout: 'empty',
        }
    },
    {
        path: '/user/register',
        component: UserRegister,
        meta: {
            layout: 'empty',
        }
    },
    {
        path: '/user/teamCreate',
        component: TeamCreate,
        meta: {
            layout: 'basic',
            navTitle: '我创建的队伍',
            showBack: true,
            showSearch: false
        }
    },
    {
        path: '/user/teamJoin',
        component: TeamJoin,
        meta: {
            layout: 'basic',
            navTitle: '我加入的队伍',
            showBack: true,
            showSearch: false
        }
    },
    {
        path: '/team/create',
        component: CreateTeam,
        meta: {
            layout: 'basic',
            navTitle: '创建队伍',
            showBack: true,
            showSearch: false
        }
    },
    {
        path: '/team/update',
        component: UpdateTeam,
        meta: {
            layout: 'basic',
            navTitle: '更新队伍',
            showBack: true,
            showSearch: false
        }
    },
    {
        path: '/friend/list',
        component: FriendList,
        meta: {
            layout: 'basic',
            navTitle: '我的好友',
            showBack: true
        }
    },
    {
        path: '/message',
        component: Message,
        meta: {
            layout: 'basic',
            navTitle: '消息',
            showBack: false
        }
    },
    {
        path: '/chat',
        component: Chat,
        meta: {
            layout: 'basic',
            navTitle: (route) => route.query.name || '聊天', // 动态标题
            showBack: true
        }
    },
    {
        path: '/friend/request/list',
        component: FriendRequestList,
        meta: {
            layout: 'basic',
            navTitle: '好友请求',
            showBack: true
        }
    }
];
export default routes;