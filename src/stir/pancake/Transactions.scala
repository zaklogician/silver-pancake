package stir.pancake

import scalaz._

object Transactions {
  
  type Transaction[A] = Reader[Array[Int],A]
  
  object Transaction {
    def apply[A](run: Array[Int] => A): Transaction[A] = Reader[Array[Int],A](run)
  }
  
}