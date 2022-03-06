package zsofi.applications.githubreposearchapp.models

data class RepositoryModel(
    val id: Int,
    val name: String,
    val description: String,
    val gitHubLink: String,
    val stars: String,
    val forks: String,
    val lastUpdate: String,
    val createDate: String,
    val ownerName: String,
    val ownerAvatar: String,
    val ownerGitHubLink: String
)