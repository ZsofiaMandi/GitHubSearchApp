package zsofi.applications.githubreposearchapp.activities

import android.app.Dialog
import android.content.Intent
import android.net.Uri.encode
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import zsofi.applications.githubreposearchapp.R
import zsofi.applications.githubreposearchapp.adapters.RepositoryAdapter
import zsofi.applications.githubreposearchapp.databinding.ActivityMainBinding
import zsofi.applications.githubreposearchapp.models.RepositoryModel

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null
    private var httpClient: HttpClient? = null
    private var customProgressDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        httpClient = HttpClient(CIO) {
            install(JsonFeature)
        }

        // Setting up the Action bar
        setSupportActionBar(binding?.toolBarMain)
        if (supportActionBar != null) {
            supportActionBar?.title = "Search for GitHub Repositories"
        }

        // Starting search for API response and set RV after getting the repos
        binding?.ibSearch?.setOnClickListener {
            if (!binding?.etSearchField?.text.isNullOrEmpty()) {
                val repoSearchParameters = binding?.etSearchField!!.text.toString()
                showProgressDialog()
                lifecycleScope.launch() {
                    val repositories = getRepositories(repoSearchParameters)
                    if (repositories.isNotEmpty()){
                        setupReposRecyclerView(repositories)
                    }
                }
            }else{
                Toast.makeText(this,
                    "Text field cannot be empty, please enter your search parameters!",
                    Toast.LENGTH_SHORT).show()
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

    private suspend fun getRepositories(query_details: String) : ArrayList<RepositoryModel> {
        val repositoryModelList : ArrayList<RepositoryModel> = ArrayList()

        val encodedText = encode(query_details)

        try {
            val res =
                httpClient?.get<JsonObject>(
                    "https://api.github.com/search/repositories?q=$encodedText") {
                }
            println("-----------RESULT------------ $res")

            if (res?.get("total_count").toString().toInt() > 0){
                val items = res?.get("items")?.jsonArray ?: error("Unexpected input for items")

                // Taking out the required information from the JsonObject
                for (i in 0 until items.size) {
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
                    var ownerAvatar =
                        items[i].jsonObject["owner"]?.jsonObject?.get("avatar_url")?.toString()
                            ?: error("Unexpected format in an 'item")
                    var ownerGitHubLink =
                        items[i].jsonObject["owner"]?.jsonObject?.get("html_url")?.toString()
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


                    // Creating a repository instance from Repository Model
                    val repository = RepositoryModel(
                        id, name, description, gitHubLink, stars, forks, lastUpdate,
                        createDate, ownerName, ownerAvatar, ownerGitHubLink
                    )

                    // Adding the repository to the list of repositories
                    repositoryModelList.add(repository)
                }

            }else{
                showAlertDialog("No Result", "No results are matching your search criteria.")
            }

            runOnUiThread {
                cancelProgressDialog()
            }

        }catch (e: Exception){
            e.printStackTrace()
            cancelProgressDialog()
            showAlertDialog("Failed", "Sorry! Something went wrong.")

        }
        return repositoryModelList
    }

    // Show progress dialog while loading repositories
    private fun showProgressDialog(){
        customProgressDialog = Dialog(this@MainActivity)
        customProgressDialog?.setContentView(R.layout.dialog_custom_progress)
        customProgressDialog?.setCanceledOnTouchOutside(false)
        customProgressDialog?.show()
    }

    // Cancel the progress dialog
    private fun cancelProgressDialog(){
        if(customProgressDialog != null){
            customProgressDialog?.dismiss()
            customProgressDialog = null
        }
    }

    private fun showAlertDialog(title: String, message: String){
        val builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton("OK"){dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        builder.show()
    }

    companion object{
        var EXTRA_REPOSITORY_DETAILS = "extra_repository_details"
    }
}