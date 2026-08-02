package ir.farhangi.core.data.usecase

import ir.farhangi.core.data.repository.AuthRepository
import ir.farhangi.core.model.Session
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveSession @Inject constructor(
    private val authRepository: AuthRepository,
) {
    operator fun invoke(): Flow<Session?> = authRepository.observeSession()
}
