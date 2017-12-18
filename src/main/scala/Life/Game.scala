package Life.Game
import io.threadcso._
import scala.io.Source
import Life.utils._

/** Conway's Game of Life simulator
  * We have a number of worker threads (each assigned a horizontal strip of the
  * board). To update to the next generation, each worker first copies the row
  * of cells immediately above and below its section to memory, and then uses
  * this to update the board. Because the board is shared, we use a barrier to
  * keep the workers synchronized - no thread can start updating the board
  * until all threads have finished copying. 
  */ 

class LifeGame(params : GameParams, k : Continuation[Data]) extends Game(params,k){
  def make = proc {}
  def pause = {}
  def resume = {}


  private val BOARD_SIZE = 150 
  private val WORKERS = 8 
  private var BORN = new Array[Boolean](9)
  private var SURVIVE = new Array[Boolean](9) 
 
  private var board = Array.ofDim[Boolean](BOARD_SIZE, BOARD_SIZE)
  //private val display = new Display(BOARD_SIZE, board)
  
  private var alive = true 
  private var generations = 0
  private var totalgenerations = 0

  private val barrier = new Barrier(WORKERS)

  // Takes an RLE line and unpacks the repetitions. 
  def unpack(s : String) : String = {
    var t = ""
    var i = 0
    while (i < s.length){
      var d = "0"
      while (s(i).isDigit){
        d = d + s(i)
        i += 1  
      }
      t = t + (s(i).toString * (math.max(d.toInt -1,0)))
      t = t + s(i)
      i += 1
    }
    return t
  }
  
  // Takes a config filename, reads it and writes it onto the board.
  private def setconfig(filename : String) = {
    val lines = Source.fromFile(filename).getLines
    var dim = "#"; var state = ""    

    // Sets dim to be the first non-comment line
    while (lines.hasNext && dim(0) == '#'){
      dim = lines.next
    }

    // Contains the width, height, born, and survive data, in that order.
    var numbers = dim.replaceAll("[^0-9]+", " ").trim.split(" ")
    
    var width, height, born, survive = "" 

    width = numbers(0)
    height = numbers(1)
    // If the file doesnt specify the ruleset we default to Conway's
    if (numbers.length < 4) {
      born = "2"
      survive = "23"
    }
    else {
      born = numbers(2)
      survive = numbers(3)
    } 

    for (i<- born) BORN(i.toString.toInt) = true
    for (i<- survive)  SURVIVE(i.toString.toInt) = true 

    var x = (BOARD_SIZE - width.toInt)/2; var startx = x
    var y = (BOARD_SIZE - height.toInt)/2

    // Write to the board line by line
    for (line <- lines if line(0) != '#'){
      for (ch <- unpack(line)) ch match {
        case 'o' => board(y)(x) = true; x += 1
        case 'b' => x += 1
        case '$' => x = startx; y += 1
        case _ =>
      }
    }
  }

  class Worker(y : Int,  height : Int) {
    // Local copy of the next generation
    private var next = Array.ofDim[Boolean](BOARD_SIZE, height)

    // These arrays comprise a local copy of the strips right next to the edge 
    private var top, bottom = Array.ofDim[Boolean](BOARD_SIZE)

    // Helper function, deals with board wrapping
    private def boardwrap(i : Int, j : Int) : Boolean = {
      board(Math.floorMod(j, BOARD_SIZE))(Math.floorMod(i, BOARD_SIZE))
    }

    // Only to be used to access cells in the current block or the boundary 
    private def getCell(i : Int, j : Int) : Boolean = {
      // Top row
      if (j == -1){
        top(Math.floorMod(i, BOARD_SIZE))
      }
      // Bottom row
      else if (j == height){
        bottom(Math.floorMod(i, BOARD_SIZE))
      }
      // Interior
      else {
        boardwrap(i,y+j)
      }
    }

    // Copy the boundary to local memory
    private def copy = {
      for (i<- 0 until BOARD_SIZE){
        top(i) = boardwrap(i,y-1)
        bottom(i) = boardwrap(i,y+height)    
      }
    }

    // Calculate the next generation in local storage, then copy it to the shared board
    private def update = {
      var neighbours = 0
      for (i<- 0 until BOARD_SIZE){
      for (j<- 0 until height){
        neighbours = 0
        for ((h,v) <- List((-1,-1), (-1,0), (-1,1), (0,-1), (0,1), (1,-1), (1,0), (1,1) )  ){
          if (getCell(i+h,j+v)) neighbours +=1
        }
        next(i)(j) = ( (BORN(neighbours) && !getCell(i,j)) || ( SURVIVE(neighbours) && getCell(i,j))) 
      }}
      
      for (i <- 0 until BOARD_SIZE){
      for (j <- 0 until height){
        board(y+j)(i) =  next(i)(j)
      }}
    }

    // The manager calls this every iteration.
    protected def manage = { }

    def worker = proc {
      barrier.sync()
      while(alive) {
        copy
        barrier.sync()
        update
        manage 
        barrier.sync()
      }
    }
  }

  class Manager(y : Int, height : Int) extends Worker(y, height){
    protected override def manage = {
      if (generations >= totalgenerations) alive=false 
      generations += 1
      k(generations.toString) 
    }
  }

  // Generates an appropriate amount of workers with the proper dimensions
  private def makeWorkers(numWorkers : Int) : Array[PROC] = {
    var height = BOARD_SIZE / numWorkers
    var extra = BOARD_SIZE % numWorkers
    var ws = new Array[Worker](numWorkers) 
    var currentY = 0
    for (i<- 0 until extra){
      ws(i) = new Worker(currentY , height+1)
      currentY += height+1
    }
    for (i<- extra until numWorkers-1){
      ws(i) = new Worker(currentY, height)
      currentY += height
    }
    ws(numWorkers-1) = new Manager(currentY , height) 
    (for (w<- ws) yield w.worker)
  }

  def kill = {alive = false}

  def game : PROC = {
      //totalgenerations = 100
      ||(makeWorkers(WORKERS)) 
      
  }
 }

