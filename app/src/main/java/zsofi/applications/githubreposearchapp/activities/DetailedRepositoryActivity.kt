package zsofi.applications.githubreposearchapp.activities

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import kotlinx.coroutines.launch
import zsofi.applications.githubreposearchapp.databinding.ActivityDetailedRepositoryBinding
import zsofi.applications.githubreposearchapp.models.RepositoryModel
import android.graphics.BitmapFactory
import com.squareup.picasso.Picasso
import java.net.URL


class DetailedRepositoryActivity : AppCompatActivity() {

    private var binding: ActivityDetailedRepositoryBinding? = null
    private var httpClient: HttpClient? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var repositoryDetailModel : RepositoryModel? = null
        httpClient = HttpClient(CIO) {
        }

        binding = ActivityDetailedRepositoryBinding.inflate(layoutInflater)
        setContentView(binding?.root)


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

            // Set image from URL using Picasso
            val imageUrl = repositoryDetailModel.ownerAvatar.toString()
            Picasso.get().load(imageUrl).into(binding?.ivAvatar);

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