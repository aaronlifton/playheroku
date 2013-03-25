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
 
  def create(thread: Thread): Unit = {
    DB.withConnection { implicit connection =>
      SQL("insert into thread(name) values ({name})").on(
        'name -> thread.name
      ).executeUpdate()
    }
  }
 
}