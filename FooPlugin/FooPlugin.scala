import sbt._
import Keys._

object FooPlugin extends Plugin { //see http://harrah.github.com/xsbt/latest/api/#sbt.Plugin
  override lazy val settings = Seq(commands ++= Seq(helloCommand, helloCommandWithArgsFromProject))

  lazy val helloCommand = 
    Command.command("hello", "Simple Hello World Command", "detailed description of hello") { (state: State) =>
      println("Hi!  ") 
      state
    }


  lazy val helloCommandWithArgsFromProject = 
    Command.command("hello2", "bla 2", "blubb 2") { (state: State) =>
      println("Hi!  " + projectSettings + " global:" + globalSettings + " build:" + buildSettings) 
      state
    }

    val newTask = TaskKey[Unit]("hello3", description ="noch ein Task mit eigenen Settings")
    val newTask2 = TaskKey[Unit]("hello4", description ="noch ein Task mit 2 eigenen Settings")
    val newTask3 = TaskKey[Unit]("hello5", description ="app-Daten auslesen")
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
    )
} 
