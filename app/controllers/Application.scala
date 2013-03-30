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

import play.api.Play.current
import akka.util.Timeout
import scala.concurrent._ //{ Await, Future }
import play.api.libs.concurrent.Execution.Implicits._
import scala.concurrent.duration._

import java.util.Date
import java.net.URLEncoder
import play.api.libs.ws
import play.api.libs.ws.WS

import play.api.libs.json
import play.api.libs.json.{JsObject, JsValue}

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

class Pusher {
  val appId = Play.configuration.getString("pusher.appId")
  val key = Play.configuration.getString("pusher.key")
  val secret = Play.configuration.getString("pusher.secret")
  
  import java.security.MessageDigest
  import java.math.BigInteger
  import javax.crypto.Mac
  import javax.crypto.spec.SecretKeySpec
  
  def trigger(channel:String, event:String, message:String): ws.Response = {
    val domain = "api.pusherapp.com"
    val url = "/apps/"+appId+"/channels/"+channel+"/events";
    val body = message
    // val body = JsObject(Seq("test" -> message)) // Seq([String, JsValue])
    val params = List( 
      ("auth_key", key),
      ("auth_timestamp", (new Date().getTime()/1000) toInt ),
      ("auth_version", "1.0"),
      ("name", event),
      ("body_md5", md5(body))
    ).sortWith((a,b) => a._1 < b._1 ).map( o => o._1+"="+URLEncoder.encode(o._2.toString)).mkString("&");
    
    val signature = sha256(List("POST", url, params).mkString("\n"), secret.get); 
    val signatureEncoded = URLEncoder.encode(signature, "UTF-8");
	implicit val timeout = Timeout(5 seconds)
	val f = WS.url("http://"+domain+url+"?"+params+"&auth_signature="+signatureEncoded).post(body)
	Await.result(f,timeout.duration)
  }
  
  def byteArrayToString(data: Array[Byte]) = {
     val hash = new BigInteger(1, data).toString(16);
     "0"*(32-hash.length) + hash
  }
  
  def md5(s: String):String = byteArrayToString(MessageDigest.getInstance("MD5").digest(s.getBytes("US-ASCII")));
  
  def sha256(s: String, secret: String):String = {
    val mac = Mac.getInstance("HmacSHA256");
    mac.init(new SecretKeySpec(secret.getBytes(), "HmacSHA256"));
    val digest = mac.doFinal(s.getBytes());
    String.format("%0" + (digest.length << 1) + "x", new BigInteger(1, digest));
  }
}

object WebSockets extends Pusher {
  val channel = "new-messages-scala" // Play.configuration.getString("websockets.channel")
  def trigger(event:String, message:String): ws.Response = trigger(channel, event, message)
}
 
object Test {
  def test = WebSockets.trigger("hello", "{ \"message\": \"test\" }")
}

object MessagePusher {
  def newMessage(json: String) = WebSockets.trigger("new-message", json)
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
	 Test.test
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
                MessagePusher.newMessage(json)
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