package Modelo

data class Pacientes(
    val id_paciente: Int,
    val nombres: String,
    val apellidos: String,
    val edad: Int,
    val enfermaedad: String,
    val numero_habitacion: Int,
    val numero_cama: Int,
    val fecha_ingreso: String
)
