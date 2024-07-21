package RecyclerViewHelpers

import Modelo.ClaseConexion
import Modelo.Pacientes
import Modelo.PacientesDetalles
import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import diana.padilla.ricardo.pocasangre.myapplication.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.w3c.dom.Text
import java.sql.ResultSet

class AdaptadorPacientes(var Datos: List<Pacientes>): RecyclerView.Adapter<ViewHolderPacientes>() {

    fun ActualizarLista(nuevaLista: List<Pacientes>) {
        Datos = nuevaLista
        notifyDataSetChanged()
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
                    Toast.makeText(context, "No se pudo mostrar la información del paciente", Toast.LENGTH_LONG).show()
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