package test

import org.specs2.mutable._

import controllers.routes
import models.{AppDB, Thread}

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

import org.squeryl.PrimitiveTypeMode.inTransaction

import play.api.http.ContentTypes.JSON

import play.api.test._
import play.api.test.Helpers._

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
class ApplicationSpec extends FlatSpec with ShouldMatchers {
    
    "A request to the addThread action" should "respond" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        val result = controllers.Application.addThread(FakeRequest().withFormUrlEncodedBody("name" -> "FooThread"))
        status(result) should equal (SEE_OTHER)
        redirectLocation(result) should equal (Some(routes.Application.index.url))
      }
    }

}