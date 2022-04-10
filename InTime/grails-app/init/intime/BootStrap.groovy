package intime

import org.eminux.intime.*
import org.eminux.intime.networkTopology.NetworkTopology
import org.eminux.intime.taskExecutor.TaskManager

class BootStrap {

    def init = { servletContext ->
        /*Thread t = new Thread() {
            @Override
            public void run() {
                InTimeCommunication itc = InTimeCommunication.getInstance()
                itc.subscribe("intime.channel.hello_world")
            }
        }.start()*/
        //Faccio partire il gestore della rete di Agenti
        NetworkTopology itnt = new NetworkTopology()
        itnt.start()
        //inizializzo il gestore dei comandi
        TaskManager tm = TaskManager.instance
        tm.start()

    }
    def destroy = {
    }
}
