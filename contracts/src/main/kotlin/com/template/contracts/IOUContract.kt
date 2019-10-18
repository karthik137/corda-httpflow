package com.template.contracts

import com.template.states.IOUState
import net.corda.core.contracts.CommandData
import net.corda.core.contracts.Contract
import net.corda.core.contracts.requireSingleCommand
import net.corda.core.contracts.requireThat
import net.corda.core.transactions.LedgerTransaction

// ************
// * Contract *
// ************
//class IOUContract : Contract {
//    companion object {
//        // Used to identify our contract when building a transaction.
//        const val ID = "com.template.contracts.TemplateContract"
//    }
//
//    // A transaction is valid if the verify() function of the contract of all the transaction's input and output states
//    // does not throw an exception.
//    override fun verify(tx: LedgerTransaction) {
//        // Verification logic goes here.
//    }
//
//    // Used to indicate the transaction's intent.
//    interface Commands : CommandData {
//        class Action : Commands
//    }
//}

class IOUContract : Contract {

    companion object {
        const val ID = "com.template.contracts.IOUContract"
    }

    // On Create command
    class Create : CommandData

    override fun verify(tx: LedgerTransaction) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

        val command = tx.commands.requireSingleCommand<Create>()

        requireThat {
            // Constraints on the shape of the transaction
            "No inputs should be consumed when issuing an IOU". using(tx.inputs.isEmpty())
            "There should be one output state" using (tx.outputStates.size == 1)

            // IOU Specific constraints.
            val output = tx.outputsOfType<IOUState>().single()
            "The IOU's value must be a non-negative." using (output.value > 0)
            "The lender and the borrower cannot be the same entity". using (output.lender != output.borrower)
            //"Notary cannot be lender/borrower". using (output.lender != tx.notary && output.borrower != tx.notary)

            // Constraints on the signers
            val expectedSigners = listOf(output.borrower.owningKey, output.lender.owningKey)
            println("Printing to set value :"+ command.signers.toSet().size)
            println("Printing signers size : "+command.signers.size)
            "There should be two signers." using (command.signers.toSet().size == 2)
            "The borrower and lender must be the signers." using (command.signers.containsAll(expectedSigners))
        }
    }
}