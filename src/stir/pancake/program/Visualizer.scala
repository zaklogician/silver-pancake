package stir.pancake.program

import stir.pancake._

import squants._
import squants.thermal.Celsius

import java.awt.Color
import java.awt.Graphics
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.ActionListener
import java.awt.event.ActionEvent
import java.awt.FlowLayout

import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JButton
import javax.swing.BoxLayout

object Main {
  def main(args: Array[String]): Unit = {
    MainWindow.setVisible(true)
  }
}

object MainWindow extends JFrame("Bathtub Simulator 2016") {
  val model: Model  = Model1
  val tileSize: Int = 8
  
  this.add(PanelV)
  this.setSize(Canvas.getWidth,Canvas.getHeight)
  this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
  
  object PanelV extends JPanel {
    this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS))
    this.add(Canvas)
    val modeButton: JButton = new JButton("Mode")
    modeButton.addActionListener(new ActionListener {
      override def actionPerformed(e: ActionEvent): Unit = { Canvas.switchVisualizer; Canvas.repaint() }
    })
    val stepButton: JButton = new JButton("Step")
    stepButton.addActionListener(new ActionListener {
      override def actionPerformed(e: ActionEvent): Unit = {
        var time = System.currentTimeMillis()
        for (x <- 1 to 1) model.runIteration 
        Canvas.repaint()
        println("-------------- Iteration " + model.currentIteration)
        println("  Elapsed:     " + (model.currentIteration*0.06).round + " min")
        println("  Temperature: " + model.tempAverage.toCelsiusScale + " C" )
        println("  Std. Dev.:   " + model.tempStdev)
        println("  Took:        " + (System.currentTimeMillis() - time) + "ms")
      }
    })
    val step100Button:  JButton = new JButton("250")
    step100Button.addActionListener(new ActionListener {
      override def actionPerformed(e: ActionEvent): Unit = { 
        var time = System.currentTimeMillis()
        for (x <- 1 to 250) model.runIteration 
        Canvas.repaint()
        println("-------------- Iteration " + model.currentIteration)
        println("  Elapsed:     " + (model.currentIteration*0.06).round + " min")
        println("  Temperature: " + model.tempAverage.toCelsiusScale + " C" )
        println("  Std. Dev.:   " + model.tempStdev)
        println("  Took:        " + (System.currentTimeMillis() - time) + "ms")
      }
    })
    val step1000Button:  JButton = new JButton("Run")
    step1000Button.addActionListener(new ActionListener {
      override def actionPerformed(e: ActionEvent): Unit = { 
        var time = System.currentTimeMillis()
        for (x <- 1 to 250) model.runIteration 
        Canvas.repaint()
        println("-------------- Iteration " + model.currentIteration)
        println("  Elapsed:     " + (model.currentIteration*0.06).round + " min")
        println("  Temperature: " + model.tempAverage.toCelsiusScale + " C" )
        println("  Std. Dev.:   " + model.tempStdev)
        println("  Took:        " + (System.currentTimeMillis() - time) +"ms")
        for (x <- 1 to 250) model.runIteration 
        Canvas.repaint()
        println("-------------- Iteration " + model.currentIteration)
        println("  Elapsed:     " + (model.currentIteration*0.06).round + " min")
        println("  Temperature: " + model.tempAverage.toCelsiusScale + " C" )
        println("  Std. Dev.:   " + model.tempStdev)
        println("  Took:        " + (System.currentTimeMillis() - time) +"ms")
        for (x <- 1 to 250) model.runIteration 
        Canvas.repaint()
        println("-------------- Iteration " + model.currentIteration)
        println("  Elapsed:     " + (model.currentIteration*0.06).round + " min")
        println("  Temperature: " + model.tempAverage.toCelsiusScale + " C" )
        println("  Std. Dev.:   " + model.tempStdev)
        println("  Took:        " + (System.currentTimeMillis() - time) +"ms")
        for (x <- 1 to 250) model.runIteration 
        Canvas.repaint()
        println("-------------- Iteration " + model.currentIteration)
        println("  Elapsed:     " + (model.currentIteration*0.06).round + " min")
        println("  Temperature: " + model.tempAverage.toCelsiusScale + " C" )
        println("  Std. Dev.:   " + model.tempStdev)
        println("  Took:        " + (System.currentTimeMillis() - time) +"ms")
      }
    })
    object PanelH extends JPanel {
      this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS))
      this.add(modeButton)
      this.add(stepButton)
      this.add(step100Button)
      this.add(step1000Button)
    }
    this.add(PanelH)
  }
  
  
  object Canvas extends JPanel {
    this.setSize((model.columnsC+5)*tileSize, (model.rowsC+7)*tileSize)
    
    sealed trait Visualizer {
      def apply(cell: model.IndexC): Color
    }
    case object TemperatureMode extends Visualizer {
      def apply(cell: model.IndexC): Color = {
        if (cell.valid) {
          val red: Int = (cell.temperature.to(Celsius)*8.5 - 170).toInt
          new Color(if (red > 255) 255 else if (red < 0) 0 else red,0,0)
        } else Color.magenta
      }
    }
    case object MaterialMode extends Visualizer {
      def apply(cell: model.IndexC): Color = {
        if (cell.valid) cell.material match {
          case Water        => Color.blue
          case Steel        => Color.gray
          case Acrylic      => Color.orange
          case Air          => Color.white
          case HumanInWater => new Color(0,0,127)
        } else Color.magenta
      }
    }
    case object CalorMode extends Visualizer {
      def apply(cell: model.IndexC): Color = {
        if (cell.valid) {
          val n = cell.calors
          val cyan = if (n > 23) 255 else if (n <= 2) 0 else 12*n - 22
          try { new Color(0,cyan,cyan) } catch { case (e: java.lang.IllegalArgumentException) => println(cyan) }
          new Color(0,cyan,cyan)
        } else Color.magenta
      }
    }
    
    private var visualizer: Visualizer = TemperatureMode
    def switchVisualizer: Unit = visualizer = visualizer match {
      case TemperatureMode => CalorMode
      case MaterialMode    => TemperatureMode
      case CalorMode       => MaterialMode
    }
    
    override def paintComponent(g: Graphics): Unit = model.cells foreach { c =>
      g.setColor(visualizer(c))
      g.fillRect(c.column*tileSize, c.row*tileSize, tileSize, tileSize)
    }
    
    this.addMouseListener( new MouseAdapter {
      override def mouseClicked(e: MouseEvent): Unit = {
        val loc = e.getPoint
        val o   = model.IndexC(loc.getX.toInt/tileSize,loc.getY.toInt/tileSize)
        if(o.valid) {
          println("Location " + o.column + "," + o.row)
          println("    material:      " + o.material )
          println("    temperature:   " + o.temperature.toCelsiusScale )
          println("    calors:        " + o.calors )
        }
      }
    })
    
  }
  
}