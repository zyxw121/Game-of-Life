package Life.Display
import io.threadcso._
import Life.utils._
import swing._
import scala.swing.BorderPanel.Position._
import java.awt.{Graphics2D, Color}
import event._
import Life.LifeGame._
import Life.Controller.Controller

//A display for a specific game type
//update is called by its controller
trait Display[P,O,T <: Game[P,O]] {
  def update : O => Unit 
}


class Canvas(N : Int, a : Array[Array[Boolean]]) extends Panel{
  override def paintComponent(g : Graphics2D){
    for (i<- 0 until N; j <- 0 until N){
      if (a(j)(i)) g.setColor(Color.black) else g.setColor(Color.white)
    }
  } 
}







object Main extends SwingApplication{
  val lab = new Label("test")

  var patternFileChooser = new FileChooser(new java.io.File("./"))
  var patternFile = ""  
  var sizeChooser = new TextField
  var size = 0

  // Settings Input
  val okB = new Button("OK")
  val sets = new Frame {contents = new FlowPanel(sizeChooser, okB)}
  listenTo(okB)
  reactions += {case ButtonClicked(`okB`) => size = 5; sets.visible=false; println("clicked ok") } 


  val quitB = new Button("Quit")
  val startB = new Button("Start")
  val newB = new Button("New Game")

  var disp = lab //new Canvas(5, null)
  val menu = new FlowPanel(quitB,startB,newB)
  
  def upp(n:Int) = lab.text=n.toString
 
  def top = new MainFrame{
      contents = new BorderPanel{ 
        layout(disp) = Center 
        layout(menu) = South
      }
    } 

  def update = (x:LifeGame.LifeData) => {}

  override def startup(args : Array[String]){
   val c = OneOne[Int]
    val myproc = proc {
      var n = 0
      while (true){sleep(1*Sec); upp(n); n+=1}
    }
    
    fork(myproc)  
    //INIT Controller 
   /* val ch = OneOne[Command[LifeParams]]
    val fac = new LifeFactory
    val con = new Controller(fac, () => ch?(), update)
    fork(proc {con.make()})*/
    //END INIT

    val t = top
    if (t.size == new Dimension(0,0)) t.pack()
    t.visible = true

  }

  listenTo(newB)
  listenTo(quitB)
  reactions +={
    case ButtonClicked(`newB`) => sets.visible=true
    case ButtonClicked(`quitB`) => this.quit() 
  }

}





class LifeDisplay(k : Command[LifeParams] => Unit) extends Display[LifeParams, LifeGame.LifeData, LifeGame]{
  def update = (x:LifeGame.LifeData) => {}  
}



/*class LifeDisplay(k : Command[LifeParams] => Unit) extends Frame with Display[LifeParams, LifeGame.LifeData, LifeGame] {

 
  def makeListener(cm : Command[LifeParams]) : ActionListener = new ActionListener { def actionPerformed(e : ActionEvent) = { k(cm) }}

  var a : Array[Array[Boolean]] = Array.ofDim[Boolean](150,150) 
  private var board : Board = new Board() 
  private var N = 150
  private var CellSize = 3
  private val buttonBar = new Panel()
  private val pane = new ScrollPane()
  pane.add(board)
  private def params : LifeParams = LifeParams("/Users/Dan/Coding/Game-of-Life/examples/lif.txt",N,true)
  


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


  val h=new Button("Stop")  
  h.setBounds(30,180,80,30)
  this.add(h)
  h.addActionListener( makeListener(Stop)  )
  val quitButton=new Button("Quit")  
  quitButton.setBounds(0,0,50,30)
  buttonBar.add(quitButton)
  quitButton.addActionListener( makeListener(Quit)  )

  val startButton=new Button("Start Game")  
  startButton.setBounds(150,0,50,30)
  buttonBar.add(startButton)
  startButton.addActionListener( new ActionListener{ def actionPerformed(e : ActionEvent){
    k(Create(params))
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

  def update = (x:LifeGame.LifeData) => {
    a = x; drawBoard
  }

    
}*/

