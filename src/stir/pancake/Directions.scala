package stir.pancake

sealed trait Direction { val column: Int; val row: Int }
case object Up    extends Direction { val column: Int =  0; val row: Int =  1 }
case object Down  extends Direction { val column: Int =  0; val row: Int = -1 }
case object Left  extends Direction { val column: Int = -1; val row: Int =  0 }
case object Right extends Direction { val column: Int =  1; val row: Int =  0 }

object Direction {
  def list: List[Direction] = List(Up,Down,Left,Right)
}