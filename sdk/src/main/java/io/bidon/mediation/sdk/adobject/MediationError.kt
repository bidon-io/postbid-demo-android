package io.bidon.mediation.sdk.adobject

data class MediationError(val code: Int, val message: String) {

    companion object {
        const val INVALID_PARAMETER = 1
        const val INVALID_STATE = 2
        const val INTERNAL = 3

        fun invalidParameter(message: String): MediationError = MediationError(INVALID_PARAMETER, message)

        fun invalidState(message: String): MediationError = MediationError(INVALID_STATE, message)

        fun internal(message: String): MediationError = MediationError(INTERNAL, message)
    }

    override fun toString(): String {
        return "($code) $message"
    }

}