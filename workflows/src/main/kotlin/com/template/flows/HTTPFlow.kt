package com.template.flows

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.InitiatingFlow
import net.corda.core.flows.StartableByRPC
import net.corda.core.internal.FetchDataFlow
import net.corda.core.utilities.ProgressTracker
import org.bouncycastle.asn1.ocsp.Request
import java.net.HttpURLConnection
import java.net.URL

//import okhttp3.Request

@InitiatingFlow
@StartableByRPC
class HTTPFlow : FlowLogic<String>() {
    override val progressTracker: ProgressTracker = ProgressTracker()

    @Suspendable
    override fun call(): String {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        //val httpRequest =
        //val httpRequest =

        val getData =  sendGet("http://jsonplaceholder.typicode.com/todos/1");
        println("Printing get data "+ getData)
        return getData;
    }


    fun sendGet(urlString: String): String {
        val url = URL(urlString)
        var data = ""

        with(url.openConnection() as HttpURLConnection) {
            requestMethod = "GET"  // optional default is GET

            println("\nSent 'GET' request to URL : $url; Response Code : $responseCode")
            inputStream.bufferedReader().use {
                it.lines().forEach { line ->
                    println(line)
                    data = data + line
                }
            }
        }

        return data;
    }
}