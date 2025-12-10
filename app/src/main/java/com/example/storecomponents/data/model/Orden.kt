package com.example.storecomponents.data.model



// Definición de la clase de datos para órdenes
data class orden(
    val idorden: Int = 0,
    val userId: String = "",
    val productos : List<orderitem> = emptyList(),
    val totalacumulado : Double = 0.0,
    val estado : ordenestado = ordenestado.PENDIENTE
)
// Definición de la clase de datos para items de orden
data class orderitem(
    val idproducto: String = "",
    val cantidad: Int = 0,
    val precio: Double = 0.0
)
// Definición de estados de orden una vez realizada
enum class ordenestado {
    PENDIENTE,
    PROCESANDO,
    EN_CAMINO,
    PAGADO,
    ENVIADO,
    ENTREGADO,
    CANCELADO

}


