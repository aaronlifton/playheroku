package controllers

import play.api._
import play.api.mvc._

import java.lang.reflect.{Type, ParameterizedType}
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.`type`.TypeReference;

import play.api.data.Form
// import play.api.data.Forms.{single, nonEmptyText, tuple}
import play.api.data.Forms._
import play.api.mvc.{Action, Controller}
import anorm.NotAssigned
 
import models._

import anorm._
import anorm.SqlParser._

import play.api.libs.json._
import play.api.libs.json.util._
import play.api.libs.json.Reads._
import play.api.data.validation.ValidationError

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
    if (m.typeArguments.isEmpty) m.runtimeClass
    else new ParameterizedType {
      def getRawType = m.runtimeClass
      def getActualTypeArguments = m.typeArguments.map(typeFromManifest).toArray
      def getOwnerType = null
    }
  }
}

object Application extends Controller {
 // def notEqualReads[T](v: T)(implicit r: Reads[T]): Reads[T] = Reads.filterNot(ValidationError("validate.error.unexpected.value", v))( _ == v )
 // 
 //  def skipReads(implicit r: Reads[String]): Reads[String] = r.map(_.substring(2))
 // 
 //  implicit val threadReads: Reads[Thread] = (
 //    (__ \ "id").read[Long],
 //    (__ \ "name").read[String]
 //  )(Thread)
	
  val threadForm = Form(
	  single("name" -> nonEmptyText)
  )

  val messageForm = Form(
    single(
	  "body"   -> text	
	)
  )

  def index = Action {
    // Ok(views.html.index("Your new application is ready."))
    Ok(views.html.index(threadForm))
  }

  def addThread = Action { implicit request =>
  	threadForm.bindFromRequest.fold(
		errors => BadRequest,
		{
			case (name) =>
				val threadId = Thread.createAndReturnId(Thread(NotAssigned, name))
			    val json = JacksonWrapper.serialize(threadId.getOrElse(None))
				Ok(json).as(JSON)
		  		// Redirect(routes.Application.index())
		}
	)
  }
  
  def getThreads = Action {
     val threads = Thread.findAll()
	 val json = JacksonWrapper.serialize(threads)
     Ok(json).as(JSON)
  }
  
  def getThread(id: Long) = Action {
    val thread: Thread = Thread.findById(id) //.getOrElse(NotFound)
	val messages: Seq[Message] = Message.findAllByThreadId(id)
    Ok(views.html.thread(messageForm, thread, messages))
  }
  
  def addMessage(threadId: Long) = Action { implicit request =>
  	messageForm.bindFromRequest.fold(
		errors => BadRequest,
		{
			case (body) =>
				val messageId = Message.createAndReturnId(Message(NotAssigned, threadId, body))
			    val json = JacksonWrapper.serialize(messageId)
				Ok(json).as(JSON)
		  		// Redirect(routes.Application.index())
		}
	)
  }
  
  def getMessages(threadId: Long) = Action {
     val messages = Message.findAllByThreadId(threadId)
	 val json = JacksonWrapper.serialize(messages)
     Ok(json).as(JSON)
  }
  
}