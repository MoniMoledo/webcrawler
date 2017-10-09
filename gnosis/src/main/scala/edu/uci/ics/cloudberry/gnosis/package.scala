package edu.uci.ics.cloudberry

import java.io.File
import java.nio.charset.{Charset, CodingErrorAction}

package object gnosis {

  class UnknownEntityException(entity: IEntity) extends RuntimeException("unknown entity:" + entity)

  type TypeLevel = Int

  val BrCountryLevel: TypeLevel = 1
  val BrStateLevel: TypeLevel = 2
  val BrCityLevel: TypeLevel = 3

  val BrOrderedLevels: Seq[TypeLevel] = Seq(BrCountryLevel, BrStateLevel, BrCityLevel)

  def loadSmallJSONFile(file: File): String = {
    val decoder = Charset.forName("UTF-8").newDecoder()
    decoder.onMalformedInput(CodingErrorAction.IGNORE)
    scala.io.Source.fromFile(file)(decoder).getLines().mkString("\n")
  }

}
