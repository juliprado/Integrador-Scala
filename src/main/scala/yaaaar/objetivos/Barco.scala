package yaaaar.objetivos

import Yaaaar.Item
import yaaaar.misiones.Mision
import yaaaar.piratas.Pirata

class Barco(val capacidad: Int, var mision: Mision, var tripulantes: List[Pirata]) extends Objetivo {

  override def loPuedeSaquear(unPirata: Pirata): Boolean = unPirata.estaPasadoDeGrog()

  def puedeUnirse(unPirata: Pirata): Boolean = this.hayLugar() && mision.esUtil(unPirata)

  def hayLugar(): Boolean = this.tieneCantidadDeTripulantes(_ < capacidad)

  def tieneCantidadDeTripulantes(unCriterio: Int => Boolean): Boolean = unCriterio(this.cantidadTripulantes())

  def cantidadTripulantes(): Int = tripulantes.size

  def agregar(unTripulante: Pirata): Unit = if(this.puedeUnirse(unTripulante)) this.modificarTripulantes(_ ::: List(unTripulante))

  def modificarTripulantes(unCriterio: List[Pirata] => List[Pirata]): Unit = tripulantes = unCriterio(tripulantes)

  def cambiarMision(unaMision: Mision): Unit = {
    this.eliminarInutiles(unaMision)
    mision = unaMision
  }

  def eliminarInutiles(unaMision: Mision): Unit = this.modificarTripulantes(_.filter { unaMision.esUtil })

  def pirataMasEbrio(): Pirata = tripulantes.maxBy(_.nivelEbriedad) 

  def anclarEn(unaCiudad: CiudadCostera): Unit = {
    this.todosTomanGrogYGastanMonedas()
    this.perderTripulanteMasEbrioEn(unaCiudad)
  }

  def todosTomanGrogYGastanMonedas(): Unit = tripulantes.foreach(_.tomarUnTrago()) 

  def perderTripulanteMasEbrioEn(unaCiudad: CiudadCostera): Unit = {
    this.modificarTripulantes(_.diff(List(this.pirataMasEbrio())))
    unaCiudad.agregarUnHabitante()
  }

  def esTemible(): Boolean = mision.puedeRealizarla(this)
  
  def tieneSuficienteTripulacion(): Boolean = this.tieneCantidadDeTripulantes(_ >= (capacidad * 0.9))

  def tiene(unItem: Item): Boolean = tripulantes.exists { _.tiene(unItem) }

  override def cantidadNecesariaDeTripulantes(): Int => Boolean = this.cantidadTripulantes() / 2 >= _

  def estanTodosPasadosDeGrog(): Boolean = tripulantes.forall { _.estaPasadoDeGrog() }

  def cantidadDeTripulantesPasadosDeGrog(): Int = tripulantes.count { _.estaPasadoDeGrog() }

  def tripulantesPasadosDeGrog(): List[Pirata] = tripulantes.filter { _.estaPasadoDeGrog() }

  def cantidadItemsDistintosEntreTripulantesPasadosDeGrog(): Int = this.itemsDeTodosLosTripulantes().toSet.size

  def itemsDeTodosLosTripulantes(): List[Item] = this.tripulantesPasadosDeGrog().flatMap(_.items)

  def tripulantePasadosDeGrogConMasDinero(): Pirata = this.tripulantesPasadosDeGrog().maxBy(_.cantidadMonedas)

  def tripulanteMasInvitador(): Pirata = tripulantes.maxBy(_.cantidadDeInvitadosPara(this))

  def cantidadDeInvitadosPor(unTripulante: Pirata): Int = tripulantes.count { otroTripulante =>  otroTripulante.fueInvitadoPor(unTripulante) }

}
