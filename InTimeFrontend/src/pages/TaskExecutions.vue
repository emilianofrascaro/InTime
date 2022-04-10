<template>
    <q-page class="">
        <div class="row q-my-sm">
            <div class="q-mx-auto text-h5 text-primary">Lista Esecuzioni</div>
        </div>
        <div>
            <div v-if="this.tasks">
                <q-list bordered separator padding class="q-pa-sm">
                    <q-item  v-for="task in this.tasks" v-bind="task" :key="task.command">
                        <q-item-section>
                            <q-item-label>{{task.taskName}}</q-item-label>
                            <q-item-label caption>Eseguito su {{task.taskExecutionServer}}  -> {{task.taskExecutionDate}}</q-item-label>
                        </q-item-section>
                        <q-item-section top side>
                          <div class="text-grey-8 q-gutter-xs">
                            <q-icon name="loop" class="text-green" style="font-size: 1.5rem;" v-if="task.taskExecutionStatus==='IN_CORSO'"/>
                            <q-icon name="check_circle_outline" class="text-green" style="font-size: 1.5rem;" v-if="task.taskExecutionStatus==='END'"/>
                            <q-icon name="report_problem" class="text-red" style="font-size: 1.5rem;" v-if="task.taskExecutionStatus==='ERROR'"/>
                            <q-btn size="12px" flat dense round icon="library_books" @click="showLog(task.taskExecutionId)"/>
                          </div>
                        </q-item-section>
                    </q-item>
                </q-list>
            </div>
            <div>
              <q-dialog v-model="log_popup">
                <q-card style="min-width: 350px">
                  <q-card-section>
                    <div class="text-h6">Log per il task '{{this.nomeTask}}'</div>
                    <div class="caption">Eseguito sul server: {{this.nomeServer}}</div>
                    <div class="caption">Orario esecuzione: {{this.oraEsecuzione}}</div>
                  </q-card-section>
                  <q-card-section>
                    <q-item  v-for="taskLog in this.listaLog" v-bind="taskLog" :key="taskLog.id">
                        <q-item-section>
                          <div class="row">
                            <div class="col col-2">
                              <q-icon v-if="taskLog.tipoLog === 'OUTPUT'" name="label" style="color: blue;"/>
                              <q-icon v-if="taskLog.tipoLog === 'ERROR'" name="warning" style="color: red;"/>
                              <q-icon v-if="taskLog.tipoLog === 'END'" name="done" style="color: green;" />
                            </div>
                            <div class="col col-4 caption" >
                              {{taskLog.oraLog}}
                            </div>
                            <div class="col">
                              {{taskLog.logMessage}}
                            </div>
                          </div>
                        </q-item-section>
                    </q-item>
                  </q-card-section>
                  <q-card-actions align="right" class="text-primary">
                    <q-btn flat label="Cancel" @click="closeLog" />
                  </q-card-actions>
                </q-card>
              </q-dialog>
            </div>
        </div>
    </q-page>
</template>
<script>
export default {
  name: 'TaskExecutions',
  data () {
    return {
      tasks: this.loadData(),
      log_popup: false,
      taskExecution: null,
      nomeServer: '',
      nomeTask: '',
      oraEsecuzione: '',
      listaLog: []
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
      this.$axios.get('/api/command/taskExecutionForToday')
        .then((response) => {
          this.tasks = response.data
        })
        .catch(() => {
          this.$q.notify({
            color: 'negative',
            position: 'top',
            message: 'Loading failed',
            icon: 'report_problem'
          })
        })
    },
    showLog (taskId) {
      this.log_popup = true
      this.loadLogData(taskId)
    },
    closeLog () {
      this.log_popup = false
      this.taskExecution = null
      this.nomeServer = ''
      this.nomeTask = ''
      this.oraEsecuzione = ''
    },
    loadLogData (taskId) {
      console.log(taskId)
      this.$axios.post('/api/command/taskLogForExecution', {
        executionId: taskId
      })
        .then((response) => {
          this.taskExecution = response.data
          this.nomeServer = this.taskExecution.nomeServer
          this.nomeTask = this.taskExecution.nomeTask
          this.oraEsecuzione = this.taskExecution.orarioEsecuzione
          this.listaLog = this.taskExecution.listaLog
        })
        .catch(() => {
        })
    }
  }
}
</script>
<style scoped>
.caption {
  color: grey;
  font-size: smaller;
}
</style>
