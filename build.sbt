name := "nani4s"

version := "0.1"

scalaVersion := "2.13.8"

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

libraryDependencies += "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.13.1"
libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value
libraryDependencies += "org.scalaj" %% "scalaj-http" % "2.4.2"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.10" % "test"
libraryDependencies += "org.mockito" % "mockito-core" % "2.7.19" % Test
libraryDependencies += "org.scalatestplus" %% "mockito-3-4" % "3.3.0.0-SNAP3" % Test
