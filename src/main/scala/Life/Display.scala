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


class Canvas extends Panel{
  var N = 0 
  var a : Array[Array[Boolean]] = null
  val CS = 3  
  
  def set(n : Int, b : Array[Array[Boolean]]) = {
    N = n
    a = b
    //this.repaint()
  }
  
  override def paintComponent(g : Graphics2D){
    println("painting"+ N.toString)
   
   
    for (i<- 0 until N; j <- 0 until N){
      if (a(j)(i)) g.setColor(Color.black) else g.setColor(Color.white)
      g.fillRect(CS*i,CS*j,CS,CS)
    }
  } 
}



object Main extends SwingApplication{
  val lab = new Label("test")

def checkArr(a:Array[Array[Boolean]])= {
  var test = false
  for (row <- a; el <- row) test = test || el
  println(test)
}
  var board : Array[Array[Boolean]] = null

  val patternFCB = new Button("Select pattern file")
  val patternFCL = new Label("N/A")
  var patternFileChooser = new FileChooser(new java.io.File("./"))
  var patternFile = "/Users/Dan/Coding/Game-of-Life/examples/lif.txt"  
  var sizeChooser = new TextField(1)
  var N = 0
  var state  : GameState = Dead

  // Settings Input
  val okB = new Button("OK")
  val settingsOptions = new GridPanel(2,1) {
    contents += new FlowPanel(new Label("Size"),sizeChooser) 
    contents += new FlowPanel(new Label("Pattern file:"),patternFCL, patternFCB)
  }
  val settingsMenu = new FlowPanel(okB)
  val sets = new Frame {contents = new GridPanel(2,1) {
    contents += settingsOptions
    contents += settingsMenu}
  }
  listenTo(okB, patternFCB)
  reactions += {
    case ButtonClicked(`okB`) => {
      //size = sizeChooser.text.toInt //Make sure its int
      N = 150
      sets.visible=false
      //board =Array.ofDim[Boolean](size,size)
      ch!Create(LifeParams(patternFile, N, true))
      state = Fresh
      startB.enabled = true
      quitB.text = "Stop"
      //top.preferredSize = new Dimension(500,200)
      disp.preferredSize = new Dimension(N*3, N*3)
    } 
    case ButtonClicked(`patternFCB`) => { 
      if(patternFileChooser.showOpenDialog(settingsMenu) ==
            FileChooser.Result.Approve) {
          patternFile = patternFileChooser.selectedFile.getAbsolutePath
          patternFCL.text = patternFile
        }
    }
  } 

    val ch = OneOne[Command[LifeParams]]

  val quitB = new Button("Quit")
  val startB = new Button("Start")
  startB.enabled = false
  val newB = new Button("New Game")

  var disp  = new Panel {
    override def paintComponent(g : Graphics2D){
      val x1 = Math.max(size.width - N*3,0)/2
      val y1 = Math.max(size.height - N*3,0)/2
      println(x1.toString + ", " + y1.toString)
      for (i<- 0 until N; j <- 0 until N){
        if (board(j)(i)) g.setColor(Color.black) else g.setColor(Color.white)
        g.fillRect(x1+3*i,y1+3*j,3,3)
      }
    } 
  } 
  val menu = new FlowPanel(quitB,startB,newB)
  
  def upp(n:Int) = lab.text=n.toString
 
  def top = new MainFrame{
      preferredSize = new Dimension(500,600)
      contents = new BorderPanel{ 
        layout(new ScrollPane { contents = disp}) = Center 
        layout(menu) = South
      }
    } 

  def update = (x:LifeGame.LifeData) => {board = x;disp.repaint()}

  override def startup(args : Array[String]){
   val c = OneOne[Int]
    val myproc = proc {
      var n = 0
      while (true){sleep(1*Sec); upp(n); n+=1}
    }
    
    //fork(myproc)  
    //INIT Controller 
    val fac = new LifeFactory
    val con = new Controller(fac, () => ch?(), update)
    fork(proc {con.make()})
    //END INIT

    val t = top
    if (t.size == new Dimension(0,0)) t.pack()
    t.visible = true

  }


  listenTo(newB,quitB,startB)
  reactions +={
    case ButtonClicked(`startB`) => state match{
      case Fresh => ch!Start; startB.text = "Pause"; state = Running
      case Running => ch!Pause; startB.text = "Resume"; state = Paused
      case Paused => ch!Resume; startB.text = "Pause"; state = Running
      case _ =>
    }
    case ButtonClicked(`newB`) if state == Dead => {
      sets.visible=true
      newB.enabled = false
    }
    case ButtonClicked(`quitB`) => state match{
      case Dead => this.quit()
      case _ => {
        state = Dead
        ch!Quit
        quitB.text = "Quit"
        newB.enabled = true
        startB.enabled = false
        startB.text = "Start"
      }
    } 
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

