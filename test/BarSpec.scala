import models.{AppDB, Thread}

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

import org.squeryl.PrimitiveTypeMode.inTransaction

import play.api.test._
import play.api.test.Helpers._

class ThreadSpec extends FlatSpec with ShouldMatchers {

  "A Thread" should "be creatable" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      inTransaction {
        val thread = AppDB.threadTable insert Thread(Some("foo"))
        thread.id should not equal(0)
      }
    }
  }

  "A request to the getThreads Action" should "respond with data" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      inTransaction(AppDB.threadTable insert Thread(Some("foo")))

      val result = controllers.Application.getThreads(FakeRequest())
      status(result) should equal (OK)
      contentAsString(result) should include ("foo")
    }
  }


}