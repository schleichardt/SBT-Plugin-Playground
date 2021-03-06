import java.io.FileInputStream
import javassist.ClassPool
import sbt._
import Keys._

/*
  please look at 
*/

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


  //some stuff is from play framework 2.0.3
  def PostCompile(scope: Configuration) = (sourceDirectory in scope, dependencyClasspath in scope, compile in scope, javaSource in scope, sourceManaged in scope, classDirectory in scope, scalaSource in scope) map { (src, deps, analysis, javaSrc, srcManaged, classes, scalaSource) =>

    val classpath = (deps.map(_.data.getAbsolutePath).toArray :+ classes.getAbsolutePath).mkString(java.io.File.pathSeparator)

    println("classpath=" + classpath)

    val javaClasses = (javaSrc ** "*.java").get.map { sourceFile =>
      analysis.relations.products(sourceFile)
    }.flatten.distinct

    println(javaClasses.mkString("Java classes:\n","\n", "\n"))

    val scalaClasses = (scalaSource ** "*.scala").get.map { sourceFile =>
      analysis.relations.products(sourceFile)
    }.flatten.distinct

    println(scalaClasses.mkString("Scala classes:\n", "\n", "\n"))


    val classPool = new ClassPool()
    classPool.appendSystemPath()
    classPool.appendPathList(classpath)

    for (scalaClassFile <- (scalaClasses) if !scalaClassFile.absolutePath.endsWith("FindMe.class")) {
      val is = new FileInputStream(scalaClassFile)
        val ctClass = classPool.makeClass(is)
        val output= ctClass.getDeclaredFields.foreach(f => println("+" + f.getFieldInfo.toString))

      println(output)
    }
    analysis
  }


  val newTask = TaskKey[Unit]("hello3", description = "noch ein Task mit eigenen Settings")
  val newTask2 = TaskKey[Unit]("hello4", description = "noch ein Task mit 2 eigenen Settings")
  val newTask3 = TaskKey[Unit]("hello5", description = "app-Daten auslesen")
  val newTask4 = TaskKey[Unit]("hello6", description = "app-Daten auslesen 2")//not assigned
  val newTask5 = TaskKey[Unit]("hello10", description = "depends on hello11")
  val newTask6 = TaskKey[String]("hello11", description = "is needed by hello10")
  val newSetting = SettingKey[String]("new-setting")
  val newSetting2 = SettingKey[String]("new-setting2")

  /*
  use := if you add a static value/function, that needs no arguments
  use <<= if you are dependend of other settings or tasks
  */

  def printlnNewSettingKeys(s1: String, s2: String) = println(s1 + " " + s2)
  
  // a group of settings ready to be added to a Project
  // to automatically add them, do
  val newSettings = Seq(
    newSetting := "test", //kann in build.sbt der App überschrieben werden,
    newSetting2 := "default",
    newTask <<= newSetting map { str => println(str)}, 
    newTask2 <<= (newSetting, newSetting2) map printlnNewSettingKeys,//you need only to write the function name, even if it has 2 parameters
    newTask3 <<= name map { x => println("TODO" + x) } // globale Einstellung nutzen
    , sourceGenerators in Compile <+= generateSourcesInitialization
    , compile in (Compile) <<= PostCompile(scope = Compile)
    , newTask6 := {println("hello11 called"); "resultOfTaskhello11"}
    , newTask5 <<= newTask6 map {value => println("hello10 received: " + value)}
  )
} 
