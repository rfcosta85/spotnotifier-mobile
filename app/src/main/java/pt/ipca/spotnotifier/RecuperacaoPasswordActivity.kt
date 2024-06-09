package pt.ipca.spotnotifier

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RecuperacaoPasswordActivity : AppCompatActivity() {

        override fun onCreate(savedInstanceState: Bundle?){
            super.onCreate(savedInstanceState)
            setContentView(R.layout.recuperacao_password)

            val button = findViewById<ImageButton>(R.id.imageButton)
            button.setOnClickListener {
                // Finish the current activity and go back to the previous one
                finish()
            }
        }

    fun recovery_password(view: View){
        Toast.makeText(baseContext, "Botão clicado", Toast.LENGTH_SHORT).show()
    }
}
