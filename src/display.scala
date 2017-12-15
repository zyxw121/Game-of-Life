package display 
import java.awt._

class Display(N: Int, a:Array[Array[Boolean]]) extends Frame {
  // Define some constants
 
  private val boardSize = N * 3 
  private val pane = new ScrollPane() 
  pane.setSize(boardSize, boardSize)
  private val board = new Board()
  pane.add(board)
  this.add(pane, "Center")
  this.pack()
  this.setVisible(true)
  this.setTitle("Life")
  this.setSize(N*3, N*3)
  
  def drawBoard = {
    for (i <- 0 until N){ for (j<- 0 until N){
      board.drawCell(j,i, if (a(i)(j)) Color.black else Color.white)
    }}
  }

  override def paint(g: Graphics) = drawBoard
  
class Board extends Component{

    def drawCell(x: Int, y: Int, c: Color) = {    
      val g = getGraphics()
      g.setColor(c)
      g.fillRect(x*3, y*3, 3, 3)
    }
  }
}


