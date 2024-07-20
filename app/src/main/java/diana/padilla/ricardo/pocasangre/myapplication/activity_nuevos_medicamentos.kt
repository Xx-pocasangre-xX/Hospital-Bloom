package diana.padilla.ricardo.pocasangre.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

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
    }
}