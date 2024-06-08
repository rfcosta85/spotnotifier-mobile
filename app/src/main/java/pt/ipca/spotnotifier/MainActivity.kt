package pt.ipca.spotnotifier

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
//    private lateinit var  auth: FirebaseAuth
//    private lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
//        auth = Firebase.auth
//        db = Firebase.firestore
//
//        auth.currentUser?.let {
//            readUserFromDB(it.uid)
//        }

    }
//    fun insertUserIntoDB(uid: String, name: String, email: String) {
//        val user = hashMapOf(
//            "uid" to uid,
//            "name" to name,
//            "email" to email
//        )
//        db.collection("users").document("$uid")
//            .set(user)
//            .addOnCompleteListener{ documentReference ->
//                Log.d("MEI", "DocumentSnapshot added with ID: ${documentReference}")
//            }
//            .addOnFailureListener { e ->
//                Log.w("MEI", "Error adding document", e)
//            }
//
//    }
//    private fun readUserFromDB(uid: String) {
//        val docRef = db.collection("users").document("$uid")
//        docRef.addSnapshotListener{ snapshot, e ->
//            if (e != null) {
//                Log.w("MEI", "Listen failed", e)
//                return@addSnapshotListener
//            }
//
//            if (snapshot != null && snapshot.exists()) {
//                val name = snapshot.getString("name")
////                val tv = findViewById<TextView>(R.id.main_tv_username)
////                tv.text = name
//                Log.d("MEI", "Current data: ${snapshot.data}")
//            } else {
//                Log.d("MEI", "Current data: null")
//            }
//        }
//    }

//    fun register(v: View) {
//        val email = findViewById<EditText>(R.id.main_et_email).text.toString()
//        val password = findViewById<EditText>(R.id.main_et_password).text.toString()
//        val name = findViewById<EditText>(R.id.main_et_name).text.toString()
//        createUserInFirebase(name, email, password)
//    }
//
//    private fun createUserInFirebase(name: String, email: String, password: String) {
//        auth.createUserWithEmailAndPassword(email, password)
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    Log.d("ME", "createUserWithEmail:success")
//                    val user = auth.currentUser
//                    Toast.makeText(
//                        baseContext,
//                        "Authentication Success",
//                        Toast.LENGTH_SHORT,
//                    ).show()
//                    if(user != null) {
//                        insertUserIntoDB(user.uid, name, email)
//                    }
//                } else {
//                    Log.w("MEI", "createUserWithEmail:failure", task.exception)
//                    Toast.makeText(
//                        baseContext,
//                        "Authentication failed",
//                        Toast.LENGTH_SHORT,
//                    ).show()
//                }
//            }
//    }
//
//    fun login(v: View) {
//        val email = findViewById<EditText>(R.id.main_et_email).text.toString()
//        val password = findViewById<EditText>(R.id.main_et_password).text.toString()
//        authUserInFireBase(email, password)
//    }
//
//    private fun authUserInFireBase(email: String, password: String) {
//        auth.signInWithEmailAndPassword(email, password)
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    Log.d("MEI", "authUserWithEmail:success")
//                    val user = auth.currentUser
//                        Toast.makeText(
//                            baseContext,
//                            "Authentication Success",
//                            Toast.LENGTH_SHORT,
//                        ).show()
//                } else {
//                    Log.w("MEI", "authUserWithEmail:failure", task.exception)
//                    Toast.makeText(
//                        baseContext,
//                        "Authentication failed",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//    }
}