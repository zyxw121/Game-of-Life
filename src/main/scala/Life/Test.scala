package Life.Test
import io.threadcso._
import io.threadcso.semaphore._
import Life.utils._ 

class TestGame(p : TestParams, k : Continuation[Data]) extends Game(p,k) {
  var state : GameState = Fresh

  var alive = true 
  var n = 0
  val e = new BooleanSemaphore(available = true, fair = true)

  val barrier = new Barrier(2) 

  p match {
    case TestParams(start) => n=start
  }

  def make = run || control

  private val run = proc {
    while (alive) { 
      barrier.sync
      k(n.toString)
      n +=1 
    }
  }

  private val control = proc {
    state = Running
    while(alive){
      e.acquire
      if (state == Running) {
        sleep(10)
        barrier.sync 
      }
      e.release
    }
  }

  def pause = {e.acquire; state = Paused; e.release}
  def resume = {e.acquire; state = Running; e.release}
  def kill = {e.acquire; alive=false; state = Dead; e.release}

}
