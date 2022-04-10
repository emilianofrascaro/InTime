<template>
    <q-page class="">
        <div class="row q-my-sm">
            <div class="q-mx-auto text-h5 text-primary">Lista Task</div>
        </div>
        <div class="q-pa-md q-gutter-sm">
            <q-btn label="Nuovo" color="primary" @click="nuovoTask = true" />
        </div>
        <div>
            <div>
                <q-list bordered separator padding class="q-pa-sm">
                    <q-item  v-for="task in this.tasks" v-bind="task" :key="task.command">
                        <q-item-section top>
                            <q-item-label>{{task.descrizione}}</q-item-label>
                            <q-item-label caption>{{task.command}}</q-item-label>
                        </q-item-section>
                        <q-item-section top side>
                        <div class="text-grey-8 q-gutter-xs">
                          <q-chip>
                            <q-avatar color="green" text-color="white" v-if="task.numSchedulazioniAttive !== 0">{{task.numSchedulazioniAttive}}</q-avatar>
                            <q-avatar color="red" text-color="black" v-else>0</q-avatar>
                            Attive
                          </q-chip>
                            <q-btn size="12px" flat dense round icon="play_circle_outline" @click="runnow(task.id)"/>
                            <q-btn size="12px" flat dense round icon="add_alarm" @click="showSchedule(task.id, task.descrizione)"/>
                            <q-btn size="12px" flat dense round icon="list" @click="showListaSchedulazioni(task.id, task.descrizione)"/>
                            <q-btn size="12px" flat dense round icon="delete_outline" v-if="task.numSchedulazioniAttive == 0" @click="archiviaSchedulazioni(task.id, task.descrizione)"/>
                        </div>
                        </q-item-section>
                    </q-item>
                </q-list>
            </div>
            <div>
                <q-dialog v-model="listaSchedulazioniPopup">
                    <q-card style="min-width: 350px">
                        <q-card-section>
                        <div class="text-h6">Schedulazioni per il task "{{schedulazioniTaskName}}"</div>
                        </q-card-section>

                        <q-card-section class="q-pt-none">
                          <q-list bordered separator padding class="q-pa-sm">
                            <q-item  v-for="schedul in this.listaSchedulazioniPerTask" v-bind="schedul" :key="schedul.id">
                                <q-item-section top>
                                    <q-item-label>{{schedul.cronExpression}}</q-item-label>
                                    <q-item-label caption>{{schedul.serverName}}</q-item-label>
                                </q-item-section>
                                <q-item-section top side>
                                <div class="q-pa-xs q-gutter-sm">
                                  <q-chip size='sm' color="green" v-if="schedul.statusCode === 0"></q-chip>
                                  <q-btn size="12px" flat dense round icon="stop" v-if='schedul.statusCode === 0' @click='stopSchedulazione(schedul.schedulazioneId, schedulazioniTaskId)'/>
                                  <q-chip size='sm' color="red" v-if="schedul.statusCode === 1"></q-chip>
                                  <q-btn size="12px" flat dense round icon="play_arrow" v-if='schedul.statusCode === 1' @click="startSchedulazione(schedul.schedulazioneId, schedulazioniTaskId)"/>
                                  <q-btn size="12px" flat dense round icon="delete" v-if='schedul.statusCode === 1' @click="deleteSchedulazione(schedul.schedulazioneId, schedulazioniTaskId)"/>
                                  <q-icon name="autorenew" class="text-green" style="font-size: 1.5rem;"  v-if="taskScheduling" />
                                  <q-icon name="warning" class="text-red" style="font-size: 1.5rem;" v-if="taskSchedulingProblem"/>
                                </div>
                                </q-item-section>
                            </q-item>
                          </q-list>
                        </q-card-section>

                        <q-card-actions align="right" class="text-primary">
                        <q-btn flat label="Cancel" @click="closeListaSchedulazioni" />
                        </q-card-actions>
                    </q-card>
                </q-dialog>
            </div>
            <div>
                <q-dialog v-model="nuovoTask">
                    <q-card style="min-width: 350px">
                      <q-form ref="newTask">
                        <q-card-section class="q-pt-none">
                            <div class="text-h6">Inserisci i dati del nuovo task</div>
                        </q-card-section>
                        <q-card-section class="q-pt-none">
                        <q-input dense v-model="descrizione" autofocus label="Nome task" />
                        </q-card-section>

                        <q-card-section class="q-pt-none">
                        <q-input dense v-model="command" label="Comando"/>
                        </q-card-section>

                        <q-card-section class="q-pt-none" v-if="newTaskError">
                            <div>Si e' verificato un errore, controlla i dati immessi</div>
                        </q-card-section>

                        <q-card-actions align="right" class="text-primary">
                        <q-btn flat label="Cancel" @click="closeNewTaskForm"/>
                        <q-btn flat label="Salva" type="submit" color="primary" @click="createNewTask" />
                        </q-card-actions>
                      </q-form>
                    </q-card>
                </q-dialog>
            </div>
            <div>
                <q-dialog v-model="runTask">
                    <q-card style="min-width: 350px">
                        <q-card-section class="q-pt-none">
                            <div class="text-h6">Scegli il server su cui lanciare il task</div>
                        </q-card-section>
                        <q-card-section>
                            <NetworkStateElement v-for="nelement in networkElements" v-bind="nelement" :key="nelement.nome" @click="selectServer(nelement.nome)" />
                        </q-card-section>
                        <div v-if="serverSelected" class="q-ma-sm">
                            Hai selezionato {{nomeServer}}
                        </div>
                        <q-card-actions align="right" class="text-primary">
                        <q-btn flat label="Cancel" @click="closeRunTask"/>
                        <q-btn flat label="Lancia" color="green" v-if="serverSelected" @click="runNowTask"/>
                        <q-btn flat label="Lancia" color="grey" v-else disable/>
                        <q-icon name="autorenew" class="text-green" style="font-size: 1.5rem;"  v-if="taskRunning" />
                        <q-icon name="warning" class="text-red" style="font-size: 1.5rem;" v-if="taskRunningProblem"/>
                        </q-card-actions>
                    </q-card>
                </q-dialog>
            </div>
            <div>
                <q-dialog v-model="scheduleTask">
                    <q-card style="min-width: 350px">
                        <q-card-section class="q-pt-none">
                            <div class="text-h6">Scegli il server su cui schedulare il task "{{taskToSchedule}}"</div>
                        </q-card-section>
                        <q-card-section>
                            <NetworkStateElement v-for="nelement in networkElements" v-bind="nelement" :key="nelement.nome" @click="selectScheduleServer(nelement.nome)" />
                        </q-card-section>
                        <q-card-section>
                        <div v-if="serverScheduleSelected" class="text-primary q-ma-sm">
                            Hai selezionato {{nomeScheduleServer}}
                        </div>
                        </q-card-section>
                        <q-card-section>
                            <div class="q-pa-sm">
                              <div class="q-gutter-y-md">
                                <q-btn-toggle
                                  v-model="tipo_schedulazione"
                                  spread
                                  class=""
                                  no-caps
                                  unelevated
                                  toggle-color="primary"
                                  color="white"
                                  text-color="primary"
                                  :options="[
                                    {label: 'Ricorrente', value: 'ricorrente'},
                                    {label: 'Una tantum', value: 'una-tantum'}
                                    ]"
                                />
                              </div>
                            </div>
                        </q-card-section>
                        <q-card-section v-if="tipo_schedulazione==='ricorrente'">
                            <div class="row">
                                <div class="col">
                                    <q-select filled v-model="minuto" class="q-px-sm" multiple :options="minuti" label="Minuti" style="width: 110px"/>
                                </div>
                                <div class="col">
                                    <q-select filled v-model="ora" class="q-px-sm" multiple :options="ore" label="Ora" style="width: 110px"/>
                                </div>
                                <div class="col">
                                    <q-select filled v-model="giornomese" class="q-px-sm" multiple :options="giornimese" label="Giorno" style="width: 110px"/>
                                </div>
                                <div class="col">
                                    <q-select filled v-model="mese" class="q-px-sm" multiple :options="mesi" label="Mese" style="width: 110px"/>
                                </div>
                                <div class="col">
                                    <q-select filled v-model="giornosettimana" class="q-px-sm" multiple :options="giornisettimana" label="Giorno Sett" style="width: 110px"/>
                                </div>
                            </div>
                        </q-card-section>
                        <q-card-section v-else>
                            <div class="q-pa-sm" style="color:grey;">Non ancora implementato!</div>
                        </q-card-section>
                        <q-card-section v-if='schedulazione_text !== ""'>
                            Schedulazione prevista: {{ schedulazione_text }}
                        </q-card-section>
                        <q-card-actions align="right" class="text-primary">
                        <q-btn flat label="Cancel" @click="closeScheduleTask"/>
                        <q-btn flat label="Schedula" color="green" v-if="schedule_possible" @click="scheduleNowTask"/>
                        <q-btn flat label="Schedula" color="grey" v-else disable/>
                        <q-icon name="autorenew" class="text-green" style="font-size: 1.5rem;"  v-if="taskScheduling" />
                        <q-icon name="warning" class="text-red" style="font-size: 1.5rem;" v-if="taskSchedulingProblem"/>
                        </q-card-actions>
                    </q-card>
                </q-dialog>
            </div>
        </div>
        </q-page>
