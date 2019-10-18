package com.template.states

import com.template.contracts.IOUContract
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.ContractState
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party

// *********
// * State *
// *********
//@BelongsToContract(TemplateContract::class)
//data class IOUState(val data: String, override val participants: List<AbstractParty> = listOf()) : ContractState

/**
 * Properties of IOUState
 *  value: Int
 *  lender: Party
 *  borrower: Party
 *
 *  Implements ContractState
 */

@BelongsToContract(IOUContract::class)
class IOUState: ContractState{

    val value: Int
    val lender: Party
    val borrower: Party

    constructor(value: Int, lender: Party, borrower: Party){
        this.value = value
        this.lender = lender
        this.borrower = borrower
    }

    override val participants: List<AbstractParty>
        get() = listOf(lender, borrower)

}