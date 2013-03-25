package controllers

import play.api._
import play.api.mvc._

import java.lang.reflect.{Type, ParameterizedType}
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.`type`.TypeReference;

import play.api.data.Form
import play.api.data.Forms.{mapping, text, optional}

import org.squeryl.PrimitiveTypeMode._
import models.{AppDB, Bar}

object JacksonWrapper {
  val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)

  def serialize(value: Any): String = {
    import java.io.StringWriter
    val writer = new StringWriter()
    mapper.writeValue(writer, value)
    writer.toString
  }

  def deserialize[T: Manifest](value: String) : T =
    mapper.readValue(value, typeReference[T])

  private [this] def typeReference[T: Manifest] = new TypeReference[T] {
    override def getType = typeFromManifest(manifest[T])
  }

  private [this] def typeFromManifest(m: Manifest[_]): Type = {
    if (m.typeArguments.isEmpty) { m.runtimeClass }
    else new ParameterizedType {
      def getRawType = m.runtimeClass
      def getActualTypeArguments = m.typeArguments.map(typeFromManifest).toArray
      def getOwnerType = null
    }
  }
}

object Application extends Controller {
  
  val barForm = Form(
  	mapping(
  		"name" -> optional(text)
		)(Bar.apply)(Bar.unapply)
  )

  def index = Action {
    // Ok(views.html.index("Your new application is ready."))
    Ok(views.html.index(barForm))
  }

  def addBar = Action { implicit request =>
  	barForm.bindFromRequest.value map { bar =>
  		inTransaction(AppDB.barTable insert bar)
  		Redirect(routes.Application.index())
  	} getOrElse BadRequest
  }

   def getBars = Action {
    val json = inTransaction {
      val bars = from(AppDB.barTable)(barTable =>
        select(barTable)
      )
      JacksonWrapper.serialize(bars)
    }
    Ok(json).as(JSON)
  }
  
}