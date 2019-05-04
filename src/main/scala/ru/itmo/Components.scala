package ru.itmo

import java.util.concurrent.ForkJoinPool

import com.typesafe.scalalogging.Logger
import monix.execution.ExecutionModel.BatchedExecution
import monix.execution.Scheduler
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext

trait Components {
  implicit val logger: Logger = Logger(LoggerFactory.getLogger("reactive-config"))
  implicit val executionContext: ExecutionContext = ExecutionContext.fromExecutor(new ForkJoinPool())
  implicit val scheduler: Scheduler = Scheduler.computation(
    executionModel = BatchedExecution(8)
  )
}
