import com.google.common.base.Strings._


object Main extends App {
  println("App started ")
  println(GeneratedClassWithVal.aValue)

  println(repeat("NaN", 10))
}

case class Bar(barMember1: String, barMember2: Int)