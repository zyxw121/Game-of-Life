package Life
package object utils{
import io.threadcso._

  type Data = String
  abstract class Command
  case object Start extends Command
  case object Quit extends Command
  case object Pause extends Command
  case object Resume extends Command 
  case object Stop extends Command 
 
  type Continuation[T] = T => Unit

  abstract class GameParams

  case class TestParams(start : Int) extends GameParams

  abstract class GameState
  case object Fresh extends GameState
  case object Running extends GameState
  case object Paused extends GameState
  case object Dead extends GameState

  //A pattern to be simulated
  //The state really _should_ be a member of Game, but for
  abstract class Game(params : GameParams, k : Continuation[Data]){

    //Fresh
    def make : PROC 
    //Running

    //Running
    def pause
    //Paused
    
    //Paused
    def resume
    //Running

    def kill
    //Dead
  }



}
