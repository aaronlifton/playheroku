import models.{AppDB, Bar}

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

import org.squeryl.PrimitiveTypeMode.inTransaction

import play.api.test._
import play.api.test.Helpers._

class BarSpec extends FlatSpec with ShouldMatchers {

  "A Bar" should "be creatable" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      inTransaction {
        val bar = AppDB.barTable insert Bar(Some("foo"))
        bar.id should not equal(0)
      }
    }
  }

  "A request to the getBars Action" should "respond with data" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      inTransaction(AppDB.barTable insert Bar(Some("foo")))

      val result = controllers.Application.getBars(FakeRequest())
      status(result) should equal (OK)
      contentAsString(result) should include ("foo")
    }
  }


}