package com.healthykid.healthbasicmeasure.repository

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.healthykid.healthbasicmeasure.modelclass.BasicDetails
import com.healthykid.healthbasicmeasure.network.BASE_URL
import com.healthykid.healthbasicmeasure.network.NetworkCalls
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DetailsActivityRepository(val application: Application){
    val showProgress=MutableLiveData<Boolean>()
    val isValidUhID=MutableLiveData<Boolean>()
    val studentDetails=MutableLiveData<BasicDetails>()

    fun getStudentDetails(id:String){
        showProgress.value=true

        val retrofit=Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()

        val service=retrofit.create(NetworkCalls::class.java)

        service.getStudentBasicInfo(id).enqueue(object :Callback<BasicDetails>{
            override fun onFailure(call: Call<BasicDetails>, t: Throwable) {
                showProgress.value=false
                t.message?.let { Log.e("Error", it) }
                Toast.makeText(application,"Problem  occured while getting data.Please Try Again"+ t.message, Toast.LENGTH_SHORT).show()

            }

            override fun onResponse(call: Call<BasicDetails>, response: Response<BasicDetails>) {
                showProgress.value=false
                if (response.body()==null){
                    isValidUhID.value=false
                  //  Toast.makeText(application,"Null Body",Toast.LENGTH_SHORT).show()
                }else{
                    isValidUhID.value=true
                    studentDetails.value=response.body()
                }

            }

        })
    }

}