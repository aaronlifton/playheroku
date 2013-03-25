package models

import org.squeryl.KeyedEntity
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl.{OneToMany}
import org.squeryl.Schema
import org.squeryl.annotations.Column
import org.squeryl.{Session, SessionFactory}

import java.sql.Timestamp
import System._
import java.lang.{Integer}

case class BaseEntity(name: Option[String]) extends KeyedEntity[Long] {
	val id: Long = 0
}

trait SlugField {
    val slug: String = ""
}

trait CreationTimeMonitoring {
    val created_at: Timestamp = new Timestamp(System.currentTimeMillis)
}

class User(var username: String,
	   var password: String,
	   var email: String,
	   var verification_code: String,
	   var is_verified: Boolean) extends BaseEntity {
    def this() = this("", "", "", "", false)
}

class Thread(var name: String) extends BaseEntity with SlugField with CreationTimeMonitoring {
	def this() = this("")
}
object CoreSchema extends Schema {
	val threadTable = table[Thread]("thread")
	
	def getThread(id: Long):Thread = {
	
	}
	
	on(threadTable)(ent => declare(
		ent.id is(autoIncremented),
		// ent.slug is(unique)
	))
}