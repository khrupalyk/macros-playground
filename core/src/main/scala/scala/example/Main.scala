package scala.example

import scala.example.`macro`.{Demo, Obfuscate}

@Obfuscate("pass")
case class User(email: String, pass: String, fname: String, lname: String)

object Main extends App {
  val obj = User("an@gmail.com", "strong_pass", "fname", "lname")
  println(obj)
}
