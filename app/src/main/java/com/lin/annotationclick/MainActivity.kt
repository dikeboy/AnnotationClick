package com.lin.annotationclick

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.LayoutInflaterCompat
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import ndkteset.lin.com.androidano.BindView
import java.util.*

class MainActivity : AppCompatActivity() {
    private var builder: IViewBuilder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        builder =InjectHelper.initViewBuilder()
        installViewFactory()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.button1).setOnClickListener{
            Log.e("lin","${it} button1 click")
        }

        findViewById<TextView>(R.id.button2).setOnClickListener{
            Log.e("lin","${it} button2 click");
        }
    }


    private fun installViewFactory() {
        LayoutInflaterCompat.setFactory2(
            LayoutInflater.from(this),
            object : LayoutInflater.Factory2 {
                override fun onCreateView(name: String?, context: Context?, attrs: AttributeSet?): View? {
                    return null
                }
                override fun onCreateView(parent: View?, name: String?, context: Context?, attrs: AttributeSet?): View? {

                    //appcompat 创建view代码

                    var o = builder?.getView(name,context!!, attrs!!)
                    Log.e("lin","name="+name+ " attrs="+o+" builder="+builder);
                    if(o!=null){
                        return o as View;
                    }
                    val delegate = delegate
                    if(parent!=null){
                        return delegate.createView(parent, name, context!!, attrs!!)
                    }
                    else{
                        return null;
                    }
                }
            }
        )
    }
}
