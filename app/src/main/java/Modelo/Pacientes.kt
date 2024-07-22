package Modelo

data class Pacientes(
    var id_paciente: Int,
    var nombres: String,
    var apellidos: String,
    var edad: Int,
    var enfermaedad: String,
    var numero_habitacion: Int,
    var numero_cama: Int,
    var fecha_ingreso: String
)
