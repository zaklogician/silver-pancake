package stir.pancake

object RNG extends java.util.Random(0xDEADBEEF) {
  
  def nextDirection: Direction = List(Up,Down,Left,Right)(this.nextInt(4))
  
  def partition(number: Int, buckets: Int): List[Int] = {
    // performance critical, keep imperative
    val bucket: Array[Int] = Array.ofDim(buckets)
    var remaining = number
    while (remaining > 0) {
      val b = this.nextInt(bucket.length)
      bucket.update(b, bucket(b) + 1)
      remaining = remaining - 1
    }
    bucket.toList
  }
}