package pt.ipca.spotnotifier

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class SobreActivity : AppCompatActivity() {

        override fun onCreate(savedInstanceState: Bundle?){
            super.onCreate(savedInstanceState)
            setContentView(R.layout.sobre)

            val button = findViewById<ImageButton>(R.id.imageButton)
            button.setOnClickListener {
                // Finish the current activity and go back to the previous one
                finish()
            }
        }
}
