package zsofi.applications.githubreposearchapp.activities

import android.content.Intent
import android.net.Uri.encode
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import zsofi.applications.githubreposearchapp.adapters.RepositoryAdapter
import zsofi.applications.githubreposearchapp.databinding.ActivityMainBinding
import zsofi.applications.githubreposearchapp.models.RepositoryModel
import zsofi.applications.githubreposearchapp.helpers.Dialogs.Companion.cancelProgressDialog
import zsofi.applications.githubreposearchapp.helpers.Dialogs.Companion.showAlertDialog
import zsofi.applications.githubreposearchapp.helpers.Dialogs.Companion.showProgressDialog
import zsofi.applications.githubreposearchapp.helpers.NetworkCheck.Companion.isNetworkAvailable

class MainActivity : AppCompatActivity() {

    // Variables
    private var binding: ActivityMainBinding? = null
    private var httpClient: HttpClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        // Creating HttpClient for API call
        httpClient = HttpClient(CIO) {
            install(JsonFeature)
        }

        // Setting up the Action bar
        setSupportActionBar(binding?.toolBarMain)
        if (supportActionBar != null) {
            supportActionBar?.title = "Search for GitHub Repositories"
        }

        // Starting to perform GitHub search when there is a click on the Search Button
        binding?.ibSearch?.setOnClickListener {
            // Checking if input text field is empty or not
            if (!binding?.etSearchField?.text.isNullOrEmpty()) {

                // Search repositories only if the network is available
                if (isNetworkAvailable(this)){
                    // Setting search parameters based on the input text field text
                    val repoSearchParameters = binding?.etSearchField!!.text.toString()

                    // Show progressDialog during the coroutine is loading the repositories
                    showProgressDialog(this)

                    // Starting coroutine for API call
                    lifecycleScope.launch {
                        // Starting search for API response
                        val repositories = getRepositories(repoSearchParameters)

                        // If the repository list isn't empty after the API call,
                        // show it's elements in the RecyclerView
                        if (repositories.isNotEmpty()){
                            setupReposRecyclerView(repositories)
                        }
                    }
                }else{
                    // Show a Toast if there is no Internet connection
                    Toast.makeText(this,
                        "The repository search needs Internet connection, " +
                                "please connect to the Internet !",
                        Toast.LENGTH_LONG).show()
                }

            }else{
                // Show a Toast if the input text field is empty
                Toast.makeText(this,
                    "Text field cannot be empty, please enter your search parameters!",
                    Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        // Setting back binding to null when the Activity is destroyed
        if (binding != null) {
            binding = null
        }
        // Setting back httpClient to null when the Activity is destroyed
        if (httpClient != null){
            httpClient = null
        }
    }

    // Setting up RecyclerView to show the Repository List on the MainActivity
    private fun setupReposRecyclerView(repositoryList: ArrayList<RepositoryModel>){
        // Set Repository list visible and "No results yet.." text to Gone
        binding?.rvRepos?.visibility = View.VISIBLE
        binding?.tvBeforeSearch?.visibility = View.GONE

        // Creating Adapter for the Repositories
        val reposAdapter = RepositoryAdapter(repositoryList)
        binding?.rvRepos?.adapter = reposAdapter

        // Start DetailedRepositoryActivity when an item was clicked
        reposAdapter.setOnClickListener(object : RepositoryAdapter.OnClickListener{
            override fun onClick(position: Int, model: RepositoryModel){
                val intent = Intent(this@MainActivity, DetailedRepositoryActivity::class.java)
                intent.putExtra(EXTRA_REPOSITORY_DETAILS, model)
                startActivity(intent)
            }
        })

    }

    // Function to get an ArrayList from the RepositoryList from GitHub based on the search parameters
    private suspend fun getRepositories(query_details: String) : ArrayList<RepositoryModel> {
        val repositoryModelList : ArrayList<RepositoryModel> = ArrayList()

        val encodedText = encode(query_details)

        try {
            val res =
                httpClient?.get<JsonObject>(
                    "https://api.github.com/search/repositories?q=$encodedText") {
                }

            if (res?.get("total_count").toString().toInt() > 0){
                val items = res?.get("items")?.jsonArray ?: error("Unexpected input for items")

                // Taking out the required information from the JsonObject
                for (i in 0 until items.size) {
                    var name = items[i].jsonObject["name"]?.toString() ?: error("Unexpected format in an 'item")
                    var description = items[i].jsonObject["description"]?.toString() ?: error("Unexpected format in an 'item")
                    var gitHubLink = items[i].jsonObject["html_url"]?.toString() ?: error("Unexpected format in an 'item")
                    val stars = items[i].jsonObject["stargazers_count"]?.toString() ?: error("Unexpected format in an 'item")
                    val forks = items[i].jsonObject["forks_count"]?.toString() ?: error("Unexpected format in an 'item")
                    var lastUpdate = items[i].jsonObject["pushed_at"]?.toString() ?: error("Unexpected format in an 'item")
                    var createDate = items[i].jsonObject["created_at"]?.toString() ?: error("Unexpected format in an 'item")
                    var ownerName = items[i].jsonObject["owner"]?.jsonObject?.get("login")?.toString() ?: error("Unexpected format in an 'item")
                    var ownerAvatar = items[i].jsonObject["owner"]?.jsonObject?.get("avatar_url")?.toString() ?: error("Unexpected format in an 'item")
                    var ownerGitHubLink = items[i].jsonObject["owner"]?.jsonObject?.get("html_url")?.toString() ?: error("Unexpected format in an 'item")


                    // Creating substrings, cutting off the "" marks, converting values to the right look
                    name = name.substring(1, name.length - 1)
                    description = description.substring(1, description.length - 1)
                    gitHubLink = gitHubLink.substring(1, gitHubLink.length - 1)
                    ownerName = ownerName.substring(1, ownerName.length - 1)
                    ownerAvatar = ownerAvatar.substring(1, ownerAvatar.length - 1)
                    ownerGitHubLink = ownerGitHubLink.substring(1, ownerGitHubLink.length - 1)
                    // Modifying dates to be in the right format(yyyy.MM.DD.)
                    lastUpdate = lastUpdate.substring(1, 5) + "." + lastUpdate.substring(6, 8)+
                            "." + lastUpdate.substring(9, 11) + "."
                    createDate = createDate.substring(1, 5) + "." + createDate.substring(6, 8)+
                            "." + createDate.substring(9, 11) + "."

                    // Creating a repository instance from Repository Model
                    val repository = RepositoryModel(
                        name, description, gitHubLink, stars, forks, lastUpdate,
                        createDate, ownerName, ownerAvatar, ownerGitHubLink
                    )

                    // Adding the repository to the list of repositories
                    repositoryModelList.add(repository)
                }

            }else{
                runOnUiThread {
                    showAlertDialog(this,
                        "No Result",
                        "No results are matching your search criteria.")
                }
            }
            runOnUiThread {
                cancelProgressDialog()
            }
        }catch (e: Exception){
            e.printStackTrace()
            runOnUiThread{
                cancelProgressDialog()
                showAlertDialog(this,
                    "Could not perform this search",
                    "Sorry! The search couldn't be performed.")
            }
        }
        return repositoryModelList
    }

    companion object{
        // Creating variable to use it to put extra information when
        // starting intent to go from the MainActivity to the DetailedActivity
        var EXTRA_REPOSITORY_DETAILS = "extra_repository_details"
    }


}