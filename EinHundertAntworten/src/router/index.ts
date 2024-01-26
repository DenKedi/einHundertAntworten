import { useAuthStore } from '@/stores/auth'
import DashboardView from '@/views/DashboardView.vue'
import LoginView from '@/views/LoginView.vue'
import ProfileView from '@/views/ProfileView.vue'
import GameManagerView from '@/views/GameManagerView.vue'
import QuizView from '@/views/QuizView.vue'
import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/home',
      name: 'Dashboard',
      component: DashboardView
    },{
      path: '/',
      name: 'start',
      redirect: '/home'
    },    
    {
      path: '/login',
      name: 'Login',
      component: LoginView
    },
    {
      path: '/profile',
      name: 'Profile',
      component: ProfileView
    },
    {
      path: '/gameManager',
      name: 'GameManager',
      component: GameManagerView
    },
    {
      path: '/quiz',
      name: 'Quiz',
      component: QuizView
    }

  ]
})

router.beforeEach(async (to) => {
  const publicPages = ['/login', '/home', '/quiz']
  const authRequired = !publicPages.includes(to.path)
  const auth = useAuthStore();
  // if auth is required and user is not logged in ->
  
  if(authRequired && !auth.user) {
    return '/login'
  }
  
})




export default router
