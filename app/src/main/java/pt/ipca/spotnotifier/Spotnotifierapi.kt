import android.accounts.NetworkErrorException
import android.net.http.NetworkException
import android.util.Log
import org.json.JSONObject
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import android.os.StrictMode
import android.widget.Toast
import java.io.BufferedWriter
import java.io.OutputStreamWriter

class Spotnotifierapi (ip: String){

    val ip:String = ip
    fun rest_request(url: String, tipo: String, data: String, expectedResponseCode: Int) : String {
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().permitNetwork().build())
        val response = StringBuilder()

        val connection = URL(url).openConnection() as HttpURLConnection
        connection.requestMethod = tipo

        if (data.length > 0) {
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            val writer = BufferedWriter(OutputStreamWriter(connection.outputStream))
            writer.write(data)
            writer.flush()
            writer.close()
        }

        val responseCode = connection.responseCode
        if (responseCode == expectedResponseCode) {
            val inputStream = connection.inputStream
            val reader = BufferedReader(InputStreamReader(inputStream))


            var line: String?
            while (reader.readLine().also { line = it } != null) {
                response.append(line)
            }
            reader.close()

            println("Response data: ${response.toString()}")
        } else {
            println("Failed to fetch data. Response code: $responseCode")
        }
        connection.disconnect()

        return response.toString()
    }

    fun registo_utilizador(primeironome: String,
                           ultimo_nome: String,
                           password: String,
                           email: String,
                           imagem: String?
                           ) {
        var x = rest_request("http://"+ ip + ":8000/api/Utilizadores/", "POST",
            """
                {
                    "email": "$email",
                    "password": "$password",
                    "primeiro_nome": "$primeironome",
                    "ultimo_nome": "$ultimo_nome",
                    "imagem": "$imagem"
                }
            """.trimIndent(),
            HttpURLConnection.HTTP_CREATED
            )

    }

    fun get_utilizadores() : String{
        var r = rest_request("http://"+ ip + ":8000/api/Utilizadores/", "GET",
            "",
            HttpURLConnection.HTTP_OK
        )
        return r
    }

    fun codigo_teste() {
        var x = rest_request("http://" + ip + ":8000/api/Utilizadores/", "GET", "",  HttpURLConnection.HTTP_OK)
        val jsonArray = JSONArray(x)
        for (i in 0 until jsonArray.length()) {
            val item = jsonArray.getJSONObject(i)
            val email_backend = item.getString("name")

        }
    }
    fun recuperacao_password(login: String,
                             nova_pass: String
    ) {
        val x = this.get_utilizadores()
        val jsonArray = JSONArray(x)
        for (i in 0 until jsonArray.length()) {
            val item = jsonArray.getJSONObject(i)
            val email = item.getString("email")
            if (email == login) {
                val id = item.getString("id")
                val password = nova_pass
                val primeironome = item.getString("primero_nome")
                val ultimo_nome = item.getString("ultimo_nome")

                var x = rest_request("http://"+ ip + ":8000/api/Utilizadores/" + id + "/", "PUT",
                    """
                {
                    "email": "$email",
                    "password": "$password",
                    "primeiro_nome": "$primeironome",
                    "ultimo_nome": "$ultimo_nome"
                }
            """.trimIndent(),
                    HttpURLConnection.HTTP_OK
                )

            }

        }
    }

    fun recuperacao_nome(login: String,
                        novo_nome: String) {


        val x = this.get_utilizadores()
        val jsonArray = JSONArray(x)
        for (i in 0 until jsonArray.length()) {
            val item = jsonArray.getJSONObject(i)
            val email = item.getString("email")
            if (email == login) {
                val id = item.getString("id")
                val password = item.getString("password")
                val primeironome = novo_nome
                val ultimo_nome = item.getString("ultimo_nome")

                var x = rest_request("http://"+ ip + ":8000/api/Utilizadores/" + id + "/", "PUT",
                    """
                {
                    "email": "$email",
                    "password": "$password",
                    "primeiro_nome": "$primeironome",
                    "ultimo_nome": "$ultimo_nome"
                }
            """.trimIndent(),
                    HttpURLConnection.HTTP_OK
                )

            }

        }

    }

    fun morada_ler(login: String): List<String> {
        return ArrayList<String>()
    }

    fun morada_atualizar(login: String,
                         morada: List<String>
    ) {

    }

    fun email_ler(login: String) : String {
        return ""
    }

    fun email_atualizar(login: String) {
        val x = this.get_utilizadores()
        val jsonArray = JSONArray(x)
        for (i in 0 until jsonArray.length()) {
            val item = jsonArray.getJSONObject(i)
            val email = item.getString("email")
            if (email == login) {
                val id = item.getString("id")
                val password = item.getString("password")
                val primeironome = item.getString("primero_nome")
                val ultimo_nome = item.getString("ultimo_nome")

                var x = rest_request("http://"+ ip + ":8000/api/Utilizadores/" + id + "/", "PUT",
                    """
                {
                    "email": "$login",
                    "password": "$password",
                    "primeiro_nome": "$primeironome",
                    "ultimo_nome": "$ultimo_nome"
                }
            """.trimIndent(),
                    HttpURLConnection.HTTP_OK
                )

            }

        }
    }
}