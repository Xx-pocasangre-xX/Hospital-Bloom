package RecyclerViewHelpers

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import diana.padilla.ricardo.pocasangre.myapplication.R

class ViewHolderPacientes(view: View):RecyclerView.ViewHolder(view) {
    val txtPacienteNombre = view.findViewById<TextView>(R.id.txtPacienteNombre)
    val txtPacienteApellido = view.findViewById<TextView>(R.id.txtPacienteApellido)
    val btnBorrar = view.findViewById<ImageView>(R.id.btnBorrar)
    val btnEditar = view.findViewById<ImageView>(R.id.btnEditar)
}