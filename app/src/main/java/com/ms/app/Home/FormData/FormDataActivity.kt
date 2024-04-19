package com.ms.app.Home.FormData

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.ms.app.Home.FormData.fragment.HomeFragment
import com.ms.app.Home.FormData.fragment.IndexFragment
import com.ms.app.Home.FormData.fragment.QCRaisedFragment
import com.ms.app.Home.FormData.viewModel.SectionViewModel
import com.ms.app.JSONModel.JSONFormDataModel
import com.ms.app.MasterDB.FormSubmissionData
import com.ms.app.MasterDB.MasterDBHelper
import com.ms.app.MasterDB.MasterDBHelper.Companion.DRAFT
import com.ms.app.MasterDB.MasterDBHelper.Companion.QC
import com.ms.app.MasterDB.MasterDBHelper.Companion.SUMMERY
import com.ms.app.R
import com.ms.app.api.Status
import com.ms.app.api.respose.QCData
import com.ms.app.getCurrentDateTime
import com.ms.app.getPrefStringData
import com.ms.app.hideLoader
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson

private val TAG = "FormDataActivity"

class FormDataActivity : AppCompatActivity() {

    private lateinit var btnAdd: Button
    private lateinit var rvFormData: RecyclerView
    private lateinit var dbHelper: MasterDBHelper
    private lateinit var txtTitleName: TextView

    private lateinit var sectionViewModel: SectionViewModel
    //private lateinit var imgFilter: ImageView
    private lateinit var ivBack: ImageView
    private var selectedValue: String? = null
    lateinit var bottomNavigationView: BottomNavigationView
    companion object {
        var formId = 0
        var summeryTableName = ""
        var qcTableName = ""
        var qcRaisedList=ArrayList<QCData>()
        lateinit var imgFilter: ImageView

    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_data)
        dbHelper = MasterDBHelper(this)
        sectionViewModel = ViewModelProvider(this).get(SectionViewModel::class.java)
        selectedValue = getString(R.string.all)

        val formJsonData = intent.getStringExtra("formJsonData")
        formId = intent.getIntExtra("formId", 0).toInt()

        txtTitleName = findViewById(R.id.txtTitleName_Home)
        imgFilter = findViewById(R.id.imgFilter)
        ivBack = findViewById(R.id.ivBack)

        ivBack.setOnClickListener {
            finish()
        }

        val jsonData = Gson().fromJson(formJsonData, JSONFormDataModel::class.java)
        summeryTableName = jsonData.acronym + SUMMERY
        qcTableName = jsonData.acronym + QC
        dbHelper.createTableQCQuery(qcTableName)

        val userName = getPrefStringData(this, "userName")
        txtTitleName.text = userName

        //imgFilter.visibility = View.GONE


        replaceFragment(IndexFragment())

         bottomNavigationView = findViewById(R.id.bottomNavigationView)

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                /*R.id.home -> {
                    imgFilter.visibility = View.GONE
                    replaceFragment(HomeFragment())
                    true
                }*/
                R.id.index -> {
                    //imgFilter.visibility = View.VISIBLE
                    replaceFragment(IndexFragment())
                    true
                }
                R.id.QCRaised -> {
                    imgFilter.visibility = View.GONE
                    replaceFragment(QCRaisedFragment())
                    true
                }
                R.id.newForm -> {

                    val currentDateTime = getCurrentDateTime()
                    val insertedId = dbHelper.insertFormSubmissionData(
                        FormSubmissionData(
                            formId = formId.toInt(),
                            status = "Draft",
                            responseJson = formJsonData,
                            createdBy = null,
                            createdAt = currentDateTime,
                            updateAt = null,
                            isDeleted = null,
                            deletedAt = null
                        )
                    )

                    val values = ContentValues()
                    values.put(MasterDBHelper.MASTER_FORM_ID, formId)
                    values.put(MasterDBHelper.FORM_SUB_ID, insertedId)
                    values.put(MasterDBHelper.STATUS, DRAFT)

                    val summeryId = dbHelper.insertData(summeryTableName, values)

                    val intentAdd = Intent(this, AddFormDataActivity::class.java)
                    intentAdd.putExtra("formJsonData", formJsonData)
                    intentAdd.putExtra("formId", formId)
                    intentAdd.putExtra("subId", insertedId.toInt())
                    intentAdd.putExtra("summeryId", summeryId.toInt())
                    startActivity(intentAdd)
                    false
                }

