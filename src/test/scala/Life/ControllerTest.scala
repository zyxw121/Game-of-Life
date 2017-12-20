package Life.Tests
import org.scalatest.{FlatSpec, Matchers}
import Life.Controller._
import Life.utils._
import io.threadcso._
import io.threadcso.semaphore._
import org.scalamock.scalatest.MockFactory

abstract class GameState
case object Running extends GameState
case object Paused extends GameState

abstract class MyTest extends FlatSpec with Matchers with MockFactory

class ControllerTests extends MyTest{ 
  override def withFixture(test: NoArgTest) = { // Define a shared fixture
    // Shared setup (run at beginning of each test)
    try test()
    finally {
      // Shared cleanup (run at end of each test)
    }
  }
 
  "Controller" should "quit cleanly" in {
    
  val c = OneOne[Command[Int]]
 
  val mockGame = mock[Game[Int,Int]]
  val mockMake = mockFunction[(Int => Unit, Int), PROC]
//  val mockPause, mockResume, mockKill = mockFunction[(),Unit]
  val mockOut = mockFunction[Int,Unit]
  val mockFac = mock[GameFactory[Int,Int,Game[Int,Int]]]
  val Con = new Controller(mockFac, c, mockOut)

  
  ( (proc {c!Quit}) || Con.make )()

  } 
  


}
