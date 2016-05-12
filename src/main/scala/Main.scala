import org.apache.mesos.MesosSchedulerDriver
import org.apache.mesos.Protos
import org.apache.mesos.Protos.FrameworkInfo
import org.apache.mesos.Protos.ExecutorInfo
import org.apache.mesos.Protos.CommandInfo
import org.apache.mesos.Scheduler

object Main extends App {
  if (args.length != 1) {
    println("please provide mesos master as argument")
    sys.exit(1)
  }

  val mesosMaster = args.head

  val frameworkName = "framework-example"
  val executorName = "ExampleExecutor"
  val path = sys.env.get("MESOS_EXAMPLE_FRAMEWORK_PATH").getOrElse("path/to/jar")
  val command = sys.env.get("MESOS_EXAMPLE_FRAMEWORK_COMMAND").getOrElse("path/to jar ExampleExecutor")

  private def getFrameworkInfo = FrameworkInfo.newBuilder()
                                  .setFailoverTimeout(120000)
                                  .setUser("")
                                  .setName(frameworkName)
                                  .build

  private def getUri = CommandInfo.URI.newBuilder()
                        .setValue(path)
                        .setExtract(false)
                        .build

  private def getCommandInfo = Protos.CommandInfo.newBuilder()
                                .addUris(getUri)
                                .setValue(command)
                                .build

  private def getExecutorInfo = ExecutorInfo.newBuilder
                                  .setExecutorId(Protos.ExecutorID.newBuilder().setValue(executorName))
                                  .setCommand(getCommandInfo)
                                  .setName(executorName)
                                  .setSource("java")
                                  .build

  val scheduler = new ExampleScheduler(getExecutorInfo)
  val driver = new MesosSchedulerDriver(scheduler, getFrameworkInfo, mesosMaster)
  val status = if (driver.run() == Protos.Status.DRIVER_STOPPED) 0 else 1

  driver.stop()
  sys.exit(status)

}
