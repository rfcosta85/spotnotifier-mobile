package pt.ipca.spotnotifier

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class UpdateProfile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_update_profile)

        val button = findViewById<ImageButton>(R.id.imageButtonBack)
        button.setOnClickListener {
            // Finish the current activity and go back to the previous one
            finish()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


    }

    fun onUpdateAdress(view: View) {
        val intent = Intent(this, UpdateAdress::class.java)
        startActivity(intent)
    }

    fun onUpdateEmail(view: View) {
        val intent = Intent(this, UpdateEmail::class.java)
        startActivity(intent)
    }

}