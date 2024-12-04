/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package kdoc.core.settings.catalog.section

import kdoc.core.env.EnvironmentType
import kotlinx.serialization.Serializable

/**
 * Contains settings related to server runtime.
 *
 * @property machineId The unique machine ID. Used for generating unique IDs for call traceability.
 * @property environment The environment type. Not to be confused with the development mode flag.
 * @property doubleReceiveEnvironments The list of environments where the double receive plugin is enabled.
 * @property workingDir The working directory where files are stored.
 */
@Serializable
public data class RuntimeSettings(
    val machineId: Int,
    val environment: EnvironmentType,
    val doubleReceiveEnvironments: List<EnvironmentType>,
    val workingDir: String
)
