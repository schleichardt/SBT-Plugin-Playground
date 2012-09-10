import sbt._
import Keys._

object FooPlugin extends Plugin {
  override lazy val settings = Seq(commands += helloCommand)

  lazy val helloCommand = 
    Command.command("hello") { (state: State) =>
      println("Hi!  ") 
      state
    }
} 
