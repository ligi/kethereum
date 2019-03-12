package org.kethereum.bip32.model

import kotlinx.io.ByteBuffer
import kotlinx.io.errors.IOException
import org.kethereum.crypto.getCompressedPublicKey
import org.kethereum.encodings.encodeToBase58WithChecksum
import org.kethereum.model.ECKeyPair
import org.kethereum.model.PRIVATE_KEY_SIZE
import org.kethereum.model.extensions.toBytesPadded
import org.kethereum.model.number.BigInteger


data class ExtendedKey(
    val keyPair: ECKeyPair,
    internal val chainCode: ByteArray,
    internal val depth: Byte,
    private val parentFingerprint: Int,
    private val sequence: Int
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true

        other as ExtendedKey

        if (keyPair != other.keyPair) return false
        if (!chainCode.contentEquals(other.chainCode)) return false
        if (depth != other.depth) return false
        if (parentFingerprint != other.parentFingerprint) return false
        if (sequence != other.sequence) return false

        return true
    }

    override fun hashCode(): Int {
        var result = keyPair.hashCode()
        result = 31 * result + chainCode.hashCode()
        result = 31 * result + depth
        result = 31 * result + parentFingerprint
        result = 31 * result + sequence
        return result
    }

    fun serialize(publicKeyOnly: Boolean = false): String {
        val out = ByteBuffer.allocate(EXTENDED_KEY_SIZE)
        try {
            if (publicKeyOnly || keyPair.privateKey.key == BigInteger.ZERO) {
                out.put(xpub)
            } else {
                out.put(xprv)
            }
            out.put(depth)
            out.putInt(parentFingerprint)
            out.putInt(sequence)
            out.put(chainCode)
            if (publicKeyOnly || keyPair.privateKey.key == BigInteger.ZERO) {
                out.put(keyPair.getCompressedPublicKey())
            } else {
                out.put(0x00)
                out.put(keyPair.privateKey.key.toBytesPadded(PRIVATE_KEY_SIZE))
            }
        } catch (e: IOException) {
        }

        return out.array().encodeToBase58WithChecksum()
    }

}
