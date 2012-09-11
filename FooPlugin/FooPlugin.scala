import sbt._
import Keys._

object FooPlugin extends Plugin { //see http://harrah.github.com/xsbt/latest/api/#sbt.Plugin
  override lazy val settings = Seq(commands ++= Seq(helloCommand, helloCommandWithArgsFromProject, callOtherCommandFromSbt, callOtherTaskFromPlugin, commandWithArguments))

  lazy val helloCommand = 
    Command.command("hello", "Simple Hello World Command", "detailed description of hello") { (state: State) =>
      println("Hi!  ") 
      state
    }


  lazy val helloCommandWithArgsFromProject = 
    Command.command("hello2", "bla 2", "blubb 2") { (state: State) =>
      println("Hi!  " + projectSettings + " global:" + globalSettings + " build:" + buildSettings + " name: " + Keys.name) 

      val extracted = Project extract state
      println(extracted.get(name)) //needs import Keys._
      println(extracted.get(sbtVersion))
      println(extracted.get(scalaVersion))    
      println(extracted.get(version))   
      println(extracted.get(newSetting))  //this is a plugin SettingKey  
      state
    }

   lazy val callOtherCommandFromSbt =  Command.command("hello7", "bla 7", "blubb 7") { (state: State) =>
        val taskKey = Keys.compile in Compile

        println("compiling")

        // Evaluate the task
        // None if the key is not defined
        // Some(Inc) if the task does not complete successfully (Inc for incomplete)
        // Some(Value(v)) with the resulting value
        val result: Option[(State, Result[_])] = Project.runTask(taskKey, state, true)
        // handle the result
        (result map (_._2)) match
        {
                case None => println("Key wasn't defined.")
                case Some(Inc(inc)) => println(Incomplete.show(inc.tpe))
                case Some(Value(v)) => println("value: " + v)
        }

    state //ggf. muss state aus dem result verwendet werden

  }

   lazy val callOtherTaskFromPlugin =  Command.command("hello8", "bla 8", "blubb 8") { (state: State) =>
        println("calling hello4")

        val result: Option[(State, Result[_])] = Project.runTask(newTask2, state, true)
        // handle the result
        (result map (_._2)) match
        {
                case None => println("Key wasn't defined.")
                case Some(Inc(inc)) => println(Incomplete.show(inc.tpe))
                case Some(Value(v)) => println("value: " + v)
        }

    state

  }

    //call: sbt "hello9 arg1 arg2 arg3"
   val commandWithArguments: Command = Command.args("hello9", "Hinweis bei Tab completion") { (state, args) =>
    println("command with args: " + args.mkString(", "))
    state
  }


    val newTask = TaskKey[Unit]("hello3", description ="noch ein Task mit eigenen Settings")
    val newTask2 = TaskKey[Unit]("hello4", description ="noch ein Task mit 2 eigenen Settings")
    val newTask3 = TaskKey[Unit]("hello5", description ="app-Daten auslesen")
    val newTask4 = TaskKey[Unit]("hello6", description ="app-Daten auslesen 2")
    val newSetting = SettingKey[String]("new-setting")
    val newSetting2 = SettingKey[String]("new-setting2")

    // a group of settings ready to be added to a Project
    // to automatically add them, do 
    val newSettings = Seq(
            newSetting := "test", //kann in build.sbt der App überschrieben werden,
            newSetting2 := "default",
            newTask <<= newSetting map { str => println(str) },
            newTask2 <<= (newSetting, newSetting2) map { (set1, set2) => println(set1 + " " + set2) }, //vor map als tuple, und als anonyme Funktion auch wie Tuple aufgebaut
            newTask3 <<= name map { x => println("TODO"  + x) } // globale Einstellung nutzen
            //,newTask4 <<= {println("TODO" ) } // globale Einstellung nutzen
    )
} 
