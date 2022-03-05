package zsofi.applications.githubreposearchapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import zsofi.applications.githubreposearchapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var binding : ActivityMainBinding? = null
    private val httpClient : HttpClient = HttpClient(CIO){
        install(JsonFeature)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        // Setting up the Action bar
        setSupportActionBar(binding?.toolBarMain)
        if(supportActionBar != null){
            supportActionBar?.title = "Search for GitHub Repositories"
        }


        binding?.ibSearch?.setOnClickListener {
            lifecycleScope.launch() {
                val res = getRepositories()
                println(res)
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()

        // Setting back binding to null when the Activity is destroyed
        if(binding != null){
            binding = null
        }
    }

    private suspend fun getRepositories(): List<String> {
        val res =
            httpClient.get<JsonObject>("https://api.github.com/search/repositories"){
                parameter("q", "topic:aoc-2021-in-kotlin")
            }
        val items = res["items"]?.jsonArray ?: error("Unexpected input for items")
        return items.map {
            it.jsonObject["full_name"]?.toString() ?: error("Unexpected format in an 'item.")
        }
    }


}