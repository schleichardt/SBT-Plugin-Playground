import sbt._
import Keys._

object DemoBuild extends Build {

    //run other test with sbt other:test
    val otherScopeName = "other"
    val OtherTestScope = config(otherScopeName) extend(Test) intransitive

    val specs2 = "org.specs2" %% "specs2" % "1.12.1"

    val root = Project("hello", base = file(".")).
      settings(inConfig(OtherTestScope)(Defaults.testSettings):_*).
      settings(libraryDependencies += specs2).
     //sbt other:test executes tests in src/testOfOtherCategory/scala
      settings(sourceDirectory in OtherTestScope <<= sourceDirectory apply (_ / "testOfOtherCategory")
    )
//.configs( OtherTestScope) //buggy, uses not correct test folder, so specs2 dependency cannot be scoped
}