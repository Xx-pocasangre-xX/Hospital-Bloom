package RecyclerViewHelpers

import Modelo.ClaseConexion
import Modelo.Pacientes
import Modelo.PacientesDetalles
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.icu.util.Calendar
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import diana.padilla.ricardo.pocasangre.myapplication.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.w3c.dom.Text
import java.sql.ResultSet
import java.sql.SQLException

class AdaptadorPacientes(var Datos: List<Pacientes>): RecyclerView.Adapter<ViewHolderPacientes>() {

    fun ActualizarLista(nuevaLista: List<Pacientes>) {
        Datos = nuevaLista
        notifyDataSetChanged()
    }

    fun updateScreen(
        id_paciente: Int,
        nuevaEdad: Int,
        nuevaEnfermedad: String,
        nuevaHabitacion: Int,
        nuevaCama: Int,
        nuevoIngreso: String
    ) {
        val index = Datos.indexOfFirst { it.id_paciente == id_paciente }
        if (index != -1){
            Datos[index].edad = nuevaEdad
        Datos[index].enfermaedad = nuevaEnfermedad
        Datos[index].numero_habitacion = nuevaHabitacion
        Datos[index].numero_cama = nuevaCama
        Datos[index].fecha_ingreso = nuevoIngreso
        notifyItemChanged(index)
    }
}

