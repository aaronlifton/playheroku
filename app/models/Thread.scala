package models
 
import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

case class Thread(id: Pk[Long], name: String)
 
object Thread {
 
  val simple = {
    get[Pk[Long]]("id") ~
    get[String]("name") map {
      case id~name => Thread(id, name)
    }
  }
 
  def findAll(): Seq[Thread] = {
    DB.withConnection { implicit connection =>
      SQL("select * from thread").as(Thread.simple *)
    }
  }
  def findById(id: Long): Thread = {
    DB.withConnection { implicit connection => 
      SQL("select * from thread where id = {id}")
	  .on("id" -> id).using(simple).single() // quirky
	}
  }
  def findByName(name: String): Thread = {
	DB.withConnection { implicit connection => 
	  SQL("select * from thread where name = {name}")
	  .on("name" -> name).as(Thread.simple *).head
	}
  }
  
  // def findByParam(param: String, paramVal: [_])
 
  def create(thread: Thread): Unit = {
    DB.withConnection { implicit connection =>
      SQL("insert into thread(name) values ({name})").on(
        'name -> thread.name
      ).executeUpdate()
    }
  }
  
  def createAndReturnId(thread: Thread): Unit = {
    DB.withConnection { implicit connection =>
	  SQL("insert into thread(name) values ({name})").on(
		'name -> thread.name
	  ).executeInsert().get
    }
  }
 
}