                else -> {
                    // imgFilter.visibility = View.GONE
                    replaceFragment(HomeFragment())
                    true
                }
            }
        }

        /*btnAdd = findViewById(R.id.btnAdd)
        rvFormData = findViewById(R.id.rvFormData)

        dbHelper = MasterDBHelper(this)

        btnAdd.setOnClickListener {
            val currentDateTime = getCurrentDateTime()
            var insertedId=dbHelper.insertFormSubmissionData(
                FormSubmissionData(
                    formId = formJsonId.toInt(),
                    status = "Draft",
                    responseJson = formJsonData,
                    createdBy = null,
                    createdAt = currentDateTime,
                    updateAt = null,
                    isDeleted = null,
                    deletedAt = null
                )
            )

            val intentAdd = Intent(this, AddFormDataActivity::class.java)
            intentAdd.putExtra("formJsonData", formJsonData)
            intentAdd.putExtra("formJsonId", formJsonId.toLong())
            intentAdd.putExtra("subId", insertedId.toInt())
            startActivity(intentAdd)
        }*/


    }


    override fun onResume() {
        super.onResume()
        imgFilter = findViewById(R.id.imgFilter)
        qcRaisedList.clear()
        var allRecordCursor= dbHelper.getAllRecords(qcTableName)
        while (allRecordCursor.moveToNext()) {
            val qcId = allRecordCursor.getString(allRecordCursor.getColumnIndex(MasterDBHelper.ID))
            val id = allRecordCursor.getString(allRecordCursor.getColumnIndex(MasterDBHelper.QC_ID))
            val formId = allRecordCursor.getString(allRecordCursor.getColumnIndex(MasterDBHelper.FORM_ID))
            val status = allRecordCursor.getString(allRecordCursor.getColumnIndex(MasterDBHelper.STATUS))
            val primaryView = allRecordCursor.getString(allRecordCursor.getColumnIndex(MasterDBHelper.PRIMARY_VIEW))
            val qcSchema = allRecordCursor.getString(allRecordCursor.getColumnIndex(MasterDBHelper.QC_SCHEMA))

            val map = primaryView
                .drop(1)
                .dropLast(1)
                .split(", ")
                .map {
                    val (key, value) = it.split("=")
                    key to value
                }
                .toMap()
                .toMutableMap()

            val QCData = QCData(
                id,
                qcId,
                formId,
                status,
                primary_view_map=map
            )
            qcRaisedList.add(QCData)
        }
        val menu = bottomNavigationView.menu
        val menuItem1 = menu.findItem(R.id.QCRaised)
        if (qcRaisedList.size!! >0){
            menuItem1.title = "QC Raised-"+qcRaisedList.size
//                            qcRaisedList=data?.data!!
        }else{
            menuItem1.title = "QC Raised"
        }

        getQCRaisedList(formId)
        /*val getSubmissionTableList = getSubmissionTableByFormId(formJsonId)

        getSubmissionTableList.reverse()

        rvFormData.layoutManager = LinearLayoutManager(this)
        val adapter = FormDataAdapter(this, getSubmissionTableList) { item ->
            val intentAdd = Intent(this, AddFormDataActivity::class.java)
            intentAdd.putExtra("formJsonData", item.responseJson)
            intentAdd.putExtra("formJsonId", formJsonId.toLong())
            intentAdd.putExtra("subId", item.id.toInt())
            startActivity(intentAdd)
        }
        rvFormData.adapter = adapter
    */
    }

    @SuppressLint("Range")
    private fun getQCRaisedList(formId: Int) {
        val token = getPrefStringData(this, "token")
        sectionViewModel.getQCRaisedList(
            token.toString(),
            formId,
        ).observe(this) { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    val data = resource.data?.body()
                    if (data?.code == 1) {

                      /*  Toast.makeText(this, data?.title, Toast.LENGTH_LONG)
                            .show()
                        Log.d(TAG, "SYNC BODY: " + data)*/
                        // Update the text for each menu item

                        data.data.data.forEachIndexed { index, qcData ->
                            var data : QCData ?=null
                            data=qcRaisedList.find { it.id==qcData.id }
                           if (data==null){
                               val QCData = QCData(
                                   qcData.id,
                                   "0",
                                   formId.toString(),
                                   MasterDBHelper.QCRAISED,
                                   primary_view_map=qcData.primary_view_map
                               )
                               qcRaisedList.add(QCData)
                           }
                        }
                        val menu = bottomNavigationView.menu
                        val menuItem1 = menu.findItem(R.id.QCRaised)
                        if (qcRaisedList.size >0){
                            menuItem1.title = "QC Raised-"+qcRaisedList.size
//                            qcRaisedList=data?.data!!
                        }else{
                            menuItem1.title = "QC Raised"
                        }

                        /*data.data.forEachIndexed { index, qcData ->
                            var cursor=dbHelper.getAllRecordsWithCondition(qcTableName,"${MasterDBHelper.QC_ID}=${qcData.id}")
                            if (cursor.count == 0) {
                                val values = ContentValues()
                                values.put(MasterDBHelper.QC_ID, qcData.id)
                                values.put(MasterDBHelper.FORM_ID, formId)
                                values.put(MasterDBHelper.STATUS, MasterDBHelper.QCRAISED)
                                values.put(MasterDBHelper.PRIMARY_VIEW, qcData.primary_view_map.toString())
                                dbHelper.insertData(qcTableName,values)
                            }
                        }*/
                    }
                }

                Status.ERROR -> {
                    Toast.makeText(this, resource.message, Toast.LENGTH_LONG).show()
                    Log.d("TAG", "Error: ${resource.message}")
                }

                Status.LOADING -> {
                    hideLoader()
                }
            }
        }
    }

    private fun getSubmissionTableByFormId(formId: Int): ArrayList<FormSubmissionData> {
        val formData = dbHelper.getSubmissionTableByFormId(formId)
        val formListDB = ArrayList<FormSubmissionData>()

        formListDB.addAll(formData)
        return formListDB
    }

    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .commit()
    }
}