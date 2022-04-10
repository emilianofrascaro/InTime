<template>
  <q-layout view="hHh Lpr lFf">
    <q-header elevated>
      <q-toolbar>
        <q-btn
          flat
          dense
          round
          icon="menu"
          aria-label="Menu"
          @click="leftDrawerOpen = !leftDrawerOpen"
        />

        <q-toolbar-title>
          InTime
        </q-toolbar-title>

      </q-toolbar>
    </q-header>

    <q-drawer
      v-model="leftDrawerOpen"
      show-if-above
      bordered
      content-class="bg-grey-1"
    >
      <q-list>
        <q-item-label
          header
          class="text-grey-8"
        >
          Dashboards
        </q-item-label>
        <LeftMenuLink
          v-for="link in leftMenuLinks"
          :key="link.title"
          v-bind="link"
        />
      </q-list>
    </q-drawer>

    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script>
import LeftMenuLink from 'components/LeftMenuLink'

export default {
  name: 'MainLayout',

  components: {
    LeftMenuLink
  },

  data () {
    return {
      leftDrawerOpen: false,
      leftMenuLinks: [
        {
          title: 'Network State',
          icon: 'fas fa-network-wired',
          link: '/network-state'
        },
        {
          title: 'Lista Tasks',
          icon: 'fas fa-tasks',
          link: '/task'
        },
        {
          title: 'Lista Esecuzioni',
          icon: 'fas fa-cogs',
          link: '/task-executions'
        }
      ]
    }
  }
}
</script>
