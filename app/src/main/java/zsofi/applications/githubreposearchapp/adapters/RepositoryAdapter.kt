package zsofi.applications.githubreposearchapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import zsofi.applications.githubreposearchapp.R
import zsofi.applications.githubreposearchapp.databinding.RepoItemBinding
import zsofi.applications.githubreposearchapp.models.RepositoryModel

class RepositoryAdapter(private val repositoryList: ArrayList<RepositoryModel>)
    : RecyclerView.Adapter<RepositoryAdapter.MainViewHolder>() {

    private var onClickListener: OnClickListener? = null

    inner class MainViewHolder(private val itemBinding: RepoItemBinding) :
    RecyclerView.ViewHolder(itemBinding.root){
        // Function to bind all of the values from one repo to it's TextViews
        fun bindItem(repo: RepositoryModel){
            itemBinding.tvName.text = repo.name
            itemBinding.tvDescription.text = repo.description
            itemBinding.tvStars.text = repo.stars
            itemBinding.tvLastUpdate.text = repo.lastUpdate
        }

        // Binding the OnClickListener to the itemView
        fun bindOnClickListener(position: Int, model: RepositoryModel){
            itemBinding.itemView.setOnClickListener {
                if (onClickListener != null){
                    onClickListener!!.onClick(position, model)
                }
            }
        }
    }

    // Returning MainViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(RepoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    // Binding all of the item values and the OnClickListener
    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val context = holder.itemView.context
        val repo = repositoryList[position]
        holder.bindItem(repo)
        holder.bindOnClickListener(position, repo)


        // Set every second item background to blue
        if(position % 2 == 0){
            holder.itemView.setBackgroundColor(
                ContextCompat.getColor(context,
                R.color.blue_1))
        }else{
            holder.itemView.setBackgroundColor(
                ContextCompat.getColor(context,
                R.color.white))
        }
    }

    // Counting and returning the size of the Repo list
    override fun getItemCount(): Int {
        return repositoryList.size
    }

    // Creating OnClickListener function to call when there is a click on one of the items
    // It will be overwritten in the MainActivity
    interface OnClickListener{
        fun onClick(position: Int, model: RepositoryModel)
    }
}