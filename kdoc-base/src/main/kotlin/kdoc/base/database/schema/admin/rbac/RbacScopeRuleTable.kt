/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package kdoc.base.database.schema.admin.rbac

import kdoc.base.database.schema.admin.rbac.types.RbacAccessLevel
import kdoc.base.database.schema.admin.rbac.types.RbacScope
import kdoc.base.database.schema.base.TimestampedTable
import kdoc.base.persistence.utils.autoGenerate
import kdoc.base.persistence.utils.enumerationById
import kdoc.base.persistence.utils.kotlinUuid
import kdoc.base.persistence.utils.references
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import kotlin.uuid.Uuid

/**
 * Database table definition holding RBAC rules for a concrete [RbacRoleTable] record.
 *
 * A scope can be any concept: a database table, a REST endpoint, a UI element, etc.
 * Is up to the designer to define what a scope is, and act accordingly when its
 * associated RBAC rule is verified.
 *
 * @see RbacRoleTable
 */
public object RbacScopeRuleTable : TimestampedTable(name = "rbac_scope_rule") {
    /**
     * The unique id of the scope rule record.
     */
    public val id: Column<Uuid> = kotlinUuid(
        name = "scope_rule_id"
    ).autoGenerate()

    /**
     * The associated [RbacRoleTable] id.
     */
    public val roleId: Column<Uuid> = kotlinUuid(
        name = "role_id"
    ).references(
        fkName = "fk_rbac_scope_rule__role_id",
        ref = RbacRoleTable.id,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.RESTRICT
    )

    /**
     * The [RbacScope] the rule is meant to target.
     */
    public val scope: Column<RbacScope> = enumerationById(
        name = "scope_id",
        fromId = RbacScope::fromId
    )

    /**
     * The [RbacAccessLevel] representing the access level for the [RbacScope].
     */
    public val accessLevel: Column<RbacAccessLevel> = enumerationById(
        name = "access_level_id",
        fromId = RbacAccessLevel::fromId
    )

    override val primaryKey: Table.PrimaryKey = PrimaryKey(
        firstColumn = id,
        name = "pk_scope_rule_id"
    )

    init {
        uniqueIndex(
            customIndexName = "uq_rbac_scope_rule__role_id__scope",
            columns = arrayOf(roleId, scope)
        )
    }
}
