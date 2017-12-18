package Life.Controller
import Life.Display.{Display}
import io.threadcso._
import scala.collection.mutable.HashMap 
import Life.utils._ 
import Life.Game.LifeGame
import io.threadcso.semaphore.BooleanSemaphore
import Life.Test.TestGame

class Controller(in : ?[Command], k : Continuation[Data]) {
  var alive = true
  var currentGame : Option[Game] = None
  def running = !(currentGame == None)
  val mid = OneOne[PROC]

  //Invariants: If currentGame not None then it is running in runProc
  //Start while running does nothing
  //Stop while not running does nothing
 
  def listen = proc {
    repeat(alive){
      val comm = in?()
      Controller.commands.get(comm) match {
        case Some(cmd) => cmd(this)
        case None => {} 
      }

    }
    in.closeIn
  } 

  def start = currentGame match {
    case Some(g) => {}
    case None => {
      val game =  new TestGame(new TestParams(5), k)
      currentGame = Some(game)
      mid!(game.make)
    }
  }  

  //Assumes p doesn't terminate unless through "stop"
  //BEWARE: Channel read may stall even when dead
  def runProcs = proc {
    repeat (alive) {
      val p = mid?()
      run(p)
    }
    mid.closeIn
  }

  def optApply(f : Game => Unit) = currentGame match {
    case Some(g) => f(g)
    case None => 
  }

  def resume = optApply(_.resume) 
  def pause = optApply(_.pause)
  def stop = {optApply(_.kill); currentGame = None}

  def quit = { 
    alive = false 
    mid!(proc {}) //Dummy to make runProc terminate
    in.closeIn
  }
  
  def make = listen || runProcs

}

object Controller {

  def main(args: Array[String]){
      val toController = OneOne[Command]
      val disp = new Display( (cm:Command) => run(proc {toController!(cm)}) ) 
      val cont = new Controller(toController, (x:Data) => disp.update(x) ) 
      ( cont.make )()
      scala.sys.exit(0)
    }

  val commands = HashMap[Command, Controller=>Unit](
      Quit -> (_.quit),
      Start -> (_.start),
      Stop -> (_.stop),
      Resume -> (_.resume),
      Pause -> (_.pause)
     )
}
