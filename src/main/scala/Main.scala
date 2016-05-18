import org.apache.mesos.MesosSchedulerDriver
import org.apache.mesos.Protos
import org.apache.mesos.Protos.FrameworkInfo
import org.apache.mesos.Protos.ExecutorInfo
import org.apache.mesos.Protos.CommandInfo
import org.apache.mesos.Scheduler

object Main extends App {
  println(args.length)
  if (args.length != 1) {
    println("please provide mesos master as argument")
    sys.exit(1)
  }

  val mesosMaster = args.head

  val frameworkName = "framework-example"
  val executorName = "ExampleExecutor"
  val path = sys.env.getOrElse("MESOS_EXAMPLE_FRAMEWORK_PATH", "/home/pi/example-mesos-framework.jar")
  val command = sys.env.getOrElse("MESOS_EXAMPLE_FRAMEWORK_COMMAND", "java -cp /home/pi/example-mesos-framework.jar ExampleExecutor")

  val frameworkInfo = FrameworkInfo.newBuilder()
                        .setFailoverTimeout(120000)
                        .setUser("pi")
                        .setName(frameworkName)
                        .build

  val uri = CommandInfo.URI.newBuilder()
              .setValue(path)
              .setExtract(false)
              .build

  val commandInfo = Protos.CommandInfo.newBuilder()
                      .addUris(uri)
                      .setValue(command)
                      .build

  val executorInfo = ExecutorInfo.newBuilder
                      .setExecutorId(Protos.ExecutorID.newBuilder().setValue(executorName))
                      .setCommand(commandInfo)
                      .setName(executorName)
                      .setSource("java")
                      .build

  val scheduler = new ExampleScheduler(executorInfo)
  val driver = new MesosSchedulerDriver(scheduler, frameworkInfo, mesosMaster)
  val status = if (driver.run() == Protos.Status.DRIVER_STOPPED) 0 else 1

  driver.stop()
  sys.exit(status)

}
