<template>
  <q-page class="">
    <div class="row q-my-sm">
      <div class="q-mx-auto text-h5 text-primary">Network State</div>
    </div>
    <div class="row">
        <div class="q-pa-md">
            <NetworkStateElement v-for="nelement in networkElements" v-bind="nelement" :key="nelement.nome" />
        </div>
    </div>
  </q-page>
</template>

<script>
import NetworkStateElement from 'components/NetworkStateElement'
export default {
  name: 'NetworkState',
  components: {
    NetworkStateElement
  },
  data () {
    return {
      networkElements: this.loadData()
    }
  },
  created () {
    this.loadData()
    this.timer = setInterval(this.loadData, 10000)
  },
  beforeDestroy () {
    clearInterval(this.timer)
  },
  methods: {
    loadData () {
      console.log('refreshing data')
      this.$axios.get('/api/network/index')
        .then((response) => {
          this.networkElements = response.data
        })
        .catch(() => {
          this.$q.notify({
            color: 'negative',
            position: 'top',
            message: 'Loading failed',
            icon: 'report_problem'
          })
        })
    }
  }
}
</script>
<style scoped>
</style>
