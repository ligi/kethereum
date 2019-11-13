package org.kethereum.ens

import org.kethereum.eip137.ENSName
import org.kethereum.eip137.toNameHashByteArray
import org.kethereum.ens.generated.ENS
import org.kethereum.ens.generated.Resolver
import org.kethereum.model.Address
import org.kethereum.rpc.EthereumRPC

class TypedENS(private val rpc: EthereumRPC,
               ensAddress: Address) {

    private val ens = ENS(rpc, ensAddress)

    fun getResolver(name: ENSName) = ens.resolver(name.toNameHashByteArray())

    fun getAddress(name: ENSName): Address? {
        val bytes32 = name.toNameHashByteArray()
        val resolver = ens.resolver(bytes32)
        return if (resolver != null && resolver != ENS_ADDRESS_NOT_FOUND) {
            return Resolver(rpc, resolver).addr(bytes32)
        } else {
            null
        }
    }
}