    fun deleteData(context: Context, id_paciente: Int, position: Int) {
        val dataList = Datos.toMutableList()
        dataList.removeAt(position)

        GlobalScope.launch(Dispatchers.IO) {
            val objConexion = ClaseConexion().cadenaConexion()
            if(objConexion != null) {
                try {
                    objConexion.autoCommit = false

                    val borrarAplicaciones = objConexion.prepareStatement("DELETE FROM Aplicacion_Medicamentos WHERE id_paciente = ?")!!
                    borrarAplicaciones.setInt(1, id_paciente)
                    val aplicacionesEliminadas = borrarAplicaciones.executeUpdate()
                    Log.d("deleteData", "Aplicaciones eliminadas: $aplicacionesEliminadas")

                    val borrarPaciente =
                        objConexion.prepareStatement("delete from Pacientes where id_paciente = ?")!!
                    borrarPaciente.setInt(1, id_paciente)
                    val pacienteEliminado = borrarPaciente.executeUpdate()
                    Log.d("deleteData", "Paciente eliminado: $pacienteEliminado")

                    objConexion.commit()
                    Log.d("deleteData", "Commit exitoso")

                    withContext(Dispatchers.Main) {
                        Datos = dataList.toList()
                        notifyItemRemoved(position)
                        notifyDataSetChanged()
                        Toast.makeText(
                            context,
                            "Paciente borrado correctamente",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                } catch (e: SQLException) {
                    Log.e("deleteData", "Error al ejecutar operación SQL", e)
                    try {
                        objConexion.rollback()
                        Log.d("deleteData", "Rollback exitoso")
                    }catch (rollbackEx: SQLException){
                        Log.e("deleteData", "Error al hacer rollback", rollbackEx)
                    }
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Error al borrar paciente: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }catch (e: Exception){
                    Log.e("deleteData", "Error inesperado", e)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Error inesperado: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                } finally {
                    try {
                        objConexion.close()
                        Log.d("deleteData", "Conexión cerrada exitosamente")
                    }catch (closeEx: SQLException){
                        Log.e("deleteData", "Error al cerrar la conexión", closeEx)
                    }
                }
            }else{
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error en la conexión a la base de datos", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun updateData(context: Context, nuevaEdad: Int, nuevaEnfermedad: String, nuevaHabitacion: Int, nuevaCama: Int, nuevoIngreso: String, id_paciente: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            val objConexion = ClaseConexion().cadenaConexion()
            if(objConexion != null) {
                try {
                    val actualizarPaciente =
                        objConexion.prepareStatement("update Pacientes set edad = ?, enfermaedad = ?, numero_habitacion = ?, numero_cama = ?, fecha_ingreso = ? where id_paciente = ?")!!
                    actualizarPaciente.setInt(1, nuevaEdad)
                    actualizarPaciente.setString(2, nuevaEnfermedad)
                    actualizarPaciente.setInt(3, nuevaHabitacion)
                    actualizarPaciente.setInt(4, nuevaCama)
                    actualizarPaciente.setString(5, nuevoIngreso)
                    actualizarPaciente.setInt(6, id_paciente)
                    actualizarPaciente.executeUpdate()

                    withContext(Dispatchers.Main) {
                        updateScreen(
                            id_paciente,
                            nuevaEdad,
                            nuevaEnfermedad,
                            nuevaHabitacion,
                            nuevaCama,
                            nuevoIngreso
                        )
                        Toast.makeText(
                            context,
                            "Datos actualizados correctamente",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Error al actualizar datos", Toast.LENGTH_SHORT)
                            .show()
                    }
                    e.printStackTrace()
                }finally {
                    objConexion.close()
                }
            }else{
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error en la conexión a la base de datos", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderPacientes {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_card_pacientes, parent, false)
        return ViewHolderPacientes(vista)
    }

    override fun getItemCount() = Datos.size

    override fun onBindViewHolder(holder: ViewHolderPacientes, position: Int) {
        val item = Datos[position]
        holder.txtPacienteNombre.text = item.nombres
        holder.txtPacienteApellido.text = item.apellidos

        holder.btnBorrar.setOnClickListener {
            val context = holder.itemView.context

            val builder = androidx.appcompat.app.AlertDialog.Builder(context)
            builder.setTitle("Borrar")
            builder.setMessage("Estás segur@ de borrar este paciente?")

            builder.setPositiveButton("Si"){dialog, which ->
                deleteData(context, item.id_paciente, position)
            }

            builder.setNeutralButton("No"){dialog, which ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()

        }

        holder.btnEditar.setOnClickListener {
            val context = holder.itemView.context

            val dialogView = LayoutInflater.from(context).inflate(R.layout.activity_editar_paciente, null)
            val editEdad = dialogView.findViewById<EditText>(R.id.txtPacienteEdadEdit)
            val editEnfermedad = dialogView.findViewById<EditText>(R.id.txtEnfermedadPacientesEdit)
            val editHabitacion = dialogView.findViewById<EditText>(R.id.txtNumeroHabitacionEdit)
            val editCama = dialogView.findViewById<EditText>(R.id.txtNumeroCamaEdit)
            val editIngreso = dialogView.findViewById<EditText>(R.id.txtFechaIngresoEdit)

            editIngreso.setOnClickListener {
                val calendario = Calendar.getInstance()
                val anio = calendario.get(Calendar.YEAR)
                val mes = calendario.get(Calendar.MONTH)
                val dia = calendario.get(Calendar.DAY_OF_MONTH)

                val datePickerDialog = DatePickerDialog(
                    context,
                    { view, anioSeleccionado, mesSeleccionado, diaSeleccionado ->
                        val calendarioSeleccionado = Calendar.getInstance()
                        calendarioSeleccionado.set(anioSeleccionado, mesSeleccionado, diaSeleccionado)
                            val fechaSeleccionada = "$diaSeleccionado/${mesSeleccionado + 1}/$anioSeleccionado"
                            editIngreso.setText(fechaSeleccionada)
                    },
                    anio, mes, dia
                )
                datePickerDialog.show()
            }

            editEdad.setHint(item.edad.toString())
            editEnfermedad.setHint(item.enfermaedad)
            editHabitacion.setHint(item.numero_habitacion.toString())
            editCama.setHint(item.numero_cama.toString())
            editIngreso.setHint(item.fecha_ingreso)

            val builder = AlertDialog.Builder(context)
            builder.setTitle("Actualizar Paciente")
            builder.setView(dialogView)
            builder.setPositiveButton("Guardar"){dialog, _ ->
                val nuevaEdad = editEdad.text.toString().toIntOrNull() ?: item.edad
                val nuevaEnfermedad = editEnfermedad.text.toString()
                val nuevaHabitacion = editHabitacion.text.toString().toIntOrNull() ?: item.numero_habitacion
                val nuevaCama = editCama.text.toString().toIntOrNull() ?: item.numero_cama
                val nuevoIngreso = editIngreso.text.toString()

                updateData(context, nuevaEdad, nuevaEnfermedad, nuevaHabitacion, nuevaCama, nuevoIngreso, item.id_paciente)

                dialog.dismiss()
            }

            builder.setNegativeButton("Cancelar") {dialog, _ ->
                dialog.dismiss()
            }

            val alertDialog = builder.create()
            alertDialog.show()
        }

        holder.itemView.setOnClickListener {
            mostrarDialog(holder.itemView.context, item.id_paciente)
        }
    }

    private fun mostrarDialog(context: Context, idPaciente: Int) {
        val builder = AlertDialog.Builder(context)
        val dialogLayout =
            LayoutInflater.from(context).inflate(R.layout.activity_paciente_detalles, null)
        builder.setView(dialogLayout)

        val alertDialog = builder.create()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val pacienteDetalles = obtenerDetallesPaciente(idPaciente)

                withContext(Dispatchers.Main) {
                    dialogLayout.findViewById<TextView>(R.id.txtPacienteNombreI)?.text =
                        pacienteDetalles.nombres
                    dialogLayout.findViewById<TextView>(R.id.txtPacienteApellidoI)?.text =
                        pacienteDetalles.apellidos
                    dialogLayout.findViewById<TextView>(R.id.txtEdadPacienteI)?.text =
                        pacienteDetalles.edad.toString()
                    dialogLayout.findViewById<TextView>(R.id.txtEnfermedadPacienteI)?.text =
                        pacienteDetalles.enfermaedad
                    dialogLayout.findViewById<TextView>(R.id.txtNumeroHabitacionI)?.text =
                        pacienteDetalles.numero_habitacion.toString()
                    dialogLayout.findViewById<TextView>(R.id.txtNumeroCamaI)?.text =
                        pacienteDetalles.numero_cama.toString()
                    dialogLayout.findViewById<TextView>(R.id.txtFechaIngresoI)?.text =
                        pacienteDetalles.fecha_ingreso
                    dialogLayout.findViewById<TextView>(R.id.txtMedicamentoI)?.text =
                        pacienteDetalles.nombre_medicamento
                    dialogLayout.findViewById<TextView>(R.id.txtHoraAplicacionI)?.text =
                        pacienteDetalles.hora_aplicaciones

                    alertDialog.show()
                }

        } catch (e: Exception){
            Log.e("AdaptadorPacientes", "Error al mostrar el dialog", e)
                withContext(Dispatchers.Main){
                    Toast.makeText(context, "No se pudo mostrar la información del paciente, debes asignar un medicamento antes", Toast.LENGTH_LONG).show()
                }
        }
    }
}

    private fun obtenerDetallesPaciente(idPaciente: Int): PacientesDetalles{
        val objConexion = ClaseConexion().cadenaConexion()
        if(objConexion == null){
            throw IllegalStateException("La conexión a la base de datos es nula")
        }

        val statement = objConexion.createStatement()
        val query = """
            SELECT 
                p.nombres AS Nombres,
                p.apellidos AS Apellidos,
                p.edad AS Edad,
                p.enfermaedad AS Enfermedad,
                p.numero_habitacion AS Numero_Habitacion,
                p.numero_cama AS Numero_Cama,
                p.fecha_ingreso AS Ingreso,
                m.nombre_medicamento AS Medicamentos,
                am.hora_aplicaciones
            FROM 
                Aplicacion_Medicamentos am
            INNER JOIN 
                Pacientes p ON am.id_paciente = p.id_paciente
            INNER JOIN 
                Medicamentos m ON am.id_medicamento = m.id_medicamento
            WHERE p.id_paciente = $idPaciente
        """.trimIndent()

        val resultSet: ResultSet = statement.executeQuery(query)
        if(!resultSet.next()){
            throw IllegalStateException("No se encontraron detalles para el paciente")
        }

        val pacienteDetalles = PacientesDetalles(
            nombres = resultSet.getString("Nombres"),
            apellidos = resultSet.getString("Apellidos"),
            edad = resultSet.getInt("Edad"),
            enfermaedad = resultSet.getString("Enfermedad"),
            numero_habitacion = resultSet.getInt("Numero_Habitacion"),
            numero_cama = resultSet.getInt("Numero_Cama"),
            fecha_ingreso = resultSet.getString("Ingreso"),
            nombre_medicamento = resultSet.getString("Medicamentos"),
            hora_aplicaciones = resultSet.getString("hora_aplicaciones")
        )

        resultSet.close()
        statement.close()
        objConexion.close()

        return pacienteDetalles
    }

}