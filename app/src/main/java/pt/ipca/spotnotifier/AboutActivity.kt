package pt.ipca.spotnotifier

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class AboutActivity : AppCompatActivity() {

        override fun onCreate(savedInstanceState: Bundle?){
            super.onCreate(savedInstanceState)
            setContentView(R.layout.about)

            val button = findViewById<ImageButton>(R.id.imageButton)
            button.setOnClickListener {
                // Finish the current activity and go back to the previous one
                finish()
            }
        }
}
