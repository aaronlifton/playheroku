package models
 
import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

case class Message(id: Pk[Long], thread: Long, body: String)
 
object Message {
 
  val simple = {
    get[Pk[Long]]("id") ~
    get[Long]("thread") ~
    get[String]("body") map {
      case id~thread~body => Message(id, thread, body)
    }
  }
 
  def findAll(): Seq[Message] = {
    DB.withConnection { implicit connection =>
      SQL("select * from message").as(Message.simple *)
    }
  }
  def findAllByThreadId(threadId: Long): Seq[Message] = {
    DB.withConnection { implicit connection => 
      SQL("select * from message where thread = {threadId}")
	  .on("threadId" -> threadId).as(Message.simple *)
	}
  }
  def findById(id: Long): Message = {
    DB.withConnection { implicit connection => 
      SQL("select * from message where id = {id}")
	  .on("id" -> id).using(simple).single()
	}
  }
  def findByName(name: String): Message = {
	DB.withConnection { implicit connection => 
	  SQL("select * from message where name = {name}")
	  .on("name" -> name).as(Message.simple *).head
	}
  }
  
  def findThread(id: Long): Message = {
	  DB.withConnection { implicit connection =>
		  SQL("select * from message where id = {id}")
		  .on("id" -> id).as(Message.simple *).head
	  }
  }
  
  // def findByParam(param: String, paramVal: [_])
  
  def create(message: Message): Unit = {
    DB.withConnection { implicit connection =>
      SQL("insert into message(thread, body) values ({thread}, {body})").on(
		'thread -> message.thread,
		'body   -> message.body
      ).executeUpdate()
    }
  }
  
  def createAndReturnId(message: Message): Option[Long] = {
    DB.withConnection { implicit connection =>
        SQL("insert into message(thread, body) values ({thread}, {body})").on(
  		'thread -> message.thread,
  		'body   -> message.body
	  ).executeInsert()
    } match {
      case l: Option[Long] => l
	}
  }
 
}