package org.eminux.intime.agent

import org.eminux.intime.agent.communication.taskExecution.TaskExecutionManager
import org.eminux.intime.agent.communication.topology.TopologyManager
import org.eminux.intime.agent.configuration.ConfigurationManager
import org.eminux.intime.agent.configuration.NetworkNode
import org.eminux.intime.agent.utils.Logger
import org.yaml.snakeyaml.Yaml

class Agent {

    public static void main(args){
        if(args.size()==0){
            println("1. NON E' STATO PASSATO NESSUN FILE DI CONFIGURAZIONE COME ARGOMENTO DI QUESTO PROGRAMMA")
            System.exit(1)
        }
        println(args[0])
        def configFile = new File(args[0])
        if(!configFile.exists()){
            println("2. NON E' STATO PASSATO NESSUN FILE DI CONFIGURAZIONE COME ARGOMENTO DI QUESTO PROGRAMMA")
            System.exit(1)
        }
        InputStream configInputStream = new FileInputStream(configFile)
        Yaml configFileYml = new Yaml()
        Map config = configFileYml.load(configFile.text)
        println 'nomeAgent: '+config.nomeAgent
        println 'ipAgent: '+config.ipAgent
        println 'redis: '+config.redis
        println 'logFolder: '+config.logFolder

        Logger logger = Logger.getInstance()
        logger.setOutputFolder(config.logFolder)
        logger.setAutoRotate(true)

        logger.log(Logger.INFO, "***** Starting InTimeAgent *****")
        logger.log(Logger.INFO, "----- Configuration -----")
        logger.log(Logger.INFO, 'nomeAgent: '+config.nomeAgent)
        logger.log(Logger.INFO, 'ipAgent: '+config.ipAgent)
        logger.log(Logger.INFO, 'redis: '+config.redis)
        logger.log(Logger.INFO, 'logFolder: '+config.logFolder)

        NetworkNode node = new NetworkNode()
        node.nome = config.nomeAgent
        node.ipAddress = config.ipAgent
        ConfigurationManager configManager = ConfigurationManager.getInstance()
        configManager.setNetworkNode(node)
        configManager.setRedisAddress(config.redis.trim())


        logger.log(Logger.INFO, 'Starting TopologyManager - connecting to the InTime Network')
        //faccio partire il topology manager
        TopologyManager manager = new TopologyManager()
        manager.start()
        //Attendo che il topology manager sia partito
        System.sleep(1000);
        //attivo il gestore dei comandi
        TaskExecutionManager tem = new TaskExecutionManager()
        //invia il messaggio in cui comunica di essere pronto a ricevere comandi
        //si sottoscrive al proprio canale da cui può ricevere comandi
        tem.init();
        logger.log(Logger.INFO, 'TopologyManager started - this agent is up and running')
    }

}
