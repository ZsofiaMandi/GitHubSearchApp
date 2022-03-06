package zsofi.applications.githubreposearchapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import zsofi.applications.githubreposearchapp.adapters.RepositoryAdapter
import zsofi.applications.githubreposearchapp.databinding.ActivityMainBinding
import zsofi.applications.githubreposearchapp.models.RepositoryModel

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null
    private val httpClient: HttpClient = HttpClient(CIO) {
        install(JsonFeature)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        // Setting up the Action bar
        setSupportActionBar(binding?.toolBarMain)
        if (supportActionBar != null) {
            supportActionBar?.title = "Search for GitHub Repositories"
        }

        // Starting search for API response and set RV after getting the repos
        binding?.ibSearch?.setOnClickListener {
            lifecycleScope.launch() {
                val repositories = getRepositories()
                setupReposRecyclerView(repositories)
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()

        // Setting back binding to null when the Activity is destroyed
        if (binding != null) {
            binding = null
        }
    }

    private fun setupReposRecyclerView(repositoryList: ArrayList<RepositoryModel>){
        binding?.rvRepos?.visibility = View.VISIBLE
        binding?.tvBeforeSearch?.visibility = View.GONE

        val reposAdapter = RepositoryAdapter(repositoryList)
        binding?.rvRepos?.adapter = reposAdapter
    }

    private suspend fun getRepositories() : ArrayList<RepositoryModel> {
        val repositoryModelList : ArrayList<RepositoryModel> = ArrayList()
        val res =
            httpClient.get<JsonObject>("https://api.github.com/search/repositories") {
                parameter("q", "tetris+language:assembly&sort=stars&order=desc")
            }
        val items = res["items"]?.jsonArray ?: error("Unexpected input for items")
        val firstItem = items[0]
        val firstItemName = firstItem.jsonObject["name"]
        println("FIRST ITEM: $firstItem")
        println("FIRST NAME: $firstItemName")


        for(i in 0 until items.size){
            val id = i
            var name = items[i].jsonObject["name"]?.toString()
                ?: error("Unexpected format in an 'item")
            var description = items[i].jsonObject["description"]?.toString()
                ?: error("Unexpected format in an 'item")
            var gitHubLink = items[i].jsonObject["html_url"]?.toString()
                ?: error("Unexpected format in an 'item")
            val stars = items[i].jsonObject["stargazers_count"]?.toString()
                ?: error("Unexpected format in an 'item")
            val forks = items[i].jsonObject["forks_count"]?.toString()
                ?: error("Unexpected format in an 'item")
            var lastUpdate = items[i].jsonObject["pushed_at"]?.toString()
                ?: error("Unexpected format in an 'item")
            var createDate = items[i].jsonObject["created_at"]?.toString()
                ?: error("Unexpected format in an 'item")
            var ownerName = items[i].jsonObject["owner"]?.jsonObject?.get("login")?.toString()
                ?: error("Unexpected format in an 'item")
            var ownerAvatar = items[i].jsonObject["owner"]?.jsonObject?.get("avatar_url")?.toString()
                ?: error("Unexpected format in an 'item")
            var ownerGitHubLink = items[i].jsonObject["owner"]?.jsonObject?.get("html_url")?.toString()
                ?: error("Unexpected format in an 'item")


            // Creating substrings, cutting off the "" marks, converting values to the right look
            name = name.substring(1, name.length - 1)
            description = description.substring(1, description.length - 1)
            gitHubLink = gitHubLink.substring(1, gitHubLink.length - 1)
            lastUpdate = lastUpdate.substring(1, 11)
            createDate = createDate.substring(1, 11)
            ownerName = ownerName.substring(1, ownerName.length - 1)
            ownerAvatar = ownerAvatar.substring(1, ownerAvatar.length - 1)
            ownerGitHubLink = ownerGitHubLink.substring(1, ownerGitHubLink.length - 1)


            val repository = RepositoryModel(
                id, name, description, gitHubLink, stars, forks, lastUpdate,
                createDate, ownerName, ownerAvatar, ownerGitHubLink
            )

            repositoryModelList.add(repository)

        }

        return repositoryModelList

    }
}