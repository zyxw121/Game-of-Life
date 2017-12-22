package Life.Display
import io.threadcso._
import java.awt._
import java.awt.event.{ActionEvent, ActionListener}
import Life.utils._
import Life.LifeGame._

//A display for a specific game type
//update is called by its controller
trait Display[P,O,T <: Game[P,O]] { 
}


class LifeDisplay(k : Command[LifeParams] => Unit) extends Frame with Display[LifeParams, LifeGame.LifeData, LifeGame] { 
  def makeListener(cm : Command[LifeParams]) : ActionListener = new ActionListener { def actionPerformed(e : ActionEvent) = { k(cm) }}

  var a : Array[Array[Boolean]] = Array.ofDim[Boolean](150,150) 
  private var board : Board = new Board() 
  private var N = 150
  private var CellSize = 3
  private val buttonBar = new Panel()
  private val pane = new ScrollPane()
  pane.add(board)

  var canPause = true
 
  val playButton=new Button("Pause")  
  playButton.setBounds(80,0,50,30)
  buttonBar.add(playButton)
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
  quitButton.setBounds(0,0,50,30)
  buttonBar.add(quitButton)
  quitButton.addActionListener( makeListener(Quit)  )

  val startButton=new Button("Start Game")  
  startButton.setBounds(150,0,50,30)
  buttonBar.add(startButton)
  startButton.addActionListener( new ActionListener{ def actionPerformed(e : ActionEvent){
    val p = getParams
    k(Create(p))
    k(Start)
  }})
  

  buttonBar.setBounds(20,N*CellSize+40,200,30)
  buttonBar.setLayout(null)
  pane.setBackground(Color.gray)
  pane.setSize(CellSize*N, CellSize*N)
  this.add(buttonBar)
  this.add(pane)
  pane.setBounds(20,20,N*CellSize, N*CellSize)
  this.setSize(CellSize*N+40,CellSize*N + 100)
  this.setLayout(null)
  this.setVisible(true)
  this.pack()

 
  class Board extends Component{
    def DrawCell(x:Int, y:Int, c:Color){
      val g = getGraphics()
      g.setColor(c)
      g.fillRect(x*CellSize, y*CellSize, CellSize, CellSize)
    }
  }



   def drawBoard = {
    for (i <- 0 until N){ for (j <- 0 until N){
      board.DrawCell(j,i, if (a(i)(j)) Color.black else Color.white )
   }}
  }

  override def paint(g:Graphics) = drawBoard

  def getParams = new LifeParams("/Users/Dan/Coding/Game-of-Life/examples/lif.txt")
  def update = (x:LifeGame.LifeData) => {
    a = x; drawBoard
  }

    
}

