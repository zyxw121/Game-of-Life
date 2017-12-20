package Life.Display
import io.threadcso._
import java.awt._
import java.awt.event.{ActionEvent, ActionListener}
import Life.utils._
import Life.LifeGame._

//A display for a specific game type
//update is called by its controller
trait Display[P,O,T <: Game[P,O]] { 
  def update(x:O)
}


class LifeDisplay(k : Command[LifeParams] => Unit) extends Frame with Display[LifeParams, LifeGame.LifeData, LifeGame] { 
  def makeListener(cm : Command[LifeParams]) : ActionListener = new ActionListener { def actionPerformed(e : ActionEvent) = { k(cm) }}

  var canPause = true
 
  val playButton=new Button("Pause")  
  playButton.setBounds(30,140,80,30)
  this.add(playButton)
  playButton.addActionListener( new ActionListener { def actionPerformed(e:ActionEvent) = {
    if (canPause) {
      playButton.setLabel("Resume")
      canPause = false
      k(Pause)
    }
    else {
      playButton.setLabel("Pause")
      canPause = true
      k(Resume)
    }
  }}) 


/*  val h=new Button("Stop")  
  h.setBounds(30,180,80,30)
  this.add(h)
  h.addActionListener( makeListener(Stop)  )*/
  val quitButton=new Button("Quit")  
  quitButton.setBounds(30,260,80,30)
  this.add(quitButton)
  quitButton.addActionListener( makeListener(Quit)  )

  val startButton=new Button("Start Game")  
  startButton.setBounds(30,100,80,30)
  this.add(startButton)
  startButton.addActionListener( makeListener(Start(LifeParams(""))) ) 

  this.setSize(300,300)
  this.setLayout(null)
  this.setVisible(true)
  
  def getParams = new LifeParams("")
  def update(x:LifeGame.LifeData) = {}
}

