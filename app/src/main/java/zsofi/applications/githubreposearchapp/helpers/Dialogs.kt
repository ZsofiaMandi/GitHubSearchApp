package zsofi.applications.githubreposearchapp.helpers

import android.app.Dialog
import android.content.Context
import androidx.appcompat.app.AlertDialog
import zsofi.applications.githubreposearchapp.R

class Dialogs {
    companion object{
        var customProgressDialog: Dialog? = null

        // Function to show Progress dialog
        fun showProgressDialog(context: Context){
            customProgressDialog = Dialog(context)
            customProgressDialog?.setContentView(R.layout.dialog_custom_progress)
            customProgressDialog?.setCanceledOnTouchOutside(false)
            customProgressDialog?.show()
        }
        // Function to dismiss Progress dialog
        fun cancelProgressDialog(){
            if(customProgressDialog != null){
                customProgressDialog?.dismiss()
                customProgressDialog = null
            }
        }

        // Show Alert Dialog with custom title and message
        fun showAlertDialog(context: Context, title: String, message: String){
            val builder = AlertDialog.Builder(context, R.style.AlertDialogTheme)
            builder.setTitle(title)
            builder.setMessage(message)
            builder.setIcon(android.R.drawable.ic_dialog_alert)
            builder.setPositiveButton("OK"){dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            builder.show()
        }
    }
}