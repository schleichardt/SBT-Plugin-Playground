import sbt._
import Keys._

object DemoBuild extends Build {

    //run other test with sbt otherTest:test
    val otherTestScopeName = "otherTest"

    val OtherTestScope = config(otherTestScopeName).extend(Test)

    val specs2 = "org.specs2" %% "specs2" % "1.12.1"

    val root = Project("hello", base = file(".")).settings( 
       inConfig(OtherTestScope)(Defaults.testSettings):_*
     ).settings(
      libraryDependencies += specs2 //TODO only load in test and otherTest

      // tests are in src/testOfOtherCategory/scala
      ,sourceDirectory in OtherTestScope <<= sourceDirectory apply (_ / "testOfOtherCategory")
    )
}