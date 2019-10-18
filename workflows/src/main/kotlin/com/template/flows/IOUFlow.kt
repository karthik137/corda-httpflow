package com.template.flows

import co.paralleluniverse.fibers.Suspendable
import com.template.contracts.IOUContract
import com.template.states.IOUState
import net.corda.core.contracts.Command
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker

// *********
// * Flows *
// *********
//@InitiatingFlow
//@StartableByRPC
//class Initiator : FlowLogic<Unit>() {
//    override val progressTracker = ProgressTracker()
//
//    @Suspendable
//    override fun call() {
//        // Initiator flow logic goes here.
//    }
//}
//
//@InitiatedBy(Initiator::class)
//class Responder(val counterpartySession: FlowSession) : FlowLogic<Unit>() {
//    @Suspendable
//    override fun call() {
//        // Responder flow logic goes here.
//    }
//}

/**
 *
 * Flow will contain the following things:
 *
 *  iouValue: Int
 *  otherParty: Party
 *  progressTracker : ProgressTracker()
 *  override call()
 *      - retrieve notary
 *      - create transaction components.
 *      - create transaction builder and components
 *      - sign the transaction
 *      - create a session with the party
 *      - Initiate Finality subflow
 *
 */
@InitiatingFlow
@StartableByRPC
class IOUFlow(val iouValue: Int,
              val otherParty: Party) : FlowLogic<SignedTransaction>() {


    override val progressTracker = ProgressTracker()
    @Suspendable
    override fun call() : SignedTransaction{
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

        // We retrieve the notary identity from the network map
        val notary = serviceHub.networkMapCache.notaryIdentities[0]

        // We create the transaction components
        val outputState = IOUState(iouValue, ourIdentity, otherParty)
        // takes command action and public key of the owner
        val command = Command(IOUContract.Create(), listOf(ourIdentity.owningKey, otherParty.owningKey))

        // We create a transaction builder and add the components.
        val txBuilder = TransactionBuilder(notary = notary)
                .addOutputState(outputState, IOUContract.ID)
                .addCommand(command)

        // Verify the transactions
        txBuilder.verify(serviceHub)

        // We sign the transaction
        val signedTx = serviceHub.signInitialTransaction(txBuilder)

        // Create session with other Party
        val otherPartySession = initiateFlow(otherParty)

        //Obtain the counterparty's signature.

        val fullySignedTx = subFlow(CollectSignaturesFlow(signedTx, listOf(otherPartySession), CollectSignaturesFlow.tracker()))

        // We finalize the transaction and then send it to the counterparty
        return subFlow(FinalityFlow(fullySignedTx, otherPartySession))
    }

}

