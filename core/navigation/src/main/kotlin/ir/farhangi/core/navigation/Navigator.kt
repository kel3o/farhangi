package ir.farhangi.core.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.NavKey
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Multi-stack navigator for top-level destinations (Nav3 pattern).
 * Each top-level tab keeps its own back stack; Search and details push on the active stack.
 * Auth uses a separate stack. Exit-through-start when popping at a non-start tab root.
 */
@Singleton
class Navigator @Inject constructor() {

    private enum class Mode { Auth, Main }

    private var mode by mutableStateOf(Mode.Auth)
    private val authBackStack: SnapshotStateList<NavKey> = mutableStateListOf()
    private val mainStacks = linkedMapOf<NavKey, SnapshotStateList<NavKey>>()

    private var startRoute: NavKey? = null
    private var topLevelRoutes: Set<NavKey> = emptySet()

    /** Observed by Compose to recompose when the active tab changes. */
    var topLevelRoute by mutableStateOf<NavKey?>(null)
        private set

    val isAuthMode: Boolean
        get() = mode == Mode.Auth

    /** Active back stack for [NavDisplay]. Reading [topLevelRoute]/[isAuthMode] enables recomposition. */
    val backStack: SnapshotStateList<NavKey>
        get() = when (mode) {
            Mode.Auth -> authBackStack
            Mode.Main -> {
                val key = topLevelRoute
                if (key != null) {
                    mainStacks.getOrPut(key) { mutableStateListOf(key) }
                } else {
                    authBackStack
                }
            }
        }

    fun configureMain(start: NavKey, topLevels: Set<NavKey>) {
        startRoute = start
        topLevelRoutes = topLevels
        topLevels.forEach { key ->
            mainStacks.getOrPut(key) { mutableStateListOf(key) }
        }
        if (topLevelRoute == null || topLevelRoute !in topLevels) {
            topLevelRoute = start
        }
    }

    fun switchTopLevel(key: NavKey) {
        if (key !in topLevelRoutes) return
        mode = Mode.Main
        topLevelRoute = key
        val stack = mainStacks.getOrPut(key) { mutableStateListOf(key) }
        if (stack.isEmpty()) {
            stack.add(key)
        }
    }

    fun navigate(key: NavKey) {
        if (mode == Mode.Main && key in topLevelRoutes) {
            switchTopLevel(key)
            return
        }
        backStack.add(key)
    }

    /**
     * Clears the destination mode stack and sets a single root.
     * Auth keys enter Auth mode; configured top-level keys enter Main and reset tab stacks.
     */
    fun replaceAll(key: NavKey) {
        if (key in topLevelRoutes) {
            enterMainResettingTabs(selected = key)
            return
        }
        mode = Mode.Auth
        authBackStack.clear()
        authBackStack.add(key)
    }

    fun enterMain(selected: NavKey? = null) {
        val start = startRoute ?: return
        enterMainResettingTabs(selected = selected?.takeIf { it in topLevelRoutes } ?: start)
    }

    private fun enterMainResettingTabs(selected: NavKey) {
        mode = Mode.Main
        authBackStack.clear()
        topLevelRoutes.forEach { key ->
            val stack = mainStacks.getOrPut(key) { mutableStateListOf() }
            stack.clear()
            stack.add(key)
        }
        topLevelRoute = selected
    }

    fun pop(): Boolean {
        val stack = backStack
        if (stack.size > 1) {
            stack.removeAt(stack.lastIndex)
            return true
        }
        val start = startRoute
        if (mode == Mode.Main && start != null && topLevelRoute != start) {
            topLevelRoute = start
            return true
        }
        return false
    }

    fun popToRoot() {
        val stack = backStack
        if (stack.isEmpty()) return
        val root = stack.first()
        stack.clear()
        stack.add(root)
    }
}
