package com.template.webserver

import com.template.flows.IOUFlow
import com.template.states.IOUState
import net.corda.core.flows.FlowLogic
import net.corda.core.identity.CordaX500Name
import net.corda.core.messaging.startTrackedFlow
import net.corda.core.messaging.vaultQueryBy
import net.corda.core.utilities.getOrThrow
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.MediaType.TEXT_PLAIN_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Define your API endpoints here.
 */

@RestController
@RequestMapping("/api/example/") // The paths for HTTP requests are relative to this base path.
class Controller(rpc: NodeRPCConnection) {

    companion object {
        private val logger = LoggerFactory.getLogger(RestController::class.java)
    }

    private val myLegalName = rpc.proxy.nodeInfo().legalIdentities.first().name
    private val proxy = rpc.proxy

//    @GetMapping(value = "/templateendpoint", produces = arrayOf("text/plain"))
//    private fun templateendpoint(): String {
//        return "Define an endpoint here."
//    }

    /**
     * Returns node name
     */
    @GetMapping(value = "/nodename", produces = [APPLICATION_JSON_VALUE])
    fun whoami():Map<String, CordaX500Name> {
         return mapOf("me" to myLegalName)
    }

//    @GetMapping(value = "/iou-list", produces = [TEXT_PLAIN_VALUE])
//    fun getIOUs(): ResponseEntity<List<StateAndRef<IOUState>>>{
//        val response =  ResponseEntity.ok(proxy.vaultQueryBy<IOUState>().states)
//        println("Printing IOU List \n "+response.body)
//        return response
//
//    }

    @GetMapping(value = "/iou-list", produces = [TEXT_PLAIN_VALUE])
    fun getIOUs(): String {
        val response =  ResponseEntity.ok(proxy.vaultQueryBy<IOUState>().states)
        println("Printing IOU List \n "+response.body)
        return response.body.toString()
    }


    @GetMapping(value = "/create-iou", produces = [TEXT_PLAIN_VALUE])
    fun createIOU(): ResponseEntity<String> {
        val iouValue = 97
        val partyName = "O=PartyB,L=New York,C=US"

        // get party
        val partyX500Name = CordaX500Name.parse(partyName)
        val otherParty = proxy.wellKnownPartyFromX500Name(partyX500Name) ?: return ResponseEntity.badRequest().body("Party named $partyName cannot be found.\n")
        return try {

            //val signedTx = proxy.startTrackedFlow(::IOUFlow, iouValue, otherParty).returnValue.getOrThrow()
            //proxy.startTrac
            //val signedTx = proxy.startTrackedFlowDynamic(::IOUFlow, iouValue, otherParty)
            //val signedTx = proxy.startTrackedFlow(::IOUFlow, iouValue, otherParty)

            //val signedTx = proxy.startTrackedFlowDynamic(::IOUFlow);
            //proxy.startTrackedFlowDynamic(, iouValue, otherParty);
            //val v = proxy.startTrackedFlow(IOUFlow::class, iouValue, otherParty);
            //val v2 = proxy.startTrackedFlowDynamic()
            val signedTx = proxy.startTrackedFlowDynamic(IOUFlow::class.java, iouValue, otherParty);
            //val signedTx = proxy.startFlow(::IOUFlow, iouValue, otherParty)
            //val v3 = proxy
            //val test = ::IOUFlow
            //val testValue = IOUFlow::class
            //val testValue2 = IOUFlow::class.java

            ResponseEntity.status(HttpStatus.CREATED).body("txId = $signedTx.id")
        }catch (ex: Throwable){
            ResponseEntity.badRequest().body(ex.message)
        }
    }
}