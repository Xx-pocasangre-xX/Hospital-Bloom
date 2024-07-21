package Modelo

data class PacientesDetalles(
    val nombres: String,
    val apellidos: String,
    val edad: Int,
    val enfermaedad: String,
    val numero_habitacion: Int,
    val numero_cama: Int,
    val fecha_ingreso: String,
    val nombre_medicamento: String,
    val hora_aplicaciones: String
)
