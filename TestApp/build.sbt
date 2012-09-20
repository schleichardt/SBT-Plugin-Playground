seq( FooPlugin.newSettings : _*)

seq(com.github.retronym.SbtOneJar.oneJarSettings: _*)

newSetting := "light" 

name := "SBT-Plugin-Test-App"

libraryDependencies += "com.google.guava" % "guava" % "13.0"

libraryDependencies += "com.google.code.findbugs" % "jsr305" % "1.3.9" //necessary for Google Guava in Scala

exportJars := true