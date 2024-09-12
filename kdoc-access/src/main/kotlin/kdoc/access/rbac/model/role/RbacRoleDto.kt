/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package kdoc.access.rbac.model.role

import kdoc.access.rbac.model.field.RbacFieldRuleDto
import kdoc.access.rbac.model.scope.RbacScopeRuleDto
import kdoc.base.database.schema.admin.rbac.RbacFieldRuleTable
import kdoc.base.database.schema.admin.rbac.RbacRoleTable
import kdoc.base.database.schema.admin.rbac.RbacScopeRuleTable
import kdoc.base.persistence.model.Meta
import kdoc.base.persistence.serializers.SUuid
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow
import kotlin.uuid.Uuid

/**
 * Represents a single RBAC Role.
 *
 * @property id The unique id of the role record.
 * @property roleName The unique role name.
 * @property description Optional role description.
 * @property isSuper Whether this is a super-role, in which case it should have all permissions granted.
 * @property scopeRules The list of [RbacScopeRuleDto] entries for the role.
 * @property meta The metadata of the record.
 */
@Serializable
public data class RbacRoleDto(
    val id: SUuid,
    val roleName: String,
    val description: String?,
    val isSuper: Boolean,
    val scopeRules: List<RbacScopeRuleDto>,
    val meta: Meta
) {
    internal companion object {
        /**
         * Maps a list of [ResultRow]s to a [RbacRoleDto] instance.
         * Each row is expected to represent a different scope rule.
         *
         * @param roleId The id of the role.
         * @param rows The [ResultRow] list to map.
         * @return The mapped [RbacRoleDto] instance.
         */
        fun from(roleId: Uuid, rows: List<ResultRow>): RbacRoleDto {
            // Construct the child entries (if any).
            val scopeRules: List<RbacScopeRuleDto> = rows
                .filter { it.getOrNull(RbacScopeRuleTable.id) != null }
                .distinctBy { it[RbacScopeRuleTable.id] }
                .map { scopeRuleRow ->
                    val fieldRuleRows: List<ResultRow> = rows.filter {
                        it[RbacFieldRuleTable.scopeRuleId] == scopeRuleRow[RbacScopeRuleTable.id]
                    }
                    val fieldRules: List<RbacFieldRuleDto> = fieldRuleRows.map {
                        RbacFieldRuleDto.from(row = it)
                    }
                    RbacScopeRuleDto.from(row = scopeRuleRow, fieldRules = fieldRules)
                }

            // Use the first row as the role of the 1-N relationship,
            // as the rows come in a flattened format due to the SQL joins.
            val record: ResultRow = rows.first()

            return RbacRoleDto(
                id = roleId,
                roleName = record[RbacRoleTable.role_name],
                description = record[RbacRoleTable.description],
                isSuper = record[RbacRoleTable.isSuper],
                scopeRules = scopeRules,
                meta = Meta.from(row = record, table = RbacRoleTable)
            )
        }
    }
}
