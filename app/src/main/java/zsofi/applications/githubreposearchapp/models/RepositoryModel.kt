package zsofi.applications.githubreposearchapp.models

import android.os.Parcel
import android.os.Parcelable

data class RepositoryModel(
    val name: String?,
    val description: String?,
    val gitHubLink: String?,
    val stars: String?,
    val forks: String?,
    val lastUpdate: String?,
    val createDate: String?,
    val ownerName: String?,
    val ownerAvatar: String?,
    val ownerGitHubLink: String?
): Parcelable // To allow the model instance to pass with an intent
{
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeString(gitHubLink)
        parcel.writeString(stars)
        parcel.writeString(forks)
        parcel.writeString(lastUpdate)
        parcel.writeString(createDate)
        parcel.writeString(ownerName)
        parcel.writeString(ownerAvatar)
        parcel.writeString(ownerGitHubLink)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RepositoryModel> {
        override fun createFromParcel(parcel: Parcel): RepositoryModel {
            return RepositoryModel(parcel)
        }

        override fun newArray(size: Int): Array<RepositoryModel?> {
            return arrayOfNulls(size)
        }
    }
}