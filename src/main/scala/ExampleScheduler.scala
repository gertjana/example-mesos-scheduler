import java.util

import com.google.protobuf.ByteString
import org.apache.mesos.Protos.{MasterInfo, Offer, _}
import org.apache.mesos.{Protos, Scheduler, SchedulerDriver}

import collection.JavaConversions._
import collection.JavaConverters._

class ExampleScheduler(val executorInfo: ExecutorInfo) extends Scheduler {
  var taskCounter = 0


  override def registered(driver: SchedulerDriver, frameworkId: FrameworkID, masterInfo: MasterInfo) = {
    println(s"Registered: $frameworkId")
  }

  override def reregistered(driver: SchedulerDriver, masterInfo: MasterInfo) = {
    println(s"Re-Registered")
  }

  override def resourceOffers(driver: SchedulerDriver, offers: util.List[Offer]) = {
    offers.foreach { offer =>
      val taskId = newTaskId

      val task = Protos.TaskInfo.newBuilder
        .setName("task " + taskId)
        .setTaskId(taskId)
        .setSlaveId(offer.getSlaveId)
        .addResources(buildResource("cpus", 1))
        .addResources(buildResource("mem", 128))
        .setData(ByteString.copyFromUtf8("" + taskCounter))
        .setExecutor(Protos.ExecutorInfo.newBuilder(executorInfo))
        .build

      launchTask(driver, offer, task)
    }
  }


  private def launchTask(schedulerDriver : SchedulerDriver, offer: Protos.Offer, task: Protos.TaskInfo) = {
    val tasks = Seq(task)
    val offerIDs = Seq(offer.getId)
    schedulerDriver.launchTasks(offerIDs, tasks)
  }

  private def buildResource(name:String, value:Double) = {
    Protos.Resource.newBuilder.setName(name)
      .setType(Protos.Value.Type.SCALAR)
      .setScalar(buildScalar(value)).build
  }

  private def buildScalar(value: Double) = {
    Protos.Value.Scalar.newBuilder.setValue(value)
  }
  private def newTaskId = {
    taskCounter += 1
    Protos.TaskID.newBuilder.setValue(taskCounter.toString)
  }

  override def offerRescinded(driver: SchedulerDriver, offerId: OfferID) =
    println("This offer's been rescinded.")

  override def disconnected(driver: SchedulerDriver) =
    println("We got disconnected")

  override def statusUpdate(driver: SchedulerDriver, status: TaskStatus) =
    println(s"Status update: ${status.getState} from ${status.getTaskId.getValue}")

  override def frameworkMessage(driver: SchedulerDriver, executorId: ExecutorID, slaveId: SlaveID, data: Array[Byte]) =
    println(s"Received message (scheduler): ${new String(data, "UTF-8")} from ${executorId.getValue}")

  override def slaveLost(driver: SchedulerDriver, slaveId: SlaveID) =
    println(s"Lost slave: $slaveId")

  override def executorLost(driver: SchedulerDriver, executorId: ExecutorID, slaveId: SlaveID, status: Int) =
    println(s"Lost executor on slave $slaveId")

  override def error(driver: SchedulerDriver, message: String) = {
    println(s"An Error occurred: $message")
  }




}
