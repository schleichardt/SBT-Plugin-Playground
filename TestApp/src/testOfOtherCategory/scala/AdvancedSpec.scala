import org.specs2.mutable._

  class AdvancedSpec extends Specification {

    "The 'Hello world' string" should {
      "contain 11 characters" in {
        "Hello world" must have size(11)
      }
    }
  }