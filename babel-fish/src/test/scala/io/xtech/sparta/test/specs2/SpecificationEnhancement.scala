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
 * ==================================================================================
 */

package io.xtech.sparta.test.specs2

import org.specs2.specification.{ SpecificationStructure, Text, Fragments }
import org.specs2.mutable.Specification

import scala.collection.immutable

/**
  * Formats the Specification output by adding #### to titles (everything but examples)
  * todo: add more flexibilities
  */
trait FormatedSpecification extends Specification {

  abstract override def is: Fragments = super.is.map(x => x match {
    //case ex: Example => ex.copy(desc = ex.desc)
    case st: Text if !st.t.trim().isEmpty => st.copy(text = st.text.prepend("####"))
    case any                              => any
  })

}

/**
  * Creates a Specification aggregating other Specification
  */
trait AggregatingSpecification extends Specification {

  def title: String

  def specs: immutable.Seq[SpecificationStructure]

  override def is = s2"""
  ${title.title}
  ${specs.foldLeft(Fragments())((x, y) => x.append(y.is))}
  """

}

/**
  * add the possibility to create a link to another specification
  */
trait ParentSpecification {
  self: org.specs2.Specification =>

  def createSubPage(title: String, spec: SpecificationStructure) = ("" ~ (title, spec))
}
