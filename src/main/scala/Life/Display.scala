package Life.Display
import io.threadcso._
import java.awt._
import java.awt.event.{ActionEvent, ActionListener}
import Life.utils._

class Display(k : Continuation[Command]) extends Frame { 

  def makeListener(cm : Command) : ActionListener = new ActionListener { def actionPerformed(e : ActionEvent) = { k(cm) }}
 
  val g=new Button("Pause")  
  g.setBounds(30,140,80,30)
  this.add(g)
  g.addActionListener( makeListener(Pause)  )
  val h=new Button("Stop")  
  h.setBounds(30,180,80,30)
  this.add(h)
  h.addActionListener( makeListener(Stop)  )
  val j=new Button("Resume")  
  j.setBounds(30,220,80,30)
  this.add(j)
  j.addActionListener( makeListener(Resume)  )
  val l=new Button("Quit")  
  l.setBounds(30,260,80,30)
  this.add(l)
  l.addActionListener( makeListener(Quit)  )

  val b=new Button("Game")  
  b.setBounds(30,100,80,30)
  this.add(b)
  this.setSize(300,300)
  this.setLayout(null)
  this.setVisible(true)
  b.addActionListener( makeListener(Start) )
  
  def _update(x : Data) = {b.setLabel(x) }

  def update(x:Data) = {_update(x) }  

}

