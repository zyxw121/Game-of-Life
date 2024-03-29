package Life
package object utils{
import io.threadcso._

  sealed abstract class Command[+P]
  case class Create[P](p:P) extends Command[P]
  case object Start extends Command
  case object Quit extends Command
  case object Pause extends Command
  case object Resume extends Command 
  case object Stop extends Command 

  sealed abstract class GameState
  case object Dead extends GameState
  case object Fresh extends GameState
  case object Paused extends GameState
  case object Running extends GameState
 
  //A computation to be performed
  //The state really _should_ be a member of Game, but for
  abstract class Game[P,O] {
    var state : GameState = Fresh
    def start : () => Unit
    def pause()
    def resume()
    def kill()
  }

  abstract class GameFactory[P,O,T <:Game[P,O]]{
    def make(k : (O=>Unit), p :P) : T
  }

 // def wrap[O,P,T <: Game[P,O]]

}
