package stir.pancake

object RNG extends java.util.Random(0xDEADBEEF) {
  
  def nextDirection: Direction = List(Up,Down,Left,Right)(this.nextInt(4))
  
  def weightedPick(distr: List[Double]): Int = {
    val threshold: Double = RNG.nextDouble * distr.sum
    var cumulative: Double = 0
    var pick: Int = 0
    for( p <- distr ) {
      cumulative = cumulative + p
      if (threshold <= cumulative) return pick else pick = pick + 1
    }
    return 0
  }
  
}