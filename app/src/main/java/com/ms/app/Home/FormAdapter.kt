package com.ms.app.Home

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ms.app.Home.FormData.FormDataActivity
import com.ms.app.MasterDB.FormDataID
import com.ms.app.MasterDB.FormJsonDataModel
import com.ms.app.MasterDB.FormListModel
import com.ms.app.R

class FormAdapter(
    private var context: Context,
    private var formList: ArrayList<FormListModel>,
    private var formJsonData: ArrayList<FormJsonDataModel>,
    private var formJsonId: ArrayList<FormDataID>
) : RecyclerView.Adapter<FormAdapter.FormViewHolder>() {

    class FormViewHolder(view: View) : ViewHolder(view) {
        var formTv: TextView = view.findViewById(R.id.formTv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FormViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.form_list, parent, false)
        return FormViewHolder(view)
    }

    override fun getItemCount(): Int {
        return formList.size
    }

    override fun onBindViewHolder(holder: FormViewHolder, position: Int) {
        val currentForm = formList[position].formName
        holder.formTv.text = currentForm


        holder.formTv.setOnClickListener {

            try {
                //val jsonData: JSONFormDataModel = Gson().fromJson(formJsonData[position].formSchema, JSONFormDataModel::class.java)

                val intent = Intent(context, FormDataActivity::class.java)
                intent.putExtra("formJsonData", formJsonData[position].formSchema)
                intent.putExtra("formId", formJsonId[position].id)
                /*intent.putExtra("subId",subId[position].id)*/
                context.startActivity(intent)

            } catch (e: Exception) {
                Log.e("TAG", "JsonSyntaxException: ${e.message}")
            }
        }

    }
    /*private fun getSubmissionTableData(): ArrayList<FormSubmissionData> {
        val formData = dbHelper.getSubmissionTable()

        val formListDB = ArrayList<FormSubmissionData>()

        formListDB.addAll(formData)

        return formListDB
    }*/
}