
import org.apache.mesos.Executor
import org.apache.mesos.ExecutorDriver
import org.apache.mesos.MesosExecutorDriver
import org.apache.mesos.Protos
import org.apache.mesos.Protos._

import scala.util.Random

class ExampleExecutor extends Executor {
  override def shutdown(driver: ExecutorDriver) = {}

  override def disconnected(driver: ExecutorDriver) = {}

  override def killTask(driver: ExecutorDriver, taskId: TaskID) = {}

  override def reregistered(driver: ExecutorDriver, slaveInfo: SlaveInfo) = {}

  override def error(driver: ExecutorDriver, message: String) = {}

  override def frameworkMessage(driver: ExecutorDriver, data: Array[Byte]) = {}

  override def registered(driver: ExecutorDriver, executorInfo: ExecutorInfo, frameworkInfo: FrameworkInfo, slaveInfo: SlaveInfo) = {}

  override def launchTask(driver: ExecutorDriver, task: TaskInfo) = {
    val id = task.getData.toString("UTF-8")
    driver.sendFrameworkMessage(id.getBytes)
    val status = Protos.TaskStatus.newBuilder
        .setTaskId(task.getTaskId)
        .setState(Protos.TaskState.TASK_FINISHED).build
    Thread.sleep(Math.abs(Random.nextInt() % 3000)+1000)
    driver.sendStatusUpdate(status)
  }
}

object ExampleExecutor extends App {
  val driver = new MesosExecutorDriver(new ExampleExecutor)
  val status = if (driver.run() == Protos.Status.DRIVER_STOPPED) 0 else 1
  sys.exit(status)
}

