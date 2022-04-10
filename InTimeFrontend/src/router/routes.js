
const routes = [
  {
    path: '/',
    component: () => import('layouts/MainLayout.vue'),
    children: [
      { path: 'network-state', component: () => import('pages/NetworkState.vue') },
      { path: 'task', component: () => import('pages/Task.vue') },
      { path: 'task-executions', component: () => import('pages/TaskExecutions.vue') },
      { path: '/', component: () => import('pages/Index.vue') }
    ]
  }
]

// Always leave this as last one
if (process.env.MODE !== 'ssr') {
  routes.push({
    path: '*',
    component: () => import('pages/Error404.vue')
  })
}

export default routes
