package scala.example.`macro`

import language.experimental.macros
import scala.reflect.macros.blackbox.Context

object Demo {

  val fieldsSeparator = ", "
  val keyValueSeparator = " = "

  def prettyPrint[T](a: T): Unit = macro prettyPrintImpl[T]

  def prettyPrintImpl[T: c.WeakTypeTag](c: Context)(a: c.Expr[T]): c.Expr[Unit] = {
    import c.universe._

    val tpe = weakTypeOf[T]

    if(!tpe.typeSymbol.asClass.isCaseClass) {
      c.abort(c.enclosingPosition, s"Type $tpe is not case class")
    }

    val formattedClassName = s"${tpe.typeSymbol.name.toString}: "

    val methods = tpe.decls.collect {
      case field if field.isMethod && field.asMethod.isCaseAccessor =>
        val name = c.Expr[String](q"${field.name.toString}")
        q"$name + $keyValueSeparator + $a.$field"
    } reduce((l,r) => q"$l + $fieldsSeparator + $r")

    c.Expr[Unit](q"println($formattedClassName + $methods)")
  }
}
