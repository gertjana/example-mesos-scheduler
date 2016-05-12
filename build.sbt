name := "mesos-scheduler"

organization := "net.addictivesoftware.mesos"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.apache.mesos" % "mesos" % "0.28.1"
)

enablePlugins(JavaAppPackaging)

// we specify the name for our fat jar
jarName in assembly := "example-mesos-framework.jar"

// removes all jar mappings in universal and appends the fat jar
mappings in Universal <<= (mappings in Universal, assembly in Compile) map { (mappings, fatJar) =>
  val filtered = mappings filter { case (file, name) =>  ! name.endsWith(".jar") }
  filtered :+ (fatJar -> ("lib/" + fatJar.getName))
}

// the bash scripts classpath only needs the fat jar
 scriptClasspath := Seq( (jarName in assembly).value )

