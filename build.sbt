scalaVersion := "2.12.3"

name := "zeta-types-db"

parallelExecution in Test := false

// Show warnings
scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-Ywarn-unused", "-Xlint")

// Dependencies
libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.4"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.4" % "test"

libraryDependencies += "org.json4s" %% "json4s-native" % "3.6.0-M1"

libraryDependencies += "org.mongodb.scala" %% "mongo-scala-driver" % "2.3.0"

libraryDependencies += "org.scala-lang" % "scala-compiler" % "2.12.3"

//dependsOn(RootProject(uri("git://github.com/torstein-vik/zeta-types-scala.git#1.0.x")))
