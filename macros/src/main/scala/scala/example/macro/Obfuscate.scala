package scala.example.`macro`


import scala.annotation.StaticAnnotation
import scala.language.experimental.macros
import scala.language.postfixOps
import scala.reflect.macros.blackbox.Context

class Obfuscate(fields: String*) extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro ObfuscatorImpl.impl
}

object ObfuscatorImpl {

  val fieldsSeparator = ", "

  def impl(c: Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._

    def getAnnotationFields(tree: Tree) = tree match {
      case q"new $_(..$fields)" => fields.map(_.toString().replace("\"", ""))
    }

    val fieldsToRemove = getAnnotationFields(c.prefix.tree)

    val klass = annottees.head

    val newKlass = klass.tree match {
      case q"case class $name(..$params) extends {..$parent} with ..$traits { ..$body }" =>

        val filteredFields = params.collect {
          case q"$_ val $name: $_ = $_" if !fieldsToRemove.contains(name.toString()) => name.toString()
        }

        val result = filteredFields
          .map(TermName(_))
          .map(name => q"this.$name": Tree)
          .reduce((a,b) => q"$a + $fieldsSeparator + $b")

        val start = s"$name("
        val end = ")"

        val overridedToString = q"override def toString(): String = {$start + $result + $end}"

      q"case class $name(..$params) extends {..$parent} with ..$traits { ..$body; $overridedToString}"
    }

    c.Expr(newKlass)
  }
}