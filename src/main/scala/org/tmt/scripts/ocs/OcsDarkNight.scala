package org.tmt.scripts.ocs

import tmt.sequencer.ScriptImports._

class OcsDarkNight(cs: CswServices) extends Script(cs) {

  val iris = cs.sequenceProcessor("iris")
  val tcs = cs.sequenceProcessor("tcs")

  var eventCount = 0
  var commandCount = 0

  val subscription = cs.subscribe("ocs") { event =>
    eventCount = eventCount + 1
    println(
      s"------------------> received-event: ${event.value} on key: ${event.key}")
    Done
  }

  val cancellable = cs.publish(16.seconds) {
    SequencerEvent("ocs-metadata", (eventCount + commandCount).toString)
  }

  cs.handleCommand("setup-iris") { commandA =>
    spawn {
      val maybeCommandB = cs.nextIf(c => c.name == "setup-iris").await
      val subCommandsB = if (maybeCommandB.isDefined) {
        val commandB = maybeCommandB.get
        val subCommandB1 = commandB.withId(Id(s"${commandB.id}1"))
        val subCommandB2 = commandB.withId(Id(s"${commandB.id}2"))

        List(subCommandB1, subCommandB2)
      } else List.empty

      println(s"[Ocs] Received commandA: ${commandA.name}")
      val subCommandA1 = commandA.withId(Id(s"${commandA.id}1"))
      val subCommandA2 = commandA.withId(Id(s"${commandA.id}2"))

      val subCommandsA = List(subCommandA1, subCommandA2)
      val sequence = subCommandsA ++ subCommandsB

      val response = iris
        .feed(sequence)
        .await
        .markSuccessful(commandA)
        .markSuccessful(maybeCommandB)

      println(s"[Ocs] Received response: $response")
      response
    }
  }

  cs.handleCommand("setup-iris-tcs") { commandC =>
    spawn {
      val maybeCommandD = cs.nextIf(c2 => c2.name == "setup-iris-tcs").await
      val tcsSequence = if (maybeCommandD.isDefined) {
        val nextCommand = maybeCommandD.get
        val subCommandD1 =
          nextCommand.withName("setup-tcs").withId(Id(s"${nextCommand.id}1"))
        val subCommandD2 =
          nextCommand.withName("setup-tcs").withId(Id(s"${nextCommand.id}2"))
        List(subCommandD1, subCommandD2)
      } else {
        List.empty
      }

      println(s"[Ocs] Received commandC: ${commandC.name}")
      val subCommandC1 =
        commandC.withName("setup-iris").withId(Id(s"${commandC.id}1"))
      val subCommandC2 =
        commandC.withName("setup-iris").withId(Id(s"${commandC.id}2"))
      val irisSequence = List(subCommandC1, subCommandC2)

      val aggregateResponse = parAggregate(
        iris.feed(irisSequence),
        tcs.feed(tcsSequence)
      ).await

      val response =
        aggregateResponse.markSuccessful(commandC).markSuccessful(maybeCommandD)

      println(s"[Ocs] Received response: $response")
      response
    }
  }

  cs.handleCommand("setup-tcs") { command =>
    spawn {
      println(s"[Ocs] Received command: ${command.name}")

      val responseE = tcs.feed(List(command)).await.markSuccessful(command)

      println(s"[Ocs] Received response: $responseE")
      responseE
    }
  }

  override def onShutdown(): Future[Done] = spawn {
    subscription.shutdown()
    cancellable.cancel()
    println("shutdown")
    Done
  }
}
