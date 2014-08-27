/*
 *
 *    ___                      _   _     _ _          ___        _
 *   / __|___ _ _  _ _  ___ __| |_(_)_ _(_) |_ _  _  | __|_ _ __| |_ ___ _ _ _  _  TM
 *  | (__/ _ \ ' \| ' \/ -_) _|  _| \ V / |  _| || | | _/ _` / _|  _/ _ \ '_| || |
 *   \___\___/_||_|_||_\___\__|\__|_|\_/|_|\__|\_, | |_|\__,_\__|\__\___/_|  \_, |
 *                                             |__/                          |__/
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 *  Unauthorized copying of this file, via any medium, is strictly prohibited.
 *  Proprietary and confidential.
 *
 * ==================================================================================
 */

package io.xtech.babel.grammar.diagram

import chrriis.grammar.model.{Rule, GrammarToRRDiagram, Grammar, BNFToGrammar}
import chrriis.grammar.rrdiagram.{RRDiagramToSVG, RRDiagram}
import java.io._

object GrammarHelper {
  def getGrammarFromFile(filePath: String): Grammar = {
    val reader = new BufferedReader(new FileReader(filePath))

    (new BNFToGrammar()).convert(reader)
  }

  def getGrammarFromString(grammar: String): Grammar = {
    (new BNFToGrammar()).convert(new StringReader(grammar))
  }

  def getDiagramFromGrammar(grammar: Grammar): List[RRDiagram] = {
    val grammarToRRDiagram = new GrammarToRRDiagram()
    val rules: List[Rule] = grammar.getRules().toList
    rules.map(rule => {
      // Do something with diagram, like get the SVG.
      grammarToRRDiagram.convert(rule)

    })
  }

  def getSVGFromRRDiagram(diagram: RRDiagram): String = {
    val rrDiagramToSVG = new RRDiagramToSVG()
    rrDiagramToSVG.convert(diagram)
  }

  def getSVGFromFile(file: File): List[String] = {
    val grammar = getGrammarFromFile(file.getPath)
    val diagrams = getDiagramFromGrammar(grammar)
    diagrams.map(getSVGFromRRDiagram)
  }
}

/**
 * Executed by the maven build to generate the documentation RailRoad diagrams.
 */
object GrammarParser extends App {
  val filepath: String = args(0) // grammar directory
  val fileOutput: String = args(1) // grammar diagrams directory

  val fileExtension: String = "grammar"
  val svgExtension: String = "svg"

  (new File(fileOutput)).mkdir()

  try {
    val files = (new File(filepath)).listFiles().filter(_.getName.endsWith(fileExtension))
    files.foreach(file => {
      val fileName = file.getName.splitAt(file.getName.lastIndexOf(s".$fileExtension"))._1
      val diagrams = GrammarHelper.getSVGFromFile(file)

      var i = 0

      diagrams.foreach(diagram => {
        i += 1
        val pw = new java.io.PrintWriter(new File(s"$fileOutput/$fileName-$i.$svgExtension"))
        pw.write(diagram)
        pw.close()
      })

    })
  }catch{
    case ex: Throwable => {
      println(s"${ex.getMessage} thrown during grammar processing")
      ex.printStackTrace()
    }
  }



}
