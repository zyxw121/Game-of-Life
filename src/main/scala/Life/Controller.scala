package Life.Controller
import Life.utils._
import io.threadcso._

class Controller[P,O,T <:Game[P,O]](fac : GameFactory[P,O,T], in : ?[Command[P]], k : O => Unit){
  private val mid = OneOne[() => Unit]
  private var currentGame : Option[T] = None
  private var alive = true
  
  private var listen : PROC = proc {
    repeat(alive){
      val cm = in?()
      cm match{
        case Start(p) => start(p)
        case Pause => pause()
        case Resume => resume()
        case Stop => stop()
        case Quit => quit()
      }
    }
    in.closeIn; mid.closeOut
  }
  private val runGame = proc {
    repeat(alive){
      val p = mid?()
      p()
    }
    mid.closeIn
  }


  private def optApply(f : T => Unit) = currentGame match{ 
    case Some(game) => f(game)
    case None =>
  }

  private def quit() = {
    alive = false
    stop() 
    mid!(()=>{}) //"clear the pipes" so runGame terminates
  }
  private def stop() = optApply(_.kill())
  private def pause() = optApply(_.pause())
  private def resume() = optApply(_.resume())
  private def start(p : P) = currentGame match{
    case Some(game) => 
    case None => { 
      val game = fac.make(k,p)
      currentGame = Some(game)
      mid!game.start
    }
  }

  def make : PROC = listen || runGame

}