</template>
<script>
import NetworkStateElement from 'components/NetworkStateElement'
export default {
  name: 'Task',
  components: {
    NetworkStateElement
  },
  computed: {
    schedule_possible: function () {
      if (this.tipo_schedulazione === 'ricorrente') {
        if (this.nomeScheduleServer !== null && this.minuto !== null && this.ora !== null && this.giornomese !== null && this.mese !== null && this.giornosettimana !== null) {
          return true
        } else {
          return false
        }
      } else {
        return false
      }
    },
    schedulazione_text: function () {
      let testo = ''
      if (this.minuto !== null && this.minuto.length > 0) {
        if (this.minuto[0] === '*') {
          testo = testo + 'Ogni minuto'
        } else {
          testo = testo + ' nei minuti ' + this.minuto
        }
      }
      if (this.ora !== null && this.ora.length > 0) {
        if (this.ora[0] === '*') {
          testo = testo + ' di ogni ora'
        } else {
          testo = testo + ' delle ore ' + this.ora
        }
      }
      if (this.giornomese !== null && this.giornomese.length > 0) {
        if (this.giornomese[0] === '*') {
          testo = testo + ' di ogni giorno del mese'
        } else {
          testo = testo + ' nei giorni ' + this.giornomese
        }
      }
      if (this.mese !== null && this.mese.length > 0) {
        if (this.mese[0] === '*') {
          testo = testo + ' di ogni mese'
        } else {
          testo = testo + ' nei mesi ' + this.mese
        }
      }
      return testo
    }
  },
  data () {
    return {
      tasks: this.loadData(),
      networkElements: this.loadNetworkElementData(),
      command: null,
      descrizione: null,
      nuovoTask: false,
      prompt: false,
      newTaskError: false,
      runTask: false,
      taskIdToRun: null,
      serverSelected: false,
      taskRunning: false,
      taskRunningProblem: false,
      scheduleTask: false,
      taskIdToSchedule: null,
      serverScheduleSelected: false,
      nomeScheduleServer: null,
      taskScheduling: false,
      taskSchedulingProblem: false,
      tipo_schedulazione: 'ricorrente',
      minuti: ['*', 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59],
      ore: ['*', 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24],
      giornimese: ['*', 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31],
      mesi: ['*', 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12],
      giornisettimana: ['*', 'Lunedì', 'Martedì', 'Mercoledì', 'Giovedì', 'Venerdì', 'Sabato', 'Domenica'],
      minuto: null,
      ora: null,
      giornomese: null,
      mese: null,
      anno: null,
      giornosettimana: null,
      taskToSchedule: null,
      schedulazioniTaskName: '',
      schedulazioniTaskId: null,
      listaSchedulazioniPopup: false,
      listaSchedulazioniPerTask: null
    }
  },
  created () {
    this.loadData()
    this.timer = setInterval(this.loadData, 10000)
    this.loadNetworkElementData()
    this.timer2 = setInterval(this.loadNetworkElementData, 10000)
  },
  beforeDestroy () {
    clearInterval(this.timer)
    clearInterval(this.timer2)
  },
  methods: {
    loadData () {
      console.log('refreshing data')
      this.$axios.get('/api/command/listTask')
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
    createNewTask () {
      this.$refs.newTask.validate().then(success => {
        this.newTaskError = false
        console.log('sono dentro submit')
        const self = this
        if (success) {
          console.log('sono dentro success')
          this.$axios.post('/api/command/saveTask', {
            descrizione: self.descrizione,
            comando: self.command
          })
            .then((response) => {
              console.log('sono dentro then')
              this.$refs.newTask.resetValidation()
              this.closeNewTaskForm()
              this.tasks = this.loadData()
            }).catch(() => {
              this.newTaskError = true
            })
        } else {
          this.newTaskError = true
        }
      })
    },
    closeNewTaskForm () {
      this.nuovoTask = false
    },
    runnow (id) {
      console.log('Chiamato il runnow su ' + id)
      this.runTask = true
      this.taskIdToRun = id
    },
    closeRunTask () {
      this.runTask = false
      this.taskIdToRun = null
      this.serverSelected = false
      this.nomeServer = null
    },
    loadNetworkElementData () {
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
    },
    selectServer (nomeServer) {
      console.log(nomeServer)
      this.serverSelected = true
      this.nomeServer = nomeServer
    },
    runNowTask () {
      this.taskRunning = true
      this.taskRunningProblem = false
      console.log('chiamato runNowTask')
      const self = this
      this.$axios.post('/api/command/runNowTask', {
        idComando: self.taskIdToRun,
        server: self.nomeServer
      })
        .then((response) => {
          console.log('sono dentro then')
          self.closeRunTask()
          self.taskRunning = false
          self.taskRunningProblem = false
        }).catch(() => {
          console.log('si e\' verificato un problema')
          self.taskRunning = false
          self.taskRunningProblem = true
        })
    },
    showSchedule (id, taskToSchedule) {
      this.scheduleTask = true
      this.taskIdToSchedule = id
      this.taskToSchedule = taskToSchedule
    },
    closeScheduleTask () {
      this.scheduleTask = false
      this.taskIdToSchedule = null
      this.serverScheduleSelected = false
      this.nomeScheduleServer = null
      this.minuto = null
      this.ora = null
      this.giornomese = null
      this.mese = null
      this.anno = null
      this.giornosettimana = null
      this.taskToSchedule = null
      this.crontab = null
      this.loadData()
    },
    selectScheduleServer (nomeServer) {
      console.log(nomeServer)
      this.serverScheduleSelected = true
      this.nomeScheduleServer = nomeServer
    },
    scheduleNowTask () {
      console.log('schedulo')
      this.taskScheduling = true
      this.taskSchedulingProblem = false
      this.crontab = this.calculateCrontab()
      const self = this
      this.$axios.post('/api/command/scheduleTask', {
        taskId: self.taskIdToSchedule,
        serverName: self.nomeScheduleServer,
        crontab: self.crontab
      })
        .then((response) => {
          console.log('sono dentro then' + response.data)
          if (response.data === 'OK') {
            self.closeScheduleTask()
            self.taskScheduling = false
            self.taskSchedulingProblem = false
          } else {
            console.log('si e\' verificato un problema')
            self.taskScheduling = false
            self.taskSchedulingProblem = true
          }
        }).catch(() => {
          console.log('si e\' verificato un problema')
          self.taskScheduling = false
          self.taskSchedulingProblem = true
        })
    },
    showListaSchedulazioni (taskId, taskName) {
      console.log('showListaSchedulazioni ' + taskId)
      this.listaSchedulazioniPopup = true
      this.schedulazioniTaskName = taskName
      this.schedulazioniTaskId = taskId
      this.loadListaSchedulazioni(taskId)
    },
    loadListaSchedulazioni (taskId) {
      console.log(taskId)
      this.$axios.post('/api/command/listOfSchedulePerTask', {
        taskId: taskId
      })
        .then((response) => {
          this.listaSchedulazioniPerTask = response.data
        }).catch(() => {
        })
    },
    closeListaSchedulazioni () {
      this.listaSchedulazioniPopup = false
      this.schedulazioniTaskName = null
      this.schedulazioniTaskId = null
      this.listaSchedulazioniPerTask = null
      this.loadData()
    },
    stopSchedulazione (schedulazioneId, taskId) {
      this.schedulazioniTaskId = taskId
      console.log(taskId)
      const self = this
      this.$axios.post('/api/command/unscheduleTask', {
        schedulazioneId: schedulazioneId
      })
        .then((response) => {
          console.log('sono dentro then ' + response.data)
          if (response.data === 'OK') {
            self.closeScheduleTask()
            self.taskScheduling = false
            self.taskSchedulingProblem = false
            this.loadListaSchedulazioni(self.schedulazioniTaskId)
            this.loadData()
          } else {
            console.log('si e\' verificato un problema')
            self.taskScheduling = false
            self.taskSchedulingProblem = true
          }
        }).catch(() => {
          console.log('si e\' verificato un problema catch')
          self.taskScheduling = false
          self.taskSchedulingProblem = true
        })
    },
    startSchedulazione (schedulazioneId, taskId) {
      this.schedulazioniTaskId = taskId
      console.log(taskId)
      const self = this
      this.$axios.post('/api/command/rescheduleTask', {
        schedulazioneId: schedulazioneId
      })
        .then((response) => {
          console.log('sono dentro then ' + response.data)
          if (response.data === 'OK') {
            self.closeScheduleTask()
            self.taskScheduling = false
            self.taskSchedulingProblem = false
            this.loadListaSchedulazioni(self.schedulazioniTaskId)
            this.loadData()
          } else {
            console.log('si e\' verificato un problema')
            self.taskScheduling = false
            self.taskSchedulingProblem = true
          }
        }).catch(() => {
          console.log('si e\' verificato un problema catch')
          self.taskScheduling = false
          self.taskSchedulingProblem = true
        })
    },
    deleteSchedulazione (schedulazioneId, taskId) {
      this.schedulazioniTaskId = taskId
      console.log(taskId)
      const self = this
      this.$axios.post('/api/command/deleteSchedulazione', {
        schedulazioneId: schedulazioneId
      })
        .then((response) => {
          console.log('sono dentro then ' + response.data)
          if (response.data === 'OK') {
            self.closeScheduleTask()
            self.taskScheduling = false
            self.taskSchedulingProblem = false
            this.loadListaSchedulazioni(self.schedulazioniTaskId)
          } else {
            console.log('si e\' verificato un problema')
            self.taskScheduling = false
            self.taskSchedulingProblem = true
          }
        }).catch(() => {
          console.log('si e\' verificato un problema catch')
          self.taskScheduling = false
          self.taskSchedulingProblem = true
        })
    },
    calculateCrontab () {
      let crontab = '0 '
      if (this.minuto !== null && this.minuto.length > 0) {
        if (this.minuto[0] === '*') {
          crontab = crontab + '* '
        } else {
          let isLast = false
          if (this.minuto.lenght === 1) {
            isLast = true
          }
          for (let i = 0; i < this.minuto.length; i++) {
            if (i === this.minuto.length - 1) {
              isLast = true
            }
            crontab = crontab + this.minuto[i]
            if (!isLast) {
              crontab = crontab + ','
            } else {
              crontab = crontab + ' '
            }
          }
        }
      }
      if (this.ora !== null && this.ora.length > 0) {
        if (this.ora[0] === '*') {
          crontab = crontab + '* '
        } else {
          let isLast = false
          if (this.ora.lenght === 1) {
            isLast = true
          }
          for (let i = 0; i < this.ora.length; i++) {
            if (i === this.ora.length - 1) {
              isLast = true
            }
            crontab = crontab + this.ora[i]
            if (!isLast) {
              crontab = crontab + ','
            } else {
              crontab = crontab + ' '
            }
          }
        }
      }
      if (this.giornomese !== null && this.giornomese.length > 0) {
        if (this.giornomese[0] === '*') {
          crontab = crontab + '? '
        } else {
          let isLast = false
          if (this.giornomese.lenght === 1) {
            isLast = true
          }
          for (let i = 0; i < this.giornomese.length; i++) {
            if (i === this.giornomese.length - 1) {
              isLast = true
            }
            crontab = crontab + this.giornomese[i]
            if (!isLast) {
              crontab = crontab + ','
            } else {
              crontab = crontab + ' '
            }
          }
        }
      }
      if (this.mese !== null && this.mese.length > 0) {
        if (this.mese[0] === '*') {
          crontab = crontab + '* '
        } else {
          let isLast = false
          if (this.mese.lenght === 1) {
            isLast = true
          }
          for (let i = 0; i < this.mese.length; i++) {
            if (i === this.mese.length - 1) {
              isLast = true
            }
            crontab = crontab + this.mese[i]
            if (!isLast) {
              crontab = crontab + ','
            } else {
              crontab = crontab + ' '
            }
          }
        }
      }
      if (this.giornosettimana !== null && this.giornosettimana.length > 0) {
        if (this.giornosettimana[0] === '*') {
          crontab = crontab + '?'
        } else {
          let isLast = false
          if (this.giornosettimana.lenght === 1) {
            isLast = true
          }
          for (let i = 0; i < this.giornosettimana.length; i++) {
            if (i === this.giornosettimana.length - 1) {
              isLast = true
            }
            if (this.giornosettimana[i] === 'Lunedì') {
              crontab = crontab + 'MON'
            }
            if (this.giornosettimana[i] === 'Martedì') {
              crontab = crontab + 'TUE'
            }
            if (this.giornosettimana[i] === 'Mercoledì') {
              crontab = crontab + 'WED'
            }
            if (this.giornosettimana[i] === 'Giovedì') {
              crontab = crontab + 'THU'
            }
            if (this.giornosettimana[i] === 'Venerdì') {
              crontab = crontab + 'FRI'
            }
            if (this.giornosettimana[i] === 'Sabato') {
              crontab = crontab + 'SAT'
            }
            if (this.giornosettimana[i] === 'Domenica') {
              crontab = crontab + 'SUN'
            }
            if (!isLast) {
              crontab = crontab + ','
            }
          }
        }
      }
      return crontab
    },
    archiviaSchedulazioni (taskId, taskDescrizione) {
      console.log('')
      const self = this
      this.$axios.post('/api/command/archiviaTask', {
        taskId: taskId
      })
        .then((response) => {
          console.log('sono dentro then')
          self.loadData()
        }).catch(() => {
          console.log('si e\' verificato un problema')
        })
    }
  }
}

</script>
<style scoped>
</style>
