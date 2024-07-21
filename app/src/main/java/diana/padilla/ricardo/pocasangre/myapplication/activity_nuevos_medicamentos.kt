package diana.padilla.ricardo.pocasangre.myapplication

import Modelo.ClaseConexion
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class activity_nuevos_medicamentos : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_nuevos_medicamentos)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnPacientes = findViewById<Button>(R.id.btnPacientes)
        btnPacientes.setOnClickListener {
            val pantallaRegistroPacientes = Intent(this, activity_registro_pacientes::class.java)
            startActivity(pantallaRegistroPacientes)
            overridePendingTransition(0, 0)
        }

        val btnAsignarMedicamentos = findViewById<Button>(R.id.btnAsignarMedicamentos)
        btnAsignarMedicamentos.setOnClickListener {
            val pantallaAsignarMedicamentos = Intent(this, activity_asignar_medicamentos::class.java)
            startActivity(pantallaAsignarMedicamentos)
            overridePendingTransition(0, 0)
        }

        val txtNombreMedicamento = findViewById<EditText>(R.id.txtNombreMedicamento)
        val txtDescripcionMedicamento = findViewById<EditText>(R.id.txtDescripcionMedicamento)
        val btnAgregarMedicamento = findViewById<Button>(R.id.btnAgregarMedicamento)

        btnAgregarMedicamento.setOnClickListener {

            val nombre = txtNombreMedicamento.text.toString()
            val descripcion = txtDescripcionMedicamento.text.toString()

            if(nombre.isEmpty() || descripcion.isEmpty()){
                Toast.makeText(
                    this,
                    "Error, para guardar el medicamento debes llenar todas las casillas.",
                    Toast.LENGTH_SHORT
                ).show()
            }else {
                CoroutineScope(Dispatchers.IO).launch {
                    val objConexion = ClaseConexion().cadenaConexion()
                    val addMedicamento =
                        objConexion?.prepareStatement("INSERT INTO Medicamentos (nombre_medicamento, descripcion) VALUES (?, ?)")!!
                    addMedicamento.setString(1, txtNombreMedicamento.text.toString())
                    addMedicamento.setString(2, txtDescripcionMedicamento.text.toString())
                    addMedicamento.executeQuery()

                    withContext(Dispatchers.Main) {
                        AlertDialog.Builder(this@activity_nuevos_medicamentos)
                            .setTitle("Registro de medicamento exitoso!")
                            .setMessage("El medicamento se ha guardado exitosamente.")
                            .setPositiveButton("Aceptar", null)
                            .show()
                        txtNombreMedicamento.setText("")
                        txtDescripcionMedicamento.setText("")
                    }
                }
            }
        }
    }
}