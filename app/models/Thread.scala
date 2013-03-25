package models
import org.squeryl.{Schema, KeyedEntity}

case class Thread(name: Option[String]) extends KeyedEntity[Long] {
	val id: Long = 0
}

object AppDB extends Schema {
	val threadTable = table[Thread]("thread")
}