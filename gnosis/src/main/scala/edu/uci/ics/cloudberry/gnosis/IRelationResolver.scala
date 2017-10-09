package edu.uci.ics.cloudberry.gnosis

/**
  * This file contains code that is borrowed from https://github.com/ISG-ICS/cloudberry.
  *
  * Copyright: mixed. See gnosis/LICENSE for copyright and licensing information.
  **/


trait IRelationResolver {

  def getChildren(entity: IEntity): Seq[IEntity]

  def getParent(entity: IEntity): Option[IEntity]
}

