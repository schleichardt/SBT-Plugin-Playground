import sbt._
import Keys._

object FooPlugin extends Plugin {  //see http://harrah.github.com/xsbt/latest/api/#sbt.Plugin

  override lazy val settings = Seq(commands ++= Seq(helloCommand, helloCommandWithArgsFromProject, callOtherCommandFromSbt, callOtherTaskFromPlugin, commandWithArguments))

  lazy val helloCommand = {
    val helloAction = (state: State) => { println("Hi!  "); state }
    val briefHelp = "Simple Hello World Command" //appears on sbt help
    val detailHelp = "detailed description of hello" //appears on sbt "help hello"
    val name = "hello" //command is called with sbt hello
    Command.command(name, briefHelp, detailHelp)(helloAction)
  }

  lazy val helloCommandWithArgsFromProject =
    Command.command("hello2") {
      (state: State) =>
        val extracted = Project extract state
        println(extracted.get(name)) //needs import Keys._ //=> SBT-Plugin-Test-App
        println(extracted.get(sbtVersion))//=> SBT-Plugin-Test-App
        println(extracted.get(scalaVersion))//=>SBT-Plugin-Test-App
        println(extracted.get(version))//SBT-Plugin-Test-App
        println(extracted.get(newSetting)) //this is a plugin SettingKey //=> light //with test app
        state
    }

  lazy val callOtherCommandFromSbt = Command.command("hello7") {
    (state: State) =>
      val taskKey = Keys.compile in Compile
      val result: Option[(State, Result[_])] = Project.runTask(taskKey, state, true)
      (result map (_._2)) match {
        case None => println("Key wasn't defined.")
        case Some(Inc(inc)) => println(Incomplete.show(inc.tpe))
        case Some(Value(v)) => println("value: " + v)
      }
      result match {
        case Some((newState, _)) => newState
        case None => state
      }
  }

  lazy val callOtherTaskFromPlugin = Command.command("hello8") {
    (state: State) =>
      println("calling hello4")
      val result: Option[(State, Result[_])] = Project.runTask(newTask2, state, true)
      result match {
        case Some((newState, _)) => newState
        case None => state
      }
  }

  //call: sbt "hello9 arg1 arg2 arg3"
  val commandWithArguments: Command = Command.args("hello9", "Hinweis bei Tab completion") {
    (state, args) => {println("command with args: " + args.mkString(", ")); state }
  }

  //http://www.scala-sbt.org/howto/generatefiles.html
  val generateSourcesInitialization =  sourceManaged in Compile map { dir =>
    val file = dir / "demo" / "Test.scala"
    println("generating file: " + file)
    IO.write(file, """object GeneratedClassWithVal { val aValue = "Hi from generated" }""")
    Seq(file) //return Seq of paths to generated files
  }

  val newTask = TaskKey[Unit]("hello3", description = "noch ein Task mit eigenen Settings")
  val newTask2 = TaskKey[Unit]("hello4", description = "noch ein Task mit 2 eigenen Settings")
  val newTask3 = TaskKey[Unit]("hello5", description = "app-Daten auslesen")
  val newTask4 = TaskKey[Unit]("hello6", description = "app-Daten auslesen 2")
  val newSetting = SettingKey[String]("new-setting")
  val newSetting2 = SettingKey[String]("new-setting2")

  // a group of settings ready to be added to a Project
  // to automatically add them, do
  val newSettings = Seq(
    newSetting := "test", //kann in build.sbt der App überschrieben werden,
    newSetting2 := "default",
    newTask <<= newSetting map { str => println(str)},
    newTask2 <<= (newSetting, newSetting2) map { (set1, set2) => println(set1 + " " + set2)},
    newTask3 <<= name map { x => println("TODO" + x) } // globale Einstellung nutzen
    //,newTask4 <<= {println("TODO" ) } // globale Einstellung nutzen
    , sourceGenerators in Compile <+= generateSourcesInitialization
  )
} 
