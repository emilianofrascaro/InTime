<template>
  <div class="flex">
    <div class="row items-start " :id="nome">
        <q-card class="col">
            <q-card-section @click="handleClick">
                <q-icon name='fas fa-desktop'  v-if=" status === 'ON_LINE' " class="q-mx-sm text-yellow justify-center" size='lg'/>
                <q-icon name='fas fa-desktop'  v-else-if=" status === 'READY' " class="q-mx-sm text-green justify-center" size='lg'/>
                <q-icon name='fas fa-desktop'  v-else class="q-mx-sm text-red justify-center" size='lg'/>
                <div class='text-overline'>
                    {{nome}}
                </div>
                <div class="text-caption">{{ipAddress}}</div>
                <q-tooltip v-if=" status !== 'ON_LINE' ">
                    {{lastHearthBeatTimePretty}}
                </q-tooltip>
            </q-card-section>

        </q-card>
    </div>
  </div>
</template>

<script>
import moment from 'moment'

export default {
  name: 'NetworkStateElement',
  props: {
    nome: {
      type: String,
      required: true
    },

    ipAddress: {
      type: String,
      default: ''
    },
    status: {
      type: String,
      default: '#'
    },
    lastHearthBeatTime: {
      type: String,
      default: ''
    }
  },
  computed: {
    lastHearthBeatTimePretty: function () {
      return moment(String(this.lastHearthBeatTime)).format('YYYY/MM/DD HH:mm:ss')
    }
  },
  methods: {
    handleClick () {
      this.$emit('click')
    }
  }
}
</script>
<style scoped>
.intime-ready{
    color:green;
}
.intime-online{
    color:yellow;
}
.intime-offline{
    color:red;
}
</style>
