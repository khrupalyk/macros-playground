package scala.example

import scala.example.`macro`.Demo

case class User(email: String, pass: String)

object Main extends App {
  val obj = User("an@gmail.com", "strong_pass")
  Demo.prettyPrint(obj)
}
