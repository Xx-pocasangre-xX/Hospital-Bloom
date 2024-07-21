package RecyclerViewHelpers

import Modelo.Pacientes
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import diana.padilla.ricardo.pocasangre.myapplication.R

class AdaptadorPacientes(var Datos: List<Pacientes>): RecyclerView.Adapter<ViewHolderPacientes>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderPacientes {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.activity_card_pacientes, parent, false)
        return ViewHolderPacientes(vista)
    }

    override fun getItemCount() = Datos.size

    override fun onBindViewHolder(holder: ViewHolderPacientes, position: Int) {
        val item = Datos[position]
        holder.txtPacienteNombre.text = item.nombres
        holder.txtPacienteApellido.text = item.apellidos
    }

}