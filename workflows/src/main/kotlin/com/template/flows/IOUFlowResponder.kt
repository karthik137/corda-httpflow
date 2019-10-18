package com.template.flows

import co.paralleluniverse.fibers.Suspendable
import com.template.states.IOUState
import net.corda.core.contracts.requireThat
import net.corda.core.flows.*
import net.corda.core.transactions.SignedTransaction

// Here we will call ReceiveFinalityFlow

@InitiatedBy(IOUFlow::class)
class IOUFlowResponder(
        private val otherPartySession: FlowSession
) : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {

        // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

        val signTransactionFlow = object : SignTransactionFlow(otherPartySession){
            override fun checkTransaction(stx: SignedTransaction) {
               // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                return requireThat {
                    val output = stx.tx.outputs.single().data

                    "This must be an IOU transaction." using (output is IOUState)

                    val iou = output as IOUState

                    "The IOU's value can't be too high." using (iou.value < 100)
                }
            }

        }

        val expectedTxId = subFlow(signTransactionFlow).id
        subFlow(ReceiveFinalityFlow(otherPartySession, expectedTxId))

    }

}


//@InitiatedBy(IOUFlow::class)
//class IOUFlowResponder : FlowLogic<Unit>() {
//    private var otherPartySession: FlowSession
//        get() {
//        return this.otherPartySession
//        }
//
//    constructor(otherPartySession: FlowSession): super() {
//        this.otherPartySession = otherPartySession
//    }
//
//
//    override fun call() {
//        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//
//    }
//
//}

