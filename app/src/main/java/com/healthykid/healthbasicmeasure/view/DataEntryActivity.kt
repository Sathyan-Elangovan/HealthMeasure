package com.healthykid.healthbasicmeasure.view

import android.content.Context
import android.content.Intent
import android.icu.util.Measure
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.*
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.healthykid.healthbasicmeasure.R
import com.healthykid.healthbasicmeasure.modelclass.Measurement
import com.healthykid.healthbasicmeasure.viewmodel.DataEntryActivityViewModel
import kotlinx.android.synthetic.main.activity_data_entry.*
import kotlinx.android.synthetic.main.activity_fetch.*
import org.w3c.dom.Text
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DataEntryActivity : AppCompatActivity() {
    private var studentId:String=""
    private var studentName:String=""
    private var sWeight:String=""
    private var sHeight:String=""
    private var sSystol:String=""
    private var sDiastol:String=""
    private var sTemp:String=""
    private var sPulse:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_entry)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        val viewmdel=ViewModelProvider(this).get(DataEntryActivityViewModel::class.java)

        studentId=intent.getStringExtra("id").toString()
        studentName=intent.getStringExtra("name").toString()

        viewmdel.updateSuccess.observe(this, Observer {
            if (it==true){
                dataentry_progressbar.visibility= INVISIBLE
                val i =Intent(this,ResultActivity::class.java)
                i.putExtra("name",studentName)
                i.putExtra("id",studentId)
                startActivity(i)
            }else{
                dataentry_progressbar.visibility= INVISIBLE
                Toast.makeText(this,"Problem while uploading data.Please Try Again!",Toast.LENGTH_SHORT).show()
            }
        })


        upload_details_btn.setOnClickListener {
            hideKeyboard()
             if (checkInternet()){
                    val isValid=validation()
                    if (isValid){
                        dataentry_progressbar.visibility= VISIBLE
                        val sBloodPressurevalue= "$sSystol/$sDiastol mmHg"
                        val sHeightValue="$sHeight cm"
                        val sWeightValue="$sWeight kg"
                        val sTempValue="$sTemp F"
                        val sPulseValue="$sPulse per Min"

                        val measure=Measurement(sHeightValue,sWeightValue,sBloodPressurevalue,sTempValue,sPulseValue)
                        viewmdel.updateDetails(measure, studentId)
                    }
                }else{
                dataentryLayout.visibility=GONE
                dataentry_internet_layout.visibility= VISIBLE
                refresh()
                Toast.makeText(this,"Turn on Your Network Connection",Toast.LENGTH_SHORT).show()
            }

        }
        dataentry_refresh_btn.setOnClickListener {
            refresh()
        }

        height_entry_et.addTextChangedListener(object:TextWatcher{
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
               heightError_tv.visibility= GONE

            }

        })
        weight_entry_et.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    weightError_tv.visibility= GONE
            }
        })
        cistol_et.addTextChangedListener(object:TextWatcher{
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                pbError_tv.visibility= GONE
            }
        })
        diastol_et.addTextChangedListener(object:TextWatcher{
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                pbError_tv.visibility= GONE
            }
        })
        temperature_entry_et.addTextChangedListener(object:TextWatcher{
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                tempError_tv.visibility= GONE
            }
        })
        pulse_entry_et.addTextChangedListener(object:TextWatcher{
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                pulseError_tv.visibility= GONE
            }

        })
    }

    fun validation():Boolean{
        //Weight validation
        var isWeightValid=false
        var isHeightValid=false
        var isTempValid=false
        var isPulseValid=false
        var isSystolValid=false
        var isDiastolValid=false
        val invalid="Invalid Input"
        val empty="Enter Value"
      //  val isWeightValid=false
        if (weight_entry_et.text.toString().isNotEmpty()){
            if (weight_entry_et.text.toString()=="-"){
                weightError_tv.visibility= INVISIBLE
                sWeight="-"
                isWeightValid=true
            }else if (weight_entry_et.text.toString().toFloat() in (5.0..200.0)){
                weightError_tv.visibility= INVISIBLE
                sWeight=weight_entry_et.text.toString()
                isWeightValid=true
            }
            else{
                weightError_tv.text=invalid
                weightError_tv.visibility= VISIBLE
                weight_entry_et.error
                isWeightValid=false
            }

        }
        else{
            weightError_tv.text=empty
            weightError_tv.visibility= VISIBLE
            isWeightValid=false
            weight_entry_et.error
        }
        //Height Validation
        if (height_entry_et.text.toString().isNotEmpty() ){
            if (height_entry_et.text.toString()=="-"){
                heightError_tv.visibility= INVISIBLE
                isHeightValid=true
                sHeight="-"
            }else if(height_entry_et.text.toString().toFloat() in (10.0..250.0)){
                heightError_tv.visibility= INVISIBLE
                isHeightValid=true
                sHeight=height_entry_et.text.toString()
            }else{
                heightError_tv.text=invalid
                heightError_tv.visibility= VISIBLE
                isHeightValid=false
                height_entry_et.error
            }
        }
        else{
            heightError_tv.text=empty
            heightError_tv.visibility= VISIBLE
            isHeightValid=false
            height_entry_et.error
        }
        //Systol Validation
        if (cistol_et.text.toString().isNotEmpty()){
            if (cistol_et.text.toString()=="-"){
                pbError_tv.visibility= INVISIBLE
                isSystolValid=true
                sSystol="-"
            }else if(cistol_et.text.toString().toInt() in (80..141)){
                pbError_tv.visibility= INVISIBLE
                isSystolValid=true
                sSystol=cistol_et.text.toString()
            }else{
                pbError_tv.text=invalid
                isSystolValid=false
                cistol_et.error=invalid
            }
        }
        else{
            pbError_tv.text=empty
            isSystolValid=false
            cistol_et.error
        }
        //Diastol Validation
        if (diastol_et.text.toString().isNotEmpty()){
            if (diastol_et.text.toString()=="-"){
                pbError_tv.visibility= INVISIBLE
                isDiastolValid=true
                sDiastol="-"
            }else if(diastol_et.text.toString().toInt() in (50..100)){
                pbError_tv.visibility= INVISIBLE
                isDiastolValid=true
                sDiastol=diastol_et.text.toString()
            }else{
                pbError_tv.text=invalid
                isDiastolValid=false
            }
        }
        else{
            pbError_tv.text=empty
            isDiastolValid=false
        }
        //Temperature Validation
        if (temperature_entry_et.text.toString().isNotEmpty()){
            if (temperature_entry_et.text.toString()=="-"){
                tempError_tv.visibility= INVISIBLE
                isTempValid=true
                sTemp="-"
            }else if (temperature_entry_et.text.toString().toFloat() in 93.0..104.0){
                tempError_tv.visibility= INVISIBLE
                isTempValid=true
                sTemp=temperature_entry_et.text.toString()
            }else{
                tempError_tv.text=invalid
                tempError_tv.visibility= VISIBLE
                isTempValid=false
                temperature_entry_et.error
            }
        }
        else{
            tempError_tv.text=empty
            tempError_tv.visibility= VISIBLE
            isTempValid=false
            temperature_entry_et.error
        }
        //Pulse Validation
        if (pulse_entry_et.text.toString().isNotEmpty()){
            if (pulse_entry_et.text.toString()=="-"){
                pulseError_tv.visibility= INVISIBLE
                isPulseValid=true
                   sPulse="-"
            }else if(pulse_entry_et.text.toString().toInt() in (1..200)){
                pulseError_tv.visibility= INVISIBLE
                isPulseValid=true
                sPulse=pulse_entry_et.text.toString()
            }else{
                pulseError_tv.text=invalid
                pulseError_tv.visibility= VISIBLE
                isPulseValid=false
                pulse_entry_et.error
            }
        }
        else{
            pulseError_tv.text=empty
            pulseError_tv.visibility= VISIBLE
            isPulseValid=false
            pulse_entry_et.error
        }
        if (!isDiastolValid || !isSystolValid){
            pbError_tv.visibility= VISIBLE
        }
        if(isWeightValid && isHeightValid && isPulseValid && isSystolValid && isDiastolValid && isTempValid){
            return  true
        }
        return  false
    }
    private fun checkInternet():Boolean{
        val connectManager=this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork=connectManager.activeNetworkInfo
        val isConected=activeNetwork?.isConnectedOrConnecting == true
        return isConected
    }
    private fun refresh(){
        if (checkInternet()){
            dataentryLayout.visibility= VISIBLE
            dataentry_internet_layout.visibility= INVISIBLE

        }else{
            Toast.makeText(this,"No InternetConnection!",Toast.LENGTH_SHORT).show()
        }
    }

    private fun hideKeyboard(){
        val view=this.currentFocus
        view?.let {
            val v=getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            v.hideSoftInputFromWindow(it.windowToken,0)
        }
    }
}