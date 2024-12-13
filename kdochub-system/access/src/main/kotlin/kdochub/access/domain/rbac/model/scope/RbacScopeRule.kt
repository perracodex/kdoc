/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package kdochub.access.domain.rbac.model.scope

import kdochub.access.domain.rbac.model.field.RbacFieldRule
import kdochub.access.domain.rbac.model.role.RbacRole
import kdochub.core.serializer.Uuid
import kdochub.database.model.Meta
import kdochub.database.schema.admin.rbac.RbacScopeRuleTable
import kdochub.database.schema.admin.rbac.type.RbacAccessLevel
import kdochub.database.schema.admin.rbac.type.RbacScope
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow

/**
 * Represents a concrete RBAC scope rule for a concrete [RbacRole].
 *
 * A scope can be any concept: a database table, a REST endpoint, a UI element, etc.
 * Is up to the designer to define what a scope is, and act accordingly when its
 * associated RBAC rule is verified.
 *
 * @property id The unique id of the Scope Rule record.
 * @property roleId The associated parent [RbacRole] id.
 * @property scope The [RbacScope] to which the scope rule belong.
 * @property accessLevel The required [RbacAccessLevel] for the [RbacScope].
 * @property fieldRules Optional [RbacFieldRule] list associated with the Scope Rule.
 * @property meta The metadata of the record.
 */
@Serializable
public data class RbacScopeRule private constructor(
    val id: Uuid,
    val roleId: Uuid,
    val scope: RbacScope,
    val accessLevel: RbacAccessLevel,
    val fieldRules: List<RbacFieldRule>?,
    val meta: Meta
) {
    internal companion object {
        /**
         * Maps a [ResultRow] to a [RbacScopeRule] instance.
         *
         * @param row The [ResultRow] to map.
         * @param fieldRules Optional [RbacFieldRule] list associated with the [RbacScopeRule].
         * @return The mapped [RbacScopeRule] instance.
         */
        fun from(row: ResultRow, fieldRules: List<RbacFieldRule>?): RbacScopeRule {
            return RbacScopeRule(
                id = row[RbacScopeRuleTable.id],
                roleId = row[RbacScopeRuleTable.roleId],
                scope = row[RbacScopeRuleTable.scope],
                accessLevel = row[RbacScopeRuleTable.accessLevel],
                fieldRules = fieldRules?.takeIf { it.isNotEmpty() },
                meta = Meta.from(row = row, table = RbacScopeRuleTable)
            )
        }
    }
}