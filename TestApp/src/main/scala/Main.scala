 
object Main extends App {
  println("App started ")
  println(GeneratedClassWithVal.aValue)
}

@deprecated
class FindMe(val why:String) extends scala.annotation.StaticAnnotation  {}

@FindMe("inCompile")
case class Bar(barMember1: String, barMember2: Int)