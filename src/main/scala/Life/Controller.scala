package Life.Controller
import Life.utils._
import io.threadcso._
import swing._
import Life.Display.{LifeDisplay, TestD}
import Life.LifeGame.{LifeFactory, LifeParams, LifeGame}


//TODO make sure that game state is respected
class Controller[P,O,T <:Game[P,O]](fac : GameFactory[P,O,T], in : () => Command[P], k : O => Unit){
  private val mid = OneOne[() => Unit]
  private var currentGame : Option[T] = None
  private var alive = true 

  private var listen : PROC = proc {
    repeat(alive){
      val cm = in()
      cm match{
        case Create(p) => create(p) 
        case Start => start()
        case Pause => pause()
        case Resume => resume()
        case Stop => stop()
        case Quit => quit()
      }
    }
    mid.closeOut
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
  private def pause() = optApply((g:T) => if (g.state == Running) {g.pause(); g.state = Paused} )

  private def resume() = optApply((g:T) => if (g.state == Paused) {g.resume(); g.state = Running} )
  private def start() =optApply((g:T) => if (g.state == Fresh) { mid!g.start; g.state = Running}) 
  private def create(p:P) = currentGame match {
    case Some(game) => 
    case None => { 
      val game = fac.make(k,p)
      currentGame = Some(game)
    }

  }
  def make : () => Unit = () => (listen || runGame)()

}


/*object LifeController extends SimpleSwingApplication{
  def top = new TestD 

  override def startup(args : Array[String]){
    val c = OneOne[Command[LifeParams]]
    
    val disp = new LifeDisplay( (x:Command[LifeParams]) => c!x)
    val fac = new LifeFactory
    val con = new Controller(fac, () => c?(), disp.update)
    con.make()
    this.quit()
  }
}*/
