package zsofi.applications.githubreposearchapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.LinkMovementMethod
import zsofi.applications.githubreposearchapp.databinding.ActivityDetailedRepositoryBinding
import zsofi.applications.githubreposearchapp.models.RepositoryModel
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_COMPACT
import androidx.core.text.HtmlCompat.fromHtml
import com.squareup.picasso.Picasso


class DetailedRepositoryActivity : AppCompatActivity() {

    private var binding: ActivityDetailedRepositoryBinding? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var repositoryDetailModel : RepositoryModel? = null

        binding = ActivityDetailedRepositoryBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        // Getting the information of the clicked repository
        if (intent.hasExtra(MainActivity.EXTRA_REPOSITORY_DETAILS)){
            repositoryDetailModel = intent.getParcelableExtra(
                MainActivity.EXTRA_REPOSITORY_DETAILS) as RepositoryModel?
        }

        if(repositoryDetailModel != null){
            // Setting up the Action bar
            setSupportActionBar(binding?.toolBarDetailedRepository)
            if (supportActionBar != null) {
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                supportActionBar?.title = "Repository details"
                binding?.toolBarDetailedRepository?.setNavigationOnClickListener {
                    onBackPressed()
                }
            }

            // Binding the Repository values
            binding?.tvOwnerName?.text = repositoryDetailModel.ownerName
            binding?.tvRepoName?.text = repositoryDetailModel.name
            binding?.tvRepoDescription?.text = repositoryDetailModel.description
            binding?.tvCreatingDate?.text = repositoryDetailModel.createDate
            binding?.tvLastUpdate?.text = repositoryDetailModel.lastUpdate
            binding?.tvStars?.text = repositoryDetailModel.stars
            binding?.tvForks?.text = repositoryDetailModel.forks

            // Setting image from URL using Picasso
            val imageUrl = repositoryDetailModel.ownerAvatar.toString()
            Picasso.get().load(imageUrl).into(binding?.ivAvatar)

            // Setting Hyperlinks for GitHub links
            // Link to GitHub Profile
            val profileLinkText = "<a href='${repositoryDetailModel.ownerGitHubLink}'>Go to GitHub Profile</a>"
            binding?.tvOwnerGitHubLink?.isClickable = true
            binding?.tvOwnerGitHubLink?.movementMethod = LinkMovementMethod.getInstance()
            binding?.tvOwnerGitHubLink?.text = fromHtml(profileLinkText, FROM_HTML_MODE_COMPACT)
            // Link to GitHub Repository
            val repoLinkText = "<a href='${repositoryDetailModel.gitHubLink}'>Go to GitHub Repository</a>"
            binding?.tvRepoGitHubLink?.isClickable = true
            binding?.tvRepoGitHubLink?.movementMethod = LinkMovementMethod.getInstance()
            binding?.tvRepoGitHubLink?.text = fromHtml(repoLinkText, FROM_HTML_MODE_COMPACT)

        }

    }

    override fun onDestroy() {
        super.onDestroy()

        // Setting back binding to null when the Activity is destroyed
        if (binding != null) {
            binding = null
        }
    }




}