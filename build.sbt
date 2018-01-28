scalaVersion := "2.12.3"

name := "zeta-types-db"

parallelExecution in Test := false

// Show warnings
scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-Ywarn-unused", "-Xlint")

// Dependencies
libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.4"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.4" % "test"

dependsOn(RootProject(uri("git://github.com/torstein-vik/zeta-types-scala.git#1.0.x")))
